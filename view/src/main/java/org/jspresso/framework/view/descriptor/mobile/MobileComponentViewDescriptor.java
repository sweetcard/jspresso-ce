/*
 * Copyright (c) 2005-2014 Vincent Vandenschrick. All rights reserved.
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
package org.jspresso.framework.view.descriptor.mobile;

import java.util.List;

import org.jspresso.framework.view.descriptor.basic.AbstractComponentViewDescriptor;
import org.jspresso.framework.view.descriptor.EHorizontalPosition;

/**
 * A composite view descriptor that aggregates mobile views on a single page.
 *
 * @author Vincent Vandenschrick
 * @version $LastChangedRevision$
 */
public class MobileComponentViewDescriptor extends AbstractComponentViewDescriptor
    implements IMobilePageSectionViewDescriptor {

  /**
   * Always 1 in mobile environment.
   * <p>
   * {@inheritDoc}
   */
  @Override
  protected Integer getPropertyWidth(String propertyName) {
    return 1;
  }

  /**
   * Always null in mobile environment.
   * <p>
   * {@inheritDoc}
   */
  @Override
  protected List<String> computeDefaultRenderedChildProperties(String propertyName) {
    return null;
  }

  /**
   * Always 1 in mobile environment.
   * <p>
   * {@inheritDoc}
   */
  @Override
  public int getColumnCount() {
    return 1;
  }

  /**
   * Always {@code EHorizontalPosition.LEFT} in mobile environment.
   * <p>
   * {@inheritDoc}
   */
  @Override
  public EHorizontalPosition getLabelsHorizontalPosition() {
    return EHorizontalPosition.LEFT;
  }

  /**
   * Always {@code true} in mobile environment.
   * <p>
   * {@inheritDoc}
   */
  @Override
  public boolean isScrollable() {
    return true;
  }

  /**
   * Always {@code true} in mobile environment.
   * <p>
   * {@inheritDoc}
   */
  @Override
  public boolean isVerticallyScrollable() {
    return true;
  }

  /**
   * Always {@code false} in mobile environment.
   * <p>
   * {@inheritDoc}
   */
  @Override
  public boolean isHorizontallyScrollable() {
    return false;
  }
}
