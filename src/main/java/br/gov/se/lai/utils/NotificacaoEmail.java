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
	
//	public static void enviarEmailAutomatico(Solicitacao solicitacao, String titulo, String mensagem) {
////		String destinatario = ((UsuarioBean) usuario.getCidadaos()).getEmail();
//		String destinatario = "michael.mendonca@cge.se.gov.br";
//				
//		String msg = "<h4>Notificação do prazo de solicitação</h4>" + 
//				"<table>" + 
//				"	<tr>" + 
//				"<h5 style=\"margin-bottom: 0\"> teste </h5>" + 
//				"	</tr>" + 
//				"</table>" + 
//				"<h5 style=\"margin-bottom: 0\">Acesse o sistema para visualizar as solicitações:</h5>" + 
//				"<a href=\"http://esic.se.gov.br\">http://esic.se.gov.br</a>";
//		
//		try {
//			enviarEmailHTML(destinatario, titulo, msg);
//		} catch (EmailException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
	
	public static void enviarEmailTramites(Solicitacao solicitacao, String texto, Responsavel respRemetente, Responsavel respDestinatario) {
		String destinatario = respDestinatario.getEmail();
		String titulo = "esic-SE - Nova manifestação encaminhada para " + solicitacao.getEntidades().getSigla();
		
		String tipoSol1;
		if (solicitacao.getTipo().equals("Informação")) tipoSol1 = "Pedido de Informação";
		else tipoSol1 = solicitacao.getTipo();
		
		String msg = "			<h4> Nova manifestação encaminhada para " + solicitacao.getEntidades().getSigla() + " - " + solicitacao.getEntidades().getNome() + "</h4>" + 
				"			<table>" + 
				"				<tr>" + 
				"				<td><h4 style=\"margin: 0\">Protocolo: </h4></td>" + 
				"				<td><p style=\"margin: 0\">" + solicitacao.getProtocolo() + "</p></td>" + 
				"				</tr>" + 
				"			</table>" +
				"			<table>" + 
				"				<tr>" + 
				"				<td><h4 style=\"margin: 0\">Tipo: </h4></td>" + 
				"				<td><p style=\"margin: 0\">" + tipoSol1 + "</p></td>" + 
				"				</tr>" + 
				"			</table>" +
				"			<table>" + 
				"				<tr>" + 
				"				<td><h4 style=\"margin: 0\">Órgão: </h4></td>" + 
				"				<td><p style=\"margin: 0\">" + solicitacao.getEntidades().getSigla() + " - " + solicitacao.getEntidades().getNome() + "</p></td>" + 
				"				</tr>" + 
				"			</table>" + 
				"				<tr>" + 
				"				<td><h4 style=\"margin: 0\">Manifestante: </h4></td>" + 
				"				<td><p style=\"margin: 0\">" + solicitacao.getCidadao().getUsuario().getNome() + "</p></td>" + 
				"				</tr>" + 
				"			</table>" +
				"			<br/>" +
				"			</table>" + 
				"				<tr>" + 
				"				<td><h4 style=\"margin: 0\">Encaminhada por: </h4></td>" + 
				"				<td><p style=\"margin: 0\">" + respRemetente.getUsuario().getNome() + "(" + respRemetente.getEmail() + ")</p></td>" + 
				"				</tr>" + 
				"			</table>" +
				
				"			</table>" + 
				"				<tr>" + 
				"				<td><h4 style=\"margin: 0\">Justificativa: </h4></td>" + 
				"				</tr>" + 
				"				<tr>" + 
				"				<td><p style=\"margin: 0\"> \"" + texto + "\"</p></td>" + 
				"				</tr>" + 
				"			</table>" +
				"			<h5 style=\"margin-bottom: 0\">Acesse o sistema para visualizar as solicitações:</h5>" + 
				"			<a href=\"http://esic.se.gov.br\">http://esic.se.gov.br</a>";
				
				try {
					enviarEmailHTML(destinatario, titulo, msg);
				} catch (EmailException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	}
	
	public static void enviarEmailNovaSolicitacaoCidadao(Solicitacao solicitacao, Usuario usuario) {
		String[] envio = destinatarioEmail(usuario, solicitacao);
		String destinatario = envio[0];
		String titulo = "esic-SE - Manifestação realizada com sucesso";
		
		String tipoSol;
		if (solicitacao.getTipo().equals("Informação")) tipoSol = "Seu Pedido de Informação foi realizado com sucesso!";
		else if (solicitacao.getTipo().equals("Elogio")) tipoSol = "Seu Elogio foi realizado com sucesso!";
		else tipoSol = "Sua " + solicitacao.getTipo() + " foi realizada com sucesso!";
		
		String msg = "<p style=\"font-size: 30px; margin-top: 0\">Olá, " + usuario.getNome() + "</p>" + 
				"			<h4>" + tipoSol + "</h4>" + 
				"			<table>" + 
				"				<tr>" + 
				"				<td><h4 style=\"margin: 0\">Protocolo: </h4></td>" + 
				"				<td><p style=\"margin: 0\">" + solicitacao.getProtocolo() + "</p></td>" + 
				"				</tr>" + 
				"			</table>" + 
				"			<table>" + 
				"				<tr>" + 
				"				<td><h4 style=\"margin: 0\">Órgão: </h4></td>" + 
				"				<td><p style=\"margin: 0\">" + solicitacao.getEntidades().getSigla() + " - " + solicitacao.getEntidades().getNome() + "</p></td>" + 
				"				</tr>" + 
				"			</table>" + 
				"			<table>" + 
				"				<tr>" + 
				"				<td><h4 style=\"margin: 0\">Título: </h4></td>" + 
				"				<td><p style=\"margin: 0\">" + solicitacao.getTitulo() + "</p></td>" + 
				"				</tr>" + 
				"			</table>" + 
				"			<table>" + 
				"				<tr>" + 
				"				<td><h4 style=\"margin: 0\">Data de envio: </h4></td>" + 
				"				<td><p style=\"margin: 0\">" + DateFormat.getDateInstance(DateFormat.SHORT, Locale.CANADA).format(solicitacao.getDataIni()) + "</p></td>" + 
				"				</tr>" + 
				"			</table>" +
				"			<table>" +
				"			<table>" + 
				"				<tr>" + 
				"				<td><h4 style=\"margin: 0\">Data limite: </h4></td>" + 
				"				<td><p style=\"margin: 0\">" + DateFormat.getDateInstance(DateFormat.SHORT, Locale.CANADA).format(solicitacao.getDataLimite()) + "</p></td>" + 
				"				</tr>" + 
				"			</table>" + 
				"			<h5>Por favor, aguarde a resposta do órgão competente dentro do prazo estipulado</h5>" + 
				"			<h5 style=\"margin-bottom: 0\">Acesse o sistema para visualizar suas solicitações:</h5>" + 
				"			<a href=\"http://esic.se.gov.br\">http://esic.se.gov.br</a>";
				
				try {
					enviarEmailHTML(destinatario, titulo, msg);
				} catch (EmailException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	}
	
	public static void enviarEmailNovaSolicitacaoResp(Solicitacao solicitacao) {
		ArrayList<String> destinatarios = destinatarioResp(solicitacao.getEntidades());
		
		String titulo = "esic-SE - Nova manifestação para " + solicitacao.getEntidades().getSigla();
		
		String tipoSol1;
		if (solicitacao.getTipo().equals("Informação")) tipoSol1 = "Pedido de Informação";
		else tipoSol1 = solicitacao.getTipo();
		
		String msg = "			<h4> Nova manifestação realizada para " + solicitacao.getEntidades().getSigla() + " - " + solicitacao.getEntidades().getNome() + "</h4>" + 
				"			<table>" + 
				"				<tr>" + 
				"				<td><h4 style=\"margin: 0\">Protocolo: </h4></td>" + 
				"				<td><p style=\"margin: 0\">" + solicitacao.getProtocolo() + "</p></td>" + 
				"				</tr>" + 
				"			</table>" +
				"			<table>" + 
				"				<tr>" + 
				"				<td><h4 style=\"margin: 0\">Tipo: </h4></td>" + 
				"				<td><p style=\"margin: 0\">" + tipoSol1 + "</p></td>" + 
				"				</tr>" + 
				"			</table>" +
				"			<table>" + 
				"				<tr>" + 
				"				<td><h4 style=\"margin: 0\">Órgão: </h4></td>" + 
				"				<td><p style=\"margin: 0\">" + solicitacao.getEntidades().getSigla() + " - " + solicitacao.getEntidades().getNome() + "</p></td>" + 
				"				</tr>" + 
				"			</table>" +
				"			<table>" + 
				"				<tr>" + 
				"				<td><h4 style=\"margin: 0\">Título: </h4></td>" + 
				"				<td><p style=\"margin: 0\">" + solicitacao.getTitulo() + "</p></td>" + 
				"				</tr>" + 
				"			</table>" +
				"			<table>" +
				"				<tr>" + 
				"				<td><h4 style=\"margin: 0\">Manifestante: </h4></td>" + 
				"				<td><p style=\"margin: 0\">" + solicitacao.getCidadao().getUsuario().getNome() + "</p></td>" + 
				"				</tr>" + 
				"			</table>" +
				"			<h5 style=\"margin-bottom: 0\">Acesse o sistema para visualizar as solicitações:</h5>" + 
				"			<a href=\"http://esic.se.gov.br\">http://esic.se.gov.br</a>";
				
				try {
					for (int i = 0; i < destinatarios.size(); i++) {
						enviarEmailHTML(destinatarios.get(i), titulo, msg);
					}
				} catch (EmailException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	}

	public static void enviarEmailNotificacaoCidadao(Solicitacao solicitacao, Mensagem mensagem) {
		String destinatario = solicitacao.getCidadao().getEmail();
		String titulo = "esic-SE - Atualização na manifestação "+ solicitacao.getProtocolo();
		
		String msg = 	"<h4>Nova atualização na manifestação " + solicitacao.getProtocolo() + " - " + solicitacao.getTitulo() + ".</h4>" +
						"<table>" +
						"	<tr>" +
						"	<td><h4 style=\"margin: 0\">Protocolo: </h4></td>" +
						"	<td><p style=\"margin: 0\">" + solicitacao.getProtocolo() + "</p></td>" +
						"	</tr>" +
						"</table>" +
						"<table>" +
						"	<tr>" +
						"	<td><h4 style=\"margin: 0\">Título: </h4></td>" +
						"	<td><p style=\"margin: 0\">" + solicitacao.getTitulo() + "</p></td>" +
						"	</tr>" +
						"</table>" +
						"<table>" +
						"	<tr>" +
						"	<td><h4 style=\"margin: 0\">Órgão: </h4></td>" +
						"	<td><p style=\"margin: 0\">" + solicitacao.getEntidades().getSigla() + " - "  + solicitacao.getEntidades().getNome() + "</p></td>" +
						"	</tr>" +
						"</table>" +
						"<br/>" +
						"<table>" +
						"	<tr>" +
						"	<td><h4 style=\"margin: 0\">Atualização: </h4></td>" +
						"	<td><p style=\"margin: 0\">" + solicitacao.getStatus() + "</p></td>" +
						"	</tr>" +
						"</table>" +
						"<table>" +
						"	<tr>" +
						"	<td><h4 style=\"margin: 0\">Data: </h4></td>" +
						"	<td><p style=\"margin: 0\">" + DateFormat.getDateInstance(DateFormat.SHORT, Locale.CANADA).format(mensagem.getData()) + "</p></td>" +
						"	</tr>" +
						"</table>" +
						"<table>" +
						"	<tr>" +
						"	<td><h4 style=\"margin: 0\"> Mensagem: </h4></td>" +
						"	</tr>" +
						"	<tr>" +
						"	<td><p style=\"margin: 0\">\" " + mensagem.getTexto() + " \"</p></td>" +
						"	</tr>" +
						"</table>" +
						"<h5 style=\"margin-bottom: 0\">Acesse o sistema para visualização completa:</h5>" +
						"<a href=\"http://esic.se.gov.br\">http://esic.se.gov.br</a>";

		try {
			enviarEmailHTML(destinatario, titulo, msg);
		} catch (EmailException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void enviarEmailNotificacaoRecurso(Solicitacao solicitacao) {
		ArrayList<String> destinatarios = destinatarioResp(solicitacao.getEntidades());
		
		String titulo = "esic-SE - Manifestação para " + solicitacao.getEntidades().getSigla() + " entrou em recurso.";
		
		String tipoSol1;
		if (solicitacao.getTipo().equals("Informação")) tipoSol1 = "Pedido de Informação";
		else tipoSol1 = solicitacao.getTipo();
		
		String msg = "			<h4> Manifestação realizada para " + solicitacao.getEntidades().getSigla() + " - " + solicitacao.getEntidades().getNome() + " entrou em recuso.</h4>" + 
				"			<table>" + 
				"				<tr>" + 
				"				<td><h4 style=\"margin: 0\">Protocolo: </h4></td>" + 
				"				<td><p style=\"margin: 0\">" + solicitacao.getProtocolo() + "</p></td>" + 
				"				</tr>" + 
				"			</table>" +
				"			<table>" + 
				"				<tr>" + 
				"				<td><h4 style=\"margin: 0\">Tipo: </h4></td>" + 
				"				<td><p style=\"margin: 0\">" + tipoSol1 + "</p></td>" + 
				"				</tr>" + 
				"			</table>" +
				"			<table>" + 
				"				<tr>" + 
				"				<td><h4 style=\"margin: 0\">Órgão: </h4></td>" + 
				"				<td><p style=\"margin: 0\">" + solicitacao.getEntidades().getSigla() + " - " + solicitacao.getEntidades().getNome() + "</p></td>" + 
				"				</tr>" + 
				"			</table>" + 
				"			<table>" + 
				"				<tr>" + 
				"				<td><h4 style=\"margin: 0\">Data de envio: </h4></td>" + 
				"				<td><p style=\"margin: 0\">" + DateFormat.getDateInstance(DateFormat.SHORT, Locale.CANADA).format(solicitacao.getDataIni()) + "</p></td>" + 
				"				</tr>" + 
				"			</table>" + 
				"			<table>" + 
				"				<tr>" + 
				"				<td><h4 style=\"margin: 0\">Data limite: </h4></td>" + 
				"				<td><p style=\"margin: 0\">" + DateFormat.getDateInstance(DateFormat.SHORT, Locale.CANADA).format(solicitacao.getDataLimite()) + "</p></td>" + 
				"				</tr>" + 
				"			</table>" +
				"			<table>" + 
				"				<tr>" + 
				"				<td><h4 style=\"margin: 0\">Manifestante: </h4></td>" + 
				"				<td><p style=\"margin: 0\">" + solicitacao.getCidadao().getUsuario().getNome() + "</p></td>" + 
				"				</tr>" + 
				"			</table>" +
				"			<h5 style=\"margin-bottom: 0\">Acesse o sistema para visualizar as solicitações:</h5>" + 
				"			<a href=\"http://esic.se.gov.br\">http://esic.se.gov.br</a>";
				
				try {
					for (int i = 0; i < destinatarios.size(); i++) {
						enviarEmailHTML(destinatarios.get(i), titulo, msg);
					}
				} catch (EmailException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	}

	public static void enviarEmailRequisicaoResponsavel(Usuario usuario, Entidades entidade, int idUser, int idEntidade, String emailUser, String hashcode, String destinatario) {
		String link = DadosAutenticacao.getEnderecohomologacao()+"/Cadastro/cad_responsavel.xhtml?access-key="+hashcode.substring(0, hashcode.length()/2)
																+"&user="+idUser
																+"&identidade="+idEntidade
																+"&"+hashcode.substring(hashcode.length()/2, hashcode.length())
																+"&mail="+emailUser;
		String titulo = "esic-SE - Nova requisição de responsável";
		
		String msg = "<h4>Foi solicitada uma nova requisição de cadastro como responsável por:</h4>" +
					"	<table>" +
					"		<tr>" +
					"		<td><h4 style=\"margin: 0\">Nome: </h4></td>" +
					"		<td><p style=\"margin: 0\">" + usuario.getNome() + "</p></td>" +
					"		</tr>" +
					"	</table>" +
					"	<table>" +
					"		<tr>" +
					"		<td><h4 style=\"margin: 0\">Nick: </h4></td>" +
					"		<td><p style=\"margin: 0\">" + usuario.getNick() + "</p></td>" +
					"		</tr>" +
					"	</table>" +
					"	<table>" +
					"		<tr>" +
							"<td><h4 style=\"margin: 0\">E-mail: </h4></td>" +
							"<td><p style=\"margin: 0\">" + emailUser + "</p></td>" +
							"</tr>" +
						"</table>" +
						"<table>" +
							"<tr>" +
							"<td><h4 style=\"margin: 0\">Órgão: </h4></td>" +
							"<td><p style=\"margin: 0\">" + entidade.getSigla() + " - " + entidade.getNome() + "</p></td>" +
							"</tr>" +
						"</table>" +
						"<h5 style=\"margin-bottom: 50px\">Aperte o botão abaixo para prosseguir com cadastro de responsável.</h5>" +
						"<table>" +
							"<tr>" +
								"<a href=\"" + link + "\" style=\"color: white; border-radius: 3px; border: 1px solid #1d4a74; padding: 10px; font-size: 20px; background-color: #296099; background: linear-gradient(to bottom right, #135186, #4087dc text-decoration: none;\">" +
								"Cadastrar" +
								"</a>" +
							"</tr>" +
						"</table>" +
						"<h5 style=\"margin-top: 50px\">Caso não deseje cadastrá-lo, ignore esta mensagem.</h5>";
		
		try {
			enviarEmailHTML(destinatario, titulo, msg);
		} catch (EmailException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void enviarEmailRedefinicaoSenha(String hashcodeUser, String destinatario, String nomeUser) {
		String link = DadosAutenticacao.getEnderecohomologacao()+"Alterar/redefinir_senha.xhtml?access_key="+hashcodeUser;
		String titulo = "esic-SE - Redefinição de senha";
		
		String msg = "<h4 style=\"margin-bottom:50px\">Se você esqueceu sua senha ou deseja redefini-la, utilize o botão abaixo para fazê-lo.</p>" + 
				"	<table align=\"center\">" + 
				"		<tr align=\"center\">" + 
				"		<td>" + 
				"			<a href=\"" + link + "\" style=\"color: white; border-radius: 3px; border: 1px solid #1d4a74; padding: 10px; font-size: 20px; background-color: #296099; background: linear-gradient(to bottom right, #135186, #4087dc); text-decoration: none;\">" + 
				"			Nova Senha" + 
				"			</a>" + 
				"		</td>" + 
				"		</tr>" + 
				"	</table>" + 
				"<h5 style=\"margin-bottom: 0; margin-top: 50px; font-weight: normal\">Caso o botão não funcione, clique ou copie o link a seguir no navegador:</h5>" + 
				"<a href=\"" + link + "\">" + link + "</a>" + 
				"<h5 style=\"margin-bottom: 0; font-weight: normal\">Se você não requisitou a mudança de senha, ignore este e-mail. Apenas uma pessoa com acesso ao seu e-mail pode redefinir sua senha.</h5>";
		
		try {
			enviarEmailHTML(destinatario, titulo, msg);
		} catch (EmailException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void enviarEmailPrazo(Solicitacao solicitacao, String mensagem) {
		ArrayList<String> destinatarios = destinatarioResp(solicitacao.getEntidades());
				
		String titulo = "esic-SE - Notificação de prazo da manifestação " + solicitacao.getProtocolo();
		
		String tipoSol1;
		if (solicitacao.getTipo().equals("Informação")) tipoSol1 = "Pedido de Informação";
		else tipoSol1 = solicitacao.getTipo();
		
		String msg = "			<h4>" + mensagem + "</h4>" + 
				"			<table>" + 
				"				<tr>" + 
				"				<td><h4 style=\"margin: 0\">Protocolo: </h4></td>" + 
				"				<td><p style=\"margin: 0\">" + solicitacao.getProtocolo() + "</p></td>" + 
				"				</tr>" + 
				"			</table>" +
				"			<table>" + 
				"				<tr>" + 
				"				<td><h4 style=\"margin: 0\">Tipo: </h4></td>" + 
				"				<td><p style=\"margin: 0\">" + tipoSol1 + "</p></td>" + 
				"				</tr>" + 
				"			</table>" +
				"			<table>" + 
				"				<tr>" + 
				"				<td><h4 style=\"margin: 0\">Órgão: </h4></td>" + 
				"				<td><p style=\"margin: 0\">" + solicitacao.getEntidades().getSigla() + " - " + solicitacao.getEntidades().getNome() + "</p></td>" + 
				"				</tr>" + 
				"			</table>" +
				"			<table>" + 
				"				<tr>" + 
				"				<td><h4 style=\"margin: 0\">Título: </h4></td>" + 
				"				<td><p style=\"margin: 0\">" + solicitacao.getTitulo() + "</p></td>" + 
				"				</tr>" + 
				"			</table>" +
				"			<table>" +
				"				<tr>" + 
				"				<td><h4 style=\"margin: 0\">Manifestante: </h4></td>" + 
				"				<td><p style=\"margin: 0\">" + solicitacao.getCidadao().getUsuario().getNome() + "</p></td>" + 
				"				</tr>" + 
				"			</table>" +
					"			<h5 style=\"margin-bottom: 0\">Acesse o sistema para visualizar as solicitações:</h5>" + 
					"			<a href=\"http://esic.se.gov.br\">http://esic.se.gov.br</a>";
		
		try {
			for (int i = 0; i < destinatarios.size(); i++) {
				enviarEmailHTML(destinatarios.get(i), titulo, msg);
			}
		} catch (EmailException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	public static void enviarEmailCadastroCid (Cidadao cidadao) {
		String destinatario = cidadao.getEmail();
		
		String titulo = "esic-SE - Bem vindo ao esic-SE";
		
		String msg = 	"<p style=\"font-size: 30px; margin-top: 0\">Olá, " + cidadao.getUsuario().getNome() + "</p>" + 
						"			<h4>Bem vindo ao esic-SE</h4>" + 
						"			<br/>" +
						"			<h3>Lembre-se do seu nome de usuário (" + cidadao.getUsuario().getNick() + ") e senha para acessar o sistema.</h3>" +
						"			<h3 style=\"margin:0\">Acesse o sistema para realizar e visualizar manifestações:</h3>" + 
						"			<a style=\"font-size: 15px\" href=\"http://esic.se.gov.br\">http://esic.se.gov.br</a>";
		
		try {
			enviarEmailHTML(destinatario, titulo, msg);
		} catch (EmailException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void enviarEmailCadastroResp (Responsavel responsavel) {
		String destinatario = responsavel.getEmail();
		
		String titulo = "esic-SE - Cadastro como responsável";
		
		String msg = 	"<p style=\"font-size: 30px; margin-top: 0\">Olá, " + responsavel.getUsuario().getNome() + "</p>" + 
				"			<h2>Você foi cadastrado no sistema como responsável do órgão " + responsavel.getEntidades().getSigla() + " - " + responsavel.getEntidades().getNome() + ".</h2>" + 
				"			<h3>Lembre-se do seu nome de usuário (" + responsavel.getUsuario().getNick() + ") e senha para acessar o sistema.</h3>" +
				"			<h3 style=\"margin:0\">Acesse o sistema para visualizar e responder manifestações:</h3>" + 
				"			<a style=\"font-size: 15px\" href=\"http://esic.se.gov.br\">http://esic.se.gov.br</a>";
		
		try {
			enviarEmailHTML(destinatario, titulo, msg);
		} catch (EmailException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	//+++++++++++++++++++ Email HTML Template
	public static void enviarEmailHTML(String destinatario, String titulo, String mensagem) throws EmailException {
		
		// Create the email message
		ImageHtmlEmail emailHtml = new ImageHtmlEmail();
		emailHtml.setDebug(true);
		emailHtml.setHostName(DadosAutenticacao.getHostNameEmail());  
		emailHtml.setAuthentication(DadosAutenticacao.getUserLoginEmailAuthentication(),DadosAutenticacao.getSenhaUserLoginEmailAuthentication());  
		emailHtml.addTo(destinatario);
		emailHtml.setFrom("no_reply@cge.se.gov.br"); //será passado o email que você fará a autenticação
		emailHtml.setSubject(titulo);
		
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
		
		msg.append(mensagem);
		
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
		
		// set the html message
		emailHtml.setHtmlMsg(msg.toString());
		
		// set the alternative message
		emailHtml.setTextMsg("Seu provedor de email não suporta este tipo de mensagens.");
		
		// send the email
		emailHtml.send();
	}
	
	//+++++++++++++++++++ Email simples
		public static void enviarEmail(String remetente, String destinatario, String titulo, String mensagem) throws EmailException {
			Email email = new SimpleEmail();
			
			email.setDebug(true);  
			email.setHostName(DadosAutenticacao.getHostNameEmail());  
			email.setAuthentication(DadosAutenticacao.getUserLoginEmailAuthentication(),DadosAutenticacao.getSenhaUserLoginEmailAuthentication());  
			email.addTo(destinatario); //pode ser qualquer email  
			email.setFrom(remetente);//será passado o email que você fará a autenticação 
			email.setSubject(titulo);  
			email.setMsg(mensagem);  // só para teste  
			email.send();  
			
		}

	//+++++++++++++++++++ Destinatário do email
		public static String[] destinatarioEmail(Usuario usuario, Solicitacao solicitacao) {
			String[] envio = new String[3];
			if(usuario.getPerfil() == 3 || (usuario.getPerfil() == 4 && UsuarioBean.perfilAlterarCidadaoResponsavel == false) ) {
				List<Cidadao> listCidadao = new ArrayList<Cidadao>(usuario.getCidadaos());	
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
		
	//+++++++++++++++++++ Destinatário todos responsáveis
		public static ArrayList<String> destinatarioResp(Entidades entidade) {
			List<Responsavel> listResp = ResponsavelDAO.findResponsavelEntidade(entidade.getIdEntidades());
			ArrayList<String> destinatarios = new ArrayList<String>();
			
			for (int i = 0; i < listResp.size(); i++) {
				if (listResp.get(i).getNivel().shortValue() == 1) {
					destinatarios.add(listResp.get(i).getEmail());
				}
			}
			
			return destinatarios;
		}
}
