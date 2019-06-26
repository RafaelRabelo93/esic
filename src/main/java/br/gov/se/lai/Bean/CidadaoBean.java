package br.gov.se.lai.Bean;

import java.util.List;
import java.util.Set;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import br.gov.se.lai.DAO.CidadaoDAO;
import br.gov.se.lai.DAO.UsuarioDAO;
import br.gov.se.lai.entity.Cidadao;
import br.gov.se.lai.entity.Usuario;
import br.gov.se.lai.utils.HibernateUtil;
import br.gov.se.lai.utils.NotificacaoEmail;
import br.gov.se.lai.utils.PermissaoUsuario;

@ManagedBean(name = "cidadao")
@SessionScoped
public class CidadaoBean implements Serializable, PermissaoUsuario {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6367328853853098867L;
	private Cidadao cidadao;
	private Cidadao cidAlt;
	private Cidadao cidCad = new Cidadao();
	private Usuario usuario;
	private String email;
//	private String cpf;
//	private String rg;
	private String orgaoexp;
	private String bairro;
	private String complemento;
	private Date datanasc;
	private String sexo;
	private int escolaridade;
	private String profissao;
	private String endereco;
	private String estado;
	private String cidade;
	private String cep;
//	private UsuarioBean usuarioBean;
	private int renda;
	private String numero;
	private String telefone;
	private String celular;
	private String mensagemErro;
	private String mensagemErro2;

	@PostConstruct
	public void init() {
		cidadao = new Cidadao();
		cidCad = new Cidadao();
	}

	/**
	 * Função save Salva o perfil de cidadão.
	 * 
	 * @return
	 */
	public String save() {
//		usuarioBean = (UsuarioBean) HibernateUtil.RecuperarDaSessao("usuario");
//		this.usuario = usuarioBean.getUsuario();
		
		if (!cpfCadastrado(this.cidadao.getCpf()) && !rgCadastrado(this.cidadao.getRg()) && !emailCadastrado(this.cidadao.getEmail())) {
			
			this.cidadao.setUsuario(this.usuario);
			
			if(this.cidadao.getNumero().isEmpty()) {
				this.cidadao.setNumero(null);
			}
			
			Set<Cidadao> cidSet = new HashSet<Cidadao>();
			cidSet.add(this.cidadao);
			System.out.println("Set solto: "+ cidSet);
			this.usuario.setCidadaos(cidSet);
			
			
			System.out.println("Set salvo: "+ this.usuario.getCidadaos());
			
			if (CidadaoDAO.saveOrUpdate(this.cidadao) && UsuarioDAO.saveOrUpdate(this.usuario)) {
				CidadaoDAO.saveOrUpdate(this.cidadao);
				if(ehRepresentanteCidadao(this.usuario)) 
					this.usuario.setPerfil((short)4);
				else
					this.usuario.setPerfil((short)3);
				UsuarioDAO.saveOrUpdate(this.usuario);
				
				
				NotificacaoEmail.enviarEmailCadastroCid(cidadao);
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Cidadão cadastrado com sucesso."));
				return "/index?faces-redirect=true";
			} else {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Ocorreu um erro ao realizar cadastro.", "Por favor tente, novamente."));
				return null;
			}
		
		} else {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_WARN, mensagemErro, mensagemErro2));
			return null;
		}
	}
	
	
	public String edit() {
		if (!cpfCadastrado(this.cidAlt.getCpf()) && !rgCadastrado(this.cidAlt.getRg()) && !emailCadastrado(this.cidAlt.getEmail())) {
			
			if (this.cidadao == null) {
				this.cidadao = new Cidadao();
			}
			
			this.cidadao.setEmail(this.cidAlt.getEmail());
			this.cidadao.setCpf(this.cidAlt.getCpf());
			this.cidadao.setTipo(this.cidAlt.getTipo());
			this.cidadao.setRg(this.cidAlt.getRg());
			this.cidadao.setOrgaexp(this.cidAlt.getOrgaexp());
			this.cidadao.setDatanasc(this.cidAlt.getDatanasc());
			this.cidadao.setSexo(this.cidAlt.getSexo());
			this.cidadao.setEscolaridade(this.cidAlt.getEscolaridade());
			this.cidadao.setProfissao(this.cidAlt.getProfissao());
			this.cidadao.setEndereco(this.cidAlt.getEndereco());
			this.cidadao.setEstado(this.cidAlt.getEstado());
			this.cidadao.setCidade(this.cidAlt.getCidade());
			this.cidadao.setCep(this.cidAlt.getCep());
			this.cidadao.setRenda(this.cidAlt.getRenda());
			this.cidadao.setSolicitacaos(this.cidAlt.getSolicitacaos());
			this.cidadao.setNumero(this.cidAlt.getNumero());
			this.cidadao.setTelefone(this.cidAlt.getTelefone());
			this.cidadao.setCelular(this.cidAlt.getCelular());
			
			if(this.cidadao.getNumero().isEmpty()) {
				this.cidadao.setNumero(null);
			}
			
			this.cidadao.setUsuario(this.usuario);
			
			
			if (CidadaoDAO.saveOrUpdate(this.cidadao) && UsuarioDAO.saveOrUpdate(this.usuario)) {
				CidadaoDAO.saveOrUpdate(this.cidadao);
				UsuarioDAO.saveOrUpdate(this.usuario);
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Dados alterados com sucesso."));
				return "/index";
			} else {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Ocorreu um erro ao realizar cadastro.", "Por favor tente, novamente."));
				return null;
			}
			
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, mensagemErro, mensagemErro2));
			return null;
		}
	}

	
	public String loadUsuario() {
//		usuarioBean = (UsuarioBean) HibernateUtil.RecuperarDaSessao("usuario");
//		this.usuario = usuarioBean.getUsuario();
		this.cidadao = new Cidadao();
		this.cidadao.setTipo(true);
		
		return "/Cadastro/cad_cidadao?faces-redirect=true";
	}
	
	/**
	 * Redireciona para página alterar dados caso seja necessário
	 */
	public String alterarDadosUsuario() {
//		usuarioBean = (UsuarioBean) HibernateUtil.RecuperarDaSessao("usuario");
//		this.usuario = usuarioBean.getUsuario();
		if (usuario.getPerfil() == (short) 3 || usuario.getPerfil() == (short) 4) {
			this.cidadao = CidadaoDAO.findCidadaoUsuario(usuario.getIdUsuario());
			this.cidAlt = (Cidadao) this.cidadao.clone();
			return "/Alterar/alterar_usuario";
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Cidadão não cadastrado.", "Por favor complete seu cadastro."));
			return null;
		}
	}
	
	
	/**
	 * Função de cpfCadastrado Verifica se o cpf inserido pelo usuário já consta no
	 * banco de dados.
	 * 
	 * @param cpf
	 * @return
	 */
	
	public boolean cpfCadastrado(String cpf) {
		boolean retorno = false;
		try {
			if (!cpf.equals("")) {
				List<String> cpfLista = new ArrayList<String>(CidadaoDAO.findCPFs());
				
//				System.out.println("\n\n CPF comparado: " + cpf);
//				System.out.println("\n Lista original CPFs:");
//				for(String c : cpfLista) System.out.println(c);
//				System.out.println("\n CPF removido: " + this.cidadao.getCpf());
				
				cpfLista.remove(this.cidadao.getCpf());
				
//				System.out.println("\n Lista alterada CPFs:");
//				for(String c : cpfLista) System.out.println(c);
				
				if (cpfLista.contains(cpf)) {
					mensagemErro = "CPF "+ cpf + " já cadastrado no sistema.";
					mensagemErro2= "";
					retorno =  true;
				}
			}
		} catch (Exception e) {
		} 
		return retorno;
	}

	/**
	 * Função rgCadastrado
	 * 
	 * Verifica se o rg digitado pelo usuário já consta no banco de dados.
	 * 
	 * @param rg
	 * @return
	 */
	public boolean rgCadastrado(String rg) {
		boolean retorno = false;
		try {
			if (!rg.equals("")) {
				List<String> rgLista = new ArrayList<String>(CidadaoDAO.findRGs());
				rgLista.remove(this.cidadao.getRg());
				if (rgLista.contains(rg)) {
					mensagemErro = "RG "+ rg + " já existente no sistema.";
					mensagemErro2= "";
					retorno = true;
				}
			}
		} catch (Exception e) {
		} 
		return retorno;
	}

	/**
	 * Função email cadastrado
	 * 
	 * Verifica se aquele email já consta no armazenamento.
	 * 
	 * @param email
	 * @return
	 */
	public boolean emailCadastrado(String email) {
		boolean retorno = false;
		try {
			if (!email.equals("")) {
				List<String> emailLista = new ArrayList<String>(CidadaoDAO.findEmails());
				emailLista.remove(this.cidadao.getEmail());
				if (emailLista.contains(email)) {
					mensagemErro = "Email "+ email + " já cadastrado no sistema.";
					mensagemErro2= "";
					retorno = true;
				}
			}
		}catch (IndexOutOfBoundsException e) {
		}
		return retorno;
	}
	
	/**
	 * Função que verifica se o email que o cidadao está inserindo é email pessoal.
	 * @param email
	 * @return
	 */
	public boolean emailPessoal(String email) {
		if (UsuarioBean.tratarEmail(email)) {
			mensagemErro = "Email inválido";
			mensagemErro2 = "Por favor insira um email pessoal.";
			return false;
		}else {
			return true;
		}
	}
	

	/**
	 * Função ehRepresentanteCidadao
	 * 
	 * Verifica se o perfil que solicita instancia de cidadão já se encontra
	 * vinculada a um perfil responsável.
	 * 
	 * @param usuario
	 * @return
	 */
	public boolean ehRepresentanteCidadao(Usuario usuario) {
		if (usuario.getPerfil() == 2) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Função edit
	 * 
	 * Atualiza a instancia de cidadão ligada ao usuário logado.
	 * 
	 * @return - redireciona para a página principal.
	 * 
	 */
	public String edit(Cidadao cidadao) {
//		usuarioBean = (UsuarioBean) HibernateUtil.RecuperarDaSessao("usuario");
//		this.usuario = usuarioBean.getUsuario();
		cidadao.setUsuario(this.usuario);
		CidadaoDAO.saveOrUpdate(cidadao);
		return "/index";
	}

	@Override
	public boolean verificaPermissao() {
		if (this.usuario.getPerfil() == 1) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Função limparCidadaoBean
	 * 
	 * Limpa o objeto preenchendo todos os seus campos com null.
	 */
	public void limparCidadaoBean() {
		setBairro(null);
		setCep(null);
		setComplemento(null);
		setCidade(null);
		setEndereco(null);
		setEstado(null);
		setNumero(null);
		setTelefone(null);
		setCelular(null);
	}

	// GETTERS E SETTERS
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	public Cidadao getCidadao() {
		return cidadao;
	}

	public void setCidadao(Cidadao cidadao) {
		this.cidadao = cidadao;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
//
//	public String getCpf() {
//		return cpf;
//	}
//
//	public void setCpf(String cpf) {
//		this.cpf = cpf;
//	}
//
//	public String getRg() {
//		return rg;
//	}
//
//	public void setRg(String rg) {
//		this.rg = rg;
//	}

	public String getOrgaoexp() {
		return orgaoexp;
	}

	public void setOrgaoexp(String orgaoexp) {
		this.orgaoexp = orgaoexp;
	}

	public Date getDatanasc() {
		return datanasc;
	}

	public void setDatanasc(Date datanasc) {
		this.datanasc = datanasc;
	}

	public String getSexo() {
		return sexo;
	}

	public void setSexo(String sexo) {
		this.sexo = sexo;
	}

	public int getEscolaridade() {
		return escolaridade;
	}

	public void setEscolaridade(int escolaridade) {
		this.escolaridade = escolaridade;
	}

	public String getProfissao() {
		return profissao;
	}

	public void setProfissao(String profissao) {
		this.profissao = profissao;
	}

	public String getEndereco() {
		return endereco;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public String getCep() {
		return cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public int getRenda() {
		return renda;
	}

	public void setRenda(int renda) {
		this.renda = renda;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public String getComplemento() {
		return complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	String getMensagemErro() {
		return mensagemErro;
	}

	void setMensagemErro(String mensagemErro) {
		this.mensagemErro = mensagemErro;
	}

	String getMensagemErro2() {
		return mensagemErro2;
	}

	void setMensagemErro2(String mensagemErro2) {
		this.mensagemErro2 = mensagemErro2;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public String getCelular() {
		return celular;
	}

	public void setCelular(String celular) {
		this.celular = celular;
	}

	public Cidadao getCidAlt() {
		return cidAlt;
	}

	public void setCidAlt(Cidadao cidAlt) {
		this.cidAlt = cidAlt;
	}

	public Cidadao getCidCad() {
		return cidCad;
	}

	public void setCidCad(Cidadao cidCad) {
		this.cidCad = cidCad;
	}

	
}
