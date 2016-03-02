<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h1>Editar nota</h1>
        <form action="go">
            <p>Contenido:</p>
            <% 
                String contenido = request.getParameter("contenido").replace("|", " ") ;
               %>
            <textarea name="contenido" rows="4" cols="20"><%= contenido%></textarea>
            <br>
            <input type="submit" value="Submit" name="Submit" />
            
            <input type="hidden" name="accion" value="" />
            <input type="hidden" name="op" value="update" />
            <input type="hidden" name="origen" value="web" />
            <input type="hidden" name="tabla" value="keep" />
            <input type="hidden" name="id" value="<%= request.getParameter("id") %>" />
            <input type="hidden" name="login" value="<%= request.getParameter("login") %>" />
        </form>
    </body>
</html>
