-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Versión del servidor:         5.6.30-1 - (Debian)
-- SO del servidor:              debian-linux-gnu
-- HeidiSQL Versión:             9.4.0.5125
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT = @@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS = @@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS = 0 */;
/*!40101 SET @OLD_SQL_MODE = @@SQL_MODE, SQL_MODE = 'NO_AUTO_VALUE_ON_ZERO' */;

-- Volcando estructura de base de datos para clifisBDdev
DROP DATABASE IF EXISTS `clifisBDdev`;
CREATE DATABASE IF NOT EXISTS `clifisBDdev` /*!40100 DEFAULT CHARACTER SET utf8
  COLLATE utf8_unicode_ci */;
USE `clifisBDdev`;

create table Gestor
(
  Ges_Password varchar(64) not null
)
;


-- Volcando estructura para tabla clifisBDdev.Cita
DROP TABLE IF EXISTS `Cita`;
CREATE TABLE IF NOT EXISTS `Cita` (
  `Cita_IdPaciente`     INT(11)   NOT NULL,
  `Cita_IdMedico`       INT(11)   NOT NULL,
  `Cita_IdEspecialidad` INT(11)   NOT NULL,
  `Cita_IdConsulta`     INT(11)   NOT NULL,
  `Cita_Fecha`          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `Cita_Id`             INT(11)   NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`Cita_Id`),
  KEY `Cita_Consulta_Con_Id_fk` (`Cita_IdConsulta`),
  KEY `Cita_Especialidad_Es_Id_fk` (`Cita_IdEspecialidad`),
  KEY `Cita_Medico_Med_Id_fk` (`Cita_IdMedico`),
  KEY `Cita_Paciente_Pac_Id_fk` (`Cita_IdPaciente`),
  CONSTRAINT `Cita_Consulta_Con_Id_fk` FOREIGN KEY (`Cita_IdConsulta`) REFERENCES `Consulta` (`Con_Id`),
  CONSTRAINT `Cita_Especialidad_Es_Id_fk` FOREIGN KEY (`Cita_IdEspecialidad`) REFERENCES `Especialidad` (`Es_Id`),
  CONSTRAINT `Cita_Medico_Med_Id_fk` FOREIGN KEY (`Cita_IdMedico`) REFERENCES `Medico` (`Med_Id`),
  CONSTRAINT `Cita_Paciente_Pac_Id_fk` FOREIGN KEY (`Cita_IdPaciente`) REFERENCES `Paciente` (`Pac_Id`)
)
  ENGINE = InnoDB
  AUTO_INCREMENT = 20
  DEFAULT CHARSET = utf8
  COLLATE = utf8_unicode_ci;

-- La exportación de datos fue deseleccionada.
-- Volcando estructura para tabla clifisBDdev.Consulta
DROP TABLE IF EXISTS `Consulta`;
CREATE TABLE IF NOT EXISTS `Consulta` (
  `Con_Id`             INT(11)                 NOT NULL AUTO_INCREMENT,
  `Con_NumSala`        INT(11)                 NOT NULL,
  `Con_HoraInicio`     TIME                    NOT NULL,
  `Con_HoraFin`        TIME                    NOT NULL,
  `Con_IdEspecialidad` INT(11)                 NOT NULL,
  `Con_DuracionCita`   INT(11)                 NOT NULL,
  `Con_IdMedico`       INT(11)                 NOT NULL,
  `Con_DiaSemana`      VARCHAR(20)
                       COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`Con_Id`),
  KEY `Consulta_Especialidad_Es_Id_fk` (`Con_IdEspecialidad`),
  KEY `Consulta_Medico_Med_Id_fk` (`Con_IdMedico`),
  CONSTRAINT `Consulta_Especialidad_Es_Id_fk` FOREIGN KEY (`Con_IdEspecialidad`) REFERENCES `Especialidad` (`Es_Id`),
  CONSTRAINT `Consulta_Medico_Med_Id_fk` FOREIGN KEY (`Con_IdMedico`) REFERENCES `Medico` (`Med_Id`)
)
  ENGINE = InnoDB
  AUTO_INCREMENT = 27
  DEFAULT CHARSET = utf8
  COLLATE = utf8_unicode_ci;

-- La exportación de datos fue deseleccionada.
-- Volcando estructura para tabla clifisBDdev.Especialidad
DROP TABLE IF EXISTS `Especialidad`;
CREATE TABLE IF NOT EXISTS `Especialidad` (
  `Es_Id`     INT(11)                 NOT NULL AUTO_INCREMENT,
  `Es_Nombre` VARCHAR(50)
              COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`Es_Id`)
)
  ENGINE = InnoDB
  AUTO_INCREMENT = 5
  DEFAULT CHARSET = utf8
  COLLATE = utf8_unicode_ci;

-- La exportación de datos fue deseleccionada.
-- Volcando estructura para tabla clifisBDdev.HistorialMedico
DROP TABLE IF EXISTS `HistorialMedico`;
CREATE TABLE HistorialMedico
(
  H_Id             INT AUTO_INCREMENT
    PRIMARY KEY,
  H_IdMedico       INT                                 NOT NULL,
  H_IdEspecialidad INT                                 NOT NULL,
  H_Fecha          TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  H_Comentario     TEXT                                NOT NULL,
  H_IdPaciente     INT                                 NULL,
  CONSTRAINT HistorialMedico_Medico_Med_Id_fk
  FOREIGN KEY (H_IdMedico) REFERENCES clifisBDdev.Medico (Med_Id)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
  CONSTRAINT HistorialMedico_Especialidad_Es_Id_fk
  FOREIGN KEY (H_IdEspecialidad) REFERENCES clifisBDdev.Especialidad (Es_Id)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
  CONSTRAINT HistorialMedico_Paciente_Pac_Id_fk
  FOREIGN KEY (H_IdPaciente) REFERENCES clifisBDdev.Paciente (Pac_Id)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

CREATE INDEX HistorialMedico_Especialidad_Es_Id_fk
  ON HistorialMedico (H_IdEspecialidad);

CREATE INDEX HistorialMedico_Medico_Med_Id_fk
  ON HistorialMedico (H_IdMedico);

CREATE INDEX HistorialMedico_Paciente_Pac_Id_fk
  ON HistorialMedico (H_IdPaciente);

-- La exportación de datos fue deseleccionada.
-- Volcando estructura para tabla clifisBDdev.Medico
DROP TABLE IF EXISTS `Medico`;
CREATE TABLE IF NOT EXISTS `Medico` (
  `Med_NumColegiado` INT(11)                 NOT NULL,
  `Med_Nombre`       VARCHAR(20)
                     COLLATE utf8_unicode_ci NOT NULL,
  `Med_Id`           INT(11)                 NOT NULL AUTO_INCREMENT,
  `Med_Apellidos`    VARCHAR(50)
                     COLLATE utf8_unicode_ci NOT NULL,
  `Med_Password`     VARCHAR(64)
                     COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`Med_Id`)
)
  ENGINE = InnoDB
  AUTO_INCREMENT = 8
  DEFAULT CHARSET = utf8
  COLLATE = utf8_unicode_ci;

-- La exportación de datos fue deseleccionada.
-- Volcando estructura para tabla clifisBDdev.Medico_Especialidad
DROP TABLE IF EXISTS `Medico_Especialidad`;
CREATE TABLE IF NOT EXISTS `Medico_Especialidad` (
  `MedEsp_MedId` INT(11) DEFAULT NULL,
  `MedEsp_EspId` INT(11) DEFAULT NULL,
  KEY `Medico_Especialidad_Especialidad_Es_Id_fk` (`MedEsp_MedId`),
  KEY `Medico_Especialidad_Medico_Med_Id_fk` (`MedEsp_EspId`),
  CONSTRAINT `Medico_Especialidad_Especialidad_Es_Id_fk` FOREIGN KEY (`MedEsp_MedId`) REFERENCES `Medico` (`Med_Id`),
  CONSTRAINT `Medico_Especialidad_Medico_Med_Id_fk` FOREIGN KEY (`MedEsp_EspId`) REFERENCES `Especialidad` (`Es_Id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_unicode_ci;

-- La exportación de datos fue deseleccionada.
-- Volcando estructura para tabla clifisBDdev.Paciente
DROP TABLE IF EXISTS `Paciente`;
CREATE TABLE IF NOT EXISTS `Paciente` (
  `Pac_DNI`       VARCHAR(20)
                  COLLATE utf8_unicode_ci NOT NULL,
  `Pac_Nombre`    VARCHAR(20)
                  COLLATE utf8_unicode_ci NOT NULL,
  `Pac_Apellidos` VARCHAR(50)
                  COLLATE utf8_unicode_ci NOT NULL,
  `Pac_SegMedico` VARCHAR(20)
                  COLLATE utf8_unicode_ci NOT NULL,
  `Pac_Id`        INT(11)                 NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`Pac_Id`)
)
  ENGINE = InnoDB
  AUTO_INCREMENT = 7
  DEFAULT CHARSET = utf8
  COLLATE = utf8_unicode_ci;

-- La exportación de datos fue deseleccionada.
/*!40101 SET SQL_MODE = IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS = IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT = @OLD_CHARACTER_SET_CLIENT */;
