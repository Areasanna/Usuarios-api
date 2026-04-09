package com.empresa.usuarios_api.service;

import com.empresa.usuarios_api.dtos.UsuarioRequest;
import com.empresa.usuarios_api.dtos.UsuarioResponse;
import com.empresa.usuarios_api.entity.Usuario;
import com.empresa.usuarios_api.exception.ResourceNotFoundException;
import com.empresa.usuarios_api.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }
    public UsuarioResponse criar(UsuarioRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("E-mail já cadastrado: " +
                    request.getEmail());
        }
        Usuario usuario = new Usuario();
        usuario.setNome(request.getNome());
        usuario.setEmail(request.getEmail());
        Usuario salvo = usuarioRepository.save(usuario);
        return new UsuarioResponse(salvo.getId(), salvo.getNome(),
                salvo.getEmail());
    }
    public List<UsuarioResponse> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(u -> new UsuarioResponse(u.getId(), u.getNome(), u.getEmail()))
                .toList();
    }
    public UsuarioResponse buscarPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário ID " + id +
                        " não encontrado"));
        return new UsuarioResponse(usuario.getId(), usuario.getNome(),
                usuario.getEmail());
    }
    public void deletar(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuário ID " + id + " não encontrado");
        }
        usuarioRepository.deleteById(id);
    }
}
