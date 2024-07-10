package pe.shadow.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pe.shadow.model.Evaluacion;
import pe.shadow.model.Inscripcion;
import pe.shadow.repository.CuestionarioRepository;
import pe.shadow.repository.EvaluacionRepository;
import pe.shadow.repository.UsuarioRepository;

@Controller
@RequestMapping("/admin/evaluaciones")
public class EvaluacionAdminController {

    @Autowired
    private EvaluacionRepository evaluacionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CuestionarioRepository cuestionarioRepository;

    @GetMapping("")
    String index(Model model,
                 @PageableDefault(size = 5, sort = "usuario") Pageable pageable,
                 @RequestParam(required = false) String usuario) {

        Page<Evaluacion> evaluacions;
        if (usuario != null && !usuario.trim().isEmpty()) {
            evaluacions = evaluacionRepository.findByUsuarioCreacion_EmailContaining(usuario, pageable);
        } else {
            evaluacions = evaluacionRepository.findAll(pageable);
        }
        model.addAttribute("evaluacions", evaluacions);
        return "/admin/evaluaciones/index";
    }
}
