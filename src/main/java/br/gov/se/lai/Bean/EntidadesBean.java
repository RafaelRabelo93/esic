package br.gov.se.lai.Bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.AjaxBehaviorEvent;

import br.gov.se.lai.DAO.EntidadesDAO;
import br.gov.se.lai.entity.Acoes;
import br.gov.se.lai.entity.Competencias;
import br.gov.se.lai.entity.Entidades;
import br.gov.se.lai.utils.HibernateUtil;


@ManagedBean(name = "entidades")
@SessionScoped
public class EntidadesBean implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1157796158944497538L;
	private Entidades entidades;
	private UsuarioBean usuarioBean ;
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
		usuarioBean = (UsuarioBean) HibernateUtil.RecuperarDaSessao("usuario");	
		
	}
	
	public String save() {
		if(usuarioBean.getUsuario().getIdUsuario() == 0 || usuarioBean.getUsuario().getIdUsuario() == 4 ) {
			entidades.setAtiva(true);
			EntidadesDAO.saveOrUpdate(entidades);
		}
		return "/index";
	}
	
	public void delete() {
		if(usuarioBean.getUsuario().getIdUsuario() == 0 || usuarioBean.getUsuario().getIdUsuario() == 4 ) {
			entidades = EntidadesDAO.find(idEntidades);
			//EntidadesDAO.delete(entidades);
			entidades.setAtiva(false);
		}	
	}
	
	public String edit() {
		if(usuarioBean.getUsuario().getIdUsuario() == 0 || usuarioBean.getUsuario().getIdUsuario() == 4 ) {
			EntidadesDAO.saveOrUpdate(entidades);
		}
		return "/index";
	}
	

	public void filtraEntidades(AjaxBehaviorEvent e){
		if(idEntidades != 0) {
			this.listEntidades = EntidadesDAO.listPersonalizada(idEntidades);
		}else {
			listEntidades = null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public void verificaCompetenciasEntidade(){
		List<Competencias> compEnt = new ArrayList<Competencias>(this.entidades.getCompetenciases());
		AcoesBean acaobean = new AcoesBean();
		acaobean.filtrarAcoes(compEnt);
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
	
	public void setEntidades(Entidades ent) {
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
