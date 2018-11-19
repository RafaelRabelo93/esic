package br.gov.se.lai.relatorios;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.imageio.ImageIO;
import javax.swing.plaf.basic.BasicBorders.MarginBorder;

import java.io.File;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.color.PDJPXColorSpace;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import br.gov.se.lai.DAO.EntidadesDAO;

@ManagedBean(name = "chartExport")
@SessionScoped
public class ChartExport {
	
	private String base64Str;
	private StreamedContent file;
	
	private String chartGeral;
	private String chartMensal;
	private String chartAnual;
	private String chartAnualAcu;
	private String chartOrgao;
	private String chartEntidade;
	private String chartAssunto;
	private String chartPessoa;
	private String chartEstado;

	private String nome;
	
	public void submittedBase64Str(ActionEvent event) throws IOException{
		// You probably want to have a more comprehensive check here. 
	    // In this example I only use a simple check
	    if(base64Str.split(",").length > 1){
	        String encoded = base64Str.split(",")[1];
	        byte[] decoded = org.apache.commons.codec.binary.Base64.decodeBase64(encoded);
	        //	            RenderedImage renderedImage = ImageIO.read(new ByteArrayInputStream(decoded));
			//	            ImageIO.write(renderedImage, "png", new File("C:\\Users\\mmmendonca\\Pictures\\chart\\chart.png")); // use a proper path & file name here.
	            InputStream in = new ByteArrayInputStream(decoded);
	            file = new DefaultStreamedContent(in, "image/png", "chart.png");
	    }
	    
	}
	
	
	public void gerarRelatorio(short tipo) throws IOException {
		
		PDDocument document = new PDDocument();  
		PDPageContentStream contentStream;
		
		try {
	      
	      // Cria página A4 em paisagem
			PDPage page, page2, page3, page4, page5, page6, page7, page8, page9;
			page = new PDPage(new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth()));
			page2 = new PDPage(new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth()));
			page3 = new PDPage(new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth()));
			page4 = new PDPage(new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth()));
			page7 = new PDPage(new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth()));
			page8 = new PDPage(new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth()));
			page9 = new PDPage(new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth()));
			  
			PDRectangle pageSize = page.getMediaBox();
			float pageWidth = pageSize.getWidth();
			float pageHeight = pageSize.getHeight();
	      
	      
	      // Criar Imagens
		PDImageXObject imgChartGeral, imgChartMensal, imgChartAnual, imgChartAnualAcu, imgChartOrgao, imgChartEntidade, imgChartAssunto, imgChartPessoa, imgChartEstado;
		imgChartGeral = LosslessFactory.createFromImage(document, criarImagem(chartGeral));
		imgChartMensal = LosslessFactory.createFromImage(document, criarImagem(chartMensal));
		imgChartAnual = LosslessFactory.createFromImage(document, criarImagem(chartAnual));
		imgChartAnualAcu = LosslessFactory.createFromImage(document, criarImagem(chartAnualAcu));
		imgChartAssunto = LosslessFactory.createFromImage(document, criarImagem(chartAssunto));
		imgChartPessoa = LosslessFactory.createFromImage(document, criarImagem(chartPessoa));
		imgChartEstado = LosslessFactory.createFromImage(document, criarImagem(chartEstado));
	      
//	      Path folder = Paths.get(FacesContext.getCurrentInstance().getExternalContext().getRealPath("/"));
	      
//	      PDImageXObject pdImage = PDImageXObject.createFromFile(folder.toString()+"\\resources\\img\\esiclogo_se.png", document);
//	      PDImageXObject pdImage = PDImageXObject.createFromFile(ChartExport.class.getResource("resources/img/esiclogo_se.png").getPath(), document);
	      
	      PDFont font = PDType1Font.HELVETICA;
	      int marginTop = 30;
	      int fontSize = 20;
	      
	      Color color = new Color(52, 105, 170);
	      
	      String line1 = "Acesso à Informação Pública - Transparência Passiva";
	      String line2 = "Estatística Mensal de Atendimento - Art. 30 , III, da Lei nº 12.527/2011";
	      String line3 = "Relatório Geral - " + getMesFinal() + " de " + FiltrarDadosRelatorioEstatico.anoFinal;
	      String _line3 = "Relatório " + EntidadesDAO.find(FiltrarDadosRelatorioEstatico.idEntidade).getSigla() + " - " + getMesFinal() + " de " + FiltrarDadosRelatorioEstatico.anoFinal;
	      String fonte = "Fonte: esic.se.gov.br";
	      String data = "Gerado em " + getDataAtual() ;
	      
	      // Página 01
	      contentStream = new PDPageContentStream(document, page);
	      
	      contentStream.setNonStrokingColor(color);
	      contentStream.addRect(170, 500, 700, 70);
	      contentStream.fill();
	      
	      contentStream.beginText();
	      	contentStream.setNonStrokingColor(Color.WHITE);
	      	contentStream.setLeading(15f);
		  	contentStream.setFont(font, fontSize);
		  	contentStream.newLineAtOffset(startX(font, fontSize, line1, pageWidth), startY(font, fontSize, pageHeight, marginTop));
		  	contentStream.showText(line1);
		  	contentStream.newLine();
		  	contentStream.setFont(font, 14);
		  	contentStream.showText(line2);
		  	contentStream.newLine();
		  	if(tipo == 1) contentStream.showText(line3); else contentStream.showText(_line3);
	      contentStream.endText();

//	      contentStream.drawImage(pdImage, (float) (pageWidth*0.05), (float) (pageHeight*0.80), pdImage.getWidth(), pdImage.getHeight());
	      contentStream.drawImage(imgChartGeral, (float) (pageWidth*0.05), (float) (pageHeight*0.2), (float) (imgChartGeral.getWidth()*0.85), (float) (imgChartGeral.getHeight()*0.85));
	      
	      contentStream.beginText();
		      contentStream.setNonStrokingColor(Color.DARK_GRAY);
		      contentStream.setFont(font, 10);
		      contentStream.newLineAtOffset(startX(font, 10, fonte, pageWidth), 30);
		      contentStream.showText(fonte);
	      contentStream.endText();
	      
	      contentStream.beginText();
		      contentStream.setNonStrokingColor(Color.DARK_GRAY);
		      contentStream.setFont(font, 7);
		      contentStream.newLineAtOffset(startX(font, 7, data, pageWidth), 20);
		      contentStream.showText(data);
	      contentStream.endText();
	      
	      contentStream.close();
	      document.addPage(page);
	      
	      // Página 02
	      contentStream = new PDPageContentStream(document, page2);

	      contentStream.setNonStrokingColor(color);
	      contentStream.addRect(170, 500, 700, 70);
	      contentStream.fill();
	      
	      contentStream.beginText();
	      	contentStream.setNonStrokingColor(Color.WHITE);
	      	contentStream.setLeading(15f);
		  	contentStream.setFont(font, fontSize);
		  	contentStream.newLineAtOffset(startX(font, fontSize, line1, pageWidth), startY(font, fontSize, pageHeight, marginTop));
		  	contentStream.showText(line1);
		  	contentStream.newLine();
		  	contentStream.setFont(font, 14);
		  	contentStream.showText(line2);
		  	contentStream.newLine();
		  	if(tipo == 1) contentStream.showText(line3); else contentStream.showText(_line3);
	      contentStream.endText();
	      
	      contentStream.drawImage(imgChartMensal, (float) (pageWidth*0.05), (float) (pageHeight*0.2), (float) (imgChartMensal.getWidth()*0.85), (float) (imgChartMensal.getHeight()*0.85));
	      
	      contentStream.beginText();
		      contentStream.setNonStrokingColor(Color.DARK_GRAY);
		      contentStream.setFont(font, 10);
		      contentStream.newLineAtOffset(startX(font, 10, fonte, pageWidth), 30);
		      contentStream.showText(fonte);
	      contentStream.endText();
	      
	      contentStream.beginText();
		      contentStream.setNonStrokingColor(Color.DARK_GRAY);
		      contentStream.setFont(font, 7);
		      contentStream.newLineAtOffset(startX(font, 7, data, pageWidth), 20);
		      contentStream.showText(data);
	      contentStream.endText();
	      
	      contentStream.close();
	      document.addPage(page2);
	      
	      // Página 03
	      contentStream = new PDPageContentStream(document, page3);

	      contentStream.setNonStrokingColor(color);
	      contentStream.addRect(170, 500, 700, 70);
	      contentStream.fill();
	      
	      contentStream.beginText();
	      	contentStream.setNonStrokingColor(Color.WHITE);
	      	contentStream.setLeading(15f);
		  	contentStream.setFont(font, fontSize);
		  	contentStream.newLineAtOffset(startX(font, fontSize, line1, pageWidth), startY(font, fontSize, pageHeight, marginTop));
		  	contentStream.showText(line1);
		  	contentStream.newLine();
		  	contentStream.setFont(font, 14);
		  	contentStream.showText(line2);
		  	contentStream.newLine();
		  	if(tipo == 1) contentStream.showText(line3); else contentStream.showText(_line3);
	      contentStream.endText();

	      contentStream.drawImage(imgChartAnual, (float) (pageWidth*0.05), (float) (pageHeight*0.2), (float) (imgChartAnual.getWidth()*0.85), (float) (imgChartAnual.getHeight()*0.85));
	      
	      contentStream.beginText();
		      contentStream.setNonStrokingColor(Color.DARK_GRAY);
		      contentStream.setFont(font, 10);
		      contentStream.newLineAtOffset(startX(font, 10, fonte, pageWidth), 30);
		      contentStream.showText(fonte);
	      contentStream.endText();
	      
	      contentStream.beginText();
		      contentStream.setNonStrokingColor(Color.DARK_GRAY);
		      contentStream.setFont(font, 7);
		      contentStream.newLineAtOffset(startX(font, 7, data, pageWidth), 20);
		      contentStream.showText(data);
	      contentStream.endText();
	      
	      contentStream.close();
	      document.addPage(page3);
	      
	      // Página 04
	      contentStream = new PDPageContentStream(document, page4);

	      contentStream.setNonStrokingColor(color);
	      contentStream.addRect(170, 500, 700, 70);
	      contentStream.fill();
	      
	      contentStream.beginText();
	      	contentStream.setNonStrokingColor(Color.WHITE);
	      	contentStream.setLeading(15f);
		  	contentStream.setFont(font, fontSize);
		  	contentStream.newLineAtOffset(startX(font, fontSize, line1, pageWidth), startY(font, fontSize, pageHeight, marginTop));
		  	contentStream.showText(line1);
		  	contentStream.newLine();
		  	contentStream.setFont(font, 14);
		  	contentStream.showText(line2);
		  	contentStream.newLine();
		  	if(tipo == 1) contentStream.showText(line3); else contentStream.showText(_line3);
	      contentStream.endText();

	      contentStream.drawImage(imgChartAnualAcu, (float) (pageWidth*0.05), (float) (pageHeight*0.2), (float) (imgChartAnualAcu.getWidth()*0.85), (float) (imgChartAnualAcu.getHeight()*0.85));
	      
	      contentStream.beginText();
		      contentStream.setNonStrokingColor(Color.DARK_GRAY);
		      contentStream.setFont(font, 10);
		      contentStream.newLineAtOffset(startX(font, 10, fonte, pageWidth), 30);
		      contentStream.showText(fonte);
	      contentStream.endText();
	      
	      contentStream.beginText();
		      contentStream.setNonStrokingColor(Color.DARK_GRAY);
		      contentStream.setFont(font, 7);
		      contentStream.newLineAtOffset(startX(font, 7, data, pageWidth), 20);
		      contentStream.showText(data);
	      contentStream.endText();
	      
	      contentStream.close();
	      document.addPage(page4);
	      
	      if (tipo == 1) {
	    	  page5 = new PDPage(new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth()));
	    	  page6 = new PDPage(new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth()));
	      
	    	  imgChartOrgao = LosslessFactory.createFromImage(document, criarImagem(chartOrgao));
	    	  imgChartEntidade = LosslessFactory.createFromImage(document, criarImagem(chartEntidade));
	    	  
	      // Página 05
	      contentStream = new PDPageContentStream(document, page5);

	      contentStream.setNonStrokingColor(color);
	      contentStream.addRect(170, 500, 700, 70);
	      contentStream.fill();
	      
	      contentStream.beginText();
	      	contentStream.setNonStrokingColor(Color.WHITE);
	      	contentStream.setLeading(15f);
		  	contentStream.setFont(font, fontSize);
		  	contentStream.newLineAtOffset(startX(font, fontSize, line1, pageWidth), startY(font, fontSize, pageHeight, marginTop));
		  	contentStream.showText(line1);
		  	contentStream.newLine();
		  	contentStream.setFont(font, 14);
		  	contentStream.showText(line2);
		  	contentStream.newLine();
		  	if(tipo == 1) contentStream.showText(line3); else contentStream.showText(_line3);
	      contentStream.endText();

	      contentStream.drawImage(imgChartOrgao, (float) (pageWidth*0.05), (float) (pageHeight*0.2), (float) (imgChartOrgao.getWidth()*0.85), (float) (imgChartOrgao.getHeight()*0.85));
	      
	      contentStream.beginText();
		      contentStream.setNonStrokingColor(Color.DARK_GRAY);
		      contentStream.setFont(font, 10);
		      contentStream.newLineAtOffset(startX(font, 10, fonte, pageWidth), 30);
		      contentStream.showText(fonte);
	      contentStream.endText();

	      contentStream.beginText();
		      contentStream.setNonStrokingColor(Color.DARK_GRAY);
		      contentStream.setFont(font, 7);
		      contentStream.newLineAtOffset(startX(font, 7, data, pageWidth), 20);
		      contentStream.showText(data);
	      contentStream.endText();
	      
	      contentStream.close();
	      document.addPage(page5);
	      
	      // Página 06
	      contentStream = new PDPageContentStream(document, page6);

	      contentStream.setNonStrokingColor(color);
	      contentStream.addRect(170, 500, 700, 70);
	      contentStream.fill();
	      
	      contentStream.beginText();
	      	contentStream.setNonStrokingColor(Color.WHITE);
	      	contentStream.setLeading(15f);
		  	contentStream.setFont(font, fontSize);
		  	contentStream.newLineAtOffset(startX(font, fontSize, line1, pageWidth), startY(font, fontSize, pageHeight, marginTop));
		  	contentStream.showText(line1);
		  	contentStream.newLine();
		  	contentStream.setFont(font, 14);
		  	contentStream.showText(line2);
		  	contentStream.newLine();
		  	if(tipo == 1) contentStream.showText(line3); else contentStream.showText(_line3);
	      contentStream.endText();

//	      contentStream.drawImage(pdImage, (float) (pageWidth*0.05), (float) (pageHeight*0.80));
	      contentStream.drawImage(imgChartEntidade, (float) (pageWidth*0.05), (float) (pageHeight*0.2), (float) (imgChartEntidade.getWidth()*0.85), (float) (imgChartEntidade.getHeight()*0.85));
	      
	      contentStream.beginText();
		      contentStream.setNonStrokingColor(Color.DARK_GRAY);
		      contentStream.setFont(font, 10);
		      contentStream.newLineAtOffset(startX(font, 10, fonte, pageWidth), 30);
		      contentStream.showText(fonte);
	      contentStream.endText();

	      contentStream.beginText();
		      contentStream.setNonStrokingColor(Color.DARK_GRAY);
		      contentStream.setFont(font, 7);
		      contentStream.newLineAtOffset(startX(font, 7, data, pageWidth), 20);
		      contentStream.showText(data);
	      contentStream.endText();
	      
	      contentStream.close();
	      document.addPage(page6);
	      
	      }
	      
	      // Página 07
	      contentStream = new PDPageContentStream(document, page7);

	      contentStream.setNonStrokingColor(color);
	      contentStream.addRect(170, 500, 700, 70);
	      contentStream.fill();
	      
	      contentStream.beginText();
	      	contentStream.setNonStrokingColor(Color.WHITE);
	      	contentStream.setLeading(15f);
		  	contentStream.setFont(font, fontSize);
		  	contentStream.newLineAtOffset(startX(font, fontSize, line1, pageWidth), startY(font, fontSize, pageHeight, marginTop));
		  	contentStream.showText(line1);
		  	contentStream.newLine();
		  	contentStream.setFont(font, 14);
		  	contentStream.showText(line2);
		  	contentStream.newLine();
		  	if(tipo == 1) contentStream.showText(line3); else contentStream.showText(_line3);
	      contentStream.endText();

	      contentStream.drawImage(imgChartAssunto, (float) (pageWidth*0.05), (float) (pageHeight*0.2), (float) (imgChartAssunto.getWidth()*0.85), (float) (imgChartAssunto.getHeight()*0.85));
	      
	      contentStream.beginText();
		      contentStream.setNonStrokingColor(Color.DARK_GRAY);
		      contentStream.setFont(font, 10);
		      contentStream.newLineAtOffset(startX(font, 10, fonte, pageWidth), 30);
		      contentStream.showText(fonte);
	      contentStream.endText();

	      contentStream.beginText();
		      contentStream.setNonStrokingColor(Color.DARK_GRAY);
		      contentStream.setFont(font, 7);
		      contentStream.newLineAtOffset(startX(font, 7, data, pageWidth), 20);
		      contentStream.showText(data);
	      contentStream.endText();
	      
	      contentStream.close();
	      document.addPage(page7);
	      
	      // Página 08
	      contentStream = new PDPageContentStream(document, page8);

	      contentStream.setNonStrokingColor(color);
	      contentStream.addRect(170, 500, 700, 70);
	      contentStream.fill();
	      
	      contentStream.beginText();
	      	contentStream.setNonStrokingColor(Color.WHITE);
	      	contentStream.setLeading(15f);
		  	contentStream.setFont(font, fontSize);
		  	contentStream.newLineAtOffset(startX(font, fontSize, line1, pageWidth), startY(font, fontSize, pageHeight, marginTop));
		  	contentStream.showText(line1);
		  	contentStream.newLine();
		  	contentStream.setFont(font, 14);
		  	contentStream.showText(line2);
		  	contentStream.newLine();
		  	if(tipo == 1) contentStream.showText(line3); else contentStream.showText(_line3);
	      contentStream.endText();

	      contentStream.drawImage(imgChartPessoa, (float) (pageWidth*0.05), (float) (pageHeight*0.2), (float) (imgChartPessoa.getWidth()*0.85), (float) (imgChartPessoa.getHeight()*0.85));
	      
	      contentStream.beginText();
		      contentStream.setNonStrokingColor(Color.DARK_GRAY);
		      contentStream.setFont(font, 10);
		      contentStream.newLineAtOffset(startX(font, 10, fonte, pageWidth), 30);
		      contentStream.showText(fonte);
	      contentStream.endText();

	      contentStream.beginText();
		      contentStream.setNonStrokingColor(Color.DARK_GRAY);
		      contentStream.setFont(font, 7);
		      contentStream.newLineAtOffset(startX(font, 7, data, pageWidth), 20);
		      contentStream.showText(data);
	      contentStream.endText();
	      
	      contentStream.close();
	      document.addPage(page8);
	      
	      // Página 09
	      contentStream = new PDPageContentStream(document, page9);

	      contentStream.setNonStrokingColor(color);
	      contentStream.addRect(170, 500, 700, 70);
	      contentStream.fill();
	      
	      contentStream.beginText();
	      	contentStream.setNonStrokingColor(Color.WHITE);
	      	contentStream.setLeading(15f);
		  	contentStream.setFont(font, fontSize);
		  	contentStream.newLineAtOffset(startX(font, fontSize, line1, pageWidth), startY(font, fontSize, pageHeight, marginTop));
		  	contentStream.showText(line1);
		  	contentStream.newLine();
		  	contentStream.setFont(font, 14);
		  	contentStream.showText(line2);
		  	contentStream.newLine();
		  	if(tipo == 1) contentStream.showText(line3); else contentStream.showText(_line3);
	      contentStream.endText();

	      contentStream.drawImage(imgChartEstado, (float) (pageWidth*0.05), (float) (pageHeight*0.2), (float) (imgChartEstado.getWidth()*0.85), (float) (imgChartEstado.getHeight()*0.85));
	      
	      contentStream.beginText();
		      contentStream.setNonStrokingColor(Color.DARK_GRAY);
		      contentStream.setFont(font, 10);
		      contentStream.newLineAtOffset(startX(font, 10, fonte, pageWidth), 30);
		      contentStream.showText(fonte);
	      contentStream.endText();

	      contentStream.beginText();
		      contentStream.setNonStrokingColor(Color.DARK_GRAY);
		      contentStream.setFont(font, 7);
		      contentStream.newLineAtOffset(startX(font, 7, data, pageWidth), 20);
		      contentStream.showText(data);
	      contentStream.endText();
	      
	      contentStream.close();
	      document.addPage(page9);
	      
	      // Converter página Byte Array para possibilitar download
	      ByteArrayOutputStream output = new ByteArrayOutputStream();
	      document.save(output);
	      byte[] buf = output.toByteArray();
	      
	      Relatorios relatorio = new Relatorios();
	      
	      InputStream in = new ByteArrayInputStream(buf);
	      if (tipo == 1) {
	    	  file = new DefaultStreamedContent(in, "application/pdf", "Relatorio_Geral_"  + FiltrarDadosRelatorioEstatico.mesFinal + "_" + FiltrarDadosRelatorioEstatico.anoFinal + ".pdf");
	      } else {
	    	  file = new DefaultStreamedContent(in, "application/pdf", "Relatorio_" + EntidadesDAO.find(FiltrarDadosRelatorioEstatico.idEntidade).getSigla() + "_" + FiltrarDadosRelatorioEstatico.mesFinal + "_" + FiltrarDadosRelatorioEstatico.anoFinal + ".pdf");
	      }
	      
	      document.close();
	      
		} catch (Exception e) {
			e.printStackTrace();
			document.close();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Erro ao gerar relatório"));
		}
	}
	
	private String getMesFinal() {
		String[] meses = { "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro" };
		
		return meses[FiltrarDadosRelatorioEstatico.mesFinal-1];
	}
	
	private String getDataAtual() {
		String data;
		Calendar c = Calendar.getInstance();
		int diaAtual = c.get(Calendar.DAY_OF_MONTH);
		int mesAtual = c.get(Calendar.MONTH);
		int anoAtual = c.get(Calendar.YEAR);
		int horaAtual = c.get(Calendar.HOUR_OF_DAY);
		int minAtual = c.get(Calendar.MINUTE);
		
		data = diaAtual + "/" + mesAtual + "/" + anoAtual + " às " + horaAtual + ":" + minAtual;
		
 		return data;
	}
	
	private float startX(PDFont font, int fontSize, String line, float pageWidth) throws IOException {
		float titleWidth = font.getStringWidth(line) / 1000 * fontSize;
		
        float startX = (pageWidth - titleWidth) / 2;
        
		return startX;
	}
	
	private float startY(PDFont font, int fontSize, float pageHeight, float marginTop) throws IOException {
        float titleHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * fontSize;
		
		float startY = pageHeight - marginTop - titleHeight;
		
		return startY;
	}
	
	public BufferedImage criarImagem(String imagem) throws IOException {
		if (imagem != null) {
			String encoded = imagem.split(",")[1];
			byte[] decoded = org.apache.commons.codec.binary.Base64.decodeBase64(encoded);
			ByteArrayInputStream bais = new ByteArrayInputStream(decoded);
			BufferedImage bim = ImageIO.read(bais);
			return bim;
		}else {
			return null;
		}
		
		
	}
	
	
    public StreamedContent getFile() {
    	return file;
    }
	
	public String getBase64Str() {
		return base64Str;
	}

	public void setBase64Str(String base64Str) {
		this.base64Str = base64Str;
	}


	public String getChartGeral() {
		return chartGeral;
	}


	public void setChartGeral(String chartGeral) {
		this.chartGeral = chartGeral;
	}


	public String getChartMensal() {
		return chartMensal;
	}


	public void setChartMensal(String chartMensal) {
		this.chartMensal = chartMensal;
	}


	public String getChartAnual() {
		return chartAnual;
	}


	public void setChartAnual(String chartAnual) {
		this.chartAnual = chartAnual;
	}


	public String getChartOrgao() {
		return chartOrgao;
	}


	public void setChartOrgao(String chartOrgao) {
		this.chartOrgao = chartOrgao;
	}


	public String getChartAnualAcu() {
		return chartAnualAcu;
	}


	public void setChartAnualAcu(String chartAnualAcu) {
		this.chartAnualAcu = chartAnualAcu;
	}


	public String getChartEntidade() {
		return chartEntidade;
	}


	public void setChartEntidade(String chartEntidade) {
		this.chartEntidade = chartEntidade;
	}


	public String getChartAssunto() {
		return chartAssunto;
	}


	public void setChartAssunto(String chartAssunto) {
		this.chartAssunto = chartAssunto;
	}


	public String getChartPessoa() {
		return chartPessoa;
	}


	public void setChartPessoa(String chartPessoa) {
		this.chartPessoa = chartPessoa;
	}


	public String getChartEstado() {
		return chartEstado;
	}


	public void setChartEstado(String chartEstado) {
		this.chartEstado = chartEstado;
	}
	

	
}
