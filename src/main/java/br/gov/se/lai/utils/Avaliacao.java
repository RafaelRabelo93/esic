package br.gov.se.lai.utils;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import br.gov.se.lai.Bean.MensagemBean;
import br.gov.se.lai.DAO.EntidadesDAO;
import br.gov.se.lai.DAO.MensagemDAO;
import br.gov.se.lai.DAO.SolicitacaoDAO;
import br.gov.se.lai.entity.Mensagem;
import br.gov.se.lai.entity.Solicitacao;
import br.gov.se.lai.entity.Entidades;



@ManagedBean(name = "avaliacao")
@SessionScoped
public class Avaliacao {

	public int nota;
	public static double media;
	private Solicitacao solicitacao;
	public int idEntidade;
	public List<Solicitacao> solicitacoesAvaliadas;
	
	public static void avaliarSolicitacao(Solicitacao solicitacao) {
		ArrayList<Mensagem> listMensagensAvaliadas = (ArrayList<Mensagem>) MensagemDAO.listMensagensTipo(solicitacao.getIdSolicitacao(), (short)6);
		int nota = 0;
		int qntdAvaliacoes = 0;
		for(Mensagem m : listMensagensAvaliadas) {
			if(m.getNota()!=0) {
				nota += m.getNota();
				qntdAvaliacoes++;
			}
		}
		
		solicitacao.setAvaliacao((int) (nota/(qntdAvaliacoes == 0 ? 1 : qntdAvaliacoes)));
		SolicitacaoDAO.saveOrUpdate(solicitacao);
	}
	

	/**
	 * Retorna lista de solicita��es avaliadas de determinada entidade
	 * 
	 * @param idEntidade
	 * @return
	 */
	public List<Solicitacao> listSolicitacoesAvaliadas(int idEntidade) {
		solicitacoesAvaliadas = SolicitacaoDAO.listarAvaliadas(idEntidade);
		return solicitacoesAvaliadas;
	}
	
	public double calcularAvaliacao(List<Solicitacao> solicitacoesAvaliada) {
		media = 0;
		int qntdGeral = 0;
		for(Solicitacao solicitacao : solicitacoesAvaliada) {
			if(solicitacao.getAvaliacao()!=0) {
				media += solicitacao.getAvaliacao();
				qntdGeral++;
			}
		}
		
		media = (media/(qntdGeral == 0 ? 1 : qntdGeral));
		return media;
	}
	
    public String redirecionarAvaliacao() {
    	calcularAvaliacao(listSolicitacoesAvaliadas(idEntidade));
        return "/Consulta/consulta_avaliacao.xhtml";
    }



	public int getNota() {
		return this.nota ;
	}

	public void setNota(int nota) {
		this.nota = nota;
	}

	public Solicitacao getSolicitacao() {
		return solicitacao;
	}

	public void setSolicitacao(Solicitacao solicitacao) {
		this.solicitacao = solicitacao;
	}

	public double getMedia() {
		return media;
	}

	public void setMedia(double media) {
		Avaliacao.media = media;
	}


	public int getIdEntidade() {
		return idEntidade;
	}


	public void setIdEntidade(int idEntidade) {
		this.idEntidade = idEntidade;
	}


	public List<Solicitacao> getSolicitacoesAvaliadas() {
		return solicitacoesAvaliadas;
	}


	public void setSolicitacoesAvaliadas(List<Solicitacao> solicitacoesAvaliadas) {
		this.solicitacoesAvaliadas = solicitacoesAvaliadas;
	}
	
	
	
	
}
