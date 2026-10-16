package com.proyecto.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.springframework.web.filter.OncePerRequestFilter;

public class RequestSecurityFilter extends OncePerRequestFilter {
  private final RateLimits limits;
  private final SecurityAudit audit;

  public RequestSecurityFilter(RateLimits limits, SecurityAudit audit) {
    this.limits = limits;
    this.audit = audit;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest req, HttpServletResponse res, FilterChain chain)
      throws ServletException, IOException {
    String nonce = TokenTools.token(), id = UUID.randomUUID().toString();
    req.setAttribute("cspNonce", nonce);
    req.setAttribute("requestId", id);
    res.setHeader("X-Request-ID", id);
    res.setHeader(
        "Content-Security-Policy",
        "default-src 'self'; script-src 'self' 'nonce-"
            + nonce
            + "'; script-src-attr 'none'; style-src 'self' 'unsafe-inline'"
            + "; font-src 'self' data:;"
            + " img-src 'self' data:; frame-src https://www.google.com https://maps.google.com;"
            + " connect-src 'self'; object-src 'none'; base-uri 'self'; frame-ancestors 'none';"
            + " form-action 'self'");
    res.setHeader("Permissions-Policy", "camera=(), microphone=(), geolocation=(self)");
    res.setHeader(
        "Referrer-Policy",
        req.getServletPath().startsWith("/recuperar-password")
            ? "no-referrer"
            : "strict-origin-when-cross-origin");
    if (req.getServletPath().startsWith("/recuperar-password"))
      res.setHeader("Cache-Control", "no-store");
    try {
      if ("POST".equals(req.getMethod()) && "/login".equals(req.getServletPath())) {
        limits.check("login-ip", req.getRemoteAddr(), 60, java.time.Duration.ofMinutes(5));
        String name = req.getParameter("username");
        limits.check(
            "login-account",
            name == null ? "" : name.trim().toLowerCase(java.util.Locale.ROOT),
            15,
            java.time.Duration.ofMinutes(5));
      }
      chain.doFilter(req, res);
    } catch (BusinessException e) {
      res.setStatus(e.getStatus().value());
      res.setContentType("text/plain;charset=UTF-8");
      res.getWriter().write(e.getMessage());
    } finally {
      if (res.getStatus() >= 400
          || !java.util.Set.of("GET", "HEAD", "OPTIONS").contains(req.getMethod()))
        audit.event(
            "HTTP_" + req.getMethod() + "_" + res.getStatus(),
            SecurityAudit.actor()
                + " path="
                + SecurityAudit.clean(req.getServletPath())
                + " requestId="
                + id);
    }
  }
}
