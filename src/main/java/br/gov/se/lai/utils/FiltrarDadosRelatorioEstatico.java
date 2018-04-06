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
import br.gov.se.lai.DAO.EntidadesDAO;
import br.gov.se.lai.DAO.SolicitacaoDAO;
import br.gov.se.lai.entity.Entidades;

public class FiltrarDadosRelatorioEstatico implements Serializable {

	protected final static String[] meses = { "Janeiro", "Fevereiro", "Mar�o","Abril", "Maio",  "Junho", "Julho",
			"Agosto", "Setembro", "Outubro", "Novembro", "Dezembro" };
	protected final static int anoInicial = 2012;

	/**
	 * Gerar Relat�rio Final
	 * 
	 * Retorna um n�mero de total de Pedidos(solicitacoes),
	 * Atendimentos(finalizado), Em tr�mite e Em aberto(n�o visualizado) em uma
	 * gr�fico de barras, onde a base s�o os status dos pedidos de informa��o e os
	 * valores s�o os n�meros totais referentes.
	 */
	public static Map<String, int[]> gerarAcompanhamentoGeralDosPedidosInformacao() {
		int numeroTotal = SolicitacaoDAO.listarGeral().size();
		int numeroTotalAtendimentos = SolicitacaoDAO.listarGeralFinalizada().size();
		int numeroTotalAberto = SolicitacaoDAO.listPorStatus("Aberta").size();

		String[] baseChart = { "Pedido", "Em aberto", "Atendimentos" };
		int[] dadosRelacionadosBase = { numeroTotal, numeroTotalAberto, numeroTotalAtendimentos };
		Map<String, int[]> dadosChart = new HashMap<>();
		for (int i = 0; i < baseChart.length; i++) {
			int[] dados = { dadosRelacionadosBase[i] };
			dadosChart.put(baseChart[i], dados);
		}

		return dadosChart;
	}

	/**
	 * Fun��o para filtrar os dados referentes aos pedidos de informa��o em cada
	 * m�s.
	 * 
	 * @return
	 */
	public static Map<String, ArrayList<Integer>> gerarAcompanhamentoMensalPedidoInformacao() {
		Map<String, ArrayList<Integer>> dadosChart = new HashMap<>();
		Calendar c = Calendar.getInstance();
		int mesAtual = c.get(Calendar.MONTH);
		int anoAtual = c.get(Calendar.YEAR);
		ArrayList<String> base = new ArrayList<>();
		ArrayList<ArrayList<Integer>> dadosRelacionadorBase = new ArrayList<>();

		for (int i = 1; i <= mesAtual; i++) {
			base.add(meses[i-1]);
			ArrayList<Integer> dadosEspecificos = new ArrayList<>();
			dadosEspecificos.add(SolicitacaoDAO.listPorTipoPeriodo("Informa��o", anoAtual + "-0" + i + "%").size());
			dadosEspecificos.add(
					SolicitacaoDAO.listPorTipoStatusPeriodo("Informa��o", "Aberta", anoAtual + "-0" + i + "%").size());
			dadosEspecificos.add(SolicitacaoDAO
					.listPorTipoStatusPeriodo("Informa��o", "Finalizada", anoAtual + "-0" + i + "%").size());
			dadosRelacionadorBase.add(dadosEspecificos);
		}

		for (int i = 0; i < base.size(); i++) {
			dadosChart.put(base.get(i), dadosRelacionadorBase.get(i));
		}
		return dadosChart;
	}

	/**
	 * Fun��o para retornar valores de pedidos de informa��o n�o acumulado dos anos
	 * de 2012 at� o ano atual.
	 * 
	 * @return
	 */
	public static Map<String, ArrayList<Integer>> gerarAcompanhamentoAnualPedidoInformacao() {
		Map<String, ArrayList<Integer>> dadosChart = new HashMap<>();
		Calendar c = Calendar.getInstance();
		int anoAtual = c.get(Calendar.YEAR);
		ArrayList<String> base = new ArrayList<>();
		ArrayList<ArrayList<Integer>> dadosRelacionadorBase = new ArrayList<>();

		for (int i = anoInicial; i <= anoAtual; i++) {
			base.add(String.valueOf(i));
			ArrayList<Integer> dadosEspecificos = new ArrayList<>();
			dadosEspecificos.add(SolicitacaoDAO.listPorTipoPeriodo("Informa��o", i + "%").size());
			dadosEspecificos.add(SolicitacaoDAO.listPorTipoStatusPeriodo("Informa��o", "Aberta", i + "%").size());
			dadosEspecificos.add(SolicitacaoDAO.listPorTipoStatusPeriodo("Informa��o", "Finalizada", i + "%").size());
			dadosRelacionadorBase.add(dadosEspecificos);
		}

		for (int i = 0; i < base.size(); i++) {
			dadosChart.put(base.get(i), dadosRelacionadorBase.get(0));
		}

		return dadosChart;
	}

	public static Map<String, ArrayList<Integer>> gerarAcompanhamentoAnualAcumuladoPedidoInformacao() {
		Map<String, ArrayList<Integer>> dadosChart = new HashMap<>();
		Calendar c = Calendar.getInstance();
		int anoAtual = c.get(Calendar.YEAR);
		ArrayList<String> base = new ArrayList<>();
		ArrayList<ArrayList<Integer>> dadosRelacionadorBase = new ArrayList<>();

		for (int i = anoInicial; i <= anoAtual; i++) {
			base.add(String.valueOf(i));
			ArrayList<Integer> dadosEspecificos = new ArrayList<>();
			int pedidosTotalPeriodo = 0;
			int pedidosAbertoPeriodo = 0;
			int pedidosFinalizadosPeriodo = 0;
			for (int ano = anoInicial; ano <= i; ano++) {
				pedidosTotalPeriodo += SolicitacaoDAO.listPorTipoPeriodo("Informa��o", ano + "%").size();
				pedidosAbertoPeriodo += SolicitacaoDAO.listPorTipoStatusPeriodo("Informa��o", "Aberta", ano + "%")
						.size();
				pedidosFinalizadosPeriodo += SolicitacaoDAO
						.listPorTipoStatusPeriodo("Informa��o", "Finalizada", ano + "%").size();
			}
			dadosEspecificos.add(pedidosTotalPeriodo);
			dadosEspecificos.add(pedidosAbertoPeriodo);
			dadosEspecificos.add(pedidosFinalizadosPeriodo);
			dadosRelacionadorBase.add(dadosEspecificos);
		}

		for (int i = 0; i < base.size(); i++) {
			dadosChart.put(base.get(i), dadosRelacionadorBase.get(0));
		}

		return dadosChart;
	}

	public static Map<String, ArrayList<Integer>> gerarAcompanhamentoOrgaoPedidoInformacao() {
		Map<String, ArrayList<Integer>> dadosChart = new HashMap<>();
		Calendar c = Calendar.getInstance();
		int mesAtual = c.get(Calendar.MONTH);
		int anoAtual = c.get(Calendar.YEAR);

		ArrayList<String> base = new ArrayList<>();
		ArrayList<ArrayList<Integer>> dadosRelacionadorBase = new ArrayList<>();

		ArrayList<Entidades> orgaos = new ArrayList<>(EntidadesDAO.listOrgaos());

		for (Entidades entidades : orgaos) {
			base.add(entidades.getSigla());
			ArrayList<Integer> dadosEspecificos = new ArrayList<>();
			dadosEspecificos.add(SolicitacaoDAO.listarPorEntidade(entidades.getIdEntidades(), "Informa��o", anoAtual + "-0" + mesAtual + "%").size());
			dadosEspecificos.add(SolicitacaoDAO.listarPorEntidade(entidades.getIdEntidades(), "Informa��o", "Aberta",
					anoAtual + "-0" + mesAtual + "%").size());
			dadosEspecificos.add(SolicitacaoDAO.listarPorEntidade(entidades.getIdEntidades(), "Informa��o",
					"Finalizada", anoAtual + "-0" + mesAtual + "%").size());
			dadosRelacionadorBase.add(dadosEspecificos);
		}

		for (int i = 0; i < base.size(); i++) {
			dadosChart.put(base.get(i), dadosRelacionadorBase.get(i));
		}

		return dadosChart;
	}

	public static Map<String, ArrayList<Integer>> nome() {
		Map<String, ArrayList<Integer>> dadosChart = new HashMap<>();
		return dadosChart;
	}
}
