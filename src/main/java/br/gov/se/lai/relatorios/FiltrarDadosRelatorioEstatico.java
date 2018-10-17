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
	protected final static int anoInicial = 2012;				//Comentado por motivo de n�o existe armazenamento dispon�vel dos anos anteriores.
//	protected final static int anoInicial = 2018;				//Contagem iniciando no ano vigente do sistema.
	
	
	
	/**
	 * Gerar Relat�rio Final
	 * 
	 * Retorna um n�mero de total de Pedidos(solicitacoes),
	 * Atendimentos(finalizado), Em tr�mite e Em aberto(n�o visualizado) em uma
	 * gr�fico de barras, onde a base s�o os status dos pedidos de informa��o e os
	 * valores s�o os n�meros totais referentes.
	 */
	public static Map<String, ArrayList<Integer>> gerarAcompanhamentoGeralDosPedidosInformacao() {
		int numeroTotal = SolicitacaoDAO.listarGeral().size();
		int numeroTotalTramite= SolicitacaoDAO.listPorStatus("Aberta").size() 
								+ SolicitacaoDAO.listPorStatus("Transi��o").size() 
								+ SolicitacaoDAO.listPorStatus("Atendida").size() 
								+ SolicitacaoDAO.listPorStatus("Recurso").size() 
								+ SolicitacaoDAO.listPorStatus("Prorrogada").size() 
								+ SolicitacaoDAO.listPorStatus("Reencaminhada").size();
//		int numeroTotalAtendidos = SolicitacaoDAO.listPorStatus("Atendida").size();
		int numeroTotalFinalizados = SolicitacaoDAO.listarGeralFinalizada().size();
		int numeroTotalSemResposta = SolicitacaoDAO.listPorStatus("Sem Resposta").size() + SolicitacaoDAO.listPorStatus("Negada").size();

		String[] baseChart = { "Total", "Em Tr�mite", "Finalizados", "Sem Resposta" };
		int[] dadosRelacionadosBase = { numeroTotal, numeroTotalTramite, numeroTotalFinalizados, numeroTotalSemResposta };
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
		int mesAtual = c.get(Calendar.MONTH)+1;
		int anoAtual = c.get(Calendar.YEAR);
		ArrayList<String> base = new ArrayList<>();
		ArrayList<ArrayList<Integer>> dadosRelacionadorBase = new ArrayList<>();

		for (int i = 1; i <= mesAtual; i++) {
			base.add(meses[i - 1]);
			ArrayList<Integer> dadosEspecificos = new ArrayList<>();
			
			if (i<=9) {
				dadosEspecificos.add(SolicitacaoDAO.listPorTipoStatusPeriodo("Informa��o", "Aberta", anoAtual + "-0" + i + "%").size() 
						+ SolicitacaoDAO.listPorTipoStatusPeriodo("Informa��o", "Transi��o", anoAtual + "-0" + i + "%").size()
						+ SolicitacaoDAO.listPorTipoStatusPeriodo("Informa��o", "Atendida", anoAtual + "-0" + i + "%").size()
						+ SolicitacaoDAO.listPorTipoStatusPeriodo("Informa��o", "Recurso", anoAtual + "-0" + i + "%").size()
						+ SolicitacaoDAO.listPorTipoStatusPeriodo("Informa��o", "Prorrogada", anoAtual + "-0" + i + "%").size()
						+ SolicitacaoDAO.listPorTipoStatusPeriodo("Informa��o", "Reencaminhada", anoAtual + "-0" + i + "%").size()	);
				dadosEspecificos.add(SolicitacaoDAO.listPorTipoStatusPeriodo("Informa��o", "Finalizada", anoAtual + "-0" + i + "%").size());
				dadosEspecificos.add(SolicitacaoDAO.listPorTipoStatusPeriodo("Informa��o", "Sem Resposta", anoAtual + "-0" + i + "%").size());
				dadosEspecificos.add(SolicitacaoDAO.listPorTipoPeriodo("Informa��o", anoAtual + "-0" + i + "%").size());
				dadosRelacionadorBase.add(dadosEspecificos);
			} else {
				dadosEspecificos.add(SolicitacaoDAO.listPorTipoStatusPeriodo("Informa��o", "Aberta", anoAtual + "-" + i + "%").size() 
						+ SolicitacaoDAO.listPorTipoStatusPeriodo("Informa��o", "Transi��o", anoAtual + "-" + i + "%").size()
						+ SolicitacaoDAO.listPorTipoStatusPeriodo("Informa��o", "Atendida", anoAtual + "-" + i + "%").size()
						+ SolicitacaoDAO.listPorTipoStatusPeriodo("Informa��o", "Recurso", anoAtual + "-" + i + "%").size()
						+ SolicitacaoDAO.listPorTipoStatusPeriodo("Informa��o", "Prorrogada", anoAtual + "-" + i + "%").size()
						+ SolicitacaoDAO.listPorTipoStatusPeriodo("Informa��o", "Reencaminhada", anoAtual + "-" + i + "%").size()	);
				dadosEspecificos.add(SolicitacaoDAO.listPorTipoStatusPeriodo("Informa��o", "Finalizada", anoAtual + "-" + i + "%").size());
				dadosEspecificos.add(SolicitacaoDAO.listPorTipoStatusPeriodo("Informa��o", "Sem Resposta", anoAtual + "-" + i + "%").size());
				dadosEspecificos.add(SolicitacaoDAO.listPorTipoPeriodo("Informa��o", anoAtual + "-" + i + "%").size());
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
		Map<String, ArrayList<Integer>> dadosChart = new HashMap<>();
		Calendar c = Calendar.getInstance();
		int anoAtual = c.get(Calendar.YEAR);
		ArrayList<String> base = new ArrayList<>();
		ArrayList<ArrayList<Integer>> dadosRelacionadorBase = new ArrayList<>();

		for (int i = anoInicial; i <= anoAtual; i++) {
			base.add(String.valueOf(i));
			ArrayList<Integer> dadosEspecificos = new ArrayList<>();
			dadosEspecificos.add(SolicitacaoDAO.listPorTipoStatusPeriodo("Informa��o", "Aberta", i + "%").size() 
					+ SolicitacaoDAO.listPorTipoStatusPeriodo("Informa��o", "Transi��o", i + "%").size()
					+ SolicitacaoDAO.listPorTipoStatusPeriodo("Informa��o", "Atendida", i + "%").size()
					+ SolicitacaoDAO.listPorTipoStatusPeriodo("Informa��o", "Recurso", i + "%").size()
					+ SolicitacaoDAO.listPorTipoStatusPeriodo("Informa��o", "Prorrogada", i + "%").size()
					+ SolicitacaoDAO.listPorTipoStatusPeriodo("Informa��o", "Reencaminhada", i + "%").size()	);
			dadosEspecificos.add(SolicitacaoDAO.listPorTipoStatusPeriodo("Informa��o", "Finalizada", i + "%").size());
			dadosEspecificos.add(SolicitacaoDAO.listPorTipoStatusPeriodo("Informa��o", "Sem Resposta", i + "%").size());
			dadosEspecificos.add(SolicitacaoDAO.listPorTipoPeriodo("Informa��o", i + "%").size());
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
				pedidosTramitePeriodo += SolicitacaoDAO.listPorTipoStatusPeriodo("Informa��o", "Aberta", ano + "%").size() 
						+ SolicitacaoDAO.listPorTipoStatusPeriodo("Informa��o", "Transi��o", ano + "%").size()
						+ SolicitacaoDAO.listPorTipoStatusPeriodo("Informa��o", "Atendida", ano + "%").size()
						+ SolicitacaoDAO.listPorTipoStatusPeriodo("Informa��o", "Recurso", ano + "%").size()
						+ SolicitacaoDAO.listPorTipoStatusPeriodo("Informa��o", "Prorrogada", ano + "%").size()
						+ SolicitacaoDAO.listPorTipoStatusPeriodo("Informa��o", "Reecaminhada", ano + "%").size();
				pedidosFinalizadosPeriodo += SolicitacaoDAO.listPorTipoStatusPeriodo("Informa��o", "Finalizada", ano + "%").size();
				pedidosSemRespostaPeriodo = SolicitacaoDAO.listPorTipoStatusPeriodo("Informa��o","Sem Resposta", ano + "%").size();
				pedidosTotalPeriodo += SolicitacaoDAO.listPorTipoPeriodo("Informa��o", ano + "%").size();
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
	 * Esta fun��o faz uma contagem dos valores encontrados referentes aos �rg�os ativos armazenados no banco.
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
			
				dadosEspecificos.add(SolicitacaoDAO.listarPorEntidade(entidades.getIdEntidades(), "Informa��o", "Aberta", "%").size() 
						+ SolicitacaoDAO.listarPorEntidade(entidades.getIdEntidades(), "Informa��o", "Transi��o", "%").size()
						+ SolicitacaoDAO.listarPorEntidade(entidades.getIdEntidades(), "Informa��o", "Atendida", "%").size()
						+ SolicitacaoDAO.listarPorEntidade(entidades.getIdEntidades(), "Informa��o", "Recurso", "%").size()
						+ SolicitacaoDAO.listarPorEntidade(entidades.getIdEntidades(), "Informa��o", "Prorrogada", "%").size()
						+ SolicitacaoDAO.listarPorEntidade(entidades.getIdEntidades(), "Informa��o", "Reecaminhada", "%").size());
				dadosEspecificos.add(SolicitacaoDAO.listarPorEntidade(entidades.getIdEntidades(), "Informa��o", "Finalizada", "%").size());
				dadosEspecificos.add(SolicitacaoDAO.listarPorEntidade(entidades.getIdEntidades(), "Informa��o", "Sem Resposta", "%").size());
				dadosRelacionadorBase.add(dadosEspecificos);
			
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
		Map<String, ArrayList<Integer>> dadosChart = new HashMap<>();

		ArrayList<String> base = new ArrayList<>();
		ArrayList<ArrayList<Integer>> dadosRelacionadorBase = new ArrayList<>();

		ArrayList<Entidades> orgaos = new ArrayList<>(EntidadesDAO.listEntidades());

		for (Entidades entidades : orgaos) {
			base.add(entidades.getSigla());
			ArrayList<Integer> dadosEspecificos = new ArrayList<>();
			 
				dadosEspecificos.add(SolicitacaoDAO.listarPorEntidade(entidades.getIdEntidades(), "Informa��o", "Aberta", "%").size() 
						+ SolicitacaoDAO.listarPorEntidade(entidades.getIdEntidades(), "Informa��o", "Transi��o", "%").size()
						+ SolicitacaoDAO.listarPorEntidade(entidades.getIdEntidades(), "Informa��o", "Atendida", "%").size()
						+ SolicitacaoDAO.listarPorEntidade(entidades.getIdEntidades(), "Informa��o", "Recurso", "%").size()
						+ SolicitacaoDAO.listarPorEntidade(entidades.getIdEntidades(), "Informa��o", "Prorrogada", "%").size()
						+ SolicitacaoDAO.listarPorEntidade(entidades.getIdEntidades(), "Informa��o", "Reecaminhada", "%").size());
				dadosEspecificos.add(SolicitacaoDAO.listarPorEntidade(entidades.getIdEntidades(), "Informa��o", "Finalizada", "%").size());
				dadosEspecificos.add(SolicitacaoDAO.listarPorEntidade(entidades.getIdEntidades(), "Informa��o", "Sem Resposta", "%").size());
				dadosRelacionadorBase.add(dadosEspecificos);
			
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
		ArrayList<String> estados = new ArrayList<>(SolicitacaoDAO.listarPorFederacao("Informa��o", periodo));
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
	 * Esta fun��o verifica qual o tipo de pessoa (f�sica ou jur�dica) e qual o g�nero (feminino ou masculino) dos cidad�os
	 * ligados aos pedidos de informa��o.
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

		base.add("Pessoa Jur�dica");
		dadosRelacionadorBase.add(new ArrayList<>(Arrays.asList(numeroJuridica)));
		base.add("Pessoa F�sica Feminino");
		dadosRelacionadorBase.add(new ArrayList<>(Arrays.asList(numeroFisicaFeminino)));
		base.add("Pessoa F�sica Masculino");
		dadosRelacionadorBase.add(new ArrayList<>(Arrays.asList(numeroFisicaMasculino)));
		base.add("Pessoa N�o Identificada");
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
		ArrayList<String> assuntos = new ArrayList<>(SolicitacaoDAO.listarAssuntos("Informa��o", periodo));

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
