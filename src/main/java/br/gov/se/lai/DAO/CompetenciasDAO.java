package br.gov.se.lai.DAO;

import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;

import br.gov.se.lai.entity.Competencias;
import br.gov.se.lai.utils.HibernateUtil;

public class  CompetenciasDAO {
	
	private static EntityManager em = HibernateUtil.geteEntityManagerFactory().createEntityManager();
	
    public static void saveOrUpdate(Competencias competencias) {     	        
        try {
        	if(!em.getTransaction().isActive()) em.getTransaction().begin();
        	if(competencias.getIdCompetencias() ==  null) {
        		em.persist(competencias);
    		}else {
    			em.merge(competencias);
    		}
            em.getTransaction().commit();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Competencias  salvo(a) com sucesso!"));
        } catch (Exception e) {
        	FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Erro ao cadastrar competencia "));
        	System.out.println(e);
            em.getTransaction().rollback();
        }
    }
    public static void delete(Competencias competencias) {        
        try {            
        	Competencias comp = em.find(Competencias.class, competencias.getIdCompetencias());
            if (comp == null) {
            	FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Competencias  n„o cadastrado."));
			}else {
				em.getTransaction().begin();
				em.remove(comp);
	            em.getTransaction().commit();
	            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Competencias exclu√≠da com sucesso!"));
			}            
        } catch (Exception e) {
            em.getTransaction().rollback();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Erro ao excluir competencias "));
        }
    }    
    
    public static Competencias findCompetencias(int idCompetencias){
    	return em.find(Competencias.class, idCompetencias) ;
    }
    

	@SuppressWarnings("unchecked")
	public static List<Competencias> list() {
		return em.createNativeQuery("SELECT * FROM esic.competencias", Competencias.class).getResultList();
    }  
}
