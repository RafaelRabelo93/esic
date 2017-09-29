package br.gov.se.lai.entity;
// Generated 18/09/2017 08:12:36 by Hibernate Tools 5.2.5.Final

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
 * Entidades generated by hbm2java
 */
@Entity
@Table(name = "entidades", catalog = "esic")
public class Entidades implements java.io.Serializable {


	private static final long serialVersionUID = 6715782305551647625L;
	private Integer idEntidades;
	private int idOrgaos;
	private String nome;
	private String sigla;
	private boolean orgao;
	private boolean ativa;
	private Set<Responsavel> responsavels = new HashSet<Responsavel>(0);
	private Set<Solicitacao> solicitacaos = new HashSet<Solicitacao>(0);
	private Set<Competencias> competenciases = new HashSet<Competencias>(0);

	public Entidades() {
	}

	public Entidades(int idOrgaos, String nome, boolean orgao, boolean ativa) {
		this.idOrgaos = idOrgaos;
		this.nome = nome;
		this.orgao = orgao;
		this.ativa = ativa;
	}

	public Entidades(int idOrgaos, String nome, boolean orgao, boolean ativa, Set<Responsavel> responsavels,
			Set<Solicitacao> solicitacaos, Set<Competencias> competenciases) {
		this.idOrgaos = idOrgaos;
		this.nome = nome;
		this.orgao = orgao;
		this.ativa = ativa;
		this.responsavels = responsavels;
		this.solicitacaos = solicitacaos;
		this.competenciases = competenciases;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)

	@Column(name = "idEntidades", unique = true, nullable = false)
	public Integer getIdEntidades() {
		return this.idEntidades;
	}

	public void setIdEntidades(Integer idEntidades) {
		this.idEntidades = idEntidades;
	}

	@Column(name = "idOrgaos", nullable = false)
	public int getIdOrgaos() {
		return this.idOrgaos;
	}

	public void setIdOrgaos(int idOrgaos) {
		this.idOrgaos = idOrgaos;
	}

	@Column(name = "nome", nullable = false, length = 45)
	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Column(name = "orgao", nullable = false)
	public boolean isOrgao() {
		return this.orgao;
	}

	public void setOrgao(boolean orgao) {
		this.orgao = orgao;
	}

	@Column(name = "ativa", nullable = false)
	public boolean isAtiva() {
		return this.ativa;
	}

	public void setAtiva(boolean ativa) {
		this.ativa = ativa;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "entidades")
	public Set<Responsavel> getResponsavels() {
		return this.responsavels;
	}

	public void setResponsavels(Set<Responsavel> responsavels) {
		this.responsavels = responsavels;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "entidades")
	public Set<Solicitacao> getSolicitacaos() {
		return this.solicitacaos;
	}

	public void setSolicitacaos(Set<Solicitacao> solicitacaos) {
		this.solicitacaos = solicitacaos;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "entidades")
	public Set<Competencias> getCompetenciases() {
		return this.competenciases;
	}

	public void setCompetenciases(Set<Competencias> competenciases) {
		this.competenciases = competenciases;
	}

	@Column(name = "sigla", nullable = false, length = 45)
	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

}
