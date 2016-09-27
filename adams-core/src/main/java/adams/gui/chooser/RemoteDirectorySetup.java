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
 * RemoteDirectorySetup.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.chooser;

import adams.core.CleanUpHandler;
import adams.core.option.OptionHandler;

/**
 * Interface for setup classes that define access to remote directories.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface RemoteDirectorySetup
  extends OptionHandler, CleanUpHandler {

  /**
   * Returns whether the setup needs to be configured by the user or whether
   * it can be used straight away.
   *
   * @return		true if user needs to configure first
   */
  public boolean requiresInitialization();
}
