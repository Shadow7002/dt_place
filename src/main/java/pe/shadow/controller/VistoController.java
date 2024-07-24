package pe.shadow.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pe.shadow.model.Archivo;
import pe.shadow.model.Usuario;
import pe.shadow.model.Visto;
import pe.shadow.repository.ArchivoRepository;
import pe.shadow.repository.UsuarioRepository;
import pe.shadow.repository.VistoRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/archivo")
public class VistoController {

    @Autowired
    private VistoRepository VistoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ArchivoRepository archivoRepository;

    @PostMapping("/visto")
    public String registrarArchivoVisto(@RequestParam("idfile") Integer idfile,
                                        @AuthenticationPrincipal UserDetails userDetails,
                                        @RequestHeader(value = HttpHeaders.REFERER, required = false) String referer) {

        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        Archivo archivo = archivoRepository.findById(idfile)
                .orElseThrow(() -> new IllegalArgumentException("Archivo no encontrado"));

        Visto archivoVisto = new Visto();
        archivoVisto.setArchivo(archivo);
        archivoVisto.setUsuarioCreacion(usuario);

        // Verificar si ya se ha marcado como visto
        if (VistoRepository.findByUsuarioCreacionAndArchivo(usuario, archivo).isEmpty()) {
            VistoRepository.save(archivoVisto);
        }

        // Redirigir a la URL de referencia o a la página de capacitación por defecto
        return "redirect:" + (referer != null ? referer : "/capas");
    }
}
