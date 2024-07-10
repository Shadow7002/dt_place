package pe.shadow.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pe.shadow.model.*;
import pe.shadow.repository.*;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/evaluaciones")
public class EvaluacionController {
    @Autowired
    private EvaluacionRepository evaluacionRepository;

    @Autowired
    private RespuestaRepository respuestaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PreguntaRepository preguntaRepository;

    @Autowired
    private OpcionRepository opcionRepository;

    @Autowired
    private CuestionarioRepository cuestionarioRepository;

    @PostMapping("/enviar")
    public String submitEvaluacion(@RequestParam Map<String, String> params,
                                   @RequestParam("idCuestionario") Integer idCuestionario,
                                   @AuthenticationPrincipal UserDetails userDetails,
                                   Model model) {
        // Obtener el cuestionario actual (ajustar según tu lógica)
        Cuestionario cuestionario = cuestionarioRepository.findById(idCuestionario)
                .orElseThrow(() -> new IllegalArgumentException("Cuestionario no encontrado"));

        // Obtener el usuario actual
        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // Crear nueva evaluación
        Evaluacion evaluacion = new Evaluacion();
        evaluacion.setCuestionario(cuestionario);
        evaluacion.setUsuarioCreacion(usuario);

        // Calcular el puntaje por pregunta
        long totalPreguntas = params.keySet().stream()
                .filter(key -> key.startsWith("pregunta_"))
                .count();
        int puntajePorPregunta = cuestionario.getCalificacionMaxima() / (int) totalPreguntas;

        // Calcular el puntaje
        int puntajeObtenido = 0;
        List<Respuesta> respuestas = new ArrayList<>();

        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (entry.getKey().startsWith("pregunta_")) {
                Integer idPregunta = Integer.valueOf(entry.getKey().substring(9));
                Integer idOpcionSeleccionada = Integer.valueOf(entry.getValue());

                Pregunta pregunta = preguntaRepository.findById(idPregunta).orElse(null);
                Opcion opcionSeleccionada = opcionRepository.findById(idOpcionSeleccionada).orElse(null);

                if (pregunta != null && opcionSeleccionada != null) {
                    Respuesta respuesta = new Respuesta();
                    respuesta.setEvaluacion(evaluacion);
                    respuesta.setPregunta(pregunta);
                    respuesta.setOpcion(opcionSeleccionada);
                    respuesta.setCorrecta(opcionSeleccionada.getEsCorrecta());
                    respuesta.setUsuario(usuario);

                    respuestas.add(respuesta);

                    if (opcionSeleccionada.getEsCorrecta()) {
                        puntajeObtenido += puntajePorPregunta;
                    }
                }
            }
        }

        evaluacion.setPuntaje(puntajeObtenido);
        evaluacionRepository.save(evaluacion);

        for (Respuesta respuesta : respuestas) {
            respuestaRepository.save(respuesta);
        }

        // Redirigir a una página de resultados
        model.addAttribute("evaluacion", evaluacion);
        return "cuestionarios/resultados";
    }
}
