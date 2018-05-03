
package relatorio;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.AjaxBehaviorEvent;

import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.HorizontalBarChartModel;
import org.primefaces.model.chart.PieChartModel;

@ManagedBean(name = "relatorioDinamico")
@SessionScoped
public class RelatorioDinamico {

	public int tipoRelatorio;
	public int tipoGrafico;
	public Map<String, ArrayList<Integer>> dadosChart;
	public static String[] tipo = { "Pedidos totais do E-SIC", "Pedidos mensais do E-SIC", "Pedidos anuais do E-SIC",
			"Pedidos anuais acumulados do E-SIC", "Pedidos por órgão do E-SIC", "Pedidos por entidade do E-SIC",
			"Pedidos por assunto do E-SIC", "Pedidos por tipo de pessoa do E-SIC", "Pedidos por estado do E-SIC" };
	public int[] metricas;
	public boolean dataBool;
	public boolean mesesBool;
	public boolean anosBool;
	public boolean orgaoBool;
	public boolean entidadeBool;
	public boolean estadosBool;
	public boolean assuntoBool;
	public boolean tipoPessoaBool;
	public Date periodoIni;
	public Date periodoFim;
	public String[] meses;
	public String[] anosSelecionados;
	public String[] orgaos;
	public String[] entidades;
	public String[] assuntos;
	public String tipoPessoa;
	public String[] estado;
	public String tipoSolicitacao;
	public String tempo;
	public String status;
	public ArrayList<Integer> anos;
	public BarChartModel barModel;
	public HorizontalBarChartModel hBarModel;;

	@PostConstruct
	public void Relatorios() {
	}


	public int identificarValorMaximoGrafico(Map<String, ArrayList<Integer>> dadosChart) {
		int valorMaior = 0;
		for (String key : dadosChart.keySet()) {
			ArrayList<Integer> list = dadosChart.get(key);
			valorMaior = Collections.max(list, null) > valorMaior ? Collections.max(list, null) : valorMaior;
		}
		return (valorMaior + 10);
	}


	@SuppressWarnings("serial")
	public void verificarBool() {
		List<Integer> comparar = new ArrayList<Integer>() {
			{
				add(1);
				add(2);
				add(3);
				add(4);
				add(5);
				add(6);
				add(7);
				add(8);
			}
		};

		if (metricas.length != 0) {
			for (int i : metricas) {
				comparar.remove(comparar.indexOf(i));
				switch (i) {
				case 1:
					setDataBool(true);
					break;
				case 2:
					setMesesBool(true);
					break;
				case 3:
					setAnosBool(true);
					break;
				case 4:
					setOrgaoBool(true);
					break;
				case 5:
					setEntidadeBool(true);
					break;
				case 6:
					setEstadosBool(true);
					break;
				case 7:
					setAssuntoBool(true);
					break;
				case 8:
					setTipoPessoaBool(true);
					break;
				}
			}

			for (int i : comparar) {
				switch (i) {
				case 1:
					setDataBool(false);
					break;
				case 2:
					setMesesBool(false);
					break;
				case 3:
					setAnosBool(false);
					break;
				case 4:
					setOrgaoBool(false);
					break;
				case 5:
					setEntidadeBool(false);
					break;
				case 6:
					setEstadosBool(false);
					break;
				case 7:
					setAssuntoBool(false);
					break;
				case 8:
					setTipoPessoaBool(false);
					break;
				}
			}
		} else {
			setMesesBool(false);
			setAnosBool(false);
			setAssuntoBool(false);
			setDataBool(false);
			setEntidadeBool(false);
			setEstadosBool(false);
			setOrgaoBool(false);
			setTipoPessoaBool(false);

		}
	}

	@SuppressWarnings("unused")
	public Map<String, ArrayList<String>> selecionarDados() {
		Map<String, ArrayList<String>> dados = new HashMap<>();
		ArrayList<String> aux = new ArrayList<>();
		String[] keys = { "data", "mes", "ano", "orgao", "entidade", "estados", "assunto", "tipoPessoa" };
		List<List<String>> variaveis = new ArrayList<List<String>>();
		for (int metrica : metricas) {
			switch (metrica) {
			case 1:
				aux = new ArrayList<>();
				aux.add(periodoIni.toString());
				aux.add(periodoFim.toString());
				break;
			case 2:
				aux = new ArrayList<>();
				for (String mes : meses) {
					aux.add(mes);
				}
				dados.put(keys[metrica - 1], aux);
				break;
			case 3:
				aux = new ArrayList<>();
				for (String ano : anosSelecionados) {
					aux.add(ano);
				}
				dados.put(keys[metrica - 1], aux);
				break;
			case 4:
				aux = new ArrayList<>();
				for (String orgao : orgaos) {
					aux.add(orgao);
				}
				dados.put(keys[metrica - 1], aux);
				break;
			case 5:
				aux = new ArrayList<>();
				for (String entidade : entidades) {
					aux.add(entidade);
				}
				dados.put(keys[metrica - 1], aux);
				break;
			case 6:
				aux = new ArrayList<>();
				for (String uf : estado) {
					aux.add(uf);
				}
				dados.put(keys[metrica - 1], aux);
				break;
			case 7:
				aux = new ArrayList<>();
				for (String assunto : assuntos) {
					aux.add(assunto);
				}
				dados.put(keys[metrica - 1], aux);
				break;
			case 8:
				aux = new ArrayList<>();
				aux.add(tipoPessoa);
				dados.put(keys[metrica - 1], aux);
				break;
			default:
				break;
			}
		}
		return dados;
	}
	
	
	public ArrayList<String> definirMedidaTempo() {
		ArrayList<String> medidaTempo = new ArrayList<>();
		if (tempo.equals("mes")) {
			if (Arrays.asList(metricas).contains(2)) {
				for (String mes : meses) {
					medidaTempo.add(mes);
				}
			} else {
				Calendar instance = Calendar.getInstance();
				int mesAtual = instance.get(Calendar.MONTH);
				for (int i = 0; i <= mesAtual; i++) {
					medidaTempo.add(FiltrarDadosRelatorioEstatico.meses[i]);
				}
			}
		} else if (tempo.equals("ano")) {
			if (Arrays.asList(metricas).contains(3)) {
				for (String anos : anosSelecionados) {
					medidaTempo.add(anos);
				}
			} else {
				Calendar instance = Calendar.getInstance();
				int anoAtual = instance.get(Calendar.YEAR);
				for (int i = 2012; i <= anoAtual; i++) {
					medidaTempo.add(Integer.toString(i));
				}
			}
		}
		
		return medidaTempo;
	}
	
	public void desenharBarChartModel() {
		Map<String, ArrayList<String>> dados = selecionarDados();
		ArrayList<String> medidaTempo = definirMedidaTempo();
		String retorno = definirStringQuery(tipoSolicitacao, tempo, status, dados, medidaTempo);
		dadosChart = FiltrarDadosRelatorioDinamico.gerarAcompanhamentoDinamico(retorno, medidaTempo, tempo, dados);
		int valorMaior = identificarValorMaximoGrafico(dadosChart);
		DrawBarChart model = new DrawBarChart();
		barModel = model.createBarModel(tipo[0], dadosChart, (long) 1, valorMaior);
		hBarModel = model.createHorizontalBarModel(tipo[0], dadosChart, (long) 1, valorMaior);
	}

	public String definirStringQuery(String tipoSolicitacao, String tempo, String status, Map<String, ArrayList<String>> dados,
			ArrayList<String> medidaTempo) {
		boolean escritaDeUmOrgaoOuEntidade = false;
		String query = "FROM Solicitacao as slt";
		if (dados.containsKey("tipoPessoa") || dados.containsKey("estados")) {
			query = query.concat(" JOIN slt.cidadao as cidadao WHERE slt.cidadao.idCidadao = cidadao.idCidadao "
					+ "AND slt.tipo = '" + tipoSolicitacao + "' AND slt.status = '" + status + "'");
		} else {
			query = query.concat(" WHERE slt.tipo = '" + tipoSolicitacao + "' AND slt.status = '" + status + "'");
		}

		for (String key : dados.keySet()) {
			switch (key) {
			case "orgao":
				for (int i = 0; i < dados.get(key).size(); i++) {
					if (i != 0) {
						query = query.concat(" OR slt.entidades.idEntidades = '" + dados.get(key).get(i) + "'");
					} else {
						if (escritaDeUmOrgaoOuEntidade) {
							query = query.concat(" OR");
							query = query.concat(" slt.entidades.idEntidades = '" + dados.get(key).get(i) + "'");
						} else {
							query = query.concat(" AND ");
							query = query.concat("( slt.entidades.idEntidades = '" + dados.get(key).get(i) + "'");
						}
					}
				}
				if (dados.containsKey("entidade")) {
					if (escritaDeUmOrgaoOuEntidade) {
						query = query.concat(")");
					} else {
						escritaDeUmOrgaoOuEntidade = true;
					}
				} else {
					query = query.concat(")");
				}
				break;
			case "entidade":
				for (int i = 0; i < dados.get(key).size(); i++) {
					if (i != 0) {
						query = query.concat(" OR slt.entidades.idEntidades = '" + dados.get(key).get(i) + "'");
					} else {
						if (escritaDeUmOrgaoOuEntidade) {
							query = query.concat(" OR");
							query = query.concat(" slt.entidades.idEntidades = '" + dados.get(key).get(i) + "'");
						} else {
							query = query.concat(" AND ");
							query = query.concat("( slt.entidades.idEntidades = '" + dados.get(key).get(i) + "'");
						}
					}
				}

				if (dados.containsKey("orgao")) {
					if (escritaDeUmOrgaoOuEntidade) {
						query = query.concat(")");
					} else {
						escritaDeUmOrgaoOuEntidade = true;
					}
				} else {
					query = query.concat(")");
				}
				break;
			case "estados":
				query = query.concat(" AND ");
				for (int i = 0; i < dados.get(key).size(); i++) {
					if (i != 0) {
						query = query.concat(" OR slt.cidadao.estado = '" + dados.get(key).get(i) + "'");
					} else {
						query = query.concat(" slt.cidadao.estado = '" + dados.get(key).get(i) + "'");
					}
				}
				break;

			case "tipoPessoa":
				query = query.concat(" AND ");
				switch (dados.get("tipoPessoa").get(0)) {
				case "F":
					query = query.concat(" (cidadao.tipo = '1')");
					break;
				case "FF":
					query = query.concat(" (cidadao.tipo = '1' AND cidadao.sexo = 'F')");
					break;
				case "FM":
					query = query.concat(" (cidadao.tipo = '1' AND cidadao.sexo = 'M')");
					break;
				case "J":
					query = query.concat(" (cidadao.tipo = '0')");
					break;
				case "JF":
					query = query.concat(" (cidadao.tipo = '0' AND cidadao.sexo = 'F')");
					break;
				case "JM":
					query = query.concat(" (cidadao.tipo = '0' AND cidadao.sexo = 'M')");
					break;

				}
				break;
			case "assunto":
				query = query.concat(" AND ");
				for (int i = 0; i < dados.get(key).size(); i++) {
					if (i != 0) {
						query = query.concat(" OR slt.acoes.idAcoes = '" + dados.get(key).get(i) + "'");
					} else {
						query = query.concat(" slt.acoes.idAcoes = '" + dados.get(key).get(i) + "'");
					}
				}
				break;

			default:
				break;
			}
		}
		return query;

	}

	public int teste1(int a, int b) {
		return a + b;
	}

	public int teste1(int a) {
		return a + 1;
	}

	public int getTipoRelatorio() {
		return tipoRelatorio;
	}

	public void setTipoRelatorio(int tipoRelatorio) {
		this.tipoRelatorio = tipoRelatorio;
	}

	public int getTipoGrafico() {
		return tipoGrafico;
	}

	public void setTipoGrafico(int tipoGrafico) {
		this.tipoGrafico = tipoGrafico;
	}

	public boolean isDataBool() {
		return dataBool;
	}

	public void setDataBool(boolean newData) {
		dataBool = newData;
	}

	public boolean isMesesBool() {
		return mesesBool;
	}

	public void setMesesBool(boolean mesesBool) {
		this.mesesBool = mesesBool;
	}

	public boolean isAnosBool() {
		return anosBool;
	}

	public void setAnosBool(boolean anosBool) {
		this.anosBool = anosBool;
	}

	public boolean isOrgaoBool() {
		return orgaoBool;
	}

	public void setOrgaoBool(boolean newOrgao) {
		orgaoBool = newOrgao;
	}

	public boolean isEntidadeBool() {
		return entidadeBool;
	}

	public void setEntidadeBool(boolean newEntidade) {
		entidadeBool = newEntidade;

	}

	public boolean isEstadosBool() {
		return estadosBool;
	}

	public void setEstadosBool(boolean newEstado) {
		estadosBool = newEstado;
	}

	public boolean isAssuntoBool() {
		return assuntoBool;
	}

	public void setAssuntoBool(boolean newAss) {
		assuntoBool = newAss;
	}

	public boolean isTipoPessoaBool() {
		return tipoPessoaBool;
	}

	public void setTipoPessoaBool(boolean newTipo) {
		tipoPessoaBool = newTipo;
	}

	public int[] getMetricas() {
		return metricas;
	}

	public void setMetricas(int[] metricas) {
		this.metricas = metricas;
	}

	public Date getPeriodoIni() {
		return periodoIni;
	}

	public void setPeriodoIni(Date periodoIni) {
		this.periodoIni = periodoIni;
	}

	public Date getPeriodoFim() {
		return periodoFim;
	}

	public void setPeriodoFim(Date periodoFim) {
		this.periodoFim = periodoFim;
	}

	public String[] getMeses() {
		return meses;
	}

	public void setMeses(String[] meses) {
		this.meses = meses;
	}

	public String[] getOrgaos() {
		return orgaos;
	}

	public void setOrgaos(String[] orgaos) {
		this.orgaos = orgaos;
	}

	public String[] getEntidades() {
		return entidades;
	}

	public void setEntidades(String[] entidades) {
		this.entidades = entidades;
	}

	public String[] getAssuntos() {
		return assuntos;
	}

	public void setAssuntos(String[] assuntos) {
		this.assuntos = assuntos;
	}

	public String getTipoPessoa() {
		return tipoPessoa;
	}

	public void setTipoPessoa(String tipoPessoa) {
		this.tipoPessoa = tipoPessoa;
	}

	public String[] getEstado() {
		return estado;
	}

	public void setEstado(String[] estado) {
		this.estado = estado;
	}

	public String getTipoSolicitacao() {
		return tipoSolicitacao;
	}

	public void setTipoSolicitacao(String tipoSolicitacao) {
		this.tipoSolicitacao = tipoSolicitacao;
	}

	public String getTempo() {
		return tempo;
	}

	public void setTempo(String tempo) {
		this.tempo = tempo;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public ArrayList<Integer> getAnos() {
		return anos;
	}

	public void setAnos(ArrayList<Integer> anos) {
		this.anos = anos;
	}

	public String[] getAnosSelecionados() {
		return anosSelecionados;
	}

	public void setAnosSelecionados(String[] anosSelecionados) {
		this.anosSelecionados = anosSelecionados;
	}

	public BarChartModel getBarModel() {
		return barModel;
	}

	public void setBarModel(BarChartModel barModel) {
		this.barModel = barModel;
	}

	public HorizontalBarChartModel gethBarModel() {
		return hBarModel;
	}

	public void sethBarModel(HorizontalBarChartModel hBarModel) {
		this.hBarModel = hBarModel;
	}

}
