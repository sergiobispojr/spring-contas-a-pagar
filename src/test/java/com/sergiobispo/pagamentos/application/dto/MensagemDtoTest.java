package com.sergiobispo.pagamentos.application.dto;

import org.junit.jupiter.api.Test;

import com.sergiobispo.pagamentos.application.dto.MensagemDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class MensagemDtoTest {

    @Test
    void testNoArgsConstructor() {
        MensagemDto mensagemDto = new MensagemDto();
        assertNull(mensagemDto.getMensagem());
    }

    @Test
    void testAllArgsConstructor() {
        String mensagem = "Teste de mensagem";
        MensagemDto mensagemDto = new MensagemDto(mensagem);
        assertEquals(mensagem, mensagemDto.getMensagem());
    }

    @Test
    void testGettersAndSetters() {
        String mensagem = "Teste de mensagem";
        MensagemDto mensagemDto = new MensagemDto();
        mensagemDto.setMensagem(mensagem);
        assertEquals(mensagem, mensagemDto.getMensagem());
    }

    @Test
    void testEqualsAndHashCode() {
        String mensagem = "Teste de mensagem";
        MensagemDto mensagemDto1 = new MensagemDto(mensagem);
        MensagemDto mensagemDto2 = new MensagemDto(mensagem);
        assertEquals(mensagemDto1, mensagemDto2);
        assertEquals(mensagemDto1.hashCode(), mensagemDto2.hashCode());
    }

    @Test
    void testToString() {
        String mensagem = "Teste de mensagem";
        MensagemDto mensagemDto = new MensagemDto(mensagem);
        String expectedToString = "MensagemDto(mensagem=Teste de mensagem)";
        assertEquals(expectedToString, mensagemDto.toString());
    }

}