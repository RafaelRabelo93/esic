package br.gov.se.lai.Bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;

import org.apache.commons.mail.EmailException;

import br.gov.se.lai.DAO.EntidadesDAO;
import br.gov.se.lai.DAO.ResponsavelDAO;
import br.gov.se.lai.DAO.UsuarioDAO;
import br.gov.se.lai.entity.Entidades;
import br.gov.se.lai.entity.Responsavel;
import br.gov.se.lai.entity.Usuario;
import br.gov.se.lai.utils.HibernateUtil;
import br.gov.se.lai.utils.NotificacaoEmail;


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
	private static List<Responsavel> listRespDaEntidade ;

	
	@PostConstruct
	public void init() {
		usuarioBean = (UsuarioBean) HibernateUtil.RecuperarDaSessao("usuario");	
		this.responsavel = new Responsavel();
		perfilGestorGeral();
		pegarParamURL();
		todosResponsaveis = ResponsavelDAO.list();
		listRespDaEntidade = ResponsavelDAO.findResponsavelUsuarioAtivo(usuarioBean.getUsuario().getIdUsuario());
	}
	
	public String save() {
		try {
			this.usuario = UsuarioDAO.buscarUsuario(nick);

			if(!verificaExistenciaResponsavelNaEntidade(usuario, this.idEntidade)) {
				try {
				
					this.responsavel.setEntidades(EntidadesDAO.find(this.idEntidade));
					this.responsavel.setAtivo(true);
					if(ehGestorSistema()) {
						this.usuario.setPerfil((short)5);
						this.responsavel.setNivel((short)3);
					}else if(ehCidadaoRepresentante(usuario)) {
						this.usuario.setPerfil((short)4);
					}else if(ehRepresentante(usuario)){
						this.usuario.setPerfil((short)4);
					}else {
						this.usuario.setPerfil((short)2);
					}
					this.responsavel.setUsuario(this.usuario);
					ResponsavelDAO.saveOrUpdate(responsavel);
					UsuarioDAO.saveOrUpdate(usuario);	
					todosResponsaveis.add(responsavel);
					responsavel = new Responsavel();
					idEntidade = 0;
					nick = null;
					return "/Consulta/consulta_responsavel.xhtml?faces-redirect=true";
				}catch (Exception e) {
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Entidade não encontrado!", null));
					return null;
				}
			}else{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Responsável já alocado para essa entidade!", null));
				return null;
				
			}
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
	
	public boolean ehGestorSistema() { 
		if(responsavel.getNivel() == 5) {
			return true;
		}else {
			return false;
		}
	}
	
	public boolean ehCidadaoRepresentante(Usuario usuario) { 
		if(usuario.getPerfil() == 3) {
			return true;
		}else {
			return false;
		}
	}

	public boolean ehRepresentante(Usuario usuario) { 
		if(usuario.getPerfil() == 4) {
			return true;
		}else {
			return false;
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
	
	public boolean verificaExistenciaResponsavelNaEntidade(Usuario usuario, int idEntidadeEntrada) {
		boolean retorno = false;
		if(verificaExistenciaResponsavel(usuario)) {
			try{
				List<Responsavel> respVinculadosAoNovoUsuario = ResponsavelDAO.findResponsavelUsuario(usuario.getIdUsuario());
				if(!respVinculadosAoNovoUsuario.isEmpty()){
					for (Responsavel resp : respVinculadosAoNovoUsuario) {
						if(resp.getEntidades().getIdEntidades().equals(idEntidadeEntrada)) {
							retorno = true;
							break;
						}
					}
				}
			}catch (IndexOutOfBoundsException e) {
			}
			return retorno;
		}else {
			return retorno;
		}
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
		if(verificaExistenciaGestorSistema(usuarioBean.getUsuario()) || verificaAcesso()) {
			responsavel.setEntidades(EntidadesDAO.find(idEntidade));
			ResponsavelDAO.saveOrUpdate(responsavel);
			responsavel = new Responsavel();
			idEntidade = 0;
		}
		responsavel = new Responsavel();
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
		if ( usuarioBean.getUsuario().getPerfil() == 6 ){
			return true;
		}else {
			return false;
		}
	}
		
	public boolean verificaExistenciaResponsavel(Usuario usuario) {
		if(usuario.getPerfil() == (short) 2 || usuario.getPerfil() == (short) 4) {
			return true;
		}else {
			return false;			
		}
	}

	public boolean verificaExistenciaGestorSistema(Usuario usuario) {
		if(usuario.getPerfil() == (short) 5 ) {
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
				List<Responsavel> resp = ResponsavelDAO.findResponsavelEntidadeNivel(entidadeId, instancia);
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
	

	public void perfilGestorGeral() {
		if(usuarioBean.getUsuario().getPerfil() == 6) {
			this.entidades = new ArrayList<Entidades>(EntidadesDAO.listAtivas());
		}else{
			try {
				if(usuarioBean.getUsuario().getPerfil() == 5) {
					this.entidades = new ArrayList<Entidades>(EntidadesDAO.listAtivas());
					permissao = true;
				}else {
				List<Responsavel> respList= ResponsavelDAO.findResponsavelUsuario(usuarioBean.getUsuario().getIdUsuario());
				for (Responsavel responsavel : respList) {
						try {
							this.entidades.addAll(EntidadesDAO.listPersonalizada(responsavel.getEntidades().getIdEntidades()));
						}catch (NullPointerException e) {
							this.entidades = new ArrayList<Entidades>(EntidadesDAO.listPersonalizada(responsavel.getEntidades().getIdEntidades()));
						}finally {
							permissao = false;
						}
					}
				}
					responsavel = new Responsavel();
			} catch (IndexOutOfBoundsException e) {
			}
		}
	}
	
	
	public String redirectCadastroUsuario() {
		responsavel = new Responsavel();
		return "/Cadastro/cad_responsavel.xhtml?faces-redirect=true";
	}
	
	public static boolean permissaoDeAcessoEntidades(int idOrgao, int idEntidade) {
		boolean retorno = false;
		for (Responsavel respo : listRespDaEntidade) {
			if(respo.getEntidades().getIdOrgaos() == idOrgao) {
				if(respo.getEntidades().getIdEntidades().equals(idEntidade)) {
					retorno =  true;
				}else if(responsavelDisponivel(1, idEntidade) == -1 ){
					retorno =  true;
				}
//				break;
			}
		}
		return retorno;
	}
	
	public List<Entidades> possivelCadastrarResponsavelDasEntidades(){
		if(usuarioBean.getUsuario().getPerfil() == (short)5 || usuarioBean.getUsuario().getPerfil() == (short)6) {
			List<Entidades> entidadesPossiveisDeCadastro = new ArrayList<>(EntidadesDAO.listAtivas());
			return entidadesPossiveisDeCadastro;
		}else {
			List<Entidades> entidadesPossiveisDeCadastro = new ArrayList<>();
			for (Responsavel resp : listRespDaEntidade) {
				if (resp.getNivel().equals((short) 3)) {
					entidadesPossiveisDeCadastro.add(resp.getEntidades());
				}
			}
			return entidadesPossiveisDeCadastro;
		}
	}
	

	public boolean possivelEditarResponsavelDasEntidades(int idOrgao){
		boolean retorno = false;
		for (Responsavel resp : listRespDaEntidade) {
			if(resp.getNivel().equals((short)3) && resp.getEntidades().getIdOrgaos() == idOrgao) {
				retorno =  true;
				break;
			}
		}
		return retorno;
	}
	
	public boolean bloquearEdicaoPessoalPermissoes(int idUsuarioResponsavelAvaliado) {
		if(idUsuarioResponsavelAvaliado == usuarioBean.getUsuario().getIdUsuario()) {
			return true;
		}else {
			return false;
		}
	}
	
	public Entidades pegarEntidade() {
		Entidades entidade = EntidadesDAO.find(idEntidade);
		return entidade;
	}
	
	public String requisitarCadastroResponsavel() {
		Entidades ent = pegarEntidade();
		String hashcode=UsuarioBean.generateSessionId();
		String mensagem = "O usuário "+ usuarioBean.getUsuario().getNome()+" está requisitando acesso como representante no e-SIC."
						  + "\n\n >> Dados do usuário:"
						  + "\n\n Usuario: "+usuario.getNome()
						  + "\n\n Nick: "+getNick()
						  +"\n Email: "+getEmail()
						  +"\n Entidade:" + ent.getNome() + " - "+ent.getSigla()
						  + "\n\n Clique aqui: ";
		int idDestinatario= responsavelDisponivel(3, ent.getIdEntidades());
		if(idDestinatario != -1) {
			String destinatario = ResponsavelDAO.findResponsavel(idDestinatario).getEmail();
			NotificacaoEmail.enviarEmailRequisicaoResponsavel(usuario.getIdUsuario(), getIdEntidade(),getEmail(), hashcode, destinatario, mensagem);
			return "/index.xhtml?faces-redirect=true";
		}else {
			idDestinatario= responsavelDisponivel(3, ent.getIdOrgaos());
			if(idDestinatario != -1) {
				String destinatario = ResponsavelDAO.findResponsavel(idDestinatario).getEmail();
				NotificacaoEmail.enviarEmailRequisicaoResponsavel(usuario.getIdUsuario(), getIdEntidade(), getEmail(), hashcode, destinatario, mensagem);
				return "/index.xhtml?faces-redirect=true";
			}else {
				//Busca
				return "/index.xhtml?faces-redirect=true";
			}
		}
	}
	
	public void pegarParamURL() {
		if(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
				.get("access-key") != null ) {
			
		this.responsavel = new Responsavel();
		 this.responsavel.setEmail( FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
				.get("mail"));
		 idEntidade = (Integer.parseInt(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
					.get("identidade")));
		 nick = (UsuarioDAO.findUsuario(Integer.parseInt((FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
				 .get("user"))))).getNick();
		}
	}
	
	public List<Entidades> entidadesSolicitacaoResponsavel(){
		Set ids = new HashSet<>(ResponsavelDAO.listEntidadePossuemGestores());
		return EntidadesDAO.listAtivas();
	}
	
	
	public boolean temPerfilAtivo() {
		return (ResponsavelDAO.findResponsavelUsuarioAtivo(usuarioBean.getUsuario().getIdUsuario()).size() > 0) ;
	}
	
	public static String retornaListaEmail(Usuario usuario) {
		String emailRetorno = "";
		for (Responsavel resp : ResponsavelDAO.findResponsavelUsuarioAtivo(usuario.getIdUsuario())) {
			emailRetorno += resp.getEmail()+"\n";
		}
		return emailRetorno;
	}

	public static String retornaListaEntidadeSiglas(Usuario usuario) {
		String siglaRetorno = "";
		for (Responsavel resp : ResponsavelDAO.findResponsavelUsuarioAtivo(usuario.getIdUsuario())) {
			siglaRetorno += resp.getEntidades().getSigla()+"\n";
		}
		return siglaRetorno;
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

	public List<Responsavel> getListRespDaEntidade() {
		return listRespDaEntidade;
	}

	public void setListRespDaEntidade(List<Responsavel> listRespDaEntidade) {
		this.listRespDaEntidade = listRespDaEntidade;
	}
	
	
	
	
	
}
