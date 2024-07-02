package pe.shadow.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.shadow.model.Capa;
import pe.shadow.model.Inscripcion;
import pe.shadow.model.Usuario;
import pe.shadow.repository.CapaRepository;
import pe.shadow.repository.InscripcionRepository;
import pe.shadow.repository.UsuarioRepository;
import pe.shadow.service.FileSystemStorageService;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/capas")
public class CapaController {

    @Autowired
    private CapaRepository capaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private FileSystemStorageService fileSystemStorageService;

    @Autowired
    private InscripcionRepository inscripcionRepository;

    @GetMapping("")
    String index(Model model,
                 @PageableDefault(size = 5, sort = "nombre") Pageable pageable,
                 @RequestParam(required = false) String nombre,
                 @AuthenticationPrincipal UserDetails userDetails) {

        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        Page<Capa> capas;
        if (nombre != null && !nombre.trim().isEmpty()) {
            capas = capaRepository.findByNombreContainingAndEstadoAndEliminado(nombre, Capa.Estado.PUBLICO,0, pageable);
        } else {
            capas = capaRepository.findByEstadoAndEliminado(Capa.Estado.PUBLICO, 0, pageable);
        }

        // Obtener todas las inscripciones del usuario en una sola consulta
        List<Inscripcion> inscripciones = inscripcionRepository.findByUsuarioAndEliminado(usuario, 0);

        // Crear un conjunto de ids de capas en las que el usuario está inscrito
        Set<Integer> idsCapasInscritas = inscripciones.stream()
                .map(inscripcion -> inscripcion.getCapa().getId())
                .collect(Collectors.toSet());

        // Marcar las capas como inscritas
        capas.forEach(capa -> {
            capa.setInscrito(idsCapasInscritas.contains(capa.getId()));
        });

        model.addAttribute("capas", capas);
        return "capas/index";
    }

    @GetMapping("/{id}")
    String view(@PathVariable Integer id, Model model, @AuthenticationPrincipal UserDetails userDetails,
                RedirectAttributes ra) {
        Optional<Capa> optionalCapa = capaRepository.findByIdAndEliminado(id, 0);
        if (!optionalCapa.isPresent()) {
            ra.addFlashAttribute("msgError", "Capacitación no encontrada.");
            return "redirect:/capas";
        }

        Capa capa = optionalCapa.get();

        // Verificar si el usuario está inscrito en esta capa
        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        model.addAttribute("capa", capa);
        return "/capas/view";
    }
}
