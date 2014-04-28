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
 * Statistics.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.spreadsheetviewer;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;

import adams.data.spreadsheet.Cell.ContentType;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.SpreadSheetTable;

/**
 * Generates a table with some simple statistics.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Statistics
  extends AbstractViewPlugin {

  /** for serialization. */
  private static final long serialVersionUID = 8565680908628147610L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates a table with some simple statistics.";
  }

  /**
   * Returns the text of the menu item.
   *
   * @return 		the text
   */
  @Override
  public String getMenuText() {
    return "Statistics";
  }

  /**
   * Returns the file name of the icon.
   *
   * @return		the filename or null if no icon available
   */
  @Override
  public String getMenuIcon() {
    return "statistics.png";
  }

  /**
   * Performs the actual generation of the information.
   * 
   * @param sheet	the sheet to process
   * @return		the generated information panel
   */
  @Override
  protected BasePanel doGenerate(SpreadSheet sheet) {
    BasePanel				result;
    SpreadSheetTable			table;
    SpreadSheet				stats;
    Hashtable<ContentType,Integer>	counts;
    int					i;
    ArrayList<ContentType>		types;
    ArrayList<ContentType>		numeric;
    Row					row;
    
    result = new BasePanel(new BorderLayout());

    counts = new Hashtable<ContentType,Integer>();
    for (ContentType ct: ContentType.values()) {
      if (ct == ContentType.MISSING)
	continue;
      counts.put(ct, 0);
    }
    numeric = new ArrayList<ContentType>(Arrays.asList(new ContentType[]{ContentType.DOUBLE, ContentType.LONG}));
    for (i = 0; i < sheet.getColumnCount(); i++) {
      types = new ArrayList<ContentType>(sheet.getContentTypes(i));
      // pure column?
      if (types.size() == 1)
	counts.put(types.get(0), counts.get(types.get(0)) + 1);
      // numeric one? -> Double
      else if ((types.size() == numeric.size()) && types.containsAll(numeric))
	counts.put(ContentType.DOUBLE, counts.get(ContentType.DOUBLE) + 1);
    }
    
    // assemble table
    stats = new SpreadSheet();
    row   = stats.getHeaderRow();
    row.addCell("key").setContent("Key");
    row.addCell("value").setContent("Value");

    // rows
    row = stats.addRow();
    row.addCell(0).setContent("Rows");
    row.addCell(1).setContent(sheet.getRowCount());

    // columns
    row = stats.addRow();
    row.addCell(0).setContent("Columns");
    row.addCell(1).setContent(sheet.getColumnCount());
    
    // column types
    for (ContentType ct: ContentType.values()) {
      if (ct == ContentType.MISSING)
	continue;
      row = stats.addRow();
      row.addCell(0).setContent(ct + " columns");
      row.addCell(1).setContent(counts.get(ct));
    }
    
    table = new SpreadSheetTable(stats);
    table.setAutoResizeMode(SpreadSheetTable.AUTO_RESIZE_OFF);
    table.setOptimalColumnWidth();
    result.add(new BaseScrollPane(table));
    
    return result;
  }
}
