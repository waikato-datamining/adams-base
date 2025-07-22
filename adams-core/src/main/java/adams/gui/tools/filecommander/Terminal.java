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
 * Terminal.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.tools.filecommander;

import adams.core.io.PlaceholderDirectory;
import adams.core.io.lister.LocalDirectoryLister;

import java.awt.event.ActionEvent;

/**
 * Opens a terminal in the active directory.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class Terminal
  extends AbstractFileCommanderAction {

  private static final long serialVersionUID = -4283657453144615239L;

  /**
   * Instantiates the action.
   */
  public Terminal() {
    super();
    setName("Terminal");
  }

  /**
   * Invoked when an action occurs.
   *
   * @param e the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    adams.core.management.Terminal.launch(
      new PlaceholderDirectory(m_Owner.getActive().getDirectory()).getAbsoluteFile());
  }

  /**
   * Updates the action.
   */
  @Override
  public void update() {
    setEnabled(m_Owner.getActive().getDirectoryLister() instanceof LocalDirectoryLister);
  }
}
