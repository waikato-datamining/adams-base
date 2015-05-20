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
 * TimeseriesShiftTimestamps.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */
package adams.data.filter;

import adams.core.Constants;
import adams.core.DateFormat;
import adams.core.DateUtils;
import adams.core.base.BaseDate;
import adams.core.base.BaseDateTime;
import adams.core.base.BaseTime;
import adams.data.DateFormatString;
import adams.data.report.Field;
import adams.data.timeseries.Timeseries;
import adams.data.timeseries.TimeseriesPoint;

import java.util.Date;

/**
 <!-- globalinfo-start -->
 * Shifts the timestamps of the timeseries by a calculated amount.<br>
 * The amount is either the difference between the 'new' timestamp and a timestamp from the report or a supplied timestamp.<br>
 * Accepted date formats (unless custom format specified):<br>
 * yyyy-MM-dd HH:mm:ss<br>
 * yyyy-MM-dd HH:mm:ss.S<br>
 * yyyy-MM-dd
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-no-id-update &lt;boolean&gt; (property: dontUpdateID)
 * &nbsp;&nbsp;&nbsp;If enabled, suppresses updating the ID of adams.data.id.IDHandler data containers.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-source &lt;REPORT_FIELD|SUPPLIED_TIMESTAMP&gt; (property: source)
 * &nbsp;&nbsp;&nbsp;Specifies where to get the 'old' timestamp from.
 * &nbsp;&nbsp;&nbsp;default: SUPPLIED_TIMESTAMP
 * </pre>
 * 
 * <pre>-report-field &lt;adams.data.report.Field&gt; (property: reportField)
 * &nbsp;&nbsp;&nbsp;The report field to obtain the timestamp from.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-custom-format &lt;java.lang.String&gt; (property: customFormat)
 * &nbsp;&nbsp;&nbsp;The custom date format to use for parsing the value from the report.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-supplied-timestamp &lt;adams.core.base.BaseDateTime&gt; (property: suppliedTimestamp)
 * &nbsp;&nbsp;&nbsp;The supplied timestamp value to use.
 * &nbsp;&nbsp;&nbsp;default: NOW
 * </pre>
 * 
 * <pre>-new-timestamp &lt;adams.core.base.BaseDateTime&gt; (property: newTimestamp)
 * &nbsp;&nbsp;&nbsp;The new timestamp value to use.
 * &nbsp;&nbsp;&nbsp;default: NOW
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 7789 $
 */
public class TimeseriesShiftTimestamps
  extends AbstractFilter<Timeseries> {

  /** for serialization. */
  private static final long serialVersionUID = 2616498525816421178L;

  /**
   * Enumeration for where to obtain the "old" timestamp from.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision: 7789 $
   */
  public enum TimestampSource {
    /** retrieves the old timestamp value from the report. */
    REPORT_FIELD,
    /** uses the supplied timestamp. */
    SUPPLIED_TIMESTAMP
  }

  /** the timestamp source. */
  protected TimestampSource m_Source;

  /** the reprt field to use. */
  protected Field m_ReportField;

  /** the custom format to use. */
  protected String m_CustomFormat;

  /** the supplied timestamp. */
  protected BaseDateTime m_SuppliedTimestamp;

  /** the new timstamp. */
  protected BaseDateTime m_NewTimestamp;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Shifts the timestamps of the timeseries by a calculated amount.\n"
          + "The amount is either the difference between the 'new' timestamp "
          + "and a timestamp from the report or a supplied timestamp.\n"
          + "Accepted date formats (unless custom format specified):\n"
          + Constants.TIMESTAMP_FORMAT + "\n"
          + Constants.TIMESTAMP_FORMAT_MSECS + "\n"
          + Constants.DATE_FORMAT;
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "source", "source",
      TimestampSource.SUPPLIED_TIMESTAMP);

    m_OptionManager.add(
      "report-field", "reportField",
      new Field());

    m_OptionManager.add(
      "custom-format", "customFormat",
      "");

    m_OptionManager.add(
      "supplied-timestamp", "suppliedTimestamp",
      new BaseDateTime(BaseDateTime.NOW));

    m_OptionManager.add(
      "new-timestamp", "newTimestamp",
      new BaseDateTime(BaseDateTime.NOW));
  }

  /**
   * Sets where to get the 'old' timestamp from.
   *
   * @param value	the source
   */
  public void setSource(TimestampSource value) {
    m_Source = value;
    reset();
  }

  /**
   * Returns where to get the 'old' timestamp from.
   *
   * @return		the source
   */
  public TimestampSource getSource() {
    return m_Source;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String sourceTipText() {
    return "Specifies where to get the 'old' timestamp from.";
  }

  /**
   * Sets the report field to obtain the timestamp from.
   *
   * @param value	the field
   */
  public void setReportField(Field value) {
    m_ReportField = value;
    reset();
  }

  /**
   * The report field to obtain the timestamp from.
   *
   * @return		the field
   */
  public Field getReportField() {
    return m_ReportField;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String reportFieldTipText() {
    return "The report field to obtain the timestamp from.";
  }

  /**
   * Sets the custom date format to use for parsing. Ignored if empty.
   *
   * @param value	the custom format
   */
  public void setCustomFormat(String value) {
    DateFormatString format;

    format = new DateFormatString();
    if (value.isEmpty() || format.isValid(value)) {
      m_CustomFormat = value;
      reset();
    }
    else {
      getLogger().warning("Invalid date format: " + value);
    }
  }

  /**
   * Returns the custom date format to use for parsing. Ignored if empty.
   *
   * @return		the custom format
   */
  public String getCustomFormat() {
    return m_CustomFormat;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String customFormatTipText() {
    return "The custom date format to use for parsing the value from the report.";
  }

  /**
   * Sets the supplied timestamp to use.
   *
   * @param value	the timestamp
   */
  public void setSuppliedTimestamp(BaseDateTime value) {
    m_SuppliedTimestamp = value;
    reset();
  }

  /**
   * The supplied timestamp to use.
   *
   * @return		the timestamp
   */
  public BaseDateTime getSuppliedTimestamp() {
    return m_SuppliedTimestamp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String suppliedTimestampTipText() {
    return "The supplied timestamp value to use.";
  }

  /**
   * Sets the new timestamp to use.
   *
   * @param value	the timestamp
   */
  public void setNewTimestamp(BaseDateTime value) {
    m_NewTimestamp = value;
    reset();
  }

  /**
   * The new timestamp to use.
   *
   * @return		the timestamp
   */
  public BaseDateTime getNewTimestamp() {
    return m_NewTimestamp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String newTimestampTipText() {
    return "The new timestamp value to use.";
  }

  /**
   * Performs the actual filtering.
   *
   * @param data	the data to filter
   * @return		the filtered data
   */
  @Override
  protected Timeseries processData(Timeseries data) {
    Timeseries		result;
    TimeseriesPoint	point;
    int			i;
    String              oldStr;
    BaseDateTime        old;
    long                diff;
    Date                oldDate;

    result = data.getHeader();

    switch (m_Source) {
      case REPORT_FIELD:
        if (!data.hasReport())
          throw new IllegalStateException("No report available!");
        if (!data.getReport().hasValue(m_ReportField))
          throw new IllegalStateException("No report field '" + m_ReportField + "' not available!");
        oldStr = data.getReport().getStringValue(m_ReportField);
        if (oldStr.isEmpty())
          throw new IllegalStateException("Empty timestamp stored in report field '" + m_ReportField + "'!");
        if (m_CustomFormat.isEmpty()) {
          if (DateUtils.checkDateTime(oldStr))
            old = new BaseDateTime(oldStr);
          else if (DateUtils.checkDate(oldStr))
            old = new BaseDateTime(new BaseDate(oldStr).dateValue());
          else if (DateUtils.checkTime(oldStr))
            old = new BaseDateTime(new BaseTime(oldStr).dateValue());
          else
            throw new IllegalStateException("Timestamp in report field '" + m_ReportField + "' not recognized: " + oldStr);
        }
        else {
          oldDate = new DateFormat(m_CustomFormat).parse(oldStr);
          if (oldDate == null)
            throw new IllegalStateException("Failed to parse report field '" + m_ReportField + "' using '" + m_CustomFormat + "':" + oldStr);
          old = new BaseDateTime(oldDate);
        }
        break;

      case SUPPLIED_TIMESTAMP:
        old = m_SuppliedTimestamp;
        break;

      default:
        throw new IllegalStateException("Unhandled source: " + m_Source);
    }

    diff = m_NewTimestamp.dateValue().getTime() - old.dateValue().getTime();
    if (isLoggingEnabled())
      getLogger().info("difference to old ('" + old + "'): " + diff);
    for (i = 0; i < data.size(); i++) {
      point = (TimeseriesPoint) data.toList().get(i);
      result.add(new TimeseriesPoint(new Date(point.getTimestamp().getTime() + diff), point.getValue()));
    }
    
    return result;
  }
}
