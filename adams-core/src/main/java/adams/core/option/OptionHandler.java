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
 * OptionHandler.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */

package adams.core.option;

import adams.core.Destroyable;

/**
 * An interface to indicate that this class can handle commandline options.
 * <br><br>
 * Most of the methods only need to be implemented in the superclass, also
 * including the List holding the Option objects. Derived classes only
 * override <code>defineOptions()</code> to add more Option objects to the
 * internal List.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface OptionHandler
  extends Destroyable {

  /**
   * Adds options to the internal list of options.
   * <br><br>
   * Every subclass needs to add the necessary Option objects to its internal
   * List with Option objects.
   */
  public void defineOptions();

  /**
   * Returns the option manager.
   * <br><br>
   * Only needs to be implemented in the superclass, which declares the
   * OptionManager object managing all the Option objects.
   *
   * @return		the internal option list
   */
  public OptionManager getOptionManager();

  /**
   * Cleans up the options.
   */
  public void cleanUpOptions();

  /**
   * Returns the commandline string.
   *
   * @return		 the commandline
   */
  public String toCommandLine();
}
