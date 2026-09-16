package com.desarrolloweb.vuelos.repository;

import com.desarrolloweb.vuelos.entity.EstadoVuelo;
import com.desarrolloweb.vuelos.entity.Vuelo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Capa Repository: acceso a datos de Vuelo y consultas parametrizadas (reportes).
 */
public interface VueloRepository extends JpaRepository<Vuelo, Long> {

    List<Vuelo> findAllByOrderByFechaSalidaDesc();

    boolean existsByNumeroAndPuestoAndFechaSalida(String numero, String puesto, LocalDateTime fechaSalida);

    /**
     * REPORTE 1 DE VUELO (parametrizado): vuelos de una aerolinea dentro de un
     * rango de fechas de salida. Todos los parametros son opcionales.
     */
    @Query("""
            SELECT v FROM Vuelo v
            WHERE (:aerolinea IS NULL OR LOWER(v.aerolinea) LIKE LOWER(CONCAT('%', :aerolinea, '%')))
              AND (:desde IS NULL OR v.fechaSalida >= :desde)
              AND (:hasta IS NULL OR v.fechaSalida <= :hasta)
            ORDER BY v.fechaSalida
            """)
    List<Vuelo> reportePorAerolineaYFechas(@Param("aerolinea") String aerolinea,
                                           @Param("desde") LocalDateTime desde,
                                           @Param("hasta") LocalDateTime hasta);

    /**
     * REPORTE 2 DE VUELO (parametrizado): vuelos por estado y rango de valor del tiquete.
     */
    @Query("""
            SELECT v FROM Vuelo v
            WHERE (:estado IS NULL OR v.estado = :estado)
              AND (:valorMinimo IS NULL OR v.valor >= :valorMinimo)
              AND (:valorMaximo IS NULL OR v.valor <= :valorMaximo)
            ORDER BY v.valor DESC
            """)
    List<Vuelo> reportePorEstadoYValor(@Param("estado") EstadoVuelo estado,
                                       @Param("valorMinimo") BigDecimal valorMinimo,
                                       @Param("valorMaximo") BigDecimal valorMaximo);

    /**
     * REPORTE 3 DE VUELO (parametrizado): ocupacion de una ruta entre dos aeropuertos.
     */
    @Query("""
            SELECT v FROM Vuelo v
            WHERE (:origen IS NULL OR LOWER(v.aeropuertoSalida) LIKE LOWER(CONCAT('%', :origen, '%')))
              AND (:destino IS NULL OR LOWER(v.aeropuertoLlegada) LIKE LOWER(CONCAT('%', :destino, '%')))
            ORDER BY v.fechaSalida DESC
            """)
    List<Vuelo> reportePorRuta(@Param("origen") String origen,
                               @Param("destino") String destino);

    /** Suma de ventas de la busqueda por aerolinea, para el pie del reporte. */
    @Query("SELECT COALESCE(SUM(v.valor), 0) FROM Vuelo v WHERE v.estado <> com.desarrolloweb.vuelos.entity.EstadoVuelo.CANCELADO")
    BigDecimal totalVendidoSinCancelados();

    @Query("SELECT v.estado, COUNT(v) FROM Vuelo v GROUP BY v.estado ORDER BY v.estado")
    List<Object[]> contarPorEstado();

    @Query("SELECT DISTINCT v.aerolinea FROM Vuelo v ORDER BY v.aerolinea")
    List<String> listarAerolineas();
}