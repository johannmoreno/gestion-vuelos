package com.desarrolloweb.vuelos.service.impl;

import com.desarrolloweb.vuelos.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Envio de correo mediante JavaMailSender (SMTP).
 * Si el envio esta deshabilitado (app.mail.habilitado=false), el enlace
 * se escribe en el log para no bloquear las pruebas locales.
 */
@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final JavaMailSender mailSender;

    @Value("${app.mail.remitente:no-reply@gestionvuelos.com}")
    private String remitente;

    @Value("${app.mail.habilitado:true}")
    private boolean habilitado;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void enviarEnlaceRecuperacion(String destinatario, String nombreUsuario, String enlace) {
        String cuerpo = """
                Hola %s,

                Recibimos una solicitud para restablecer la clave de tu cuenta en Gestion de Vuelos.
                Abre el siguiente enlace para crear una clave nueva. El enlace vence en 30 minutos
                y solo puede usarse una vez.

                %s

                Si no solicitaste el cambio, ignora este mensaje: tu clave actual sigue funcionando.
                """.formatted(nombreUsuario, enlace);

        if (!habilitado) {
            log.warn("Envio de correo deshabilitado. Enlace de recuperacion para {}: {}", destinatario, enlace);
            return;
        }

        try {
            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setFrom(remitente);
            mensaje.setTo(destinatario);
            mensaje.setSubject("Restablece tu clave - Gestion de Vuelos");
            mensaje.setText(cuerpo);
            mailSender.send(mensaje);
            log.info("Correo de recuperacion enviado a {}", destinatario);
        } catch (Exception ex) {
            log.error("No fue posible enviar el correo a {}. Enlace generado: {}", destinatario, enlace, ex);
        }
    }
}