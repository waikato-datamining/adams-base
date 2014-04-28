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
 * SystemClassPathAugmenter.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.core.management;

/**
 * Simply returns the CLASSPATH environment variable.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SystemClassPathAugmenter
  extends AbstractClassPathAugmenter {

  /** for serialization. */
  private static final long serialVersionUID = -7672756657681267610L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Returns the system's classpath via the CLASSPATH environment variable.";
  }
  
  /**
   * Returns the classpath parts (jars, directories) to add to the classpath.
   * 
   * @return		the additional classpath parts
   */
  public String[] getClassPathAugmentation() {
    String[]	result;
    String	env;
    
    env = System.getenv("CLASSPATH");
    if (env == null)
      result = new String[0];
    else
      result = env.split(System.getProperty("path.separator"));
    
    return result;
  }
}
