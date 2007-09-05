/*
 * Copyright (c) 2005 Design2see. All rights reserved.
 */
package com.d2s.framework.application.frontend.action.wings;

import java.util.Map;

import javax.swing.Action;

import org.wings.SComponent;
import org.wings.SContainer;
import org.wings.SDialog;
import org.wings.SIcon;

import com.d2s.framework.action.ActionContextConstants;
import com.d2s.framework.application.frontend.action.ActionWrapper;
import com.d2s.framework.util.wings.WingsUtil;

/**
 * This class serves as base class for wings actions. It provides accessors on
 * commonly used artifacts.
 * <p>
 * Copyright 2005 Design2See. All rights reserved.
 * <p>
 * 
 * @version $LastChangedRevision$
 * @author Vincent Vandenschrick
 */
public abstract class AbstractWingsAction extends
    ActionWrapper<SComponent, SIcon, Action> {

  /**
   * If the ancestor of the action widget is a dialog, dispose it.
   * 
   * @param context
   *          the action context.
   */
  protected void closeDialog(Map<String, Object> context) {
    SContainer actionWindow = WingsUtil
        .getVisibleWindow(getActionWidget(context));
    if (actionWindow instanceof SDialog) {
      ((SDialog) actionWindow).dispose();
    }
  }

  /**
   * Retrieves the widget which triggered the action from the action context.
   * 
   * @param context
   *          the action context.
   * @return the widget which triggered the action.
   */
  public SComponent getActionWidget(Map<String, Object> context) {
    return (SComponent) context.get(ActionContextConstants.ACTION_WIDGET);
  }

  /**
   * Retrieves the widget this action was triggered from. It may serve to
   * determine the root window or dialog for instance. It uses a well-known
   * action context key which is :
   * <li> <code>ActionContextConstants.SOURCE_COMPONENT</code>.
   * 
   * @param context
   *          the action context.
   * @return the source widget this action was triggered from.
   */
  public SComponent getSourceComponent(Map<String, Object> context) {
    return (SComponent) context.get(ActionContextConstants.SOURCE_COMPONENT);
  }
}
