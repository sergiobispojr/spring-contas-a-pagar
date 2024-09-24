package com.sergiobispo.pagamentos.application.service;

import com.sergiobispo.pagamentos.application.service.UsuarioService;
import com.sergiobispo.pagamentos.domain.entities.Usuario;
import com.sergiobispo.pagamentos.exception.NotFoundException;
import com.sergiobispo.pagamentos.infrastructure.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void testSave() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setSenha("1234");

        when(passwordEncoder.encode(any(CharSequence.class))).thenReturn("encodedSenha123");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        Usuario savedUsuario = usuarioService.save(usuario);

        assertNotNull(savedUsuario);
        assertEquals(1L, savedUsuario.getId());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void testUpdateUsuarioExistente() {
        Long id = 1L;
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setSenha("1234");

        when(passwordEncoder.encode(any(CharSequence.class))).thenReturn("encodedSenha123");
        when(usuarioRepository.existsById(id)).thenReturn(true);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        Usuario updatedUsuario = usuarioService.update(id, usuario);

        assertNotNull(updatedUsuario);
        assertEquals(id, updatedUsuario.getId());
        verify(usuarioRepository).existsById(id);
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void testUpdateUsuarioNaoExistente() {
        Long id = 1L;
        Usuario usuario = new Usuario();
        usuario.setId(id);

        when(usuarioRepository.existsById(id)).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            usuarioService.update(id, usuario);
        });

        assertEquals("Usuário não encontrado com o ID: " + id, exception.getMessage());
        verify(usuarioRepository).existsById(id);
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 10);
        Usuario usuario1 = new Usuario();
        usuario1.setId(1L);
        Usuario usuario2 = new Usuario();
        usuario2.setId(2L);

        Page<Usuario> page = new PageImpl<>(List.of(usuario1, usuario2));

        when(usuarioRepository.findAll(pageable)).thenReturn(page);

        Page<Usuario> result = usuarioService.findAll(pageable);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        verify(usuarioRepository).findAll(pageable);
    }

    @Test
    void testFindByIdUsuarioExistente() {
        Long id = 1L;
        Usuario usuario = new Usuario();
        usuario.setId(id);

        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));

        Usuario foundUsuario = usuarioService.findById(id);

        assertNotNull(foundUsuario);
        assertEquals(id, foundUsuario.getId());
        verify(usuarioRepository).findById(id);
    }

    @Test
    void testFindByIdUsuarioNaoExistente() {
        Long id = 1L;

        when(usuarioRepository.findById(id)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            usuarioService.findById(id);
        });

        assertEquals(String.format("Usuário com o id: %d não encontrado.", id), exception.getMessage());
        verify(usuarioRepository).findById(id);
    }

    @Test
    void testDeleteUsuarioExistente() {
        Long id = 1L;

        when(usuarioRepository.existsById(id)).thenReturn(true);
        doNothing().when(usuarioRepository).deleteById(id);

        usuarioService.delete(id);

        verify(usuarioRepository).existsById(id);
        verify(usuarioRepository).deleteById(id);
    }

    @Test
    void testDeleteUsuarioNaoExistente() {
        Long id = 1L;

        when(usuarioRepository.existsById(id)).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            usuarioService.delete(id);
        });

        assertEquals("Usuário não encontrado com o ID: " + id, exception.getMessage());
        verify(usuarioRepository).existsById(id);
        verify(usuarioRepository, never()).deleteById(id);
    }
}