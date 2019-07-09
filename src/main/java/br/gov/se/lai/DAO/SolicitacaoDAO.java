package br.gov.se.lai.DAO;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import br.gov.se.lai.Bean.UsuarioBean;
import br.gov.se.lai.entity.Cidadao;
import br.gov.se.lai.entity.Responsavel;
import br.gov.se.lai.entity.Solicitacao;
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
              FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Solicitacao  não cadastrado."));
      }else {
        em.getTransaction().begin();
        em.remove(soli);
              em.getTransaction().commit();
//              FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Solicitacao  excluÃ­da com sucesso!"));
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
  public static List<Solicitacao> list(UsuarioBean usuarioBean) {
//    UsuarioBean usuarioBean = (UsuarioBean) HibernateUtil.RecuperarDaSessao("usuario");
    if(usuarioBean != null) 
    { 
      if(usuarioBean.getUsuario().getPerfil() == 3 || usuarioBean.getUsuario().getPerfil() == 4 && !usuarioBean.isPerfilAlterarCidadaoResponsavel()) {
        return (List<Solicitacao>) Consultas.buscaPersonalizada("FROM Solicitacao as slt WHERE slt.cidadao.usuario.idUsuario = "+usuarioBean.getUsuario().getIdUsuario(),em);
      } else {
        if(usuarioBean.getUsuario().getPerfil() == 2 || usuarioBean.getUsuario().getPerfil() == 4 && usuarioBean.isPerfilAlterarCidadaoResponsavel()) {
          
          List<Responsavel> ListResp = new ArrayList<Responsavel>(ResponsavelDAO.findResponsavelUsuario(usuarioBean.getUsuario().getIdUsuario()));
          
          for (Responsavel resp : ListResp) {
            if (resp.getEntidades().getSigla().equals("OGE") && resp.isAtivo()) {
              String query2 = "FROM Solicitacao as slt";
              
              return (List<Solicitacao>) Consultas.buscaPersonalizada(query2,em);
            }
          }
          
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
              query += ") AND (slt.tipo != 'Denúncia' OR slt.liberaDenuncia = 1))";
            }

          }
          
          return (List<Solicitacao>) Consultas.buscaPersonalizada(query,em);
        } else if(usuarioBean.getUsuario().getPerfil() == 5 || usuarioBean.getUsuario().getPerfil() == 6) {
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
          query += " AND slt.tipo != 'Denúncia')";
          
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
  
  public static int lastId() {
    String HQL = "Select max(idSolicitacao) from Solicitacao";
    return Consultas.valorSQL(HQL, em);
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
  public static List<String> listarPorFederacao(String tipo, String dataInicial, String dataFinal) {
    String HQL= "SELECT solicitacao.cidadao.estado FROM  Solicitacao as solicitacao "+
          "JOIN  Cidadao as cidadao "+
          "ON solicitacao.cidadao.idCidadao = cidadao.idCidadao "+
          "WHERE solicitacao.dataIni BETWEEN '" + dataInicial +  "' and '" + dataFinal + " 23:59:59' "+
          "AND solicitacao.tipo = 'Informação'" +
          "ORDER BY solicitacao.cidadao.estado ASC";
    return (List<String>) Consultas.buscaPersonalizada(HQL, em); 
  }
  
  @SuppressWarnings("unchecked")
  public static List<Solicitacao> queryDinamica(String query) {
    return (List<Solicitacao>) Consultas.buscaPersonalizada(query, em); 
  } 

  
  @SuppressWarnings("unchecked")
  public static List<Cidadao> listarCidadao(String tipo, String dataInicial, String dataFinal) {
    String HQL= "SELECT solicitacao.cidadao FROM  Solicitacao as solicitacao "+
        "JOIN Cidadao as cidadao "+
        "ON solicitacao.cidadao.idCidadao = cidadao.idCidadao "+
        "WHERE solicitacao.dataIni BETWEEN '" + dataInicial +  "' and '" + dataFinal + " 23:59:59' "+
        "AND solicitacao.tipo = '"+tipo+"'";
    return (List<Cidadao>) Consultas.buscaPersonalizada(HQL, em); 
  } 
  

  @SuppressWarnings("unchecked")
  public static List<String> listarAssuntos(String tipo, String dataInicial, String dataFinal) {
    String HQL= "Select acoes.titulo from Acoes as acoes " +
        "INNER JOIN Solicitacao as solicitacao on solicitacao.acoes.idAcoes = acoes.idAcoes " +
        "WHERE solicitacao.dataIni BETWEEN '" + dataInicial +  "' and '" + dataFinal + " 23:59:59' "+
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
  
  // Consultas utilizadas exclusivamente para geração de gráficos
    
  @SuppressWarnings("unchecked")
  public static int listarTotalPorPeriodo(String dataInicial, String dataFinal) {
    String HQL = "SELECT COUNT(DISTINCT slt.idSolicitacao) FROM Solicitacao as slt WHERE slt.tipo = 'Informação' AND slt.dataIni BETWEEN '" + dataInicial +  "' and '" + dataFinal + " 23:59:59')";
    return Consultas.contadorSQL(HQL, em);
  }
  
  @SuppressWarnings("unchecked")
  public static int listarAtendidasPorPeriodo(String dataInicial, String dataFinal) {
    String HQL= "SELECT COUNT(DISTINCT slt.idSolicitacao) " + 
        "FROM Solicitacao as slt " + 
        "INNER JOIN Mensagem as msg  " + 
        "ON slt.idSolicitacao = msg.solicitacao.idSolicitacao  " + 
        "WHERE ((msg.tipo = 2 " + 
        "AND slt.tipo = 'Informação' " + 
        "AND slt.status != 'Sem Resposta') " +
        "OR slt.status = 'Finalizada') " +
        "AND slt.dataIni BETWEEN '" + dataInicial +  "' and '" + dataFinal + " 23:59:59 ' ";
    return Consultas.contadorSQL(HQL, em);
  }
  
  public static int listarSemRespostaPorPeriodo(String dataInicial, String dataFinal) {
    String HQL= "SELECT COUNT(DISTINCT slt.idSolicitacao) " + 
        "FROM Solicitacao as slt " + 
        "INNER JOIN Mensagem as msg  " + 
        "ON slt.idSolicitacao = msg.solicitacao.idSolicitacao  " + 
        "WHERE slt.tipo = 'Informação' " + 
        "AND slt.dataIni BETWEEN '" + dataInicial +  "' and '" + dataFinal + " 23:59:59' " + 
        "AND (slt.status = 'Sem Resposta' " + 
        "OR (slt.status = 'Negada'  " + 
        "AND slt.idSolicitacao NOT IN ( " + 
        "SELECT DISTINCT slt2.idSolicitacao  " + 
        "FROM Solicitacao as slt2  " + 
        "INNER JOIN Mensagem as msg2 ON slt2.idSolicitacao = msg2.solicitacao.idSolicitacao " + 
        "WHERE msg2.tipo = 2 " + 
        "AND slt2.tipo = 'Informação' AND slt2.status != 'Sem Resposta')))";
    return Consultas.contadorSQL(HQL, em);
  }
  
  @SuppressWarnings("unchecked")
  public static int listarEmTramitePorPeriodo(String dataInicial, String dataFinal) {
    String HQL= "SELECT COUNT(DISTINCT slt.idSolicitacao) " + 
        "FROM Solicitacao as slt " + 
        "INNER JOIN Mensagem as msg  " + 
        "ON slt.idSolicitacao = msg.solicitacao.idSolicitacao  " + 
        "WHERE slt.tipo = 'Informação' " + 
        "AND slt.dataIni BETWEEN '" + dataInicial +  "' and '" + dataFinal + " 23:59:59' " + 
        "AND slt.visualizada = 1 " + 
        "AND slt.idSolicitacao NOT IN (SELECT DISTINCT slt2.idSolicitacao " + 
        "FROM Solicitacao as slt2 " + 
        "INNER JOIN Mensagem as msg2 " + 
        "ON slt2.idSolicitacao = msg2.solicitacao.idSolicitacao  " + 
        "WHERE msg2.tipo = 2 " + 
        "AND slt2.dataIni BETWEEN '" + dataInicial +  "' and '" + dataFinal + " 23:59:59' " + 
        "AND slt2.tipo = 'Informação' " + 
        "AND slt2.status != 'Sem Resposta') " + 
        "AND slt.status != 'Sem Resposta' " + 
        "AND slt.status != 'Negada' " +
        "AND slt.status != 'Finalizada'";
    return Consultas.contadorSQL(HQL, em);
  }
  
  @SuppressWarnings("unchecked")
  public static int listarNaoVisualizadasPorPeriodo(String dataInicial, String dataFinal) {
    String HQL = "SELECT COUNT(DISTINCT slt.idSolicitacao) " + 
        "FROM Solicitacao as slt  " + 
        "WHERE slt.tipo = 'Informação'  " + 
        "AND slt.dataIni BETWEEN '" + dataInicial +  "' and '" + dataFinal + " 23:59:59' " + 
        "AND slt.visualizada = 0 " + 
        "AND (slt.status = 'Aberta' OR slt.status = 'Transição')";
    return Consultas.contadorSQL(HQL, em);
  }
  
  // Consultas utilizadas exclusivamente para geração de gráficos por órgão
  
  @SuppressWarnings("unchecked")
  public static int listarTotalPorEntidade(int idEntidade, String dataInicial, String dataFinal) {
    String HQL = "SELECT COUNT(DISTINCT slt.idSolicitacao) FROM Solicitacao as slt "
          + "WHERE slt.tipo = 'Informação' "
          + "AND slt.dataIni BETWEEN '" + dataInicial +  "' and '" + dataFinal + " 23:59:59' "
          + "AND slt.entidades.idEntidades = " + idEntidade;
    return Consultas.contadorSQL(HQL, em);
  }
  
  @SuppressWarnings("unchecked")
  public static int listarAtendidasPorEntidade(int idEntidade, String dataInicial, String dataFinal) {
    String HQL= "SELECT COUNT(DISTINCT slt.idSolicitacao) " + 
        "FROM Solicitacao as slt " + 
        "INNER JOIN Mensagem as msg  " + 
        "ON slt.idSolicitacao = msg.solicitacao.idSolicitacao  " + 
        "WHERE ((msg.tipo = 2 " + 
        "AND slt.tipo = 'Informação' " + 
        "AND slt.status != 'Sem Resposta') " +
        "OR slt.status = 'Finalizada' ) " +
        "AND slt.dataIni BETWEEN '" + dataInicial +  "' and '" + dataFinal + " 23:59:59' " + 
        "AND slt.entidades.idEntidades = " + idEntidade + "";
    return Consultas.contadorSQL(HQL, em);
  }
  
  @SuppressWarnings("unchecked")
  public static int listarSemRespostaPorEntidade(int idEntidade, String dataInicial, String dataFinal) {
    String HQL= "SELECT COUNT(DISTINCT slt.idSolicitacao) " + 
        "FROM Solicitacao as slt " + 
        "INNER JOIN Mensagem as msg  " + 
        "ON slt.idSolicitacao = msg.solicitacao.idSolicitacao  " + 
        "WHERE slt.tipo = 'Informação' " + 
        "AND slt.dataIni BETWEEN '" + dataInicial +  "' and '" + dataFinal + " 23:59:59' " + 
        "AND (slt.status = 'Sem Resposta' " + 
        "OR (slt.status = 'Negada'  " + 
        "AND slt.idSolicitacao NOT IN ( " + 
        "SELECT DISTINCT slt2.idSolicitacao  " + 
        "FROM Solicitacao as slt2  " + 
        "INNER JOIN Mensagem as msg2 ON slt2.idSolicitacao = msg2.solicitacao.idSolicitacao " + 
        "WHERE msg2.tipo = 2  " + 
        "AND slt2.tipo = 'Informação' AND slt2.status != 'Sem Resposta')))" +
        "AND slt.entidades.idEntidades = " + idEntidade + "";
    return Consultas.contadorSQL(HQL, em);
  }
  
  @SuppressWarnings("unchecked")
  public static int listarEmTramitePorEntidade(int idEntidade, String dataInicial, String dataFinal) {
    String HQL= "SELECT COUNT(DISTINCT slt.idSolicitacao) " + 
        "FROM Solicitacao as slt " + 
        "INNER JOIN Mensagem as msg  " + 
        "ON slt.idSolicitacao = msg.solicitacao.idSolicitacao  " + 
        "WHERE slt.tipo = 'Informação' " + 
        "AND slt.dataIni BETWEEN '" + dataInicial +  "' and '" + dataFinal + " 23:59:59' " + 
        "AND slt.visualizada = 1 " + 
        "AND slt.idSolicitacao NOT IN (SELECT DISTINCT slt2.idSolicitacao " + 
        "FROM Solicitacao as slt2 " + 
        "INNER JOIN Mensagem as msg2 " + 
        "ON slt2.idSolicitacao = msg2.solicitacao.idSolicitacao  " + 
        "WHERE msg2.tipo = 2 " + 
        "AND slt2.dataIni BETWEEN '" + dataInicial +  "' and '" + dataFinal + " 23:59:59' " + 
        "AND slt2.tipo = 'Informação' " + 
        "AND slt2.status != 'Sem Resposta') " + 
        "AND slt.status != 'Sem Resposta' " + 
        "AND slt.status != 'Negada'" +
        "AND slt.status != 'Finalizada'" +
        "AND slt.entidades.idEntidades = " + idEntidade + "";
    return Consultas.contadorSQL(HQL, em);
  }
  
  @SuppressWarnings("unchecked")
  public static int listarNaoVisualizadasPorEntidade(int idEntidade, String dataInicial, String dataFinal) {
    String HQL = "SELECT COUNT(DISTINCT slt.idSolicitacao) " + 
        "FROM Solicitacao as slt  " + 
        "WHERE slt.tipo = 'Informação'  " + 
        "AND slt.dataIni BETWEEN '" + dataInicial +  "' and '" + dataFinal + " 23:59:59' " + 
        "AND slt.visualizada = 0 " + 
        "AND (slt.status = 'Aberta' OR slt.status = 'Transição')" +
        "AND slt.entidades.idEntidades = " + idEntidade + "";
    return Consultas.contadorSQL(HQL, em); 
  }
  
  @SuppressWarnings("unchecked")
  public static List<String> listarAssuntosPorEntidade(String tipo, int idEntidade, String dataInicial, String dataFinal ) {
    String HQL= "Select acoes.titulo from Acoes as acoes " +
        "INNER JOIN Solicitacao as solicitacao on solicitacao.acoes.idAcoes = acoes.idAcoes " +
        "WHERE solicitacao.dataIni BETWEEN '" + dataInicial +  "' and '" + dataFinal + " 23:59:59' "+
        "AND solicitacao.tipo = '"+tipo+"' " +
        "AND solicitacao.entidades.idEntidades = " + idEntidade;
    return (List<String>) Consultas.buscaPersonalizada(HQL, em); 
  }
  
  @SuppressWarnings("unchecked")
  public static List<Cidadao> listarCidadaoPorEntidade(String tipo, int idEntidade, String dataInicial, String dataFinal ) {
    String HQL= "SELECT solicitacao.cidadao FROM  Solicitacao as solicitacao "+
        "JOIN Cidadao as cidadao "+
        "ON solicitacao.cidadao.idCidadao = cidadao.idCidadao "+
        "WHERE solicitacao.dataIni BETWEEN '" + dataInicial +  "' and '" + dataFinal + " 23:59:59' "+
        "AND solicitacao.tipo = '"+tipo+"'" +
        "AND solicitacao.entidades.idEntidades = " + idEntidade;
    return (List<Cidadao>) Consultas.buscaPersonalizada(HQL, em); 
  }
  
  @SuppressWarnings("unchecked")
  public static List<String> listarPorFederacaoPorEntidade(String tipo, int idEntidade, String dataInicial, String dataFinal ) {
    String HQL= "SELECT solicitacao.cidadao.estado FROM  Solicitacao as solicitacao "+
          "JOIN  Cidadao as cidadao "+
          "ON solicitacao.cidadao.idCidadao = cidadao.idCidadao "+
          "WHERE solicitacao.dataIni BETWEEN '" + dataInicial +  "' and '" + dataFinal + " 23:59:59' "+
          "AND solicitacao.tipo = 'Informação'" +
          "AND solicitacao.entidades.idEntidades = " + idEntidade +
          "ORDER BY solicitacao.cidadao.estado ASC";
    return (List<String>) Consultas.buscaPersonalizada(HQL, em); 
  }
  
  // Consultas utilizadas para obter datas com solicitações realizadas
  
  @SuppressWarnings("unchecked")
  public static List<Integer> listarAnos() {
    String HQL = "SELECT DISTINCT EXTRACT(YEAR FROM sol.dataIni) FROM Solicitacao as sol ORDER BY EXTRACT(YEAR FROM sol.dataIni) ASC";
    return (List<Integer>) Consultas.buscaPersonalizada(HQL, em);
  }
  
  @SuppressWarnings("unchecked")
  public static List<Integer> listarMesesDoAno(int ano) {
    String HQL = "SELECT DISTINCT EXTRACT(MONTH FROM sol.dataIni) FROM Solicitacao as sol WHERE sol.dataIni LIKE '" + ano + "-%' ORDER BY EXTRACT(MONTH FROM sol.dataIni) ASC";
    return (List<Integer>) Consultas.buscaPersonalizada(HQL, em);
  }
  
  
  // Consultas do dashboard de administrador por tipo de solicitação
  public static int contarTotalPorTipo(String tipo) {
    String HQL = "SELECT COUNT(*) FROM Solicitacao as sol WHERE sol.tipo = '" + tipo + "'";
    return Consultas.contadorSQL(HQL, em);
  }
  
  public static int contarAtendidasPorTipo(String tipo) {
    String HQL= "SELECT COUNT(DISTINCT slt.idSolicitacao) " + 
        "FROM Solicitacao as slt " + 
        "INNER JOIN Mensagem as msg  " + 
        "ON slt.idSolicitacao = msg.solicitacao.idSolicitacao  " + 
        "WHERE ((msg.tipo = 2 " + 
        "AND slt.status != 'Sem Resposta') " +
        "OR slt.status = 'Finalizada') " +
        "AND slt.tipo = '" + tipo + "'";
    return Consultas.contadorSQL(HQL, em);
  }
  
  public static int contarSemRespostaPorTipo(String tipo) {
    String HQL= "SELECT COUNT(DISTINCT slt.idSolicitacao)" + 
        "FROM Solicitacao as slt " + 
        "INNER JOIN Mensagem as msg  " + 
        "ON slt.idSolicitacao = msg.solicitacao.idSolicitacao  " + 
        "WHERE slt.tipo = '" + tipo + "' " + 
        "AND (slt.status = 'Sem Resposta' " + 
        "OR (slt.status = 'Negada'  " + 
        "AND slt.idSolicitacao NOT IN ( " + 
        "SELECT DISTINCT slt2.idSolicitacao  " + 
        "FROM Solicitacao as slt2  " + 
        "INNER JOIN Mensagem as msg2 ON slt2.idSolicitacao = msg2.solicitacao.idSolicitacao " + 
        "WHERE msg2.tipo = 2  " + 
        "AND slt2.tipo = '" + tipo + "' AND slt2.status != 'Sem Resposta')))";
    return Consultas.contadorSQL(HQL, em);
  }
  
  public static int contarEmTramitePorTipo(String tipo) {
    String HQL= "SELECT COUNT(DISTINCT slt.idSolicitacao)" + 
        "FROM Solicitacao as slt " + 
        "INNER JOIN Mensagem as msg  " + 
        "ON slt.idSolicitacao = msg.solicitacao.idSolicitacao  " + 
        "WHERE slt.tipo = '" + tipo + "' " +
        "AND slt.visualizada = 1 " + 
        "AND slt.idSolicitacao NOT IN (SELECT DISTINCT slt2.idSolicitacao " + 
        "FROM Solicitacao as slt2 " + 
        "INNER JOIN Mensagem as msg2 " + 
        "ON slt2.idSolicitacao = msg2.solicitacao.idSolicitacao  " + 
        "WHERE msg2.tipo = 2 " + 
        "AND slt2.tipo = '" + tipo + "' " + 
        "AND slt2.status != 'Sem Resposta') " + 
        "AND slt.status != 'Sem Resposta' " + 
        "AND slt.status != 'Finalizada' " + 
        "AND slt.status != 'Negada'";
    return Consultas.contadorSQL(HQL, em);
  }
  
  public static int contarNaoVisualizadasPorTipo(String tipo) {
    String HQL = "SELECT COUNT(DISTINCT slt.idSolicitacao)" + 
        "FROM Solicitacao as slt  " + 
        "WHERE slt.tipo =  '" + tipo + "' " + 
        "AND slt.visualizada = 0 " + 
        "AND (slt.status = 'Aberta' OR slt.status = 'Transição')";
    return Consultas.contadorSQL(HQL, em);
  }
  
  // Consultas do dashboard de gestor por tipo de solicitação por orgao
    public static int contarPorEntidade(String tipo) {
      UsuarioBean usuarioBean = (UsuarioBean) HibernateUtil.RecuperarDaSessao("usuario");
      
      if(usuarioBean != null) {
        if(usuarioBean.getUsuario().getPerfil() == 2 || usuarioBean.getUsuario().getPerfil() == 4 && usuarioBean.isPerfilAlterarCidadaoResponsavel()) {
          List<Responsavel> ListResp = new ArrayList<Responsavel>(ResponsavelDAO.findResponsavelUsuario(usuarioBean.getUsuario().getIdUsuario()));
          
          String query = "SELECT COUNT(DISTINCT slt.idSolicitacao) FROM Solicitacao as slt ";
          
          if(tipo.equals("Denúncia")) {
            query += "WHERE (slt.tipo =  '" + tipo + "' AND slt.liberaDenuncia = 1)";
          } else {
            query += "WHERE slt.tipo =  '" + tipo + "' ";
          }
          
          query += " AND (";
              
          for (int i = 0; i < ListResp.size(); i++) {
            if (ListResp.get(i).isAtivo()) {
              if(query.contains("slt.entidades.idEntidades")) {
                query += " OR ";
              } 
              query += "slt.entidades.idEntidades = " + ListResp.get(i).getEntidades().getIdEntidades();
            }
          }
          
          query += ")";
          
          return Consultas.contadorSQL(query, em);
        }
      }
      
      return 0;     
      
    }
    
    public static int contarAtendidasPorEntidade(String tipo) {
      UsuarioBean usuarioBean = (UsuarioBean) HibernateUtil.RecuperarDaSessao("usuario");
      
      if(usuarioBean != null) {
        if(usuarioBean.getUsuario().getPerfil() == 2 || usuarioBean.getUsuario().getPerfil() == 4 && usuarioBean.isPerfilAlterarCidadaoResponsavel()) {
          List<Responsavel> ListResp = new ArrayList<Responsavel>(ResponsavelDAO.findResponsavelUsuario(usuarioBean.getUsuario().getIdUsuario()));
          
          String query= "SELECT COUNT(DISTINCT slt.idSolicitacao) " + 
              "FROM Solicitacao as slt " + 
              "INNER JOIN Mensagem as msg  " + 
              "ON slt.idSolicitacao = msg.solicitacao.idSolicitacao  " + 
              "WHERE ((msg.tipo = 2 " +
              "AND slt.status != 'Sem Resposta') " +
              "OR slt.status = 'Finalizada') " +
              "AND slt.tipo = '" + tipo + "'" +
              " AND (";
              
          for (int i = 0; i < ListResp.size(); i++) {
            if (ListResp.get(i).isAtivo()) {
              if(query.contains("slt.entidades.idEntidades")) {
                query += " OR ";
              }
              query += "slt.entidades.idEntidades = " + ListResp.get(i).getEntidades().getIdEntidades();
            }
          }
          
          
          query += ")";
          
          if(tipo.equals("Denúncia")) {
            query += "AND (slt.tipo = '" + tipo + "' AND slt.liberaDenuncia = 1)";
          } else {
            query += "AND slt.tipo = '" + tipo + "' ";
          }
          
          return Consultas.contadorSQL(query, em);
        }
      }
      
      return 0;     
      
    }
    
    public static int contarSemRespostaPorEntidade(String tipo) {
      UsuarioBean usuarioBean = (UsuarioBean) HibernateUtil.RecuperarDaSessao("usuario");
      
      if(usuarioBean != null) {
        if(usuarioBean.getUsuario().getPerfil() == 2 || usuarioBean.getUsuario().getPerfil() == 4 && usuarioBean.isPerfilAlterarCidadaoResponsavel()) {
          List<Responsavel> ListResp = new ArrayList<Responsavel>(ResponsavelDAO.findResponsavelUsuario(usuarioBean.getUsuario().getIdUsuario()));
          
          String query = "SELECT COUNT(DISTINCT slt.idSolicitacao)" + 
              "FROM Solicitacao as slt " + 
              "INNER JOIN Mensagem as msg  " + 
              "ON slt.idSolicitacao = msg.solicitacao.idSolicitacao  ";
              
          if(tipo.equals("Denúncia")) {
            query += "WHERE slt.tipo = '" + tipo + "'  AND slt.liberaDenuncia = 1";
          } else {
            query += "WHERE slt.tipo = '" + tipo + "' ";
          }
              
          query += "AND (slt.status = 'Sem Resposta' " + 
               "OR (slt.status = 'Negada'  " + 
               "AND slt.idSolicitacao NOT IN ( " + 
               "SELECT DISTINCT slt2.idSolicitacao  " + 
               "FROM Solicitacao as slt2  " + 
               "INNER JOIN Mensagem as msg2 ON slt2.idSolicitacao = msg2.solicitacao.idSolicitacao " +
               "WHERE msg2.tipo = 2  ";
               
           if(tipo.equals("Denúncia")) {
             query += "AND slt2.tipo = '" + tipo + "' AND slt2.status != 'Sem Resposta' AND slt.liberaDenuncia = 1)))";
           } else {
             query += "AND slt2.tipo = '" + tipo + "' AND slt2.status != 'Sem Resposta')))";
           }
               
           query += " AND (";
              
          for (int i = 0; i < ListResp.size(); i++) {
            if (ListResp.get(i).isAtivo()) {
              if(query.contains("slt.entidades.idEntidades")) {
                query += " OR ";
              }
              query += "slt.entidades.idEntidades = " + ListResp.get(i).getEntidades().getIdEntidades();
            }
          }
          
          query += ")";
          
          return Consultas.contadorSQL(query, em);
        }
      }
      
      return 0;
      
    }
    
    public static int contarEmTramitePorEntidade(String tipo) {
      UsuarioBean usuarioBean = (UsuarioBean) HibernateUtil.RecuperarDaSessao("usuario");
      
      if(usuarioBean != null) {
        if(usuarioBean.getUsuario().getPerfil() == 2 || usuarioBean.getUsuario().getPerfil() == 4 && usuarioBean.isPerfilAlterarCidadaoResponsavel()) {
          List<Responsavel> ListResp = new ArrayList<Responsavel>(ResponsavelDAO.findResponsavelUsuario(usuarioBean.getUsuario().getIdUsuario()));
          
          String query = "SELECT COUNT(DISTINCT slt.idSolicitacao)" + 
              "FROM Solicitacao as slt " + 
              "INNER JOIN Mensagem as msg  " + 
              "ON slt.idSolicitacao = msg.solicitacao.idSolicitacao  "; 
              
          if(tipo.equals("Denúncia")) {
            query += "WHERE (slt.tipo = '" + tipo + "' AND slt.liberaDenuncia = 1)";
          } else {
            query += "WHERE slt.tipo = '" + tipo + "' ";
          }
          
          query += "AND slt.visualizada = 1 " + 
               "AND slt.idSolicitacao NOT IN (SELECT DISTINCT slt2.idSolicitacao " + 
               "FROM Solicitacao as slt2 " + 
               "INNER JOIN Mensagem as msg2 " + 
               "ON slt2.idSolicitacao = msg2.solicitacao.idSolicitacao  " + 
               "WHERE msg2.tipo = 2 ";
          
          if(tipo.equals("Denúncia")) {
            query += "AND (slt2.tipo = '" + tipo + "' AND slt2.liberaDenuncia = 1)";
          } else {
            query += "AND slt2.tipo = '" + tipo + "' ";
          }
          
          
          query += "AND slt2.status != 'Sem Resposta') " +
               "AND slt.status != 'Sem Resposta' " +
               "AND slt.status != 'Finalizada' " +
               "AND slt.status != 'Negada'" +
               " AND (";
              
          for (int i = 0; i < ListResp.size(); i++) {
            if (ListResp.get(i).isAtivo()) {
              if(query.contains("slt.entidades.idEntidades")) {
                query += " OR ";
              }
              query += "slt.entidades.idEntidades = " + ListResp.get(i).getEntidades().getIdEntidades();
            }
          }
          
          query += ")";
          
          return Consultas.contadorSQL(query, em);
        }
      }
      
      return 0;
    }
    
    public static int contarNaoVisualizadasPorEntidade(String tipo) {
      UsuarioBean usuarioBean = (UsuarioBean) HibernateUtil.RecuperarDaSessao("usuario");
      
      if(usuarioBean != null) {
        if(usuarioBean.getUsuario().getPerfil() == 2 || usuarioBean.getUsuario().getPerfil() == 4 && usuarioBean.isPerfilAlterarCidadaoResponsavel()) {
          List<Responsavel> ListResp = new ArrayList<Responsavel>(ResponsavelDAO.findResponsavelUsuario(usuarioBean.getUsuario().getIdUsuario()));
          
          String query = "SELECT COUNT(DISTINCT slt.idSolicitacao)" + 
              "FROM Solicitacao as slt  ";
          
          if(tipo.equals("Denúncia")) {
            query += "WHERE slt.tipo =  '" + tipo + "' AND slt.liberaDenuncia = 1";
          } else {
            query += "WHERE slt.tipo =  '" + tipo + "' ";
          }
          
          query += "AND slt.visualizada = 0 " + 
               "AND (slt.status = 'Aberta' OR slt.status = 'Transição')" +
               " AND (";
              
          for (int i = 0; i < ListResp.size(); i++) {
            if (ListResp.get(i).isAtivo()) {
              if(query.contains("slt.entidades.idEntidades")) {
                query += " OR ";
              }
              query += "slt.entidades.idEntidades = " + ListResp.get(i).getEntidades().getIdEntidades();
            }
          }
          
          query += ")";
          
          return Consultas.contadorSQL(query, em);
        }
      }
      
      return 0;
    }
    
    // Consultas do dashboard de cidadao
    public static int contarTotalDoCidadao() {
      UsuarioBean usuarioBean = (UsuarioBean) HibernateUtil.RecuperarDaSessao("usuario");
      Cidadao cid = CidadaoDAO.findCidadaoUsuario(usuarioBean.getUsuario().getIdUsuario());
      
      if(usuarioBean != null) {
        String HQL = "SELECT COUNT(*) FROM Solicitacao as sol WHERE sol.cidadao.idCidadao = '" + cid.getIdCidadao() + "'";
        return Consultas.contadorSQL(HQL, em);
      }
      return 0;
    }
    public static int contarAtendidasDoCidadao() {
      UsuarioBean usuarioBean = (UsuarioBean) HibernateUtil.RecuperarDaSessao("usuario");
      Cidadao cid = CidadaoDAO.findCidadaoUsuario(usuarioBean.getUsuario().getIdUsuario());
      
      if(usuarioBean != null) {
        String HQL= "SELECT COUNT(DISTINCT slt.idSolicitacao) " + 
            "FROM Solicitacao as slt " + 
            "INNER JOIN Mensagem as msg  " + 
            "ON slt.idSolicitacao = msg.solicitacao.idSolicitacao  " + 
            "WHERE msg.tipo = 2 " + 
            "AND slt.cidadao.idCidadao = '" + cid.getIdCidadao() + "' " +
            "AND slt.status != 'Sem Resposta'";
        return Consultas.contadorSQL(HQL, em);
      }
      return 0;
    }
    public static int contarSemRespostaDoCidadao() {
      UsuarioBean usuarioBean = (UsuarioBean) HibernateUtil.RecuperarDaSessao("usuario");
      Cidadao cid = CidadaoDAO.findCidadaoUsuario(usuarioBean.getUsuario().getIdUsuario());
      
      if(usuarioBean != null) {
        String HQL= "SELECT COUNT(DISTINCT slt.idSolicitacao)" + 
            "FROM Solicitacao as slt " + 
            "INNER JOIN Mensagem as msg  " + 
            "ON slt.idSolicitacao = msg.solicitacao.idSolicitacao  " + 
            "WHERE slt.cidadao.idCidadao = '" + cid.getIdCidadao() + "' " +
            "AND (slt.status = 'Sem Resposta' " + 
            "OR (slt.status = 'Negada'  " + 
            "AND slt.idSolicitacao NOT IN ( " + 
            "SELECT DISTINCT slt2.idSolicitacao  " + 
            "FROM Solicitacao as slt2  " + 
            "INNER JOIN Mensagem as msg2 ON slt2.idSolicitacao = msg2.solicitacao.idSolicitacao " + 
            "WHERE msg2.tipo = 2  " +
            "AND slt2.cidadao.idCidadao = '" + cid.getIdCidadao() + "' " +
            "AND slt2.status != 'Sem Resposta')))";
        return Consultas.contadorSQL(HQL, em);
      }
      return 0;
    }
    
    public static int contarEmTramiteDoCidadao() {
      UsuarioBean usuarioBean = (UsuarioBean) HibernateUtil.RecuperarDaSessao("usuario");
      Cidadao cid = CidadaoDAO.findCidadaoUsuario(usuarioBean.getUsuario().getIdUsuario());
      
      if(usuarioBean != null) {
        String HQL= "SELECT COUNT(DISTINCT slt.idSolicitacao)" + 
            "FROM Solicitacao as slt " + 
            "INNER JOIN Mensagem as msg  " + 
            "ON slt.idSolicitacao = msg.solicitacao.idSolicitacao  " + 
            "WHERE slt.cidadao.idCidadao = '" + cid.getIdCidadao() + "' " +
            "AND slt.visualizada = 1 " + 
            "AND slt.idSolicitacao NOT IN (SELECT DISTINCT slt2.idSolicitacao " + 
            "FROM Solicitacao as slt2 " + 
            "INNER JOIN Mensagem as msg2 " + 
            "ON slt2.idSolicitacao = msg2.solicitacao.idSolicitacao  " + 
            "WHERE msg2.tipo = 2 " + 
            "AND slt2.cidadao.idCidadao = '" + cid.getIdCidadao() + "' " +
            "AND slt2.status != 'Sem Resposta') " + 
            "AND slt.status != 'Sem Resposta' " + 
            "AND slt.status != 'Negada'";
        return Consultas.contadorSQL(HQL, em);
      }
      return 0;
    }
    
    public static int contarNaoVisualizadaDoCidadao() {
      UsuarioBean usuarioBean = (UsuarioBean) HibernateUtil.RecuperarDaSessao("usuario");
      Cidadao cid = CidadaoDAO.findCidadaoUsuario(usuarioBean.getUsuario().getIdUsuario());
      
      if(usuarioBean != null) {
        String HQL = "SELECT COUNT(DISTINCT slt.idSolicitacao)" + 
            "FROM Solicitacao as slt  " + 
            "WHERE slt.cidadao.idCidadao = '" + cid.getIdCidadao() + "' " +
            "AND slt.visualizada = 0 " + 
            "AND (slt.status = 'Aberta' OR slt.status = 'Transição')";
        return Consultas.contadorSQL(HQL, em);
      }
      return 0;
    }
    
    @SuppressWarnings("unchecked")
    public static List<Solicitacao> listPorProtocolo(String protocolo) {
      String HQL = "FROM Solicitacao as slt where replace(replace(slt.protocolo,'-',''),'/','') like replace(replace('%"+ protocolo +"','-',''),'/','')";
      return (List<Solicitacao>) Consultas.buscaPersonalizada(HQL,em);
    }
  
}
