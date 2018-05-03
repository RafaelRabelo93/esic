package relatorio;

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

	protected final static String[] meses = { "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho",
			"Agosto", "Setembro", "Outubro", "Novembro", "Dezembro" };
	protected final static int anoInicial = 2012;



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
						dadosRelacionadorBase.add(new FiltrarDadosRelatorioDinamico().verificaTempoAno(queryTempo, query, meses[Integer.parseInt(t)-1], dados));
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
				queryTempo = query.concat(" AND dataIni LIKE '" + t + "-0"+mes+"%'");
				List<Solicitacao> auxQuery = SolicitacaoDAO.queryDinamica(queryTempo);
				aux.add(auxQuery!= null ? auxQuery.size() : 0);
			}
		}else {
			queryTempo = query.concat(" AND dataIni LIKE '" + Arrays.asList(meses).indexOf(t)+1 + "%'");
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
				queryTempo = query.concat(" AND dataIni LIKE '"+ano+"-0" + (int)(Arrays.asList(meses).indexOf(t)+1) + "-%'");
				List<Solicitacao> auxQuery = SolicitacaoDAO.queryDinamica(queryTempo);
				aux.add(auxQuery!= null ? auxQuery.size() : 0);
			}
		}else {
			queryTempo = query.concat(" AND dataIni LIKE '%-0" +  (int)(Arrays.asList(meses).indexOf(t)+1) + "-%'");
			List<Solicitacao> auxQuery = SolicitacaoDAO.queryDinamica(queryTempo);
			aux.add(auxQuery!= null ? auxQuery.size() : 0);
		}
		return aux;
	}
	
	
	
	
	
	
	public static Map<String, ArrayList<Integer>> nome() {
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

	
}
