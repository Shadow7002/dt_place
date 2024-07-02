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
import pe.shadow.repository.CuestionarioRepository;
import pe.shadow.repository.OpcionRepository;
import pe.shadow.repository.PreguntaRepository;
import pe.shadow.repository.UsuarioRepository;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/cuestionarios")
public class CuestionarioAdminController {

    @Autowired
    private CuestionarioRepository cuestionarioRepository;

    @Autowired
    private PreguntaRepository preguntaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private OpcionRepository opcionRepository;

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
        return "admin/cuestionarios/index";
    }

    @GetMapping("/nuevo")
    String nuevo(Model model) {
        model.addAttribute("cuestionario", new Cuestionario());
        return "admin/cuestionarios/nuevo";
    }

    @PostMapping("/nuevo")
    String insertar(Model model, @Validated Cuestionario cuestionario,
                    BindingResult bindingResult, RedirectAttributes ra,
                    @AuthenticationPrincipal UserDetails userDetails) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("cuestionario", cuestionario);
            return "admin/cuestionarios/nuevo";
        }

        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        cuestionario.setUsuarioCreacion(usuario);
        cuestionario.setUsuarioActualizacion(usuario);

        cuestionarioRepository.save(cuestionario);

        ra.addFlashAttribute("msgExito", "El cuestionario fue registrado exitosamente");
        return "redirect:/admin/cuestionarios";
    }

    @PostMapping("/eliminar/{id}")
    String eliminar(@PathVariable("id") Integer id, RedirectAttributes ra)
    {
        Cuestionario cuestionario = cuestionarioRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("ID inválido: " + id));
        cuestionario.setEliminado(1);

        cuestionarioRepository.save(cuestionario);
        ra.addFlashAttribute("msgExito", "Cuestionario eliminado exitosamente");
        return "redirect:/admin/cuestionarios";
    }

    @GetMapping("/editar/{id}")
    String editar(Model model, @PathVariable("id") Integer id)
    {
        Cuestionario cuestionario = cuestionarioRepository.getById(id);
        model.addAttribute("cuestionario", cuestionario);
        return "admin/cuestionarios/editar";
    }

    @PostMapping("/editar/{id}")
    String actualizar(Model model, @PathVariable("id") Integer id,@Validated Cuestionario cuestionario, BindingResult bindingResult, RedirectAttributes ra, @AuthenticationPrincipal UserDetails userDetails)
    {
        if (bindingResult.hasErrors())
        {
            model.addAttribute("cuestionario", cuestionario);
            return "admin/cuestionarios/editar";
        }

        //1. obtener de la base de datos
        Cuestionario cuestionarioFromDB = cuestionarioRepository.getById(id);

        //2. asignar los nuevos valores al modelo libro
        cuestionarioFromDB.setNombre(cuestionario.getNombre());
        cuestionarioFromDB.setCalificacionMaxima(cuestionario.getCalificacionMaxima());


        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        cuestionarioFromDB.setUsuarioActualizacion(usuario);

        cuestionarioRepository.save(cuestionarioFromDB);

        ra.addFlashAttribute("msgExito", "El cuestionario se actualizó correctamente");
        return "redirect:/admin/cuestionarios";
    }

    @GetMapping("/view/{id}")
    String view(@PathVariable Integer id, Model model) {
        Optional<Cuestionario> optionalCuestionario = cuestionarioRepository.findById(id);
        if (optionalCuestionario.isPresent()) {
            Cuestionario cuestionario = optionalCuestionario.get();
            List<Pregunta> preguntas = preguntaRepository.findByCuestionarioAndEliminado(cuestionario,0);
            model.addAttribute("cuestionario", cuestionario);
            model.addAttribute("preguntas", preguntas);
            model.addAttribute("pregunta", new Pregunta());

            List<Opcion> opciones = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                opciones.add(new Opcion());
            }
            model.addAttribute("opciones", opciones);

            return "admin/cuestionarios/view";
        } else {
            throw new IllegalArgumentException("ID inválido: " + id);
        }
    }

}
