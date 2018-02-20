package br.gov.se.lai.Bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import br.gov.se.lai.DAO.AcoesDAO;
import br.gov.se.lai.DAO.CompetenciasDAO;
import br.gov.se.lai.entity.Acoes;
import br.gov.se.lai.entity.Competencias;
import br.gov.se.lai.utils.HibernateUtil;
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
	
	
	@PostConstruct
	public void init() {
		acao = new Acoes();
		acoes = new ArrayList<Acoes>(AcoesDAO.list());		
		usuarioBean = (UsuarioBean) HibernateUtil.RecuperarDaSessao("usuario");	
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
			AcoesDAO.saveOrUpdate(acao);
			acoes.add(acao);
		}
		acao = new Acoes();
	}
	
	public void delete() {
		if(verificaPermissao() ) {
			int ind = acoes.indexOf(acao);
			acoes.remove(ind);
			AcoesDAO.delete(acao);
			acao = new Acoes();
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
	
	public List<Acoes> AcaoLigadaEntidadeAtiva() {

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
	

	@Override
	public boolean verificaPermissao() {
		if(usuarioBean.getUsuario().getPerfil() == 2 || usuarioBean.getUsuario().getPerfil() != 4 || usuarioBean.getUsuario().getPerfil() != 5 || usuarioBean.getUsuario().getPerfil() != 6 ) {
			return true;
		}else {
			return false;
		}
	}
	
	public String redirecionarConsultaAcoes() {
		carregarLista();
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


}