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
 * CopySetup.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.classifytab.output.perfold;

import adams.core.option.OptionUtils;
import com.github.fracpete.jclipboardhelper.ClipboardHelper;

import javax.swing.JMenuItem;
import java.awt.event.ActionEvent;

/**
 * Simply copies the classifier setup to the clipboard.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class CopySetup
  extends AbstractPerFoldPopupMenuItem {

  /**
   * The category for grouping menu items.
   *
   * @return		the group
   */
  @Override
  public String getCategory() {
    return "Model";
  }

  /**
   * The menu item title.
   *
   * @return		the title
   */
  @Override
  public String getTitle() {
    return "Copy setup";
  }

  /**
   * Creates the menu item to add to the pane's popup menu.
   *
   * @param pane	the per-fold panel this menu is for
   * @param indices	the selected indices
   * @return		the menu item, null if failed to generate
   */
  @Override
  public JMenuItem createMenuItem(PerFoldMultiPagePane pane, int[] indices) {
    JMenuItem result;

    result = new JMenuItem("Copy setup");
    result.setEnabled(true);
    result.addActionListener((ActionEvent ae) -> {
      String setup = OptionUtils.getCommandLine(pane.getItem().getTemplate());
      ClipboardHelper.copyToClipboard(setup);
    });

    return result;
  }
}
