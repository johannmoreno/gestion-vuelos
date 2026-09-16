package com.desarrolloweb.vuelos.service;

import com.desarrolloweb.vuelos.entity.TokenRecuperacion;

import java.util.Optional;

/**
 * Capa Service: flujo de recuperacion de clave por correo electronico.
 */
public interface RecuperacionClaveService {

    /** Genera el token, lo persiste y envia el correo. Devuelve el enlace generado. */
    Optional<String> solicitarRecuperacion(String email, String urlBase);

    Optional<TokenRecuperacion> validarToken(String token);

    boolean restablecerClave(String token, String nuevaClave);
}