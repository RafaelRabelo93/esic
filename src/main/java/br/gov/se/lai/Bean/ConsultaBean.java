package br.gov.se.lai.Bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ManagedBean;

import br.gov.se.lai.DAO.SolicitacaoDAO;
import br.gov.se.lai.entity.Solicitacao;
import br.gov.se.lai.utils.HibernateUtil;
 
@ManagedBean(name = "consultas")
@SessionScoped
public class ConsultaBean implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1139941339777680358L;
	private List<Solicitacao> solicitacoes;
	private Solicitacao solicitacao;	
         
	@PostConstruct
    public void init() {
		System.out.println("Init ConsultasBean");
		this.solicitacoes = new ArrayList<Solicitacao>(SolicitacaoDAO.list((UsuarioBean) HibernateUtil.RecuperarDaSessao("usuario")));
		this.solicitacao = new Solicitacao();
    }
	
	public void linkSolicitacao(){		
		org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('detalheDialog').show();");
	}

//GETTERS E SETTERS ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	
	
	public Solicitacao getSolicitacao() {
		return solicitacao;
	}

	public void setSolicitacao(Solicitacao solicitacao) {
		this.solicitacao = solicitacao;
	}

	public List<Solicitacao> getSolicitacoes() {
		return solicitacoes;
	}

	public void setSolicitacoes(List<Solicitacao> solicitacoes) {
		this.solicitacoes = solicitacoes;
	}
     
}