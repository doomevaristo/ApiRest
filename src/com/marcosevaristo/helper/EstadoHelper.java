package com.marcosevaristo.helper;

import java.util.HashMap;
import java.util.Map;

import com.marcosevaristo.util.StringUtils;

public class EstadoHelper {

	private static Map<String, String> siglasNomes = montaSiglasNomes();
	
	private static Map<String, String> montaSiglasNomes() {
		if(siglasNomes == null) {
			siglasNomes = new HashMap<>();
			
			siglasNomes.put("AC", "Acre");
			siglasNomes.put("AL", "Alagoas");
			siglasNomes.put("AP", "Amapá");
			siglasNomes.put("AM", "Amazonas");
			siglasNomes.put("BA", "Bahia");
			siglasNomes.put("CE", "Ceará");
			siglasNomes.put("DF", "Distrito Federal");
			siglasNomes.put("ES", "Espírito Santo");
			siglasNomes.put("GO", "Goiás");
			siglasNomes.put("MA", "Maranhão");
			siglasNomes.put("MT", "Mato Grosso");
			siglasNomes.put("MS", "Mato Grosso do Sul");
			siglasNomes.put("MG", "Minas Gerais");
			siglasNomes.put("PA", "Pará");
			siglasNomes.put("PB", "Paraíba");
			siglasNomes.put("PR", "Paraná");
			siglasNomes.put("PE", "Pernambuco");
			siglasNomes.put("PI", "Piauí");
			siglasNomes.put("RN", "Rio Grande do Norte");
			siglasNomes.put("RS", "Rio Grande do Sul");
			siglasNomes.put("RJ", "Rio de Janeiro");
			siglasNomes.put("RO", "Rondônia");
			siglasNomes.put("RR", "Roraima");
			siglasNomes.put("SC", "Santa Catarina");
			siglasNomes.put("SP", "São Paulo");
			siglasNomes.put("SE", "Sergipe");
			siglasNomes.put("TO", "Tocantins");
		}
		return siglasNomes;
	}
	
	public static Map<String, String> getSiglasNomes() {
		return siglasNomes;
	}
	
	public static String getSiglaPorNome(String nome) {
		if(StringUtils.isNotBlank(nome)) {
			for(String umaSigla : siglasNomes.keySet()) {
				if(siglasNomes.get(umaSigla).equals(nome)) {
					return umaSigla;
				}
			}
		}
		return null;
	}
}
