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
 * PreviewSelectedFile.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.gui.core.spreadsheettable;

import adams.core.io.PlaceholderFile;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.gui.core.GUIHelper;
import adams.gui.core.SpreadSheetTable;
import adams.gui.dialog.SimplePreviewBrowserDialog;

/**
 * Allows preview of the selected file in separate dialog.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PreviewSelectedFile
  extends AbstractProcessSelectedRows {

  private static final long serialVersionUID = 7786133414905315983L;

  /** the column that contains the filename. */
  protected SpreadSheetColumnIndex m_Column;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Allows the user to preview the selected file in the specified column.";
  }

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "column", "column",
      new SpreadSheetColumnIndex(SpreadSheetColumnIndex.FIRST));
  }

  /**
   * Sets the column with the file names.
   *
   * @param value 	the column
   */
  public void setColumn(SpreadSheetColumnIndex value) {
    m_Column = value;
    reset();
  }

  /**
   * Returns the column with the file names.
   *
   * @return 		the column
   */
  public SpreadSheetColumnIndex getColumn() {
    return m_Column;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String columnTipText() {
    return "The column with the file names.";
  }

  /**
   * Returns the minimum number of rows that the plugin requires.
   *
   * @return		the minimum
   */
  @Override
  public int minNumRows() {
    return 1;
  }

  /**
   * Returns the maximum number of rows that the plugin requires.
   *
   * @return		the maximum, -1 for none
   */
  @Override
  public int maxNumRows() {
    return 1;
  }

  /**
   * Returns the name of the icon.
   *
   * @return            the name, null if none available
   */
  @Override
  public String getIconName() {
    return "previewbrowser.png";
  }

  /**
   * Returns the default name for the menu item.
   *
   * @return            the name
   */
  protected String getDefaultMenuItem() {
    return "Preview selected file";
  }

  /**
   * Processes the specified rows.
   *
   * @param table	the source table
   * @param sheet	the spreadsheet to use as basis
   * @param actRows	the actual rows in the spreadsheet
   * @param selRows	the selected rows in the table
   * @return		true if successful
   */
  @Override
  protected boolean doProcessSelectedRows(SpreadSheetTable table, SpreadSheet sheet, int[] actRows, int[] selRows) {
    int				col;
    SimplePreviewBrowserDialog	dialog;

    // determine column
    m_Column.setData(sheet);
    col = m_Column.getIntIndex();
    if (col == -1) {
      GUIHelper.showErrorMessage(table.getParent(), "Failed to locate column:" + m_Column);
      return false;
    }

    dialog = new SimplePreviewBrowserDialog();
    dialog.open(new PlaceholderFile(sheet.getCell(actRows[0], col).toString()));
    dialog.setLocationRelativeTo(table.getParent());
    dialog.setVisible(true);
    dialog.setDefaultCloseOperation(SimplePreviewBrowserDialog.DISPOSE_ON_CLOSE);

    return true;
  }
}
