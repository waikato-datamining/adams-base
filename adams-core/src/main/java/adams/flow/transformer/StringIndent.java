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
 * StringIndent.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.Utils;

/**
 <!-- globalinfo-start -->
 * Indents the string with the specified character.<br>
 * Splits multi-line strings first and processes each line separately.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String[]<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String[]<br>
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
 * &nbsp;&nbsp;&nbsp;default: StringIndent
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
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
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing 
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-indentation &lt;java.lang.String&gt; (property: indentation)
 * &nbsp;&nbsp;&nbsp;The backquoted string to use for indentation; use '\t' for tab.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-num-times &lt;int&gt; (property: numTimes)
 * &nbsp;&nbsp;&nbsp;The number of times to insert the indentation string.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class StringIndent
  extends AbstractStringOperation {

  /** for serialization. */
  private static final long serialVersionUID = 9030574317512531337L;

  /** the string to use for indentation. */
  protected String m_Indentation;

  /** the number of times to indent each line. */
  protected int m_NumTimes;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return
      "Indents the string with the specified character.\n"
      + "Splits multi-line strings first and processes each line separately.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "indentation", "indentation",
      "");

    m_OptionManager.add(
      "num-times", "numTimes",
      1, 0, null);
  }

  /**
   * Sets the backquoted string to use for indentation.
   *
   * @param value	the indentation string
   */
  public void setIndentation(String value) {
    m_Indentation = Utils.unbackQuoteChars(value);
    reset();
  }

  /**
   * Returns the backquoted string to use for indentation.
   *
   * @return		the indentation string
   */
  public String getIndentation() {
    return Utils.backQuoteChars(m_Indentation);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String indentationTipText() {
    return "The backquoted string to use for indentation; use '\\t' for tab.";
  }

  /**
   * Sets the number of times to insert the indentation string.
   *
   * @param value	the number of times
   */
  public void setNumTimes(int value) {
    if (getOptionManager().isValid("numTimes", value)) {
      m_NumTimes = value;
      reset();
    }
  }

  /**
   * Returns the number of times to insert the indentation string.
   *
   * @return		the number of times
   */
  public int getNumTimes() {
    return m_NumTimes;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numTimesTipText() {
    return "The number of times to insert the indentation string.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "numTimes", m_NumTimes)
      + " x "
      + QuickInfoHelper.toString(this, "indentation", "'" + m_Indentation + "'");
  }

  /**
   * Processes the string.
   *
   * @param s		the string to process
   * @return		the processed string
   */
  protected String process(String s) {
    StringBuilder	result;
    String[]		lines;
    int			i;
    int 		n;

    result = new StringBuilder();
    lines  = s.split("\n");
    for (i = 0; i < lines.length; i++) {
      if (i > 0)
	result.append("\n");
      for (n = 0; n < m_NumTimes; n++)
	result.append(m_Indentation);
      result.append(lines[i]);
    }

    return result.toString();
  }
}
