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
 * FileBrowser.java
 * Copyright (C) 2015-2023 University of Waikato, Hamilton, NZ
 */

package adams.core.management;

import adams.core.logging.Logger;
import adams.core.logging.LoggingHelper;

import java.awt.Desktop;
import java.io.File;
import java.util.logging.Level;

/**
 * Allows to launch the OS-specific file browser.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class FileBrowser {

  protected static Logger LOGGER = LoggingHelper.getLogger(FileBrowser.class);

  /**
   * Launches the file browser with the user's home directory.
   *
   * @return		true if successfully launched
   */
  public static boolean launch() {
    return launch(new File(User.getHomeDir()));
  }

  /**
   * Launches the file browser with the specified directory.
   *
   * @param dir		the directory to launch with
   * @return		true if successfully launched
   */
  public static boolean launch(File dir) {
    if (!dir.exists())
      return false;
    if (!dir.isDirectory())
      dir = dir.getParentFile();

    try {
      Desktop.getDesktop().open(dir.getAbsoluteFile());
      return true;
    }
    catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Failed to launch file browser:", e);
      return false;
    }
  }
}
