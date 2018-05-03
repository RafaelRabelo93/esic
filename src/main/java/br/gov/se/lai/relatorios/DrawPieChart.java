package br.gov.se.lai.relatorios;
import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import org.primefaces.model.chart.PieChartModel;

@ManagedBean
public class DrawPieChart implements Serializable{

	 

	 
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
	     
//	    private void createPieModels() {
//	        createPieModel1();
//	        createPieModel2();
//	    }
//	 
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
