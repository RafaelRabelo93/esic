package br.gov.se.lai.DAO;

import java.util.List;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import br.gov.se.lai.entity.Entidades;
import br.gov.se.lai.entity.Usuario;
import br.gov.se.lai.utils.Consultas;
import br.gov.se.lai.utils.HibernateUtil;

public class  EntidadesDAO {
	
	private static EntityManager em = HibernateUtil.geteEntityManagerFactory().createEntityManager();
	
    public static void saveOrUpdate(Entidades entidade) {     	        
        try {
        	if(!em.getTransaction().isActive()) em.getTransaction().begin();
        	if(entidade.getIdEntidades() ==  null) {
        		em.persist(entidade);
    		}else { 
    			em.merge(entidade);
    		}
            em.getTransaction().commit();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Entidade "+ entidade.getNome()+" salvo(a) com sucesso!"));
        } catch (Exception e) {
        	FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Erro ao cadastrar entidade "+ entidade.getNome()));
        	System.out.println(e);
            em.getTransaction().rollback();
        }
    }
    public static void delete(Entidades entidade) {        
        try {            
        	Entidades usu = em.find(Entidades.class, entidade.getIdEntidades());
            if (usu == null) {
            	FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Entidade "+ entidade.getNome() +" n„o cadastrado."));
			}else {
				em.getTransaction().begin();
				em.remove(usu);
	            em.getTransaction().commit();
	            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Atividade "+ entidade.getNome()+" exclu√≠da com sucesso!"));
			}            
        } catch (Exception e) {
            em.getTransaction().rollback();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Erro ao excluir atividade "+ entidade.getNome()));
        }
    }    
    
    public static Entidades find(int id){
    	return em.find(Entidades.class, id);
    }
    
	@SuppressWarnings("unchecked")
	public static boolean existeNome(String nome){
		Query query = em.createQuery("FROM Entidades as ent WHERE ent.nome= :nomeParam");
    	query.setParameter("nomeParam", nome);    	
    	List<Usuario> results = query.getResultList();

    	if(results.isEmpty()){
    	    return false;
    	} else {
    	    return true;
    	}
    }
    
    @SuppressWarnings("unchecked")
	public static boolean existeSigla(String sigla){
    	
    	Query query = em.createQuery("FROM Entidades as ent WHERE ent.sigla= :siglaParam");
    	query.setParameter("siglaParam", sigla);    	
    	List<Usuario> results = query.getResultList();

    	if(results.isEmpty()){
    		  return false;
    	} else {
    	    return true;
    	}

    }
   

    @SuppressWarnings("unchecked")
    public static List<Entidades> listOrgaos() {		
    	return (List<Entidades>) (Consultas.buscaPersonalizada("FROM Entidades as ent WHERE ent.orgao = "+1,em));
    }
    
    
	@SuppressWarnings("unchecked")
	public static List<Entidades> list() {		
        return em.createNativeQuery("SELECT * FROM esic.entidades", Entidades.class).getResultList();
    }  
	
	@SuppressWarnings("unchecked")
	public static List<Entidades> listAtivas() {		
        return (List<Entidades>) (Consultas.buscaPersonalizada("FROM Entidades as ent WHERE ent.ativa = "+1,em));
    }

	@SuppressWarnings("unchecked")
	public static List<Entidades> listPersonalizada(int idEntidades) {
		return (List<Entidades>) (Consultas.buscaPersonalizada("FROM Entidades as ent WHERE ent.idEntidades = "+idEntidades,em)); 
		//return (List<Entidades>) (Consultas.buscaPersonalizada("SELECT * FROM esic.entidades as ent INNER JOIN esic.competencias as comp ON ent.idEntidades = "+idAcoes,em));
	}
	@SuppressWarnings("unchecked")
	public static List<Entidades> listOrgaoEntidade(int idOrgao) {
		return (List<Entidades>) (Consultas.buscaPersonalizada("FROM Entidades as ent WHERE ent.idOrgaos = "+idOrgao,em)); 
	}
}
