package com.desarrolloweb.vuelos.config;

import com.desarrolloweb.vuelos.service.impl.DetalleUsuarioService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Configuracion de autenticacion y control de acceso.
 * - Login por formulario contra la tabla usuarios (claves BCrypt).
 * - /usuarios/** solo para el rol ADMIN.
 * - El resto de la aplicacion exige sesion iniciada.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final DetalleUsuarioService detalleUsuarioService;

    public SecurityConfig(DetalleUsuarioService detalleUsuarioService) {
        this.detalleUsuarioService = detalleUsuarioService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider proveedor = new DaoAuthenticationProvider();
        proveedor.setUserDetailsService(detalleUsuarioService);
        proveedor.setPasswordEncoder(passwordEncoder());
        return proveedor;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authenticationProvider(authenticationProvider())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/img/**", "/favicon.ico").permitAll()
                        .requestMatchers("/login", "/recuperar-clave", "/restablecer-clave").permitAll()
                        .requestMatchers("/usuarios/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .usernameParameter("nombre")
                        .passwordParameter("clave")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/login?error")
                        .permitAll())
                .logout(logout -> logout
                        .logoutRequestMatcher(AntPathRequestMatcher.antMatcher("/logout"))
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll())
                .exceptionHandling(ex -> ex.accessDeniedPage("/acceso-denegado"))
                .sessionManagement(session -> session.maximumSessions(1));

        return http.build();
    }
}