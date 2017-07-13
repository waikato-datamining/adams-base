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
 * CopySelectedFiles.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.gui.core.spreadsheettable;

import adams.core.MessageCollection;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderDirectory;
import adams.core.io.PlaceholderFile;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.gui.chooser.BaseDirectoryChooser;
import adams.gui.core.GUIHelper;
import adams.gui.core.SpreadSheetTable;

import java.io.File;

/**
 * Allows copying of the selected files to a target directory.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CopySelectedFiles
  extends AbstractProcessSelectedRows {

  private static final long serialVersionUID = 7786133414905315983L;

  /** the column that contains the filename. */
  protected SpreadSheetColumnIndex m_Column;

  /** the target directory. */
  protected PlaceholderDirectory m_TargetDir;

  /** the directory chooser for the target dir. */
  protected BaseDirectoryChooser m_TargetChooser;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Allows the user to copy the selected files in the specified column.";
  }

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "column", "column",
      new SpreadSheetColumnIndex(SpreadSheetColumnIndex.FIRST));

    m_OptionManager.add(
      "target-dir", "targetDir",
      new PlaceholderDirectory());
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
   * Sets the initial target directory.
   *
   * @param value 	the directory
   */
  public void setTargetDir(PlaceholderDirectory value) {
    m_TargetDir = value;
    reset();
  }

  /**
   * Returns the initial target directory.
   *
   * @return 		the directory
   */
  public PlaceholderDirectory getTargetDir() {
    return m_TargetDir;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String targetDirTipText() {
    return "The initial target directory for the copy operation.";
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
   * Returns the name for the menu item.
   *
   * @return            the name
   */
  @Override
  public String getMenuItem() {
    return "Copy selected file(s)";
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
    int			retVal;
    int			col;
    File		sourceFile;
    File		targetDir;
    MessageCollection	errors;

    // select target dir
    if (m_TargetChooser == null) {
      m_TargetChooser = new BaseDirectoryChooser();
      m_TargetChooser.setSelectedFile(m_TargetDir);
      m_TargetChooser.setDialogTitle(getMenuItem());
    }
    retVal = m_TargetChooser.showOpenDialog(table.getParent());
    if (retVal != BaseDirectoryChooser.APPROVE_OPTION)
      return false;
    targetDir = m_TargetChooser.getSelectedDirectory();

    errors = new MessageCollection();
    m_Column.setData(sheet);
    col = m_Column.getIntIndex();
    for (int row: actRows) {
      sourceFile = new PlaceholderFile(sheet.getCell(row, col).toString());
      try {
	if (!FileUtils.copy(sourceFile, targetDir))
	  errors.add("Failed to copy '" + sourceFile + "' to '" + targetDir + "'!");
      }
      catch (Exception e) {
	errors.add("Failed to copy '" + sourceFile + "' to '" + targetDir + "'!", e);
      }
    }

    if (!errors.isEmpty())
      GUIHelper.showErrorMessage(table.getParent(), "Failed to copy files:\n" + errors);

    return errors.isEmpty();
  }
}
