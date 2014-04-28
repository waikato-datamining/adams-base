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
 * StringIndexOf.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.util.ArrayList;
import java.util.List;

import adams.core.Index;
import adams.core.QuickInfoHelper;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Determines the position of a substring in the string passing through.<br/>
 * The generated index is 1-based, with 0 indicating that the substring could not be located.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;java.lang.Integer<br/>
 * <p/>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: StringIndexOf
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
 * <pre>-find &lt;java.lang.String&gt; (property: find)
 * &nbsp;&nbsp;&nbsp;The string to look for.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-from-index &lt;adams.core.Index&gt; (property: fromIndex)
 * &nbsp;&nbsp;&nbsp;The index to start the search from.
 * &nbsp;&nbsp;&nbsp;default: first
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 * 
 * <pre>-case-sensitive &lt;boolean&gt; (property: caseSensitive)
 * &nbsp;&nbsp;&nbsp;If enabled, a case-sensitive search is performed.
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 * 
 * <pre>-backward &lt;boolean&gt; (property: backward)
 * &nbsp;&nbsp;&nbsp;If enabled, the search starts from the back rather than from the start of 
 * &nbsp;&nbsp;&nbsp;the string.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class StringIndexOf
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -3687113148170774846L;

  /** the string to look for. */
  protected String m_Find;

  /** the starting index. */
  protected Index m_FromIndex;

  /** whether to perform case-sensitive search. */
  protected boolean m_CaseSensitive;

  /** whether to start search from the back of the string. */
  protected boolean m_Backward;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Determines the position of a substring in the string passing through.\n"
	+ "The generated index is 1-based, with 0 indicating that the substring "
	+ "could not be located.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "find", "find",
	    "");

    m_OptionManager.add(
	    "from-index", "fromIndex",
	    new Index(Index.FIRST));

    m_OptionManager.add(
	    "case-sensitive", "caseSensitive",
	    true);

    m_OptionManager.add(
	    "backward", "backward",
	    false);
  }
  
  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String		result;
    List<String>	options;

    result  = QuickInfoHelper.toString(this, "find", m_Find, "find: ");
    result += QuickInfoHelper.toString(this, "fromIndex", m_FromIndex, ", from: ");

    // further options
    options = new ArrayList<String>();
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "caseSensitive", m_CaseSensitive, "case-sensitive"));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "backward", m_Backward, "backward"));
    result += QuickInfoHelper.flatten(options);

    return result;
  }

  /**
   * Sets the regular expression used for splitting the string.
   *
   * @param value	the expression
   */
  public void setFind(String value) {
    m_Find = value;
    reset();
  }

  /**
   * Returns the regular expression for splitting the string.
   *
   * @return		the expression
   */
  public String getFind() {
    return m_Find;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String findTipText() {
    return "The string to look for.";
  }

  /**
   * Sets the index to start the search from.
   *
   * @param value	the index
   */
  public void setFromIndex(Index value) {
    m_FromIndex = value;
    reset();
  }

  /**
   * Returns the index to start the search from.
   *
   * @return		the index
   */
  public Index getFromIndex() {
    return m_FromIndex;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fromIndexTipText() {
    return "The index to start the search from.";
  }

  /**
   * Sets whether to perform case-sensitive search.
   *
   * @param value	true if to search case-sensitive
   */
  public void setCaseSensitive(boolean value) {
    m_CaseSensitive = value;
    reset();
  }

  /**
   * Returns whether to perform case-sensitive search.
   *
   * @return		true if case-sensitive search
   */
  public boolean getCaseSensitive() {
    return m_CaseSensitive;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String caseSensitiveTipText() {
    return "If enabled, a case-sensitive search is performed.";
  }

  /**
   * Sets whether to start the search from the back rather than the front.
   *
   * @param value	true if to search backwards
   */
  public void setBackward(boolean value) {
    m_Backward = value;
    reset();
  }

  /**
   * Returns whether to start the search from the rather than the front.
   *
   * @return		true if backwards search
   */
  public boolean getBackward() {
    return m_Backward;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String backwardTipText() {
    return "If enabled, the search starts from the back rather than from the start of the string.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->java.lang.String.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{String.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->java.lang.Integer.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{Integer.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    String	str;
    int		from;
    Integer	index;
    String	find;

    result = null;

    str  = (String) m_InputToken.getPayload();
    find = m_Find;
    if (!m_CaseSensitive) {
      str  = str.toLowerCase();
      find = find.toLowerCase();
    }
    
    m_FromIndex.setMax(str.length());
    from = m_FromIndex.getIntIndex();

    if (m_Backward)
      index = str.lastIndexOf(find, from) + 1;
    else
      index = str.indexOf(find, from) + 1;
    
    m_OutputToken = new Token(index);

    return result;
  }
}
