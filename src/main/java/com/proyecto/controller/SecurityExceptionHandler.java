package com.proyecto.controller;

import com.proyecto.security.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class SecurityExceptionHandler {
  private Object response(HttpServletRequest req, int status, String message) {
    if ("XMLHttpRequest".equals(req.getHeader("X-Requested-With")))
      return ResponseEntity.status(status)
          .body(
              Map.of(
                  "ok",
                  false,
                  "mensaje",
                  message,
                  "requestId",
                  String.valueOf(req.getAttribute("requestId"))));
    var view = new ModelAndView("error");
    view.setStatus(org.springframework.http.HttpStatus.valueOf(status));
    view.addObject("codigo", status);
    view.addObject("mensaje", message);
    return view;
  }

  @ExceptionHandler(BusinessException.class)
  public Object business(BusinessException e, HttpServletRequest q) {
    return response(q, e.getStatus().value(), e.getMessage());
  }

  @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
  public Object denied(Exception e, HttpServletRequest q) {
    LoggerFactory.getLogger("SECURITY")
        .warn("SECURITY_ALERT ACCESS_DENIED requestId={}", q.getAttribute("requestId"));
    return response(q, 403, "No tienes permiso para realizar esta acción.");
  }

  @ExceptionHandler({
    org.springframework.validation.BindException.class,
    org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class,
    org.springframework.web.bind.MissingServletRequestParameterException.class,
    jakarta.validation.ConstraintViolationException.class,
    IllegalArgumentException.class
  })
  public Object invalid(Exception e, HttpServletRequest q) {
    return response(q, 400, "Los datos enviados no son válidos.");
  }

  @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
  public Object conflict(Exception e, HttpServletRequest q) {
    return response(q, 409, "La operación entra en conflicto con datos existentes o relacionados.");
  }

  @ExceptionHandler(org.springframework.web.multipart.MaxUploadSizeExceededException.class)
  public Object tooLarge(Exception e, HttpServletRequest q) {
    return response(q, 413, "El archivo supera el tamaño permitido.");
  }

  @ExceptionHandler(org.springframework.web.servlet.resource.NoResourceFoundException.class)
  public Object missingResource(Exception e, HttpServletRequest q) {
    return response(q, 404, "Recurso no encontrado.");
  }

  @ExceptionHandler(org.springframework.web.HttpRequestMethodNotSupportedException.class)
  public Object wrongMethod(Exception e, HttpServletRequest q) {
    return response(q, 405, "Método no permitido.");
  }

  @ExceptionHandler(org.springframework.web.HttpMediaTypeNotSupportedException.class)
  public Object wrongContent(Exception e, HttpServletRequest q) {
    return response(q, 415, "Formato de contenido no permitido.");
  }

  @ExceptionHandler(Exception.class)
  public Object unexpected(Exception e, HttpServletRequest q) {
    LoggerFactory.getLogger("SECURITY")
        .error(
            "SECURITY_ALERT SERVER_ERROR requestId={} type={}",
            q.getAttribute("requestId"),
            e.getClass().getName());
    return response(
        q,
        500,
        "No se pudo completar la operación. Contacta al administrador e indica el código de"
            + " solicitud.");
  }
}
