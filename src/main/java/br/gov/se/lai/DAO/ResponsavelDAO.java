package br.gov.se.lai.DAO;

import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import br.gov.se.lai.entity.Cidadao;
import br.gov.se.lai.entity.Entidades;
import br.gov.se.lai.entity.Responsavel;
import br.gov.se.lai.entity.Solicitacao;
import br.gov.se.lai.utils.Consultas;
import br.gov.se.lai.utils.HibernateUtil;

public class  ResponsavelDAO {
	
	private static EntityManager em = HibernateUtil.geteEntityManagerFactory().createEntityManager();
	
    public static void saveOrUpdate(Responsavel responsavel) {     	        
        try {
        	if(!em.getTransaction().isActive()) em.getTransaction().begin();
        	if(responsavel.getIdResponsavel() ==  null) {
        		em.persist(responsavel);
    		}else {
    			em.merge(responsavel);
    		}
            em.getTransaction().commit();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Responsavel  salvo(a) com sucesso!"));
        } catch (Exception e) {
        	FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Erro ao cadastrar responsavel "));
        	System.out.println(e);
            em.getTransaction().rollback();
        }
    }
    public static void delete(Responsavel responsavel) {        
        try {            
        	Responsavel usu = em.find(Responsavel.class, responsavel.getIdResponsavel());
            if (usu == null) {
            	FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Responsavel  n„o cadastrado."));
			}else {
				em.getTransaction().begin();
				em.remove(usu);
	            em.getTransaction().commit();
	            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Atividade exclu√≠da com sucesso!"));
			}            
        } catch (Exception e) {
            em.getTransaction().rollback();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Erro ao excluir responsavel "));
        }
    }    
    
    public static Responsavel findResponsavel(int idResponsavel){
    	return em.find(Responsavel.class, idResponsavel) ;
    }

    @SuppressWarnings("unchecked")
	public static List<Responsavel> findResponsavelEntidadeNivel(int idEntidades, int nivel) {    	
		return (List<Responsavel>) Consultas.buscaPersonalizada("FROM Responsavel as resp WHERE resp.entidades.idEntidades = "+idEntidades
																	+" AND resp.nivel = "+nivel+" AND resp.ativo = 1",em);
    }

    @SuppressWarnings("unchecked")
    public static List<Responsavel> findResponsavelEntidade(int idEntidades) {    	
    	return (List<Responsavel>) Consultas.buscaPersonalizada("FROM Responsavel as resp WHERE resp.entidades.idEntidades = "+idEntidades
    																+"AND resp.ativo = 1",em);
    }
    
    
    public static boolean verificaResponsavelEmail(String email) {
    	 if(Consultas.buscaPersonalizada("FROM Responsavel as resp WHERE resp.email = "+email,em).size() > 0) {
    		 return true;
    	 }else {
    		 return false;
    	 }
    }
    @SuppressWarnings("unchecked")
	public static Responsavel findResponsavelEmail(String email) {
    	Query query = em.createQuery("FROM Responsavel as resp WHERE resp.email = :emailParam");
    	query.setParameter("emailParam", email);  

    	List<Responsavel> results = query.getResultList();
  	 	return results.get(0);
    }
    
    @SuppressWarnings("unchecked")
    public static List<Responsavel> findResponsavelUsuarioAtivo(int idUsuario) {
    	Query query = em.createQuery("FROM Responsavel as resp WHERE resp.usuario.idUsuario = :usuarioParam AND resp.ativo = 1");
    	query.setParameter("usuarioParam", idUsuario);  
    	
    	List<Responsavel> results = query.getResultList();
    	return results;
    }

    @SuppressWarnings("unchecked")
    public static List<Responsavel> findResponsavelUsuario(int idUsuario) {
    	Query query = em.createQuery("FROM Responsavel as resp WHERE resp.usuario.idUsuario = :usuarioParam");
    	query.setParameter("usuarioParam", idUsuario);  
    	
    	List<Responsavel> results = query.getResultList();
    	return results;
    }

    @SuppressWarnings("unchecked")
	public static List<Entidades> listEntidadePossuemGestores() {		
        String hql = "SELECT responsavel.entidades FROM Responsavel as responsavel "
        		+ "JOIN  responsavel.entidades as entidades "
        		+ "WHERE responsavel.nivel = 3 AND responsavel.ativo = true "
        		+ "AND responsavel.entidades.idEntidades = entidades.idEntidades";
    	return (List<Entidades>) Consultas.buscaPersonalizada(hql, em); 
	
    }    

    @SuppressWarnings("unchecked")
    public static List<Responsavel> list() {		
    	Query query = em.createQuery("FROM Responsavel as resp WHERE resp.ativo = 1 OR resp.ativo = 0");
    	
    	List<Responsavel> results = query.getResultList();
    	return results;    	
    }   
    
    @SuppressWarnings("unchecked")
    public static List<Responsavel> listar(){
    	return (List<Responsavel>) em.createNativeQuery("SELECT * FROM Responsavel", Responsavel.class).getResultList();
    }
}
