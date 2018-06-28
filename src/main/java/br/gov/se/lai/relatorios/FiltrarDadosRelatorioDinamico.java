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
import br.gov.se.lai.entity.Entidades;
import br.gov.se.lai.entity.Solicitacao;

public class FiltrarDadosRelatorioDinamico implements Serializable {
	
	/**
	 * Essa classe se refere a organização do que é preciso para realizar a query no banco de dados
	 * e então tratar o retorno da query para em uma estrutura para que possa ser plotada nos gráficos 
	 * dos gráficos mensais, que são gráficos sobre o pedido de informação realizado no mês e ano atual.
	 * 
	 * Esta classe trata unicamente dos pedidos de informação do ano e mês selecionado. Caso queira visualizar 
	 * os relatórios estático vá para a classe FiltrarDadosRelatorioDinamico e RelatorioDinamico.
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
	//protected final static int anoInicial = 2012;				//Comentado por motivo de não existe armazenamento disponível dos anos anteriores.
	protected final static int anoInicial = 2018;				//Contagem iniciando no ano vigente do sistema.
	


	/**
	 * Este método visa buscar as informações conforme selecionados pelo usuário. Existe nessa estrutura uma 
	 * questão importante para definição dos valores da base, pois o gráfico funciona numa relação de Tipo de 
	 * solicitação X tempo, e esse tempo pode ser mês ou ano. Além disso, o usuário pode especificar qual o 
	 * mês ou ano específico que ele quer realizar a busca o que restringe o formato da busca e estrutura do
	 * arraylist de valores.
	 * 
	 * Outro caso que esse método prevêr é o de o usuário pedir um gráfico em função de um tempo e especificar
	 * o outro. Ex. O gráfico ser em função do mês, porém o usuário quer ver esse mês nos anos de 2012, 2016 e 2018.
	 * 
	 * Para isso é passado como parametro a string param, que se refere a em função de quê que o gráfico será
	 * gerado. Tendo o valor do param verificamos se foi feita um pedido de especificação por parte do usuário
	 * e assim gerar os valores mais adequadamente. 
	 * 
	 * @param query
	 * @param baseRecebida
	 * @param param
	 * @param dados
	 * @return
	 */
	public static Map<String, ArrayList<Integer>> gerarAcompanhamentoDinamico(String query, ArrayList<String> baseRecebida, String param, Map<String, ArrayList<String>> dados) {

		Map<String, ArrayList<Integer>> dadosChart = new HashMap<>();
		ArrayList<ArrayList<Integer>> dadosRelacionadorBase = new ArrayList<>();
		ArrayList<String> keysValues = new ArrayList<>();
		Set<String> uf = new HashSet<>();
		ArrayList<String> base = new ArrayList<>();
		String queryTempo = query;
		
		if (dados.containsKey(param)) {
			dadosRelacionadorBase = new ArrayList<>();
			if (!dados.get(param).get(0).equals("Todos")) {
				keysValues = dados.get(param);
				if (param.equals("mes")) {
					for (String t : keysValues) {
						base.add(meses[Integer.parseInt(t)-1]);
						dadosRelacionadorBase.add(new FiltrarDadosRelatorioDinamico()
								.verificaTempoAno(queryTempo, query, meses[Integer.parseInt(t)-1], dados));
					}
				} else {
					for (String t : keysValues) {
						base.add(t);
						dadosRelacionadorBase.add(new FiltrarDadosRelatorioDinamico().verificaTempoMes(queryTempo, query, t, dados));
					}
				}
			}
			

			dados.remove(param);
		}else if(!dados.containsKey(param) || dados.get(param).get(0).equals("Todos") ) {
			base = baseRecebida;
			dadosRelacionadorBase = new ArrayList<>();
			for(String t : base) {
				if(param.equals("ano")) {
					dadosRelacionadorBase.add(new FiltrarDadosRelatorioDinamico().verificaTempoMes(queryTempo, query, t, dados));
				}else if (param.equals("mes")) {
					dadosRelacionadorBase.add(new FiltrarDadosRelatorioDinamico().verificaTempoAno(queryTempo, query, t, dados));
				}
			}

		}


		for (int i = 0; i < base.size(); i++) {
			dadosChart.put(base.get(i), dadosRelacionadorBase.get(i));
		}

		return dadosChart;
	}
	
	public ArrayList<Integer> verificaTempoMes(String queryTempo, String query, String t, Map<String, ArrayList<String>> dados) {
		ArrayList<String> keysValues = new ArrayList<>();
		ArrayList<Integer> aux = new ArrayList<>();
		
		if(dados.containsKey("mes") && dados.get("mes") != null) {
			keysValues = dados.get("mes");
			for ( String mes : keysValues ) {
				if(query.contains("WHERE")) {
					queryTempo = query.concat(" AND ");
				}else {
					queryTempo = query.concat(" WHERE ");
				}
				queryTempo = queryTempo.concat(" dataIni LIKE '" + t + "-0"+mes+"%'");
				List<Solicitacao> auxQuery = SolicitacaoDAO.queryDinamica(queryTempo);
				aux.add(auxQuery!= null ? auxQuery.size() : 0);
			}
		}else {
			if(query.contains("WHERE")) {
				queryTempo = query.concat(" AND ");
			}else {
				queryTempo = query.concat(" WHERE ");
			}
			queryTempo = queryTempo.concat(" dataIni LIKE '" +t + "-%'");
			List<Solicitacao> auxQuery = SolicitacaoDAO.queryDinamica(queryTempo);
			aux.add(auxQuery!= null ? auxQuery.size() : 0);
		}
		
		return aux;
	}
	
	
	
	public ArrayList<Integer> verificaTempoAno(String queryTempo, String query, String t, Map<String, ArrayList<String>> dados) {
		ArrayList<String> keysValues = new ArrayList<>();
		ArrayList<Integer> aux = new ArrayList<>();
		
		if(dados.containsKey("ano") && dados.get("ano") != null) {
			for ( String ano : dados.get("ano") ) {
				if(query.contains("WHERE")) {
					queryTempo = query.concat(" AND ");
				}else {
					queryTempo = query.concat(" WHERE ");
				}
				queryTempo = queryTempo.concat("dataIni LIKE '"+ano+"-0" + (int)(Arrays.asList(meses).indexOf(t)+1) + "-%'");
				List<Solicitacao> auxQuery = SolicitacaoDAO.queryDinamica(queryTempo);
				aux.add(auxQuery!= null ? ( aux!= null ? aux.get(aux.size()-1) + auxQuery.size() : 0) : 0);
			}
		}else {
			if(query.contains("WHERE")) {
				queryTempo = query.concat(" AND ");
			}else {
				queryTempo = query.concat(" WHERE ");
			}
			queryTempo = queryTempo.concat("  dataIni LIKE '%-0" +  (int)(Arrays.asList(meses).indexOf(t)+1) + "-%'");
			List<Solicitacao> auxQuery = SolicitacaoDAO.queryDinamica(queryTempo);
			aux.add(auxQuery!= null ? auxQuery.size() : 0);
		}
		return aux;
	}
	
	
	
	
	
	
	// Essa é a forma do método que gera o dicionário na estrutura que a classe do gráfico precisa 
	//para criar o objeto gráfico.
/*	public static Map<String, ArrayList<Integer>> metodoExemplo() {
		Map<String, ArrayList<Integer>> dadosChart = new HashMap<>();
		Calendar c = Calendar.getInstance();
		int mesAtual = c.get(Calendar.MONTH);
		int anoAtual = c.get(Calendar.YEAR);
		String periodo = anoAtual + "-0" + mesAtual + "%";
		
		ArrayList<String> base = new ArrayList<>();
		ArrayList<ArrayList<Integer>> dadosRelacionadorBase = new ArrayList<>();
		
		Set<String> uf = new HashSet<>();
		
		for (int i = 0; i < base.size(); i++) {
			dadosChart.put(base.get(i), dadosRelacionadorBase.get(i));
		}
		
		return dadosChart;
	}
*/
	
}
