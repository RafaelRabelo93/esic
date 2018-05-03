package relatorio;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

@ManagedBean(name = "chartExport")
@SessionScoped
public class ChartExport {
	
	private String base64Str;
	private StreamedContent file;
	
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
	
    
//    public FileDownloadView() {        
//        InputStream stream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/resources/demo/images/optimus.jpg");
//        file = new DefaultStreamedContent(stream, "image/jpg", "downloaded_optimus.jpg");
//    }
 
	
	
    public StreamedContent getFile() {
    	return file;
    }
	
	public String getBase64Str() {
		return base64Str;
	}

	public void setBase64Str(String base64Str) {
		this.base64Str = base64Str;
	}
	
}
