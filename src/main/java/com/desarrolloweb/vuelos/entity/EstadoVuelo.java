package com.desarrolloweb.vuelos.entity;

public enum EstadoVuelo {
    PROGRAMADO("Programado"),
    ABORDANDO("Abordando"),
    EN_VUELO("En vuelo"),
    ATERRIZADO("Aterrizado"),
    RETRASADO("Retrasado"),
    CANCELADO("Cancelado");

    private final String etiqueta;

    EstadoVuelo(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}