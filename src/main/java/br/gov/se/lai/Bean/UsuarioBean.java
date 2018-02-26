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
import br.gov.se.lai.utils.NotificacaoEmail;
import br.gov.se.lai.utils.verificarStatusSolicitacao;

@ManagedBean(name = "usuario")
@SessionScoped
public class UsuarioBean implements Serializable {

	private static final long serialVersionUID = 4098925984824190470L;
	private Usuario usuario;
	private Usuario usuarioNovo;
	private String senha;
	private String nick;
	private String nome;
	private String nomeCompleto;
	private String email;
	private String emailRedirect;
	private String tipoString;
	private int veioDeSolicitacao;
	private String escolaridade;
	private Date dataHoje = new Date();
	public boolean alterarSenha = false;
	private String codigoRedefSenha;
	private String codigoURLTemporaria;
	private String sessionId;
	private boolean perfilAlterarCidadaoResponsavel;

	/*
	 * Instanciar objeto, iniciar verificação constante dos status de solicitações
	 * do banco de dados
	 */
	@PostConstruct
	public void init() {
		usuario = new Usuario();
		usuarioNovo = new Usuario();
		perfilAlterarCidadaoResponsavel = false;
		SchedulerFactory shedFact = new StdSchedulerFactory();
		try {
			Scheduler scheduler = shedFact.getScheduler();
			scheduler.start();
			JobDetail job = JobBuilder.newJob(verificarStatusSolicitacao.class)
					.withIdentity("verificarStatusSolicitacao", "grupo01").build();
			Trigger trigger = TriggerBuilder.newTrigger().withIdentity("validadorTRIGGER", "grupo01")
					.withSchedule(CronScheduleBuilder.cronSchedule("0 0/2 10 * * ?")).build();
			scheduler.scheduleJob(job, trigger);
//			 JobDetail jobEmail = JobBuilder.newJob(NotificacaoEmail.class).withIdentity("enviarEmailAutomatico", "grupo02").build();
//			 Trigger triggerEmail = TriggerBuilder.newTrigger().withIdentity("validadorTRIGGER2", "grupo02").withSchedule(CronScheduleBuilder.cronSchedule("0 5 0 * * ?")).build();
//			 scheduler.scheduleJob(jobEmail, triggerEmail);
		} catch (SchedulerException e) {
			System.out.println(e.getMessage());
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

	/*
	 * Operações:
	 * 
	 * save() - Salvar novo usuário edit() - edita valores do usuário delete() -
	 * apagar usuario sugestaoNick() - Sugerir nick para o usuário na hora de
	 * preencher o cadastro de usuário verificaExistenciaNick(String nick) -
	 * Verifica se o nick digitado/sugerido já existe, retorno booleano.
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
						"Nick já existente no sistema.", "Escolha outro."));
				usuario = new Usuario();
				return null;
			}
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
					"Não é possível cadastrar usuário.", "Preencha os campos vazios."));
			usuario = new Usuario();
			return null;
		}

	}

	public void cadastrarCidadao() {
		save();
	}
	
	public String criarNovoUsuarioPorGestor() { 
		if (!verificaSeVazio(usuarioNovo.getNome()) == true && !verificaSeVazio(usuarioNovo.getSenha()) == true
				&& !verificaSeVazio(usuarioNovo.getNick()) == true) {
			senha = usuarioNovo.getSenha();
			usuarioNovo.setSenha(Criptografia.Criptografar(senha));
			usuarioNovo.setPerfil((short) 1);
			if (!verificaExistenciaNick(usuarioNovo.getNick())) {
				UsuarioDAO.saveOrUpdate(usuarioNovo);
				return "/index.xhtml?faces-redirect=true";
				
			} else {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
						"Nick já existente no sistema.", "Escolha outro."));
				usuarioNovo = new Usuario();
				return null;
			}
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
					"Não é possível cadastrar usuário.", "Preencha os campos vazios."));
			usuarioNovo = new Usuario();
			return null;
		}
	}

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
	public boolean verificaSeVazio(String campo) {
		String verificacao = campo.replaceAll(" ", "");
		if (verificacao.equals("")) {
			return true;
		} else {
			return false;
		}
	}

	public String delete() {
		UsuarioDAO.delete(usuario);
		return "/index.xhtml";
	}

	public String edit() {
		if (Criptografia.Comparar(Criptografia.Criptografar(senha), usuario.getSenha())) {
			UsuarioDAO.saveOrUpdate(usuario);

			if (usuario.getPerfil() != 1) {

				if (usuario.getPerfil() == 3) {
					CidadaoBean cidadao = new CidadaoBean();
					cidadao.save();
				} else {
					ResponsavelBean responsavel = new ResponsavelBean();
					responsavel.save();
				}
			}

			if (usuario.getPerfil() != 1) {
				if (usuario.getPerfil() == 3) {
					List<Cidadao> listCidadao = new ArrayList<Cidadao>(usuario.getCidadaos());
					listCidadao.get(0).setEmail(email);
					;
				} else {
					List<Responsavel> listResponsavel = new ArrayList<Responsavel>(usuario.getResponsavels());
					listResponsavel.get(0).setEmail(email);
					;
				}
			}
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "Atualização concluída.", "Sucesso."));
			return "/index.xhtml?faces-redirect=true";
		} else {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro na validação.", "Tente novamente."));
			return null;
		}

	}

	private boolean verificaExistenciaNick(String nick) {
		if (UsuarioDAO.buscarUsuario(nick) != null) {
			return true;
		} else {
			return false;
		}
	}

	public void sugestaoNick(String nome) {
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
			
			if(usuario.getPerfil() == 0) {
				this.usuario.setNick(this.nick);
			}else {
				this.usuarioNovo.setNick(nick);
			}
			
			nomeCompleto(usuario.getNome());

		}

	}

	/*
	 * login() - Login do usuario no sistema. logout() - Logout do usuario no
	 * sistema. loadEmail() - Buscar email daquele usuario, retorna string.
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
				loadEmail();
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Login executado com sucesso."));
				acessoUsuario(this.usuario);
				nomeCompleto(usuario.getNome());
				return "/index?faces-redirect=true";
			}
		}
	}

	public void loadEmail() {
		if (usuario.getPerfil() == 3 && !usuario.getCidadaos().isEmpty()) {
			List<Cidadao> listCidadao = new ArrayList<Cidadao>(usuario.getCidadaos());
			setEmail(listCidadao.get(0).getEmail());
		} else {
			if (usuario.getPerfil() == 2 && !usuario.getResponsavels().isEmpty()) {
				List<Responsavel> listResponsavel = new ArrayList<Responsavel>(usuario.getResponsavels());
				setEmail(listResponsavel.get(0).getEmail());
			} else {
				setEmail("Não cadastrado");
			}
		}
	}

	public String logout() {
		FacesContext fc = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
		session.invalidate();
		this.usuario = null;
		return "/index";
	}

	public void generateSessionId() {
		int len = 45;
		String charsCaps = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String Chars = "abcdefghijklmnopqrstuvwxyz";
		String nums = "0123456789";
		String passSymbols = charsCaps + Chars + nums;
		Random rnd = new Random();
		this.sessionId = "";

		for (int i = 0; i < len; i++) {
			this.sessionId += passSymbols.charAt(rnd.nextInt(passSymbols.length()));
		}

	}

	@SuppressWarnings("unused")
	private void acessoUsuario(Usuario usuario) {
		generateSessionId();
		usuario.setLastLogged(new Date(System.currentTimeMillis()));
		usuario.setSessionId(sessionId);
		UsuarioDAO.saveOrUpdate(usuario);
	}

	/*
	 * emailRedefinirSenha() - Gera access_key e chama método para enviar email para
	 * o usuario solicitado pegaParamURL() - Pega o access_key da URL que está
	 * referenciando a um usuario tratarEmail() - Verifica se o email digitado
	 * pertence a algum órgão do governo do estado de Sergipe
	 */

	public String redirectRedefSenha() {
		if (pegarParamURL() == null) {
			return "/Alterar/redefinir_senha_email.xhtml";
		} else {
			return "/Alterar/redefinir_senha.xhtml";
		}
	}

	public void redefinirSenha() {
		if (!verificarParamURL()) {
			try {
				emailRedefinirSenha();
			} catch (Exception e) {
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
								new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro na validação.", "Senha inválida"));

					}
				}
			} catch (EmailException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	public String alterarDadosUsuario() {
		if (usuario.getPerfil() == (short) 3 || usuario.getPerfil() == (short) 4) {
			return "Alterar/alterar_usuario";
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Usuário básico.", "Por favor complete seu cadastro."));
			return null;
		}
	}

	public boolean tratarEmail(String email) {
		int retorno = 0;
		String[] emailSplit = email.split("@");
		String dominios = emailSplit[1].replace(".", "_");
		String[] dominiosArray = dominios.split("_");
		for (String dominio : dominiosArray) {
			if (dominio.equals("gov") || dominio.equals("se")) {
				retorno += 1;
			}
		}

		if (retorno >= 1) {
			return true;
		} else {
			return false;
		}

	}

	public void emailRedefinirSenha() {
		if (!verificaSeVazio(emailRedirect)) {
			if (tratarEmail(emailRedirect)) {
				Responsavel resp = (Responsavel) ResponsavelDAO.findResponsavelEmail(emailRedirect);
				if (!resp.equals(null)) {
					acessoUsuario(resp.getUsuario());
					String accessKey = resp.getUsuario().getSessionId();
					NotificacaoEmail.enviarEmailRedefinicaoSenha(accessKey, emailRedirect);
					usuario = new Usuario();
				}

			} else {
				Cidadao cid = CidadaoDAO.findCidadaoEmail(emailRedirect);
				if (!cid.equals(null)) {
					acessoUsuario(cid.getUsuario());
					String accessKey = cid.getUsuario().getSessionId();
					NotificacaoEmail.enviarEmailRedefinicaoSenha(accessKey, emailRedirect);
					usuario = new Usuario();
				}

			}

		} else {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_WARN, "Campo vazio", null));

		}

	}

	public String pegarParamURL() {
		codigoRedefSenha = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
				.get("access_key");
		// codigoURLTemporaria =
		// FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
		// .get("access_expire_date");
		return codigoRedefSenha;
	}

	public boolean verificarParamURL() {
		if (codigoRedefSenha != null) {
			return true;
		} else {
			return false;
		}
	}

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

	public void pegarUsuarioURL(String codigoRedefSenha) throws EmailException {
		Usuario usuario = UsuarioDAO.buscarSessionIds(codigoRedefSenha);

		// Aqui descriptografa o access_key e pega o email e data

		if (usuario.getPerfil() == 2) {
			Responsavel resp = ResponsavelDAO.findResponsavelUsuario(usuario.getIdUsuario()).get(0);
			if (!resp.equals(null)) {
				this.usuario = resp.getUsuario();
				NotificacaoEmail.enviarEmail(resp.getEmail(), "Alteração de Email",
						"Sua senha para o login no E-SIC foi alterada.");
			}
			;
		} else {
			Cidadao cid = CidadaoDAO.findCidadaoUsuario(usuario.getIdUsuario());
			if (!cid.equals(null)) {
				this.usuario = cid.getUsuario();
				NotificacaoEmail.enviarEmail(cid.getEmail(), "Alteração de Email",
						"Sua senha para o login no E-SIC foi alterada.");
			}
			;
		}

	}

	/*
	 * getGeneroString() - Formatar saída de dados Gênero do banco de dados para
	 * exibição no sistema getEscolaridade() - Formatar saída dos dados Escolaridade
	 * do banco de dados para exibição no sistema getTipoString() - Formatar saída
	 * de dados Tipo de Pessoa do banco de dados para exibição no sistema
	 */

	public String getGeneroString() {
		if (getCidadao().getSexo().equals("F")) {
			return "Feminino";
		} else {
			return "Masculino";
		}

	}

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
				escolaridade = "Ensino Médio Incompleto";
			case 5:
				escolaridade = "Ensino Médio Completo";
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

	public String getTipoString() {
		if (getCidadao().getTipo() == true) {
			;
			return "Física";
		} else {
			return "Jurídica";
		}

	}

	public static String deAccent(String str) {
		String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD);
		Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
		return pattern.matcher(nfdNormalizedString).replaceAll("");
	}
	
	public static List<String> completeNick (String prefix) {
		List<String> nicks = UsuarioDAO.completeNick(prefix);
		return nicks;
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
	}
	
	

}
