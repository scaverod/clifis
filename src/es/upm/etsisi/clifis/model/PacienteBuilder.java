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

public class PacienteBuilder {
    private int id;
    private String nombre;
    private String apellidos;
    private String dni;
    private String aseguradora;

    public PacienteBuilder setId(int id) {
        this.id = id;
        return this;
    }

    public PacienteBuilder setNombre(String nombre) {
        this.nombre = nombre;
        return this;
    }

    public PacienteBuilder setApellidos(String apellidos) {
        this.apellidos = apellidos;
        return this;
    }

    public PacienteBuilder setDni(String dni) {
        this.dni = dni;
        return this;
    }

    public PacienteBuilder setAseguradora(String aseguradora) {
        this.aseguradora = aseguradora;
        return this;
    }

    public Paciente build(){
        return new Paciente(id, nombre, apellidos, dni, aseguradora);
    }


}
