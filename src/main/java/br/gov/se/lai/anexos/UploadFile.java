package br.gov.se.lai.anexos;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;

import org.primefaces.model.UploadedFile;

public class UploadFile {
	
	public UploadedFile file;
	
	private Connection myConn = null; 
	private PreparedStatement myStmt = null;
	
	FileInputStream input = null;
	
	ConnectionFile connection = new ConnectionFile();
	
	String url = connection.url;
	String user = connection.user;
	String password = connection.password;
	
	public void upload(UploadedFile file, Integer idMensagem) throws IOException, SQLException {
		try {
			
			Class.forName("org.gjt.mm.mysql.Driver");
			myConn = DriverManager.getConnection(url, user, password);
			
			String sql = "INSERT INTO anexo (nome, Mensagem_idMensagem, tipo, conteudo, tamanho) VALUES(?, ?, ?, ?, ?)";
			myStmt = myConn.prepareStatement(sql);
					
			InputStream input = file.getInputstream();
			myStmt.setString(1, file.getFileName());
			myStmt.setInt(2, idMensagem);
			myStmt.setString(3, file.getContentType());
			myStmt.setBinaryStream(4, input);
			myStmt.setLong(5, file.getSize());
			
			System.out.println("\nSalvando arquivo \"" + file.getFileName() + "\" no banco... ");
			
			if (file.getContentType().equals("image/png")) {
				System.out.println(file.getContentType());
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Anexo enviado com sucesso",""));
			}
			
			myStmt.executeUpdate();
			
			System.out.println("\nConcluído com sucesso!");
			
		} catch (Exception exc) {
			exc.printStackTrace();
		} finally {			
			if (input != null) {
				input.close();
			}
			
			close(myConn, myStmt);			
		}
	}

	private static void close(Connection myConn, Statement myStmt)
			throws SQLException {

		if (myStmt != null) {
			myStmt.close();
		}
		
		if (myConn != null) {
			myConn.close();
		}
	}

	public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}
}
