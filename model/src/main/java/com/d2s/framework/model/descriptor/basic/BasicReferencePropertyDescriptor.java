/*
 * Copyright (c) 2005 Design2see. All rights reserved.
 */
package com.d2s.framework.model.descriptor.basic;

import java.util.Map;

import com.d2s.framework.model.descriptor.IComponentDescriptor;
import com.d2s.framework.model.descriptor.IReferencePropertyDescriptor;

/**
 * Default implementation of a reference descriptor.
 * <p>
 * Copyright 2005 Design2See. All rights reserved.
 * <p>
 * 
 * @version $LastChangedRevision$
 * @author Vincent Vandenschrick
 * @param <E>
 *          the concrete component type.
 */
public class BasicReferencePropertyDescriptor<E> extends
    BasicRelationshipEndPropertyDescriptor implements
    IReferencePropertyDescriptor<E> {

  private Map<String, String>     initializationMapping;
  private IComponentDescriptor<E> referencedDescriptor;

  /**
   * {@inheritDoc}
   */
  public IComponentDescriptor getComponentDescriptor() {
    return getReferencedDescriptor();
  }

  /**
   * Gets the initializationMapping.
   * 
   * @return the initializationMapping.
   */
  public Map<String, String> getInitializationMapping() {
    if (initializationMapping != null) {
      return initializationMapping;
    }
    if (getParentDescriptor() != null) {
      return ((IReferencePropertyDescriptor<?>) getParentDescriptor())
          .getInitializationMapping();
    }
    return initializationMapping;
  }

  /**
   * {@inheritDoc}
   */
  public Class getModelType() {
    return getReferencedDescriptor().getComponentContract();
  }

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  public IComponentDescriptor<E> getReferencedDescriptor() {
    if (referencedDescriptor != null) {
      return referencedDescriptor;
    }
    if (getParentDescriptor() != null) {
      return ((IReferencePropertyDescriptor<E>) getParentDescriptor())
          .getReferencedDescriptor();
    }
    return referencedDescriptor;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isQueryable() {
    return true;
  }

  /**
   * Sets the initializationMapping.
   * 
   * @param initializationMapping
   *          the initializationMapping to set.
   */
  public void setInitializationMapping(Map<String, String> initializationMapping) {
    this.initializationMapping = initializationMapping;
  }

  /**
   * Sets the referencedDescriptor property.
   * 
   * @param referencedDescriptor
   *          the referencedDescriptor to set.
   */
  public void setReferencedDescriptor(
      IComponentDescriptor<E> referencedDescriptor) {
    this.referencedDescriptor = referencedDescriptor;
  }
}
