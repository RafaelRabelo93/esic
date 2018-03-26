package br.gov.se.lai.utils;

final class  DadosAutenticacao {
	
	private final static String hostNameEmail = "smtp.expresso.se.gov.br";
	private final static String userLoginEmailAuthentication = "mayara.machado";
	private final static String senhaUserLoginEmailAuthentication = "abcd1234";
	private final static String emailFrom = "no_reply@expresso.se.gov.br";
	private final static String endereco = "http://esic.se.gov.br";
	
	static String getHostNameEmail() {
		return hostNameEmail;
	}
	static String getUserLoginEmailAuthentication() {
		return userLoginEmailAuthentication;
	}
	static String getSenhaUserLoginEmailAuthentication() {
		return senhaUserLoginEmailAuthentication;
	}
	static String getEmailFrom() {
		return emailFrom;
	}
	static String getEndereco() {
		return endereco;
	}
	
	
}
