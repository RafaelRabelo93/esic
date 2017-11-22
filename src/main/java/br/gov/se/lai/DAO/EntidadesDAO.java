package br.gov.se.lai.DAO;

import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;

import br.gov.se.lai.entity.Entidades;
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
    
    public static Entidades existeNome(String nome){
    	return (Entidades) (Consultas.buscaPersonalizada("FROM Entidades as ent WHERE ent.nome = "+nome,em));
    }
   
    public static Entidades existeSigla(String sigla){
    	return (Entidades) (Consultas.buscaPersonalizada("FROM Entidades as ent WHERE ent.sigla = "+sigla,em));
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
}
