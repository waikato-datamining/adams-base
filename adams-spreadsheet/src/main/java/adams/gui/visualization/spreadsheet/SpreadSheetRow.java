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
 * SpreadSheetRow.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.spreadsheet;

import adams.core.Range;
import adams.data.Notes;
import adams.data.NotesHandler;
import adams.data.container.AbstractDataContainer;
import adams.data.container.DataContainer;
import adams.data.container.DataPointComparator;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.MutableReportHandler;
import adams.data.report.Report;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.DenseDataRow;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetSupporter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Stores values from Row objects, with X being the
 * attribute index (integer) and Y being the internal value (double).
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 12575 $
 */
public class SpreadSheetRow
  extends AbstractDataContainer<SpreadSheetRowPoint>
  implements MutableReportHandler<Report>, NotesHandler, SpreadSheetSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 8553741559715144356L;

  /** the key in the report for the spreadsheet name. */
  public static final String REPORT_SPREADSHEET = "SpreadSheet-Name";

  /** the key in the report for the spreadsheet ID. */
  public static final String REPORT_DB_ID = "DB-ID";

  /** the key in the report for the ID. */
  public static final String REPORT_ID = "ID";

  /** the key in the report for the display ID. */
  public static final String REPORT_DISPLAY_ID = "Display-ID";

  /** the key in the report for the row in the spreadsheet. */
  public static final String REPORT_ROW = "SpreadSheet-Row";

  /** the key prefix in the report for the additional columns. */
  public static final String REPORT_ADDITIONAL_PREFIX = "Column-";

  /** the default comparator. */
  protected static DataPointComparator m_Comparator;

  /** a reference to the dataset the data was obtained from. */
  protected SpreadSheet m_DatasetHeader;

  /** the automatically generated report. */
  protected Report m_Report;

  /** the notes for the chromatogram. */
  protected Notes m_Notes;

  /**
   * Initializes the sequence.
   */
  public SpreadSheetRow() {
    super();

    m_Report = newReport();
    m_Notes  = new Notes();

    if (m_Comparator == null)
      m_Comparator = newComparator();

    setID("");
  }

  /**
   * Sets the ID of the sequence.
   *
   * @param value	the new ID
   */
  @Override
  public void setID(String value) {
    super.setID(value);
    m_Report.addParameter(REPORT_DISPLAY_ID, value);
  }

  /**
   * Returns the comparator in use.
   *
   * @return		the comparator to use
   */
  public DataPointComparator<SpreadSheetRowPoint> newComparator() {
    return new SpreadSheetRowPointComparator();
  }

  /**
   * Returns the comparator in use.
   *
   * @return		the comparator in use
   */
  public DataPointComparator<SpreadSheetRowPoint> getComparator() {
    return m_Comparator;
  }

  /**
   * Returns a new instance of a sequence point.
   *
   * @return		the new sequence point
   */
  public SpreadSheetRowPoint newPoint() {
    return new SpreadSheetRowPoint();
  }

  /**
   * Creates an empty report.
   *
   * @return		the empty report
   */
  protected Report newReport() {
    Report	result;

    result = new Report();
    result.addField(new Field(REPORT_SPREADSHEET,    DataType.STRING));
    result.addField(new Field(REPORT_DB_ID,      DataType.NUMERIC));
    result.addField(new Field(REPORT_ID,         DataType.STRING));
    result.addField(new Field(REPORT_DISPLAY_ID, DataType.STRING));

    return result;
  }

  /**
   * Removes all the points and report and nulls the header reference.
   *
   * @see	#m_DatasetHeader
   */
  @Override
  public void clear() {
    m_DatasetHeader = null;
    m_Report        = newReport();

    super.clear();
  }

  /**
   * Obtains the stored variables from the other data point, but not the
   * actual data points.
   *
   * @param other	the data point to get the values from
   */
  @Override
  public void assign(DataContainer<SpreadSheetRowPoint> other) {
    super.assign(other);

    m_DatasetHeader = ((SpreadSheetRow) other).getDatasetHeader();
    m_Report        = ((SpreadSheetRow) other).getReport().getClone();
    m_Notes         = ((SpreadSheetRow) other).getNotes().getClone();
  }

  /**
   * Returns whether a header of a dataset is available.
   *
   * @return		true if a header is available
   */
  public boolean hasDatasetHeader() {
    return (m_DatasetHeader != null);
  }

  /**
   * Returns the header of the underlying dataset.
   *
   * @return		the header, null if none currently set
   */
  public SpreadSheet getDatasetHeader() {
    return m_DatasetHeader;
  }

  /**
   * Clears the container and adds the data from the Row
   * (internal values).
   *
   * @param row	the instance to use
   */
  public void set(Row row) {
    set(row, -1, new int[0], new Range("first-last"), null);
  }

  /**
   * Clears the container and adds the data from the Row
   * (internal values). Uses only the attributes specified in the range.
   *
   * @param row		the row to use
   * @param index	the row index in the original dataset, use -1 to ignore
   * @param additional	the indices of the additional attribute values to
   * 			store in the report
   * @param range	the range of attributes to limit the instance to
   * @param attTypes	whether to restrict to attributes types, null or zero-length array means no restriction
   */
  public void set(Row row, int index, int[] additional, Range range, HashSet<Integer> attTypes) {
    ArrayList<SpreadSheetRowPoint>	list;
    int					i;
    String				fieldStr;
    Cell 				cell;
    Double				dbl;

    clear();

    // keep reference to header
    m_DatasetHeader = row.getOwner().getHeader();

    range.setMax(row.getOwner().getColumnCount());
    list = new ArrayList<>();
    for (i = 0; i < row.getOwner().getColumnCount(); i++) {
      if (!range.isInRange(i))
	continue;
      if ((attTypes != null) && (!attTypes.containsAll(row.getOwner().getContentTypes(i))))
	continue;
      cell = row.getCell(i);
      if ((cell == null) || cell.isMissing() || !cell.isNumeric())
	continue;
      list.add(new SpreadSheetRowPoint(i, cell.toDouble()));
    }

    addAll(list);

    // row
    if (index != -1) {
      m_Report.addField(new Field(REPORT_ROW, DataType.NUMERIC));
      m_Report.addParameter(REPORT_ROW, new Double(index + 1));
    }
    // additional attributes
    for (i = 0; i < additional.length; i++) {
      cell = row.getCell(additional[i]);
      if ((cell != null) && !cell.isMissing()) {
	fieldStr = REPORT_ADDITIONAL_PREFIX + (additional[i] + 1) + "-" + row.getOwner().getHeaderRow().getCell(i).getContent();
	if (cell.isNumeric()) {
	  m_Report.addField(new Field(fieldStr, DataType.NUMERIC));
	  m_Report.addParameter(fieldStr, cell.toDouble());
	}
	else {
	  m_Report.addField(new Field(fieldStr, DataType.STRING));
	  m_Report.addParameter(fieldStr, cell.getContent());
	}
      }
    }

    // display ID (hashcode of string representation of Instance)
    if (getID().length() == 0)
      setID("" + row.toString().hashCode());
  }

  /**
   * Checks whether a report is present.
   *
   * @return		always true
   */
  public boolean hasReport() {
    return true;
  }

  /**
   * Sets a new report.
   *
   * @param value	the new report
   */
  public void setReport(Report value) {
    m_Report = value;
  }

  /**
   * Returns the report.
   *
   * @return		the report, can be null if none available
   */
  public Report getReport() {
    return m_Report;
  }

  /**
   * Generates a spreadsheet row, if a spreadsheet header is available.
   *
   * @return		the generated row, null if no header available
   */
  public Row toRow() {
    Row		result;

    result = null;

    if (hasDatasetHeader()) {
      result = new DenseDataRow();
      result.setOwner(m_DatasetHeader);
      for (SpreadSheetRowPoint point: this) {
	if (point.getX() < m_DatasetHeader.getColumnCount())
	  result.addCell(point.getX()).setContent(point.getY());
      }
    }

    return result;
  }

  /**
   * Returns the currently stored notes.
   *
   * @return		the current notes
   */
  public Notes getNotes() {
    return m_Notes;
  }

  /**
   * Returns the content as spreadsheet.
   * 
   * @return		the content
   */
  public SpreadSheet toSpreadSheet() {
    Iterator<SpreadSheetRowPoint>	iter;
    SpreadSheet				result;
    Row					row;
    SpreadSheetRowPoint 		point;

    result = new DefaultSpreadSheet();
    result.setName(getID());
    row    = result.getHeaderRow();
    row.addCell("C").setContent("Column");
    row.addCell("V").setContent("Value");
    iter = iterator();
    while (iter.hasNext()) {
      point = iter.next();
      row = result.addRow();
      row.addCell("C").setContent(point.getX());
      row.addCell("V").setContent(point.getY());
    }
    
    return result;
  }
}
