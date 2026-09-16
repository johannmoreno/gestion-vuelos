package com.desarrolloweb.vuelos.service;

import com.desarrolloweb.vuelos.entity.EstadoVuelo;
import com.desarrolloweb.vuelos.entity.Vuelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Capa Service: contrato de la logica de negocio de Vuelo (ejercicio 18).
 */
public interface VueloService {

    List<Vuelo> listar();

    Optional<Vuelo> buscarPorId(Long id);

    Vuelo guardar(Vuelo vuelo);

    void eliminar(Long id);

    List<Vuelo> reportePorAerolineaYFechas(String aerolinea, LocalDateTime desde, LocalDateTime hasta);

    List<Vuelo> reportePorEstadoYValor(EstadoVuelo estado, BigDecimal valorMinimo, BigDecimal valorMaximo);

    List<Vuelo> reportePorRuta(String origen, String destino);

    BigDecimal sumarValores(List<Vuelo> vuelos);

    BigDecimal totalVendidoSinCancelados();

    List<Object[]> contarPorEstado();

    List<String> listarAerolineas();

    long contarVuelos();
}