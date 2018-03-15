package br.gov.se.lai.Bean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import br.gov.se.lai.DAO.AnexoDAO;
import br.gov.se.lai.entity.Anexo;
import br.gov.se.lai.entity.Mensagem;

@ManagedBean(name = "anexo")
@SessionScoped
public class AnexoBean implements Serializable {

	private static final long serialVersionUID = 106779207846313149L;
	private Anexo anexo;
	private final String descricaoPadrao = "Anexo relacionado ao tema ";
	private UploadedFile file;
	private String descricao;
	private String nomeView;
	private StreamedContent fileDownload;
	private static List<File> filesInFolder;
	private static final String filesPath = "\\resources\\arquivos";

	@PostConstruct
	public void init() {
		anexo = new Anexo();
	}

	public void save(Anexo anexo, Mensagem mensagem, UploadedFile file) throws IOException, NullPointerException{
//		Path folder = Paths.get(filesPath);
		Path folder = Paths.get(FacesContext.getCurrentInstance().getExternalContext().getRealPath("/") + filesPath );   
		try (InputStream input = file.getInputstream()) {
			if (!file.getFileName().equals("")) {
				String[] infoExtensao = file.getContentType().split("/")[1].split("-");
				String extensao = "." + infoExtensao[infoExtensao.length-1];
				String anexoNome = mensagem.getSolicitacao().getIdSolicitacao().toString()+"_"+mensagem.getIdMensagem()+ "_anexo";
				
				Path filePath = Files.createTempFile(folder,anexoNome,extensao);
				Files.copy(input, filePath, StandardCopyOption.REPLACE_EXISTING);
				
				anexo.setNome(anexoNome);
				anexo.setFile(anexoNome+extensao);
				anexo.setMensagem(mensagem);
				if (anexo.getDescricao() == null) {
					anexo.setDescricao("Anexo referente a " +mensagem.getSolicitacao().getTitulo() + "e mensagem do código "
							+ mensagem.getIdMensagem().toString());
				}
			} else {
				this.anexo.setNome("anexopadrao.png");
			}
			AnexoDAO.saveOrUpdate(anexo);
		} 
	}
	
	public String downloadAnexo(Mensagem mensagem) {
//		int msgKey = 0;
//		for (Mensagem msg : mensagem.getSolicitacao().getMensagems()) {
//			if(msg != mensagem) {
//				msgKey++;
//			}else {
//				break;
//			}
//		}
		listarFiles();
		String retorno = null;
//		String msgKeyStr = ""+msgKey;
		for (File file : filesInFolder) {
			String[] anexoNome = file.getName().split("_",4);
			String solicitacaoNum = anexoNome[0];
			String mensagemNum = anexoNome[1];
			if(solicitacaoNum.equals(mensagem.getSolicitacao().getIdSolicitacao().toString()) && mensagemNum.equals(mensagem.getIdMensagem().toString())) {
				retorno = (Paths.get(FacesContext.getCurrentInstance().getExternalContext().getRealPath("/") + filesPath +"//"+file.getName())).toString();
				nomeView = file.getName();
				break;
			}
		}
		
		return retorno;
		
//		InputStream stream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream(retorno);
//        fileDownload = new DefaultStreamedContent(stream, "jpg", "download_esic.jpg");
//		
//		return fileDownload;
	}
	
	public void redirecionar(Mensagem msg){
	      try {
//	          FacesContext.getCurrentInstance().getExternalContext().redirect(downloadAnexo(msg));
	          FacesContext.getCurrentInstance().getExternalContext().redirect("file:///C:/Users/msmachado/Pictures/coentro.png");
//	          FacesContext.getCurrentInstance().getExternalContext().redirect("http://google.com");
	      } catch (IOException ex) {
	    	  ex.printStackTrace();
	      }
	  }

	
	public StreamedContent downloadArquivo(File file) {
        try {
        	File arquivo = new File(file.getPath());
        	fileDownload = (StreamedContent) arquivo;
            InputStream stream;
            stream = new FileInputStream(arquivo);

            StreamedContent file2 = new DefaultStreamedContent(stream, fileDownload.getContentType(), "download_anexo");
            return file2;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

	
	public static void listarFiles() {
		try {
			filesInFolder = (List<File>) Files.walk(Paths.get(FacesContext.getCurrentInstance().getExternalContext().getRealPath("/") + filesPath ))
					.filter(Files::isRegularFile)
					.map(Path::toFile)
					.collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public List<Anexo> listarAnexos(Mensagem mensagem){
		return (List<Anexo>)AnexoDAO.listarAnexoMensagem(mensagem.getIdMensagem());
	}
	
	
	// GETTERS E SETTERS
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	public Anexo getAnexo() {
		return anexo;
	}

	public void setAnexo(Anexo anexo) {
		this.anexo = anexo;
	}

	public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public StreamedContent getFileDownload() {
		return fileDownload;
	}

	public void setFileDownload(StreamedContent fileDownload) {
		this.fileDownload = fileDownload;
	}

	public String getNomeView() {
		return nomeView;
	}

	public void setNomeView(String nomeView) {
		this.nomeView = nomeView;
	}

	
}