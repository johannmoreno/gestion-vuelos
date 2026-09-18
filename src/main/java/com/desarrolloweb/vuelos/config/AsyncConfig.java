package com.desarrolloweb.vuelos.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Habilita la ejecucion de metodos @Async, usada para enviar el correo
 * de recuperacion de clave sin bloquear la respuesta HTTP al usuario.
 */
@Configuration
@EnableAsync
public class AsyncConfig {
}