package br.gov.se.lai.relatorios;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
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
import br.gov.se.lai.entity.Competencias;
import br.gov.se.lai.entity.Entidades;
import br.gov.se.lai.entity.Solicitacao;

public class FiltrarDadosRelatorioEstatico implements Serializable {
	
	/**
	 * Essa classe se refere a organiza��o do que � preciso para realizar a query no banco de dados
	 * e ent�o tratar o retorno da query para em uma estrutura para que possa ser plotada nos gr�ficos 
	 * dos gr�ficos mensais, que s�o gr�ficos sobre o pedido de informa��o realizado no m�s e ano atual.
	 * 
	 * Esta classe trata unicamente dos pedidos de informa��o do ano e m�s atual. Caso queira filtrar relat�rios
	 * de forma din�mica v� para a classe FiltrarDadosRelatorioDinamico e RelatorioDinamico.
	 * 
	 * Nessa classe j� est� prevista a contagem do ano inicial como 2012, pois foi em 2012 que o sistema
	 * esic foi colocado para servi�o, ent�o � importante n�o esquecer que existe um hist�rico.
	 * 
	 * As fun��es previstas nessa classe retornam dicion�rios onde a chave se refere ao eixo x do gr�fico 
	 * que ser� plotado e o valor se refere a um arraylist que armazenar� os valores das colunas. O valor � 
	 * um array pois em certos gr�fico � necess�rio plotar valores por status da solicita��o (total de pedidos, 
	 * respondidos, aberto..) referentes a um per�odo de tempo.
	 */

	
	protected final static String[] meses = { "Janeiro", "Fevereiro", "Mar�o", "Abril", "Maio", "Junho", "Julho",
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
	public static Map<String, ArrayList<Integer>> gerarAcompanhamentoGeralDosPedidosInformacao() {
		int numeroTotal = SolicitacaoDAO.listarTotalPorPeriodo("%").size();
		int numeroTotalAtendidos = SolicitacaoDAO.listarAtendidasPorPeriodo("%").size();
		int numeroTotalTramite = SolicitacaoDAO.listarEmTramitePorPeriodo("%").size();
		int numeroTotalNaoVisualizados = SolicitacaoDAO.listarNaoVisualizadasPorPeriodo("%").size();
		int numeroTotalSemResposta = SolicitacaoDAO.listarSemRespostaPorPeriodo("%").size();

		String[] baseChart = { "Total", "Atendidas", "Sem Resposta", "Em Tr�mite", "N�o Visualizadas" };
		int[] dadosRelacionadosBase = { numeroTotal, numeroTotalAtendidos, numeroTotalSemResposta, numeroTotalTramite, numeroTotalNaoVisualizados };
		Map<String, ArrayList<Integer>> dadosChart = new LinkedHashMap<>();
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
		Map<String, ArrayList<Integer>> dadosChart = new LinkedHashMap<>();
		Calendar c = Calendar.getInstance();
		int mesAtual = c.get(Calendar.MONTH)+1;
		int anoAtual = c.get(Calendar.YEAR);
		ArrayList<String> base = new ArrayList<>();
		ArrayList<ArrayList<Integer>> dadosRelacionadorBase = new ArrayList<>();

		for (int i = 1; i <= mesAtual; i++) {
			base.add(meses[i - 1]);
			ArrayList<Integer> dadosEspecificos = new ArrayList<>();
			
			if (i<=9) {
				dadosEspecificos.add(SolicitacaoDAO.listarTotalPorPeriodo(anoAtual + "-0" + i + "%").size());
				dadosEspecificos.add(SolicitacaoDAO.listarAtendidasPorPeriodo(anoAtual + "-0" + i + "%").size());
				dadosEspecificos.add(SolicitacaoDAO.listarSemRespostaPorPeriodo(anoAtual + "-0" + i + "%").size());
				dadosEspecificos.add(SolicitacaoDAO.listarEmTramitePorPeriodo(anoAtual + "-0" + i + "%").size());
				dadosEspecificos.add(SolicitacaoDAO.listarNaoVisualizadasPorPeriodo(anoAtual + "-0" + i + "%").size());
				dadosRelacionadorBase.add(dadosEspecificos);
			} else {
				dadosEspecificos.add(SolicitacaoDAO.listarTotalPorPeriodo(anoAtual + "-" + i + "%").size());
				dadosEspecificos.add(SolicitacaoDAO.listarAtendidasPorPeriodo(anoAtual + "-" + i + "%").size());
				dadosEspecificos.add(SolicitacaoDAO.listarSemRespostaPorPeriodo(anoAtual + "-" + i + "%").size());
				dadosEspecificos.add(SolicitacaoDAO.listarEmTramitePorPeriodo(anoAtual + "-" + i + "%").size());
				dadosEspecificos.add(SolicitacaoDAO.listarNaoVisualizadasPorPeriodo(anoAtual + "-" + i + "%").size());
				dadosRelacionadorBase.add(dadosEspecificos);
			} 
			
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
		Map<String, ArrayList<Integer>> dadosChart = new LinkedHashMap<>();
		Calendar c = Calendar.getInstance();
		Integer anoAtual = c.get(Calendar.YEAR);
		ArrayList<String> base = new ArrayList<>();
		ArrayList<ArrayList<Integer>> dadosRelacionadorBase = new ArrayList<>();
		
		for (int i = anoInicial; i <= anoAtual; i++) {
			
			base.add(String.valueOf(i));
			ArrayList<Integer> dadosEspecificos = new ArrayList<>();
			dadosEspecificos.add(SolicitacaoDAO.listarTotalPorPeriodo(i + "%").size());
			dadosEspecificos.add(SolicitacaoDAO.listarAtendidasPorPeriodo(i + "%").size());
			dadosEspecificos.add(SolicitacaoDAO.listarSemRespostaPorPeriodo(i + "%").size());
			dadosEspecificos.add(SolicitacaoDAO.listarEmTramitePorPeriodo(i + "%").size());
			dadosEspecificos.add(SolicitacaoDAO.listarNaoVisualizadasPorPeriodo(i + "%").size());
			dadosRelacionadorBase.add(dadosEspecificos);
		}
		
		for (int i = 0; i < base.size(); i++) {
			dadosChart.put(base.get(i), dadosRelacionadorBase.get(i));
		}
		
		return dadosChart;
	}

	
	/**
	 * Esta fun��o busca os valores dos pedidos de informa��o dos anos anteriores at� o ano atual de forma que seja acumulada.
	 * Ou seja, o ano seguinte corresponder� ao valor da quantidade de pedidos de informa��es do ano em quest�o, somados aos valores
	 * dos anos anteriores. 
	 * @return
	 */
	public static Map<String, ArrayList<Integer>> gerarAcompanhamentoAnualAcumuladoPedidoInformacao() {
		Map<String, ArrayList<Integer>> dadosChart = new LinkedHashMap<>();
		Calendar c = Calendar.getInstance();
		int anoAtual = c.get(Calendar.YEAR);
		ArrayList<String> base = new ArrayList<>();
		ArrayList<ArrayList<Integer>> dadosRelacionadorBase = new ArrayList<>();

		for (int i = anoInicial; i <= anoAtual; i++) {
			base.add(String.valueOf(i));
			ArrayList<Integer> dadosEspecificos = new ArrayList<>();
			int pedidosTotalPeriodo = 0;
			int pedidosAtendidasPeriodo = 0;
			int pedidosSemRespostaPeriodo = 0;
			int pedidosTramitePeriodo = 0;
			int pedidosNaoVisualizadasPeriodo = 0;
			
			for (int ano = anoInicial; ano <= i; ano++) {
				pedidosTotalPeriodo += SolicitacaoDAO.listarTotalPorPeriodo(ano + "%").size();
				pedidosAtendidasPeriodo += SolicitacaoDAO.listarAtendidasPorPeriodo(ano + "%").size();
				pedidosSemRespostaPeriodo += SolicitacaoDAO.listarSemRespostaPorPeriodo(ano + "%").size();
				pedidosTramitePeriodo += SolicitacaoDAO.listarEmTramitePorPeriodo(ano + "%").size();
				pedidosNaoVisualizadasPeriodo += SolicitacaoDAO.listarNaoVisualizadasPorPeriodo(ano + "%").size();
				
			}
			
			dadosEspecificos.add(pedidosTotalPeriodo);
			dadosEspecificos.add(pedidosAtendidasPeriodo);
			dadosEspecificos.add(pedidosSemRespostaPeriodo);
			dadosEspecificos.add(pedidosTramitePeriodo);
			dadosEspecificos.add(pedidosNaoVisualizadasPeriodo);
			dadosRelacionadorBase.add(dadosEspecificos);
		}
		
		for (int i = 0; i < base.size(); i++) {
			dadosChart.put(base.get(i), dadosRelacionadorBase.get(i));
		}

		return dadosChart;
	}
	
	/**
	 * Esta fun��o faz uma contagem dos valores encontrados referentes aos �rg�os ativos armazenados no banco.
	 * @return
	 */
	public static Map<String, ArrayList<Integer>> gerarAcompanhamentoOrgaoPedidoInformacao() {
		Map<String, ArrayList<Integer>> dadosChart = new LinkedHashMap<>();
		Calendar c = Calendar.getInstance();
		int anoAtual = c.get(Calendar.YEAR);
		int mesAtual = c.get(Calendar.MONTH)+1;
		
		String periodo;
		if (mesAtual<=9) {
			periodo = anoAtual + "-0" + mesAtual + "%";
		} else {
			periodo = anoAtual + "-" + mesAtual + "%";
		} 
		
		ArrayList<String> base = new ArrayList<>();
		ArrayList<ArrayList<Integer>> dadosRelacionadorBase = new ArrayList<>();

		ArrayList<Entidades> orgaos = new ArrayList<>(EntidadesDAO.listOrgaosComSolicitacoes(periodo));
		
		if(orgaos.isEmpty()) {
//			orgaos = new ArrayList<>(EntidadesDAO.listOrgaos());
//			while(orgaos.size() > 7) {
//				orgaos.remove(orgaos.size()-1);
//			}
			base.add("");
			ArrayList<Integer> dadosEspecificos = new ArrayList<>();
			dadosEspecificos.add(0);
			dadosEspecificos.add(0);
			dadosEspecificos.add(0);
			dadosEspecificos.add(0);
			dadosEspecificos.add(0);
			dadosRelacionadorBase.add(dadosEspecificos);
		}
		else {
		
			for (Entidades entidades : orgaos) {
				base.add(entidades.getSigla());
				ArrayList<Integer> dadosEspecificos = new ArrayList<>();
				
				dadosEspecificos.add(SolicitacaoDAO.listarTotalPorEntidade(periodo, entidades.getIdEntidades()).size());
				dadosEspecificos.add(SolicitacaoDAO.listarAtendidasPorEntidade(periodo, entidades.getIdEntidades()).size());
				dadosEspecificos.add(SolicitacaoDAO.listarSemRespostaPorEntidade(periodo, entidades.getIdEntidades()).size());
				dadosEspecificos.add(SolicitacaoDAO.listarEmTramitePorEntidade(periodo, entidades.getIdEntidades()).size());
				dadosEspecificos.add(SolicitacaoDAO.listarNaoVisualizadasPorEntidade(periodo, entidades.getIdEntidades()).size());
				dadosRelacionadorBase.add(dadosEspecificos);
			}
			
		}

		for (int i = 0; i < base.size(); i++) {
			dadosChart.put(base.get(i), dadosRelacionadorBase.get(i));
		}

		return dadosChart;
	}

	
	/**
	 * Esta fun��o retorna os valores dos pedidos de informa��o feitos direcionados a cada entidade ativa.
	 * Os valores se referem a pedido de informa��o total, pedido de informa��o abertos e pedido de informa��o finalizado.
	 * @return
	 */
	public static Map<String, ArrayList<Integer>> gerarAcompanhamentoEntidadePedidoInformacao() {
		Map<String, ArrayList<Integer>> dadosChart = new LinkedHashMap<>();
		Calendar c = Calendar.getInstance();
		int anoAtual = c.get(Calendar.YEAR);
		int mesAtual = c.get(Calendar.MONTH)+1;
		
		String periodo;
		if (mesAtual<=9) {
			periodo = anoAtual + "-0" + mesAtual + "%";
		} else {
			periodo = anoAtual + "-" + mesAtual + "%";
		} 
		
		ArrayList<String> base = new ArrayList<>();
		ArrayList<ArrayList<Integer>> dadosRelacionadorBase = new ArrayList<>();

		ArrayList<Entidades> orgaos = new ArrayList<>(EntidadesDAO.listEntidadesComSolicitacoes(periodo));
		
		if(orgaos.isEmpty()) {
//			orgaos = new ArrayList<>(EntidadesDAO.listEntidades());
//			while(orgaos.size() > 7) {
//				orgaos.remove(orgaos.size()-1);
//			}
			base.add("");
			ArrayList<Integer> dadosEspecificos = new ArrayList<>();
			dadosEspecificos.add(0);
			dadosEspecificos.add(0);
			dadosEspecificos.add(0);
			dadosEspecificos.add(0);
			dadosEspecificos.add(0);
			dadosRelacionadorBase.add(dadosEspecificos);
		}
		else {
			
			for (Entidades entidades : orgaos) {
				base.add(entidades.getSigla());
				ArrayList<Integer> dadosEspecificos = new ArrayList<>();
				
				dadosEspecificos.add(SolicitacaoDAO.listarTotalPorEntidade(periodo, entidades.getIdEntidades()).size());
				dadosEspecificos.add(SolicitacaoDAO.listarAtendidasPorEntidade(periodo, entidades.getIdEntidades()).size());
				dadosEspecificos.add(SolicitacaoDAO.listarSemRespostaPorEntidade(periodo, entidades.getIdEntidades()).size());
				dadosEspecificos.add(SolicitacaoDAO.listarEmTramitePorEntidade(periodo, entidades.getIdEntidades()).size());
				dadosEspecificos.add(SolicitacaoDAO.listarNaoVisualizadasPorEntidade(periodo, entidades.getIdEntidades()).size());
				dadosRelacionadorBase.add(dadosEspecificos);
			}
			
		}

		for (int i = 0; i < base.size(); i++) {
			dadosChart.put(base.get(i), dadosRelacionadorBase.get(i));
		}

		return dadosChart;
	}

	/**
	 * Esta fun��o retorna um dicion�rio informando quantos pedidos de informa��es foram recebidos pelo ponto de vista
	 * de estados, que est�o vinculados aos cidad�os solicitantes.
	 * @return
	 */
	public static Map<String, ArrayList<Integer>> gerarAcompanhamentoEstadosPedidoInformacao() {
		Map<String, ArrayList<Integer>> dadosChart = new LinkedHashMap<>();
		Calendar c = Calendar.getInstance();
		int mesAtual = c.get(Calendar.MONTH)+1;
		int anoAtual = c.get(Calendar.YEAR);
		
		String periodo; 
		if (mesAtual<=9) {
			periodo = anoAtual + "-0" + mesAtual + "%";
		} else {
			periodo = anoAtual + "-" + mesAtual + "%";
		} 
		
		periodo = "%";

		Set<String> uf = new LinkedHashSet<>();
		ArrayList<String> estados = new ArrayList<>(SolicitacaoDAO.listarPorFederacao("Informa��o", periodo));
		if (estados != null) {
			uf = estados.stream().collect(Collectors.toSet());
//			uf.remove("");
		}

		ArrayList<String> base = new ArrayList<>();
		ArrayList<ArrayList<Integer>> dadosRelacionadorBase = new ArrayList<>();
		for (String estadoEnviado : uf) {
			if(estadoEnviado.equals("")) {
				base.add("N�o Informado - " + Collections.frequency(estados, estadoEnviado));
			} else {
				base.add(estadoEnviado + " - " + Collections.frequency(estados, estadoEnviado));
			}
			dadosRelacionadorBase.add(new ArrayList<Integer>(Arrays.asList((Collections.frequency(estados, estadoEnviado)))));
		}

		for (int i = 0; i < base.size(); i++) {
			dadosChart.put(base.get(i), dadosRelacionadorBase.get(i));
		}

		return dadosChart;
	
	}

	
	/**
	 * Esta fun��o verifica qual o tipo de pessoa (f�sica ou jur�dica) e qual o g�nero (feminino ou masculino) dos cidad�os
	 * ligados aos pedidos de informa��o.
	 * @return
	 */
	public static Map<String, ArrayList<Integer>> gerarAcompanhamentoTipoPessoaGeneroPedidoInformacao() {
		Map<String, ArrayList<Integer>> dadosChart = new HashMap<>();
		Calendar c = Calendar.getInstance();
		int mesAtual = c.get(Calendar.MONTH)+1;
		int anoAtual = c.get(Calendar.YEAR);
		
		String periodo; 
		if (mesAtual<=9) {
			periodo = anoAtual + "-0" + mesAtual + "%";
		} else {
			periodo = anoAtual + "-" + mesAtual + "%";
		} 
		
		periodo = "%";

		ArrayList<String> base = new ArrayList<>();
		ArrayList<ArrayList<Integer>> dadosRelacionadorBase = new ArrayList<>();

		ArrayList<Cidadao> pessoas = new ArrayList<Cidadao>(SolicitacaoDAO.listarCidadao("Informa��o", periodo));
		int numeroJuridica = 0;
		int numeroFisicaFeminino = 0;
		int numeroFisicaMasculino = 0;
		for (Cidadao cidadao : pessoas) {
			try{
				if (cidadao.getTipo().equals(true)) {
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
				} else {
					numeroJuridica++;
				}
			}catch (NullPointerException e) {
			}
		}

		base.add("Pessoa Jur�dica - " + numeroJuridica );
		dadosRelacionadorBase.add(new ArrayList<>(Arrays.asList(numeroJuridica)));
		base.add("Pessoa F�sica Feminino - " + numeroFisicaFeminino);
		dadosRelacionadorBase.add(new ArrayList<>(Arrays.asList(numeroFisicaFeminino)));
		base.add("Pessoa F�sica Masculino - " + numeroFisicaMasculino);
		dadosRelacionadorBase.add(new ArrayList<>(Arrays.asList(numeroFisicaMasculino)));
		base.add("Pessoa N�o Identificada - " + (pessoas.size()-(numeroFisicaFeminino+numeroFisicaMasculino+numeroJuridica)));
		dadosRelacionadorBase.add(new ArrayList<>(Arrays.asList(pessoas.size()-(numeroFisicaFeminino+numeroFisicaMasculino+numeroJuridica))));

		for (int i = 0; i < base.size(); i++) {
			dadosChart.put(base.get(i), dadosRelacionadorBase.get(i));
		}

		return dadosChart;
	}

	
	/**
	 * Esta fun��o faz uma busca nos assuntos dos pedidos de informa��o recebidos. 
	 * @return
	 */
	public static Map<String, ArrayList<Integer>> gerarAcompanhamentoAssuntoPedidoInformacao() {
		Map<String, ArrayList<Integer>> dadosChart = new HashMap<>();
		Calendar c = Calendar.getInstance();
		int mesAtual = c.get(Calendar.MONTH)+1;
		int anoAtual = c.get(Calendar.YEAR);
		
		String periodo; 
		if (mesAtual<=9) {
			periodo = anoAtual + "-0" + mesAtual + "%";
		} else {
			periodo = anoAtual + "-" + mesAtual + "%";
		} 
		

		ArrayList<String> base = new ArrayList<>();
		ArrayList<ArrayList<Integer>> dadosRelacionadorBase = new ArrayList<>();

		Set<String> assunto = new HashSet<>();
		ArrayList<String> assuntos = new ArrayList<>(SolicitacaoDAO.listarAssuntos("Informa��o", periodo));

		if (assuntos != null) {
			assunto = assuntos.stream().collect(Collectors.toSet());
			assunto.remove("");
		}
		
		if (assuntos.size() == 0 ) {
			assunto.add("Nenhum");
		}

		for (String ass : assunto) {
			base.add(ass + " - " + Collections.frequency(assuntos, ass));
			dadosRelacionadorBase.add(new ArrayList<Integer>(Arrays.asList((Collections.frequency(assuntos, ass)))));
		}

		for (int i = 0; i < base.size(); i++) {
			dadosChart.put(base.get(i), dadosRelacionadorBase.get(i));
		}

		return dadosChart;
	}
	
//	===============================================================
//	Fun��es para gera��o de Relat�rio Completo por �rg�o / Entidade
//	===============================================================
	
	public static Map<String, ArrayList<Integer>> gerarAcompanhamentoGeralPorEntidade(int idEntidade) {
		int numeroTotal = SolicitacaoDAO.listarTotalPorEntidade("%", idEntidade).size();
		int numeroTotalAtendidos = SolicitacaoDAO.listarAtendidasPorEntidade("%", idEntidade).size();
		int numeroTotalTramite = SolicitacaoDAO.listarEmTramitePorEntidade("%", idEntidade).size();
		int numeroTotalNaoVisualizados = SolicitacaoDAO.listarNaoVisualizadasPorEntidade("%", idEntidade).size();
		int numeroTotalSemResposta = SolicitacaoDAO.listarSemRespostaPorEntidade("%", idEntidade).size();
	
		String[] baseChart = { "Total", "Atendidas", "Sem Resposta", "Em Tr�mite", "N�o Visualizadas" };
		int[] dadosRelacionadosBase = { numeroTotal, numeroTotalAtendidos, numeroTotalSemResposta, numeroTotalTramite, numeroTotalNaoVisualizados };
		Map<String, ArrayList<Integer>> dadosChart = new LinkedHashMap<>();
		for (int i = 0; i < baseChart.length; i++) {
			ArrayList<Integer> dados = new ArrayList<>(Arrays.asList(dadosRelacionadosBase[i]));
			dadosChart.put(baseChart[i], dados);
		}
	
		return dadosChart;
	}
	
	public static Map<String, ArrayList<Integer>> gerarAcompanhamentoMensalPorEntidade(int idEntidade) {
		Map<String, ArrayList<Integer>> dadosChart = new LinkedHashMap<>();
		Calendar c = Calendar.getInstance();
		int mesAtual = c.get(Calendar.MONTH)+1;
		int anoAtual = c.get(Calendar.YEAR);
		ArrayList<String> base = new ArrayList<>();
		ArrayList<ArrayList<Integer>> dadosRelacionadorBase = new ArrayList<>();
	
		for (int i = 1; i <= mesAtual; i++) {
			base.add(meses[i - 1]);
			ArrayList<Integer> dadosEspecificos = new ArrayList<>();
			
			if (i<=9) {
				dadosEspecificos.add(SolicitacaoDAO.listarTotalPorEntidade(anoAtual + "-0" + i + "%", idEntidade).size());
				dadosEspecificos.add(SolicitacaoDAO.listarAtendidasPorEntidade(anoAtual + "-0" + i + "%", idEntidade).size());
				dadosEspecificos.add(SolicitacaoDAO.listarSemRespostaPorEntidade(anoAtual + "-0" + i + "%", idEntidade).size());
				dadosEspecificos.add(SolicitacaoDAO.listarEmTramitePorEntidade(anoAtual + "-0" + i + "%", idEntidade).size());
				dadosEspecificos.add(SolicitacaoDAO.listarNaoVisualizadasPorEntidade(anoAtual + "-0" + i + "%", idEntidade).size());
				dadosRelacionadorBase.add(dadosEspecificos);
			} else {
				dadosEspecificos.add(SolicitacaoDAO.listarTotalPorEntidade(anoAtual + "-" + i + "%", idEntidade).size());
				dadosEspecificos.add(SolicitacaoDAO.listarAtendidasPorEntidade(anoAtual + "-" + i + "%", idEntidade).size());
				dadosEspecificos.add(SolicitacaoDAO.listarSemRespostaPorEntidade(anoAtual + "-" + i + "%", idEntidade).size());
				dadosEspecificos.add(SolicitacaoDAO.listarEmTramitePorEntidade(anoAtual + "-" + i + "%", idEntidade).size());
				dadosEspecificos.add(SolicitacaoDAO.listarNaoVisualizadasPorEntidade(anoAtual + "-" + i + "%", idEntidade).size());
				dadosRelacionadorBase.add(dadosEspecificos);
			} 
			
		}
	
		for (int i = 0; i < base.size(); i++) {
			dadosChart.put(base.get(i), dadosRelacionadorBase.get(i));
		}
		return dadosChart;
	}
	
	public static Map<String, ArrayList<Integer>> gerarAcompanhamentoAnualPorEntidade(int idEntidade) {
		Map<String, ArrayList<Integer>> dadosChart = new LinkedHashMap<>();
		Calendar c = Calendar.getInstance();
		Integer anoAtual = c.get(Calendar.YEAR);
		ArrayList<String> base = new ArrayList<>();
		ArrayList<ArrayList<Integer>> dadosRelacionadorBase = new ArrayList<>();
		
		for (int i = anoInicial; i <= anoAtual; i++) {
			
			base.add(String.valueOf(i));
			ArrayList<Integer> dadosEspecificos = new ArrayList<>();
			dadosEspecificos.add(SolicitacaoDAO.listarTotalPorEntidade(i + "%", idEntidade).size());
			dadosEspecificos.add(SolicitacaoDAO.listarAtendidasPorEntidade(i + "%", idEntidade).size());
			dadosEspecificos.add(SolicitacaoDAO.listarSemRespostaPorEntidade(i + "%", idEntidade).size());
			dadosEspecificos.add(SolicitacaoDAO.listarEmTramitePorEntidade(i + "%", idEntidade).size());
			dadosEspecificos.add(SolicitacaoDAO.listarNaoVisualizadasPorEntidade(i + "%", idEntidade).size());
			dadosRelacionadorBase.add(dadosEspecificos);
		}
		
		for (int i = 0; i < base.size(); i++) {
			dadosChart.put(base.get(i), dadosRelacionadorBase.get(i));
		}
		
		return dadosChart;
	}

	
	public static Map<String, ArrayList<Integer>> gerarAcompanhamentoAnualAcumuladoPorEntidade(int idEntidade) {
		Map<String, ArrayList<Integer>> dadosChart = new LinkedHashMap<>();
		Calendar c = Calendar.getInstance();
		int anoAtual = c.get(Calendar.YEAR);
		ArrayList<String> base = new ArrayList<>();
		ArrayList<ArrayList<Integer>> dadosRelacionadorBase = new ArrayList<>();

		for (int i = anoInicial; i <= anoAtual; i++) {
			base.add(String.valueOf(i));
			ArrayList<Integer> dadosEspecificos = new ArrayList<>();
			int pedidosTotalPeriodo = 0;
			int pedidosAtendidasPeriodo = 0;
			int pedidosSemRespostaPeriodo = 0;
			int pedidosTramitePeriodo = 0;
			int pedidosNaoVisualizadasPeriodo = 0;
			
			for (int ano = anoInicial; ano <= i; ano++) {
				pedidosTotalPeriodo += SolicitacaoDAO.listarTotalPorEntidade(ano + "%", idEntidade).size();
				pedidosAtendidasPeriodo += SolicitacaoDAO.listarAtendidasPorEntidade(ano + "%", idEntidade).size();
				pedidosSemRespostaPeriodo += SolicitacaoDAO.listarSemRespostaPorEntidade(ano + "%", idEntidade).size();
				pedidosTramitePeriodo += SolicitacaoDAO.listarEmTramitePorEntidade(ano + "%", idEntidade).size();
				pedidosNaoVisualizadasPeriodo += SolicitacaoDAO.listarNaoVisualizadasPorEntidade(ano + "%", idEntidade).size();
				
			}
			
			dadosEspecificos.add(pedidosTotalPeriodo);
			dadosEspecificos.add(pedidosAtendidasPeriodo);
			dadosEspecificos.add(pedidosSemRespostaPeriodo);
			dadosEspecificos.add(pedidosTramitePeriodo);
			dadosEspecificos.add(pedidosNaoVisualizadasPeriodo);
			dadosRelacionadorBase.add(dadosEspecificos);
		}
		
		for (int i = 0; i < base.size(); i++) {
			dadosChart.put(base.get(i), dadosRelacionadorBase.get(i));
		}

		return dadosChart;
	}
	
	public static Map<String, ArrayList<Integer>> gerarAcompanhamentoAssuntoPorEntidade(int idEntidade) {
		Map<String, ArrayList<Integer>> dadosChart = new HashMap<>();
		Calendar c = Calendar.getInstance();
		int mesAtual = c.get(Calendar.MONTH)+1;
		int anoAtual = c.get(Calendar.YEAR);
		
		String periodo; 
		if (mesAtual<=9) {
			periodo = anoAtual + "-0" + mesAtual + "%";
		} else {
			periodo = anoAtual + "-" + mesAtual + "%";
		} 
		

		ArrayList<String> base = new ArrayList<>();
		ArrayList<ArrayList<Integer>> dadosRelacionadorBase = new ArrayList<>();

		Set<String> assunto = new HashSet<>();
		ArrayList<String> assuntos = new ArrayList<>(SolicitacaoDAO.listarAssuntosPorEntidade("Informa��o", periodo, idEntidade));

		if (assuntos != null) {
			assunto = assuntos.stream().collect(Collectors.toSet());
			assunto.remove("");
		}
		
		if (assuntos.size() == 0 ) {
			assunto.add("Nenhum");
		}

		for (String ass : assunto) {
			base.add(ass + " - " + Collections.frequency(assuntos, ass));
			dadosRelacionadorBase.add(new ArrayList<Integer>(Arrays.asList((Collections.frequency(assuntos, ass)))));
		}

		for (int i = 0; i < base.size(); i++) {
			dadosChart.put(base.get(i), dadosRelacionadorBase.get(i));
		}

		return dadosChart;
	}
	
	public static Map<String, ArrayList<Integer>> gerarAcompanhamentoTipoPessoaGeneroPorEntidade(int idEntidade) {
		Map<String, ArrayList<Integer>> dadosChart = new HashMap<>();
		Calendar c = Calendar.getInstance();
		int mesAtual = c.get(Calendar.MONTH)+1;
		int anoAtual = c.get(Calendar.YEAR);
		
		String periodo; 
		if (mesAtual<=9) {
			periodo = anoAtual + "-0" + mesAtual + "%";
		} else {
			periodo = anoAtual + "-" + mesAtual + "%";
		} 
		
		periodo = "%";

		ArrayList<String> base = new ArrayList<>();
		ArrayList<ArrayList<Integer>> dadosRelacionadorBase = new ArrayList<>();

		ArrayList<Cidadao> pessoas = new ArrayList<Cidadao>(SolicitacaoDAO.listarCidadaoPorEntidade("Informa��o", periodo, idEntidade));
		int numeroJuridica = 0;
		int numeroFisicaFeminino = 0;
		int numeroFisicaMasculino = 0;
		for (Cidadao cidadao : pessoas) {
			try{
				if (cidadao.getTipo().equals(true)) {
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
				} else {
					numeroJuridica++;
				}
			}catch (NullPointerException e) {
			}
		}

		base.add("Pessoa Jur�dica - " + numeroJuridica );
		dadosRelacionadorBase.add(new ArrayList<>(Arrays.asList(numeroJuridica)));
		base.add("Pessoa F�sica Feminino - " + numeroFisicaFeminino);
		dadosRelacionadorBase.add(new ArrayList<>(Arrays.asList(numeroFisicaFeminino)));
		base.add("Pessoa F�sica Masculino - " + numeroFisicaMasculino);
		dadosRelacionadorBase.add(new ArrayList<>(Arrays.asList(numeroFisicaMasculino)));
		base.add("Pessoa N�o Identificada - " + (pessoas.size()-(numeroFisicaFeminino+numeroFisicaMasculino+numeroJuridica)));
		dadosRelacionadorBase.add(new ArrayList<>(Arrays.asList(pessoas.size()-(numeroFisicaFeminino+numeroFisicaMasculino+numeroJuridica))));

		for (int i = 0; i < base.size(); i++) {
			dadosChart.put(base.get(i), dadosRelacionadorBase.get(i));
		}

		return dadosChart;
	}
	
	public static Map<String, ArrayList<Integer>> gerarAcompanhamentoEstadosPorEntidade(int idEntidade) {
		Map<String, ArrayList<Integer>> dadosChart = new LinkedHashMap<>();
		Calendar c = Calendar.getInstance();
		int mesAtual = c.get(Calendar.MONTH)+1;
		int anoAtual = c.get(Calendar.YEAR);
		
		String periodo; 
		if (mesAtual<=9) {
			periodo = anoAtual + "-0" + mesAtual + "%";
		} else {
			periodo = anoAtual + "-" + mesAtual + "%";
		} 
		
		periodo = "%";

		Set<String> uf = new LinkedHashSet<>();
		ArrayList<String> estados = new ArrayList<>(SolicitacaoDAO.listarPorFederacaoPorEntidade("Informa��o", periodo, idEntidade));
		if (estados != null) {
			uf = estados.stream().collect(Collectors.toSet());
//			uf.remove("");
		}

		ArrayList<String> base = new ArrayList<>();
		ArrayList<ArrayList<Integer>> dadosRelacionadorBase = new ArrayList<>();
		
		if (uf.size() == 0 ) {
			uf.add("Nenhum");
		} 
		
		for (String estadoEnviado : uf) {
			if(estadoEnviado.equals("")) {
				base.add("N�o Informado - " + Collections.frequency(estados, estadoEnviado));
			} else {
				base.add(estadoEnviado + " - " + Collections.frequency(estados, estadoEnviado));
			}
			dadosRelacionadorBase.add(new ArrayList<Integer>(Arrays.asList((Collections.frequency(estados, estadoEnviado)))));
		}
		
		for (int i = 0; i < base.size(); i++) {
			dadosChart.put(base.get(i), dadosRelacionadorBase.get(i));
		}

		return dadosChart;
	
	}
		
/*	//Forma de fun��o para montar estrutura para ser lida na classe Relatorios
 * // e que acaba sendo fonte de informa��o para plotar no gr�fico.
 * public static Map<String, ArrayList<Integer>> nome() {
		Map<String, ArrayList<Integer>> dadosChart = new HashMap<>();
		Calendar c = Calendar.getInstance();
		int mesAtual = c.get(Calendar.MONTH)+1;
		int anoAtual = c.get(Calendar.YEAR);
		String periodo = anoAtual + "-0" + mesAtual + "%";
		
		ArrayList<String> base = new ArrayList<>();
		ArrayList<ArrayList<Integer>> dadosRelacionadorBase = new ArrayList<>();
		
		Set<String> uf = new HashSet<>();
		
		for (int i = 0; i < base.size(); i++) {
			dadosChart.put(base.get(i), dadosRelacionadorBase.get(i));
		}
		
		return dadosChart;
	}*/

	
}
