package com.empresa.usuarios_api.user;

import com.empresa.usuarios_api.controller.UsuarioController;
import com.empresa.usuarios_api.dtos.UsuarioRequest;
import com.empresa.usuarios_api.dtos.UsuarioResponse;
import com.empresa.usuarios_api.service.UsuarioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;


import java.util.List;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UsuarioController.class)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService usuarioService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void criar_Sucesso() throws Exception {
        UsuarioRequest request = new UsuarioRequest("Anna Jullia", "anna@email.com");
        UsuarioResponse response = new UsuarioResponse(1L, "Anna Jullia", "anna@email.com");

        when(usuarioService.criar(any(UsuarioRequest.class))).thenReturn(response);

        mockMvc.perform(post("/v1/usuarios").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request))).andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Deve retornar 400 quando houver erro de validação")
    void criar_ErroValidacao() throws Exception {
        UsuarioRequest requestInvalido = new UsuarioRequest("", "email-invalido");

        mockMvc.perform(post("/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest()); // Certifique-se de que está isBadRequest() e não isCreated()
    }

    @Test
    @DisplayName("Deve retornar 200 e lista de usuários")
    void listar_Sucesso() throws Exception {
        List<UsuarioResponse> lista = List.of(new UsuarioResponse(1L, "Anna", "anna@email.com"));

        when(usuarioService.listarTodos()).thenReturn(lista);

        mockMvc.perform(get("/v1/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Anna"));
    }

    @Test
    @DisplayName("Deve retornar 200 quando buscar por ID existente")
    void buscarPorId_Sucesso() throws Exception {
        UsuarioResponse response = new UsuarioResponse(1L, "Anna", "anna@email.com");

        when(usuarioService.buscarPorId(1L)).thenReturn(response);

        mockMvc.perform(get("/v1/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("Deve retornar 404 ao buscar ID inexistente")
    void buscarPorId_NaoEncontrado() throws Exception {
        // 1. Simulando que o service lança a exceção para o ID 99
        Mockito.when(usuarioService.buscarPorId(99L))
                .thenThrow(new RuntimeException("Usuário não encontrado"));

        // 2. Executando a requisição
        mockMvc.perform(get("/v1/usuarios/99"))
                .andExpect(status().isNotFound()); // Agora o Handler captura e retorna 404
    }
    @Test
    @DisplayName("Deve retornar 204 ao deletar usuário")
    void deletar_Sucesso() throws Exception {
        mockMvc.perform(delete("/v1/usuarios/1"))
                .andExpect(status().isNoContent());
    }
}