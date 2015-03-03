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
 * AbstractInitialization.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.application;

import adams.core.ClassLister;
import adams.core.option.OptionUtils;

/**
 * Ancestor for initialization applets.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractInitialization 
  implements Initialization {

  /** for serialization. */
  private static final long serialVersionUID = -2596434986976571342L;

  /**
   * The title of the initialization.
   * 
   * @return		the title
   */
  public abstract String getTitle();
  
  /**
   * Performs the initialization.
   * 
   * @param parent	the application this initialization is for
   * @return		true if successful
   */
  public abstract boolean initialize(AbstractApplicationFrame parent);

  /**
   * Runs all the initialization applets.
   * 
   * @return		true if all successful
   */
  public static boolean initAll() {
    return initAll(null);
  }

  /**
   * Runs all the initialization applets.
   * 
   * @param parent	the application this initialization is for, can be null
   * @return		true if all successful
   */
  public static boolean initAll(AbstractApplicationFrame parent) {
    boolean		result;
    String[]		classes;
    Initialization	init;
    
    result = true;
    classes = AbstractInitialization.getInitializations();
    for (String cls: classes) {
      try {
	init   = (Initialization) Class.forName(cls).newInstance();
	result = init.initialize(parent) || result;
      }
      catch (Exception e) {
	System.err.println("Failed to run initialization applet '" + cls + "':");
	e.printStackTrace();
      }
    }
    
    return result;
  }
  
  /**
   * Returns a list with classnames of initialization applets.
   *
   * @return		the initialization applet classnames
   */
  public static String[] getInitializations() {
    return ClassLister.getSingleton().getClassnames(Initialization.class);
  }

  /**
   * Instantiates the initialization applet with the (optional) options.
   *
   * @param classname	the classname of the initialization applet to instantiate
   * @param options	the options for the initialization applet
   * @return		the instantiated initialization applet or null if an error occurred
   */
  public static Initialization forName(String classname, String[] options) {
    Initialization	result;

    try {
      result = (Initialization) OptionUtils.forName(Initialization.class, classname, options);
    }
    catch (Exception e) {
      e.printStackTrace();
      result = null;
    }

    return result;
  }
}
