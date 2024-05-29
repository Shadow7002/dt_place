package pe.shadow.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.shadow.model.Capa;
import pe.shadow.repository.CapaRepository;
import pe.shadow.service.FileSystemStorageService;

import java.util.Optional;

@Controller
@RequestMapping("/admin/capas")
public class CapaAdminController {

    @Autowired
    private CapaRepository capaRepository;

    @Autowired
    private FileSystemStorageService fileSystemStorageService;

    @GetMapping("")
    String index(Model model,
                 @PageableDefault(size = 5, sort = "nombre") Pageable pageable,
                 @RequestParam(required = false) String nombre) {

        Page<Capa> capas;
        if (nombre != null && !nombre.trim().isEmpty()) {
            capas = capaRepository.findByNombreContaining(nombre, pageable);
        } else {
            capas = capaRepository.findAll(pageable);
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
        model.addAttribute("capa", new Capa());
        return "admin/capas/nuevo";
    }

    @PostMapping("/nuevo")
    String insertar(Model model, @Validated Capa capa, BindingResult bindingResult, RedirectAttributes ra) {
        if (capa.getImagen().isEmpty()) {
            bindingResult.rejectValue("imagen", "MultipartNotEmpty");
        }


        if (bindingResult.hasErrors()) {
            model.addAttribute("capa", capa);
            return "admin/capas/nuevo";
        }

        String rutaImagen = fileSystemStorageService.store(capa.getImagen());
        capa.setRutaImagen(rutaImagen);
        capaRepository.save(capa);

        ra.addFlashAttribute("msgExito", "La capacitación fue registrada exitosamente");
        return "redirect:/admin/capas";
    }

    @GetMapping("/editar/{id}")
    String editar(Model model, @PathVariable("id") Integer id)
    {
        Capa capa = capaRepository.getById(id);
        model.addAttribute("capa", capa);
        return "admin/capas/editar";
    }

    @PostMapping("/editar/{id}")
    String actualizar(Model model, @PathVariable("id") Integer id,@Validated Capa capa, BindingResult bindingResult, RedirectAttributes ra)
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
        capaRepository.save(capaFromDB);

        ra.addFlashAttribute("msgExito", "La capacitación se actualizó correctamente");
        return "redirect:/admin/capas";
    }
}
