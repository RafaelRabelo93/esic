package br.gov.se.lai.DAO;

import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;

import br.gov.se.lai.entity.Solicitacao;
import br.gov.se.lai.utils.HibernateUtil;

public class SolicitacaoDAO {
	
	private static EntityManager em = HibernateUtil.geteEntityManagerFactory().createEntityManager();
	
    public static void saveOrUpdate(Solicitacao solicitacao) {     	        
        try {
        	if(!em.getTransaction().isActive()) em.getTransaction().begin();
        	if(solicitacao.getIdSolicitacao() ==  null) {
        		em.persist(solicitacao);
    		}else {
    			em.merge(solicitacao);
    		}
            em.getTransaction().commit();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Solicitacao salvo(a) com sucesso!"));
        } catch (Exception e) {
        	FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Erro ao cadastrar solicitacao "));
        	System.out.println(e);
            em.getTransaction().rollback();
        }
    }
    public static void delete(Solicitacao solicitacao) {        
        try {            
        	Solicitacao soli = em.find(Solicitacao.class, solicitacao.getIdSolicitacao());
            if (soli == null) {
            	FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Solicitacao  n„o cadastrado."));
			}else {
				em.getTransaction().begin();
				em.remove(soli);
	            em.getTransaction().commit();
	            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Solicitacao  exclu√≠da com sucesso!"));
			}            
        } catch (Exception e) {
            em.getTransaction().rollback();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Erro ao excluir solicitacao "));
        }
    }    
    
    public static Solicitacao findSolicitacao(int idSolicitacao){
    	return em.find(Solicitacao.class, idSolicitacao) ;
    }

	@SuppressWarnings("unchecked")
	public static List<Solicitacao> list() {
		return em.createNativeQuery("SELECT * FROM esic.solicitacao", Solicitacao.class).getResultList();
    }  
}
