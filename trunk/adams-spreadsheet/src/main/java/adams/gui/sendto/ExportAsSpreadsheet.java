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
 * ExportAsSpreadsheet.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.sendto;

import java.io.File;
import java.util.logging.Level;

import javax.swing.JTable;

import adams.data.io.output.SpreadSheetWriter;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetSupporter;
import adams.gui.chooser.SpreadSheetFileChooser;
import adams.gui.core.SortableTableModel;
import adams.gui.core.SpreadSheetTable;
import adams.gui.core.SpreadSheetTableModel;
import adams.gui.core.TableRowRange;

/**
 * Action for exporting a table as spreadsheet.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ExportAsSpreadsheet
  extends AbstractSendToAction {

  /** for serialization. */
  private static final long serialVersionUID = -5286281737195775697L;

  /**
   * Returns the short description of the sendto action.
   * Description gets used for menu items.
   *
   * @return		the short description
   */
  @Override
  public String getAction() {
    return "Export as spreadsheet";
  }

  /**
   * Returns the file name of the icon.
   *
   * @return		the filename or null if no icon available
   */
  @Override
  public String getIconName() {
    return "spreadsheet.png";
  }

  /**
   * Returns the classes that the action accepts.
   *
   * @return		the accepted classes
   */
  @Override
  public Class[] accepts() {
    return new Class[]{SpreadSheet.class, SpreadSheetTable.class, SpreadSheetSupporter.class, JTable.class};
  }

  /**
   * Performs the actual sending/forwarding/processing of the data.
   *
   * @param o		the object to send
   * @return		null if everything OK, otherwise error message
   */
  @Override
  public String send(Object o) {
    String			result;
    int				retVal;
    SpreadSheet			sheet;
    SpreadSheetFileChooser 	fileChooser;
    File			file;
    SpreadSheetWriter		writer;
    String			msg;
    int				i;
    int				n;
    JTable			table;
    Row				row;
    Object			value;

    result = null;

    sheet = null;
    if (o instanceof SpreadSheet) {
      sheet = (SpreadSheet) o;
    }
    else if (o instanceof SpreadSheetTable) {
      sheet = ((SpreadSheetTable) o).toSpreadSheet(TableRowRange.VISIBLE);
    }
    else if (o instanceof SpreadSheetSupporter) {
      sheet = ((SpreadSheetSupporter) o).toSpreadSheet();
    }
    else if (o instanceof JTable) {
      table = (JTable) o;
      if (table.getModel() instanceof SpreadSheetTableModel) {
	sheet = ((SpreadSheetTableModel) table.getModel()).toSpreadSheet();
      }
      else if (table.getModel() instanceof SortableTableModel) {
	if (((SortableTableModel) table.getModel()) instanceof SpreadSheetTableModel)
	  sheet = ((SpreadSheetTableModel) ((SortableTableModel) table.getModel()).getUnsortedModel()).toSpreadSheet();
      }
      if (sheet == null) {
	sheet = new SpreadSheet();
	// header
	row = sheet.getHeaderRow();
	for (i = 0; i < table.getColumnCount(); i++)
	  row.addCell("" + i).setContent(table.getColumnName(i));
	for (n = 0; n < table.getRowCount(); n++) {
	  row = sheet.addRow("" + n);
	  for (i = 0; i < table.getColumnCount(); i++) {
	    value = table.getValueAt(n, i);
	    if (value != null)
	      row.addCell("" + i).setContent(value.toString());
	  }
	}
      }
    }

    if (sheet != null) {
      fileChooser = new SpreadSheetFileChooser();
      // display save dialog
      retVal = fileChooser.showSaveDialog(null);
      if (retVal != SpreadSheetFileChooser.APPROVE_OPTION)
        result = "Export canceled by user!";

      if (result == null) {
	// save the file
	try {
	  file   = fileChooser.getSelectedFile().getAbsoluteFile();
	  writer = fileChooser.getWriter();
	  if (!writer.write(sheet, file))
	    result = "Failed to export spreadsheet!";
	}
	catch (Exception e) {
	  msg    = "Failed to export spreadsheet: ";
	  result = msg + e;
	  getLogger().log(Level.SEVERE, msg, e);
	}
      }
    }
    else {
      result = "Cannot export object as spreadsheet: " + o.getClass();
    }

    return result;
  }
}
