package com.desarrolloweb.vuelos.config;

import com.desarrolloweb.vuelos.entity.*;
import com.desarrolloweb.vuelos.repository.UsuarioRepository;
import com.desarrolloweb.vuelos.repository.VueloRepository;
import com.desarrolloweb.vuelos.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Carga inicial: crea el administrador y algunos vuelos de ejemplo la primera
 * vez que arranca la aplicacion sobre una base de datos vacia.
 */
@Configuration
public class DatosInicialesConfig {

    private static final Logger log = LoggerFactory.getLogger(DatosInicialesConfig.class);

    @Bean
    public CommandLineRunner cargarDatos(UsuarioRepository usuarioRepository,
                                         UsuarioService usuarioService,
                                         VueloRepository vueloRepository,
                                         @Value("${app.admin.nombre:admin}") String adminNombre,
                                         @Value("${app.admin.clave:Admin12345}") String adminClave,
                                         @Value("${app.admin.email:admin@gestionvuelos.com}") String adminEmail) {
        return args -> {
            if (usuarioRepository.count() == 0) {
                Usuario admin = new Usuario();
                admin.setNombre(adminNombre);
                admin.setEmail(adminEmail);
                admin.setRol(Rol.ADMIN);
                usuarioService.guardar(admin, adminClave);

                Usuario operador = new Usuario();
                operador.setNombre("operador");
                operador.setEmail("operador@gestionvuelos.com");
                operador.setRol(Rol.OPERADOR);
                usuarioService.guardar(operador, "Operador123");

                log.info("Usuarios iniciales creados: {} (ADMIN) y operador (OPERADOR)", adminNombre);
            }

            if (vueloRepository.count() == 0) {
                vueloRepository.save(crear("AV9412", "Avianca", "Despegar", LocalDate.of(2026, 1, 12),
                        LocalDateTime.of(2026, 2, 3, 6, 40), LocalDateTime.of(2026, 2, 3, 8, 5),
                        EstadoVuelo.ATERRIZADO, "480000", "Laura Restrepo", "14C", "Airbus A320",
                        "Rafael Nunez (CTG)", "El Dorado (BOG)", "Cap. Andres Mejia"));

                vueloRepository.save(crear("LA4131", "Latam", "Viajes Falabella", LocalDate.of(2026, 2, 1),
                        LocalDateTime.of(2026, 3, 14, 15, 20), LocalDateTime.of(2026, 3, 14, 16, 50),
                        EstadoVuelo.PROGRAMADO, "612500", "Juan Camilo Ortiz", "7A", "Airbus A319",
                        "El Dorado (BOG)", "Jose Maria Cordova (MDE)", "Cap. Silvia Rojas"));

                vueloRepository.save(crear("CM0231", "Copa Airlines", "Aviatur", LocalDate.of(2026, 1, 28),
                        LocalDateTime.of(2026, 3, 20, 9, 15), LocalDateTime.of(2026, 3, 20, 11, 40),
                        EstadoVuelo.RETRASADO, "1350000", "Marcela Pineda", "3F", "Boeing 737-800",
                        "Rafael Nunez (CTG)", "Tocumen (PTY)", "Cap. Hector Villamil"));

                vueloRepository.save(crear("VV1180", "Wingo", "Despegar", LocalDate.of(2026, 2, 10),
                        LocalDateTime.of(2026, 4, 2, 18, 0), LocalDateTime.of(2026, 4, 2, 19, 25),
                        EstadoVuelo.CANCELADO, "295000", "Diego Salazar", "22B", "Boeing 737-700",
                        "Rafael Nunez (CTG)", "El Dorado (BOG)", "Cap. Andres Mejia"));

                vueloRepository.save(crear("AV8532", "Avianca", "Aviatur", LocalDate.of(2026, 2, 18),
                        LocalDateTime.of(2026, 4, 11, 5, 30), LocalDateTime.of(2026, 4, 11, 7, 10),
                        EstadoVuelo.PROGRAMADO, "725000", "Paula Mendoza", "9D", "Airbus A320neo",
                        "El Dorado (BOG)", "Alfonso Bonilla (CLO)", "Cap. Silvia Rojas"));

                log.info("Vuelos de ejemplo cargados: {}", vueloRepository.count());
            }
        };
    }

    private Vuelo crear(String numero, String aerolinea, String agencia, LocalDate compra,
                        LocalDateTime salida, LocalDateTime llegada, EstadoVuelo estado, String valor,
                        String cliente, String puesto, String avion, String origen, String destino,
                        String piloto) {
        Vuelo v = new Vuelo();
        v.setNumero(numero);
        v.setAerolinea(aerolinea);
        v.setAgenciaViajes(agencia);
        v.setFechaCompra(compra);
        v.setFechaSalida(salida);
        v.setFechaLlegada(llegada);
        v.setEstado(estado);
        v.setValor(new BigDecimal(valor));
        v.setCliente(cliente);
        v.setPuesto(puesto);
        v.setAvion(avion);
        v.setAeropuertoSalida(origen);
        v.setAeropuertoLlegada(destino);
        v.setPiloto(piloto);
        return v;
    }
}