package br.gov.se.lai.DAO;

import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import br.gov.se.lai.entity.Acoes;
import br.gov.se.lai.entity.Competencias;
import br.gov.se.lai.entity.Usuario;
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
//            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Acoes salvo(a) com sucesso!"));
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
		return em.createNativeQuery("SELECT * FROM esic.acoes ORDER BY acoes.titulo ASC", Acoes.class).getResultList();
    }  


    @SuppressWarnings("unchecked")
	public static List<Acoes> listPorStatus(String status){
    	Query query = em.createQuery("FROM Acoes as acoes WHERE acoes.status = '"+status+"'");
    	List<Acoes> results = query.getResultList();
   		
    	return results;  	
    }
    
    @SuppressWarnings("unchecked")
    public static List<Acoes> listPorStatus(String status, String status2){
    	Query query = em.createQuery("FROM Acoes as acoes WHERE acoes.status = '"+status+"' OR acoes.status = '"+status2+"' ORDER BY acoes.titulo ASC");
    	List<Acoes> results = query.getResultList();
   		
    	return results;  	
    }

	
}
