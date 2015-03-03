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
 * DataChangeEvent.java
 * Copyright (C) 2008-2010 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.event;

import java.util.EventObject;

import adams.core.Utils;
import adams.gui.visualization.container.AbstractContainer;
import adams.gui.visualization.container.AbstractContainerManager;

/**
 * An event indicating that the underlying containers have changed.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DataChangeEvent
  extends EventObject {

  /** the type of event. */
  public static enum Type {
    /** all data was removed. */
    CLEAR,
    /** a container was added. */
    ADDITION,
    /** a container was removed. */
    REMOVAL,
    /** a container was replaced. */
    REPLACEMENT,
    /** an update finished. */
    UPDATE,
    /** a larger update finished. */
    BULK_UPDATE,
    /** the visibility of a container got changed. */
    VISIBILITY,
    /** whether a search was performed. */
    SEARCH
  }

  /** for serialization. */
  private static final long serialVersionUID = 3847803691713697508L;

  /** the type of event. */
  protected Type m_Type;

  /** the indices of the modified containers. */
  protected int[] m_Indices;

  /** the replaced containers. */
  protected AbstractContainer[] m_Containers;

  /**
   * Initializes the event.
   *
   * @param source	the manager that triggered the event
   * @param type	the type of event
   */
  public DataChangeEvent(AbstractContainerManager source, Type type) {
    this(source, type, null);
  }

  /**
   * Initializes the event.
   *
   * @param source	the manager that triggered the event
   * @param type	the type of event
   * @param index	the relevant index
   */
  public DataChangeEvent(AbstractContainerManager source, Type type, int index) {
    this(source, type, new int[]{index}, null);
  }

  /**
   * Initializes the event.
   *
   * @param source	the manager that triggered the event
   * @param type	the type of event
   * @param index	the relevant index
   * @param cont	the old container, can be null
   */
  public DataChangeEvent(AbstractContainerManager source, Type type, int index, AbstractContainer cont) {
    this(source, type, new int[]{index}, new AbstractContainer[]{cont});
  }

  /**
   * Initializes the event.
   *
   * @param source	the manager that triggered the event
   * @param type	the type of event
   * @param indices	the relevant indices, can be null
   */
  public DataChangeEvent(AbstractContainerManager source, Type type, int[] indices) {
    this(source, type, indices, null);
  }

  /**
   * Initializes the event.
   *
   * @param source	the manager that triggered the event
   * @param type	the type of event
   * @param indices	the relevant indices, can be null
   * @param cont	the old containers, can be null
   */
  public DataChangeEvent(AbstractContainerManager source, Type type, int[] indices, AbstractContainer[] cont) {
    super(source);

    m_Type       = type;
    m_Containers = cont;
    if (indices != null)
      m_Indices = indices.clone();
    else
      m_Indices = null;

    if ((m_Indices != null) && (m_Containers != null)) {
      if (m_Indices.length != m_Containers.length)
	throw new IllegalArgumentException(
	    "Number of indices and containers don't match: "
	    + m_Indices.length + " != " + m_Containers.length);
    }
  }

  /**
   * Returns the manager that triggered the event.
   *
   * @return		the manager
   */
  public AbstractContainerManager getManager() {
    return (AbstractContainerManager) getSource();
  }

  /**
   * Returns the type of event.
   *
   * @return		the type
   */
  public Type getType() {
    return m_Type;
  }

  /**
   * Returns the relevant indices, can be null.
   *
   * @return		the indices, can be null
   */
  public int[] getIndices() {
    return m_Indices;
  }

  /**
   * Returns the old containers, can be null.
   *
   * @return		the containers, can be null
   */
  public AbstractContainer[] getContainers() {
    return m_Containers;
  }

  /**
   * Returns a short string representation of the event.
   *
   * @return		the string representation
   */
  @Override
  public String toString() {
    String	result;

    result = getSource().getClass().getName() + "/" + getSource().hashCode();
    result += ", type=" + getType();
    if (getIndices() != null)
      result += ", indices=" + Utils.arrayToString(getIndices());
    if (getContainers() != null)
      result += ", # containers=" + getContainers().length;

    return result;
  }
}
