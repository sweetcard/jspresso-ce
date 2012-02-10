/*
 * Copyright (c) 2005-2012 Vincent Vandenschrick. All rights reserved.
 *
 *  This file is part of the Jspresso framework.
 *
 *  Jspresso is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Jspresso is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Jspresso.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jspresso.framework.application.startup.development;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.jspresso.framework.application.backend.BackendControllerHolder;
import org.jspresso.framework.application.backend.persistence.hibernate.HibernateBackendController;
import org.jspresso.framework.model.component.IComponent;
import org.jspresso.framework.model.entity.IEntity;
import org.jspresso.framework.model.entity.IEntityFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;

/**
 * A utility class used to persist some test data.
 * 
 * @version $LastChangedRevision$
 * @author Vincent Vandenschrick
 */
public abstract class AbstractTestDataPersister {

  private IEntityFactory    entityFactory;
  private HibernateTemplate hibernateTemplate;

  /**
   * Constructs a new <code>AbstractTestDataPersister</code> instance.
   * 
   * @param beanFactory
   *          the spring bean factory to use.
   */
  public AbstractTestDataPersister(BeanFactory beanFactory) {
    HibernateBackendController hbc = (HibernateBackendController) beanFactory
        .getBean("applicationBackController");
    BackendControllerHolder.setCurrentBackendController(hbc);
    hibernateTemplate = hbc.getHibernateTemplate();
    entityFactory = hbc.getEntityFactory();
  }

  /**
   * Creates and persist the test data.
   */
  public abstract void persistTestData();

  /**
   * Creates a component instance.
   * 
   * @param <T>
   *          the actual component type.
   * @param componentContract
   *          the component contract.
   * @return the created component.
   */
  protected <T extends IComponent> T createComponentInstance(
      Class<T> componentContract) {
    return entityFactory.createComponentInstance(componentContract);
  }

  /**
   * Creates an entity instance.
   * 
   * @param <T>
   *          the actual entity type.
   * @param entityContract
   *          the entity contract.
   * @return the created entity.
   */
  protected <T extends IEntity> T createEntityInstance(Class<T> entityContract) {
    return entityFactory.createEntityInstance(entityContract);
  }

  /**
   * Persists or update an entity.
   * 
   * @param entity
   *          the entity to persist or update.
   */
  protected void saveOrUpdate(IEntity entity) {
    hibernateTemplate.saveOrUpdate(entity);
  }

  /**
   * Query entities.
   * 
   * @param queryString
   *          the HSQL query string.
   * @return the entity list.
   */
  protected List<?> find(String queryString) {
    return hibernateTemplate.find(queryString);
  }

  /**
   * Query entities by criteria.
   * 
   * @param criteria
   *          the Hibernate detached criteria.
   * @return the entity list.
   */
  protected List<?> findByCriteria(DetachedCriteria criteria) {
    return hibernateTemplate.findByCriteria(criteria);
  }
}
