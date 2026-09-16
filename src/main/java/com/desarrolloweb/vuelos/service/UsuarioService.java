package com.desarrolloweb.vuelos.service;

import com.desarrolloweb.vuelos.entity.Rol;
import com.desarrolloweb.vuelos.entity.Usuario;

import java.util.List;
import java.util.Optional;

/**
 * Capa Service: contrato de la logica de negocio de Usuario.
 */
public interface UsuarioService {

    List<Usuario> listar();

    Optional<Usuario> buscarPorId(Long id);

    Usuario guardar(Usuario usuario, String claveEnTextoPlano);

    void eliminar(Long id);

    List<Usuario> reportePorRolYEstado(Rol rol, Boolean activo);

    List<Usuario> reportePorNombreOCorreo(String texto);

    List<Object[]> contarPorRol();

    boolean nombreDisponible(String nombre, Long idActual);

    boolean emailDisponible(String email, Long idActual);
}