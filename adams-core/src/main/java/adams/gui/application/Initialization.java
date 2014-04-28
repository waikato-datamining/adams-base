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
 * Initialization.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.application;

import java.io.Serializable;

/**
 * Interface for initialization applets.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface Initialization 
  extends Serializable {

  /**
   * The title of the initialization.
   * 
   * @return		the title
   */
  public abstract String getTitle();
  
  /**
   * Performs the initialization.
   * 
   * @param parent	the application this initialization is for, can be null
   * @return		true if successful
   */
  public abstract boolean initialize(AbstractApplicationFrame parent);
}
