package br.gov.se.lai.entity;
// Generated 18/09/2017 08:12:36 by Hibernate Tools 5.2.5.Final

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

/**
 * Cidadao generated by hbm2java
 */
@Entity
@Table(name = "cidadao", catalog = "esic", uniqueConstraints = { @UniqueConstraint(columnNames = "cpf"),
		@UniqueConstraint(columnNames = "email") })
public class Cidadao implements java.io.Serializable {


	private static final long serialVersionUID = -7063672246584518287L;
	private Integer idCidadao;
	private Usuario usuario;
	private String email;
	private String cpf;
	private Boolean tipo;
	private String rg;
	private String orgaexp;
	private Date datanasc;
	private String sexo;
	private Short escolaridade;
	private String profissao;
	private String endereco;
	private String estado;
	private String cidade;
	private String cep;
	private String bairro;
	private String complemento;
	private short renda;
	private String numero;
	private String telefone;
	private String celular;
	private Set<Solicitacao> solicitacaos = new HashSet<Solicitacao>(0);

	public Cidadao() {
	}

	public Cidadao(Usuario usuario, String email) {
		this.usuario = usuario;
		this.email = email;
	}

	public Cidadao(Usuario usuario, String email, String cpf, Boolean tipo, String rg, String orgaexp, Date datanasc,
			String sexo, Short escolaridade, String profissao, String endereco, String estado, String cidade,
			String cep, short renda  ,Set<Solicitacao> solicitacaos, String numero, String telefone, String celular) {

		this.usuario = usuario;
		this.email = email;
		this.cpf = cpf;
		this.tipo = tipo;
		this.rg = rg;
		this.orgaexp = orgaexp;
		this.datanasc = datanasc;
		this.sexo = sexo;
		this.escolaridade = escolaridade;
		this.profissao = profissao;
		this.endereco = endereco;
		this.estado = estado;
		this.cidade = cidade;
		this.cep = cep;
		this.renda = renda;
		this.solicitacaos = solicitacaos;
		this.numero = numero;
		this.telefone = telefone;
		this.celular = celular;
	}
	
	@Override
	public Object clone() {
	    try {
	        Cidadao cid = (Cidadao) super.clone();
	        cid.setSolicitacaos(new HashSet<Solicitacao>(0));
	        return cid;
	    } catch (CloneNotSupportedException e) {
	        return new Cidadao(
		        this.usuario,
		        this.email, 
				this.cpf,
				this.tipo,
				this.rg,
				this.orgaexp,
				this.datanasc,
				this.sexo,
				this.escolaridade,
				this.profissao,
				this.endereco,
				this.estado,
				this.cidade,
				this.cep,
				this.renda,
				this.solicitacaos,
				this.numero,
				this.telefone,
				this.celular
			);
	    }
	}
	
	@Id
	@GeneratedValue(strategy = IDENTITY)

	@Column(name = "idCidadao", unique = true, nullable = false)
	public Integer getIdCidadao() {
		return this.idCidadao;
	}

	public void setIdCidadao(Integer idCidadao) {
		this.idCidadao = idCidadao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idUsuario", nullable = false)
	public Usuario getUsuario() {
		return this.usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	@Column(name = "email", unique = true, nullable = false, length = 100)
	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column(name = "cpf", unique = true, length = 15)
	public String getCpf() {
		return this.cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	@Column(name = "tipo")
	public Boolean getTipo() {
		return this.tipo;
	}

	public void setTipo(Boolean tipo) {
		this.tipo = tipo;
	}

	@Column(name = "rg", length = 45)
	public String getRg() {
		return this.rg;
	}

	public void setRg(String rg) {
		this.rg = rg;
	}

	@Column(name = "orgaexp", length = 45)
	public String getOrgaexp() {
		return this.orgaexp;
	}

	public void setOrgaexp(String orgaexp) {
		this.orgaexp = orgaexp;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "datanasc", length = 10)
	public Date getDatanasc() {
		return this.datanasc;
	}

	public void setDatanasc(Date datanasc) {
		this.datanasc = datanasc;
	}

	@Column(name = "sexo", length = 1)
	public String getSexo() {
		return this.sexo;
	}

	public void setSexo(String sexo) {
		this.sexo = sexo;
	}

	@Column(name = "escolaridade")
	public Short getEscolaridade() {
		return this.escolaridade;
	}

	public void setEscolaridade(Short escolaridade) {
		this.escolaridade = escolaridade;
	}

	@Column(name = "profissao", length = 45)
	public String getProfissao() {
		return this.profissao;
	}

	public void setProfissao(String profissao) {
		this.profissao = profissao;
	}

	@Column(name = "endereco", length = 65535)
	public String getEndereco() {
		return this.endereco;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	@Column(name = "estado", length = 2)
	public String getEstado() {
		return this.estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	@Column(name = "cidade", length = 45)
	public String getCidade() {
		return this.cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	@Column(name = "cep", length = 10)
	public String getCep() {
		return this.cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "cidadao")
	public Set<Solicitacao> getSolicitacaos() {
		return this.solicitacaos;
	}

	public void setSolicitacaos(Set<Solicitacao> solicitacaos) {
		this.solicitacaos = solicitacaos;
	}

	@Column(name = "bairro", length = 45)
	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	@Column(name = "complemento", length = 200)
	public String getComplemento() {
		return complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	@Column(name = "renda", length = 6)
	public short getRenda() {
		return renda;
	}

	public void setRenda(short renda) {
		this.renda = renda;
	}
	
	@Column(name = "numero", length = 5)
	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}
	
	@Column(name = "telefone", length = 16)
	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}
	
	@Column(name = "celular", length = 16)
	public String getCelular() {
		return celular;
	}

	public void setCelular(String celular) {
		this.celular = celular;
	}
	

}
