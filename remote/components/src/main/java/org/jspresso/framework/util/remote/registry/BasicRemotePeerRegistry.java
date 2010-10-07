/*
 * Copyright (c) 2005-2010 Vincent Vandenschrick. All rights reserved.
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
package org.jspresso.framework.util.remote.registry;

import java.lang.ref.Reference;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.map.AbstractReferenceMap;
import org.apache.commons.collections.map.ReferenceMap;
import org.jspresso.framework.util.automation.IAutomatable;
import org.jspresso.framework.util.remote.IRemotePeer;

/**
 * The basic implementation of a remote peer registry. It is stored by a
 * reference map so that it is memory neutral.
 * 
 * @version $LastChangedRevision$
 * @author Vincent Vandenschrick
 */
public class BasicRemotePeerRegistry implements IRemotePeerRegistry {

  private Map<String, String>              automationBackingStore;
  private Map<String, Integer>             automationIndices;
  private Map<String, IRemotePeer>         backingStore;

  private Set<IRemotePeerRegistryListener> rprListeners;

  /**
   * Constructs a new <code>BasicRemotePeerRegistry</code> instance.
   */
  @SuppressWarnings("unchecked")
  public BasicRemotePeerRegistry() {
    backingStore = new RemotePeerReferenceMap(AbstractReferenceMap.WEAK,
        AbstractReferenceMap.WEAK, true);
    automationBackingStore = new ReferenceMap(AbstractReferenceMap.WEAK,
        AbstractReferenceMap.WEAK, true);
    automationIndices = new HashMap<String, Integer>();
  }

  /**
   * {@inheritDoc}
   */
  public void clear() {
    backingStore.clear();
    automationBackingStore.clear();
    automationIndices.clear();
  }

  /**
   * {@inheritDoc}
   */
  public IRemotePeer getRegistered(String guid) {
    return backingStore.get(guid);
  }

  /**
   * {@inheritDoc}
   */
  public IRemotePeer getRegisteredForAutomationId(String automationId) {
    if (automationId != null) {
      return getRegistered(automationBackingStore.get(automationId));
    }
    return null;
  }

  /**
   * {@inheritDoc}
   */
  public boolean isRegistered(String guid) {
    return backingStore.containsKey(guid);
  }

  /**
   * {@inheritDoc}
   */
  public void register(IRemotePeer remotePeer) {
    backingStore.put(remotePeer.getGuid(), remotePeer);
    if (remotePeer instanceof IAutomatable) {
      String automationId = ((IAutomatable) remotePeer).getAutomationId();
      if (automationId != null) {
        automationBackingStore.put(automationId, remotePeer.getGuid());
      }
    }
    fireRemotePeerAdded(remotePeer);
  }

  /**
   * {@inheritDoc}
   */
  public String registerAutomationId(String automationsSeed, String guid) {
    String seed = automationsSeed;
    if (seed == null) {
      seed = "generic";
    }
    String automationId = computeNextAutomationId(seed);
    automationBackingStore.put(automationId, guid);
    return automationId;
  }

  /**
   * {@inheritDoc}
   */
  public void unregister(String guid) {
    IRemotePeer remotePeer = backingStore.remove(guid);
    if (remotePeer instanceof IAutomatable) {
      String automationId = ((IAutomatable) remotePeer).getAutomationId();
      if (automationId != null) {
        automationBackingStore.remove(automationId);
      }
    }
    fireRemotePeerRemoved(guid);
  }

  private synchronized String computeNextAutomationId(String seed) {
    if (seed == null) {
      return null;
    }
    Integer currentIndex = automationIndices.get(seed);
    int idIndex = 0;
    if (currentIndex != null) {
      idIndex = currentIndex.intValue() + 1;
    }
    automationIndices.put(seed, new Integer(idIndex));
    return new StringBuffer(seed).append("#").append(idIndex).toString();
  }

  class RemotePeerReferenceMap extends ReferenceMap {

    private static final long serialVersionUID = 1494465151770293403L;

    public RemotePeerReferenceMap(int keyType, int valueType,
        boolean purgeValues) {
      super(keyType, valueType, purgeValues);
    }

    @Override
    protected HashEntry createEntry(HashEntry next, int hashCode, Object key,
        Object value) {
      if (value instanceof IRemotePeer) {
        return new RemotePeerReferenceEntry(((IRemotePeer) value).getGuid(),
            this, next, hashCode, key, value);
      }
      return super.createEntry(next, hashCode, key, value);
    }

    @SuppressWarnings("rawtypes")
    @Override
    protected void purge(Reference ref) {
      int hash = ref.hashCode();
      int index = hashIndex(hash, data.length);
      HashEntry entry = data[index];
      if (entry instanceof IRemotePeer) {
        fireRemotePeerRemoved(((IRemotePeer) entry).getGuid());
      }
      super.purge(ref);
    }

    class RemotePeerReferenceEntry extends ReferenceEntry implements
        IRemotePeer {

      private String guid;

      public RemotePeerReferenceEntry(String guid, AbstractReferenceMap parent,
          HashEntry next, int hashCode, Object key, Object value) {
        super(parent, next, hashCode, key, value);
        this.guid = guid;
      }

      /**
       * {@inheritDoc}
       */
      public String getGuid() {
        return guid;
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  public void addRemotePeerRegistryListener(IRemotePeerRegistryListener listener) {
    if (rprListeners == null && listener != null) {
      rprListeners = new LinkedHashSet<IRemotePeerRegistryListener>();
    }
    rprListeners.add(listener);
  }

  /**
   * {@inheritDoc}
   */
  public void removeRemotePeerRegistryListener(
      IRemotePeerRegistryListener listener) {
    if (rprListeners == null || listener == null) {
      return;
    }
    rprListeners.remove(listener);
  }

  /**
   * Notifies the listeners that a remote peer has been added.
   * 
   * @param peer
   *          the added remote peer.
   */
  protected void fireRemotePeerAdded(IRemotePeer peer) {
    if (rprListeners != null) {
      for (IRemotePeerRegistryListener listener : rprListeners) {
        listener.remotePeerAdded(peer);
      }
    }
  }

  /**
   * Notifies the listeners that a remote peer has been removed.
   * 
   * @param guid
   *          the removed remote peer guid.
   */
  protected void fireRemotePeerRemoved(String guid) {
    if (rprListeners != null) {
      for (IRemotePeerRegistryListener listener : rprListeners) {
        listener.remotePeerRemoved(guid);
      }
    }
  }
}
