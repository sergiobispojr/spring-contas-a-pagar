package com.sergiobispo.pagamentos.application.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sergiobispo.pagamentos.application.dto.ContaDto;
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
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContaService {
    @Autowired
    private ContaRepository contaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional
    public Conta save(Conta conta) {


        conta.setDataPagamento(null);
        conta.setSituacao(Situacao.PENDENTE);

        Usuario usuario = usuarioRepository.findById(conta.getUsuario().getId())
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

        conta.setUsuario(usuario);

        return contaRepository.save(conta);
    }

    @Transactional
    public Conta update(Long id, Conta conta) {

        Conta contaAntiga = contaRepository.findById(id).orElseThrow(() -> new NotFoundException("Conta não encontrada"));
        Usuario usuario = usuarioRepository.findById(conta.getUsuario().getId())
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));


        if (conta.getSituacao() == null) {
            conta.setSituacao(contaAntiga.getSituacao());
        }

        conta.setId(id);
        conta.setUsuario(usuario);

        return contaRepository.save(conta);
    }

    @Transactional
    public BigDecimal pagarConta(Long id, Long usuarioId) {
        Conta conta = contaRepository.findById(id).orElseThrow(() -> new NotFoundException("Conta não encontrada"));
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

        if (conta.getUsuario() != usuario) {
            throw new UsuarioSemPermissaoException(usuario.getNome() + " não tem permissão para fazer o pagamento dessa conta.");
        }

        if (conta.getValor().compareTo(usuario.getSaldo()) > 0) {
            throw new SaldoInsuficienteException("O usuário não possui saldo suficiente.");
        }

        if (conta.getSituacao() == Situacao.PAGO) {
            throw new ContaJaPagaException("Essa conta já foi paga.");
        }

        usuario.setSaldo(usuario.getSaldo().subtract(conta.getValor()));

        LocalDate hoje = LocalDate.now();

        conta.setDataPagamento(hoje);
        conta.setSituacao(Situacao.PAGO);

        contaRepository.save(conta);
        usuarioRepository.save(usuario);

        return usuario.getSaldo();
    }

    public Page<Conta> findAll(Pageable pageable) {
        return contaRepository.findAll(pageable);
    }

    public Conta findById(Long id) {
        return contaRepository.findById(id).orElseThrow(() -> new NotFoundException("Conta não encontrada"));
    }

    public BigDecimal getTotalPago(LocalDate dataInicio, LocalDate dataFim, Usuario usuario) {
        List<Conta> contas = contaRepository.findAll();
        return contas.stream()
                .filter(conta -> conta.getUsuario() == usuario)
                .filter(conta -> conta.getDataPagamento() != null)
                .filter(conta -> conta.getSituacao() == Situacao.PAGO)
                .filter(conta -> !conta.getDataPagamento().isBefore(dataInicio) && !conta.getDataPagamento().isAfter(dataFim))
                .map(Conta::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<ContaDto> getTodasContasPendentes(LocalDate dataInicio, LocalDate dataFim) {
        List<Conta> contas = contaRepository.findAll();
        return contas.stream()
                .filter(conta -> conta.getDataPagamento() == null)
                .filter(conta -> conta.getSituacao() == Situacao.PENDENTE)
                .filter(conta -> !conta.getDataVencimento().isBefore(dataInicio) && !conta.getDataVencimento().isAfter(dataFim))
                .map(ContaDto::new)
                .collect(Collectors.toList());
    }

    public List<ContaDto> getContasPendentesUsuario(LocalDate dataInicio, LocalDate dataFim, Long usuarioId) {

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

        List<Conta> contas = contaRepository.findAll();
        return contas.stream()
                .filter(conta -> conta.getUsuario() == usuario)
                .filter(conta -> conta.getDataPagamento() == null)
                .filter(conta -> conta.getSituacao() == Situacao.PENDENTE)
                .filter(conta -> !conta.getDataVencimento().isBefore(dataInicio) && !conta.getDataVencimento().isAfter(dataFim))
                .map(ContaDto::new)
                .collect(Collectors.toList());
    }


    @Transactional
    public void delete(Long id) {
        if (!contaRepository.existsById(id)) {
            throw new NotFoundException("Conta não encontrada com ID: " + id);
        }
        contaRepository.deleteById(id);
    }

    public List<ContaDto> findByUsuarioId(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

        List<Conta> contas = contaRepository.findByUsuario(usuario);
        return contas.stream().map(ContaDto::new).collect(Collectors.toList());
    }

    public Page<Conta> findByFilters(LocalDate data, String nome, Pageable pageable) {
        return contaRepository.findByFilters(data, nome, pageable);
    }
}
