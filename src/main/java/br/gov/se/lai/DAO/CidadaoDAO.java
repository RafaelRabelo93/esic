package br.gov.se.lai.DAO;

import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;

import br.gov.se.lai.entity.Cidadao;
import br.gov.se.lai.utils.HibernateUtil;

public class CidadaoDAO {
	private static EntityManager em = HibernateUtil.geteEntityManagerFactory().createEntityManager();
	
    public static void saveOrUpdate(Cidadao cidadao) {     	        
        try {
        	em.getTransaction().begin();
        	if(cidadao.getIdCidadao() == null) {
        		em.persist(cidadao);
    		}else {
    			em.merge(cidadao);
    		}
            em.getTransaction().commit();  
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Cidadao "+ cidadao.getUsuario().getNome()+" salvo(a) com sucesso!"));
        } catch (Exception e) {   
        	FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Erro ao cadastrar cidadao "));
        	System.out.println(e);
            em.getTransaction().rollback();
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

	public static List<Cidadao> list() {
	
        return null;
    }  
}
