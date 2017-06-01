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

package es.upm.etsisi.clifis.model;

import java.sql.Timestamp;

public class HistorialMedicoBuilder {

    private int id;
    private Paciente paciente;
    private Medico medico;
    private Timestamp fecha;
    private Especialidad especialidad;
    private String comentario;

    public HistorialMedicoBuilder setId(int id) {
        this.id = id;
        return this;
    }

    public HistorialMedicoBuilder setPaciente(Paciente paciente) {
        this.paciente = paciente;
        return this;
    }

    public HistorialMedicoBuilder setMedico(Medico medico) {
        this.medico = medico;
        return this;
    }

    public HistorialMedicoBuilder setFecha(Timestamp fecha) {
        this.fecha = fecha;
        return this;
    }

    public HistorialMedicoBuilder setEspecialidad(Especialidad especialidad) {
        this.especialidad = especialidad;
        return this;
    }

    public HistorialMedicoBuilder setComentario(String comentario) {
        this.comentario = comentario;
        return this;
    }

    public HistorialMedico build(){
        return new HistorialMedico(id, paciente, medico, fecha, especialidad, comentario);
    }
}
