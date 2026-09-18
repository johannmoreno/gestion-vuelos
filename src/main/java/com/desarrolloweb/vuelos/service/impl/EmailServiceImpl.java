package com.desarrolloweb.vuelos.service.impl;

import com.desarrolloweb.vuelos.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

/**
 * Envio de correo mediante la API HTTP de Brevo (antes Sendinblue).
 * Se usa HTTP en vez de SMTP porque algunas plataformas de despliegue
 * (como Railway) bloquean las conexiones salientes por el puerto SMTP,
 * mientras que HTTPS (puerto 443) si esta disponible.
 * Se ejecuta en un hilo aparte (@Async) para no bloquear la respuesta HTTP.
 */
@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);
    private static final String BREVO_URL = "https://api.brevo.com/v3/smtp/email";

    private final RestClient restClient;

    @Value("${app.mail.remitente:no-reply@gestionvuelos.com}")
    private String remitente;

    @Value("${app.mail.remitente-nombre:Gestion de Vuelos}")
    private String remitenteNombre;

    @Value("${app.mail.habilitado:true}")
    private boolean habilitado;

    @Value("${app.mail.brevo.api-key:}")
    private String brevoApiKey;

    public EmailServiceImpl() {
        this.restClient = RestClient.create();
    }

    @Override
    @Async
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

        if (brevoApiKey == null || brevoApiKey.isBlank()) {
            log.error("BREVO_API_KEY no configurada. Enlace de recuperacion para {}: {}", destinatario, enlace);
            return;
        }

        Map<String, Object> body = Map.of(
                "sender", Map.of("name", remitenteNombre, "email", remitente),
                "to", List.of(Map.of("email", destinatario, "name", nombreUsuario)),
                "subject", "Restablece tu clave - Gestion de Vuelos",
                "textContent", cuerpo
        );

        try {
            restClient.post()
                    .uri(BREVO_URL)
                    .header("api-key", brevoApiKey)
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();
            log.info("Correo de recuperacion enviado a {} via Brevo", destinatario);
        } catch (Exception ex) {
            log.error("No fue posible enviar el correo a {} via Brevo. Enlace generado: {}",
                    destinatario, enlace, ex);
        }
    }
}