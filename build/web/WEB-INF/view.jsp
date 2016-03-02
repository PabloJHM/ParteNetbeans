<%@page import="hibernate.Usuario"%>
<%@page import="hibernate.Keep"%>
<%@page import="java.util.List"%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    List<Keep> lista = (List<Keep>)request.getAttribute("listado");
    String login=request.getParameter("login"),pass=request.getParameter("pass");
%>  
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h1>Lista de notas</h1>
        <a href="add.jsp?login=<%= login %>">Nota nueva</a>
        <br><br>
        <table border="1">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Usuario</th>
                    <th>Contenido</th>
                    <th>Id Android</th>
                    <th>Estado</th>
                    <th>Editar</th>
                    <th>Borrar</th>
                </tr>
            </thead>
            <tbody>
                <%
                for(Keep p: lista){
                     String contenido= p.getContenido().replace("|", " ");
                    %>
                    <tr>
                        <td><%= p.getId()%></td>
                        <td><%= p.getUserName()%></td>
                        <td><%= contenido%></td>
                        <td><%= p.getIdAndroid()%></td>
                        <td><%= p.getEstado()%></td>
                        <td><a href="edit.jsp?id=<%= p.getId() %>&login=<%= login %>&contenido=<%= p.getContenido()%>">Editar</a></td>
                        <td><a href="go?tabla=keep&origen=web&op=delete&accion=&id=<%= p.getId() %>&login=<%= login %>" class="borrar">Borrar</a></td>
                    </tr>
                    <%
                }
                %>               
            </tbody>
        </table>
        <br>
        <a href="go?login=<%= login %>&pass=<%= login %>&tabla=usuario&accion=&op=login&origen=web">Actualizar</a>
    </body>
</html>
