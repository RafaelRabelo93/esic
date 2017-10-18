package br.gov.se.lai.Bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.primefaces.context.RequestContext;

import br.gov.se.lai.DAO.AcoesDAO;
import br.gov.se.lai.entity.Acoes;
import br.gov.se.lai.entity.Competencias;
import br.gov.se.lai.utils.HibernateUtil;


@ManagedBean(name = "acoes")
@SessionScoped
public class AcoesBean implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1157796158944497538L;
	private Acoes acao;
	private UsuarioBean usuarioBean ;
	private int idAcao;
	private List<Acoes> acoes;
	private String titulo;
	
	
	@PostConstruct
	public void init() {
		acao = new Acoes();
		acoes = new ArrayList<Acoes>(AcoesDAO.list());
		usuarioBean = (UsuarioBean) HibernateUtil.RecuperarDaSessao("usuario");	
	}
	
	public void save() {
		if(usuarioBean.getUsuario().getPerfil() == 0 || usuarioBean.getUsuario().getPerfil() == 4 ) {
			AcoesDAO.saveOrUpdate(acao);
		}
		
	}
	
	
	public String delete() {
		if(usuarioBean.getUsuario().getIdUsuario() == 0 || usuarioBean.getUsuario().getIdUsuario() == 4 ) {
			AcoesDAO.delete(acao);
		}
		return "/index";
	}
	
	public String edit() {

		return "/index";
	}
	
	public String login() {
		return "index";
	}
	
	public String filtrarAcoes(List<Competencias> comps){
		Iterator<Acoes> a = acoes.iterator();
		for (Competencias competencias : comps) {
			while(a.hasNext()) {
				Acoes acao = a.next();
				if(competencias.getAcoes() == a) {
					a.remove();
					break;
				}
			}
		}
		return "cad_competencias2";
	}
	
	//GETTERS E SETTERS ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	
	
		public Acoes getAcao() {
			return acao;
		}

		public void setAcao() {
			Acoes ac = AcoesDAO.findAcoes(idAcao);
			this.acao = ac;
		}

		public List<Acoes> getAcoes() {
			return acoes;
		}

		public void setAcoes(List<Acoes> acoes) {
			this.acoes = acoes;
		}

		public int getIdAcao() {
			return idAcao;
		}

		public void setIdAcao(int idAcao) {
			this.idAcao = idAcao;
		}

		public void setAcao(Acoes acao) {
			this.acao = acao;
		}

		public String getTitulo() {
			return titulo;
		}

		public void setTitulo(String titulo) {
			this.titulo = titulo;
		}


}