package br.gov.se.lai.Bean;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import br.gov.se.lai.entity.Usuario;

@ManagedBean(name = "usuario")
@SessionScoped
public class usuarioBean {
	
	private Usuario usuario;
	
	@PostConstruct
	public void init() {
		
	}
	
	public String save() {
		setUsuario(new Usuario());
		return "usuario";
	}
	
	public String delete() {

		return "usuario";
	}
	
	public String edit() {

		return "usuario";
	}
	
	public String login() {
		return "index";
	}

//GETTERS E SETTERS ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	
	
	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
}
