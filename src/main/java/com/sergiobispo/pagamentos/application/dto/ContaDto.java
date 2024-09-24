package com.sergiobispo.pagamentos.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sergiobispo.pagamentos.domain.entities.Conta;
import com.sergiobispo.pagamentos.domain.entities.Usuario;
import com.sergiobispo.pagamentos.domain.enums.Situacao;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContaDto {
    private Long id;
    @NotBlank(message = "Nome não pode ser null")
    private String nome;
    @NotNull(message = "Valor não pode ser null")
    private BigDecimal valor;
    @NotBlank(message = "Descrição não pode ser null")
    private String descricao;
    private String observacao;
    private LocalDate dataPagamento;
    @NotNull(message = "Data de vencimento não pode ser null")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataVencimento;
    private Situacao situacao;
    @NotNull
    private Long usuarioId;

    public ContaDto(Conta conta) {
        this.id = conta.getId();
        this.nome = conta.getNome();
        this.valor = conta.getValor();
        this.descricao = conta.getDescricao();
        this.observacao = conta.getObservacao();
        this.dataPagamento = conta.getDataPagamento();
        this.dataVencimento = conta.getDataVencimento();
        this.situacao = conta.getSituacao();
        this.usuarioId = conta.getUsuario().getId();
    }

    public Conta toConta() {
        Conta conta = new Conta();
        conta.setId(this.id);
        conta.setNome(this.nome);
        conta.setValor(this.valor);
        conta.setDescricao(this.descricao);
        conta.setDataPagamento(this.dataPagamento);
        conta.setDataVencimento(this.dataVencimento);
        conta.setObservacao(this.observacao);
        conta.setSituacao(this.situacao);

        Usuario usuario = new Usuario();
        usuario.setId(this.usuarioId);

        conta.setUsuario(usuario);

        return conta;
    }
}