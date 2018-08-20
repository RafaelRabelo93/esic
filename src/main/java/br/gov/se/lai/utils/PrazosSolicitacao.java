package br.gov.se.lai.utils;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

import br.gov.se.lai.entity.Solicitacao;

public class PrazosSolicitacao {
	
	private static int prazoResposta = 5; // Produção: 20
	private static int prazoProrrogada = 2; // Produção: 10
	private static int prazoRecurso = 2; // Produção: 10
	

	public static int prazoResposta(String status) {
		switch (status) {
		case "Aberta":
			return prazoResposta;
		case "Prorrogada":
			return prazoProrrogada;
		case "Respondida":
			return prazoRecurso;
		case "Recurso":
			return prazoRecurso;
		default:
			return prazoResposta;
		}
	}
	
	
	
	@SuppressWarnings({ "finally", "unused" })
	public static boolean verificaSe24Horas(Solicitacao solicitacao) {
		boolean retorno = false;
		try {
			Calendar hoje = Calendar.getInstance();
			Calendar limite = Calendar.getInstance();
			limite.setTime(solicitacao.getDataIni());
			limite.add(Calendar.DATE, +1);
			if (hoje.before(limite)) {
				retorno = true;
			}
		} catch (NullPointerException e) {
		}finally {
			return retorno;
		}
	}

	/**
	 * Função contarDiasUteis
	 * 
	 * Essa função retorna o número que deve ser acrescido na data limite da solicitação 
	 * para que sejam considerados apenas os dias uteis.
	 * 
	 * @return
	 */
	public static int contarDiasUteis(String status) {
		Calendar hoje = Calendar.getInstance();
	    int hojeNum = hoje.get (Calendar.DAY_OF_WEEK);
		boolean ehDiaSemana = ((hojeNum >= Calendar.MONDAY) && (hojeNum <= Calendar.FRIDAY));
		int dias = prazoResposta(status);
		
		if(ehDiaSemana) {
			dias += (prazoResposta(status)/5)*2;
		}
		
		return status.equals("Aberta") ? (dias-1) : dias;

	}
	
	/**
	 * Função diaUtilDataLimite
	 * 
	 * Essa função verifica se a data limite cai em um final de semana. Caso verdade, é acrescido 
	 * 1 ou 2 dias ao prazo final. 
	 * 
	 * @param status - para recuperar quantidade de dias de prazo variante do tipo de solicitação.
	 * @param dataLimite 
	 * @return
	 */
	
	public static Date diaUtilDataLimite(String status, Date dataLimite) {
		
		Calendar limite = Calendar.getInstance();
		int limiteDia = limite.get(Calendar.DAY_OF_WEEK);
		limite.setTime(dataLimite);
		limite.add(Calendar.DATE,limiteDia == Calendar.FRIDAY ? prazoResposta(status) +3 : prazoResposta(status));
		
		
		if( (limiteDia == Calendar.SUNDAY) || (limiteDia == Calendar.SATURDAY)) {
			limite.add(Calendar.DATE, +Calendar.DAY_OF_WEEK);
		}
		
		return limite.getTime();
	}
	public static Date gerarDiaUtilDataLimite(String status) {
		
		Calendar limite = Calendar.getInstance();
		int limiteDia = limite.get(Calendar.DAY_OF_WEEK);
		limite.setTime(java.sql.Date.valueOf(LocalDate.now().plusDays(
				limiteDia == Calendar.FRIDAY ? prazoResposta(status) +3 : prazoResposta(status)
				)));
		
		
		
		if( (limiteDia == Calendar.SUNDAY) || (limiteDia == Calendar.SATURDAY)) {
			limite.add(Calendar.DATE, +Calendar.DAY_OF_WEEK);
		}
		
		return limite.getTime();
	}
}
