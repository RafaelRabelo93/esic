package br.gov.se.lai.entity;
// Generated 18/09/2017 08:12:36 by Hibernate Tools 5.2.5.Final

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.primefaces.model.UploadedFile;

/**
 * Anexo generated by hbm2java
 */
@Entity
@Table(name = "anexo", catalog = "esic")
public class Anexo implements java.io.Serializable {

	private static final long serialVersionUID = 359937344229720347L;
	private Integer idAnexo;
	private Mensagem mensagem;
	private String nome;
	private String tipo;
	private Long tamanho;
	private byte[] conteudo;

	public Anexo() {
	}

	public Anexo(Mensagem mensagem) {
		this.mensagem = mensagem;
	}

	public Anexo(Mensagem mensagem, String nome, String tipo, Long tamanho, byte[] conteudo) {
		this.mensagem = mensagem;
		this.nome = nome;
		this.setTipo(tipo);
		this.setTamanho(tamanho);
		this.setConteudo(conteudo);
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

	@Column(name = "nome", length = 100)
	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
	
	@Column(name = "tipo", length = 800)
	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	
	@Column(name = "tamanho")
	public Long getTamanho() {
		return tamanho;
	}

	public void setTamanho(Long tamanho) {
		this.tamanho = tamanho;
	}
	
	@Column(name = "conteudo")
	//@Lob
	public byte[] getConteudo() {
		return conteudo;
	}

	public void setConteudo(byte[] input) {
		this.conteudo = input;
	}

}
