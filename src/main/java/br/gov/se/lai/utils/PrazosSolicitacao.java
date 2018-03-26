package br.gov.se.lai.utils;

import java.time.LocalDate;
import java.util.Calendar;

import br.gov.se.lai.entity.Solicitacao;

public class PrazosSolicitacao {

	public static int prazoResposta(String status) {
		switch (status) {
		case "Aberta":
			return 4;
//			return constanteAdicionalTempo;
		case "Prorrogada":
			return 2;
//			return constanteAdicionalTempo;
		case "Respondida":
			return 2;
//			return constanteAdicionalTempo;
		case "Recurso":
			return 2;
//			return 5;
		default:
			return 4;
//			return constanteTempo;
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
	 * @return
	 */
	
	public static Calendar diaUtilDataLimite(String status) {
		
		Calendar limite = Calendar.getInstance();
		limite.setTime(java.sql.Date.valueOf(LocalDate.now().plusDays(prazoResposta(status))));
		
		int limiteNum = limite.get(Calendar.DAY_OF_WEEK);
		if( (limiteNum == Calendar.SUNDAY) || (limiteNum == Calendar.SATURDAY)) {
			limite.add(Calendar.DATE, +Calendar.DAY_OF_WEEK);
		}
		
		return limite;
	}
}
