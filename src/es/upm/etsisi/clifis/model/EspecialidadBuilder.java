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

public class EspecialidadBuilder{

    private String nombre;

    private int id;

    public EspecialidadBuilder setNombre(String nombre) {
        this.nombre = nombre;
        return this;
    }

    public EspecialidadBuilder setId(int id) {
        this.id = id;
        return this;
    }

    public Especialidad build (){
        return new Especialidad (nombre,id);
    }
}