package com.desarrolloweb.vuelos.service.impl;

import com.desarrolloweb.vuelos.entity.Rol;
import com.desarrolloweb.vuelos.entity.Usuario;
import com.desarrolloweb.vuelos.repository.UsuarioRepository;
import com.desarrolloweb.vuelos.service.UsuarioService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

/**
 * Implementacion de la logica de negocio de Usuario.
 * Spring inyecta el repositorio y el codificador de claves por constructor.
 */
@Service
@Transactional
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    /**
     * Si la clave llega vacia en una edicion, se conserva la clave actual.
     * En cualquier otro caso se cifra con BCrypt antes de persistir.
     */
    @Override
    public Usuario guardar(Usuario usuario, String claveEnTextoPlano) {
        if (StringUtils.hasText(claveEnTextoPlano)) {
            usuario.setClave(passwordEncoder.encode(claveEnTextoPlano));
        } else if (usuario.getId() != null) {
            Usuario actual = usuarioRepository.findById(usuario.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + usuario.getId()));
            usuario.setClave(actual.getClave());
        } else {
            throw new IllegalArgumentException("La clave es obligatoria para un usuario nuevo");
        }
        return usuarioRepository.save(usuario);
    }

    @Override
    public void eliminar(Long id) {
        usuarioRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> reportePorRolYEstado(Rol rol, Boolean activo) {
        return usuarioRepository.reportePorRolYEstado(rol, activo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> reportePorNombreOCorreo(String texto) {
        return usuarioRepository.reportePorNombreOCorreo(texto == null ? "" : texto.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> contarPorRol() {
        return usuarioRepository.contarPorRol();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean nombreDisponible(String nombre, Long idActual) {
        return usuarioRepository.findByNombre(nombre)
                .map(u -> u.getId().equals(idActual))
                .orElse(true);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean emailDisponible(String email, Long idActual) {
        return usuarioRepository.findByEmail(email)
                .map(u -> u.getId().equals(idActual))
                .orElse(true);
    }
}