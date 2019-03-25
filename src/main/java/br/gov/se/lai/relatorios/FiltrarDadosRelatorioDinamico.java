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
	 * Essa classe se refere a organiza��o do que � preciso para realizar a query no banco de dados
	 * e ent�o tratar o retorno da query para em uma estrutura para que possa ser plotada nos gr�ficos 
	 * dos gr�ficos mensais, que s�o gr�ficos sobre o pedido de informa��o realizado no m�s e ano atual.
	 * 
	 * Esta classe trata unicamente dos pedidos de informa��o do ano e m�s selecionado. Caso queira visualizar 
	 * os relat�rios est�tico v� para a classe FiltrarDadosRelatorioDinamico e RelatorioDinamico.
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
	//protected final static int anoInicial = 2012;				//Comentado por motivo de n�o existe armazenamento dispon�vel dos anos anteriores.
	protected final static int anoInicial = 2018;				//Contagem iniciando no ano vigente do sistema.
	


	/**
	 * Este m�todo visa buscar as informa��es conforme selecionados pelo usu�rio. Existe nessa estrutura uma 
	 * quest�o importante para defini��o dos valores da base, pois o gr�fico funciona numa rela��o de Tipo de 
	 * solicita��o X tempo, e esse tempo pode ser m�s ou ano. Al�m disso, o usu�rio pode especificar qual o 
	 * m�s ou ano espec�fico que ele quer realizar a busca o que restringe o formato da busca e estrutura do
	 * arraylist de valores.
	 * 
	 * Outro caso que esse m�todo prev�r � o de o usu�rio pedir um gr�fico em fun��o de um tempo e especificar
	 * o outro. Ex. O gr�fico ser em fun��o do m�s, por�m o usu�rio quer ver esse m�s nos anos de 2012, 2016 e 2018.
	 * 
	 * Para isso � passado como parametro a string param, que se refere a em fun��o de qu� que o gr�fico ser�
	 * gerado. Tendo o valor do param verificamos se foi feita um pedido de especifica��o por parte do usu�rio
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
	
	
	
	
	
	
	// Essa � a forma do m�todo que gera o dicion�rio na estrutura que a classe do gr�fico precisa 
	//para criar o objeto gr�fico.
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
