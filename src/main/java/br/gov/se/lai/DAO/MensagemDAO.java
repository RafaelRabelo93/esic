package br.gov.se.lai.DAO;

import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;

import br.gov.se.lai.entity.Mensagem;
import br.gov.se.lai.utils.Consultas;
import br.gov.se.lai.utils.HibernateUtil;

public class MensagemDAO {
	
	private static EntityManager em = HibernateUtil.geteEntityManagerFactory().createEntityManager();
	
    public static boolean saveOrUpdate(Mensagem mensagem) {     	        
        try {
        	if(!em.getTransaction().isActive()) em.getTransaction().begin();
        	if(mensagem.getIdMensagem() ==  null) {
        		em.persist(mensagem);
    		}else {
    			em.merge(mensagem);
    		}
        	em.flush();
            em.getTransaction().commit();
//            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Mensagem salvo(a) com sucesso!"));
            return true;
        } catch (Exception e) {
        	FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Erro ao cadastrar mensagem "));
        	System.out.println(e);
            em.getTransaction().rollback();
            return false;
        }
    }
    
    public static void delete(Mensagem mensagem) {        
        try {            
        	Mensagem msg = em.find(Mensagem.class, mensagem.getIdMensagem());
            if (msg == null) {
            	FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Mensagem  n„o cadastrado."));
			}else {
				em.getTransaction().begin();
				em.remove(msg);
	            em.getTransaction().commit();
	            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Mensagem  exclu√≠da com sucesso!"));
			}            
        } catch (Exception e) {
            em.getTransaction().rollback();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Erro ao excluir mensagem "));
        }
    }    
    
    public static Mensagem findMensagem(int idMensagem){
    	return em.find(Mensagem.class, idMensagem) ;
    }

	@SuppressWarnings("unchecked")
	public static List<Mensagem> list(int idSolicitacao) {
		//return em.createNativeQuery("SELECT * FROM esic.mensagem", Mensagem.class).getResultList();
		return (List<Mensagem>) Consultas.buscaPersonalizada("FROM Mensagem as msg WHERE msg.solicitacao.idSolicitacao = "+idSolicitacao,em);
    }
	
	@SuppressWarnings("unchecked")
	public static List<Mensagem> listMensagensSolicitacao(int idSolicitacao){
		String query = "From Mensagem as msg WHERE msg.solicitacao.idSolicitacao = "+idSolicitacao+" AND (msg.tipo = 1 OR msg.tipo = 2)";
		return (List<Mensagem>) Consultas.buscaPersonalizada(query, em);
	}
	@SuppressWarnings("unchecked")
	public static List<Mensagem> listMensagensHistorico(int idSolicitacao){
		String query = "From Mensagem as msg WHERE msg.solicitacao.idSolicitacao = "+idSolicitacao+" AND (msg.tipo = 3 OR msg.tipo = 4)";
		return (List<Mensagem>) Consultas.buscaPersonalizada(query, em);
	}
	@SuppressWarnings("unchecked")
	public static List<Mensagem> listMensagensTramiteInterno(int idSolicitacao){
		String query = "From Mensagem as msg WHERE msg.solicitacao.idSolicitacao = "+idSolicitacao+" AND (msg.tipo = 5)";
		return (List<Mensagem>) Consultas.buscaPersonalizada(query, em);
	}
	
}
