package br.gov.se.lai.utils;

import java.util.ArrayList;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;

@ManagedBean(name = "relatorioOrgao")
@SessionScoped
public class RelatorioOrgao {

	    private BarChartModel barModel;
	 
	    @PostConstruct
	    public void init() {
	        createBarModels();
	    }
	 
	    public BarChartModel getBarModel() {
	        return barModel;
	    }
	     
	 
	    private BarChartModel initBarModel() {
	        BarChartModel model = new BarChartModel();
	 
	        Map<String, ArrayList<Integer>> dadosChart = FiltrarDadosRelatorioEstatico.gerarAcompanhamentoAnualPedidoInformacao();
	        
	        ChartSeries pedido = new ChartSeries();
	        ChartSeries aberto = new ChartSeries();
	        ChartSeries atendido = new ChartSeries();
	        
	        pedido.setLabel("Pedido");
	        aberto.setLabel("Em aberto");
	        atendido.setLabel("Atendidos");
	        
	        for(String key : dadosChart.keySet()) {
	        	pedido.set(key, dadosChart.get(key).get(0));
	        	aberto.set(key, dadosChart.get(key).get(1));
	        	atendido.set(key, dadosChart.get(key).get(2));
	        	
	        }
	        
//	        for(int mes = 0; mes < dadosChart.size(); mes++) {
//	        	String key = FiltrarDadosRelatorioEstatico.meses[mes];
//	        	pedido.set(key, dadosChart.get(key).get(0));
//	        	aberto.set(key, dadosChart.get(key).get(1));
//	        	atendido.set(key, dadosChart.get(key).get(2));
//	        	
//	        }
	        
	        model.addSeries(pedido);
	        model.addSeries(aberto);
	        model.addSeries(atendido);
	         
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
	        yAxis.setMax(50);
	    }
	     
}
