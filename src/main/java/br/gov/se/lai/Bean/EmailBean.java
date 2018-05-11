package br.gov.se.lai.Bean;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

import br.gov.se.lai.entity.Mensagem;
import br.gov.se.lai.utils.NotificacaoEmail;


@ManagedBean(name = "email")
@SessionScoped
public class EmailBean {
	
	private static String nome;
	private static String email;
	private static String assunto;
	private static String mensagem;
	private final static String destinatario = "michael.mendonca@cge.se.gov.br";
	
	public static void enviarEmail() {
		String mensagemFinal = "Mensagem de: " +nome+ "\n E-mail:" +email+ "\n\n" +mensagem;
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "E-mail enviado com sucesso. ","Seu e-mail foi encaminhado a Controladoria-Geral do Estado, em breve entraremos em contato."));
		
		try {
			NotificacaoEmail.enviarEmail(email, destinatario, assunto, mensagemFinal);
		} catch (EmailException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getAssunto() {
		return assunto;
	}
	public void setAssunto(String assunto) {
		this.assunto = assunto;
	}
	public String getMensagem() {
		return mensagem;
	}
	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}
	
	

}