/*
 * Copyright (c) 2005 Design2see. All rights reserved.
 */
package com.d2s.framework.view;

import com.d2s.framework.binding.IValueConnector;
import com.d2s.framework.view.descriptor.IViewDescriptor;

/**
 * This interface establishes the general contract of a view. Its main role is
 * to provide a pair of (visible peer , connector) used by the framework.
 * <p>
 * Copyright 2005 Design2See. All rights reserved.
 * <p>
 * 
 * @version $LastChangedRevision$
 * @author Vincent Vandenschrick
 * @param <E>
 *          The root class of the view peers.
 */
public interface IView<E> extends IViewProvider {

  /**
   * Gets the view connector of this view. This connector is built connected to
   * the view peer.
   * 
   * @return the view connector.
   */
  IValueConnector getConnector();

  /**
   * Gets the descriptor of this view.
   * 
   * @return the view descriptor.
   */
  IViewDescriptor getDescriptor();

  /**
   * Gets the parent view if any or null.
   * 
   * @return the parent view.
   */
  IView<E> getParent();

  /**
   * Gets the view peer of this view.
   * 
   * @return the view peer.
   */
  E getPeer();

  /**
   * Sets the view connector of this view. This connector is built connected to
   * the view peer.
   * 
   * @param connector
   *          the view connector.
   */
  void setConnector(IValueConnector connector);

  /**
   * Sets the parent view if any or null.
   * 
   * @param parent
   *          the parent view.
   */
  void setParent(IView<E> parent);

  /**
   * Sets the view peer of this view.
   * 
   * @param peer
   *          the view peer.
   */
  void setPeer(E peer);
}
