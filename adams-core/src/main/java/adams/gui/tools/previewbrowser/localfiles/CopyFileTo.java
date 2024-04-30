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
 * CopyFileTo.java
 * Copyright (C) 2024 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.previewbrowser.localfiles;

import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.core.logging.LoggingLevel;
import adams.gui.chooser.DirectoryChooserFactory;
import adams.gui.chooser.FileChooser;
import adams.gui.core.ConsolePanel;

import java.awt.event.ActionEvent;
import java.io.File;

/**
 * Copies the file(s) to a location the user specifies.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class CopyFileTo
  extends AbstractLocalFilesAction {

  private static final long serialVersionUID = 4099532419561897428L;

  /** the last directory a file was copied to. */
  protected File m_LastDir;

  /**
   * Initializes the action.
   */
  public CopyFileTo() {
    setName("Copy file to...");
    setIcon("copy_file.gif");
  }

  /**
   * Updates the action.
   */
  @Override
  protected void doUpdate() {
    setEnabled((m_Owner.getCurrentFiles() != null) && (m_Owner.getCurrentFiles().length >= 1));
    if (isEnabled()) {
      if (m_Owner.getCurrentFiles().length == 1)
	setName("Copy file to...");
      else
	setName("Copy " + m_Owner.getCurrentFiles().length + " files to...");
    }
  }

  /**
   * Invoked when an action occurs.
   *
   * @param e the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    FileChooser		chooser;
    int			retVal;

    if (m_LastDir == null)
      m_LastDir = new PlaceholderFile(".");
    chooser = DirectoryChooserFactory.createChooser(m_LastDir);
    retVal  = chooser.showOpenDialog(getOwner());
    if (retVal != DirectoryChooserFactory.APPROVE_OPTION)
      return;

    m_LastDir = chooser.getSelectedFile();
    for (File current: m_Owner.getCurrentFiles()) {
      try {
	ConsolePanel.getSingleton().append(LoggingLevel.INFO, "Copying '" + current.getAbsolutePath() + "' to '" + m_LastDir + "'\n");
	if (!FileUtils.copy(current, m_LastDir))
	  ConsolePanel.getSingleton().append(LoggingLevel.SEVERE, "Failed copying '" + current.getAbsolutePath() + "' to '" + m_LastDir + "'!\n");
      }
      catch (Exception ex) {
	ConsolePanel.getSingleton().append("Failed copying '" + current.getAbsolutePath() + "' to '" + m_LastDir + "'!\n", ex);
      }
    }
  }
}
