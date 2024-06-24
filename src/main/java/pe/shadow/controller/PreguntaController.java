package pe.shadow.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.shadow.model.Cuestionario;
import pe.shadow.model.Pregunta;
import pe.shadow.model.Usuario;
import pe.shadow.repository.CuestionarioRepository;
import pe.shadow.repository.PreguntaRepository;
import pe.shadow.repository.UsuarioRepository;

import java.util.Optional;

@Controller
@RequestMapping("/admin/preguntas")
public class PreguntaController {

    @Autowired
    private PreguntaRepository preguntaRepository;

    @Autowired
    private CuestionarioRepository cuestionarioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/eliminar/{id}")
    String eliminar(@PathVariable("id") Integer id, RedirectAttributes ra) {
        Optional<Pregunta> optionalPregunta = preguntaRepository.findById(id);
        if (optionalPregunta.isPresent()) {
            Pregunta pregunta = optionalPregunta.get();
            Integer idcuestionario = pregunta.getCuestionario().getId();
            pregunta.setEliminado(1); // Marcamos como eliminada (no eliminamos físicamente)
            preguntaRepository.save(pregunta);
            ra.addFlashAttribute("msgExito", "Pregunta eliminada exitosamente");
            return "redirect:/admin/cuestionarios/view/" + idcuestionario;
        } else {
            ra.addFlashAttribute("msgError", "No se encontró la pregunta con ID: " + id);
        }
        return "redirect:/admin/cuestionarios";
    }

    @PostMapping("/nuevo")
    public String guardarNuevaPregunta(@ModelAttribute Pregunta pregunta, @RequestParam Integer idcuestionario, BindingResult bindingResult, RedirectAttributes ra, @AuthenticationPrincipal UserDetails userDetails) {
        if (bindingResult.hasErrors()) {
            return "redirect:/admin/cuestionarios/view/" + idcuestionario;
        }

        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        pregunta.setUsuarioCreacion(usuario);
        pregunta.setUsuarioActualizacion(usuario);

        Cuestionario cuestionario = cuestionarioRepository.findById(idcuestionario)
                .orElseThrow(() -> new IllegalArgumentException("ID de cuestionario inválido: " + idcuestionario));
        pregunta.setCuestionario(cuestionario);
        preguntaRepository.save(pregunta);
        return "redirect:/admin/cuestionarios/view/" + idcuestionario;
    }
}
