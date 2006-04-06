/*
 * Copyright (c) 2005 Design2see. All rights reserved.
 */
package com.d2s.framework.application.backend.session.hibernate;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.hibernate.Hibernate;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.collection.PersistentCollection;
import org.hibernate.collection.PersistentList;
import org.hibernate.collection.PersistentSet;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.d2s.framework.application.backend.session.basic.BasicApplicationSession;
import com.d2s.framework.model.entity.IEntity;
import com.d2s.framework.util.bean.PropertyHelper;

/**
 * Basic implementation of an application session aware of hibernate when
 * merging back entities from the unit of work.
 * <p>
 * Copyright 2005 Design2See. All rights reserved.
 * <p>
 * 
 * @version $LastChangedRevision$
 * @author Vincent Vandenschrick
 */
public class HibernateAwareApplicationSession extends BasicApplicationSession {

  private HibernateTemplate hibernateTemplate;

  /**
   * Does the unit of work back merge in the scope of an hibernate session.
   * <p>
   * {@inheritDoc}
   */
  @Override
  public void commitUnitOfWork() {
    hibernateTemplate.execute(new HibernateCallback() {

      /**
       * {@inheritDoc}
       */
      public Object doInHibernate(Session session) {
        for (IEntity entityToMergeBack : getEntitiesToMergeBack()) {
          session.lock(entityToMergeBack, LockMode.NONE);
        }
        performActualUnitOfWorkCommit();
        return null;
      }
    });
  }

  private void performActualUnitOfWorkCommit() {
    super.commitUnitOfWork();
  }

  /**
   * This method wraps transient collections in the equivalent hibernate ones.
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  @Override
  protected Collection<IEntity> wrapDetachedEntityCollection(IEntity entity,
      Collection<IEntity> transientCollection,
      Collection<IEntity> snapshotCollection, String role) {
    if (!(transientCollection instanceof PersistentCollection)) {
      if (entity.isPersistent()) {
        if (transientCollection instanceof Set) {
          PersistentSet persistentSet = new PersistentSet(null,
              (Set<?>) transientCollection);
          persistentSet.setOwner(entity);
          HashMap<Object, Object> snapshot = new HashMap<Object, Object>();
          if (snapshotCollection == null) {
            persistentSet.clearDirty();
            snapshotCollection = transientCollection;
          }
          for (Object snapshotCollectionElement : snapshotCollection) {
            snapshot.put(snapshotCollectionElement, snapshotCollectionElement);
          }
          persistentSet.setSnapshot(entity.getId(), getHibernateRoleName(entity
              .getContract(), role), snapshot);
          return persistentSet;
        } else if (transientCollection instanceof List) {
          PersistentList persistentList = new PersistentList(null,
              (List<?>) transientCollection);
          persistentList.setOwner(entity);
          ArrayList<Object> snapshot = new ArrayList<Object>();
          if (snapshotCollection == null) {
            persistentList.clearDirty();
            snapshotCollection = transientCollection;
          }
          for (Object snapshotCollectionElement : snapshotCollection) {
            snapshot.add(snapshotCollectionElement);
          }
          persistentList.setSnapshot(entity.getId(), getHibernateRoleName(
              entity.getContract(), role), snapshot);
          return persistentList;
        }
      }
    } else {
      if (snapshotCollection == null) {
        ((PersistentCollection) transientCollection).clearDirty();
      } else {
        ((PersistentCollection) transientCollection).dirty();
      }
    }
    return super.wrapDetachedEntityCollection(entity, transientCollection,
        snapshotCollection, role);
  }

  /**
   * Sets the hibernateTemplate.
   * 
   * @param hibernateTemplate
   *          the hibernateTemplate to set.
   */
  public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
    this.hibernateTemplate = hibernateTemplate;
  }

  private static String getHibernateRoleName(Class<?> entityContract,
      String role) {
    PropertyDescriptor roleDescriptor = PropertyHelper.getPropertyDescriptor(
        entityContract, role);
    Class<?> entityDeclaringClass = roleDescriptor.getReadMethod()
        .getDeclaringClass();
    if (!IEntity.class.isAssignableFrom(entityDeclaringClass)) {
      entityDeclaringClass = entityContract;
    }
    return entityDeclaringClass.getName() + "." + role;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isInitialized(Object objectOrProxy) {
    return Hibernate.isInitialized(objectOrProxy);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void initializePropertyIfNeeded(final IEntity entity,
      final String propertyName) {
    if (Hibernate.isInitialized(entity.straightGetProperty(propertyName))) {
      return;
    }
    boolean dirtRecorderWasEnabled = getDirtRecorder().isEnabled();
    try {
      getDirtRecorder().setEnabled(false);

      hibernateTemplate.execute(new HibernateCallback() {

        /**
         * {@inheritDoc}
         */
        public Object doInHibernate(Session session) {
          IEntity lockedEntity = null;
          if (lockedEntity == null) {
            session.lock(entity, LockMode.NONE);
            lockedEntity = entity;
          }
          session.setReadOnly(lockedEntity, true);

          Object initializedProperty = lockedEntity
              .straightGetProperty(propertyName);
          Hibernate.initialize(initializedProperty);
          if (lockedEntity != entity) {
            entity.straightSetProperty(propertyName, initializedProperty);
          }
          return null;
        }
      });
    } finally {
      getDirtRecorder().setEnabled(dirtRecorderWasEnabled);
    }
  }
}
