package com.marcosevaristo.model.dto;

import com.marcosevaristo.helper.EstadoHelper;

public class EstadoDTO {
	
	private String sigla;
	private Long qtdCidades;
	private String nome;
	
	public EstadoDTO(){}
	
	public EstadoDTO(String sigla, Long qtdCidades) {
		this.sigla = sigla;
		this.qtdCidades = qtdCidades;
		this.nome = EstadoHelper.getSiglasNomes().get(sigla);
	}
	
	public String getSigla() {
		return sigla;
	}
	public String getNome() {
		return nome;
	}
	public void setSigla(String sigla) {
		this.sigla = sigla;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}

	public Long getQtdCidades() {
		return qtdCidades;
	}

	public void setQtdCidades(Long qtdCidades) {
		this.qtdCidades = qtdCidades;
	}
}
