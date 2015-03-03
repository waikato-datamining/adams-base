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
 * UnQuote.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import adams.core.ClassCrossReference;
import adams.core.Utils;
import adams.data.conversion.Quote.QuoteType;

/**
 <!-- globalinfo-start -->
 * Removes the quotes (single or double) surrounding a string. Also unbackquotes new lines and tabs.<br/>
 * <br/>
 * See also:<br/>
 * adams.data.conversion.Quote<br/>
 * adams.data.conversion.BackQuote<br/>
 * adams.data.conversion.UnBackQuote
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
 * <pre>-quote-type &lt;SINGLE|DOUBLE&gt; (property: quoteType)
 * &nbsp;&nbsp;&nbsp;The type of quote to use.
 * &nbsp;&nbsp;&nbsp;default: DOUBLE
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class UnQuote
  extends AbstractStringConversion 
  implements ClassCrossReference {
  
  /** for serialization. */
  private static final long serialVersionUID = -1362461206623476937L;
  
  /** the quote type to use. */
  protected QuoteType m_QuoteType;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Removes the quotes (single or double) surrounding a string. Also unbackquotes new lines and tabs.";
  }

  /**
   * Returns the cross-referenced classes.
   *
   * @return		the classes
   */
  @Override
  public Class[] getClassCrossReferences() {
    return new Class[]{
	Quote.class,
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
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    String	input;
    
    input = (String) m_Input;
    
    switch (m_QuoteType) {
      case DOUBLE:
	return Utils.unDoubleQuote(input);
      case SINGLE:
	return Utils.unquote(input);
      default:
	throw new IllegalStateException("Unhandled quote type: " + m_QuoteType);
    }
  }
}
