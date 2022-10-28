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
 * AbstractSimpleLookAndFeel.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.laf;

import javax.swing.UIManager;

/**
 * Ancestor for look and feels that only require a classname.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractSimpleLookAndFeel
  extends AbstractLookAndFeel {

  private static final long serialVersionUID = -1937345062210504141L;

  /**
   * Returns the classname of the look and feel to use.
   *
   * @return		the classname
   */
  protected abstract String getLookAndFeelClassname();

  /**
   * Checks whether the look and feel is available.
   *
   * @return true if available
   */
  @Override
  public boolean isAvailable() {
    try {
      Class.forName(getLookAndFeelClassname());
      return true;
    }
    catch (Exception e) {
      return false;
    }
  }

  /**
   * Installs the look and feel.
   *
   * @throws Exception 	if installation fails
   */
  @Override
  public void doInstall() throws Exception{
    UIManager.setLookAndFeel(getLookAndFeelClassname());
  }
}
