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
 * Timeseries.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.timeseries;

import java.util.Date;
import java.util.List;

import adams.core.Constants;
import adams.core.DateFormat;
import adams.core.DateUtils;
import adams.core.Utils;
import adams.data.Notes;
import adams.data.NotesHandler;
import adams.data.container.AbstractDataContainer;
import adams.data.container.DataContainer;
import adams.data.id.MutableDatabaseIDHandler;
import adams.data.id.MutableIDHandler;
import adams.data.report.MutableReportHandler;
import adams.data.report.Report;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetSupporter;
import adams.data.statistics.InformativeStatisticSupporter;
import adams.data.statistics.TimeseriesStatistic;

/**
 * Container for a full timeseries.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Timeseries<P extends TimeseriesPoint, R extends Report, S extends TimeseriesStatistic>
  extends AbstractDataContainer<P>
  implements MutableReportHandler<R>, MutableDatabaseIDHandler,
             NotesHandler, SpreadSheetSupporter, 
             InformativeStatisticSupporter<TimeseriesStatistic> {

  /** for serialization. */
  private static final long serialVersionUID = -915092788633841806L;

  /** the default comparator. */
  protected static TimeseriesPointComparator m_Comparator;

  /** the database ID. */
  protected int m_DatabaseID;

  /** the report. */
  protected R m_Report;

  /** the notes. */
  protected Notes m_Notes;

  /** point of greatest value. */
  protected TimeseriesPoint m_MaxValue;

  /** point of smallest value. */
  protected TimeseriesPoint m_MinValue;

  /**
   * Initializes the profile with no RFID tag ID.
   */
  public Timeseries() {
    this("none");
  }

  /**
   * Initializes the profile with the specified RFID tag ID.
   *
   * @param id		the RFID tag ID
   */
  public Timeseries(String id) {
    super();

    if (m_Comparator == null)
      m_Comparator = newComparator();

    setID(id);
    setReport(newReport());
    setDatabaseID(Constants.NO_ID);

    m_Notes         = new Notes();
    m_MinValue  = null;
    m_MaxValue  = null;
  }
  
  /**
   * Creates a new, empty report.
   * 
   * @return		the report
   */
  protected R newReport() {
    return (R) new Report();
  }

  /**
   * Method that gets notified about changes in the collection of data points.
   * Just passes the modified state through.
   *
   * @param modified	whether the action modified the collection
   * @return		the same as the input
   */
  @Override
  protected boolean modifiedListener(boolean modified) {
    if (modified)
      invalidateMinMax();

    return modified;
  }

  /**
   * Returns a new instance of the default comparator.
   *
   * @return		the comparator instance
   */
  public TimeseriesPointComparator newComparator() {
    return new TimeseriesPointComparator();
  }

  /**
   * Returns the comparator in use.
   *
   * @return		the comparator in use
   */
  public TimeseriesPointComparator getComparator() {
    return m_Comparator;
  }

  /**
   * Returns a new instance of a DataContainer point.
   *
   * @return		the new DataContainer point
   */
  public P newPoint() {
    return (P) new TimeseriesPoint();
  }

  /**
   * Invalidates the min/max timeseries points.
   */
  protected synchronized void invalidateMinMax() {
    m_MinValue  = null;
    m_MaxValue  = null;
  }

  /**
   * Initializes the min/max timeseries points.
   */
  protected synchronized void validateMinMax() {
    if (m_MinValue != null)
      return;

    for (TimeseriesPoint point: this) {
      if (    (m_MaxValue == null)
	   || (point.getValue() > m_MaxValue.getValue()) )
	m_MaxValue = point;
      if (    (m_MinValue == null)
	   || (point.getValue() < m_MinValue.getValue()))
	m_MinValue = point;
    }
  }

  /**
   * Get point with greatest timeseries value.
   *
   * @return	the data point
   */
  public TimeseriesPoint getMaxValue(){
    validateMinMax();
    return m_MaxValue;
  }

  /**
   * Get point with smallest timeseries value.
   *
   * @return	the data point
   */
  public TimeseriesPoint getMinValue(){
    validateMinMax();
    return m_MinValue;
  }

  /**
   * Obtains the stored variables from the other data point, but not the
   * actual data points.
   *
   * @param other	the data point to get the values from
   */
  @Override
  public void assign(DataContainer<P> other) {
    Timeseries	cont;

    super.assign(other);

    cont = (Timeseries) other;

    setDatabaseID(cont.getDatabaseID());
    if (cont.hasReport())
      setReport((R) cont.getReport().getClone());
    m_Notes = new Notes();
    m_Notes.mergeWith(cont.getNotes());
  }

  /**
   * Returns a timeseries with just the header information, but no data 
   * points.
   *
   * @return		the new header
   */
  @Override
  public Timeseries getHeader() {
    Timeseries	result;

    result = new Timeseries();
    result.assign(this);

    return result;
  }

  /**
   * Checks whether a report is present.
   *
   * @return		true if a report is present
   */
  public boolean hasReport() {
    return (m_Report != null);
  }

  /**
   * Sets a new report.
   *
   * @param value	the new report
   */
  public void setReport(R value) {
    m_Report = value;
  }

  /**
   * Returns the report.
   *
   * @return		the report, can be null if none available
   */
  public R getReport() {
    return m_Report;
  }

  /**
   * Returns the database ID.
   *
   * @return		the database ID
   */
  public int getDatabaseID() {
    return m_DatabaseID;
  }

  /**
   * Sets the database ID.
   *
   * @param value	the database ID
   */
  public void setDatabaseID(int value) {
    m_DatabaseID = value;
    if (m_Report != null) {
      m_Report.setDatabaseID(value);
      m_Report.update();
    }
  }

  /**
   * Sets the ID of the profile.
   *
   * @param value	the new ID
   */
  @Override
  public void setID(String value) {
    super.setID(value);

    if (m_Report != null) {
      if (m_Report instanceof MutableIDHandler)
	((MutableIDHandler) m_Report).setID(value);
      m_Report.update();
    }
  }

  /**
   * Compares this object with the specified object for order.  Returns a
   * negative integer, zero, or a positive integer as this object is less
   * than, equal to, or greater than the specified object.
   *
   * @param   o the object to be compared.
   * @return  a negative integer, zero, or a positive integer as this object
   *		is less than, equal to, or greater than the specified object.
   * @throws ClassCastException if the specified object's type prevents it
   *         from being compared to this object.
   */
  @Override
  public int compareToHeader(Object o) {
    int			result;
    Timeseries	tp;

    result = super.compareToHeader(o);
    if (result != 0)
      return result;

    tp = (Timeseries) o;

    if (result == 0)
      result = new Integer(getDatabaseID()).compareTo(new Integer(tp.getDatabaseID()));

    if (result == 0)
      result = Utils.compare(getReport(), tp.getReport());

    return result;
  }

  /**
   * Returns a statistic object of this profile.
   *
   * @return		statistics about this profile
   */
  public S toStatistic() {
    return (S) new TimeseriesStatistic(this);
  }

  /**
   * Returns the TimeseriesPoint with the exact timestamp, null if not found.
   *
   * @param timestamp	the timestamp to look for
   * @return		the TimeseriesPoint or null if not found
   */
  public TimeseriesPoint find(Date timestamp) {
    TimeseriesPoint	result;
    int		index;

    result = null;

    index = TimeseriesUtils.findTimestamp((List<TimeseriesPoint>) m_Points, timestamp);
    if (index > -1)
      result = m_Points.get(index);

    return result;
  }

  /**
   * Returns the TimeseriesPoint with a timestamp closest to the one provided.
   *
   * @param timestamp	the timestamp to look for
   * @return		the TimeseriesPoint
   */
  public TimeseriesPoint findClosest(Date timestamp) {
    TimeseriesPoint	result;
    int			index;

    result = null;

    index = TimeseriesUtils.findClosestTimestamp((List<TimeseriesPoint>) m_Points, timestamp);
    if (index > -1)
      result = m_Points.get(index);

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
    SpreadSheet		result;
    Row			row;
    int			i;
    TimeseriesPoint	point;
    DateFormat		dformat;
    
    result = new SpreadSheet();
    result.setName(getID());
    if (hasReport()) {
      result.addComment(getReport().toString());
    }
    else {
      result.addComment(getID());
    }
    
    // header
    row = result.getHeaderRow();
    row.addCell("D").setContent("Timestamp");
    row.addCell("V").setContent("Value");
    
    // data
    dformat = DateUtils.getTimestampFormatterMsecs();
    for (i = 0; i < size(); i++) {
      point = toList().get(i);
      row   = result.addRow();
      row.addCell("D").setContent(dformat.format(point.getTimestamp()));
      row.addCell("V").setContent(point.getValue());
    }
    
    return result;
  }
}
