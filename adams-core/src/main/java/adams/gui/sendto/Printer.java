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
 * Printer.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 * Copyright (C) 1995, 2008, Oracle and/or its affiliates.
 */
package adams.gui.sendto;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.logging.Level;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.text.JTextComponent;

import adams.core.License;
import adams.core.annotation.MixedCopyright;

/**
 * Action for sending text to the printer.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
@MixedCopyright(
    copyright = "1995, 2008, Oracle and/or its affiliates",
    license = License.BSD3,
    url = "http://download.oracle.com/javase/tutorial/2d/printing/examples/PrintUIWindow.java"
)
public class Printer
  extends AbstractSendToAction {

  /** for serialization. */
  private static final long serialVersionUID = -6357616730945070639L;

  /**
   * Returns the short description of the sendto action.
   * Description gets used for menu items.
   *
   * @return		the short description
   */
  @Override
  public String getAction() {
    return "Printer";
  }

  /**
   * Returns the file name of the icon.
   *
   * @return		the filename or null if no icon available
   */
  @Override
  public String getIconName() {
    return "print.gif";
  }

  /**
   * Returns the classes that the action accepts.
   *
   * @return		the accepted classes
   */
  @Override
  public Class[] accepts() {
    return new Class[]{
	Component.class,
	JComponent.class,
	JTextComponent.class,
	JTable.class,
    };
  }

  /**
   * Performs the actual sending/forwarding/processing of the data.
   * <br><br>
   * Code for printing java.awt.Component taken from
   * <a href="http://download.oracle.com/javase/tutorial/2d/printing/examples/PrintUIWindow.java" target="_blank">here</a>.
   *
   * @param o		the object to send
   * @return		null if everything OK, otherwise error message
   */
  @Override
  public String send(Object o) {
    String		result;
    JTextComponent	text;
    final Component	comp;
    PrinterJob 		job;
    String		msg;

    result = null;

    if (o instanceof JTextComponent) {
      text = (JTextComponent) o;
      try {
	text.print(null, null, true, null, null, true);
      }
      catch (Exception e) {
	msg = "Failed to print:";
	getLogger().log(Level.SEVERE, msg, e);
	result = msg + "\n" + e;
      }
    }
    else if (o instanceof JTable) {
      try {
	if (!((JTable) o).print())
	  result = "Printing canceled by user!";
      }
      catch (Exception e) {
	msg = "Failed to print:";
	getLogger().log(Level.SEVERE, msg, e);
	result = msg + "\n" + e;
      }
    }
    else if ((o instanceof Component) || (o instanceof JComponent)) {
      comp = (Component) o;
      job = PrinterJob.getPrinterJob();
      job.setPrintable(new Printable() {
        public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
          if (pageIndex > 0)
            return NO_SUCH_PAGE;
          ((Graphics2D) graphics).translate(pageFormat.getImageableX(), pageFormat.getImageableY());
          comp.printAll(graphics);
          return PAGE_EXISTS;
        }
      });
      boolean ok = job.printDialog();
      if (ok) {
	try {
	  job.print();
	}
	catch (PrinterException e) {
	  msg = "Failed to print:";
	  getLogger().log(Level.SEVERE, msg, e);
	  result = msg + "\n" + e;
	}
      }
      else {
	result = "Failed to display printer dialog!";
      }
    }
    else {
      result = "Cannot print object: " + o.getClass();
    }

    return result;
  }
}
