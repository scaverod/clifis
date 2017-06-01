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

import java.sql.Time;

public class ConsultaBuilder {
    private int id;
    private int numSala;
    private Medico medico;
    private Especialidad especialidad;
    private int duracion;
    private Time horaInicio;
    private Time horaFin;
    private String diaSemana;

    public Consulta build() {
       return new Consulta(id,numSala,medico,especialidad,duracion,horaInicio,horaFin, diaSemana);
    }

    public ConsultaBuilder setId(int id) {
        this.id = id;
        return this;
    }

    public ConsultaBuilder setNumSala(int numSala) {
        this.numSala = numSala;
        return this;
    }

    public ConsultaBuilder setMedico(Medico medico) {
        this.medico = medico;
        return this;
    }

    public ConsultaBuilder setEspecialidad(Especialidad especialidad) {
        this.especialidad = especialidad;
        return this;
    }

    public ConsultaBuilder setDuracion(int duracion) {
        this.duracion = duracion;
        return this;
    }

    public ConsultaBuilder setHoraInicio(Time horaInicio) {
        this.horaInicio = horaInicio;
        return this;
    }

    public ConsultaBuilder setHoraFin(Time horaFin) {
        this.horaFin = horaFin;
        return this;
    }

    public ConsultaBuilder setDiaSemana(String diaSemana) {
        this.diaSemana = diaSemana;
        return this;
    }
}
