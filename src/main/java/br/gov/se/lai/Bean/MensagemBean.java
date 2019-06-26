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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;

import org.primefaces.model.UploadedFile;

import br.gov.se.lai.DAO.AnexoDAO;
import br.gov.se.lai.DAO.MensagemDAO;
import br.gov.se.lai.DAO.SolicitacaoDAO;
import br.gov.se.lai.DAO.UsuarioDAO;
import br.gov.se.lai.entity.Anexo;
//import br.gov.se.lai.entity.Anexo;
import br.gov.se.lai.entity.Mensagem;
import br.gov.se.lai.entity.Solicitacao;
import br.gov.se.lai.entity.Usuario;
import br.gov.se.lai.utils.Avaliacao;
import br.gov.se.lai.utils.HibernateUtil;
import br.gov.se.lai.utils.NotificacaoEmail;
import br.gov.se.lai.utils.PermissaoUsuario;
import br.gov.se.lai.utils.PrazosSolicitacao;
import javassist.expr.NewArray;




@ManagedBean(name = "mensagem")
@SessionScoped
public class MensagemBean implements Serializable, PermissaoUsuario{
	
	private static final long serialVersionUID = -353994363743436917L;
	private static Mensagem mensagem;
	private String status;
	private Calendar data;
	private int idSolicitacao;
	private Solicitacao solicitacao;
	private static List<Mensagem> mensagensSolicitacao;
	private static List<Mensagem> mensagensHistorico;
	private static List<Mensagem> mensagensTramites;
//	private Anexo anexo;
	private final int constanteTempo = 10;
	private Usuario usuario;
	private UploadedFile file;
	private int nota;
	private List<Anexo> anexoList;

 
	@PostConstruct
	public void init() {
		mensagem = new Mensagem();		
//		anexo = new Anexo();
		usuario = ((UsuarioBean) HibernateUtil.RecuperarDaSessao("usuario")).getUsuario();
//		AnexoBean.listarFiles();
		carregaMensagens();
		this.setAnexoList(new ArrayList<Anexo>());
	}

	public String save() {

		if(verificaPermissao()) {
			
			if(usuario.getIdUsuario() != null) {
				mensagem.setUsuario(usuario);
			}else {
				mensagem.setUsuario(((UsuarioBean) HibernateUtil.RecuperarDaSessao("usuario")).getUsuario());
			}
			mensagem.setData(new Date(System.currentTimeMillis()));
			mensagem.setSolicitacao(solicitacao);
			mensagem.setTipo((short)2);
			mensagem.setNota(0);
			if(MensagemDAO.saveOrUpdate(mensagem)) {
				mensagensSolicitacao.add(mensagem);
				MensagemDAO.saveOrUpdate(mensagem);
				NotificacaoEmail.enviarEmailNotificacaoCidadao(solicitacao, mensagem);
				try {
					if (!this.getAnexoList().isEmpty()) {
						try {
							
							for (Anexo anx : this.getAnexoList()) {
								anx.setMensagem(mensagem);
								AnexoDAO.saveOrUpdate(anx);
							}
							
						} catch (Exception e) {
							FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Anexo não pôde ser salvo.", e.getMessage()));
						}
					}
				} catch (Exception e) {
				FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Anexo não pôde ser salvo.", e.getMessage()));
				}
				
				verificaMensagem();
				
			}
		
		mensagem = new Mensagem();
		return "/Consulta/consulta?faces-redirect=true";
		}else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Usuário sem permissão..",null));

			return null;
		}
	}
	
	/**
	 * Salvar mensagem específica de avaliação
	 */
	public void salvarMensagemAvaliacao() {
		if(usuario.getIdUsuario() != null) {
			mensagem.setUsuario(usuario);
		}else {
			mensagem.setUsuario(((UsuarioBean) HibernateUtil.RecuperarDaSessao("usuario")).getUsuario());
		}
		mensagem.setData(new Date(System.currentTimeMillis()));
		mensagem.setSolicitacao(solicitacao);
		mensagem.setTipo((short)6);
		mensagem.setNota(nota);
		if(MensagemDAO.saveOrUpdate(mensagem)) {
			MensagemBean.salvarStatus(solicitacao, "Avaliada", null, null, nota);
			Avaliacao.avaliarSolicitacao(solicitacao);
		}
	}
	
	/**
	 * Verificar se a mensagem já foi respondida, para então alterar o status e o prazo de resposta
	 */
	public void verificaMensagem() {
		if(solicitacao.getStatus() != "Atendida") {
			solicitacao.setStatus("Atendida");
			// ATIVAR DATA PRAZO
			//solicitacao.setDataLimite(PrazosSolicitacao.gerarPrazoDiaUtilLimite(solicitacao.getDataLimite(), PrazosSolicitacao.prazoResposta(solicitacao.getStatus())));;
			try {
				if(SolicitacaoDAO.saveOrUpdate(solicitacao)) {
					SolicitacaoDAO.saveOrUpdate(solicitacao);
					salvarStatus(solicitacao, solicitacao.getStatus(), null, null, 0);
				}
			} catch (Exception e) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao atualizar status na manifestação", e.getMessage()));
				System.out.println("Erro ao salvar status da resposta:" + e.getMessage());
			}
			//SolicitacaoBean.addQuantidadeSolicitacaoRespondida();
			//SolicitacaoBean.rmvQuantidadeSolicitacaoPendente();
		}
		mensagem.setTipo((short)2);
	}
	
	public int tipoMensagem() {
		return 1;
	}

	/**
	 * Salva mensagem específica de negativa de solicitação
	 * @return
	 */
	public String negar() {
		mensagem.setUsuario(usuario);
		mensagem.setData(new Date(System.currentTimeMillis()));
		mensagem.setSolicitacao(solicitacao);
		mensagem.setTipo((short)7);
		// ATIVAR DATA PRAZO
		//solicitacao.setDataLimite(PrazosSolicitacao.gerarPrazoDiaUtilLimite(new Date(System.currentTimeMillis()), PrazosSolicitacao.prazoResposta("Recurso")));
		
		if(MensagemDAO.saveOrUpdate(mensagem)) {
			mensagensSolicitacao.add(mensagem);
			NotificacaoEmail.enviarEmailNotificacaoCidadao(solicitacao, mensagem);
		}
		
		solicitacao.setStatus("Negada");
		if(SolicitacaoDAO.saveOrUpdate(solicitacao)) {
			salvarStatus(solicitacao, solicitacao.getStatus(), null, null, 0);
		}
		
//		SolicitacaoBean.rmvQuantidadeSolicitacaoPendente();
//		SolicitacaoBean.addQuantidadeSolicitacaoFinalizada();
		
		mensagem = new Mensagem();	
		return "/Consulta/consulta?faces-redirect=true";
	}
	
	public void reformular() {
//		Usuario usuario = ((UsuarioBean) HibernateUtil.RecuperarDaSessao("usuario")).getUsuario();
		
		// Justificativa
		mensagem.setSolicitacao(solicitacao);
		mensagem.setTipo((short) 7);
		mensagem.setUsuario(usuario);
		mensagem.setData(new Date(System.currentTimeMillis()));
		
		if (MensagemDAO.saveOrUpdate(mensagem)) {
			mensagensSolicitacao.add(mensagem);
			MensagemDAO.saveOrUpdate(mensagem);
			NotificacaoEmail.enviarEmailNotificacaoCidadao(solicitacao, mensagem);
		}
		
		solicitacao.setStatus("Reformulação");
		
		try {
			SolicitacaoDAO.saveOrUpdate(solicitacao);
			MensagemBean.salvarStatus(solicitacao, "Reformulação", null, null, nota);
			mensagem = new Mensagem();
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Solicitação não enviada.", e.getMessage()));
		}
		
	}
	
	/**
	 * Cria uma nova mensagem aviso de sistema referente a status da ultima movimentação da solicitação
	 * @param solicitacao
	 * @param status
	 * @param entidadeNova
	 * @param entidadeVelha
	 * @param nota
	 */
	public static void salvarStatus(Solicitacao solicitacao, String status, String entidadeNova, String entidadeVelha, int nota) {
		int tipoAux = 4;
		mensagem = new Mensagem();
		mensagem.setData(new Date(System.currentTimeMillis()));
		mensagem.setSolicitacao(solicitacao);
		mensagem.setUsuario(UsuarioDAO.buscarUsuario("Sistema"));
//		UsuarioBean usuario = ((UsuarioBean) HibernateUtil.RecuperarDaSessao("usuario"));
		
		switch (status) {
		case "Recurso":
			tipoAux = 3;
			mensagem.setTexto("Manifestação "+solicitacao.getProtocolo() +" entrou no "+ solicitacao.getInstancia() +"º "+status.toLowerCase()+" no sistema.");
			System.out.println(mensagem.getTexto());
			break;
			
		case "Prorrogada":
			tipoAux = 4;
			mensagem.setTexto("Manifestação "+solicitacao.getProtocolo() +" foi "+status.toLowerCase()+" no sistema.");
			System.out.println(mensagem.getTexto());
			break;

		case "Negada":
			tipoAux = 4;
			mensagem.setTexto("Manifestação "+solicitacao.getProtocolo() +" foi "+status.toLowerCase()+" no sistema. Entre com recurso para que sua solicitação seja reavaliada.");
			System.out.println(mensagem.getTexto());
			break;

		case "Finalizada":
			tipoAux = 4;
			mensagem.setTexto("Manifestação "+solicitacao.getProtocolo() +" foi "+status.toLowerCase()+" no sistema.");
			System.out.println(mensagem.getTexto());
			break;
			
		case "Encaminhada":
			tipoAux = 4;
			mensagem.setTexto("Manifestação "+solicitacao.getProtocolo() +" foi "+status.toLowerCase()+" no sistema da entidade "+entidadeVelha+" para "+entidadeNova+".");
			System.out.println(mensagem.getTexto());
			break;

		case "Recebida":
			tipoAux = 4;
			mensagem.setTexto("Manifestação "+solicitacao.getProtocolo() +" foi "+status.toLowerCase()+" no sistema da entidade "+solicitacao.getEntidades().getNome()+".");
			System.out.println(mensagem.getTexto());
			break;
			
		case "Status Denuncia 1":
			tipoAux = 0;
			mensagem.setTexto("Manifestação "+solicitacao.getProtocolo() +" alterou o status para "+status.toLowerCase()+" no sistema.");
			System.out.println(mensagem.getTexto());
			break;
			
		case "Status Denuncia 2":
			tipoAux = 0;
			mensagem.setTexto("Manifestação "+solicitacao.getProtocolo() +" alterou o status para "+status.toLowerCase()+" no sistema.");
			System.out.println(mensagem.getTexto());
			break;
			
		case "Status Denuncia 3":
			mensagem.setTexto("Manifestação "+solicitacao.getProtocolo() +" alterou o status para "+status.toLowerCase()+" no sistema.");
			System.out.println(mensagem.getTexto());
			tipoAux = 0;
			break;

		case "Atendida":
			tipoAux = 4;
			mensagem.setTexto("Manifestação "+solicitacao.getProtocolo() +" recebeu resposta no sistema.");
			System.out.println(mensagem.getTexto());
			break;
			
		case "Visualizada":
			tipoAux = 4;
			mensagem.setTexto("Manifestação "+solicitacao.getProtocolo() +" foi visualizada por "+ ((UsuarioBean) HibernateUtil.RecuperarDaSessao("usuario")).getNomeCompleto());
			System.out.println(mensagem.getTexto());
			break;
			
		case "Avaliada":
			tipoAux = 2;
			mensagem.setTexto("Manifestação "+solicitacao.getProtocolo() +" foi avaliada pelo cidadã(o).");
			System.out.println(mensagem.getTexto());
			break;
			
		case "Sem Resposta":
			tipoAux = 4;
			mensagem.setTexto("Manifestação " + solicitacao.getProtocolo() + " não recebeu resposta no sistema.");
			System.out.println(mensagem.getTexto());
			break;
		
		case "Limite Recurso":
			tipoAux = 4;
			mensagem.setTexto("Prazo para realizar recurso na manifestação " + solicitacao.getProtocolo() + " encerrado.");
			System.out.println(mensagem.getTexto());
			break;
			
		case "Reformulação":
			tipoAux = 4;
			mensagem.setTexto("Pedido de reformulação de manifestação realizado por "+ ((UsuarioBean) HibernateUtil.RecuperarDaSessao("usuario")).getNomeCompleto());
			System.out.println(mensagem.getTexto());
			break;
			
		default:
			mensagem.setTexto("Nova solicitacao no sistema.");
			break;
		}
		mensagem.setNota(0);
		mensagem.setTipo((short)tipoAux);
		try {
			MensagemDAO.saveOrUpdate(mensagem);
//			NotificacaoEmail.enviarNotificacao(solicitacao,((UsuarioBean) HibernateUtil.RecuperarDaSessao("usuario")).getUsuario());
			MensagemBean.attMensagemHistorico(mensagem);
			mensagem = new Mensagem();
		}catch (NullPointerException e) {
		}finally {
			mensagem = new Mensagem();
			
		}
	}

	/**
	 * Carrega as mensagens vinculadas a solicitação
	 */
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
	
	/**
	 * Retorna todas as mensagens de cunho avaliativo ligados a Entidade passada como parâmetro
	 * @param idSolicitacao
	 * @return
	 */
	public static String mensagemAvaliacao (int idSolicitacao) {
		String texto;
		List<Mensagem> mensagem = MensagemDAO.listMensagensTipo(idSolicitacao, (short)6);
		
		if(mensagem.size() > 0) {
			texto = mensagem.get(0).getTexto();
			return texto;
		}
		else return texto = "Mensagem não encontrada";
		
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

	public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}

	@Override
	public boolean verificaPermissao() {
		if(usuario.getPerfil() == 1) {
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

	public int getNota() {
		return nota;
	}

	public void setNota(int nota) {
		this.nota = nota;
	}

	public int getIdSolicitacao() {
		return idSolicitacao;
	}

	public void setIdSolicitacao(int idSolicitacao) {
		this.idSolicitacao = idSolicitacao;
	}

	public List<Anexo> getAnexoList() {
		return anexoList;
	}

	public void setAnexoList(List<Anexo> anexoList) {
		this.anexoList = anexoList;
	}

	
}