package br.gov.se.lai.Bean;

import java.io.IOException;
import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import br.gov.se.lai.DAO.UsuarioDAO;
import br.gov.se.lai.entity.Usuario;
import br.gov.se.lai.utils.Criptografia;

@ManagedBean(name = "usuario")
@SessionScoped
public class UsuarioBean implements Serializable{

	private static final long serialVersionUID = 4098925984824190470L;
	private Usuario usuario;
	private String email;
	private String senha;
	private String nick;
	
	@PostConstruct
	public void init() {
		usuario = new Usuario();
	}
	
	public String save() {
		usuario.setSenha(Criptografia.Criptografar(usuario.getSenha()));
		usuario.setPerfil((short) 3);
		UsuarioDAO.saveOrUpdate(usuario);
		//return "cad_cidadao";
		return "cad_responsavel";
	}
	
	public String delete() {

		return "usuario";
	}
	
	public String edit() {

		return "usuario";
	}
	
    public String login() throws IOException {

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
    
    public String logout() throws IOException{
		FacesContext fc = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
		session.invalidate();
		this.usuario = null;		
    	return "index";
    }

//GETTERS E SETTERS ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	
	
	public Usuario getUsuario() {
		return this.usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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
	
	
}
