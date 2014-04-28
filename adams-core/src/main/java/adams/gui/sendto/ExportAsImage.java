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
 * ExportAsImage.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.sendto;

import java.io.File;
import java.util.logging.Level;

import javax.swing.JComponent;

import adams.core.io.PlaceholderFile;
import adams.gui.print.JComponentWriter;
import adams.gui.print.JComponentWriterFileChooser;

/**
 * Action for exporting a graphical component as image.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ExportAsImage
  extends AbstractSendToAction {

  /** for serialization. */
  private static final long serialVersionUID = -5286281737195775697L;

  /**
   * Returns the short description of the sendto action.
   * Description gets used for menu items.
   *
   * @return		the short description
   */
  @Override
  public String getAction() {
    return "Export as image";
  }

  /**
   * Returns the file name of the icon.
   *
   * @return		the filename or null if no icon available
   */
  @Override
  public String getIconName() {
    return "landscape.gif";
  }

  /**
   * Returns the classes that the action accepts.
   *
   * @return		the accepted classes
   */
  @Override
  public Class[] accepts() {
    return new Class[]{JComponent.class};
  }

  /**
   * Performs the actual sending/forwarding/processing of the data.
   *
   * @param o		the object to send
   * @return		null if everything OK, otherwise error message
   */
  @Override
  public String send(Object o) {
    String			result;
    int				retVal;
    JComponent			component;
    JComponentWriterFileChooser fileChooser;
    File			file;
    JComponentWriter		writer;
    String			msg;

    result = null;

    if (o instanceof JComponent) {
      component   = (JComponent) o;
      fileChooser = new JComponentWriterFileChooser();
      // display save dialog
      retVal = fileChooser.showSaveDialog(component);
      if (retVal != JComponentWriterFileChooser.APPROVE_OPTION)
        result = "Export canceled by user!";

      if (result == null) {
	// save the file
	try {
	  file   = fileChooser.getSelectedFile().getAbsoluteFile();
	  writer = fileChooser.getWriter();
	  writer.setComponent(component);
	  writer.setFile(new PlaceholderFile(file));
	  writer.toOutput();
	}
	catch (Exception e) {
	  msg    = "Failed to export image: ";
	  result = msg + e;
	  getLogger().log(Level.SEVERE, msg, e);
	}
      }
    }
    else {
      result = "Cannot export object as image: " + o.getClass();
    }

    return result;
  }
}
