package br.gov.se.lai.Bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

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
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9191715805520708190L;
	private Solicitacao solicitacao;
	private Cidadao cidadao;;
	private List<Entidades> entidades;
	private Calendar datainic;
	private String status;
	private Calendar datafim;
	private int idEntidades;
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
		
		//Salvar Solicitação
		this.solicitacao.setCidadao(listCidadao.get(0));		
		this.solicitacao.setEntidades(EntidadesDAO.find(3));		
		this.solicitacao.setDataIni(new Date(System.currentTimeMillis()));					
		SolicitacaoDAO.saveOrUpdate(solicitacao);
		
		//Salvar Mensagem
		this.mensagem.setUsuario(usuarioBean.getUsuario()); 
		this.mensagem.setData(new Date(System.currentTimeMillis()));	
		this.mensagem.setSolicitacao(solicitacao);
		MensagemDAO.saveOrUpdate(mensagem);
		
		return "teste_redirecionamento";
	}
	
	public String delete() {

		return "usuario";
	}
	
	public String edit() {

		return "usuario";
	}
	
	public String login() {
		return "index";
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
	
	
}