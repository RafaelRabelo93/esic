package br.gov.se.lai.utils;

public final class DadosAutenticacao {

	private final static String hostNameEmail = "smtp.expresso.se.gov.br";
	private final static String emailFrom = "no_reply@cge.se.gov.br";
	private final static String userLoginEmailAuthentication = "mayara.machado";
	private final static String senhaUserLoginEmailAuthentication = "abcd1234";
	
	static String getHostNameEmail() {
		return hostNameEmail;
	}
	
	static String getEmailFrom() {
		return emailFrom;
	}
	
	static String getUserLoginEmailAuthentication() {
		return userLoginEmailAuthentication;
	}
	
	static String getSenhaUserLoginEmailAuthentication() {
		return senhaUserLoginEmailAuthentication;
	}
	
	
}
