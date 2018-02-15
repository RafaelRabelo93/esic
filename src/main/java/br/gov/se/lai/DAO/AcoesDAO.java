package br.gov.se.lai.DAO;

import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;

import br.gov.se.lai.entity.Acoes;
import br.gov.se.lai.entity.Competencias;
import br.gov.se.lai.utils.Consultas;
import br.gov.se.lai.utils.HibernateUtil;

public class AcoesDAO {
	
	private static EntityManager em = HibernateUtil.geteEntityManagerFactory().createEntityManager();
	
    public static void saveOrUpdate(Acoes acoes) {     	        
        try {
        	if(!em.getTransaction().isActive()) em.getTransaction().begin();
        	if(acoes.getIdAcoes() ==  null) {
        		em.persist(acoes);
    		}else {
    			em.merge(acoes);
    		}
            em.getTransaction().commit();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Acoes salvo(a) com sucesso!"));
        } catch (Exception e) {
        	FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Erro ao cadastrar acoes "));
        	System.out.println(e);
            em.getTransaction().rollback();
        }
    }
    public static void delete(Acoes acoes) {        
        try {            
        	Acoes acao = em.find(Acoes.class, acoes.getIdAcoes());
            if (acao == null) {
            	FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Acoes  não cadastrado."));
			}else {
				em.getTransaction().begin();
				em.remove(acao);
	            em.getTransaction().commit();
	            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Acoes  excluida com sucesso!"));
			}            
        } catch (Exception e) {
            em.getTransaction().rollback();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Erro ao excluir acoes "));
        }
    }    
    
    public static Acoes findAcoes(int idAcoes){
    	return em.find(Acoes.class, idAcoes) ;
    }


	@SuppressWarnings("unchecked")
	public static List<Acoes> list() {
		return em.createNativeQuery("SELECT * FROM esic.acoes", Acoes.class).getResultList();
    }  
	
}
