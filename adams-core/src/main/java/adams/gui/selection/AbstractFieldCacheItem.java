/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * AbstractFieldCacheItem.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.selection;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import adams.data.report.AbstractField;
import adams.data.report.DataType;
import adams.data.report.FieldType;
import adams.db.AbstractDatabaseConnection;
import adams.db.DatabaseConnectionProvider;
import adams.db.FieldProvider;
import adams.gui.event.FieldCacheUpdateEvent;
import adams.gui.event.FieldCacheUpdateListener;

/**
 * For caching fields per database connection.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractFieldCacheItem
  implements Serializable, DatabaseConnectionProvider {

  /** for serialization. */
  private static final long serialVersionUID = 6989601817204575826L;

  /** the field provider to use. */
  protected FieldProvider m_Provider;

  /** the actual cache. */
  protected Hashtable<FieldType,Vector<AbstractField>> m_Values;

  /** the listeners. */
  protected Hashtable<FieldType,HashSet<FieldCacheUpdateListener>> m_CacheListeners;

  /**
   * Initializes the cache.
   *
   * @param provider	the field provider
   */
  public AbstractFieldCacheItem(FieldProvider provider) {
    super();

    m_Provider       = provider;
    m_Values         = null;
    m_CacheListeners = new Hashtable<FieldType,HashSet<FieldCacheUpdateListener>>();
    for (FieldType type: FieldType.values())
      m_CacheListeners.put(type, new HashSet<FieldCacheUpdateListener>());
  }

  /**
   * Returns the underlying field provider.
   *
   * @return		the field provider
   */
  public FieldProvider getFieldProvider() {
    return m_Provider;
  }

  /**
   * Returns the database connection of this cache item.
   *
   * @return		the database connection
   */
  public AbstractDatabaseConnection getDatabaseConnection() {
    return m_Provider.getDatabaseConnection();
  }

  /**
   * Clears the cache.
   */
  public void clear() {
    if (m_Values != null)
      m_Values.clear();
    m_Values = null;
  }

  /**
   * Checks whether the cache is initialized.
   *
   * @return		true if initialized
   */
  public boolean isInitialized() {
    return (m_Values != null);
  }

  /**
   * Returns the cached values.
   *
   * @return		the cached values
   */
  public Hashtable<FieldType,Vector<AbstractField>> getValues() {
    try {
      initCache();
    }
    catch (Exception e) {
      m_Values = newCache();
      System.err.println("Failed to initialized field cache for " + m_Provider.getDatabaseConnection().toStringShort() + ":");
      e.printStackTrace();
    }

    return m_Values;
  }

  /**
   * Returns the cached values for the specified field type.
   *
   * @param fieldType	the field type to get the values for
   * @return		the cached values
   */
  public Vector<AbstractField> getValues(FieldType fieldType) {
    return getValues().get(fieldType);
  }

  /**
   * Returns the cached values for the specified field/data type.
   *
   * @param fieldType	the field type to get the values for
   * @param dataType	the data type to get the values for
   * @return		the cached values
   */
  public Vector<AbstractField> getValues(FieldType fieldType, DataType dataType) {
    Vector<AbstractField>	result;
    Vector<AbstractField>	values;

    result = new Vector<AbstractField>();
    values = getValues(fieldType);

    for (AbstractField field: values) {
      if (field.getDataType() == dataType)
	result.add(field);
    }

    return result;
  }

  /**
   * Returns an initialized, but empty cache.
   *
   * @return		the empty cache
   */
  protected Hashtable<FieldType,Vector<AbstractField>> newCache() {
    Hashtable<FieldType,Vector<AbstractField>>	result;

    result = new Hashtable<FieldType,Vector<AbstractField>>();
    for (FieldType type: FieldType.values())
      result.put(type, new Vector<AbstractField>());

    return result;
  }

  /**
   * Creates a new field.
   *
   * @param field	the field to transform
   * @return		the transformed field
   */
  protected abstract AbstractField newField(AbstractField field);

  /**
   * Creates a new suffix field.
   *
   * @param field	the field to transform
   * @return		the suffix field
   */
  protected abstract AbstractField newSuffixField(AbstractField field);

  /**
   * Creates a new prefix field.
   *
   * @param field	the field to transform
   * @return		the prefix field
   */
  protected abstract AbstractField newPrefixField(AbstractField field);

  /**
   * Initializes the cache, if necessary (i.e., m_Cache is null). Notifies
   * all listeners about the update.
   */
  protected void initCache() {
    Iterator<FieldCacheUpdateListener>	iter;
    Vector<AbstractField>		fields;
    int					i;
    AbstractField			field;
    FieldCacheUpdateEvent		event;

    if (m_Values == null) {
      if ((getFieldProvider() == null) || !getFieldProvider().getDatabaseConnection().isConnected())
	fields = new Vector<AbstractField>();
      else
	fields = getFieldProvider().getFields();
      m_Values = newCache();
      for (i = 0; i < fields.size(); i++) {
	// FIELD
	field = newField(fields.get(i));
	m_Values.get(FieldType.FIELD).add(field);

	if (fields.get(i).isCompound()) {
	  // PREFIX_FIELD
	  field = newPrefixField(fields.get(i));
	  if (!m_Values.get(FieldType.PREFIX_FIELD).contains(field))
	    m_Values.get(FieldType.PREFIX_FIELD).add(field);
	  // SUFFIX_FIELD
	  field = newSuffixField(fields.get(i));
	  if (!m_Values.get(FieldType.SUFFIX_FIELD).contains(field))
	    m_Values.get(FieldType.SUFFIX_FIELD).add(field);
	}
      }

      // sort
      for (FieldType type: FieldType.values())
	Collections.sort(m_Values.get(type));

      // notify listeners
      for (FieldType type: FieldType.values()) {
	event = new FieldCacheUpdateEvent(this);
	iter  = m_CacheListeners.get(type).iterator();
	while (iter.hasNext())
	  iter.next().cacheUpdated(event);
      }
    }
  }

  /**
   * Adds a cache listener.
   *
   * @param fieldtype	the fieldtype to register the listener for
   * @param l		the listener to add
   */
  public void addCacheListener(FieldType fieldtype, FieldCacheUpdateListener l) {
    m_CacheListeners.get(fieldtype).add(l);
  }

  /**
   * Removes a cache listener.
   *
   * @param fieldtype	the fieldtype to unregister the listener for
   * @param l		the listener to remove
   */
  public void removeCacheListener(FieldType fieldtype, FieldCacheUpdateListener l) {
    m_CacheListeners.get(fieldtype).remove(l);
  }

  /**
   * Removes all cache listeners.
   */
  public void removeCacheListeners() {
    m_CacheListeners.clear();
  }
}