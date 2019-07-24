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
 * PreviewSelectedFile.java
 * Copyright (C) 2017-2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.core.spreadsheettable;

import adams.core.Properties;
import adams.core.io.PlaceholderFile;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.gui.core.GUIHelper;
import adams.gui.core.PropertiesParameterPanel;
import adams.gui.core.PropertiesParameterPanel.PropertyType;
import adams.gui.core.SpreadSheetTable;
import adams.gui.dialog.PropertiesParameterDialog;
import adams.gui.dialog.SimplePreviewBrowserDialog;

import java.awt.Dialog.ModalityType;

/**
 * Allows preview of the selected file in separate dialog.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class PreviewSelectedFile
  extends AbstractProcessRow {

  private static final long serialVersionUID = 7786133414905315983L;

  public static final String KEY_COLUMNS = "column";

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
  @Override
  public String getMenuItem() {
    return "Preview selected file";
  }

  /**
   * Prompts the user to configure the parameters.
   *
   * @param table	the table to do this for
   * @return		the parameters, null if cancelled
   */
  protected Properties promptParameters(SpreadSheetTable table) {
    PropertiesParameterDialog 	dialogParams;
    PropertiesParameterPanel 	propsPanel;
    Properties			last;

    if (GUIHelper.getParentDialog(table) != null)
      dialogParams = new PropertiesParameterDialog(GUIHelper.getParentDialog(table), ModalityType.DOCUMENT_MODAL);
    else
      dialogParams = new PropertiesParameterDialog(GUIHelper.getParentFrame(table), true);
    propsPanel = dialogParams.getPropertiesParameterPanel();
    propsPanel.addPropertyType(KEY_COLUMNS, PropertyType.INDEX);
    propsPanel.setLabel(KEY_COLUMNS, "Column");
    propsPanel.setHelp(KEY_COLUMNS, "The column containing the file name");
    last = new Properties();
    last.setProperty(KEY_COLUMNS, SpreadSheetColumnIndex.FIRST);
    dialogParams.setProperties(last);
    last = (Properties) table.getLastSetup(getClass(), true, true);
    if (last != null)
      dialogParams.setProperties(last);
    dialogParams.setTitle(getMenuItem());
    dialogParams.pack();
    dialogParams.setLocationRelativeTo(table.getParent());
    dialogParams.setVisible(true);
    if (dialogParams.getOption() != PropertiesParameterDialog.APPROVE_OPTION)
      return null;

    return dialogParams.getProperties();
  }

  /**
   * Processes the specified rows.
   *
   * @param table	the source table
   * @param sheet	the spreadsheet to use as basis
   * @param actRow	the actual rows in the spreadsheet
   * @param selRow	the selected rows in the table
   * @return		true if successful
   */
  @Override
  protected boolean doProcessRow(SpreadSheetTable table, SpreadSheet sheet, int actRow, int selRow) {
    Properties			last;
    int				col;
    SimplePreviewBrowserDialog	dialog;
    SpreadSheetColumnIndex 	column;

    // prompt user for parameters
    last = promptParameters(table);
    if (last == null)
      return false;

    // determine column
    column = new SpreadSheetColumnIndex(last.getProperty(KEY_COLUMNS, SpreadSheetColumnIndex.FIRST));
    column.setData(sheet);
    col = column.getIntIndex();
    if (col == -1) {
      GUIHelper.showErrorMessage(table.getParent(), "Failed to locate column:" + column);
      return false;
    }
    table.addLastSetup(getClass(), false, true, last);

    dialog = new SimplePreviewBrowserDialog();
    dialog.open(new PlaceholderFile(sheet.getCell(actRow, col).toString()));
    dialog.setLocationRelativeTo(table.getParent());
    dialog.setVisible(true);
    dialog.setDefaultCloseOperation(SimplePreviewBrowserDialog.DISPOSE_ON_CLOSE);

    return true;
  }
}
