package br.gov.se.lai.entity;
// Generated 05/09/2017 09:17:51 by Hibernate Tools 5.2.5.Final

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Usuario generated by hbm2java
 */
@Entity
@Table(name = "usuario", catalog = "esic")
public class Usuario implements java.io.Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = -2380284948334048659L;
	private Integer idUsuario;
	private String nome;
	private String nick;
	private String senha;
	private short perfil;
	private Set<Mensagem> mensagems = new HashSet<Mensagem>(0);
	private Set<Cidadao> cidadaos = new HashSet<Cidadao>(0);
	private Set<Responsavel> responsavels = new HashSet<Responsavel>(0);

	public Usuario() {
	}

	public Usuario(String nome, String nick, String senha, short perfil) {
		this.nome = nome;
		this.nick = nick;
		this.senha = senha;
		this.perfil = perfil;
	}

	public Usuario(String nome, String nick, String senha, short perfil, Set<Mensagem> mensagems, Set<Cidadao> cidadaos,
			Set<Responsavel> responsavels) {
		this.nome = nome;
		this.nick = nick;
		this.senha = senha;
		this.perfil = perfil;
		this.mensagems = mensagems;
		this.cidadaos = cidadaos;
		this.responsavels = responsavels;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)

	@Column(name = "idUsuario", unique = true, nullable = false)
	public Integer getIdUsuario() {
		return this.idUsuario;
	}

	public void setIdUsuario(Integer idUsuario) {
		this.idUsuario = idUsuario;
	}

	@Column(name = "nome", nullable = false, length = 65)
	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Column(name = "nick", nullable = false, length = 12)
	public String getNick() {
		return this.nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	@Column(name = "senha", nullable = false, length = 64)
	public String getSenha() {
		return this.senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	@Column(name = "perfil", nullable = false)
	public short getPerfil() {
		return this.perfil;
	}

	public void setPerfil(short perfil) {
		this.perfil = perfil;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "usuario")
	public Set<Mensagem> getMensagems() {
		return this.mensagems;
	}

	public void setMensagems(Set<Mensagem> mensagems) {
		this.mensagems = mensagems;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "usuario")
	public Set<Cidadao> getCidadaos() {
		return this.cidadaos;
	}

	public void setCidadaos(Set<Cidadao> cidadaos) {
		this.cidadaos = cidadaos;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "usuario")
	public Set<Responsavel> getResponsavels() {
		return this.responsavels;
	}

	public void setResponsavels(Set<Responsavel> responsavels) {
		this.responsavels = responsavels;
	}

}
