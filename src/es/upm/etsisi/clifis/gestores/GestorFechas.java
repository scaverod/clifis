/*
        Clifis v1.0b (c) 2017 Escuela Técnica Superior de Ingeniería de Sistemas Informáticos (UPM)

        This file is part of Clifis.

        Clifis is free software: you can redistribute it and/or modify
        it under the terms of the GNU General Public License as published by
        the Free Software Foundation, either version 3 of the License, or
        (at your option) any later version.

        Clifis is distributed in the hope that it will be useful,
        but WITHOUT ANY WARRANTY; without even the implied warranty of
        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
        GNU General Public License for more details.

        You should have received a copy of the GNU General Public License
        along with Clifis.  If not, see <http://www.gnu.org/licenses/>.
*/

package es.upm.etsisi.clifis.gestores;

import es.upm.etsisi.clifis.model.Consulta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class GestorFechas {

    private static Logger LOG = LoggerFactory.getLogger("es.upm.etsisi.clifis.gestores.GestorFechas");

    /**
     * Dado un día de la semana (Lunes, Martes, Miércoles, Jueves, Viernes, Sábado, Domingo),
     * devuelve un entero que coincide con una constante mágica que representa ese día
     * en la clase <i>java.util.Calcendar</i>.
     *
     * @param diaSemana String con uno de estos valores: Lunes, Martes, Miércoles, Jueves, Viernes, Sábado, Domingo
     * @return El entero correspondiente <i>Caledar.DAY_OF_WEEK</i> o 0 si lo que serecibe no es un
     * <i>String</i> válido, o es null.
     */
    public static int getCalendarInt(String diaSemana) {
        int resultado = 0;

        if(diaSemana !=null) {
            switch (diaSemana) {
                case "Lunes":
                    resultado = Calendar.MONDAY;
                    break;
                case "Martes":
                    resultado = Calendar.TUESDAY;
                    break;
                case "Miércoles":
                    resultado = Calendar.WEDNESDAY;
                    break;
                case "Jueves":
                    resultado = Calendar.THURSDAY;
                    break;
                case "Viernes":
                    resultado = Calendar.FRIDAY;
                    break;
                case "Sábado":
                    resultado = Calendar.SATURDAY;
                    break;
                case "Domingo":
                    resultado = Calendar.SUNDAY;
                    break;
            }
        }

        return resultado;
    }

    /**
     * Dada una consulta devuelve un <i>java.util.Calendar</i> con el inicio (fecha y hora) de la siguiente consulta
     * con respecto a la fecha actual del sistema.
     *
     * @param consulta De la que se quiere la información
     * @return Fecha y hora del inicio de la siguiente consulta (fecha y hora). Null si consulta es null.
     */
    public static Calendar getSiguienteInicioDeConsulta(Consulta consulta) {
        Calendar resultado = null;
        if (consulta != null) {

            resultado = Calendar.getInstance();

            while (resultado.get(Calendar.DAY_OF_WEEK) != getCalendarInt(consulta.getDiaSemana())) {
                resultado.add(Calendar.DATE, 1);
            }
            Calendar horaInicioConsulta = Calendar.getInstance();
            horaInicioConsulta.setTimeInMillis(consulta.getHoraInicio().getTime());

            resultado.set(Calendar.HOUR_OF_DAY, horaInicioConsulta.get(Calendar.HOUR_OF_DAY));
            resultado.set(Calendar.MINUTE, horaInicioConsulta.get(Calendar.MINUTE));
        }

        return resultado;
    }

    /**
     * Dada una consulta devuelve un <i>java.util.Calendar</i> con el inicio (fecha y hora) de la siguiente consulta
     * con respecto a la fecha actual del sistema.
     *
     * @param consulta De la que se quiere la información
     * @return Fecha y hora del inicio de la siguiente consulta (fecha y hora).
     */
    public static Calendar getSiguienteFinDeConsulta(Consulta consulta) {
        Calendar resultado = null;
        if (consulta != null) {

            resultado = Calendar.getInstance();

            while (resultado.get(Calendar.DAY_OF_WEEK) != getCalendarInt(consulta.getDiaSemana())) {
                resultado.add(Calendar.DATE, 1);
            }

            Calendar horaFinConsulta = Calendar.getInstance();
            horaFinConsulta.setTimeInMillis(consulta.getHoraFin().getTime());

            resultado.set(Calendar.HOUR_OF_DAY, horaFinConsulta.get(Calendar.HOUR_OF_DAY));
            resultado.set(Calendar.MINUTE, horaFinConsulta.get(Calendar.MINUTE));
        }

        return resultado;
    }

    /**
     * Recupera el valor de un componente de un formulario y lo pasa a tipo <i>int</i> para devolverlo. Si la cadena
     * obtenida del formulario es <i>null</i> o no puede ser convertida (fallo en el formato) se devuelve el valor 0
     * pero no se eleva ninguna excepción.
     *
     * @param parameter Nombre del parámetro (identificador usado en el atributo <i>name</i> del <i>html</i>).
     * @param req Objeto que tiene los valores del formulario.
     * @return el entero convertido o 0 si ha habido algún problema con la conversión.
     */
    public static int getIntFromParameter(String parameter, HttpServletRequest req) {
        int value = 0;
        try {
            String numeroS = req.getParameter(parameter);
            if (numeroS == null || numeroS.isEmpty())
                LOG.debug("El parámetro '{}' venía vacío (nada que convertir)", parameter);
            else
                value = Integer.parseInt(numeroS);
        } catch (NumberFormatException e) {
            LOG.debug("Ha fallado la conversión del parámetro '{}' a entero. Cadena recibida: '{}'", parameter, req.getParameter(parameter), e);
        }

        return value;
    }

    /**
     * Devuelve un Timestamp a partir de un <i>String</i> que debe tener el formato <i>dd/MM/yyyy HH:mm</i>.
     *
     * @param fecha La fecha que es quire convertir a TimeStamp.
     * @return La fecha convertida.
     * @throws GestorException Si hay algún problema con el formato del <i>String</i>.
     */
    public static Timestamp getTimestampFromString(String fecha) throws GestorException {
        Timestamp resultado = null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        try {
            resultado = new Timestamp(sdf.parse(fecha).getTime());
        } catch (ParseException e) {
            LOG.debug("La fecha recibida no se corresponde con el patrón 'dd/mm/yyyy': {}", fecha, e);
            throw new GestorException("La fecha recibida no se corresponde con el patrón 'dd/mm/yyyy' (" + fecha + ").");
        }

        return resultado;
    }

    /**
     * Devuelve un Timestamp a partir de un <i>String</i> que debe tener el formato <i>dd/MM/yyyy</i>.
     *
     * @param fecha La fecha que es quire convertir a TimeStamp.
     * @return La fecha convertida.
     * @throws GestorException Si hay algún problema con el formato del <i>String</i>.
     */
    public static Timestamp getTimestampDateFromString(String fecha) throws GestorException {
        Timestamp resultado = null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        try {
            resultado = new Timestamp(sdf.parse(fecha).getTime());
        } catch (ParseException e) {
            LOG.debug("La fecha recibida no se corresponde con el patrón 'dd/mm/yyyy': {}", fecha, e);
            throw new GestorException("La fecha recibida no se corresponde con el patrón 'dd/mm/yyyy' (" + fecha + ").");
        }

        return resultado;
    }

    /**
     * De vuelve la fecha de un <i>Calendar</i> como <i>String</i> con el formato: dd/MM/yyyy.
     *
     * @param cal Un objeto Calendar con la fecha que se quiere obtener
     * @return String con formato dd/MM/yyyy
     */
    public static String getStringFechaFromCalendar (Calendar cal) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(cal.getTime());
    }

    /**
     * De vuelve la hora de un <i>Calendar</i> como <i>String</i> con el formato: HH:mm.
     *
     * @param cal Un objeto Calendar con la fecha que se quiere obtener
     * @return String con formato HH:mm
     */
    public static String getStringHoraFromCalendar (Calendar cal) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(cal.getTime());
    }


}
