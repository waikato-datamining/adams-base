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
 * PDFPrinter.java
 * Copyright (C) 2015-2022 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.sendto;

import adams.core.io.IcePDF;
import adams.core.io.PDFBox;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.icepdf.core.pobjects.Document;

import java.io.File;

/**
 * Action for sending PDF to the printer.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class PDFPrinter
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
    return "Printer (PDF)";
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
	Document.class,
	PDDocument.class,
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
    File                file;
    String		msg;

    result = null;

    if (o instanceof Document) {
      file = SendToActionUtils.nextTmpFile("pdfviewer", "pdf");
      msg  = IcePDF.saveTo((Document) o, file);
      if (msg == null) {
	if (!PDFBox.printWithDialog(file))
	  result = "Failed to print PDF document: " + file;
      }
      else {
	result = "Failed to save PDF document to: " + file + "\n" + msg;
      }
    }
    else if (o instanceof org.apache.pdfbox.pdmodel.PDDocument) {
      if (!PDFBox.print((org.apache.pdfbox.pdmodel.PDDocument) o))
	result = "Failed to print PDF document";
    }
    else {
      result = "Cannot print object: " + o.getClass();
    }

    return result;
  }
}
