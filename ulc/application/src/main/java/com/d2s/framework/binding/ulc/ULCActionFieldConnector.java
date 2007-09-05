/*
 * Copyright (c) 2005 Design2see. All rights reserved.
 */
package com.d2s.framework.binding.ulc;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.apache.commons.lang.StringUtils;

import com.d2s.framework.binding.ConnectorValueChangeEvent;
import com.d2s.framework.binding.IConnectorValueChangeListener;
import com.d2s.framework.gui.ulc.components.server.ULCActionField;

/**
 * ULCActionFieldConnector connector.
 * <p>
 * Copyright 2005 Design2See. All rights reserved.
 * <p>
 * 
 * @version $LastChangedRevision$
 * @author Vincent Vandenschrick
 */
public class ULCActionFieldConnector extends
    ULCComponentConnector<ULCActionField> {

  /**
   * Constructs a new <code>ULCActionFieldConnector</code> instance.
   * 
   * @param id
   *          the id of the connector.
   * @param actionField
   *          the connected ULCActionField.
   */
  public ULCActionFieldConnector(String id, ULCActionField actionField) {
    super(id, actionField);
    if (!getConnectedULCComponent().isShowingTextField()) {
      addConnectorValueChangeListener(new IConnectorValueChangeListener() {

        public void connectorValueChange(ConnectorValueChangeEvent evt) {
          if (evt.getNewValue() instanceof byte[]) {
            getConnectedULCComponent().setDecorated(
                ((byte[]) evt.getNewValue()).length > 0);
          } else {
            getConnectedULCComponent().setDecorated(evt.getNewValue() != null);
          }
        }
      });
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void bindULCComponent() {
    getConnectedULCComponent().addPropertyChangeListener("actionText",
        new PropertyChangeListener() {

          /**
           * {@inheritDoc}
           */
          public void propertyChange(@SuppressWarnings("unused")
          PropertyChangeEvent evt) {
            performActionIfNeeded();
          }
        });

    getConnectedULCComponent().addPropertyChangeListener("value",
        new PropertyChangeListener() {

          /**
           * {@inheritDoc}
           */
          public void propertyChange(@SuppressWarnings("unused")
          PropertyChangeEvent evt) {
            fireConnectorValueChange();
          }
        });
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Object getConnecteeValue() {
    return getConnectedULCComponent().getValue();
  }

  /**
   * Performs the action field action if the action field is not synchronized.
   */
  private void performActionIfNeeded() {
    if (!getConnectedULCComponent().isSynchronized()) {
      if (StringUtils.isEmpty(getConnectedULCComponent().getActionText())) {
        getConnectedULCComponent().setValue(null);
      } else {
        getConnectedULCComponent().performAction(0);
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void setConnecteeValue(Object aValue) {
    getConnectedULCComponent().setValue(aValue);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void updateState() {
    super.updateState();
    getConnectedULCComponent().setEditable(isWritable());
  }
}
