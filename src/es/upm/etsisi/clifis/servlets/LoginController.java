package es.upm.etsisi.clifis.servlets;

import es.upm.etsisi.clifis.gestores.GestorException;
import es.upm.etsisi.clifis.gestores.GestorFechas;
import es.upm.etsisi.clifis.gestores.GestorUsuarios;
import es.upm.etsisi.clifis.model.Gestor;
import es.upm.etsisi.clifis.model.Medico;
import es.upm.etsisi.clifis.model.Usuario;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = {"/process_login", "/process_logout"})
public class LoginController extends HttpServlet {

    Logger LOG = LoggerFactory.getLogger("es.upm.etsisi.clifis.servlets.GestorLogin");
    private GestorUsuarios gestorUsuarios = null;

    @Override
    public void init() throws ServletException {
        this.gestorUsuarios = (GestorUsuarios)this.getServletContext().getAttribute("gestor_usuarios");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String URI = req.getRequestURI();

        switch (URI) {
            case "/process_login":
                this.process_login(req);
                break;
            case "/process_logout":
                this.process_logout(req);
                break;
        }

        RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/index.jsp");
        dispatcher.forward(req, resp);
    }

    /**
     * Si existe un usuario en la sesión, lo borra.
     *
     * @param req de donde coge la session y el usuario en caso de existir.
     */
    private void process_logout(HttpServletRequest req) {
        if (req.getSession().getAttribute("usuario") == null) {
            req.setAttribute("gestorException", new GestorException("Ningún usuario había había iniciado sesión."));
        } else {
            req.getSession().removeAttribute("usuario");
        }
    }

    /**
     * Recoge el usuario y el password metido en la página de login y comprueba si son correctos. Si lo son,
     * pone en la sesión el objeto {@link Gestor} o {@link Medico} según corresponda para que permanezca
     * identificado durante la sessión. Si no lo son, no pone nada.
     *
     * @param req De donde recoger los datos que se necesitan: id de usuario y password.
     */
    private void process_login(HttpServletRequest req) {
        // Se coge el id del médico o del gestor (que es el 0). También, se obtiene el sha256 del pwd.
        int idUsuario = GestorFechas.getIntFromParameter("usuario", req);
        String password = DigestUtils.sha256Hex(req.getParameter("password"));

        try {
            Usuario usuario =  this.gestorUsuarios.checkPassword(idUsuario, password);
            if (usuario != null) {
                req.getSession().setAttribute("usuario", usuario);
            } else {
                req.setAttribute("gestorException", new GestorException("El usuario no ha podidio iniciar sesión. Vuelva a intentarlo."));
            }

        } catch (GestorException e) {
            LOG.debug("No venía ningún médico válido del login.", e);
            req.setAttribute("gestorException", e);
        }
    }
}
