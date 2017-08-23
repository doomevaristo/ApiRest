package com.marcosevaristo.business;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.marcosevaristo.model.CidadeEntity;
import com.marcosevaristo.persistence.CidadeDAO;

public class CidadeAS {

	public Map<Double, CidadeEntity[]> recuperaDuasCidadesMaisDistantes() {
		Map<Double, CidadeEntity[]> cidadesMaisDistantes = new HashMap<>();
		List<CidadeEntity> lTodasCidades = CidadeDAO.getInstance().recuperaCidadePorID(null);
		
		double distancia = 0.0;
		double maiorDistancia = 0.0;
		CidadeEntity[] cidadesArr = new CidadeEntity[2];
		for(CidadeEntity umaCidade : lTodasCidades) {
			for(CidadeEntity outraCidade : lTodasCidades) {
				if(!umaCidade.getIbge_id().equals(outraCidade)) {
					distancia = medeDistanciaEntreDuasCidades(umaCidade, outraCidade);
					if(distancia > maiorDistancia) {
						maiorDistancia = distancia;
						cidadesArr[0] = umaCidade;
						cidadesArr[1] = outraCidade;
					}
				}
			}
		}
		
		cidadesMaisDistantes.put(maiorDistancia, cidadesArr);
		return cidadesMaisDistantes;
	}
	
	private double medeDistanciaEntreDuasCidades(CidadeEntity cidade1, CidadeEntity cidade2) {
	    int raioDaTerra = 6371;

	    double distanciaLatitude = Math.toRadians(Double.valueOf(cidade2.getLat()) - Double.valueOf(cidade1.getLat()));
	    double distanciaLongitude = Math.toRadians(Double.valueOf(cidade2.getLon()) - Double.valueOf(cidade1.getLon()));
	    double calculo = Math.sin(distanciaLatitude/2) * Math.sin(distanciaLatitude/2) 
	    		+ Math.cos(Math.toRadians(Double.valueOf(cidade1.getLat()))) * Math.cos(Math.toRadians(Double.valueOf(cidade2.getLat())))
	            * Math.sin(distanciaLongitude/2) * Math.sin(distanciaLongitude/2);
	    calculo = 2 * Math.atan2(Math.sqrt(calculo), Math.sqrt(1 - calculo));
	    double distanciaEmMetros = raioDaTerra * calculo * 1000;

	    distanciaEmMetros = Math.pow(distanciaEmMetros, 2);

	    return Math.sqrt(distanciaEmMetros);
	}
	
}
