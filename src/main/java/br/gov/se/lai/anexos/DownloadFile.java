package br.gov.se.lai.anexos;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.sun.faces.application.resource.Resource;

import java.io.IOException;
import java.sql.PreparedStatement;

public class DownloadFile {
	
	private Connection myConn = null;
	private PreparedStatement myStmt = null;
	private ResultSet myRs = null;
	
	private InputStream input = null;
	private FileOutputStream output = null;
	
	private StreamedContent file;

	private static final int BUFFER_SIZE = 4096;
	
	ConnectionFile connection = new ConnectionFile();
	
	String url = connection.url;
	String user = connection.user;
	String password = connection.password;
	
	
	public StreamedContent download(Integer idMensagem) throws Exception {
		
		Class.forName("org.gjt.mm.mysql.Driver");
 
        try {
            Connection conn = DriverManager.getConnection(url, user, password);
 
            String sql = "SELECT * FROM anexo WHERE Mensagem_idMensagem = ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setInt(1, idMensagem);
 
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                Blob blob = result.getBlob("conteudo");
                String tipo = result.getString("tipo");
                String nome = result.getString("nome");
                
                InputStream inputStream = blob.getBinaryStream();
                
                file = new DefaultStreamedContent(inputStream, tipo, nome);
                
                inputStream.close();
            }
            conn.close();
            
            return file;
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        return file;
        
    }
	
	public StreamedContent getFile() {
        return file;
    }
	
	public boolean temAnexo(int idMensagem) {
		
//		idMensagem = 241;
		
        try {
        	
        	Class.forName("org.gjt.mm.mysql.Driver");
        	
            Connection conn = DriverManager.getConnection(url, user, password);
 
            String sql = "SELECT conteudo FROM anexo WHERE Mensagem_idMensagem = ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setInt(1, idMensagem);
            
            ResultSet result = statement.executeQuery();
            
            if (result.next()) {
            	return true;
            } else return false;
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
	

}