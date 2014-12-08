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
package org.jspresso.framework.model.persistence.mongo;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import org.springframework.data.mapping.model.FieldNamingStrategy;
import org.springframework.data.mapping.model.SimpleTypeHolder;
import org.springframework.data.mongodb.core.mapping.CachingMongoPersistentProperty;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity;

import org.jspresso.framework.model.entity.IEntity;

/**
 * Jspresso specialized Mongo persistent property.
 *
 * @author Vincent Vandenschrick
 * @version $LastChangedRevision$
 */
public class JspressoMongoPersistentProperty extends CachingMongoPersistentProperty {

  private static final org.springframework.data.mongodb.core.mapping.DBRef DB_REF = new DBRef() {
    @Override
    public Class<? extends Annotation> annotationType() {
      return DBRef.class;
    }
    @Override
    public String db() {
      return null;
    }
    @Override
    public boolean lazy() {
      return true;
    }
  };

  /**
   * Creates a new {@link org.springframework.data.mongodb.core.mapping.CachingMongoPersistentProperty}.
   *
   * @param field the field
   * @param propertyDescriptor the property descriptor
   * @param owner the owner
   * @param simpleTypeHolder the simple type holder
   * @param fieldNamingStrategy the field naming strategy
   */
  public JspressoMongoPersistentProperty(Field field, PropertyDescriptor propertyDescriptor,
                                         MongoPersistentEntity<?> owner, SimpleTypeHolder simpleTypeHolder,
                                         FieldNamingStrategy fieldNamingStrategy) {
    super(field, propertyDescriptor, owner, simpleTypeHolder, fieldNamingStrategy);
  }

  @Override
  public DBRef getDBRef() {
    if (IEntity.class.isAssignableFrom(getType())) {
      return DB_REF;
    }
    return super.getDBRef();
  }

  @Override
  public boolean isDbReference() {
    if (IEntity.class.isAssignableFrom(getType())) {
      return true;
    }
    return super.isDbReference();
  }
}
