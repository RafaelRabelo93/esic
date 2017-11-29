package br.gov.se.lai.utils;

import java.util.ArrayList;
import java.util.List;
import java.io.File;  

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import br.gov.se.lai.DAO.MensagemDAO;
import br.gov.se.lai.DAO.ResponsavelDAO;
import br.gov.se.lai.entity.Cidadao;
import br.gov.se.lai.entity.Mensagem;
import br.gov.se.lai.entity.Responsavel;
import br.gov.se.lai.entity.Solicitacao;
import br.gov.se.lai.entity.Usuario;

public class NotificacaoEmail implements Job{


	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
	}
	
	public static void enviarEmail(Solicitacao solicitacao, Usuario usuario) {
		
		String[] envio = destinatarioEmail(usuario, solicitacao);
		String remetente = envio[0];
		String destinatario = envio[1];
		
		Email email = new SimpleEmail();
		List<Mensagem> mensagem = new ArrayList<Mensagem>(MensagemDAO.list(solicitacao.getIdSolicitacao()));	
		
		try {  
			System.out.println("Entrou em email");
			System.out.println(remetente + " - "+destinatario);
			
			EmailAttachment attachment = new EmailAttachment();
			
			email.setDebug(true);  
			email.setHostName("smtp.expresso.se.gov.br");  
			email.setAuthentication("mayara.machado","abcd1234");  
			email.addTo("mayara.machado@cge.se.gov.br"); //pode ser qualquer email  
			email.setFrom(remetente); //será passado o email que você fará a autenticação 
			email.setSubject(solicitacao.getTitulo().toString());  
			email.setMsg(mensagem.get(mensagem.size()-1).getTexto());  
			email.send();  
			
			
			
		} catch (EmailException e) {  
			
			System.out.println(e.getMessage());  
			
		}   
		
	}
	
	
	public static String[] destinatarioEmail(Usuario usuario, Solicitacao solicitacao) {
		String[] envio = new String[2];
		if(usuario.getPerfil() == 3) {
			List<Cidadao> listCidadao = new ArrayList<Cidadao>(usuario.getCidadaos());	
			envio[0] = listCidadao.get(0).getEmail().toString();
			if(solicitacao.getInstancia().equals((short)1)) {
					envio[1] = ResponsavelDAO.findResponsavelEntidade(solicitacao.getEntidades().getIdEntidades(), 1).get(0).getEmail().toString();
			}else if (solicitacao.getInstancia().equals((short)2)){
					envio[1] = ResponsavelDAO.findResponsavelEntidade(solicitacao.getEntidades().getIdEntidades(), 2).get(0).getEmail().toString();
			} else if (solicitacao.getInstancia().equals((short)3)) {
					envio[1] = ResponsavelDAO.findResponsavelEntidade(solicitacao.getEntidades().getIdEntidades(), 3).get(0).getEmail().toString();
			}
		}else {
			if(usuario.getPerfil() == 2) {
				@SuppressWarnings("unchecked")
				List<Responsavel> r = new ArrayList<Responsavel>(usuario.getResponsavels());
				envio[0] = r.get(0).getEmail().toString();
				envio[1] = solicitacao.getCidadao().getEmail();
			}
		}
		
		return envio;
	}
	
	public static void enviarEmailAutomatico(Solicitacao solicitacao,String titulo, String mensagem) {
		Email email = new SimpleEmail();
		
		try {  
			System.out.println("Entrou em email");
			email.setDebug(true);  
			email.setHostName("smtp.expresso.se.gov.br");  
			email.setAuthentication("mayara.machado","abcd1234");  
			email.addTo("mayara.machado@cge.se.gov.br"); //pode ser qualquer email  
			email.setFrom("no_reply@cge.se.gov.br"); //será passado o email que você fará a autenticação 
			email.setSubject(titulo);  
			email.setMsg(mensagem);  
			email.send();  
		} catch (EmailException e) {  
			System.out.println(e.getMessage());  
		}
	}
}
