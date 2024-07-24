package pe.shadow.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.shadow.model.Cuestionario;

import java.util.List;

@Repository
public interface CuestionarioRepository extends JpaRepository<Cuestionario, Integer> {

    Page<Cuestionario> findByNombreContainingAndEliminado(String nombre, int eliminado, Pageable pageable);
    Page<Cuestionario> findByEliminado(int eliminado, Pageable pageable);

    List<Cuestionario> findByIdAndEliminado(int id, int eliminado);
}
