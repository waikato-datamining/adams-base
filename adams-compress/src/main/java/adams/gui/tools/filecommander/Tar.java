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
 * Tar.java
 * Copyright (C) 2024 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.filecommander;

import adams.core.Utils;
import adams.core.io.PlaceholderFile;
import adams.core.io.TarUtils;
import adams.gui.core.GUIHelper;

import javax.swing.SwingWorker;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * Creates a Tar file from the selected files.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Tar
  extends AbstractFileCommanderAction {

  private static final long serialVersionUID = -8374323161691034031L;

  /**
   * Instantiates the action.
   */
  public Tar() {
    super();
    setName("Tar");
  }

  /**
   * Invoked when an action occurs.
   *
   * @param e		the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    final String 	tar;
    String 		exts;
    SwingWorker		worker;

    exts = Utils.flatten(TarUtils.getSupportedCompressionExtensions(), ", ");
    tar  = GUIHelper.showInputDialog(getOwner(), "Please enter the name for the Tar file (compression suffixes: " + exts + ")", "compressed.tar");
    if (tar == null)
      return;

    worker = new SwingWorker() {
      @Override
      protected Object doInBackground() throws Exception {
	File[] files = getOwner().getActive().getFilePanel().getSelectedFiles();
	PlaceholderFile tarFile = new PlaceholderFile(getOwner().getActive().getFilePanel().getCurrentDir() + "/" + tar);
	String regExp = getOwner().getActive().getFilePanel().getCurrentDir().replace("/", ".").replace("\\", ".");
	String msg = TarUtils.compress(tarFile, files, regExp, 1024);
	if (msg != null) {
	  GUIHelper.showErrorMessage(getOwner(), "Failed to create Tar file:\n" + tarFile + "\n" + msg);
	}
	else {
	  getOwner().getActive().reload();
	  showStatus("Created Tar file: " + tarFile);
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
    boolean	local;

    local = getOwner().getActive().getFilePanel().isLocal();
    setEnabled(local && (getOwner().getActive().getFilePanel().getSelectedFiles().length > 0));
  }
}
