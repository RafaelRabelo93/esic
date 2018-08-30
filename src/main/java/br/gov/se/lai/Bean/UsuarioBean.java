package br.gov.se.lai.Bean;

import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.servlet.http.HttpSession;
import javax.swing.text.DateFormatter;

import org.apache.commons.mail.EmailException;
import org.hibernate.id.UUIDGenerationStrategy;
import org.quartz.CronScheduleBuilder;
import org.quartz.DateBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.ObjectAlreadyExistsException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import br.gov.se.lai.DAO.CidadaoDAO;
import br.gov.se.lai.DAO.ResponsavelDAO;
import br.gov.se.lai.DAO.UsuarioDAO;
import br.gov.se.lai.entity.Cidadao;
import br.gov.se.lai.entity.Responsavel;
import br.gov.se.lai.entity.Usuario;
import br.gov.se.lai.utils.Criptografia;
import br.gov.se.lai.utils.HibernateUtil;
import br.gov.se.lai.utils.NotificacaoEmail;
import br.gov.se.lai.utils.verificarStatusSolicitacao;

@ManagedBean(name = "usuario")
@SessionScoped
public class UsuarioBean implements Serializable {

	private static final long serialVersionUID = 4098925984824190470L;
	private Usuario usuario;
	private Usuario usuarioNovo;
	private String senha;
	private String senhaAtual;
	private String novaSenha;
	private String nick;
	private String nome;
	private String nomeCompleto;
	private String email;
	private String emailCid;
	private String sigla;
	private String emailRedirect;
	private String tipoString;
	private int veioDeSolicitacao;
	private String escolaridade;
	private Date dataHoje = new Date();
	public boolean alterarSenha = false;
	private String codigoRedefSenha;
	private String codigoURLTemporaria;
	private static String sessionId;
	public List<Usuario> filteredGestores;
	public static boolean perfilAlterarCidadaoResponsavel;
	private String[] palavrasReservadas = {"admin", "administrador", "sistema", "gestor", "gestorsistema", "gestor.sistema", "anonimo", "teste", "administrator"
			, "sistema.gestor","sistemagestor", "usuario", "sudo", "sudo.admin"};
	private boolean acessoMigrado;

	/*
	 * Instanciar objeto, iniciar verifica��o constante dos status de solicita��es
	 * do banco de dados
	 */
	@PostConstruct
	public void init() {
		usuario = new Usuario();
		usuarioNovo = new Usuario();
		perfilAlterarCidadaoResponsavel = false;
		SchedulerFactory sf = new StdSchedulerFactory();
		try {
			Scheduler sched = sf.getScheduler();
			JobDetail job = JobBuilder.newJob(verificarStatusSolicitacao.class)
					.withIdentity("job1", "group1")
					.build();
			
			Date runTime = DateBuilder.evenMinuteDate(new Date());
			
			Trigger trigger = TriggerBuilder.newTrigger()
					.withIdentity("trigger1", "group1")
					.startAt(runTime)
					.build();
			
			sched.scheduleJob(job, trigger);
			sched.start();
			Thread.sleep(90L * 1000L);
			sched.shutdown(true);
			
//			sched.start();
//			JobDetail job = JobBuilder.newJob(verificarStatusSolicitacao.class).withIdentity("verificarStatusSolicitacao", "grupo01").build();
//			Trigger trigger = TriggerBuilder.newTrigger().withIdentity("validadorTRIGGER", "grupo01").withSchedule(CronScheduleBuilder.cronSchedule("0 1 0 * * ?")).build();
//			sched.scheduleJob(job, trigger);
		} catch (SchedulerException e) {
			System.out.println(e.getMessage());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void redirectPages(String pageUrl) throws IOException {
		FacesContext.getCurrentInstance().getExternalContext().redirect(pageUrl);
	}

	public void Data() throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
		String dataStr = "10/10/2017 10:40:20";
		java.sql.Date data = new java.sql.Date(format.parse(dataStr).getTime());

		System.out.println(data);
	}

	/**
	 * save() - Salvar novo usu�rio 
	 * edit() - edita valores do usu�rio 
	 * delete() - apagar usuario 
	 * gerarNick() - Sugerir nick para o usu�rio na hora de preencher o cadastro de usu�rio 
	 * verificaExistenciaNick(String nick) - Verifica se o nick digitado/sugerido j� existe, retorno booleano.
	 * verificaSeVazio(String campo) - retorna valor booleano se determinado campo
	 * estiver vazio
	 */
	public String save() {

		if (!verificaSeVazio(usuario.getNome()) == true && !verificaSeVazio(usuario.getSenha()) == true
				&& !verificaSeVazio(usuario.getNick()) == true) {
			senha = usuario.getSenha();
			usuario.setSenha(Criptografia.Criptografar(senha));
			usuario.setPerfil((short) 1);
			if (!verificaExistenciaNick(usuario.getNick())) {
				UsuarioDAO.saveOrUpdate(usuario);
				if (veioDeSolicitacao == 0) {
					nick = usuario.getNick();
					login();
					return "/Cadastro/confirmacao?faces-redirect=true";
				} else {
					nick = usuario.getNick();
					login();
					return "/Cadastro/confirmacao?faces-redirect=true";
				}
			} else {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
						"Nick j� existente no sistema.", "Escolha outro."));
				usuario = new Usuario();
				return null;
			}
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
					"N�o � poss�vel cadastrar usu�rio.", "Preencha os campos vazios."));
			usuario = new Usuario();
			return null;
		}

	}
	
	public String saveGestorSistema() {
		if(verificaAdmin() || verificaGestor()) {
			usuarioNovo = UsuarioDAO.buscarUsuario(usuarioNovo.getNick());
			usuarioNovo.setPerfil((short)5);
			if(UsuarioDAO.saveOrUpdate(usuarioNovo)) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
						"Cadastro realizado com sucesso.", " "));
			}else{;
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"N�o � poss�vel cadastrar usu�rio.", "Um erro inesperado ocorreu, tente novamente mais tarde."));
			}
			return "/Consulta/consulta_responsavel.xhtml";
		}else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
					"N�o � poss�vel cadastrar gestor.", "O usu�rio n�o possui permiss�o."));
			return null;
		}
	}
	
	/**
	 * Fun��o nickUsuarioInvalido
	 * 
	 * Verifica se o nome digitado pelo usu�rio se enquadra 
	 * em palavras reservadas do sistema.
	 * 
	 * @param nick
	 * @return
	 */
	public boolean nickUsuarioInvalido(String nick) {
		boolean retorno = false;
		for (String string : palavrasReservadas) {
			if(nick.contains(string)) {
				retorno =  true;
				break;
			}
		}
		return retorno;
	}

	/**
	 * Salvar cidad�o
	 */
	public void cadastrarCidadao() {
		save();
	}
	
	public boolean usuarioLogado() {
		return (usuario.getNome() != null);
	}
	
	public String redirecionamentoNegado() {
		if(usuarioLogado()) {
			return null ;
		}else {
			return  "/Menu/acessoNegado.xhtml?faces-redirect=true" ;
		}
	}
	
	/**
	 * Criar um novo usu�rio sem sobrepor o usu�rio logado.
	 * Fun��o espec�fica para gestor e administrador do sistema.
	 * @return
	 */
	public String criarNovoUsuarioPorGestor() { 
		if (!verificaSeVazio(usuarioNovo.getNome()) == true && !verificaSeVazio(usuarioNovo.getSenha()) == true
				&& !verificaSeVazio(usuarioNovo.getNick()) == true) {
			senha = usuarioNovo.getSenha();
			usuarioNovo.setSenha(Criptografia.Criptografar(senha));
			usuarioNovo.setPerfil((short) 1);
			if (!verificaExistenciaNick(usuarioNovo.getNick())) {
				if(UsuarioDAO.saveOrUpdate(usuarioNovo)) {
					return "/index.xhtml?faces-redirect=true";
				}else {
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
							"N�o � poss�vel cadastrar usu�rio.", "Preencha os campos vazios."));
					usuarioNovo = new Usuario();
					return null;
				}
				
			} else {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
						"Nick j� existente no sistema.", "Escolha outro."));
				usuarioNovo = new Usuario();
				return null;
			}
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
					"N�o � poss�vel cadastrar usu�rio.", "Preencha os campos vazios."));
			usuarioNovo = new Usuario();
			return null;
		}
	}

	/**
	 * Gera o nome completo a partir da uni�o do primeiro e ultimo nome 
	 * do usu�rio.
	 * @param nomeCompleto
	 */
	public void nomeCompleto(String nomeCompleto) {
		if (nomeCompleto != null) {
			String[] nomeSobrenome = nomeCompleto.split(" ");
			if (nomeSobrenome.length > 1) {
				this.nome = nomeSobrenome[0] + " " + nomeSobrenome[nomeSobrenome.length - 1];
			} else {
				this.nome = nomeSobrenome[0];
			}
		}
	}
	
	/**
	 * Verifica se o usu�rio deixou algum campo importante vazio.
	 * @param campo
	 * @return
	 */
	public boolean verificaSeVazio(String campo) {
		String verificacao = campo.replaceAll(" ", "");
		if (verificacao.equals("")) {
			return true;
		} else {
			return false;
		}
	}
	
	public void limparUsuarioNovo() {
		usuarioNovo = new Usuario();
	}
	

//	public String delete() {
//		UsuarioDAO.delete(usuario);
//		return "/index.xhtml";
//	}

	/**
	 * Atualiza as informa��es do usu�rio
	 * @return
	 */
	public String edit() {
//		this.usuario = ((UsuarioBean)  HibernateUtil.RecuperarDaSessao("usuario")).getUsuario();
		
//		if (Criptografia.Comparar(Criptografia.Criptografar(senha), this.usuario.getSenha())) {
			
//		System.out.println(usuario.getPerfil());
		
			if (usuario.getPerfil() != 1) {

				if (usuario.getPerfil() == 3) {
					CidadaoBean cidadao = new CidadaoBean();
					Set<Cidadao> cidadao2 = this.usuario.getCidadaos();
					Cidadao cidadao3 = cidadao2.iterator().next();
//					cidadao.save();
					cidadao.edit(cidadao3);
				} else {
					ResponsavelBean responsavel = new ResponsavelBean();
					responsavel.save();
				}
			}

			if (usuario.getPerfil() != 1) {
				if (usuario.getPerfil() == 3) {
					List<Cidadao> listCidadao = new ArrayList<Cidadao>(usuario.getCidadaos());
					listCidadao.get(0).setEmail(email);
				} else {
					List<Responsavel> listResponsavel = new ArrayList<Responsavel>(usuario.getResponsavels());
					listResponsavel.get(0).setEmail(email);
					;
				}
			}
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "Atualiza��o conclu�da.", "Sucesso."));
			return "/index.xhtml?faces-redirect=true";
//		} else {
//			FacesContext.getCurrentInstance().addMessage(null,
//					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro na valida��o.", "Tente novamente."));
//			return null;
//		}

	}
	
	public String editarSenha() {
		if (Criptografia.Comparar(Criptografia.Criptografar(senhaAtual), this.usuario.getSenha())) {
			this.usuario.setSenha(Criptografia.Criptografar(novaSenha));
			UsuarioDAO.saveOrUpdate(this.usuario);
			return "/index.xhtml?faces-redirect=true";
		} else {
			FacesContext.getCurrentInstance().addMessage(null,
			new FacesMessage(FacesMessage.SEVERITY_ERROR, "Senha incorreta.", "Tente novamente."));
			return null;
		}
	}
	
	public void editarGestor() {	
		if(usuario.getPerfil() == (short)6  ) {
			if(!verificaSeVazio(senha)) {
			usuarioNovo.setSenha(Criptografia.Criptografar(senha));
			if(UsuarioDAO.saveOrUpdate(usuarioNovo)) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, "Atualiza��o conclu�da.", "Sucesso."));
			}else {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro na atualiza��o.", "Tente novamente mais tarde."));
			}
			
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_WARN, "Erro na atualiza��o.", "Campo senha n�o pode ser vazio."));
			}
		}else {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_WARN, "Erro na atualiza��o.", "Usu�rio sem permiss�o."));
		}
		
		senha = "";
	}

	/**
	 * Verifica se o nick j� consta no sistema.
	 * @param nick
	 * @return
	 */
	private boolean verificaExistenciaNick(String nick) {
		if (UsuarioDAO.buscarUsuario(nick) != null) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Gerar o nick a partir do nome completo do usu�rio.
	 * @param nome
	 */
	public void gerarNick(String nome) {
		int cont = 1;
		String nck = "";
		try {
			nomeCompleto(nome);
			nck = deAccent((getNomeCompleto().replace(" ", ".")).toLowerCase());
			List<Usuario> usuarios = UsuarioDAO.buscarNicks(nck);
			cont += (usuarios.size());
		} catch (NullPointerException e) {
		} catch (ArrayIndexOutOfBoundsException e) {
		} finally {
			if (cont == 1) {
				this.nick = nck;
			} else {
				this.nick = nck + cont;
			}
			
			if (nickUsuarioInvalido(nck)) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Nome de usu�rio inv�lido.", "Digite um nome v�lido."));
				this.usuario.setNick(null);
			} else {
				if (usuario.getPerfil() == 0) {
					this.usuario.setNick(this.nick);
				} else {
					this.usuarioNovo.setNick(nick);
				}
				nomeCompleto(usuario.getNome());
			}
			

		}

	}

	/**
	 * Fun��o login
	 *  Login do usuario no sistema. 
	 */

	public String login() {
		// this.logout();
		this.usuario = UsuarioDAO.buscarUsuario(this.nick);
		if (this.usuario == null) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Login ou Senha Incorretos.", "Tente novamente."));
			logout();
			return null;
		} else {
			if (!Criptografia.Comparar(Criptografia.Criptografar(senha), usuario.getSenha())) {
				FacesContext.getCurrentInstance().addMessage(null, 
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Login ou Senha Incorretos.", "Tente novamente."));
				logout();
				return null;
			} else {
				if(!usuarioInativo()) {
					FacesContext.getCurrentInstance().addMessage(null, 
							new FacesMessage(FacesMessage.SEVERITY_ERROR, "Usu�rio inativo.", "Solicite ativa��o ao gestor da sua entidade."));
					logout();
					return null;
					
				}else {
					loadEmail(this.usuario);
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Login executado com sucesso."));
					if((usuario.getLastLogged() == null || Criptografia.Comparar("8D969EEF6ECAD3C29A3A629280E686CF0C3F5D5A86AFF3CA12020C923ADC6C92", usuario.getSenha())) && usuario.isMigrado()) {
						acessoMigrado = true;
//						FacesContext.getCurrentInstance().addMessage(null,
//								new FacesMessage(FacesMessage.SEVERITY_INFO, "Primeiro login", "Esse � seu primeiro login!"));
					} else acessoMigrado = false;
					acessoUsuario(this.usuario);
					SolicitacaoBean.calcularQuantitativoSolicitacao();
					nomeCompleto(usuario.getNome());
					return "/index?faces-redirect=true";
				}
			}
		}
	}
	
	/*
	 * Esta fun��o foi criada unicamente com o intuito de permitir apenas usuarios com permiss�o privilegiada pudessem logar.
	 * Essa situa��o foi colocada apenas como um paliativo para a vers�o em produ��o, pois a vers�o que est� em produ��o ainda
	 * n�o est� aberta para o p�blico logo s� quem tem permiss�o pode entrar. Quando for lan�ada a vers�o definitiva do Esic 
	 * esta fun��o deve ser exclu�da e os logins devem ser direcionados para a fun��o login() vista acima. 
	 * @return
	 */
	public String loginSistema() {
		String retorno = "";
		this.usuario = UsuarioDAO.buscarUsuario(this.nick);
		if (this.usuario == null) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Login ou Senha Incorretos.", "Tente novamente."));
			logout();
		} else {
			if (!Criptografia.Comparar(Criptografia.Criptografar(senha), usuario.getSenha())) {
				FacesContext.getCurrentInstance().addMessage(null, 
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Login ou Senha Incorretos.", "Tente novamente."));
				logout();
			} else {
//				if(verificaAdmin() || verificaPermissaoPrivilegiada()) {
//					loadEmail(this.usuario);
//					FacesContext.getCurrentInstance().addMessage(null,
//							new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Login executado com sucesso."));
//					acessoUsuario(this.usuario);
//					nomeCompleto(usuario.getNome());
//					retorno = "/index.xhtml?faces-redirect=true";
//				}else {
//					FacesContext.getCurrentInstance().addMessage(null,
//							new FacesMessage(FacesMessage.SEVERITY_INFO, "Acesso negado.", "Voc� n�o possui permiss�o para acesso."));
//				}
				
				loadEmail(this.usuario);
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Login executado com sucesso."));
				acessoUsuario(this.usuario);
				SolicitacaoBean.calcularQuantitativoSolicitacao();

				nomeCompleto(usuario.getNome());
				retorno = "/index.xhtml?faces-redirect=true";
		}
		}
		
		return retorno;
			
	}
	
	public boolean verificaPermissaoPrivilegiada() {
		return (this.nick.equals("michael.mendonca") || this.nick.equals("mayara.machado")
				|| this.nick.equals("francyelle.mascarenhas") || this.nick.equals("rafael.oliveira") );
	}
	
	/**
	 * Usu�rio respons�vel que se encontra inativo e n�o tem 
	 * mais permiss�o de acessar o sistema como respons�vel.
	 * @return
	 */
	public boolean usuarioInativo() {
		boolean retorno = false;
		if(this.usuario.getPerfil() == (short)2 ) {
			if(ResponsavelDAO.findResponsavelUsuarioAtivo(this.usuario.getIdUsuario()).size() > 0) {
				retorno = true;
			}
		}else if(this.usuario.getPerfil() == (short)1 || this.usuario.getPerfil() == (short)3 ||this.usuario.getPerfil() == (short)4 
				|| this.usuario.getPerfil() == (short) 6 || this.usuario.getPerfil() == (short) 5 ) {
//			if(ResponsavelDAO.findResponsavelUsuarioAtivo(this.usuario.getIdUsuario()).size() > 0) {
//				retorno = true;
//			}
			retorno = true;
		}
		
		return retorno;
	}

	/**
	 *Fun��o loadEmail
	 *
	 *Busca o email daquele usuario, retorna string.
	 */
	public void loadEmail(Usuario usuario) {
		if (usuario.getPerfil() == 3 && !usuario.getCidadaos().isEmpty()) {
			List<Cidadao> listCidadao = new ArrayList<Cidadao>(usuario.getCidadaos());
			setEmailCid(listCidadao.get(0).getEmail());
		} else if (usuario.getPerfil() == 2 && !usuario.getResponsavels().isEmpty()) {
				setEmail(ResponsavelBean.retornaListaEmail(usuario));
				setSigla(ResponsavelBean.retornaListaEntidadeSiglas(usuario)); 
		}else if ( usuario.getPerfil() == 4) {
			List<Cidadao> listCidadao = new ArrayList<Cidadao>(usuario.getCidadaos());
			setEmailCid(listCidadao.get(0).getEmail());
			setEmail(ResponsavelBean.retornaListaEmail(usuario));
			setSigla(ResponsavelBean.retornaListaEntidadeSiglas(usuario)); 
		}else {
			setEmail("N�o cadastrado");
		}
	}

	/**
	 * Fun��o logout
	 * Logout do usuario no sistema. 
	 * 	@return
	 */
	public String logout() {
		FacesContext fc = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
		session.invalidate();
		this.usuario = null;
		return "/index.xhtml?faces-redirect=true";
	}

	public static String generateSessionId() {
		int len = 45;
		String charsCaps = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String Chars = "abcdefghijklmnopqrstuvwxyz";
		String nums = "0123456789";
		String passSymbols = charsCaps + Chars + nums;
		Random rnd = new Random();
		sessionId = "";

		for (int i = 0; i < len; i++) {
			sessionId += passSymbols.charAt(rnd.nextInt(passSymbols.length()));
		}
		return sessionId;

	}

	/**
	 * Gera uma nova sessionId quando o usu�rio efetua login.
	 * @param usuario
	 */
	private void acessoUsuario(Usuario usuario) {
		generateSessionId();
		usuario.setLastLogged(new Date(System.currentTimeMillis()));
		usuario.setSessionId(sessionId);
		UsuarioDAO.saveOrUpdate(usuario);
	}

	/**
	 *  pegaParamURL() - Pega o access_key da URL que est�
	 * referenciando a um usuario tratarEmail() - Verifica se o email digitado
	 * pertence a algum �rg�o do governo do estado de Sergipe
	 */

	public String redirectRedefSenha() {
		if (pegarParamURL() == null) {
			return "/Alterar/redefinir_senha_email.xhtml";
		} else {
			return "/Alterar/redefinir_senha.xhtml";
		}
	}
	
	/**
	 * Fun��o emailRedefinirSenha
	 * Gera access_key e chama m�todo para enviar email para
	 * o usuario solicitado
	 * @return
	 */

	public String redefinirSenha() {
		String retorno = null;
		if (!verificarParamURL()) {
			try {
				if(emailRedefinirSenha()) {
					retorno = "/index.xhtml?faces-redirect=true";
				}else {
					retorno = "/Menu/erroEmail.xhtml?faces-redirect=true";
				}
			} catch (Exception e) {
				retorno = "/Menu/erroEmail.xhtml";
				e.printStackTrace();
			}
		} else {
			try {
				pegarUsuarioURL(codigoRedefSenha);

				if (!verificaSeVazio(senha)) {
					String senhaCrip = Criptografia.Criptografar(senha);
					if (senhaCrip != usuario.getSenha()) {
						usuario.setSenha(senhaCrip);
						acessoUsuario(usuario);
						usuario = new Usuario();
						try {
							redirectPages("/esic");
						} catch (Exception e) {
							e.printStackTrace();
						}

					} else {
						FacesContext.getCurrentInstance().addMessage(null,
								new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro na valida��o.", "Senha inv�lida"));
						retorno = "/Menu/erroEmail.xhtml";

					}
				}
			} catch (EmailException e1) {
				e1.printStackTrace();
				retorno = "/Menu/confirmaEmail.xhtml?faces-redirect=true";
			}
		}
		return retorno;
	}
	
	/**
	 * Redireciona para p�gina alterar dados caso seja necess�rio
	 */
	public String alterarDadosUsuario() {
		if (usuario.getPerfil() == (short) 3 || usuario.getPerfil() == (short) 4) {
			return "/Alterar/alterar_usuario";
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Usu�rio b�sico.", "Por favor complete seu cadastro."));
			return null;
		}
	}

	/**
	 * Fun��o tratarEmail
	 * Verifica se o email � de um respons�vel ou de um cidad�o. 
	 * Email de respons�vel possuem extens�es relacionadas ao governo do estado.
	 * 
	 * @param email
	 * @return
	 */
	public static boolean tratarEmail(String email) {
		int retorno = 0;
		String[] emailSplit = email.split("@");
		String dominios = emailSplit[1].replace(".", "_");
		String[] dominiosArray = dominios.split("_");
		for (String dominio : dominiosArray) {
			if (dominio.equals("gov") || dominio.equals("se")) {
				retorno += 1;
			}
		}
		return (retorno >= 1);
	}

	/**
	 * Gera texto e define para qual email est�o enviando o c�digo de reacesso.
	 * 
	 */
	public boolean emailRedefinirSenha() {
		boolean valor = false;
		if (!verificaSeVazio(emailRedirect)) {
			if (tratarEmail(emailRedirect)) {
				Responsavel resp = (Responsavel) ResponsavelDAO.findResponsavelEmail(emailRedirect);
				if (!resp.equals(null)) {
					acessoUsuario(resp.getUsuario());
					String accessKey = resp.getUsuario().getSessionId();
					String nomeUser = resp.getUsuario().getNick();
					NotificacaoEmail.enviarEmailRedefinicaoSenha(accessKey, emailRedirect, nomeUser);
					usuario = new Usuario();
					valor = true;
				}

			} else {
				Cidadao cid = CidadaoDAO.findCidadaoEmail(emailRedirect);
				if (!cid.equals(null)) {
					acessoUsuario(cid.getUsuario());
					String accessKey = cid.getUsuario().getSessionId();
					String nomeUser = cid.getUsuario().getNick();
					NotificacaoEmail.enviarEmailRedefinicaoSenha(accessKey, emailRedirect, nomeUser);
					usuario = new Usuario();
					valor = true;
				}

			}

		} else {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_WARN, "Campo vazio", null));
			valor = false;

		}
		
		return valor;

	}

	/**
	 * Pegar parametro de acesso da url, links utilizados na redefini��o de senha. 
	 * @return
	 */
	public String pegarParamURL() {
		codigoRedefSenha = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
				.get("access_key");
		// codigoURLTemporaria =
		// FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
		// .get("access_expire_date");
		return codigoRedefSenha;
	}
	
	
	/**
	 * Ver se o link � de redefini��o de senha ou login normal
	 * @return
	 */
	public boolean verificarParamURL() {
		if (codigoRedefSenha != null) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Verifica se o parametro da url ainda est� em tempo v�lido para utiliza��o.
	 * @return
	 */
	@SuppressWarnings("finally")
	public boolean verificarValidadeURL() {
		boolean retorno = false;
		Usuario usuario = UsuarioDAO.buscarSessionIds(codigoRedefSenha);
		try {
			Calendar hoje = Calendar.getInstance();
			Calendar limite = Calendar.getInstance();
			limite.setTime(usuario.getLastLogged());
			limite.add(Calendar.DAY_OF_WEEK, +1);
			if (hoje.before(limite)) {
				retorno = true;
			}
		} catch (NullPointerException e) {
			usuario = new Usuario();
		} finally {
			return retorno;
		}

	}

	/**
	 * Identificar qual usu�rio est� tentando recuperar a senha a partir do c�digo da URL
	 * 
	 * @param codigoRedefSenha
	 * @throws EmailException
	 */
	public void pegarUsuarioURL(String codigoRedefSenha) throws EmailException {
		Usuario usuario = UsuarioDAO.buscarSessionIds(codigoRedefSenha);

		// Aqui descriptografa o access_key e pega o email e data

		if (usuario.getPerfil() == 2) {
			Responsavel resp = ResponsavelDAO.findResponsavelUsuario(usuario.getIdUsuario()).get(0);
			if (!resp.equals(null)) {
				this.usuario = resp.getUsuario();
				NotificacaoEmail.enviarEmailHTML(resp.getEmail(), "Altera��o de Email", "Sua senha para o login no E-SIC foi alterada.");
			}
			;
		} else {
			Cidadao cid = CidadaoDAO.findCidadaoUsuario(usuario.getIdUsuario());
			if (!cid.equals(null)) {
				this.usuario = cid.getUsuario();
				NotificacaoEmail.enviarEmailHTML(cid.getEmail(), "Altera��o de Email", "Sua senha para o login no E-SIC foi alterada.");
			}
			;
		}

	}
	

	/**
	 * Fun��o getGeneroString
	 *  Formatar sa�da de dados G�nero do banco de dados para
	 * exibi��o no sistema 
	 *  
	 */

	public String getGeneroString() {
		if (getCidadao().getSexo().equals("F")) {
			return "Feminino";
		} else {
			return "Masculino";
		}

	}

	/**
	 * Fun��o getEscolaridade
	 * Formatar sa�da dos dados Escolaridade
	 * do banco de dados para exibi��o no sistema
	 * @return
	 */
	public String getEscolaridade() {
		try {
			switch (getCidadao().getEscolaridade()) {
			case 1:
				escolaridade = "Analfabeto";
			case 2:
				escolaridade = "Semi-Analfabeto";
			case 3:
				escolaridade = "Fundamental";
			case 4:
				escolaridade = "Ensino M�dio Incompleto";
			case 5:
				escolaridade = "Ensino M�dio Completo";
			case 6:
				escolaridade = "Ensino Superior Incompleto";
			case 7:
				escolaridade = "Ensino Superior Completo";
			}
			return escolaridade;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return "...";
		}
	}
	
	/**
	 * Fun��o getTipoString
	 * 
	 * Formatar sa�da
	 * de dados Tipo de Pessoa do banco de dados para exibi��o no sistema
	 * @return
	 */

	public String getTipoString() {
		if (getCidadao().getTipo() == true) {
			;
			return "F�sica";
		} else {
			return "Jur�dica";
		}

	}

	/**
	 * Fun��o para retirar o acento dos nomes.
	 * @param str
	 * @return
	 */
	public static String deAccent(String str) {
		String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD);
		Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
		return pattern.matcher(nfdNormalizedString).replaceAll("");
	}
	
	/**
	 * Fun��o que completa a digita��o dos nicks buscando os nicks existentes no banco de dados.
	 * @param prefix
	 * @return
	 */
	public static List<String> completeNick (String prefix) {
		List<String> nicks = UsuarioDAO.completeNick(prefix);
		return nicks;
	}

	public boolean verificaAdmin() {
		return usuario.getPerfil()==(short)6;
	}
	public boolean verificaGestor() {
		return usuario.getPerfil()==(short)5;
	}
	public boolean verificaResponsavelCidadaoPerfil() {
		return usuario.getPerfil()==(short)4;
	}
	public boolean verificaResponsavel() {
		return usuario.getPerfil()==(short)2;
	}
	
	public String redirecionarIndex() {
		SolicitacaoBean t = new SolicitacaoBean();
		t.finalizarSolicitacao();
		return "../../esic/index.xhtml";
	}

	public List<Usuario> listarGestores(){
		return UsuarioDAO.listarGestoresSistema();
	}
	
//	public void emailTeste() {
//		NotificacaoEmail.emailNovaSolicitacao();
//	}

	// GETTERS E SETTERS
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	public Usuario getUsuario() {
		return this.usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getNomeCompleto() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Cidadao getCidadao() {
		List<Cidadao> listCidadao = new ArrayList<Cidadao>(usuario.getCidadaos());
		return listCidadao.get(0);
	}

	public Responsavel getResponsavel() {
		try {
			Responsavel listResponsavel = ResponsavelDAO.findResponsavelUsuario(usuario.getIdUsuario()).get(0);
			if (!listResponsavel.equals(null)) {
				return listResponsavel;
			} else {
				return null;
			}
		}catch (Exception e) {
			return null;
		}
	}

	public int getVeioDeSolicitacao() {
		return veioDeSolicitacao;
	}

	public void setVeioDeSolicitacao(int veioDeSolicitacao) {
		this.veioDeSolicitacao = veioDeSolicitacao;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setTipoString(Boolean tipo) {
		getCidadao().setTipo(tipo);
	}

	public Date getDataHoje() {
		return dataHoje;
	}

	public void setDataHoje(Date hoje) {
		this.dataHoje = hoje;
	}

	public boolean isAlterarSenha() {
		return alterarSenha;
	}

	public void setAlterarSenha(boolean alterarSenha) {
		this.alterarSenha = alterarSenha;
	}

	public String getCodigoRedefSenha() {
		return codigoRedefSenha;
	}

	public void setCodigoRedefSenha(String codigoRedefSenha) {
		this.codigoRedefSenha = codigoRedefSenha;
	}

	public String getEmailRedirect() {
		return emailRedirect;
	}

	public void setEmailRedirect(String emailRedirect) {
		this.emailRedirect = emailRedirect;
	}

	public String getCodigoURLTemporaria() {
		return codigoURLTemporaria;
	}

	public void setCodigoURLTemporaria(String codigoURLTemporaria) {
		this.codigoURLTemporaria = codigoURLTemporaria;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public Usuario getUsuarioNovo() {
		return usuarioNovo;
	}

	public void setUsuarioNovo(Usuario usuarioNovo) {
		this.usuarioNovo = usuarioNovo;
	}

	public boolean isPerfilAlterarCidadaoResponsavel() {
		return perfilAlterarCidadaoResponsavel;
	}

	public void setPerfilAlterarCidadaoResponsavel(boolean perfilAlterarCidadaoResponsavelNovo) {
		perfilAlterarCidadaoResponsavel = perfilAlterarCidadaoResponsavelNovo;
		SolicitacaoBean.calcularQuantitativoSolicitacao();
	}

	public String getSigla() {
		return sigla;
	}
	

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public String getEmailCid() {
		return emailCid;
	}

	public void setEmailCid(String emailCid) {
		this.emailCid = emailCid;
	}

	public String getNovaSenha() {
		return novaSenha;
	}

	public void setNovaSenha(String novaSenha) {
		this.novaSenha = novaSenha;
	}

	public String getSenhaAtual() {
		return senhaAtual;
	}

	public void setSenhaAtual(String senhaAtual) {
		this.senhaAtual = senhaAtual;
	}

	public List<Usuario> getFilteredGestores() {
		return filteredGestores;
	}

	public void setFilteredGestores(List<Usuario> filteredGestore) {
		this.filteredGestores = filteredGestore;
	}

	public boolean isAcessoMigrado() {
		return acessoMigrado;
	}

	public void setAcessoMigrado(boolean acessoMigrado) {
		this.acessoMigrado = acessoMigrado;
	}

	public void setTipoString(String tipoString) {
		this.tipoString = tipoString;
	}

	public void setNomeCompleto(String nomeCompleto) {
		this.nomeCompleto = nomeCompleto;
	}
	
	
	
}
