/* Copyright � 2019, Oracle And/or its affiliates. All rights reserved. */
package com.universal.core.library.configuration;

/**
 * @author cksiow
 *
 */
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import javax.persistence.EntityManager;
import java.io.Serializable;

public class BaseRepositoryFactoryBean<R extends JpaRepository<T, I>, T, I extends Serializable>
		extends JpaRepositoryFactoryBean<R, T, I> {

	/**
	 * @param repositoryInterface
	 */
	public BaseRepositoryFactoryBean(Class<? extends R> repositoryInterface) {
		super(repositoryInterface);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected RepositoryFactorySupport createRepositoryFactory(EntityManager em) {
		return new BaseRepositoryFactory(em);
	}

	private static class BaseRepositoryFactory<T, I extends Serializable> extends JpaRepositoryFactory {

		private final EntityManager em;

		public BaseRepositoryFactory(EntityManager em) {
			super(em);
			this.em = em;
		}

		@Override
		protected JpaRepositoryImplementation<?, ?> getTargetRepository(RepositoryInformation information,
				EntityManager entityManager) {

			JpaEntityInformation<?, Serializable> entityInformation = getEntityInformation(information.getDomainType());
			return getTargetRepositoryViaReflection(information, entityInformation, entityManager);

		}


	}
}
