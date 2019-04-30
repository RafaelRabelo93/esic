package br.gov.se.lai.Bean;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import com.sun.mail.iap.ByteArray;

import br.gov.se.lai.DAO.AnexoDAO;
import br.gov.se.lai.DAO.CompetenciasDAO;
import br.gov.se.lai.entity.Anexo;
import br.gov.se.lai.entity.Mensagem;
import br.gov.se.lai.entity.Solicitacao;

@ManagedBean(name = "anexo")
@SessionScoped
public class AnexoBean  implements Serializable {

	private static final long serialVersionUID = -60333894898290094L;
	private Anexo anexo;
//	private Mensagem mensagem;
	private String nome;
	private String tipo;
	private Integer tamanho;
	private Blob conteudo;
	
	@PostConstruct
	public void init() {
		this.anexo = new Anexo();
	}
	
	public void save(UploadedFile file, Mensagem mensagem) throws IOException{
		try {
			
			anexo.setNome(file.getFileName());
			anexo.setMensagem(mensagem);
			anexo.setTamanho(file.getSize());
			anexo.setTipo(file.getContentType());
			anexo.setConteudo(file.getContents());
			
			AnexoDAO.saveOrUpdate(anexo);
			
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Anexo não pôde ser salvo.", e.getMessage()));
		}
	}
	
	public boolean temAnexo(int idMensagem) {
		int qtdAnexo = AnexoDAO.temAnexo(idMensagem);
		if (qtdAnexo > 0) return true;
		else return false;
	}
	
	public StreamedContent download(int idMensagem) throws IOException {
		StreamedContent file;
		
		List<Anexo> anexoList = AnexoDAO.list(idMensagem);
		Anexo anx = anexoList.get(0);
		
		byte[] buf = anx.getConteudo();
		
		InputStream inputStream = new ByteArrayInputStream(buf);
        
        file = new DefaultStreamedContent(inputStream, anx.getTipo(), anx.getNome());
        
        inputStream.close();
    
        return file;
	}
	
//GETTERS E SETTERS ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	
	public Anexo getAnexo() {
		return anexo;
	}

	public void setAnexo(Anexo anexo) {
		this.anexo = anexo;
	}

//	public Mensagem getMensagem() {
//		return mensagem;
//	}
//
//	public void setMensagem(Mensagem mensagem) {
//		this.mensagem = mensagem;
//	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public Integer getTamanho() {
		return tamanho;
	}

	public void setTamanho(Integer tamanho) {
		this.tamanho = tamanho;
	}

	public Blob getConteudo() {
		return conteudo;
	}

	public void setConteudo(Blob conteudo) {
		this.conteudo = conteudo;
	}
	
}