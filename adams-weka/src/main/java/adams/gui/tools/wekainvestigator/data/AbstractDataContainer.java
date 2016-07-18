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

  /** the underlying data. */
  protected Instances m_Data;

  /** whether the data has been modified. */
  protected boolean m_Modified;

  /**
   * Initializes the container with no data.
   */
  public AbstractDataContainer() {
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
   * @return		true if succesfully reloaded
   */
  protected abstract boolean doReload();

  /**
   * Reloads the data.
   *
   * @return		true if succesfully reloaded
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
}
