package com.desarrolloweb.vuelos.controller;

import com.desarrolloweb.vuelos.service.RecuperacionClaveService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller de autenticacion: login, recuperacion y restablecimiento de clave.
 * Todos los metodos retornan el NOMBRE de una plantilla Thymeleaf.
 */
@Controller
public class AuthController {

    private final RecuperacionClaveService recuperacionClaveService;

    public AuthController(RecuperacionClaveService recuperacionClaveService) {
        this.recuperacionClaveService = recuperacionClaveService;
    }

    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error,
                        @RequestParam(required = false) String logout,
                        Model model) {
        if (error != null) {
            model.addAttribute("mensajeError", "Usuario o clave incorrectos.");
        }
        if (logout != null) {
            model.addAttribute("mensajeOk", "Cerraste la sesion.");
        }
        return "auth/login";
    }

    @GetMapping("/recuperar-clave")
    public String formularioRecuperacion() {
        return "auth/recuperar-clave";
    }

    @PostMapping("/recuperar-clave")
    public String solicitarRecuperacion(@RequestParam String email,
                                        HttpServletRequest request,
                                        RedirectAttributes flash) {
        recuperacionClaveService.solicitarRecuperacion(email, urlBase(request));
        // Mensaje neutro: no revela si el correo existe en la base de datos.
        flash.addFlashAttribute("mensajeOk",
                "Si el correo esta registrado, enviamos un enlace para crear una clave nueva. Revisa tu bandeja de entrada.");
        return "redirect:/login";
    }

    @GetMapping("/restablecer-clave")
    public String formularioRestablecer(@RequestParam String token, Model model) {
        if (recuperacionClaveService.validarToken(token).isEmpty()) {
            model.addAttribute("mensajeError", "El enlace vencio o ya fue usado. Solicita uno nuevo.");
            return "auth/recuperar-clave";
        }
        model.addAttribute("token", token);
        return "auth/restablecer-clave";
    }

    @PostMapping("/restablecer-clave")
    public String restablecer(@RequestParam String token,
                              @RequestParam String clave,
                              @RequestParam String confirmacion,
                              Model model,
                              RedirectAttributes flash) {
        if (!StringUtils.hasText(clave) || clave.length() < 8) {
            model.addAttribute("token", token);
            model.addAttribute("mensajeError", "La clave debe tener al menos 8 caracteres.");
            return "auth/restablecer-clave";
        }
        if (!clave.equals(confirmacion)) {
            model.addAttribute("token", token);
            model.addAttribute("mensajeError", "Las dos claves no coinciden.");
            return "auth/restablecer-clave";
        }
        if (!recuperacionClaveService.restablecerClave(token, clave)) {
            model.addAttribute("mensajeError", "El enlace vencio o ya fue usado. Solicita uno nuevo.");
            return "auth/recuperar-clave";
        }
        flash.addFlashAttribute("mensajeOk", "Clave actualizada. Inicia sesion con la nueva clave.");
        return "redirect:/login";
    }

    @GetMapping("/acceso-denegado")
    public String accesoDenegado() {
        return "error/acceso-denegado";
    }

    private String urlBase(HttpServletRequest request) {
        String esquema = request.getHeader("X-Forwarded-Proto");
        String host = request.getHeader("X-Forwarded-Host");
        if (StringUtils.hasText(esquema) && StringUtils.hasText(host)) {
            return esquema + "://" + host + request.getContextPath();
        }
        String url = request.getRequestURL().toString();
        return url.substring(0, url.length() - request.getRequestURI().length()) + request.getContextPath();
    }
}