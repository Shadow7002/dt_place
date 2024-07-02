package pe.shadow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.shadow.model.Opcion;
import pe.shadow.model.Pregunta;

import java.util.List;

@Repository
public interface OpcionRepository extends JpaRepository<Opcion, Integer> {

    List<Opcion> findByPreguntaIdAndEliminado(Integer idpregunta, Integer eliminado);

    List<Opcion> findByPreguntaAndEliminado(Pregunta pregunta, Integer eliminado);
}
