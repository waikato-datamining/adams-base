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

/**
 * ConfigurableEnumeration.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * A more flexible way for enumerations.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ConfigurableEnumeration
  implements Serializable, Iterable<ConfigurableEnumeration.Item> {

  private static final long serialVersionUID = 747043367670890397L;

  /**
   * Represents a single item of an enumeration.
   *
   * @author FracPete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class Item
    implements Serializable, CloneHandler<Item>, Comparable<Item> {

    private static final long serialVersionUID = -6709282604894298581L;

    /** the enumeration this item belongs to. */
    protected ConfigurableEnumeration m_Enumeration;

    /** the label. */
    protected String m_Label;

    /** the (optional) id. */
    protected String m_ID;

    /** the (optional) display text. */
    protected String m_Display;

    /**
     * Initializes the enum type.
     *
     * @param enumeration	the owning enumeration
     * @param label		the label of the enum type
     */
    public Item(ConfigurableEnumeration enumeration, String label) {
      this(enumeration, label, null, null);
    }

    /**
     * Initializes the enum type.
     *
     * @param enumeration	the owning enumeration
     * @param label		the label of the enum type
     * @param id		the ID of the enum type, can be null
     */
    public Item(ConfigurableEnumeration enumeration, String label, String id) {
      this(enumeration, label, id, null);
    }

    /**
     * Initializes the enum type.
     *
     * @param enumeration	the owning enumeration
     * @param label		the label of the enum type
     * @param id		the ID of the enum type, can be null
     * @param display		the display text, can be null
     */
    public Item(ConfigurableEnumeration enumeration, String label, String id, String display) {
      m_Enumeration = enumeration;
      if (label == null)
	throw new IllegalArgumentException("Item label cannot be null!");
      m_Label       = label;
      m_ID          = (id == null) ? label : id;
      m_Display     = (display == null) ? label : display;
    }

    /**
     * Sets the owning enumeration.
     *
     * @param value	the enumeration
     */
    public void setEnumeration(ConfigurableEnumeration value) {
      m_Enumeration = value;
    }

    /**
     * Returns the enumeration this item belongs to.
     *
     * @return		the owner
     */
    public ConfigurableEnumeration getEnumeration() {
      return m_Enumeration;
    }

    /**
     * Returns the label.
     *
     * @return		the label
     */
    public String getLabel() {
      return m_Label;
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
    public Item getClone() {
      return new Item(m_Enumeration, m_Label, m_ID, m_Display);
    }

    /**
     * Compares this item against the provided one.
     *
     * @param o		the item to compare against
     * @return		less than, equal to, or greater than 0 if this item is
     * 			less than, equal to, or greater than the provided item
     */
    @Override
    public int compareTo(Item o) {
      int	result;

      result = 0;

      if ((getEnumeration() != null) && (o.getEnumeration() != null))
	result = getEnumeration().toString().compareTo(o.getEnumeration().toString());

      if (result == 0)
	result = getLabel().compareTo(o.getLabel());

      return result;
    }

    /**
     * Checks if the provided item is the same as this one.
     *
     * @param obj	the object to check
     * @return		true if the same
     * @see		#compareTo(Item)
     */
    @Override
    public boolean equals(Object obj) {
      return (obj instanceof Item) && (compareTo((Item) obj) == 0);
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

  /**
   * Interface for enumeration sources.
   *
   * @author FracPete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public interface Source {

    /**
     * Returns the values to use for the enumeration.
     *
     * @param enumeration	the enumeration the values should be added to
     * @return			the values
     */
    public Item[] values(ConfigurableEnumeration enumeration);
  }

  /**
   * Ancestor for sources.
   *
   * @author FracPete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static abstract class AbstractSource
    implements Source {

    /** the items generated from the properties. */
    protected List<Item> m_Items;

    /**
     * Initializes the source.
     */
    protected AbstractSource() {
      m_Items = new ArrayList<>();
    }

    /**
     * Returns the values to use for the enumeration.
     *
     * @param enumeration	the enumeration the values should be added to
     * @return			the values
     */
    @Override
    public Item[] values(ConfigurableEnumeration enumeration) {
      Item[]	result;
      int	i;

      result = new Item[m_Items.size()];
      for (i = 0; i < result.length; i++) {
	result[i] = m_Items.get(i).getClone();
	result[i].setEnumeration(enumeration);
      }

      return result;
    }

    /**
     * Returns the internal items as comma-separated list.
     *
     * @return		the list
     */
    public String toString() {
      return Utils.flatten(m_Items, ",");
    }
  }

  /**
   * Source for enumerations that uses a props file as backend.
   * <br>
   * Format:
   * items=item1,item2,item3
   *
   * item1.label=ITEM1
   * item1.id=I1
   * item1.display=This is item 1
   *
   * item2.label=ITEM2
   * item2.id=I2
   * item2.display=This is item 2
   *
   * item3.label=ITEM3
   * item3.id=I3
   * item3.display=This is item 3
   *
   * @author FracPete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class PropertiesSource
    extends AbstractSource {

    /** the key for listing all the items. */
    public final static String KEY_ITEMS = "items";

    /** the key suffix for the label. */
    public final static String SUFFIX_LABEL = ".label";

    /** the key suffix for the ID. */
    public final static String SUFFIX_ID = ".id";

    /** the key suffix for the display text. */
    public final static String SUFFIX_DISPLAY = ".display";

    /**
     * Initializes the source.
     *
     * @param props	the properties to read the items from
     */
    public PropertiesSource(Properties props) {
      super();
      if (props.hasKey(KEY_ITEMS)) {
	String[] keys = props.getProperty(KEY_ITEMS).split(",");
	for (String key: keys) {
	  if (props.hasKey(key + SUFFIX_LABEL)) {
	    String label = props.getProperty(key + SUFFIX_LABEL);
	    String id = props.getProperty(key + SUFFIX_ID);
	    String display = props.getProperty(key + SUFFIX_DISPLAY);
	    Item item = new Item(null, label, id, display);
	    m_Items.add(item);
	  }
	}
      }
    }
  }

  /**
   * Simple source for using a fixed list of labels.
   *
   * @author FracPete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class FixedSource
    extends AbstractSource {

    /**
     * Initializes the source.
     *
     * @param labels	the labels to use
     */
    public FixedSource(String[] labels) {
      super();
      for (String label: labels)
	m_Items.add(new Item(null, label));
    }
  }

  /** the items of the enumeration. */
  protected Item[] m_Items;

  /**
   * Initializes the enumeration.
   *
   * @param source	the source to obtain the items from
   */
  public ConfigurableEnumeration(Source source) {
    if (source == null)
      throw new IllegalArgumentException("Source cannot be null!");
    m_Items = check(source.values(this));
  }

  /**
   * Checks the uniqueness of the items. Also ensures that items are not null.
   *
   * @param items			the items to check
   * @throws IllegalArgumentException	if not unique or null
   * @return				the items
   */
  protected Item[] check(Item[] items) {
    Set<String> 	unique;

    if (items == null)
      throw new IllegalArgumentException("Items cannot be null!");

    unique = new HashSet<>();
    for (Item item: items)
      unique.add(item.getLabel());
    if (unique.size() != items.length)
      throw new IllegalArgumentException("Item labels not unique: " + Utils.flatten(items, ","));

    // check uniqueness: ID
    unique = new HashSet<>();
    for (Item item: items)
      unique.add(item.getID());
    if (unique.size() != items.length)
      throw new IllegalArgumentException("Item IDs not unique: " + Utils.flatten(items, ","));

    // check uniqueness: display text
    unique = new HashSet<>();
    for (Item item: items)
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
  public Item[] values() {
    return m_Items;
  }

  /**
   * Parses the given string and returns the associated Item, making use
   * of label and ID.
   *
   * @param s		the string to parse
   * @return		the item, null if failed to locate item
   */
  public Item parse(String s) {
    Item	result;

    result = null;

    for (Item item: m_Items) {
      if (item.getLabel().equals(s) || item.getID().equals(s)) {
	result = item;
	break;
      }
    }

    return result;
  }

  /**
   * Returns an iterator over the items.
   *
   * @return		the iterator
   */
  @Override
  public Iterator<Item> iterator() {
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
