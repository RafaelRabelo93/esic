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
		acoesNaoVinculadas = new ArrayList<Acoes>(AcoesDAO.listPorStatus("Não-Vinculada"));
		acoesVinculadas = new ArrayList<Acoes>(AcoesDAO.listPorStatus("Vinculada"));
		acoesCadastradas = new ArrayList<Acoes>(AcoesDAO.listPorStatus("Vinculada", "Não-Vinculada"));
		acoes = new ArrayList<Acoes>(acoesVinculadas);
		acoes.addAll(acoesNaoVinculadas);		
	}
	
	/**
	 * Método carregarLista
	 * 
	 * Método estático que instancia a variável global acoes  com
	 * o idAcoes disponibilizados na classe CompetenciasBean. 
	 * Caso não haja nenhum idAcoes selecionado, a variável não
	 * é instanciada.
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
	 *  Salva uma nova ação. Verifica o perfil do usuário que está salvando.
	 * 	Caso seja um gestor do sistema salva com status de não-vinculada
	 * 	visto que ainda não está vinculada a uma entidade/órgao.
	 * 	Caso seja um responsável salva como pendente e ainda fica indisponível para
	 * 	uso no sistema.
	 *	Caso seja cidadão não salva a ação e joga aviso de usuário sem permissão.
	 */
	public void save() {
//		if(verificaPermissao() ) {
		if(usuarioBean.getUsuario().getPerfil() == (short)6 || usuarioBean.getUsuario().getPerfil() == (short)5 || usuarioBean.isResponsavelOGE()) {
			acao.setStatus("Não-vinculada");
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
//					new FacesMessage(FacesMessage.SEVERITY_WARN, "Ação não efetuada.", "Usuário sem permissão."));
//		}
		acao = new Acoes();
	}
	
	/**
	 * Função remove
	 * 
	 * Verifica se o usuário tem permissão para excluir ação.
	 * Somente ações com status Pendente e Não-Vinculada podem ser removidas.
	 * 
	 * @param acao - ação a ser removida
	 * 
	 */
	
	public void remove(Acoes acao) {
		if(verificaPermissao() ) {
			acoes.remove(acao);
			if(acao.getStatus().equals("Pendente")) {
				getAcoesPendentes().remove(acao);
			}else if( acao.getStatus().equals("Não-vincualada")) {
				getAcoesNaoVinculadas().remove(acao);
			}
			AcoesDAO.delete(acao);
		}
	}
	
	/**
	 * Função filtrarAcoes
	 * Filtra a lista de ações que aparecem para o usuário em relação
	 * às ações que já estão vinculadas a uma entidade/órgao.
	 * Para não disponibilizar ações que já são vinculadas, a função
	 * verifica quais as competências e comparam com as ações que existem.
	 * As que já forem vinculadas são removidas da visualização.
	 *  
	 * 
	 * @param comps - lista de competências da entidade/órgão
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
	 * Função listarAcoesPendentes
	 * Lista ações com status Pendente.
	 */
	public void listarAcoesPendentes(){
		acoesPendentes = AcoesDAO.listPorStatus("Pendente");
	}
	
	/**
	 * Função listarAcoesNãoVinculadas
	 * Lista ações com status NãoVinculada.
	 */

	public void listarAcoesNaoVinculadas(){
		acoesNaoVinculadas = AcoesDAO.listPorStatus("Não-vinculada");
	}
	
	/**
	 * Função listarAcoesVinculadas
	 * Lista ações com status Vinculada.
	 */
	
	public void listarAcoesVinculadas(){
		acoesVinculadas = AcoesDAO.listPorStatus("Vinculada");
	}
	
	
	/**
	 * Função acaoLigadaEntidadeAtiva
	 * Função para verificar quais ações estão vinculadas a entidades ativas.
	 * 
	 * @return uma lista somente de ações que estão vinculadas a entidades ativas. 
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
	 * Função autenticaAcao
	 * Função que torna o status de Pendente para Não-vinculada.
	 * A função remove a ação da lista de ações pendentes e a 
	 * insere na lista de ações não vinculadas.
	 * 
	 * @param acao - a ação que terá o sttaus alterado
	 */
	
	public void autenticaAcao(Acoes acao) {
		if(usuarioBean.verificaGestor() || usuarioBean.verificaAdmin()) {
			acao.setStatus("Não-vinculada");
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
		acoesNaoVinculadas = new ArrayList<Acoes>(AcoesDAO.listPorStatus("Não-Vinculada"));
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