package com.proyecto.security;

import com.proyecto.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

public class SessionValidityFilter extends OncePerRequestFilter {
  private final UsuarioRepository users;

  public SessionValidityFilter(UsuarioRepository users) {
    this.users = users;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest req, HttpServletResponse res, FilterChain chain)
      throws ServletException, IOException {
    var auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null && auth.getPrincipal() instanceof AppUserPrincipal principal) {
      var user = users.findById(principal.getUsuarioId()).orElse(null);
      if (user == null
          || user.getSecurityVersion() != principal.getSecurityVersion()
          || !user.getCorreoElectronico().equals(principal.getUsername())) {
        var session = req.getSession(false);
        if (session != null) session.invalidate();
        SecurityContextHolder.clearContext();
        if ("XMLHttpRequest".equals(req.getHeader("X-Requested-With"))) {
          res.sendError(401);
        } else {
          res.sendRedirect(req.getContextPath() + "/login?sesionExpirada=true");
        }
        return;
      }
    }
    chain.doFilter(req, res);
  }
}
