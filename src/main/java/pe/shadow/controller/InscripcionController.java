package pe.shadow.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.shadow.model.Capa;
import pe.shadow.model.Inscripcion;
import pe.shadow.model.Usuario;
import pe.shadow.repository.CapaRepository;
import pe.shadow.repository.InscripcionRepository;
import pe.shadow.repository.UsuarioRepository;

import java.util.Optional;

@Controller
@RequestMapping("/api/inscripcion")
public class InscripcionController {

    @Autowired
    private InscripcionRepository inscripcionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CapaRepository capaRepository;

    @PostMapping("/inscribir")
    public String inscribirUsuario(@RequestParam Integer idCapa2,
                                   @AuthenticationPrincipal UserDetails userDetails,
                                   RedirectAttributes ra) {

        // Obtener el email del usuario autenticado
        String email = userDetails.getUsername();
        Optional<Usuario> optionalUsuarioCreacion = usuarioRepository.findByEmail(email);

        // Verificar si el usuario fue encontrado
        if (!optionalUsuarioCreacion.isPresent()) {
            ra.addFlashAttribute("msgError", "Usuario no encontrado.");
            return "redirect:/capas";
        }

        Usuario usuarioCreacion = optionalUsuarioCreacion.get();

        // Verificar si la capa existe y no está eliminada
        Optional<Capa> optionalCapa = capaRepository.findByIdAndEliminado(idCapa2, 0);
        if (!optionalCapa.isPresent()) {
            ra.addFlashAttribute("msgError", "No se encontró la capacitación.");
            return "redirect:/capas";
        }

        Capa capa = optionalCapa.get();

        // Verificar si el usuario ya está inscrito en esta capa
        boolean estaInscrito = inscripcionRepository.existsByCapaAndUsuario(capa, usuarioCreacion);
        if (estaInscrito) {
            ra.addFlashAttribute("msgError", "El usuario ya está inscrito en esta capacitación.");
            return "redirect:/capas";
        }

        // Crear la inscripción
        Inscripcion inscripcion = new Inscripcion();
        inscripcion.setCapa(capa);
        inscripcion.setUsuario(usuarioCreacion);
        inscripcion.setEliminado(0);
        inscripcion.setUsuarioCreacion(usuarioCreacion);
        inscripcion.setUsuarioActualizacion(usuarioCreacion);

        // Guardar la inscripción
        inscripcionRepository.save(inscripcion);

        ra.addFlashAttribute("msgExito", "Usuario inscrito correctamente.");
        return "redirect:/capas";
    }
}
