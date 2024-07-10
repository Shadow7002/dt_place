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
import pe.shadow.model.*;
import pe.shadow.repository.*;

import java.util.*;
import java.util.stream.Collectors;

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

    @Autowired
    private InscripcionRepository inscripcionRepository;

    @GetMapping("")
    String index(Model model,
                 @PageableDefault(size = 5, sort = "nombre") Pageable pageable,
                 @RequestParam(required = false) String nombre,
                 @AuthenticationPrincipal UserDetails userDetails,
                 RedirectAttributes ra) {

        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // Verificar número de capas inscritas por el usuario
        List<Inscripcion> capasInscritas = inscripcionRepository.findByUsuarioAndEliminado(usuario, 0);
        if (capasInscritas.size() < 3) {
            ra.addFlashAttribute("msgError", "Debes inscribirte a las 3 capacitaciones");
            return "redirect:/capas";
        }

        Page<Cuestionario> cuestionarios;
        if (nombre != null && !nombre.trim().isEmpty()) {
            cuestionarios = cuestionarioRepository.findByNombreContainingAndEliminado(nombre, 0, pageable);
        } else {
            cuestionarios = cuestionarioRepository.findByEliminado(0, pageable);
        }

        // Obtener todas las evaluaciones del usuario en una sola consulta
        List<Evaluacion> evaluaciones = evaluacionRepository.findByUsuarioCreacion(usuario);

        // Crear un mapa de calificaciones por id de cuestionario
        Map<Integer, Integer> mapCalificaciones = new HashMap<>();
        evaluaciones.forEach(evaluacion -> {
            mapCalificaciones.put(evaluacion.getCuestionario().getId(), evaluacion.getPuntaje());
        });

        // Asignar la calificación y marcar los cuestionarios como evaluados
        cuestionarios.forEach(cuestionario -> {
            if (mapCalificaciones.containsKey(cuestionario.getId())) {
                cuestionario.setCalificacion(mapCalificaciones.get(cuestionario.getId()));
                cuestionario.setEvaluado(true);
            } else {
                cuestionario.setEvaluado(false);
            }
        });

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
