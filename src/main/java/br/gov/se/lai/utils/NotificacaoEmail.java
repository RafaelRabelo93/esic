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

import br.gov.se.lai.Bean.ResponsavelBean;
import br.gov.se.lai.DAO.EntidadesDAO;
import br.gov.se.lai.DAO.MensagemDAO;
import br.gov.se.lai.DAO.ResponsavelDAO;
import br.gov.se.lai.entity.Cidadao;
import br.gov.se.lai.entity.Entidades;
import br.gov.se.lai.entity.Mensagem;
import br.gov.se.lai.entity.Responsavel;
import br.gov.se.lai.entity.Solicitacao;
import br.gov.se.lai.entity.Usuario;

public class NotificacaoEmail implements Job{


	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
	}
	
	public static void enviarNotificacao(Solicitacao solicitacao, Usuario usuario) {
		
		String[] envio = destinatarioEmail(usuario, solicitacao);
		String remetente = envio[0];
		String destinatario = envio[1];
		String titulo = "Solicita��o "+solicitacao.getProtocolo() + " - " + solicitacao.getTitulo().toString();

//		List<Mensagem> mensagens = new ArrayList<Mensagem>(MensagemDAO.list(solicitacao.getIdSolicitacao()));	
//		String mensagem = mensagens.get(mensagens.size()-1).getTexto();
		String mensagem = "H� uma nova mensagem de "+remetente + " para "+solicitacao.getEntidades().getSigla()+" na solicita��o de protocolo "+ 
						solicitacao.getProtocolo() + " acesse o portal do esic para visualizar. \n Acesse: http://localhost:8080/esic" + "\n"+envio[2]; 

		
		try {  
			enviarEmail(destinatario, titulo, mensagem);
			
		} catch (EmailException e) {  
			
			
		}   
		
	}
	
	
	public static String[] destinatarioEmail(Usuario usuario, Solicitacao solicitacao) {
		String[] envio = new String[3];
		if(usuario.getPerfil() == 3) {
			List<Cidadao> listCidadao = new ArrayList<Cidadao>(usuario.getCidadaos());	
			envio[0] = listCidadao.get(0).getUsuario().getNome();
			int respId = ResponsavelBean.responsavelDisponivel(solicitacao.getInstancia(), solicitacao.getEntidades().getIdEntidades());
			if(respId != -1) {
				envio[1] = ResponsavelDAO.findResponsavel(respId).getEmail();
				envio[2] = "";
			}else {
				Entidades orgaoEntidade = EntidadesDAO.find(solicitacao.getEntidades().getIdOrgaos());
				respId = ResponsavelBean.responsavelDisponivel(solicitacao.getInstancia(), orgaoEntidade.getIdEntidades());
				if(respId != -1) {
					envio[1] = ResponsavelDAO.findResponsavel(respId).getEmail();
					envio[2] = "Mensagem autom�tica: N�o h� respons�veis cadastrados e/ou ativos no sistema para a entidade "+solicitacao.getEntidades().getNome()+
							   " ("+ solicitacao.getEntidades().getSigla()+") para a inst�ncia "+solicitacao.getInstancia() +".\n A solicita��o "+solicitacao.getProtocolo()+
							   " efetuada pela(o) cidad�(o)"+ solicitacao.getCidadao().getUsuario().getNome()+"("+solicitacao.getCidadao().getEmail()+
							   ") direcionada para esta entidade n�o pode ser notificada a nenhum respons�vel ligado a/ao " + solicitacao.getEntidades().getSigla()+
							   ".\nNotificamos o �rg�o respons�vel.";

				}else {
					respId = ResponsavelBean.responsavelDisponivel(1, 1);
					if(respId != -1) {
						envio[1] = ResponsavelDAO.findResponsavel(respId).getEmail();
						envio[2] = "Mensagem autom�tica: N�o h� respons�veis cadastrados e/ou ativos no sistema para a entidade "+solicitacao.getEntidades().getNome()+
								" ("+ solicitacao.getEntidades().getSigla()+") para a inst�ncia "+solicitacao.getInstancia() +".\n A solicita��o "+solicitacao.getProtocolo()+" efetuada pela(o) cidad�(o)"+
								solicitacao.getCidadao().getUsuario().getNome()+"("+solicitacao.getCidadao().getEmail()+") direcionada para esta entidade n�o "+ 
								"pode ser notificada a nenhum respons�vel ligado a/ao " + solicitacao.getEntidades().getSigla()+".\nNotificamos a CGE.";
					}else {
						envio[1] = "";
						envio[2] = "";
								
					}
					
				}
			}
				
		}else {
			if(usuario.getPerfil() == 2) {
				@SuppressWarnings("unchecked")
				List<Responsavel> r = new ArrayList<Responsavel>(usuario.getResponsavels());
				for (Responsavel resp : r) {
					if(resp.isAtivo()) {
						envio[0] = resp.getUsuario().getNome();
						break;
					}
				}
				envio[1] = solicitacao.getCidadao().getEmail();
			}
		}
		
		return envio;
	}


	public static void enviarEmailAutomatico(Solicitacao solicitacao,String titulo, String mensagem) {
		Email email = new SimpleEmail();
		
		try {  
			Responsavel resp = ResponsavelDAO.findResponsavel(ResponsavelBean.responsavelDisponivel(1, solicitacao.getEntidades().getIdEntidades()));
//			System.out.println("Entrou em email");
			email.setDebug(true);  
			email.setHostName("smtp.expresso.se.gov.br");  
			email.setAuthentication("mayara.machado","efgh1234");  
//			email.addTo(resp.getEmail().toString()); //pode ser qualquer email  
			email.addTo("mayara.machado@cge.se.gov.br"); //pode ser qualquer email  
			email.setFrom("no_reply@cge.se.gov.br"); //ser� passado o email que voc� far� a autentica��o 
			email.setSubject(titulo);  
			email.setMsg(mensagem);  
			email.send();  
		} catch (EmailException e) {  
//			System.out.println(e.getMessage());  
		}catch (NullPointerException e) {
//			System.out.println(e.getMessage());  
		}catch (ArrayIndexOutOfBoundsException e) {
//			System.out.println(e.getMessage());  
		}
	}

	public static void enviarEmailTramites(Solicitacao solicitacao, String mensagem, Responsavel respRemetente, Responsavel respDestinatario ) {
		Email email = new SimpleEmail();
		List<Responsavel> resp = ResponsavelDAO.findResponsavelEntidade(solicitacao.getEntidades().getIdEntidades(), 1);
		
		try {  
			email.setDebug(true);  
			email.setHostName("smtp.expresso.se.gov.br");  
			email.setAuthentication("mayara.machado","efgh1234");  
//			email.addTo(respDestinatario); //pode ser qualquer email  
			email.addTo("mayara.machado@cge.se.gov.br");  
			email.setFrom(respRemetente.getEmail()); 
			email.setSubject("Tr�mite Interno");  
			email.setMsg(mensagem+" >"+respDestinatario.getEmail());  
			email.send();  
		} catch (EmailException e) {  
			e.getMessage();
		}
	}

	public static void enviarEmailAutoridades(Solicitacao solicitacao, String titulo, String mensagem) {
		Email email = new SimpleEmail();
		List<Responsavel> respSec = ResponsavelDAO.findResponsavelEntidade(solicitacao.getEntidades().getIdEntidades(), 3);
		List<Responsavel> respDir = ResponsavelDAO.findResponsavelEntidade(solicitacao.getEntidades().getIdEntidades(), 2);
		
		try {  
			email.setDebug(true);  
			email.setHostName("smtp.expresso.se.gov.br");  
			email.setAuthentication("mayara.machado","efgh1234");  
			email.addTo("mayara.machado@cge.se.gov.br"); //pode ser qualquer email  
//			email.addTo(respSec.get(0).getEmail().toString()); //pode ser qualquer email  
//			email.addCc(solicitacao.getCidadao().getEmail()); //pode ser qualquer email  
//			if(respSec.size() > 1) {
//				email.addCc(respSec.get(1).toString());
//			}
//			email.addCc(respDir.get(0).toString());
			email.setFrom("no_reply@cge.se.gov.br"); //ser� passado o email que voc� far� a autentica��o 
			email.setSubject(titulo);  
			email.setMsg(mensagem);  
			email.send();  
		} catch (EmailException e) {   
//			System.out.println(e.getMessage());  
		}
	}
	

	
	public static void enviarEmailRedefinicaoSenha(String hashcodeUser, String email) {
		String mensagem = "Clique no link para redefinir sua senha:  http://localhost:8080/esic/Alterar/redefinir_senha.xhtml?access_key="+hashcodeUser;
		try {
			enviarEmail(email, "Redefini��o de senha", mensagem);

		} catch (EmailException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	//+++++++++++++++++++ Email simples
	public static void enviarEmail(String destinatario, String titulo, String mensagem) throws EmailException {
		
		Email email = new SimpleEmail();
		EmailAttachment attachment = new EmailAttachment();
		
		email.setDebug(true);  
		email.setHostName("smtp.expresso.se.gov.br");  
		email.setAuthentication("mayara.machado","efgh1234");  
		email.addTo("mayara.machado@cge.se.gov.br"); 
//		email.addCc("michael.mendonca@cge.se.gov.br");
//		email.addTo(destinatario);  
		email.setFrom("no_reply@cge.se.gov.br"); //ser� passado o email que voc� far� a autentica��o 
		email.setSubject(titulo);  
		email.setMsg(mensagem+"\n\n\n\n\n\n destinatario: "+destinatario);  // s� para teste  
		email.send();  
		
	}
	

//	public static void enviarEmail(String destinatario, String remetente, String titulo, String mensagem) throws EmailException {
//		
//		//System.out.println("Entrou em email");
//		//System.out.println(remetente + " - "+destinatario);
//		
//		
//		Email email = new SimpleEmail();
//		EmailAttachment attachment = new EmailAttachment();
//		
//		email.setDebug(true);  
//		email.setHostName("smtp.expresso.se.gov.br");  
//		email.setAuthentication("michael.mendonca","potter0159");  
//		email.addTo("michael.mendonca@cge.se.gov.br"); //pode ser qualquer email  
//		email.setFrom(remetente); //ser� passado o email que voc� far� a autentica��o 
//		email.setSubject(titulo);  
//		email.setMsg(mensagem);  
//		email.send();  
//		
//	}
	
}
