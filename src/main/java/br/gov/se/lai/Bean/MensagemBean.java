package br.gov.se.lai.Bean;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import br.gov.se.lai.DAO.MensagemDAO;
import br.gov.se.lai.entity.Mensagem;
import br.gov.se.lai.utils.HibernateUtil;




@ManagedBean(name = "mensagem")
@SessionScoped
public class MensagemBean implements Serializable{
	

	private static final long serialVersionUID = -353994363743436917L;
	private Mensagem mensagem;
	private Calendar data;


	@PostConstruct
	public void init() {
		this.mensagem = new Mensagem();		
	}

	public String save() {
		UsuarioBean usuarioBean = (UsuarioBean) HibernateUtil.RecuperarDaSessao("usuario");
		this.mensagem.setUsuario(usuarioBean.getUsuario()); 
		mensagem.setData(new Date(System.currentTimeMillis()));	
		MensagemDAO.saveOrUpdate(mensagem);
		return "teste_redirecionamento";
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


	
}