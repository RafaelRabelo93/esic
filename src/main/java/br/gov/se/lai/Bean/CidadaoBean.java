package br.gov.se.lai.Bean;

import java.util.List;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

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
import br.gov.se.lai.utils.PermissaoUsuario;

@ManagedBean(name = "cidadao")
@SessionScoped
public class CidadaoBean implements Serializable, PermissaoUsuario{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6367328853853098867L;
	private Cidadao cidadao;
	private Usuario usuario;
	private String email;
	private String cpf;
	private String rg;
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
	private String tel;
	private UsuarioBean usuarioBean;
	private int renda;
	private String numero;
	private String mensagemErro;
	
	@PostConstruct
	public void init() {
		cidadao = new Cidadao();	
	}
	
	public String save() {
		usuarioBean = (UsuarioBean) HibernateUtil.RecuperarDaSessao("usuario");
		if ( !cpfCadastrado(cpf) && !rgCadastrado(rg)) {
			this.usuario = usuarioBean.getUsuario();
			if (this.cidadao == null) {
				cidadao = new Cidadao();
			}
			cidadao.setCpf(cpf);
			cidadao.setRg(rg);
			try {
				if (!getNumero().isEmpty()) { cidadao.setNumero(numero);}
			}catch (NullPointerException e) { cidadao.setNumero(null);} 
			cidadao.setUsuario(this.usuario);
			if (CidadaoDAO.saveOrUpdate(cidadao)) {
				usuarioBean.setEmail(cidadao.getEmail());
				//usuario.getCidadaos().add(cidadao);
				if(ehRepresentanteCidadao(usuario)) {
					this.usuario.setPerfil((short) 4);
				}else {
					this.usuario.setPerfil((short) 3);
				}
				UsuarioDAO.saveOrUpdate(this.usuario);
			}
			return "/index";
		} else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
					mensagemErro, null));
			return null;
		}
	}
	
	@SuppressWarnings("finally")
	public boolean cpfCadastrado(String cpf) {
		try {
			if (!cpf.equals("") ) {
				List<Cidadao> cpfLista = new ArrayList<Cidadao>(CidadaoDAO.findCPFs());
				if (cpfLista.contains(cpf)) {
					mensagemErro = "CPF já existente no sistema.";
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} catch (Exception e) {
		} finally {
			return false;
		}
	}
	
	@SuppressWarnings("finally")
	public boolean rgCadastrado(String rg) {
		try {
			if (!rg.equals("")) {
				List<Cidadao> rgLista = new ArrayList<Cidadao>(CidadaoDAO.findRGs());
				if (rgLista.contains(rg) && !rg.equals("")) {
					mensagemErro = "RG já existente no sistema.";
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} catch (Exception e) {
		} finally {
			return false;
		}
	}
	
	public boolean ehRepresentanteCidadao(Usuario usuario) { 
		if(usuario.getPerfil() == 2 ) {
			return true;
		}else {
			return false;
		}
	}
	
	public String delete() {

		return "usuario";
	}
	
	public String edit() {
		usuarioBean = (UsuarioBean) HibernateUtil.RecuperarDaSessao("usuario");
		if (verificaPermissao()) {
			this.usuario = usuarioBean.getUsuario();
			cidadao.setUsuario(this.usuario);
			CidadaoDAO.saveOrUpdate(cidadao);
			return "/index";
		} else {
			return null;
		}
	}
	
	@Override
	public boolean verificaPermissao() {
		if(usuarioBean.getUsuario().getPerfil() == 1) {
			return true;
		}else {
			return false;
		}
	}
	
	public void limparCidadaoBean() {
		setEmail(null);
		setBairro(null);
		setCep(null);
		setComplemento(null);
		setCidade(null);
		setEndereco(null);
		setEstado(null);
		setNumero(null);
	}

//GETTERS E SETTERS ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	

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

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getRg() {
		return rg;
	}

	public void setRg(String rg) {
		this.rg = rg;
	}

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

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
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
	

}
