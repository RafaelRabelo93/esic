package br.gov.se.lai.Bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.AjaxBehaviorEvent;

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
	private int idEntidades;
	private String nome;
	private boolean ativa;
	private List<Entidades> listEntidades;
	private List<Entidades> todasEntidades;
	
	
	
	@PostConstruct
	public void init() {
		entidades = new Entidades();
		todasEntidades = new ArrayList<Entidades>(EntidadesDAO.list());
		
	}
	
	public String save() {
		entidades.setAtiva(true);
		EntidadesDAO.saveOrUpdate(entidades);
		return "/index";
	}
	
//	public String delete() {
//		entidades = EntidadesDAO.find(idEntidades);
//		EntidadesDAO.delete(entidades);
//		return "usuario";
//	}
//	
	public String edit() {
		EntidadesDAO.saveOrUpdate(entidades);
		return "/index";
	}
	

	public void filtraEntidades(AjaxBehaviorEvent e){
		if(idEntidades != 0) {
			this.listEntidades = EntidadesDAO.listPersonalizada(idEntidades);
		}else {
			listEntidades = null;
		}
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

	public void setEntidades() {
		Entidades ent = EntidadesDAO.find(idEntidades);
		this.entidades = ent;
	}


	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public List<Entidades> getListEntidades() {
		return listEntidades;
	}

	public void setListEntidades(List<Entidades> listEntidades) {
		this.listEntidades = listEntidades;
	}

	public int getIdEntidades() {
		return idEntidades;
	}

	public void setIdEntidades(int idEntidades) {
		this.idEntidades = idEntidades;
	}

	public boolean isAtiva() {
		return ativa;
	}

	public void setAtiva(boolean ativa) {
		this.ativa = ativa;
	}

	public List<Entidades> getTodasEntidades() {
		return todasEntidades;
	}

	public void setTodasEntidades(List<Entidades> todasEntidades) {
		this.todasEntidades = todasEntidades;
	}
	
}
