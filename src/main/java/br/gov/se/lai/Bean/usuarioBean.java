package br.gov.se.lai.Bean;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import br.gov.se.lai.DAO.UsuarioDAO;
import br.gov.se.lai.Entity.Usuario;
import br.gov.se.lai.utils.Criptografia;

@ManagedBean(name = "usuario")
@SessionScoped
public class UsuarioBean implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4098925984824190470L;
	private Usuario usuario;
	private String email;
	
	
	@PostConstruct
	public void init() {
		usuario = new Usuario();
	}
	
	public String save() {
		usuario.setSenha(Criptografia.Criptografar(usuario.getSenha()));
		usuario.setPerfil((short) 3);
		UsuarioDAO.saveOrUpdate(usuario);
		return "cad_cidadao";
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
