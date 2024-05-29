package pe.shadow.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import pe.shadow.model.Archivo;
import pe.shadow.model.Capa;
import pe.shadow.repository.ArchivoRepository;
import pe.shadow.repository.CapaRepository;

@Controller
@RequestMapping("/archivos")
public class ArchivoController {

    @Autowired
    private ArchivoRepository archivoRepository;

    @Autowired
    private CapaRepository capaRepository;

}
