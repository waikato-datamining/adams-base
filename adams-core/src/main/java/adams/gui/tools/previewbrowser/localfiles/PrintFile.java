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
import java.io.File;

/**
 * Prints a file with the associated application.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class PrintFile
  extends AbstractLocalFilesAction {

  private static final long serialVersionUID = 4099532419561897428L;

  /** whether the action is supported. */
  protected boolean m_Supported;

  /**
   * Initializes the action.
   */
  public PrintFile() {
    setName("Print");
    setIcon("print.gif");
    m_Supported = true;
  }

  /**
   * Updates the action.
   */
  @Override
  protected void doUpdate() {
    setEnabled(m_Supported && (m_Owner.getCurrentFiles() != null) && (m_Owner.getCurrentFiles().length > 0));
  }

  /**
   * Invoked when an action occurs.
   *
   * @param e the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    for (File f: m_Owner.getCurrentFiles()) {
      try {
        Desktop.getDesktop().print(f.getAbsoluteFile());
      }
      catch (UnsupportedOperationException ex) {
        m_Supported = false;
        displayError("Failed to print file!", ex);
        break;
      }
      catch (Exception ex) {
        displayError("Failed to print file!", ex);
        break;
      }
    }
  }
}
