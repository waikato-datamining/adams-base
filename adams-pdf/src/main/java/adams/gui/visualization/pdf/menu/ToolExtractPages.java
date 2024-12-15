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
 * ToolExtractPages.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.pdf.menu;

import adams.core.UnorderedRange;
import adams.core.io.iTextPDF;
import adams.gui.core.GUIHelper;

import javax.swing.SwingWorker;
import java.awt.event.ActionEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Extracts a range of pages from the current PDF and adds them as a new document.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class ToolExtractPages
  extends AbstractPDFViewerAction {

  private static final long serialVersionUID = 2142193307909160127L;

  /**
   * Initializes the action.
   */
  @Override
  protected void initialize() {
    super.initialize();
    setName("Extract pages...");
    setIcon("pdf_extract_pages");
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

    pages = GUIHelper.showInputDialog(m_Owner, "Please enter range of pages to extract:", "1-last");
    if (pages == null)
      return;
    if (!UnorderedRange.isValid(pages, m_Owner.getCurrentPanel().getDocument().getNumberOfPages())) {
      GUIHelper.showErrorMessage(m_Owner, "Not a valid range: " + pages);
      return;
    }

    range = new UnorderedRange(pages);
    range.setMax(m_Owner.getCurrentPanel().getDocument().getNumberOfPages());
    worker = new SwingWorker() {
      byte[] m_Data = null;
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
	  m_Data = bos.toByteArray();
	return null;
      }
      @Override
      protected void done() {
	if (m_Data != null)
	  m_Owner.load(m_Data);
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
