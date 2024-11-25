package com.brainboost.brainboost.Repository;

import com.brainboost.brainboost.Entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    UserDetails findByLogin(String login);

    Usuario findBytokenRecSenha(String token);

    Usuario findBytokenConfirmacao(String token);

}
