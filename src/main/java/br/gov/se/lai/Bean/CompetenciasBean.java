package br.gov.se.lai.Bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.event.AjaxBehaviorEvent;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import br.gov.se.lai.DAO.AcoesDAO;
import br.gov.se.lai.DAO.CompetenciasDAO;
import br.gov.se.lai.DAO.EntidadesDAO;
import br.gov.se.lai.entity.Acoes;
import br.gov.se.lai.entity.Competencias;
import br.gov.se.lai.entity.Entidades;


@ManagedBean(name = "competencias")
@SessionScoped
public class CompetenciasBean implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1964244964778917209L;
	private Competencias competencias;
	private List<Entidades> entidades;
	private List<Competencias> listCompetencias;
	private List<Acoes> acoes;
	private int idAcoes;
	private int idEntidades;

	
	@PostConstruct
	public void init() {
		this.competencias = new Competencias();
		this.acoes = new ArrayList<Acoes>(AcoesDAO.list());
		this.entidades = new ArrayList<Entidades>(EntidadesDAO.list());
	}
	
	public String save() {
		this.competencias.setAcoes(AcoesDAO.findAcoes(idAcoes));
		this.competencias.setEntidades(EntidadesDAO.find(this.idEntidades));
		CompetenciasDAO.saveOrUpdate(competencias);
		return "/index";
	}
	
	public void filtrarCompetencias(AjaxBehaviorEvent e) {
		if(idAcoes != 0) {
			listCompetencias = CompetenciasDAO.filtrarCompetencias(idAcoes);
		}else {
			listCompetencias = null;
		}
	}	
	
	public String delete() {

		return "usuario";
	}
	
	public String edit() {

		return "usuario";
	}
	

//GETTERS E SETTERS ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	

	public Competencias getCompetencias() {
		return competencias;
	}

	public void setCompetencia(Competencias competencias) {
		this.competencias = competencias;
	}
	
	public List<Acoes> getAcoes() {
		return acoes;
	}

	public void setAcoes(List<Acoes> acoes) {
		this.acoes = acoes;
	}
	
	public List<Entidades> getEntidades() {
		return entidades;
	}

	public void setEntidades(List<Entidades> entidades) {
		this.entidades = entidades;
	}


	public int getIdAcoes() {
		return idAcoes;
	}

	public void setIdAcoes(int idAcoes) {
		this.idAcoes = idAcoes;
	}

	public int getIdEntidades() {
		return idEntidades;
	}

	public void setIdEntidades(int idEntidades) {
		this.idEntidades = idEntidades;
	}

	public List<Competencias> getListCompetencias() {
		return listCompetencias;
	}

	public void setListCompetencias(List<Competencias> listCompetencias) {
		this.listCompetencias = listCompetencias;
	}

	public void setCompetencias(Competencias competencias) {
		this.competencias = competencias;
	}

	
	
		
}
