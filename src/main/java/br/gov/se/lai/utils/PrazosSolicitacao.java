package br.gov.se.lai.utils;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

import br.gov.se.lai.entity.Solicitacao;

public class PrazosSolicitacao {
	
	private static int prazoResposta = 5; // Produ��o: 20
	private static int prazoProrrogada = 2; // Produ��o: 10
	private static int prazoRecurso = 2; // Produ��o: 10
	private static int prazoEncaminhamento = 1; // Produ��o: 5
	

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
	public static boolean verificaSeEncaminhavel(Solicitacao solicitacao) {
		boolean retorno = false;
		try {
			Calendar hoje = Calendar.getInstance();
			Calendar limite = Calendar.getInstance();
			limite.setTime(solicitacao.getDataIni());
			limite.add(Calendar.DATE, +prazoEncaminhamento);
			if (hoje.before(limite)) {
				retorno = true;
			}
		} catch (NullPointerException e) {
		}finally {
			return retorno;
		}
	}
	
	/**
	 * Fun��o contarDiasUteis
	 * 
	 * Essa fun��o retorna o n�mero que deve ser acrescido na data limite da solicita��o 
	 * para que sejam considerados apenas os dias uteis.
	 * 
	 * @deprecated
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
	 * Fun��o gerarPrazoDiasUteis
	 * 
	 * Esta fun��o gera uma data limite a partir de um prazo considerando apenas dias �teis a partir da data estabelecida.
	 * 
	 * @deprecated
	 * 
	 * @param data - Data inicial que ser� acrescida a quantida de dias �teis.
	 * @param prazo - N�mero que representa a quantidade de dias �teis que devem ser acrescidos.
	 * @return Data final ap�s contagem de dias �teis.
	 */
	
	public static Date gerarPrazoDiasUteis(Date data, int prazo) {
		
		Calendar dataInicial = Calendar.getInstance();
		dataInicial.setTime(data);
		
		while(prazo > 0) {
			dataInicial.add(Calendar.DAY_OF_MONTH, 1);
			int diaDaSemana = dataInicial.get(Calendar.DAY_OF_WEEK);
			
			if (diaDaSemana != Calendar.SATURDAY && diaDaSemana != Calendar.SUNDAY) {
				--prazo;
			}
			
		}
		
		return dataInicial.getTime();
	}
	
	/**
	 * Fun��o gerarPrazoDiaUtilLimite
	 * 
	 * Essa fun��o verifica se a data inicial ou limite cai em um final de semana. Caso verdade, � acrescido 
	 * 1 ou 2 dias ao prazo final. 
	 * 
	 * @param data - Data inicial que ser� acrescida a quantida de dias �teis.
	 * @param prazo - N�mero que representa a quantidade de dias �teis que devem ser acrescidos.
	 * @return Data final ap�s contagem de dias �teis.
	 */
	
	public static Date gerarPrazoDiaUtilLimite(Date data, int prazo) {
		
		Calendar dataInicial = Calendar.getInstance();
		dataInicial.setTime(data);
		
		dataInicial.add(Calendar.DAY_OF_MONTH, +1);
		
		pularFimDeSemana(dataInicial);
		
		dataInicial.add(Calendar.DAY_OF_MONTH, prazo);
		
		pularFimDeSemana(dataInicial);
		
		return dataInicial.getTime();
	}
	
	public static Calendar pularFimDeSemana(Calendar data) {
		
		if (data.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
			data.add(Calendar.DAY_OF_MONTH, +2);
		} else if (data.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
			data.add(Calendar.DAY_OF_MONTH, +1);
		}
		
		return data;
	}
	
	
	
}
