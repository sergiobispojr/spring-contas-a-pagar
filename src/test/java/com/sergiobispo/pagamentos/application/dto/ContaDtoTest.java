package com.sergiobispo.pagamentos.application.dto;

import org.junit.jupiter.api.Test;

import com.sergiobispo.pagamentos.application.dto.ContaDto;
import com.sergiobispo.pagamentos.domain.entities.Conta;
import com.sergiobispo.pagamentos.domain.entities.Usuario;
import com.sergiobispo.pagamentos.domain.enums.Situacao;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class ContaDtoTest {

    @Test
    void testConstructorWithConta() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        Conta conta = new Conta();
        conta.setId(1L);
        conta.setNome("Conta Teste");
        conta.setValor(new BigDecimal("100.00"));
        conta.setDescricao("Descrição Teste");
        conta.setObservacao("Observação Teste");
        conta.setDataPagamento(LocalDate.now());
        conta.setDataVencimento(LocalDate.now().plusDays(30));
        conta.setSituacao(Situacao.PENDENTE);
        conta.setUsuario(usuario);

        ContaDto contaDto = new ContaDto(conta);

        assertEquals(conta.getId(), contaDto.getId());
        assertEquals(conta.getNome(), contaDto.getNome());
        assertEquals(conta.getValor(), contaDto.getValor());
        assertEquals(conta.getDescricao(), contaDto.getDescricao());
        assertEquals(conta.getObservacao(), contaDto.getObservacao());
        assertEquals(conta.getDataPagamento(), contaDto.getDataPagamento());
        assertEquals(conta.getDataVencimento(), contaDto.getDataVencimento());
        assertEquals(conta.getSituacao(), contaDto.getSituacao());
        assertEquals(conta.getUsuario().getId(), contaDto.getUsuarioId());
    }

    @Test
    void testToConta() {
        ContaDto contaDto = new ContaDto();
        contaDto.setId(1L);
        contaDto.setNome("Conta Teste");
        contaDto.setValor(new BigDecimal("100.00"));
        contaDto.setDescricao("Descrição Teste");
        contaDto.setObservacao("Observação Teste");
        contaDto.setDataPagamento(LocalDate.now());
        contaDto.setDataVencimento(LocalDate.now().plusDays(30));
        contaDto.setSituacao(Situacao.PENDENTE);
        contaDto.setUsuarioId(1L);

        Conta conta = contaDto.toConta();

        assertEquals(contaDto.getId(), conta.getId());
        assertEquals(contaDto.getNome(), conta.getNome());
        assertEquals(contaDto.getValor(), conta.getValor());
        assertEquals(contaDto.getDescricao(), conta.getDescricao());
        assertEquals(contaDto.getObservacao(), conta.getObservacao());
        assertEquals(contaDto.getDataPagamento(), conta.getDataPagamento());
        assertEquals(contaDto.getDataVencimento(), conta.getDataVencimento());
        assertEquals(contaDto.getSituacao(), conta.getSituacao());
        assertNotNull(conta.getUsuario());
        assertEquals(contaDto.getUsuarioId(), conta.getUsuario().getId());
    }

    @Test
    void testGettersAndSetters() {
        ContaDto contaDto = new ContaDto();
        contaDto.setId(1L);
        contaDto.setNome("Conta Teste");
        contaDto.setValor(new BigDecimal("100.00"));
        contaDto.setDescricao("Descrição Teste");
        contaDto.setObservacao("Observação Teste");
        contaDto.setDataPagamento(LocalDate.now());
        contaDto.setDataVencimento(LocalDate.now().plusDays(30));
        contaDto.setSituacao(Situacao.PENDENTE);
        contaDto.setUsuarioId(1L);

        assertEquals(1L, contaDto.getId());
        assertEquals("Conta Teste", contaDto.getNome());
        assertEquals(new BigDecimal("100.00"), contaDto.getValor());
        assertEquals("Descrição Teste", contaDto.getDescricao());
        assertEquals("Observação Teste", contaDto.getObservacao());
        assertEquals(LocalDate.now(), contaDto.getDataPagamento());
        assertEquals(LocalDate.now().plusDays(30), contaDto.getDataVencimento());
        assertEquals(Situacao.PENDENTE, contaDto.getSituacao());
        assertEquals(1L, contaDto.getUsuarioId());
    }

    @Test
    void testNoArgsConstructor() {
        ContaDto contaDto = new ContaDto();
        assertNull(contaDto.getId());
        assertNull(contaDto.getNome());
        assertNull(contaDto.getValor());
        assertNull(contaDto.getDescricao());
        assertNull(contaDto.getObservacao());
        assertNull(contaDto.getDataPagamento());
        assertNull(contaDto.getDataVencimento());
        assertNull(contaDto.getSituacao());
        assertNull(contaDto.getUsuarioId());
    }

    @Test
    void testAllArgsConstructor() {
        LocalDate now = LocalDate.now();
        ContaDto contaDto = new ContaDto(1L, "Conta Teste", new BigDecimal("100.00"), "Descrição Teste",
                "Observação Teste", now, now.plusDays(30), Situacao.PENDENTE, 1L);

        assertEquals(1L, contaDto.getId());
        assertEquals("Conta Teste", contaDto.getNome());
        assertEquals(new BigDecimal("100.00"), contaDto.getValor());
        assertEquals("Descrição Teste", contaDto.getDescricao());
        assertEquals("Observação Teste", contaDto.getObservacao());
        assertEquals(now, contaDto.getDataPagamento());
        assertEquals(now.plusDays(30), contaDto.getDataVencimento());
        assertEquals(Situacao.PENDENTE, contaDto.getSituacao());
        assertEquals(1L, contaDto.getUsuarioId());
    }
}