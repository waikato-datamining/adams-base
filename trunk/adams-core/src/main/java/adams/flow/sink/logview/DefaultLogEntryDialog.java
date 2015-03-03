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
 * DefaultLogEntryDialog.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.sink.logview;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dialog.ModalityType;

import adams.db.LogEntry;
import adams.gui.core.BaseDialog;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.dialog.TextPanel;

/**
 * Simply shows the content of the {@link LogEntry} in a text area.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DefaultLogEntryDialog
  extends AbstractLogEntryDialog {

  /** for serialization. */
  private static final long serialVersionUID = 1513413650025226239L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Simply shows the message in a text area.";
  }
  
  /**
   * Creates the dialog.
   * 
   * @param entry	the entry to display
   * @return		the dialog
   */
  @Override
  protected BaseDialog createDialog(LogEntry entry) {
    ApprovalDialog	result;
    TextPanel		textPanel;

    result = new ApprovalDialog((Dialog) null, ModalityType.DOCUMENT_MODAL);
    result.setTitle(createTitle(entry));
    result.setDefaultCloseOperation(ApprovalDialog.DISPOSE_ON_CLOSE);
    result.setApproveVisible(true);
    result.setCancelVisible(false);
    result.setDiscardVisible(false);
    
    textPanel = new TextPanel();
    result.getContentPane().add(textPanel, BorderLayout.CENTER);
    textPanel.setContent(toString(entry));
    
    return result;
  }
}
