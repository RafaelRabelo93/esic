package br.gov.se.lai.DAO;

import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;

import br.gov.se.lai.entity.Responsavel;
import br.gov.se.lai.utils.HibernateUtil;

public class  ResponsavelDAO {
	
	private static EntityManager em = HibernateUtil.geteEntityManagerFactory().createEntityManager();
	
    public static void saveOrUpdate(Responsavel responsavel) {     	        
        try {
        	if(!em.getTransaction().isActive()) em.getTransaction().begin();
        	if(responsavel.getIdResponsavel() ==  null) {
        		em.persist(responsavel);
    		}else {
    			em.merge(responsavel);
    		}
            em.getTransaction().commit();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Responsavel  salvo(a) com sucesso!"));
        } catch (Exception e) {
        	FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Erro ao cadastrar responsavel "));
        	System.out.println(e);
            em.getTransaction().rollback();
        }
    }
    public static void delete(Responsavel responsavel) {        
        try {            
        	Responsavel usu = em.find(Responsavel.class, responsavel.getIdResponsavel());
            if (usu == null) {
            	FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Responsavel  n�o cadastrado."));
			}else {
				em.getTransaction().begin();
				em.remove(usu);
	            em.getTransaction().commit();
	            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Atividade excluída com sucesso!"));
			}            
        } catch (Exception e) {
            em.getTransaction().rollback();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Erro ao excluir responsavel "));
        }
    }    
    
    public static Responsavel findResponsavel(String email){
    	return em.find(Responsavel.class, email) ;
    }

	public static List<Responsavel> list() {
	
        return null;
    }  
}
