package com.desarrolloweb.vuelos.service.impl;

import com.desarrolloweb.vuelos.entity.EstadoVuelo;
import com.desarrolloweb.vuelos.entity.Vuelo;
import com.desarrolloweb.vuelos.repository.VueloRepository;
import com.desarrolloweb.vuelos.service.VueloService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implementacion de la logica de negocio de Vuelo.
 */
@Service
@Transactional
public class VueloServiceImpl implements VueloService {

    private final VueloRepository vueloRepository;

    public VueloServiceImpl(VueloRepository vueloRepository) {
        this.vueloRepository = vueloRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vuelo> listar() {
        return vueloRepository.findAllByOrderByFechaSalidaDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Vuelo> buscarPorId(Long id) {
        return vueloRepository.findById(id);
    }

    /**
     * Regla de negocio: la llegada no puede ser anterior o igual a la salida,
     * y la compra no puede ser posterior a la fecha de salida.
     */
    @Override
    public Vuelo guardar(Vuelo vuelo) {
        if (vuelo.getFechaSalida() != null && vuelo.getFechaLlegada() != null
                && !vuelo.getFechaLlegada().isAfter(vuelo.getFechaSalida())) {
            throw new IllegalArgumentException("La fecha de llegada debe ser posterior a la fecha de salida");
        }
        if (vuelo.getFechaCompra() != null && vuelo.getFechaSalida() != null
                && vuelo.getFechaCompra().isAfter(vuelo.getFechaSalida().toLocalDate())) {
            throw new IllegalArgumentException("La fecha de compra no puede ser posterior a la fecha de salida");
        }
        if (vuelo.getNumero() != null) {
            vuelo.setNumero(vuelo.getNumero().trim().toUpperCase());
        }
        if (vuelo.getPuesto() != null) {
            vuelo.setPuesto(vuelo.getPuesto().trim().toUpperCase());
        }
        return vueloRepository.save(vuelo);
    }

    @Override
    public void eliminar(Long id) {
        vueloRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vuelo> reportePorAerolineaYFechas(String aerolinea, LocalDateTime desde, LocalDateTime hasta) {
        String filtro = StringUtils.hasText(aerolinea) ? aerolinea.trim() : null;
        return vueloRepository.reportePorAerolineaYFechas(filtro, desde, hasta);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vuelo> reportePorEstadoYValor(EstadoVuelo estado, BigDecimal valorMinimo, BigDecimal valorMaximo) {
        return vueloRepository.reportePorEstadoYValor(estado, valorMinimo, valorMaximo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vuelo> reportePorRuta(String origen, String destino) {
        return vueloRepository.reportePorRuta(
                StringUtils.hasText(origen) ? origen.trim() : null,
                StringUtils.hasText(destino) ? destino.trim() : null);
    }

    @Override
    public BigDecimal sumarValores(List<Vuelo> vuelos) {
        return vuelos.stream()
                .map(Vuelo::getValor)
                .filter(v -> v != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal totalVendidoSinCancelados() {
        BigDecimal total = vueloRepository.totalVendidoSinCancelados();
        return total == null ? BigDecimal.ZERO : total;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> contarPorEstado() {
        return vueloRepository.contarPorEstado();
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> listarAerolineas() {
        return vueloRepository.listarAerolineas();
    }

    @Override
    @Transactional(readOnly = true)
    public long contarVuelos() {
        return vueloRepository.count();
    }
}