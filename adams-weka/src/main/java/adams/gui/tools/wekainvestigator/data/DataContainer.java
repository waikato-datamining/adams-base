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
 * DataContainer.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.data;

import adams.core.CleanUpHandler;
import adams.gui.core.UndoHandlerWithQuickAccess;
import weka.core.Instances;

import java.util.Date;

/**
 * Interface for data containers.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface DataContainer
  extends Comparable<DataContainer>, UndoHandlerWithQuickAccess, CleanUpHandler {

  /**
   * Returns the container ID.
   *
   * @return		the ID
   */
  public int getID();

  /**
   * Returns the source of the data item.
   *
   * @return		the source
   */
  public String getSource();

  /**
   * Whether it is possible to reload this item.
   *
   * @return		true if reloadable
   */
  public boolean canReload();

  /**
   * Reloads the data.
   *
   * @return		true if successfully reloaded
   */
  public boolean reload();

  /**
   * Checks whether the data has been modified.
   *
   * @return		true if modified
   */
  public boolean isModified();

  /**
   * Sets whether the data has been modified.
   *
   * @param value	true if modified
   */
  public void setModified(boolean value);

  /**
   * Sets the data.
   *
   * @param value	the data to use
   */
  public void setData(Instances value);

  /**
   * Returns the actual underlying data.
   *
   * @return		the data
   */
  public Instances getData();

  /**
   * Returns the timestamp the data was last updated.
   *
   * @return		the timestamp
   */
  public Date lastUpdated();
}
