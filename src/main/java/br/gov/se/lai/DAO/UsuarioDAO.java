package br.gov.se.lai.DAO;

import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import br.gov.se.lai.entity.Usuario;
import br.gov.se.lai.utils.HibernateUtil;

public class UsuarioDAO {
	
	private static EntityManager em = HibernateUtil.geteEntityManagerFactory().createEntityManager();
	
    public static boolean saveOrUpdate(Usuario usuario) {     	        
        try {
        	if(!em.getTransaction().isActive()) em.getTransaction().begin();
        	if(usuario.getIdUsuario() ==  null) {
        		em.persist(usuario);
    		}else {
    			em.merge(usuario);
    		}
            em.getTransaction().commit();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Usuario "+ usuario.getNome()+" salvo(a) com sucesso!"));
            return true;
        } catch (Exception e) {
        	FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Erro ao cadastrar usuario "+ usuario.getNome()));
        	System.out.println(e); 
            em.getTransaction().rollback();
            return false;
        }
    }
    public static void delete(Usuario usuario) {        
        try {            
        	Usuario usu = em.find(Usuario.class, usuario.getIdUsuario());
            if (usu == null) {
            	FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Usuario "+ usuario.getNome() +" não cadastrado."));
			}else {
				em.getTransaction().begin();
				em.remove(usu);
	            em.getTransaction().commit();
	            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Usuario "+ usuario.getNome()+" exclui­do com sucesso!"));
			}            
        } catch (Exception e) {
            em.getTransaction().rollback();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Erro ao excluir usuario "+ usuario.getNome()));
        }
    }    
    
    public static Usuario buscarUsuario(String nick) {      	
    	Query query = em.createQuery("FROM Usuario as usu WHERE usu.nick= :nickParam");
    	query.setParameter("nickParam", nick);    	
    	@SuppressWarnings("unchecked")
    	List<Usuario> results = query.getResultList();

    	if(results.isEmpty()){
    	    return null;
    	} else {
    	    return results.get(0);
    	}
        
    }
    
    
    @SuppressWarnings("unchecked")
	public static List<Usuario> buscarNicks(String nick){
    	Query query = em.createQuery("FROM Usuario as usu WHERE usu.nick LIKE :nickParam");
    	query.setParameter("nickParam", nick+"%");
    	List<Usuario> results = query.getResultList();
    	
    	if (results.isEmpty()) {
    		return null;
    	}else {    		
    		return results;
    	}
    	
    }

    @SuppressWarnings("unchecked")
    public static List<Usuario> listarGestoresSistema(){
    	Query query = em.createQuery("FROM Usuario as usu WHERE usu.perfil = 5");
    	List<Usuario> results = query.getResultList();
    	
    	if (results.isEmpty()) {
    		return null;
    	}else {    		
    		return results;
    	}
    	
    }
    
    @SuppressWarnings("unchecked")
    public static Usuario buscarSessionIds(String sessionId){
    	Query query = em.createQuery("FROM Usuario as usu WHERE usu.sessionId LIKE :sessionIdParam");
    	query.setParameter("sessionIdParam", "%"+sessionId+"%");
    	List<Usuario> results = query.getResultList();
    	
    	if (results.isEmpty()) {
    		return null;
    	}else {    		
    		return results.get(0);
    	}
    	
    }
    
    public static Usuario findUsuario(int id){
    	return em.find(Usuario.class, id) ;
    }

	public static List<Usuario> list() {
        return null;
    }
	
//	@SuppressWarnings("unchecked")
//	public static List<Usuario> completeNick (String prefix) {
//		Query query = em.createQuery("FROM Usuario WHERE nick LIKE '%"+prefix+"%' ORDER BY nick");
//		List<Usuario> results = query.getResultList();
//    	
//    	if (results.isEmpty()) {
//    		return null;
//    	}else {    		
//    		return results;
//    	}
//	}

	@SuppressWarnings("unchecked")
	public static List<String> completeNick (String prefix) {
		Query query = em.createQuery("SELECT nick FROM Usuario WHERE nick LIKE '%"+prefix+"%' ORDER BY nick");
		List<String> results = query.getResultList();
    	
    	if (results.isEmpty()) {
    		return null;
    	}else {    		
    		return results;
    	}
	}
}
