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
 * ConfigurableEnumeration.java
 * Copyright (C) 2016-2017 University of Waikato, Hamilton, NZ
 */

package adams.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A more flexible way for enumerations.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class ConfigurableEnumeration<T extends ConfigurableEnumeration.AbstractItem>
  implements Serializable, Iterable<T> {

  private static final long serialVersionUID = 747043367670890397L;

  /**
   * Represents a single item of an enumeration.
   *
   * @author FracPete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static abstract class AbstractItem<E extends ConfigurableEnumeration>
    implements Serializable, CloneHandler<AbstractItem>, Comparable<AbstractItem> {

    private static final long serialVersionUID = -6709282604894298581L;

    /** the enumeration this item belongs to. */
    protected E m_Enumeration;

    /** the id. */
    protected String m_ID;

    /** the (optional) display text. */
    protected String m_Display;

    /**
     * Initializes the enum type.
     *
     * @param enumeration	the owning enumeration
     * @param id		the ID of the enum type, can be null
     * @param display		the display text, can be null
     */
    public AbstractItem(E enumeration, String id, String display) {
      m_Enumeration = enumeration;
      if (id == null)
        throw new IllegalArgumentException("Item ID cannot be null!");
      m_ID          = id;
      m_Display     = (display == null) ? id : display;
    }

    /**
     * Sets the owning enumeration.
     *
     * @param value	the enumeration
     */
    public void setEnumeration(E value) {
      m_Enumeration = value;
    }

    /**
     * Returns the enumeration this item belongs to.
     *
     * @return		the owner
     */
    public E getEnumeration() {
      return m_Enumeration;
    }

    /**
     * Returns the ID.
     *
     * @return		the ID
     */
    public String getID() {
      return m_ID;
    }

    /**
     * Returns the display text.
     *
     * @return		the display text
     */
    public String getDisplay() {
      return m_Display;
    }

    /**
     * Returns a clone of the object.
     *
     * @return		the clone
     */
    public AbstractItem getClone() {
      return getEnumeration().newItem(getID(), getDisplay());
    }

    /**
     * Compares this item against the provided one.
     *
     * @param o		the item to compare against
     * @return		less than, equal to, or greater than 0 if this item is
     * 			less than, equal to, or greater than the provided item
     */
    @Override
    public int compareTo(AbstractItem o) {
      int	result;

      result = 0;

      if ((getEnumeration() != null) && (o.getEnumeration() != null))
        result = getEnumeration().toString().compareTo(o.getEnumeration().toString());

      if (result == 0)
        result = getID().compareTo(o.getID());

      return result;
    }

    /**
     * Checks if the provided item is the same as this one.
     *
     * @param obj	the object to check
     * @return		true if the same
     * @see		#compareTo(AbstractItem)
     */
    @Override
    public boolean equals(Object obj) {
      return (obj instanceof AbstractItem) && (compareTo((AbstractItem) obj) == 0);
    }

    /**
     * Just returns the display text.
     *
     * @return		the display text
     */
    public String toString() {
      return getDisplay();
    }
  }

  /** the items of the enumeration. */
  protected T[] m_Items;

  /**
   * Initializes the enumeration.
   */
  public ConfigurableEnumeration() {
    m_Items = check(initialize());
  }

  /**
   * Initializes the items.
   *
   * @return		the items
   */
  protected abstract T[] initialize();

  /**
   * Checks the uniqueness of the items. Also ensures that items are not null.
   *
   * @param items			the items to check
   * @throws IllegalArgumentException	if not unique or null
   * @return				the items
   */
  protected T[] check(T[] items) {
    Set<String> 	unique;

    if (items == null)
      throw new IllegalArgumentException("Items cannot be null!");

    // check uniqueness: ID
    unique = new HashSet<>();
    for (T item: items)
      unique.add(item.getID());
    if (unique.size() != items.length)
      throw new IllegalArgumentException("Item IDs not unique: " + Utils.flatten(items, ","));

    // check uniqueness: display text
    unique = new HashSet<>();
    for (T item: items)
      unique.add(item.getDisplay());
    if (unique.size() != items.length)
      throw new IllegalArgumentException("Item display texts not unique: " + Utils.flatten(items, ","));

    return items;
  }

  /**
   * Returns the items of the enumeration.
   *
   * @return		the items
   */
  public T[] values() {
    return m_Items;
  }

  /**
   * Parses the given string and returns the associated Item, making use
   * of label and ID.
   *
   * @param s		the string to parse
   * @return		the item, null if failed to locate item
   */
  public T parse(String s) {
    T	result;

    result = null;
    s      = s.toLowerCase();

    for (T item: m_Items) {
      if (item.getID().toLowerCase().equals(s)
        || item.getDisplay().toLowerCase().equals(s)) {
        result = item;
        break;
      }
    }

    return result;
  }

  /**
   * Initializes the enum type.
   *
   * @param id		the ID of the enum type, can be null
   * @param display	the display text, can be null
   */
  public abstract T newItem(String id, String display);

  /**
   * Returns an iterator over the items.
   *
   * @return		the iterator
   */
  @Override
  public Iterator<T> iterator() {
    return new ArrayList<>(Arrays.asList(m_Items)).iterator();
  }

  /**
   * Just returns all the underlying items as comma-separated list.
   *
   * @return		the items as list
   */
  public String toString() {
    return Utils.flatten(m_Items, ",");
  }
}
