package br.gov.se.lai.Bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import br.gov.se.lai.DAO.EntidadesDAO;
import br.gov.se.lai.DAO.ResponsavelDAO;
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
	private Usuario usuario;
	private List<Entidades> entidades;
	
	
	@PostConstruct
	public void init() {
		responsavel = new Responsavel();
		entidades = new ArrayList<Entidades>(EntidadesDAO.list());
	}
	
	public String save() {
		responsavel.setEntidades(EntidadesDAO.find(3));
		responsavel.setUsuario(usuario);
		ResponsavelDAO.saveOrUpdate(responsavel);
		return "teste_redirecionamento";
	}
	
	public String delete() {

		return "usuario";
	}
	
	public String edit() {

		return "usuario";
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

	
		
}
