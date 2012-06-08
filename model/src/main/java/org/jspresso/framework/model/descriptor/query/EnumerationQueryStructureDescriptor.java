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
package org.jspresso.framework.model.descriptor.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jspresso.framework.model.component.query.EnumValueQueryStructure;
import org.jspresso.framework.model.descriptor.IEnumerationPropertyDescriptor;
import org.jspresso.framework.model.descriptor.IPropertyDescriptor;
import org.jspresso.framework.model.descriptor.basic.AbstractEnumerationPropertyDescriptor;
import org.jspresso.framework.model.descriptor.basic.BasicBooleanPropertyDescriptor;
import org.jspresso.framework.model.descriptor.basic.BasicCollectionPropertyDescriptor;
import org.jspresso.framework.model.descriptor.basic.BasicComponentDescriptor;
import org.jspresso.framework.model.descriptor.basic.BasicPropertyDescriptor;
import org.jspresso.framework.model.descriptor.basic.BasicSetDescriptor;

/**
 * A query structure used to implement enumeration disjunctions in filters.
 * 
 * @version $LastChangedRevision$
 * @author Vincent Vandenschrick
 */
public class EnumerationQueryStructureDescriptor extends BasicCollectionPropertyDescriptor<EnumValueQueryStructure> {

  /**
   * <code>SELECTED</code> value.
   */
  public static final String             SELECTED = "selected";
  /**
   * <code>VALUE</code> value.
   */
  public static final String             VALUE    = "value";

  private IEnumerationPropertyDescriptor sourceDescriptor;

  /**
   * Constructs a new <code>EnumerationQueryStructureDescriptor</code> instance.
   * 
   * @param sourceDescriptor
   *          the actual enumeration property descriptor to wrap.
   */
  public EnumerationQueryStructureDescriptor(AbstractEnumerationPropertyDescriptor sourceDescriptor) {
    super();
    this.sourceDescriptor = sourceDescriptor;
    setName(sourceDescriptor.getName());
    setI18nNameKey(sourceDescriptor.getI18nNameKey());
    setDescription(sourceDescriptor.getDescription());

    BasicComponentDescriptor<EnumValueQueryStructure> elementDescriptor = new BasicComponentDescriptor<EnumValueQueryStructure>(
        EnumValueQueryStructure.class.getName());

    BasicBooleanPropertyDescriptor selectedPropertyDescriptor = new BasicBooleanPropertyDescriptor();
    selectedPropertyDescriptor.setName(SELECTED);
    selectedPropertyDescriptor.setI18nNameKey("enumValue.selected");
    BasicPropertyDescriptor valuePropertyDescriptor = sourceDescriptor.clone();
    valuePropertyDescriptor.setName(VALUE);
    if (sourceDescriptor.getI18nNameKey() != null) {
      valuePropertyDescriptor.setI18nNameKey(sourceDescriptor.getI18nNameKey());
    } else {
      valuePropertyDescriptor.setI18nNameKey(sourceDescriptor.getName());
    }

    List<IPropertyDescriptor> propertyDescriptors = new ArrayList<IPropertyDescriptor>();
    propertyDescriptors.add(selectedPropertyDescriptor);
    propertyDescriptors.add(valuePropertyDescriptor);
    elementDescriptor.setPropertyDescriptors(propertyDescriptors);

    elementDescriptor.setRenderedProperties(Arrays.asList(SELECTED, VALUE));

    BasicSetDescriptor<EnumValueQueryStructure> referencedDescriptor = new BasicSetDescriptor<EnumValueQueryStructure>();
    referencedDescriptor.setElementDescriptor(elementDescriptor);

    setReferencedDescriptor(referencedDescriptor);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public BasicCollectionPropertyDescriptor<EnumValueQueryStructure> createQueryDescriptor() {
    return this;
  }

  /**
   * Gets the sourceDescriptor.
   * 
   * @return the sourceDescriptor.
   */
  public IEnumerationPropertyDescriptor getSourceDescriptor() {
    return sourceDescriptor;
  }
}