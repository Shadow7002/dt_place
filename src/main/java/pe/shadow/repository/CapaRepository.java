package pe.shadow.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.shadow.model.Capa;
import pe.shadow.model.Inscripcion;

import java.util.List;
import java.util.Optional;

@Repository
public interface CapaRepository extends JpaRepository<Capa, Integer> {
    Page<Capa> findByNombreContainingAndEliminado(String nombre, int eliminado, Pageable pageable);
    Page<Capa> findByEliminado(int eliminado, Pageable pageable);
    Optional<Capa> findByIdAndEliminado(int id, int eliminado);
    Page<Capa> findByEstadoAndEliminado(Capa.Estado estado, Integer eliminado, Pageable pageable);
    Page<Capa> findByNombreContainingAndEstadoAndEliminado(String nombre, Capa.Estado estado, Integer eliminado, Pageable pageable);
    List<Capa> findTop5ByOrderByFechaCreacionDesc();
}
