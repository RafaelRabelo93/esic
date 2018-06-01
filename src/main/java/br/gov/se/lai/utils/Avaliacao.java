package br.gov.se.lai.utils;

import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import br.gov.se.lai.Bean.MensagemBean;
import br.gov.se.lai.DAO.MensagemDAO;
import br.gov.se.lai.DAO.SolicitacaoDAO;
import br.gov.se.lai.entity.Mensagem;
import br.gov.se.lai.entity.Solicitacao;


@ManagedBean(name = "avaliacao")
@SessionScoped
public class Avaliacao {

	private int nota;
	private Solicitacao solicitacao;
	
	public void avaliarSolicitacao(Solicitacao solicitacao, ArrayList<Mensagem> listMensagens) {
		int nota = 0;
		int qntdAvaliacoes = 0;
		for(Mensagem m : listMensagens) {
			if(m.getNota()!=0) {
				nota += m.getNota();
				qntdAvaliacoes++;
			}
		}
		
		solicitacao.setAvaliacao((int) (nota/qntdAvaliacoes));
		SolicitacaoDAO.saveOrUpdate(solicitacao);
	}
	
	public void avaliarUltimaMensagem() {
		ArrayList<Mensagem> listMensagens = (ArrayList<Mensagem>) MensagemDAO.listMensagensTipo(solicitacao.getIdSolicitacao(), (short)2);
		setarNotaResposta( listMensagens.get(listMensagens.size()-1));
		avaliarSolicitacao(solicitacao, listMensagens);
	}
	
	public int setarNotaResposta(Mensagem mensagem) {
		mensagem.setNota(nota);
		MensagemDAO.saveOrUpdate(mensagem);
		return nota;
	}

	public int getNota() {
		return nota;
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
	
	
	
	
}
