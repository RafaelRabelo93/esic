package br.gov.se.lai.DAO;

import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;

import br.gov.se.lai.entity.Anexo;
import br.gov.se.lai.entity.Mensagem;
import br.gov.se.lai.utils.Consultas;
import br.gov.se.lai.utils.HibernateUtil;

public class AnexoDAO {
	
	private static EntityManager em = HibernateUtil.geteEntityManagerFactory().createEntityManager();
	
    public static void saveOrUpdate(Anexo anexo) {     	        
        try {
        	if(!em.getTransaction().isActive()) em.getTransaction().begin();
        	if(anexo.getIdAnexo() ==  null) {
        		em.persist(anexo);
    		}else {
    			em.merge(anexo);
    		}
            em.getTransaction().commit();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Anexo salvo(a) com sucesso!"));
        } catch (Exception e) {
        	FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Erro ao cadastrar anexo "));
        	System.out.println(e);
            em.getTransaction().rollback();
        }
    }
    public static void delete(Anexo anexo) {        
        try {            
        	Anexo anx = em.find(Anexo.class, anexo.getIdAnexo());
            if (anx == null) {
            	FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Anexo  n„o cadastrado."));
			}else {
				em.getTransaction().begin();
				em.remove(anx);
	            em.getTransaction().commit();
	            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Anexo  exclu√≠da com sucesso!"));
			}            
        } catch (Exception e) {
            em.getTransaction().rollback();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Erro ao excluir anexo "));
        }
    }    
    
    public static Anexo findAnexo(int idAnexo){
    	return em.find(Anexo.class, idAnexo) ;
    }
    

	public static List<Anexo> list() {
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static List<Anexo> listarAnexoMensagem(int idMensagem){
		return (List<Anexo>) Consultas.buscaPersonalizada("FROM Anexo as anx WHERE anx.mensagem.idMensagem = "+idMensagem,em);
	}
}
