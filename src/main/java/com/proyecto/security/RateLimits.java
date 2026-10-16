package com.proyecto.security;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class RateLimits {
  private record Window(long expires, AtomicInteger count) {}

  private final Cache<String, Window> windows =
      Caffeine.newBuilder().maximumSize(20000).expireAfterWrite(Duration.ofMinutes(30)).build();
  private final SecurityAudit audit;

  public RateLimits(SecurityAudit audit) {
    this.audit = audit;
  }

  public void check(String bucket, String identity, int max, Duration duration) {
    String key = bucket + ":" + TokenTools.hash(identity == null ? "unknown" : identity);
    long now = System.nanoTime();
    Window w =
        windows
            .asMap()
            .compute(
                key,
                (k, old) ->
                    old == null || old.expires() < now
                        ? new Window(now + duration.toNanos(), new AtomicInteger())
                        : old);
    if (w.count().incrementAndGet() > max) {
      audit.alert("RATE_LIMIT_" + bucket, key);
      throw new BusinessException(
          HttpStatus.TOO_MANY_REQUESTS,
          "Demasiados intentos. Espera unos minutos e inténtalo de nuevo.");
    }
  }
}
