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
 * SpreadSheet.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.source;

import adams.core.DateUtils;
import adams.data.conversion.Conversion;
import adams.data.conversion.MultiConversion;
import adams.data.conversion.SpreadSheetToWekaInstances;
import adams.data.conversion.StringToSpreadSheet;
import adams.data.io.input.SimpleCsvSpreadSheetReader;
import adams.data.io.input.SpreadSheetReader;
import adams.gui.core.GUIHelper;
import adams.gui.goe.GenericObjectEditorDialog;
import adams.gui.tools.wekainvestigator.data.MemoryContainer;
import com.github.fracpete.jclipboardhelper.ClipboardHelper;
import weka.core.Instances;

import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.util.Date;

/**
 * Parses content on the clipboard.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Clipboard
  extends AbstractSource {

  private static final long serialVersionUID = 5646388990155938153L;

  /** the last spreadsheet reader used. */
  protected SpreadSheetReader m_LastReader;

  /**
   * Instantiates the action.
   */
  public Clipboard() {
    super();
    setName("Clipboard...");
    setIcon("paste.gif");
  }

  /**
   * Creates a new dialog.
   *
   * @return		the dialog
   */
  protected GenericObjectEditorDialog createDialog() {
    GenericObjectEditorDialog	result;

    if (getOwner().getParentDialog() != null)
      result = new GenericObjectEditorDialog(getOwner().getParentDialog(), ModalityType.DOCUMENT_MODAL);
    else
      result = new GenericObjectEditorDialog(getOwner().getParentFrame(), true);
    result.setDefaultCloseOperation(GenericObjectEditorDialog.DISPOSE_ON_CLOSE);
    result.setTitle("Import of clipboard content");
    result.getGOEEditor().setClassType(SpreadSheetReader.class);
    result.getGOEEditor().setCanChangeClassInDialog(true);
    result.setCurrent(m_LastReader);
    result.setLocationRelativeTo(getOwner());

    return result;
  }

  /**
   * Invoked when an action occurs.
   *
   * @param e		the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    String			content;
    GenericObjectEditorDialog	dialog;
    StringToSpreadSheet 	str2sheet;
    SpreadSheetToWekaInstances	sheet2weka;
    MultiConversion		multi;
    String			msg;
    Instances			data;

    if (!ClipboardHelper.canPasteStringFromClipboard()) {
      GUIHelper.showErrorMessage(getOwner(), "Clipboard empty!");
      return;
    }

    if (m_LastReader == null) {
      m_LastReader = new SimpleCsvSpreadSheetReader();
      ((SimpleCsvSpreadSheetReader) m_LastReader).setNoHeader(true);
      ((SimpleCsvSpreadSheetReader) m_LastReader).setSeparator("\\t");
    }

    dialog = createDialog();
    dialog.setVisible(true);
    if (dialog.getResult() != GenericObjectEditorDialog.APPROVE_OPTION)
      return;

    m_LastReader = (SpreadSheetReader) (dialog.getCurrent());
    str2sheet = new StringToSpreadSheet();
    str2sheet.setReader(m_LastReader);
    str2sheet.setInput(ClipboardHelper.pasteStringFromClipboard());
    sheet2weka = new SpreadSheetToWekaInstances();
    multi = new MultiConversion();
    multi.setSubConversions(new Conversion[]{str2sheet, sheet2weka});
    multi.setInput(ClipboardHelper.pasteStringFromClipboard());
    msg  = multi.convert();
    data = null;
    if (msg == null)
      data = (Instances) multi.getOutput();
    else
      GUIHelper.showErrorMessage(getOwner(), "Failed to parse clipboard content!\n" + msg);
    multi.cleanUp();

    data.setRelationName("Clipboard - " + DateUtils.getTimestampFormatter().format(new Date()));
    addData(new MemoryContainer(data));
  }
}
