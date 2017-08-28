package com.marcosevaristo.persistence;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityTransaction;
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
			EntityTransaction tran = getEntityManager().getTransaction();
			for(CidadeEntity umaCidade : lCidades) {
				tran.begin();
				persist(umaCidade);
				tran.commit();
			}
		}
	}
	
	public void insereCidade(CidadeEntity cidade) {
		if(cidade != null) {
			EntityTransaction tran = getEntityManager().getTransaction();
			tran.begin();
			persist(cidade);
			tran.commit();
		}
	}
	
	public void deletaCidade(Long cidadeID) {
		List<CidadeEntity> lCidades = recuperaCidadePorID(cidadeID);
		if(CollectionUtils.isNotEmpty(lCidades)) {
			CidadeEntity cidadeExcluir = lCidades.get(0);
			if(cidadeExcluir != null) {
				EntityTransaction tran = getEntityManager().getTransaction();
				tran.begin();
				delete(cidadeExcluir);
				tran.commit();
			}
		}
		
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
		
		StringBuilder sbSql = new StringBuilder();
		sbSql.append(" select count(obj.ibge_id), obj.uf from CidadeEntity obj ");
		sbSql.append(" group by obj.uf ");
		
		Query query = getEntityManager().createQuery(sbSql.toString());
		List lRetornos = query.getResultList();
		
		if(CollectionUtils.isNotEmpty(lRetornos)) {
			List<Object[]> lRetornosObj = (List<Object[]>) lRetornos;
			for(Object[] umObjArr : lRetornosObj) {
				Long qtdCidades = (Long) umObjArr[0];
				String sigla = (String) umObjArr[1];
				lEstados.add(new EstadoDTO(sigla, qtdCidades));
			}
		}
		
		return lEstados;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<CidadeEntity> recuperaCidadePorID(Long id) {
		StringBuilder hql = new StringBuilder();
		hql.append(" select obj from CidadeEntity obj ");
		if(id != null) {
			hql.append(" where obj.id = :id ");
		}
		
		Query query = getEntityManager().createQuery(hql.toString());
		
		if(id != null) {
			query.setParameter("id", id);
		}
		
		List lResultados = query.getResultList();
		if(CollectionUtils.isNotEmpty(lResultados)) {
			return (List<CidadeEntity>) lResultados;
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
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
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<String> recuperaDadosDaColuna(String campoStr) {
		if(isCampoDaEntidade(campoStr, CidadeEntity.class)) {
			List lResultados = null;
			StringBuilder sbSql = new StringBuilder();
			sbSql.append("select distinct obj.");
			sbSql.append(":coluna");
			sbSql.append(" from CidadeEntity obj ");
			
			Query query = createQuery(sbSql.toString());
			query.setParameter("coluna", campoStr);
			
			lResultados = query.getResultList();
			
			if(CollectionUtils.isNotEmpty(lResultados)) {
				return (List<String>) lResultados;
			}
		}
		return null;
	}
	
	public int recuperaQtdRegistrosTotais() {
		Query query = getEntityManager().createNamedQuery("queryRecuperaQtdRegistrosTotais", CidadeEntity.class);
		return (int) query.getSingleResult();
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
