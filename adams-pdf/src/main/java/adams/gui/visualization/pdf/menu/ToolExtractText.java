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
 * ToolExtractText.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.pdf.menu;

import adams.core.UnorderedRange;
import adams.core.io.PDFBox;
import adams.core.io.iTextPDF;
import adams.gui.core.GUIHelper;
import adams.gui.dialog.TextDialog;

import javax.swing.SwingWorker;
import java.awt.event.ActionEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Extracts text from a range of pages of the current PDF and displays it.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class ToolExtractText
  extends AbstractPDFViewerAction {

  private static final long serialVersionUID = 2142193307909160127L;

  /**
   * Initializes the action.
   */
  @Override
  protected void initialize() {
    super.initialize();
    setName("Extract text...");
    setIcon("pdf_extract_text");
  }

  /**
   * Invoked when an action occurs.
   *
   * @param e		the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    String			pages;
    final UnorderedRange 	range;
    SwingWorker			worker;

    pages = GUIHelper.showInputDialog(m_Owner, "Please enter range of pages to extract the text from:", "1-last");
    if (pages == null)
      return;
    if (!UnorderedRange.isValid(pages, m_Owner.getCurrentPanel().getDocument().getNumberOfPages())) {
      GUIHelper.showErrorMessage(m_Owner, "Not a valid range: " + pages);
      return;
    }

    range = new UnorderedRange(pages);
    range.setMax(m_Owner.getCurrentPanel().getDocument().getNumberOfPages());
    worker = new SwingWorker() {
      String m_Data = null;
      @Override
      protected Object doInBackground() throws Exception {
	ByteArrayOutputStream bos = new ByteArrayOutputStream();
	m_Owner.getCurrentPanel().getDocument().saveToOutputStream(bos);
	ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
	bos.close();
	bos = new ByteArrayOutputStream();
	String result = iTextPDF.extractPages(m_Owner.getCurrentPanel(), bis, range, bos);
	if (result != null)
	  m_Owner.getCurrentPanel().getLogger().severe(result);
	else
	  m_Data = PDFBox.extractText(bos.toByteArray());
	return null;
      }
      @Override
      protected void done() {
	if (m_Data != null) {
	  TextDialog textDlg;
	  if (GUIHelper.getParentDialog(m_Owner) != null)
	    textDlg = new TextDialog(GUIHelper.getParentDialog(m_Owner));
	  else
	    textDlg = new TextDialog(GUIHelper.getParentFrame(m_Owner));
	  textDlg.setUpdateParentTitle(false);
	  textDlg.setTitle("Text of pages: " + range.getRange());
	  textDlg.setDefaultCloseOperation(TextDialog.DISPOSE_ON_CLOSE);
	  textDlg.setContent(m_Data);
	  GUIHelper.pack(textDlg, GUIHelper.getDefaultTinyDialogDimension(), GUIHelper.getDefaultDialogDimension());
	  textDlg.setLocationRelativeTo(m_Owner);
	  textDlg.setVisible(true);
	}
	super.done();
      }
    };
    worker.execute();
  }

  /**
   * Updates the action.
   */
  @Override
  public void update() {
    setEnabled(
      (m_Owner != null)
	&& (m_Owner.getCurrentPanel() != null)
	&& m_Owner.getCurrentPanel().hasDocument());
  }
}
