package br.gov.se.lai.Bean;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import br.gov.se.lai.DAO.AcoesDAO;
import br.gov.se.lai.entity.Acoes;


@ManagedBean(name = "acoes")
@SessionScoped
public class AcoesBean implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1157796158944497538L;
	private Acoes acoes;
	
	
	@PostConstruct
	public void init() {
		acoes = new Acoes();
		
	}
	
	public String save() {
		AcoesDAO.saveOrUpdate(acoes);
		return "teste_redirecionamento";
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
	
	//GETTERS E SETTERS ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	
	
		public Acoes getAcao() {
			return acoes;
		}

		public void setAcoes(Acoes acoes) {
			this.acoes = acoes;
		}


}