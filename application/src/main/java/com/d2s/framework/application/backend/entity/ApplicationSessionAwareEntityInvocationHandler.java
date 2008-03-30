/*
 * Copyright (c) 2005-2008 Vincent Vandenschrick. All rights reserved.
 */
package com.d2s.framework.application.backend.entity;

import java.lang.reflect.Proxy;

import com.d2s.framework.application.backend.component.ApplicationSessionAwareComponentInvocationHandler;
import com.d2s.framework.application.backend.session.IApplicationSession;
import com.d2s.framework.model.component.IComponent;
import com.d2s.framework.model.component.IComponentCollectionFactory;
import com.d2s.framework.model.component.IComponentExtensionFactory;
import com.d2s.framework.model.component.IComponentFactory;
import com.d2s.framework.model.component.basic.BasicComponentInvocationHandler;
import com.d2s.framework.model.descriptor.ICollectionPropertyDescriptor;
import com.d2s.framework.model.descriptor.IComponentDescriptor;
import com.d2s.framework.model.descriptor.IReferencePropertyDescriptor;
import com.d2s.framework.model.entity.IEntity;
import com.d2s.framework.model.entity.basic.BasicEntityInvocationHandler;
import com.d2s.framework.util.accessor.IAccessorFactory;

/**
 * This entity invocation handler handles initialization of lazy loaded
 * properties like collections an entity references, delegating the
 * initialization job to the application session.
 * <p>
 * Copyright (c) 2005-2008 Vincent Vandenschrick. All rights reserved.
 * <p>
 * 
 * @version $LastChangedRevision$
 * @author Vincent Vandenschrick
 */
public class ApplicationSessionAwareEntityInvocationHandler extends
    BasicEntityInvocationHandler {

  private static final long   serialVersionUID = 3663517052427878204L;

  private IApplicationSession applicationSession;

  /**
   * Constructs a new
   * <code>ApplicationSessionAwareEntityInvocationHandler</code> instance.
   * 
   * @param entityDescriptor
   *            The descriptor of the proxy entity.
   * @param inlineComponentFactory
   *            the factory used to create inline components.
   * @param collectionFactory
   *            The factory used to create empty entity collections from
   *            collection getters.
   * @param accessorFactory
   *            The factory used to access proxy properties.
   * @param extensionFactory
   *            The factory used to create entity extensions based on their
   *            classes.
   * @param applicationSession
   *            the current application session.
   */
  protected ApplicationSessionAwareEntityInvocationHandler(
      IComponentDescriptor<IComponent> entityDescriptor,
      IComponentFactory inlineComponentFactory,
      IComponentCollectionFactory<IComponent> collectionFactory,
      IAccessorFactory accessorFactory,
      IComponentExtensionFactory extensionFactory,
      IApplicationSession applicationSession) {
    super(entityDescriptor, inlineComponentFactory, collectionFactory,
        accessorFactory, extensionFactory);
    this.applicationSession = applicationSession;
  }

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  @Override
  protected Object getCollectionProperty(Object proxy,
      ICollectionPropertyDescriptor propertyDescriptor) {
    applicationSession.initializePropertyIfNeeded((IEntity) proxy,
        propertyDescriptor);
    return super.getCollectionProperty(proxy, propertyDescriptor);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Object getReferenceProperty(Object proxy,
      IReferencePropertyDescriptor<IComponent> propertyDescriptor) {

    applicationSession.initializePropertyIfNeeded((IEntity) proxy,
        propertyDescriptor);
    return super.getReferenceProperty(proxy, propertyDescriptor);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean isInitialized(Object objectOrProxy) {
    return applicationSession.isInitialized(objectOrProxy);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void storeReferenceProperty(
      IReferencePropertyDescriptor<?> propertyDescriptor, Object propertyValue) {
    if (propertyValue != null && isInlineComponentReference(propertyDescriptor)) {
      if (Proxy.isProxyClass(propertyValue.getClass())
          && Proxy.getInvocationHandler(propertyValue) instanceof BasicComponentInvocationHandler
          && !(Proxy.getInvocationHandler(propertyValue) instanceof ApplicationSessionAwareComponentInvocationHandler)) {
        IComponent sessionAwareComponent = getInlineComponentFactory()
            .createComponentInstance(((IComponent) propertyValue).getContract());
        sessionAwareComponent
            .straightSetProperties(((IComponent) propertyValue)
                .straightGetProperties());
        super.storeReferenceProperty(propertyDescriptor, sessionAwareComponent);
      } else {
        super.storeReferenceProperty(propertyDescriptor, propertyValue);
      }
    } else {
      super.storeReferenceProperty(propertyDescriptor, propertyValue);
    }
  }
}
