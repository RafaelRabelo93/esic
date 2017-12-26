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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.behavior.AjaxBehavior;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;

import org.apache.commons.fileupload.RequestContext;
import org.omg.CORBA.Request;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
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
import br.gov.se.lai.entity.Usuario;
import br.gov.se.lai.utils.HibernateUtil;
import br.gov.se.lai.utils.NotificacaoEmail;

@ManagedBean(name = "solicitacao")
@SessionScoped
@SuppressWarnings("unused")
public class SolicitacaoBean implements Serializable {

	
	
	private List<Solicitacao> solicitacoes;
	private int idAcao;
	private List<Solicitacao> filteredSolicitacoes;
	private static List<Mensagem> mensagensSolicitacao;
	private static final long serialVersionUID = -9191715805520708190L;
	private Solicitacao solicitacao;
	private UsuarioBean userBean;
	private Anexo anexo;
	private Cidadao cidadao;
	private List<Entidades> entidades;
	private Calendar datainic;
	private String status;
	private Calendar datafim;
	private int idEntidades;
	private int formaRecebimento;
	private int idSolicitacao;
	private Mensagem mensagem;
	private UploadedFile file;
	private Acoes acoesTemporaria;
	private final static int constanteTempo = 20;
	private final static int constanteAdicionalTempo = 10;
	private final static int constanteDeRecurso = 2;
	private final static String[] tipos = { "Aberta", "Respondida", "Prorrogada", "Recurso", "Finalizada" };

	
	
	
	@PostConstruct
	public void init() {
		this.solicitacao = new Solicitacao();
		this.mensagem = new Mensagem();
		this.cidadao = new Cidadao();
		this.anexo = new Anexo();
		this.entidades = new ArrayList<Entidades>(EntidadesDAO.list());
		mensagensSolicitacao = new ArrayList<Mensagem>();
		this.userBean = (UsuarioBean) HibernateUtil.RecuperarDaSessao("usuario");
	}

	public String save() {
		List<Cidadao> listCidadao = new ArrayList<Cidadao>(userBean.getUsuario().getCidadaos());
		
		if (solicitacao.getTipo().equals("Sugestao") || solicitacao.getTipo().equals("elogio")) {
			this.solicitacao.setDataLimite(new Date(System.currentTimeMillis()));
			this.solicitacao.setDatafim(new Date(System.currentTimeMillis()));
			this.solicitacao.setStatus("Finalizada");
			
		}else {
			if(LocalDate.now().getDayOfWeek().name().toLowerCase().equals("friday")) {
				this.solicitacao.setDataLimite(java.sql.Date.valueOf(LocalDate.now().plusDays(constanteTempo+3)));
				this.solicitacao.setStatus("Aberta");
			}
			
		}

		// Salvar Solicitação
		this.solicitacao.setCidadao(listCidadao.get(0));
		this.solicitacao.setAcoes(getAcoesTemporaria());
		this.solicitacao.setDataIni(new Date(System.currentTimeMillis()));
		this.solicitacao.setInstancia((short) 1);
		
		if (SolicitacaoDAO.saveOrUpdate(solicitacao)) {

			// Salvar Mensagem
			this.mensagem.setUsuario(userBean.getUsuario());
			this.mensagem.setData(new Date(System.currentTimeMillis()));
			this.mensagem.setSolicitacao(solicitacao);
			this.mensagem.setTipo((short) 1);
			if (MensagemDAO.saveOrUpdate(mensagem)) {

				if (!(file.getContents().length == 0)) {
					AnexoBean anx = new AnexoBean();
					anx.save(anexo, mensagem, file);
				}
				mensagensSolicitacao.add(mensagem);
				NotificacaoEmail.enviarNotificacao(solicitacao,userBean.getUsuario());
				enviarMensagemAutomatica();
			}

		}

		this.solicitacao = new Solicitacao();
		this.mensagem = new Mensagem();
		CompetenciasBean.listCompetencias = null;
		CompetenciasBean.listEntidades = null;
		return "/index";
	}
	

	public String verificaCidadaoSolicitacao() {
		List<Cidadao> listCidadao = new ArrayList<Cidadao>(userBean.getUsuario().getCidadaos());

		if (userBean.getUsuario().getPerfil() == 0) {
			// se não tiver cadastro de usuario, vai cadastrar primeiro
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Usário inválido.", "Realize cadastro."));
			userBean.setVeioDeSolicitacao(1);
			return "Cadastro/cad_usuario";
		} else {
			if (userBean.getUsuario().getPerfil() == 1 || userBean.getUsuario().getPerfil() != 2) {
				// verifico se há a instancia de um usuario e se este usuario não é um
				// responsável

				if ((listCidadao.isEmpty()) && (userBean.getUsuario().getPerfil() == 1)) {
					// se tiver cadastro de usuario mas não tiver de cidadão, primeiro precisa
					// cadastrar cidadão
					return "Cadastro/cad_cidadao";
				} else {
					// se já for cadastrado usuario e cidadão inicia solicitacao
					return "Solicitacao/questionario1";
				}
			} else {
				// Se for um responsável não tem autorização para solicitar
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Usuário sem permissão.", "Tente outro login."));
				return null;
			}
		}
	}

	public void enviarMensagemAutomatica() {
		NotificacaoEmail.enviarEmailAutomatico(solicitacao, "Mensagem Automática", solicitacao.getTipo()+" recebido com sucesso.");
	}
	
	/*
	 * Métodos relacionados a consulta de solicitações 
	 * 
	 * verificaCidadaoConsulta(): String - retorna a página com a lista populada de solicitações relacionadas ao cidadão.
	 * consultarSolicitacao() : String - Filtra a lista de solicitações de acordo com a entidade passada como parâmetro da tela para o bean.
	 * listPersonalizada(AjaxBehaviorEvent e) : void - filtra lista de solicitações com base no status passado como parâmetro da tela para o bean.  
	 * attMensagens(Mensagem m) : void - adiciona uma nova mensagem, que foi enviada durante a sessão, na lista de mensagens relacionada àquela solicitacao.
	 * popularMensagens(AjaxBehaviorEvent e) : List<Mensagem> - popula a lista de mensagens relacionadas àquela solicitação.
	 */
	public String verificaCidadaoConsulta() {
		if (userBean.getUsuario().getPerfil() == 3) {
			this.filteredSolicitacoes = SolicitacaoDAO.list();
			return "Consulta/consulta";
		} else {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Usuário sem permissão.", "Tente outro login."));
			return null;
		}
	}

	public String consultarSolicitacao() {
		if (getIdEntidades() == 0) {
			this.filteredSolicitacoes = SolicitacaoDAO.list();
		} else {
			this.filteredSolicitacoes = SolicitacaoDAO.listarPorEntidade(getIdEntidades());
		}

		return "/Consulta/consulta";
	}

	public void listPersonalizada(AjaxBehaviorEvent e) {
		if (status == "Todas") {
			filteredSolicitacoes = SolicitacaoDAO.list();
		} else {
			filteredSolicitacoes = SolicitacaoDAO.listPorStatus(status);
		}
	}

	public static void attMensagens(Mensagem mensagem) {
		mensagensSolicitacao.add(mensagem);
	}

	public List<Mensagem> popularMensagens(AjaxBehaviorEvent e) {
		mensagensSolicitacao = new ArrayList<>(solicitacao.getMensagems());
		return mensagensSolicitacao;
	}

	
	
	
	/////////// Tipologias das solicitações - Tratamentos específicos
	
	public 	String Denuncia() {
		solicitacao.setEntidades(EntidadesDAO.find(1));
		solicitacao.setAcoes(AcoesDAO.findAcoes(1));
		return "/Solicitacao/solicitacao.xhtml";
	}


	
	///////////// Redirecionamento de paginas
	
	public String questionarioParaSolicitacao() {
		if(idAcao == 0) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Não permite campos vazios.", "Preencha os campos."));
			return null;
		}else {
			return "/Solicitacao/solicitacao.xhtml";
		}
	}
	
	///////// Verificação dos Status em relação ao Tempo

	private void alterarPrazo(Solicitacao solicitacao) {
		if (solicitacao != null) {
			solicitacao.setStatus(status);
			solicitacao.setDataLimite(java.sql.Date.valueOf(LocalDate.now().plusDays(prazoResposta(solicitacao.getStatus()))));
			SolicitacaoDAO.saveOrUpdate(solicitacao);
			MensagemBean.salvarStatus(solicitacao, solicitacao.getStatus());
		}

	}

	public boolean ehProrrogavel() {
		if (!verificaSeProrrogada(solicitacao)) {
			return true;
		} else {
			return false;
		}
	}

	private boolean verificaSeProrrogada(Solicitacao solicitacao) {
		boolean retorno = false;
//		List<Mensagem> msgs = new ArrayList<>(solicitacao.getMensagems());
		for (Mensagem mensagem : mensagensSolicitacao) {
			if (mensagem.getTipo().equals((short) 4)) {
				retorno = true;
				break;
			}
		}
		return retorno;
	}

	public boolean recursoLiberado() {
		if(!verificaSeLimiteRecurso(solicitacao) && solicitacao.getStatus().equals("Respondida")) {
			return true;
		}else {
			return false;
		}
	}

	public void prorrogar() {
		this.mensagem.setSolicitacao(solicitacao);
		this.mensagem.setTipo((short)4);
		this.mensagem.setUsuario( ((UsuarioBean) HibernateUtil.RecuperarDaSessao("usuario")).getUsuario());
		this.mensagem.setData(new Date(System.currentTimeMillis()));
		if(MensagemDAO.saveOrUpdate(mensagem)) {
			alterarPrazo(solicitacao);
			MensagemBean.attMensagem(mensagem);
		};
		mensagem = new Mensagem();
		
	}
	public void recurso() {
		short novaInstancia = (short) (solicitacao.getInstancia() + 1);
		solicitacao.setInstancia(novaInstancia);
		alterarPrazo(solicitacao);
		this.mensagem.setSolicitacao(solicitacao);
		this.mensagem.setTipo((short)1);
		this.mensagem.setUsuario( ((UsuarioBean) HibernateUtil.RecuperarDaSessao("usuario")).getUsuario());
		this.mensagem.setData(new Date(System.currentTimeMillis()));
		MensagemDAO.saveOrUpdate(mensagem);
		MensagemBean.attMensagem(mensagem);
		mensagem = new Mensagem();

	}
	
	public static int prazoResposta(String status) {
		switch (status) {
		case "Aberta":
			return constanteAdicionalTempo;
		case "Prorrogada":
			return constanteAdicionalTempo;
		case "Respondida":
			return constanteAdicionalTempo;
		case "Recurso":
			return 5;
		default:
			return constanteTempo;
		}
	}

	public void onRowSelect(SelectEvent event) {
		int rownum = filteredSolicitacoes.indexOf((Solicitacao) event.getObject());
		solicitacao = SolicitacaoDAO.findSolicitacao(rownum);

	}


	private boolean verificaSeRespondida(Solicitacao solicitacao) {
		boolean retorno = false;
//		List<Mensagem> msgs = new ArrayList<>(solicitacao.getMensagems());
		for (Mensagem mensagem : mensagensSolicitacao) {
			if (mensagem.getTipo().equals((short) 2)) {
				retorno = true;
				break;
			}
		}
		return retorno;
	}

	private boolean verificaSeLimiteRecurso(Solicitacao solicitacao) {
		int cont = 0;
		boolean retorno = false;
//		List<Mensagem> msgs = new ArrayList<>(solicitacao.getMensagems());
		for (Mensagem mensagem : mensagensSolicitacao) {
			if (mensagem.getTipo().equals((short) 3)) {
				cont++;
				if (cont == constanteDeRecurso) {
					retorno = true;
					break;
				}
			}
		}

		return retorno;
	}
	
	
	
	//Reencaminhamento de solicitacao
	public boolean reencaminhar() {
		if (LocalDate.now().isBefore(solicitacao.getDataLimite().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().plusDays(1))) {
			return true;
		}else {
			return false;
		}
	}

	// GETTERS E SETTERS
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

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
		return (Set<Mensagem>) MensagemDAO.list(getIdEntidades());
	}

	// public int getIdAcao() {
	// return idAcao;
	// }
	//
	//
	public void setIdAcao(int idAcao) {
		this.idAcao = idAcao;
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

	public static List<Mensagem> getMensagensSolicitacao() {
		return mensagensSolicitacao;
	}

	public static void setMensagensSolicitacao(List<Mensagem> mensagensSolicitacao) {
		SolicitacaoBean.mensagensSolicitacao = mensagensSolicitacao;
	}

	public int getFormaRecebimento() {
		return formaRecebimento;
	}

	public void setFormaRecebimento(int formaRecebimento) {
		this.formaRecebimento = formaRecebimento;
	}
	
}