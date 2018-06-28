package br.gov.se.lai.relatorios;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.primefaces.component.chart.Chart;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.HorizontalBarChartModel;

@ManagedBean(name = "barChartClass")
@SessionScoped
public class DrawBarChart {
		
		/** Classe para desenhar o gráfico em barras utilizado nos relatórios.
		 * 
		 * 	Os gráfico são montados a partir do recebimento de valores que são 
		 * passados aos método em forma de dicionário no formato <Nome da base,
		 * lista que representa os valores das colunas>
		 * 
		 * 	Podemos ter dois tipos de gráficos em barra. O primeiro tipo são 
		 * aqueles que possuem as três colunas relacionadas a base, sendo elas:
		 * situação total, situação em aberto, situação atentidada. O segundo 
		 * tipo são aquelas situações onde temos apenas uma coluna que se refere 
		 * a um valor total, ou seja, o valor do dicionário é passado como um 
		 * arraylist de tamanho igual a 1.
		 * 
		 */

	    private BarChartModel barModel;
	    private HorizontalBarChartModel horizontalBarModel;
	 
	    @PostConstruct
	    public void init() {
//	        createBarModels();
	    }
	 
	    public BarChartModel getBarModel() {
	        return barModel;
	    }
	     
	 
	    private BarChartModel initBarModel(Map<String, ArrayList<Integer>> dadosChart, long tipoDados ) {
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

		    switch ((int)tipoDados) {
		    case 1:
		    	variaveis = graficoGeral(variaveis, dadosChart);	
		    	break;
			case 2:
				variaveis = graficoMensalComSeries(variaveis, dadosChart);	
				break;
//			case 3:
//				variaveis = graficoMensalSemSeries(variaveis, dadosChart);	
//				break;

			default:
				variaveis = graficoComSeries(variaveis, dadosChart);
				break;
			}

	        for (ChartSeries chartSeries : variaveis) {
	        	model.addSeries(chartSeries);
			}
	         
	        return model;
	    }
	    private HorizontalBarChartModel initHorizontalBarModel( Map<String, ArrayList<Integer>> dadosChart, long tipoDados ) {
	    	HorizontalBarChartModel model =  new HorizontalBarChartModel();
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
	    	
	    	switch ((int)tipoDados) {
	    	case 1:
	    		variaveis = graficoGeral(variaveis, dadosChart);	
	    		break;
	    	case 2:
	    		variaveis = graficoMensalComSeries(variaveis, dadosChart);	
	    		break;
//	    	case 3:
//	    		variaveis = graficoMensalSemSeries(variaveis, dadosChart);	
//	    		break;
	    		
	    	default:
	    		variaveis = graficoComSeries(variaveis, dadosChart);
	    		break;
	    	}
	    	
	    	for (ChartSeries chartSeries : variaveis) {
	    		model.addSeries(chartSeries);
	    	}
	    	
	    	return model;
	    }
	    
	    public HorizontalBarChartModel createHorizontalBarModel(String title, Map<String, ArrayList<Integer>> dadosChart, long tipoDados, int valorMaior) {
	        
	        horizontalBarModel = initHorizontalBarModel(dadosChart, tipoDados);
	         
	        horizontalBarModel.setTitle(title);
	        horizontalBarModel.setLegendPosition("e");
	        horizontalBarModel.setStacked(true);
	        horizontalBarModel.setZoom(true);

	         
	        Axis xAxis = horizontalBarModel.getAxis(AxisType.X);
	        xAxis.setLabel("Metrica");
	        xAxis.setMin(0);
	        xAxis.setMax(valorMaior);
	         
	        Axis yAxis = horizontalBarModel.getAxis(AxisType.Y);
	        yAxis.setLabel("Quantidade");    
	        return horizontalBarModel;
	    }
	     

	    public BarChartModel createBarModel(String title, Map<String, ArrayList<Integer>> dadosChart, long tipoDados, int valorMaior ) {
	   
	    	barModel = initBarModel( dadosChart, tipoDados );
	         
	        barModel.setTitle(title);
	        barModel.setLegendPosition("ne");
	        barModel.setZoom(true);
	         
	        Axis xAxis = barModel.getAxis(AxisType.X);
	        xAxis.setLabel("Metrica");
	         
	        Axis yAxis = barModel.getAxis(AxisType.Y);
	        yAxis.setLabel("Quantidade");
	        yAxis.setMin(0);
	        yAxis.setMax(valorMaior);
	        
	        return barModel;
	    }

	    
	    private ArrayList<ChartSeries> graficoGeral(ArrayList<ChartSeries> series, Map<String, ArrayList<Integer>> dadosChart ){
		    for(String key : dadosChart.keySet()) {
		    	series.get(0).set(key, dadosChart.get(key).get(0));
		    }
	    	return series;
	    }

	    private ArrayList<ChartSeries> graficoMensalComSeries(ArrayList<ChartSeries> series, Map<String, ArrayList<Integer>> dadosChart  ){
	        for(int mes = 0; mes < dadosChart.size(); mes++) {
	        	String key = FiltrarDadosRelatorioEstatico.meses[mes];
	        	for(int i=0; i < series.size(); i++) {
			       	series.get(i).set(key, dadosChart.get(key).get(i));
		    	}
	        	
	        }
	    	return series;
	    }

	    private ArrayList<ChartSeries> graficoMensalSemSeries(ArrayList<ChartSeries> series, Map<String, ArrayList<Integer>> dadosChart  ){
	    	for(int mes = 0; mes < dadosChart.size(); mes++) {
	    		for(String m : FiltrarDadosRelatorioEstatico.meses ) {
	    			if(!series.get(0).getData().containsKey(m) && dadosChart.containsKey(m)) {
	    				series.get(0).set(m, dadosChart.get(m).get(0));
	    				break;
	    			}
	    		}
	    		
	    	}
	    	return series;
	    }
	    
	    private ArrayList<ChartSeries> graficoComSeries(ArrayList<ChartSeries> series, Map<String, ArrayList<Integer>> dadosChart ) {
	    	for(String key : dadosChart.keySet()) {
		    	for(int i=0; i < series.size(); i++) {
			       	series.get(i).set(key, dadosChart.get(key).get(i));
		    	}
		    }
	    	
	    	return series;
	    }
	    
	    
}
