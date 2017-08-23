package com.marcosevaristo.persistence;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import com.marcosevaristo.helper.EstadoHelper;
import com.marcosevaristo.model.CidadeEntity;
import com.marcosevaristo.model.dto.EstadoDTO;
import com.marcosevaristo.util.CollectionUtils;
import com.marcosevaristo.util.ConnectionMarcos;
import com.marcosevaristo.util.StringUtils;

public class CidadeDAO extends ConnectionMarcos{
	
	private static CidadeDAO instance;
	
	public static CidadeDAO getInstance() {
		if(instance == null) {
			instance = new CidadeDAO();
		}
		return instance;
	} 

	public void populaBaseCidades(List<CidadeEntity> lCidades) {
		if(CollectionUtils.isNotEmpty(lCidades)) {
			beginTransaction();
			for(CidadeEntity umaCidade : lCidades) {
				persist(umaCidade);
			}
			close();
		}
	}
	
	public void insereCidade(CidadeEntity cidade) {
		if(cidade != null) {
			beginTransaction();
			persist(cidade);
			close();
		}
	}
	
	public void deletaCidade(CidadeEntity cidade) {
		
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<CidadeEntity> recuperaCapitaisOrdenadasPorNome() {
		Query query = getEntityManager().createNamedQuery("queryRecuperaCapitaisOrdenadasPorNome", CidadeEntity.class);
		List lResultados = query.getResultList();
		if(CollectionUtils.isNotEmpty(lResultados)) {
			return (List<CidadeEntity>) lResultados;
		}
		return null;
	}
	
	public List<EstadoDTO> recuperaEstadosMenorEMaiorNroCidades() {
		List<EstadoDTO> lEstados = new ArrayList<>();
		String siglaEstadoMaiorNroCidades;
		String siglaEstadoMenorNroCidades;
		
		Query query = getEntityManager().createNamedQuery("queryRecuperaEstadoMaiorNroCidades", CidadeEntity.class);
		siglaEstadoMaiorNroCidades = (String) query.getSingleResult();
		
		query = getEntityManager().createNamedQuery("queryRecuperaEstadoMenorNroCidades", CidadeEntity.class);
		siglaEstadoMenorNroCidades = (String) query.getSingleResult();
		
		EstadoDTO estadoDTO = new EstadoDTO();
		estadoDTO.setSigla(siglaEstadoMenorNroCidades);
		estadoDTO.setNome(EstadoHelper.getSiglasNomes().get(siglaEstadoMenorNroCidades));
		lEstados.add(estadoDTO);
		
		estadoDTO = new EstadoDTO();
		estadoDTO.setSigla(siglaEstadoMaiorNroCidades);
		estadoDTO.setNome(EstadoHelper.getSiglasNomes().get(siglaEstadoMaiorNroCidades));
		lEstados.add(estadoDTO);
		
		return lEstados;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<EstadoDTO> recuperaQtdCidadesPorEstado() {
		List<EstadoDTO> lEstados = new ArrayList<>();
		Query query = getEntityManager().createNamedQuery("queryRecuperaQtdCidadesPorEstado", CidadeEntity.class);
		List lRetornos = query.getResultList();
		
		if(CollectionUtils.isNotEmpty(lRetornos)) {
			List<Object[]> lRetornosObj = (List<Object[]>) lRetornos;
			for(Object[] umObjArr : lRetornosObj) {
				Integer qtdCidades = (Integer) umObjArr[0];
				String sigla = (String) umObjArr[1];
				lEstados.add(new EstadoDTO(sigla, qtdCidades));
			}
		}
		
		return lEstados;
	}
	
	public CidadeEntity recuperaCidadePorID(Long id) {
		Query query = getEntityManager().createNamedQuery("queryRecuperaCidadePorID", CidadeEntity.class);
		query.setParameter("id", id);
		
		Object obj = query.getSingleResult();
		if(obj != null) {
			return (CidadeEntity) obj;
		} else {
			return null;
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<String> recuperaNomeCidadesPorEstado(String estadoStr) {
		Query query = getEntityManager().createNamedQuery("queryRecuperaNomeCidadesPorEstado", CidadeEntity.class);
		
		if(StringUtils.isNotBlank(estadoStr)) {
			if(estadoStr.length() == 2) { //Sigla
				query.setParameter("uf", estadoStr);
			} else { //Nome
				query.setParameter("uf", EstadoHelper.getSiglaPorNome(estadoStr));
			}
		}
		
		List lObj = query.getResultList();
		if(CollectionUtils.isNotEmpty(lObj)) {
			return (List<String>) lObj;
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<CidadeEntity> recuperaCidadesPorFiltroInformado(String campoStr, String valorStr) {
		if(isCampoDaEntidade(campoStr, CidadeEntity.class)) {
			List lResultados = null;
			Query query = getEntityManager().createNamedQuery("queryRecuperaCidadePorFiltroInformado", CidadeEntity.class);
			
			query.setParameter("campo", campoStr);
			query.setParameter("valor", "%"+valorStr+"%");
			
			lResultados = query.getResultList();
			
			if(CollectionUtils.isNotEmpty(lResultados)) {
				return (List<CidadeEntity>) lResultados;
			}
		}
		return null;
	}
	
	private boolean isCampoDaEntidade(String campoStr, Class<?> clazz) {
		Field[] fieldsArr = clazz.getDeclaredFields();
		for(Field umField : fieldsArr) {
			if(umField.getName().equals(campoStr)) {
				return true;
			}
		}
		return false;
	}
}
