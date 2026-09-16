package com.desarrolloweb.vuelos.repository;

import com.desarrolloweb.vuelos.entity.TokenRecuperacion;
import com.desarrolloweb.vuelos.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TokenRecuperacionRepository extends JpaRepository<TokenRecuperacion, Long> {

    Optional<TokenRecuperacion> findByToken(String token);

    @Modifying
    @Query("UPDATE TokenRecuperacion t SET t.usado = true WHERE t.usuario = :usuario AND t.usado = false")
    void invalidarTokensDe(@Param("usuario") Usuario usuario);
}