package pe.shadow.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.shadow.model.Opcion;
import pe.shadow.model.Pregunta;
import pe.shadow.model.Usuario;
import pe.shadow.repository.OpcionRepository;
import pe.shadow.repository.PreguntaRepository;
import pe.shadow.repository.UsuarioRepository;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/opciones")
public class OpcionAdminController {

    private static final Logger logger = LoggerFactory.getLogger(OpcionAdminController.class);

    @Autowired
    private OpcionRepository opcionRepository;

    @Autowired
    private PreguntaRepository preguntaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("")
    public ResponseEntity<List<Opcion>> mostrarOpciones(@RequestParam("idPregunta") Integer idPregunta) {
        Optional<Pregunta> preguntaOptional = preguntaRepository.findById(idPregunta);
        if (preguntaOptional.isPresent()) {
            Pregunta pregunta = preguntaOptional.get();
            List<Opcion> opciones = opcionRepository.findByPreguntaAndEliminado(pregunta, 0);
            return ResponseEntity.ok(opciones);
        } else {
            logger.warn("No se encontró la pregunta con ID {}", idPregunta);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/crear")
    public String crearOpciones(@RequestParam("idPregunta") Integer idPregunta,
                                @RequestParam("opcion") List<String> textos,
                                @RequestParam("correcta") String[] esCorrectaArray,
                                RedirectAttributes ra,
                                @AuthenticationPrincipal UserDetails userDetails) {
        Optional<Pregunta> preguntaOptional = preguntaRepository.findById(idPregunta);
        if (!preguntaOptional.isPresent()) {
            logger.warn("No se encontró la pregunta con ID {} al intentar crear opciones", idPregunta);
            return "error/404";
        }

        Pregunta pregunta = preguntaOptional.get();

        Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(userDetails.getUsername());
        if (!usuarioOptional.isPresent()) {
            logger.error("Usuario no encontrado con email {}", userDetails.getUsername());
            return "error/401";
        }

        Usuario usuario = usuarioOptional.get();

        List<Boolean> esCorrectaList = new ArrayList<>();
        for (String correcta : esCorrectaArray) {
            esCorrectaList.add(correcta.equals("1")); // Convierte "1" a true, "0" a false
        }

        for (int i = 0; i < textos.size(); i++) {
            if (textos.get(i) != null && !textos.get(i).isEmpty()) {
                Opcion opcion = new Opcion();
                opcion.setPregunta(pregunta);
                opcion.setTexto(textos.get(i));
                opcion.setEsCorrecta(esCorrectaList.get(i));
                opcion.setUsuarioCreacion(usuario);
                opcion.setUsuarioActualizacion(usuario);
                opcionRepository.save(opcion);
            }
        }

        logger.info("Opciones creadas exitosamente para la pregunta con ID {}", idPregunta);
        ra.addFlashAttribute("msgExito", "Las opciones fueron registrada exitosamente");
        return "redirect:/admin/cuestionarios/view/" + pregunta.getCuestionario().getId();
    }

    @PostMapping("/actualizar")
    public String actualizarOpciones(@RequestParam("idPregunta") Integer idPregunta,
                                     @RequestParam("opcion") List<String> textos,
                                     @RequestParam("correcta") List<Boolean> esCorrectaList,
                                     RedirectAttributes ra,
                                     @AuthenticationPrincipal UserDetails userDetails) {
        Optional<Pregunta> preguntaOptional = preguntaRepository.findById(idPregunta);
        if (!preguntaOptional.isPresent()) {
            logger.warn("No se encontró la pregunta con ID {} al intentar actualizar opciones", idPregunta);
            return "error/404";
        }

        Pregunta pregunta = preguntaOptional.get();

        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() ->{
                    logger.error("Usuario no encontrado con email {}", userDetails.getUsername());
                    return new IllegalArgumentException("Usuario no encontrado");
                });


        List<Opcion> opcionesExistentes = opcionRepository.findByPreguntaAndEliminado(pregunta, 0);
        for (int i = 0; i < opcionesExistentes.size(); i++) {
            Opcion opcionExistente = opcionesExistentes.get(i);
            opcionExistente.setTexto(textos.get(i));
            opcionExistente.setEsCorrecta(esCorrectaList.get(i));
            opcionExistente.setUsuarioActualizacion(usuario);
            opcionRepository.save(opcionExistente);
        }

        logger.info("Opciones actualizadas exitosamente para la pregunta con ID {}", idPregunta);
        ra.addFlashAttribute("msgExito", "Las opciones fueron actualizadas exitosamente");
        return "redirect:/admin/cuestionarios/view/" + pregunta.getCuestionario().getId();
    }
}
