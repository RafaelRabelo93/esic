package br.gov.se.lai.Bean;

import java.io.Serializable;
import java.util.ArrayList;
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
import br.gov.se.lai.entity.Usuario;
import br.gov.se.lai.utils.HibernateUtil;
import br.gov.se.lai.utils.PermissaoUsuario;


@ManagedBean(name = "entidades")
@SessionScoped
public class EntidadesBean implements Serializable, PermissaoUsuario{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1157796158944497538L;
	private Entidades entidades;
	private Usuario user ;
	private int idOrgaos;
	private int idEntidades;
	private String nome;
	private boolean ativa;
	private boolean forOrgao;
	private List<Entidades> listOrgao;
	private List<Entidades> todasEntidades;
	
	
	
	@PostConstruct
	public void init() {
		entidades = new Entidades();
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
	
	public String edit() {
		if(verificaPermissao()) {
			EntidadesDAO.saveOrUpdate(entidades);
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
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean verificaPermissao() {
		if(user.getPerfil() == 4 || (user.getPerfil() == 2 && 
				((ArrayList<Responsavel>) user.getResponsavels()).get(0).getNivel() == (short) 3)) {
			return true;
		}else {
			return false;
		}
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
		todasEntidades = new ArrayList<Entidades>(EntidadesDAO.list());
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

}
