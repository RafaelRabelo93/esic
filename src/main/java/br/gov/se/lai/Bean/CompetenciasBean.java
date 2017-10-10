package br.gov.se.lai.Bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.faces.event.AjaxBehaviorEvent;

import org.hibernate.sql.Update;

import com.mysql.jdbc.IterateBlock;

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
	private Entidades ent;

	
	@PostConstruct
	public void init() {
		this.competencias = new Competencias();
		this.acoes = new ArrayList<Acoes>(AcoesDAO.list());
		this.entidades = new ArrayList<Entidades>(EntidadesDAO.list());
		this.listCompetencias= new ArrayList<Competencias>();
	}
	
	public String save() {
		for (Competencias comp : listCompetencias) {
			CompetenciasDAO.saveOrUpdate(comp);
		}		
		return "/index";
	}
	
	public void filtrarCompetencias(AjaxBehaviorEvent e) {
		if(idAcoes != 0) {
			listCompetencias = CompetenciasDAO.filtrarCompetencias(idAcoes);
		}else {
			listCompetencias = null;
		}
	}	
	
	public List<Competencias> addLista() {
		competencias.setEntidades(ent);
		competencias.setAcoes(AcoesDAO.findAcoes(idAcoes));
		listCompetencias.add(competencias);
		listaAcoesUpdate();
		competencias = new Competencias();
		return listCompetencias;
	}
	
	private List<Acoes> listaAcoesUpdate(){
		Iterator<Acoes> a = acoes.iterator();
		while(a.hasNext()) {
			Acoes acao = a.next();
			if(acao.getIdAcoes() == idAcoes) {
				a.remove();
				break;
			}
		}
	return this.acoes;
	}
	
	public List<Competencias> listaCompetenciasUpdate(){
		Iterator<Competencias> c = listCompetencias.iterator();
		while(c.hasNext()) {
			Competencias comp = c.next();
			if(comp == competencias) {
				c.remove();
				break;
			}
		}
		
		return listCompetencias;
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

	public List<Competencias> getListCompetencias() {
		return listCompetencias;
	}

	public void setListCompetencias(List<Competencias> listCompetencias) {
		this.listCompetencias = listCompetencias;
	}

	public void setCompetencias(Competencias competencias) {
		this.competencias = competencias;
	}

	public Entidades getEnt() {
		return ent;
	}

	public void setEnt(Entidades ent) {
		this.ent = ent;
		competencias.setEntidades(ent);
	}

}
