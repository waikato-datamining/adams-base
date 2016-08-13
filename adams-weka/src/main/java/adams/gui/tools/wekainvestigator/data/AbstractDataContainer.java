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
 * AbstractDataContainer.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.data;

import adams.core.logging.LoggingObject;
import weka.core.Instances;

/**
 * Ancestor for data containers.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractDataContainer
  extends LoggingObject
  implements DataContainer {

  private static final long serialVersionUID = 6267905940957451551L;

  /** the ID counter. */
  protected static int m_IDCounter;

  /** the ID of the container. */
  protected int m_ID;

  /** the underlying data. */
  protected Instances m_Data;

  /** whether the data has been modified. */
  protected boolean m_Modified;

  /**
   * Initializes the container with no data.
   */
  public AbstractDataContainer() {
    m_ID       = nextID();
    m_Data     = null;
    m_Modified = false;
  }

  /**
   * Initializes the container with just the data.
   *
   * @param data	the data to use
   */
  public AbstractDataContainer(Instances data) {
    m_Data     = data;
    m_Modified = false;
  }

  /**
   * Sets the data.
   *
   * @param value	the data to use
   */
  public void setData(Instances value) {
    if (m_Data != null)
      setModified(true);
    m_Data = value;
  }

  /**
   * Returns the actual underlying data.
   *
   * @return		the data
   */
  @Override
  public Instances getData() {
    return m_Data;
  }

  /**
   * Returns the container ID.
   *
   * @return		the ID
   */
  public int getID() {
    return m_ID;
  }

  /**
   * Checks whether the data has been modified.
   *
   * @return		true if modified
   */
  public boolean isModified() {
    return m_Modified;
  }

  /**
   * Sets whether the data has been modified.
   *
   * @param value	true if modified
   */
  public void setModified(boolean value) {
    m_Modified = value;
  }

  /**
   * Reloads the data.
   *
   * @return		true if successfully reloaded
   */
  protected abstract boolean doReload();

  /**
   * Reloads the data.
   *
   * @return		true if successfully reloaded
   */
  @Override
  public boolean reload() {
    boolean	result;

    result = false;

    if (canReload()) {
      result = doReload();
      if (result)
	setModified(false);
    }

    return result;
  }

  /**
   * Compares this container with the specified one.
   *
   * @param o		the container to compare with
   * @return		less than, equal to or greater than 0 if the container's
   * 			{@link #getSourceShort()} is smaller, equal to or greater
   * 			then the provided one
   */
  public int compareTo(DataContainer o) {
    return getSourceFull().compareTo(o.getSourceFull());
  }

  /**
   * Checks whether the specified object is the same.
   *
   * @param obj		the object to check
   * @return		true if the same, i.e., the same {@link #getSourceFull()}
   * @see		#getID()
   */
  @Override
  public boolean equals(Object obj) {
    return (obj instanceof DataContainer) && (getID() == ((DataContainer) obj).getID());
  }

  /**
   * Returns a short description of the container.
   *
   * @return		the description
   */
  @Override
  public String toString() {
    return getData().relationName() + " [" + getSourceFull() + "]";
  }

  /**
   * Returns the next container ID.
   *
   * @return		the next ID
   */
  protected static synchronized int nextID() {
    return m_IDCounter++;
  }
}
