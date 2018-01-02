package br.gov.se.lai.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Criptografia {
	
	public static String Criptografar(String senha) {	    
		StringBuilder hexStringSenhaAdmin = null;
		try {
			MessageDigest algorithm = MessageDigest.getInstance("SHA-256");
		    byte messageDigestSenhaAdmin[] = algorithm.digest(senha.getBytes("UTF-8"));
		     
		    hexStringSenhaAdmin = new StringBuilder();
		    for (byte b : messageDigestSenhaAdmin) {
		             hexStringSenhaAdmin.append(String.format("%02X", 0xFF & b));
		    }		    
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return hexStringSenhaAdmin.toString();
	}    
    
	
    
   
	public static Boolean Comparar(String senhaDigitada, String senhaCadastrada) {
		if (senhaDigitada.equals(senhaCadastrada)) {
			return true;
		} else {
			return false;
		}
    }
                       
    

}
