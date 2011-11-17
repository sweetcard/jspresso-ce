/*
 * Copyright (c) 2005-2011 Vincent Vandenschrick. All rights reserved.
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
package org.jspresso.framework.binding;

import java.util.Collection;

/**
 * This is the interface implemented by connectors nesting other connectors
 * (composite).
 * 
 * @version $LastChangedRevision$
 * @author Vincent Vandenschrick
 */
public interface ICompositeValueConnector extends IValueConnector {

  /**
   * Adds a new child connector to this composite.
   * 
   * @param storageKey
   *          the key under which the child connector will be stored /
   *          retrieved.
   * @param childConnector
   *          the added connector.
   */
  void addChildConnector(String storageKey, IValueConnector childConnector);

  /**
   * Removes a child connector from this composite.
   * 
   * @param storageKey
   *          the key to remove the child connector for.
   */
  void removeChildConnector(String storageKey);

  /**
   * Are the children connectors readable ?
   * 
   * @return true if readable.
   */
  boolean areChildrenReadable();

  /**
   * Are the children connectors writable ?
   * 
   * @return true if writable.
   */
  boolean areChildrenWritable();

  /**
   * Clones this connector.
   * 
   * @return the connector's clone.
   */
  @Override
  ICompositeValueConnector clone();

  /**
   * Clones this connector.
   * 
   * @param newConnectorId
   *          the identifier of the clone connector
   * @return the connector's clone.
   */
  @Override
  ICompositeValueConnector clone(String newConnectorId);

  /**
   * Gets a child connector based on its identifier. It should directly delegate
   * to the <code>IConnectorMap</code>.
   * 
   * @param connectorKey
   *          The key indexing the looked-up connector
   * @return The retrieved connector or null if none exists
   */
  IValueConnector getChildConnector(String connectorKey);

  /**
   * Gets the number of child connectors contained in this composite connector.
   * 
   * @return the element connectors count.
   */
  int getChildConnectorCount();

  /**
   * Gets the collection of connectors hosted by this connector map. It should
   * directly delegate to the <code>IConnectorMap</code>.
   * 
   * @return The collection of child connector in this connector.
   */
  Collection<String> getChildConnectorKeys();
}
