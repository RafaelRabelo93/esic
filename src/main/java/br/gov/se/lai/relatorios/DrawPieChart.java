package br.gov.se.lai.relatorios;
import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.faces.bean.ManagedBean;

import org.primefaces.model.chart.DonutChartModel;
import org.primefaces.model.chart.PieChartModel;

@ManagedBean
public class DrawPieChart implements Serializable{

		/**
		 * Classe detinada ao desenho dos gráficos pizzas do relatório.
		 * 
		 * Os métodos para criação do objeto PieChart recebem um dicionário
		 * no formato <String referente a chave, arraylist referente aos valores>.
		 * 
		 * É válido lembrar que o PieChart só apresenta um tipo de valor relacionada
		 * a chave, e por isso o arraylist de valores sempre terá comprimento 1.
		 */

	 
	    private PieChartModel pieModel1;
	    private PieChartModel pieModel2;
	    private DonutChartModel donutModel;
	 
	    @PostConstruct
	    public void init() {
//	        createPieModels();
	    }
	 
	    public PieChartModel getPieModel1() {
	        return pieModel1;
	    }
	     
	    public PieChartModel getPieModel2() {
	        return pieModel2;
	    }
	     

	    @SuppressWarnings("unused")
		private PieChartModel createPieModel1(Map<String, ArrayList<Integer>> dadosChart, String title) {
	    	pieModel1 = new PieChartModel();	  
	    	
	        for(String key : dadosChart.keySet()) {
		    	pieModel1.set(key, dadosChart.get(key).get(0));
	        }
	        
	         
	        pieModel1.setTitle(title);
	        pieModel1.setLegendPosition("w");
	        
	        return pieModel1;
	    }
	     
	    public PieChartModel createPieModel2(Map<String, ArrayList<Integer>> dadosChart, String title, long tipoDados) {
	        pieModel2 = new PieChartModel();
	         
	        for(String key : dadosChart.keySet()) {
		    	pieModel2.set(key, dadosChart.get(key).get(0));
		    }
	         
	        pieModel2.setTitle(title);
	        pieModel2.setLegendPosition("e");
	        pieModel2.setFill(true);
	        pieModel2.setDiameter(225);
	        pieModel2.setShowDatatip(true);
	        pieModel2.setShadow(false);
	        if (tipoDados == 9 || tipoDados == 16)	pieModel2.setLegendCols(3);
	        
	        pieModel2.setSeriesColors("e6194B, 3cb44b, ffe119, 4363d8, f58231, 911eb4, 42d4f4, f032e6, bfef45, fabebe, 469990, e6beff, 9A6324, 800000, aaffc3, 808000, ffd8b1, 000075, a9a9a9");
	        
	        return pieModel2;
	    }
	    
	    public DonutChartModel createDonutModel(Map<String, ArrayList<Integer>> dadosChart, String title, long tipoDados) {
	    	donutModel = new DonutChartModel();
	    	
	    	Map<String, Number> circle1 = new LinkedHashMap<String, Number>();
	    	
	    	for(String key : dadosChart.keySet()) {
	    		circle1.put(key, dadosChart.get(key).get(0));
	    	}
	    	donutModel.addCircle(circle1);
	    	
	    	donutModel.setTitle(title);
	    	donutModel.setLegendPosition("e");
	    	donutModel.setFill(true);
	    	donutModel.setShowDataLabels(true);
	    	if (tipoDados == 9)	donutModel.setLegendCols(3);
	    	
//	    	donutModel.setSeriesColors("f3a935,c73558,6ebe9f,55596a,16a085,f39c12,e67e22,c0392b,bdc3c7,3498db,8e44ad,34495e");
	    	
	    	return donutModel;
	    }
	     
	
}
