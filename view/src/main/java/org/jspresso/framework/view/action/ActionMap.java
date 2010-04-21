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
package org.jspresso.framework.view.action;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jspresso.framework.util.gui.ERenderingOptions;

/**
 * This class implements a hierachical structure for holding action maps. An
 * action map is a map of action sets keyed by their grouping goal (like 'EDIT',
 * 'VIEW', ...).
 * 
 * @version $LastChangedRevision$
 * @author Vincent Vandenschrick
 */
public class ActionMap {

  private List<ActionList>  actionLists;
  private List<ActionMap>   parentActionMaps;

  private ERenderingOptions renderingOptions;

  private static void completeActionMap(
      Map<String, ActionList> bufferActionMap, List<ActionList> actionLists) {
    if (actionLists != null) {
      Map<String, ActionList> mapOfActionLists = new LinkedHashMap<String, ActionList>();
      for (ActionList al : actionLists) {
        mapOfActionLists.put(al.getName(), al);
      }
      for (Map.Entry<String, ActionList> actionListEntry : mapOfActionLists
          .entrySet()) {
        ActionList bufferActionList = bufferActionMap.get(actionListEntry
            .getKey());
        if (bufferActionList == null) {
          bufferActionList = actionListEntry.getValue().clone();
          bufferActionMap.put(actionListEntry.getKey(), bufferActionList);
        } else {
          for (IDisplayableAction localAction : actionListEntry.getValue()
              .getActions()) {
            int existingIndex = bufferActionList.getActions().indexOf(
                localAction);
            if (existingIndex >= 0) {
              bufferActionList.getActions().set(existingIndex, localAction);
            } else {
              bufferActionList.getActions().add(localAction);
            }
          }
        }
      }
    }
  }

  /**
   * Gets the list of action sets composing the parent action maps with the
   * local one.
   * 
   * @return the actions list.
   */
  public List<ActionList> getActionLists() {
    Map<String, ActionList> buffer = new LinkedHashMap<String, ActionList>();
    if (parentActionMaps != null) {
      for (ActionMap parentActionMap : parentActionMaps) {
        completeActionMap(buffer, parentActionMap.getActionLists());
      }
    }
    if (actionLists != null) {
      completeActionMap(buffer, actionLists);
    }
    return new ArrayList<ActionList>(buffer.values());
  }

  /**
   * Gets the renderingOptions.
   * 
   * @return the renderingOptions.
   */
  public ERenderingOptions getRenderingOptions() {
    return renderingOptions;
  }

  /**
   * Sets the action lists list.
   * 
   * @param actionLists
   *          the action lists list to set.
   */
  public void setActionLists(List<ActionList> actionLists) {
    this.actionLists = actionLists;
  }

  /**
   * Sets the parentActionMaps.
   * 
   * @param parentActionMaps
   *          the parentActionMaps to set.
   */
  public void setParentActionMaps(List<ActionMap> parentActionMaps) {
    this.parentActionMaps = parentActionMaps;
  }

  /**
   * Sets the renderingOptions.
   * 
   * @param renderingOptions
   *          the renderingOptions to set.
   */
  public void setRenderingOptions(ERenderingOptions renderingOptions) {
    this.renderingOptions = renderingOptions;
  }
}
