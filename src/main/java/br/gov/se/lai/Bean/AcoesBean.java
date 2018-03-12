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
	
	
	@PostConstruct
	public void init() {
		acao = new Acoes();
		usuarioBean = (UsuarioBean) HibernateUtil.RecuperarDaSessao("usuario");	
		acoesPendentes = new ArrayList<Acoes>(AcoesDAO.listPorStatus("Pendente"));
		acoesNaoVinculadas = new ArrayList<Acoes>(AcoesDAO.listPorStatus("Não-Vinculada"));
		acoesVinculadas = new ArrayList<Acoes>(AcoesDAO.listPorStatus("Vinculada"));
		acoes = new ArrayList<Acoes>(acoesVinculadas);
		acoes.addAll(acoesNaoVinculadas);		

	}
	
	public static void carregarLista() {
		if(CompetenciasBean.idAcoes == 0) {
			acoes = new ArrayList<Acoes>(AcoesDAO.list());
		}else {
			acoes = null;
		}
	}
	
	public void limpaAcao() {
		acao = new Acoes();
	}
	
	public void save() {
		if(verificaPermissao() ) {
			if(usuarioBean.verificaGestor()) {
				acao.setStatus("Não-vinculada");
				AcoesDAO.saveOrUpdate(acao);
				getAcoesNaoVinculadas().add(acao);
				getAcoes().add(acao);
			}else if(usuarioBean.verificaResponsavel() || usuarioBean.verificaResponsavelCidadaoPerfil() ){
				acao.setStatus("Pendente");
				AcoesDAO.saveOrUpdate(acao);
				getAcoesPendentes().add(acao);
			}
		}else {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_WARN, "Ação não eftuada.", "Usuário sem permissão."));
		}
		acao = new Acoes();
	}
	
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
	
	public void listarAcoesPendentes(){
		acoesPendentes = AcoesDAO.listPorStatus("Pendente");
	}

	public void listarAcoesNaoVinculadas(){
		acoesNaoVinculadas = AcoesDAO.listPorStatus("Não-vinculada");
	}

	public void listarAcoesVinculadas(){
		acoesVinculadas = AcoesDAO.listPorStatus("Vinculada");
	}
	
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
	
	public void autenticaAcao(Acoes acao) {
		acao.setStatus("Não-vinculada");
		AcoesDAO.saveOrUpdate(acao);
		getAcoesPendentes().remove(acao);
		getAcoesNaoVinculadas().add(acao);
		getAcoes().add(acao);
	}
	

	@Override
	public boolean verificaPermissao() {
		if(usuarioBean.verificaGestor() || usuarioBean.verificaResponsavel()|| usuarioBean.verificaResponsavelCidadaoPerfil()) {
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

		

}