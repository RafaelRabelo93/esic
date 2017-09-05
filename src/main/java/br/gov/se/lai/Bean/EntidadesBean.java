package br.gov.se.lai.Bean;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import br.gov.se.lai.DAO.EntidadesDAO;
import br.gov.se.lai.entity.Entidades;


@ManagedBean(name = "entidades")
@SessionScoped
public class EntidadesBean implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1157796158944497538L;
	private Entidades entidades;
	private int idOrgaos;
	
	
	@PostConstruct
	public void init() {
		entidades = new Entidades();
		
	}
	
	public String save() {
		entidades.setIdOrgaos(idOrgaos);
		EntidadesDAO.saveOrUpdate(entidades);
		return "index";
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

	
	
	
//GETTERS E SETTERS ==============================================	
	
	
	public int getIdOrgaos() {
		return idOrgaos;
	}

	public void setIdOrgaos(int idOrgaos) {
		this.idOrgaos = idOrgaos;
	}

	public Entidades getEntidades() {
		return entidades;
	}

	public void setEntidades(Entidades entidades) {
		this.entidades = entidades;
	}
}
