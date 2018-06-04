package br.gov.se.lai.Bean;

import java.io.Serializable;
import java.util.ArrayList;
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
	public static List<Competencias> listCompetencias;
	private List<Competencias> listCompetenciasExcluir;
	private List<Acoes> acoes;
	public static int idAcoes;
	public static int idEntidade;
	private Entidades ent;
	private String novaAcao;
	private Usuario user;

	
	@PostConstruct
	public void init() {
		this.competencias = new Competencias();
		this.entidades = new ArrayList<Entidades>(EntidadesDAO.list());
		listCompetencias= new ArrayList<Competencias>();
		user = ((UsuarioBean) HibernateUtil.RecuperarDaSessao("usuario")).getUsuario();
	}
	
	/**
	 * Função save
	 * 
	 * Salva uma lista de competências caso o usuário tenha permissão.
	 * @return
	 */
	public String save() {
		if(verificaPermissao()) {
			for (Competencias comp : listCompetencias) {
				CompetenciasDAO.saveOrUpdate(comp);
			}	
		}
		return "/Alterar/alterar_competencias";
	}
	
	/**
	 * Função consultarCompetencias
	 * 
	 * Lista todas as competências da entidade da entidade utilizada na classe.
	 * Entidade passada através de parâmetros. 
	 * @return
	 */
	//Alterar, utilizar metodo com entidade vindo no parâmetro
	public String consultarCompetencias() {
		listCompetencias = CompetenciasDAO.filtrarCompetenciaPorEntidade(ent.getIdEntidades());
		return "/Alterar/alterar_competencias" ;
	}

	/**
	 * Função filtarCompetenciasEntidade
	 * Filtra competências de uma entidade selecionada na tela.
	 * 
	 * @param e - método é chamado a partir de um evento Ajax.
	 */
	public void filtrarCompetenciasEntidade(AjaxBehaviorEvent e) {
		List<Competencias> compEnt = new ArrayList<Competencias>(EntidadesDAO.find(idEntidade).getCompetenciases());
		listCompetencias = compEnt;	
	}	
	
	/**
	 * Função filtrarCompetencias
	 * 
	 * Instancializa a lista de competências ligadas a uma ação especifica.
	 * 
	 * @param e - método é chamado a partir de um evento Ajax.
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
	 * Função filtraEntidades
	 * 
	 * Instancializa a lista de entidades ligadas.
	 * 
	 * @param e - método é chamado a partir de um evento Ajax.
	 */
	public void filtraEntidades(AjaxBehaviorEvent e){
		if(idEntidade != 0) {
			listEntidades = EntidadesDAO.listPersonalizada(idEntidade);
		}else {
			listEntidades = null;
		}
	}
	

	/**
	 * Função addLista
	 * 
	 * Antes de salvar, as competências são inseridas em listas e depois salvas no banco de dados.
	 * 
	 */
	public void addLista() {
		competencias.setEntidades(ent);
		competencias.setAcoes(AcoesDAO.findAcoes(idAcoes));
		listCompetencias.add(competencias);
		save();
		listaAcoesUpdate();
		competencias = new Competencias();
		idAcoes = 0;
	}
	
	/**
	 * Função listaUpdate
	 * 
	 * Ao salvar uma competência, remove a ação ligada a ela para que não 
	 * haja duplicidade na entidade/[porgão.
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
	
	
	
	public void delete() {
		if(verificaPermissao()) {
			for (Competencias comp : listCompetenciasExcluir) {
				CompetenciasDAO.delete(comp);
			}
		}	
	}
	
	
	@Override
	public boolean verificaPermissao() {
		if(user.getPerfil() == 2 || user.getPerfil() == 4 || user.getPerfil() == 5 ) {
			return true;
		}else {
			return false;
		}	
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

	public List<Competencias> getListCompetencias() {
		return listCompetencias;
	}

	public void setListCompetencias(List<Competencias> NovalistCompetencias) {
		listCompetencias = NovalistCompetencias;
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
	
//	Para editar objetos de competencias alterando a sua ação respectiva, recebendo uma string.

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
	
	

}
