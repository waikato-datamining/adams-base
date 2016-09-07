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
 * Zip.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.filecommander;

import adams.core.io.PlaceholderFile;
import adams.core.io.ZipUtils;
import adams.gui.core.GUIHelper;

import java.awt.event.ActionEvent;
import java.io.File;

/**
 * Creates a ZIP file from the selected files.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Zip
  extends AbstractFileCommanderAction {

  private static final long serialVersionUID = -8374323161691034031L;

  /**
   * Instantiates the action.
   */
  public Zip() {
    super();
    setName("ZIP");
  }

  /**
   * Invoked when an action occurs.
   *
   * @param e		the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    String		zip;
    PlaceholderFile	zipFile;
    String		regExp;
    File[]		files;
    String		msg;

    zip = GUIHelper.showInputDialog(getOwner(), "Please enter the name for the ZIP file", "compressed.zip");
    if (zip == null)
      return;

    files   = getOwner().getActive().getSelectedFiles();
    zipFile = new PlaceholderFile(getOwner().getActive().getCurrentDir() + "/" + zip);
    regExp  = getOwner().getActive().getCurrentDir().getAbsolutePath().replace("/", ".").replace("\\", ".");
    msg     = ZipUtils.compress(zipFile, files, regExp, 1024);
    if (msg != null)
      GUIHelper.showErrorMessage(getOwner(), "Failed to create ZIP file:\n" + zipFile + "\n" + msg);
    else
      getOwner().getActive().reload();
  }

  /**
   * Updates the action.
   */
  @Override
  public void update() {
    setEnabled(getOwner().getActive().getSelectedFiles().length > 0);
  }
}
