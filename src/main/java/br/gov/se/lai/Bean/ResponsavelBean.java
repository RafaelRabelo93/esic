package br.gov.se.lai.Bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import br.gov.se.lai.DAO.EntidadesDAO;
import br.gov.se.lai.DAO.ResponsavelDAO;
import br.gov.se.lai.DAO.UsuarioDAO;
import br.gov.se.lai.entity.Entidades;
import br.gov.se.lai.entity.Responsavel;
import br.gov.se.lai.entity.Usuario;
import br.gov.se.lai.utils.HibernateUtil;


@ManagedBean(name = "responsavel")
@SessionScoped
public class ResponsavelBean implements Serializable{
	
	/**
	 * 
	 */
	
	private static final long serialVersionUID = -534835121161473086L;
	private Responsavel responsavel;
	private List<Entidades> entidades;
	private UsuarioBean usuarioBean ;
	private Usuario usuario;
	private int idEntidade;
	private int nivel;
	private String email;
	private String nick;
<<<<<<< HEAD
<<<<<<< HEAD
	private boolean ativo;

=======
	
	
<<<<<<< HEAD
>>>>>>> origin/Hotfix
=======
>>>>>>> d012a0c2e37b068f46023447d8af5dac9ab09c0d
=======
	
=======
	private boolean ativo;
	private List<Responsavel> todosResponsaveis;

>>>>>>> origin/master
	
>>>>>>> hotfix
	@PostConstruct
	public void init() {
		this.responsavel = new Responsavel();
		this.entidades = new ArrayList<Entidades>(EntidadesDAO.listAtivas());
		usuarioBean = (UsuarioBean) HibernateUtil.RecuperarDaSessao("usuario");	
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
	
	public void delete() {
		//Será deletado a instancia de responsável? Ou apenas colocar como status inativo?
		if(verificaAcesso()) {
			this.responsavel = ResponsavelDAO.findResponsavel(email);
			this.responsavel.getUsuario().setPerfil((short)-1);
			ResponsavelDAO.saveOrUpdate(responsavel);
		}
	}
	
	public String edit() {
		if(usuarioBean.getUsuario().getIdUsuario() == 0 || usuarioBean.getUsuario().getIdUsuario() == 4 ) {
			this.usuario = UsuarioDAO.buscarUsuario(nick);
			this.responsavel.setUsuario(this.usuario);
			ResponsavelDAO.saveOrUpdate(responsavel);
		}	
		
		return "/index";
	}
	
	public boolean verificaAcesso() {
		if(usuarioBean.getUsuario().getPerfil() == 4 || usuarioBean.getUsuario().getPerfil() == 5 ) {
			return true;
		}else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Acesso Negado!", null));
			return false;
		}
	}
		
<<<<<<< HEAD
<<<<<<< HEAD
=======

>>>>>>> hotfix
=======
	public boolean verificaExistenciaResponsavel(Usuario usuario) {
		if(usuario.getPerfil() == (short) 2) {
			return true;
		}else {
			return false;			
		}
	}
	
<<<<<<< HEAD
>>>>>>> d012a0c2e37b068f46023447d8af5dac9ab09c0d

=======
>>>>>>> origin/master
>>>>>>> hotfix
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

<<<<<<< HEAD
<<<<<<< HEAD
=======
		
>>>>>>> hotfix
=======
	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}
	
	public List<Responsavel> getTodosResponsaveis() {
		todosResponsaveis = new ArrayList<Responsavel>(ResponsavelDAO.list());
		return todosResponsaveis;
	}
	
	public void setTodosResponsaveis(List<Responsavel> todosResponsaveis) {
		this.todosResponsaveis = todosResponsaveis;
	}
	
<<<<<<< HEAD
>>>>>>> d012a0c2e37b068f46023447d8af5dac9ab09c0d
		
=======
>>>>>>> origin/master
>>>>>>> hotfix
}
