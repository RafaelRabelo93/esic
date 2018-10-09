package br.gov.se.lai.Bean;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.dom4j.VisitorSupport;
import org.omg.CORBA.Request;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.UploadedFile;
import org.primefaces.model.Visibility;

import br.gov.se.lai.DAO.AcoesDAO;
import br.gov.se.lai.DAO.CidadaoDAO;
import br.gov.se.lai.DAO.CompetenciasDAO;
import br.gov.se.lai.DAO.EntidadesDAO;
import br.gov.se.lai.DAO.MensagemDAO;
import br.gov.se.lai.DAO.ResponsavelDAO;
import br.gov.se.lai.DAO.SolicitacaoDAO;
import br.gov.se.lai.DAO.UsuarioDAO;
import br.gov.se.lai.anexos.UploadFile;
import br.gov.se.lai.entity.Acoes;
import br.gov.se.lai.entity.Cidadao;
import br.gov.se.lai.entity.Competencias;
import br.gov.se.lai.entity.Entidades;
import br.gov.se.lai.entity.Mensagem;
import br.gov.se.lai.entity.Responsavel;
import br.gov.se.lai.entity.Solicitacao;
import br.gov.se.lai.entity.Usuario;
import br.gov.se.lai.relatorios.RelatorioDinamico;
import br.gov.se.lai.utils.Avaliacao;
import br.gov.se.lai.utils.HibernateUtil;
import br.gov.se.lai.utils.NotificacaoEmail;
import br.gov.se.lai.utils.PrazosSolicitacao;

@ManagedBean(name = "solicitacao")
@SessionScoped
@SuppressWarnings("unused")
public class SolicitacaoBean implements Serializable {

	private List<Solicitacao> solicitacoes;
	private int idAcao;
	private int idCompetencias;
	private List<Solicitacao> filteredSolicitacoes;
	private List<Solicitacao> solicitacoesFiltradas;
	private static List<Mensagem> mensagensSolicitacao;
	private static final long serialVersionUID = -9191715805520708190L;
	private Solicitacao solicitacao;
	private Entidades entReencaminhar;
	private UsuarioBean userBean;
//	private Anexo anexo;
	private Cidadao cidadao;
	private List<Entidades> entidades;
	private Calendar datainic;
	private String status;
	private String tipo;
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
	private boolean modoSigilo;
	private boolean modoIdentificavel;
	private final static int constanteTempo = 20;
	private final static int constanteAdicionalTempo = 10;
	private final static int constanteDeRecurso = 2;
	private final static String[] tipos = { "Aberta", "Atendida", "Prorrogada", "Recurso", "Finalizada", "Negada", "Sem Resposta", "Transição" };
	private boolean form = false;
	private boolean mudarEndereco;
	private boolean mudarEmail;
	private CidadaoBean cidadaoBean;
	public static int solicitacaoTotal;
	public static int solicitacaoPendente;
	public static int solicitacaoNegada;
	public static int solicitacaoRespondida;
	public static int solicitacaoDenuncia;
	public static int solicitacaoFinalizadas;
	private List<Boolean> list = Arrays.asList(true, true, true, true, true, true, true, true, false, false, true, true);
	
	@PostConstruct
	public void init() {
		this.solicitacao = new Solicitacao();
		this.entReencaminhar = new Entidades();
		this.mensagem = new Mensagem();
		this.mensagemEncaminhar = new Mensagem();
		this.cidadao = new Cidadao();
//		this.anexo = new Anexo();
		this.cidadaoBean = new CidadaoBean();
		this.entidades = new ArrayList<Entidades>(EntidadesDAO.list());
		mensagensSolicitacao = new ArrayList<Mensagem>();
		this.userBean = (UsuarioBean) HibernateUtil.RecuperarDaSessao("usuario");
	}

	/**
	 * Função salvar a solicitação
	 * 
	 * @return
	 */
	public String save() {

		String page = null;
		gerarDataLimite();
		gerarDataFim(); // caso seja Elogio/Sugestão
		if (solicitacao.getTipo().equals("Denúncia")) {
			settarCidadaoDenuncia(); // Caso específico para Denuncia
			this.solicitacao.setCompetencias(CompetenciasDAO.findCompetencias(idCompetencias));
		} else {
			settarCidadao();
			this.solicitacao.setCompetencias(CompetenciasDAO.findCompetencias(idCompetencias));
		}

		// Salvar Solicitação
		this.solicitacao.setDataIni(new Date(System.currentTimeMillis()));
		this.solicitacao.setInstancia((short) 0);
		this.solicitacao.setProtocolo(gerarProtocolo());
		this.solicitacao.setAvaliacao(0);

		try {
			SolicitacaoDAO.saveOrUpdate(solicitacao);

			if (solicitacao.getTipo().equals("Solicitação")) {
				dadosRecebimentoSolicitacao(solicitacao);
			}
			
		if (solicitacao.getTipo().equals("Denúncia"))	this.mensagem.setUsuario(UsuarioDAO.findUsuario(0));
		else	this.mensagem.setUsuario(solicitacao.getCidadao().getUsuario());
		
		this.mensagem.setData(new Date(System.currentTimeMillis()));
		this.mensagem.setSolicitacao(solicitacao);
		this.mensagem.setTipo((short) 1);
		MensagemDAO.saveOrUpdate(mensagem);

		MensagemBean.salvarStatus(solicitacao, "Recebida", null, null, 0);
		
		if (!(file.getContents().length == 0)) {
			try {
				UploadFile upload = new UploadFile();
				upload.upload(file, mensagem.getIdMensagem());
			} catch (Exception e) {
				FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_ERROR, "Anexo não pôde ser salvo.", e.getMessage()));
			}
		}
			
			if (!solicitacao.getCidadao().getUsuario().getNick().contains("anonimo") || !solicitacao.getTipo().equals("Sugestão") || !solicitacao.getTipo().equals("Reclamação")) {
				addQuantidadeSolicitacaoTotal();
				addQuantidadeSolicitacaoPendente();
			} else if (solicitacao.getTipo().equals("Sugestão") || solicitacao.getTipo().equals("Reclamação")) {
				addQuantidadeSolicitacaoTotal();
				addQuantidadeSolicitacaoFinalizada();
			}
			
			if(!solicitacao.getCidadao().getUsuario().getNick().contains("anonimo")) {
				NotificacaoEmail.enviarEmailNovaSolicitacaoCidadao(solicitacao, ((UsuarioBean) HibernateUtil.RecuperarDaSessao("usuario")).getUsuario());
			}
			NotificacaoEmail.enviarEmailNovaSolicitacaoResp(solicitacao);

			page = "/Solicitacao/confirmacao.xhtml?faces-redirect=true";
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro.", "Solicitação não enviada."));
			page = "/index.xhtml?faces-redirect=true";

		} finally {
			solicitacao = new Solicitacao();
			finalizarSolicitacao();
			return page;
		}

	}

	/**
	 * Limpar os objetos utilizados para salvar a solicitação no banco.
	 */
	public void finalizarSolicitacao() {
		this.solicitacao = new Solicitacao();
		this.mensagem = new Mensagem();
		new CompetenciasBean().listCompetencias = null;
		CompetenciasBean.listEntidades = null;
		CompetenciasBean.idAcoes = 0;
		CompetenciasBean.idEntidade = 0;
		idCompetencias = 0;
		acoesTemporaria = null;
		idAcao = 0;
		formaRecebimento = 0;
		try {
			cidadaoBean.limparCidadaoBean();
			cidadaoBean = new CidadaoBean();
		} catch (NullPointerException e) {
		}

	}

	/**
	 * Gerar data limite para resposta do responsável. Caso a solicitação seja feita
	 * no final de semana, o prazo inicia a contar no primeiro dia da semana.
	 */
	public void gerarDataLimite() {
		if (solicitacao.getTipo().equals("Sugestão") || solicitacao.getTipo().equals("Elogio")) {
			this.solicitacao.setDataLimite(new Date(System.currentTimeMillis()));
			this.solicitacao.setDatafim(new Date(System.currentTimeMillis()));
			this.solicitacao.setStatus("Finalizada");

		} else {
			this.solicitacao.setDataLimite(PrazosSolicitacao.gerarPrazoDiaUtilLimite(new Date(System.currentTimeMillis()), PrazosSolicitacao.prazoResposta("Aberta")));
			this.solicitacao.setStatus("Aberta");
		}
	}

	/**
	 * Gerar data de finalização para tipos de solicitação que não necessitam de
	 * resposta.
	 */
	public void gerarDataFim() {
		if (solicitacao.getTipo().equals("Sugestao") || solicitacao.getTipo().equals("Elogio")) {
			this.solicitacao.setDatafim(new Date(System.currentTimeMillis()));
			this.solicitacao.setStatus("Finalizada");
		}
	}

	/**
	 * Ligar a instância de cidadão logada a solicitação efetuada.
	 */
	public void settarCidadao() {
		List<Cidadao> listCidadao = new ArrayList<Cidadao>(userBean.getUsuario().getCidadaos());
		this.solicitacao.setCidadao(listCidadao.get(0));
	}

	/**
	 * Ligar o tipo de cidadão ao tipo de solicitação denúncia. O cidadão será
	 * anônimo caso o usuário tenha ativado o modoAnônimo.
	 */
	public void settarCidadaoDenuncia() {
		try {
			if (modoAnonimo) {
//				Usuario usuarioAnonimo = new Usuario();
//				usuarioAnonimo.setNome("Anônimo");
//				usuarioAnonimo.setNick("anonimo");
//				usuarioAnonimo.setPerfil((short)3);
//				Cidadao cidadaoAnonimo = new Cidadao();
//				cidadaoAnonimo.setUsuario(usuarioAnonimo);;
//				solicitacao.setCidadao(cidadaoAnonimo);
				solicitacao.setCidadao(CidadaoDAO.findIdCidadao(0));
			} else {
				solicitacao.setCidadao(userBean.getCidadao());
			}
			
			if(modoSigilo) {
				solicitacao.setSigilo(true);
			}
		} catch (NullPointerException e) {
			solicitacao.setCidadao(CidadaoDAO.findIdCidadao(0));
		}
	}

	/**
	 * Função dadosRecebimentoSolicitacao
	 * 
	 * Adição de informações mais específicas para recebimento de um pedido de
	 * solicitação. Pode ser email, correspondência ou email e correspondência.
	 * 
	 * @param solicitacao
	 */
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

	/**
	 * Função emailRecebimento
	 * 
	 * Verifica qual endereço de email o usuario quer receber a resposta da
	 * solicitacao e o adiciona à mensagem da solicitação.
	 * 
	 * @param solicitacao
	 */
	public void emailRecebimentoSolicitacao(Solicitacao solicitacao) {
		if (mudarEmail) {
			mensagem.setTexto(mensagem.getTexto().concat("\nEmail de recebimento: " + cidadaoBean.getEmail()));
		} else {
			mensagem.setTexto(
					mensagem.getTexto().concat("\nEmail de recebimento: " + solicitacao.getCidadao().getEmail()));
		}
	}

	/**
	 * Função enderecoRecebimento
	 * 
	 * Verifica qual endereço físico o usuario quer receber a resposta da
	 * solicitacao e o adiciona à mensagem da solicitação.
	 * 
	 * @param solicitacao
	 */
	public void enderecoRecebimentoSolicitacao(Solicitacao solicitacao) {
		if (mudarEndereco) {
			mensagem.setTexto(mensagem.getTexto()
					.concat("\nEndereço de recebimento: \n" + " CEP: " + cidadaoBean.getCep() + "\n" + "Cidade: "
							+ cidadaoBean.getCidade() + "  - Estado: " + cidadaoBean.getEstado() + "\n" + "Logradouro"
							+ cidadaoBean.getEndereco() + "  - Numero: " + cidadaoBean.getNumero() + "\n"
							+ "Complemento: " + cidadaoBean.getComplemento() + "  - Bairro: "
							+ cidadaoBean.getBairro()));
		} else {
			Cidadao cid = solicitacao.getCidadao();
			mensagem.setTexto(mensagem.getTexto()
					.concat("\n\nEndereço de recebimento: \n" + "CEP: " + cid.getCep() + "\n" + "Cidade: "
							+ cid.getCidade() + "  -  Estado: " + cid.getEstado() + "\n" + "Bairro: " + cid.getBairro()
							+ "\n" + "Logradouro: " + cid.getEndereco() + "  -  Numero: " + cid.getNumero() + "\n"
							+ "Complemento: " + cid.getComplemento()));
		}
	}

	/**
	 * verificaCidadaoSolicitacao void : String
	 * 
	 * Verifica o tipo de usuário que está solicitando acesso e redireciona para
	 * ação uma condizente com a situação. Se não tiver cadastro de usuario, vai
	 * cadastrar primeiro. Verifica se há a instancia de um usuario e se este
	 * usuario não é um responsável. Se tiver cadastro de usuario mas não tiver de
	 * cidadão, primeiro precisa cadastrar cidadão. Se o usuário já for cadastrado
	 * como usuario e cidadão a solicitacao é iniciada. Se for um responsável não
	 * tem autorização para solicitar.
	 */

	public String verificaCidadaoSolicitacao() {
		List<Cidadao> listCidadao = new ArrayList<Cidadao>(userBean.getUsuario().getCidadaos());

		if (userBean.getUsuario().getPerfil() == 0) {

//			FacesContext.getCurrentInstance().addMessage(null,
//					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Usário inválido.", "Realize cadastro."));
			userBean.setVeioDeSolicitacao(1);
			return "/Menu/login";
		} else {
			if (userBean.getUsuario().getPerfil() == 1 || userBean.getUsuario().getPerfil() != 2) {
				if ((listCidadao.isEmpty()) && (userBean.getUsuario().getPerfil() == 1)) {
					return "/Cadastro/cad_cidadao";
				} else {
					finalizarSolicitacao();
					return "/Solicitacao/questionario1";
				}
			} else {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Usuário sem permissão.", "Tente outro login."));
				return null;
			}
		}
	}

	/**
	 * Função iniciarSolicitação
	 * 
	 * Limpar a solicitação da solicitação anterior e definir o tipo da solicitação.
	 * 
	 * @return
	 */
	public String iniciarSolicitacao() {
		finalizarSolicitacao();
		solicitacao.setTipo(this.tipo);
		if (estaUsuarioLogado()) {
//			System.out.println("Modo anônimo ativado");
			setModoAnonimo(true);
			setModoSigilo(false);
			setModoIdentificavel(false);
		}
		CompetenciasBean competencias = new CompetenciasBean();
		competencias.listCompetencias = new ArrayList<Competencias>();
		return "Solicitacao/questionario2.xhtml?faces-redirect=true";
	}

//	/**
//	 * Função enviarMensagemAutomatica
//	 * 
//	 * Envia notificação para o cidadão informando que a solicitação dele foi
//	 * recebida.
//	 */
//
//	public void enviarMensagemAutomatica() {
//		NotificacaoEmail.enviarEmailAutomatico(solicitacao, "Mensagem Automática", solicitacao.getTipo() + " recebido com sucesso.");
//	}

	/**
	 * Função gerarProtocolo
	 * 
	 * Gerar números de protocolo para as solicitações
	 */
	public String gerarProtocolo() {
		Date now = new Date(System.currentTimeMillis());
		SimpleDateFormat ft = new SimpleDateFormat("yy");
		String protocolo = String.valueOf(SolicitacaoDAO.listarGeral().size());
		int loop = protocolo.length();
		for (int i = 0; i<(5-loop); i++ ) {
			protocolo = "0"+protocolo;
		}
		
		protocolo += "/"+ft.format(now);
		
		switch (this.solicitacao.getTipo()) {
		case "Reclamação":
			protocolo += "-1";
			break;
		case "Denúncia":
			protocolo += "-2";
			break;
		case "Informação":
			protocolo += "-3";
			break;
		case "Solicitação":
			protocolo += "-4";
			break;
		case "Sugestão":
			protocolo += "-5";
			break;
		case "Elogio":
			protocolo += "-6";
			break;
		}
		
		return protocolo;
	}

	/**
	 * Função verificaCidadaoConsulta Retorna a página com a lista populada de
	 * solicitações relacionadas ao cidadão.
	 */
	public String verificaCidadaoConsulta() {
		finalizarSolicitacao();
		if (userBean.getUsuario().getPerfil() == 3
				|| userBean.getUsuario().getPerfil() == 4 && !userBean.isPerfilAlterarCidadaoResponsavel()) {
			this.filteredSolicitacoes = SolicitacaoDAO.list();
			return "/Consulta/consulta.xhtml?faces-redirect=true";
		} else {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Usuário sem permissão.", "Tente outro login."));
			return null;
		}
	}

	/**
	 * Função consultarSolicitacao
	 * 
	 * Filtra a lista de solicitações de acordo com a entidade passada como
	 * parâmetro da tela para o bean.
	 * 
	 */
	public String consultarSolicitacao() {
		if (getIdEntidades() == 0) {
			this.filteredSolicitacoes = SolicitacaoDAO.list();
		} else {
			this.filteredSolicitacoes = SolicitacaoDAO.listarPorEntidade(getIdEntidades());
		}

		for (Responsavel resp : userBean.getUsuario().getResponsavels()) {
			if (resp.getEntidades().getIdEntidades().equals(EntidadesDAO.FindSigla("CGE").get(0).getIdEntidades())
					&& resp.getNivel() >= 2) {
				ArrayList<Solicitacao> listDenuncia = new ArrayList<>(SolicitacaoDAO.listPorTipo("Denúncia"));
				this.filteredSolicitacoes.addAll(new ArrayList<Solicitacao>(SolicitacaoDAO.listPorTipo("Denúncia")));
			}
		}

		if (userBean.getUsuario().getPerfil() == (short) 5 || userBean.getUsuario().getPerfil() == (short) 6) {
			this.filteredSolicitacoes.addAll(new ArrayList<Solicitacao>(SolicitacaoDAO.listPorStatus("Denúncia")));
		}

		return "/Consulta/consulta";

	}

	/**
	 * Função consultarSolicitacaoGestor
	 * 
	 * Verifica o tipo de usuário para poder disponibilizar a visualização das
	 * solicitações
	 * 
	 * @return
	 */
	public String consultarSolicitacaoGestor() {
		if (userBean.getUsuario().getPerfil() == 5 || userBean.getUsuario().getPerfil() == 6) {
			this.filteredSolicitacoes = SolicitacaoDAO.list();
			return "/Consulta/consulta";
		} else {
			if (ResponsavelBean.permissaoDeAcessoEntidades(EntidadesDAO.find(getIdEntidades()).getIdOrgaos(),
					getIdEntidades())) {
				this.filteredSolicitacoes = SolicitacaoDAO.listarPorEntidade(getIdEntidades());
				return "/Consulta/consulta";
			} else {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Usuário sem permissão.", "Tente outro login."));
				return null;
			}
		}
	}

	/**
	 * Consultar solicitação de entidade especifica
	 * 
	 * @param idEntidade
	 * @return
	 */
	public String consultarSolicitacaoEspecifica(int idEntidade) {
		this.filteredSolicitacoes = SolicitacaoDAO.listarPorEntidade(idEntidade);

		for (Responsavel resp : userBean.getUsuario().getResponsavels()) {
			if (resp.getEntidades().getIdEntidades().equals(EntidadesDAO.FindSigla("CGE").get(0))
					&& resp.getNivel() >= 2) {
				this.filteredSolicitacoes.addAll(new ArrayList<Solicitacao>(SolicitacaoDAO.listPorStatus("Denúncia")));
			}
		}

		if (userBean.getUsuario().getPerfil() == (short) 5 || userBean.getUsuario().getPerfil() == (short) 6) {
			this.filteredSolicitacoes.addAll(new ArrayList<Solicitacao>(SolicitacaoDAO.listPorStatus("Denúncia")));
		}

		return "/Consulta/consulta";
	}
	
	

	/**
	 * listPersonalizada
	 * 
	 * Filtra lista de solicitações com base no status passado como parâmetro da
	 * tela para o bean.
	 * 
	 * @param e
	 */
	public void listPersonalizada(AjaxBehaviorEvent e) {
		if (status == "Todas") {
			filteredSolicitacoes = SolicitacaoDAO.list();
		} else {
			filteredSolicitacoes = SolicitacaoDAO.listPorStatus(status);
		}
	}

	/**
	 * Função attMensagens adiciona uma nova mensagem, que foi enviada durante a
	 * sessão, na lista de mensagens relacionada àquela solicitacao.
	 * 
	 * @param mensagem
	 */
	public static void attMensagens(Mensagem mensagem) {
		mensagensSolicitacao.add(mensagem);
	}

	/**
	 * Função popularMensagens
	 * 
	 * Popula a lista de mensagens relacionadas àquela solicitação.
	 * 
	 * @return
	 */
	public List<Mensagem> popularMensagens() {
		mensagensSolicitacao = new ArrayList<>(solicitacao.getMensagems());
		return mensagensSolicitacao;
	}

	/**
	 * Função alterarEnc
	 * 
	 * Configura status de encaminhada da solicitacação
	 */

	public void alterarEnc() {
		for (Solicitacao slt : SolicitacaoDAO.listarGeral()) {
			slt.setEncaminhada(false);
			SolicitacaoDAO.saveOrUpdate(slt);
		}
	}

	public static void calcularQuantitativoSolicitacao() {
		List<Solicitacao> aux = new ArrayList<>();

		aux = SolicitacaoDAO.list();
		solicitacaoTotal = aux != null ? aux.size() : 0;

		aux = SolicitacaoDAO.listStatus("Atendida");
		solicitacaoRespondida = aux != null ? aux.size() : 0;
		aux = SolicitacaoDAO.listStatus("Aberta");
		solicitacaoPendente = aux != null ? aux.size() : 0;
		aux = SolicitacaoDAO.listStatus("Recurso");
		solicitacaoPendente += aux != null ? aux.size() : 0;
		aux = SolicitacaoDAO.listStatus("Reencaminhada");
		solicitacaoPendente += aux != null ? aux.size() : 0;
		aux = SolicitacaoDAO.listStatus("Prorrogada");
		solicitacaoPendente += aux != null ? aux.size() : 0;
		aux = SolicitacaoDAO.listStatus("Transição");
		solicitacaoPendente += aux != null ? aux.size() : 0;
		
		aux = SolicitacaoDAO.listStatus("Finalizada");
		solicitacaoFinalizadas = aux != null ? aux.size() : 0;
		aux = SolicitacaoDAO.listStatus("Sem Resposta");
		solicitacaoFinalizadas += aux != null ? aux.size() : 0;
		aux = SolicitacaoDAO.listStatus("Negada");
		solicitacaoFinalizadas += aux != null ? aux.size() : 0;

		if(visualizaDenunciaNaBoard()) {
				aux = SolicitacaoDAO.listPorTipo("Denúncia");
				solicitacaoTotal += aux != null ? aux.size() : 0;
				solicitacaoDenuncia = aux != null ? aux.size() : 0;
				
				aux = SolicitacaoDAO.listPorTipoStatus("Denúncia", "Finalizada");
				solicitacaoFinalizadas += aux != null ? aux.size() : 0;
				aux = SolicitacaoDAO.listPorTipoStatus("Denúncia", "Atendida");
				solicitacaoRespondida += aux != null ? aux.size() : 0;
				aux = SolicitacaoDAO.listPorTipoStatus("Denúncia", "Aberta");
				solicitacaoPendente += aux != null ? aux.size() : 0;
			
		}
		
	}

	public static void addQuantidadeSolicitacaoTotal() {
		solicitacaoTotal++;
	}

	public static void addQuantidadeSolicitacaoPendente() {
		solicitacaoPendente++;
	}

	public static void addQuantidadeSolicitacaoNegada() {
		solicitacaoNegada++;
	}

	public static void addQuantidadeSolicitacaoRespondida() {
		solicitacaoRespondida++;
	}

	public static void rmvQuantidadeSolicitacaoTotal() {
		solicitacaoTotal--;
	}

	public static void rmvQuantidadeSolicitacaoPendente() {
		solicitacaoPendente--;
	}

	public static void rmvQuantidadeSolicitacaoNegada() {
		solicitacaoNegada--;
	}

	public static void rmvQuantidadeSolicitacaoRespondida() {
		solicitacaoRespondida--;
	}
	
	public static void addQuantidadeSolicitacaoFinalizada() {
		solicitacaoFinalizadas++;
	}
	
	public static void rmvQuantidadeSolicitacaoFinalizada() {
		solicitacaoFinalizadas--;
	}

	public static boolean visualizaDenunciaNaBoard() {
		UsuarioBean u = (UsuarioBean) (HibernateUtil.RecuperarDaSessao("usuario"));
		short perfil = u.getUsuario().getPerfil();
		boolean perfilValido = perfil == (short)2 || perfil == (short) 4 ||perfil == (short)5 || perfil == (short)6 ? true : false;
		
		List<Responsavel> r = ResponsavelDAO.findResponsavelUsuarioAtivo(u.getUsuario().getIdUsuario());
		boolean respValido = false;
		String nomeDaEntidade = "Controladoria Geral do Estado";
		String siglaEntidade =  "CGE";
		if(!r.isEmpty()) {
			for(Responsavel resp : r) {
				if(resp.getNivel() == (short)3 &&
				   resp.getEntidades().getIdEntidades() == (EntidadesDAO.FindSigla(siglaEntidade).get(0).getIdEntidades())) {
					respValido = true;
					break;
				}
			}
		}
		
		return (perfilValido && respValido);
	}
	public void visualizouSolicitacao(Solicitacao solicitacao) {
		if (((userBean.getUsuario().getPerfil() == (short) 2) || (userBean.getUsuario().getPerfil() == (short) 4 && userBean.isPerfilAlterarCidadaoResponsavel()))) {
			if (!solicitacao.isVisualizada()) {
				MensagemBean.salvarStatus(solicitacao, "Visualizada", null, null,0);
				solicitacao.setVisualizada(true);
				SolicitacaoDAO.saveOrUpdate(solicitacao);
			} else if (solicitacao.getStatus().equals("Reencaminhada")) {
				solicitacao.setStatus("Aberta");
				solicitacao.setVisualizada(true);
				SolicitacaoDAO.saveOrUpdate(solicitacao);
				MensagemBean.salvarStatus(solicitacao, "Visualizada", null, null, 0);
			}
		}
	}

	// +++++++++++++++++++++++++++ Tipologias das solicitações - Tratamentos
	// específicos

	public String Denuncia() {
		solicitacao.setEntidades(EntidadesDAO.FindSigla("CGE").get(0));
		solicitacao.setTipo("Denúncia");
		setModoAnonimo(false);
		setModoSigilo(true);
		setModoIdentificavel(false);

		CompetenciasBean.idCompetencias = 0;
		AcoesBean.carregarLista();
		return "/Solicitacao/solicitacao.xhtml?faces-redirect=true";
	}
	
	public void DenunciaOuSigiloOuIdentificavel(AjaxBehaviorEvent e) {
		
		if(modoAnonimo) {
			if(modoSigilo) {
				setModoSigilo(false);
			}
			
			if(modoIdentificavel) {
				setModoIdentificavel(false);
			}
		}
	}
	
	public void SigiloOuDenunciaOuIdentificavel(AjaxBehaviorEvent e) {
		if(modoSigilo) {
			if(modoAnonimo) {
				setModoAnonimo(false);
			}
			if (modoIdentificavel) {
				setModoIdentificavel(false);
			}
		}
	}

	public void IdentificavelOuSigiloOuDenuncia(AjaxBehaviorEvent e) {
		if(modoIdentificavel) {
			if(modoAnonimo) {
				setModoAnonimo(false);
			}
			
			if (modoSigilo) {
				setModoSigilo(false);
			}
		}
	}

	public boolean estaUsuarioLogado() {
		UsuarioBean usuarioBean = (UsuarioBean) HibernateUtil.RecuperarDaSessao("usuario");
		if (usuarioBean != null) {
			return true;
		} else {
			return false;
		}
	}

	// +++++++++++++++++++++++++++ Redirecionamento de paginas

	public String questionarioParaSolicitacao() {
		if (idCompetencias == 0 || idEntidades == 0) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Não permite campos vazios.", "Preencha os campos."));
			return null;
		} else {
			solicitacao.setEntidades(EntidadesDAO.find(idEntidades));
			solicitacao.setCompetencias(CompetenciasDAO.findCompetencias(idCompetencias));
			return "/Solicitacao/solicitacao.xhtml";
		}
	}

	// +++++++++++++++++++++++++++ Verificação dos Status em relação ao Tempo

	private void alterarPrazo(Solicitacao solicitacao) {
		if (solicitacao != null) {
			solicitacao.setStatus(status);
			if (solicitacao.getStatus().equals("Prorrogar")) {
				solicitacao.setDataLimite(PrazosSolicitacao.gerarPrazoDiaUtilLimite(solicitacao.getDataLimite(), PrazosSolicitacao.prazoResposta(status)));
			} else {
				solicitacao.setDataLimite(PrazosSolicitacao.gerarPrazoDiaUtilLimite(new Date(System.currentTimeMillis()), PrazosSolicitacao.prazoResposta(status)));
			}
			SolicitacaoDAO.saveOrUpdate(solicitacao);
			MensagemBean.salvarStatus(solicitacao, solicitacao.getStatus(), null, null, 0);
		}

	}

	public boolean ehProrrogavel() {
		if (solicitacao.getDataLimite() != null) {

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
		} else {
			return true;
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
		try {
			if (!verificaSeLimiteRecurso(solicitacao)
					&& (solicitacao.getStatus().equals("Atendida") || solicitacao.getStatus().equals("Negada") || solicitacao.getStatus().equals("Sem Resposta"))) {
				return true;
			} else {
				return false;
			}
		} catch (NullPointerException e) {
			return false;
		}
	}

	public void prorrogar() {
//		alterarPrazo(solicitacao);
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
		NotificacaoEmail.enviarEmailNotificacaoRecurso(solicitacao);
		addQuantidadeSolicitacaoPendente();
		rmvQuantidadeSolicitacaoRespondida();
		mensagem = new Mensagem();
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
		return solicitacao.isEncaminhada();
	}

	public boolean ehEncaminhavel() {
		if (!verificaSeEncaminhada(solicitacao) && PrazosSolicitacao.verificaSeEncaminhavel(solicitacao)) {
			return true;
		} else {
			return false;
		}
	}

	// +++++++++++++++++++++++++++ Reencaminhar

	public void encaminhar() {
		Responsavel respRemetente ;
		if (!solicitacao.isEncaminhada()) {

			popularEncaminharEntidade();

			entReencaminhar = EntidadesDAO.find(idEntidades);
			Usuario usuario = ((UsuarioBean) HibernateUtil.RecuperarDaSessao("usuario")).getUsuario();
			if(usuario.getPerfil() == (short)5 || usuario.getPerfil() == (short)6 ) {
				respRemetente = new Responsavel();
				respRemetente.setUsuario(usuario);
				respRemetente.setEmail("admin_esic@cge.se.gov.br");
			}else {
				respRemetente = ResponsavelDAO.findResponsavelUsuario(usuario.getIdUsuario()).get(0);
			}
			int idResp = ResponsavelBean.responsavelDisponivel(1, entReencaminhar.getIdEntidades());
			Responsavel respDestinatario = new Responsavel();
			if (idResp == -1) {
				respDestinatario = ResponsavelDAO.findResponsavel(ResponsavelBean.responsavelDisponivel(1, 1));
			} else {
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
				NotificacaoEmail.enviarEmailNotificacaoCidadao(solicitacao, mensagem);
			}
			;
			mensagem = new Mensagem();

			// Encaminhar
			if (!solicitacao.isEncaminhada()) {

				this.solicitacao.setEntidades(entReencaminhar);
				solicitacao.setEncaminhada(true);
				solicitacao.setStatus("Reencaminhada");

				if (SolicitacaoDAO.saveOrUpdate(solicitacao)) {
					this.mensagemEncaminhar.setSolicitacao(solicitacao);
					this.mensagemEncaminhar.setTipo((short) 5);
					this.mensagemEncaminhar.setUsuario(usuario);
					this.mensagemEncaminhar.setData(new Date(System.currentTimeMillis()));
					if (MensagemDAO.saveOrUpdate(mensagemEncaminhar)) {
						MensagemBean.attMensagemTramites(mensagemEncaminhar);
						MensagemBean.salvarStatus(solicitacao, "Encaminhada", solicitacao.getEntidades().getNome(),	antigaEnt.getNome(), 0);
						NotificacaoEmail.enviarEmailTramites(solicitacao, mensagemEncaminhar.getTexto(), respRemetente,	respDestinatario);
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
		if (form) {
			CompetenciasBean.idEntidade = 0;
		} else {
			CompetenciasBean.listEntidades = null;
			CompetenciasBean.idAcoes = 0;
			AcoesBean.carregarLista();
		}
	}

	public String redirecionarEstatistica() {
		RelatorioDinamico rel = new RelatorioDinamico();
		return "/Relatorios/relatorios-especificos.xhtml";
	}
	
	
	public void testeData() throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy");
		Date date = sdf.parse("01/09/2018");
		System.out.println( PrazosSolicitacao.gerarPrazoDiaUtilLimite(date, PrazosSolicitacao.prazoResposta("Aberta")) );
	}
	
	public void onToggle(ToggleEvent e) {
		list.set((Integer) e.getData(), e.getVisibility() == Visibility.VISIBLE);
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

//	public Anexo getAnexo() {
//		return anexo;
//	}
//
//	public void setAnexo(Anexo anexo) {
//		this.anexo = anexo;
//	}

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

	public void setForm(boolean form) {
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

	public List<Solicitacao> getSolicitacoesFiltradas() {
		return solicitacoesFiltradas;
	}

	public void setSolicitacoesFiltradas(List<Solicitacao> solicitacoesFiltradas) {
		this.solicitacoesFiltradas = solicitacoesFiltradas;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public int getSolicitacaoTotal() {
		return solicitacaoTotal;
	}

	@SuppressWarnings("static-access")
	public void setSolicitacaoTotal(int solicitacaoTotal) {
		this.solicitacaoTotal = solicitacaoTotal;
	}

	public int getSolicitacaoPendente() {
		return solicitacaoPendente;
	}

	@SuppressWarnings("static-access")
	public void setSolicitacaoPendente(int solicitacaoPendente) {
		this.solicitacaoPendente = solicitacaoPendente;
	}

	public int getSolicitacaoNegada() {
		return solicitacaoNegada;
	}

	@SuppressWarnings("static-access")
	public void setSolicitacaoNegada(int solicitacaoNegada) {
		this.solicitacaoNegada = solicitacaoNegada;
	}

	public int getSolicitacaoRespondida() {
		return solicitacaoRespondida;
	}

	@SuppressWarnings("static-access")
	public void setSolicitacaoRespondida(int solicitacaoRespondida) {
		this.solicitacaoRespondida = solicitacaoRespondida;
	}
	
	public int getSolicitacaoDenuncia() {
		return solicitacaoDenuncia;
	}

	@SuppressWarnings("static-access")
	public void setSolicitacaoDenuncia(int solicitacaoDenuncia) {
		this.solicitacaoDenuncia = solicitacaoDenuncia;
	}
	
	public int getSolicitacaoFinalizadas() {
		return solicitacaoFinalizadas;
	}

	@SuppressWarnings("static-access")
	public void setSolicitacaoFinalizadas(int solicitacaoFinalizadas) {
		this.solicitacaoFinalizadas = solicitacaoFinalizadas;
	}

	public boolean isModoSigilo() {
		return modoSigilo;
	}

	public void setModoSigilo(boolean modoSigilo) {
		this.modoSigilo = modoSigilo;
	}

	public int getIdCompetencias() {
		return idCompetencias;
	}

	public void setIdCompetencias(int idCompetencias) {
		this.idCompetencias = idCompetencias;
	}

	public boolean isModoIdentificavel() {
		return modoIdentificavel;
	}

	public void setModoIdentificavel(boolean modoIdentificavel) {
		this.modoIdentificavel = modoIdentificavel;
	}
	
	public List<Boolean> getList() {
		return list;
	}

	public void setList(List<Boolean> list) {
		this.list = list;
	}

}