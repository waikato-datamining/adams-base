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
 * JythonHandler.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.core.scripting;

import adams.core.Variables;
import adams.core.io.PlaceholderFile;

/**
 * Scripting handler for Jython.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class JythonHandler
  extends AbstractScriptingHandler {

  /** for serialization. */
  private static final long serialVersionUID = -8266587082145465710L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Scripting handler for Jython.";
  }

  /**
   * Returns whether the handler can be used.
   *
   * @return 			true if available
   */
  @Override
  public boolean isPresent() {
    return Jython.getSingleton().isPresent();
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
    return Jython.getSingleton().loadScriptObject(cls, scriptFile, new JythonScript(), scriptOptions, vars);
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
    return Jython.getSingleton().invoke(o, methodName, paramClasses, paramValues);
  }
}
