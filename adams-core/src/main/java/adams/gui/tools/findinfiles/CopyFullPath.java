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
 * CopyFullPath.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.findinfiles;

import adams.core.Utils;
import com.github.fracpete.jclipboardhelper.ClipboardHelper;

import java.awt.event.ActionEvent;

/**
 * Copies the full path(s) to the clipboard.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class CopyFullPath
  extends AbstractFindInFilesAction {

  private static final long serialVersionUID = 2602074439493570865L;

  /**
   * Returns the text for the menu item.
   *
   * @return		the text
   */
  protected String getMenuItemText() {
    return "Copy full path";
  }

  /**
   * Updates the action based on the current state of the owner.
   */
  @Override
  protected void doUpdate() {
    setEnabled(m_Owner.getSelectedFiles().length > 0);
  }

  /**
   * Invoked when an action occurs.
   *
   * @param e		the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    ClipboardHelper.copyToClipboard(Utils.flatten(getOwner().getSelectedFiles(), "\n"));
  }
}
