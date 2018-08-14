package br.gov.se.lai.DAO;

import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import br.gov.se.lai.entity.Cidadao;
import br.gov.se.lai.entity.Responsavel;
import br.gov.se.lai.entity.Usuario;
import br.gov.se.lai.utils.Consultas;
import br.gov.se.lai.utils.HibernateUtil;

public class CidadaoDAO {
	private static EntityManager em = HibernateUtil.geteEntityManagerFactory().createEntityManager();
	
    public static boolean saveOrUpdate(Cidadao cidadao) {     	        
        try {
        	em.getTransaction().begin();
        	if(cidadao.getIdCidadao() == null) {
        		em.persist(cidadao);
    		}else {
    			em.merge(cidadao);
    		}
            em.getTransaction().commit();  
//            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Cidadao "+ cidadao.getUsuario().getNome()+" salvo(a) com sucesso!"));
            return true;
        } catch (Exception e) {   
        	FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Erro ao cadastrar cidadao "));
        	System.out.println(e);
            em.getTransaction().rollback();
            return false;
        }
    }
    public static void delete(Cidadao cidadao) {        
        try {             
        	Cidadao usu = em.find(Cidadao.class, cidadao.getIdCidadao());
            if (usu == null) {
            	FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Cidadao não cadastrado."));
			}else {
				em.getTransaction().begin();
				em.remove(usu);
	            em.getTransaction().commit();	            
			}            
        } catch (Exception e) {
            em.getTransaction().rollback();            
        }
    }    
    
    public static Cidadao findCidadao(int id){
    	return em.find(Cidadao.class, id) ;
    }
    
    @SuppressWarnings("unchecked")
	public static Cidadao findCidadaoEmail(String email) {
       	Query query = em.createQuery("FROM Cidadao as cid WHERE cid.email= :emailParam");
    	query.setParameter("emailParam", email);  

    	List<Cidadao> results = query.getResultList();
  	 	return results.get(0);
    }
    
    @SuppressWarnings("unchecked")
    public static Cidadao findIdCidadao(int id) {
    	Query query = em.createQuery("FROM Cidadao as cid WHERE cid.idCidadao= :idParam");
    	query.setParameter("idParam", id);  
    	
    	List<Cidadao> results = query.getResultList();
    	return results.get(0);
    }
   
  	 	
    @SuppressWarnings("unchecked")
    public static List<Cidadao> findCPFs() {
    	Query query = em.createQuery("SELECT cid.cpf FROM Cidadao as cid ");
    	List<Cidadao> results = query.getResultList();
  	 	return results;
     }
    
    
    @SuppressWarnings("unchecked")
    public static List<Cidadao> findRGs() {
    	Query query = em.createQuery("SELECT cid.rg FROM Cidadao as cid ");
    	List<Cidadao> results = query.getResultList();
    	return results;
    }


    @SuppressWarnings("unchecked")
    public static Cidadao findCidadaoUsuario(int idUsuario) {
    	Query query = em.createQuery("FROM Cidadao as cid WHERE cid.usuario.idUsuario= :idUsuarioParam");
    	query.setParameter("idUsuarioParam", idUsuario);  
    	
    	List<Cidadao> results = query.getResultList();
    	return results.get(0);
    	
    }

	public static List<Cidadao> list() {
	
        return null;
    }  
}
