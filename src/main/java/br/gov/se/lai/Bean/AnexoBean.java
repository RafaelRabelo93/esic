package br.gov.se.lai.Bean;

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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
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

import br.gov.se.lai.DAO.AnexoDAO;
import br.gov.se.lai.entity.Anexo;
import br.gov.se.lai.entity.Mensagem;
import br.gov.se.lai.entity.Solicitacao;

@ManagedBean(name = "anexo")
@SessionScoped
public class AnexoBean  implements Serializable {

	private static final long serialVersionUID = 106779207846313149L;
	private Anexo anexo;
	private final String descricaoPadrao = "Anexo relacionado ao tema ";
	private UploadedFile file;
	private File arquivoEntrada;
	private String descricao;
	private String nomeView;
	private StreamedContent fileDownload;
	private static List<File> filesInFolder;
	private static final String filesPath = "\\resources\\arquivos";

	@PostConstruct
	public void init() {
		anexo = new Anexo();
	}
	
	/**
	 * Função save()
	 * 
	 * Função para salvar anexos.
	 * Define diretório para armazenamento do arquivo. Recupera a extensão do arquivo, 
	 * define nome e salva.
	 * 
	 * @param anexo - objeto para ser salvo.
	 * @param mensagem - mensagem a qual o anexo será vinculado.
	 * @param file - a mídia do anexo.
	 * @throws IOException - caso dê erro na hora de escrever o arquivo.
	 * @throws NullPointerException -  caso o objeto não tenha sido devidamente inicializado.
	 */

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
	
	/**
	 * Função que recebe uma mensagem e retorna anexos ligados a ela.
	 * Ainda está dando erro
	 */
	
	public File downloadAnexo(Mensagem mensagem) {
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
//				retorno = (Paths.get(FacesContext.getCurrentInstance().getExternalContext().getRealPath("/") + filesPath +"//"+file.getName())).toString();
				arquivoEntrada = file;
				nomeView = file.getName();
				break;
			}
		}
		
		return arquivoEntrada;

	}
	
	public boolean verificaExistencia(Mensagem msg) {
		return !msg.getAnexos().isEmpty();
	}

	
	/**
	 * Função de download de arquivo.
	 * @param msg
	 * @return 
	 */
	
	
	public StreamedContent downloadArquivo(Mensagem msg) {	
		try {
			File file = downloadAnexo(msg);
			if (file != null) {
				InputStream in = new FileInputStream(file);
				String contentType = Files.probeContentType(file.toPath());
				String[] extensao = contentType.split("/")[1].split("-");
				fileDownload = new DefaultStreamedContent(in, contentType, "download." + extensao[extensao.length - 1]);
				return fileDownload;
			}else {
				return null;
			}
		} catch (IOException e) {
			System.out.println("Erro:"+ e.getMessage());
			return null;
		}
    }

	/**
	 * Função listarFiles
	 * Lista todos os arquivos armazenados no diretório pré-definido.
	 */
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
	
	 /**
	  * 
	  * Função listarAnexos
	  * Lista todos os anexos ligados a uma mensagem.
	  *  
	  * @param mensagem
	  * @return
	  */
	public List<Anexo> listarAnexos(Mensagem mensagem){
		return (List<Anexo>)AnexoDAO.listarAnexoMensagem(mensagem.getIdMensagem());
	}
	
	void goGet(HttpServletRequest request, HttpServletResponse response) {
		File arquivo = new File("C:\\Users\\msmachado\\Pictures\\profile_github.png");
		int tamanho = (int) arquivo.length();
		
		HttpServletResponse response1 = (HttpServletResponse)FacesContext.getCurrentInstance().getExternalContext().getResponse();
		
		response1.setContentType("image/png");
		response1.setContentLength(tamanho);
		response1.setHeader("Content-Disposition", "attachment); filename=profile.png");
		
		OutputStream output;
		try {
			output = response1.getOutputStream();
			Files.copy(arquivo.toPath(), output);
		} catch (IOException e) {
			e.printStackTrace();
		}
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