package com.marcosevaristo.util;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import net.sf.ehcache.CacheManager;


public class ConnectionMarcos {
	
	private final String DATABASE_NAME = "CIDADES";
	private EntityManagerFactory entityManagerFactory;
	private EntityManager entityManager;

	protected EntityManager getEntityManager() {
		if(entityManager == null) {
			entityManagerFactory = Persistence.createEntityManagerFactory(DATABASE_NAME);
			entityManager = entityManagerFactory.createEntityManager();
			CacheManager.create();
		}
		return entityManager;
	}
	
	protected void close() {
		if(entityManager.isOpen()) {
			entityManager.close();
		}
		if(entityManagerFactory.isOpen()) {
			entityManagerFactory.close();
		}
	}
	
	protected void getTransaction() {
		if(entityManager != null) {
		} else {
			entityManager = getEntityManager();
		}
		entityManager.getTransaction();
	}
	
	protected void persist(Object objeto) {
		if(entityManager.isOpen() && entityManager.getTransaction().isActive()) {
			entityManager.persist(objeto);
		} else {
			System.out.println(MensagemUtils.ENTITY_MANAGER_FECHADO_OU_TRANSACAO_INATIVA);
		}
	}
	
	protected void delete(Object objeto) {
		if(entityManager.isOpen() && entityManager.getTransaction().isActive()) {
			entityManager.remove(objeto);
		} else {
			System.out.println(MensagemUtils.ENTITY_MANAGER_FECHADO_OU_TRANSACAO_INATIVA);
		}
	}
	
	protected void commit() {
		if(entityManager.isOpen() && entityManager.getTransaction().isActive()) {
			entityManager.getTransaction().commit();
			entityManager.flush();
			close();
		} else {
			System.out.println(MensagemUtils.ENTITY_MANAGER_FECHADO_OU_TRANSACAO_INATIVA);
		}
	}
	
	protected Query createQuery(String hql) {
		if(entityManager != null && StringUtils.isNotBlank(hql)) {
			return entityManager.createQuery(hql);
		}
		return null;
	}
}
