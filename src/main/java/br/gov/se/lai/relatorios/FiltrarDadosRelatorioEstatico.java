package br.gov.se.lai.relatorios;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
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
	protected final static int anoInicial = 2012;				//Comentado por motivo de não existe armazenamento disponível dos anos anteriores.
//	protected final static int anoInicial = 2018;				//Contagem iniciando no ano vigente do sistema.
	
	
	
	/**
	 * Gerar Relatório Final
	 * 
	 * Retorna um número de total de Pedidos(solicitacoes),
	 * Atendimentos(finalizado), Em trâmite e Em aberto(não visualizado) em uma
	 * gráfico de barras, onde a base são os status dos pedidos de informação e os
	 * valores são os números totais referentes.
	 */
	public static Map<String, ArrayList<Integer>> gerarAcompanhamentoGeralDosPedidosInformacao() {
		int numeroTotal = SolicitacaoDAO.listarGeral().size();
		int numeroTotalTramite= SolicitacaoDAO.listPorStatus("Aberta").size() 
								+ SolicitacaoDAO.listPorStatus("Transição").size() 
								+ SolicitacaoDAO.listPorStatus("Atendida").size() 
								+ SolicitacaoDAO.listPorStatus("Recurso").size() 
								+ SolicitacaoDAO.listPorStatus("Prorrogada").size() 
								+ SolicitacaoDAO.listPorStatus("Reencaminhada").size();
//		int numeroTotalAtendidos = SolicitacaoDAO.listPorStatus("Atendida").size();
		int numeroTotalFinalizados = SolicitacaoDAO.listarGeralFinalizada().size();
		int numeroTotalSemResposta = SolicitacaoDAO.listPorStatus("Sem Resposta").size() + SolicitacaoDAO.listPorStatus("Negada").size();

		String[] baseChart = { "Total", "Em Trâmite", "Finalizados", "Sem Resposta" };
		int[] dadosRelacionadosBase = { numeroTotal, numeroTotalTramite, numeroTotalFinalizados, numeroTotalSemResposta };
		Map<String, ArrayList<Integer>> dadosChart = new HashMap<>();
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
		Map<String, ArrayList<Integer>> dadosChart = new HashMap<>();
		Calendar c = Calendar.getInstance();
		int mesAtual = c.get(Calendar.MONTH)+1;
		int anoAtual = c.get(Calendar.YEAR);
		ArrayList<String> base = new ArrayList<>();
		ArrayList<ArrayList<Integer>> dadosRelacionadorBase = new ArrayList<>();

		for (int i = 1; i <= mesAtual; i++) {
			base.add(meses[i - 1]);
			ArrayList<Integer> dadosEspecificos = new ArrayList<>();
			
			if (i<=9) {
				dadosEspecificos.add(SolicitacaoDAO.listPorTipoStatusPeriodo("Informação", "Aberta", anoAtual + "-0" + i + "%").size() 
						+ SolicitacaoDAO.listPorTipoStatusPeriodo("Informação", "Transição", anoAtual + "-0" + i + "%").size()
						+ SolicitacaoDAO.listPorTipoStatusPeriodo("Informação", "Atendida", anoAtual + "-0" + i + "%").size()
						+ SolicitacaoDAO.listPorTipoStatusPeriodo("Informação", "Recurso", anoAtual + "-0" + i + "%").size()
						+ SolicitacaoDAO.listPorTipoStatusPeriodo("Informação", "Prorrogada", anoAtual + "-0" + i + "%").size()
						+ SolicitacaoDAO.listPorTipoStatusPeriodo("Informação", "Reencaminhada", anoAtual + "-0" + i + "%").size()	);
				dadosEspecificos.add(SolicitacaoDAO.listPorTipoStatusPeriodo("Informação", "Finalizada", anoAtual + "-0" + i + "%").size());
				dadosEspecificos.add(SolicitacaoDAO.listPorTipoStatusPeriodo("Informação", "Sem Resposta", anoAtual + "-0" + i + "%").size());
				dadosEspecificos.add(SolicitacaoDAO.listPorTipoPeriodo("Informação", anoAtual + "-0" + i + "%").size());
				dadosRelacionadorBase.add(dadosEspecificos);
			} else {
				dadosEspecificos.add(SolicitacaoDAO.listPorTipoStatusPeriodo("Informação", "Aberta", anoAtual + "-" + i + "%").size() 
						+ SolicitacaoDAO.listPorTipoStatusPeriodo("Informação", "Transição", anoAtual + "-" + i + "%").size()
						+ SolicitacaoDAO.listPorTipoStatusPeriodo("Informação", "Atendida", anoAtual + "-" + i + "%").size()
						+ SolicitacaoDAO.listPorTipoStatusPeriodo("Informação", "Recurso", anoAtual + "-" + i + "%").size()
						+ SolicitacaoDAO.listPorTipoStatusPeriodo("Informação", "Prorrogada", anoAtual + "-" + i + "%").size()
						+ SolicitacaoDAO.listPorTipoStatusPeriodo("Informação", "Reencaminhada", anoAtual + "-" + i + "%").size()	);
				dadosEspecificos.add(SolicitacaoDAO.listPorTipoStatusPeriodo("Informação", "Finalizada", anoAtual + "-" + i + "%").size());
				dadosEspecificos.add(SolicitacaoDAO.listPorTipoStatusPeriodo("Informação", "Sem Resposta", anoAtual + "-" + i + "%").size());
				dadosEspecificos.add(SolicitacaoDAO.listPorTipoPeriodo("Informação", anoAtual + "-" + i + "%").size());
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
		Map<String, ArrayList<Integer>> dadosChart = new HashMap<>();
		Calendar c = Calendar.getInstance();
		int anoAtual = c.get(Calendar.YEAR);
		ArrayList<String> base = new ArrayList<>();
		ArrayList<ArrayList<Integer>> dadosRelacionadorBase = new ArrayList<>();

		for (int i = anoInicial; i <= anoAtual; i++) {
			base.add(String.valueOf(i));
			ArrayList<Integer> dadosEspecificos = new ArrayList<>();
			dadosEspecificos.add(SolicitacaoDAO.listPorTipoStatusPeriodo("Informação", "Aberta", i + "%").size() 
					+ SolicitacaoDAO.listPorTipoStatusPeriodo("Informação", "Transição", i + "%").size()
					+ SolicitacaoDAO.listPorTipoStatusPeriodo("Informação", "Atendida", i + "%").size()
					+ SolicitacaoDAO.listPorTipoStatusPeriodo("Informação", "Recurso", i + "%").size()
					+ SolicitacaoDAO.listPorTipoStatusPeriodo("Informação", "Prorrogada", i + "%").size()
					+ SolicitacaoDAO.listPorTipoStatusPeriodo("Informação", "Reencaminhada", i + "%").size()	);
			dadosEspecificos.add(SolicitacaoDAO.listPorTipoStatusPeriodo("Informação", "Finalizada", i + "%").size());
			dadosEspecificos.add(SolicitacaoDAO.listPorTipoStatusPeriodo("Informação", "Sem Resposta", i + "%").size());
			dadosEspecificos.add(SolicitacaoDAO.listPorTipoPeriodo("Informação", i + "%").size());
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
		Map<String, ArrayList<Integer>> dadosChart = new HashMap<>();
		Calendar c = Calendar.getInstance();
		int anoAtual = c.get(Calendar.YEAR);
		ArrayList<String> base = new ArrayList<>();
		ArrayList<ArrayList<Integer>> dadosRelacionadorBase = new ArrayList<>();

		for (int i = anoAtual; i >= anoInicial; i--) {
			base.add(String.valueOf(i));
			ArrayList<Integer> dadosEspecificos = new ArrayList<>();
			int pedidosTotalPeriodo = 0;
			int pedidosTramitePeriodo = 0;
			int pedidosFinalizadosPeriodo = 0;
			int pedidosSemRespostaPeriodo = 0;
			
			for (int ano = anoInicial; ano <= i; ano++) {
				pedidosTramitePeriodo += SolicitacaoDAO.listPorTipoStatusPeriodo("Informação", "Aberta", ano + "%").size() 
						+ SolicitacaoDAO.listPorTipoStatusPeriodo("Informação", "Transição", ano + "%").size()
						+ SolicitacaoDAO.listPorTipoStatusPeriodo("Informação", "Atendida", ano + "%").size()
						+ SolicitacaoDAO.listPorTipoStatusPeriodo("Informação", "Recurso", ano + "%").size()
						+ SolicitacaoDAO.listPorTipoStatusPeriodo("Informação", "Prorrogada", ano + "%").size()
						+ SolicitacaoDAO.listPorTipoStatusPeriodo("Informação", "Reecaminhada", ano + "%").size();
				pedidosFinalizadosPeriodo += SolicitacaoDAO.listPorTipoStatusPeriodo("Informação", "Finalizada", ano + "%").size();
				pedidosSemRespostaPeriodo = SolicitacaoDAO.listPorTipoStatusPeriodo("Informação","Sem Resposta", ano + "%").size();
				pedidosTotalPeriodo += SolicitacaoDAO.listPorTipoPeriodo("Informação", ano + "%").size();
			}
			
			dadosEspecificos.add(pedidosTramitePeriodo);
			dadosEspecificos.add(pedidosFinalizadosPeriodo);
			dadosEspecificos.add(pedidosSemRespostaPeriodo);
			dadosEspecificos.add(pedidosTotalPeriodo);
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
		Map<String, ArrayList<Integer>> dadosChart = new HashMap<>();

		ArrayList<String> base = new ArrayList<>();
		ArrayList<ArrayList<Integer>> dadosRelacionadorBase = new ArrayList<>();

		ArrayList<Entidades> orgaos = new ArrayList<>(EntidadesDAO.listOrgaos());

		for (Entidades entidades : orgaos) {
			base.add(entidades.getSigla());
			ArrayList<Integer> dadosEspecificos = new ArrayList<>();
			
				dadosEspecificos.add(SolicitacaoDAO.listarPorEntidade(entidades.getIdEntidades(), "Informação", "Aberta", "%").size() 
						+ SolicitacaoDAO.listarPorEntidade(entidades.getIdEntidades(), "Informação", "Transição", "%").size()
						+ SolicitacaoDAO.listarPorEntidade(entidades.getIdEntidades(), "Informação", "Atendida", "%").size()
						+ SolicitacaoDAO.listarPorEntidade(entidades.getIdEntidades(), "Informação", "Recurso", "%").size()
						+ SolicitacaoDAO.listarPorEntidade(entidades.getIdEntidades(), "Informação", "Prorrogada", "%").size()
						+ SolicitacaoDAO.listarPorEntidade(entidades.getIdEntidades(), "Informação", "Reecaminhada", "%").size());
				dadosEspecificos.add(SolicitacaoDAO.listarPorEntidade(entidades.getIdEntidades(), "Informação", "Finalizada", "%").size());
				dadosEspecificos.add(SolicitacaoDAO.listarPorEntidade(entidades.getIdEntidades(), "Informação", "Sem Resposta", "%").size());
				dadosRelacionadorBase.add(dadosEspecificos);
			
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
		Map<String, ArrayList<Integer>> dadosChart = new HashMap<>();

		ArrayList<String> base = new ArrayList<>();
		ArrayList<ArrayList<Integer>> dadosRelacionadorBase = new ArrayList<>();

		ArrayList<Entidades> orgaos = new ArrayList<>(EntidadesDAO.listEntidades());

		for (Entidades entidades : orgaos) {
			base.add(entidades.getSigla());
			ArrayList<Integer> dadosEspecificos = new ArrayList<>();
			 
				dadosEspecificos.add(SolicitacaoDAO.listarPorEntidade(entidades.getIdEntidades(), "Informação", "Aberta", "%").size() 
						+ SolicitacaoDAO.listarPorEntidade(entidades.getIdEntidades(), "Informação", "Transição", "%").size()
						+ SolicitacaoDAO.listarPorEntidade(entidades.getIdEntidades(), "Informação", "Atendida", "%").size()
						+ SolicitacaoDAO.listarPorEntidade(entidades.getIdEntidades(), "Informação", "Recurso", "%").size()
						+ SolicitacaoDAO.listarPorEntidade(entidades.getIdEntidades(), "Informação", "Prorrogada", "%").size()
						+ SolicitacaoDAO.listarPorEntidade(entidades.getIdEntidades(), "Informação", "Reecaminhada", "%").size());
				dadosEspecificos.add(SolicitacaoDAO.listarPorEntidade(entidades.getIdEntidades(), "Informação", "Finalizada", "%").size());
				dadosEspecificos.add(SolicitacaoDAO.listarPorEntidade(entidades.getIdEntidades(), "Informação", "Sem Resposta", "%").size());
				dadosRelacionadorBase.add(dadosEspecificos);
			
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
		Map<String, ArrayList<Integer>> dadosChart = new HashMap<>();
		Calendar c = Calendar.getInstance();
		int mesAtual = c.get(Calendar.MONTH)+1;
		int anoAtual = c.get(Calendar.YEAR);
		String auxp; 
		if (mesAtual<=9) {
			auxp = anoAtual + "-0" + mesAtual + "%";
		} else {
			auxp = anoAtual + "-" + mesAtual + "%";
		} 
		String periodo = auxp;

		Set<String> uf = new HashSet<>();
		ArrayList<String> estados = new ArrayList<>(SolicitacaoDAO.listarPorFederacao("Informação", periodo));
		if (estados != null) {
			uf = estados.stream().collect(Collectors.toSet());
			uf.remove("");
		}

		ArrayList<String> base = new ArrayList<>();
		ArrayList<ArrayList<Integer>> dadosRelacionadorBase = new ArrayList<>();
		for (String estadoEnviado : uf) {
			base.add(estadoEnviado);
			dadosRelacionadorBase
					.add(new ArrayList<Integer>(Arrays.asList((Collections.frequency(estados, estadoEnviado)))));
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
		String auxp; 
		if (mesAtual<=9) {
			auxp = anoAtual + "-0" + mesAtual + "%";
		} else {
			auxp = anoAtual + "-" + mesAtual + "%";
		} 
		String periodo = auxp;

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

		base.add("Pessoa Jurídica");
		dadosRelacionadorBase.add(new ArrayList<>(Arrays.asList(numeroJuridica)));
		base.add("Pessoa Física Feminino");
		dadosRelacionadorBase.add(new ArrayList<>(Arrays.asList(numeroFisicaFeminino)));
		base.add("Pessoa Física Masculino");
		dadosRelacionadorBase.add(new ArrayList<>(Arrays.asList(numeroFisicaMasculino)));
		base.add("Pessoa Não Identificada");
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
		String auxp; 
		if (mesAtual<=9) {
			auxp = anoAtual + "-0" + mesAtual + "%";
		} else {
			auxp = anoAtual + "-" + mesAtual + "%";
		} 
		String periodo = auxp;

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
			base.add(ass);
			dadosRelacionadorBase.add(new ArrayList<Integer>(Arrays.asList((Collections.frequency(assuntos, ass)))));
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
