-- EJEMPLO PARA ADAPTAR POR EL DBA: no ejecutar sin cambiar host y contraseña.
-- La cuenta de la aplicación no necesita ALTER, DROP, CREATE ni permisos globales.
CREATE USER 'delivery_app'@'HOST_DEL_SERVIDOR_APP' IDENTIFIED BY 'REEMPLAZAR_POR_UN_SECRETO_ALEATORIO_NUEVO';
GRANT SELECT, INSERT, UPDATE, DELETE ON proyecto_david.* TO 'delivery_app'@'HOST_DEL_SERVIDOR_APP';
-- Si MySQL se encuentra en otra máquina: habilitar TLS verificado en Connector/J.
-- Usar sslMode=VERIFY_IDENTITY y una CA confiable, sin desactivar la comprobación.
-- Utilizar una cuenta migradora distinta para ejecutar Flyway; no usar root en la app.
