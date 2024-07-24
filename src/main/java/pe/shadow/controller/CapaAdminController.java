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
import pe.shadow.model.Capa;
import pe.shadow.model.Cuestionario;
import pe.shadow.model.Evaluacion;
import pe.shadow.model.Usuario;
import pe.shadow.repository.CapaRepository;
import pe.shadow.repository.CuestionarioRepository;
import pe.shadow.repository.EvaluacionRepository;
import pe.shadow.repository.UsuarioRepository;
import pe.shadow.service.FileSystemStorageService;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/capas")
public class CapaAdminController {

    @Autowired
    private CapaRepository capaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private FileSystemStorageService fileSystemStorageService;

    @Autowired
    private CuestionarioRepository cuestionarioRepository;

    @GetMapping("")
    String index(Model model,
                 @PageableDefault(size = 5, sort = "nombre") Pageable pageable,
                 @RequestParam(required = false) String nombre) {

        Page<Capa> capas;
        if (nombre != null && !nombre.trim().isEmpty()) {
            capas = capaRepository.findByNombreContainingAndEliminado(nombre,0, pageable);
        } else {
            capas = capaRepository.findByEliminado(0, pageable);
        }
        model.addAttribute("capas", capas);
        return "admin/capas/index";
    }

    @GetMapping("/view/{id}")
    String view(@PathVariable Integer id, Model model) {
        Optional<Capa> capa = capaRepository.findById(id);
        model.addAttribute("capa", capa.get());
        return "admin/capas/view";
    }

    @GetMapping("/nuevo")
    String nuevo(Model model) {
        List<Cuestionario> cuestionarios = cuestionarioRepository.findAll();


        model.addAttribute("capa", new Capa());
        model.addAttribute("cuestionarios", cuestionarioRepository.findAll());
        return "admin/capas/nuevo";
    }

    @PostMapping("/nuevo")
    String insertar(Model model, @Validated Capa capa, BindingResult bindingResult,
                    RedirectAttributes ra, @AuthenticationPrincipal UserDetails userDetails,
                    @RequestParam("idCuestionario") Integer idCuestionario) {
        if (capa.getImagen().isEmpty()) {
            bindingResult.rejectValue("imagen", "MultipartNotEmpty");
        }


        if (bindingResult.hasErrors()) {
            model.addAttribute("capa", capa);
            return "admin/capas/nuevo";
        }

        String rutaImagen = fileSystemStorageService.store(capa.getImagen());
        capa.setRutaImagen(rutaImagen);

        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        capa.setUsuarioCreacion(usuario);
        capa.setUsuarioActualizacion(usuario);

        if (!EnumSet.allOf(Capa.Estado.class).contains(capa.getEstado())) {
            throw new IllegalArgumentException("Estado inválido: " + capa.getEstado());
        }

        // Asignar la evaluación seleccionada
        Cuestionario cuestionario = cuestionarioRepository.findById(idCuestionario)
                .orElseThrow(() -> new IllegalArgumentException("Evaluación no encontrada"));
        capa.setCuestionario(cuestionario);

        capaRepository.save(capa);

        ra.addFlashAttribute("msgExito", "La capacitación fue registrada exitosamente");
        return "redirect:/admin/capas";
    }

    @GetMapping("/editar/{id}")
    String editar(Model model, @PathVariable("id") Integer id)
    {
        Capa capa = capaRepository.getById(id);
        List<Cuestionario> cuestionarios = cuestionarioRepository.findAll();


        model.addAttribute("capa", capa);
        model.addAttribute("cuestionarios", cuestionarioRepository.findAll());
        return "admin/capas/editar";
    }

    @PostMapping("/editar/{id}")
    String actualizar(Model model, @PathVariable("id") Integer id,
                      @Validated Capa capa, BindingResult bindingResult,
                      RedirectAttributes ra, @AuthenticationPrincipal UserDetails userDetails,
                      @RequestParam("idCuestionario") Integer idCuestionario)
    {
        if (capa.getImagen().isEmpty())
        {
            bindingResult.rejectValue("imagen", "MultipartNotEmpty");
        }

        if (bindingResult.hasErrors())
        {
            model.addAttribute("capa", capa);
            return "admin/capas/editar";
        }

        //1. obtener de la base de datos
        Capa capaFromDB = capaRepository.getById(id);

        //2. asignar los nuevos valores al modelo libro
        capaFromDB.setNombre(capa.getNombre());
        capaFromDB.setDescripcion(capa.getDescripcion());
        capaFromDB.setInstructor(capa.getInstructor());
        capaFromDB.setEstado(capa.getEstado());
        capaFromDB.setFecIni(capa.getFecIni());
        capaFromDB.setFecFin(capa.getFecFin());
        capaFromDB.setVacante(capa.getVacante());
        capaFromDB.setRutaImagen(capa.getRutaImagen());

        //3. guardar en bd
        String rutaImagen = fileSystemStorageService.store(capa.getImagen());
        capaFromDB.setRutaImagen(rutaImagen);

        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        capaFromDB.setUsuarioActualizacion(usuario);

        // Asignar la evaluación seleccionada
        Cuestionario cuestionario = cuestionarioRepository.findById(idCuestionario)
                .orElseThrow(() -> new IllegalArgumentException("Evaluación no encontrada"));
        capaFromDB.setCuestionario(cuestionario);

        capaRepository.save(capaFromDB);

        ra.addFlashAttribute("msgExito", "La capacitación se actualizó correctamente");
        return "redirect:/admin/capas";
    }

    @PostMapping("/eliminar/{id}")
    String eliminar(@PathVariable("id") Integer id, RedirectAttributes ra)
    {
        Capa capa = capaRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("ID inválido: " + id));
        capa.setEliminado(1);

        capaRepository.save(capa);
        ra.addFlashAttribute("msgExito", "Capacitación elimina exitosamente");
        return "redirect:/admin/capas";
    }
}
