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
 * DoubleToString.java
 * Copyright (C) 2011-2012 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import java.util.Locale;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.management.LocaleHelper;
import adams.core.management.OptionHandlingLocaleSupporter;

/**
 <!-- globalinfo-start -->
 * Turns a Double into a String.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to 
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-num-decimals &lt;int&gt; (property: numDecimals)
 * &nbsp;&nbsp;&nbsp;The number of decimals for numeric values to use; -1 uses Java's Double.toString
 * &nbsp;&nbsp;&nbsp;() method.
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-fixed-decimals (property: fixedDecimals)
 * &nbsp;&nbsp;&nbsp;If enabled and 'num-decimals' is specified, a fixed number of decimals will 
 * &nbsp;&nbsp;&nbsp;get output (incl. trailing zeroes), otherwise up-to 'num-decimals'.
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DoubleToString
  extends AbstractConversionToString
  implements OptionHandlingLocaleSupporter {

  /** for serialization. */
  private static final long serialVersionUID = -9142177169642814841L;

  /** the locale to use. */
  protected Locale m_Locale;

  /** the number of decimals to use in the output. */
  protected int m_NumDecimals;

  /** whether to use a fixed number of decimals. */
  protected boolean m_FixedDecimals;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Turns a Double into a String.";
  }

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
	    "num-decimals", "numDecimals",
	    -1, -1, null);

    m_OptionManager.add(
	    "fixed-decimals", "fixedDecimals",
	    false);
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
   * Sets the number of decimals for numbers in tables.
   *
   * @param value	the number of decimals
   */
  public void setNumDecimals(int value) {
    if ((value >= 0) || (value == -1)) {
      m_NumDecimals = value;
      reset();
    }
    else {
      System.err.println("Number of decimals cannot be negative!");
    }
  }

  /**
   * Returns the number of decimals for numbers in tables.
   *
   * @return 		the number of decimals
   */
  public int getNumDecimals() {
    return m_NumDecimals;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numDecimalsTipText() {
    return "The number of decimals for numeric values to use; -1 uses Java's Double.toString() method.";
  }

  /**
   * Sets whether to always use a fixed number of decimals, incl trailing zeroes.
   *
   * @param value	true to use fixed number
   */
  public void setFixedDecimals(boolean value) {
    m_FixedDecimals = value;
    reset();
  }

  /**
   * Returns whether to always use a fixed number of decimals, incl trailing zeroes.
   *
   * @return 		true if fixed number used
   */
  public boolean getFixedDecimals() {
    return m_FixedDecimals;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fixedDecimalsTipText() {
    return 
	"If enabled and 'num-decimals' is specified, a fixed number of "
	+ "decimals will get output (incl. trailing zeroes), otherwise up-to "
	+ "'num-decimals'.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "locale", m_Locale, null);
    result += QuickInfoHelper.toString(this, "numDecimals", m_NumDecimals, ", decimals: ");
    result += QuickInfoHelper.toString(this, "fixedDeciumals", m_FixedDecimals, "fixed", ", ");
    
    return result;
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return Double.class;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    if (m_NumDecimals == -1) {
      return LocaleHelper.getSingleton().getNumberFormat(m_Locale).format((Double) m_Input);
    }
    else {
      if (m_FixedDecimals)
	return Utils.doubleToStringFixed((Double) m_Input, m_NumDecimals, m_Locale);
      else
	return Utils.doubleToString((Double) m_Input, m_NumDecimals, m_Locale);
    }
  }
}
