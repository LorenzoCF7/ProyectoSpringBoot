-- Script para inicializar la base de datos con datos de prueba
-- Ejecutar con: mysql -u root proyectoSpringboot < init_database.sql
-- NOTA: Las contraseñas están encriptadas con BCrypt. La contraseña para todos es: 1234

USE proyectoSpringboot;

-- Limpiar datos existentes (en orden por dependencias)
DELETE FROM auditoria_log;
DELETE FROM pago_tarjeta;
DELETE FROM pago_paypa;
DELETE FROM pago_transferencia;
DELETE FROM pago;
DELETE FROM factura;
DELETE FROM suscripcion;
DELETE FROM perfil;
DELETE FROM plan;
DELETE FROM usuario;

-- Insertar Usuarios (contraseña: 1234 encriptada con BCrypt)
-- admin@admin.com / 1234 -> ROLE_ADMIN
-- juan.perez@email.com / 1234 -> ROLE_USER
-- maria.garcia@email.com / 1234 -> ROLE_USER
INSERT INTO usuario (id, email, password, pais, activo, rol) VALUES
(1, 'admin@admin.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 0, true, 'ROLE_ADMIN'),
(2, 'juan.perez@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 0, true, 'ROLE_USER'),
(3, 'maria.garcia@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 1, true, 'ROLE_USER');

-- Insertar Planes (3 niveles: Basic, Premium, Enterprise)
INSERT INTO plan (id, nombre, descripcion, precio, duracion_meses) VALUES
(1, 'Basic', 'Plan básico con acceso limitado. Ideal para usuarios individuales.', 9.99, 1),
(2, 'Premium', 'Plan premium con acceso completo y soporte prioritario.', 19.99, 6),
(3, 'Enterprise', 'Plan empresarial con acceso ilimitado, soporte 24/7 y funciones avanzadas.', 49.99, 12);

-- Insertar Perfiles (relacionados con usuarios)
INSERT INTO perfil (id, nombre, apellido, telefono, usuario_id) VALUES
(1, 'Juan', 'Pérez', '555-0001', 2),
(2, 'Juan Segundo', 'Pérez', '555-0002', 2),
(3, 'María', 'García', '555-0003', 3);

-- Insertar Suscripciones (relacionadas con perfiles, usuarios y planes)
INSERT INTO suscripcion (id, fecha_inicio, fecha_fin, estado, perfil_id, usuario_id, plan_id, renovacion_automatica) VALUES
(1, '2024-01-01', '2024-02-01', 0, 1, 2, 1, false),
(2, '2024-02-01', '2024-08-01', 0, 3, 3, 2, true);

-- Insertar Facturas (relacionadas con suscripciones)
INSERT INTO factura (id, fecha_emision, monto, suscripcion_id, pagada) VALUES
(1, '2024-01-01', 9.99, 1, false),
(2, '2024-02-01', 19.99, 2, false);

-- Mostrar datos insertados
SELECT 'Usuarios insertados:' as '';
SELECT * FROM usuario;

SELECT 'Planes insertados:' as '';
SELECT * FROM plan;

SELECT 'Perfiles insertados:' as '';
SELECT * FROM perfil;

SELECT 'Suscripciones insertadas:' as '';
SELECT * FROM suscripcion;

SELECT 'Facturas insertadas:' as '';
SELECT * FROM factura;

SELECT 'Base de datos inicializada correctamente!' as '';
