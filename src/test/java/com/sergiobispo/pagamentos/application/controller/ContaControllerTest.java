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
import org.springframework.web.multipart.MultipartFile;

import com.sergiobispo.pagamentos.application.dto.ContaDto;
import com.sergiobispo.pagamentos.application.dto.MensagemDto;
import com.sergiobispo.pagamentos.application.service.ContaService;
import com.sergiobispo.pagamentos.application.service.CsvContaService;
import com.sergiobispo.pagamentos.application.service.UsuarioService;
import com.sergiobispo.pagamentos.domain.entities.Conta;
import com.sergiobispo.pagamentos.domain.entities.Usuario;
import com.sergiobispo.pagamentos.exception.NullParameterException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContaControllerTest {


    @Mock
    private ContaService contaService;

    @Mock
    private CsvContaService csvContaService;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private ContaController contaController;

    @Mock
    private MultipartFile file;

    @Test
    void criarContaTestSucesso() {
        ContaDto contaDto = new ContaDto();
        contaDto.setNome("Teste");
        contaDto.setDescricao("Descrição do teste");
        contaDto.setValor(new BigDecimal("100.00"));
        contaDto.setDataVencimento(LocalDate.now());

        Conta conta = contaDto.toConta();

        when(contaService.save(any(Conta.class))).thenReturn(conta);

        ResponseEntity<ContaDto> response = contaController.criarConta(contaDto);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Teste", Objects.requireNonNull(response.getBody()).getNome());
        assertEquals("Descrição do teste", response.getBody().getDescricao());

        verify(contaService).save(any(Conta.class));
    }

    @Test
    void criarContaTestNullParameterException() {

        NullParameterException thrown = assertThrows(
                NullParameterException.class,
                () -> contaController.criarConta(null),
                "Esperado que criarConta() lance NullParameterException"
        );

        assertEquals("Conta não pode ser null", thrown.getMessage());
    }

    @Test
    void atualizarContaTest() {
        Long id = 1L;
        ContaDto contaDto = new ContaDto();
        contaDto.setNome("Teste");
        contaDto.setDescricao("Descrição do teste");
        contaDto.setValor(new BigDecimal("100.00"));
        contaDto.setDataVencimento(LocalDate.now());
        contaDto.setId(id);

        Conta conta = contaDto.toConta();

        when(contaService.update(eq(id), any(Conta.class))).thenReturn(conta);

        ResponseEntity<ContaDto> response = contaController.atualizarConta(id, contaDto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(id, response.getBody().getId());
    }


    @Test
    void pagamentoTest() {
        Long id = 1L;
        Long usuarioId = 1L;
        BigDecimal saldoRetornado = new BigDecimal("150.00");

        when(contaService.pagarConta(id, usuarioId)).thenReturn(saldoRetornado);

        ResponseEntity<MensagemDto> response = contaController.pagamento(id, usuarioId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Pagamento registrado com sucesso. Você possui um saldo de: R$150.00", response.getBody().getMensagem());

        verify(contaService).pagarConta(id, usuarioId);
    }

    @Test
    void listarContasTest() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        Conta conta = new Conta();
        conta.setUsuario(usuario);

        Page<Conta> contas = new PageImpl<>(List.of(conta));

        when(contaService.findAll(any(PageRequest.class))).thenReturn(contas);

        PageRequest pageable = PageRequest.of(0, 10);

        ResponseEntity<Page<ContaDto>> response = contaController.listarContas(pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
        assertEquals(1, response.getBody().getTotalElements());
        verify(contaService).findAll(pageable);
    }

    @Test
    void buscarContaPorIdTest() {
        Long id = 1L;
        ContaDto contaDto = new ContaDto();
        contaDto.setNome("Teste");
        contaDto.setDescricao("Descrição do teste");
        contaDto.setValor(new BigDecimal("100.00"));
        contaDto.setDataVencimento(LocalDate.now());
        contaDto.setId(id);

        Conta conta = contaDto.toConta();

        when(contaService.findById(id)).thenReturn(conta);

        ResponseEntity<ContaDto> response = contaController.buscarContaPorId(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(id, response.getBody().getId());

        verify(contaService).findById(id);
    }

    @Test
    void getTotalPagoTest() {
        Long usuarioId = 1L;
        String dataInicio = "2024-01-01";
        String dataFim = "2024-01-31";
        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setNome("Teste Usuario");
        BigDecimal totalPago = new BigDecimal("300.00");

        when(usuarioService.findById(usuarioId)).thenReturn(usuario);
        when(contaService.getTotalPago(LocalDate.parse(dataInicio), LocalDate.parse(dataFim), usuario)).thenReturn(totalPago);

        ResponseEntity<MensagemDto> response = contaController.getTotalPago(dataInicio, dataFim, usuarioId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("O usuário Teste Usuario pagou um total em contas de: R$300.00", Objects.requireNonNull(response.getBody()).getMensagem());

        verify(usuarioService).findById(usuarioId);
        verify(contaService).getTotalPago(any(LocalDate.class), any(LocalDate.class), any(Usuario.class));
    }

    @Test
    void getTodasContasPendentesTest() {
        String dataInicio = "2024-01-01";
        String dataFim = "2024-01-31";
        List<ContaDto> contasPendentes = Arrays.asList(new ContaDto(), new ContaDto()); // Suponha que ContaDto está configurado adequadamente

        when(contaService.getTodasContasPendentes(LocalDate.parse(dataInicio), LocalDate.parse(dataFim))).thenReturn(contasPendentes);

        ResponseEntity<List<ContaDto>> response = contaController.getTodasContasPendentes(dataInicio, dataFim);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, Objects.requireNonNull(response.getBody()).size());
        verify(contaService).getTodasContasPendentes(any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    void getContasPendentesUsuarioTest() {
        Long usuarioId = 1L;
        String dataInicio = "2024-01-01";
        String dataFim = "2024-01-31";
        List<ContaDto> contasPendentes = Arrays.asList(new ContaDto(), new ContaDto()); // Suponha que ContaDto está configurado adequadamente

        when(contaService.getContasPendentesUsuario(LocalDate.parse(dataInicio), LocalDate.parse(dataFim), usuarioId)).thenReturn(contasPendentes);

        ResponseEntity<List<ContaDto>> response = contaController.getContasPendentesUsuario(dataInicio, dataFim, usuarioId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size()); // Verifica se a quantidade de contas retornadas é a esperada
        verify(contaService).getContasPendentesUsuario(any(LocalDate.class), any(LocalDate.class), eq(usuarioId));
    }

    @Test
    void deleteContaTest() {
        Long id = 1L;

        doNothing().when(contaService).delete(id);

        ResponseEntity<Void> response = contaController.deleteConta(id);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(contaService).delete(id);
    }

    @Test
    void getContasByUsuarioIdTest() {
        Long usuarioId = 1L;
        List<ContaDto> contasPendentes = Arrays.asList(new ContaDto(), new ContaDto()); // Suponha que ContaDto está configurado adequadamente

        when(contaService.findByUsuarioId(usuarioId)).thenReturn(contasPendentes);

        ResponseEntity<List<ContaDto>> response = contaController.getContasByUsuarioId(usuarioId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size()); // Verifica se a quantidade de contas retornadas é a esperada
        verify(contaService).findByUsuarioId(usuarioId);
    }

    @Test
    void uploadCsvFileTestSuccesso() throws Exception {
        lenient().when(file.isEmpty()).thenReturn(false);
        doNothing().when(csvContaService).importCsvFile(file);

        ResponseEntity<String> response = contaController.uploadCsvFile(file);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Arquivo processado com sucesso e contas criadas.", response.getBody());
        verify(csvContaService).importCsvFile(file);
    }

    @Test
    void uploadCsvFileTestFalha() throws Exception {
        lenient().when(file.isEmpty()).thenReturn(false);
        doThrow(new RuntimeException("Falha no processamento")).when(csvContaService).importCsvFile(file);

        ResponseEntity<String> response = contaController.uploadCsvFile(file);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).contains("Erro ao processar o arquivo"));
        verify(csvContaService).importCsvFile(file);
    }

    @Test
    void listarContasFiltradasTest() {
        Pageable pageable = PageRequest.of(0, 10);
        LocalDate dataVencimento = LocalDate.now();
        String nome = "Teste";

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        Conta conta = new Conta();
        conta.setUsuario(usuario);

        Page<Conta> contasPage = new PageImpl<>(Collections.singletonList(conta));

        when(contaService.findByFilters(any(LocalDate.class), anyString(), any(Pageable.class))).thenReturn(contasPage);

        ResponseEntity<Page<ContaDto>> response = contaController.listarContasFiltradas(dataVencimento, nome, pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());

        verify(contaService, times(1)).findByFilters(dataVencimento, nome, pageable);
    }

}