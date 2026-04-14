package com.empresa.usuarios_api.user;

import com.empresa.usuarios_api.dtos.UsuarioRequest;
import com.empresa.usuarios_api.dtos.UsuarioResponse;
import com.empresa.usuarios_api.entity.Usuario;
import com.empresa.usuarios_api.exception.ResourceNotFoundException;
import com.empresa.usuarios_api.repository.UsuarioRepository;
import com.empresa.usuarios_api.service.UsuarioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest{

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    @DisplayName("Deve criar um usuário com sucesso")
    void criarSucesso() {
        UsuarioRequest request = new UsuarioRequest("joao", "joao@email.com");
        Usuario usuarioSalvo = new Usuario(1L, "joão", "joao@email.com");
        when(usuarioRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioSalvo);

        //ACT
        UsuarioResponse response = usuarioService.criar(request);
        
        //ASSERT
        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("joão", response.nome());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve retornar lista de usuários")
    void lsitarTodos() {

        Usuario u1 = new Usuario(1L, "Ana", "ana@email.com");
        when(usuarioRepository.findAll()).thenReturn(List.of(u1));

        //ACT
        List<UsuarioResponse> lista = usuarioService.listarTodos();

        //ASSERT
        assertFalse(lista.isEmpty());
        assertEquals(1, lista.size());
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver usuario")
    void listarTodos_Vazio() {

        when(usuarioRepository.findAll()).thenReturn(Collections.emptyList());

        //ACT
        List<UsuarioResponse> lista = usuarioService.listarTodos();

        //ASSERT
        assertTrue(lista.isEmpty());
    }

    @Test
    @DisplayName("Deve buscar usuário por Id com sucesso")
    void buscarPorId() {

        Long id = 1L;
        Usuario usuario = new Usuario(id,"Carlos", "carlos@email.com");
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));

        UsuarioResponse response = usuarioService.buscarPorId(id);

        assertEquals(id, response.id());
        assertEquals("Carlos", response.nome());
    }

    @Test
    @DisplayName("Deve dar o erro ResourceNotFoundException quando ID não existir na busca")
    void buscarPorId_Erro() {
        Long id = 99L;
        when(usuarioRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> usuarioService.buscarPorId(id));
    }
    @Test
    @DisplayName("Deve deletar usuário com sucesso")
    void deletar_Sucesso() {
        // Arrange
        Long id = 1L;
        when(usuarioRepository.existsById(id)).thenReturn(true);

        // Act
        usuarioService.deletar(id);

        // Assert
        verify(usuarioRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Deve lançar erro ao tentar deletar usuário inexistente")
    void deletar_ErroNaoEncontrado() {
        // Arrange
        Long id = 1L;
        when(usuarioRepository.existsById(id)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> usuarioService.deletar(id));
        verify(usuarioRepository, never()).deleteById(anyLong());
    }

    // --- TESTES DO MÉTODO ATUALIZAR EMAIL ---

    @Test
    @DisplayName("Deve atualizar email com sucesso")
    void atualizarEmail_Sucesso() {
        // Arrange
        Long id = 1L;
        String novoEmail = "novo@email.com";
        Usuario usuario = new Usuario(id, "Maria", "velho@email.com");
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));

        // Act
        usuarioService.atualizarEmail(id, novoEmail);

        // Assert
        assertEquals(novoEmail, usuario.getEmail());
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    @DisplayName("Deve lançar erro ao atualizar email de usuário inexistente")
    void atualizarEmail_ErroNaoEncontrado() {
        // Arrange
        Long id = 1L;
        when(usuarioRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> usuarioService.atualizarEmail(id, "teste@email.com"));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }
}
