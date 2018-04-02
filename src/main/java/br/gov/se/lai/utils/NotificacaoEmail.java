package br.gov.se.lai.utils;

import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailConstants;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.apache.commons.mail.resolver.DataSourceUrlResolver;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.ImageHtmlEmail;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.sun.mail.imap.protocol.MailboxInfo;

import br.gov.se.lai.Bean.ResponsavelBean;
import br.gov.se.lai.DAO.EntidadesDAO;
import br.gov.se.lai.DAO.MensagemDAO;
import br.gov.se.lai.DAO.ResponsavelDAO;
import br.gov.se.lai.DAO.UsuarioDAO;
import br.gov.se.lai.entity.Cidadao;
import br.gov.se.lai.entity.Entidades;
import br.gov.se.lai.entity.Mensagem;
import br.gov.se.lai.entity.Responsavel;
import br.gov.se.lai.entity.Solicitacao;
import br.gov.se.lai.entity.Usuario;

public class NotificacaoEmail implements Job{
	

	private static File img1;


	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
	}
	
	public static void enviarNotificacao(Solicitacao solicitacao, Usuario usuario) {
		
		String[] envio = destinatarioEmail(usuario, solicitacao);
		String remetente = envio[0];
		String destinatario = envio[1];
		String titulo = "Solicitação "+solicitacao.getProtocolo() + " - " + solicitacao.getTitulo().toString();

//		List<Mensagem> mensagens = new ArrayList<Mensagem>(MensagemDAO.list(solicitacao.getIdSolicitacao()));	
//		String mensagem = mensagens.get(mensagens.size()-1).getTexto();
		StringBuffer msg = new StringBuffer();
		  msg.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
		  msg.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
		  msg.append("<head>");
		  msg.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");
		  msg.append("<title>Redefinição de senha e-SIC</title>");
		  msg.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"/>");
		  msg.append("</head>");
		  
		  msg.append("<body style=\"margin: 20px; padding: 0; background-color: #ebecf0 !important; font-family: lato, Sans-serif; font-weight: normal;\">");
		 
		  msg.append("<table align=\"center\" border=\"1\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" style=\"border: 1px solid #d9dbde; border-collapse: collapse;  margin-top: 10px;\">");
		  
			  msg.append("<tr>");
				  msg.append("<td align=\"center\" bgcolor=\"#145187\" style=\"padding: 20px;\">");
				  	msg.append("<h2 style=\"margin-bottom: 0; color: white;\">e-SIC - SE</h2>");
				  msg.append("</td>");
			  msg.append("</tr>");
			  
			  msg.append("<tr>");
				  msg.append("<td bgcolor=\"#ffffff\" style=\"padding: 40px; text-align: center;\">");
//				  =======================
//				  == CORPO DA MENSAGEM ==
//				  =======================
					  msg.append("<p style=\"font-size: 30px; margin-top: 0\">Nova Mensagem</p>");
					  msg.append("<p>Há uma nova mensagem de " + remetente + " para " + solicitacao.getEntidades().getSigla() + " na solicitação de protocolo " + solicitacao.getProtocolo() + "</p>");
					  msg.append("<p>Acesse o portal do esic para visualizar: </p>");
					  msg.append("<a href=\"http://esic.se.gov.br/\"> http://esic.se.gov.br </a>" );
					  msg.append("<p>" + envio[2] + "</p>");
//				  =======================
//				  =======================
				  msg.append("</td>");
			  msg.append("</tr>");
			  
			  msg.append("<tr>");
				  msg.append("<td bgcolor=\"#145187\" style=\"padding: 20px;\">");
					  msg.append("<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
						  msg.append("<tr>");
							  msg.append("<td>");
//							  	msg.append("<img src=\"https://i.imgur.com/V73uJdA.png\" height=\"80px\" style=\"display: block;\" />");
							  msg.append("</td>");
							  msg.append("<td>");
								  msg.append("<h2 style=\"margin-bottom: 0; color: white;\">Controladoria-Geral do Estado</h2>");
								  msg.append("<p style=\"margin-bottom: 0; color: white;\">Governo do Estado de Sergipe</p>");
							  msg.append("</td>");
						  msg.append("</tr>");
					  msg.append("</table>");
				  msg.append("</td>");
			  msg.append("</tr>");
		  
		  msg.append("</table>");
		  
		  msg.append("</body>");
		  msg.append("</html>");
		  
//		String mensagem = "Há uma nova mensagem de "+remetente + " para "+solicitacao.getEntidades().getSigla()+" na solicitação de protocolo "+ 
//						solicitacao.getProtocolo() + " acesse o portal do esic para visualizar. \n Acesse: http://esic.se.gov.br" + "\n"+envio[2]; 

		try {  
			enviarEmailHTML(destinatario, titulo, msg.toString());
			
		} catch (EmailException e) {  
			System.out.println(e.getCause());
			
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
					envio[2] = "Mensagem automática: Não há responsáveis cadastrados e/ou ativos no sistema para a entidade "+solicitacao.getEntidades().getNome()+
							   " ("+ solicitacao.getEntidades().getSigla()+") para a instância "+solicitacao.getInstancia() +".\n A solicitação "+solicitacao.getProtocolo()+
							   " efetuada pela(o) cidadã(o)"+ solicitacao.getCidadao().getUsuario().getNome()+"("+solicitacao.getCidadao().getEmail()+
							   ") direcionada para esta entidade não pode ser notificada a nenhum responsável ligado a/ao " + solicitacao.getEntidades().getSigla()+
							   ".\nNotificamos o órgão responsável.";

				}else {
					respId = ResponsavelBean.responsavelDisponivel(1, 1);
					if(respId != -1) {
						envio[1] = ResponsavelDAO.findResponsavel(respId).getEmail();
						envio[2] = "Mensagem automática: Não há responsáveis cadastrados e/ou ativos no sistema para a entidade "+solicitacao.getEntidades().getNome()+
								" ("+ solicitacao.getEntidades().getSigla()+") para a instância "+solicitacao.getInstancia() +".\n A solicitação "+solicitacao.getProtocolo()+" efetuada pela(o) cidadã(o)"+
								solicitacao.getCidadao().getUsuario().getNome()+"("+solicitacao.getCidadao().getEmail()+") direcionada para esta entidade não "+ 
								"pode ser notificada a nenhum responsável ligado a/ao " + solicitacao.getEntidades().getSigla()+".\nNotificamos a CGE.";
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
			email.setAuthentication("mayara.machado","abcd1234");  
//			email.setSmtpPort(465);
//			email.addTo(resp.getEmail().toString()); //pode ser qualquer email  
			email.addTo("mayara.machado@cge.se.gov.br"); //pode ser qualquer email  
			email.setFrom("no_reply@cge.se.gov.br"); //será passado o email que você fará a autenticação
			
			StringBuffer msg = new StringBuffer();
			  msg.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
			  msg.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
			  msg.append("<head>");
			  msg.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");
			  msg.append("<title>Redefinição de senha e-SIC</title>");
			  msg.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"/>");
			  msg.append("</head>");
			  
			  msg.append("<body style=\"margin: 20px; padding: 0; background-color: #ebecf0 !important; font-family: lato, Sans-serif; font-weight: normal;\">");
			 
			  msg.append("<table align=\"center\" border=\"1\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" style=\"border: 1px solid #d9dbde; border-collapse: collapse;  margin-top: 10px;\">");
			  
				  msg.append("<tr>");
					  msg.append("<td align=\"center\" bgcolor=\"#145187\" style=\"padding: 20px;\">");
					  	msg.append("<h2 style=\"margin-bottom: 0; color: white;\">e-SIC - SE</h2>");
					  msg.append("</td>");
				  msg.append("</tr>");
				  
				  msg.append("<tr>");
					  msg.append("<td bgcolor=\"#ffffff\" style=\"padding: 40px; text-align: center;\">");
//					  =======================
//					  == CORPO DA MENSAGEM ==
//					  =======================
						  msg.append("<p style=\"font-size: 30px; margin-top: 0\">" + titulo + "</p>");
						  msg.append("<p>" + mensagem + "</p>");
//					  =======================
//					  =======================
					  msg.append("</td>");
				  msg.append("</tr>");
				  
				  msg.append("<tr>");
					  msg.append("<td bgcolor=\"#145187\" style=\"padding: 20px;\">");
						  msg.append("<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
							  msg.append("<tr>");
								  msg.append("<td>");
//								  	msg.append("<img src=\"https://i.imgur.com/V73uJdA.png\" height=\"80px\" style=\"display: block;\" />");
								  msg.append("</td>");
								  msg.append("<td>");
									  msg.append("<h2 style=\"margin-bottom: 0; color: white;\">Controladoria-Geral do Estado</h2>");
									  msg.append("<p style=\"margin-bottom: 0; color: white;\">Governo do Estado de Sergipe</p>");
								  msg.append("</td>");
							  msg.append("</tr>");
						  msg.append("</table>");
					  msg.append("</td>");
				  msg.append("</tr>");
			  
			  msg.append("</table>");
			  
			  msg.append("</body>");
			  msg.append("</html>");
			
			  
		} catch (EmailException e) {  
			System.out.println(e.getMessage());  
			System.out.println(email.getSocketConnectionTimeout());  
		}catch (NullPointerException e) {
//			System.out.println(e.getMessage());  
		}catch (ArrayIndexOutOfBoundsException e) {
//			System.out.println(e.getMessage());  
		}
	}

	public static void enviarEmailTramites(Solicitacao solicitacao, String mensagem, Responsavel respRemetente, Responsavel respDestinatario ) {
		Email email = new SimpleEmail();
		List<Responsavel> resp = ResponsavelDAO.findResponsavelEntidadeNivel(solicitacao.getEntidades().getIdEntidades(), 1);
		
		try {  
			email.setDebug(true);  
			email.setHostName("smtp.expresso.se.gov.br");  
			email.setAuthentication("mayara.machado","abcd1234");  
//			email.setSmtpPort(465);
//			email.addTo(respDestinatario); //pode ser qualquer email  
			email.addTo("mayara.machado@cge.se.gov.br");  
			email.setFrom(respRemetente.getEmail()); 
			email.setSubject("Trâmite Interno");  
			email.setMsg(mensagem+" >"+respDestinatario.getEmail());  
			email.send();  
		} catch (EmailException e) {  
			e.getMessage();
		}
	}

	public static void enviarEmailAutoridades(int idEntidade, String titulo, String mensagem) {
		Email email = new SimpleEmail();
		List<Responsavel> respSec = ResponsavelDAO.findResponsavelEntidadeNivel(idEntidade, 3);
		List<Responsavel> respDir = ResponsavelDAO.findResponsavelEntidadeNivel(idEntidade, 2);
		
		try {  
			email.setDebug(true);  
			email.setHostName("smtp.expresso.se.gov.br");
			email.setAuthentication("mayara.machado","abcd1234");  
//			email.setSmtpPort(465);
			email.addTo("mayara.machado@cge.se.gov.br"); //pode ser qualquer email  
//			email.addTo(respSec.get(0).getEmail().toString()); //pode ser qualquer email  
//			email.addCc(solicitacao.getCidadao().getEmail()); //pode ser qualquer email  
//			if(respSec.size() > 1) {
//				email.addCc(respSec.get(1).toString());
//			}
//			email.addCc(respDir.get(0).toString());
			email.setFrom("no_reply@cge.se.gov.br"); //será passado o email que você fará a autenticação 
			email.setSubject(titulo);  
			email.setMsg(mensagem);  
			email.send();  
		} catch (EmailException e) {   
//			System.out.println(e.getMessage());  
		}
	}
	

	
//	public static void enviarEmailRedefinicaoSenha(String hashcodeUser, String email) {
//		String mensagem = "Clique no link para redefinir sua senha:  http://localhost:8080/esic/Alterar/redefinir_senha.xhtml?access_key="+hashcodeUser;
//		try {
//			enviarEmail(email, "Redefinição de senha", mensagem);
//
//		} catch (EmailException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
	public static void enviarEmailRedefinicaoSenha(String hashcodeUser, String email) {
		String link = "http://esic.se.gov.br/Alterar/redefinir_senha.xhtml?access_key="+hashcodeUser;
		try {
			  StringBuffer msg = new StringBuffer();
			  msg.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
			  msg.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
			  msg.append("<head>");
			  msg.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");
			  msg.append("<title>Redefinição de senha e-SIC</title>");
			  msg.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"/>");
			  msg.append("</head>");
			  
			  msg.append("<body style=\"margin: 20px; padding: 0; background-color: #ebecf0 !important; font-family: lato, Sans-serif; font-weight: normal;\">");
			 
			  msg.append("<table align=\"center\" border=\"1\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" style=\"border: 1px solid #d9dbde; border-collapse: collapse;  margin-top: 10px;\">");
			  
				  msg.append("<tr>");
					  msg.append("<td align=\"center\" bgcolor=\"#145187\" style=\"padding: 20px;\">");
					  	msg.append("<h2 style=\"margin-bottom: 0; color: white;\">e-SIC - SE</h2>");
					  msg.append("</td>");
				  msg.append("</tr>");
				  
				  msg.append("<tr>");
					  msg.append("<td bgcolor=\"#ffffff\" style=\"padding: 40px; text-align: center;\">");
						  msg.append("<p style=\"font-size: 30px; margin-top: 0\">Redefinição de Senha</p>");
						  msg.append("<p>Se você esqueceu sua senha ou deseja redefini-la, use o botão abaixo para fazê-lo.</p>");
						  msg.append("<a href=\"" + link + "\">" );
						  msg.append("<button style=\"margin: 20px; color: white; border-radius: 3px; border: 1px solid #1d4a74; padding: 10px; font-size: 20px; background-color: #296099; background: linear-gradient(to bottom right, #135186, #4087dc);\"> Nova Senha</button>");
						  msg.append("</a>");
						  msg.append("<p style=\"color: #9E9E9E; margin-bottom: 0\">Se você não requisitou a mudança de senha, ignore este e-mail. Apenas uma pessoa com acesso ao seu e-mail pode redefinir sua senha.</p>");
						  msg.append("<p style=\"color: #9E9E9E; margin-bottom: 0\">Caso o botão não funcione, clique ou copie o link a seguir no navegador:</p>");
						  msg.append("<a style=\"color: #9E9E9E;\" href=\"" + link + "\">"+ link +"</a>" );
					  msg.append("</td>");
				  msg.append("</tr>");
				  
				  msg.append("<tr>");
					  msg.append("<td bgcolor=\"#145187\" style=\"padding: 20px;\">");
						  msg.append("<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
							  msg.append("<tr>");
								  msg.append("<td>");
//								  	msg.append("<img src=\"https://i.imgur.com/V73uJdA.png\" height=\"80px\" style=\"display: block;\" />");
								  msg.append("</td>");
								  msg.append("<td>");
									  msg.append("<h2 style=\"margin-bottom: 0; color: white;\">Controladoria-Geral do Estado</h2>");
									  msg.append("<p style=\"margin-bottom: 0; color: white;\">Governo do Estado de Sergipe</p>");
								  msg.append("</td>");
							  msg.append("</tr>");
						  msg.append("</table>");
					  msg.append("</td>");
				  msg.append("</tr>");
			  
			  msg.append("</table>");
			  
			  msg.append("</body>");
			  msg.append("</html>");
			  
			  // set the html message
			  enviarEmailHTML(email, "Redefinição de senha e-SIC SE", msg.toString());

		} catch (EmailException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	public static void enviarEmailRequisicaoResponsavel(int idUser, int idEntidade, String emailUser,String hashcode, String email, String mensagem) {
		mensagem += "\n\nClique no link para cadastrar usuário:  http://localhost:8080/esic/Cadastro/cad_responsavel.xhtml?access-key="+hashcode.substring(0, hashcode.length()/2)
																								+"&user="+idUser
																								+"&identidade="+idEntidade
																								+"&"+hashcode.substring(hashcode.length()/2, hashcode.length())
																								+"&mail="+emailUser;
		try {
			enviarEmail(email, "Requisição de responsável", mensagem);
			
		} catch (EmailException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//+++++++++++++++++++ Email HTML
	public static void enviarEmailHTML(String destinatario, String titulo, String mensagem) throws EmailException {
		
		// Create the email message
		ImageHtmlEmail emailHtml = new ImageHtmlEmail();
		emailHtml.setDebug(true);
		emailHtml.setHostName("smtp.expresso.se.gov.br");  
		emailHtml.setAuthentication("michael.mendonca","015975325");  
		emailHtml.addTo(destinatario);
		emailHtml.setFrom("no_reply@cge.se.gov.br"); //será passado o email que você fará a autenticação
		emailHtml.setSubject(titulo);
		
		// set the html message
		emailHtml.setHtmlMsg(mensagem);
		
		// set the alternative message
		emailHtml.setTextMsg("Seu provedor de email não suporta este tipo de mensagens.");
		
		// send the email
		emailHtml.send();
	}
	
	//+++++++++++++++++++ Email simples
	public static void enviarEmail(String destinatario, String titulo, String mensagem) throws EmailException {
		
		Email email = new SimpleEmail();
		EmailAttachment attachment = new EmailAttachment();
		
		email.setDebug(true);  
		email.setHostName("smtp.expresso.se.gov.br");  
		email.setAuthentication("mayara.machado","abcd1234");  
//		email.setSmtpPort(465);
		email.addTo("mayara.machado@cge.se.gov.br"); 
//		email.addCc("michael.mendonca@cge.se.gov.br");
//		email.addTo(destinatario);  
		email.setFrom("no_reply@cge.se.gov.br"); //será passado o email que você fará a autenticação 
		email.setSubject(titulo);  
		email.setMsg(mensagem+"\n\n\n\n\n\n destinatario: "+destinatario);  // só para teste  
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
//		email.setAuthentication("michael.mendonca","015975325");  
//		email.addTo("michael.mendonca@cge.se.gov.br"); //pode ser qualquer email  
//		email.setFrom(remetente); //será passado o email que você fará a autenticação 
//		email.setSubject(titulo);  
//		email.setMsg(mensagem);  
//		email.send();  
//		
//	}
	
}
