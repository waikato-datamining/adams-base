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
 * NewList.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.source;

import java.util.List;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.flow.core.Token;
import adams.flow.source.newlist.AbstractListGenerator;
import adams.flow.source.newlist.FixedList;

/**
 <!-- globalinfo-start -->
 * Generates a list string based on the items produced by the generator and the specified separator.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: NewList
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-generator &lt;adams.flow.source.newlist.AbstractListGenerator&gt; (property: generator)
 * &nbsp;&nbsp;&nbsp;The list generator to use.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.source.newlist.FixedList
 * </pre>
 * 
 * <pre>-separator &lt;java.lang.String&gt; (property: separator)
 * &nbsp;&nbsp;&nbsp;The separator to use between the list elements.
 * &nbsp;&nbsp;&nbsp;default: ,
 * </pre>
 * 
 * <pre>-quote-type &lt;NONE|SINGLE|DOUBLE&gt; (property: quoteType)
 * &nbsp;&nbsp;&nbsp;The type of quote to use.
 * &nbsp;&nbsp;&nbsp;default: NONE
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 7828 $
 */
public class NewList
  extends AbstractSimpleSource {

  /** for serialization. */
  private static final long serialVersionUID = 7272049518765623563L;

  /** 
   * The type of quoting to use use.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision: 6182 $
   */
  public enum QuoteType {
    /** none. */
    NONE,
    /** single quote ('). */
    SINGLE,
    /** double quote ("). */
    DOUBLE
  }

  /** the generator to use. */
  protected AbstractListGenerator m_Generator;
  
  /** the separator for the list items. */
  protected String m_Separator;
  
  /** the quote type to use. */
  protected QuoteType m_QuoteType;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Generates a list string based on the items produced by the generator "
	+ "and the specified separator.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "generator", "generator",
	    new FixedList());

    m_OptionManager.add(
	    "separator", "separator",
	    ",");

    m_OptionManager.add(
	    "quote-type", "quoteType",
	    QuoteType.NONE);
  }
  
  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "generator", m_Generator, "generator: ");
    result += QuickInfoHelper.toString(this, "separator", "'" + m_Separator + "'", ", sep: ");
    result += QuickInfoHelper.toString(this, "quoteType", m_QuoteType, ", quote: ");

    return result;
  }

  /**
   * Sets the generator to use.
   *
   * @param value	the generator
   */
  public void setGenerator(AbstractListGenerator value) {
    m_Generator = value;
    reset();
  }

  /**
   * Returns the generator to use.
   *
   * @return		the generator
   */
  public AbstractListGenerator getGenerator() {
    return m_Generator;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String generatorTipText() {
    return "The list generator to use.";
  }

  /**
   * Sets the list element separator to use.
   *
   * @param value	the separator
   */
  public void setSeparator(String value) {
    m_Separator = value;
    reset();
  }

  /**
   * Returns the list element separator to use.
   *
   * @return		the separator
   */
  public String getSeparator() {
    return m_Separator;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String separatorTipText() {
    return "The separator to use between the list elements.";
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
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{String.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    StringBuilder	list;
    List<String>	elements;
    
    result = null;
    
    try {
      elements = m_Generator.generate();
      list     = new StringBuilder();
      for (String element: elements) {
	if (list.length() > 0)
	  list.append(m_Separator);
	switch (m_QuoteType) {
	  case DOUBLE:
	    element = Utils.doubleQuote(element);
	    break;
	  case SINGLE:
	    element = Utils.quote(element);
	    break;
	  case NONE:
	    break;
	  default:
	    throw new IllegalStateException("Unhandled quote type: " + m_QuoteType);
	}
	list.append(element);
      }
      m_OutputToken = new Token(list.toString());
    }
    catch (Exception e) {
      result = handleException("Failed to generate list!", e);
    }
    
    return result;
  }
}
