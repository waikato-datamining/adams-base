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
 * InterQuartileRangeViewer.java
 * Copyright (C) 2012-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.previewbrowser;

import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.SpreadSheetTable;
import weka.filters.unsupervised.attribute.InterquartileRange;
import weka.filters.unsupervised.attribute.InterquartileRange.ValueType;

/**
 * Displays internal values of the {@link InterquartileRange} filter.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class InterQuartileRangeViewer
  extends AbstractSerializedObjectViewer {

  /** for serialization. */
  private static final long serialVersionUID = 1805163535974387818L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Displays the internal values (extreme values, outliers, etc) of " 
	+ InterquartileRange.class.getName() + " objects.";
  }

  /**
   * Returns whether viewer handles this object.
   * 
   * @param obj		the object to check
   * @return		true if the object can be handled
   */
  @Override
  public boolean handles(Object obj) {
    //return false;
    return (obj instanceof InterquartileRange);
  }

  /**
   * Creates the actual preview.
   *
   * @param obj		the object to create the preview for
   * @return		the preview, null if failed to generate preview
   */
  @Override
  protected PreviewPanel createPreview(Object obj) {
    PreviewPanel	result;
    SpreadSheetTable	table;
    SpreadSheet		sheet;
    Row			row;
    int			i;
    InterquartileRange	iqr;

    // generate output
    sheet = new DefaultSpreadSheet();
    
    // header
    iqr = (InterquartileRange) obj;
    row = sheet.getHeaderRow();
    row.addCell("0").setContent("Attribute Index");
    for (ValueType type: ValueType.values())
      row.addCell(type.toString()).setContent(type.toString());
    
    // data
    for (i = 0; i < iqr.getValues(ValueType.IQR).length; i++) {
      row = sheet.addRow("" + sheet.getRowCount());
      row.addCell("0").setContent("" + (i+1));
      for (ValueType type: ValueType.values())
	row.addCell(type.toString()).setContent(iqr.getValues(type)[i]);
    }
    
    table  = new SpreadSheetTable(sheet);
    table.setNumDecimals(6);
    result = new PreviewPanel(new BaseScrollPane(table), table);
    
    return result;
  }
}
