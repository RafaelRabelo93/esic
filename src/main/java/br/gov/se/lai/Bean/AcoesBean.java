package br.gov.se.lai.Bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import br.gov.se.lai.DAO.AcoesDAO;
import br.gov.se.lai.DAO.CompetenciasDAO;
import br.gov.se.lai.entity.Acoes;
import br.gov.se.lai.entity.Competencias;
import br.gov.se.lai.utils.HibernateUtil;
import br.gov.se.lai.utils.NotificacaoEmail;
import br.gov.se.lai.utils.PermissaoUsuario;


@ManagedBean(name = "acoes")
@SessionScoped
public class AcoesBean implements Serializable, PermissaoUsuario{
	
	private static final long serialVersionUID = -1157796158944497538L;
	private Acoes acao;
	private UsuarioBean usuarioBean ;
	private int idAcao;
	public static List<Acoes> acoes;
	private String titulo;
	private List<Acoes>  acoesPendentes;
	private List<Acoes>  acoesNaoVinculadas;
	private List<Acoes>  acoesVinculadas;
	private List<Acoes>  acoesPendentesFilter;
	private List<Acoes>  acoesNaoVinculadasFilter;
	private List<Acoes>  acoesVinculadasFilter;
	private List<Acoes> acoesCadastradas;
	
	
	@PostConstruct
	public void init() {
		acao = new Acoes();
		usuarioBean = (UsuarioBean) HibernateUtil.RecuperarDaSessao("usuario");	
		acoesPendentes = new ArrayList<Acoes>(AcoesDAO.listPorStatus("Pendente"));
		acoesNaoVinculadas = new ArrayList<Acoes>(AcoesDAO.listPorStatus("N�o-Vinculada"));
		acoesVinculadas = new ArrayList<Acoes>(AcoesDAO.listPorStatus("Vinculada"));
		acoesCadastradas = new ArrayList<Acoes>(AcoesDAO.listPorStatus("Vinculada", "N�o-Vinculada"));
		acoes = new ArrayList<Acoes>(acoesVinculadas);
		acoes.addAll(acoesNaoVinculadas);		
	}
	
	/**
	 * M�todo carregarLista
	 * 
	 * M�todo est�tico que instancia a vari�vel global acoes  com
	 * o idAcoes disponibilizados na classe CompetenciasBean. 
	 * Caso n�o haja nenhum idAcoes selecionado, a vari�vel n�o
	 * � instanciada.
	 */
	
	public static void carregarLista() {
		if(CompetenciasBean.idAcoes == 0) {
			acoes = new ArrayList<Acoes>(AcoesDAO.list());
		}else {
			acoes = null;
		}
	}
	
	/**
	 *  
	 */
	
	public void limpaAcao() {
		acao = new Acoes();
	}
	
	/**
	 *  Salva uma nova a��o. Verifica o perfil do usu�rio que est� salvando.
	 * 	Caso seja um gestor do sistema salva com status de n�o-vinculada
	 * 	visto que ainda n�o est� vinculada a uma entidade/�rgao.
	 * 	Caso seja um respons�vel salva como pendente e ainda fica indispon�vel para
	 * 	uso no sistema.
	 *	Caso seja cidad�o n�o salva a a��o e joga aviso de usu�rio sem permiss�o.
	 */
	public void save() {
//		if(verificaPermissao() ) {
		if(usuarioBean.getUsuario().getPerfil() == (short)6 || usuarioBean.getUsuario().getPerfil() == (short)5 || usuarioBean.isResponsavelOGE()) {
			acao.setStatus("N�o-vinculada");
			AcoesDAO.saveOrUpdate(acao);
			getAcoesNaoVinculadas().add(acao);
			getAcoes().add(acao);
		} else if(usuarioBean.getUsuario().getPerfil() == (short)4 || usuarioBean.getUsuario().getPerfil() == (short)2 ){
			acao.setStatus("Pendente");
			AcoesDAO.saveOrUpdate(acao);
			getAcoesPendentes().add(acao);
		}
//		}else {
//			FacesContext.getCurrentInstance().addMessage(null,
//					new FacesMessage(FacesMessage.SEVERITY_WARN, "A��o n�o efetuada.", "Usu�rio sem permiss�o."));
//		}
		acao = new Acoes();
	}
	
	/**
	 * Fun��o remove
	 * 
	 * Verifica se o usu�rio tem permiss�o para excluir a��o.
	 * Somente a��es com status Pendente e N�o-Vinculada podem ser removidas.
	 * 
	 * @param acao - a��o a ser removida
	 * 
	 */
	
	public void remove(Acoes acao) {
		if(verificaPermissao() ) {
			acoes.remove(acao);
			if(acao.getStatus().equals("Pendente")) {
				getAcoesPendentes().remove(acao);
			}else if( acao.getStatus().equals("N�o-vincualada")) {
				getAcoesNaoVinculadas().remove(acao);
			}
			AcoesDAO.delete(acao);
		}
	}
	
	/**
	 * Fun��o filtrarAcoes
	 * Filtra a lista de a��es que aparecem para o usu�rio em rela��o
	 * �s a��es que j� est�o vinculadas a uma entidade/�rgao.
	 * Para n�o disponibilizar a��es que j� s�o vinculadas, a fun��o
	 * verifica quais as compet�ncias e comparam com as a��es que existem.
	 * As que j� forem vinculadas s�o removidas da visualiza��o.
	 *  
	 * 
	 * @param comps - lista de compet�ncias da entidade/�rg�o
	 * @return
	 */
	
	public String filtrarAcoes(List<Competencias> comps){
		Iterator<Acoes> a = acoes.iterator();
		for (Competencias competencias : comps) {
			while(a.hasNext()) {
				Acoes acao = a.next();
				if(competencias.getAcoes() == acao) {
					a.remove();
					break;
				}
			}
		}
		return "cad_competencias2";
	}
	
	/**
	 * Fun��o listarAcoesPendentes
	 * Lista a��es com status Pendente.
	 */
	public void listarAcoesPendentes(){
		acoesPendentes = AcoesDAO.listPorStatus("Pendente");
	}
	
	/**
	 * Fun��o listarAcoesN�oVinculadas
	 * Lista a��es com status N�oVinculada.
	 */

	public void listarAcoesNaoVinculadas(){
		acoesNaoVinculadas = AcoesDAO.listPorStatus("N�o-vinculada");
	}
	
	/**
	 * Fun��o listarAcoesVinculadas
	 * Lista a��es com status Vinculada.
	 */
	
	public void listarAcoesVinculadas(){
		acoesVinculadas = AcoesDAO.listPorStatus("Vinculada");
	}
	
	
	/**
	 * Fun��o acaoLigadaEntidadeAtiva
	 * Fun��o para verificar quais a��es est�o vinculadas a entidades ativas.
	 * 
	 * @return uma lista somente de a��es que est�o vinculadas a entidades ativas. 
	 */
	public List<Acoes> acaoLigadaEntidadeAtiva() {

		Iterator<Acoes> a = acoes.iterator();
		while (a.hasNext()) {
			Acoes acao = a.next();
			List<Competencias> comp = CompetenciasDAO.filtrarCompetencias(acao.getIdAcoes());
			if(comp.isEmpty() ) {
				a.remove();
			}else {
				for (Competencias competencias : comp) {
					if (!competencias.getEntidades().isAtiva()) {
						a.remove();
						break;
					}
				}
			}
		}
		return acoes;
	}
	
	/**
	 * Fun��o autenticaAcao
	 * Fun��o que torna o status de Pendente para N�o-vinculada.
	 * A fun��o remove a a��o da lista de a��es pendentes e a 
	 * insere na lista de a��es n�o vinculadas.
	 * 
	 * @param acao - a a��o que ter� o sttaus alterado
	 */
	
	public void autenticaAcao(Acoes acao) {
		if(usuarioBean.verificaGestor() || usuarioBean.verificaAdmin()) {
			acao.setStatus("N�o-vinculada");
			AcoesDAO.saveOrUpdate(acao);
			getAcoesPendentes().remove(acao);
			getAcoesNaoVinculadas().add(acao);
			getAcoes().add(acao);
		}
	}
	

	@Override
	public boolean verificaPermissao() {
		if(usuarioBean.verificaGestor() || usuarioBean.verificaResponsavel()|| usuarioBean.verificaResponsavelCidadaoPerfil() || usuarioBean.verificaAdmin()) {
			return true;
		}else {
			return false;
		}
	}
	
	public String redirecionarConsultaAcoes() {
		return "/Consulta/consulta_acoes.xhtml?faces-redirect=true";
	}

	public String redirecionarCadastroAcoes() {
		acao = new Acoes();
		return "/Cadastro/cad_acoes.xhtml?faces-redirect=true";
	}
	
	
	public void refreshAcoes() {
		acoesPendentes = new ArrayList<Acoes>(AcoesDAO.listPorStatus("Pendente"));
		acoesNaoVinculadas = new ArrayList<Acoes>(AcoesDAO.listPorStatus("N�o-Vinculada"));
		acoesVinculadas = new ArrayList<Acoes>(AcoesDAO.listPorStatus("Vinculada"));
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

		public void setAcoes(List<Acoes> novaAcoes) {
			acoes = novaAcoes;
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

		public List<Acoes> getAcoesPendentes() {
			return acoesPendentes;
		}

		public void setAcoesPendentes(List<Acoes> acoesPendentes) {
			this.acoesPendentes = acoesPendentes;
		}

		public List<Acoes> getAcoesNaoVinculadas() {
			return acoesNaoVinculadas;
		}

		public void setAcoesNaoVinculadas(List<Acoes> acoesNaoVinculadas) {
			this.acoesNaoVinculadas = acoesNaoVinculadas;
		}

		public List<Acoes> getAcoesVinculadas() {
			return acoesVinculadas;
		}

		public void setAcoesVinculadas(List<Acoes> acoesVinculadas) {
			this.acoesVinculadas = acoesVinculadas;
		}

		public List<Acoes> getAcoesPendentesFilter() {
			return acoesPendentesFilter;
		}

		public void setAcoesPendentesFilter(List<Acoes> acoesPendentesFilter) {
			this.acoesPendentesFilter = acoesPendentesFilter;
		}

		public List<Acoes> getAcoesNaoVinculadasFilter() {
			return acoesNaoVinculadasFilter;
		}

		public void setAcoesNaoVinculadasFilter(List<Acoes> acoesNaoVinculadasFilter) {
			this.acoesNaoVinculadasFilter = acoesNaoVinculadasFilter;
		}

		public List<Acoes> getAcoesVinculadasFilter() {
			return acoesVinculadasFilter;
		}

		public void setAcoesVinculadasFilter(List<Acoes> acoesVinculadasFilter) {
			this.acoesVinculadasFilter = acoesVinculadasFilter;
		}

		public List<Acoes> getAcoesCadastradas() {
			return acoesCadastradas;
		}

		public void setAcoesCadastradas(List<Acoes> acoesCadastradas) {
			this.acoesCadastradas = acoesCadastradas;
		}

		

}