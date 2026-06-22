-- Carga inicial de Médicos
INSERT IGNORE INTO doctors (id, name, specialty, phone, email) VALUES
(1, 'Dra. María González', 'Cardiología', '555-1001', 'maria.gonzalez@medisalud.com'),
(2, 'Dr. Carlos Ruiz', 'Pediatría', '555-1002', 'carlos.ruiz@medisalud.com'),
(3, 'Dra. Ana López', 'Dermatología', '555-1003', 'ana.lopez@medisalud.com');

-- Ajuste para MySQL
ALTER TABLE doctors AUTO_INCREMENT = 4;

-- Carga inicial de Pacientes
INSERT IGNORE INTO patients (id, name, document, phone, email) VALUES
(1, 'Juan Pérez', '10203040', '555-2001', 'juan.perez@correo.com'),
(2, 'Laura Gómez', '50607080', '555-2002', 'laura.gomez@correo.com');

-- Ajuste para MySQL
ALTER TABLE patients AUTO_INCREMENT = 3;