-- ========================================
-- Script de inicialización para clearcost_change_db
-- Se ejecuta automáticamente al iniciar el microservicio
-- ========================================

-- Crear tabla de estados de solicitudes de cambio
CREATE TABLE IF NOT EXISTS change_process_statuses (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- Insertar los estados predefinidos (se ejecuta solo si no existen)
INSERT INTO change_process_statuses (id, name) VALUES 
    (1, 'PENDING'),
    (2, 'APPROVED'),
    (3, 'REJECTED')
ON DUPLICATE KEY UPDATE name = VALUES(name);
