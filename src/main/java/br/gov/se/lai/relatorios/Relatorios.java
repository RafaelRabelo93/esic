package br.gov.se.lai.relatorios;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.AjaxBehaviorEvent;

import org.primefaces.context.RequestContext;
import org.primefaces.event.ItemSelectEvent;
import org.primefaces.event.MoveEvent;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.DonutChartModel;
import org.primefaces.model.chart.HorizontalBarChartModel;
import org.primefaces.model.chart.PieChartModel;

import br.gov.se.lai.Bean.EntidadesBean;
import br.gov.se.lai.DAO.EntidadesDAO;
import br.gov.se.lai.DAO.SolicitacaoDAO;
import br.gov.se.lai.entity.Cidadao;
import br.gov.se.lai.entity.Entidades;

@ManagedBean(name = "relatorios")
@SessionScoped
public class Relatorios {

	public int tipoRelatorio;
	public int tipoGrafico;
	public Map<String, ArrayList<Integer>> dadosChart;
	public Entidades entidade;
	public int idEntidade = 0;
	public String sigla;
	public String[] tipo = new String[18];
	public int[] metricas;
	public boolean dataBool;
	public boolean mesesBool;
	public boolean anosBool;
	public boolean orgaoBool;
	public boolean entidadeBool;
	public boolean estadosBool;
	public boolean assuntoBool;
	public boolean tipoPessoaBool;
	public Date periodoIni;
	public Date periodoFim;
//	public String[] meses ;
	public String[] anosSelecionados;
	public String[] orgaos;
	public String[] entidades;
	public String[] assuntos;
	public String tipoPessoa;
	public String[] estado;
	public String tipoSolicitacao;
	public String param;
	public String status;
//	public ArrayList<Integer> anos;
	public BarChartModel barModel;
	public HorizontalBarChartModel hBarModel;
	public List<Integer> anos;
	public List<Integer> meses;
	public Integer anoInicial;
	public Integer mesInicial;
	public Map<String, Integer> mesesDoAno;
	public int customTooltip;

	public int anoFinal = 2018;
	public int mesFinal;
		
	@PostConstruct
	public void Relatorios() {
		anos = SolicitacaoDAO.listarAnos();
		anoFinal = FiltrarDadosRelatorioEstatico.getAnoFinal();
		mesFinal = FiltrarDadosRelatorioEstatico.getMesFinal();
//		anos = retornarListaAnosAteHoje();
		customTooltip = 99;
	}
	
	@SuppressWarnings("unused")
	public String gerarPeriodo() {
		FiltrarDadosRelatorioEstatico.setMesFinal(mesFinal);
		FiltrarDadosRelatorioEstatico.setAnoFinal(anoFinal);
		FiltrarDadosRelatorioEstatico.setIdEntidade(idEntidade);
		return "Relatorios/relatorio_mensal.xhtml";
	}
	
//	public void showCustomTooltip(ItemSelectEvent event) {
//		System.out.println("Antes :" + customTooltip);
////		if (customTooltip != 99) {
////			RequestContext.getCurrentInstance().closeDialog("dashboard" + customTooltip);
////		}
//		customTooltip = event.getItemIndex();
//		org.primefaces.context.RequestContext.getCurrentInstance().execute("PF('dashboard" + customTooltip + "').show();");
//		System.out.println("Depois :" + customTooltip);
//    }
	
	/**
	 * Significado dos inteiros que direcionam qual tipo de filtragem irá ser realizada.
	 * 
	 * 1 - pedidos totais do esic
	 * 2- pedidos mensais
	 * 3- pedidos anuais
	 * 4- pedidos acumulados
	 * 5 - pedidos por orgao
	 * 6 - pedidos por entidade
	 * 7- pedidos por tema
	 * 8 - pedidos por tipo de pessoa
	 * 9- uf
	 * @return 
	 */
	
	public Map<String, ArrayList<Integer>> redirecionarFiltroDados(long tipoDados) {
		
		switch ((int)tipoDados){
		case 1:
			dadosChart = FiltrarDadosRelatorioEstatico.gerarAcompanhamentoGeralDosPedidosInformacao();
			tipo[0] = "Pedidos Totais do E-SIC - " +getMesAtual() + " de " +anoFinal;
			break;
		case 2:
			dadosChart = FiltrarDadosRelatorioEstatico.gerarAcompanhamentoMensalPedidoInformacao();
			tipo[1] = "Pedidos Mensais do E-SIC - " +getMesAtual() + " de " +anoFinal;
			break;
		case 3:
			dadosChart = FiltrarDadosRelatorioEstatico.gerarAcompanhamentoAnualPedidoInformacao();
			tipo[2] = "Pedidos Anuais do E-SIC - " +getMesAtual() + " de " +anoFinal;
			break;
		case 4:
			dadosChart = FiltrarDadosRelatorioEstatico.gerarAcompanhamentoAnualAcumuladoPedidoInformacao();
			tipo[3] = "Pedidos Anuais Acumulados do E-SIC - " +getMesAtual() + " de " +anoFinal;
			break;
		case 5:
			dadosChart = FiltrarDadosRelatorioEstatico.gerarAcompanhamentoOrgaoPedidoInformacao();
			tipo[4] = "Pedidos por Órgão da Administração Direta - " +getMesAtual() + " de " +anoFinal;
			break;
		case 6:
			dadosChart = FiltrarDadosRelatorioEstatico.gerarAcompanhamentoEntidadePedidoInformacao();
			tipo[5] = "Pedidos por Entidade Órgão da Administração Indireta - " +getMesAtual() + " de " +anoFinal;
			break;
		case 7:
			dadosChart = FiltrarDadosRelatorioEstatico.gerarAcompanhamentoAssuntoPedidoInformacao();
			tipo[6] = "Pedidos Mensais por Tema do E-SIC  - " +getMesAtual() + " de " +anoFinal;
			break;
		case 8:
			dadosChart = FiltrarDadosRelatorioEstatico.gerarAcompanhamentoTipoPessoaGeneroPedidoInformacao();
			tipo[7] = "Pedidos Totais por Tipo e Gênero de Pessoa - " +getMesAtual() + " de " +anoFinal;
			break;
		case 9:
			dadosChart = FiltrarDadosRelatorioEstatico.gerarAcompanhamentoEstadosPedidoInformacao();
			tipo[8] = "Pedidos Totais por Ente Federativo - " +getMesAtual() + " de " +anoFinal;
			break;
		case 10:
			dadosChart = FiltrarDadosRelatorioEstatico.gerarAcompanhamentoGeralPorEntidade(idEntidade);
			entidade = EntidadesDAO.find(idEntidade);
			tipo[9] = "Pedidos Totais da " +entidade.getSigla() + " - " +getMesAtual() + " de " +anoFinal;
			break;
		case 11:
			dadosChart = FiltrarDadosRelatorioEstatico.gerarAcompanhamentoMensalPorEntidade(idEntidade);
			tipo[10] = "Pedidos Mensais da " +entidade.getSigla() + " - " +getMesAtual() + " de " +anoFinal;
			break;
		case 12:
			dadosChart = FiltrarDadosRelatorioEstatico.gerarAcompanhamentoAnualPorEntidade(idEntidade);
			tipo[11] = "Pedidos Anuais da " +entidade.getSigla() + " - " +getMesAtual() + " de " +anoFinal;
			break;
		case 13:
			dadosChart = FiltrarDadosRelatorioEstatico.gerarAcompanhamentoAnualAcumuladoPorEntidade(idEntidade);
			tipo[12] = "Pedidos Anuais acumulados da " +entidade.getSigla() + " - " +getMesAtual() + " de " +anoFinal;
			break;
		case 14:
			dadosChart = FiltrarDadosRelatorioEstatico.gerarAcompanhamentoAssuntoPorEntidade(idEntidade);
			tipo[13] = "Pedidos Mensais por Tema da " +entidade.getSigla() + " - " +getMesAtual() + " de " +anoFinal;
			break;
		case 15:
			dadosChart = FiltrarDadosRelatorioEstatico.gerarAcompanhamentoTipoPessoaGeneroPorEntidade(idEntidade);
			tipo[14] = "Pedidos Totais por Tipo da " +entidade.getSigla() + " - " +getMesAtual() + " de " +anoFinal;
			break;
		case 16:
			dadosChart = FiltrarDadosRelatorioEstatico.gerarAcompanhamentoEstadosPorEntidade(idEntidade);
			tipo[15] = "Pedidos Totais por Ente Federativo da " +entidade.getSigla() + " - " +getMesAtual() + " de " +anoFinal;
			break;
		case 17:
			dadosChart = FiltrarDadosRelatorioEstatico.gerarAcompanhamentoTiposSolicitacoes();
//			tipo[16] = "Tipos de Manifestações";
			break;
		case 18:
			dadosChart = FiltrarDadosRelatorioEstatico.gerarAcompanhamentoTiposSolicitacoesPorEntidade();
			break;
		}
			
		
		return dadosChart;
	}
	
	public void desenhar(long tipoDados) {
		
		if(tipoDados == (long)7 || tipoDados == (long)8 || tipoDados == (long)9) {
			desenharPieChart( tipoDados);
		}else {
			desenharBarChart( tipoDados);
		}
	}
	
	/**
	 * Gera o dicionário com os valores que preencheram o gráfico para então criar o objeto PieChart.
	 * @param tipoDados
	 * @return
	 */
	public PieChartModel desenharPieChart( long tipoDados) {
		redirecionarFiltroDados(tipoDados);
		DrawPieChart modelPie = new DrawPieChart();
		return modelPie.createPieModel2(dadosChart, tipo[(int)tipoDados-1], tipoDados, 255);
	}
	
	public PieChartModel desenharBigPieChart( long tipoDados) {
		redirecionarFiltroDados(tipoDados);
		DrawPieChart modelPie = new DrawPieChart();
		return modelPie.createPieModel2(dadosChart, tipo[(int)tipoDados-1], tipoDados, 400);
	}
	
	public DonutChartModel desenharDonutChart( long tipoDados) {
		redirecionarFiltroDados(tipoDados);
		DrawPieChart modelPie = new DrawPieChart();
		return modelPie.createDonutModel(dadosChart, tipo[(int)tipoDados-1], tipoDados);
	}
	
	/**
	 * Gera o dicionário com os valores que preencheram o gráfico para então criar o objeto BarChart,
	 * além de verificar qual o valor mais alto para estabelecer qual a altura máxima do gráfico.
	 * @param tipoDados
	 * @return
	 */
	public BarChartModel desenharBarChart( long tipoDados) {
		redirecionarFiltroDados(tipoDados);
		int valorMaior = identificarValorMaximoGrafico(dadosChart);
		DrawBarChart model = new DrawBarChart();
		return model.createBarModel(tipo[(int)tipoDados-1], dadosChart, tipoDados, valorMaior);
	}
	
	public HorizontalBarChartModel desenharBarChartHorizontal( long tipoDados) {
		redirecionarFiltroDados(tipoDados);
		int valorMaior = identificarValorMaximoGrafico(dadosChart);
		DrawBarChart model = new DrawBarChart();
		return model.createHorizontalBarModel(tipo[(int)tipoDados-1], dadosChart, tipoDados, valorMaior);
		
	}
	
	public int identificarValorMaximoGrafico(Map<String, ArrayList<Integer>> dadosChart) {
		int valorMaior = 0;
		for (String key : dadosChart.keySet()) {
			ArrayList<Integer> list = dadosChart.get(key);
			valorMaior = Collections.max(list,null) > valorMaior ? Collections.max(list,null) : valorMaior;
		}
		
		if (valorMaior*0.2 < 10) {
			return ((valorMaior/10)*10)+10;
		} 
		else return (int) ((valorMaior+(valorMaior*0.2))/10)*10;
	}
	
	public ArrayList<Integer> retornarListaAnosAteHoje() {
		ArrayList<Integer> anos = new ArrayList<>();
		Calendar a = Calendar.getInstance();
		for (int i = 2012; i <= a.get(Calendar.YEAR); i++) {
			anos.add(i);
		}
		Collections.reverse(anos);
		return anos;
	}
	
	public int getAnoAtual() {
		Calendar c = Calendar.getInstance();
		int anoAtual = c.get(Calendar.YEAR);
		
		return anoAtual;
	}
	
	public String getMesAtual() {
		String[] meses = { "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro" };
		
		return meses[mesFinal-1];
	}
	
	public String getSigla() {
		List<String> sigla = EntidadesDAO.getSigla(48);
		
		if (sigla.isEmpty()) {
			return "O";
		} else {
			System.out.println(sigla);
			return sigla.get(0);
		}
			
//		return sigla;
	}
	
	public void setarData() {
//		FiltrarDadosRelatorioEstatico.set
		FiltrarDadosRelatorioEstatico.setAnoFinal(anoFinal);
		FiltrarDadosRelatorioEstatico.setMesFinal(mesFinal);
	}
	
	public void listarMesesDoAno() {
		meses = SolicitacaoDAO.listarMesesDoAno(anoFinal);
		
//		for (Integer temp : meses) {
//			
//			switch (temp) {
//				case 1:
//					mesesDoAno.put("Janeiro", temp);
//					break;
//				case 2:
//					mesesDoAno.put("Fevereiro", temp);
//					break;
//				case 3:
//					mesesDoAno.put("Março", temp);
//					break;
//				case 4:
//					mesesDoAno.put("Abril", temp);
//					break;
//				case 5:
//					mesesDoAno.put("Maio", temp);
//					break;
//				case 6:
//					mesesDoAno.put("Junho", temp);
//					break;
//				case 7:
//					mesesDoAno.put("Julho", temp);
//					break;
//				case 8:
//					mesesDoAno.put("Agosto", temp);
//					break;
//				case 9:
//					mesesDoAno.put("Setembro", temp);
//					break;
//				case 10:
//					mesesDoAno.put("Outubro", temp);
//					break;
//				case 11:
//					mesesDoAno.put("Novembro", temp);
//					break;
//				case 12:
//					mesesDoAno.put("Dezembro", temp);
//					break;
//			}
//		}
	}

	public int getTipoRelatorio() {
		return tipoRelatorio;
	}
	public void setTipoRelatorio(int tipoRelatorio) {
		this.tipoRelatorio = tipoRelatorio;
	}
	public int getTipoGrafico() {
		return tipoGrafico;
	}
	public void setTipoGrafico(int tipoGrafico) {
		this.tipoGrafico = tipoGrafico;
	}

	public boolean isDataBool() {
		return dataBool;
	}

	public void setDataBool(boolean newData) {
			dataBool = newData;
	}
	
	public boolean isMesesBool() {
		return mesesBool;
	}

	public void setMesesBool(boolean mesesBool) {
		this.mesesBool = mesesBool;
	}

	public boolean isAnosBool() {
		return anosBool;
	}

	public void setAnosBool(boolean anosBool) {
		this.anosBool = anosBool;
	}

	public boolean isOrgaoBool() {
		return orgaoBool;
	}

	public void setOrgaoBool(boolean newOrgao) {
			orgaoBool = newOrgao;
	}

	public boolean isEntidadeBool() {
		return entidadeBool;
	}

	public void setEntidadeBool(boolean newEntidade) {
		entidadeBool = newEntidade;
		
	}

	public boolean isEstadosBool() {
		return estadosBool;
	}

	public void setEstadosBool(boolean newEstado) {
			estadosBool = newEstado;
	}

	public boolean isAssuntoBool() {
		return assuntoBool;
	}

	public void setAssuntoBool(boolean newAss) {
		assuntoBool = newAss;
	}

	public boolean isTipoPessoaBool() {
		return tipoPessoaBool;
	}

	public void setTipoPessoaBool(boolean newTipo) {
			tipoPessoaBool = newTipo;
	}

	public int[] getMetricas() {
		return metricas;
	}

	public void setMetricas(int[] metricas) {
		this.metricas = metricas;
	}

	public Date getPeriodoIni() {
		return periodoIni;
	}

	public void setPeriodoIni(Date periodoIni) {
		this.periodoIni = periodoIni;
	}

	public Date getPeriodoFim() {
		return periodoFim;
	}

	public void setPeriodoFim(Date periodoFim) {
		this.periodoFim = periodoFim;
	}

//	public String[] getMeses() {
//		return meses;
//	}
//
//	public void setMeses(String[] meses) {
//		this.meses = meses;
//	}

	public String[] getOrgaos() {
		return orgaos;
	}

	public void setOrgaos(String[] orgaos) {
		this.orgaos = orgaos;
	}

	public String[] getEntidades() {
		return entidades;
	}

	public void setEntidades(String[] entidades) {
		this.entidades = entidades;
	}

	public String[] getAssuntos() {
		return assuntos;
	}

	public void setAssuntos(String[] assuntos) {
		this.assuntos = assuntos;
	}

	public String getTipoPessoa() {
		return tipoPessoa;
	}

	public void setTipoPessoa(String tipoPessoa) {
		this.tipoPessoa = tipoPessoa;
	}

	public String[] getEstado() {
		return estado;
	}

	public void setEstado(String[] estado) {
		this.estado = estado;
	}

	public String  getTipoSolicitacao() {
		return tipoSolicitacao;
	}

	public void setTipoSolicitacao(String  tipoSolicitacao) {
		this.tipoSolicitacao = tipoSolicitacao;
	}

	public String  getTempo() {
		return param;
	}

	public void setTempo(String  param) {
		this.param = param;
	}

	public String  getStatus() {
		return status;
	}

	public void setStatus(String  status) {
		this.status = status;
	}

//	public ArrayList<Integer> getAnos() {
//		return anos;
//	}
//
//	public void setAnos(ArrayList<Integer> anos) {
//		this.anos = anos;
//	}

	public String[] getAnosSelecionados() {
		return anosSelecionados;
	}

	public void setAnosSelecionados(String[] anosSelecionados) {
		this.anosSelecionados = anosSelecionados;
	}

	public BarChartModel getBarModel() {
		return barModel;
	}

	public void setBarModel(BarChartModel barModel) {
		this.barModel =  barModel;
	}

	public HorizontalBarChartModel gethBarModel() {
		return hBarModel;
	}

	public void sethBarModel(HorizontalBarChartModel hBarModel) {
		this.hBarModel = hBarModel;
	}

	public int getIdEntidade() {
		return idEntidade;
	}
	
	public void setIdEntidade(int idEntidade) {
		this.idEntidade = idEntidade;
	}
	
	public Entidades getEntidade() {
		return entidade;
	}
	
	public void setEntidade(Entidades entidade) {
		this.entidade = entidade;
	}
	
	public List<Integer> getAnos() {
		return anos;
	}
	
	public void setAnos(List<Integer> anos) {
		this.anos = anos;
	}
	
	public List<Integer> getMeses() {
		return meses;
	}
	
	public void setMeses(List<Integer> meses) {
		this.meses = meses;
	}
	
	public Integer getAnoInicial() {
		return anoInicial;
	}


	public void setAnoInicial(Integer anoInicial) {
		this.anoInicial = anoInicial;
	}


	public Integer getAnoFinal() {
		return anoFinal;
	}


	public void setAnoFinal(Integer anoFinal) {
		this.anoFinal = anoFinal;
	}


	public Integer getMesInicial() {
		return mesInicial;
	}


	public void setMesInicial(Integer mesInicial) {
		this.mesInicial = mesInicial;
	}


	public Integer getMesFinal() {
		return mesFinal;
	}

	public void setMesFinal(Integer mesFinal) {
		this.mesFinal = mesFinal;
	}
	
	public Map<String, Integer> getMesesDoAno() {
		return mesesDoAno;
	}

	public void setMesesDoAno(Map<String, Integer> mesesDoAno) {
		this.mesesDoAno = mesesDoAno;
	}

	
	public int getCustomTooltip() {
		return customTooltip;
	}

	public void setCustomTooltip(int customTooltip) {
		this.customTooltip = customTooltip;
	}
}
