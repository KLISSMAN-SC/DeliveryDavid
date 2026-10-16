package com.proyecto.config;

import com.proyecto.repository.UsuarioRepository;
import com.proyecto.security.*;
import com.proyecto.services.CustomUserDetailsService;
import jakarta.servlet.DispatcherType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.csrf.CsrfFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12);
  }

  @Bean
  public DaoAuthenticationProvider authenticationProvider(
      CustomUserDetailsService service, PasswordEncoder encoder) {
    var provider = new DaoAuthenticationProvider(service);
    provider.setPasswordEncoder(encoder);
    return provider;
  }

  @Bean
  public SecurityFilterChain filterChain(
      HttpSecurity http,
      DaoAuthenticationProvider provider,
      RateLimits limits,
      SecurityAudit audit,
      UsuarioRepository users,
      @Value("${app.require-https:false}") boolean https)
      throws Exception {
    http.authenticationProvider(provider)
        .authorizeHttpRequests(
            a ->
                a.dispatcherTypeMatchers(DispatcherType.ERROR)
                    .permitAll()
                    .requestMatchers(
                        HttpMethod.GET,
                        "/",
                        "/login",
                        "/registro",
                        "/verificar",
                        "/recuperar-password",
                        "/recuperar-password/reset",
                        "/terminos-condiciones",
                        "/politica-privacidad",
                        "/error",
                        "/csrf")
                    .permitAll()
                    .requestMatchers(
                        HttpMethod.POST,
                        "/login",
                        "/registro/enviar",
                        "/verificar/confirmar",
                        "/recuperar-password",
                        "/recuperar-password/reset")
                    .permitAll()
                    .requestMatchers(
                        HttpMethod.GET,
                        "/vendor/**",
                        "/css/**",
                        "/js/**",
                        "/imagenes/**",
                        "/promociones/**",
                        "/productos/**",
                        "/banner/**",
                        "/categorias_comidas/**",
                        "/carta/**",
                        "/negocios/buscar",
                        "/tipo/**",
                        "/restaurante/**")
                    .permitAll()
                    .requestMatchers(HttpMethod.GET, "/panel", "/panel/")
                    .hasAnyRole("ADMINISTRADOR", "REPARTIDOR")
                    .requestMatchers("/panel/**")
                    .hasRole("ADMINISTRADOR")
                    .requestMatchers("/perfil/**", "/direcciones/**", "/carrito/**", "/pedido/**")
                    .authenticated()
                    .anyRequest()
                    .denyAll())
        .formLogin(
            f ->
                f.loginPage("/login")
                    .loginProcessingUrl("/login")
                    .defaultSuccessUrl("/", true)
                    .failureHandler(
                        (q, s, e) -> {
                          audit.event("LOGIN_FAILURE", "anonymous");
                          s.sendRedirect(q.getContextPath() + "/login?error=true");
                        })
                    .permitAll())
        .logout(
            l ->
                l.logoutUrl("/logout")
                    .logoutSuccessUrl("/")
                    .invalidateHttpSession(true)
                    .clearAuthentication(true)
                    .deleteCookies("JSESSIONID"))
        .csrf(org.springframework.security.config.Customizer.withDefaults())
        .sessionManagement(s -> s.sessionFixation(f -> f.changeSessionId()))
        .exceptionHandling(
            e ->
                e.authenticationEntryPoint(
                        (q, s, x) -> {
                          if ("XMLHttpRequest".equals(q.getHeader("X-Requested-With")))
                            s.sendError(401);
                          else s.sendRedirect(q.getContextPath() + "/login");
                        })
                    .accessDeniedHandler(
                        (q, s, x) -> {
                          audit.alert("ACCESS_DENIED", q.getServletPath());
                          s.sendError(403);
                        }))
        .addFilterBefore(new RequestSecurityFilter(limits, audit), CsrfFilter.class)
        .addFilterBefore(new SessionValidityFilter(users), AuthorizationFilter.class);
    if (https) http.redirectToHttps(org.springframework.security.config.Customizer.withDefaults());
    return http.build();
  }
}
