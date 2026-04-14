package com.empresa.usuarios_api.user;

import com.empresa.usuarios_api.entity.Usuario;
import com.empresa.usuarios_api.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class UsuarioRepositoryTest{

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Retornar verdadeiro quando o email já existe")
    void existsByEmail() {

        Usuario usuario = new Usuario();
        usuario.setNome("Anna Jullia");
        usuario.setEmail("anna@email.com");
        entityManager.persist(usuario); // salva diretamento no banco

        boolean existe = usuarioRepository.existsByEmail("anna@email.com");

        assertThat(existe).isTrue();
    }

    @Test
    @DisplayName("Restornar falso se não existe o email")
    void existsByEmail_Erro() {
        
        boolean existe = usuarioRepository.existsByEmail("nao_existe"); 
        
        assertThat(existe).isFalse(); 
    }

    @Test
    @DisplayName("Deve salvar e recuperar um usuário corretamente")
    void salvarUsuario() {

        Usuario usuario = new Usuario();
        usuario.setNome("Joana");
        usuario.setEmail("joana@email.com");

        Usuario salvo = usuarioRepository.save(usuario);

        Usuario encontrado = entityManager.find(Usuario.class, salvo.getId());

        assertNotNull(encontrado);
        assertEquals("Joana", encontrado.getNome());
        assertEquals("joana@email.com", encontrado.getEmail());
    }
}