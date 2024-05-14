package pe.shadow.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.shadow.model.Usuario;
import pe.shadow.repository.UsuarioRepository;

@Controller
public class RegistroController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/registro")
    public String index(Model model){
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }

    @PostMapping("/registro")
    public String crear(Model model, @Validated Usuario usuario, BindingResult br, RedirectAttributes ra){

        if (br.hasErrors()){
            model.addAttribute("usuario", usuario);
            return  "registro";
        }

        //validar si esl usuario o email ya existe en la base de datos
        String email = usuario.getEmail();
        boolean usuarioExiste = usuarioRepository.existsByEmail(email);

        if (usuarioExiste){
            br.rejectValue("email", "EmailAlredyExists");
        }

        //validar las contraseñas 1 y 2 coincidan
        if (!usuario.getPassword1().equals(usuario.getPassword2())){
            br.rejectValue("password1", "PasswordNotEquals");
        }

        if (br.hasErrors())
        {
            model.addAttribute("usuario", usuario);
            return "registro";
        }

        //si no hay errores de validacion registramos usuario
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword1()));
        usuario.setRol(Usuario.Rol.USUARIO);
        usuarioRepository.save(usuario);

        ra.addFlashAttribute("registroExitoso", "");
        return "redirect:/login";

    }
}
