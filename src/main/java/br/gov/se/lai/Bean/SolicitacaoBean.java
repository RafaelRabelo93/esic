package br.gov.se.lai.Bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import br.gov.se.lai.DAO.AcoesDAO;
import br.gov.se.lai.DAO.EntidadesDAO;
import br.gov.se.lai.DAO.MensagemDAO;
import br.gov.se.lai.DAO.SolicitacaoDAO;
import br.gov.se.lai.entity.Cidadao;
import br.gov.se.lai.entity.Entidades;
import br.gov.se.lai.entity.Mensagem;
import br.gov.se.lai.entity.Solicitacao;
import br.gov.se.lai.utils.HibernateUtil;



@ManagedBean(name = "solicitacao")
@SessionScoped
public class SolicitacaoBean implements Serializable{
	

	private static final long serialVersionUID = -9191715805520708190L;
	private Solicitacao solicitacao;
	private List<Solicitacao> solicitacoes;
	private List<Solicitacao> filteredSolicitacoes;
	private Cidadao cidadao;;
	private List<Entidades> entidades;
	private Calendar datainic;
	private String status;
	private Calendar datafim;
	private int idEntidades;
	private int idAcao;
	private int idSolicitacao;
	private Mensagem mensagem;	

	@PostConstruct
	public void init() {
		this.solicitacao = new Solicitacao();
		this.mensagem = new Mensagem();
		this.cidadao = new Cidadao();
		this.entidades = new ArrayList<Entidades>(EntidadesDAO.list());
	
	}
	

	public String save() {
		UsuarioBean usuarioBean = (UsuarioBean) HibernateUtil.RecuperarDaSessao("usuario");		
		List<Cidadao> listCidadao = new ArrayList<Cidadao>(usuarioBean.getUsuario().getCidadaos());	
		
		//Salvar Solicita��o
		this.solicitacao.setCidadao(listCidadao.get(0));		
		//this.solicitacao.setEntidades(EntidadesDAO.find(2));	
		this.solicitacao.setAcoes(AcoesDAO.findAcoes(idAcao));
		this.solicitacao.setDataIni(new Date(System.currentTimeMillis()));	
		this.solicitacao.setStatus("aberto");
		SolicitacaoDAO.saveOrUpdate(solicitacao);
		
		//Salvar Mensagem
		this.mensagem.setUsuario(usuarioBean.getUsuario()); 
		this.mensagem.setData(new Date(System.currentTimeMillis()));	
		this.mensagem.setSolicitacao(solicitacao);
		MensagemDAO.saveOrUpdate(mensagem);
		
		return "/index";
	}	
	public void finalizarSolicitacao() {
		this.solicitacao = SolicitacaoDAO.findSolicitacao(idSolicitacao);
		this.solicitacao.setStatus("finalizada");
		this.solicitacao.setDatafim(new Date(System.currentTimeMillis()));
		SolicitacaoDAO.saveOrUpdate(solicitacao);
	}
	
	public String verificaCidadao() {
		UsuarioBean usuarioBean = (UsuarioBean) HibernateUtil.RecuperarDaSessao("usuario");
		List<Cidadao> listCidadao = new ArrayList<Cidadao>(usuarioBean.getUsuario().getCidadaos());	
		if(usuarioBean.getUsuario().getIdUsuario() == null) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Us�rio inv�lido.", "Realize cadastro."));
			usuarioBean.setVeioDeSolicitacao(1);
			return "pages/cad_usuario";
		}else {
			if(usuarioBean.getUsuario().getPerfil() == 0 || usuarioBean.getUsuario().getPerfil() != 2 ) {
				if((listCidadao.isEmpty()) && (usuarioBean.getUsuario().getPerfil() == 0)) {
					return "pages/cad_cidadao";
				}else{
					return "pages/questionario1" ;
				}
			}else {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Uus�rio sem permiss�o.", "Tente outro login."));
				return null;
			}
		}	
	}
	
	
//GETTERS E SETTERS ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	
	
	
	public Solicitacao getSolicitacao() {
		return solicitacao;
	}

	public void setSolicitacao(Solicitacao solicitacao) {
		this.solicitacao = solicitacao;
	}

	public List<Entidades> getEntidades() {
		return entidades;
	}

	public void setEntidades(List<Entidades> entidades) {
		this.entidades = entidades;
	}

	public Calendar getDatainic() {
		return datainic;
	}

	public void setDatainic(Calendar date) {
		this.datainic = date;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Calendar getDatafim() {
		return datafim;
	}

	public void setDatafim(Calendar datafim) {
		this.datafim = datafim;
	}

	public int getIdEntidades() {
		return idEntidades;
	}

	public void setIdEntidades(int idEntidades) {
		this.idEntidades = idEntidades;
	}

	public Cidadao getCidadao() {
		return cidadao;
	}

	public void setCidadao(Cidadao cidadao) {
		this.cidadao = cidadao;
	}


	public Mensagem getMensagem() {
		return mensagem;
	}


	public void setMensagem(Mensagem mensagem) {
		this.mensagem = mensagem;
	}


	public List<Solicitacao> getSolicitacoes() {
		return SolicitacaoDAO.list();
	}


	@SuppressWarnings("unchecked")
	public Set<Mensagem> getMensagems() {
		return (Set<Mensagem>) MensagemDAO.list(getIdEntidades()) ;
	}


	public int getIdAcao() {
		return idAcao;
	}


	public void setIdAcao(int idAcao) {
		this.idAcao = idAcao;
	}


	public void setSolicitacoes(List<Solicitacao> solicitacoes) {
		this.solicitacoes = solicitacoes;
	}


	public int getIdSolicitacao() {
		return idSolicitacao;
	}


	public void setIdSolicitacao(int idSolicitacao) {
		this.idSolicitacao = idSolicitacao;
	}


	public List<Solicitacao> getFilteredSolicitacoes() {
		return filteredSolicitacoes;
	}


	public void setFilteredSolicitacoes(List<Solicitacao> filteredSolicitacoes) {
		this.filteredSolicitacoes = filteredSolicitacoes;
	}
	
	
	
}