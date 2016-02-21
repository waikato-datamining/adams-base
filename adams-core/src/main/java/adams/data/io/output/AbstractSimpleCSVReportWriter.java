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
 * AbstractCSVReportWriter.java
 * Copyright (C) 2009-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.data.io.output;

import adams.data.io.input.AbstractSimpleCSVReportReader;
import adams.data.report.AbstractField;
import adams.data.report.DataType;
import adams.data.report.Report;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;

import java.util.Collections;
import java.util.List;

/**
 * Abstract ancestor for writing reports in CSV format.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of report to handle
 */
public abstract class AbstractSimpleCSVReportWriter<T extends Report>
  extends AbstractReportWriter<T> {

  /** for serialization. */
  private static final long serialVersionUID = 1068874780353203514L;

  /** the name of the "field" column. */
  public final static String COL_FIELD = "Field";

  /** the name of the "type" column. */
  public final static String COL_TYPE = "Type";

  /** the name of the "value" column. */
  public final static String COL_VALUE = "Value";

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Writes reports in CSV file format.";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return "CSV file format";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension(s) (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{AbstractSimpleCSVReportReader.FILE_EXTENSION};
  }

  /**
   * Performs the actual writing.
   *
   * @param data	the data to write
   * @return		true if successfully written
   */
  @Override
  protected boolean writeData(T data) {
    boolean		result;
    SpreadSheet		sheet;
    List<AbstractField>	fields;
    int			i;
    Row			row;

    sheet = new DefaultSpreadSheet();

    // header
    row = sheet.getHeaderRow();
    row.addCell(COL_FIELD).setContent(COL_FIELD);
    row.addCell(COL_TYPE).setContent(COL_TYPE);
    row.addCell(COL_VALUE).setContent(COL_VALUE);

    // the parent ID
    row = sheet.addRow(Report.PROPERTY_PARENTID);
    row.addCell(COL_FIELD).setContent(Report.PROPERTY_PARENTID);
    row.addCell(COL_TYPE).setContent(DataType.NUMERIC.toString());
    row.addCell(COL_VALUE).setContent("" + data.getDatabaseID());

    // transfer properties
    fields = data.getFields();
    Collections.sort(fields);
    for (i = 0; i < fields.size(); i++) {
      row = sheet.addRow(fields.get(i).toString());
      row.addCell(COL_FIELD).setContent(fields.get(i).toString());
      row.addCell(COL_TYPE).setContent(fields.get(i).getDataType().toString());
      row.addCell(COL_VALUE).setContent(data.getValue(fields.get(i)).toString());
    }

    // write CSV file
    result = new CsvSpreadSheetWriter().write(sheet, m_Output.getAbsolutePath());
    if (!result)
      getLogger().severe("Error writing report #" + data.getDatabaseID() + " to " + m_Output);

    return result;
  }
}
