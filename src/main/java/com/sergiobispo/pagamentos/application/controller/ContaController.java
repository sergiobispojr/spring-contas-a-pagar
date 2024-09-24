package com.sergiobispo.pagamentos.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
import java.util.List;


@RestController
@RequestMapping("/contas")
@Tag(name = "Contas")
public class ContaController {
    @Autowired
    private ContaService contaService;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private CsvContaService csvContaService;

    @Operation(summary = "Cria uma nova conta",
            description = "Faz a criação de uma nova conta.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = ContaDto.class))})
    })

    @PostMapping
    public ResponseEntity<ContaDto> criarConta(@Valid @RequestBody ContaDto contaDto) {

        if (contaDto == null) {
            throw new NullParameterException("Conta não pode ser null");
        }

        Conta novaConta = contaService.save(contaDto.toConta());
        ContaDto dto = new ContaDto(novaConta);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }


    @Operation(summary = "Atualiza uma conta.",
            description = "A partir de um ID passado no path uma conta será atualizada.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = ContaDto.class))}),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<ContaDto> atualizarConta(@PathVariable Long id, @RequestBody ContaDto contaDto) {
        Conta contaAtualizada = contaService.update(id, contaDto.toConta());
        ContaDto dto = new ContaDto(contaAtualizada);
        return ResponseEntity.ok(dto);
    }


    @Operation(summary = "Realiza o pagamento de uma conta.",
            description = "É necessário passar o ID de uma conta e o valor para ser realizado o pagamento.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = String.class))})
    })
    @GetMapping("/{id}/pagamento/usuario/{usuarioId}")
    public ResponseEntity<MensagemDto> pagamento(@PathVariable Long id, @PathVariable Long usuarioId) {
        BigDecimal saldo = contaService.pagarConta(id, usuarioId);
        return ResponseEntity.ok(new MensagemDto("Pagamento registrado com sucesso. Você possui um saldo de: R$" + saldo));
    }


    @Operation(summary = "Obtém todas as contas.", description = "Caso o JSON de paginação venha vazio o valor default é 20 por página.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(type = "array", implementation = ContaDto.class))})
    })
    @GetMapping
    public ResponseEntity<Page<ContaDto>> listarContas(Pageable pageable) {
        Page<Conta> contas = contaService.findAll(pageable);
        Page<ContaDto> dtoPage = contas.map(ContaDto::new);
        return ResponseEntity.ok(dtoPage);
    }


    @Operation(summary = "Obtém uma conta através do ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = ContaDto.class))}),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<ContaDto> buscarContaPorId(@PathVariable Long id) {
        Conta conta = contaService.findById(id);
        ContaDto dto = new ContaDto(conta);
        return ResponseEntity.ok(dto);
    }


    @Operation(summary = "Busca o total já pago em contas.",
            description = "Será feito uma busca de todas as contas pagas e feito uma soma do valor total já pago.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = String.class))})
    })
    @GetMapping("/usuario/{usuarioId}/total-pago")
    public ResponseEntity<MensagemDto> getTotalPago(@RequestParam String dataInicio, @RequestParam String dataFim, @PathVariable Long usuarioId) {
        LocalDate start = LocalDate.parse(dataInicio);
        LocalDate end = LocalDate.parse(dataFim);
        Usuario usuario = usuarioService.findById(usuarioId);
        BigDecimal totalPago = contaService.getTotalPago(start, end, usuario);

        return ResponseEntity.ok(new MensagemDto("O usuário " + usuario.getNome() + " pagou um total em contas de: R$" + totalPago));
    }

    @Operation(summary = "Busca todas as contas pendentes.",
            description = "Será feito uma busca de todas as contas pendentes.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(type = "array", implementation = ContaDto.class))})
    })
    @GetMapping("/pendentes")
    public ResponseEntity<List<ContaDto>> getTodasContasPendentes(@RequestParam String dataInicio, @RequestParam String dataFim) {
        LocalDate start = LocalDate.parse(dataInicio);
        LocalDate end = LocalDate.parse(dataFim);
        List<ContaDto> contas = contaService.getTodasContasPendentes(start, end);
        return ResponseEntity.ok(contas);
    }

    @Operation(summary = "Busca todas as contas pendentes de um usuário.",
            description = "Será feito uma busca de todas as contas pendentes de um usuário.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(type = "array", implementation = ContaDto.class))})
    })
    @GetMapping("/usuario/{usuarioId}/pendentes")
    public ResponseEntity<List<ContaDto>> getContasPendentesUsuario(@RequestParam String dataInicio, @RequestParam String dataFim, @PathVariable Long usuarioId) {
        LocalDate start = LocalDate.parse(dataInicio);
        LocalDate end = LocalDate.parse(dataFim);
        List<ContaDto> contas = contaService.getContasPendentesUsuario(start, end, usuarioId);
        return ResponseEntity.ok(contas);
    }

    @Operation(summary = "Deleta uma conta a partir do ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", content = @Content),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada",
                    content = @Content)})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConta(@PathVariable Long id) {
        contaService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Busca contas por ID do usuário", description = "Retorna todas as contas associadas a um usuário específico.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contas encontradas"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<ContaDto>> getContasByUsuarioId(@PathVariable Long usuarioId) {
        List<ContaDto> contas = contaService.findByUsuarioId(usuarioId);
        return ResponseEntity.ok(contas);
    }

    @Operation(summary = "Carrega arquivo CSV para criar contas", description = "Upload de um arquivo CSV para criar contas com status 'PENDENTE'")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Arquivo processado com sucesso e contas criadas"),
            @ApiResponse(responseCode = "400", description = "Erro ao processar o arquivo")
    })
    @RequestMapping(
            path = "/upload-csv",
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<String> uploadCsvFile(@Parameter(name = "file", required = true) @RequestPart("file") MultipartFile file) {
        try {
            csvContaService.importCsvFile(file);
            return ResponseEntity.ok("Arquivo processado com sucesso e contas criadas.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao processar o arquivo " + file.getOriginalFilename() + " - Erro: " + e.getMessage());
        }
    }

    @Operation(summary = "Obtém a lista de contas a pagar com filtro de data de vencimento e nome",
            description = "Retorna a lista de contas a pagar filtrada por data de vencimento e nome.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(type = "array", implementation = ContaDto.class))})
    })
    @GetMapping("/contas-filtro")
    public ResponseEntity<Page<ContaDto>> listarContasFiltradas(
            @RequestParam(required = false) LocalDate dataVencimento,
            @RequestParam(required = false) String nome,
            Pageable pageable) {

        Page<Conta> contas = contaService.findByFilters(dataVencimento, nome, pageable);
        Page<ContaDto> dtoPage = contas != null ? contas.map(ContaDto::new) : Page.empty(pageable);
        return ResponseEntity.ok(dtoPage);
    }


}