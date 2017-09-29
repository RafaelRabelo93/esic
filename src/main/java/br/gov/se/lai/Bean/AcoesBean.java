package br.gov.se.lai.Bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
	private Acoes acao;
	private int idAcao;
	private List<Acoes> acoes;
	private String teste;
	
	
	@PostConstruct
	public void init() {
		acao = new Acoes();
		acoes = new ArrayList<Acoes>(AcoesDAO.list());		
	}
	
	public String save() {
		AcoesDAO.saveOrUpdate(acao);
		return "/index";
	}
	
	public String delete() {
		AcoesDAO.delete(acao);
		return "/index";
	}
	
	public String edit() {

		return "/index";
	}
	
	public String login() {
		return "index";
	}
	
	public List<String> completeText(String query) {
        List<String> results = new ArrayList<String>();
        for(int i = 0; i < 10; i++) {
            results.add(query + i);
        }
         
        return results;
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

		public String getTeste() {
			return teste;
		}

		public void setTeste(String teste) {
			this.teste = teste;
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


}