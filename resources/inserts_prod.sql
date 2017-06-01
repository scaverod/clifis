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

-- Volcando datos para la tabla clifisBD.Cita: ~40 rows (aproximadamente)
/*!40000 ALTER TABLE `Cita` DISABLE KEYS */;
INSERT INTO `Cita` (`Cita_IdPaciente`, `Cita_IdMedico`, `Cita_IdEspecialidad`, `Cita_IdConsulta`, `Cita_Fecha`, `Cita_Id`) VALUES
  (1, 3, 2, 19, '2017-05-16 11:30:00', 1),
  (1, 3, 2, 19, '2017-05-16 12:30:00', 2),
  (1, 3, 2, 19, '2017-05-16 11:00:00', 3),
  (1, 2, 2, 20, '2017-05-25 11:20:00', 4),
  (1, 6, 3, 15, '2017-05-16 09:30:00', 5),
  (1, 1, 1, 1, '2017-05-29 13:45:00', 20),
  (1, 6, 3, 15, '2017-05-30 09:30:00', 21),
  (1, 6, 3, 12, '2017-05-31 12:45:00', 22),
  (1, 6, 3, 12, '2017-05-31 13:00:00', 23),
  (1, 6, 3, 25, '2017-06-01 12:50:00', 24),
  (2, 6, 3, 5, '2017-05-29 13:45:00', 25),
  (2, 4, 1, 17, '2017-05-30 10:15:00', 26),
  (2, 5, 4, 7, '2017-05-29 18:00:00', 27),
  (2, 6, 3, 12, '2017-05-31 12:15:00', 28),
  (3, 7, 1, 2, '2017-05-29 18:00:00', 29),
  (3, 5, 4, 13, '2017-05-31 12:30:00', 30),
  (3, 2, 1, 18, '2017-05-30 14:45:00', 31),
  (3, 3, 2, 21, '2017-06-01 17:30:00', 32),
  (4, 6, 3, 5, '2017-05-29 13:30:00', 33),
  (4, 5, 4, 7, '2017-05-29 17:30:00', 34),
  (4, 4, 1, 17, '2017-05-30 11:45:00', 35),
  (4, 3, 2, 21, '2017-06-01 17:00:00', 36),
  (4, 4, 1, 10, '2017-05-31 11:45:00', 37),
  (5, 3, 2, 19, '2017-05-30 13:00:00', 38),
  (5, 5, 4, 13, '2017-05-31 12:00:00', 39),
  (5, 6, 3, 12, '2017-05-31 12:30:00', 40),
  (6, 3, 2, 19, '2017-05-30 12:30:00', 41),
  (6, 7, 1, 2, '2017-05-29 17:00:00', 42),
  (6, 5, 4, 14, '2017-05-31 18:00:00', 43),
  (7, 6, 1, 4, '2017-05-29 19:20:00', 44),
  (7, 6, 3, 15, '2017-05-30 12:00:00', 45),
  (8, 3, 2, 19, '2017-05-30 10:00:00', 47),
  (8, 7, 1, 2, '2017-05-29 17:15:00', 48),
  (8, 5, 4, 13, '2017-05-31 11:00:00', 49),
  (9, 2, 1, 18, '2017-05-30 14:30:00', 50),
  (9, 5, 4, 14, '2017-05-31 17:30:00', 51),
  (10, 3, 2, 19, '2017-05-30 12:00:00', 52),
  (10, 1, 1, 1, '2017-05-29 13:30:00', 53),
  (11, 7, 1, 2, '2017-05-29 16:45:00', 54),
  (11, 5, 4, 14, '2017-05-31 17:00:00', 55);
/*!40000 ALTER TABLE `Cita` ENABLE KEYS */;

-- Volcando datos para la tabla clifisBD.Consulta: ~26 rows (aproximadamente)
/*!40000 ALTER TABLE `Consulta` DISABLE KEYS */;
INSERT INTO `Consulta` (`Con_Id`, `Con_NumSala`, `Con_HoraInicio`, `Con_HoraFin`, `Con_IdEspecialidad`, `Con_DuracionCita`, `Con_IdMedico`, `Con_DiaSemana`) VALUES
  (1, 100, '09:00:00', '14:00:00', 1, 15, 1, 'Lunes'),
  (2, 100, '14:30:00', '19:00:00', 1, 15, 7, 'Lunes'),
  (3, 102, '09:30:00', '12:45:00', 1, 15, 4, 'Lunes'),
  (4, 102, '17:00:00', '20:00:00', 1, 20, 6, 'Lunes'),
  (5, 105, '10:00:00', '14:00:00', 3, 15, 6, 'Lunes'),
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

-- Volcando datos para la tabla clifisBD.Especialidad: ~4 rows (aproximadamente)
/*!40000 ALTER TABLE `Especialidad` DISABLE KEYS */;
INSERT INTO `Especialidad` (`Es_Id`, `Es_Nombre`) VALUES
  (1, 'Médico de Familia'),
  (2, 'Cirugía ambulatoria'),
  (3, 'Enfermería'),
  (4, 'Odontología');
/*!40000 ALTER TABLE `Especialidad` ENABLE KEYS */;

-- Volcando datos para la tabla clifisBD.Gestor: ~1 rows (aproximadamente)
/*!40000 ALTER TABLE `Gestor` DISABLE KEYS */;
INSERT INTO `Gestor` (`Ges_Password`) VALUES
  ('a1159e9df3670d549d04524532629f5477ceb7deec9b45e47e8c009506ecb2c8');
/*!40000 ALTER TABLE `Gestor` ENABLE KEYS */;

-- Volcando datos para la tabla clifisBD.HistorialMedico: ~15 rows (aproximadamente)
/*!40000 ALTER TABLE `HistorialMedico` DISABLE KEYS */;
INSERT INTO `HistorialMedico` (`H_Id`, `H_IdMedico`, `H_IdEspecialidad`, `H_Fecha`, `H_Comentario`, `H_IdPaciente`) VALUES
  (1, 3, 2, '2017-05-29 00:00:00', 'Paciente portadora de una diabetes mellitus, controlada con régimen (que sigue en forma irregular), e hipoglicemiantes orales. Desde hace unos dos a tres meses presenta polidipsia, poliuria y ha bajado de peso. Las veces que se ha controlado la glicemia, ha estado sobre 200 mg/dL. Desde tres días atrás comenzó a notar disuria dolorosa y poliaquiuria. También ha sentido un dolor sordo ubicado en la región lumbar derecha y cree haber tenido fiebre, pero no se la ha registrado. La orina la ha notado más fuerte de olor.', 1),
  (2, 4, 1, '2017-05-27 00:00:00', 'Estos son algunos de los síntomas comunes de leucemia son:\r\n\r\nFiebre, escalofríos y otros síntomas parecidos a los de la gripe\r\nLa debilidad y la fatiga\r\nInfecciones frecuentes\r\nPérdida de apetito y/o pérdida de peso\r\nInflamación o sensibilidad de los ganglios linfáticos, el hígado o el bazo', 1),
  (3, 4, 1, '2017-05-25 00:00:00', 'Síntomas relativos a los ganglios linfáticos, el hígado y el bazo:\r\nInflamación de los ganglios linfáticos\r\nSensibilidad de los ganglios linfáticos\r\nInflamación del hígado\r\nSensibilidad del hígado\r\nInflamación del bazo', 1),
  (4, 4, 1, '2017-05-25 00:00:00', 'Síntomas relativos a los ganglios linfáticos, el hígado y el bazo:\r\nInflamación de los ganglios linfáticos\r\nSensibilidad de los ganglios linfáticos\r\nInflamación del hígado\r\nSensibilidad del hígado\r\nInflamación del bazo', 2),
  (5, 4, 1, '2017-05-25 00:00:00', 'Enfermedades en los ojos\r\nÚlceras en la córnea\r\nQueratitis\r\nPérdida de la visión', 4),
  (6, 4, 1, '2017-04-25 00:00:00', 's enfermedades infecciosas son enfermedades ocasionadas por gérmenes (microbios). Es importante comprender que no todos los gérmenes (bacterias, virus, hongos y parásitos) ocasionan enfermedades. De hecho, un huésped de las bacterias generalmente vive en la piel, párpados, nariz y boca, y en el intestino. Estas bacterias se denominan flora normal y se consideran habitantes normales. ¡Esta flora normal es útil para nosotros!', 4),
  (7, 4, 1, '2017-04-05 00:00:00', 'Algunos factores importantes en el niño incluyen la edad, inmunidad, nutrición, composición genética y salud en general. Los recién nacidos están en riesgo ya que sus sistemas de protección aún no han sido puestos a prueba y no siempre están maduros. Los bebés están en riesgo ya que tie', 4),
  (8, 6, 3, '2017-04-05 00:00:00', 'No todas las infecciones son contagiosas (se pueden propagar de persona a persona). Las infecciones de oído y vejiga no se propagan de niño a niño, mientras que la diarrea y los resfriados se propagan fácilmente.', 6),
  (9, 6, 1, '2016-08-10 00:00:00', 'Los factores en las bacterias, virus y hongos incluyen genes que determinan qué tan dañino (virulento) puede ser el microbio. Algunos gérmenes crean toxinas que ocasionan enfermedades por sí mismas o contribuyen con las infecciones ocasionadas por el germen. Entre los ejemplos están las enterotoxinas, que ocasionan diarrea; toxinas del tétanos, que ocasionan tétanos; y toxinas de shock tóxico, que ocasiona un presión arterial baja y colapsos (shock).', 6),
  (10, 6, 1, '2017-04-05 00:00:00', 'El período de incubación es el tiempo que transcurre después de que se infecta un niño hasta que se vuelve una enfermedad. Algunas veces la incubación es más corta (por ejemplo, un día o más para la gripe), mientras que otras veces es un poco más (por ejemplo, 2 semanas para la varicela y muchos años para el virus de inmunodeficiencia humana [VIH]).', 6),
  (11, 1, 1, '2017-04-05 00:00:00', 'Gérmenes y niños: Terminología\r\nflora normal	Bacterias que viven en un niño\r\npatógeno	Un germen que puede ocasionar una enfermedad\r\ncolonización	Presencia de un germen en usted sin enfermedades.\r\ninfección	Un germen que ocasiona una enfermedad. Su cuerpo reaccionará al crear anticuerpos', 6),
  (12, 1, 1, '2017-04-05 00:00:00', 'Muchas infecciones aparecen y desaparecen sin dañar al niño. Otras ocasionan dolor y, algunas veces, la muerte. Algunas infecciones se curan, pero dejan a un niño con daños en un órgano.', 3),
  (13, 6, 3, '2017-04-13 00:00:00', 'No todas las infecciones son contagiosas (se pueden propagar de persona a persona). Las infecciones de oído y vejiga no se propagan de niño a niño, mientras que la diarrea y los resfriados se propagan fácilmente.', 3),
  (14, 6, 3, '2017-04-13 00:00:00', 'El período de incubación es el tiempo que transcurre después de que se infecta un niño hasta que se vuelve una enfermedad. Algunas veces la incubación es más corta (por ejemplo, un día o más para la gripe), mientras que otras veces es un poco más (por ejemplo, 2 semanas para la varicela y muchos años para el virus de inmunodeficiencia humana [VIH]). En algunos casos, una persona es contagiosa durante el período de incubación, mientras que otras personas no son contagiosas hasta que empieza la enfermedad.', 11),
  (15, 6, 3, '2016-10-12 00:00:00', 'Muchas infecciones aparecen y desaparecen sin dañar al niño. Otras ocasionan dolor y, algunas veces, la muerte. Algunas infecciones se curan, pero dejan a un niño con daños en un órgano.', 11);
/*!40000 ALTER TABLE `HistorialMedico` ENABLE KEYS */;

-- Volcando datos para la tabla clifisBD.Medico: ~7 rows (aproximadamente)
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

-- Volcando datos para la tabla clifisBD.Medico_Especialidad: ~10 rows (aproximadamente)
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

-- Volcando datos para la tabla clifisBD.Paciente: ~11 rows (aproximadamente)
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
