package br.gov.se.lai.DAO;

import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import br.gov.se.lai.Bean.UsuarioBean;
import br.gov.se.lai.entity.Cidadao;
import br.gov.se.lai.entity.Entidades;
import br.gov.se.lai.entity.Responsavel;
import br.gov.se.lai.entity.Solicitacao;
import br.gov.se.lai.entity.Usuario;
import br.gov.se.lai.utils.Consultas;
import br.gov.se.lai.utils.HibernateUtil;

public class SolicitacaoDAO {
	
	private static EntityManager em = HibernateUtil.geteEntityManagerFactory().createEntityManager();
	
    public static boolean saveOrUpdate(Solicitacao solicitacao) {     	        
        try {
        	if(!em.getTransaction().isActive()) em.getTransaction().begin();
        	if(solicitacao.getIdSolicitacao() ==  null) {
        		em.persist(solicitacao);
    		}else {
    			em.merge(solicitacao);
    		}
            em.getTransaction().commit();
//            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Solicitacao salvo(a) com sucesso!"));
            return true;
        } catch (Exception e) {
        	FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Erro ao cadastrar solicitacao "));
        	System.out.println(e);
            em.getTransaction().rollback();
            return false;
        }
    }
    public static void delete(Solicitacao solicitacao) {        
        try {            
        	Solicitacao soli = em.find(Solicitacao.class, solicitacao.getIdSolicitacao());
            if (soli == null) {
            	FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Solicitacao  n�o cadastrado."));
			}else {
				em.getTransaction().begin();
				em.remove(soli);
	            em.getTransaction().commit();
//	            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Solicitacao  excluída com sucesso!"));
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
		UsuarioBean usuarioBean = (UsuarioBean) HibernateUtil.RecuperarDaSessao("usuario");
		if(usuarioBean != null) 
		{
			if(usuarioBean.getUsuario().getPerfil() == 3 || usuarioBean.getUsuario().getPerfil() == 4 && !usuarioBean.isPerfilAlterarCidadaoResponsavel()) 
			{
				return (List<Solicitacao>) Consultas.buscaPersonalizada("FROM Solicitacao as slt WHERE slt.cidadao.usuario.idUsuario = "+usuarioBean.getUsuario().getIdUsuario(),em);
			}else 
			{
				if(usuarioBean.getUsuario().getPerfil() == 2 || usuarioBean.getUsuario().getPerfil() == 4 && usuarioBean.isPerfilAlterarCidadaoResponsavel()) 
				{
					List<Responsavel> ListResp = new ArrayList<Responsavel>(ResponsavelDAO.findResponsavelUsuario(usuarioBean.getUsuario().getIdUsuario()));
					
					String query = "FROM Solicitacao as slt WHERE (  ";
							
					for (int i = 0; i < ListResp.size(); i++) {
						if (ListResp.get(i).isAtivo()) {
							if(query.contains("slt.entidades.idEntidades")) {
								query += " OR ";
							}
							query += " slt.entidades.idEntidades = ";
							query = query +ListResp.get(i).getEntidades().getIdEntidades()+" AND (slt.instancia = 0";
							for (int j = 1 ;  j <= ListResp.get(i).getNivel(); j++) {
								query = query+" OR slt.instancia = "+ j;
							}
							query += ") AND slt.tipo != 'Den�ncia')";
						}
//						if(i == ListResp.size()-1) {
//							query += ")";
//						}else {
//							query += ") OR ";
//						}

					}
					
					return (List<Solicitacao>) Consultas.buscaPersonalizada(query,em);
				}else if(usuarioBean.getUsuario().getPerfil() != 1)
				{
					return  (List<Solicitacao>)  em.createNativeQuery("SELECT * FROM esic.solicitacao ", Solicitacao.class).getResultList();		
				}else {
					return null;
				}
				
			} 
		}else
		{	
			return null;
		}  
	}
	
	@SuppressWarnings("unchecked")
	public static List<Solicitacao> listStatus(String status) {
		UsuarioBean usuarioBean = (UsuarioBean) HibernateUtil.RecuperarDaSessao("usuario");
		if(usuarioBean != null) 
		{
			if(usuarioBean.getUsuario().getPerfil() == 3 || usuarioBean.getUsuario().getPerfil() == 4 && !usuarioBean.isPerfilAlterarCidadaoResponsavel()) 
			{
				return (List<Solicitacao>)  Consultas.buscaPersonalizada("FROM Solicitacao as slt WHERE slt.status = '"+status
						+ "' AND slt.cidadao.usuario.idUsuario = "+usuarioBean.getUsuario().getIdUsuario(),em);
			}else 
			{
				if(usuarioBean.getUsuario().getPerfil() == 2 || (usuarioBean.getUsuario().getPerfil() == 4 && usuarioBean.isPerfilAlterarCidadaoResponsavel())) 
				{
					List<Responsavel> ListResp = new ArrayList<Responsavel>(ResponsavelDAO.findResponsavelUsuario(usuarioBean.getUsuario().getIdUsuario()));
					
					String query = "FROM Solicitacao as slt WHERE  slt.status = '"+status+"' AND (" ;
							
					for (int i = 0; i < ListResp.size(); i++) {
						if (ListResp.get(i).isAtivo()) {
							
							if(query.contains("slt.entidades.idEntidades")) {
								query += " OR ";
							}
							
							query += "( slt.entidades.idEntidades = " + ListResp.get(i).getEntidades().getIdEntidades() ;
							
							if (ListResp.get(i).getNivel() == 1)
								query += " AND slt.instancia = 0 )" ;
							if (ListResp.get(i).getNivel() == 2)
								query += " AND slt.instancia <= 1 )" ;
							if (ListResp.get(i).getNivel() == 3)
								query += " AND slt.instancia <= 3 )" ;
						
						}
					}
					query += " AND slt.tipo != 'Den�ncia')";
					
					return (List<Solicitacao>)  Consultas.buscaPersonalizada(query+")",em);
				}else if(usuarioBean.getUsuario().getPerfil() != 1)
				{
					return  (List<Solicitacao>)  em.createNativeQuery("SELECT * FROM esic.solicitacao as slt WHERE  slt.status = '"+status+"' ", Solicitacao.class).getResultList();		
				}else {
					return null;
				}
				
			} 
		}else
		{	
			return null;
		}  
	}	
	
	@SuppressWarnings("unchecked")
	public static List<Solicitacao> listPorStatus(String status) {
			return (List<Solicitacao>) (Consultas.buscaPersonalizada("FROM Solicitacao as slt WHERE slt.status = '"+status+"'",em)); 
	}	
	@SuppressWarnings("unchecked")
	public static List<Solicitacao> listPorNaoFinalizada() {
		return (List<Solicitacao>) (Consultas.buscaPersonalizada("FROM Solicitacao as slt WHERE slt.status != 'Finalizada'",em)); 
	}	

	@SuppressWarnings("unchecked")
	public static List<Solicitacao> listPorTipo(String tipo) {
		return (List<Solicitacao>) (Consultas.buscaPersonalizada("FROM Solicitacao as slt WHERE slt.tipo = '"+tipo+"'",em)); 
	}	

	@SuppressWarnings("unchecked")
	public static List<Solicitacao> listPorTipoStatus(String tipo, String status) {
		return (List<Solicitacao>) (Consultas.buscaPersonalizada("FROM Solicitacao as slt WHERE slt.status = '"+status+"' AND slt.tipo = '"+tipo+"'",em)); 
	}	
	
	@SuppressWarnings("unchecked")
	public static List<Solicitacao> listPorTipoStatusPeriodo(String tipo, String status, String periodo) {
		return (List<Solicitacao>) (Consultas.buscaPersonalizada("FROM Solicitacao as slt WHERE slt.status = '"+status+"' AND slt.tipo = '"+tipo+"' AND dataIni LIKE '"+periodo+"'",em)); 
	}	
	
	@SuppressWarnings("unchecked")
	public static List<Solicitacao> listPorTipoPeriodo(String tipo, String periodo) {
		return (List<Solicitacao>) (Consultas.buscaPersonalizada("FROM Solicitacao as slt WHERE slt.tipo = '"+tipo+"' AND dataIni LIKE '"+periodo+"'",em)); 
	}	
	
	@SuppressWarnings("unchecked")
	public static List<Solicitacao> listarPorEntidade(int idEntidade) {
		return (List<Solicitacao>) (Consultas.buscaPersonalizada("FROM Solicitacao as slt WHERE slt.entidades.idEntidades = "+idEntidade,em)); 
	}	
	
	@SuppressWarnings("unchecked")
	public static List<Solicitacao> listarPorEntidade(int idEntidade,String tipo, String periodo) {
		return (List<Solicitacao>) (Consultas.buscaPersonalizada("FROM Solicitacao as slt WHERE slt.entidades.idEntidades = '"+idEntidade+"' AND slt.tipo = '"+tipo+"' AND dataIni LIKE '"+periodo+"'",em)); 
	}	
	@SuppressWarnings("unchecked")
	public static List<Solicitacao> listarPorEntidade(int idEntidade,String tipo, String status, String periodo) {
		return (List<Solicitacao>) (Consultas.buscaPersonalizada("FROM Solicitacao as slt WHERE slt.entidades.idEntidades = "+idEntidade+" AND slt.tipo = '"+tipo+"' AND slt.status = '"+status+"' AND dataIni LIKE '"+periodo+"'",em)); 
	}	

	@SuppressWarnings("unchecked")
	public static List<String> listarPorFederacao(String tipo, String periodo) {
		String HQL= "SELECT solicitacao.cidadao.estado FROM  Solicitacao as solicitacao "+
					"JOIN  Cidadao as cidadao "+
					"ON solicitacao.cidadao.idCidadao = cidadao.idCidadao "+
					"WHERE solicitacao.dataIni LIKE '"+periodo+"' "+
					"AND solicitacao.tipo = 'Informa��o'";
		return (List<String>) Consultas.buscaPersonalizada(HQL, em); 
	}
	
	@SuppressWarnings("unchecked")
	public static List<Solicitacao> queryDinamica(String query) {
		return (List<Solicitacao>) Consultas.buscaPersonalizada(query, em); 
	}	

	
	@SuppressWarnings("unchecked")
	public static List<Cidadao> listarCidadao(String tipo, String periodo) {
		String HQL= "SELECT solicitacao.cidadao FROM  Solicitacao as solicitacao "+
				"JOIN Cidadao as cidadao "+
				"ON solicitacao.cidadao.idCidadao = cidadao.idCidadao "+
				"WHERE solicitacao.dataIni LIKE '"+periodo+"' "+
				"AND solicitacao.tipo = '"+tipo+"'";
		return (List<Cidadao>) Consultas.buscaPersonalizada(HQL, em); 
	}	
	

	@SuppressWarnings("unchecked")
	public static List<String> listarAssuntos(String tipo, String periodo) {
		String HQL= "SELECT solicitacao.competencias.acoes.titulo FROM Solicitacao as solicitacao "+
				"JOIN  Competencias as competencias "+
				"ON solicitacao.competencias.idCompetencias = competencias.idCompetencias "+
				"JOIN  Acoes as acoes  "+
				"ON  solicitacao.competencias.acoes.idAcoes = acoes.idAcoes "+
				"WHERE solicitacao.dataIni LIKE '"+periodo+"' "+
				"AND solicitacao.tipo = '"+tipo+"'";
		return (List<String>) Consultas.buscaPersonalizada(HQL, em); 
	}	
	
	
	@SuppressWarnings("unchecked")
	public static List<Solicitacao> listarGeralFinalizada() {
		String status = "Finalizada";
		Query query = em.createQuery("FROM Solicitacao as slt WHERE slt.status =  :sltParam");
		query.setParameter("sltParam", status);
    	
    	List<Solicitacao> results = query.getResultList();
    	return results;    	
    } 

	@SuppressWarnings("unchecked")
	public static List<Solicitacao> listarGeral() {
		Query query = em.createQuery("FROM Solicitacao as slt ");
		
		List<Solicitacao> results = query.getResultList();
		return results;    	
	} 
	
	@SuppressWarnings("unchecked")
	public static List<Solicitacao> listarAvaliadas(int idEntidade) {
		return (List<Solicitacao>) (Consultas.buscaPersonalizada("FROM Solicitacao as slt WHERE slt.entidades.idEntidades = "+idEntidade+" AND ( slt.avaliacao IS NOT NULL and slt.avaliacao != 0)",em)); 
	}
	
	// Consultas utilizadas exclusivamente para gera��o de gr�ficos
		
	@SuppressWarnings("unchecked")
	public static List<Solicitacao> listarTotalPorPeriodo(String periodo) {
		return (List<Solicitacao>) (Consultas.buscaPersonalizada("FROM Solicitacao as slt WHERE slt.tipo = 'Informa��o' AND slt.dataIni LIKE '" + periodo +  "')",em)); 
	}
	
	@SuppressWarnings("unchecked")
	public static List<Solicitacao> listarAtendidasPorPeriodo(String periodo) {
		String HQL= "SELECT DISTINCT slt " + 
				"FROM Solicitacao as slt " + 
				"INNER JOIN Mensagem as msg  " + 
				"ON slt.idSolicitacao = msg.solicitacao.idSolicitacao  " + 
				"WHERE msg.tipo = 2 " + 
				"AND slt.dataIni LIKE '" + periodo + "' " + 
				"AND slt.tipo = 'Informa��o' " + 
				"AND slt.status != 'Sem Resposta'";
		return (List<Solicitacao>) Consultas.buscaPersonalizada(HQL, em);
	}
	
	@SuppressWarnings("unchecked")
	public static List<Solicitacao> listarSemRespostaPorPeriodo(String periodo) {
		String HQL= "SELECT DISTINCT slt " + 
				"FROM Solicitacao as slt " + 
				"INNER JOIN Mensagem as msg  " + 
				"ON slt.idSolicitacao = msg.solicitacao.idSolicitacao  " + 
				"WHERE slt.tipo = 'Informa��o' " + 
				"AND slt.dataIni LIKE '" + periodo + "' " + 
				"AND (slt.status = 'Sem Resposta' " + 
				"OR (slt.status = 'Negada'  " + 
				"AND slt.idSolicitacao NOT IN ( " + 
				"SELECT DISTINCT slt2.idSolicitacao  " + 
				"FROM Solicitacao as slt2  " + 
				"INNER JOIN Mensagem as msg2 ON slt2.idSolicitacao = msg2.solicitacao.idSolicitacao " + 
				"WHERE msg2.tipo = 2  " + 
				"AND slt2.tipo = 'Informa��o' AND slt2.status != 'Sem Resposta')))";
		return (List<Solicitacao>) Consultas.buscaPersonalizada(HQL, em);
	}
	
	@SuppressWarnings("unchecked")
	public static List<Solicitacao> listarEmTramitePorPeriodo(String periodo) {
		String HQL= "SELECT DISTINCT slt " + 
				"FROM Solicitacao as slt " + 
				"INNER JOIN Mensagem as msg  " + 
				"ON slt.idSolicitacao = msg.solicitacao.idSolicitacao  " + 
				"WHERE slt.tipo = 'Informa��o' " + 
				"AND slt.dataIni LIKE '" + periodo + "' " + 
				"AND slt.visualizada = 1 " + 
				"AND slt.idSolicitacao NOT IN (SELECT DISTINCT slt2.idSolicitacao " + 
				"FROM Solicitacao as slt2 " + 
				"INNER JOIN Mensagem as msg2 " + 
				"ON slt2.idSolicitacao = msg2.solicitacao.idSolicitacao  " + 
				"WHERE msg2.tipo = 2 " + 
				"AND slt2.dataIni LIKE '" + periodo + "' " + 
				"AND slt2.tipo = 'Informa��o' " + 
				"AND slt2.status != 'Sem Resposta') " + 
				"AND slt.status != 'Sem Resposta' " + 
				"AND slt.status != 'Negada'";
		return (List<Solicitacao>) Consultas.buscaPersonalizada(HQL, em);
	}
	
	@SuppressWarnings("unchecked")
	public static List<Solicitacao> listarNaoVisualizadasPorPeriodo(String periodo) {
		String HQL = "SELECT slt " + 
				"FROM Solicitacao as slt  " + 
				"WHERE slt.tipo = 'Informa��o'  " + 
				"AND slt.dataIni LIKE '" + periodo + "' " + 
				"AND slt.visualizada = 0 " + 
				"AND (slt.status = 'Aberta' OR slt.status = 'Transi��o')";
		return (List<Solicitacao>) Consultas.buscaPersonalizada(HQL, em); 
	}
	
	// Consultas utilizadas exclusivamente para gera��o de gr�ficos por �rg�o
	
	@SuppressWarnings("unchecked")
	public static List<Solicitacao> listarTotalPorEntidade(String periodo, int idEntidade) {
		String HQL = "SELECT slt FROM Solicitacao as slt "
					+ "WHERE slt.tipo = 'Informa��o' "
					+ "AND slt.dataIni LIKE '" + periodo +  "' "
					+ "AND slt.entidades.idEntidades = " + idEntidade;
		return (List<Solicitacao>) (Consultas.buscaPersonalizada(HQL,em)); 
	}
	
	@SuppressWarnings("unchecked")
	public static List<Solicitacao> listarAtendidasPorEntidade(String periodo, int idEntidade) {
		String HQL= "SELECT DISTINCT slt " + 
				"FROM Solicitacao as slt " + 
				"INNER JOIN Mensagem as msg  " + 
				"ON slt.idSolicitacao = msg.solicitacao.idSolicitacao  " + 
				"WHERE msg.tipo = 2 " + 
				"AND slt.dataIni LIKE '" + periodo + "' " + 
				"AND slt.tipo = 'Informa��o' " + 
				"AND slt.status != 'Sem Resposta'" +
				"AND slt.entidades.idEntidades = " + idEntidade + "";
		return (List<Solicitacao>) Consultas.buscaPersonalizada(HQL, em);
	}
	
	@SuppressWarnings("unchecked")
	public static List<Solicitacao> listarSemRespostaPorEntidade(String periodo, int idEntidade) {
		String HQL= "SELECT DISTINCT slt " + 
				"FROM Solicitacao as slt " + 
				"INNER JOIN Mensagem as msg  " + 
				"ON slt.idSolicitacao = msg.solicitacao.idSolicitacao  " + 
				"WHERE slt.tipo = 'Informa��o' " + 
				"AND slt.dataIni LIKE '" + periodo + "' " + 
				"AND (slt.status = 'Sem Resposta' " + 
				"OR (slt.status = 'Negada'  " + 
				"AND slt.idSolicitacao NOT IN ( " + 
				"SELECT DISTINCT slt2.idSolicitacao  " + 
				"FROM Solicitacao as slt2  " + 
				"INNER JOIN Mensagem as msg2 ON slt2.idSolicitacao = msg2.solicitacao.idSolicitacao " + 
				"WHERE msg2.tipo = 2  " + 
				"AND slt2.tipo = 'Informa��o' AND slt2.status != 'Sem Resposta')))" +
				"AND slt.entidades.idEntidades = " + idEntidade + "";
		return (List<Solicitacao>) Consultas.buscaPersonalizada(HQL, em);
	}
	
	@SuppressWarnings("unchecked")
	public static List<Solicitacao> listarEmTramitePorEntidade(String periodo, int idEntidade) {
		String HQL= "SELECT DISTINCT slt " + 
				"FROM Solicitacao as slt " + 
				"INNER JOIN Mensagem as msg  " + 
				"ON slt.idSolicitacao = msg.solicitacao.idSolicitacao  " + 
				"WHERE slt.tipo = 'Informa��o' " + 
				"AND slt.dataIni LIKE '" + periodo + "' " + 
				"AND slt.visualizada = 1 " + 
				"AND slt.idSolicitacao NOT IN (SELECT DISTINCT slt2.idSolicitacao " + 
				"FROM Solicitacao as slt2 " + 
				"INNER JOIN Mensagem as msg2 " + 
				"ON slt2.idSolicitacao = msg2.solicitacao.idSolicitacao  " + 
				"WHERE msg2.tipo = 2 " + 
				"AND slt2.dataIni LIKE '" + periodo + "' " + 
				"AND slt2.tipo = 'Informa��o' " + 
				"AND slt2.status != 'Sem Resposta') " + 
				"AND slt.status != 'Sem Resposta' " + 
				"AND slt.status != 'Negada'" +
				"AND slt.entidades.idEntidades = " + idEntidade + "";
		return (List<Solicitacao>) Consultas.buscaPersonalizada(HQL, em);
	}
	
	@SuppressWarnings("unchecked")
	public static List<Solicitacao> listarNaoVisualizadasPorEntidade(String periodo, int idEntidade) {
		String HQL = "SELECT slt " + 
				"FROM Solicitacao as slt  " + 
				"WHERE slt.tipo = 'Informa��o'  " + 
				"AND slt.dataIni LIKE '" + periodo + "' " + 
				"AND slt.visualizada = 0 " + 
				"AND (slt.status = 'Aberta' OR slt.status = 'Transi��o')" +
				"AND slt.entidades.idEntidades = " + idEntidade + "";
		return (List<Solicitacao>) Consultas.buscaPersonalizada(HQL, em); 
	}
	
}
