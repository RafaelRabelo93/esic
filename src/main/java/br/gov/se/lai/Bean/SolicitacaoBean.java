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
import br.gov.se.lai.DAO.UsuarioDAO;
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
	private boolean form = false;
	private boolean mudarEndereco;
	private boolean mudarEmail;
	private CidadaoBean cidadaoBean;

	@PostConstruct
	public void init() { 
		this.solicitacao = new Solicitacao();
		this.entReencaminhar = new Entidades();
		this.mensagem = new Mensagem();
		this.mensagemEncaminhar = new Mensagem();
		this.cidadao = new Cidadao();
		this.anexo = new Anexo();
		this.cidadaoBean = new CidadaoBean();
		this.entidades = new ArrayList<Entidades>(EntidadesDAO.list());
		mensagensSolicitacao = new ArrayList<Mensagem>();
		this.userBean = (UsuarioBean) HibernateUtil.RecuperarDaSessao("usuario");
		
	}

	public String save() {
		
		String page = null;
		gerarDataLimite();
		gerarDataFim(); // caso seja Elogio/Sugestão
		if(solicitacao.getTipo().equals("Denúncia")) {
			settarCidadaoDenuncia(); // Caso específico para Denuncia
		}else {
			settarCidadao(); 
		}

		// Salvar Solicitação
		this.solicitacao.setAcoes(getAcoesTemporaria());
		this.solicitacao.setDataIni(new Date(System.currentTimeMillis()));
		this.solicitacao.setInstancia((short) 1);
		this.solicitacao.setEncaminhada(false);
		this.solicitacao.setProtocolo(gerarProtocolo());
	
		try {
			
			SolicitacaoDAO.saveOrUpdate(solicitacao);
			
			if(solicitacao.getTipo().equals("Solicitação")) {
				dadosRecebimentoSolicitacao(solicitacao);
			}
	
			this.mensagem.setUsuario(solicitacao.getCidadao().getUsuario());
			this.mensagem.setData(new Date(System.currentTimeMillis()));
			this.mensagem.setSolicitacao(solicitacao);
			this.mensagem.setTipo((short) 1);
			MensagemDAO.saveOrUpdate(mensagem);
			
			MensagemBean.salvarStatus(solicitacao, "Recebida", null, null);
	
			if (!(file.getContents().length == 0)) {
				AnexoBean anx = new AnexoBean();
				try {
					anx.save(anexo, mensagem, file);
				} catch (Exception e) {
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, "Anexo não pode ser salvo.", e.getMessage()));
				}
			}
	
			NotificacaoEmail.enviarNotificacao(solicitacao, userBean.getUsuario());
			enviarMensagemAutomatica();
			page = "/Solicitacao/confirmacao.xhtml?faces-redirect=true";
		}catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro.", "Solicitação não enviada."));
			page = "/index.xhtml?faces-redirect=true";
			
		}finally {
			solicitacao = new Solicitacao();
			finalizarSolicitacao();
			return page;
		}
		
	}
	
	public void finalizarSolicitacao() {
		this.solicitacao = new Solicitacao();
		this.mensagem = new Mensagem();
		CompetenciasBean.listCompetencias = null;
		CompetenciasBean.listEntidades = null;
		CompetenciasBean.idAcoes = 0;
		acoesTemporaria = null;
		idAcao = 0;
		formaRecebimento = 0;
		cidadaoBean.limparCidadaoBean();
		cidadaoBean = new CidadaoBean();
		
	}

	public void gerarDataLimite() {
		if (solicitacao.getTipo().equals("Sugestao") || solicitacao.getTipo().equals("Elogio")) {
			this.solicitacao.setDataLimite(new Date(System.currentTimeMillis()));
			this.solicitacao.setDatafim(new Date(System.currentTimeMillis()));
			this.solicitacao.setStatus("Finalizada");

		} else {
			if (LocalDate.now().getDayOfWeek().name().toLowerCase().equals("friday")) {
//				this.solicitacao.setDataLimite(java.sql.Date.valueOf(LocalDate.now().plusDays(constanteTempo + 3)));
				this.solicitacao.setDataLimite(java.sql.Date.valueOf(LocalDate.now().plusDays(7)));
			}else {
//				this.solicitacao.setDataLimite(java.sql.Date.valueOf(LocalDate.now().plusDays(constanteTempo+1)));
				this.solicitacao.setDataLimite(java.sql.Date.valueOf(LocalDate.now().plusDays(4)));
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
			List<Cidadao> listCidadao = new ArrayList<Cidadao>(userBean.getUsuario().getCidadaos());
			this.solicitacao.setCidadao(listCidadao.get(0));
	}
	
	public void settarCidadaoDenuncia() {
		try {
			if (modoAnonimo) {
				solicitacao.setCidadao(CidadaoDAO.findCidadao(0));
			}else {
				solicitacao.setCidadao(userBean.getCidadao());
			}
		}catch (NullPointerException e) {
			solicitacao.setCidadao(CidadaoDAO.findCidadao(0));
		}
	}
	
	public void dadosRecebimentoSolicitacao(Solicitacao solicitacao) {
		switch (solicitacao.getFormaRecebimento()) {
		case 1: 
			enderecoRecebimentoSolicitacao(solicitacao);
			break;
		case 2:
			emailRecebimentoSolicitacao(solicitacao);
			break;
		case 3:
			enderecoRecebimentoSolicitacao(solicitacao);
			emailRecebimentoSolicitacao(solicitacao);
			break;
		}
	}
	
	public void emailRecebimentoSolicitacao(Solicitacao solicitacao) {
		if(mudarEmail) {
			mensagem.setTexto(mensagem.getTexto().concat("\nEmail de recebimento: "+cidadaoBean.getEmail()));
		}else {
			mensagem.setTexto(mensagem.getTexto().concat("\nEmail de recebimento: "+solicitacao.getCidadao().getEmail()));
		}
	}
	
	public void enderecoRecebimentoSolicitacao(Solicitacao solicitacao) {
		if(mudarEndereco) {
			mensagem.setTexto(mensagem.getTexto().concat("\nEndereço de recebimento: \n"
															+ " CEP: "+ cidadaoBean.getCep()+ "\n"
															+ "Cidade: " + cidadaoBean.getCidade() 
															+ "  - Estado: " + cidadaoBean.getEstado() + "\n"
															+ "Logradouro" + cidadaoBean.getEndereco()
															+ "  - Numero: " + cidadaoBean.getNumero() + "\n"
															+ "Complemento: " + cidadaoBean.getComplemento() 
															+ "  - Bairro: "+ cidadaoBean.getBairro()));
		}else {
			Cidadao cid = solicitacao.getCidadao();
			mensagem.setTexto(mensagem.getTexto().concat("\n\nEndereço de recebimento: \n"
														+ "CEP: "+ cid.getCep()+ "\n"
														+ "Cidade: " + cid.getCidade() 
														+ "  -  Estado: " + cid.getEstado() + "\n"
														+ "Bairro: "+ cid.getBairro() + "\n"
														+ "Logradouro: " + cid.getEndereco()
														+ "  -  Numero: " + cid.getNumero() + "\n"
														+ "Complemento: " + cid.getComplemento()));
		}
	}
	
	
	
	/*
	 * verificaCidadaoSolicitacao void : String 
	 * 
	 * Verifica o tipo de usuário que está solicitando acesso e redireciona para ação uma condizente com a situação.
	 * Se não tiver cadastro de usuario, vai cadastrar primeiro.
	 * Verifica se há a instancia de um usuario e se este usuario não é um  responsável.
	 * Se tiver cadastro de usuario mas não tiver de cidadão, primeiro precisa cadastrar cidadão.
	 * Se o usuário já for cadastrado como usuario e cidadão a solicitacao é iniciada.
	 * Se for um responsável não tem autorização para solicitar.
	 */ 
 
	public String verificaCidadaoSolicitacao() {
		List<Cidadao> listCidadao = new ArrayList<Cidadao>(userBean.getUsuario().getCidadaos());

		if (userBean.getUsuario().getPerfil() == 0) {
			
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Usário inválido.", "Realize cadastro."));
			userBean.setVeioDeSolicitacao(1);
			return "/Menu/login";
		} else {
			if (userBean.getUsuario().getPerfil() == 1 || userBean.getUsuario().getPerfil() != 2) {
				if ((listCidadao.isEmpty()) && (userBean.getUsuario().getPerfil() == 1)) {
					return "/Cadastro/cad_cidadao";
				} else {
					return "/Solicitacao/questionario1";
				}
			} else {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Usuário sem permissão.", "Tente outro login."));
				return null;
			}
		}
	}
	
	/*
	 * enviarMensagemAutomatica void : void - Envia notificação para o cidadão informando que a solicitação dele foi recebida.
	 */

	public void enviarMensagemAutomatica() {
		NotificacaoEmail.enviarEmailAutomatico(solicitacao, "Mensagem Automática",
				solicitacao.getTipo() + " recebido com sucesso.");
	}
	
	/*
	 * gerarProtocolo void : String - Gerar números de protocolo para as solicitações
	 */
	public String  gerarProtocolo() {
		Date now = new Date(System.currentTimeMillis());
		SimpleDateFormat ft = new SimpleDateFormat("yyyyddssMs");
		String protocolo = ft.format(now);
		switch(this.solicitacao.getTipo()) {
			case "Reclamação":
				protocolo += "1";
			case "Denúncia":
				protocolo += "2";
			case "Informação":
				protocolo += "3";
			case "Solicitação":
				protocolo += "4";
			case "Sugestão":
				protocolo += "5";
			case "Elogio":
				protocolo += "6";
		}
		
		return protocolo;
	}
	

	

	/* verificaCidadaoConsulta(): String - retorna a página com a lista populada de
	 * solicitações relacionadas ao cidadão. 
	 */
	public String verificaCidadaoConsulta() {
		if (userBean.getUsuario().getPerfil() == 3 || userBean.getUsuario().getPerfil() == 4 && !userBean.isPerfilAlterarCidadaoResponsavel()) {
			this.filteredSolicitacoes = SolicitacaoDAO.list();
			return "/Consulta/consulta.xhtml?faces-redirect=true";
		} else {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Usuário sem permissão.", "Tente outro login."));
			return null;
		}
	}

	/*consultarSolicitacao() : String - Filtra a lista de solicitações de acordo com a
	 * entidade passada como parâmetro da tela para o bean. 
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
	public String consultarSolicitacaoGestor() {
		if(userBean.getUsuario().getPerfil() == 5 || userBean.getUsuario().getPerfil() == 6  ) {
			this.filteredSolicitacoes = SolicitacaoDAO.list();
			return "/Consulta/consulta";
		}else {
			if(ResponsavelBean.permissaoDeAcessoEntidades(EntidadesDAO.find(getIdEntidades()).getIdOrgaos()) ) {
				this.filteredSolicitacoes = SolicitacaoDAO.listarPorEntidade(getIdEntidades());
				return "/Consulta/consulta";
			}else {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Usuário sem permissão.", "Tente outro login."));
				return null;
			}
		}
	}
	
	public String consultarSolicitacaoEspecifica(int idEntidade) {
		this.filteredSolicitacoes = SolicitacaoDAO.listarPorEntidade(idEntidade);
		return "/Consulta/consulta";
	}
	

	/*listPersonalizada(AjaxBehaviorEvent e) : void - filtra lista de solicitações com 
	 * base no status passado como parâmetro da tela para o bean. 
	 */
	public void listPersonalizada(AjaxBehaviorEvent e) {
		if (status == "Todas") {
			filteredSolicitacoes = SolicitacaoDAO.list();
		} else {
			filteredSolicitacoes = SolicitacaoDAO.listPorStatus(status);
		}
	}

	/*attMensagens(Mensagem m) : void - adiciona uma nova mensagem, que foi enviada durante 
	 * a sessão, na lista de mensagens relacionada àquela solicitacao. 
	 */
	public static void attMensagens(Mensagem mensagem) {
		mensagensSolicitacao.add(mensagem);
	}

	/*popularMensagens(AjaxBehaviorEvent e) : List<Mensagem> - popula a lista de mensagens
	 * relacionadas àquela solicitação.
	 */
	public List<Mensagem> popularMensagens() {
		mensagensSolicitacao = new ArrayList<>(solicitacao.getMensagems());
		return mensagensSolicitacao;
	}
	
	/*alterarEnc() : void - Configura status de encaminhada da solicitacação
	 */

	public void alterarEnc() {
		for (Solicitacao slt : SolicitacaoDAO.listarGeral()) {
			slt.setEncaminhada(false);
			SolicitacaoDAO.saveOrUpdate(slt);
		}
	}

	// +++++++++++++++++++++++++++ Tipologias das solicitações - Tratamentos específicos

	public String Denuncia() {
		solicitacao.setEntidades(EntidadesDAO.find(1));
		solicitacao.setTipo("Denúncia");
		setModoAnonimo(true);
		CompetenciasBean.idAcoes = 0;
		AcoesBean.carregarLista();
		return "/Solicitacao/solicitacao.xhtml?faces-redirect=true";
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
		if (idAcao == 0 || idEntidades == 0) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Não permite campos vazios.", "Preencha os campos."));
			return null;
		} else {
			solicitacao.setEntidades(EntidadesDAO.find(idEntidades));
			solicitacao.setAcoes(AcoesDAO.findAcoes(idAcao));
			return "/Solicitacao/solicitacao.xhtml";
		}
	}

	// +++++++++++++++++++++++++++ Verificação dos Status em relação ao Tempo

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
			return 4;
//			return constanteAdicionalTempo;
		case "Prorrogada":
			return 2;
//			return constanteAdicionalTempo;
		case "Respondida":
			return 2;
//			return constanteAdicionalTempo;
		case "Recurso":
			return 2;
//			return 5;
		default:
			return 4;
//			return constanteTempo;
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
	@SuppressWarnings("finally")
	private boolean verificaSeEncaminhada(Solicitacao solicitacao) {
		boolean retorno = false;
		try {
			if (mensagensSolicitacao.isEmpty())
				popularMensagens();
			for (Mensagem msg : mensagensSolicitacao) {
				if (msg.getTipo().equals((short) 5)) {
					retorno = true;
					break;
				}
			}
		} catch (Exception e) {
		} finally {
			return retorno;
		}
	}

	@SuppressWarnings("finally")
	private boolean verificaSe24Horas() {
		boolean retorno = false;
		try {
			Calendar hoje = Calendar.getInstance();
			Calendar limite = Calendar.getInstance();
			limite.setTime(solicitacao.getDataIni());
			limite.add(Calendar.DATE, +1);
			if (hoje.before(limite)) {
				retorno = true;
			}
		} catch (NullPointerException e) {
		}finally {
			return retorno;
		}
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

			entReencaminhar = EntidadesDAO.find(idEntidades);
			Usuario usuario = ((UsuarioBean) HibernateUtil.RecuperarDaSessao("usuario")).getUsuario();
			Responsavel respRemetente = ResponsavelDAO.findResponsavelUsuario(usuario.getIdUsuario()).get(0);
			int idResp = ResponsavelBean.responsavelDisponivel(1, entReencaminhar.getIdEntidades()) ; 
			Responsavel respDestinatario = new Responsavel();
			if( idResp == -1) {
				respDestinatario = ResponsavelDAO.findResponsavel(ResponsavelBean.responsavelDisponivel(1,1));
			}else {
				respDestinatario = ResponsavelDAO.findResponsavel(idResp);
			}
			Entidades antigaEnt = solicitacao.getEntidades();

			// Avisa ao cidadão
			this.mensagem.setSolicitacao(solicitacao);
			this.mensagem.setTipo((short) 2);
			this.mensagem.setUsuario(usuario);
			this.mensagem.setData(new Date(System.currentTimeMillis()));

			if (MensagemDAO.saveOrUpdate(mensagem)) {
				MensagemBean.attMensagemSolicitacao(mensagem);
//				NotificacaoEmail.enviarNotificacao(solicitacao, usuario);
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
					"Solicitação já encaminhada.", "Não é possível executar uma nova encaminhação."));
		}
	}
	
	public void popularEncaminharEntidade() {
		entReencaminhar = new ArrayList<Entidades>(EntidadesDAO.listPersonalizada(idEntidades)).get(0);
	}
	
	public void limparEntidade(AjaxBehaviorEvent e) {
		if(form) {
			CompetenciasBean.idEntidade = 0;
		}else {
			CompetenciasBean.listEntidades = null;
			CompetenciasBean.idAcoes = 0;
			AcoesBean.carregarLista();
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
				formaRecebimentoString = "Correspondência";
			case 2:
				formaRecebimentoString = "Email";
			case 3:
				formaRecebimentoString = "Email e Correspondência";
			}
			return formaRecebimentoString;
		} catch (Exception e) {
			return "...";
		}
	}

	public boolean isModoAnonimo() {
		return modoAnonimo;
	}

	public void setModoAnonimo(boolean modoAnonimo) {
		this.modoAnonimo = modoAnonimo;
	}

	public boolean getForm() {
		return form;
	}

	public void setForm (boolean form) {
		this.form = form;
	}

	public boolean getMudarEndereco() {
		return mudarEndereco;
	}

	public void setMudarEndereco(boolean mudarEndereco) {
		this.mudarEndereco = mudarEndereco;
	}

	public boolean getMudarEmail() {
		return mudarEmail;
	}

	public void setMudarEmail(boolean mudarEmail) {
		this.mudarEmail = mudarEmail;
	}

	public CidadaoBean getCidadaoBean() {
		return cidadaoBean;
	}

	public void setCidadaoBean(CidadaoBean cidadaoBean) {
		this.cidadaoBean = cidadaoBean;
	}
	
}