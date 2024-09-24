package com.sergiobispo.pagamentos.application.dto;

import org.junit.jupiter.api.Test;

import com.sergiobispo.pagamentos.application.dto.UsuarioDto;
import com.sergiobispo.pagamentos.domain.entities.Usuario;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class UsuarioDtoTest {

    @Test
    void testConstructorWithUsuario() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Teste");
        usuario.setEmail("teste@example.com");
        usuario.setSenha("senha123");
        usuario.setSaldo(new BigDecimal("1000.00"));

        UsuarioDto usuarioDto = new UsuarioDto(usuario);

        assertEquals(usuario.getId(), usuarioDto.getId());
        assertEquals(usuario.getNome(), usuarioDto.getNome());
        assertEquals(usuario.getEmail(), usuarioDto.getEmail());
        assertEquals(usuario.getSenha(), usuarioDto.getSenha());
        assertEquals(usuario.getSaldo(), usuarioDto.getSaldo());
    }

    @Test
    void testToUsuario() {
        UsuarioDto usuarioDto = new UsuarioDto();
        usuarioDto.setId(1L);
        usuarioDto.setNome("Teste");
        usuarioDto.setEmail("teste@example.com");
        usuarioDto.setSenha("senha123");
        usuarioDto.setSaldo(new BigDecimal("1000.00"));

        Usuario usuario = usuarioDto.toUsuario();

        assertEquals(usuarioDto.getId(), usuario.getId());
        assertEquals(usuarioDto.getNome(), usuario.getNome());
        assertEquals(usuarioDto.getEmail(), usuario.getEmail());
        assertEquals(usuarioDto.getSenha(), usuario.getSenha());
        assertEquals(usuarioDto.getSaldo(), usuario.getSaldo());
    }

    @Test
    void testGettersAndSetters() {
        UsuarioDto usuarioDto = new UsuarioDto();
        usuarioDto.setId(1L);
        usuarioDto.setNome("Teste");
        usuarioDto.setEmail("teste@example.com");
        usuarioDto.setSenha("senha123");
        usuarioDto.setSaldo(new BigDecimal("1000.00"));

        assertEquals(1L, usuarioDto.getId());
        assertEquals("Teste", usuarioDto.getNome());
        assertEquals("teste@example.com", usuarioDto.getEmail());
        assertEquals("senha123", usuarioDto.getSenha());
        assertEquals(new BigDecimal("1000.00"), usuarioDto.getSaldo());
    }

    @Test
    void testNoArgsConstructor() {
        UsuarioDto usuarioDto = new UsuarioDto();
        assertNull(usuarioDto.getId());
        assertNull(usuarioDto.getNome());
        assertNull(usuarioDto.getEmail());
        assertNull(usuarioDto.getSenha());
        assertNull(usuarioDto.getSaldo());
    }

    @Test
    void testAllArgsConstructor() {
        UsuarioDto usuarioDto = new UsuarioDto(1L, "Teste", "teste@example.com", "senha123", new BigDecimal("1000.00"));

        assertEquals(1L, usuarioDto.getId());
        assertEquals("Teste", usuarioDto.getNome());
        assertEquals("teste@example.com", usuarioDto.getEmail());
        assertEquals("senha123", usuarioDto.getSenha());
        assertEquals(new BigDecimal("1000.00"), usuarioDto.getSaldo());
    }

    @Test
    void testEqualsAndHashCode() {
        UsuarioDto usuarioDto1 = new UsuarioDto(1L, "Teste", "teste@example.com", "senha123", new BigDecimal("1000.00"));
        UsuarioDto usuarioDto2 = new UsuarioDto(1L, "Teste", "teste@example.com", "senha123", new BigDecimal("1000.00"));

        assertEquals(usuarioDto1, usuarioDto2);
        assertEquals(usuarioDto1.hashCode(), usuarioDto2.hashCode());
    }

    @Test
    void testToString() {
        UsuarioDto usuarioDto = new UsuarioDto(1L, "Teste", "teste@example.com", "senha123", new BigDecimal("1000.00"));
        String expectedToString = "UsuarioDto(id=1, nome=Teste, email=teste@example.com, senha=senha123, saldo=1000.00)";

        assertEquals(expectedToString, usuarioDto.toString());
    }
}