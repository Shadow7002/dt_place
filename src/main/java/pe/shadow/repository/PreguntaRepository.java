package pe.shadow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.shadow.model.Cuestionario;
import pe.shadow.model.Pregunta;

import java.util.List;

@Repository
public interface PreguntaRepository extends JpaRepository<Pregunta, Integer> {

    List<Pregunta> findByCuestionarioAndEliminado(Cuestionario cuestionario, Integer eliminado);

}
