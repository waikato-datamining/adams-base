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
 * AbstractSimpleCSVReportReader.java
 * Copyright (C) 2009-2017 University of Waikato, Hamilton, New Zealand
 */

package adams.data.io.input;

import adams.core.option.AbstractOption;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.Report;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract ancestor for reports to be written in CSV format.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of report to use
 */
public abstract class AbstractSimpleCSVReportReader<T extends Report>
  extends AbstractReportReader<T> {

  /** for serialization. */
  private static final long serialVersionUID = 8997935127278404969L;

  /** the file extension. */
  public final static String FILE_EXTENSION = "csv";

  /** the file extension for compressed files. */
  public final static String FILE_EXTENSION_GZ = "csv.gz";

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Reads a report file in CSV file format.";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return "Simple report CSV file format";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension(s) (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{FILE_EXTENSION, FILE_EXTENSION_GZ};
  }

  /**
   * Tries to determine the parent ID for the current report.
   *
   * @param report	the report to determine the ID for
   * @return		the parent database ID, -1 if it cannot be determined
   */
  @Override
  protected int determineParentID(Report report) {
    return report.getDatabaseID();
  }

  /**
   * Performs the actual reading.
   *
   * @return		the reports that were read
   */
  @Override
  protected List<T> readData() {
    List<T>		result;
    SpreadSheet		sheet;
    Row			row;
    int			i;
    DataType		type;
    String		value;
    Field		field;
    String		fieldStr;
    String		colField;
    String		colType;
    String		colValue;

    result = new ArrayList<T>();
    result.add(newInstance());
    result.get(0).setLoggingLevel(getLoggingLevel());
    sheet    = new CsvSpreadSheetReader().read(getInput().getAbsolutePath());
    colField = sheet.getHeaderRow().getCellKey(0);
    colType  = sheet.getHeaderRow().getCellKey(1);
    colValue = sheet.getHeaderRow().getCellKey(2);
    for (i = 0; i < sheet.getRowCount(); i++) {
      row      = sheet.getRow(i);
      fieldStr = row.getCell(colField).getContent();
      type     = (DataType) DataType.valueOf((AbstractOption) null, row.getCell(colType).getContent());
      value    = row.getCell(colValue).getContent();
      if (fieldStr.equals(Report.PROPERTY_PARENTID)) {
	result.get(0).setDatabaseID(Integer.parseInt(value));
      }
      else {
	field = new Field(fieldStr, type);
	result.get(0).setValue(field, value);
      }
    }

    return result;
  }
}
