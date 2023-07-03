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
 * TimestampedFormat.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink.simplelogging.format;

import adams.core.Constants;
import adams.core.DateFormat;
import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.data.DateFormatString;

import java.util.Date;

/**
 * Outputs the message using the specified format.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class TimestampedFormat
  extends AbstractSimpleFormat {

  private static final long serialVersionUID = 5348572885140295922L;

  /** the placeholder for the timestamp. */
  public final static String PLACEHOLDER_TIMESTAMP = "{T}";

  /** the placeholder for the message. */
  public final static String PLACEHOLDER_MESSAGE = "{M}";

  /** the timestamp format. */
  protected DateFormatString m_TimestampFormat;

  /** the date formatter in use. */
  protected transient DateFormat m_TimestampFormatter;

  /** the indentation to use for the message. */
  protected String m_MessageIndentation;

  /** the format string. */
  protected String m_LogFormat;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Outputs the message using the specified format.\n"
      + "Each lines of the message gets indented first using the specified indentation.\n"
      + "The available placeholders:\n"
      + "- timestamp: " + PLACEHOLDER_TIMESTAMP + "\n"
      + "- message: " + PLACEHOLDER_MESSAGE;
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "timestamp-format", "timestampFormat",
      new DateFormatString(Constants.TIMESTAMP_FORMAT_MSECS));

    m_OptionManager.add(
      "message-indentation", "messageIndentation",
      "");

    m_OptionManager.add(
      "log-format", "logFormat",
      PLACEHOLDER_TIMESTAMP + " - " + PLACEHOLDER_MESSAGE);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_TimestampFormatter = null;
  }

  /**
   * Sets the format for the timestamp.
   *
   * @param value the format
   */
  public void setTimestampFormat(DateFormatString value) {
    m_TimestampFormat = value;
    reset();
  }

  /**
   * Returns the format for the timestamp.
   *
   * @return the format
   */
  public DateFormatString getTimestampFormat() {
    return m_TimestampFormat;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for
   * displaying in the GUI or for listing the options.
   */
  public String timestampFormatTipText() {
    return "The format to use for generating the timestamp.";
  }

  /**
   * Sets the indentation string to prefix each line of the message with.
   *
   * @param value the indentation string
   */
  public void setMessageIndentation(String value) {
    m_MessageIndentation = Utils.unbackQuoteChars(value);
    reset();
  }

  /**
   * Returns the indentation string to prefix each line of the message with.
   *
   * @return the indentation string
   */
  public String getMessageIndentation() {
    return Utils.backQuoteChars(m_MessageIndentation);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for
   * displaying in the GUI or for listing the options.
   */
  public String messageIndentationTipText() {
    return "The indentation string to prefix each line in the message with; use \\n for new line and \\t for tab; splits the message on new lines.";
  }

  /**
   * Sets the format for the message.
   *
   * @param value the format
   */
  public void setLogFormat(String value) {
    m_LogFormat = Utils.unbackQuoteChars(value);
    reset();
  }

  /**
   * Returns the format for the message.
   *
   * @return the format
   */
  public String getLogFormat() {
    return Utils.backQuoteChars(m_LogFormat);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for
   * displaying in the GUI or for listing the options.
   */
  public String logFormatTipText() {
    return "The format to use for the message; use \\t for tabs and \\n for new lines.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "logFormat", m_LogFormat, "format: ");
  }

  /**
   * Formats the logging message and returns the updated message.
   *
   * @param msg the message to format
   * @return the formatted message
   */
  @Override
  protected String doFormatMessage(String msg) {
    String 		result;
    StringBuilder	m;
    String[]		lines;

    m = new StringBuilder();
    if (m_MessageIndentation.length() > 0) {
      lines = Utils.split(msg, "\n");
      for (String line: lines) {
        if (m.length() > 0)
          m.append("\n");
        m.append(m_MessageIndentation);
        m.append(line.stripTrailing());
      }
    }
    else {
      m.append(msg);
    }

    result = m_LogFormat;
    if (m_LogFormat.contains(PLACEHOLDER_TIMESTAMP)) {
      if (m_TimestampFormatter == null)
        m_TimestampFormatter = m_TimestampFormat.toDateFormat();
      result = result.replace(PLACEHOLDER_TIMESTAMP, m_TimestampFormatter.format(new Date()));
    }

    result = result.replace(PLACEHOLDER_MESSAGE, m.toString());

    return result;
  }
}
