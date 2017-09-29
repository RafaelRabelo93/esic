package br.gov.se.lai.Bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import br.gov.se.lai.DAO.EntidadesDAO;
import br.gov.se.lai.DAO.ResponsavelDAO;
import br.gov.se.lai.DAO.UsuarioDAO;
import br.gov.se.lai.entity.Entidades;
import br.gov.se.lai.entity.Responsavel;
import br.gov.se.lai.entity.Usuario;


@ManagedBean(name = "responsavel")
@SessionScoped
public class ResponsavelBean implements Serializable{
	
	/**
	 * 
	 */
	
	private static final long serialVersionUID = -534835121161473086L;
	private Responsavel responsavel;
	private List<Entidades> entidades;
	private Usuario usuario;
	private int idEntidade;
	private int nivel;
	private String email;
	private String nick;
	
	
	@PostConstruct
	public void init() {
		this.responsavel = new Responsavel();
		this.entidades = new ArrayList<Entidades>(EntidadesDAO.listAtivas());
	}
	
	public String save() {
		this.responsavel.setEntidades(EntidadesDAO.find(this.idEntidade));
		this.usuario = UsuarioDAO.buscarUsuario(nick);
		this.usuario.setPerfil((short)2);
		this.responsavel.setUsuario(this.usuario);
		ResponsavelDAO.saveOrUpdate(responsavel);
		UsuarioDAO.saveOrUpdate(usuario);
		return "/index";
	}
	
	public String delete() {

		return "usuario";
	}
	
	public String edit() {
		this.usuario = UsuarioDAO.buscarUsuario(nick);
		this.responsavel.setUsuario(this.usuario);
		ResponsavelDAO.saveOrUpdate(responsavel);
		
		return "/index";
	}
	

//GETTERS E SETTERS ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	
	public Responsavel getResponsavel() {
		return responsavel;
	}

	public void setResponsavel(Responsavel responsavel) {
		this.responsavel = responsavel;
	}

	public List<Entidades> getEntidades() {
		return entidades;
	}

	public void setEntidades(List<Entidades> entidades) {
		this.entidades = entidades;
	}

	public int getIdEntidade() {
		return idEntidade;
	}

	public void setIdEntidade(int idEntidade) {
		this.idEntidade = idEntidade;
	}

	public int getNivel() {
		return nivel;
	}

	public void setNivel(int nivel) {
		this.nivel = nivel;
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

		
}
