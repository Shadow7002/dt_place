package pe.shadow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.shadow.model.Archivo;
import pe.shadow.model.Capa;

import java.util.List;

public interface ArchivoRepository extends JpaRepository<Archivo, Integer> {
    List<Archivo> findAllByIdcapa(Integer idCapa);

    List<Archivo> findByIdcapa(Integer idCapa);

    List<Archivo> findByIdcapaAndEliminado(Integer idcapa, Integer eliminado);
}
