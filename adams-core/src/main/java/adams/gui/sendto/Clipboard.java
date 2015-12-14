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
 * Clipboard.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.sendto;

import adams.gui.core.GUIHelper;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.text.JTextComponent;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

/**
 * Action for copying text/image to clipboard.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Clipboard
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
    return "Clipboard";
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
    return new Class[]{
	String.class,
	JComponent.class,
	JTextComponent.class,
	JTable.class,
    };
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
    JTextComponent	text;
    JTable		table;
    Action 		copy;
    ActionEvent 	event;
    JComponent		comp;
    BufferedImage 	img;
    Graphics 		g;

    result = null;

    if (o instanceof String) {
      GUIHelper.copyToClipboard((String) o);
    }
    else if (o instanceof JTextComponent) {
      text = (JTextComponent) o;
      GUIHelper.copyToClipboard(text.getText());
    }
    else if (o instanceof JTable) {
      table = (JTable) o;
      copy  = table.getActionMap().get("copy");
      event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "");
      copy.actionPerformed(event);
    }
    else if (o instanceof JComponent) {
      comp = (JComponent) o;
      img  = new BufferedImage(comp.getWidth(), comp.getHeight(), BufferedImage.TYPE_INT_RGB);
      g    = img.getGraphics();
      g.setPaintMode();
      g.fillRect(0, 0, comp.getWidth(), comp.getHeight());
      comp.printAll(g);
      GUIHelper.copyToClipboard(img);
    }
    else {
      result = "Cannot copy object: " + o.getClass();
    }

    return result;
  }
}
