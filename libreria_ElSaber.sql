-- =============================================================
-- BASE DE DATOS PARA PROYECTO POO: libreria_ElSaber
-- =============================================================

CREATE DATABASE IF NOT EXISTS libreria_ElSaber
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE libreria_ElSaber;

-- -------------------------------------------------------------
-- 1. Tabla: cliente
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS cliente (
    id_cliente INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    dni VARCHAR(20) NOT NULL,
    direccion VARCHAR(150),
    telefono VARCHAR(20)
);

-- -------------------------------------------------------------
-- 2. Tabla: libro
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS libro (
    id_libro INT AUTO_INCREMENT PRIMARY KEY,
    autor VARCHAR(100) NOT NULL,
    titulo VARCHAR(150) NOT NULL,
    editorial VARCHAR(100),
    materia VARCHAR(100),
    precio DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    stock INT NOT NULL DEFAULT 0
);

-- -------------------------------------------------------------
-- 3. Tabla: cuento
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS cuento (
    id_cuento INT AUTO_INCREMENT PRIMARY KEY,
    autor VARCHAR(100) NOT NULL,
    titulo VARCHAR(150) NOT NULL,
    editorial VARCHAR(100),
    edad_recomendada INT,
    numero_paginas INT,
    precio DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    stock INT NOT NULL DEFAULT 0
);

-- -------------------------------------------------------------
-- 4. Tabla: revista
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS revista (
    id_revista INT AUTO_INCREMENT PRIMARY KEY,
    marca VARCHAR(100) NOT NULL,
    titulo VARCHAR(150) NOT NULL,
    edicion INT,
    fecha_publicacion VARCHAR(50),
    precio DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    stock INT NOT NULL DEFAULT 0
);

-- -------------------------------------------------------------
-- 5. Tabla: producto (relación de productos libro/cuento/revista)
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS producto (
    id_producto INT AUTO_INCREMENT PRIMARY KEY,
    id_cuento INT NULL,
    id_revista INT NULL,
    id_libro INT NULL,
    CONSTRAINT fk_producto_cuento FOREIGN KEY (id_cuento) REFERENCES cuento(id_cuento) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT fk_producto_revista FOREIGN KEY (id_revista) REFERENCES revista(id_revista) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT fk_producto_libro FOREIGN KEY (id_libro) REFERENCES libro(id_libro) ON DELETE SET NULL ON UPDATE CASCADE
);

-- -------------------------------------------------------------
-- 6. Procedimientos Almacenados
-- -------------------------------------------------------------

-- CRL: Registrar Libro
DROP PROCEDURE IF EXISTS CRL;
DELIMITER //
CREATE PROCEDURE CRL(
    IN p_autor VARCHAR(100),
    IN p_titulo VARCHAR(150),
    IN p_editorial VARCHAR(100),
    IN p_materia VARCHAR(100),
    IN p_precio DECIMAL(10,2),
    IN p_stock INT
)
BEGIN
    INSERT INTO libro (autor, titulo, editorial, materia, precio, stock)
    VALUES (p_autor, p_titulo, p_editorial, p_materia, p_precio, p_stock);

    INSERT INTO producto (id_cuento, id_revista, id_libro)
    VALUES (NULL, NULL, LAST_INSERT_ID());
END //
DELIMITER ;

-- CRC: Registrar Cuento
DROP PROCEDURE IF EXISTS CRC;
DELIMITER //
CREATE PROCEDURE CRC(
    IN p_autor VARCHAR(100),
    IN p_titulo VARCHAR(150),
    IN p_editorial VARCHAR(100),
    IN p_edad_recomendada INT,
    IN p_numero_paginas INT,
    IN p_precio DECIMAL(10,2),
    IN p_stock INT
)
BEGIN
    INSERT INTO cuento (autor, titulo, editorial, edad_recomendada, numero_paginas, precio, stock)
    VALUES (p_autor, p_titulo, p_editorial, p_edad_recomendada, p_numero_paginas, p_precio, p_stock);

    INSERT INTO producto (id_cuento, id_revista, id_libro)
    VALUES (LAST_INSERT_ID(), NULL, NULL);
END //
DELIMITER ;

-- CRR: Registrar Revista
DROP PROCEDURE IF EXISTS CRR;
DELIMITER //
CREATE PROCEDURE CRR(
    IN p_marca VARCHAR(100),
    IN p_titulo VARCHAR(150),
    IN p_edicion INT,
    IN p_fecha_publicacion VARCHAR(50),
    IN p_precio DECIMAL(10,2),
    IN p_stock INT
)
BEGIN
    INSERT INTO revista (marca, titulo, edicion, fecha_publicacion, precio, stock)
    VALUES (p_marca, p_titulo, p_edicion, p_fecha_publicacion, p_precio, p_stock);

    INSERT INTO producto (id_cuento, id_revista, id_libro)
    VALUES (NULL, LAST_INSERT_ID(), NULL);
END //
DELIMITER ;

-- -------------------------------------------------------------
-- Datos de prueba iniciales
-- -------------------------------------------------------------
INSERT INTO cliente (nombre, dni, direccion, telefono)
VALUES ('Ana Torres', '72839102', 'Av Lima 123', '987654321');

CALL CRL('Gabriel Garcia Marquez', 'Cien Anos de Soledad', 'Sudamericana', 'Literatura', 45.00, 20);
CALL CRL('Mario Vargas Llosa', 'La Ciudad y los Perros', 'Seix Barral', 'Literatura', 38.50, 15);
CALL CRC('Hans Christian Andersen', 'El Patito Feo', 'Planeta Kids', 5, 32, 18.50, 40);
CALL CRR('Marvel Comics', 'Spider-Man #1', 1, '2024-05-10', 15.00, 30);

