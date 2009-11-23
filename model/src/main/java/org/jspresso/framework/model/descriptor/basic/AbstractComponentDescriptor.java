/*
 * Copyright (c) 2005-2009 Vincent Vandenschrick. All rights reserved.
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
package org.jspresso.framework.model.descriptor.basic;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.jspresso.framework.model.component.service.IComponentService;
import org.jspresso.framework.model.component.service.ILifecycleInterceptor;
import org.jspresso.framework.model.descriptor.DescriptorException;
import org.jspresso.framework.model.descriptor.ICollectionPropertyDescriptor;
import org.jspresso.framework.model.descriptor.IComponentDescriptor;
import org.jspresso.framework.model.descriptor.IComponentDescriptorProvider;
import org.jspresso.framework.model.descriptor.IPropertyDescriptor;
import org.jspresso.framework.model.descriptor.IReferencePropertyDescriptor;
import org.jspresso.framework.model.descriptor.IStringPropertyDescriptor;
import org.jspresso.framework.model.descriptor.ITextPropertyDescriptor;
import org.jspresso.framework.model.entity.IEntity;
import org.jspresso.framework.util.accessor.IAccessor;
import org.jspresso.framework.util.collection.ESort;
import org.jspresso.framework.util.descriptor.DefaultIconDescriptor;
import org.jspresso.framework.util.exception.NestedRuntimeException;
import org.jspresso.framework.util.gate.IGate;
import org.jspresso.framework.util.lang.StringUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

/**
 * Abstract implementation of a component descriptor.
 * 
 * @version $LastChangedRevision$
 * @author Vincent Vandenschrick
 * @param <E>
 *          the concrete type of components.
 */
public abstract class AbstractComponentDescriptor<E> extends
    DefaultIconDescriptor implements IComponentDescriptor<E>, BeanFactoryAware {

  private BeanFactory                      beanFactory;

  private List<IComponentDescriptor<?>>    ancestorDescriptors;
  private Class<?>                         componentContract;

  private Collection<String>               grantedRoles;
  private List<String>                     lifecycleInterceptorClassNames;
  private List<String>                     lifecycleInterceptorBeanNames;

  private List<ILifecycleInterceptor<?>>   lifecycleInterceptors;
  private Map<String, IPropertyDescriptor> nestedPropertyDescriptors;
  private Map<String, ESort>               orderingProperties;
  private Map<String, IPropertyDescriptor> propertyDescriptorsMap;
  private List<String>                     queryableProperties;

  private List<String>                     renderedProperties;
  private Set<Class<?>>                    serviceContracts;
  private Map<String, String>              serviceDelegateClassNames;
  private Map<String, String>              serviceDelegateBeanNames;

  private Map<Method, IComponentService>   serviceDelegates;
  private List<IPropertyDescriptor>        tempPropertyBuffer;
  private String                           toStringProperty;
  private Collection<String>               unclonedProperties;

  private Collection<IGate>                readabilityGates;
  private Collection<IGate>                writabilityGates;

  private Integer                          pageSize;

  private String                           sqlName;

  /**
   * Constructs a new <code>AbstractComponentDescriptor</code> instance.
   * 
   * @param name
   *          the name of the descriptor which has to be the fully-qualified
   *          class name of its contract.
   */
  public AbstractComponentDescriptor(String name) {
    setName(name);
  }

  /**
   * Gets the descriptor ancestors collection. It directly translates the
   * components inheritance hierarchy since the component property descriptors
   * are the union of the declared property descriptors of the component and of
   * its ancestors one. A component may have multiple ancestors which means that
   * complex multi-inheritance hierarchy can be mapped.
   * 
   * @return ancestorDescriptors The list of ancestor entity descriptors.
   */
  public List<IComponentDescriptor<?>> getAncestorDescriptors() {
    return ancestorDescriptors;
  }

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  public Class<? extends E> getComponentContract() {
    if (componentContract == null && getName() != null) {
      try {
        componentContract = Class.forName(getName());
      } catch (ClassNotFoundException ex) {
        throw new NestedRuntimeException(ex);
      }
    }
    return (Class<? extends E>) componentContract;
  }

  /**
   * {@inheritDoc}
   */
  public IComponentDescriptor<E> getComponentDescriptor() {
    return this;
  }

  /**
   * {@inheritDoc}
   */
  public Collection<IPropertyDescriptor> getDeclaredPropertyDescriptors() {
    processPropertiesBufferIfNecessary();
    if (propertyDescriptorsMap != null) {
      return propertyDescriptorsMap.values();
    }
    return null;
  }

  /**
   * Gets the grantedRoles.
   * 
   * @return the grantedRoles.
   */
  public Collection<String> getGrantedRoles() {
    return grantedRoles;
  }

  /**
   * Gets the lifecycleInterceptors.
   * 
   * @return the lifecycleInterceptors.
   */
  public List<ILifecycleInterceptor<?>> getLifecycleInterceptors() {
    List<ILifecycleInterceptor<?>> allInterceptors = new ArrayList<ILifecycleInterceptor<?>>();
    if (getAncestorDescriptors() != null) {
      for (IComponentDescriptor<?> ancestorDescriptor : getAncestorDescriptors()) {
        allInterceptors.addAll(ancestorDescriptor.getLifecycleInterceptors());
      }
    }
    registerLifecycleInterceptorsIfNecessary();
    if (lifecycleInterceptors != null) {
      allInterceptors.addAll(lifecycleInterceptors);
    }
    return allInterceptors;
  }

  /**
   * {@inheritDoc}
   */
  public Class<?> getModelType() {
    return getComponentContract();
  }

  /**
   * {@inheritDoc}
   */
  public Map<String, ESort> getOrderingProperties() {
    // use a set to avoid duplicates.
    Map<String, ESort> properties = new LinkedHashMap<String, ESort>();
    if (orderingProperties != null) {
      properties.putAll(orderingProperties);
    }
    if (getAncestorDescriptors() != null) {
      for (IComponentDescriptor<?> ancestorDescriptor : getAncestorDescriptors()) {
        if (ancestorDescriptor.getOrderingProperties() != null) {
          properties.putAll(ancestorDescriptor.getOrderingProperties());
        }
      }
    }
    if (properties.isEmpty()) {
      return null;
    }
    return properties;
  }

  /**
   * {@inheritDoc}
   */
  public IPropertyDescriptor getPropertyDescriptor(String propertyName) {
    IPropertyDescriptor descriptor = null;
    int nestedDotIndex = propertyName.indexOf(IAccessor.NESTED_DELIM);
    if (nestedDotIndex > 0) {
      if (nestedPropertyDescriptors == null) {
        nestedPropertyDescriptors = new HashMap<String, IPropertyDescriptor>();
      }
      descriptor = nestedPropertyDescriptors.get(propertyName);
      if (descriptor == null) {
        IComponentDescriptor<?> componentDescriptor = ((IComponentDescriptorProvider<?>) getPropertyDescriptor(propertyName
            .substring(0, nestedDotIndex))).getComponentDescriptor();
        descriptor = componentDescriptor.getPropertyDescriptor(
            propertyName.substring(nestedDotIndex + 1)).clone();
        if (descriptor instanceof BasicPropertyDescriptor) {
          ((BasicPropertyDescriptor) descriptor).setName(propertyName);
        }
        nestedPropertyDescriptors.put(propertyName, descriptor);
      }
    } else {
      descriptor = getDeclaredPropertyDescriptor(propertyName);
      if (descriptor == null && getAncestorDescriptors() != null) {
        for (Iterator<IComponentDescriptor<?>> ite = getAncestorDescriptors()
            .iterator(); descriptor == null && ite.hasNext();) {
          IComponentDescriptor<?> ancestorDescriptor = ite.next();
          descriptor = ancestorDescriptor.getPropertyDescriptor(propertyName);
        }
      }
    }
    return descriptor;
  }

  /**
   * {@inheritDoc}
   */
  public Collection<IPropertyDescriptor> getPropertyDescriptors() {
    // A map is used instead of a set since a set does not replace an element it
    // already contains.
    Map<String, IPropertyDescriptor> allDescriptors = new LinkedHashMap<String, IPropertyDescriptor>();
    if (getAncestorDescriptors() != null) {
      for (IComponentDescriptor<?> ancestorDescriptor : getAncestorDescriptors()) {
        for (IPropertyDescriptor propertyDescriptor : ancestorDescriptor
            .getPropertyDescriptors()) {
          allDescriptors.put(propertyDescriptor.getName(), propertyDescriptor);
        }
      }
    }
    Collection<IPropertyDescriptor> declaredPropertyDescriptors = getDeclaredPropertyDescriptors();
    if (declaredPropertyDescriptors != null) {
      for (IPropertyDescriptor propertyDescriptor : declaredPropertyDescriptors) {
        allDescriptors.put(propertyDescriptor.getName(), propertyDescriptor);
      }
    }
    return allDescriptors.values();
  }

  /**
   * {@inheritDoc}
   */
  public List<String> getQueryableProperties() {
    if (queryableProperties == null) {
      queryableProperties = new ArrayList<String>();
      for (String renderedProperty : getRenderedProperties()) {
        IPropertyDescriptor propertyDescriptor = getPropertyDescriptor(renderedProperty);
        if (propertyDescriptor.isQueryable()) {
          queryableProperties.add(renderedProperty);
        }
      }
    }
    return explodeComponentReferences(queryableProperties);
  }

  /**
   * {@inheritDoc}
   */
  public Class<?> getQueryComponentContract() {
    return getComponentContract();
  }

  /**
   * {@inheritDoc}
   */
  public List<String> getRenderedProperties() {
    if (renderedProperties == null) {
      renderedProperties = new ArrayList<String>();
      for (IPropertyDescriptor propertyDescriptor : getPropertyDescriptors()) {
        if (!(propertyDescriptor instanceof ICollectionPropertyDescriptor<?>)
            && !(propertyDescriptor instanceof ITextPropertyDescriptor)) {
          renderedProperties.add(propertyDescriptor.getName());
        }
      }
    }
    return explodeComponentReferences(renderedProperties);
  }

  /**
   * {@inheritDoc}
   */
  public Collection<String> getServiceContractClassNames() {
    Set<String> serviceContractClassNames = new LinkedHashSet<String>();
    if (serviceContracts != null) {
      for (Class<?> serviceContract : serviceContracts) {
        serviceContractClassNames.add(serviceContract.getName());
      }
    } else {
      if (serviceDelegateClassNames != null) {
        serviceContractClassNames.addAll(serviceDelegateClassNames.keySet());
      }
      if (serviceDelegateBeanNames != null) {
        serviceContractClassNames.addAll(serviceDelegateBeanNames.keySet());
      }
    }
    return serviceContractClassNames;
  }

  /**
   * {@inheritDoc}
   */
  public Collection<Class<?>> getServiceContracts() {
    registerDelegateServicesIfNecessary();
    if (serviceContracts != null) {
      return new ArrayList<Class<?>>(serviceContracts);
    }
    return null;
  }

  /**
   * {@inheritDoc}
   */
  public IComponentService getServiceDelegate(Method targetMethod) {
    registerDelegateServicesIfNecessary();
    IComponentService service = null;
    if (serviceDelegates != null) {
      service = serviceDelegates.get(targetMethod);
    }
    if (service == null && getAncestorDescriptors() != null) {
      for (Iterator<IComponentDescriptor<?>> ite = getAncestorDescriptors()
          .iterator(); service == null && ite.hasNext();) {
        IComponentDescriptor<?> ancestorDescriptor = ite.next();
        service = ancestorDescriptor.getServiceDelegate(targetMethod);
      }
    }
    return service;
  }

  /**
   * Gets the toStringProperty.
   * 
   * @return the toStringProperty.
   */
  public String getToStringProperty() {
    if (toStringProperty == null) {
      for (String renderedProperty : getRenderedProperties()) {
        if (getPropertyDescriptor(renderedProperty) instanceof IStringPropertyDescriptor) {
          toStringProperty = renderedProperty;
          break;
        }
      }
      if (toStringProperty == null) {
        toStringProperty = getRenderedProperties().get(0);
      }
    }
    return toStringProperty;
  }

  /**
   * The properties returned include the uncloned properties of the ancestors.
   * <p>
   * {@inheritDoc}
   */
  public Collection<String> getUnclonedProperties() {
    Set<String> properties = new HashSet<String>();
    if (unclonedProperties != null) {
      properties.addAll(unclonedProperties);
    }
    if (getAncestorDescriptors() != null) {
      for (IComponentDescriptor<?> ancestorDescriptor : getAncestorDescriptors()) {
        properties.addAll(ancestorDescriptor.getUnclonedProperties());
      }
    }
    return properties;
  }

  /**
   * Registers this descriptor with a collection of ancestors. It directly
   * translates the components inheritance hierarchy since the component
   * property descriptors are the union of the declared property descriptors of
   * the component and of its ancestors one. A component may have multiple
   * ancestors which means that complex multi-inheritance hierarchy can be
   * mapped.
   * 
   * @param ancestorDescriptors
   *          The list of ancestor component descriptors.
   */
  public void setAncestorDescriptors(
      List<IComponentDescriptor<?>> ancestorDescriptors) {
    this.ancestorDescriptors = ancestorDescriptors;
  }

  /**
   * Sets the grantedRoles.
   * 
   * @param grantedRoles
   *          the grantedRoles to set.
   */
  public void setGrantedRoles(Collection<String> grantedRoles) {
    this.grantedRoles = StringUtils.ensureSpaceFree(grantedRoles);
  }

  /**
   * Sets the lifecycleInterceptorClassNames.
   * 
   * @param lifecycleInterceptorClassNames
   *          the lifecycleInterceptorClassNames to set.
   */
  public void setLifecycleInterceptorClassNames(
      List<String> lifecycleInterceptorClassNames) {
    this.lifecycleInterceptorClassNames = StringUtils
        .ensureSpaceFree(lifecycleInterceptorClassNames);
  }

  /**
   * Sets the orderingProperties.
   * 
   * @param untypedOrderingProperties
   *          the orderingProperties to set.
   */
  public void setOrderingProperties(Map<String, ?> untypedOrderingProperties) {
    if (untypedOrderingProperties != null) {
      orderingProperties = new LinkedHashMap<String, ESort>();
      for (Map.Entry<String, ?> untypedOrderingProperty : untypedOrderingProperties
          .entrySet()) {
        if (untypedOrderingProperty.getValue() instanceof ESort) {
          orderingProperties.put(untypedOrderingProperty.getKey(),
              (ESort) untypedOrderingProperty.getValue());
        } else if (untypedOrderingProperty.getValue() instanceof String) {
          orderingProperties.put(untypedOrderingProperty.getKey(), ESort
              .valueOf((String) untypedOrderingProperty.getValue()));
        } else {
          orderingProperties.put(untypedOrderingProperty.getKey(),
              ESort.ASCENDING);
        }
      }
    } else {
      orderingProperties = null;
    }
  }

  /**
   * Sets the propertyDescriptors property.
   * 
   * @param descriptors
   *          the propertyDescriptors to set.
   */
  public void setPropertyDescriptors(Collection<IPropertyDescriptor> descriptors) {
    // This is important to use an intermediate structure since all descriptors
    // may not have their names fully initialized.
    if (descriptors != null) {
      tempPropertyBuffer = new ArrayList<IPropertyDescriptor>(descriptors);
      propertyDescriptorsMap = null;
    } else {
      tempPropertyBuffer = null;
      propertyDescriptorsMap = null;
    }
  }

  /**
   * Sets the queryableProperties.
   * 
   * @param queryableProperties
   *          the queryableProperties to set.
   */
  public void setQueryableProperties(List<String> queryableProperties) {
    this.queryableProperties = StringUtils.ensureSpaceFree(queryableProperties);
  }

  /**
   * Sets the renderedProperties.
   * 
   * @param renderedProperties
   *          the renderedProperties to set.
   */
  public void setRenderedProperties(List<String> renderedProperties) {
    this.renderedProperties = StringUtils.ensureSpaceFree(renderedProperties);
  }

  /**
   * Registers the service delegates which help the component to implement the
   * services defined by its contract.
   * 
   * @param serviceDelegateClassNames
   *          the component services to be registered keyed by their contract. A
   *          service contract is an interface class defining the service
   *          methods to be registered as implemented by the service delegate.
   *          Map values must be instances of <code>IComponentService</code>.
   */
  public void setServiceDelegateClassNames(
      Map<String, String> serviceDelegateClassNames) {
    this.serviceDelegateClassNames = StringUtils
        .ensureSpaceFree(serviceDelegateClassNames);
  }

  /**
   * Sets the toStringProperty.
   * 
   * @param toStringProperty
   *          the toStringProperty to set.
   */
  public void setToStringProperty(String toStringProperty) {
    this.toStringProperty = toStringProperty;
  }

  /**
   * Sets the unclonedProperties.
   * 
   * @param unclonedProperties
   *          the unclonedProperties to set.
   */
  public void setUnclonedProperties(Collection<String> unclonedProperties) {
    this.unclonedProperties = StringUtils.ensureSpaceFree(unclonedProperties);
  }

  private List<String> explodeComponentReferences(List<String> propertyNames) {
    List<String> explodedProperties = new ArrayList<String>();
    for (String propertyName : propertyNames) {
      IPropertyDescriptor propertyDescriptor = getPropertyDescriptor(propertyName);
      if ((propertyDescriptor instanceof IReferencePropertyDescriptor<?> && !IEntity.class
          .isAssignableFrom(((IReferencePropertyDescriptor<?>) propertyDescriptor)
              .getReferencedDescriptor().getComponentContract()))) {
        List<String> nestedProperties = new ArrayList<String>();
        for (String nestedRenderedProperty : ((IReferencePropertyDescriptor<?>) propertyDescriptor)
            .getReferencedDescriptor().getRenderedProperties()) {
          nestedProperties.add(propertyName + "." + nestedRenderedProperty);
        }
        explodedProperties.addAll(explodeComponentReferences(nestedProperties));
      } else {
        explodedProperties.add(propertyName);
      }
    }
    return explodedProperties;
  }

  private IPropertyDescriptor getDeclaredPropertyDescriptor(String propertyName) {
    processPropertiesBufferIfNecessary();
    if (propertyDescriptorsMap != null) {
      return propertyDescriptorsMap.get(propertyName);
    }
    return null;
  }

  private synchronized void processPropertiesBufferIfNecessary() {
    if (tempPropertyBuffer != null) {
      propertyDescriptorsMap = new LinkedHashMap<String, IPropertyDescriptor>();
      for (IPropertyDescriptor descriptor : tempPropertyBuffer) {
        propertyDescriptorsMap.put(descriptor.getName(), descriptor);
      }
      tempPropertyBuffer = null;
    }
  }

  private synchronized void registerDelegateServicesIfNecessary() {
    if (serviceDelegateClassNames != null) {
      for (Entry<String, String> nextPair : serviceDelegateClassNames
          .entrySet()) {
        try {
          IComponentService delegate = null;
          if (!("".equals(nextPair.getValue()) || "null"
              .equalsIgnoreCase(nextPair.getValue()))) {
            delegate = (IComponentService) Class.forName(nextPair.getValue())
                .newInstance();
          }
          registerService(Class.forName(nextPair.getKey()), delegate);
        } catch (ClassNotFoundException ex) {
          throw new DescriptorException(ex);
        } catch (InstantiationException ex) {
          throw new DescriptorException(ex);
        } catch (IllegalAccessException ex) {
          throw new DescriptorException(ex);
        }
      }
      serviceDelegateClassNames = null;
    }
    if (serviceDelegateBeanNames != null && beanFactory != null) {
      for (Entry<String, String> nextPair : serviceDelegateBeanNames.entrySet()) {
        try {
          registerService(Class.forName(nextPair.getKey()),
              (IComponentService) beanFactory.getBean(nextPair.getValue(),
                  IComponentService.class));
        } catch (ClassNotFoundException ex) {
          throw new DescriptorException(ex);
        }
      }
      serviceDelegateBeanNames = null;
    }
  }

  private synchronized void registerLifecycleInterceptorsIfNecessary() {
    // process creation of lifecycle interceptors.
    if (lifecycleInterceptorClassNames != null) {
      for (String lifecycleInterceptorClassName : lifecycleInterceptorClassNames) {
        try {
          registerLifecycleInterceptor((ILifecycleInterceptor<?>) Class
              .forName(lifecycleInterceptorClassName).newInstance());
        } catch (InstantiationException ex) {
          throw new DescriptorException(ex);
        } catch (IllegalAccessException ex) {
          throw new DescriptorException(ex);
        } catch (ClassNotFoundException ex) {
          throw new DescriptorException(ex);
        }
      }
      lifecycleInterceptorClassNames = null;
    }
    if (lifecycleInterceptorBeanNames != null && beanFactory != null) {
      for (String lifecycleInterceptorBeanName : lifecycleInterceptorBeanNames) {
        registerLifecycleInterceptor((ILifecycleInterceptor<?>) beanFactory
            .getBean(lifecycleInterceptorBeanName, ILifecycleInterceptor.class));
      }
      lifecycleInterceptorBeanNames = null;
    }
  }

  private void registerLifecycleInterceptor(
      ILifecycleInterceptor<?> lifecycleInterceptor) {
    if (lifecycleInterceptors == null) {
      lifecycleInterceptors = new ArrayList<ILifecycleInterceptor<?>>();
    }
    lifecycleInterceptors.add(lifecycleInterceptor);
  }

  private synchronized void registerService(Class<?> serviceContract,
      IComponentService service) {
    if (serviceDelegates == null) {
      serviceDelegates = new HashMap<Method, IComponentService>();
      serviceContracts = new HashSet<Class<?>>();
    }
    serviceContracts.add(serviceContract);
    Method[] contractServices = serviceContract.getMethods();
    for (Method serviceMethod : contractServices) {
      serviceDelegates.put(serviceMethod, service);
    }
  }

  /**
   * {@inheritDoc}
   */
  public Integer getPageSize() {
    return pageSize;
  }

  /**
   * Sets the pageSize.
   * 
   * @param pageSize
   *          the pageSize to set.
   */
  public void setPageSize(Integer pageSize) {
    this.pageSize = pageSize;
  }

  /**
   * Sets the serviceDelegateBeanNames.
   * 
   * @param serviceDelegateBeanNames
   *          the serviceDelegateBeanNames to set. They are used to retrieve
   *          delegate instances from the Spring bean factory this descriptor
   *          comes from if any.
   */
  public void setServiceDelegateBeanNames(
      Map<String, String> serviceDelegateBeanNames) {
    this.serviceDelegateBeanNames = StringUtils
        .ensureSpaceFree(serviceDelegateBeanNames);
  }

  /**
   * {@inheritDoc}
   */
  public void setBeanFactory(BeanFactory beanFactory) {
    this.beanFactory = beanFactory;
  }

  /**
   * Sets the lifecycleInterceptorBeanNames.
   * 
   * @param lifecycleInterceptorBeanNames
   *          the lifecycleInterceptorBeanNames to set. They are used to
   *          retrieve interceptor instances from the Spring bean factory this
   *          descriptor comes from if any.
   */
  public void setLifecycleInterceptorBeanNames(
      List<String> lifecycleInterceptorBeanNames) {
    this.lifecycleInterceptorBeanNames = StringUtils
        .ensureSpaceFree(lifecycleInterceptorBeanNames);
  }

  /**
   * Gets the readabilityGates.
   * 
   * @return the readabilityGates.
   */
  public Collection<IGate> getReadabilityGates() {
    Set<IGate> gates = new HashSet<IGate>();
    if (readabilityGates != null) {
      gates.addAll(readabilityGates);
    }
    if (getAncestorDescriptors() != null) {
      for (IComponentDescriptor<?> ancestorDescriptor : getAncestorDescriptors()) {
        gates.addAll(ancestorDescriptor.getReadabilityGates());
      }
    }
    return gates;
  }

  /**
   * Sets the readabilityGates.
   * 
   * @param readabilityGates
   *          the readabilityGates to set.
   */
  public void setReadabilityGates(Collection<IGate> readabilityGates) {
    this.readabilityGates = readabilityGates;
  }

  /**
   * Gets the writabilityGates.
   * 
   * @return the writabilityGates.
   */
  public Collection<IGate> getWritabilityGates() {
    Set<IGate> gates = new HashSet<IGate>();
    if (writabilityGates != null) {
      gates.addAll(writabilityGates);
    }
    if (getAncestorDescriptors() != null) {
      for (IComponentDescriptor<?> ancestorDescriptor : getAncestorDescriptors()) {
        gates.addAll(ancestorDescriptor.getWritabilityGates());
      }
    }
    return gates;
  }

  /**
   * Sets the writabilityGates.
   * 
   * @param writabilityGates
   *          the writabilityGates to set.
   */
  public void setWritabilityGates(Collection<IGate> writabilityGates) {
    this.writabilityGates = writabilityGates;
  }

  /**
   * {@inheritDoc}
   */
  public boolean isReadOnly() {
    return false;
  }

  /**
   * Sets the sqlName.
   * 
   * @param sqlName
   *          the sqlName to set.
   */
  public void setSqlName(String sqlName) {
    this.sqlName = sqlName;
  }

  /**
   * Gets the sqlName.
   * 
   * @return the sqlName.
   */
  public String getSqlName() {
    return sqlName;
  }

}
