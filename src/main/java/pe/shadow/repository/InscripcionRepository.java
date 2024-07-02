package pe.shadow.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.shadow.model.Capa;
import pe.shadow.model.Inscripcion;
import pe.shadow.model.Usuario;

import java.util.List;
import java.util.Optional;

@Repository
public interface InscripcionRepository extends JpaRepository<Inscripcion, Integer> {
    Page<Inscripcion> findById(int id, Pageable pageable);
    Page<Inscripcion> findByUsuarioEmailContainingAndEliminado(String email, int eliminado, Pageable pageable);
    Page<Inscripcion> findByEliminado(int eliminado, Pageable pageable);
    boolean existsByCapaAndUsuario(Capa capa, Usuario usuario);
    List<Inscripcion> findByUsuarioAndEliminado(Usuario usuario, int eliminado);
}
