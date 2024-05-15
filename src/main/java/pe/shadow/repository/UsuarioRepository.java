package pe.shadow.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pe.shadow.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByEmail(String email); //select * from usuario where email = ?

    boolean existsByEmail(String email); //exists (select * from usuario where email = ?)

    Page<Usuario> findByApellidosContaining(String apellidos, Pageable pageable);
}
