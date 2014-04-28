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
 * Dummy.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.core.scripting;

import adams.core.Variables;
import adams.core.io.PlaceholderFile;

/**
 * Dummy handler, which does nothing.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Dummy
  extends AbstractScriptingHandler {

  /** for serialization. */
  private static final long serialVersionUID = 868900095276931089L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Dummy hanlder, which does nothing.";
  }

  /**
   * Returns whether the handler can be used.
   *
   * @return 			always false
   */
  @Override
  public boolean isPresent() {
    return false;
  }

  /**
   * Loads the scripts object and sets its options.
   *
   * @param cls			the class to instantiate
   * @param scriptFile		the external file to load
   * @param scriptOptions	the options to set
   * @param vars		the variables to use for expanding
   * @return			element 0: error messsage (null if ok), element 1: script object
   */
  @Override
  public Object[] loadScriptObject(Class cls, PlaceholderFile scriptFile, String scriptOptions, Variables vars) {
    return new Object[]{
	"Dummy",
	null
    };
  }

  /**
   * Executes the specified method on the current interpreter and returns the
   * result, if any.
   *
   * @param o			the object the method should be called from,
   * 				e.g., an Interpreter
   * @param methodName		the name of the method
   * @param paramClasses	the classes of the parameters
   * @param paramValues		the values of the parameters
   * @return			the return value of the method, if any (in that case null)
   */
  @Override
  public Object invoke(Object o, String methodName, Class[] paramClasses, Object[] paramValues) {
    return null;
  }
}
