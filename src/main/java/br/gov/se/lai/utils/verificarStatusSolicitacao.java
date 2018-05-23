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
		System.out.println("Entrou em verificacoes");
		for (Solicitacao solicitacao : SolicitacaoDAO.listPorNaoFinalizada()) {
			try {
				updateStatusSolicitacao(solicitacao);
				verificaTempoSolicitacao(solicitacao);
			} catch (NullPointerException e) {
				System.out.println(e.getMessage());
			}
		}

	}
	
	//Para o servidor-homologação
	@SuppressWarnings({ "unused", "unlikely-arg-type" })
	private void verificaTempoSolicitacao(Solicitacao solicitacao) {
		
		if (!(solicitacao.getStatus().equals("Finalizada") || solicitacao.getStatus().equals("Respondida"))) {

			LocalDate now = LocalDate.now();

			int prazo =PrazosSolicitacao.prazoResposta(solicitacao.getStatus());
			//Datas dos prazos para comparações
			//LocalDate lcSolicitacaoInicio = solicitacao.getDataIni().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			LocalDate inicioPrazo = solicitacao.getDataIni().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().plusDays(1);
			LocalDate metadePrazo = solicitacao.getDataLimite().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().minusDays((prazo)/2);
			LocalDate vesperaPrazo = solicitacao.getDataLimite().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().minusDays(1);
			LocalDate vesperaPrazoGestor = solicitacao.getDataLimite().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().minusDays(2);
			
//			String tituloMensagem = "Notificação de prazo da solicitacao "+ solicitacao.getIdSolicitacao();
			
			
			if(now.isEqual(inicioPrazo)) {
				String mensagem = "Solicitacao "+ solicitacao.getProtocolo()+" completou 5 dias.";
				NotificacaoEmail.enviarEmailPrazo(solicitacao, mensagem);
			}else if (now.isEqual(metadePrazo)) {
				String mensagem = "Faltam "+(prazo/2)+" dias para a solicitacao "+ solicitacao.getProtocolo()+" expirar.";
				NotificacaoEmail.enviarEmailPrazo(solicitacao, mensagem);
			}else if(now.isEqual(vesperaPrazoGestor)) {
				String mensagem = "Solicitação ainda não respondida.\nFaltam 5 dias para a solicitacao "+ solicitacao.getProtocolo()+" expirar.";
				NotificacaoEmail.enviarEmailPrazo(solicitacao, mensagem);
			}else if(now.isEqual(vesperaPrazo)) {
				String mensagem = "Falta 1 dia para a solicitacao "+ solicitacao.getProtocolo()+" expirar.";
				NotificacaoEmail.enviarEmailPrazo(solicitacao, mensagem);
			}
		}		
	} 
	
//	@SuppressWarnings({ "unused", "unlikely-arg-type" })
//	private void verificaTempoSolicitacao(Solicitacao solicitacao) {
//		
//		if (!(solicitacao.getStatus().equals("Finalizada") || solicitacao.getStatus().equals("Respondida"))) {
//			
//			LocalDate now = LocalDate.now();
//			
//			int prazo =SolicitacaoBean.prazoResposta(solicitacao.getStatus());
//			//Datas dos prazos para comparações
//			//LocalDate lcSolicitacaoInicio = solicitacao.getDataIni().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//			LocalDate inicioPrazo = solicitacao.getDataIni().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().plusDays(5);
//			LocalDate metadePrazo = solicitacao.getDataLimite().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().minusDays((prazo)/2);
//			LocalDate vesperaPrazo = solicitacao.getDataLimite().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().minusDays(1);
//			LocalDate vesperaPrazoGestor = solicitacao.getDataLimite().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().minusDays(5);
//			
//			String tituloMensagem = "Notificação de prazo da solicitacao "+ solicitacao.getIdSolicitacao();
//			
//			
//			if(now.isEqual(inicioPrazo)) {
//				String corpoMensagem = "Solicitacao "+ solicitacao.getProtocolo()+" completou 5 dias.";
//				NotificacaoEmail.enviarEmailAutomatico(solicitacao, tituloMensagem, corpoMensagem);
//			}else if (now.isEqual(metadePrazo)) {
//				String corpoMensagem = "Faltam "+(prazo/2)+" dias para a solicitacao "+ solicitacao.getProtocolo()+" expirar.";
//				NotificacaoEmail.enviarEmailAutoridades(solicitacao, tituloMensagem, corpoMensagem);
//			}else if(now.isEqual(vesperaPrazoGestor)) {
//				String corpoMensagem = "Solicitação ainda não respondida.\nFaltam 5 dias para a solicitacao "+ solicitacao.getProtocolo()+" expirar.";
//				NotificacaoEmail.enviarEmailAutoridades(solicitacao, tituloMensagem, corpoMensagem);
//			}else if(now.isEqual(vesperaPrazo)) {
//				String corpoMensagem = "Falta 1 dia para a solicitacao "+ solicitacao.getProtocolo()+" expirar.";
//				NotificacaoEmail.enviarEmailAutoridades(solicitacao, tituloMensagem, corpoMensagem);
//			}
//		}		
//	} 
//	
	public void updateStatusSolicitacao(Solicitacao solicitacao) {

		if (!solicitacao.getStatus().equals("Finalizada")) {
		
			Date now = new Date();
			if (now.after(solicitacao.getDataLimite())) {
				 System.out.println("Finalizou");
				if ((solicitacao.getStatus().equals("Respondida")
						|| solicitacao.getStatus().equals("Negada")
						|| ((solicitacao.getStatus().equals("Recurso") && solicitacao.getInstancia().equals((short) 3))))) {
	
						System.out.println("Finalizou");
						solicitacao.setDatafim(new Date(System.currentTimeMillis()));
						solicitacao.setStatus("Finalizada");
						SolicitacaoDAO.saveOrUpdate(solicitacao);
						MensagemBean.salvarStatus(solicitacao, solicitacao.getStatus(), null, null);

					} else {
						if ((solicitacao.getStatus().equals("Aberta") ||  solicitacao.getStatus().equals("Prorrogada")
								|| (solicitacao.getStatus().equals("Recurso") && solicitacao.getInstancia() < (short) 3)
								|| solicitacao.getStatus().equals("Reencaminhada"))) {
							solicitacao.setStatus("Negada");
							solicitacao.setDataLimite(PrazosSolicitacao.diaUtilDataLimite(solicitacao.getStatus(), solicitacao.getDataLimite()));
							SolicitacaoDAO.saveOrUpdate(solicitacao);
							MensagemBean.salvarStatus(solicitacao, solicitacao.getStatus(), null, null);

						}
					}
				}
			}
	
	}
	

}

