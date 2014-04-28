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
 * Environment.java
 * Copyright (C) 2009-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.env;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Manages properties files and returns merged versions.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Environment
  extends AbstractEnvironment {

  /** for serialization. */
  private static final long serialVersionUID = -2199293612498875147L;

  /**
   * Initializes the object.
   */
  protected Environment() {
    super();
  }

  /**
   * Returns the resource locations of the properties files with the definitions.
   *
   * @return		the resource locations
   */
  @Override
  protected List<String> getPropertiesDefinitions() {
    return new ArrayList<String>(
	Arrays.asList(
	    new String[]{
		"adams/env/" + FILENAME
	    }));
  }

  /**
   * Returns the project's name.
   *
   * @return		the internal name of the project
   */
  @Override
  public String getProject() {
    return Project.NAME;
  }

  /**
   * Outputs a list of available conversions.
   * 
   * @param lister	the classname lister to use
   * @param args	the commandline options: [-project] [-home] [-definitions] [-properties] [-resource path]
   */
  public static void main(String[] args) throws Exception {
    runEnvironment(Environment.class, args);
  }
}
