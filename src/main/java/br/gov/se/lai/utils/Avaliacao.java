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
	public final int size = 173;
	public String sizeFinal;
	
	/**
	 * Faz a média das mensagens vinculadas a uma solicitação e atualiza a avaliação da solicitação em questão. 
	 * @param solicitacao
	 */
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
		
		solicitacao.setAvaliacao((nota/(qntdAvaliacoes == 0 ? 1 : qntdAvaliacoes)));
		SolicitacaoDAO.saveOrUpdate(solicitacao);
	}
	

	/**
	 * Retorna lista de solicitações avaliadas de determinada entidade
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
    	calcSize();
        return "/Consulta/consulta_avaliacao.xhtml";
    }

    /**
	 * Gera a representação gráfica da média
	 * 
	 * @param idEntidade
     * @return 
	 * @return
	 */
    public String calcSize() {
    	float calc = (float) ((media * 100)/5);
    	int size2 = (int) (size * (calc/100));
    	sizeFinal = size2 + "px";
    	return sizeFinal;
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
	
	public int getSize() {
		return size;
	}
	
	public String getSizeFinal() {
		return sizeFinal;
	}
	
	public void setSizeFinal(String sizeFinal) {
		this.sizeFinal = sizeFinal;
	}
	
}
