package br.gov.se.lai.Bean;

import java.io.Serializable;


import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import br.gov.se.lai.DAO.AnexoDAO;
import br.gov.se.lai.entity.Anexo;





@ManagedBean(name = "anexo")
@SessionScoped
public class AnexoBean implements Serializable{

	
	private static final long serialVersionUID = 106779207846313149L;
	private Anexo anexo;
	


	@PostConstruct
	public void init() {
			anexo = new Anexo();
	}

	public void save() {
		anexo.setNome("anexo");
		anexo.setFile("anexo.txt");
		AnexoDAO.saveOrUpdate(anexo);
	}
	
	public String delete() {

		return "usuario";
	}
	
	public String edit() {

		return "calma";
	}


//GETTERS E SETTERS ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	
	
	public Anexo getAnexo() {
		return anexo;
	}

	public void setAnexo(Anexo anexo) {
		this.anexo = anexo;
	}

}