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
 * TableContentPanel.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.output;

import adams.data.io.output.CsvSpreadSheetWriter;
import adams.data.io.output.SpreadSheetWriter;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetSupporter;
import adams.gui.chooser.SpreadSheetFileChooser;
import adams.gui.core.BaseTable;
import adams.gui.core.BaseTableWithButtons;
import adams.gui.core.JTableHelper;
import com.github.fracpete.jclipboardhelper.ClipboardHelper;
import com.googlecode.jfilechooserbookmarks.gui.BaseScrollPane;

import javax.swing.JComponent;
import javax.swing.JTable;
import java.awt.BorderLayout;
import java.io.File;
import java.io.StringWriter;

/**
 * Panel for exporting the table as spreadsheet.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TableContentPanel
  extends AbstractOutputPanelWithPopupMenu<SpreadSheetFileChooser>
  implements SpreadSheetSupporter {

  private static final long serialVersionUID = 8183731075946484533L;

  /** the actual component. */
  protected JComponent m_Component;

  /**
   * Initializes the panel with the specified textual component.
   *
   * @param comp		the component to embed
   * @param useScrollPane	whether to use a scroll pane
   */
  public TableContentPanel(JTable comp, boolean useScrollPane) {
    super();
    initGUI(comp, useScrollPane);
  }

  /**
   * Initializes the panel with the specified textual component.
   *
   * @param comp		the component to embed
   * @param useScrollPane	whether to use a scroll pane
   */
  public TableContentPanel(BaseTable comp, boolean useScrollPane) {
    super();
    initGUI(comp, useScrollPane);
  }

  /**
   * Initializes the panel with the specified textual component.
   *
   * @param comp		the component to embed
   * @param useScrollPane	whether to use a scroll pane
   */
  public TableContentPanel(BaseTableWithButtons comp, boolean useScrollPane) {
    super();
    initGUI(comp, useScrollPane);
  }

  /**
   * Initializes the panel with the specified component.
   *
   * @param comp		the component to embed
   * @param useScrollPane	whether to use a scroll pane
   */
  protected void initGUI(JComponent comp, boolean useScrollPane) {
    m_Component = comp;

    if (useScrollPane)
      getContentPanel().add(new BaseScrollPane(m_Component), BorderLayout.CENTER);
    else
      getContentPanel().add(m_Component, BorderLayout.CENTER);
  }

  /**
   * Returns the embedded component.
   *
   * @return		the component
   */
  public JComponent getComponent() {
    return m_Component;
  }

  /**
   * Creates the filechooser to use.
   *
   * @return		the filechooser
   */
  @Override
  protected SpreadSheetFileChooser createFileChooser() {
    SpreadSheetFileChooser	result;

    result = new SpreadSheetFileChooser();
    result.setAcceptAllFileFilterUsed(false);
    result.setAutoAppendExtension(true);

    return result;
  }

  /**
   * Saves the content to the specified file.
   *
   * @param file	the file to save to
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String save(File file) {
    String		result;
    SpreadSheet 	content;
    SpreadSheetWriter	writer;

    result = null;

    content = toSpreadSheet();
    if (content == null)
      result = "Unhandled component: " + m_Component.getClass().getName();

    if (result == null) {
      writer = m_FileChooser.getWriter();
      if (!writer.write(content, file))
        result = "Failed to write data to: " + file;
    }

    return result;
  }

  /**
   * Returns the content as spreadsheet.
   *
   * @return		the content
   */
  public SpreadSheet toSpreadSheet() {
    SpreadSheet result;

    if (m_Component instanceof BaseTable)
      result = ((BaseTable) m_Component).toSpreadSheet();
    else if (m_Component instanceof BaseTableWithButtons)
      result = ((BaseTableWithButtons) m_Component).toSpreadSheet();
    else if (m_Component instanceof JTable)
      result = JTableHelper.toSpreadSheet((JTable) m_Component);
    else
      result = null;

    return result;
  }

  /**
   * Returns whether copying to the clipboard is supported.
   *
   * @return		true if copy to clipboard is supported
   * @see		#copyToClipboard()
   */
  public boolean canCopyToClipboard() {
    return true;
  }

  /**
   * Copies the content to the clipboard.
   *
   * @see 		#canCopyToClipboard()
   */
  public void copyToClipboard() {
    SpreadSheet			sheet;
    CsvSpreadSheetWriter	writer;
    StringWriter		swriter;

    swriter = new StringWriter();
    sheet   = toSpreadSheet();
    writer  = new CsvSpreadSheetWriter();
    writer.setSeparator("\\t");
    writer.write(sheet, swriter);
    ClipboardHelper.copyToClipboard(swriter.toString());
  }
}
