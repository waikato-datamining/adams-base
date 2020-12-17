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
 * OpenFile.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.previewbrowser.localfiles;

import java.awt.Desktop;
import java.awt.event.ActionEvent;

/**
 * Opens a file with the associated application.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class OpenFile
  extends AbstractLocalFilesAction {

  private static final long serialVersionUID = 4099532419561897428L;

  /**
   * Initializes the action.
   */
  public OpenFile() {
    setName("Open");
    setIcon("open.gif");
  }

  /**
   * Updates the action.
   */
  @Override
  protected void doUpdate() {
    setEnabled((m_Owner.getCurrentFiles() != null) && (m_Owner.getCurrentFiles().length == 1));
  }

  /**
   * Invoked when an action occurs.
   *
   * @param e the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    try {
      Desktop.getDesktop().open(m_Owner.getCurrentFiles()[0].getAbsoluteFile());
    }
    catch (Exception ex) {
      displayError("Failed to open file!", ex);
    }
  }
}
