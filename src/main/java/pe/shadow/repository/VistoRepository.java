package pe.shadow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.shadow.model.Archivo;
import pe.shadow.model.Capa;
import pe.shadow.model.Usuario;
import pe.shadow.model.Visto;

import java.util.List;

public interface VistoRepository extends JpaRepository<Visto, Integer> {
    List<Visto> findByUsuarioCreacionAndArchivo_Idcapa(Usuario usuario, Integer idcapa);

    List<Visto> findByUsuarioCreacionAndArchivo(Usuario usuario, Archivo archivo);
}
