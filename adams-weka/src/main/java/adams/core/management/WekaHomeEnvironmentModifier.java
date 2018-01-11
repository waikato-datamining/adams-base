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
 * WekaHomeEnvironmentModifier.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.core.management;

import adams.core.io.FileUtils;
import adams.env.Environment;
import weka.core.Version;

import java.io.File;
import java.util.List;

/**
 * Sets a custom WEKA_HOME environment variable inside the project's home directory.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class WekaHomeEnvironmentModifier
  extends AbstractEnvironmentModifier {

  private static final long serialVersionUID = 900388473930981697L;

  /** the WEKA_HOME environment variable. */
  public final static String ENV_VAR = "WEKA_HOME";

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Sets the " + ENV_VAR + " environment variable to if not already present in the environment:\n"
	+ "<project-home>/wekafiles/<weka-version>";
  }

  /**
   * Updates the environment variables that the {@link Launcher} uses for
   * launching the ADAMS process.
   *
   * @param env		the current key=value pairs
   * @return		if the environment got updated
   */
  @Override
  public boolean updateEnvironment(List<String> env) {
    String	path;
    File	dir;

    for (String var: env) {
      if (var.startsWith(ENV_VAR + "=")) {
	getLogger().warning("Environment variable " + ENV_VAR + " already present, not overriding.");
        return false;
      }
    }

    path = Environment.getInstance().getHome()
      + File.separator
      + "wekafiles"
      + File.separator + FileUtils.createFilename(Version.VERSION, "_");

    // try to create directory
    dir = new File(path);
    if (!dir.exists()) {
      if (isLoggingEnabled())
        getLogger().info("Creating directory: " + path);
      if (!dir.mkdirs()) {
	getLogger().warning("Failed to create directory: " + path);
        return false;
      }
    }

    env.add(ENV_VAR + "=" + path);
    if (isLoggingEnabled())
      getLogger().info(ENV_VAR + ": " + path);

    return true;
  }
}
