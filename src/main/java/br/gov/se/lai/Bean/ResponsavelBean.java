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
import br.gov.se.lai.DAO.ResponsavelDAO;
import br.gov.se.lai.DAO.UsuarioDAO;
import br.gov.se.lai.entity.Entidades;
import br.gov.se.lai.entity.Responsavel;
import br.gov.se.lai.entity.Usuario;
import br.gov.se.lai.utils.HibernateUtil;


@ManagedBean(name = "responsavel")
@SessionScoped
public class ResponsavelBean implements Serializable{
	
	/**
	 * 
	 */
	
	private static final long serialVersionUID = -534835121161473086L;
	private Responsavel responsavel;
	private List<Entidades> entidades;
	private UsuarioBean usuarioBean ;
	private Usuario usuario;
	private int idEntidade;
	private int nivel;
	private String email;
	private String nick;
	private boolean ativo;
	private boolean permissao;
	private List<Responsavel> todosResponsaveis;

	
	@PostConstruct
	public void init() {
		usuarioBean = (UsuarioBean) HibernateUtil.RecuperarDaSessao("usuario");	
		perfilGestorGeral();
		this.responsavel = new Responsavel();
		todosResponsaveis = ResponsavelDAO.list();
	}
	
	public String save() {
		try {
			
			this.responsavel.setEntidades(EntidadesDAO.find(this.idEntidade));
			this.responsavel.setAtivo(true);
			this.usuario = UsuarioDAO.buscarUsuario(nick);
			this.usuario.setPerfil((short)2);
			this.responsavel.setUsuario(this.usuario);
			ResponsavelDAO.saveOrUpdate(responsavel);
			UsuarioDAO.saveOrUpdate(usuario);	
			todosResponsaveis.add(responsavel);
			responsavel = new Responsavel();
			idEntidade = 0;
			nick = null;
			return "/Consulta/consulta_responsavel.xhtml?faces-redirect=true";
		}catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Usuario não encontrado!", null));
			return null;
		}
	}
	
	public void delete() {
		//Será deletado a instancia de responsável? Ou apenas colocar como status inativo?
		if(verificaAcesso()) {
			this.responsavel = ResponsavelDAO.findResponsavelEmail(email);
			this.responsavel.getUsuario().setPerfil((short)-1);
			ResponsavelDAO.saveOrUpdate(responsavel);
		}
	}
	
	public String edit() {
		if(verificaAcesso() ) {
			this.usuario = UsuarioDAO.buscarUsuario(nick);
			this.responsavel.setUsuario(this.usuario);
			ResponsavelDAO.saveOrUpdate(responsavel);
		}	
		
		return "/index";
	}
	
	public String alterarVinculo() {
		if(verificaAcesso()) {
			this.usuario = UsuarioDAO.buscarUsuario(nick);
			this.responsavel.setUsuario(this.usuario);
			ResponsavelDAO.saveOrUpdate(responsavel);
		}	
		return "Consulta/consultar_responsavel";
	}
	
	@SuppressWarnings("unchecked")
	public void selecionarResponsavel() {
		Usuario usuarioResponsavel = UsuarioDAO.buscarUsuario(nick);
		List<Responsavel> resp =new ArrayList<Responsavel>(usuarioResponsavel.getResponsavels());
		this.responsavel =  resp.get(0);		
	}
	
	public String alterarDadosUsuario() {
		responsavel.setEntidades(EntidadesDAO.find(idEntidade));
		ResponsavelDAO.saveOrUpdate(responsavel);
		responsavel = new Responsavel();
		idEntidade = 0;
		return "Consulta/consultar_responsavel";
	}
	
	public boolean verificaAcesso() {
		if(verificaGestor() || verificaAdmin()) {
			return true;
		}else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Acesso Negado!", null));
			return false;
		}
	}
	
	public boolean verificaGestor() {
		if ((usuarioBean.getUsuario().getPerfil() == 2 && (new ArrayList<Responsavel>(usuarioBean.getUsuario().getResponsavels()).get(0).getNivel().equals((short)3)))){
			return true;
		}else {
			return false;
		}
	}

	public boolean verificaAdmin() {
		if ( usuarioBean.getUsuario().getPerfil() == 4 ){
			return true;
		}else {
			return false;
		}
	}
		
	public boolean verificaExistenciaResponsavel(Usuario usuario) {
		if(usuario.getPerfil() == (short) 2) {
			return true;
		}else {
			return false;			
		}
	}
	
	public static int responsavelDisponivel(int instancia, int entidadeId) {
		boolean busca = false;
		Responsavel respBusca =  new Responsavel();
		while(instancia <= 3) {			
			try{
				List<Responsavel> resp = ResponsavelDAO.findResponsavelEntidade(entidadeId, instancia);
				for (Responsavel r : resp) {
					if(r.isAtivo()) {
						busca = true;
						respBusca = r;
						break;
					}
				}
				if(busca == true) {
					break;
				}else {
					instancia++;
				}
			}catch (NullPointerException e) {
					responsavelDisponivel(instancia+1, entidadeId);
				}
		}
		
		if(busca == false) {
			return -1;
		}else {
			return respBusca.getIdResponsavel();
		}
	}
	
	public String cadResponsavel() {
		List<Responsavel> resp = new ArrayList<Responsavel>(usuarioBean.getUsuario().getResponsavels());
		String retorno = "";
		for (Responsavel r : resp) {
			if(r.isAtivo() && verificaAcesso()) {
				popularListaEntidadesParaCadastro(r);
				retorno = "/Cadastro/cad_responsavel";
				break;
			}
		}
		
		if(retorno.length() > 0) {
			return retorno;
		}else{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Acesso Negado!", null));
			return null;
		}
	}
	
	public void popularListaEntidadesParaCadastro(Responsavel r) {
			if(r.getEntidades().isAtiva() ) {
				if(r.getEntidades().getIdEntidades().equals(1)) {
					this.entidades = new ArrayList<Entidades>(EntidadesDAO.listAtivas());
				}else if(!(r.getEntidades().isOrgao())) {
					this.entidades = new ArrayList<Entidades>(EntidadesDAO.listPersonalizada(r.getEntidades().getIdEntidades()));
				}else if (r.getEntidades().isOrgao()) {
					this.entidades = new ArrayList<Entidades>(EntidadesDAO.listOrgaoEntidade(r.getEntidades().getIdEntidades()));
				}
			}
	}
	
	@SuppressWarnings("unchecked")
	public void perfilGestorGeral() {
		try {
			responsavel = ResponsavelDAO.findResponsavelUsuario(usuarioBean.getUsuario().getIdUsuario()).get(0);
			if (responsavel.getEntidades().getIdEntidades().equals(1)) {
				this.entidades = new ArrayList<Entidades>(EntidadesDAO.listAtivas());
				permissao = true;
			} else {
				this.entidades = new ArrayList<Entidades>(
						EntidadesDAO.listPersonalizada(responsavel.getEntidades().getIdEntidades()));
				permissao = false;
			}
			responsavel = new Responsavel();
		} catch (IndexOutOfBoundsException e) {
			this.entidades = new ArrayList<Entidades>(EntidadesDAO.listAtivas());
		}
	}
	
//GETTERS E SETTERS ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	
	public Responsavel getResponsavel() {
		return responsavel;
	}

	public void setResponsavel(Responsavel responsavel) {
		this.responsavel = responsavel;
	}

	public List<Entidades> getEntidades() {
		return entidades;
	}

	public void setEntidades(List<Entidades> entidades) {
		this.entidades = entidades;
	}

	public int getIdEntidade() {
		return idEntidade;
	}

	public void setIdEntidade(int idEntidade) {
		this.idEntidade = idEntidade;
	}

	public int getNivel() {
		return nivel;
	}

	public void setNivel(int nivel) {
		this.nivel = nivel;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}
	
	public List<Responsavel> getTodosResponsaveis() {
		return todosResponsaveis;
	}
	
	public void setTodosResponsaveis(List<Responsavel> todosResponsaveis) {
		this.todosResponsaveis = todosResponsaveis;
	}

	public boolean isPermissao() {
		return permissao;
	}

	public void setPermissao(boolean permissao) {
		this.permissao = permissao;
	}
	
	
}
