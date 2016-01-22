/*
 * Copyright (c) 2005-2016 Vincent Vandenschrick. All rights reserved.
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
package org.jspresso.framework.binding.remote;

import java.util.ArrayList;

import org.jspresso.framework.binding.ICollectionConnector;
import org.jspresso.framework.binding.basic.BasicCollectionConnectorProvider;
import org.jspresso.framework.gui.remote.RIcon;
import org.jspresso.framework.state.remote.IRemoteStateOwner;
import org.jspresso.framework.state.remote.IRemoteStateValueMapper;
import org.jspresso.framework.state.remote.RemoteCompositeValueState;
import org.jspresso.framework.util.automation.IPermIdSource;
import org.jspresso.framework.util.remote.IRemotePeer;
import org.jspresso.framework.util.resources.server.ResourceProviderServlet;

/**
 * The server peer of a remote collection connector provider.
 *
 * @author Vincent Vandenschrick
 */
public class RemoteCollectionConnectorProvider extends
    BasicCollectionConnectorProvider implements IRemotePeer, IRemoteStateOwner,
    IPermIdSource {

  private       String                    permId;
  private final RemoteConnectorFactory    connectorFactory;
  private       String                    guid;
  private       IRemoteStateValueMapper   remoteStateValueMapper;
  private       RemoteCompositeValueState state;

  /**
   * Constructs a new {@code RemoteCollectionConnectorProvider} instance.
   *
   * @param id
   *          the connector id.
   * @param connectorFactory
   *          the remote connector factory.
   */
  public RemoteCollectionConnectorProvider(String id, RemoteConnectorFactory connectorFactory) {
    super(id);
    this.guid = connectorFactory.generateGUID();
    this.connectorFactory = connectorFactory;
    connectorFactory.register(this);
  }

  /**
   * Returns the actual connector value.
   * <p>
   * {@inheritDoc}
   */
  @Override
  public Object actualValue() {
    return getConnectorValue();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public RemoteCollectionConnectorProvider clone() {
    return clone(getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public RemoteCollectionConnectorProvider clone(String newConnectorId) {
    RemoteCollectionConnectorProvider clonedConnector = (RemoteCollectionConnectorProvider) super.clone(newConnectorId);
    clonedConnector.guid = connectorFactory.generateGUID();
    clonedConnector.state = null;
    connectorFactory.attachListeners(clonedConnector);
    connectorFactory.register(clonedConnector);
    return clonedConnector;
  }

  /**
   * Gets the permId.
   *
   * @return the permId.
   */
  @Override
  public String getPermId() {
    if (permId != null) {
      return permId;
    }
    return getId();
  }

  /**
   * Gets the guid.
   *
   * @return the guid.
   */
  @Override
  public String getGuid() {
    return guid;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public RemoteCompositeValueState getState() {
    if (state == null) {
      state = createState();
      synchRemoteState();
    }
    return state;
  }

  /**
   * Sets the permanent identifier to this application element. Permanent
   * identifiers are used by different framework parts, like dynamic security or
   * record/replay controllers to uniquely identify an application element.
   * Permanent identifiers are generated by the SJS build based on the element
   * id but must be explicitly set if Spring XML is used.
   *
   * @param permId
   *          the permId to set.
   */
  @Override
  public void setPermId(String permId) {
    this.permId = permId;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void synchRemoteState() {
    RemoteCompositeValueState currentState = getState();
    currentState.setValue(getDisplayValue());
    currentState.setReadable(isReadable());
    currentState.setWritable(isWritable());
    currentState.setDescription(getDisplayDescription());
    currentState.setIconImageUrl(ResourceProviderServlet
        .computeImageResourceDownloadUrl(getDisplayIcon(), RIcon.DEFAULT_DIM));
  }

  /**
   * Creates a new state instance representing this connector.
   *
   * @return the newly created state.
   */
  protected RemoteCompositeValueState createState() {
    RemoteCompositeValueState createdState = connectorFactory
        .createRemoteCompositeValueState(getGuid(), getPermId());
    ICollectionConnector collectionConnector = getCollectionConnector();
    if (collectionConnector instanceof RemoteCollectionConnector) {
      createdState.setChildren(new ArrayList<>(
          ((RemoteCollectionConnector) collectionConnector).getState()
              .getChildren()));
    }
    return createdState;
  }

  /**
   * Sets the remoteStateValueMapper.
   *
   * @param remoteStateValueMapper
   *          the remoteStateValueMapper to set.
   */
  @Override
  public void setRemoteStateValueMapper(
      IRemoteStateValueMapper remoteStateValueMapper) {
    this.remoteStateValueMapper = remoteStateValueMapper;
  }

  /**
   * Gets the remoteStateValueMapper.
   *
   * @return the remoteStateValueMapper.
   */
  protected IRemoteStateValueMapper getRemoteStateValueMapper() {
    return remoteStateValueMapper;
  }

  /**
   * Gets the value that has to be set to the remote state when updating it. It
   * defaults to the connector value but the  is given a chance here
   * to mutate the actual object returned. This allows for changing the type of
   * objects actually exchanged with the remote frontend peer.
   *
   * @return the value that has to be set to the remote state when updating it.
   */
  protected Object getValueForState() {
    Object valueForState = getConnectorValue();
    if (getRemoteStateValueMapper() != null) {
      valueForState = getRemoteStateValueMapper().getValueForState(getState(), valueForState);
    }
    return valueForState;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setValueFromState(Object stateValue) {
    Object valueFromState;
    if (getRemoteStateValueMapper() != null) {
      valueFromState = getRemoteStateValueMapper()
          .getValueFromState(getState(), stateValue);
    } else {
      valueFromState = stateValue;
    }
    setConnectorValue(valueFromState);
    // There are rare cases (e.g. due to interceptSetter that resets the command value to the connector
    // actual state), when the connector and the state are not synced.
    synchRemoteState();
  }
}
