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
 * TimestampFile.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.control.flowrestart.notification;

import adams.core.DateFormat;
import adams.core.QuickInfoHelper;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.core.management.TimeZoneHelper;
import adams.data.DateFormatString;
import adams.flow.control.Flow;

import java.util.Date;
import java.util.TimeZone;

/**
 * Updates the timestamp in the specified file.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class TimestampFile
  extends AbstractNotification {

  private static final long serialVersionUID = -4491368520369909199L;

  /** the format to use. */
  protected DateFormatString m_Format;

  /** the timezone to use. */
  protected TimeZone m_TimeZone;

  /** for generating the timestamp. */
  protected transient DateFormat m_Formatter;

  /** the file to update. */
  protected PlaceholderFile m_File;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Updates the timestamp in the specified file.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "format", "format",
      getDefaultFormat());

    m_OptionManager.add(
      "time-zone", "timeZone",
      TimeZone.getDefault(), false);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Formatter = null;
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
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "format", (!m_Format.isEmpty() ? m_Format.getValue() : null));
    result += QuickInfoHelper.toString(this, "timeZone", TimeZoneHelper.toString(m_TimeZone), ", tz: ");
    result += QuickInfoHelper.toString(this, "file", m_File, ", file: ");

    return result;
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
   * Sets the format string to use.
   *
   * @param value	the format
   */
  public void setFormat(DateFormatString value) {
    m_Format = value;
    reset();
  }

  /**
   * Returns the format string in use.
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
    return "The format of the timestamp to generate.";
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
   * Sets the file to update.
   *
   * @param value	the file
   */
  public void setFile(PlaceholderFile value) {
    m_File = value;
    reset();
  }

  /**
   * Returns the file to update.
   *
   * @return		the file
   */
  public PlaceholderFile getFile() {
    return m_File;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String fileTipText() {
    return "The file to update.";
  }

  /**
   * Sends a notification.
   *
   * @param flow	the flow that triggered the notification
   * @return		null if successfully notified, otherwise the error message
   */
  @Override
  public String notify(Flow flow) {
    String	result;
    String	timestamp;

    timestamp = getFormatter().format(new Date());
    result    = FileUtils.writeToFileMsg(m_File.getAbsolutePath(), timestamp, false, null);

    return result;
  }
}
