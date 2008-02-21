/*
 * Copyright (c) 2005-2008 Vincent Vandenschrick. All rights reserved.
 */
package com.d2s.framework.application.backend.action.persistence.hibernate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.d2s.framework.application.model.BeanCollectionModule;
import com.d2s.framework.application.model.BeanModule;
import com.d2s.framework.application.model.Module;
import com.d2s.framework.binding.ICompositeValueConnector;
import com.d2s.framework.model.entity.IEntity;

/**
 * Saves the projected object(s) in a transaction.
 * <p>
 * Copyright (c) 2005-2008 Vincent Vandenschrick. All rights reserved.
 * <p>
 * 
 * @version $LastChangedRevision$
 * @author Vincent Vandenschrick
 */
public class SaveModuleObjectAction extends SaveAction {

  /**
   * Saves the projected object(s) in a transaction.
   * <p>
   * {@inheritDoc}
   */
  @Override
  protected List<IEntity> getEntitiesToSave(Map<String, Object> context) {
    List<IEntity> entitiesToSave = new ArrayList<IEntity>();
    ICompositeValueConnector moduleConnector = getModuleConnector(context);
    Module module = (Module) moduleConnector.getConnectorValue();
    if (module instanceof BeanCollectionModule
        && ((BeanCollectionModule) module).getModuleObjects() != null) {
      for (Object entity : ((BeanCollectionModule) module).getModuleObjects()) {
        entitiesToSave.add((IEntity) entity);
      }
    } else if (module instanceof BeanModule) {
      entitiesToSave.add((IEntity) ((BeanModule) module).getModuleObject());
    }
    return entitiesToSave;
  }

}
