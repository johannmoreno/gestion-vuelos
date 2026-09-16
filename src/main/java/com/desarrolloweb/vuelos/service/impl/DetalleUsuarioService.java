package com.desarrolloweb.vuelos.service.impl;

import com.desarrolloweb.vuelos.entity.Usuario;
import com.desarrolloweb.vuelos.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Puente entre la entidad Usuario de la base de datos y Spring Security.
 * El nombre de usuario del login es el atributo "nombre" de la entidad.
 */
@Service
public class DetalleUsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public DetalleUsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String nombre) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByNombre(nombre)
                .or(() -> usuarioRepository.findByEmail(nombre))
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no registrado: " + nombre));

        return User.withUsername(usuario.getNombre())
                .password(usuario.getClave())
                .roles(usuario.getRol().name())
                .disabled(!usuario.isActivo())
                .build();
    }
}