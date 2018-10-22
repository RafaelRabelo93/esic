package br.gov.se.lai.relatorios;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
		
		/** Classe para desenhar o gr�fico em barras utilizado nos relat�rios.
		 * 
		 * 	Os gr�fico s�o montados a partir do recebimento de valores que s�o 
		 * passados aos m�todo em forma de dicion�rio no formato <Nome da base,
		 * lista que representa os valores das colunas>
		 * 
		 * 	Podemos ter dois tipos de gr�ficos em barra. O primeiro tipo s�o 
		 * aqueles que possuem as tr�s colunas relacionadas a base, sendo elas:
		 * situa��o total, situa��o em aberto, situa��o atentidada. O segundo 
		 * tipo s�o aquelas situa��es onde temos apenas uma coluna que se refere 
		 * a um valor total, ou seja, o valor do dicion�rio � passado como um 
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
	        ArrayList<ChartSeries> variaveis2 = new ArrayList<>();
	    	
	        ChartSeries tramite = new ChartSeries();
	    	ChartSeries finalizado = new ChartSeries();
	    	ChartSeries semResposta = new ChartSeries();
	    	ChartSeries total = new ChartSeries();
	    	
	    	tramite.setLabel("Em Tr�mite");
	    	finalizado.setLabel("Finalizados");
	    	semResposta.setLabel("Sem Resposta");
	    	total.setLabel("Total");
	    	
	    	variaveis.add(tramite);
	    	variaveis.add(finalizado);
	    	variaveis.add(semResposta);
	    	variaveis.add(total);
	    	
	    	ChartSeries solicitacoes = new ChartSeries();
	    	solicitacoes.setLabel("Solicita��es");
	    	variaveis2.add(solicitacoes);
	    	
		    switch ((int)tipoDados) {
		    case 1:
		    	variaveis = graficoGeral(variaveis2, dadosChart);	
		    	break;
			case 2:
				variaveis = graficoMensalComSeries(variaveis, dadosChart);	
				break;
//			case 3:
//				variaveis = graficoAnual(variaveis, dadosChart);	
//				break;
			default:
				variaveis = graficoComSeries(variaveis, dadosChart);
				break;
			}

	        for (ChartSeries chartSeries : variaveis) {
	        	model.addSeries(chartSeries);
			}
	        
	        model.setSeriesColors("2196f3,858f9c,e77b40,5cb85c");
	         
	        return model;
	    }
	    
	    private HorizontalBarChartModel initHorizontalBarModel( Map<String, ArrayList<Integer>> dadosChart, long tipoDados ) {
	    	HorizontalBarChartModel model =  new HorizontalBarChartModel();
	    	ArrayList<ChartSeries> variaveis = new ArrayList<>();
	    	
	    	ChartSeries tramite = new ChartSeries();
	    	ChartSeries finalizado = new ChartSeries();
	    	ChartSeries semResposta = new ChartSeries();
//	    	ChartSeries total = new ChartSeries();
	    	
	    	tramite.setLabel("Em Tr�mite");
	    	finalizado.setLabel("Finalizados");
	    	semResposta.setLabel("Sem Resposta");
//	    	total.setLabel("Total");
	    	
	    	variaveis.add(tramite);
	    	variaveis.add(finalizado);
	    	variaveis.add(semResposta);
//	    	variaveis.add(total);
	    	
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
	    	
	    	model.setSeriesColors("2196f3,858f9c,e77b40");
	    	
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
//	        xAxis.setTickInterval("1");
	        xAxis.setMax(valorMaior);
	         
	        Axis yAxis = horizontalBarModel.getAxis(AxisType.Y);
	        yAxis.setLabel("Quantidade");    
	        return horizontalBarModel;
	    }
	     

	    public BarChartModel createBarModel(String title, Map<String, ArrayList<Integer>> dadosChart, long tipoDados, int valorMaior) {
	   
	    	barModel = initBarModel( dadosChart, tipoDados );
	         
	        barModel.setTitle(title);
	        barModel.setLegendPosition("ne");
	        barModel.setZoom(true);
//	        if (tipoDados == 3) {
//	        	barModel.setExtender("ext");
//	        }
	        
	        Axis xAxis = barModel.getAxis(AxisType.X);
	        xAxis.setLabel("Metrica");
	        xAxis.setTickInterval("1");
	         
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
//	        Collections.reverse(series);
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
		    	for(int i = series.size()-1; i >= 0; i--) {
			       	series.get(i).set(key, dadosChart.get(key).get(i));
		    	}
		    }
	    	
	    	return series;
	    }
	    
	    
}
