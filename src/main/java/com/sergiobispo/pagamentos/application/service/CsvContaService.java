package com.sergiobispo.pagamentos.application.service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sergiobispo.pagamentos.domain.entities.Conta;
import com.sergiobispo.pagamentos.domain.entities.Usuario;
import com.sergiobispo.pagamentos.domain.enums.Situacao;
import com.sergiobispo.pagamentos.infrastructure.repository.ContaRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class CsvContaService {

    @Autowired
    private ContaRepository contaRepository;

    @Autowired
    private UsuarioService usuarioService;

    public void importCsvFile(MultipartFile file) throws Exception {
        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());


            List<Conta> contas = new ArrayList<>();
            for (CSVRecord record : parser) {
                Long idUsuario = Long.valueOf(record.get("UsuarioId"));

                Usuario usuario = usuarioService.findById(idUsuario);

                Conta conta = new Conta();
                conta.setNome(record.get("Nome"));
                conta.setDescricao(record.get("Descricao"));
                conta.setValor(new BigDecimal(record.get("Valor")));
                conta.setDataVencimento(LocalDate.parse(record.get("DataVencimento"), DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                conta.setSituacao(Situacao.PENDENTE);
                conta.setUsuario(usuario);
                contas.add(conta);
            }

            contaRepository.saveAll(contas);
        }
    }
}