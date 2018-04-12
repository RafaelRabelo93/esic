package relatorio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.PieChartModel;

@ManagedBean(name = "relatorios")
@SessionScoped
public class Relatorios {

	
	public int tipoRelatorio;
	public int tipoGrafico;
	public Map<String, ArrayList<Integer>> dadosChart;
	public static String[] tipo = {"Pedidos totais do E-SIC", "Pedidos mensais do E-SIC", "Pedidos anuais do E-SIC", "Pedidos anuais acumulados do E-SIC",
									"Pedidos por órgão do E-SIC", "Pedidos por entidade do E-SIC", "Pedidos por assunto do E-SIC", "Pedidos por tipo de pessoa do E-SIC", 
									"Pedidos por estado do E-SIC"};
	public int metricas;
	
	@PostConstruct
	public void Relatorios() {
		
	}
	
	/**
	 * 1 - pedidos totais do esic
	 * 2- pedidos mensais
	 * 3- pedidos anuais
	 * 4- pedidos acumulados
	 * 5 - pedidos por orgao
	 * 6 - pedidos por entidade
	 * 7- pedidos por assuntos
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
	
	public PieChartModel desenharPieChart( long tipoDados) {
		redirecionarFiltroDados(tipoDados);
		DrawPieChart modelPie = new DrawPieChart();
		return modelPie.createPieModel2(dadosChart, tipo[(int)tipoDados-1]);
	}
	
	
	public BarChartModel desenharBarChart( long tipoDados) {
		redirecionarFiltroDados(tipoDados);
		int valorMaior = identificarValorMaximoGrafico(dadosChart);
		DrawBarChart model = new DrawBarChart();
		return model.createBarModel(tipo[(int)tipoDados-1], dadosChart, tipoDados, valorMaior);
		
	}
	
	public int identificarValorMaximoGrafico(Map<String, ArrayList<Integer>> dadosChart) {
		int valorMaior = 0;
		for (String key : dadosChart.keySet()) {
			ArrayList<Integer> list = dadosChart.get(key);
			valorMaior = Collections.max(list,null) > valorMaior ? Collections.max(list,null) : valorMaior;
		}
		return (valorMaior+10);
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

}
