package br.gov.se.lai.utils;
import java.util.Date;

import javax.faces.context.FacesContext;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import br.gov.se.lai.Bean.MensagemBean;
import br.gov.se.lai.DAO.SolicitacaoDAO;
import br.gov.se.lai.entity.Solicitacao;
 

public class verificarStatusSolicitacao implements Job {

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		//Simula validações
        //System.out.println("Validando dados duplicados no banco. At "+new Date());
        //System.out.println("Deletando registros com mais de 10 dias sem uso. At "+new Date());
		
		Date now = new Date();
		for (Solicitacao solicitacao : SolicitacaoDAO.listar()) {
			try {
				System.out.println("Entrou "+ new Date());
				if(!solicitacao.getStatus().equals("Finalizada")) {
					if(now.after(solicitacao.getDataLimite())) {
						solicitacao.setDatafim(new Date(System.currentTimeMillis()));
						solicitacao.setStatus("Finalizada");
						SolicitacaoDAO.saveOrUpdate(solicitacao);
						MensagemBean.salvarStatus(solicitacao, solicitacao.getStatus());
						
					}
				}
			}catch (NullPointerException e) {
				System.out.println(e.getMessage());
			}
			
		} 
	}
	
	@SuppressWarnings("unused")
	private void verificaTempoSolicitacao() {
	} 

}
