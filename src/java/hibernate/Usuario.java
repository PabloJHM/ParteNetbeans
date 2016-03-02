package hibernate;


import java.util.HashSet;
import java.util.Set;

public class Usuario  implements java.io.Serializable {


     private String login;
     private String pass;
     private Set keeps = new HashSet(0);

    public Usuario() {
    }

	
    public Usuario(String login, String pass) {
        this.login = login;
        this.pass = pass;
    }
    public Usuario(String login, String pass, Set keeps) {
       this.login = login;
       this.pass = pass;
       this.keeps = keeps;
    }
   
    public String getLogin() {
        return this.login;
    }
    
    public void setLogin(String login) {
        this.login = login;
    }
    public String getPass() {
        return this.pass;
    }
    
    public void setPass(String pass) {
        this.pass = pass;
    }
    public Set getKeeps() {
        return this.keeps;
    }
    
    public void setKeeps(Set keeps) {
        this.keeps = keeps;
    }




}


