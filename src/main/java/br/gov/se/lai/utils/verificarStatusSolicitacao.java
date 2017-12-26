package br.gov.se.lai.utils;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import javax.faces.context.FacesContext;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import br.gov.se.lai.Bean.MensagemBean;
import br.gov.se.lai.Bean.SolicitacaoBean;
import br.gov.se.lai.DAO.SolicitacaoDAO;
import br.gov.se.lai.entity.Solicitacao;
 

public class verificarStatusSolicitacao implements Job {

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
//		System.out.println("Entrou em verificacoes");
		for (Solicitacao solicitacao : SolicitacaoDAO.listarGeral()) {
			try {
				updatePrazoTipo(solicitacao);
				updateStatusSolicitacao(solicitacao);
				verificaTempoSolicitacao(solicitacao);
			} catch (NullPointerException e) {
				System.out.println(e.getMessage());
			}
		}

	}
	
	@SuppressWarnings({ "unused", "unlikely-arg-type" })
	private void verificaTempoSolicitacao(Solicitacao solicitacao) {
		
		if (!(solicitacao.getStatus().equals("Finalizada") || solicitacao.getStatus().equals("Respondida"))) {

			LocalDate now = LocalDate.now();

			int prazo =SolicitacaoBean.prazoResposta(solicitacao.getStatus());
			//Datas dos prazos para comparações
			//LocalDate lcSolicitacaoInicio = solicitacao.getDataIni().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			LocalDate inicioPrazo = solicitacao.getDataIni().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().plusDays(5);
			LocalDate metadePrazo = solicitacao.getDataLimite().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().minusDays((prazo)/2);
			LocalDate vesperaPrazo = solicitacao.getDataLimite().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().minusDays(1);
			LocalDate vesperaPrazoGestor = solicitacao.getDataLimite().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().minusDays(5);
			
			String tituloMensagem = "Notificação de prazo da solicitacao "+ solicitacao.getIdSolicitacao();
			
			
			if(now.isEqual(inicioPrazo)) {
				String corpoMensagem = "Solicitacao "+ solicitacao.getProtocolo()+" completou 5 dias.";
				NotificacaoEmail.enviarEmailAutomatico(solicitacao, tituloMensagem, corpoMensagem);
			}else if (now.isEqual(metadePrazo)) {
				String corpoMensagem = "Faltam "+(prazo/2)+" dias para a solicitacao "+ solicitacao.getProtocolo()+" expirar.";
				NotificacaoEmail.enviarEmailAutoridades(solicitacao, tituloMensagem, corpoMensagem);
			}else if(now.isEqual(vesperaPrazoGestor)) {
				String corpoMensagem = "Solicitação ainda não respondida.\nFaltam 5 dias para a solicitacao "+ solicitacao.getProtocolo()+" expirar.";
				NotificacaoEmail.enviarEmailAutoridades(solicitacao, tituloMensagem, corpoMensagem);
			}else if(now.isEqual(vesperaPrazo)) {
				String corpoMensagem = "Falta 1 dia para a solicitacao "+ solicitacao.getProtocolo()+" expirar.";
				NotificacaoEmail.enviarEmailAutoridades(solicitacao, tituloMensagem, corpoMensagem);
			}
		}		
	} 
	
	public void updateStatusSolicitacao(Solicitacao solicitacao) {

		if (!solicitacao.getStatus().equals("Finalizada")) {
		
			Date now = new Date();
			if (now.after(solicitacao.getDataLimite())) {
				// System.out.println("Finalizou");
				if ((solicitacao.getStatus().equals("Respondida")
						|| (solicitacao.getStatus().equals("Recurso") && solicitacao.getInstancia().equals((short) 2)))
						&& now.after(solicitacao.getDataLimite())) {
	
						System.out.println("Finalizou");
						solicitacao.setDatafim(new Date(System.currentTimeMillis()));
						solicitacao.setStatus("Finalizada");
						SolicitacaoDAO.saveOrUpdate(solicitacao);
						MensagemBean.salvarStatus(solicitacao, solicitacao.getStatus());

					} else {
						if ((solicitacao.getStatus().equals("Aberta") || solicitacao.getStatus().equals("Prorrogada")
								|| (solicitacao.getStatus().equals("Recurso")
										&& solicitacao.getInstancia() < (short) 2))
								&& now.after(solicitacao.getDataLimite())) {
							solicitacao.setStatus("Respondida");
							solicitacao.setDataLimite(java.sql.Date.valueOf(LocalDate.now().plusDays(SolicitacaoBean.prazoResposta(solicitacao.getStatus()))));
							if(SolicitacaoDAO.saveOrUpdate(solicitacao)) {
								MensagemBean.salvarStatus(solicitacao, "Negada");
							}

						}
					}
				}
			}
	
	}
	
	public static void updatePrazoTipo(Solicitacao solicitacao) {
		Date diaAtual = new Date(System.currentTimeMillis());
		short prazoTipo;
		if (solicitacao.getStatus().equals("Finalizada")) {
			// Altera para branco se a solicitação for Finalizada
			prazoTipo = 0;
		} else if (java.sql.Date.valueOf(Instant.ofEpochMilli(solicitacao.getDataLimite().getTime()).atZone(ZoneId.systemDefault()).toLocalDate().minusDays(10)).after(diaAtual)) {
			// Altera para verde se prazo for maior que 10 dias
			prazoTipo = 3;
		} else if (java.sql.Date.valueOf(Instant.ofEpochMilli(solicitacao.getDataLimite().getTime()).atZone(ZoneId.systemDefault()).toLocalDate().minusDays(2)).after(diaAtual) & java.sql.Date.valueOf(Instant.ofEpochMilli(solicitacao.getDataLimite().getTime()).atZone(ZoneId.systemDefault()).toLocalDate().minusDays(11)).before(diaAtual)) {
			// Altera para amarel se prazo for menor que 10 dias e maior que 2 dias
			prazoTipo = 2;
		} else if (java.sql.Date.valueOf(Instant.ofEpochMilli(solicitacao.getDataLimite().getTime()).atZone(ZoneId.systemDefault()).toLocalDate().minusDays(3)).before(diaAtual)) {
			// Altera para vermelh se prazo for maior que 2 dias
			prazoTipo = 1;
		} else prazoTipo = 0;
		
		if (solicitacao.getPrazoTipo() != prazoTipo) {
			solicitacao.setPrazoTipo(prazoTipo);
			SolicitacaoDAO.saveOrUpdate(solicitacao);
		}
	}
}

