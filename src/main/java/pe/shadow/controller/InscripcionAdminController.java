package pe.shadow.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pe.shadow.model.Capa;
import pe.shadow.model.Inscripcion;
import pe.shadow.model.Usuario;
import pe.shadow.repository.CapaRepository;
import pe.shadow.repository.InscripcionRepository;
import pe.shadow.repository.UsuarioRepository;

import java.util.Optional;

@Controller
@RequestMapping("/admin/inscripciones")
public class InscripcionAdminController {

    @Autowired
    private InscripcionRepository inscripcionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CapaRepository capaRepository;

    @GetMapping("")
    String index(Model model,
                 @PageableDefault(size = 5, sort = "usuario") Pageable pageable,
                 @RequestParam(required = false) String usuario) {

        Page<Inscripcion> inscripcions;
        if (usuario != null && !usuario.trim().isEmpty()) {
            inscripcions = inscripcionRepository.findByUsuarioEmailContainingAndEliminado(usuario,0, pageable);
        } else {
            inscripcions = inscripcionRepository.findByEliminado(0, pageable);
        }
        model.addAttribute("inscripcions", inscripcions);
        return "/admin/inscripciones/index";
    }


}
