package br.gov.se.lai.DAO;

import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;

import br.gov.se.lai.entity.Entidades;
import br.gov.se.lai.utils.HibernateUtil;

public class  EntidadesDAO {
	
	private static EntityManager em = HibernateUtil.geteEntityManagerFactory().createEntityManager();
	
    public static void saveOrUpdate(Entidades entidades) {     	        
        try {
        	if(!em.getTransaction().isActive()) em.getTransaction().begin();
        	if(entidades.getIdEntidades() ==  null) {
        		em.persist(entidades);
    		}else {
    			em.merge(entidades);
    		}
            em.getTransaction().commit();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Entidade "+ entidades.getNome()+" salvo(a) com sucesso!"));
        } catch (Exception e) {
        	FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Erro ao cadastrar entidades "+ entidades.getNome()));
        	System.out.println(e);
            em.getTransaction().rollback();
        }
    }
    public static void delete(Entidades entidades) {        
        try {            
        	Entidades usu = em.find(Entidades.class, entidades.getIdEntidades());
            if (usu == null) {
            	FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Entidades "+ entidades.getNome() +" n„o cadastrado."));
			}else {
				em.getTransaction().begin();
				em.remove(usu);
	            em.getTransaction().commit();
	            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Atividade "+ entidades.getNome()+" exclu√≠da com sucesso!"));
			}            
        } catch (Exception e) {
            em.getTransaction().rollback();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Erro ao excluir atividade "+ entidades.getNome()));
        }
    }    
    
    public static Entidades find(int id){
    	return em.find(Entidades.class, id);
    }

	public static List<Entidades> list() {
	
        return null;
    }  
}
