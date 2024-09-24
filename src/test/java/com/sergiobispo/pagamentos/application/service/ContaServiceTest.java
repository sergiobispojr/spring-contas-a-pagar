package com.sergiobispo.pagamentos.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.sergiobispo.pagamentos.application.dto.ContaDto;
import com.sergiobispo.pagamentos.application.service.ContaService;
import com.sergiobispo.pagamentos.domain.entities.Conta;
import com.sergiobispo.pagamentos.domain.entities.Usuario;
import com.sergiobispo.pagamentos.domain.enums.Situacao;
import com.sergiobispo.pagamentos.exception.ContaJaPagaException;
import com.sergiobispo.pagamentos.exception.NotFoundException;
import com.sergiobispo.pagamentos.exception.SaldoInsuficienteException;
import com.sergiobispo.pagamentos.exception.UsuarioSemPermissaoException;
import com.sergiobispo.pagamentos.infrastructure.repository.ContaRepository;
import com.sergiobispo.pagamentos.infrastructure.repository.UsuarioRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ContaServiceTest {

    @Mock
    private ContaRepository contaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private ContaService contaService;

    @Test
    void testSave() {
        Conta conta = new Conta();
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        conta.setUsuario(usuario);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(contaRepository.save(conta)).thenReturn(conta);

        Conta savedConta = contaService.save(conta);

        assertEquals(conta, savedConta);
        verify(usuarioRepository).findById(1L);
        verify(contaRepository).save(conta);
    }

    @Test
    void testUpdate() {
        Long id = 1L;
        Conta conta = new Conta();
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        conta.setUsuario(usuario);

        Conta contaAntiga = new Conta();
        contaAntiga.setId(id);

        when(contaRepository.findById(id)).thenReturn(Optional.of(contaAntiga));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(contaRepository.save(conta)).thenReturn(conta);

        Conta updatedConta = contaService.update(id, conta);

        assertEquals(conta, updatedConta);
        verify(contaRepository).findById(id);
        verify(usuarioRepository).findById(1L);
        verify(contaRepository).save(conta);
    }

    @Test
    void testPagarConta() {
        Long id = 1L;
        Long usuarioId = 1L;
        Conta conta = new Conta();
        conta.setId(id);
        conta.setValor(new BigDecimal("100.00"));
        conta.setSituacao(Situacao.PENDENTE);

        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setSaldo(new BigDecimal("200.00"));

        conta.setUsuario(usuario);

        when(contaRepository.findById(id)).thenReturn(Optional.of(conta));
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(contaRepository.save(conta)).thenReturn(conta);
        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        BigDecimal saldo = contaService.pagarConta(id, usuarioId);

        assertEquals(new BigDecimal("100.00"), saldo);
        assertEquals(Situacao.PAGO, conta.getSituacao());
        verify(contaRepository).findById(id);
        verify(usuarioRepository).findById(usuarioId);
        verify(contaRepository).save(conta);
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void testPagarContaUsuarioSemPermissao() {
        Long id = 1L;
        Long usuarioId = 1L;
        Conta conta = new Conta();
        Usuario usuario1 = new Usuario();
        usuario1.setId(1L);
        Usuario usuario2 = new Usuario();
        usuario2.setId(2L);

        conta.setUsuario(usuario2);

        when(contaRepository.findById(id)).thenReturn(Optional.of(conta));
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario1));

        UsuarioSemPermissaoException exception = assertThrows(UsuarioSemPermissaoException.class, () -> {
            contaService.pagarConta(id, usuarioId);
        });

        assertEquals(usuario1.getNome() + " não tem permissão para fazer o pagamento dessa conta.", exception.getMessage());
        verify(contaRepository).findById(id);
        verify(usuarioRepository).findById(usuarioId);
    }

    @Test
    void testPagarContaSaldoInsuficiente() {
        Long id = 1L;
        Long usuarioId = 1L;
        Conta conta = new Conta();
        conta.setValor(new BigDecimal("100.00"));
        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setSaldo(new BigDecimal("50.00"));

        conta.setUsuario(usuario);

        when(contaRepository.findById(id)).thenReturn(Optional.of(conta));
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));

        SaldoInsuficienteException exception = assertThrows(SaldoInsuficienteException.class, () -> {
            contaService.pagarConta(id, usuarioId);
        });

        assertEquals("O usuário não possui saldo suficiente.", exception.getMessage());
        verify(contaRepository).findById(id);
        verify(usuarioRepository).findById(usuarioId);
    }

    @Test
    void testPagarContaJaPaga() {
        Long id = 1L;
        Long usuarioId = 1L;
        Conta conta = new Conta();
        conta.setSituacao(Situacao.PAGO);
        conta.setValor(new BigDecimal("100.00"));

        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setSaldo(new BigDecimal("200.00"));

        conta.setUsuario(usuario);

        when(contaRepository.findById(id)).thenReturn(Optional.of(conta));
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));

        ContaJaPagaException exception = assertThrows(ContaJaPagaException.class, () -> {
            contaService.pagarConta(id, usuarioId);
        });

        assertEquals("Essa conta já foi paga.", exception.getMessage());
        verify(contaRepository).findById(id);
        verify(usuarioRepository).findById(usuarioId);
    }


    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Conta> contaPage = new PageImpl<>(List.of(new Conta()));

        when(contaRepository.findAll(pageable)).thenReturn(contaPage);

        Page<Conta> result = contaService.findAll(pageable);

        assertEquals(contaPage, result);
        verify(contaRepository).findAll(pageable);
    }

    @Test
    void testFindById() {
        Long id = 1L;
        Conta conta = new Conta();
        conta.setId(id);

        when(contaRepository.findById(id)).thenReturn(Optional.of(conta));

        Conta result = contaService.findById(id);

        assertEquals(conta, result);
        verify(contaRepository).findById(id);
    }

    @Test
    void testDelete() {
        Long id = 1L;

        when(contaRepository.existsById(id)).thenReturn(true);
        doNothing().when(contaRepository).deleteById(id);

        contaService.delete(id);

        verify(contaRepository).existsById(id);
        verify(contaRepository).deleteById(id);
    }

    @Test
    void testDeleteContaNaoExistente() {
        Long id = 1L;

        when(contaRepository.existsById(id)).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            contaService.delete(id);
        });

        assertEquals("Conta não encontrada com ID: " + id, exception.getMessage());
        verify(contaRepository).existsById(id);
        verify(contaRepository, never()).deleteById(id);
    }

    @Test
    void testGetTotalPago() {
        LocalDate dataInicio = LocalDate.now().minusDays(30);
        LocalDate dataFim = LocalDate.now();
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        Conta conta1 = new Conta();
        conta1.setUsuario(usuario);
        conta1.setDataPagamento(LocalDate.now().minusDays(10));
        conta1.setSituacao(Situacao.PAGO);
        conta1.setValor(new BigDecimal("100.00"));

        Conta conta2 = new Conta();
        conta2.setUsuario(usuario);
        conta2.setDataPagamento(LocalDate.now().minusDays(20));
        conta2.setSituacao(Situacao.PAGO);
        conta2.setValor(new BigDecimal("150.00"));

        List<Conta> contas = List.of(conta1, conta2);

        when(contaRepository.findAll()).thenReturn(contas);

        BigDecimal totalPago = contaService.getTotalPago(dataInicio, dataFim, usuario);

        assertEquals(new BigDecimal("250.00"), totalPago);
        verify(contaRepository).findAll();
    }

    @Test
    void testGetTodasContasPendentes() {
        LocalDate dataInicio = LocalDate.now().minusDays(30);
        LocalDate dataFim = LocalDate.now();

        Usuario usuario = new Usuario();
        usuario.setId(1L);

        Conta conta1 = new Conta();
        conta1.setUsuario(usuario);
        conta1.setSituacao(Situacao.PENDENTE);
        conta1.setDataVencimento(LocalDate.now().minusDays(10));

        Conta conta2 = new Conta();
        conta2.setUsuario(usuario);
        conta2.setSituacao(Situacao.PENDENTE);
        conta2.setDataVencimento(LocalDate.now().minusDays(20));

        List<Conta> contas = List.of(conta1, conta2);

        when(contaRepository.findAll()).thenReturn(contas);

        List<ContaDto> result = contaService.getTodasContasPendentes(dataInicio, dataFim);

        assertEquals(2, result.size());
        verify(contaRepository).findAll();
    }

    @Test
    void testGetContasPendentesUsuario() {
        LocalDate dataInicio = LocalDate.now().minusDays(30);
        LocalDate dataFim = LocalDate.now();
        Long usuarioId = 1L;

        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);

        Conta conta1 = new Conta();
        conta1.setUsuario(usuario);
        conta1.setSituacao(Situacao.PENDENTE);
        conta1.setDataVencimento(LocalDate.now().minusDays(10));

        Conta conta2 = new Conta();
        conta2.setUsuario(usuario);
        conta2.setSituacao(Situacao.PENDENTE);
        conta2.setDataVencimento(LocalDate.now().minusDays(20));

        List<Conta> contas = List.of(conta1, conta2);

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(contaRepository.findAll()).thenReturn(contas);

        List<ContaDto> result = contaService.getContasPendentesUsuario(dataInicio, dataFim, usuarioId);

        assertEquals(2, result.size());
        verify(usuarioRepository).findById(usuarioId);
        verify(contaRepository).findAll();
    }

    @Test
    void testFindByUsuarioIdUsuarioExistente() {
        Long usuarioId = 1L;
        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);

        Conta conta1 = new Conta();
        conta1.setId(1L);
        conta1.setUsuario(usuario);
        Conta conta2 = new Conta();
        conta2.setId(2L);
        conta2.setUsuario(usuario);

        List<Conta> contas = List.of(conta1, conta2);

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(contaRepository.findByUsuario(usuario)).thenReturn(contas);

        List<ContaDto> result = contaService.findByUsuarioId(usuarioId);

        assertEquals(2, result.size());
        assertEquals(conta1.getId(), result.get(0).getId());
        assertEquals(conta2.getId(), result.get(1).getId());
        verify(usuarioRepository).findById(usuarioId);
        verify(contaRepository).findByUsuario(usuario);
    }

    @Test
    void testFindByUsuarioIdUsuarioNaoExistente() {
        Long usuarioId = 1L;

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            contaService.findByUsuarioId(usuarioId);
        });

        assertEquals("Usuário não encontrado", exception.getMessage());
        verify(usuarioRepository).findById(usuarioId);
        verify(contaRepository, never()).findByUsuario(any());
    }

    @Test
    void testFindByFilters() {
        LocalDate data = LocalDate.of(2024, 7, 21);
        String nome = "Teste";
        Pageable pageable = PageRequest.of(0, 10);

        Conta conta = new Conta();
        Page<Conta> contasPage = new PageImpl<>(Collections.singletonList(conta));

        when(contaRepository.findByFilters(any(LocalDate.class), anyString(), any(Pageable.class))).thenReturn(contasPage);

        Page<Conta> result = contaService.findByFilters(data, nome, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(conta, result.getContent().get(0));
    }
}