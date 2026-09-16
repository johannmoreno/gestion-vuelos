package com.desarrolloweb.vuelos.controller;

import com.desarrolloweb.vuelos.entity.EstadoVuelo;
import com.desarrolloweb.vuelos.entity.Vuelo;
import com.desarrolloweb.vuelos.service.VueloService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller MVC de la entidad del ejercicio 18 (Vuelo).
 * Recibe las solicitudes HTTP, delega en el Service, carga el Model y
 * retorna el nombre de la plantilla Thymeleaf que debe renderizarse.
 */
@Controller
@RequestMapping("/vuelos")
public class VueloController {

    private final VueloService vueloService;

    public VueloController(VueloService vueloService) {
        this.vueloService = vueloService;
    }

    @ModelAttribute("estados")
    public EstadoVuelo[] estados() {
        return EstadoVuelo.values();
    }

    /** LISTAR */
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("vuelos", vueloService.listar());
        return "vuelos/lista";
    }

    /** DETALLE */
    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model, RedirectAttributes flash) {
        return vueloService.buscarPorId(id)
                .map(vuelo -> {
                    model.addAttribute("vuelo", vuelo);
                    return "vuelos/detalle";
                })
                .orElseGet(() -> {
                    flash.addFlashAttribute("mensajeError", "El vuelo solicitado no existe.");
                    return "redirect:/vuelos";
                });
    }

    /** CREAR - formulario */
    @GetMapping("/nuevo")
    public String formularioNuevo(Model model) {
        model.addAttribute("vuelo", new Vuelo());
        model.addAttribute("modoEdicion", false);
        return "vuelos/formulario";
    }

    /** ACTUALIZAR - formulario */
    @GetMapping("/{id}/editar")
    public String formularioEditar(@PathVariable Long id, Model model, RedirectAttributes flash) {
        return vueloService.buscarPorId(id)
                .map(vuelo -> {
                    model.addAttribute("vuelo", vuelo);
                    model.addAttribute("modoEdicion", true);
                    return "vuelos/formulario";
                })
                .orElseGet(() -> {
                    flash.addFlashAttribute("mensajeError", "El vuelo solicitado no existe.");
                    return "redirect:/vuelos";
                });
    }

    /** CREAR y ACTUALIZAR - guardado (binding del formulario HTML al objeto Java) */
    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("vuelo") Vuelo vuelo,
                          BindingResult resultado,
                          Model model,
                          RedirectAttributes flash) {
        if (resultado.hasErrors()) {
            model.addAttribute("modoEdicion", vuelo.getId() != null);
            return "vuelos/formulario";
        }
        try {
            boolean esNuevo = vuelo.getId() == null;
            vueloService.guardar(vuelo);
            flash.addFlashAttribute("mensajeOk",
                    esNuevo ? "Vuelo registrado." : "Vuelo actualizado.");
        } catch (IllegalArgumentException ex) {
            model.addAttribute("modoEdicion", vuelo.getId() != null);
            model.addAttribute("mensajeError", ex.getMessage());
            return "vuelos/formulario";
        }
        return "redirect:/vuelos";
    }

    /** ELIMINAR */
    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        vueloService.eliminar(id);
        flash.addFlashAttribute("mensajeOk", "Vuelo eliminado.");
        return "redirect:/vuelos";
    }

    /**
     * REPORTES PARAMETRIZADOS DE VUELO.
     * Reporte 1: aerolinea + rango de fechas de salida.
     * Reporte 2: estado + rango de valor.
     * Reporte 3: ruta (aeropuerto de salida y de llegada).
     */
    @GetMapping("/reportes")
    public String reportes(@RequestParam(required = false) String aerolinea,
                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta,
                           @RequestParam(required = false) EstadoVuelo estado,
                           @RequestParam(required = false) BigDecimal valorMinimo,
                           @RequestParam(required = false) BigDecimal valorMaximo,
                           @RequestParam(required = false) String origen,
                           @RequestParam(required = false) String destino,
                           @RequestParam(required = false) String reporte,
                           Model model) {

        model.addAttribute("aerolineasDisponibles", vueloService.listarAerolineas());
        model.addAttribute("reporte", reporte);

        if ("aerolinea".equals(reporte)) {
            List<Vuelo> resultados = vueloService.reportePorAerolineaYFechas(aerolinea, desde, hasta);
            model.addAttribute("resultados", resultados);
            model.addAttribute("total", vueloService.sumarValores(resultados));
        } else if ("estado".equals(reporte)) {
            List<Vuelo> resultados = vueloService.reportePorEstadoYValor(estado, valorMinimo, valorMaximo);
            model.addAttribute("resultados", resultados);
            model.addAttribute("total", vueloService.sumarValores(resultados));
        } else if ("ruta".equals(reporte)) {
            List<Vuelo> resultados = vueloService.reportePorRuta(origen, destino);
            model.addAttribute("resultados", resultados);
            model.addAttribute("total", vueloService.sumarValores(resultados));
        }

        model.addAttribute("aerolinea", aerolinea);
        model.addAttribute("desde", desde);
        model.addAttribute("hasta", hasta);
        model.addAttribute("estadoSeleccionado", estado);
        model.addAttribute("valorMinimo", valorMinimo);
        model.addAttribute("valorMaximo", valorMaximo);
        model.addAttribute("origen", origen);
        model.addAttribute("destino", destino);
        return "vuelos/reportes";
    }
}