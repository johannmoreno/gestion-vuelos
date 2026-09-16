package com.desarrolloweb.vuelos.service;

public interface EmailService {

    void enviarEnlaceRecuperacion(String destinatario, String nombreUsuario, String enlace);
}