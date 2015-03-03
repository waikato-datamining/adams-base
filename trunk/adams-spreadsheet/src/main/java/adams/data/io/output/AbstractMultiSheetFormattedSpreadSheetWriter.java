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
 * AbstractMultiSheetFormattedSpreadSheetWriter.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.output;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.logging.Level;

import adams.core.Utils;
import adams.core.management.LocaleHelper;

/**
 * Ancestor for writers that format the numbers.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractMultiSheetFormattedSpreadSheetWriter
  extends AbstractMultiSheetSpreadSheetWriterWithMissingValueSupport {

  /** for serialization. */
  private static final long serialVersionUID = 7265152337280890568L;

  /** the locale to use. */
  protected Locale m_Locale;

  /** The format for the numbers. */
  protected String m_NumberFormat;

  /** the formatter to use. */
  protected transient NumberFormat m_Formatter;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "locale", "locale",
	    LocaleHelper.getSingleton().getDefault());

    m_OptionManager.add(
	    "number-format", "numberFormat",
	    getDefaultNumberFormat());
  }

  /**
   * Sets the locale to use.
   *
   * @param value	the locale
   */
  public void setLocale(Locale value) {
    m_Locale = value;
    reset();
  }

  /**
   * Returns the locale in use.
   *
   * @return 		the locale
   */
  public Locale getLocale() {
    return m_Locale;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String localeTipText() {
    return "The locale to use for formatting the numbers.";
  }

  /**
   * Returns the default number format.
   *
   * @return		the default format
   */
  protected String getDefaultNumberFormat() {
    return "";
  }

  /**
   * Sets the number format.
   *
   * @param value	the format
   */
  public void setNumberFormat(String value) {
    m_NumberFormat = value;
    reset();
  }

  /**
   * Returns the number format.
   *
   * @return		the format
   */
  public String getNumberFormat() {
    return m_NumberFormat;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   *         		displaying in the explorer/experimenter gui
   */
  public String numberFormatTipText() {
    return "The format for the numbers (see java.text.DecimalFormat), use empty string for default 'double' output.";
  }

  /**
   * Formats the number according to the format and returns the generated
   * textual representation.
   *
   * @param value	the double value to turn into a string
   * @return		the generated string
   */
  protected synchronized String format(double value) {
    String	result;

    if (m_NumberFormat.length() > 0) {
      if (m_Formatter == null) {
	try {
	  m_Formatter = LocaleHelper.getSingleton().getNumberFormat(m_Locale);
	  ((DecimalFormat) m_Formatter).applyPattern(m_NumberFormat);
	}
	catch (Exception e) {
	  getLogger().log(Level.SEVERE, "Failed to initialize formatter with format '" + m_NumberFormat + "':", e);
	  m_Formatter = LocaleHelper.getSingleton().getNumberFormat(m_Locale);
	}
      }

      result = m_Formatter.format(value);
    }
    else {
      result = Utils.doubleToString(value, 12, m_Locale);
    }

    return result;
  }
}
