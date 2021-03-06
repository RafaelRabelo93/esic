package br.gov.se.lai.Bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
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
import br.gov.se.lai.entity.Usuario;
import br.gov.se.lai.utils.HibernateUtil;
import br.gov.se.lai.utils.PermissaoUsuario;


@ManagedBean(name = "competencias")
@SessionScoped
public class CompetenciasBean implements Serializable, PermissaoUsuario{
	

	private static final long serialVersionUID = -1964244964778917209L;
	private Competencias competencias;
	private List<Entidades> entidades;
	public static List<Entidades> listEntidades;
	public List<Competencias> listCompetencias;
	public List<Competencias> listCompetencias2;
	private List<Competencias> listCompetenciasExcluir;
	private List<Acoes> acoes;
	public static int idAcoes;
	public static int idCompetencias;
	public static int idEntidade;
	private Entidades ent;
	private String novaAcao;
	private Usuario user;

	
	@PostConstruct
	public void init() {
		this.competencias = new Competencias();
		this.entidades = new ArrayList<Entidades>(EntidadesDAO.list());
		listCompetencias= new ArrayList<Competencias>(CompetenciasDAO.list());
		listCompetencias2= new ArrayList<Competencias>();
		user = ((UsuarioBean) HibernateUtil.RecuperarDaSessao("usuario")).getUsuario();
	}
	
	/**
	 * Fun��o save
	 * 
	 * Salva uma lista de compet�ncias caso o usu�rio tenha permiss�o.
	 * @return
	 */
	public String save() {
		if(verificaPermissao()) {
			for (Competencias comp : listCompetencias2) {
				CompetenciasDAO.saveOrUpdate(comp);
				if(comp.getAcoes().getStatus().equalsIgnoreCase("N�o-Vinculada")) {
					comp.getAcoes().setStatus("Vinculada");
					AcoesDAO.saveOrUpdate(comp.getAcoes());
				}
			}	
		}
		
		return "/Alterar/alterar_competencias";
	}
	
	/**
	 * Fun��o consultarCompetencias
	 * 
	 * Lista todas as compet�ncias da entidade da entidade utilizada na classe.
	 * Entidade passada atrav�s de par�metros. 
	 * @return
	 */
	//Alterar, utilizar metodo com entidade vindo no par�metro
	public String consultarCompetencias() {
		listCompetencias2 = CompetenciasDAO.filtrarCompetenciaPorEntidade(ent.getIdEntidades());
		listCompetencias = listCompetencias2;
//		System.out.println("Executei");
		return "/Alterar/alterar_competencias.xhtml?faces-redirect=true" ;
	}

	/**
	 * Fun��o filtarCompetenciasEntidade
	 * Filtra compet�ncias de uma entidade selecionada na tela.
	 * 
	 * @param e - m�todo � chamado a partir de um evento Ajax.
	 */
	public void filtrarCompetenciasEntidade(AjaxBehaviorEvent e) {
		List<Competencias> compEnt = new ArrayList<Competencias>(EntidadesDAO.find(idEntidade).getCompetenciases());
		listCompetencias = compEnt;	
	}	
	
	/**
	 * Fun��o filtrarCompetencias
	 * 
	 * Instancializa a lista de compet�ncias ligadas a uma a��o especifica.
	 * 
	 * @param e - m�todo � chamado a partir de um evento Ajax.
	 */
	public void filtrarCompetencias(AjaxBehaviorEvent e) {
		if(idAcoes != 0) {
			listCompetencias = CompetenciasDAO.filtrarCompetencias(idAcoes);
			listEntidades = null;
		}else {
			listCompetencias = null;
		}
		
	}	
	
	/**
	 * Fun��o filtraEntidades
	 * 
	 * Instancializa a lista de entidades ligadas.
	 * 
	 * @param e - m�todo � chamado a partir de um evento Ajax.
	 */
	public void filtraEntidades(AjaxBehaviorEvent e){
		competencias = CompetenciasDAO.findCompetencias(idCompetencias);
		idEntidade = competencias.getEntidades().getIdEntidades();
		if(idEntidade != 0) {
			listEntidades = EntidadesDAO.listPersonalizada(idEntidade);
		}else {
			listEntidades = null;
		}
	}
	

	/**
	 * Fun��o addLista
	 * 
	 * Antes de salvar, as compet�ncias s�o inseridas em listas e depois salvas no banco de dados.
	 * 
	 */
	public void addLista() {
		competencias.setEntidades(ent);
		competencias.setAcoes(AcoesDAO.findAcoes(idAcoes));
		listCompetencias2.add(competencias);
		save();
		listaAcoesUpdate();
		competencias = new Competencias();
		idAcoes = 0;
	}
	
	/**
	 * Fun��o listaUpdate
	 * 
	 * Ao salvar uma compet�ncia, remove a a��o ligada a ela para que n�o 
	 * haja duplicidade na entidade/[porg�o.
	 */
	private void listaAcoesUpdate(){
		Iterator<Acoes> a = AcoesBean.acoes.iterator();
		while(a.hasNext()) {
			Acoes acao = a.next();
			if(acao.getIdAcoes() == idAcoes) {
				a.remove();
				break;
			}
		}
	}
	
	/**
	 * Itera a lista de compet�ncias e adiciona a a��o ligada a compentencia selecionada na lista de a��es e remove esta compet�ncia do campo de escolha.
	 */
	public void listaCompetenciasUpdate(){
		Iterator<Competencias> c = listCompetencias.iterator();
		while(c.hasNext()) {
			Competencias comp = c.next();
			if(comp == competencias) {
				acoes.add(comp.getAcoes());
				c.remove();
				break;
			}
		}
		competencias = new Competencias();
	}
	
	/**
	 * Remove a��es que j� est�o ligadas a entidade selecionada da lista de a��es apresentada 
	 * @return String que representa a view de redirecionamento
	 */
	public String filtrarAcoesEntidade(){
		List<Competencias> compEnt = new ArrayList<Competencias>(this.ent.getCompetenciases());
		Iterator<Acoes> a = AcoesBean.acoes.iterator();
		for (Competencias competencias : compEnt) {
			while(a.hasNext()) {
				Acoes acao = a.next();
				if(competencias.getAcoes().getIdAcoes() == acao.getIdAcoes()) {
					a.remove();
					break;
				}
			}
		}
		return "cad_competencias2.xthml";
	}
	
	
	/**
	 * Deleta compet�ncias inseridos na lista de exclus�o
	 */
	public void delete() {
		if(verificaPermissao()) {
			for (Competencias comp : listCompetenciasExcluir) {
				CompetenciasDAO.delete(comp);
			}
		}	
	}
	
	
	@Override
	public boolean verificaPermissao() {
		if(user.getPerfil() == 2 || user.getPerfil() == 4 || user.getPerfil() == 5 || user.getPerfil() == 6) {
			return true;
		}else {
			return false;
		}	
	}

	/**
	 * Limpa objeto compet�ncia da sess�o
	 */
	public void limparCompetencia() {
		competencias = new Competencias();
	}
	

//GETTERS E SETTERS ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	

	public Competencias getCompetencias() {
		return competencias;
	}

	public void setCompetencias(Competencias competencias) {
		this.competencias = competencias;
	}
	
	public List<Acoes> getAcoes() {
		return acoes;
	}

//	public List<Acoes> getAcoes() {
//		if(this.acoes == null) {
//			List<Competencias> compEnt = new ArrayList<Competencias>(this.ent.getCompetenciases());
//			this.acoes = new ArrayList<Acoes>(AcoesDAO.list());
//			Iterator<Acoes> a = this.acoes.iterator();
//			for (Competencias competencias : compEnt) {
//				while(a.hasNext()) {
//					Acoes acao = a.next();
//					if(competencias.getAcoes().getIdAcoes() == acao.getIdAcoes()) {
//						a.remove();
//						break;
//					}
//				}
//			}
//		} else {
//			this.acoes = new ArrayList<Acoes>(AcoesDAO.list());
//			Acoes acaoRemover = AcoesDAO.findAcoes(idAcoes);
//			Iterator<Acoes> acs = getAcoes().iterator();
//			while (acs.hasNext()) {
//				Acoes a = acs.next();
//				if (a == acaoRemover) {
//					acs.remove();
//				}
//			}
//		}
//		return acoes;
//	}

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

	public void setIdAcoes(int NovaIdAcoes) {
		idAcoes = NovaIdAcoes;
	}

	public void setListCompetencias(List<Competencias> NovalistCompetencias) {
		listCompetencias = NovalistCompetencias;
	}

	public  List<Competencias> getListCompetencias() {
		return listCompetencias;
	}

	
	public List<Competencias> getListCompetencias2() {
		return listCompetencias2;
	}

	public void setListCompetencias2(List<Competencias> listCompetencias2) {
		this.listCompetencias2 = listCompetencias2;
	}


	public Entidades getEnt() {
		return ent;
	}

	public void setEnt(Entidades ent) {
		this.ent = ent;
		competencias.setEntidades(ent);
	}
	
//	Para editar objetos de competencias alterando a sua a��o respectiva, recebendo uma string.

	public String getNovaAcao() {
		return novaAcao;
	}

	public void setNovaAcao(String novaAcao) {
		this.novaAcao = novaAcao;
	}

	public int getIdEntidade() {
		return idEntidade;
	}

	public void setIdEntidade(int NovaIdEntidade) {
		idEntidade = NovaIdEntidade;
	}

	public List<Competencias> getListCompetenciasExcluir() {
		return listCompetenciasExcluir;
	}

	public void setListCompetenciasExcluir(List<Competencias> listCompetenciasExcluir) {
		this.listCompetenciasExcluir = listCompetenciasExcluir;
	}

	public List<Entidades> getListEntidades() {
		return listEntidades;
	}

	public void setListEntidades(List<Entidades> NovalistEntidades) {
		listEntidades = NovalistEntidades;
	}

	public int getIdCompetencias() {
		return idCompetencias;
	}

	public  void setIdCompetencias(int novoIdCompetencias) {
		idCompetencias = novoIdCompetencias;
	}
	
	

}
