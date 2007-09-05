/*
 * Copyright (c) 2005 Design2see. All rights reserved.
 */
package com.d2s.framework.binding.ulc;

import java.util.List;
import java.util.Map;

import com.d2s.framework.binding.ICollectionConnector;
import com.d2s.framework.binding.ICompositeValueConnector;
import com.d2s.framework.binding.IValueConnector;
import com.ulcjava.base.application.ULCTableTree;
import com.ulcjava.base.application.event.TreeModelEvent;
import com.ulcjava.base.application.event.serializable.ITreeModelListener;
import com.ulcjava.base.application.tabletree.AbstractTableTreeModel;

/**
 * This table tree model maps a connector hierarchy.
 * <p>
 * Copyright 2005 Design2See. All rights reserved.
 * <p>
 *
 * @version $LastChangedRevision$
 * @author Vincent Vandenschrick
 */
public class ConnectorHierarchyTableTreeModel extends AbstractTableTreeModel {

  private final class TreeModelListenerAdapter implements ITreeModelListener {

    private static final long serialVersionUID = 5507166182742867920L;

    /**
     * {@inheritDoc}
     */
    public void treeNodesChanged(TreeModelEvent event) {
      nodesChanged(event.getTreePath(), event.getChildIndices());
    }

    /**
     * {@inheritDoc}
     */
    public void treeNodesInserted(TreeModelEvent event) {
      nodesWereInserted(event.getTreePath(), event.getChildIndices());
    }

    /**
     * {@inheritDoc}
     */
    public void treeNodesRemoved(TreeModelEvent event) {
      nodesWereRemoved(event.getTreePath(), event.getChildIndices(), event
          .getChildren());
    }

    /**
     * {@inheritDoc}
     */
    public void treeStructureChanged(TreeModelEvent event) {
      nodeStructureChanged(event.getTreePath());
    }
  }

  private static final long           serialVersionUID = -345481127092324072L;
  private Map<String, Class>          columnClassesByIds;
  private List<String>                columnConnectorKeys;

  private ConnectorHierarchyTreeModel treeModelDelegate;

  /**
   * Constructs a new <code>ConnectorHierarchyTableTreeModel</code> instance.
   *
   * @param rootConnector
   *          the connector being the root node of the tree.
   * @param tree
   *          the tree to which this model wiil be attached to. It will be used
   *          for the model to bea notified of expansions so that it can
   *          lazy-load the tree hierarchy.
   */
  public ConnectorHierarchyTableTreeModel(
      ICompositeValueConnector rootConnector, @SuppressWarnings("unused")
      ULCTableTree tree) {
    treeModelDelegate = new ConnectorHierarchyTreeModel(rootConnector, null);
    // FIXME on extende tabletree
    // tree.addTreeExpansionListener(treeModelDelegate);
    treeModelDelegate.addTreeModelListener(new TreeModelListenerAdapter());
  }

  /**
   * {@inheritDoc}
   */
  public Object getChild(Object parent, int index) {
    return treeModelDelegate.getChild(parent, index);
  }

  /**
   * {@inheritDoc}
   */
  public int getChildCount(Object node) {
    return treeModelDelegate.getChildCount(node);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Class<?> getColumnClass(int columnIndex) {
    if (columnClassesByIds != null) {
      return columnClassesByIds.get(columnConnectorKeys.get(columnIndex));
    }
    return super.getColumnClass(columnIndex);
  }

  /**
   * {@inheritDoc}
   */
  public int getColumnCount() {
    return columnConnectorKeys.size();
  }

  /**
   * {@inheritDoc}
   */
  public int getIndexOfChild(Object parent, Object child) {
    return treeModelDelegate.getIndexOfChild(parent, child);
  }

  /**
   * {@inheritDoc}
   */
  public Object getRoot() {
    return treeModelDelegate.getRoot();
  }

  /**
   * {@inheritDoc}
   */
  public Object getValueAt(Object node, int column) {
    if (node instanceof ICompositeValueConnector
        && !(node instanceof ICollectionConnector)) {
      IValueConnector cellConnector = ((ICompositeValueConnector) node)
          .getChildConnector(columnConnectorKeys.get(column));
      if (cellConnector instanceof ICompositeValueConnector) {
        return cellConnector.toString();
      } else if (cellConnector.getConnectorValue() instanceof byte[]) {
        return null;
      }
      return cellConnector.getConnectorValue();
    }
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isCellEditable(Object node, int columnIndex) {
    if (node instanceof ICompositeValueConnector
        && !(node instanceof ICollectionConnector)) {
      ((ICompositeValueConnector) node).getChildConnector(columnConnectorKeys
          .get(columnIndex));
    }
    return false;
  }

  /**
   * {@inheritDoc}
   */
  public boolean isLeaf(Object node) {
    return treeModelDelegate.isLeaf(node);
  }

  /**
   * Sets the columnClassesByIds.
   *
   * @param columnClassesByIds
   *          the columnClassesByIds to set.
   */
  public void setColumnClassesByIds(Map<String, Class> columnClassesByIds) {
    this.columnClassesByIds = columnClassesByIds;
  }

  /**
   * Sets the columnConnectorKeys.
   *
   * @param columnConnectorKeys
   *          the columnConnectorKeys to set.
   */
  public void setColumnConnectorKeys(List<String> columnConnectorKeys) {
    this.columnConnectorKeys = columnConnectorKeys;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setValueAt(Object value, Object node, int column) {
    if (node instanceof ICompositeValueConnector
        && !(node instanceof ICollectionConnector)) {
      IValueConnector cellConnector = ((ICompositeValueConnector) node)
          .getChildConnector(columnConnectorKeys.get(column));
      if (cellConnector instanceof ICompositeValueConnector) {
        if (!(value instanceof String)) {
          // this cellValue is the real one, not the string representation
          // comming
          // back from the client side.
          cellConnector.setConnectorValue(value);
        }
      } else {
        if ("".equals(value)) {
          cellConnector.setConnectorValue(null);
        } else {
          cellConnector.setConnectorValue(value);
        }
      }
    }
  }
}
