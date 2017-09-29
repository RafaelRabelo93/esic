package br.gov.se.lai.Bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import br.gov.se.lai.DAO.UsuarioDAO;
import br.gov.se.lai.entity.Cidadao;
import br.gov.se.lai.entity.Responsavel;
import br.gov.se.lai.entity.Usuario;
import br.gov.se.lai.utils.Criptografia;

@ManagedBean(name = "usuario")
@SessionScoped
public class UsuarioBean implements Serializable{

	private static final long serialVersionUID = 4098925984824190470L;
	private Usuario usuario;
	private String senha;
	private String nick;
	private String nome;
	private int veioDeSolicitacao;
	
	@PostConstruct
	public void init() {
		usuario = new Usuario();
	}
	
	public String save() {
		senha = usuario.getSenha();
		usuario.setSenha(Criptografia.Criptografar(usuario.getSenha()));
		UsuarioDAO.saveOrUpdate(usuario);
		if (veioDeSolicitacao == 0) {
			nick = usuario.getNick();
			login();
			return "/index";
		}else {
			nick = usuario.getNick();
			login();
			return "cad_cidadao";
		}
	}
	
	public String delete() {
		UsuarioDAO.delete(usuario);
		return "/index.xhtml";
	}
	
	public String edit() {	
		usuario.setSenha(Criptografia.Criptografar(usuario.getSenha()));
		UsuarioDAO.saveOrUpdate(usuario);
		if(usuario.getPerfil() == 3) {
			CidadaoBean cidadao = new CidadaoBean();
			cidadao.save();
		}else {
			ResponsavelBean responsavel = new ResponsavelBean();
			responsavel.save();
		}
		return "/index";
	}
	
    public String login(){
    	//this.logout();
    	this.usuario = UsuarioDAO.buscarUsuario(this.nick);
    	if(this.usuario == null) {    		
    		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Login ou Senha Incorretos.", "Tente novamente."));    		
    	}else {
    		if(!Criptografia.Comparar(Criptografia.Criptografar(senha), usuario.getSenha())){    		
    			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Login ou Senha Incorretos.", "Tente novamente."));
    			this.usuario = null;
    		}else {
    			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Login executado com sucesso."));
    		}    		
    	}
    	return "index";       	
    }
    
    public String logout(){
		FacesContext fc = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
		session.invalidate();
		this.usuario = null;		
    	return "/index";
    }

//GETTERS E SETTERS ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	
	

	public Usuario getUsuario() {
		return this.usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public String getEmail() {
		if(usuario.getPerfil() == 3) {
			List<Cidadao> listCidadao = new ArrayList<Cidadao>(usuario.getCidadaos());
			return listCidadao.get(0).getEmail();
		}else {
			List<Responsavel> listResponsavel = new ArrayList<Responsavel>(usuario.getResponsavels());
			return listResponsavel.get(0).getEmail();
		}
	}
	
	public void setEmail(String email) {
		if(usuario.getPerfil() == 3) {
			List<Cidadao> listCidadao = new ArrayList<Cidadao>(usuario.getCidadaos());
			 listCidadao.get(0).setEmail(email);;
		}else {
			List<Responsavel> listResponsavel = new ArrayList<Responsavel>(usuario.getResponsavels());
			listResponsavel.get(0).setEmail(email);;
		}
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

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Cidadao getCidadao() {	
		List<Cidadao> listCidadao = new ArrayList<Cidadao>(usuario.getCidadaos());	
		return listCidadao.get(0);
	}

	public Responsavel getResponsavel() {
		List<Responsavel> listResponsavel = new ArrayList<Responsavel>(usuario.getResponsavels());
		return listResponsavel.get(0);
	}

	public int getVeioDeSolicitacao() {
		return veioDeSolicitacao;
	}

	public void setVeioDeSolicitacao(int veioDeSolicitacao) {
		this.veioDeSolicitacao = veioDeSolicitacao;
	}
	
	
}
