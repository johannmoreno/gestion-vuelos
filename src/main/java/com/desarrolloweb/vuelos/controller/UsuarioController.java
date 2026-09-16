package com.desarrolloweb.vuelos.controller;

import com.desarrolloweb.vuelos.entity.Rol;
import com.desarrolloweb.vuelos.entity.Usuario;
import com.desarrolloweb.vuelos.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

/**
 * Controller MVC de la entidad comun Usuario. Solo accesible con rol ADMIN
 * (restriccion configurada en SecurityConfig).
 */
@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @ModelAttribute("roles")
    public Rol[] roles() {
        return Rol.values();
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.listar());
        return "usuarios/lista";
    }

    @GetMapping("/nuevo")
    public String formularioNuevo(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("modoEdicion", false);
        return "usuarios/formulario";
    }

    @GetMapping("/{id}/editar")
    public String formularioEditar(@PathVariable Long id, Model model, RedirectAttributes flash) {
        return usuarioService.buscarPorId(id)
                .map(usuario -> {
                    usuario.setClave("");
                    model.addAttribute("usuario", usuario);
                    model.addAttribute("modoEdicion", true);
                    return "usuarios/formulario";
                })
                .orElseGet(() -> {
                    flash.addFlashAttribute("mensajeError", "El usuario solicitado no existe.");
                    return "redirect:/usuarios";
                });
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("usuario") Usuario usuario,
                          BindingResult resultado,
                          @RequestParam(required = false) String claveNueva,
                          Model model,
                          RedirectAttributes flash) {

        boolean modoEdicion = usuario.getId() != null;

        if (!modoEdicion && (claveNueva == null || claveNueva.length() < 8)) {
            resultado.addError(new FieldError("usuario", "clave",
                    "La clave es obligatoria y debe tener al menos 8 caracteres"));
        }
        if (modoEdicion && claveNueva != null && !claveNueva.isBlank() && claveNueva.length() < 8) {
            resultado.addError(new FieldError("usuario", "clave",
                    "La clave nueva debe tener al menos 8 caracteres"));
        }
        if (!usuarioService.nombreDisponible(usuario.getNombre(), usuario.getId())) {
            resultado.addError(new FieldError("usuario", "nombre", "Ese nombre de usuario ya esta registrado"));
        }
        if (!usuarioService.emailDisponible(usuario.getEmail(), usuario.getId())) {
            resultado.addError(new FieldError("usuario", "email", "Ese correo ya esta registrado"));
        }

        boolean soloErroresDeClave = resultado.getFieldErrorCount() == resultado.getFieldErrorCount("clave")
                && resultado.getGlobalErrorCount() == 0;

        if (resultado.hasErrors() && !(modoEdicion && soloErroresDeClave && (claveNueva == null || claveNueva.isBlank()))) {
            model.addAttribute("modoEdicion", modoEdicion);
            return "usuarios/formulario";
        }

        usuarioService.guardar(usuario, claveNueva);
        flash.addFlashAttribute("mensajeOk", modoEdicion ? "Usuario actualizado." : "Usuario creado.");
        return "redirect:/usuarios";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, Principal principal, RedirectAttributes flash) {
        usuarioService.buscarPorId(id).ifPresentOrElse(usuario -> {
            if (principal != null && usuario.getNombre().equals(principal.getName())) {
                flash.addFlashAttribute("mensajeError", "No puedes eliminar el usuario con el que iniciaste sesion.");
            } else {
                usuarioService.eliminar(id);
                flash.addFlashAttribute("mensajeOk", "Usuario eliminado.");
            }
        }, () -> flash.addFlashAttribute("mensajeError", "El usuario solicitado no existe."));
        return "redirect:/usuarios";
    }

    /**
     * REPORTES PARAMETRIZADOS DE USUARIO.
     * Reporte 1: por rol y estado (activo/inactivo).
     * Reporte 2: busqueda por texto en nombre o correo.
     */
    @GetMapping("/reportes")
    public String reportes(@RequestParam(required = false) Rol rol,
                           @RequestParam(required = false) Boolean activo,
                           @RequestParam(required = false) String texto,
                           @RequestParam(required = false) String reporte,
                           Model model) {

        model.addAttribute("reporte", reporte);
        if ("rol".equals(reporte)) {
            model.addAttribute("resultados", usuarioService.reportePorRolYEstado(rol, activo));
        } else if ("texto".equals(reporte)) {
            model.addAttribute("resultados", usuarioService.reportePorNombreOCorreo(texto));
        }
        model.addAttribute("rolSeleccionado", rol);
        model.addAttribute("activo", activo);
        model.addAttribute("texto", texto);
        model.addAttribute("conteoPorRol", usuarioService.contarPorRol());
        return "usuarios/reportes";
    }
}