package br.gov.se.lai.Bean;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import br.gov.se.lai.DAO.AcoesDAO;
import br.gov.se.lai.DAO.AnexoDAO;
import br.gov.se.lai.DAO.EntidadesDAO;
import br.gov.se.lai.DAO.MensagemDAO;
import br.gov.se.lai.DAO.SolicitacaoDAO;
import br.gov.se.lai.entity.Acoes;
import br.gov.se.lai.entity.Anexo;
import br.gov.se.lai.entity.Cidadao;
import br.gov.se.lai.entity.Entidades;
import br.gov.se.lai.entity.Mensagem;
import br.gov.se.lai.entity.Solicitacao;
import br.gov.se.lai.utils.HibernateUtil;
import br.gov.se.lai.utils.NotificacaoEmail;



@ManagedBean(name = "solicitacao")
@SessionScoped
@SuppressWarnings("unused")
public class SolicitacaoBean implements Serializable{
	

	private List<Solicitacao> solicitacoes;
	private int idAcao;
	private List<Solicitacao> filteredSolicitacoes;
	private static final long serialVersionUID = -9191715805520708190L;
	private Solicitacao solicitacao;
	private Anexo anexo ;
	private Cidadao cidadao;;
	private List<Entidades> entidades;
	private Calendar datainic;
	private String status;
	private Calendar datafim;
	private int idEntidades;
	private int idSolicitacao;
	private Mensagem mensagem;	
	private UploadedFile file;
	private Acoes acoesTemporaria;	
	private final static int constanteTempo = 20;
	private final static int constanteAdicionalTempo = 10;
	private final static int constanteDeRecurso = 2;
	private final static String[] tipos = {"Aberta", "Respondida", "Prorrogada", "Recurso", "Finalizada"};

	@PostConstruct
	public void init() {
		this.solicitacao = new Solicitacao();
		this.mensagem = new Mensagem();
		this.cidadao = new Cidadao();
		this.anexo = new Anexo();		
		this.entidades = new ArrayList<Entidades>(EntidadesDAO.list());
	}
	

	public String save() {
		UsuarioBean usuarioBean = (UsuarioBean) HibernateUtil.RecuperarDaSessao("usuario");		
		List<Cidadao> listCidadao = new ArrayList<Cidadao>(usuarioBean.getUsuario().getCidadaos());	
		
		//Salvar Solicitação
		this.solicitacao.setCidadao(listCidadao.get(0));	
		this.solicitacao.setAcoes(getAcoesTemporaria());
		this.solicitacao.setDataIni(new Date(System.currentTimeMillis()));	
		this.solicitacao.setDataLimite(java.sql.Date.valueOf(LocalDate.now().plusDays(constanteTempo)));
		this.solicitacao.setStatus("Aberta");
		this.solicitacao.setInstancia((short) 1);
		SolicitacaoDAO.saveOrUpdate(solicitacao);
		
		
		//Salvar Mensagem
		this.mensagem.setUsuario(usuarioBean.getUsuario()); 
		this.mensagem.setData(new Date(System.currentTimeMillis()));	
		this.mensagem.setSolicitacao(solicitacao);
		this.mensagem.setTipo((short)1);
		MensagemDAO.saveOrUpdate(mensagem);
		
		if (file != null) {
			AnexoBean anx = new AnexoBean();
			anx.save(anexo, mensagem,file);
		}
        
		//Salvar Alteração de Status
		//MensagemBean.salvarStatus(solicitacao, solicitacao.getStatus());
		
		//verifique se não está ocorrendo erro
		//NotificacaoEmail.enviarEmail(solicitacao, usuarioBean.getUsuario());
		
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
		if(solicitacao != null) {
			solicitacao.setStatus(status);
			solicitacao.setDataLimite(java.sql.Date.valueOf(Instant.ofEpochMilli(solicitacao.getDataLimite().getTime()).atZone(ZoneId.systemDefault()).toLocalDate().plusDays(prazoResposta(solicitacao.getStatus()))));
			SolicitacaoDAO.saveOrUpdate(solicitacao);
			MensagemBean.salvarStatus(solicitacao, solicitacao.getStatus());
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
			short novaInstancia = (short) (solicitacao.getInstancia() + 1);
			solicitacao.setInstancia(novaInstancia);
			alterarPrazo(solicitacao);
		}else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Não está disponível para Recurso.",null));
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


	public Anexo getAnexo() {
		return anexo;
	}


	public void setAnexo(Anexo anexo) {
		this.anexo = anexo;
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


//	public int getIdAcao() {
//		return idAcao;
//	}
//
//
	public void setIdAcao(int idAcao) {
		setAcoesTemporaria(idAcao);
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
	
	public static String[] getTipos() {
		return tipos;
	}


	public Acoes getAcoesTemporaria() {
		return acoesTemporaria;
	}


	public void setAcoesTemporaria(int idAcao) {
		this.acoesTemporaria = AcoesDAO.findAcoes(idAcao);
	}
	
	public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}
	

}