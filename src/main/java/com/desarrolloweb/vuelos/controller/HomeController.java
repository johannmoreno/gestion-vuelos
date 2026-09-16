package com.desarrolloweb.vuelos.controller;

import com.desarrolloweb.vuelos.service.UsuarioService;
import com.desarrolloweb.vuelos.service.VueloService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

/**
 * Tablero de inicio: resumen de la operacion para el usuario autenticado.
 */
@Controller
public class HomeController {

    private final VueloService vueloService;
    private final UsuarioService usuarioService;

    public HomeController(VueloService vueloService, UsuarioService usuarioService) {
        this.vueloService = vueloService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/")
    public String inicio(Model model, Principal principal) {
        model.addAttribute("usuarioActual", principal != null ? principal.getName() : "invitado");
        model.addAttribute("totalVuelos", vueloService.contarVuelos());
        model.addAttribute("totalUsuarios", usuarioService.listar().size());
        model.addAttribute("totalVendido", vueloService.totalVendidoSinCancelados());
        model.addAttribute("conteoPorEstado", vueloService.contarPorEstado());
        model.addAttribute("proximos", vueloService.listar().stream().limit(5).toList());
        return "index";
    }
}