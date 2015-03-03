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
 * EmailRecipient.java
 * Copyright (C) 2011-2012 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.sendto;

import java.awt.Dialog.ModalityType;
import java.io.File;

import adams.core.io.PlaceholderFile;
import adams.core.net.EmailHelper;
import adams.env.Environment;
import adams.gui.dialog.ComposeEmailDialog;

/**
 * Action for sending files as emails.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class EmailRecipient
  extends AbstractSendToAction {

  /** for serialization. */
  private static final long serialVersionUID = 5177606045614245912L;

  /**
   * Returns the short description of the sendto action.
   * Description gets used for menu items.
   *
   * @return		the short description
   */
  @Override
  public String getAction() {
    return "Email recipient";
  }

  /**
   * Returns the file name of the icon.
   *
   * @return		the filename or null if no icon available
   */
  @Override
  public String getIconName() {
    return "email.png";
  }

  /**
   * Returns the classes that the action accepts.
   *
   * @return		the accepted classes
   */
  @Override
  public Class[] accepts() {
    if (EmailHelper.isEnabled())
      return new Class[]{
	File.class,
	File[].class,
	PlaceholderFile.class,
	PlaceholderFile[].class,
	String.class};
    else
      return new Class[]{};
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
    ComposeEmailDialog	dialog;
    PlaceholderFile[]	files;
    File[]		tmpFiles;
    int			i;
    String		body;
    StringBuilder	subject;

    dialog = new ComposeEmailDialog(Environment.getInstance().getApplicationFrame());
    dialog.setModalityType(ModalityType.DOCUMENT_MODAL);
    dialog.setCloseOnSend(true);

    files = new PlaceholderFile[0];
    body  = "";
    if (o instanceof File) {
      files = new PlaceholderFile[]{new PlaceholderFile((File) o)};
    }
    else if (o instanceof File[]) {
      tmpFiles = (File[]) o;
      files    = new PlaceholderFile[tmpFiles.length];
      for (i = 0; i < tmpFiles.length; i++)
	files[i] = new PlaceholderFile(tmpFiles[i]);
    }
    else if (o instanceof PlaceholderFile) {
      files = new PlaceholderFile[]{(PlaceholderFile) o};
    }
    else if (o instanceof PlaceholderFile[]) {
      files = (PlaceholderFile[]) o;
    }
    else if (o instanceof String) {
      body = (String) o;
    }
    else {
      throw new IllegalArgumentException("Unhandled class type: " + o.getClass());
    }

    subject = new StringBuilder();
    for (i = 0; i < files.length; i++) {
      if (i > 0)
	subject.append(", ");
      subject.append(files[i].getName());
    }

    dialog.setAttachments(files);
    dialog.setSubject(subject.toString());
    dialog.setBody(body);
    dialog.setLocationRelativeTo(null);
    dialog.setVisible(true);

    result = dialog.getLastSendResult();
    if ((result == null) && dialog.getDialogClosedByUser())
      result = "Email sending canceled by user!";

    return result;
  }
}
