package com.marcosevaristo.util;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

public class ConnectionMarcos {
	
	private final String DATABASE_NAME = "CIDADES";
	private EntityManagerFactory entityManagerFactory;
	private EntityManager entityManager;

	protected EntityManager getEntityManager() {
		if(entityManager == null) {
			entityManagerFactory = Persistence.createEntityManagerFactory(DATABASE_NAME);
			entityManager = entityManagerFactory.createEntityManager();
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
	
	protected void beginTransaction() {
		if(entityManager != null && entityManager.isOpen()) {
			entityManager.getTransaction().begin();
		} else {
			entityManagerFactory = Persistence.createEntityManagerFactory(DATABASE_NAME);
			entityManager = entityManagerFactory.createEntityManager();
			entityManager.getTransaction().begin();
		}
	}
	
	protected void persist(Object objeto) {
		if(entityManager.isOpen() && entityManager.getTransaction().isActive()) {
			entityManager.persist(objeto);
		} else {
			System.out.println(MensagemUtils.ENTITY_MANAGER_FECHADO_OU_TRANSACAO_INATIVA);
		}
	}
	
	protected void commit() {
		if(entityManager.isOpen() && entityManager.getTransaction().isActive()) {
			entityManager.getTransaction().commit();
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
