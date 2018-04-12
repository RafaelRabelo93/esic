package relatorio;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.ChartSeries;

import br.com.caelum.stella.inwords.Messages;
import br.gov.se.lai.DAO.EntidadesDAO;
import br.gov.se.lai.DAO.SolicitacaoDAO;
import br.gov.se.lai.entity.Cidadao;
import br.gov.se.lai.entity.Entidades;
import br.gov.se.lai.entity.Solicitacao;

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
	public static Map<String,  ArrayList<Integer>> gerarAcompanhamentoGeralDosPedidosInformacao() {
		int numeroTotal = SolicitacaoDAO.listarGeral().size();
		int numeroTotalAtendimentos = SolicitacaoDAO.listarGeralFinalizada().size();
		int numeroTotalAberto = SolicitacaoDAO.listPorStatus("Aberta").size();

		String[] baseChart = { "Pedido", "Em aberto", "Atendimentos" };
		int[] dadosRelacionadosBase = { numeroTotal, numeroTotalAberto, numeroTotalAtendimentos };
		Map<String, ArrayList<Integer>> dadosChart = new HashMap<>();
		for (int i = 0; i < baseChart.length; i++) {
			ArrayList<Integer> dados = new ArrayList<>(Arrays.asList(dadosRelacionadosBase[i]));
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
			dadosChart.put(base.get(i), dadosRelacionadorBase.get(i));
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
			dadosChart.put(base.get(i), dadosRelacionadorBase.get(i));
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

	public static Map<String, ArrayList<Integer>> gerarAcompanhamentoEntidadePedidoInformacao() {
		Map<String, ArrayList<Integer>> dadosChart = new HashMap<>();
		Calendar c = Calendar.getInstance();
		int mesAtual = c.get(Calendar.MONTH);
		int anoAtual = c.get(Calendar.YEAR);
		
		ArrayList<String> base = new ArrayList<>();
		ArrayList<ArrayList<Integer>> dadosRelacionadorBase = new ArrayList<>();
		
		ArrayList<Entidades> orgaos = new ArrayList<>(EntidadesDAO.listEntidades());
		
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
	
	public static Map<String, ArrayList<Integer>> gerarAcompanhamentoEstadosPedidoInformacao(){
		Map<String, ArrayList<Integer>> dadosChart = new HashMap<>();
		Calendar c = Calendar.getInstance();
		int mesAtual = c.get(Calendar.MONTH);
		int anoAtual = c.get(Calendar.YEAR);
		String periodo = anoAtual+"-0"+mesAtual+"%";

		Set<String> uf = new HashSet<>();
		ArrayList<String> estados = new ArrayList<>(SolicitacaoDAO.listarPorFederacao("Informa��o", periodo));
		if(estados != null) {
			uf = estados.stream().collect(Collectors.toSet());
			uf.remove("");
		}
		
		ArrayList<String> base = new ArrayList<>();
		ArrayList<ArrayList<Integer>> dadosRelacionadorBase = new ArrayList<>();
		for (String estadoEnviado : uf) {
			base.add(estadoEnviado);
			dadosRelacionadorBase.add(new ArrayList<Integer>(Arrays.asList((Collections.frequency(estados, estadoEnviado)))));
		}
		
		for (int i = 0; i < base.size(); i++) {
			dadosChart.put(base.get(i), dadosRelacionadorBase.get(i));
		}

		return dadosChart;
	}

	public static Map<String, ArrayList<Integer>> gerarAcompanhamentoTipoPessoaGeneroPedidoInformacao(){
		Map<String, ArrayList<Integer>> dadosChart = new HashMap<>();
		Calendar c = Calendar.getInstance();
		int mesAtual = c.get(Calendar.MONTH);
		int anoAtual = c.get(Calendar.YEAR);
		String periodo = anoAtual+"-0"+mesAtual+"%";
		
		ArrayList<String> base = new ArrayList<>();
		ArrayList<ArrayList<Integer>> dadosRelacionadorBase = new ArrayList<>();
		
		ArrayList<Cidadao> pessoas= new ArrayList<>(SolicitacaoDAO.listarCidadao("Informa��o", periodo));
		int numeroJuridica = 0;
		int numeroFisicaFeminino = 0;
		int numeroFisicaMasculino = 0;
		for (Cidadao cidadao : pessoas) {
			if(cidadao.getTipo().equals(true)) {
				switch (cidadao.getSexo()) {
				case "F":
					numeroFisicaFeminino++;
					break;
				case "M":
					numeroFisicaMasculino++;
					break;
				default:
					break;
				}
			}else{
				numeroJuridica++;
			}
		}
		
		base.add("Pessoa Jur�dica");
		dadosRelacionadorBase.add(new ArrayList<>(Arrays.asList(numeroJuridica)));
		base.add("Pessoa F�sica Feminino");
		dadosRelacionadorBase.add(new ArrayList<>(Arrays.asList(numeroFisicaFeminino)));
		base.add("Pessoa F�sica Masculino");
		dadosRelacionadorBase.add(new ArrayList<>(Arrays.asList(numeroFisicaMasculino)));
		
		for (int i = 0; i < base.size(); i++) {
			dadosChart.put(base.get(i), dadosRelacionadorBase.get(i));
		}

		return dadosChart;
	}
	
	public static Map<String, ArrayList<Integer>> gerarAcompanhamentoAssuntoPedidoInformacao(){
		Map<String, ArrayList<Integer>> dadosChart = new HashMap<>();
		Calendar c = Calendar.getInstance();
		int mesAtual = c.get(Calendar.MONTH);
		int anoAtual = c.get(Calendar.YEAR);
		String periodo = anoAtual+"-0"+mesAtual+"%";
		
		ArrayList<String> base = new ArrayList<>();
		ArrayList<ArrayList<Integer>> dadosRelacionadorBase = new ArrayList<>();
		
		Set<String> assunto = new HashSet<>();
		ArrayList<String> assuntos =  new ArrayList<>(SolicitacaoDAO.listarAssuntos("Informa��o", periodo));
		
		if(assuntos!= null) {
			assunto = assuntos.stream().collect(Collectors.toSet());
			assunto.remove("");
		}
		
		for (String ass : assunto) {
			base.add(ass);
			dadosRelacionadorBase.add(new ArrayList<Integer>(Arrays.asList((Collections.frequency(assuntos, ass)))));
		}
		
		for (int i = 0; i < base.size(); i++) {
			dadosChart.put(base.get(i), dadosRelacionadorBase.get(i));
		}
		
		return dadosChart;
	}

	public static Map<String, ArrayList<Integer>> nome(){
		Map<String, ArrayList<Integer>> dadosChart = new HashMap<>();
		Calendar c = Calendar.getInstance();
		int mesAtual = c.get(Calendar.MONTH);
		int anoAtual = c.get(Calendar.YEAR);
		String periodo = anoAtual+"-0"+mesAtual+"%";
		
		ArrayList<String> base = new ArrayList<>();
		ArrayList<ArrayList<Integer>> dadosRelacionadorBase = new ArrayList<>();
		
		Set<String> uf = new HashSet<>();
		
		for (int i = 0; i < base.size(); i++) {
			dadosChart.put(base.get(i), dadosRelacionadorBase.get(i));
		}
		
		return dadosChart;
	}
}
