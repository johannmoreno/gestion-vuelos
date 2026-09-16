package com.desarrolloweb.vuelos.repository;

import com.desarrolloweb.vuelos.entity.Rol;
import com.desarrolloweb.vuelos.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Capa Repository: encapsula el acceso a datos de Usuario mediante Spring Data JPA.
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByNombre(String nombre);

    Optional<Usuario> findByEmail(String email);

    boolean existsByNombre(String nombre);

    boolean existsByEmail(String email);

    /**
     * REPORTE 1 DE USUARIO (parametrizado): usuarios por rol y estado (activo/inactivo).
     * El parametro "activo" es opcional: si llega nulo no filtra por ese campo.
     */
    @Query("""
            SELECT u FROM Usuario u
            WHERE (:rol IS NULL OR u.rol = :rol)
              AND (:activo IS NULL OR u.activo = :activo)
            ORDER BY u.rol, u.nombre
            """)
    List<Usuario> reportePorRolYEstado(@Param("rol") Rol rol,
                                       @Param("activo") Boolean activo);

    /**
     * REPORTE 2 DE USUARIO (parametrizado): busqueda por texto en nombre o correo.
     */
    @Query("""
            SELECT u FROM Usuario u
            WHERE LOWER(u.nombre) LIKE LOWER(CONCAT('%', :texto, '%'))
               OR LOWER(u.email) LIKE LOWER(CONCAT('%', :texto, '%'))
            ORDER BY u.nombre
            """)
    List<Usuario> reportePorNombreOCorreo(@Param("texto") String texto);

    /** Conteo de usuarios agrupados por rol, usado en el tablero de inicio. */
    @Query("SELECT u.rol, COUNT(u) FROM Usuario u GROUP BY u.rol ORDER BY u.rol")
    List<Object[]> contarPorRol();
}