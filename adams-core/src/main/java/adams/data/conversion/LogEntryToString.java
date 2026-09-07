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
 * LogEntryToString.java
 * Copyright (C) 2026 University of Waikato, Hamilton, New Zealand
 */

package adams.data.conversion;

import adams.core.DateFormat;
import adams.core.DateUtils;
import adams.core.Properties;
import adams.core.base.BaseText;
import adams.db.LogEntry;

/**
 <!-- globalinfo-start -->
 * Turns the log entry into a string.<br>
 * Available placeholders:<br>
 * - {DBID}<br>
 * - {HOST}<br>
 * - {IP}<br>
 * - {TYPE}<br>
 * - {GENERATION} (date, format: YYYY-MM-DD hh:mm:ss.SSS)<br>
 * - {SOURCE}<br>
 * - {STATUS}<br>
 * - {MESSAGE}<br>
 * <br>
 * Using a placeholder of format {MESSAGE.PROP} you can access the particular property PROP of the message itself.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-format &lt;adams.core.base.BaseText&gt; (property: format)
 * &nbsp;&nbsp;&nbsp;The format to use for the log entry.
 * &nbsp;&nbsp;&nbsp;default: Host: {HOST}\\nIP: {IP}\\nType: {TYPE}\\nGeneration: {GENERATION}\\nSource: {SOURCE}\\nStatus: {STATUS}\\nMessage:\\n{MESSAGE}\\n
 * </pre>
 *
 <!-- options-end -->
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class LogEntryToString
  extends AbstractConversionToString {

  private static final long serialVersionUID = -1681159988765564005L;

  public final static String PH_DBID = "{DBID}";

  public final static String PH_HOST = "{HOST}";

  public final static String PH_IP = "{IP}";

  public final static String PH_TYPE = "{TYPE}";

  public final static String PH_GENERATION = "{GENERATION}";

  public final static String PH_SOURCE = "{SOURCE}";

  public final static String PH_STATUS = "{STATUS}";

  public final static String PH_MESSAGE = "{MESSAGE}";

  public final static String PH_MESSAGE_PROPERTY_PREFIX = "{MESSAGE.";

  /** the format string. */
  protected BaseText m_Format;

  /** for formatting the date. */
  protected transient DateFormat m_DateFormat;

  /**
   * Default constructor.
   */
  public LogEntryToString() {
    super();
  }

  /**
   * Initializes the conversion with the specified format.
   *
   * @param format	the format to use
   */
  public LogEntryToString(String format) {
    this(new BaseText(format));
  }

  /**
   * Initializes the conversion with the specified format.
   *
   * @param format	the format to use
   */
  public LogEntryToString(BaseText format) {
    this();
    setFormat(format);
  }

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Turns the log entry into a string.\n"
	     + "Available placeholders:\n"
	     + "- " + PH_DBID + "\n"
	     + "- " + PH_HOST + "\n"
	     + "- " + PH_IP + "\n"
	     + "- " + PH_TYPE + "\n"
	     + "- " + PH_GENERATION + " (date, format: YYYY-MM-DD hh:mm:ss.SSS)\n"
	     + "- " + PH_SOURCE + "\n"
	     + "- " + PH_STATUS + "\n"
	     + "- " + PH_MESSAGE + "\n\n"
	     + "Using a placeholder of format " + PH_MESSAGE_PROPERTY_PREFIX + "PROP} you can access the particular property PROP of the message itself.";
  }

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "format", "format",
      new BaseText(
	"Host: " + PH_HOST + "\n"
	  + "IP: " + PH_IP + "\n"
	  + "Type: " + PH_TYPE + "\n"
	  + "Generation: " + PH_GENERATION + "\n"
	  + "Source: " + PH_SOURCE + "\n"
	  + "Status: " + PH_STATUS + "\n"
	  + "Message:\n" + PH_MESSAGE + "\n"
      ));
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_DateFormat = null;
  }

  /**
   * Sets the format for the log entry.
   *
   * @param value	the format
   */
  public void setFormat(BaseText value) {
    m_Format = value;
    reset();
  }

  /**
   * Returns the format for the log entry.
   *
   * @return 		the format
   */
  public BaseText getFormat() {
    return m_Format;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String formatTipText() {
    return "The format to use for the log entry.";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return the class
   */
  @Override
  public Class accepts() {
    return LogEntry.class;
  }

  /**
   * Performs the actual conversion.
   *
   * @throws Exception if something goes wrong with the conversion
   * @return the converted data
   */
  @Override
  protected Object doConvert() throws Exception {
    String	result;
    LogEntry	log;
    Properties	props;

    if (m_DateFormat == null)
      m_DateFormat = DateUtils.getTimestampFormatterMsecs();

    log    = (LogEntry) m_Input;
    result = m_Format.getValue();
    result = result.replace(PH_DBID, "" + log.getLargeDatabaseID());
    result = result.replace(PH_HOST, log.getHost());
    result = result.replace(PH_IP, log.getIP());
    result = result.replace(PH_TYPE, log.getType());
    result = result.replace(PH_GENERATION, m_DateFormat.format(log.getGeneration()));
    result = result.replace(PH_SOURCE, log.getSource());
    result = result.replace(PH_STATUS, log.getStatus());
    result = result.replace(PH_MESSAGE, log.getMessage());
    if (result.contains(PH_MESSAGE_PROPERTY_PREFIX)) {
      props = log.getMessageAsProperties();
      for (String key: props.keySetAll())
	result = result.replace(PH_MESSAGE_PROPERTY_PREFIX + key + "}", props.getProperty(key));
    }

    return result;
  }
}
