package com.desarrolloweb.vuelos.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad del ejercicio 18: Vuelo.
 */
@Entity
@Table(name = "vuelos", indexes = {
        @Index(name = "idx_vuelo_aerolinea", columnList = "aerolinea"),
        @Index(name = "idx_vuelo_estado", columnList = "estado"),
        @Index(name = "idx_vuelo_fecha_salida", columnList = "fecha_salida")
})
public class Vuelo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El numero de vuelo es obligatorio")
    @Size(max = 15, message = "Maximo 15 caracteres")
    @Column(nullable = false, length = 15)
    private String numero;

    @NotBlank(message = "La aerolinea es obligatoria")
    @Column(nullable = false, length = 80)
    private String aerolinea;

    @Size(max = 80)
    @Column(name = "agencia_viajes", length = 80)
    private String agenciaViajes;

    @NotNull(message = "La fecha de compra es obligatoria")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "fecha_compra", nullable = false)
    private LocalDate fechaCompra;

    @NotNull(message = "La fecha de salida es obligatoria")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(name = "fecha_salida", nullable = false)
    private LocalDateTime fechaSalida;

    @NotNull(message = "La fecha de llegada es obligatoria")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(name = "fecha_llegada", nullable = false)
    private LocalDateTime fechaLlegada;

    @NotNull(message = "El estado es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoVuelo estado = EstadoVuelo.PROGRAMADO;

    @NotNull(message = "El valor es obligatorio")
    @DecimalMin(value = "0.0", message = "El valor no puede ser negativo")
    @Digits(integer = 12, fraction = 2)
    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal valor;

    @NotBlank(message = "El cliente es obligatorio")
    @Column(nullable = false, length = 120)
    private String cliente;

    @NotBlank(message = "El puesto es obligatorio")
    @Size(max = 6, message = "Ejemplo de puesto valido: 14C")
    @Column(nullable = false, length = 6)
    private String puesto;

    @NotBlank(message = "El avion es obligatorio")
    @Column(nullable = false, length = 60)
    private String avion;

    @NotBlank(message = "El aeropuerto de salida es obligatorio")
    @Column(name = "aeropuerto_salida", nullable = false, length = 80)
    private String aeropuertoSalida;

    @NotBlank(message = "El aeropuerto de llegada es obligatorio")
    @Column(name = "aeropuerto_llegada", nullable = false, length = 80)
    private String aeropuertoLlegada;

    @NotBlank(message = "El piloto es obligatorio")
    @Column(nullable = false, length = 120)
    private String piloto;

    public Vuelo() {
    }

    /** Duracion del vuelo en minutos, calculada (no se persiste). */
    @Transient
    public long getDuracionMinutos() {
        if (fechaSalida == null || fechaLlegada == null) {
            return 0;
        }
        return Duration.between(fechaSalida, fechaLlegada).toMinutes();
    }

    /** Ruta legible para listados y reportes. */
    @Transient
    public String getRuta() {
        return aeropuertoSalida + " -> " + aeropuertoLlegada;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getAerolinea() {
        return aerolinea;
    }

    public void setAerolinea(String aerolinea) {
        this.aerolinea = aerolinea;
    }

    public String getAgenciaViajes() {
        return agenciaViajes;
    }

    public void setAgenciaViajes(String agenciaViajes) {
        this.agenciaViajes = agenciaViajes;
    }

    public LocalDate getFechaCompra() {
        return fechaCompra;
    }

    public void setFechaCompra(LocalDate fechaCompra) {
        this.fechaCompra = fechaCompra;
    }

    public LocalDateTime getFechaSalida() {
        return fechaSalida;
    }

    public void setFechaSalida(LocalDateTime fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    public LocalDateTime getFechaLlegada() {
        return fechaLlegada;
    }

    public void setFechaLlegada(LocalDateTime fechaLlegada) {
        this.fechaLlegada = fechaLlegada;
    }

    public EstadoVuelo getEstado() {
        return estado;
    }

    public void setEstado(EstadoVuelo estado) {
        this.estado = estado;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getPuesto() {
        return puesto;
    }

    public void setPuesto(String puesto) {
        this.puesto = puesto;
    }

    public String getAvion() {
        return avion;
    }

    public void setAvion(String avion) {
        this.avion = avion;
    }

    public String getAeropuertoSalida() {
        return aeropuertoSalida;
    }

    public void setAeropuertoSalida(String aeropuertoSalida) {
        this.aeropuertoSalida = aeropuertoSalida;
    }

    public String getAeropuertoLlegada() {
        return aeropuertoLlegada;
    }

    public void setAeropuertoLlegada(String aeropuertoLlegada) {
        this.aeropuertoLlegada = aeropuertoLlegada;
    }

    public String getPiloto() {
        return piloto;
    }

    public void setPiloto(String piloto) {
        this.piloto = piloto;
    }
}