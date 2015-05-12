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
 * ReportDate.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.timeseriessplit;

import adams.core.Constants;
import adams.core.DateFormat;
import adams.core.QuickInfoHelper;
import adams.core.TechnicalInformation;
import adams.core.TechnicalInformationHandler;
import adams.data.DateFormatString;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.timeseries.Timeseries;

import java.util.Date;

/**
 <!-- globalinfo-start -->
 * Splits the timeseries using a field from its report.<br/>
 * Accepted date formats:<br/>
 * yyyy-MM-dd HH:mm:ss<br/>
 * yyyy-MM-dd HH:mm:ss.S<br/>
 * yyyy-MM-dd
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-segments &lt;BOTH|BEFORE|AFTER&gt; (property: segments)
 * &nbsp;&nbsp;&nbsp;The segments to return.
 * &nbsp;&nbsp;&nbsp;default: BOTH
 * </pre>
 * 
 * <pre>-include-split-date &lt;boolean&gt; (property: includeSplitDate)
 * &nbsp;&nbsp;&nbsp;If enabled, the split date is included in the segments.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-field &lt;adams.data.report.Field&gt; (property: field)
 * &nbsp;&nbsp;&nbsp;The report field to obtain the split date from.
 * &nbsp;&nbsp;&nbsp;default: somedate[S]
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ReportDate
  extends AbstractSplitOnDate
  implements TechnicalInformationHandler {

  /** for serialization. */
  private static final long serialVersionUID = 259240444289354690L;
  
  /** the field in the report to split on. */
  protected Field m_Field;
  
  /** the custom format to use. */
  protected String m_CustomFormat;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Splits the timeseries using a field from its report.\n"
	+ "Accepted date formats (unless custom format specified):\n"
	+ Constants.TIMESTAMP_FORMAT + "\n"
	+ Constants.TIMESTAMP_FORMAT_MSECS + "\n"
	+ Constants.DATE_FORMAT + "\n"
	+ "For more information, see:\n"
	+ getTechnicalInformation().toString();
  }

  /**
   * Returns an instance of a TechnicalInformation object, containing
   * detailed information about the technical background of this class,
   * e.g., paper reference or book this class is based on.
   *
   * @return the technical information about this class
   */
  public TechnicalInformation getTechnicalInformation() {
    return new DateFormat().getTechnicalInformation();
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "field", "field",
	    new Field("somedate", DataType.STRING));

    m_OptionManager.add(
	    "custom-format", "customFormat",
	    "");
  }

  /**
   * Sets the report field to obtain the split date from.
   *
   * @param value	the report field
   */
  public void setField(Field value) {
    m_Field = value;
    reset();
  }

  /**
   * Returns the report field to obtain the split date from.
   *
   * @return		the report field
   */
  public Field getField() {
    return m_Field;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fieldTipText() {
    return "The report field to obtain the split date from.";
  }

  /**
   * Sets the custom date format to use for parsing. Ignored if empty.
   *
   * @param value	the custom format
   */
  public void setCustomFormat(String value) {
    DateFormatString	format;
    
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
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    
    result  = super.getQuickInfo();
    result += QuickInfoHelper.toString(this, "field", m_Field, ", field: ");
    result += QuickInfoHelper.toString(this, "customFormat", (m_CustomFormat.isEmpty() ? "-default-" : m_CustomFormat), ", format: ");
    
    return result;
  }

  /**
   * Performs checks on the timeseries that is to be split.
   * <p/>
   * Ensures that report and field are present.
   * 
   * @param series	the timeseries to split
   */
  @Override
  protected void check(Timeseries series) {
    DateFormat	dfts;
    DateFormat	dftsm;
    DateFormat	dfd;
    String	dateStr;
    
    super.check(series);
    
    if (!series.hasReport())
      throw new IllegalStateException("No report available!");
    if (!series.getReport().hasValue(m_Field))
      throw new IllegalStateException("Report field '" + m_Field + "' not available!");
    
    dateStr = series.getReport().getStringValue(m_Field);
    if (m_CustomFormat.isEmpty()) {
      dfts  = new DateFormat(Constants.TIMESTAMP_FORMAT);
      dftsm = new DateFormat(Constants.TIMESTAMP_FORMAT_MSECS);
      dfd   = new DateFormat(Constants.DATE_FORMAT);
      if ((dfts.parse(dateStr) == null) && (dftsm.parse(dateStr) == null) && (dfd.parse(dateStr) == null))
	throw new IllegalStateException(
	    "Report field '" + m_Field + "' is not parseable using formats "
		+ "'" + Constants.TIMESTAMP_FORMAT + "' or "
		+ "'" + Constants.TIMESTAMP_FORMAT_MSECS + "' or "
		+ "'" + Constants.DATE_FORMAT + "'!");
    }
    else {
      // nothing to check
    }
  }
  
  /**
   * Performs the actual split.
   * 
   * @param series	the timeseries to split
   * @return		the generated sub-timeseries
   */
  @Override
  protected Timeseries[] doSplit(Timeseries series) {
    DateFormat		dfts;
    DateFormat		dftsm;
    DateFormat		dfd;
    String		dateStr;
    Date		date;
    
    // obtain date
    date    = null;
    dateStr = series.getReport().getStringValue(m_Field);
    if (m_CustomFormat.isEmpty()) {
      dfts    = new DateFormat(Constants.TIMESTAMP_FORMAT);
      dftsm   = new DateFormat(Constants.TIMESTAMP_FORMAT_MSECS);
      dfd     = new DateFormat(Constants.DATE_FORMAT);
      if (dfts.parse(dateStr) != null)
	date = dfts.parse(dateStr);
      else if (dftsm.parse(dateStr) != null)
	date = dftsm.parse(dateStr);
      else if (dfd.parse(dateStr) != null)
	date = dfd.parse(dateStr);
    }
    else {
      date = new DateFormat(m_CustomFormat).parse(dateStr);
    }
    
    return doSplit(series, date);
  }
}
