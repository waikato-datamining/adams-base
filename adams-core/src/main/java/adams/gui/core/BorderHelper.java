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
 * BorderHelper.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.core;

import adams.gui.laf.AbstractLookAndFeel;

import javax.swing.BorderFactory;
import javax.swing.UIManager;
import javax.swing.border.Border;

/**
 * Helper class for Swing widget borders.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class BorderHelper {

  /**
   * Creates a beveled border, taking the look'n'feel into account.
   *
   * @param type	the type of bevel to use
   * @return		the border
   */
  public static Border createBevelBorder(int type) {
    if (AbstractLookAndFeel.hasCurrent() && AbstractLookAndFeel.getCurrent().isFlat()) {
      return BorderFactory.createLineBorder(UIManager.getColor("Separator.foreground"));
    }
    else {
      return BorderFactory.createSoftBevelBorder(type);
    }
  }
}
