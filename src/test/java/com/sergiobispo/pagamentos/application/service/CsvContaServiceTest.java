package com.sergiobispo.pagamentos.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.sergiobispo.pagamentos.application.service.CsvContaService;
import com.sergiobispo.pagamentos.application.service.UsuarioService;
import com.sergiobispo.pagamentos.domain.entities.Usuario;
import com.sergiobispo.pagamentos.infrastructure.repository.ContaRepository;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CsvContaServiceTest {

    @Mock
    private ContaRepository contaRepository;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private CsvContaService csvContaService;

    @Test
    void testImportCsvFile() throws Exception {
        String csvContent = """
                UsuarioId,Nome,Descricao,Valor,DataVencimento
                1,Conta 1,Descricao 1,100.00,01/01/2023
                1,Conta 2,Descricao 2,200.00,02/02/2023
                """;

        MultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", csvContent.getBytes(StandardCharsets.UTF_8));

        Usuario usuario = new Usuario();
        usuario.setId(1L);

        when(usuarioService.findById(1L)).thenReturn(usuario);

        csvContaService.importCsvFile(file);

        verify(usuarioService, times(2)).findById(1L);
        verify(contaRepository, times(1)).saveAll(anyList());
    }

    @Test
    void testImportCsvFileThrowsException() {
        String csvContent = """
                UsuarioId,Nome,Descricao,Valor,DataVencimento
                1,Conta 1,Descricao 1,100.00,invalid-date
                """;

        MultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", csvContent.getBytes(StandardCharsets.UTF_8));

        Exception exception = assertThrows(Exception.class, () -> {
            csvContaService.importCsvFile(file);
        });

        String expectedMessage = "Text 'invalid-date' could not be parsed";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}