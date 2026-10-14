package com.proyecto.services;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void enviarCorreoRecuperacion(String destino, String tokenReseteo, String nombreCompleto) {
        try {
            // Creamos un mensaje MIME (Soporta HTML)
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

            helper.setTo(destino);
            helper.setSubject("Recupera tu contraseña - Dablyn Delivery");

            String enlaceReseteo = "http://localhost:8080/api/recuperar-password/reset?token=" + tokenReseteo;

            // Plantilla HTML profesional embebida
            String htmlContent = "<!DOCTYPE html>"
                    + "<html>"
                    + "<body style='margin: 0; padding: 0; background-color: #f4f5f7; font-family: Arial, sans-serif;'>"
                    + "  <table width='100%' border='0' cellspacing='0' cellpadding='0' style='background-color: #f4f5f7; padding: 40px 20px;'>"
                    + "    <tr>"
                    + "      <td align='center'>"
                    + "        <table width='600' border='0' cellspacing='0' cellpadding='0' style='background-color: #ffffff; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 15px rgba(0,0,0,0.1);'>"
                    // CABECERA (Fondo oscuro con logo)
                    + "          <tr>"
                    + "            <td style='background-color: #0a0a0a; padding: 35px; text-align: center;'>"
                    + "              <h1 style='color: #ffffff; margin: 0; font-style: italic; font-size: 28px; letter-spacing: 1px;'>"
                    + "                Dablyn <span style='color: #ff6600;'>Delivery</span>"
                    + "              </h1>"
                    + "            </td>"
                    + "          </tr>"
                    // CUERPO DEL CORREO
                    + "          <tr>"
                    + "            <td style='padding: 50px 40px; text-align: center;'>"
                    + "              <p style='color: #888888; font-size: 13px; text-transform: uppercase; letter-spacing: 2px; margin-bottom: 10px; font-weight: bold;'>Seguridad de la cuenta</p>"
                    + "              <h2 style='color: #1a1a1a; font-size: 26px; margin-top: 0; margin-bottom: 25px;'>¡Estás a un clic de<br>cambiar tu contraseña!</h2>"
                    + "              <p style='color: #555555; font-size: 16px; line-height: 1.6; margin-bottom: 35px;'>"
                    + "                Hola, <strong>" + nombreCompleto + "</strong>.<br>"
                    + "                Has solicitado un cambio de contraseña para tu cuenta. Accede al siguiente enlace para poder crear una nueva."
                    + "              </p>"
                    // BOTÓN CTA
                    + "              <a href='" + enlaceReseteo + "' style='background-color: #ff6600; color: #ffffff; text-decoration: none; padding: 16px 35px; border-radius: 8px; font-size: 16px; font-weight: bold; display: inline-block;'>"
                    + "                Recuperar contraseña aquí"
                    + "              </a>"
                    // ADVERTENCIA
                    + "              <p style='color: #999999; font-size: 13px; line-height: 1.5; margin-top: 40px;'>"
                    + "                Si no solicitaste el cambio de tu contraseña, puedes obviar este mensaje y tu cuenta seguirá segura."
                    + "              </p>"
                    + "            </td>"
                    + "          </tr>"
                    // FOOTER
                    + "          <tr>"
                    + "            <td style='background-color: #f9f9f9; padding: 25px; text-align: center; border-top: 1px solid #eeeeee;'>"
                    + "              <p style='color: #aaaaaa; font-size: 12px; margin: 0;'>"
                    + "                © 2026 Dablyn Delivery. Todos los derechos reservados.<br>"
                    + "              </p>"
                    + "            </td>"
                    + "          </tr>"
                    + "        </table>"
                    + "      </td>"
                    + "    </tr>"
                    + "  </table>"
                    + "</body>"
                    + "</html>";

            // true indica que el contenido es HTML
            helper.setText(htmlContent, true);

            mailSender.send(mensaje);
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al enviar el correo HTML de recuperación");
        }
    }
 // ... (Tu método anterior de enviarCorreoRecuperacion)

    // NUEVO MÉTODO PARA REGISTRO
    public void enviarCorreoVerificacion(String destino, String codigo, String nombreCompleto) {
        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

            helper.setTo(destino);
            helper.setSubject("Verifica tu cuenta - Dablyn Delivery");

            // Plantilla HTML profesional para el código de verificación
            String htmlContent = "<!DOCTYPE html>"
                    + "<html>"
                    + "<body style='margin: 0; padding: 0; background-color: #f4f5f7; font-family: Arial, sans-serif;'>"
                    + "  <table width='100%' border='0' cellspacing='0' cellpadding='0' style='background-color: #f4f5f7; padding: 40px 20px;'>"
                    + "    <tr>"
                    + "      <td align='center'>"
                    + "        <table width='600' border='0' cellspacing='0' cellpadding='0' style='background-color: #ffffff; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 15px rgba(0,0,0,0.1);'>"
                    // CABECERA
                    + "          <tr>"
                    + "            <td style='background-color: #0a0a0a; padding: 35px; text-align: center;'>"
                    + "              <h1 style='color: #ffffff; margin: 0; font-style: italic; font-size: 28px; letter-spacing: 1px;'>"
                    + "                Dablyn <span style='color: #ff6600;'>Delivery</span>"
                    + "              </h1>"
                    + "            </td>"
                    + "          </tr>"
                    // CUERPO DEL CORREO
                    + "          <tr>"
                    + "            <td style='padding: 50px 40px; text-align: center;'>"
                    + "              <p style='color: #888888; font-size: 13px; text-transform: uppercase; letter-spacing: 2px; margin-bottom: 10px; font-weight: bold;'>Bienvenido</p>"
                    + "              <h2 style='color: #1a1a1a; font-size: 26px; margin-top: 0; margin-bottom: 25px;'>¡Verifica tu cuenta!</h2>"
                    + "              <p style='color: #555555; font-size: 16px; line-height: 1.6; margin-bottom: 35px;'>"
                    + "                Hola, <strong>" + nombreCompleto + "</strong>.<br>"
                    + "                Gracias por registrarte en Dablyn Delivery. Ingresa el siguiente código de 6 dígitos en la aplicación para verificar tu correo electrónico y activar tu cuenta."
                    + "              </p>"
                    // CÓDIGO DESTACADO
                    + "              <div style='background-color: #fff4e6; border: 2px dashed #ff6600; color: #ff6600; padding: 20px; font-size: 32px; font-weight: bold; letter-spacing: 8px; border-radius: 8px; display: inline-block; margin-bottom: 30px;'>"
                    + "                " + codigo
                    + "              </div>"
                    // ADVERTENCIA
                    + "              <p style='color: #999999; font-size: 13px; line-height: 1.5; margin-top: 10px;'>"
                    + "                Si no creaste una cuenta en Dablyn Delivery, por favor ignora este correo."
                    + "              </p>"
                    + "            </td>"
                    + "          </tr>"
                    // FOOTER
                    + "          <tr>"
                    + "            <td style='background-color: #f9f9f9; padding: 25px; text-align: center; border-top: 1px solid #eeeeee;'>"
                    + "              <p style='color: #aaaaaa; font-size: 12px; margin: 0;'>"
                    + "                © 2026 Dablyn Delivery. Todos los derechos reservados.<br>"
                    + "              </p>"
                    + "            </td>"
                    + "          </tr>"
                    + "        </table>"
                    + "      </td>"
                    + "    </tr>"
                    + "  </table>"
                    + "</body>"
                    + "</html>";

            helper.setText(htmlContent, true);

            mailSender.send(mensaje);
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al enviar el correo HTML de verificación");
        }
    }
}