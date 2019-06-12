package br.gov.se.lai.utils;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class HibernateUtil {

	//static EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("esic");	  
    private static final EntityManagerFactory entityManagerFactory;   
    static {
                try {
                     entityManagerFactory = Persistence.createEntityManagerFactory("esic");
//                     System.out.println("Entity Manager Test.............."+ entityManagerFactory);
                } catch (Throwable ex) {

//                    System.err.println("Initial SessionFactory creation failed." + ex);
                    throw new ExceptionInInitializerError(ex);

                  }
    }

	public static EntityManagerFactory geteEntityManagerFactory() {
		return entityManagerFactory;
	}

	public static void SalvarNaSessao(String key, Object obj) {
		FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(key, obj);
	}
	
	public static Object RecuperarDaSessao(String key) {
		return FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(key);
	}

}