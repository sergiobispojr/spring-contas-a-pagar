package com.sergiobispo.pagamentos.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import com.sergiobispo.pagamentos.application.dto.LoginRequestDto;
import com.sergiobispo.pagamentos.application.dto.LoginResponseDto;
import com.sergiobispo.pagamentos.application.dto.UsuarioDto;
import com.sergiobispo.pagamentos.application.service.UsuarioService;
import com.sergiobispo.pagamentos.domain.entities.Usuario;
import com.sergiobispo.pagamentos.exception.CredenciaisInvalidasException;
import com.sergiobispo.pagamentos.exception.NullParameterException;
import com.sergiobispo.pagamentos.infrastructure.helpers.JwtUtil;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "Usuários")
@Slf4j
public class UsuarioController {


    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;


    @Operation(summary = "Realiza o login do usuário", description = "Autentica o usuário com email e senha, retornando um token JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            final String jwt = jwtUtil.generateToken(loginRequest.getEmail());
            return ResponseEntity.ok(new LoginResponseDto(jwt));
        } catch (AuthenticationException e) {
            throw new CredenciaisInvalidasException("Credenciais inválidas");
        }
    }

    @Operation(summary = "Cria um novo usuário",
            description = "Faz a criação de um novo usuário.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = UsuarioDto.class))})
    })

    @PostMapping
    public ResponseEntity<UsuarioDto> criarUsuario(@Valid @RequestBody UsuarioDto usuarioDto) {

        if (usuarioDto == null) {
            throw new NullParameterException("Usuário não pode ser null");
        }

        Usuario novoUsuario = usuarioService.save(usuarioDto.toUsuario());
        UsuarioDto dto = new UsuarioDto(novoUsuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }


    @Operation(summary = "Atualiza um usuário.",
            description = "A partir de um ID passado no path um usuário será atualizado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = UsuarioDto.class))}),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado.", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDto> atualizarUsuario(@PathVariable Long id, @RequestBody UsuarioDto usuarioDto) {
        Usuario usuarioAtualizado = usuarioService.update(id, usuarioDto.toUsuario());
        UsuarioDto dto = new UsuarioDto(usuarioAtualizado);
        return ResponseEntity.ok(dto);
    }


    @Operation(summary = "Obtém todos os usuários.", description = "Caso o JSON de paginação venha vazio o valor default é 20 por página.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(type = "array", implementation = UsuarioDto.class))})
    })
    @GetMapping
    public ResponseEntity<Page<UsuarioDto>> listarUsuarios(Pageable pageable) {
        Page<Usuario> usuario = usuarioService.findAll(pageable);
        Page<UsuarioDto> dtoPage = usuario.map(UsuarioDto::new);
        return ResponseEntity.ok(dtoPage);
    }


    @Operation(summary = "Obtém um usuário através do ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = UsuarioDto.class))}),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDto> buscarUsuarioPorId(@PathVariable Long id) {
        Usuario usuario = usuarioService.findById(id);
        UsuarioDto dto = new UsuarioDto(usuario);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Deleta um usuário a partir do ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", content = @Content),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrada",
                    content = @Content)})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsuario(@PathVariable Long id) {
        usuarioService.delete(id);
        return ResponseEntity.noContent().build();
    }


}
