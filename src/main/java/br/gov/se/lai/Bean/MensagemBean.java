package br.gov.se.lai.Bean;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import br.gov.se.lai.DAO.AnexoDAO;
import br.gov.se.lai.DAO.MensagemDAO;
import br.gov.se.lai.DAO.SolicitacaoDAO;
import br.gov.se.lai.DAO.UsuarioDAO;
import br.gov.se.lai.entity.Anexo;
import br.gov.se.lai.entity.Mensagem;
import br.gov.se.lai.entity.Responsavel;
import br.gov.se.lai.entity.Solicitacao;
import br.gov.se.lai.entity.Usuario;
import br.gov.se.lai.utils.HibernateUtil;
import br.gov.se.lai.utils.NotificacaoEmail;




@ManagedBean(name = "mensagem")
@SessionScoped
public class MensagemBean implements Serializable{
	

	private static final long serialVersionUID = -353994363743436917L;
	private static Mensagem mensagem;
	private Calendar data;
	private Solicitacao solicitacao;
	private Anexo anexo;
	private final int constanteTempo = 10;
	private Usuario usuario;


	@PostConstruct
	public void init() {
		this.mensagem = new Mensagem();		
		this.anexo = new Anexo();
		usuario = ((UsuarioBean) HibernateUtil.RecuperarDaSessao("usuario")).getUsuario();
		
	}

	@SuppressWarnings("static-access")
	public String save() {
		
		this.mensagem.setUsuario(usuario); 

		mensagem.setData(new Date(System.currentTimeMillis()));	
		mensagem.setSolicitacao(solicitacao);
		verificaMensagem();
		MensagemDAO.saveOrUpdate(mensagem);
		NotificacaoEmail.enviarEmail(solicitacao, usuario);
		if(anexo != null) {
			anexo.setFile("anexo.txt");
			anexo.setNome("anexo");
			AnexoDAO.saveOrUpdate(anexo);
			this.anexo = null;
		};
		this.mensagem = new Mensagem();	
		return "consulta";
	}
	
	public void verificaMensagem() {
		if(solicitacao.getStatus() != "Respondida") {
			solicitacao.setStatus("Respondida");
			solicitacao.setDataLimite((java.sql.Date.valueOf(LocalDate.now().plusDays(SolicitacaoBean.prazoResposta(solicitacao.getStatus())))));
			SolicitacaoDAO.saveOrUpdate(solicitacao);
			mensagem.setTipo((short)2);
		}
	}
	
	public int tipoMensagem() {
		return 1;
	}
	
	public static void salvarStatus(Solicitacao solicitacao, String status) {
		int tipoAux;
		mensagem = new Mensagem();
		mensagem.setData(new Date(System.currentTimeMillis()));
		mensagem.setSolicitacao(solicitacao);
		mensagem.setTexto("Solicitação foi "+status+" no sistema.");
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

//GETTERS E SETTERS ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	
	

	public Mensagem getMensagem() {
		return mensagem;
	}

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
		this.solicitacao = solicitacao;
	}

	public Anexo getAnexo() {
		return anexo;
	}

	public void setAnexo(Anexo anexo) {
		this.anexo = anexo;
	}	
	
	
}