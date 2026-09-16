package com.desarrolloweb.vuelos.service.impl;

import com.desarrolloweb.vuelos.entity.TokenRecuperacion;
import com.desarrolloweb.vuelos.entity.Usuario;
import com.desarrolloweb.vuelos.repository.TokenRecuperacionRepository;
import com.desarrolloweb.vuelos.repository.UsuarioRepository;
import com.desarrolloweb.vuelos.service.EmailService;
import com.desarrolloweb.vuelos.service.RecuperacionClaveService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class RecuperacionClaveServiceImpl implements RecuperacionClaveService {

    private static final int MINUTOS_VIGENCIA = 30;

    private final UsuarioRepository usuarioRepository;
    private final TokenRecuperacionRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public RecuperacionClaveServiceImpl(UsuarioRepository usuarioRepository,
                                        TokenRecuperacionRepository tokenRepository,
                                        EmailService emailService,
                                        PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<String> solicitarRecuperacion(String email, String urlBase) {
        Optional<Usuario> posible = usuarioRepository.findByEmail(email == null ? "" : email.trim());
        if (posible.isEmpty()) {
            return Optional.empty();
        }
        Usuario usuario = posible.get();
        tokenRepository.invalidarTokensDe(usuario);

        String token = UUID.randomUUID().toString();
        tokenRepository.save(new TokenRecuperacion(token, usuario,
                LocalDateTime.now().plusMinutes(MINUTOS_VIGENCIA)));

        String enlace = urlBase + "/restablecer-clave?token=" + token;
        emailService.enviarEnlaceRecuperacion(usuario.getEmail(), usuario.getNombre(), enlace);
        return Optional.of(enlace);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TokenRecuperacion> validarToken(String token) {
        return tokenRepository.findByToken(token).filter(TokenRecuperacion::estaVigente);
    }

    @Override
    public boolean restablecerClave(String token, String nuevaClave) {
        Optional<TokenRecuperacion> posible = tokenRepository.findByToken(token)
                .filter(TokenRecuperacion::estaVigente);
        if (posible.isEmpty()) {
            return false;
        }
        TokenRecuperacion registro = posible.get();
        Usuario usuario = registro.getUsuario();
        usuario.setClave(passwordEncoder.encode(nuevaClave));
        usuarioRepository.save(usuario);

        registro.setUsado(true);
        tokenRepository.save(registro);
        return true;
    }
}