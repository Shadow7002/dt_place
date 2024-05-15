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
import pe.shadow.model.Usuario;
import pe.shadow.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Controller
@RequestMapping("/admin/usuarios")
public class UsuarioAdminController {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("")
    String index(Model model,
                 @PageableDefault(size = 5, sort = "apellidos") Pageable pageable,
                 @RequestParam(required = false) String apellidos){
        Page<Usuario> usuarios;
        if (apellidos != null && !apellidos.trim().isEmpty())
        {
            usuarios = usuarioRepository.findByApellidosContaining(apellidos, pageable);
        }
        else
        {
            usuarios = usuarioRepository.findAll(pageable);
        }

        model.addAttribute("usuarios", usuarios);
        return "admin/usuarios/index";
    }

    @GetMapping("/view/{id}")
    String view(@PathVariable Integer id, Model model){
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        model.addAttribute("usuario", usuario.get());
        return "admin/usuarios/view";
    }

    @GetMapping("/editar/{id}")
    String editar(Model model, @PathVariable("id") Integer id)
    {
        Usuario usuario = usuarioRepository.getById(id);
        model.addAttribute("usuario", usuario);
        return "admin/usuarios/editar";
    }

    @PostMapping("/editar/{id}")
    String actualizar(Model model, @PathVariable("id") Integer id, @Validated(Usuario.EdicionAdmin.class) Usuario usuario, BindingResult br, RedirectAttributes ra)
    {
        if (br.hasErrors()) {
            return "admin/usuarios/editar";
        }

        Usuario usuarioFromDB = usuarioRepository.getById(id);
        usuarioFromDB.setNombres(usuario.getNombres());
        usuarioFromDB.setApellidos(usuario.getApellidos());
        usuarioFromDB.setEmail(usuario.getEmail());

        usuarioRepository.save(usuarioFromDB);
        ra.addFlashAttribute("msgExito", "El usuario se actualizó correctamente");
        return "redirect:/admin/usuarios";
    }

    @PostMapping("/eliminar/{id}")
    String eliminar(@PathVariable("id") Integer id, RedirectAttributes ra)
    {
        usuarioRepository.deleteById(id);
        ra.addFlashAttribute("msgExito", "Usuario eliminado exitosamente");
        return "redirect:/admin/usuarios";
    }
}
