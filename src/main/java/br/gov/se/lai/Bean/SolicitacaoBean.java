package br.gov.se.lai.Bean;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
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
import javax.faces.event.AjaxBehaviorEvent;

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
	private final static int constanteTempo = 20;
	private final static int constanteAdicionalTempo = 10;
	private final static int constanteDeRecurso = 2;

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
		this.solicitacao.setAcoes(AcoesDAO.findAcoes(idAcao));
		this.solicitacao.setDataIni(new Date(System.currentTimeMillis()));	
		this.solicitacao.setDataLimite(java.sql.Date.valueOf(LocalDate.now().plusDays(constanteTempo)));
		this.solicitacao.setStatus("Aberta");
		SolicitacaoDAO.saveOrUpdate(solicitacao);
		
		
		//Salvar Mensagem
		this.mensagem.setUsuario(usuarioBean.getUsuario()); 
		this.mensagem.setData(java.sql.Date.valueOf(LocalDate.now()));	
		this.mensagem.setSolicitacao(solicitacao);
		this.mensagem.setTipo((short)1);
		MensagemDAO.saveOrUpdate(mensagem);
		
		//Salvar Alteração de Status
		//MensagemBean.salvarStatus(solicitacao, solicitacao.getStatus());
		
		return "/index";
	}	
	
	
	public String verificaCidadaoSolicitacao() {
		UsuarioBean usuarioBean = (UsuarioBean) HibernateUtil.RecuperarDaSessao("usuario");
		List<Cidadao> listCidadao = new ArrayList<Cidadao>(usuarioBean.getUsuario().getCidadaos());	
		
		if(usuarioBean.getUsuario().getPerfil() == 0) {
			//se não tiver cadastro de usuario, vai cadastrar primeiro
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Usário inválido.", "Realize cadastro."));
			usuarioBean.setVeioDeSolicitacao(1);
			return "pages/cad_usuario";
		} else {
				if (usuarioBean.getUsuario().getPerfil() == 1 || usuarioBean.getUsuario().getPerfil() != 2) {
					//verifico se há a instancia de um usuario e se este usuario não é um responsável
					
					if ((listCidadao.isEmpty()) && (usuarioBean.getUsuario().getPerfil() == 1)) {
						//se tiver cadastro de usuario mas não tiver de cidadão, primeiro precisa cadastrar cidadão
						return "pages/cad_cidadao";
					} else {
						//se já for cadastrado usuario e cidadão inicia solicitacao
						return "pages/questionario1";
					}
				} else {
					//Se for um responsável não tem autorização para solicitar
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Usuário sem permissão.", "Tente outro login."));
					return null;
				}
			}	
	}
	
	public String verificaCidadaoConsulta() {
		UsuarioBean usuarioBean = (UsuarioBean) HibernateUtil.RecuperarDaSessao("usuario");
		if(usuarioBean.getUsuario().getPerfil() == 3) {
			return "pages/consulta";
		}else {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Uusário sem permissão.", "Tente outro login."));
				return null;
			}
	}	

	public void listPersonalizada(AjaxBehaviorEvent e){
		if(status == "Todas") {
			filteredSolicitacoes = SolicitacaoDAO.list();
		}else {
			filteredSolicitacoes = SolicitacaoDAO.listPersonalizada(status);
		}
	}

	
	
///////// Verificação dos Status em relação ao Tempo
	
	
	private void alterarPrazo(Solicitacao solicitacao) {
		try {
			solicitacao.setStatus(status);
			solicitacao.setDataLimite(java.sql.Date.valueOf(Instant.ofEpochMilli(solicitacao.getDataLimite().getTime()).atZone(ZoneId.systemDefault()).toLocalDate().plusDays(prazoResposta(solicitacao.getStatus()))));
			SolicitacaoDAO.saveOrUpdate(solicitacao);
			MensagemBean.salvarStatus(solicitacao, solicitacao.getStatus());
		} catch (NullPointerException e) {
			System.out.println(e.getMessage());
		}	
	}
	
	
	public void prorrogar() {
		if(!verificaSeProrrogada(solicitacao)) {
			alterarPrazo(solicitacao);		
		}else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Não está disponível para Prorrogação.",null));				
		}
	}
	
	

	public void recurso() {
		if(!verificaSeLimiteRecurso(solicitacao)) {			
			alterarPrazo(solicitacao);
		}else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Não está disponível para Recurso.",null));
		}
	}

	
	public static int prazoResposta(String status) {
		switch (status) {
		case "Aberta":
			return constanteAdicionalTempo;
		case "Prorrogada":
			return constanteAdicionalTempo;
		case "Resposta":
			return constanteAdicionalTempo;
		case "Recurso":
			return 5;
		default:
			return constanteTempo;
		}
	}

	@SuppressWarnings("unused")
	private void verificaTempoSolicitacao() {
		Date now = new Date();
		for (Solicitacao solicitacao : filteredSolicitacoes) {
			try {
				if(!solicitacao.getStatus().equals("Finalizada")) {
					if(now.after(solicitacao.getDataLimite())) {
						solicitacao.setDatafim(new Date(System.currentTimeMillis()));
						solicitacao.setStatus("Finalizada");
						SolicitacaoDAO.saveOrUpdate(solicitacao);
						MensagemBean.salvarStatus(solicitacao, solicitacao.getStatus());
					}
				}
			}catch (NullPointerException e) {
				System.out.println(e.getMessage());
			}
			
		} 
	} 
	
	private boolean verificaSeProrrogada(Solicitacao solicitacao) {
		boolean retorno = false;
		List<Mensagem> msgs = new ArrayList<>(solicitacao.getMensagems());
		for (Mensagem mensagem : msgs) {
			if (mensagem.getTipo().equals((short)4)){
				retorno = true;
				break;
			}
		}
		return retorno;
	}
	
	private boolean verificaSeLimiteRecurso(Solicitacao solicitacao) {
		int cont = 0;
		boolean retorno = false;
		List<Mensagem> msgs = new ArrayList<>(solicitacao.getMensagems());
		for (Mensagem mensagem : msgs) {
			if(mensagem.getTipo().equals((short)3 )){
				cont++;
				if(cont == constanteDeRecurso) {
					retorno = true;
					break;
				}
			}
		}
		
		return retorno;
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
		this.filteredSolicitacoes =  new ArrayList<Solicitacao>(SolicitacaoDAO.list());
		return filteredSolicitacoes;
	}


	public void setFilteredSolicitacoes(List<Solicitacao> filteredSolicitacoes) {
		this.filteredSolicitacoes = filteredSolicitacoes;
	}
	
	
}