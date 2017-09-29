package br.gov.se.lai.Bean;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import br.gov.se.lai.DAO.AnexoDAO;
import br.gov.se.lai.DAO.MensagemDAO;
import br.gov.se.lai.entity.Anexo;
import br.gov.se.lai.entity.Mensagem;
import br.gov.se.lai.entity.Solicitacao;
import br.gov.se.lai.utils.HibernateUtil;




@ManagedBean(name = "mensagem")
@SessionScoped
public class MensagemBean implements Serializable{
	

	private static final long serialVersionUID = -353994363743436917L;
	private Mensagem mensagem;
	private Calendar data;
	private Solicitacao solicitacao;
	private Anexo anexo;


	@PostConstruct
	public void init() {
		this.mensagem = new Mensagem();		
		this.anexo = new Anexo();
	}

	public String save() {
		UsuarioBean usuarioBean = (UsuarioBean) HibernateUtil.RecuperarDaSessao("usuario");
		this.mensagem.setUsuario(usuarioBean.getUsuario()); 
		mensagem.setData(new Date(System.currentTimeMillis()));	
		mensagem.setSolicitacao(solicitacao);
		MensagemDAO.saveOrUpdate(mensagem);
		if(anexo != null) {
			anexo.setFile("anexo.txt");
			anexo.setNome("anexo");
			AnexoDAO.saveOrUpdate(anexo);
			this.anexo = null;
		};
		this.mensagem = new Mensagem();	
		return "consulta";
	}
	
	public String delete() {

		return "usuario";
	}
	
	public String edit() {

		return "calma";
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