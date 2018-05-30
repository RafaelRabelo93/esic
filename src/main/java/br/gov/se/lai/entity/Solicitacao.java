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

/**
 * Solicitacao generated by hbm2java
 */
@Entity
@Table(name = "solicitacao", catalog = "esic")
public class Solicitacao implements java.io.Serializable {


	private static final long serialVersionUID = -2604883216685714784L;
	private Integer idSolicitacao;
	private short instancia;
	private Cidadao cidadao;
	private Entidades entidades;
	private Competencias competencias;
	private Date dataIni;
	private String status;
	private Date datafim;
	private Date dataLimite;
	private String titulo;
	private String protocolo;
	private String tipo;
	private Set<Mensagem> mensagems = new HashSet<Mensagem>(0);
	private boolean encaminhada;
	private boolean visualizada;
	private boolean sigilo;
	private Integer formaRecebimento;
	private Integer avaliacao;

	public Solicitacao() {
	}

	public Solicitacao(Cidadao cidadao, Entidades entidades) {
		this.cidadao = cidadao;
		this.entidades = entidades;
	}

	public Solicitacao(Cidadao cidadao, Entidades entidades,Acoes acoes, Date dataIni, Date dataLimite, String status, Date datafim, String titulo,
			String protocolo,short instancia, short prazoTipo, String tipo, Set<Mensagem> mensagems, Integer formaRecebimento, boolean visualizada) {
		this.cidadao = cidadao;
		this.entidades = entidades;
		this.competencias = competencias;
		this.dataIni = dataIni;
		this.dataIni = dataLimite;
		this.status = status;
		this.datafim = datafim;
		this.titulo = titulo;
		this.protocolo = protocolo;
		this.instancia = instancia;
		this.tipo = tipo;
		this.mensagems = mensagems;
		this.formaRecebimento = formaRecebimento;
		this.visualizada = visualizada;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)

	@Column(name = "idSolicitacao", unique = true, nullable = false)
	public Integer getIdSolicitacao() {
		return this.idSolicitacao;
	}

	public void setIdSolicitacao(Integer idSolicitacao) {
		this.idSolicitacao = idSolicitacao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idCidadao", nullable = false)
	public Cidadao getCidadao() {
		return this.cidadao;
	}

	public void setCidadao(Cidadao cidadao) {
		this.cidadao = cidadao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idEntidades", nullable = false)
	public Entidades getEntidades() {
		return this.entidades;
	}

	public void setEntidades(Entidades entidades) {
		this.entidades = entidades;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "dataIni", length = 19)
	public Date getDataIni() {
		return this.dataIni;
	}

	public void setDataIni(Date dataIni) {
		this.dataIni = dataIni;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "dataLimite", length = 19)
	public Date getDataLimite() {
		return this.dataLimite;
	}
	
	public void setDataLimite(Date dataLimite) {
		this.dataLimite = dataLimite;
	}

	@Column(name = "status", length = 10)
	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "datafim", length = 10)
	public Date getDatafim() {
		return this.datafim;
	}

	public void setDatafim(Date datafim) {
		this.datafim = datafim;
	}

	@Column(name = "titulo", length = 45)
	public String getTitulo() {
		return this.titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	@Column(name = "protocolo", length = 46)
	public String getProtocolo() {
		return this.protocolo;
	}

	public void setProtocolo(String protocolo) {
		this.protocolo = protocolo;
	}

	@Column(name = "tipo", length = 30)
	public String getTipo() {
		return this.tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "solicitacao")
	public Set<Mensagem> getMensagems() {
		return this.mensagems;
	}

	public void setMensagems(Set<Mensagem> mensagems) {
		this.mensagems = mensagems;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idCompetencias", nullable = false)
	public Competencias getCompetencias() {
		return competencias;
	}

	public void setCompetencias(Competencias competencias) {
		this.competencias = competencias;
	}

	@Column(name = "instancia", length = 1)
	public Short getInstancia() {
		return instancia;
	}

	public void setInstancia(short instancia) {
		this.instancia = instancia;
	}


	@Column(name = "encaminhada", length = 1)
	public boolean isEncaminhada() {
		return encaminhada;
	}

	public void setEncaminhada(boolean encaminhada) {
		this.encaminhada = encaminhada;
	}

	@Column(name = "formaRecebimento")
	public Integer getFormaRecebimento() {
		return formaRecebimento;
	}

	public void setFormaRecebimento(Integer formaRecebimento) {
		this.formaRecebimento = formaRecebimento;
	}

	@Column(name = "visualizada", length = 1)
	public boolean isVisualizada() {
		return visualizada;
	}

	public void setVisualizada(boolean visualizada) {
		this.visualizada = visualizada;
	}

	public Integer getAvaliacao() {
		return avaliacao;
	}

	public void setAvaliacao(Integer avaliacao) {
		this.avaliacao = avaliacao;
	}

	@Column(name = "sigilo", length = 1)
	public boolean isSigilo() {
		return sigilo;
	}

	public void setSigilo(boolean sigilo) {
		this.sigilo = sigilo;
	}
	
	

}
