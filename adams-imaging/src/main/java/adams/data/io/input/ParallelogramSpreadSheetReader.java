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
 * ParallelogramSpreadSheetReader.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.data.io.input;

import adams.data.report.Report;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;
import adams.flow.transformer.locateobjects.ObjectPrefixHandler;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.awt.Polygon;
import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Reads parallelograms as polygons from a CSV file (x0,y0,...,x3,y3).
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-input &lt;adams.core.io.PlaceholderFile&gt; (property: input)
 * &nbsp;&nbsp;&nbsp;The file to read and turn into a report.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 *
 * <pre>-prefix &lt;java.lang.String&gt; (property: prefix)
 * &nbsp;&nbsp;&nbsp;The report field prefix used in the report.
 * &nbsp;&nbsp;&nbsp;default: Object.
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ParallelogramSpreadSheetReader
  extends AbstractReportReader<Report>
  implements ObjectPrefixHandler {

  private static final long serialVersionUID = -45890668031870078L;

  /** the prefix to use. */
  protected String m_Prefix;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Reads parallelograms as polygons from a CSV file (x0,y0,...,x3,y3).\n"
      + "Any additional columns are added as meta-data.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "prefix", "prefix",
      "Object.");
  }

  /**
   * Sets the field prefix used in the report.
   *
   * @param value 	the field prefix
   */
  public void setPrefix(String value) {
    m_Prefix = value;
    reset();
  }

  /**
   * Returns the field prefix used in the report.
   *
   * @return 		the field prefix
   */
  public String getPrefix() {
    return m_Prefix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String prefixTipText() {
    return "The report field prefix used in the report.";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return "Parallelograms";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension(s) (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{"csv"};
  }

  /**
   * Returns a new instance of the report class in use.
   *
   * @return		the new (empty) report
   */
  @Override
  public Report newInstance() {
    return new Report();
  }

  /**
   * Tries to determine the parent ID for the current report.
   *
   * @param report	the report to determine the ID for
   * @return		the parent database ID, -1 if it cannot be determined
   */
  @Override
  protected int determineParentID(Report report) {
    return -1;
  }

  /**
   * Returns the index of the specified column in the spreadsheet.
   *
   * @param sheet	the sheet to look for the column
   * @param col		the column name
   * @return		the index
   * @throws IllegalStateException	if column not found
   */
  protected int getIndex(SpreadSheet sheet, String col) {
    int		i;

    for (i = 0; i < sheet.getColumnNames().size(); i++) {
      if (sheet.getColumnNames().get(i).equals(col))
        return i;
    }

    getLogger().severe("Failed to locate column: " + col);
    return -1;
  }

  /**
   * Performs the actual reading.
   *
   * @return		the reports that were read
   */
  @Override
  protected List<Report> readData() {
    List<Report> 		result;
    CsvSpreadSheetReader 	reader;
    SpreadSheet			sheet;
    LocatedObjects		objects;
    LocatedObject		object;
    Polygon			poly;
    int				x0;
    int				y0;
    int				x1;
    int				y1;
    int				x2;
    int				y2;
    int				x3;
    int				y3;
    TIntSet			meta;
    int				i;

    result = new ArrayList<>();
    reader = new CsvSpreadSheetReader();
    sheet  = reader.read(m_Input);

    // locate columns
    x0 = getIndex(sheet, "x0");
    y0 = getIndex(sheet, "y0");
    x1 = getIndex(sheet, "x1");
    y1 = getIndex(sheet, "y1");
    x2 = getIndex(sheet, "x2");
    y2 = getIndex(sheet, "y2");
    x3 = getIndex(sheet, "x3");
    y3 = getIndex(sheet, "y3");
    if ((x0 == -1) || (y0 == -1) || (x1 == -1) || (y1 == -1) || (x2 == -1) || (y2 == -1) || (x3 == -1) || (y3 == -1))
      return result;

    // meta-data columns
    meta = new TIntHashSet();
    for (i = 0; i < sheet.getColumnCount(); i++)
      meta.add(i);
    meta.remove(x0);
    meta.remove(y0);
    meta.remove(x1);
    meta.remove(y1);
    meta.remove(x2);
    meta.remove(y2);
    meta.remove(x3);
    meta.remove(y3);

    // read data
    objects = new LocatedObjects();
    for (Row row: sheet.rows()) {
      poly = new Polygon(
        new int[]{row.getCell(x0).toDouble().intValue(), row.getCell(x1).toDouble().intValue(), row.getCell(x2).toDouble().intValue(), row.getCell(x3).toDouble().intValue()},
        new int[]{row.getCell(y0).toDouble().intValue(), row.getCell(y1).toDouble().intValue(), row.getCell(y2).toDouble().intValue(), row.getCell(y3).toDouble().intValue()},
	4);
      object = new LocatedObject(poly);

      // meta-data
      for (int col: meta.toArray()) {
        if (!row.hasCell(col) || row.getCell(col).isMissing())
          continue;
        object.getMetaData().put(sheet.getColumnName(col), row.getCell(col).getNative());
      }

      objects.add(object);
    }

    result.add(objects.toReport(m_Prefix));

    return result;
  }
}
