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
import br.gov.se.lai.utils.PrazosSolicitacao;




@ManagedBean(name = "mensagem")
@SessionScoped
public class MensagemBean implements Serializable, PermissaoUsuario{
	

	private static final long serialVersionUID = -353994363743436917L;
	private static Mensagem mensagem;
	private String status;
	private Calendar data;
	private Solicitacao solicitacao;
	private static List<Mensagem> mensagensSolicitacao;
	private static List<Mensagem> mensagensHistorico;
	private static List<Mensagem> mensagensTramites;
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
		carregaMensagens();
	}

	public String save() {

		if(verificaPermissao()) {
			
			mensagem.setUsuario(usuario);
			mensagem.setData(new Date(System.currentTimeMillis()));
			mensagem.setSolicitacao(solicitacao);
			mensagem.setTipo((short)2);
			if(MensagemDAO.saveOrUpdate(mensagem)) {
				mensagensSolicitacao.add(mensagem);
				verificaMensagem();
				try {
					if ((file.getContents().length != 0 && !file.equals(null))) {
						System.out.println(anexo.toString());
						AnexoBean anx = new AnexoBean();
						anx.save(anexo, mensagem, file);
					}
				}catch (Exception e) {
					e.getMessage();
				}
				
			}
			
		mensagem = new Mensagem();	
		return "/Consulta/consulta?faces-redirect=true";
		}else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Usuário sem permissão..",null));

			return null;
		}
	}
	
	public void verificaMensagem() {
		if(solicitacao.getStatus() != "Respondida") {
			solicitacao.setStatus("Respondida");
			solicitacao.setDataLimite((java.sql.Date.valueOf(LocalDate.now().plusDays(PrazosSolicitacao.prazoResposta(solicitacao.getStatus())))));
			if(SolicitacaoDAO.saveOrUpdate(solicitacao)) {
				salvarStatus(solicitacao, solicitacao.getStatus(), null, null);
			}
		}
		mensagem.setTipo((short)2);
	}
	
	public int tipoMensagem() {
		return 1;
	}
	

	public static void salvarStatus(Solicitacao solicitacao, String status, String entidadeNova, String entidadeVelha) {
		int tipoAux = 4;
		mensagem = new Mensagem();
		mensagem.setData(new Date(System.currentTimeMillis()));
		mensagem.setSolicitacao(solicitacao);
		mensagem.setUsuario(UsuarioDAO.buscarUsuario("Sistema"));
		switch (status) {
		case "Recurso":
			tipoAux = 3;
			mensagem.setTexto("Solicitação "+solicitacao.getProtocolo() +" entrou no "+solicitacao.getInstancia() +"º"+status.toLowerCase()+" no sistema.");
			break;
			
		case "Prorrogada":
			mensagem.setTexto("Solicitação "+solicitacao.getProtocolo() +" foi"+status.toLowerCase()+" no sistema.");
			break;

		case "Negada":
			mensagem.setTexto("Solicitação "+solicitacao.getProtocolo() +" foi"+status.toLowerCase()+" no sistema. Entre com recurso para que sua solicitação seja reavaliada.");
			break;

		case "Finalizada":
			mensagem.setTexto("Solicitação "+solicitacao.getProtocolo() +" foi"+status.toLowerCase()+" no sistema.");
			break;
			
		case "Encaminhada":
			mensagem.setTexto("Solicitação "+solicitacao.getProtocolo() +" foi "+status.toLowerCase()+" no sistema da entidade "+entidadeVelha+" para "+entidadeNova+".");
			break;

		case "Recebida":
			mensagem.setTexto("Solicitação "+solicitacao.getProtocolo() +" foi "+status.toLowerCase()+" no sistema da entidade "+solicitacao.getEntidades().getNome()+".");
			break;
			
		case "Status Denuncia 1":
			tipoAux = 0;
			mensagem.setTexto("Solicitação "+solicitacao.getProtocolo() +" alterou o status para"+status.toLowerCase()+" no sistema.");
			break;
			
		case "Status Denuncia 2":
			tipoAux = 0;
			mensagem.setTexto("Solicitação "+solicitacao.getProtocolo() +" alterou o status para"+status.toLowerCase()+" no sistema.");
			break;
			
		case "Status Denuncia 3":
			mensagem.setTexto("Solicitação "+solicitacao.getProtocolo() +" alterou o status para"+status.toLowerCase()+" no sistema.");
			tipoAux = 0;
			break;

		case "Respondida":
			mensagem.setTexto("Solicitação "+solicitacao.getProtocolo() +" recebeu resposta no sistema.");
			break;
			
		default:
			mensagem.setTexto("Nova solicitacao no sistema.");
			break;
		}
		mensagem.setTipo((short)tipoAux);
		MensagemDAO.saveOrUpdate(mensagem);
		NotificacaoEmail.enviarNotificacao(solicitacao,((UsuarioBean) HibernateUtil.RecuperarDaSessao("usuario")).getUsuario());
		try {
			MensagemBean.attMensagemHistorico(mensagem);
		}catch (NullPointerException e) {
			mensagem = new Mensagem();
		}
	}

	
	public void carregaMensagens() {
		if (solicitacao != null) {
			int idSolicitacao = solicitacao.getIdSolicitacao();
			mensagensSolicitacao = MensagemDAO.listMensagensSolicitacao(idSolicitacao);
			mensagensHistorico = MensagemDAO.listMensagensHistorico(idSolicitacao);
			mensagensTramites = MensagemDAO.listMensagensTramiteInterno(idSolicitacao);
		}else {
				mensagensSolicitacao = null;
				mensagensHistorico = null;
				mensagensTramites = null;
		}
	}
	
	public static void attMensagemSolicitacao(Mensagem mensagem) {
		mensagensSolicitacao.add(mensagem);
	}

	public static void attMensagemHistorico(Mensagem mensagem) {
		mensagensHistorico.add(mensagem);
	}

	public static void attMensagemTramites(Mensagem mensagem) {
		mensagensTramites.add(mensagem);
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
		if(usuario.getPerfil() == 5 || usuario.getPerfil() == 1) {
			return false;
		}else {
			return true;
		}
	}

	public List<Mensagem> getMensagensSolicitacao() {
		return mensagensSolicitacao;
	}

	public void setMensagensSolicitacao(List<Mensagem> mensagensSolicitacao) {
		MensagemBean.mensagensSolicitacao = mensagensSolicitacao;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<Mensagem> getMensagensHistorico() {
		return mensagensHistorico;
	}

	public void setMensagensHistorico(List<Mensagem> mensagensHistorico) {
		MensagemBean.mensagensHistorico = mensagensHistorico;
	}

	public List<Mensagem> getMensagensTramites() {
		return mensagensTramites;
	}

	public void setMensagensTramites(List<Mensagem> mensagensTramites) {
		MensagemBean.mensagensTramites = mensagensTramites;
	}	
	
	
}