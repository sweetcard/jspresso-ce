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
package org.jspresso.framework.util.bean;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeListenerProxy;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This property change support prevents from adding twice the same property
 * change listener.
 * 
 * @version $LastChangedRevision$
 * @author Vincent Vandenschrick
 */
public class SinglePropertyChangeSupport extends PropertyChangeSupport {

  private static final long serialVersionUID = -3547472625502417905L;

  /**
   * Constructs a new <code>SinglePropertyChangeSupport</code> instance.
   * 
   * @param sourceBean
   *          the source bean.
   */
  public SinglePropertyChangeSupport(Object sourceBean) {
    super(sourceBean);
  }

  /**
   * Checks listener uniqueness before performing.
   * <p>
   * {@inheritDoc}
   */
  @Override
  public synchronized void addPropertyChangeListener(
      PropertyChangeListener listener) {
    if (checkUniqueness(null, listener)) {
      super.addPropertyChangeListener(listener);
    }
  }

  /**
   * Checks listener uniqueness before performing.
   * <p>
   * {@inheritDoc}
   */
  @Override
  public synchronized void addPropertyChangeListener(String propertyName,
      PropertyChangeListener listener) {
    if (checkUniqueness(propertyName, listener)) {
      super.addPropertyChangeListener(propertyName, listener);
    }
  }

  private boolean checkUniqueness(String propertyName,
      PropertyChangeListener listener) {
    List<PropertyChangeListener> containedListeners = new ArrayList<PropertyChangeListener>();
    if (propertyName == null) {
      for (PropertyChangeListener pcl : getPropertyChangeListeners()) {
        if (!(pcl instanceof PropertyChangeListenerProxy)) {
          containedListeners.add(pcl);
        }
      }
    } else {
      containedListeners.addAll(Arrays
          .asList(getPropertyChangeListeners(propertyName)));
    }
    return !containedListeners.contains(listener);
  }
}
