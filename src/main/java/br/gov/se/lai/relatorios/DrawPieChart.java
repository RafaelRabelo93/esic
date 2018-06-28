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
		 * Classe detinada ao desenho dos gr�ficos pizzas do relat�rio.
		 * 
		 * Os m�todos para cria��o do objeto PieChart recebem um dicion�rio
		 * no formato <String referente a chave, arraylist referente aos valores>.
		 * 
		 * � v�lido lembrar que o PieChart s� apresenta um tipo de valor relacionada
		 * a chave, e por isso o arraylist de valores sempre ter� comprimento 1.
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
	        pieModel2.setFill(false);
	        pieModel2.setShowDataLabels(true);
	        pieModel2.setDiameter(250);
	        
	        return pieModel2;
	    }
	     
	
}
