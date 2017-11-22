package br.gov.se.lai.utils;
import java.time.LocalDate;
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
		for (Solicitacao solicitacao : SolicitacaoDAO.listar()) {
			try {
				finalizaSolicitacao(solicitacao);
			} catch (NullPointerException e) {
				System.out.println(e.getMessage());
			}
		}

	}
	
	@SuppressWarnings({ "unused", "unlikely-arg-type" })
	private void verificaTempoSolicitacao(Solicitacao solicitacao) {
		System.out.println("Entrou no metodo verificaTempoSolicitacao �s" + new Date());
		
		if (!solicitacao.getStatus().equals("Finalizada")) {

			Date now = new Date();
			
			LocalDate lcSolicitacaoInicio = LocalDate.of(solicitacao.getDataIni().getYear(), solicitacao.getDataIni().getMonth(), solicitacao.getDataIni().getDay());
			
			LocalDate metadePrazo = lcSolicitacaoInicio.plusDays((SolicitacaoBean.prazoResposta(solicitacao.getStatus()))/2);
			LocalDate vesperaPrazo = lcSolicitacaoInicio.plusDays((SolicitacaoBean.prazoResposta(solicitacao.getStatus()))-1);
			
			String tituloMensagem = "Notifica��o de prazo da solicitacao "+ solicitacao.getIdSolicitacao();
			
			if (now.equals(metadePrazo)) {
				System.out.println("Metade do prazo para resposta");
				String corpoMensagem = "Faltam "+metadePrazo+" dias para a solicitacao "+ solicitacao.getIdSolicitacao()+" expirar.";
				NotificacaoEmail.enviarEmailAutomatico(solicitacao, tituloMensagem, corpoMensagem);
			}else {
				if(now.equals(vesperaPrazo)) {
					System.out.println("V�spera do prazo para resposta");
					String corpoMensagem = "Falta 1 dia para a solicitacao "+ solicitacao.getIdSolicitacao()+" expirar.";
					NotificacaoEmail.enviarEmailAutomatico(solicitacao, tituloMensagem, corpoMensagem);
				}
			}
		}		
	} 
	
	
	public void finalizaSolicitacao(Solicitacao solicitacao) {
		System.out.println("Entrou no metodo finalizaSolicitacao �s" + new Date());

		if (!solicitacao.getStatus().equals("Finalizada")) {
		
			Date now = new Date();
			
			if (now.after(solicitacao.getDataLimite())) {
				System.out.println("Finalizou");
				solicitacao.setDatafim(new Date(System.currentTimeMillis()));
				solicitacao.setStatus("Finalizada");
				SolicitacaoDAO.saveOrUpdate(solicitacao);
				MensagemBean.salvarStatus(solicitacao, solicitacao.getStatus());
			}
		}		
	}
	
	

}
