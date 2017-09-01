package br.gov.se.lai.DAO;

import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;

import br.gov.se.lai.Entity.Usuario;
import br.gov.se.lai.utils.HibernateUtil;

public class UsuarioDAO {
	private static EntityManager em = HibernateUtil.geteEntityManagerFactory().createEntityManager();
	
    public static void saveOrUpdate(Usuario usuario) {     	        
        try {
        	em.getTransaction().begin();
        	if(usuario.getIdUsuario() == 0) {
        		em.persist(usuario);
    		}else {
    			em.merge(usuario);
    		}
            em.getTransaction().commit();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Usuario "+ usuario.getNome()+" salvo(a) com sucesso!"));
        } catch (Exception e) {
        	FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Erro ao cadastrar usuario "+ usuario.getNome()));
        	System.out.println(e);
            em.getTransaction().rollback();
        }
    }
    public static void delete(Usuario usuario) {        
        try {            
        	Usuario usu = em.find(Usuario.class, usuario.getIdUsuario());
            if (usu == null) {
            	FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Usuario "+ usuario.getNome() +" n„o cadastrado."));
			}else {
				em.getTransaction().begin();
				em.remove(usu);
	            em.getTransaction().commit();
	            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Atividade "+ usuario.getNome()+" exclu√≠da com sucesso!"));
			}            
        } catch (Exception e) {
            em.getTransaction().rollback();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Erro ao excluir atividade "+ usuario.getNome()));
        }
    }    
    
    public static Usuario findUsuario(String email){
    	return em.find(Usuario.class, email) ;
    }

	public static List<Usuario> list() {
	
        return null;
    }  
}
