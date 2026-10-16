package com.proyecto.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SecurityAudit {
  public static String actor() {
    var a =
        org.springframework.security.core.context.SecurityContextHolder.getContext()
            .getAuthentication();
    if (a == null
        || a instanceof org.springframework.security.authentication.AnonymousAuthenticationToken)
      return "anonymous";
    if (a.getPrincipal() instanceof AppUserPrincipal p) return "user:" + p.getUsuarioId();
    return "principal:" + TokenTools.hash(a.getName()).substring(0, 16);
  }

  private static final Logger LOG = LoggerFactory.getLogger("SECURITY");

  public void event(String event, Object actor) {
    LOG.info("event={} actor={}", clean(event), clean(actor));
  }

  public void alert(String event, Object actor) {
    LOG.warn("SECURITY_ALERT event={} actor={}", clean(event), clean(actor));
  }

  public static String clean(Object input) {
    String value = String.valueOf(input).replaceAll("[\r\n\t]", "_");
    return value.substring(0, Math.min(value.length(), 180));
  }
}
