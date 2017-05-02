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
 * RestorableActor.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.flow.core;

import adams.core.io.PlaceholderFile;

/**
 * Interface for actors that can save their state to disk and restore it again
 * next time they are run.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface RestorableActor
  extends Actor {

  /**
   * Sets whether to enable restoration.
   *
   * @param value	true if to enable restoration
   */
  public void setRestorationEnabled(boolean value);

  /**
   * Returns whether restoration is enabled.
   *
   * @return		true if restoration enabled
   */
  public boolean isRestorationEnabled();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String restorationEnabledTipText();

  /**
   * Sets the file for storing the state.
   *
   * @param value	the file
   */
  public void setRestorationFile(PlaceholderFile value);

  /**
   * Returns the file for storing the state.
   *
   * @return		the file
   */
  public PlaceholderFile getRestorationFile();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String restorationFileTipText();
}
