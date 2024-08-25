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
 * AbstractFlatLaf.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.laf;

import adams.gui.core.ColorHelper;
import adams.gui.core.GUIHelper;

import javax.swing.UIManager;

/**
 * Ancestor for FlatLaf look and feel plugins.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractFlatLaf
  extends AbstractLookAndFeel {

  private static final long serialVersionUID = 2394913538898207432L;

  /**
   * Checks whether the look and feel is available.
   *
   * @return true if available
   */
  @Override
  public boolean isAvailable() {
    return true;
  }

  /**
   * Installs the look and feel.
   *
   * @throws Exception 	if installation fails
   */
  @Override
  public void doInstall() throws Exception {
    GUIHelper.setDefaultHorizontalTextOffset(8);
    com.formdev.flatlaf.FlatLightLaf.setup();
    UIManager.put("Table.showHorizontalLines", true);
    UIManager.put("Table.showVerticalLines", true);
    UIManager.put("Tree.selectionBackground", ColorHelper.valueOf("#b8cfe5"));
    UIManager.put("Tree.wideSelection", false);
    UIManager.put("ScrollBar.showButtons", true);
  }
}
