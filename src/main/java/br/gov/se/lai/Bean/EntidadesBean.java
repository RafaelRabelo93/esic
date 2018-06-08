package br.gov.se.lai.Bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;

import br.gov.se.lai.DAO.EntidadesDAO;
import br.gov.se.lai.entity.Competencias;
import br.gov.se.lai.entity.Entidades;
import br.gov.se.lai.entity.Responsavel;
import br.gov.se.lai.entity.Solicitacao;
import br.gov.se.lai.entity.Usuario;
import br.gov.se.lai.utils.HibernateUtil;
import br.gov.se.lai.utils.PermissaoUsuario;


@ManagedBean(name = "entidades")
@SessionScoped
public class EntidadesBean implements Serializable, PermissaoUsuario, Comparable<Entidades>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1157796158944497538L;
	private Entidades entidades;
	private Solicitacao solicitacao;
	private Usuario user ;
	private int idOrgaos;
	private int idEntidades;
	private String nome;
	private boolean ativa;
	private boolean forOrgao = true;
	private List<Entidades> listEntidades;
	private List<Entidades> listOrgao;
	private List<Entidades> listOrgaoRelatorios;
	private List<Entidades> listOrgaosAtivos;
	private List<Entidades> todasEntidades;
	private List<Entidades> todasEntidadesAtivas;
	private List<Entidades> entidadesFiltradas;
	
	
	@PostConstruct
	public void init() {
		entidades = new Entidades();
		todasEntidades = EntidadesDAO.list();
		todasEntidadesAtivas = new ArrayList<Entidades>(EntidadesDAO.listAtivas());
		listOrgaosAtivos = EntidadesDAO.listOrgaos();
		user = ((UsuarioBean) HibernateUtil.RecuperarDaSessao("usuario")).getUsuario();	
	}
	
	public String save() {
		if(verificaPermissao()) {
			if(verificaUnicidadeNome()) {
				if(verificaUnicidadeSigla()) {

					entidades.setAtiva(true);
					entidades.setOrgao(forOrgao);
					EntidadesDAO.saveOrUpdate(entidades);
					serEntidadeOrgao(entidades);
					entidades =  new Entidades();
					return "cad_competencias2";
					
				}else {
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_WARN, "Sigla já existente no sistema.", "Escolha outra."));
					return null;
				}
			}else {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_WARN, "Nome já existente no sistema.", "Escolha outro."));
				return null;
			}
		}else{
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_WARN, "Ação negada.", "Usuário sem permissão."));
			return "/index";
		}
		
	}
	
	public void delete() {
		if(verificaPermissao() ) {
			entidades = EntidadesDAO.find(idEntidades);
			//EntidadesDAO.delete(entidades);
			entidades.setAtiva(false);
		}	
	}
	
	public String edit(Entidades entidade) {
		if(verificaPermissao()) {
			EntidadesDAO.saveOrUpdate(entidade);
			this.entidades = new Entidades();
		}
		return "/Consulta/consulta_entidades";
	}
	



	public void listarOrgaos(AjaxBehaviorEvent e) {
		if(forOrgao) {
			this.listOrgao = null;
		}else {
			this.listOrgao = EntidadesDAO.listOrgaos();
	}
	
	}
	
	public void verificaCompetenciasEntidade(){
		List<Competencias> compEnt = new ArrayList<Competencias>(this.entidades.getCompetenciases());
		AcoesBean acaobean = new AcoesBean();
		acaobean.filtrarAcoes(compEnt);
	}
	
	@Override
	public boolean verificaPermissao() {
		if(user.getPerfil() == 5 || user.getPerfil() == 6) {
			return true;
		}else {
			return false;
		}
	}
	
	public List<Entidades> exibirEntidadesEncaminhamento(){
		List<Entidades> listaAuxiliar = todasEntidades;
		List<Responsavel> resps = new ArrayList<>(user.getResponsavels());
		for (Responsavel responsavel : resps) {
			listaAuxiliar.remove(EntidadesDAO.find(responsavel.getEntidades().getIdEntidades()));
		}
		
		return listaAuxiliar;
	}
	
	public boolean verificaUnicidadeNome() {
		if(!EntidadesDAO.existeNome(entidades.getNome())) {
			return true;
		}else {
			return false;
		}
	}

	public boolean verificaUnicidadeSigla() {
		if(!EntidadesDAO.existeSigla(entidades.getSigla())) {
			return true;
		}else {
			return false;
		}
	}
	
	
	public void serEntidadeOrgao(Entidades entidade) {
		if(entidade.isOrgao()) {
			entidade.setIdOrgaos(entidade.getIdEntidades());
			EntidadesDAO.saveOrUpdate(entidade);
		}
	}
	

	public String consultarEntidades() {
		todasEntidades = EntidadesDAO.list();
		return "/Consulta/consulta_entidades";
	}
	
	public String redirecionarCadastroEntidade() {
		entidades = new Entidades();
		return  "/Cadastro/cad_entidades.xhtml?faces-redirect=true";
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

	public List<Entidades> getListOrgao() {
		return listOrgao;
	}

	public void setListOrgao(List<Entidades> listOrgao) {
		this.listOrgao = listOrgao;
	}

	public boolean isForOrgao() {
		return forOrgao;
	}

	public void setForOrgao(boolean forOrgao) {
		this.forOrgao = forOrgao;
	}

	public Solicitacao getSolicitacao() {
		return solicitacao;
	}

	public void setSolicitacao(Solicitacao solicitacao) {
		this.solicitacao = solicitacao;
	}

	public List<Entidades> getTodasEntidadesAtivas() {
		return todasEntidadesAtivas;
	}

	public void setTodasEntidadesAtivas(List<Entidades> todasEntidadesAtivas) {
		this.todasEntidadesAtivas = todasEntidadesAtivas;
	}

	public List<Entidades> getEntidadesFiltradas() {
		return entidadesFiltradas;
	}

	public void setEntidadesFiltradas(List<Entidades> entidadesFiltradas) {
		this.entidadesFiltradas = entidadesFiltradas;
	}

	public List<Entidades> getListOrgaosAtivos() {
		return listOrgaosAtivos;
	}

	public void setListOrgaosAtivos(List<Entidades> listOrgaosAtivos) {
		this.listOrgaosAtivos = listOrgaosAtivos;
	}

	@Override
	public int compareTo(Entidades e1) {
		return this.entidades.getSigla().compareTo(e1.getSigla());
	}
	
	

}
