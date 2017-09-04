package br.gov.se.lai.Bean;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import br.gov.se.lai.DAO.CidadaoDAO;
import br.gov.se.lai.entity.Cidadao;
import br.gov.se.lai.entity.Usuario;

@ManagedBean(name = "cidadao")
@SessionScoped
public class CidadaoBean implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6367328853853098867L;
	private Cidadao cidadao;
	
	@PostConstruct
	public void init() {
		cidadao = new Cidadao();
	}
	
	public String save(Usuario usuario) {
		cidadao.setUsuario(usuario);
		CidadaoDAO.saveOrUpdate(cidadao);
		return "../index.xhtml";
	}
	
	public String delete() {

		return "usuario";
	}
	
	public String edit() {

		return "usuario";
	}
	

//GETTERS E SETTERS ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	

	public Cidadao getCidadao() {
		return cidadao;
	}

	public void setCidadao(Cidadao cidadao) {
		this.cidadao = cidadao;
	}	
	
}
