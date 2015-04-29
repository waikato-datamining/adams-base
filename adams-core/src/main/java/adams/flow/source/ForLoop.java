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
 * ForLoop.java
 * Copyright (C) 2009-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import adams.flow.core.ArrayProvider;
import adams.flow.core.Token;

import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Emulates the following for-loop for integer IDs:<br/>
 * - positive step size:<br/>
 *   for (int i = lower; i &lt;= upper; i += step)<br/>
 * - negative step size:<br/>
 *   for (int i = upper; i &gt;= lower; i += step)<br/>
 * <br/>
 * The integers can be output as a single array as well.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;java.lang.Integer<br/>
 * <p/>
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
 * &nbsp;&nbsp;&nbsp;default: ForLoop
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
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-lower &lt;int&gt; (property: loopLower)
 * &nbsp;&nbsp;&nbsp;The lower bound of the loop (= the first value).
 * &nbsp;&nbsp;&nbsp;default: 1
 * </pre>
 * 
 * <pre>-upper &lt;int&gt; (property: loopUpper)
 * &nbsp;&nbsp;&nbsp;The upper bound of the loop.
 * &nbsp;&nbsp;&nbsp;default: 10
 * </pre>
 * 
 * <pre>-step &lt;int&gt; (property: loopStep)
 * &nbsp;&nbsp;&nbsp;The step size of the loop.
 * &nbsp;&nbsp;&nbsp;default: 1
 * </pre>
 * 
 * <pre>-output-array &lt;boolean&gt; (property: outputArray)
 * &nbsp;&nbsp;&nbsp;Whether to output the integers one-by-one or as array.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ForLoop
  extends AbstractForLoop
  implements ArrayProvider {

  /** for serialization. */
  private static final long serialVersionUID = 6216146938771296415L;

  /** whether to output an array. */
  protected boolean m_OutputArray;

  /** the array to output. */
  protected Integer[] m_Array;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Emulates the following for-loop for integer IDs:\n"
      + "- positive step size:\n"
      + "  for (int i = lower; i <= upper; i += step)\n"
      + "- negative step size:\n"
      + "  for (int i = upper; i >= lower; i += step)\n"
      + "\n"
      + "The integers can be output as a single array as well.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "output-array", "outputArray",
	    false);
  }

  /**
   * Sets whether to output the items as array or as single strings.
   *
   * @param value	true if output is an array
   */
  public void setOutputArray(boolean value) {
    m_OutputArray = value;
    reset();
  }

  /**
   * Returns whether to output the items as array or as single strings.
   *
   * @return		true if output is an array
   */
  public boolean getOutputArray() {
    return m_OutputArray;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputArrayTipText() {
    return "Whether to output the integers one-by-one or as array.";
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->java.lang.Integer.class<!-- flow-generates-end -->
   */
  @Override
  public Class[] generates() {
    if (m_OutputArray)
      return new Class[]{Integer[].class};
    else
      return new Class[]{Integer.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    List<Integer>     array;
    int               i;

    if (m_OutputArray) {
      array = new ArrayList<>();
      for (i = m_LoopLower; i <= m_LoopUpper; i += m_LoopStep)
        array.add(i);
      m_Array = array.toArray(new Integer[array.size()]);
    }
    else {
      return super.doExecute();
    }

    return null;
  }

  /**
   * Checks whether there is pending output to be collected after
   * executing the flow item.
   *
   * @return		true if there is pending output
   */
  @Override
  public boolean hasPendingOutput() {
    if (m_OutputArray)
      return m_Executed && (m_Array != null);
    else
      return super.hasPendingOutput();
  }

  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  @Override
  public Token output() {
    Token	result;

    if (m_OutputArray) {
      result  = new Token(m_Array);
      m_Array = null;
    }
    else {
      if (isLoggingEnabled())
        getLogger().info("i=" + m_Current);

      result = new Token(new Integer(m_Current));
      m_Current += m_LoopStep;
    }

    return result;
  }
}
