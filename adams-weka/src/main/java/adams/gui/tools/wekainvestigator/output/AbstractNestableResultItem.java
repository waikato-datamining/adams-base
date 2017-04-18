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
 * AbstractNestableResultItem.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.output;

import weka.core.Instances;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Container for a data to be stored in result history that can also store
 * nested result items.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractNestableResultItem
  extends AbstractResultItem {

  private static final long serialVersionUID = -3409493446200539772L;

  /** the name suffix to use (optional). */
  protected String m_NameSuffix;

  /** nested result items. */
  protected Map<String,AbstractNestableResultItem> m_NestedItems;

  /**
   * Initializes the item.
   *
   * @param header	the header of the training set, can be null
   */
  protected AbstractNestableResultItem(Instances header) {
    super(header);
    m_NameSuffix  = null;
    m_NestedItems = new HashMap<>();
  }

  /**
   * Sets the optional name suffix.
   *
   * @param value	the suffix, null if not to use any
   */
  public void setNameSuffix(String value) {
    m_NameSuffix = value;
  }

  /**
   * Returns the name suffix of the item.
   *
   * @return		the name suffix, null if none specified
   */
  public String getNameSuffix() {
    return m_NameSuffix;
  }

  /**
   * Adds the nested item.
   *
   * @param name	the name
   * @param item	the item
   */
  public void addNestedItem(String name, AbstractNestableResultItem item) {
    m_NestedItems.put(name, item);
  }

  /**
   * Returns whether nested items are present.
   *
   * @return		true if present
   */
  public boolean hasNestedItems() {
    return (m_NestedItems.size() > 0);
  }

  /**
   * Returns the names of the nested items.
   *
   * @return		the names
   */
  public List<String> nestedItemNames() {
    List<String>	result;

    result = new ArrayList<>(m_NestedItems.keySet());
    Collections.sort(result);

    return result;
  }

  /**
   * Returns the nested items by its name.
   *
   * @param name	the name of the item to  retrieve
   * @return		the item, null if not found
   */
  public AbstractNestableResultItem getNestedItem(String name) {
    return m_NestedItems.get(name);
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    m_NestedItems.clear();
    super.cleanUp();
  }
}
