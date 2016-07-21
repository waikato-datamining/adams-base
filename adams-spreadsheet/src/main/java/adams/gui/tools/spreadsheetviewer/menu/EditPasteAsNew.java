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
 * EditPasteAsNew.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.spreadsheetviewer.menu;

import adams.data.conversion.StringToSpreadSheet;
import adams.data.io.input.CsvSpreadSheetReader;
import adams.data.io.input.SpreadSheetReader;
import adams.data.spreadsheet.SpreadSheet;
import adams.gui.core.GUIHelper;
import adams.gui.goe.GenericObjectEditorDialog;
import com.github.fracpete.jclipboardhelper.ClipboardHelper;

import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;

/**
 * Lets the user paste a spreadsheet from the clipboard.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class EditPasteAsNew
  extends AbstractSpreadSheetViewerMenuItemAction {

  /** for serialization. */
  private static final long serialVersionUID = 5235570137451285010L;

  /** the last spreadsheet reader used. */
  protected SpreadSheetReader m_LastReader;

  /**
   * Returns the caption of this action.
   * 
   * @return		the caption, null if not applicable
   */
  @Override
  protected String getTitle() {
    return "Paste as new...";
  }

  /**
   * Creates a new dialog.
   *
   * @return		the dialog
   */
  @Override
  protected GenericObjectEditorDialog createDialog() {
    GenericObjectEditorDialog	result;

    if (getParentDialog() != null)
      result = new GenericObjectEditorDialog(getParentDialog(), ModalityType.DOCUMENT_MODAL);
    else
      result = new GenericObjectEditorDialog(getParentFrame(), true);
    result.setTitle("Import of clipboard content");
    result.getGOEEditor().setClassType(SpreadSheetReader.class);
    result.getGOEEditor().setCanChangeClassInDialog(true);
    result.setCurrent(m_LastReader);
    result.setLocationRelativeTo(m_State);

    return result;
  }

  /**
   * Invoked when an action occurs.
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    StringToSpreadSheet		conv;
    String			msg;

    if (m_LastReader == null)
      m_LastReader = new CsvSpreadSheetReader();

    getDialog().setVisible(true);
    if (getDialog().getResult() != GenericObjectEditorDialog.APPROVE_OPTION)
      return;

    m_LastReader = (SpreadSheetReader) (getDialog().getCurrent());
    conv         = new StringToSpreadSheet();
    conv.setReader(m_LastReader);
    conv.setInput(ClipboardHelper.pasteStringFromClipboard());
    msg = conv.convert();
    if (msg == null)
      m_State.getTabbedPane().addTab("clipboard", (SpreadSheet) conv.getOutput());
    else
      GUIHelper.showErrorMessage(m_State, "Failed to parse clipboard content!\n" + msg);
    conv.cleanUp();
  }

  /**
   * Performs the actual update of the state of the action.
   */
  @Override
  protected void doUpdate() {
    setEnabled(ClipboardHelper.canPasteStringFromClipboard());
  }
}
