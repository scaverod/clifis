-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Versión del servidor:         5.6.30-1 - (Debian)
-- SO del servidor:              debian-linux-gnu
-- HeidiSQL Versión:             9.4.0.5125
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

-- Volcando datos para la tabla clifisBDdev.Cita: ~5 rows (aproximadamente)
/*!40000 ALTER TABLE `Cita` DISABLE KEYS */;
INSERT INTO `Cita` (`Cita_IdPaciente`, `Cita_IdMedico`, `Cita_IdEspecialidad`, `Cita_IdConsulta`, `Cita_Fecha`, `Cita_Id`) VALUES
  (1, 3, 2, 19, '2017-05-16 11:30:00', 1),
  (1, 3, 2, 19, '2017-05-16 12:30:00', 2),
  (1, 3, 2, 19, '2017-05-16 11:00:00', 3),
  (1, 2, 2, 20, '2017-05-25 11:20:00', 4),
  (1, 6, 3, 15, '2017-05-16 09:30:00', 5);
/*!40000 ALTER TABLE `Cita` ENABLE KEYS */;

-- Volcando datos para la tabla clifisBDdev.Consulta: ~26 rows (aproximadamente)
/*!40000 ALTER TABLE `Consulta` DISABLE KEYS */;
INSERT INTO `Consulta` (`Con_Id`, `Con_NumSala`, `Con_HoraInicio`, `Con_HoraFin`, `Con_IdEspecialidad`, `Con_DuracionCita`, `Con_IdMedico`, `Con_DiaSemana`) VALUES
  (1, 100, '09:00:00', '13:00:00', 1, 15, 1, 'Lunes'),
  (2, 100, '14:30:00', '19:00:00', 1, 15, 7, 'Lunes'),
  (3, 102, '09:30:00', '12:45:00', 1, 15, 4, 'Lunes'),
  (4, 102, '17:00:00', '20:00:00', 1, 20, 6, 'Lunes'),
  (5, 105, '10:00:00', '13:15:00', 3, 15, 6, 'Lunes'),
  (6, 107, '11:00:00', '13:00:00', 4, 30, 5, 'Lunes'),
  (7, 107, '16:00:00', '18:15:00', 4, 30, 5, 'Lunes'),
  (8, 100, '09:00:00', '13:00:00', 1, 15, 1, 'Miércoles'),
  (9, 100, '14:30:00', '19:00:00', 1, 15, 7, 'Miércoles'),
  (10, 102, '09:30:00', '12:45:00', 1, 15, 4, 'Miércoles'),
  (11, 102, '17:00:00', '20:00:00', 1, 20, 6, 'Miércoles'),
  (12, 105, '10:00:00', '13:15:00', 3, 15, 6, 'Miércoles'),
  (13, 107, '11:00:00', '13:00:00', 4, 30, 5, 'Miércoles'),
  (14, 107, '16:00:00', '18:15:00', 4, 30, 5, 'Miércoles'),
  (15, 100, '09:30:00', '12:30:00', 3, 15, 6, 'Martes'),
  (16, 100, '13:00:00', '15:00:00', 3, 15, 7, 'Martes'),
  (17, 102, '10:15:00', '13:00:00', 1, 10, 4, 'Martes'),
  (18, 102, '13:15:00', '15:00:00', 1, 15, 2, 'Martes'),
  (19, 103, '09:30:00', '13:30:00', 2, 30, 3, 'Martes'),
  (20, 103, '10:00:00', '12:30:00', 2, 20, 2, 'Jueves'),
  (21, 103, '15:00:00', '18:00:00', 2, 30, 3, 'Jueves'),
  (22, 100, '10:15:00', '12:45:00', 1, 15, 7, 'Jueves'),
  (23, 100, '12:50:00', '14:50:00', 1, 15, 4, 'Jueves'),
  (24, 100, '16:00:00', '19:00:00', 1, 20, 1, 'Jueves'),
  (25, 105, '09:30:00', '13:30:00', 3, 25, 6, 'Jueves'),
  (26, 105, '16:00:00', '18:00:00', 3, 30, 7, 'Jueves');
/*!40000 ALTER TABLE `Consulta` ENABLE KEYS */;

-- Volcando datos para la tabla clifisBDdev.Especialidad: ~4 rows (aproximadamente)
/*!40000 ALTER TABLE `Especialidad` DISABLE KEYS */;
INSERT INTO `Especialidad` (`Es_Id`, `Es_Nombre`) VALUES
  (1, 'Médico de Familia'),
  (2, 'Cirugía ambulatoria'),
  (3, 'Enfermería'),
  (4, 'Odontología');
/*!40000 ALTER TABLE `Especialidad` ENABLE KEYS */;

-- Volcando datos para la tabla clifisBDdev.HistorialMedico: ~0 rows (aproximadamente)
/*!40000 ALTER TABLE `HistorialMedico` DISABLE KEYS */;
/*!40000 ALTER TABLE `HistorialMedico` ENABLE KEYS */;

-- Volcando datos para la tabla clifisBDdev.Medico: ~7 rows (aproximadamente)
/*!40000 ALTER TABLE `Medico` DISABLE KEYS */;
INSERT INTO `Medico` (`Med_NumColegiado`, `Med_Nombre`, `Med_Id`, `Med_Apellidos`, `Med_Password`) VALUES
  (1000, 'Roberto', 1, 'Deltruño', 'a1159e9df3670d549d04524532629f5477ceb7deec9b45e47e8c009506ecb2c8'),
  (2000, 'Luis', 2, 'Deltrán Muñoz', 'a1159e9df3670d549d04524532629f5477ceb7deec9b45e47e8c009506ecb2c8'),
  (3000, 'Adolfo', 3, 'Adelfas', 'a1159e9df3670d549d04524532629f5477ceb7deec9b45e47e8c009506ecb2c8'),
  (4000, 'Servando', 4, 'del Camino Pérez', 'a1159e9df3670d549d04524532629f5477ceb7deec9b45e47e8c009506ecb2c8'),
  (5000, 'Florentino', 5, 'Floripondio Flores', 'a1159e9df3670d549d04524532629f5477ceb7deec9b45e47e8c009506ecb2c8'),
  (6000, 'Juan Simón', 6, 'Flores Carrasco', 'a1159e9df3670d549d04524532629f5477ceb7deec9b45e47e8c009506ecb2c8'),
  (7000, 'Sandino', 7, 'de la Cruz Vía Crucis', 'a1159e9df3670d549d04524532629f5477ceb7deec9b45e47e8c009506ecb2c8');
/*!40000 ALTER TABLE `Medico` ENABLE KEYS */;

-- Volcando datos para la tabla clifisBDdev.Medico_Especialidad: ~10 rows (aproximadamente)
/*!40000 ALTER TABLE `Medico_Especialidad` DISABLE KEYS */;
INSERT INTO `Medico_Especialidad` (`MedEsp_MedId`, `MedEsp_EspId`) VALUES
  (1, 1),
  (7, 1),
  (4, 1),
  (3, 2),
  (2, 2),
  (5, 4),
  (7, 3),
  (6, 3),
  (6, 1),
  (2, 1);
/*!40000 ALTER TABLE `Medico_Especialidad` ENABLE KEYS */;

-- Volcando datos para la tabla clifisBDdev.Paciente: ~6 rows (aproximadamente)
/*!40000 ALTER TABLE `Paciente` DISABLE KEYS */;
INSERT INTO `Paciente` (`Pac_DNI`, `Pac_Nombre`, `Pac_Apellidos`, `Pac_SegMedico`, `Pac_Id`) VALUES
  ('50200145A', 'Romina', 'Valdepower Vodka', 'Asisa', 1),
  ('00324515F', 'Albano', 'Valdepower Vodka', 'Sanitas', 2),
  ('23455245G', 'Robustiano', 'Patrón de la Costra', 'Seguridad Social', 3),
  ('00000145A', 'Castolo', 'Meo Dyc', 'Seguridad Social', 4),
  ('78200743J', 'Chingao', 'Mu', 'Seguridad Social', 5),
  ('34555356V', 'Benito', 'Kamelas', 'Asisa', 6),
  ('01929038M', 'Luz', 'Rojas Marcos', 'Seguridad Social', 7),
  ('34297532G', 'Aquiles', 'Demas Candela', 'Asisa', 8),
  ('22345243C', 'Sol', 'Luna Tazo', 'Seguridad Social', 9),
  ('00000003C', 'Vivian', 'Lejos del Campo', 'Asisa', 10),
  ('00002342J', 'Silvestre', 'Sin Talones', 'Seguridad Social', 11);

/*!40000 ALTER TABLE `Paciente` ENABLE KEYS */;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
