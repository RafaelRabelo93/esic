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

	protected final static String[] meses = { "Janeiro", "Fevereiro", "Março","Abril", "Maio",  "Junho", "Julho",
			"Agosto", "Setembro", "Outubro", "Novembro", "Dezembro" };
	protected final static int anoInicial = 2012;

	/**
	 * Gerar Relatório Final
	 * 
	 * Retorna um número de total de Pedidos(solicitacoes),
	 * Atendimentos(finalizado), Em trâmite e Em aberto(não visualizado) em uma
	 * gráfico de barras, onde a base são os status dos pedidos de informação e os
	 * valores são os números totais referentes.
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
	 * Função para filtrar os dados referentes aos pedidos de informação em cada
	 * mês.
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
			dadosEspecificos.add(SolicitacaoDAO.listPorTipoPeriodo("Informação", anoAtual + "-0" + i + "%").size());
			dadosEspecificos.add(
					SolicitacaoDAO.listPorTipoStatusPeriodo("Informação", "Aberta", anoAtual + "-0" + i + "%").size());
			dadosEspecificos.add(SolicitacaoDAO
					.listPorTipoStatusPeriodo("Informação", "Finalizada", anoAtual + "-0" + i + "%").size());
			dadosRelacionadorBase.add(dadosEspecificos);
		}

		for (int i = 0; i < base.size(); i++) {
			dadosChart.put(base.get(i), dadosRelacionadorBase.get(i));
		}
		return dadosChart;
	}

	/**
	 * Função para retornar valores de pedidos de informação não acumulado dos anos
	 * de 2012 até o ano atual.
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
			dadosEspecificos.add(SolicitacaoDAO.listPorTipoPeriodo("Informação", i + "%").size());
			dadosEspecificos.add(SolicitacaoDAO.listPorTipoStatusPeriodo("Informação", "Aberta", i + "%").size());
			dadosEspecificos.add(SolicitacaoDAO.listPorTipoStatusPeriodo("Informação", "Finalizada", i + "%").size());
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
				pedidosTotalPeriodo += SolicitacaoDAO.listPorTipoPeriodo("Informação", ano + "%").size();
				pedidosAbertoPeriodo += SolicitacaoDAO.listPorTipoStatusPeriodo("Informação", "Aberta", ano + "%")
						.size();
				pedidosFinalizadosPeriodo += SolicitacaoDAO
						.listPorTipoStatusPeriodo("Informação", "Finalizada", ano + "%").size();
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
			dadosEspecificos.add(SolicitacaoDAO.listarPorEntidade(entidades.getIdEntidades(), "Informação", anoAtual + "-0" + mesAtual + "%").size());
			dadosEspecificos.add(SolicitacaoDAO.listarPorEntidade(entidades.getIdEntidades(), "Informação", "Aberta",
					anoAtual + "-0" + mesAtual + "%").size());
			dadosEspecificos.add(SolicitacaoDAO.listarPorEntidade(entidades.getIdEntidades(), "Informação",
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
