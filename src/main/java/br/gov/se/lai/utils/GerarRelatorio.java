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
	
	protected final static String[] meses = {"Janeiro", "Fevereiro", "Mar�o", "Maio", "Abril", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};
	
	/**
	 * Gerar Relat�rio Final 
	 * 
	 * Retorna um n�mero de total de Pedidos(solicitacoes), Atendimentos(finalizado), Em tr�mite e Em aberto(n�o visualizado)
	 * em uma gr�fico de barras, onde a base s�o os status dos pedidos de informa��o e os valores s�o os n�meros totais referentes.
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
			dadosEspecificos.add(SolicitacaoDAO.listPorTipoMes("Informa��o", i).size());
			dadosEspecificos.add(SolicitacaoDAO.listPorTipoStatusMes("Informa��o", "Aberta", i).size());
			dadosEspecificos.add(SolicitacaoDAO.listPorTipoStatusMes("Informa��o", "Finalizada", i).size());
			dadosRelacionadorBase.add(dadosEspecificos);
		}
		
		for (int i = 0; i < base.size(); i++) {
			dadosChart.put(base.get(i), dadosRelacionadorBase.get(0));
		}
		return dadosChart;
	}
		
}
