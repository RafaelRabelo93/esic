package br.gov.se.lai.Bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.servlet.http.HttpSession;

import org.hibernate.id.UUIDGenerationStrategy;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.ObjectAlreadyExistsException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import br.gov.se.lai.DAO.UsuarioDAO;
import br.gov.se.lai.entity.Cidadao;
import br.gov.se.lai.entity.Responsavel;
import br.gov.se.lai.entity.Usuario;
import br.gov.se.lai.utils.Criptografia;
import br.gov.se.lai.utils.verificarStatusSolicitacao;

@ManagedBean(name = "usuario")
@SessionScoped
public class UsuarioBean implements Serializable{

	private static final long serialVersionUID = 4098925984824190470L;
	private Usuario usuario;
	private String senha;
	private String nick;
	private String nome;
	private String nomeCompleto;
	private String email;
	private int veioDeSolicitacao;
	
	@PostConstruct
	public void init() {
		usuario = new Usuario();
		SchedulerFactory shedFact = new StdSchedulerFactory();
		try {
			Scheduler scheduler = shedFact.getScheduler();
			scheduler.start();
			JobDetail job = JobBuilder.newJob(verificarStatusSolicitacao.class).withIdentity("verificarStatusSolicitacao", "grupo01").build();
			Trigger trigger = TriggerBuilder.newTrigger().withIdentity("validadorTRIGGER", "grupo01").withSchedule(CronScheduleBuilder.cronSchedule("0 35 9 ? * *")).build();
			scheduler.scheduleJob(job, trigger);
		} catch (SchedulerException e) {
			e.getMessage();
		}
	}
	
	public String save() {
		senha = usuario.getSenha();
		usuario.setSenha(Criptografia.Criptografar(usuario.getSenha()));
		usuario.setPerfil((short) 1);
		if (!verificaExistenciaNick(usuario.getNick())) {
			UsuarioDAO.saveOrUpdate(usuario);
			if (veioDeSolicitacao == 0) {
				nick = usuario.getNick();
				login();
				return "/index";
			} else {
				nick = usuario.getNick();
				login();
				return "cad_cidadao";
			}
		} else {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_WARN, "Nick já existente no sistema.", "Escolha outro."));
			usuario = new Usuario();
			return null;
		}
	}
	
	public String delete() {
		UsuarioDAO.delete(usuario);
		return "/index.xhtml";
	}
	
	public String edit() {	
		usuario.setSenha(Criptografia.Criptografar(usuario.getSenha()));
		UsuarioDAO.saveOrUpdate(usuario);
		if(usuario.getPerfil() != 1) {
			
			if(usuario.getPerfil() == 3) {
				CidadaoBean cidadao = new CidadaoBean();
				cidadao.save();
			}else {
				ResponsavelBean responsavel = new ResponsavelBean();
				responsavel.save();
			}
		}
		
		if(usuario.getPerfil() != 1) {
			if(usuario.getPerfil() == 3) {
				List<Cidadao> listCidadao = new ArrayList<Cidadao>(usuario.getCidadaos());
				 listCidadao.get(0).setEmail(email);;
			}else {
				List<Responsavel> listResponsavel = new ArrayList<Responsavel>(usuario.getResponsavels());
				listResponsavel.get(0).setEmail(email);;
			}
		}
		
		return "/index";
	}
	
    public String login(){
    	//this.logout();
    	this.usuario = UsuarioDAO.buscarUsuario(this.nick);
    	if(this.usuario == null) {    		
    		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Login ou Senha Incorretos.", "Tente novamente."));    
    		logout();
    	}else {
    		if(!Criptografia.Comparar(Criptografia.Criptografar(senha), usuario.getSenha())){    		
    			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Login ou Senha Incorretos.", "Tente novamente."));
    			logout();
    		}else {
    			loadEmail();
    			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Login executado com sucesso."));
    		}    		
    	}
    	return "/index";   	
    }
    
    public String logout(){
		FacesContext fc = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
		session.invalidate(); 
		this.usuario = null;		
    	return "/index";
    }
    
    public void loadEmail() {
		if(usuario.getPerfil() == 3 && !usuario.getCidadaos().isEmpty()) {
			List<Cidadao> listCidadao = new ArrayList<Cidadao>(usuario.getCidadaos());
			setEmail(listCidadao.get(0).getEmail());
		}else {
			if(usuario.getPerfil() == 2 && !usuario.getResponsavels().isEmpty() ) {
				List<Responsavel> listResponsavel = new ArrayList<Responsavel>(usuario.getResponsavels());
				setEmail(listResponsavel.get(0).getEmail());
			}else {
				setEmail("Não cadastrado");
			}
		}		
    }
    
    private boolean verificaExistenciaNick(String nick) {
    	if(UsuarioDAO.buscarUsuario(nick) != null) {
    		return true;
    	}else {    		
    		return false;
    	}
    }
    
    public void sugestaoNick() {
    	int cont = 1;
    	try {
    		List<Usuario> usuarios = UsuarioDAO.buscarNicks(usuario.getNome().split(" ", 2)[0]);
    		cont += (usuarios.size());    		
    	}catch (NullPointerException e) {
			e.getMessage();
		}finally {
			if(cont == 1) {
				this.nick = ((getNomeCompleto().replace(" ", ".")).toLowerCase());								
			}else {
				this.nick = ((getNomeCompleto().replace(" ", ".")+cont).toLowerCase());								
			}
			this.usuario.setNick(this.nick);			
		}
    }
    
    

//GETTERS E SETTERS ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	
	

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
		String completo = usuario.getNome();
		if(completo != null) {
	    	String[] nomeSobrenome = completo.split(" ");
	    	if(nomeSobrenome.length > 1) {
	    		this.nome =  nomeSobrenome[0]+" "+nomeSobrenome[nomeSobrenome.length -1];
	    	}else {
	    		this.nome =  nomeSobrenome[0];
	    	}
	    	
	    	return this.nome;
		}else {
			return "";
		}
	}

	public void setNome(String nome) {
		this.nome = nome;
		sugestaoNick();
	}

	public Cidadao getCidadao() {	
		List<Cidadao> listCidadao = new ArrayList<Cidadao>(usuario.getCidadaos());	
		return listCidadao.get(0);
	}

	public Responsavel getResponsavel() {
		List<Responsavel> listResponsavel = new ArrayList<Responsavel>(usuario.getResponsavels());
		if (!listResponsavel.isEmpty()) {
			return listResponsavel.get(0);
		}else {
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

}
