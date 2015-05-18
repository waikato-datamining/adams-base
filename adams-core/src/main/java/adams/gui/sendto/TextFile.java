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
 * TextFile.java
 * Copyright (C) 2013-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.sendto;

import adams.core.io.FileUtils;
import adams.flow.sink.TextSupplier;
import adams.gui.chooser.BaseFileChooser;
import adams.gui.chooser.TextFileChooser;
import adams.gui.core.ExtensionFileFilter;

/**
 * Action for saving text in a text file.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TextFile
  extends AbstractSendToAction {

  /** for serialization. */
  private static final long serialVersionUID = -6880030063760028278L;

  /**
   * Returns the short description of the sendto action.
   * Description gets used for menu items.
   *
   * @return		the short description
   */
  @Override
  public String getAction() {
    return "Text file";
  }

  /**
   * Returns the file name of the icon.
   *
   * @return		the filename or null if no icon available
   */
  @Override
  public String getIconName() {
    return "editor.gif";
  }

  /**
   * Returns the classes that the action accepts.
   *
   * @return		the accepted classes
   */
  @Override
  public Class[] accepts() {
    return new Class[]{String.class};
  }

  /**
   * Performs the actual sending/forwarding/processing of the data.
   *
   * @param o		the object to send
   * @return		null if everything OK, otherwise error message
   */
  @Override
  public String send(Object o) {
    String		result;
    TextFileChooser	fileChooser;
    ExtensionFileFilter	filter;
    int			retVal;
    String 		msg;

    result = null;

    if (o instanceof String) {
      fileChooser = new TextFileChooser();
      filter      = null;
      if (getOwner() instanceof TextSupplier)
	filter = ((TextSupplier) getOwner()).getCustomTextFileFilter();
      if (filter != null) {
	fileChooser.resetChoosableFileFilters();
	fileChooser.addChoosableFileFilter(filter);
	fileChooser.setFileFilter(filter);
	fileChooser.setDefaultExtension(filter.getExtensions()[0]);
      }
      retVal = fileChooser.showSaveDialog(null);
      if (retVal != BaseFileChooser.APPROVE_OPTION)
        return "Save dialog canceled!";
      msg = FileUtils.writeToFileMsg(fileChooser.getSelectedFile().getAbsolutePath(), o, false, fileChooser.getEncoding());
      if (msg != null)
	result = "Failed to write text to '" + fileChooser.getSelectedFile() + "':\n" + msg;
    }
    else {
      result = "Cannot save as text: " + o.getClass();
    }

    return result;
  }
}
