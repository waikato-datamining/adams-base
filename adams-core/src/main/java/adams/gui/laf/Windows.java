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
 * Windows.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.laf;

import adams.core.management.OS;

/**
 * Windows look and feel.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class Windows
  extends AbstractSimpleLookAndFeel {

  private static final long serialVersionUID = -8114643816562107835L;

  /**
   * Returns the name for this look and feel.
   *
   * @return		the name
   */
  public String getName() {
    return "Windows";
  }

  /**
   * Checks whether the look and feel is available.
   *
   * @return true if available
   */
  @Override
  public boolean isAvailable() {
    return OS.isWindows() && super.isAvailable();
  }

  /**
   * Returns the classname of the look and feel to use.
   *
   * @return the classname
   */
  @Override
  protected String getLookAndFeelClassname() {
    return "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
  }
}
