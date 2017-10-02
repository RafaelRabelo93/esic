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
 * Anexo generated by hbm2java
 */
@Entity
@Table(name = "anexo", catalog = "esic")
public class Anexo implements java.io.Serializable {


	private static final long serialVersionUID = -6317533517362848771L;
	private Integer idAnexo;
	private Mensagem mensagem;
	private String file;
	private String descricao;
	private String nome;

	public Anexo() {
	}

	public Anexo(Mensagem mensagem, String file) {
		this.mensagem = mensagem;
		this.file = file;
	}

	public Anexo(Mensagem mensagem, String file, String descricao, String nome) {
		this.mensagem = mensagem;
		this.file = file;
		this.descricao = descricao;
		this.nome = nome;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)

	@Column(name = "idAnexo", unique = true, nullable = false)
	public Integer getIdAnexo() {
		return this.idAnexo;
	}

	public void setIdAnexo(Integer idAnexo) {
		this.idAnexo = idAnexo;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "Mensagem_idMensagem", nullable = false)
	public Mensagem getMensagem() {
		return this.mensagem;
	}

	public void setMensagem(Mensagem mensagem) {
		this.mensagem = mensagem;
	}

	@Column(name = "file", nullable = false, length = 65535)
	public String getFile() {
		return this.file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	@Column(name = "descricao", length = 200)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "nome", length = 45)
	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

}