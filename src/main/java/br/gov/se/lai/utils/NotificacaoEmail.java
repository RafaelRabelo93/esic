package br.gov.se.lai.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailConstants;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.ImageHtmlEmail;
import org.apache.commons.mail.SimpleEmail;
import org.apache.commons.mail.resolver.DataSourceUrlResolver;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.sun.mail.imap.protocol.MailboxInfo;

import br.gov.se.lai.Bean.ResponsavelBean;
import br.gov.se.lai.Bean.UsuarioBean;
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
	

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
	}
	
	public static void enviarNotificacao(Solicitacao solicitacao, Usuario usuario) {
		
		String[] envio = destinatarioEmail(usuario, solicitacao);
		String remetente = envio[0];
		String destinatario = envio[1];
//		String destinatario = DadosAutenticacao.getEmailteste();
		String titulo = "Solicitação "+ solicitacao.getProtocolo() + " - " + solicitacao.getTitulo().toString();

//		List<Mensagem> mensagens = new ArrayList<Mensagem>(MensagemDAO.list(solicitacao.getIdSolicitacao()));	
//		String mensagem = mensagens.get(mensagens.size()-1).getTexto();
		
		StringBuffer msg = new StringBuffer();
		msg.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
		msg.append("<html xmlns=\"http://www.w3.org/1999/xhtml\" >");
		msg.append("<head>");
		msg.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");
		msg.append("<title>Redefinição de senha e-SIC</title>");
		msg.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />");
		msg.append("<style>");
		msg.append("html, body {height:100%}");
		msg.append("</style>");
		msg.append("</head>");
		msg.append("<body style=\"padding: 0; margin: 0; font-family: lato, Sans-serif; font-weight: normal;\">");
		msg.append("	<table align=\"center\" cellpadding=\"0\" cellspacing=\"0\" style=\"width:100%; height:100%; background-color: #F2F2F2; padding: 20px\">");
		msg.append("		<tr>");
		msg.append("		<td style=\"padding:10px; border:none\">");
		msg.append("	<table align=\"center\" border=\"1\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" style=\"border: none; background: white; border-collapse: collapse;\">");
		msg.append("		<tr  style=\"color:white; border: none; background: rgb(8, 72, 130); background: -webkit-gradient(linear, left top, right top, from(rgba(10,46,78,1)), color-stop(24%, rgba(27,99,158,0.9)), color-stop(50%, rgba(27,99,158,0.8)), to(rgba(27,99,158,0)));background: -webkit-linear-gradient(left, rgba(10,46,78,1) 0%, rgba(27,99,158,0.9) 24%, rgba(27,99,158,0.8) 50%, rgba(27,99,158,0) 100%);background: -o-linear-gradient(left, rgba(10,46,78,1) 0%, rgba(27,99,158,0.9) 24%, rgba(27,99,158,0.8) 50%, rgba(27,99,158,0) 100%);background: linear-gradient(to right, rgba(10,46,78,1) 0%, rgba(27,99,158,0.9) 24%, rgba(27,99,158,0.8) 50%, rgba(27,99,158,0) 100%);\">");
		msg.append("			<td style=\"padding:10px; border:none\">");
		msg.append("				<p style=\"font-size: 28px; font-weight: bold; margin: 0\">e-SIC - SE</p>");
		msg.append("				<p style=\"font-size: 12px; font-weight: bold;  margin: 0\">SERVIÇO DE</p>");
		msg.append("				<p style=\"font-size: 12px; font-weight: bold;  margin: 0\">INFORMAÇÃO AO CIDADÃO</p>");
		msg.append("			</td>");
		msg.append("		</tr>");
		msg.append("		<tr>");
		msg.append("<td bgcolor=\"#ffffff\" style=\"padding: 40px; text-align: left; border: none; color: #606060\">");
		msg.append("	<h4>Nova solicitação realizada para " + solicitacao.getEntidades().getSigla() + ".</h4>");
		msg.append("	<table>");
		msg.append("		<tr>");
		msg.append("		<td><h4 style=\"margin: 0\">Protocolo: </h4></td>");
		msg.append("					<td><p style=\"margin: 0\">" + solicitacao.getProtocolo() + "</p></td>");
		msg.append("					</tr>");
		msg.append("				</table>");
		msg.append("				<table>");
		msg.append("					<tr>");
		msg.append("					<td><h4 style=\"margin: 0\">Órgão: </h4></td>");
		msg.append("					<td><p style=\"margin: 0\">" + solicitacao.getEntidades().getSigla() + " - "  + solicitacao.getEntidades().getNome() + "</p></td>");
		msg.append("					</tr>");
		msg.append("				</table>");
		msg.append("				<table>");
		msg.append("					<tr>");
		msg.append("					<td><h4 style=\"margin: 0\">Data de envio: </h4></td>");
		msg.append("					<td><p style=\"margin: 0\">" + DateFormat.getDateInstance(DateFormat.SHORT, Locale.CANADA).format(solicitacao.getDataIni()) + "</p></td>");
		msg.append("					</tr>");
		msg.append("				</table>");
		msg.append("				<table>");
		msg.append("					<tr>");
		msg.append("					<td><h4 style=\"margin: 0\">Data limite: </h4></td>");
		msg.append("					<td><p style=\"margin: 0\">" + DateFormat.getDateInstance(DateFormat.SHORT, Locale.CANADA).format(solicitacao.getDataLimite()) + "</p></td>");
		msg.append("		</tr>");
		msg.append("	</table>");
		msg.append("	<h5 style=\"margin-bottom: 0\">Acesse o sistema para visualizar as solicitações:</h5>");
		msg.append("	<a href=\"http://esic.se.gov.br\">http://esic.se.gov.br</a>");
		msg.append("	<h5 style=\"margin-bottom: 0; font-weight: normal\">Em caso de dúvida, entre em contato através do email controladoria@cge.se.gov.br ou telefone (79) 3179-3777</h5>");
		msg.append("	<h5 style=\"margin-bottom: 0; font-weight: normal\">E-mail automático, favor não responder.</h5>");
		msg.append("</td>");
		msg.append("		</tr>");
		msg.append("		<tr>");
		msg.append("			<td style=\"padding: 20px; border: none; text-align: center; font-size: 12px; color: #757575;\">");
		msg.append("				<p style=\"margin: 0\">Controladoria-Geral do Estado de Sergipe - 2018</p>");
		msg.append("			</td>");
		msg.append("		</tr>");
		msg.append("	</table>");
		msg.append("		</td>");
		msg.append("		</tr>");
		msg.append("	</table>");
		msg.append("</body>");
		msg.append("</html>");
		
		try {  
			enviarEmailHTML(destinatario, titulo, msg.toString());
			
		} catch (EmailException e) {  
			System.out.println(e.getCause());
			
		}   
		
	}
	
	
	public static String[] destinatarioEmail(Usuario usuario, Solicitacao solicitacao) {
		String[] envio = new String[3];
		if(usuario.getPerfil() == 3 || (usuario.getPerfil() == 4 && UsuarioBean.perfilAlterarCidadaoResponsavel == false) ) {
			List<Cidadao> listCidadao = new ArrayList<Cidadao>(usuario.getCidadaos());	
//			envio[0] = listCidadao.get(0).getUsuario().getNome();
			envio[0] = listCidadao.get(0).getEmail();
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
			if(usuario.getPerfil() == 2 || (usuario.getPerfil() == 4 && UsuarioBean.perfilAlterarCidadaoResponsavel == true)) {
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
		
		Responsavel resp = ResponsavelDAO.findResponsavel(ResponsavelBean.responsavelDisponivel(1, solicitacao.getEntidades().getIdEntidades()));
		
		StringBuffer msg = new StringBuffer();
		msg.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
		msg.append("<html xmlns=\"http://www.w3.org/1999/xhtml\" >");
		msg.append("<head>");
		msg.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");
		msg.append("<title>Redefinição de senha e-SIC</title>");
		msg.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />");
		msg.append("<style>");
		msg.append("html, body {height:100%}");
		msg.append("</style>");
		msg.append("</head>");
		msg.append("<body style=\"padding: 0; margin: 0; font-family: lato, Sans-serif; font-weight: normal;\">");
		msg.append("	<table align=\"center\" cellpadding=\"0\" cellspacing=\"0\" style=\"width:100%; height:100%; background-color: #F2F2F2; padding: 20px\">");
		msg.append("		<tr>");
		msg.append("		<td style=\"padding:10px; border:none\">");
		msg.append("	<table align=\"center\" border=\"1\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" style=\"border: none; background: white; border-collapse: collapse;\">");
		msg.append("		<tr  style=\"color:white; border: none; background: rgb(8, 72, 130); background: -webkit-gradient(linear, left top, right top, from(rgba(10,46,78,1)), color-stop(24%, rgba(27,99,158,0.9)), color-stop(50%, rgba(27,99,158,0.8)), to(rgba(27,99,158,0)));background: -webkit-linear-gradient(left, rgba(10,46,78,1) 0%, rgba(27,99,158,0.9) 24%, rgba(27,99,158,0.8) 50%, rgba(27,99,158,0) 100%);background: -o-linear-gradient(left, rgba(10,46,78,1) 0%, rgba(27,99,158,0.9) 24%, rgba(27,99,158,0.8) 50%, rgba(27,99,158,0) 100%);background: linear-gradient(to right, rgba(10,46,78,1) 0%, rgba(27,99,158,0.9) 24%, rgba(27,99,158,0.8) 50%, rgba(27,99,158,0) 100%);\">");
		msg.append("			<td style=\"padding:10px; border:none\">");
		msg.append("				<p style=\"font-size: 28px; font-weight: bold; margin: 0\">e-SIC - SE</p>");
		msg.append("				<p style=\"font-size: 12px; font-weight: bold;  margin: 0\">SERVIÇO DE</p>");
		msg.append("				<p style=\"font-size: 12px; font-weight: bold;  margin: 0\">INFORMAÇÃO AO CIDADÃO</p>");
		msg.append("			</td>");
		msg.append("		</tr>");
		msg.append("		<tr>");
		msg.append("<td bgcolor=\"#ffffff\" style=\"padding: 40px; text-align: left; border: none; color: #606060\">");
		msg.append("	<h4>Notificação no prazo de solicitação</h4>");
		msg.append("	<table>");
		msg.append("		<tr>");
		msg.append("	<h5 style=\"margin-bottom: 0\">" + mensagem + "</h5>");
		msg.append("		</tr>");
		msg.append("	</table>");
		msg.append("	<h5 style=\"margin-bottom: 0\">Acesse o sistema para visualizar as solicitações:</h5>");
		msg.append("	<a href=\"http://esic.se.gov.br\">http://esic.se.gov.br</a>");
		msg.append("	<h5 style=\"margin-bottom: 0; font-weight: normal\">Em caso de dúvida, entre em contato através do email controladoria@cge.se.gov.br ou telefone (79) 3179-3777</h5>");
		msg.append("	<h5 style=\"margin-bottom: 0; font-weight: normal\">E-mail automático, favor não responder.</h5>");
		msg.append("</td>");
		msg.append("		</tr>");
		msg.append("		<tr>");
		msg.append("			<td style=\"padding: 20px; border: none; text-align: center; font-size: 12px; color: #757575;\">");
		msg.append("				<p style=\"margin: 0\">Controladoria-Geral do Estado de Sergipe - 2018</p>");
		msg.append("			</td>");
		msg.append("		</tr>");
		msg.append("	</table>");
		msg.append("		</td>");
		msg.append("		</tr>");
		msg.append("	</table>");
		msg.append("</body>");
		msg.append("</html>");
		
		try {	  
		  // set the html message
		  enviarEmailHTML(resp.getEmail().toString(), titulo, msg.toString());
		} catch (EmailException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void enviarEmailTramites(Solicitacao solicitacao, String mensagem, Responsavel respRemetente, Responsavel respDestinatario ) {
		Email email = new SimpleEmail();
		List<Responsavel> resp = ResponsavelDAO.findResponsavelEntidadeNivel(solicitacao.getEntidades().getIdEntidades(), 1);

		try { 
			
		ImageHtmlEmail emailHtml = new ImageHtmlEmail();
		emailHtml.setDebug(true);
		emailHtml.setHostName(DadosAutenticacao.getHostNameEmail());  
		emailHtml.setAuthentication(DadosAutenticacao.getUserLoginEmailAuthentication(),DadosAutenticacao.getSenhaUserLoginEmailAuthentication());  
		emailHtml.addTo(respDestinatario.getEmail());
		emailHtml.setFrom(respRemetente.getEmail()); //será passado o email que você fará a autenticação
		emailHtml.setSubject("Solicitação encaminhada — esic-SE");
		
		StringBuffer msg = new StringBuffer();
		msg.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
		msg.append("<html xmlns=\"http://www.w3.org/1999/xhtml\" >");
		msg.append("<head>");
		msg.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");
		msg.append("<title>Redefinição de senha e-SIC</title>");
		msg.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />");
		msg.append("<style>");
		msg.append("html, body {height:100%}");
		msg.append("</style>");
		msg.append("</head>");
		msg.append("<body style=\"padding: 0; margin: 0; font-family: lato, Sans-serif; font-weight: normal;\">");
		msg.append("	<table align=\"center\" cellpadding=\"0\" cellspacing=\"0\" style=\"width:100%; height:100%; background-color: #F2F2F2; padding: 20px\">");
		msg.append("		<tr>");
		msg.append("		<td style=\"padding:10px; border:none\">");
		msg.append("	<table align=\"center\" border=\"1\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" style=\"border: none; background: white; border-collapse: collapse;\">");
		msg.append("		<tr  style=\"color:white; border: none; background: rgb(8, 72, 130); background: -webkit-gradient(linear, left top, right top, from(rgba(10,46,78,1)), color-stop(24%, rgba(27,99,158,0.9)), color-stop(50%, rgba(27,99,158,0.8)), to(rgba(27,99,158,0)));background: -webkit-linear-gradient(left, rgba(10,46,78,1) 0%, rgba(27,99,158,0.9) 24%, rgba(27,99,158,0.8) 50%, rgba(27,99,158,0) 100%);background: -o-linear-gradient(left, rgba(10,46,78,1) 0%, rgba(27,99,158,0.9) 24%, rgba(27,99,158,0.8) 50%, rgba(27,99,158,0) 100%);background: linear-gradient(to right, rgba(10,46,78,1) 0%, rgba(27,99,158,0.9) 24%, rgba(27,99,158,0.8) 50%, rgba(27,99,158,0) 100%);\">");
		msg.append("			<td style=\"padding:10px; border:none\">");
		msg.append("				<p style=\"font-size: 28px; font-weight: bold; margin: 0\">e-SIC - SE</p>");
		msg.append("				<p style=\"font-size: 12px; font-weight: bold;  margin: 0\">SERVIÇO DE</p>");
		msg.append("				<p style=\"font-size: 12px; font-weight: bold;  margin: 0\">INFORMAÇÃO AO CIDADÃO</p>");
		msg.append("			</td>");
		msg.append("		</tr>");
		msg.append("		<tr>");
		msg.append("<td bgcolor=\"#ffffff\" style=\"padding: 40px; text-align: left; border: none; color: #606060\">");
		msg.append("	<h4>Notificação no prazo de solicitação</h4>");
		msg.append("	<table>");
		msg.append("		<tr>");
		msg.append("	<h5 style=\"margin-bottom: 0\">" + mensagem + " encaminhada por " + respRemetente + "</h5>");
		msg.append("		</tr>");
		msg.append("	</table>");
		msg.append("	<h5 style=\"margin-bottom: 0\">Acesse o sistema para visualizar as solicitações:</h5>");
		msg.append("	<a href=\"http://esic.se.gov.br\">http://esic.se.gov.br</a>");
		msg.append("	<h5 style=\"margin-bottom: 0; font-weight: normal\">Em caso de dúvida, entre em contato através do email controladoria@cge.se.gov.br ou telefone (79) 3179-3777</h5>");
		msg.append("	<h5 style=\"margin-bottom: 0; font-weight: normal\">E-mail automático, favor não responder.</h5>");
		msg.append("</td>");
		msg.append("		</tr>");
		msg.append("		<tr>");
		msg.append("			<td style=\"padding: 20px; border: none; text-align: center; font-size: 12px; color: #757575;\">");
		msg.append("				<p style=\"margin: 0\">Controladoria-Geral do Estado de Sergipe - 2018</p>");
		msg.append("			</td>");
		msg.append("		</tr>");
		msg.append("	</table>");
		msg.append("		</td>");
		msg.append("		</tr>");
		msg.append("	</table>");
		msg.append("</body>");
		msg.append("</html>");
		
		
			// set the html message
			emailHtml.setHtmlMsg(msg.toString());
			
			// set the alternative message
			emailHtml.setTextMsg("Seu provedor de email não suporta este tipo de mensagens.");
			
			// send the email
			emailHtml.send();
			
//			email.setDebug(true);  
//			email.setHostName(DadosAutenticacao.getHostNameEmail());  
//			email.setAuthentication(DadosAutenticacao.getUserLoginEmailAuthentication(),DadosAutenticacao.getSenhaUserLoginEmailAuthentication());  
////			email.setSmtpPort(465);
////			email.addTo(resp.getEmail().toString()); //pode ser qualquer email  
//			email.addTo("mayara.machado@cge.se.gov.br"); //pode ser qualquer email  
//			email.setFrom(respRemetente.getEmail()); 
//			email.setSubject("Trâmite Interno");  
//			email.setMsg(mensagem+" >"+respDestinatario.getEmail());  
//			email.send();  
		} catch (EmailException e) {  
			e.getMessage();
		}
	}
	
	

	public static void enviarEmailAutoridades(int idEntidade, String titulo, String mensagem) {
		Email email = new SimpleEmail();
		List<Responsavel> respSec = ResponsavelDAO.findResponsavelEntidadeNivel(idEntidade, 3);
		List<Responsavel> respDir = ResponsavelDAO.findResponsavelEntidadeNivel(idEntidade, 2);
		
		try {
			
			ImageHtmlEmail emailHtml = new ImageHtmlEmail();
			emailHtml.setDebug(true);
			emailHtml.setHostName(DadosAutenticacao.getHostNameEmail());  
			emailHtml.setAuthentication(DadosAutenticacao.getUserLoginEmailAuthentication(),DadosAutenticacao.getSenhaUserLoginEmailAuthentication());  
			emailHtml.addTo(respSec.get(0).getEmail().toString());
			if(respSec.size() > 1) {
				email.addCc(respSec.get(1).toString());
			}
			emailHtml.setFrom(DadosAutenticacao.getEmailFrom()); //será passado o email que você fará a autenticação
			emailHtml.setSubject("Solicitação encaminhada — esic-SE");
			
			StringBuffer msg = new StringBuffer();
			msg.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
			msg.append("<html xmlns=\"http://www.w3.org/1999/xhtml\" >");
			msg.append("<head>");
			msg.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");
			msg.append("<title>Redefinição de senha e-SIC</title>");
			msg.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />");
			msg.append("<style>");
			msg.append("html, body {height:100%}");
			msg.append("</style>");
			msg.append("</head>");
			msg.append("<body style=\"padding: 0; margin: 0; font-family: lato, Sans-serif; font-weight: normal;\">");
			msg.append("	<table align=\"center\" cellpadding=\"0\" cellspacing=\"0\" style=\"width:100%; height:100%; background-color: #F2F2F2; padding: 20px\">");
			msg.append("		<tr>");
			msg.append("		<td style=\"padding:10px; border:none\">");
			msg.append("	<table align=\"center\" border=\"1\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" style=\"border: none; background: white; border-collapse: collapse;\">");
			msg.append("		<tr  style=\"color:white; border: none; background: rgb(8, 72, 130); background: -webkit-gradient(linear, left top, right top, from(rgba(10,46,78,1)), color-stop(24%, rgba(27,99,158,0.9)), color-stop(50%, rgba(27,99,158,0.8)), to(rgba(27,99,158,0)));background: -webkit-linear-gradient(left, rgba(10,46,78,1) 0%, rgba(27,99,158,0.9) 24%, rgba(27,99,158,0.8) 50%, rgba(27,99,158,0) 100%);background: -o-linear-gradient(left, rgba(10,46,78,1) 0%, rgba(27,99,158,0.9) 24%, rgba(27,99,158,0.8) 50%, rgba(27,99,158,0) 100%);background: linear-gradient(to right, rgba(10,46,78,1) 0%, rgba(27,99,158,0.9) 24%, rgba(27,99,158,0.8) 50%, rgba(27,99,158,0) 100%);\">");
			msg.append("			<td style=\"padding:10px; border:none\">");
			msg.append("				<p style=\"font-size: 28px; font-weight: bold; margin: 0\">e-SIC - SE</p>");
			msg.append("				<p style=\"font-size: 12px; font-weight: bold;  margin: 0\">SERVIÇO DE</p>");
			msg.append("				<p style=\"font-size: 12px; font-weight: bold;  margin: 0\">INFORMAÇÃO AO CIDADÃO</p>");
			msg.append("			</td>");
			msg.append("		</tr>");
			msg.append("		<tr>");
			msg.append("<td bgcolor=\"#ffffff\" style=\"padding: 40px; text-align: left; border: none; color: #606060\">");
			msg.append("	<h4>Notificação no prazo de solicitação</h4>");
			msg.append("	<table>");
			msg.append("		<tr>");
			msg.append("	<h5 style=\"margin-bottom: 0\">" + mensagem + "</h5>");
			msg.append("		</tr>");
			msg.append("	</table>");
			msg.append("	<h5 style=\"margin-bottom: 0\">Acesse o sistema para visualizar as solicitações:</h5>");
			msg.append("	<a href=\"http://esic.se.gov.br\">http://esic.se.gov.br</a>");
			msg.append("	<h5 style=\"margin-bottom: 0; font-weight: normal\">Em caso de dúvida, entre em contato através do email controladoria@cge.se.gov.br ou telefone (79) 3179-3777</h5>");
			msg.append("	<h5 style=\"margin-bottom: 0; font-weight: normal\">E-mail automático, favor não responder.</h5>");
			msg.append("</td>");
			msg.append("		</tr>");
			msg.append("		<tr>");
			msg.append("			<td style=\"padding: 20px; border: none; text-align: center; font-size: 12px; color: #757575;\">");
			msg.append("				<p style=\"margin: 0\">Controladoria-Geral do Estado de Sergipe - 2018</p>");
			msg.append("			</td>");
			msg.append("		</tr>");
			msg.append("	</table>");
			msg.append("		</td>");
			msg.append("		</tr>");
			msg.append("	</table>");
			msg.append("</body>");
			msg.append("</html>");
			
			// set the html message
			emailHtml.setHtmlMsg(msg.toString());
			
			// set the alternative message
			emailHtml.setTextMsg("Seu provedor de email não suporta este tipo de mensagens.");
			
			// send the email
			emailHtml.send();
			
		} catch (EmailException e) {   
//			System.out.println(e.getMessage());  
		}
	}
	

	
	public static void enviarEmailRedefinicaoSenha(String hashcodeUser, String email, String nomeUser) {
		String link = DadosAutenticacao.getEnderecohomologacao()+"/Alterar/redefinir_senha.xhtml?access_key="+hashcodeUser;
		
		StringBuffer msg = new StringBuffer();
		msg.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
		msg.append("<html xmlns=\"http://www.w3.org/1999/xhtml\" >");
		msg.append("<head>");
		msg.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");
		msg.append("<title>Redefinição de senha e-SIC</title>");
		msg.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />");
		msg.append("<style>");
		msg.append("html, body {height:100%}");
		msg.append("a:hover {text-decoration: none !important; color: #2196F3 !important}");
		msg.append("</style>");
		msg.append("</head>");
		msg.append("<body style=\"padding: 0; margin: 0; font-family: lato, Sans-serif; font-weight: normal;\">");
		msg.append("	<table align=\"center\" cellpadding=\"0\" cellspacing=\"0\" style=\"width:100%; height:100%; background-color: #F2F2F2; padding: 20px\">");
		msg.append("		<tr>");
		msg.append("		<td style=\"padding:10px; border:none\">");
		msg.append("	<table align=\"center\" border=\"1\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" style=\"border: none; background: white; border-collapse: collapse;\">");
		msg.append("		<tr  style=\"color:white; border: none; background: rgb(8, 72, 130); background: -webkit-gradient(linear, left top, right top, from(rgba(10,46,78,1)), color-stop(24%, rgba(27,99,158,0.9)), color-stop(50%, rgba(27,99,158,0.8)), to(rgba(27,99,158,0)));background: -webkit-linear-gradient(left, rgba(10,46,78,1) 0%, rgba(27,99,158,0.9) 24%, rgba(27,99,158,0.8) 50%, rgba(27,99,158,0) 100%);background: -o-linear-gradient(left, rgba(10,46,78,1) 0%, rgba(27,99,158,0.9) 24%, rgba(27,99,158,0.8) 50%, rgba(27,99,158,0) 100%);background: linear-gradient(to right, rgba(10,46,78,1) 0%, rgba(27,99,158,0.9) 24%, rgba(27,99,158,0.8) 50%, rgba(27,99,158,0) 100%);\">");
		msg.append("			<td style=\"padding:10px; border:none\">");
		msg.append("				<p style=\"font-size: 28px; font-weight: bold; margin: 0\">e-SIC - SE</p>");
		msg.append("				<p style=\"font-size: 12px; font-weight: bold;  margin: 0\">SERVIÇO DE</p>");
		msg.append("				<p style=\"font-size: 12px; font-weight: bold;  margin: 0\">INFORMAÇÃO AO CIDADÃO</p>");
		msg.append("			</td>");
		msg.append("		</tr>");
		msg.append("		<tr>");
		
		msg.append("			<td bgcolor=\"#ffffff\" style=\"padding: 40px; text-align: left; border: none; color: #606060\">");
		msg.append("			<p style=\"font-size: 30px; margin-top: 0\">Olá, " + nomeUser + "</p>");
		msg.append("			<h4 style=\"margin-bottom:50px\">Se você esqueceu sua senha ou deseja redefini-la, utilize o botão abaixo para fazê-lo.</p>");
		msg.append("				<table align=\"center\">");
		msg.append("					<tr align=\"center\">");
		msg.append("					<td>");
		msg.append("						<a href=\"" + link + "\" style=\"color: white; border-radius: 3px; border: 1px solid #1d4a74; padding: 10px; font-size: 20px; background-color: #296099; background: linear-gradient(to bottom right, #135186, #4087dc); text-decoration: none;\">");
		msg.append("						Nova Senha");
		msg.append("						</a>");
		msg.append("					</td>");
		msg.append("					</tr>");
		msg.append("				</table>");
		msg.append("			<h5 style=\"margin-bottom: 0; margin-top: 50px; font-weight: normal\">Caso o botão não funcione, clique ou copie o link a seguir no navegador:</h5>");
		msg.append("			<a href=\"" + link + "\">" + link + "</a>");
		msg.append("			<h5 style=\"margin-bottom: 0; font-weight: normal\">Se você não requisitou a mudança de senha, ignore este e-mail. Apenas uma pessoa com acesso ao seu e-mail pode redefinir sua senha.</h5>");
		msg.append("			<h5 style=\"margin-bottom: 0; font-weight: normal\">Em caso de dúvida, entre em contato através do email controladoria@cge.se.gov.br ou telefone (79) 3179-3777</h5>");
		msg.append("			<h5 style=\"margin-bottom: 0; font-weight: normal\">E-mail automático, favor não responder.</h5>");
		msg.append("			</td>");
		
		msg.append("		</tr>");
		msg.append("		<tr>");
		msg.append("			<td style=\"padding: 20px; border: none; text-align: center; font-size: 12px; color: #757575;\">");
		msg.append("				<p style=\"margin: 0\">Controladoria-Geral do Estado de Sergipe - 2018</p>");
		msg.append("			</td>");
		msg.append("		</tr>");
		msg.append("	</table>");
		msg.append("		</td>");
		msg.append("		</tr>");
		msg.append("	</table>");
		msg.append("</body>");
		msg.append("</html>");
	  
			try {	  
			  // set the html message
			  enviarEmailHTML(email, "Redefinição de senha e-SIC SE", msg.toString());
			} catch (EmailException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	public static void enviarEmailRequisicaoResponsavel(Usuario usuario, Entidades entidade, int idUser, int idEntidade, String emailUser, String hashcode, String dest) {
		String link = DadosAutenticacao.getEnderecolocal()+"/Cadastro/cad_responsavel.xhtml?access-key="+hashcode.substring(0, hashcode.length()/2)
																								+"&user="+idUser
																								+"&identidade="+idEntidade
																								+"&"+hashcode.substring(hashcode.length()/2, hashcode.length())
																								+"&mail="+emailUser;
		
		StringBuffer msg = new StringBuffer();
		msg.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
		msg.append("<html xmlns=\"http://www.w3.org/1999/xhtml\" >");
		msg.append("<head>");
		msg.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");
		msg.append("<title>Nova solicitação</title>");
		msg.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />");
		msg.append("<style>");
		msg.append("html, body {height:100%}");
		msg.append("</style>");
		msg.append("</head>");
		msg.append("<body style=\"padding: 0; margin: 0; font-family: lato, Sans-serif; font-weight: normal;\">");
		msg.append("	<table align=\"center\" cellpadding=\"0\" cellspacing=\"0\" style=\"width:100%; height:100%; background-color: #F2F2F2; padding: 20px\">");
		msg.append("		<tr>");
		msg.append("		<td style=\"padding:10px; border:none\">");
		msg.append("	<table align=\"center\" border=\"1\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" style=\"border: none; background: white; border-collapse: collapse;\">");
		msg.append("		<tr  style=\"color:white; border: none; background: rgb(8, 72, 130); background: -webkit-gradient(linear, left top, right top, from(rgba(10,46,78,1)), color-stop(24%, rgba(27,99,158,0.9)), color-stop(50%, rgba(27,99,158,0.8)), to(rgba(27,99,158,0)));background: -webkit-linear-gradient(left, rgba(10,46,78,1) 0%, rgba(27,99,158,0.9) 24%, rgba(27,99,158,0.8) 50%, rgba(27,99,158,0) 100%);background: -o-linear-gradient(left, rgba(10,46,78,1) 0%, rgba(27,99,158,0.9) 24%, rgba(27,99,158,0.8) 50%, rgba(27,99,158,0) 100%);background: linear-gradient(to right, rgba(10,46,78,1) 0%, rgba(27,99,158,0.9) 24%, rgba(27,99,158,0.8) 50%, rgba(27,99,158,0) 100%);\">");
		msg.append("			<td style=\"padding:10px; border:none\">");
		msg.append("				<p style=\"font-size: 28px; font-weight: bold; margin: 0\">e-SIC - SE</p>");
		msg.append("				<p style=\"font-size: 12px; font-weight: bold;  margin: 0\">SERVIÇO DE</p>");
		msg.append("				<p style=\"font-size: 12px; font-weight: bold;  margin: 0\">INFORMAÇÃO AO CIDADÃO</p>");
		msg.append("			</td>");
		msg.append("		</tr>");
		msg.append("		<tr>");
		msg.append("			<td bgcolor=\"#ffffff\" style=\"padding: 40px; text-align: left; border: none; color: #606060\">");
		msg.append("				<p style=\"font-size: 30px; margin-top: 0\">Nova requisição de responsável</p>");
		msg.append("				<h4>Foi solicitada uma nova requisição de cadastro como responsável por:</h4>");
		msg.append("				<table>");
		msg.append("					<tr>");
		msg.append("					<td><h4 style=\"margin: 0\">Nome: </h4></td>");
		msg.append("					<td><p style=\"margin: 0\">" + usuario.getNome() + "</p></td>");
		msg.append("					</tr>");
		msg.append("				</table>");
		msg.append("				<table>");
		msg.append("					<tr>");
		msg.append("					<td><h4 style=\"margin: 0\">Nick: </h4></td>");
		msg.append("					<td><p style=\"margin: 0\">" + usuario.getNick() + "</p></td>");
		msg.append("					</tr>");
		msg.append("				</table>");
		msg.append("				<table>");
		msg.append("					<tr>");
		msg.append("					<td><h4 style=\"margin: 0\">E-mail: </h4></td>");
		msg.append("					<td><p style=\"margin: 0\">" + emailUser + "</p></td>");
		msg.append("					</tr>");
		msg.append("				</table>");
		msg.append("				<table>");
		msg.append("					<tr>");
		msg.append("					<td><h4 style=\"margin: 0\">Órgão: </h4></td>");
		msg.append("					<td><p style=\"margin: 0\">" + entidade.getSigla() + " - " + entidade.getNome() + "</p></td>");
		msg.append("					</tr>");
		msg.append("				</table>");
		msg.append("				<h5 style=\"margin-bottom: 50px\">Aperte o botão abaixo para prosseguir com cadastro de responsável.</h5>");
		msg.append("				<table>");
		msg.append("					<tr>");
		msg.append("						<a href=\"" + link + "\" style=\"color: white; border-radius: 3px; border: 1px solid #1d4a74; padding: 10px; font-size: 20px; background-color: #296099; background: linear-gradient(to bottom right, #135186, #4087dc); text-decoration: none;\">");
		msg.append("						Cadastrar");
		msg.append("						</a>");
		msg.append("					</tr>");
		msg.append("				</table>");
		msg.append("				<h5 style=\"margin-top: 50px\">Caso não deseje cadastrá-lo, ignore esta mensagem.</h5>");
		msg.append("				<h5 style=\"margin-bottom: 0; font-weight: normal\">Em caso de dúvida, entre em contato através do email controladoria@cge.se.gov.br ou telefone (79) 3179-3777</h5>");
		msg.append("				<h5 style=\"margin-bottom: 0; font-weight: normal\">E-mail automático, favor não responder.</h5>");
		msg.append("			</td>");
		msg.append("		</tr>");
		msg.append("		<tr>");
		msg.append("			<td style=\"padding: 20px; border: none; text-align: center; font-size: 12px; color: #757575;\">");
		msg.append("				<p style=\"margin: 0\">Controladoria-Geral do Estado de Sergipe - 2018</p>");
		msg.append("			</td>");
		msg.append("		</tr>");
		msg.append("	</table>");
		msg.append("		</td>");
		msg.append("		</tr>");
		msg.append("	</table>");
		msg.append("</body>");
		msg.append("</html>");
		
		try {
			enviarEmailHTML(dest, "Nova requisição de responsável", msg.toString());
			
		} catch (EmailException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void emailNovaSolicitacao(Solicitacao solicitacao, Usuario usuario) {
	// Create the email message
			String[] envio = destinatarioEmail(usuario, solicitacao);
			String destinatario = envio[0];
			String titulo = "esic-SE - Solicitação realizada com sucesso";
			
			StringBuffer msg = new StringBuffer();
			msg.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
			msg.append("<html xmlns=\"http://www.w3.org/1999/xhtml\" >");
			msg.append("<head>");
			msg.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");
			msg.append("<title>Nova solicitação</title>");
			msg.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />");
			msg.append("<style>");
			msg.append("html, body {height:100%}");
			msg.append("</style>");
			msg.append("</head>");
			msg.append("<body style=\"padding: 0; margin: 0; font-family: lato, Sans-serif; font-weight: normal;\">");
			msg.append("	<table align=\"center\" cellpadding=\"0\" cellspacing=\"0\" style=\"width:100%; height:100%; background-color: #F2F2F2; padding: 20px\">");
			msg.append("		<tr>");
			msg.append("		<td style=\"padding:10px; border:none\">");
			msg.append("	<table align=\"center\" border=\"1\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" style=\"border: none; background: white; border-collapse: collapse;\">");
			msg.append("		<tr  style=\"color:white; border: none; background: rgb(8, 72, 130); background: -webkit-gradient(linear, left top, right top, from(rgba(10,46,78,1)), color-stop(24%, rgba(27,99,158,0.9)), color-stop(50%, rgba(27,99,158,0.8)), to(rgba(27,99,158,0)));background: -webkit-linear-gradient(left, rgba(10,46,78,1) 0%, rgba(27,99,158,0.9) 24%, rgba(27,99,158,0.8) 50%, rgba(27,99,158,0) 100%);background: -o-linear-gradient(left, rgba(10,46,78,1) 0%, rgba(27,99,158,0.9) 24%, rgba(27,99,158,0.8) 50%, rgba(27,99,158,0) 100%);background: linear-gradient(to right, rgba(10,46,78,1) 0%, rgba(27,99,158,0.9) 24%, rgba(27,99,158,0.8) 50%, rgba(27,99,158,0) 100%);\">");
			msg.append("			<td style=\"padding:10px; border:none\">");
			msg.append("				<p style=\"font-size: 28px; font-weight: bold; margin: 0\">e-SIC - SE</p>");
			msg.append("				<p style=\"font-size: 12px; font-weight: bold;  margin: 0\">SERVIÇO DE</p>");
			msg.append("				<p style=\"font-size: 12px; font-weight: bold;  margin: 0\">INFORMAÇÃO AO CIDADÃO</p>");
			msg.append("			</td>");
			msg.append("		</tr>");
			msg.append("		<tr>");
			msg.append("			<td bgcolor=\"#ffffff\" style=\"padding: 40px; text-align: left; border: none; color: #606060\">");
			msg.append("				<p style=\"font-size: 30px; margin-top: 0\">Olá, " + usuario.getNome() + "</p>");
			msg.append("				<h4>Sua solicitação foi realizada com sucesso.</h4>");
			msg.append("				<table>");
			msg.append("					<tr>");
			msg.append("					<td><h4 style=\"margin: 0\">Protocolo: </h4></td>");
			msg.append("					<td><p style=\"margin: 0\">" + solicitacao.getProtocolo() + "</p></td>");
			msg.append("					</tr>");
			msg.append("				</table>");
			msg.append("				<table>");
			msg.append("					<tr>");
			msg.append("					<td><h4 style=\"margin: 0\">Órgão: </h4></td>");
			msg.append("					<td><p style=\"margin: 0\">" + solicitacao.getEntidades().getSigla() + " - "  + solicitacao.getEntidades().getNome() + "</p></td>");
			msg.append("					</tr>");
			msg.append("				</table>");
			msg.append("				<table>");
			msg.append("					<tr>");
			msg.append("					<td><h4 style=\"margin: 0\">Data de envio: </h4></td>");
			msg.append("					<td><p style=\"margin: 0\">" + DateFormat.getDateInstance(DateFormat.SHORT, Locale.CANADA).format(solicitacao.getDataIni()) + "</p></td>");
			msg.append("					</tr>");
			msg.append("				</table>");
			msg.append("				<table>");
			msg.append("					<tr>");
			msg.append("					<td><h4 style=\"margin: 0\">Data limite: </h4></td>");
			msg.append("					<td><p style=\"margin: 0\">" + DateFormat.getDateInstance(DateFormat.SHORT, Locale.CANADA).format(solicitacao.getDataLimite()) + "</p></td>");
			msg.append("					</tr>");
			msg.append("				</table>");
			msg.append("				<h5>Por favor, aguarde a resposta do órgão competente dentro do prazo estipulado</h5>");
			msg.append("				<h5 style=\"margin-bottom: 0\">Acesse o sistema para visualizar suas solicitações:</h5>");
			msg.append("				<a href=\"http://esic.se.gov.br\">http://esic.se.gov.br</a>");
			msg.append("				<h5 style=\"margin-bottom: 0; font-weight: normal\">Em caso de dúvida, entre em contato através do email controladoria@cge.se.gov.br ou telefone (79) 3179-3777</h5>");
			msg.append("				<h5 style=\"margin-bottom: 0; font-weight: normal\">E-mail automático, favor não responder.</h5>");
			msg.append("			</td>");
			msg.append("		</tr>");
			msg.append("		<tr>");
			msg.append("			<td style=\"padding: 20px; border: none; text-align: center; font-size: 12px; color: #757575;\">");
			msg.append("				<p style=\"margin: 0\">Controladoria-Geral do Estado de Sergipe - 2018</p>");
			msg.append("			</td>");
			msg.append("		</tr>");
			msg.append("	</table>");
			msg.append("		</td>");
			msg.append("		</tr>");
			msg.append("	</table>");
			msg.append("</body>");
			msg.append("</html>");
			
			try {  
				enviarEmailHTML(destinatario, titulo, msg.toString());
				
			} catch (EmailException e) {  
				System.out.println(e.getCause());
				
			}   
			
	}
	
	
	//+++++++++++++++++++ Email simples
	public static void enviarEmail(String destinatario, String titulo, String mensagem) throws EmailException {
		
		Email email = new SimpleEmail();
		EmailAttachment attachment = new EmailAttachment();
		
		
		email.setDebug(true);  
		email.setHostName(DadosAutenticacao.getHostNameEmail());  
		email.setAuthentication(DadosAutenticacao.getUserLoginEmailAuthentication(),DadosAutenticacao.getSenhaUserLoginEmailAuthentication());  
//		email.setSmtpPort(465);
//		email.addTo(resp.getEmail().toString()); //pode ser qualquer email  
		email.addTo(destinatario); //pode ser qualquer email  
		email.setFrom(DadosAutenticacao.getEmailFrom());//será passado o email que você fará a autenticação 
		email.setSubject(titulo);  
		email.setMsg(mensagem+"\n\n\n\n\n\n destinatario: "+destinatario);  // só para teste  
		email.send();  
		
	}
	
	//+++++++++++++++++++ Email HTML
	public static void enviarEmailHTML(String destinatario, String titulo, String mensagem) throws EmailException {
		
		// Create the email message
		ImageHtmlEmail emailHtml = new ImageHtmlEmail();
		emailHtml.setDebug(true);
		emailHtml.setHostName(DadosAutenticacao.getHostNameEmail());  
		emailHtml.setAuthentication(DadosAutenticacao.getUserLoginEmailAuthentication(),DadosAutenticacao.getSenhaUserLoginEmailAuthentication());  
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
	

	
}
