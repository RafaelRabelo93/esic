package br.gov.se.lai.Bean;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
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
import br.gov.se.lai.DAO.CidadaoDAO;
import br.gov.se.lai.DAO.EntidadesDAO;
import br.gov.se.lai.DAO.MensagemDAO;
import br.gov.se.lai.DAO.ResponsavelDAO;
import br.gov.se.lai.DAO.SolicitacaoDAO;
import br.gov.se.lai.entity.Acoes;
import br.gov.se.lai.entity.Anexo;
import br.gov.se.lai.entity.Cidadao;
import br.gov.se.lai.entity.Entidades;
import br.gov.se.lai.entity.Mensagem;
import br.gov.se.lai.entity.Responsavel;
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
	private Entidades entReencaminhar;
	private UsuarioBean userBean;
	private Anexo anexo;
	private Cidadao cidadao;
	private List<Entidades> entidades;
	private Calendar datainic;
	private String status;
	private String formaRecebimentoString;
	private Calendar datafim;
	private int idEntidades;
	private int formaRecebimento;
	private int idSolicitacao;
	private Mensagem mensagem;
	private Mensagem mensagemEncaminhar;
	private UploadedFile file;
	private Acoes acoesTemporaria;
	private boolean modoAnonimo;
	private final static int constanteTempo = 20;
	private final static int constanteAdicionalTempo = 10;
	private final static int constanteDeRecurso = 2;
	private final static String[] tipos = { "Aberta", "Respondida", "Prorrogada", "Recurso", "Finalizada" };

	@PostConstruct
	public void init() { 
		this.solicitacao = new Solicitacao();
		this.entReencaminhar = new Entidades();
		this.mensagem = new Mensagem();
		this.mensagemEncaminhar = new Mensagem();
		this.cidadao = new Cidadao();
		this.anexo = new Anexo();
		this.entidades = new ArrayList<Entidades>(EntidadesDAO.list());
		mensagensSolicitacao = new ArrayList<Mensagem>();
		this.userBean = (UsuarioBean) HibernateUtil.RecuperarDaSessao("usuario");
		
	}

	public String save() {
		List<Cidadao> listCidadao = new ArrayList<Cidadao>(userBean.getUsuario().getCidadaos());
		
		gerarDataLimite();
		gerarDataFim(); // caso seja Elogio/Sugest�o

		// Salvar Solicita��o
		this.solicitacao.setCidadao(listCidadao.get(0));
		this.solicitacao.setAcoes(getAcoesTemporaria());
		this.solicitacao.setDataIni(new Date(System.currentTimeMillis()));
		this.solicitacao.setInstancia((short) 1);
		this.solicitacao.setEncaminhada(false);
		this.solicitacao.setProtocolo(gerarProtocolo());

		if (SolicitacaoDAO.saveOrUpdate(solicitacao)) {
			salvarMensagem();
		}

		this.solicitacao = new Solicitacao();
		this.mensagem = new Mensagem();
		CompetenciasBean.listCompetencias = null;
		CompetenciasBean.listEntidades = null;
		return "/index";
	}
	
	
	
	public void gerarDataLimite() {
		if (solicitacao.getTipo().equals("Sugestao") || solicitacao.getTipo().equals("Elogio")) {
			this.solicitacao.setDataLimite(new Date(System.currentTimeMillis()));
			this.solicitacao.setDatafim(new Date(System.currentTimeMillis()));
			this.solicitacao.setStatus("Finalizada");

		} else {
			if (LocalDate.now().getDayOfWeek().name().toLowerCase().equals("friday")) {
				this.solicitacao.setDataLimite(java.sql.Date.valueOf(LocalDate.now().plusDays(constanteTempo + 3)));
			}else {
				this.solicitacao.setDataLimite(java.sql.Date.valueOf(LocalDate.now().plusDays(constanteTempo+1)));
			}
			this.solicitacao.setStatus("Aberta");
		}
	}
	
	public void gerarDataFim() {
		if (solicitacao.getTipo().equals("Sugestao") || solicitacao.getTipo().equals("Elogio")) {
			this.solicitacao.setDatafim(new Date(System.currentTimeMillis()));
			this.solicitacao.setStatus("Finalizada");
		}
	}
	
	public void settarCidadao(){
		if(solicitacao.getTipo().equals("Den�ncia")) {
        	solicitacao.setCidadao(CidadaoDAO.findCidadao(7)); 
		}else {
			
		}
			
	}
	
	/*
	 * salvarMensagem() void : void
	 */
	public void salvarMensagem() {
		this.mensagem.setUsuario(userBean.getUsuario());
		this.mensagem.setData(new Date(System.currentTimeMillis()));
		this.mensagem.setSolicitacao(solicitacao);
		this.mensagem.setTipo((short) 1);
		if (MensagemDAO.saveOrUpdate(mensagem)) {

			if (!(file.getContents().length == 0)) {
				AnexoBean anx = new AnexoBean();
				anx.save(anexo, mensagem, file);
			}
			
			NotificacaoEmail.enviarNotificacao(solicitacao, ResponsavelDAO.findResponsavelEntidade(solicitacao.getEntidades().getIdEntidades(), 1).get(0).getUsuario());
			enviarMensagemAutomatica();
		}
	}
	
	public void salvarDenuncia() {
		
	}
	
	/*
	 * verificaCidadaoSolicitacao void : String 
	 * 
	 * Verifica o tipo de usu�rio que est� solicitando acesso e redireciona para a��o uma condizente com a situa��o.
	 * Se n�o tiver cadastro de usuario, vai cadastrar primeiro.
	 * Verifica se h� a instancia de um usuario e se este usuario n�o � um  respons�vel.
	 * Se tiver cadastro de usuario mas n�o tiver de cidad�o, primeiro precisa cadastrar cidad�o.
	 * Se o usu�rio j� for cadastrado como usuario e cidad�o a solicitacao � iniciada.
	 * Se for um respons�vel n�o tem autoriza��o para solicitar.
	 */

	public String verificaCidadaoSolicitacao() {
		List<Cidadao> listCidadao = new ArrayList<Cidadao>(userBean.getUsuario().getCidadaos());

		if (userBean.getUsuario().getPerfil() == 0) {
			
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Us�rio inv�lido.", "Realize cadastro."));
			userBean.setVeioDeSolicitacao(1);
			return "/Cadastro/cad_usuario";
		} else {
			if (userBean.getUsuario().getPerfil() == 1 || userBean.getUsuario().getPerfil() != 2) {
				if ((listCidadao.isEmpty()) && (userBean.getUsuario().getPerfil() == 1)) {
					return "/Cadastro/cad_cidadao";
				} else {
					return "/Solicitacao/questionario1";
				}
			} else {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Usu�rio sem permiss�o.", "Tente outro login."));
				return null;
			}
		}
	}
	/*
	 * enviarMensagemAutomatica void : void - Envia notifica��o para o cidad�o informando que a solicita��o dele foi recebida.
	 */

	public void enviarMensagemAutomatica() {
		NotificacaoEmail.enviarEmailAutomatico(solicitacao, "Mensagem Autom�tica",
				solicitacao.getTipo() + " recebido com sucesso.");
	}
	
	/*
	 * gerarProtocolo void : String - Gerar n�meros de protocolo para as solicita��es
	 */
	public String  gerarProtocolo() {
		Date now = new Date(System.currentTimeMillis());
		SimpleDateFormat ft = new SimpleDateFormat("yyyyddssMs");
		String protocolo = ft.format(now);
		switch(this.solicitacao.getTipo()) {
			case "Reclamacao":
				protocolo += "1";
			case "Denuncia":
				protocolo += "2";
			case "Informacao":
				protocolo += "3";
			case "Solicitacao":
				protocolo += "4";
			case "Sugestao":
				protocolo += "5";
			case "Elogio":
				protocolo += "6";
		}
		
		return protocolo;
	}
	

	

	/* verificaCidadaoConsulta(): String - retorna a p�gina com a lista populada de
	 * solicita��es relacionadas ao cidad�o. 
	 */
	public String verificaCidadaoConsulta() {
		if (userBean.getUsuario().getPerfil() == 3) {
			this.filteredSolicitacoes = SolicitacaoDAO.list();
			return "Consulta/consulta";
		} else {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Usu�rio sem permiss�o.", "Tente outro login."));
			return null;
		}
	}

	/*consultarSolicitacao() : String - Filtra a lista de solicita��es de acordo com a
	 * entidade passada como par�metro da tela para o bean. 
	 * 
	 */
	public String consultarSolicitacao() {
		if (getIdEntidades() == 0) {
			this.filteredSolicitacoes = SolicitacaoDAO.list();
		} else {
			this.filteredSolicitacoes = SolicitacaoDAO.listarPorEntidade(getIdEntidades());
		}

		return "/Consulta/consulta";
	}

	/*listPersonalizada(AjaxBehaviorEvent e) : void - filtra lista de solicita��es com 
	 * base no status passado como par�metro da tela para o bean. 
	 */
	public void listPersonalizada(AjaxBehaviorEvent e) {
		if (status == "Todas") {
			filteredSolicitacoes = SolicitacaoDAO.list();
		} else {
			filteredSolicitacoes = SolicitacaoDAO.listPorStatus(status);
		}
	}

	/*attMensagens(Mensagem m) : void - adiciona uma nova mensagem, que foi enviada durante 
	 * a sess�o, na lista de mensagens relacionada �quela solicitacao. 
	 */
	public static void attMensagens(Mensagem mensagem) {
		mensagensSolicitacao.add(mensagem);
	}

	/*popularMensagens(AjaxBehaviorEvent e) : List<Mensagem> - popula a lista de mensagens
	 * relacionadas �quela solicita��o.
	 */
	public List<Mensagem> popularMensagens() {
		mensagensSolicitacao = new ArrayList<>(solicitacao.getMensagems());
		return mensagensSolicitacao;
	}
	
	/*alterarEnc() : void - Configura status de encaminhada da solicitaca��o
	 */

	public void alterarEnc() {
		for (Solicitacao slt : SolicitacaoDAO.listarGeral()) {
			slt.setEncaminhada(false);
			SolicitacaoDAO.saveOrUpdate(slt);
		}
	}

	// +++++++++++++++++++++++++++ Tipologias das solicita��es - Tratamentos espec�ficos

	public String Denuncia() {
		solicitacao.setEntidades(EntidadesDAO.find(1));
		return "/Solicitacao/solicitacao.xhtml";
	}
	
	public boolean estaUsuarioLogado() {
		UsuarioBean usuarioBean = (UsuarioBean) HibernateUtil.RecuperarDaSessao("usuario");
		if (usuarioBean != null) {
			return true;
		}else {
			return false;
		}
	}
	

	// +++++++++++++++++++++++++++ Redirecionamento de paginas

	public String questionarioParaSolicitacao() {
		if (idAcao == 0) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "N�o permite campos vazios.", "Preencha os campos."));
			return null;
		} else {
			return "/Solicitacao/solicitacao.xhtml";
		}
	}

	// +++++++++++++++++++++++++++ Verifica��o dos Status em rela��o ao Tempo

	private void alterarPrazo(Solicitacao solicitacao) {
		if (solicitacao != null) {
			solicitacao.setStatus(status);
			solicitacao.setDataLimite(
					java.sql.Date.valueOf(LocalDate.now().plusDays(prazoResposta(solicitacao.getStatus()))));
			SolicitacaoDAO.saveOrUpdate(solicitacao);
			MensagemBean.salvarStatus(solicitacao, solicitacao.getStatus(), null, null);
		}

	}

	public boolean ehProrrogavel() {
		Calendar hoje = Calendar.getInstance();

		Calendar limiteMin = Calendar.getInstance();
		limiteMin.setTime(solicitacao.getDataLimite());
		limiteMin.add(Calendar.DATE, -5);
		
		Calendar limite = Calendar.getInstance();
		limite.setTime(solicitacao.getDataLimite());
		
		if (!verificaSeProrrogada(solicitacao) && hoje.after(limite) && hoje.before(limite)) {
			return true;
		} else {
			return false;
		}
	}

	private boolean verificaSeProrrogada(Solicitacao solicitacao) {
		boolean retorno = false;
		// List<Mensagem> msgs = new ArrayList<>(solicitacao.getMensagems());
		if (mensagensSolicitacao.isEmpty())
			popularMensagens();
		for (Mensagem mensagem : mensagensSolicitacao) {
			if (mensagem.getTipo().equals((short) 4)) {
				retorno = true;
				break;
			}
		}
		return retorno;
	}

	public boolean recursoLiberado() {
		if (!verificaSeLimiteRecurso(solicitacao) && solicitacao.getStatus().equals("Respondida")) {
			return true;
		} else {
			return false;
		}
	}

	public void prorrogar() {
		this.mensagem.setSolicitacao(solicitacao);
		this.mensagem.setTipo((short) 2);
		this.mensagem.setUsuario(((UsuarioBean) HibernateUtil.RecuperarDaSessao("usuario")).getUsuario());
		this.mensagem.setData(new Date(System.currentTimeMillis()));
		if (MensagemDAO.saveOrUpdate(mensagem)) {
			alterarPrazo(solicitacao);
			MensagemBean.attMensagemSolicitacao(mensagem);
		}
		;
		mensagem = new Mensagem();

	}

	public void recurso() {
		short novaInstancia = (short) (solicitacao.getInstancia() + 1);
		solicitacao.setInstancia(novaInstancia);
		alterarPrazo(solicitacao);
		this.mensagem.setSolicitacao(solicitacao);
		this.mensagem.setTipo((short) 1);
		this.mensagem.setUsuario(((UsuarioBean) HibernateUtil.RecuperarDaSessao("usuario")).getUsuario());
		this.mensagem.setData(new Date(System.currentTimeMillis()));
		MensagemDAO.saveOrUpdate(mensagem);
		MensagemBean.attMensagemSolicitacao(mensagem);
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
		// List<Mensagem> msgs = new ArrayList<>(solicitacao.getMensagems());
		if (mensagensSolicitacao.isEmpty())
			popularMensagens();
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
		// List<Mensagem> msgs = new ArrayList<>(solicitacao.getMensagems());
		if (mensagensSolicitacao.isEmpty())
			popularMensagens();
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

	// Reencaminhamento de solicitacao
	private boolean verificaSeEncaminhada(Solicitacao solicitacao) {
		boolean retorno = false;
		if (mensagensSolicitacao.isEmpty()) popularMensagens();
		for (Mensagem msg : mensagensSolicitacao) {
			if (msg.getTipo().equals((short) 5)) {
				retorno = true;
				break;
			}
		}
		return retorno;
	}

	private boolean verificaSe24Horas() {
		boolean retorno = false;
		Calendar hoje = Calendar.getInstance();
		Calendar limite = Calendar.getInstance();
		limite.setTime(solicitacao.getDataIni());
		limite.add(Calendar.DATE, +1);
		if (hoje.before(limite)) {
			retorno = true;
		}
		return retorno;
	}

	public boolean ehEncaminhavel() {
		if (!verificaSeEncaminhada(solicitacao) && verificaSe24Horas()) {
			return true;
		} else {
			return false;
		}
	}

	//+++++++++++++++++++++++++++ Reencaminhar

	public void encaminhar() {
		
		if (!solicitacao.isEncaminhada()) {
			
			popularEncaminharEntidade();

//			entReencaminhar = EntidadesDAO.find(3);
			Usuario usuario = ((UsuarioBean) HibernateUtil.RecuperarDaSessao("usuario")).getUsuario();
			Responsavel respRemetente = ResponsavelDAO.findResponsavelUsuario(usuario.getIdUsuario());
			Responsavel respDestinatario = (Responsavel) ResponsavelDAO
					.findResponsavelEntidade(entReencaminhar.getIdEntidades(), 1).get(0);
			Entidades antigaEnt = solicitacao.getEntidades();

			// Avisa ao cidad�o
			this.mensagem.setSolicitacao(solicitacao);
			this.mensagem.setTipo((short) 2);
			this.mensagem.setUsuario(usuario);
			this.mensagem.setData(new Date(System.currentTimeMillis()));

			if (MensagemDAO.saveOrUpdate(mensagem)) {
				MensagemBean.attMensagemSolicitacao(mensagem);
				NotificacaoEmail.enviarNotificacao(solicitacao, usuario);
			}
			;
			mensagem = new Mensagem();

			// Encaminhar
			if (!solicitacao.isEncaminhada()) {

				this.solicitacao.setEntidades(entReencaminhar);
				solicitacao.setEncaminhada(true);

				if (SolicitacaoDAO.saveOrUpdate(solicitacao)) {
					this.mensagemEncaminhar.setSolicitacao(solicitacao);
					this.mensagemEncaminhar.setTipo((short) 5);
					this.mensagemEncaminhar.setUsuario(usuario);
					this.mensagemEncaminhar.setData(new Date(System.currentTimeMillis()));
					if (MensagemDAO.saveOrUpdate(mensagemEncaminhar)) {
						MensagemBean.attMensagemTramites(mensagemEncaminhar);
						MensagemBean.salvarStatus(solicitacao, "Encaminhada", solicitacao.getEntidades().getNome(),
								antigaEnt.getNome());
						NotificacaoEmail.enviarEmailTramites(solicitacao, mensagemEncaminhar.getTexto(), respRemetente,
								respDestinatario);
					}
				}
			}
			mensagemEncaminhar = new Mensagem();

		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Solicita��o j� encaminhada.", "N�o � poss�vel executar uma nova encaminha��o."));
		}
	}
	
	public void popularEncaminharEntidade() {
		entReencaminhar = new ArrayList<Entidades>(EntidadesDAO.listPersonalizada(idEntidades)).get(0);
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

	 public int getIdAcao() {
	 return idAcao;
	 }
	
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

	public Mensagem getMensagemEncaminhar() {
		return mensagemEncaminhar;
	}

	public void setMensagemEncaminhar(Mensagem mensagemEncaminhar) {
		this.mensagemEncaminhar = mensagemEncaminhar;
	}

	public Entidades getEntReencaminhar() {
		return entReencaminhar;
	}

	public void setEntReencaminhar(Entidades entReencaminhar) {
		this.entReencaminhar = entReencaminhar;
	}
	
	public String getFormaRecebimentoString() {
		try {
			switch (solicitacao.getFormaRecebimento()) {
			case 1:
				formaRecebimentoString = "Correspond�ncia";
			case 2:
				formaRecebimentoString = "Email";
			case 3:
				formaRecebimentoString = "Email e Correspond�ncia";
			}
			return formaRecebimentoString;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return "...";
		}
	}

	public boolean isModoAnonimo() {
		return modoAnonimo;
	}

	public void setModoAnonimo(boolean modoAnonimo) {
		this.modoAnonimo = modoAnonimo;
	}



	
}