package br.gov.se.lai.utils;

import java.util.List;
import javax.persistence.EntityManager;

public class Consultas {
	
	/**
	 * Classe que realiza a conexão com o banco de dados para ir 
	 * realizar as consulta utilizadas nas classes DAO.
	 */
	
    public static List<?> buscaPersonalizada(String query, EntityManager em) {        
        List<?> list = null;
		try {        	
			//em.clear();
        	list  = em.createQuery(query).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
        
    }
    
    public static int contadorSQL(String query, EntityManager em) {        
    	Long in = null;
    	try {        	
    		//em.clear();
    		in = (Long) em.createQuery(query).getSingleResult();
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return in.intValue();
    	
    }
    
    
    public static List<?> buscaSQL(String query, EntityManager em) {        
        List<?> list = null;        
		try {      
			em.clear();
        	list  = em.createNativeQuery(query).getResultList();        	
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
        
    }
    

    
}
