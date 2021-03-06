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
	    	
	        ChartSeries total = new ChartSeries();
	        ChartSeries atendidas = new ChartSeries();
	    	ChartSeries semResposta = new ChartSeries();
	    	ChartSeries tramite = new ChartSeries();
	    	ChartSeries naoVisu = new ChartSeries();
	    	
	    	total.setLabel("Total");
	    	atendidas.setLabel("Atendidas");
	    	semResposta.setLabel("Sem Resposta");
	    	tramite.setLabel("Em Tr�mite");
	    	naoVisu.setLabel("N�o Visualizadas");
	    	
	    	variaveis.add(total);
	    	variaveis.add(atendidas);
	    	variaveis.add(semResposta);
	    	variaveis.add(tramite);
	    	variaveis.add(naoVisu);
	    	
	    	ChartSeries solicitacoes = new ChartSeries();
	    	solicitacoes.setLabel("Solicita��es");
	    	variaveis2.add(solicitacoes);
	    	
		    switch ((int)tipoDados) {
		    case 1:
		    	variaveis = graficoGeral(variaveis2, dadosChart);
		    	model.setExtender("ext");
		    	break;
			case 2:
				variaveis = graficoMensalComSeries(variaveis, dadosChart);	
				break;
//			case 3:
//				variaveis = graficoAnual(variaveis, dadosChart);	
//				break;
			 case 10:
		    	variaveis = graficoGeral(variaveis2, dadosChart);
		    	model.setExtender("ext");
		    	break;
			 case 11:
				variaveis = graficoMensalComSeries(variaveis, dadosChart);	
				break;
			default:
				variaveis = graficoComSeries(variaveis, dadosChart);
				break;
			}

	        for (ChartSeries chartSeries : variaveis) {
	        	model.addSeries(chartSeries);
			}
	        
	        model.setSeriesColors("47476b,005ce6,e60000,00b300,ff9900");
	         
	        return model;
	    }
	    
	    private HorizontalBarChartModel initHorizontalBarModel( Map<String, ArrayList<Integer>> dadosChart, long tipoDados ) {
	    	HorizontalBarChartModel model =  new HorizontalBarChartModel();
	    	ArrayList<ChartSeries> variaveis = new ArrayList<>();
	    	
	    	ChartSeries total = new ChartSeries();
	        ChartSeries atendidas = new ChartSeries();
	    	ChartSeries semResposta = new ChartSeries();
	    	ChartSeries tramite = new ChartSeries();
	    	ChartSeries naoVisu = new ChartSeries();
	    	
	    	total.setLabel("Total");
	    	atendidas.setLabel("Atendidas");
	    	semResposta.setLabel("Sem Resposta");
	    	tramite.setLabel("Em Tr�mite");
	    	naoVisu.setLabel("N�o Visualizadas");
	    	
	    	variaveis.add(total);
	    	variaveis.add(atendidas);
	    	variaveis.add(semResposta);
	    	variaveis.add(tramite);
	    	variaveis.add(naoVisu);
	    	
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
	    	
//	    	model.setSeriesColors("005ce6,e60000,00b300,ff9900");
	    	model.setSeriesColors("47476b,005ce6,e60000,00b300,ff9900");
	    	
	    	return model;
	    }
	    
	    public HorizontalBarChartModel createHorizontalBarModel(String title, Map<String, ArrayList<Integer>> dadosChart, long tipoDados, int valorMaior) {
	        
	        horizontalBarModel = initHorizontalBarModel(dadosChart, tipoDados);
	         
	        horizontalBarModel.setTitle(title);
	        horizontalBarModel.setLegendPosition("e");
//	        horizontalBarModel.setStacked(true);
	        horizontalBarModel.setZoom(true);
	        horizontalBarModel.setShowPointLabels(true);
	         
	        Axis xAxis = horizontalBarModel.getAxis(AxisType.X);
	        xAxis.setLabel("Metrica");
	        xAxis.setMin(0);
	        xAxis.setTickFormat("%i");
//	        xAxis.setTickAngle(-30);
	        xAxis.setMax(valorMaior);
	         
	        Axis yAxis = horizontalBarModel.getAxis(AxisType.Y);
	        yAxis.setLabel("Quantidade");  
	        return horizontalBarModel;
	    }
	     

	    public BarChartModel createBarModel(String title, Map<String, ArrayList<Integer>> dadosChart, long tipoDados, int valorMaior) {
	   
	    	barModel = initBarModel(dadosChart, tipoDados);
	         
	        barModel.setTitle(title);
	        barModel.setLegendPosition("n");
	        barModel.setLegendCols(5);
	        barModel.setZoom(true);
	        barModel.setShowPointLabels(true);
	        barModel.setShadow(false);
	        
	        Axis xAxis = barModel.getAxis(AxisType.X);
	        xAxis.setLabel("Metrica");
	        xAxis.setTickFormat("%i");
	         
	        Axis yAxis = barModel.getAxis(AxisType.Y);
	        yAxis.setLabel("Quantidade");
	        yAxis.setMin(0);
	        yAxis.setMax(valorMaior);
	        yAxis.setTickFormat("%i");
	        
	        if(tipoDados == 5 || tipoDados == 6) {
	        	xAxis.setTickAngle(30);
	        }
	        
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
