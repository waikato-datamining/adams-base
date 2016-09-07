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

/**
 * Gzip.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.filecommander;

import adams.core.io.GzipUtils;
import adams.gui.core.GUIHelper;

import javax.swing.SwingWorker;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * Compresses a single file using GZIP.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Gzip
  extends AbstractFileCommanderAction {

  private static final long serialVersionUID = -8374323161691034031L;

  /**
   * Instantiates the action.
   */
  public Gzip() {
    super();
    setName("GZIP");
  }

  /**
   * Invoked when an action occurs.
   *
   * @param e		the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    SwingWorker	worker;

    worker = new SwingWorker() {
      @Override
      protected Object doInBackground() throws Exception {
	File file = getOwner().getActive().getSelectedFile();
	String msg = GzipUtils.compress(file, 1024);
	if (msg != null) {
	  GUIHelper.showErrorMessage(getOwner(), "Failed to compress the following file using GZIP:\n" + file + "\n" + msg);
	}
	else {
	  getOwner().getActive().reload();
	  showStatus("Compressed " + file + " using GZIP");
	}
	return null;
      }
    };
    worker.execute();
  }

  /**
   * Updates the action.
   */
  @Override
  public void update() {
    File[]	files;

    files = getOwner().getActive().getSelectedFiles();
    setEnabled((files.length == 1) && files[0].isFile() && !files[0].getName().endsWith(GzipUtils.EXTENSION));
  }
}
