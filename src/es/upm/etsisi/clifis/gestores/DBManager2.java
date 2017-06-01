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

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Gestiona las conexiones a la base de datos. Utiliza el <i>pool</i> de Tomcat
 * para evitar los efectos laterales de configurar las conexiones manualmente con JDBC.
 */
class DBManager2 {

    private static DataSource ds = null;

    static {
        // FILL these fields with database proper name, user and password before runnig clifis.
        String clifisBD = "";
        String username = "";
        String password = "";

        PoolProperties p = new PoolProperties();
        p.setUrl("jdbc:mysql://localhost:3306/" + clifisBD + "?useUnicode=true&characterEncoding=utf-8");
        p.setDriverClassName("com.mysql.jdbc.Driver");
        p.setUsername(username);
        p.setPassword(password);
        p.setJmxEnabled(true);
        p.setTestWhileIdle(false);
        p.setTestOnBorrow(true);
        p.setValidationQuery("SELECT 1");
        p.setTestOnReturn(false);
        p.setValidationInterval(30000);
        p.setTimeBetweenEvictionRunsMillis(30000);
        p.setMaxActive(10);
        p.setInitialSize(10);
        p.setMaxWait(10000);
        p.setRemoveAbandonedTimeout(60);
        p.setMinEvictableIdleTimeMillis(30000);
        p.setMaxIdle(10);
        p.setMinIdle(5);
        p.setLogAbandoned(true);
        p.setRemoveAbandoned(true);
        p.setJdbcInterceptors(
                "org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"
                        + "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer;"
                        + "org.apache.tomcat.jdbc.pool.interceptor.ResetAbandonedTimer");

        ds = new DataSource();
        ds.setPoolProperties(p);
    }

    /**
     * Devuelve una de las conexiones activas con la basede datos dentro del pool.
     *
     * @return Una conexión a la base de datos.
     */
    static Connection getConn() throws DBManager2Exception {

        Connection con = null;
        try {
            con = ds.getConnection();
        } catch (SQLException e) {
            DBManager2Exception ex = new DBManager2Exception("No se ha podido realizar la conexión con la base de datos.");
            ex.addSuppressed(e);
        }
        return con;
    }

}
