package br.gov.se.lai.Bean;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ManagedBean;
import java.io.Serializable;

import br.gov.se.lai.anexos.DownloadFile;
import org.primefaces.model.StreamedContent;

@ManagedBean(name = "attachment")
@SessionScoped
public class AttachmentBean implements Serializable {
	
	private static final long serialVersionUID = 4968675036058228925L;
//	private static Attachment anexo;
//	private String nome;
//	private Integer Mensagem_idMensagem;
//	private BlobType conteudo;
//	private double tamanho;
//	private String tipo;
	
	private StreamedContent file;
	DownloadFile download = new DownloadFile();
	
	@PostConstruct
	public void init() {
//		anexo = new Attachment();
//		nome = new String();		
//		conteudo = new BlobType();
	}
	
	
	public StreamedContent download (Integer idMensagem) {
		try {
			file = download.download(idMensagem);
//			return file;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return file;
	}
	
	public StreamedContent getFile() {
        return download.getFile();
    }
	
	public boolean temAnexo(int idMensagem) {
		return download.temAnexo(idMensagem);
	}
	
	public AttachmentBean() {
		// TODO Auto-generated constructor stub
	}

}
