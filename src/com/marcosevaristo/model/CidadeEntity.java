package com.marcosevaristo.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;

@Entity
@Table(name = "TB_CIDADE")
@NamedQueries({
    @NamedQuery(name="queryRecuperaCapitaisOrdenadasPorNome",
                query=" select obj from CidadeEntity obj "
                	+ " where obj.capital = true "
                	+ " order by obj.name desc "),
    @NamedQuery(name="queryRecuperaCidadePorFiltroInformado",
    query=" select obj from CidadeEntity obj "
    		+ " where obj.:campo like :valor "),
}) 
public class CidadeEntity implements Serializable{

	private static final long serialVersionUID = -8127409820527077737L;

	@Id
	@Column(name = "CID_IBGEID")
	private Long ibge_id;
	
	@Column(name = "CID_UF", nullable = false)
	private String uf;
	
	@Column(name = "CID_NOME", nullable = false)
	private String name;
	
	@Column(name = "CID_EHCAPITAL")
	private boolean capital;
	
	@Column(name = "CID_LONGITUDE")
	private String lon;
	
	@Column(name = "CID_LATITUDE")
	private String lat;
	
	@Column(name = "CID_NOMSEMAC")
	private String no_accents;
	
	@Column(name = "CID_NOMALT")
	private String alternative_names;
	
	@Column(name = "CID_MICROREGID")
	private String microregion;
	
	@Column(name = "CID_MESOREGIAO")
	private String mesoregion;

	public Long getIbge_id() {
		return ibge_id;
	}

	public void setIbge_id(Long ibge_id) {
		this.ibge_id = ibge_id;
	}

	public String getUf() {
		return uf;
	}

	public void setUf(String uf) {
		this.uf = uf;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isCapital() {
		return capital;
	}

	public void setCapital(boolean capital) {
		this.capital = capital;
	}

	public String getLon() {
		return lon;
	}

	public void setLon(String lon) {
		this.lon = lon;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getNo_accents() {
		return no_accents;
	}

	public void setNo_accents(String no_accents) {
		this.no_accents = no_accents;
	}

	public String getAlternative_names() {
		return alternative_names;
	}

	public void setAlternative_names(String alternative_names) {
		this.alternative_names = alternative_names;
	}

	public String getMicroregion() {
		return microregion;
	}

	public void setMicroregion(String microregion) {
		this.microregion = microregion;
	}

	public String getMesoregion() {
		return mesoregion;
	}

	public void setMesoregion(String mesoregion) {
		this.mesoregion = mesoregion;
	}
}
