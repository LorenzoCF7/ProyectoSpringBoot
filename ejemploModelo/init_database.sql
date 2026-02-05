-- Script para inicializar la base de datos con datos de prueba
-- Ejecutar con: mysql -u root proyectoSpringboot < init_database.sql

USE proyectoSpringboot;

-- Limpiar datos existentes (en orden por dependencias)
DELETE FROM pago_tarjeta;
DELETE FROM pago_paypa;
DELETE FROM pago_transferencia;
DELETE FROM pago;
DELETE FROM factura;
DELETE FROM suscripcion;
DELETE FROM perfil;
DELETE FROM plan;
DELETE FROM usuario;

-- Insertar Usuarios
INSERT INTO usuario (id, nombre, email, telefono) VALUES
(1, 'Juan Pérez', 'juan.perez@email.com', '555-0001'),
(2, 'María García', 'maria.garcia@email.com', '555-0002'),
(3, 'Carlos López', 'carlos.lopez@email.com', '555-0003');

-- Insertar Planes
INSERT INTO plan (id, nombre, descripcion, precio, duracion_dias) VALUES
(1, 'Plan Básico', 'Acceso básico a contenido', 9.99, 30),
(2, 'Plan Premium', 'Acceso completo sin anuncios', 19.99, 30),
(3, 'Plan Familiar', 'Hasta 5 perfiles simultáneos', 29.99, 30);

-- Insertar Perfiles (relacionados con usuarios)
INSERT INTO perfil (id, nombre, fecha_creacion, usuario_id) VALUES
(1, 'Perfil Principal Juan', '2024-01-01', 1),
(2, 'Perfil Secundario Juan', '2024-01-15', 1),
(3, 'Perfil Principal María', '2024-02-01', 2),
(4, 'Perfil Principal Carlos', '2024-03-01', 3);

-- Insertar Suscripciones (relacionadas con usuarios y planes)
INSERT INTO suscripcion (id, fecha_inicio, fecha_fin, estado, usuario_id, plan_id) VALUES
(1, '2024-01-01', '2024-01-31', 'ACTIVA', 1, 1),
(2, '2024-02-01', '2024-02-29', 'ACTIVA', 2, 2),
(3, '2024-03-01', '2024-03-31', 'MOROSA', 3, 3);

-- Insertar Facturas (relacionadas con suscripciones)
INSERT INTO factura (id, fecha_emision, monto, suscripcion_id) VALUES
(1, '2024-01-01', 9.99, 1),
(2, '2024-02-01', 19.99, 2),
(3, '2024-03-01', 29.99, 3);

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
