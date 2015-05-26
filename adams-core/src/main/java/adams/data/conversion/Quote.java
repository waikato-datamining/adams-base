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
 * Quote.java
 * Copyright (C) 2012-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import adams.core.ClassCrossReference;
import adams.core.Constants;
import adams.core.QuickInfoHelper;
import adams.core.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Surrounds a string with quotes (single or double), if necessary due to blanks or other characters (new line, tab) in the string.<br>
 * However, you can also 'force' the quoting.<br>
 * <br>
 * See also:<br>
 * adams.data.conversion.UnQuote<br>
 * adams.data.conversion.BackQuote<br>
 * adams.data.conversion.UnBackQuote
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-quote-type &lt;SINGLE|DOUBLE|DOUBLE_UP&gt; (property: quoteType)
 * &nbsp;&nbsp;&nbsp;The type of quote to use.
 * &nbsp;&nbsp;&nbsp;default: DOUBLE
 * </pre>
 * 
 * <pre>-double-up &lt;boolean&gt; (property: doubleUp)
 * &nbsp;&nbsp;&nbsp;If enabled, internal quotes get doubled up rather than escaped with backslashes.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-force &lt;boolean&gt; (property: force)
 * &nbsp;&nbsp;&nbsp;If enabled, forces the quoting of the string.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Quote
  extends AbstractStringConversion
  implements ClassCrossReference {

  /** for serialization. */
  private static final long serialVersionUID = 8391531953153963013L;

  /** 
   * The type of quote.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum QuoteType {
    /** single quote ('). */
    SINGLE,
    /** double quote ("). */
    DOUBLE,
    /** double quote ("), doubling up internal double quotes. */
    DOUBLE_UP
  }
  
  /** the quote type to use. */
  protected QuoteType m_QuoteType;

  /** whether to double up internal quotes rather than escaping them with backslashes. */
  protected boolean m_DoubleUp;

  /** whether to enforce quoting. */
  protected boolean m_Force;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Surrounds a string with quotes (single or double), if necessary due "
	+ "to blanks or other characters (new line, tab) in the string.\n"
	+ "However, you can also 'force' the quoting.";
  }

  /**
   * Returns the cross-referenced classes.
   *
   * @return		the classes
   */
  @Override
  public Class[] getClassCrossReferences() {
    return new Class[]{
	UnQuote.class,
	BackQuote.class,
	UnBackQuote.class
    };
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "quote-type", "quoteType",
	    QuoteType.DOUBLE);

    m_OptionManager.add(
	    "double-up", "doubleUp",
	    false);

    m_OptionManager.add(
	    "force", "force",
	    false);
  }

  /**
   * Sets the type of quote to use.
   *
   * @param value	the type
   */
  public void setQuoteType(QuoteType value) {
    m_QuoteType = value;
    reset();
  }

  /**
   * Returns the type of quote to use.
   *
   * @return 		the type
   */
  public QuoteType getQuoteType() {
    return m_QuoteType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String quoteTypeTipText() {
    return "The type of quote to use.";
  }

  /**
   * Sets whether to double up internal quotes rather than escaping with backslash.
   *
   * @param value	true if to double up
   */
  public void setDoubleUp(boolean value) {
    m_DoubleUp = value;
    reset();
  }

  /**
   * Returns whether to double up internal quotes rather than escaping with backslash.
   *
   * @return 		true if to double up
   */
  public boolean getDoubleUp() {
    return m_DoubleUp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String doubleUpTipText() {
    return "If enabled, internal quotes get doubled up rather than escaped with backslashes.";
  }

  /**
   * Sets whether to force quoting.
   *
   * @param value	true if to force quoting
   */
  public void setForce(boolean value) {
    m_Force = value;
    reset();
  }

  /**
   * Returns whether to force quoting.
   *
   * @return 		true if to force quoting
   */
  public boolean getForce() {
    return m_Force;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String forceTipText() {
    return "If enabled, forces the quoting of the string.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String		result;
    List<String> 	options;

    result = QuickInfoHelper.toString(this, "quoteType", m_QuoteType, "type: ");
    options = new ArrayList<String>();
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "force", m_Force, "force"));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "doubleUp", m_DoubleUp, "double-up"));
    result += QuickInfoHelper.flatten(options);

    return result;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    String	result;
    String	input;
    
    input = (String) m_Input;
    
    switch (m_QuoteType) {
      case DOUBLE:
	if (m_DoubleUp)
	  result = Utils.doubleUpQuotes(input, '"', Constants.BACKQUOTE_CHARS, Constants.BACKQUOTED_STRINGS);
	else
	  result = Utils.doubleQuote(input);
	if (m_Force && !result.startsWith("\""))
	  result = "\"" + result + "\"";
	break;
      case SINGLE:
	if (m_DoubleUp)
	  result = Utils.doubleUpQuotes(input, '\'', Constants.BACKQUOTE_CHARS, Constants.BACKQUOTED_STRINGS);
	else
	  result = Utils.quote(input);
	if (m_Force && !result.startsWith("'"))
	  result = "'" + result + "'";
	break;
      default:
	throw new IllegalStateException("Unhandled quote type: " + m_QuoteType);
    }

    return result;
  }
}
