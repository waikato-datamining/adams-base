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
 * NullAnnotator.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.object.annotator;

import java.awt.Graphics;

/**
 * Dummy, offers no annotation support.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class NullAnnotator
  extends AbstractAnnotator {

  private static final long serialVersionUID = 1122040195846360397L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Dummy, offers no annotation support.";
  }

  /**
   * Installs the annotator with the owner.
   */
  @Override
  protected void doInstall() {
  }

  /**
   * Uninstalls the annotator with the owner.
   */
  @Override
  protected void doUninstall() {
  }

  /**
   * Paints the selection.
   *
   * @param g		the graphics context
   */
  protected void doPaintSelection(Graphics g) {
  }
}
