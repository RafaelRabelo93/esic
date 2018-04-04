package br.gov.se.lai.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.ChartSeries;

import br.com.caelum.stella.inwords.Messages;
import br.gov.se.lai.DAO.SolicitacaoDAO;
import br.gov.se.lai.entity.Solicitacao;

public class GerarRelatorio implements Serializable {
	
	protected final static String[] meses = {"Janeiro", "Fevereiro", "Março", "Maio", "Abril", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};
	
	/**
	 * Gerar Relatório Final 
	 * 
	 * Retorna um número de total de Pedidos(solicitacoes), Atendimentos(finalizado), Em trâmite e Em aberto(não visualizado)
	 * em uma gráfico de barras, onde a base são os status dos pedidos de informação e os valores são os números totais referentes.
	 */
	public static Map<String,int[]> gerarAcompanhamentoGeralDosPedidosInformacao() {
		int numeroTotal = SolicitacaoDAO.listarGeral().size();
		int numeroTotalAtendimentos = SolicitacaoDAO.listarGeralFinalizada().size();
		int numeroTotalAberto = SolicitacaoDAO.listPorStatus("Aberta").size();

		String[] baseChart = {"Pedido", "Em aberto", "Atendimentos"};
		int[] dadosRelacionadosBase = {numeroTotal, numeroTotalAberto, numeroTotalAtendimentos};
		Map<String,int[]> dadosChart = new HashMap<>();
		for (int i = 0; i<baseChart.length; i++) {
			int[] dados = {dadosRelacionadosBase[i]};
			dadosChart.put(baseChart[i], dados);
		}
		return dadosChart;
	}
	
	
	public static Map<String, ArrayList<Integer>> gerarAcompanhamentoMensalPedidoInformacao(){
		Map<String,ArrayList<Integer>> dadosChart = new HashMap<>();
		int mesAtual = Calendar.MONTH;
		ArrayList<String> base = new ArrayList<>();
		ArrayList<ArrayList<Integer>> dadosRelacionadorBase = new ArrayList<>();
		
		for(int i = 0; i <= mesAtual; i++) {
			base.add(meses[0]);
			ArrayList<Integer> dadosEspecificos = new ArrayList<>();
			dadosEspecificos.add(SolicitacaoDAO.listPorTipoMes("Informação", i).size());
			dadosEspecificos.add(SolicitacaoDAO.listPorTipoStatusMes("Informação", "Aberta", i).size());
			dadosEspecificos.add(SolicitacaoDAO.listPorTipoStatusMes("Informação", "Finalizada", i).size());
			dadosRelacionadorBase.add(dadosEspecificos);
		}
		
		for (int i = 0; i < base.size(); i++) {
			dadosChart.put(base.get(i), dadosRelacionadorBase.get(0));
		}
		return dadosChart;
	}
		
}
