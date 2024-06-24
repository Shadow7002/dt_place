package pe.shadow.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pe.shadow.model.Archivo;
import pe.shadow.model.Capa;
import pe.shadow.model.Usuario;
import pe.shadow.repository.ArchivoRepository;
import pe.shadow.repository.CapaRepository;
import pe.shadow.repository.UsuarioRepository;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/archivos")
public class ArchivoController {

    @Autowired
    private ArchivoRepository archivoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/{idCapa}")
    public ResponseEntity<List<Archivo>> listarArchivos(@PathVariable("idCapa") Integer idCapa) {
        List<Archivo> archivos = archivoRepository.findByIdcapaAndEliminado(idCapa,0);
        return ResponseEntity.ok(archivos);
    }


    @PostMapping("/subir")
    public ResponseEntity<List<Archivo>> subirArchivos(
            @RequestParam("idCapa") Integer idCapa,
            @RequestParam("archivos") MultipartFile[] files,
            @RequestParam("tipoArchivo") String tipoArchivo,
            Principal principal){


        List<Archivo> archivosGuardados = new ArrayList<>();

        // Obtener el email del usuario autenticado
        String email = principal.getName();
        Optional<Usuario> optionalUsuarioCreacion = usuarioRepository.findByEmail(email);

        // Verificar si el usuario fue encontrado
        if (!optionalUsuarioCreacion.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // o cualquier otro código de estado adecuado
        }

        Usuario usuarioCreacion = optionalUsuarioCreacion.get();

        for (MultipartFile file : files) {
            try {
                Archivo archivo = new Archivo();
                archivo.setIdcapa(idCapa);
                archivo.setNombre(file.getOriginalFilename());
                archivo.setFile(file.getBytes());
                archivo.setTipo(Archivo.Tipo.valueOf(tipoArchivo.toUpperCase()));
                archivo.setUsuarioCreacion(usuarioCreacion);
                archivo.setUsuarioActualizacion(usuarioCreacion);
                archivoRepository.save(archivo);
                archivosGuardados.add(archivo);
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }
        return ResponseEntity.ok(archivosGuardados);
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminarArchivo(@PathVariable Integer id) {
        Archivo archivo = archivoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("ID inválido: " + id));
        archivo.setEliminado(1);
        archivoRepository.save(archivo);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/descargar/{id}")
    public ResponseEntity<byte[]> descargarArchivo(@PathVariable Integer id, Principal principal) {
        // Obtener el archivo por ID
        Optional<Archivo> optionalArchivo = archivoRepository.findById(id);

        // Verificar si el archivo existe
        if (!optionalArchivo.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Archivo archivo = optionalArchivo.get();

        // Verificar si el archivo está eliminado
        if (archivo.getEliminado() == 1) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Obtener el email del usuario autenticado
        String email = principal.getName();
        Optional<Usuario> optionalUsuarioDescarga = usuarioRepository.findByEmail(email);

        // Verificar si el usuario fue encontrado
        if (!optionalUsuarioDescarga.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Establecer los encabezados de respuesta
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + archivo.getNombre() + "\"");
        headers.set(HttpHeaders.CONTENT_TYPE, "application/octet-stream");

        // Devolver el archivo como un array de bytes en el cuerpo de la respuesta
        return new ResponseEntity<>(archivo.getFile(), headers, HttpStatus.OK);
    }

}
