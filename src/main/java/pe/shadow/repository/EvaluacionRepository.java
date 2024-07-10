package pe.shadow.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.shadow.model.Cuestionario;
import pe.shadow.model.Evaluacion;
import pe.shadow.model.Inscripcion;
import pe.shadow.model.Usuario;

import java.util.List;

@Repository
public interface EvaluacionRepository extends JpaRepository<Evaluacion, Integer> {
    boolean existsByCuestionarioAndUsuarioCreacion(Cuestionario cuestionario, Usuario usuarioCreacion);

    Page<Evaluacion> findByUsuarioCreacion_EmailContaining(String email, Pageable pageable);

    List<Evaluacion> findByUsuarioCreacion(Usuario usuario);
}