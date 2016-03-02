package servlet;

import gestion.GestorKeep;
import gestion.GestorUsuario;
import hibernate.Keep;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONException;
import org.json.JSONObject;

@WebServlet(name = "Controlador", urlPatterns = {"/go"})
public class Controlador extends HttpServlet {

    enum Camino {
        forward, redirect, print;
    }

    class Destino {

        public Camino camino;
        public String url;
        public String texto;

        public Destino() {
        }

        public Destino(Camino camino, String url, String texto) {
            this.camino = camino;
            this.url = url;
            this.texto = texto;
        }
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, JSONException {
        String tabla = request.getParameter("tabla");
        String op = request.getParameter("op");
        String accion = request.getParameter("accion");
        String origen = request.getParameter("origen");
        Destino destino = handle(request, response, tabla, op, accion, origen);
        if (destino == null) {
            destino = new Destino(Camino.forward, "/WEB-INF/index.jsp", "");
        }
        if (destino.camino == Camino.forward) {
            request.getServletContext().
                    getRequestDispatcher(destino.url).forward(request, response);
        } else if (destino.camino == Camino.redirect) {
            response.sendRedirect(destino.url);
        } else {
            response.setContentType("text/html;charset=UTF-8");
            try (PrintWriter out = response.getWriter()) {
                out.println(destino.texto);
            }
        }
    }

    //"filtro" de acciones
    private Destino handle(HttpServletRequest request, HttpServletResponse response,
            String tabla, String op, String accion, String origen) throws JSONException {
        if (origen == null) {
            origen = "";
        }
        if (tabla == null || op == null || accion == null) {
            tabla = "usuario";
            op = "read";
            accion = "view";
        }
        if (tabla.equals("usuario")) {
            return handleUsuario(request, response, op, accion, origen);
        } else if (tabla.equals("keep")) {
            return handleKeep(request, response, op, accion, origen);
        }
        return null;
    }

    //Parael login del usuario. En caso de hacerlo desde android creamos la url con el login y contraseña 
    //de ese usuario. En caso de hacerse en web, si el usuario es correcto avanzamos a ver las notas, si el usuario
    //es erroneo o deja algun campo vacio lo enviamos al una pagina donde aparece un mensaje de error
    private Destino handleUsuario(HttpServletRequest request, HttpServletResponse response,
            String op, String accion, String origen) throws JSONException {
        if (origen.equals("web")) {
            if (request.getParameter("login").isEmpty() || request.getParameter("pass").isEmpty()) {
                return new Destino(Camino.print, "index.html", "<h2>Usuario o contraseña vacios!</h2>");
            } else {
                JSONObject obj = GestorUsuario.getLogin(request.getParameter("login"),
                        request.getParameter("pass"));
                if (obj.getBoolean("r")) {
                    List<Keep> keeps = GestorKeep.listKeeps(request.getParameter("login"));
                    request.setAttribute("listado", keeps);
                    request.setAttribute("login", GestorUsuario.getUserbyName(request.getParameter("login")));
                    return new Destino(Camino.forward, "/WEB-INF/view.jsp", keeps.toString());
                } else {
                    return new Destino(Camino.print, "index.html", "<h2>Usuario o contraseña erroneos</h2>");
                }
            }
        } else if (origen.equals("android")) {
            JSONObject obj = GestorUsuario.getLogin(request.getParameter("login"),
                    request.getParameter("pass"));
            return new Destino(Camino.print, "", obj.toString());
        }
        return null;
    }

    //Metodo para manejar las notas. Dependiendo de la accion que le pasemos realizara una cosa u otra
    private Destino handleKeep(HttpServletRequest request, HttpServletResponse response,
            String op, String accion, String origen) throws JSONException {
        //Para crear una nota desde android obtenemos la id de la nota que nos pasa la aplicacion y su contneido y ponemos a 1
        //el estado, que significa que esta sincronizada. Desde web simplemente recogemos el contenido, y ponemos la idAndroid a 0
        //para después desde la aplicacion poder conocer cual nota hay que descargar y cual no.
        if (op.equals("create")) {
            if (origen.equals("web")) {
                Keep k = new Keep(null, 0, request.getParameter("contenido"), null, 0);
                JSONObject obj = GestorKeep.addKeep(k, request.getParameter("login"));
                List<Keep> keeps = GestorKeep.listKeeps(request.getParameter("login"));
                request.setAttribute("listado", keeps);
                return new Destino(Camino.forward, "/WEB-INF/view.jsp", keeps.toString());
            } else if (origen.equals("android")) {
                Keep k = new Keep(null, request.getParameter("idAndroid"),
                        request.getParameter("contenido"), null, 1);
                JSONObject obj = GestorKeep.addKeep(k,
                        request.getParameter("login"));
                return new Destino(Camino.print, "", obj.toString());
            }

            // Para borrar desde android lo hacemos por idAndroid. Desde web usamos la id
        } else if (op.equals("delete")) {
            if (origen.equals("web")) {
                GestorKeep.removeKeepWeb(Integer.parseInt(request.getParameter("id")));
                List<Keep> keeps = GestorKeep.listKeeps(request.getParameter("login"));
                request.setAttribute("listado", keeps);
                return new Destino(Camino.forward, "/WEB-INF/view.jsp", keeps.toString());
            } else if (origen.equals("android")) {
                Keep k = new Keep(null, request.getParameter("idAndroid"),
                        request.getParameter("contenido"), null, 1);
                JSONObject obj = GestorKeep.removeKeep(k, request.getParameter("login"));
                return new Destino(Camino.print, "", obj.toString());
            }

            //Para modificar desde la web utilizamos la id y el usuario
        } else if (op.equals("update")) {
            GestorKeep.updateKeep(Integer.parseInt(request.getParameter("id")), request.getParameter("contenido"));
            List<Keep> keeps = GestorKeep.listKeeps(request.getParameter("login"));
            request.setAttribute("listado", keeps);
            return new Destino(Camino.forward, "/WEB-INF/view.jsp", keeps.toString());
            
            //Url para que android pueda leer las notas guardadas en el json
        } else if (op.equals("read")) {
            JSONObject obj = GestorKeep.getKeeps(request.getParameter("login"));
            return new Destino(Camino.print, "", obj.toString());
        }

        return null;
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (JSONException ex) {
            Logger.getLogger(Controlador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (JSONException ex) {
            Logger.getLogger(Controlador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
