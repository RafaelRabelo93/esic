package br.gov.se.lai.relatorios;
import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

import javax.faces.bean.ManagedBean;
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
	     
	    @SuppressWarnings("unused")
		public PieChartModel createPieModel2(Map<String, ArrayList<Integer>> dadosChart, String title) {
	        pieModel2 = new PieChartModel();
	         
	        for(String key : dadosChart.keySet()) {
		    	pieModel2.set(key, dadosChart.get(key).get(0));
		    }
	         
	        pieModel2.setTitle(title);
	        pieModel2.setLegendPosition("e");
	        pieModel2.setFill(true);
	        pieModel2.setShowDataLabels(true);
	        pieModel2.setDiameter(225);
	        
	        pieModel2.setSeriesColors("f3a935,c73558,6ebe9f,55596a,16a085,f39c12,e67e22,c0392b,bdc3c7,3498db,8e44ad,34495e");
	        
	        return pieModel2;
	    }
	     
	
}
