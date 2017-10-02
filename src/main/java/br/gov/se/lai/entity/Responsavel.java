package br.gov.se.lai.entity;
// Generated 18/09/2017 08:12:36 by Hibernate Tools 5.2.5.Final

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Responsavel generated by hbm2java
 */
@Entity
@Table(name = "responsavel", catalog = "esic")
public class Responsavel implements java.io.Serializable {


	private static final long serialVersionUID = 4683753718646281832L;
	private Integer idResponsavel;
	private Entidades entidades;
	private Usuario usuario;
	private Short nivel;
	private String email;

	public Responsavel() {
	}

	public Responsavel(Entidades entidades, Usuario usuario, String email) {
		this.entidades = entidades;
		this.usuario = usuario;
		this.email = email;
	}

	public Responsavel(Entidades entidades, Usuario usuario, Short nivel, String email) {
		this.entidades = entidades;
		this.usuario = usuario;
		this.nivel = nivel;
		this.email = email;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)

	@Column(name = "idResponsavel", unique = true, nullable = false)
	public Integer getIdResponsavel() {
		return this.idResponsavel;
	}

	public void setIdResponsavel(Integer idResponsavel) {
		this.idResponsavel = idResponsavel;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idEntidades", nullable = false)
	public Entidades getEntidades() {
		return this.entidades;
	}

	public void setEntidades(Entidades entidades) {
		this.entidades = entidades;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idUsuario", nullable = false)
	public Usuario getUsuario() {
		return this.usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	@Column(name = "nivel")
	public Short getNivel() {
		return this.nivel;
	}

	public void setNivel(Short nivel) {
		this.nivel = nivel;
	}

	@Column(name = "email", nullable = false, length = 100)
	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}