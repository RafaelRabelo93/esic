package br.gov.se.lai.relatorios;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.AjaxBehaviorEvent;

import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.DonutChartModel;
import org.primefaces.model.chart.HorizontalBarChartModel;
import org.primefaces.model.chart.PieChartModel;

import br.gov.se.lai.Bean.EntidadesBean;
import br.gov.se.lai.entity.Entidades;

@ManagedBean(name = "relatorios")
@SessionScoped
public class Relatorios {

	
	public int tipoRelatorio;
	public int tipoGrafico;
	public Map<String, ArrayList<Integer>> dadosChart;
	public static String[] tipo = {"Pedidos totais do E-SIC", "Pedidos Mensais do E-SIC", "Pedidos Anuais do E-SIC", "Pedidos Anuais acumulados do E-SIC",
									"Pedidos por Órgão da Administração Direta", "Pedidos por Entidade Órgão da Administração Indireta", "Pedidos por Tema do E-SIC", "Pedidos por Tipo de Pessoa", 
									"Pedidos por Ente Federativo"};
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
	public String[] meses ;
	public String[] anosSelecionados;
	public String[] orgaos;
	public String[] entidades;
	public String[] assuntos;
	public String tipoPessoa;
	public String[] estado;
	public String tipoSolicitacao;
	public String param;
	public String status;
	public ArrayList<Integer> anos;
	public BarChartModel barModel;
	public HorizontalBarChartModel hBarModel;
	
	
	@PostConstruct
	public void Relatorios() {
		anos = retornarListaAnosAteHoje();
	}
	
	/**
	 * Significado dos inteiros que direcionam qual tipo de filtragem irá ser realizada.
	 * 
	 * 1 - pedidos totais do esic
	 * 2- pedidos mensais
	 * 3- pedidos anuais
	 * 4- pedidos acumulados
	 * 5 - pedidos por orgao
	 * 6 - pedidos por entidade
	 * 7- pedidos por tema
	 * 8 - pedidos por tipo de pessoa
	 * 9- uf
	 * @return 
	 */
	
	public Map<String, ArrayList<Integer>> redirecionarFiltroDados(long tipoDados) {
		
		switch ((int)tipoDados){
		case 1:
			dadosChart = FiltrarDadosRelatorioEstatico.gerarAcompanhamentoGeralDosPedidosInformacao();
			break;
		case 2:
			dadosChart = FiltrarDadosRelatorioEstatico.gerarAcompanhamentoMensalPedidoInformacao();
			break;
		case 3:
			dadosChart = FiltrarDadosRelatorioEstatico.gerarAcompanhamentoAnualPedidoInformacao();
			break;
		case 4:
			dadosChart = FiltrarDadosRelatorioEstatico.gerarAcompanhamentoAnualAcumuladoPedidoInformacao();
			break;
		case 5:
			dadosChart = FiltrarDadosRelatorioEstatico.gerarAcompanhamentoOrgaoPedidoInformacao();
			break;
		case 6:
			dadosChart = FiltrarDadosRelatorioEstatico.gerarAcompanhamentoEntidadePedidoInformacao();
			break;
		case 7:
			dadosChart = FiltrarDadosRelatorioEstatico.gerarAcompanhamentoAssuntoPedidoInformacao();
			break;
		case 8:
			dadosChart = FiltrarDadosRelatorioEstatico.gerarAcompanhamentoTipoPessoaGeneroPedidoInformacao();
			break;
		case 9:
			dadosChart = FiltrarDadosRelatorioEstatico.gerarAcompanhamentoEstadosPedidoInformacao();
			break;
		}
		
		return dadosChart;
	}
	
	public void desenhar(long tipoDados) {
		
		if(tipoDados == (long)7 || tipoDados == (long)8 || tipoDados == (long)9) {
			desenharPieChart( tipoDados);
		}else {
			desenharBarChart( tipoDados);
		}
	}
	
	/**
	 * Gera o dicionário com os valores que preencheram o gráfico para então criar o objeto PieChart.
	 * @param tipoDados
	 * @return
	 */
	public PieChartModel desenharPieChart( long tipoDados) {
		redirecionarFiltroDados(tipoDados);
		DrawPieChart modelPie = new DrawPieChart();
		return modelPie.createPieModel2(dadosChart, tipo[(int)tipoDados-1], tipoDados);
	}
	
	public DonutChartModel desenharDonutChart( long tipoDados) {
		redirecionarFiltroDados(tipoDados);
		DrawPieChart modelPie = new DrawPieChart();
		return modelPie.createDonutModel(dadosChart, tipo[(int)tipoDados-1], tipoDados);
	}
	
	/**
	 * Gera o dicionário com os valores que preencheram o gráfico para então criar o objeto BarChart,
	 * além de verificar qual o valor mais alto para estabelecer qual a altura máxima do gráfico.
	 * @param tipoDados
	 * @return
	 */
	public BarChartModel desenharBarChart( long tipoDados) {
		redirecionarFiltroDados(tipoDados);
		int valorMaior = identificarValorMaximoGrafico(dadosChart);
		DrawBarChart model = new DrawBarChart();
		return model.createBarModel(tipo[(int)tipoDados-1], dadosChart, tipoDados, valorMaior);
	}
	
	public HorizontalBarChartModel desenharBarChartHorizontal( long tipoDados) {
		redirecionarFiltroDados(tipoDados);
		int valorMaior = identificarValorMaximoGrafico(dadosChart);
		DrawBarChart model = new DrawBarChart();
		return model.createHorizontalBarModel(tipo[(int)tipoDados-1], dadosChart, tipoDados, valorMaior);
		
	}
	
	public int identificarValorMaximoGrafico(Map<String, ArrayList<Integer>> dadosChart) {
		int valorMaior = 0;
		for (String key : dadosChart.keySet()) {
			ArrayList<Integer> list = dadosChart.get(key);
			valorMaior = Collections.max(list,null) > valorMaior ? Collections.max(list,null) : valorMaior;
		}
		
		if (valorMaior*0.2 < 5) {
			return ((valorMaior + 5)/10)*10;
		} 
		else return (int) ((valorMaior+(valorMaior*0.2))/10)*10;
	}
	
	public ArrayList<Integer> retornarListaAnosAteHoje() {
		ArrayList<Integer> anos = new ArrayList<>();
		Calendar a = Calendar.getInstance();
		for (int i = 2012; i <= a.get(Calendar.YEAR); i++) {
			anos.add(i);
		}
		Collections.reverse(anos);
		return anos;
	}
	
	public int getAnoAtual() {
		Calendar c = Calendar.getInstance();
		int anoAtual = c.get(Calendar.YEAR);
		
		return anoAtual;
	}
	
	public String getMesAtual() {
		String[] meses = { "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro" };
		
		Calendar c = Calendar.getInstance();
		int mesAtual = c.get(Calendar.MONTH)+1;
		
		return meses[mesAtual-1];
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

	public String  getTipoSolicitacao() {
		return tipoSolicitacao;
	}

	public void setTipoSolicitacao(String  tipoSolicitacao) {
		this.tipoSolicitacao = tipoSolicitacao;
	}

	public String  getTempo() {
		return param;
	}

	public void setTempo(String  param) {
		this.param = param;
	}

	public String  getStatus() {
		return status;
	}

	public void setStatus(String  status) {
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
		this.barModel =  barModel;
	}

	public HorizontalBarChartModel gethBarModel() {
		return hBarModel;
	}

	public void sethBarModel(HorizontalBarChartModel hBarModel) {
		this.hBarModel = hBarModel;
	}

	
	
}
