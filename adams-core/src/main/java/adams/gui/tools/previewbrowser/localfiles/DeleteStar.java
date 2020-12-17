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
 * DeleteStar.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.previewbrowser.localfiles;

import adams.core.base.BaseRegExp;
import adams.core.io.FileUtils;
import adams.core.io.lister.LocalDirectoryLister;

import java.awt.event.ActionEvent;
import java.io.File;

/**
 * Deletes all files with the same prefix as the selected file.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class DeleteStar
  extends AbstractLocalFilesAction {

  private static final long serialVersionUID = 4099532419561897428L;

  /**
   * Initializes the action.
   */
  public DeleteStar() {
    setName("Delete");
    setIcon("delete_all.gif");
  }

  /**
   * Updates the action.
   */
  @Override
  protected void doUpdate() {
    setEnabled((m_Owner.getCurrentFiles() != null) && (m_Owner.getCurrentFiles().length > 0));
    if (isEnabled()) {
      if (m_Owner.getCurrentFiles().length == 1)
	setName("Delete (same prefix)");
      else
        setName("Delete " + m_Owner.getCurrentFiles().length + " files (same prefix)");
    }
  }

  /**
   * Invoked when an action occurs.
   *
   * @param e the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    LocalDirectoryLister 	lister;
    String			regexp;
    String[]			files;

    lister = new LocalDirectoryLister();
    lister.setWatchDir(m_Owner.getDirPanel().getCurrent().getAbsolutePath());
    lister.setListDirs(false);
    lister.setListFiles(true);
    for (File file: m_Owner.getCurrentFiles()) {
      regexp = FileUtils.replaceExtension(file.getName(), "") + "\\..*";
      lister.setRegExp(new BaseRegExp(regexp));
      files = lister.list();
      for (String f: files) {
	try {
	  if (!FileUtils.delete(f)) {
	    displayError("Failed to delete file:\n" + f);
	    break;
	  }
	}
	catch (Exception ex) {
	  displayError("Failed to delete file:\n" + f, ex);
	  break;
	}
      }
    }
    m_Owner.reload();
  }
}
