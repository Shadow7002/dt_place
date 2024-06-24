package pe.shadow.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.shadow.model.Capa;

import java.util.List;

@Repository
public interface CapaRepository extends JpaRepository<Capa, Integer> {
    Page<Capa> findByNombreContainingAndEliminado(String nombre, int eliminado, Pageable pageable);
    Page<Capa> findByEliminado(int eliminado, Pageable pageable);

    List<Capa> findTop5ByOrderByFechaCreacionDesc();
}
