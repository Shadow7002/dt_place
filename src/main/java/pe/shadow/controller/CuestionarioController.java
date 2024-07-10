package pe.shadow.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.shadow.model.Cuestionario;
import pe.shadow.model.Opcion;
import pe.shadow.model.Pregunta;
import pe.shadow.model.Usuario;
import pe.shadow.repository.*;

import java.util.*;

@Controller
@RequestMapping("/cuestionarios")
public class CuestionarioController {

    @Autowired
    private CuestionarioRepository cuestionarioRepository;

    @Autowired
    private PreguntaRepository preguntaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private OpcionRepository opcionRepository;

    @Autowired
    private EvaluacionRepository evaluacionRepository;

    @GetMapping("")
    String index(Model model,
                 @PageableDefault(size = 5, sort = "nombre") Pageable pageable,
                 @RequestParam(required = false) String nombre) {

        Page<Cuestionario> cuestionarios;
        if (nombre != null && !nombre.trim().isEmpty()) {
            cuestionarios = cuestionarioRepository.findByNombreContainingAndEliminado(nombre, 0, pageable);
        } else {
            cuestionarios = cuestionarioRepository.findByEliminado(0, pageable);
        }
        model.addAttribute("cuestionarios", cuestionarios);
        return "cuestionarios/index";
    }

    @GetMapping("/view/{id}")
    String view(@PathVariable Integer id, Model model, @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes ra) {
        Optional<Cuestionario> optionalCuestionario = cuestionarioRepository.findById(id);
        if (optionalCuestionario.isPresent()) {
            Cuestionario cuestionario = optionalCuestionario.get();

            Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

            boolean yaCompletado = evaluacionRepository.existsByCuestionarioAndUsuarioCreacion(cuestionario, usuario);

            if (yaCompletado) {
                ra.addFlashAttribute("msgError", "No le quedan intentos disponibles");
                return "redirect:/cuestionarios";
            } else {
                List<Pregunta> preguntas = preguntaRepository.findByCuestionarioAndEliminado(cuestionario, 0);

                Map<Integer, List<Opcion>> opcionesPorPregunta = new HashMap<>();
                for (Pregunta pregunta : preguntas) {
                    List<Opcion> opciones = opcionRepository.findByPreguntaAndEliminado(pregunta, 0);
                    opcionesPorPregunta.put(pregunta.getId(), opciones);
                }

                model.addAttribute("cuestionario", cuestionario);
                model.addAttribute("preguntas", preguntas);
                model.addAttribute("opcionesPorPregunta", opcionesPorPregunta);
                return "cuestionarios/view";
            }
        } else {
            throw new IllegalArgumentException("ID inválido: " + id);
        }
    }
}
