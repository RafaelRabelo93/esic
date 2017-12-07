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
		for (Solicitacao solicitacao : SolicitacaoDAO.listar()) {
			try {
				updateStatusSolicitacao(solicitacao);
			} catch (NullPointerException e) {
				System.out.println(e.getMessage());
			}
		}

	}
	
	@SuppressWarnings({ "unused", "unlikely-arg-type" })
	private void verificaTempoSolicitacao(Solicitacao solicitacao) {
//		System.out.println("Entrou no metodo verificaTempoSolicitacao às" + new Date());
		
		if (!solicitacao.getStatus().equals("Finalizada")) {

			Date now = new Date();

			//Datas dos prazos para comparações
			LocalDate lcSolicitacaoInicio = LocalDate.of(solicitacao.getDataIni().getYear(), solicitacao.getDataIni().getMonth(), solicitacao.getDataIni().getDay());
			LocalDate metadePrazo = lcSolicitacaoInicio.plusDays((SolicitacaoBean.prazoResposta(solicitacao.getStatus()))/2);
			LocalDate vesperaPrazo = lcSolicitacaoInicio.plusDays((SolicitacaoBean.prazoResposta(solicitacao.getStatus()))-1);
			
			String tituloMensagem = "Notificação de prazo da solicitacao "+ solicitacao.getIdSolicitacao();
			
			if (now.equals(metadePrazo)) {
//				System.out.println("Metade do prazo para resposta");
				String corpoMensagem = "Faltam "+metadePrazo+" dias para a solicitacao "+ solicitacao.getIdSolicitacao()+" expirar.";
				NotificacaoEmail.enviarEmailAutomatico(solicitacao, tituloMensagem, corpoMensagem);
			}else {
				if(now.equals(vesperaPrazo)) {
//					System.out.println("Véspera do prazo para resposta");
					String corpoMensagem = "Falta 1 dia para a solicitacao "+ solicitacao.getIdSolicitacao()+" expirar.";
					NotificacaoEmail.enviarEmailAutomatico(solicitacao, tituloMensagem, corpoMensagem);
				}
			}
		}		
	} 
	public void updateStatusSolicitacao(Solicitacao solicitacao) {
//		System.out.println("Entrou no metodo finalizaSolicitacao às " + new Date());

		if (!solicitacao.getStatus().equals("Finalizada")) {
		
			Date now = new Date();
			if (now.after(solicitacao.getDataLimite())) {
				System.out.println("Finalizou");
			if ((solicitacao.getStatus().equals("Respondida") 
					||( solicitacao.getStatus().equals("Recurso") && solicitacao.getInstancia().equals((short)2)) ) 
					&& now.after(solicitacao.getDataLimite()))
			{
//				System.out.println("Finalizou");
			if (now.after(solicitacao.getDataLimite())) {
				System.out.println("Finalizou");
				solicitacao.setDatafim(new Date(System.currentTimeMillis()));
				solicitacao.setStatus("Finalizada");
				SolicitacaoDAO.saveOrUpdate(solicitacao);
				MensagemBean.salvarStatus(solicitacao, solicitacao.getStatus());
				
			}else {
				if((solicitacao.getStatus().equals("Aberta") 
						|| solicitacao.getStatus().equals("Prorrogada") 
						||( solicitacao.getStatus().equals("Recurso") && solicitacao.getInstancia() < (short)2))
						&& now.after(solicitacao.getDataLimite())) 
				{
//					System.out.println("Resposta negada no sistema");
					solicitacao.setStatus("Respondida");
					solicitacao.setDataLimite(java.sql.Date.valueOf(Instant.ofEpochMilli(solicitacao.getDataLimite().getTime()).atZone(ZoneId.systemDefault()).toLocalDate().plusDays(SolicitacaoBean.prazoResposta(solicitacao.getStatus()))));
					SolicitacaoDAO.saveOrUpdate(solicitacao);
					MensagemBean.salvarStatus(solicitacao, "Negada");
	
				}
			}
		}		
	}
	
	
	}	
}
}
