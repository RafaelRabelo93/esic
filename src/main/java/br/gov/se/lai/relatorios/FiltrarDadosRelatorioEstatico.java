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
	 * Essa classe se refere a organização do que é preciso para realizar a query no banco de dados
	 * e então tratar o retorno da query para em uma estrutura para que possa ser plotada nos gráficos 
	 * dos gráficos mensais, que são gráficos sobre o pedido de informação realizado no mês e ano atual.
	 * 
	 * Esta classe trata unicamente dos pedidos de informação do ano e mês atual. Caso queira filtrar relatórios
	 * de forma dinâmica vá para a classe FiltrarDadosRelatorioDinamico e RelatorioDinamico.
	 * 
	 * Nessa classe já está prevista a contagem do ano inicial como 2012, pois foi em 2012 que o sistema
	 * esic foi colocado para serviço, então é importante não esquecer que existe um histórico.
	 * 
	 * As funções previstas nessa classe retornam dicionários onde a chave se refere ao eixo x do gráfico 
	 * que será plotado e o valor se refere a um arraylist que armazenará os valores das colunas. O valor é 
	 * um array pois em certos gráfico é necessário plotar valores por status da solicitação (total de pedidos, 
	 * respondidos, aberto..) referentes a um período de tempo.
	 */

	
	protected final static String[] meses = { "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho",
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
	public static Map<String, ArrayList<Integer>> gerarAcompanhamentoGeralDosPedidosInformacao() {
		int numeroTotal = SolicitacaoDAO.listarTotalPorPeriodo("%").size();
		int numeroTotalAtendidos = SolicitacaoDAO.listarAtendidasPorPeriodo("%").size();
		int numeroTotalTramite = SolicitacaoDAO.listarEmTramitePorPeriodo("%").size();
		int numeroTotalNaoVisualizados = SolicitacaoDAO.listarNaoVisualizadasPorPeriodo("%").size();
		int numeroTotalSemResposta = SolicitacaoDAO.listarSemRespostaPorPeriodo("%").size();

		String[] baseChart = { "Total", "Atendidas", "Sem Resposta", "Em Trâmite", "Não Visualizadas" };
		int[] dadosRelacionadosBase = { numeroTotal, numeroTotalAtendidos, numeroTotalSemResposta, numeroTotalTramite, numeroTotalNaoVisualizados };
		Map<String, ArrayList<Integer>> dadosChart = new LinkedHashMap<>();
		for (int i = 0; i < baseChart.length; i++) {
			ArrayList<Integer> dados = new ArrayList<>(Arrays.asList(dadosRelacionadosBase[i]));
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
	 * Função para retornar valores de pedidos de informação não acumulado dos anos
	 * de 2012 até o ano atual.
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
	 * Esta função busca os valores dos pedidos de informação dos anos anteriores até o ano atual de forma que seja acumulada.
	 * Ou seja, o ano seguinte corresponderá ao valor da quantidade de pedidos de informações do ano em questão, somados aos valores
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
	 * Esta função faz uma contagem dos valores encontrados referentes aos órgãos ativos armazenados no banco.
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
	 * Esta função retorna os valores dos pedidos de informação feitos direcionados a cada entidade ativa.
	 * Os valores se referem a pedido de informação total, pedido de informação abertos e pedido de informação finalizado.
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
	 * Esta função retorna um dicionário informando quantos pedidos de informações foram recebidos pelo ponto de vista
	 * de estados, que estão vinculados aos cidadãos solicitantes.
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
		ArrayList<String> estados = new ArrayList<>(SolicitacaoDAO.listarPorFederacao("Informação", periodo));
		if (estados != null) {
			uf = estados.stream().collect(Collectors.toSet());
//			uf.remove("");
		}

		ArrayList<String> base = new ArrayList<>();
		ArrayList<ArrayList<Integer>> dadosRelacionadorBase = new ArrayList<>();
		for (String estadoEnviado : uf) {
			if(estadoEnviado.equals("")) {
				base.add("Não Informado - " + Collections.frequency(estados, estadoEnviado));
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
	 * Esta função verifica qual o tipo de pessoa (física ou jurídica) e qual o gênero (feminino ou masculino) dos cidadãos
	 * ligados aos pedidos de informação.
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

		ArrayList<Cidadao> pessoas = new ArrayList<Cidadao>(SolicitacaoDAO.listarCidadao("Informação", periodo));
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

		base.add("Pessoa Jurídica - " + numeroJuridica );
		dadosRelacionadorBase.add(new ArrayList<>(Arrays.asList(numeroJuridica)));
		base.add("Pessoa Física Feminino - " + numeroFisicaFeminino);
		dadosRelacionadorBase.add(new ArrayList<>(Arrays.asList(numeroFisicaFeminino)));
		base.add("Pessoa Física Masculino - " + numeroFisicaMasculino);
		dadosRelacionadorBase.add(new ArrayList<>(Arrays.asList(numeroFisicaMasculino)));
		base.add("Pessoa Não Identificada - " + (pessoas.size()-(numeroFisicaFeminino+numeroFisicaMasculino+numeroJuridica)));
		dadosRelacionadorBase.add(new ArrayList<>(Arrays.asList(pessoas.size()-(numeroFisicaFeminino+numeroFisicaMasculino+numeroJuridica))));

		for (int i = 0; i < base.size(); i++) {
			dadosChart.put(base.get(i), dadosRelacionadorBase.get(i));
		}

		return dadosChart;
	}

	
	/**
	 * Esta função faz uma busca nos assuntos dos pedidos de informação recebidos. 
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
		ArrayList<String> assuntos = new ArrayList<>(SolicitacaoDAO.listarAssuntos("Informação", periodo));

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
//	Funções para geração de Relatório Completo por Órgão / Entidade
//	===============================================================
	
	public static Map<String, ArrayList<Integer>> gerarAcompanhamentoGeralPorEntidade(int idEntidade) {
		int numeroTotal = SolicitacaoDAO.listarTotalPorEntidade("%", idEntidade).size();
		int numeroTotalAtendidos = SolicitacaoDAO.listarAtendidasPorEntidade("%", idEntidade).size();
		int numeroTotalTramite = SolicitacaoDAO.listarEmTramitePorEntidade("%", idEntidade).size();
		int numeroTotalNaoVisualizados = SolicitacaoDAO.listarNaoVisualizadasPorEntidade("%", idEntidade).size();
		int numeroTotalSemResposta = SolicitacaoDAO.listarSemRespostaPorEntidade("%", idEntidade).size();
	
		String[] baseChart = { "Total", "Atendidas", "Sem Resposta", "Em Trâmite", "Não Visualizadas" };
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
		ArrayList<String> assuntos = new ArrayList<>(SolicitacaoDAO.listarAssuntosPorEntidade("Informação", periodo, idEntidade));

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

		ArrayList<Cidadao> pessoas = new ArrayList<Cidadao>(SolicitacaoDAO.listarCidadaoPorEntidade("Informação", periodo, idEntidade));
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

		base.add("Pessoa Jurídica - " + numeroJuridica );
		dadosRelacionadorBase.add(new ArrayList<>(Arrays.asList(numeroJuridica)));
		base.add("Pessoa Física Feminino - " + numeroFisicaFeminino);
		dadosRelacionadorBase.add(new ArrayList<>(Arrays.asList(numeroFisicaFeminino)));
		base.add("Pessoa Física Masculino - " + numeroFisicaMasculino);
		dadosRelacionadorBase.add(new ArrayList<>(Arrays.asList(numeroFisicaMasculino)));
		base.add("Pessoa Não Identificada - " + (pessoas.size()-(numeroFisicaFeminino+numeroFisicaMasculino+numeroJuridica)));
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
		ArrayList<String> estados = new ArrayList<>(SolicitacaoDAO.listarPorFederacaoPorEntidade("Informação", periodo, idEntidade));
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
				base.add("Não Informado - " + Collections.frequency(estados, estadoEnviado));
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
		
/*	//Forma de função para montar estrutura para ser lida na classe Relatorios
 * // e que acaba sendo fonte de informação para plotar no gráfico.
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
