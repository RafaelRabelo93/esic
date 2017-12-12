package br.gov.se.lai.Bean;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;

import org.primefaces.model.UploadedFile;

import br.gov.se.lai.DAO.MensagemDAO;
import br.gov.se.lai.DAO.SolicitacaoDAO;
import br.gov.se.lai.DAO.UsuarioDAO;
import br.gov.se.lai.entity.Anexo;
import br.gov.se.lai.entity.Mensagem;
import br.gov.se.lai.entity.Solicitacao;
import br.gov.se.lai.entity.Usuario;
import br.gov.se.lai.utils.HibernateUtil;
import br.gov.se.lai.utils.NotificacaoEmail;
import br.gov.se.lai.utils.PermissaoUsuario;




@ManagedBean(name = "mensagem")
@SessionScoped
public class MensagemBean implements Serializable, PermissaoUsuario{
	

	private static final long serialVersionUID = -353994363743436917L;
	private static Mensagem mensagem;
	private Calendar data;
	private Solicitacao solicitacao;
	private List<Mensagem> mensagensSolicitacao;
	private Anexo anexo;
	private final int constanteTempo = 10;
	private Usuario usuario;
	private UploadedFile file;


	@PostConstruct
	public void init() {
		mensagem = new Mensagem();		
		anexo = new Anexo();
		usuario = ((UsuarioBean) HibernateUtil.RecuperarDaSessao("usuario")).getUsuario();
		AnexoBean.listarFiles();
	}

	public String save() {

		if(verificaPermissao()) {
			
			mensagem.setUsuario(usuario);
			mensagem.setData(new Date(System.currentTimeMillis()));
			mensagem.setSolicitacao(solicitacao);
			verificaMensagem();
			if(MensagemDAO.saveOrUpdate(mensagem)) {
				NotificacaoEmail.enviarEmail(solicitacao, usuario);
				
				if (file.getContents().length != 0) {
					System.out.println(anexo.toString());
					AnexoBean anx = new AnexoBean();
					anx.save(anexo, mensagem, file);
				}
				//mensagensSolicitacao.add(mensagem);
			}
			
		mensagem = new Mensagem();	
		return "/Consulta/consulta";
		}else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Usuário sem permissão..",null));

			return null;
		}
	}
	
	public void verificaMensagem() {
		if(solicitacao.getStatus() != "Respondida") {
			solicitacao.setStatus("Respondida");
			solicitacao.setDataLimite((java.sql.Date.valueOf(LocalDate.now().plusDays(SolicitacaoBean.prazoResposta(solicitacao.getStatus())))));
			SolicitacaoDAO.saveOrUpdate(solicitacao);
		}
		mensagem.setTipo((short)2);
	}
	
	public int tipoMensagem() {
		return 1;
	}
	

	public static void salvarStatus(Solicitacao solicitacao, String status) {
		int tipoAux;
		mensagem = new Mensagem();
		mensagem.setData(new Date(System.currentTimeMillis()));
		mensagem.setSolicitacao(solicitacao);
		mensagem.setTexto("Solicitação "+solicitacao.getProtocolo() +" foi "+status+" no sistema.");
		mensagem.setUsuario(UsuarioDAO.buscarUsuario("Sistema"));
		switch (status) {
		case "Recurso":
			tipoAux = 3;
			break;
			
		case "Prorrogada":
			tipoAux = 4;
			break;

		case "Finalizada":
			tipoAux = 4;
			break;
			
		default:
			tipoAux = 1;
			break;
		}
		mensagem.setTipo((short)tipoAux);
		MensagemDAO.saveOrUpdate(mensagem);
		NotificacaoEmail.enviarEmail(solicitacao,((UsuarioBean) HibernateUtil.RecuperarDaSessao("usuario")).getUsuario());
	}

	
	public void carregaMensagens() {
		if (solicitacao != null) {
			mensagensSolicitacao = MensagemDAO.list(solicitacao.getIdSolicitacao());
		}else {
				mensagensSolicitacao = null;
		}
	}
	
//GETTERS E SETTERS ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	
	

	public Mensagem getMensagem() {
		return mensagem;
	}

	@SuppressWarnings("static-access")
	public void setMensagem(Mensagem mensagem) {
		this.mensagem = mensagem;
	}

	public Calendar getData() {
		return data;
	}

	public void setData(Calendar data) {
		this.data = data;
	}

	public Solicitacao getSolicitacao() {
		return solicitacao;
	}

	public void setSolicitacao(Solicitacao solicitacao) {
		if(this.solicitacao != solicitacao) {
			mensagensSolicitacao = null;
			this.solicitacao = solicitacao;
			carregaMensagens();
		}
	}

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

	@Override
	public boolean verificaPermissao() {
		if(usuario.getPerfil() == 4 || usuario.getPerfil() == 1) {
			return false;
		}else {
			return true;
		}
	}

	public List<Mensagem> getMensagensSolicitacao() {
		return mensagensSolicitacao;
	}

	public void setMensagensSolicitacao(List<Mensagem> mensagensSolicitacao) {
		this.mensagensSolicitacao = mensagensSolicitacao;
	}	
	
	
}