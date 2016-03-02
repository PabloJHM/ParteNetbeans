
package gestion;

import hibernate.Keep;
import hibernate.Usuario;
import java.math.BigInteger;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GestorKeep {
    
    //Añadir una nota a un usuario en concreto
    public static JSONObject addKeep(Keep k, String usuario) throws JSONException{
        Configuration configuration = new Configuration().configure();
        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().
                applySettings(configuration.getProperties());
        SessionFactory factory = configuration.buildSessionFactory(builder.build());
        Session sesion = factory.openSession();
        sesion.beginTransaction();
        Usuario u = (Usuario)sesion.get(Usuario.class, usuario);
        k.setUsuario(u);
        sesion.save(k);
        Long id = ((BigInteger) sesion.createSQLQuery
            ("select last_insert_id()").uniqueResult())
            .longValue();       
        sesion.getTransaction().commit();
        sesion.flush();
        sesion.close();
        JSONObject obj = new JSONObject();
        obj.put("r", id);
        return obj;
    }
      
    //Obtener un JSONobject que contiene la lista de notas de un usuario
    public static JSONObject getKeeps(String usuario) throws JSONException{
        Configuration configuration = new Configuration().configure();
        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().
                applySettings(configuration.getProperties());
        SessionFactory factory = configuration.buildSessionFactory(builder.build());
        Session sesion = factory.openSession();
        sesion.beginTransaction();
        String hql = "from Keep where login = :login";
        Query q = sesion.createQuery(hql);
        q.setString("login", usuario);
        List<Keep> keeps = q.list();
        sesion.getTransaction().commit();
        sesion.flush();
        sesion.close();
        JSONArray array= new JSONArray();
        for(Keep k: keeps){
            JSONObject obj = new JSONObject();
            obj.put("ida", k.getIdAndroid());
            obj.put("cont", k.getContenido());
            obj.put("est", k.getEstado());
            array.put(obj);
        }
        JSONObject obj2 = new JSONObject();
        obj2.put("r", array);
        return obj2;
    }
    
    //Eliminar una nota de un usuario
    public static JSONObject removeKeep(Keep k, String usuario){
        Configuration configuration = new Configuration().configure();
        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().
                applySettings(configuration.getProperties());
        SessionFactory factory = configuration.buildSessionFactory(builder.build());
        Session sesion = factory.openSession();
        sesion.beginTransaction();
        Usuario u = (Usuario)sesion.get(Usuario.class, usuario);
        k.setUsuario(u);
        String hql = "delete from Keep where login = :login and idAndroid= :idan";
        Query q = sesion.createQuery(hql);
        q.setString("login", usuario);
        q.setInteger("idan", k.getIdAndroid());
        q.executeUpdate();
        sesion.getTransaction().commit();
        sesion.flush();
        sesion.close();
        return new JSONObject();
    }
    
    //Eliminar una nota a traves de su id android, para cuando borremos una nota desde la aplicacion
     public static JSONObject removeKeepWeb(int id){
        Configuration configuration = new Configuration().configure();
        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().
                applySettings(configuration.getProperties());
        SessionFactory factory = configuration.buildSessionFactory(builder.build());
        Session sesion = factory.openSession();
        sesion.beginTransaction();
        String hql = "delete from Keep where id= :idan";
        Query q = sesion.createQuery(hql);
        q.setInteger("idan", id);
        q.executeUpdate();
        sesion.getTransaction().commit();
        sesion.flush();
        sesion.close();
        return new JSONObject();
    }
   
    //Devuelve la lista de notas de un usuario
    public static List<Keep> listKeeps(String usuario){
       Configuration configuration = new Configuration().configure();
        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().
                applySettings(configuration.getProperties());
        SessionFactory factory = configuration.buildSessionFactory(builder.build());
        Session sesion = factory.openSession();
        sesion.beginTransaction();
        String hql = "from Keep where login = :login";
        Query q = sesion.createQuery(hql);
        q.setString("login", usuario);
        List<Keep> keeps = q.list();
        sesion.getTransaction().commit();
        sesion.flush();
        sesion.close();
        return keeps;
    }
    
    //Modificar una nota
    public static void updateKeep(int id, String cont){
         Configuration configuration = new Configuration().configure();
        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().
                applySettings(configuration.getProperties());
        SessionFactory factory = configuration.buildSessionFactory(builder.build());
        Session sesion = factory.openSession();
        sesion.beginTransaction();
        Keep k= (Keep) sesion.get(Keep.class, id);
        k.setContenido(cont);
        sesion.getTransaction().commit();
        sesion.flush();
        sesion.close();
    }
}
