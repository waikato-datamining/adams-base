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
 * LogFile.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.sink.sendnotification;

import adams.core.DateFormat;
import adams.core.QuickInfoHelper;
import adams.core.base.BaseCharset;
import adams.core.io.EncodingSupporter;
import adams.core.io.FileUtils;
import adams.core.io.FileWriter;
import adams.core.io.PlaceholderFile;
import adams.core.management.TimeZoneHelper;
import adams.data.DateFormatString;

import java.util.Date;
import java.util.TimeZone;

/**
 * Outputs the message to a log file.
 * Optional timestamp output on a separate line is available as well.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class LogFile
  extends AbstractNotification
  implements FileWriter, EncodingSupporter {

  private static final long serialVersionUID = 4577706017089540470L;

  /** the log file. */
  protected PlaceholderFile m_OutputFile;

  /** the encoding to use. */
  protected BaseCharset m_Encoding;

  /** whether to block the flow execution. */
  protected boolean m_AddTimestamp;

  /** the format to use. */
  protected DateFormatString m_Format;

  /** the timezone to use. */
  protected TimeZone m_TimeZone;

  /** for generating the timestamp. */
  protected transient DateFormat m_Formatter;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Outputs the message to a log file.\n"
      + "Optional timestamp output on a separate line is available as well.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "output-file", "outputFile",
      new PlaceholderFile());

    m_OptionManager.add(
      "encoding", "encoding",
      new BaseCharset());

    m_OptionManager.add(
      "add-timestamp", "addTimestamp",
      false);

    m_OptionManager.add(
      "format", "format",
      getDefaultFormat());

    m_OptionManager.add(
      "time-zone", "timeZone",
      TimeZone.getDefault(), false);
  }

  /**
   * Sets the output file.
   *
   * @param value	the file
   */
  public void setOutputFile(PlaceholderFile value) {
    m_OutputFile = value;
    reset();
  }

  /**
   * Returns the output file.
   *
   * @return		the file
   */
  public PlaceholderFile getOutputFile() {
    return m_OutputFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputFileTipText() {
    return "The log file to write to.";
  }

  /**
   * Sets the encoding to use.
   *
   * @param value	the encoding, e.g. "UTF-8" or "UTF-16", empty string for default
   */
  public void setEncoding(BaseCharset value) {
    m_Encoding = value;
    reset();
  }

  /**
   * Returns the encoding to use.
   *
   * @return		the encoding, e.g. "UTF-8" or "UTF-16", empty string for default
   */
  public BaseCharset getEncoding() {
    return m_Encoding;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String encodingTipText() {
    return "The type of encoding to use when reading the file, use empty string for default.";
  }

  /**
   * Sets whether to output a timestamp on separate line using the specified format.
   *
   * @param value 	true if to output timestamp
   */
  public void setAddTimestamp(boolean value) {
    m_AddTimestamp = value;
    reset();
  }

  /**
   * Returns whether to output a timestamp on separate line using the specified format.
   *
   * @return 		true if to output timestamp
   */
  public boolean getAddTimestamp() {
    return m_AddTimestamp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String addTimestampTipText() {
    return "If enabled, outputs a timestamp on separate line using the specified format.";
  }

  /**
   * Returns the instance of a date formatter to use.
   *
   * @return		the formatter object
   */
  protected synchronized DateFormat getFormatter() {
    if (m_Formatter == null)
      m_Formatter = m_Format.toDateFormat(m_TimeZone);

    return m_Formatter;
  }

  /**
   * Returns the default format to use.
   *
   * @return		the format
   */
  protected DateFormatString getDefaultFormat() {
    return new DateFormatString("yyyy-MM-dd HH:mm:ss");
  }

  /**
   * Sets the format string to use;
   * use single quotes for non-format chars, eg: "yyyyMMdd'T'HHmmss.'csv'".
   *
   * @param value	the format
   */
  public void setFormat(DateFormatString value) {
    m_Format = value;
    reset();
  }

  /**
   * Returns the format string in use;
   * use single quotes for non-format chars, eg: "yyyyMMdd'T'HHmmss.'csv'".
   *
   * @return		the format
   */
  public DateFormatString getFormat() {
    return m_Format;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String formatTipText() {
    return "The format of the timestamp to generate; "
      + "use single quotes for non-format chars, eg: \"yyyyMMdd'T'HHmmss.'csv'\"";
  }

  /**
   * Sets the time zone to use.
   *
   * @param value	the time zone
   */
  public void setTimeZone(TimeZone value) {
    m_TimeZone = value;
    reset();
  }

  /**
   * Returns the time zone in use.
   *
   * @return		the time zone
   */
  public TimeZone getTimeZone() {
    return m_TimeZone;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String timeZoneTipText() {
    return "The time zone to use for interpreting dates/times; default is the system-wide defined one.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "outputFile", m_OutputFile, "file: ");
    result += QuickInfoHelper.toString(this, "encoding", m_Encoding, ", encoding: ");
    if (m_AddTimestamp) {
      result += QuickInfoHelper.toString(this, "format", (!m_Format.isEmpty() ? ", format: " + m_Format.getValue() : ""));
      result += QuickInfoHelper.toString(this, "timeZone", TimeZoneHelper.toString(m_TimeZone), ", tz: ");
    }

    return result;
  }

  /**
   * Hook method before attempting to send the message.
   *
   * @param msg		the message to send
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String check(String msg) {
    String	result;

    result = super.check(msg);

    if (result == null) {
      if (m_OutputFile.isDirectory())
        result = "Log file points to a directory: " + m_OutputFile;
    }

    return result;
  }

  /**
   * Sends the notification.
   *
   * @param msg		the message to send
   * @return		null if successfully sent, otherwise error message
   */
  @Override
  protected String doSendNotification(String msg) {
    if (m_AddTimestamp)
      msg = getFormatter().format(new Date()) + "\n" + msg;
    return FileUtils.writeToFileMsg(m_OutputFile.getAbsolutePath(), msg, true, m_Encoding.getValue());
  }
}
