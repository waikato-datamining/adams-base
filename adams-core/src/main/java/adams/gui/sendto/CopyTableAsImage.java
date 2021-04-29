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
 * CopyTableAsImage.java
 * Copyright (C) 2021 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.sendto;

import adams.gui.core.JTableSupporter;
import com.github.fracpete.jclipboardhelper.ClipboardHelper;

import javax.swing.JTable;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.logging.Level;

/**
 * Action for copying a table to the clipboard as image.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class CopyTableAsImage
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
    return "Copy table as image";
  }

  /**
   * Returns the file name of the icon.
   *
   * @return		the filename or null if no icon available
   */
  @Override
  public String getIconName() {
    return "copy.gif";
  }

  /**
   * Returns the classes that the action accepts.
   *
   * @return		the accepted classes
   */
  @Override
  public Class[] accepts() {
    return new Class[]{JTable.class, JTableSupporter.class};
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
    JTable		table;
    String		msg;
    int			height;
    int			headerHeight;
    BufferedImage	img;
    Graphics2D		g;

    result = null;

    table = null;
    if (o instanceof JTable) {
      table = (JTable) o;
    }
    else if (o instanceof JTableSupporter) {
      table = ((JTableSupporter) o).getTable();
    }

    if (table != null) {
      // save the file
      try {
	headerHeight = (int) table.getTableHeader().getPreferredSize().getHeight();
	height       = table.getHeight() + headerHeight;
	img          = new BufferedImage(table.getWidth(), height, BufferedImage.TYPE_INT_RGB);
	g            = img.createGraphics();
	g.setPaintMode();
	g.setColor(Color.WHITE);
	g.fillRect(0, 0, table.getWidth(), height);
	table.getTableHeader().paint(g);
	g.translate(0, headerHeight);
	table.printAll(g);
	g.dispose();
	ClipboardHelper.copyToClipboard(img);
      }
      catch (Exception e) {
	msg    = "Failed to copy table as image: ";
	result = msg + e;
	getLogger().log(Level.SEVERE, msg, e);
      }
    }
    else {
      result = "Cannot export table as image: " + o.getClass();
    }

    return result;
  }
}
