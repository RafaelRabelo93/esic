package br.gov.se.lai.DAO;

import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;

import br.gov.se.lai.entity.Competencias;
import br.gov.se.lai.utils.Consultas;
import br.gov.se.lai.utils.HibernateUtil;

public class  CompetenciasDAO {
	
	private static EntityManager em = HibernateUtil.geteEntityManagerFactory().createEntityManager();
	
    public static void saveOrUpdate(Competencias competencias) {     	        
        try {        	
        	if(!em.getTransaction().isActive()) em.getTransaction().begin();
        	if(competencias.getIdCompetencias() ==  null) {
        		em.persist(competencias);
    		}else {
    			em.merge(competencias);
    		}
            em.getTransaction().commit();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Competencias  salvo(a) com sucesso!"));
        } catch (Exception e) {
        	FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Erro ao cadastrar competencia "));
        	System.out.println(e);
            em.getTransaction().rollback();
        }
    }
    public static void delete(Competencias competencias) {        
        try {            
        	Competencias comp = em.find(Competencias.class, competencias.getIdCompetencias());
            if (comp == null) {
            	FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Competencias  n„o cadastrado."));
			}else {
				em.getTransaction().begin();
				em.remove(comp);
	            em.getTransaction().commit();
	            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Competencias exclu√≠da com sucesso!"));
			}            
        } catch (Exception e) {
            em.getTransaction().rollback();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Erro ao excluir competencias "));
        }
    }    
    
    public static Competencias findCompetencias(int idCompetencias){
    	return em.find(Competencias.class, idCompetencias) ;
    }
    
    @SuppressWarnings("unchecked")
    public static boolean verificaExistencia(int idAcao, int idEntidade) {
    	String sql = "SELECT idCompetencias FROM esic.competencias WHERE idEntidades = "+ idEntidade +" AND idAcoes ="+ idAcao; 
		List<Competencias> query = (List<Competencias>) Consultas.buscaSQL(sql, em);
		if (query.isEmpty()) {
			return false;
		}else {
			return true;
		}
	}
    
//    @SuppressWarnings("unchecked")
//    public static List<Competencias> verificaExistencia(int idEntidade) {
//    	String sql = "SELECT idCompetencias FROM esic.competencias WHERE idEntidades = "; 
//		List<Competencias> query = (List<Competencias>) Consultas.buscaSQL(sql, em);
//		return query;
//	}

	@SuppressWarnings("unchecked")
	public static List<Competencias> list() {
		return em.createNativeQuery("SELECT * FROM esic.competencias", Competencias.class).getResultList();
    }  
	
	@SuppressWarnings("unchecked")
	public static List<Competencias> filtrarCompetencias(int idAcao) {
		return (List<Competencias>) Consultas.buscaPersonalizada("FROM Competencias as competencias WHERE competencias.acoes.idAcoes = "+idAcao,em);
    }
	
	@SuppressWarnings("unchecked")
	public static List<Competencias> filtrarCompetenciaPorEntidade(int idEntidade) {
		return (List<Competencias>) Consultas.buscaPersonalizada("FROM Competencias as competencias WHERE competencias.entidades.idEntidades = "+idEntidade,em);
    }
}
