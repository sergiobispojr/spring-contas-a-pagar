package com.sergiobispo.pagamentos.application.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.sergiobispo.pagamentos.application.dto.UsuarioDto;
import com.sergiobispo.pagamentos.application.service.UsuarioService;
import com.sergiobispo.pagamentos.domain.entities.Usuario;
import com.sergiobispo.pagamentos.exception.NotFoundException;
import com.sergiobispo.pagamentos.exception.NullParameterException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioController usuarioController;

    @Test
    void criarUsuarioTestSucesso() {
        UsuarioDto usuarioDto = new UsuarioDto();
        usuarioDto.setNome("Teste");
        usuarioDto.setEmail("teste@example.com");
        usuarioDto.setSenha("senha123");

        Usuario usuario = usuarioDto.toUsuario();

        when(usuarioService.save(any(Usuario.class))).thenReturn(usuario);

        ResponseEntity<UsuarioDto> response = usuarioController.criarUsuario(usuarioDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(usuario.getId(), response.getBody().getId());
        assertEquals(usuario.getNome(), response.getBody().getNome());
        assertEquals(usuario.getEmail(), response.getBody().getEmail());

        verify(usuarioService).save(any(Usuario.class));
    }

    @Test
    void criarUsuarioNullTest() {
        NullParameterException exception = assertThrows(NullParameterException.class, () -> {
            usuarioController.criarUsuario(null);
        });

        assertEquals("Usuário não pode ser null", exception.getMessage());
    }

    @Test
    void atualizarUsuarioTest() {
        Long id = 1L;
        UsuarioDto usuarioDto = new UsuarioDto();
        usuarioDto.setId(id);
        usuarioDto.setNome("Teste Atualizado");
        usuarioDto.setEmail("testeatualizado@example.com");
        usuarioDto.setSenha("senha123");

        Usuario usuario = usuarioDto.toUsuario();

        when(usuarioService.update(eq(id), any(Usuario.class))).thenReturn(usuario);

        ResponseEntity<UsuarioDto> response = usuarioController.atualizarUsuario(id, usuarioDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(usuario.getId(), response.getBody().getId());
        assertEquals(usuario.getNome(), response.getBody().getNome());
        assertEquals(usuario.getEmail(), response.getBody().getEmail());

        verify(usuarioService).update(eq(id), any(Usuario.class));
    }

    @Test
    void atualizarUsuarioNotFoundTest() {
        Long id = 1L;
        UsuarioDto usuarioDto = new UsuarioDto();
        usuarioDto.setNome("Teste Atualizado");
        usuarioDto.setEmail("testeatualizado@example.com");
        usuarioDto.setSenha("senha123");

        when(usuarioService.update(eq(id), any(Usuario.class))).thenThrow(new NotFoundException("Usuário não encontrado"));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            usuarioController.atualizarUsuario(id, usuarioDto);
        });

        assertEquals("Usuário não encontrado", exception.getMessage());
        verify(usuarioService).update(eq(id), any(Usuario.class));
    }

    @Test
    void listarUsuariosTest() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Teste");
        usuario.setEmail("teste@example.com");
        usuario.setSenha("senha123");

        Page<Usuario> usuarios = new PageImpl<>(List.of(usuario));

        when(usuarioService.findAll(any(Pageable.class))).thenReturn(usuarios);

        Pageable pageable = PageRequest.of(0, 20);
        ResponseEntity<Page<UsuarioDto>> response = usuarioController.listarUsuarios(pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
        assertEquals(1, response.getBody().getTotalElements());

        verify(usuarioService).findAll(pageable);
    }

    @Test
    void buscarUsuarioPorIdTest() {
        Long id = 1L;
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setNome("Teste");
        usuario.setEmail("teste@example.com");
        usuario.setSenha("senha123");

        when(usuarioService.findById(id)).thenReturn(usuario);

        ResponseEntity<UsuarioDto> response = usuarioController.buscarUsuarioPorId(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(usuario.getId(), response.getBody().getId());
        assertEquals(usuario.getNome(), response.getBody().getNome());
        assertEquals(usuario.getEmail(), response.getBody().getEmail());

        verify(usuarioService).findById(id);
    }

    @Test
    void buscarUsuarioPorIdNotFoundTest() {
        Long id = 1L;

        when(usuarioService.findById(id)).thenThrow(new NotFoundException("Usuário não encontrado"));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            usuarioController.buscarUsuarioPorId(id);
        });

        assertEquals("Usuário não encontrado", exception.getMessage());
        verify(usuarioService).findById(id);
    }

    @Test
    void deleteUsuarioTest() {
        Long id = 1L;

        doNothing().when(usuarioService).delete(id);

        ResponseEntity<Void> response = usuarioController.deleteUsuario(id);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(usuarioService).delete(id);
    }

    @Test
    void deleteUsuarioNotFoundTest() {
        Long id = 1L;

        doThrow(new NotFoundException("Usuário não encontrado")).when(usuarioService).delete(id);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            usuarioController.deleteUsuario(id);
        });

        assertEquals("Usuário não encontrado", exception.getMessage());
        verify(usuarioService).delete(id);
    }
}