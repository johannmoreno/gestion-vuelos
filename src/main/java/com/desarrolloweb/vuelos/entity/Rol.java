package com.desarrolloweb.vuelos.entity;

public enum Rol {
    ADMIN("Administrador"),
    OPERADOR("Operador");

    private final String etiqueta;

    Rol(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}