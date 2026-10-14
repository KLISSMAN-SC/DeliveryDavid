package com.proyecto.config;

import com.proyecto.services.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import jakarta.servlet.DispatcherType;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Define el algoritmo de encriptación
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Conecta Spring Security con tu BD y el encriptador
    @Bean
    public DaoAuthenticationProvider authenticationProvider(CustomUserDetailsService userDetailsService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, DaoAuthenticationProvider authProvider) throws Exception {
        http
            .authenticationProvider(authProvider) // Activa la validación con BD
            .authorizeHttpRequests(auth -> auth
            	.dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                .requestMatchers("/css/**", "/js/**", "/imagenes/**", "/promociones/**","/productos/**", "/banner/**", "/categorias_comidas/**", "/carta/**").permitAll()
                // AQUÍ AÑADIMOS EL PERMISO PARA LAS RUTAS DE FALLO DE LOGIN
                .requestMatchers("/", "/login", "/login?error=true", "/login?bloqueado=true", "/registro/**", "/verificar/**", "/api/**").permitAll()
                //.requestMatchers("/pedido/mis-pedidos").denyAll()
                .requestMatchers("/panel/**").hasAnyRole("ADMINISTRADOR", "REPARTIDOR")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                    .loginPage("/login")
                    .loginProcessingUrl("/login")
                    .defaultSuccessUrl("/", true) // Si el login es exitoso, redirige al Inicio
                    
                    // CAMBIO AQUÍ: Agregamos el Context Path para que respete el "/api"
                    .failureHandler((request, response, exception) -> {
                        // Extraemos la ruta base de tu proyecto (en tu caso "/api")
                        String contextPath = request.getContextPath();
                        
                        if (exception.getMessage() != null && exception.getMessage().startsWith("CUENTA_BLOQUEADA:")) {
                            String horasRestantes = exception.getMessage().split(":")[1];
                            // Redirigimos sumando el contextPath
                            response.sendRedirect(contextPath + "/login?bloqueado=true&horas=" + horasRestantes);
                        } else {
                            // Redirigimos sumando el contextPath
                            response.sendRedirect(contextPath + "/login?error=true");
                        }
                    })
                    .permitAll()
                )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .permitAll()
            )
            .csrf(csrf -> csrf.disable());

        return http.build();
    }
}