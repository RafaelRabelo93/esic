package relatorio;

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

@ManagedBean(name = "barChartClass")
@SessionScoped
public class DrawBarChart {

	    private BarChartModel barModel;
	 
//	    @PostConstruct
//	    public void init() {
//	        createBarModels();
//	    }
//	 
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
				variaveis = graficoMensal(variaveis, dadosChart);	
				break;

			default:
				variaveis = graficoComSeries(variaveis, dadosChart);
				break;
			}

	        for (ChartSeries chartSeries : variaveis) {
	        	model.addSeries(chartSeries);
			}
	         
	        return model;
	    }
	     
//	    private void createBarModels() {
//	        createBarModel();
//	    }
	     
	    public BarChartModel createBarModel(String title, Map<String, ArrayList<Integer>> dadosChart, long tipoDados, int valorMaior ) {
	        barModel = initBarModel(dadosChart, tipoDados );
	         
	        barModel.setTitle(title);
	        barModel.setLegendPosition("ne");
	         
	        Axis xAxis = barModel.getAxis(AxisType.X);
	        xAxis.setLabel("Órgãos");
	         
	        Axis yAxis = barModel.getAxis(AxisType.Y);
	        yAxis.setLabel("Quantidade");
	        yAxis.setMin(0);
	        yAxis.setMax(valorMaior);
	        
	        return barModel;
	    }

	    
	    public ArrayList<ChartSeries> graficoGeral(ArrayList<ChartSeries> series, Map<String, ArrayList<Integer>> dadosChart ){
		    for(String key : dadosChart.keySet()) {
		    	series.get(0).set(key, dadosChart.get(key).get(0));
		    }
	    	return series;
	    }

	    public ArrayList<ChartSeries> graficoMensal(ArrayList<ChartSeries> series, Map<String, ArrayList<Integer>> dadosChart  ){
	        for(int mes = 0; mes < dadosChart.size(); mes++) {
	        	String key = FiltrarDadosRelatorioEstatico.meses[mes];
	        	for(int i=0; i < series.size(); i++) {
			       	series.get(i).set(key, dadosChart.get(key).get(i));
		    	}
	        	
	        }
	    	return series;
	    }
	    
	    public ArrayList<ChartSeries> graficoComSeries(ArrayList<ChartSeries> series, Map<String, ArrayList<Integer>> dadosChart ) {
	    	for(String key : dadosChart.keySet()) {
		    	for(int i=0; i < series.size(); i++) {
			       	series.get(i).set(key, dadosChart.get(key).get(i));
		    	}
		    }
	    	
	    	return series;
	    }
	    
	    
}
