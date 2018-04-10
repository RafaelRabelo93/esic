package br.gov.se.lai.utils;

import java.util.ArrayList;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.primefaces.component.chart.Chart;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;

@ManagedBean(name = "relatorioOrgao")
@SessionScoped
public class RelatorioOrgao {

	    private BarChartModel barModel;
	    public String tipoRelatorio;
	    public String relatorioDe;
	 
	    @PostConstruct
	    public void init() {
	        createBarModels();
	    }
	 
	    public BarChartModel getBarModel() {
	        return barModel;
	    }
	     
	 
	    private BarChartModel initBarModel() {
	        BarChartModel model = new BarChartModel();
	 
	        ArrayList<ChartSeries> variaveis = new ArrayList<>();
	    	
	        ChartSeries pedido = new ChartSeries();
	        ChartSeries aberto = new ChartSeries();
	        ChartSeries atendido = new ChartSeries();
	        
	        pedido.setLabel("Pedido");
		    aberto.setLabel("Em aberto");
		    atendido.setLabel("Atendidos");
		    
		    variaveis.add(pedido);
		    variaveis.add(aberto);
		    variaveis.add(atendido);

		    switch (relatorioDe) {
			case "Mensal":
				variaveis = graficoMensal(variaveis);	
				break;
			case "Geral":
				variaveis = graficoGeral(variaveis);	
				break;

			default:
				break;
			}
	        

	        
//		    for(String key : dadosChart.keySet()) {
//		    	for(int i=0; i < series.size(); i++) {
//			       	series.get(i).set(key, dadosChart.get(key).get(0));
//		    	}
//		    }

	        for (ChartSeries chartSeries : variaveis) {
	        	model.addSeries(chartSeries);
			}
	         
	        return model;
	    }
	     
	    private void createBarModels() {
	        createBarModel();
	    }
	     
	    private void createBarModel() {
	        barModel = initBarModel();
	         
	        barModel.setTitle("Bar Chart");
	        barModel.setLegendPosition("ne");
	         
	        Axis xAxis = barModel.getAxis(AxisType.X);
	        xAxis.setLabel("Órgãos");
	         
	        Axis yAxis = barModel.getAxis(AxisType.Y);
	        yAxis.setLabel("Quantidade");
	        yAxis.setMin(0);
	        yAxis.setMax(80);
	    }

	    
	    public ArrayList<ChartSeries> graficoGeral(ArrayList<ChartSeries> series ){
	        Map<String, int[]> dadosChart = FiltrarDadosRelatorioEstatico.gerarAcompanhamentoGeralDosPedidosInformacao();

		    for(String key : dadosChart.keySet()) {
//		    	for(int i=0; i < series.size(); i++) {
//			       	series.get(i).set(key, dadosChart.get(key)[0]);
//		    	}
		    	series.get(0).set(key, dadosChart.get(key)[0]);
		    }
	    	return series;
	    }

	    public ArrayList<ChartSeries> graficoMensal(ArrayList<ChartSeries> series ){
	    	Map<String, ArrayList<Integer>> dadosChart = FiltrarDadosRelatorioEstatico.gerarAcompanhamentoMensalPedidoInformacao();
	    	
	        for(int mes = 0; mes < dadosChart.size(); mes++) {
	        	String key = FiltrarDadosRelatorioEstatico.meses[mes];
	        	for(int i=0; i < series.size(); i++) {
			       	series.get(i).set(key, dadosChart.get(key).get(i));
		    	}
	        	
	        }
	    	return series;
	    }
	    
		public String getTipoRelatorio() {
			return tipoRelatorio;
		}


		public void setTipoRelatorio(String tipoRelatorio) {
			this.tipoRelatorio = tipoRelatorio;
		}

		public String getRelatorioDe() {
			return relatorioDe;
		}

		public void setRelatorioDe(String relatorioDe) {
			this.relatorioDe = relatorioDe;
		}
	    
	    
}
