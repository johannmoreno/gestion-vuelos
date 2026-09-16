-- Script de creacion de la base de datos - Gestion de Vuelos
-- Ejercicio 18 (Vuelo) - Actividad Spring Boot MVC + Thymeleaf
-- Motor: MySQL 8

CREATE DATABASE IF NOT EXISTS gestion_vuelos
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;

USE gestion_vuelos;

CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(60) NOT NULL UNIQUE,
    clave VARCHAR(120) NOT NULL,
    rol VARCHAR(20) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    activo BOOLEAN NOT NULL DEFAULT TRUE
    );

CREATE TABLE IF NOT EXISTS vuelos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero VARCHAR(15) NOT NULL,
    aerolinea VARCHAR(80) NOT NULL,
    agencia_viajes VARCHAR(80),
    fecha_compra DATE NOT NULL,
    fecha_salida DATETIME NOT NULL,
    fecha_llegada DATETIME NOT NULL,
    estado VARCHAR(20) NOT NULL,
    valor DECIMAL(14,2) NOT NULL,
    cliente VARCHAR(120) NOT NULL,
    puesto VARCHAR(6) NOT NULL,
    avion VARCHAR(60) NOT NULL,
    aeropuerto_salida VARCHAR(80) NOT NULL,
    aeropuerto_llegada VARCHAR(80) NOT NULL,
    piloto VARCHAR(120) NOT NULL,
    INDEX idx_vuelo_aerolinea (aerolinea),
    INDEX idx_vuelo_estado (estado),
    INDEX idx_vuelo_fecha_salida (fecha_salida)
    );

CREATE TABLE IF NOT EXISTS tokens_recuperacion (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(120) NOT NULL UNIQUE,
    usuario_id BIGINT NOT NULL,
    fecha_expiracion DATETIME NOT NULL,
    usado BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_token_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
    );