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
 * TableResultsPanel.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekamultiexperimenter.analysis;

import adams.core.io.FileUtils;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.gui.chooser.BaseFileChooser;
import adams.gui.core.BaseButton;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseSplitPane;
import adams.gui.core.BaseTextAreaWithButtons;
import adams.gui.core.ExtensionFileFilter;
import adams.gui.core.Fonts;
import adams.gui.core.GUIHelper;
import adams.gui.core.SpreadSheetTable;
import adams.gui.core.SpreadSheetTableModel;
import com.github.fracpete.jclipboardhelper.ClipboardHelper;
import weka.experiment.ResultMatrixAdamsCSV;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

/**
 * Displays the results in a table.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class TableResultsPanel
  extends AbstractResultsPanel {

  private static final long serialVersionUID = 3608852939358175057L;

  /** the split pane. */
  protected BaseSplitPane m_SplitPane;

  /** the table with the statistics. */
  protected SpreadSheetTable m_TableResults;

  /** for displaying the results. */
  protected BaseTextAreaWithButtons m_TextAreaKey;

  /** the copy button. */
  protected BaseButton m_ButtonCopy;

  /** the save button. */
  protected BaseButton m_ButtonSave;

  /** the filechooser for saving the output. */
  protected BaseFileChooser m_FileChooser;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    ExtensionFileFilter filter;

    super.initialize();

    filter        = ExtensionFileFilter.getTextFileFilter();
    m_FileChooser = new BaseFileChooser();
    m_FileChooser.addChoosableFileFilter(filter);
    m_FileChooser.setFileFilter(filter);
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setLayout(new BorderLayout());

    m_SplitPane = new BaseSplitPane(BaseSplitPane.VERTICAL_SPLIT);
    m_SplitPane.setResizeWeight(1.0);
    m_SplitPane.setUISettingsParameters(getClass(), "ResultsDivider");
    add(m_SplitPane, BorderLayout.CENTER);

    m_TableResults = new SpreadSheetTable(new SpreadSheetTableModel());
    m_TableResults.setShowSimpleCellPopupMenu(true);
    m_TableResults.setShowSimpleHeaderPopupMenu(true);
    m_SplitPane.setTopComponent(new BaseScrollPane(m_TableResults));

    m_TextAreaKey = new BaseTextAreaWithButtons();
    m_TextAreaKey.setTextFont(Fonts.getMonospacedFont());
    m_SplitPane.setBottomComponent(m_TextAreaKey);

    m_ButtonCopy = new BaseButton("Copy", GUIHelper.getIcon("copy.gif"));
    m_ButtonCopy.addActionListener((ActionEvent e) -> {
      if (m_TextAreaKey.getSelectedText() != null)
	ClipboardHelper.copyToClipboard(m_TextAreaKey.getSelectedText());
      else
	ClipboardHelper.copyToClipboard(m_TextAreaKey.getText());
    });
    m_TextAreaKey.addToButtonsPanel(m_ButtonCopy);

    m_ButtonSave = new BaseButton("Save...", GUIHelper.getIcon("save.gif"));
    m_ButtonSave.addActionListener((ActionEvent e) -> {
      int retVal = m_FileChooser.showSaveDialog(TableResultsPanel.this);
      if (retVal != BaseFileChooser.APPROVE_OPTION)
	return;
      if (!FileUtils.writeToFile(m_FileChooser.getSelectedFile().getAbsolutePath(), m_TextAreaKey.getText(), false))
	GUIHelper.showErrorMessage(TableResultsPanel.this, "Failed to save output to:\n" + m_FileChooser.getSelectedFile());
    });
    m_TextAreaKey.addToButtonsPanel(m_ButtonSave);
  }

  /**
   * Returns the name to display in the GUI.
   *
   * @return		the name
   */
  public String getResultsName() {
    return "Table";
  }

  /**
   * Displays the results.
   */
  protected void doDisplay() {
    ResultMatrixAdamsCSV	matrix;
    SpreadSheet 		sheet;
    Row				row;
    String[][]			array;
    int				i;
    int				n;

    matrix = new ResultMatrixAdamsCSV(m_Matrix);
    matrix.setPrintColNames(false);
    array  = matrix.toArray();
    sheet  = new DefaultSpreadSheet();
    // header
    row = sheet.getHeaderRow();
    for (i = 0; i < array[0].length; i++)
      row.addCell("" + i).setContentAsString(array[0][i]);
    // data
    for (n = 1; n < array.length; n++) {
      row = sheet.addRow();
      for (i = 0; i < array[n].length; i++)
	row.addCell("" + i).setContent(array[n][i]);
    }
    m_TableResults.setModel(new SpreadSheetTableModel(sheet));

    m_TextAreaKey.setText(m_Matrix.toStringKey());
    m_TextAreaKey.setCaretPosition(0);
  }

  /**
   * Returns a clone of the object.
   *
   * @return		the clone
   */
  public TableResultsPanel getClone() {
    return new TableResultsPanel();
  }
}
