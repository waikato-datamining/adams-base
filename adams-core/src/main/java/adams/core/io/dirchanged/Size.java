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
 * Size.java
 * Copyright (C) 2025 University of Waikato, Hamilton, NZ
 */

package adams.core.io.dirchanged;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Checks whether the file sizes have changed.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Size
  extends AbstractDirChangeMonitor {

  private static final long serialVersionUID = 7861456311356953324L;

  /** the map for file / size. */
  protected Map<File, Long> m_Sizes;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Checks whether the file sizes have changed.";
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Sizes = null;
  }

  /**
   * Performs the actual initialization of the monitor with the specified dir.
   *
   * @param dir		the dir to initialize with
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doInitialize(File dir) {
    m_Sizes = new HashMap<>();
    for (File file: listFiles(dir))
      m_Sizes.put(file, file.length());
    return null;
  }

  /**
   * Performs the actual check whether the dir has changed.
   *
   * @param dir		the dir to check
   * @return		true if changed
   */
  @Override
  protected boolean checkChange(File dir) {
    for (File file: listFiles(dir)) {
      if (!m_Sizes.containsKey(file))
	return true;
      if (file.length() != m_Sizes.get(file))
	return true;
    }
    return false;
  }

  /**
   * Performs the actual updating of the monitor with the specified dir.
   *
   * @param dir		the dir to update with
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doUpdate(File dir) {
    return doInitialize(dir);
  }
}
