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
 * StringArraySplit.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseRegExp;

import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Uses a regular expression to identify the string that triggers one or more splits in a string array, resulting in smaller arrays being output.<br>
 * What is done with the string triggering the split, is defined in the split handling option, e.g., discard it.<br>
 * Useful actor for splitting a log file into individual entry chunks.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String[]<br>
 * - generates:<br>
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
 * &nbsp;&nbsp;&nbsp;default: StringArraySplit
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
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-output-array &lt;boolean&gt; (property: outputArray)
 * &nbsp;&nbsp;&nbsp;If enabled, the generate sub-arrays are output as an array rather than one-by-one.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-regexp &lt;adams.core.base.BaseRegExp&gt; (property: regExp)
 * &nbsp;&nbsp;&nbsp;The regular expression used for matching the strings.
 * &nbsp;&nbsp;&nbsp;default: .*
 * </pre>
 *
 * <pre>-split-handling &lt;DISCARD|AT_START|AT_END&gt; (property: splitHandling)
 * &nbsp;&nbsp;&nbsp;Determines how the string is handled that triggered a split; DISCARD: string
 * &nbsp;&nbsp;&nbsp;gets discarded; AT_START: is added at the start of the new output array;
 * &nbsp;&nbsp;&nbsp;AT_END: is added at the end of the previous output array
 * &nbsp;&nbsp;&nbsp;default: DISCARD
 * </pre>
 *
 * <pre>-remainder-handling &lt;DISCARD|OUTPUT&gt; (property: remainderHandling)
 * &nbsp;&nbsp;&nbsp;Determines how the handle any left over strings after the last split.
 * &nbsp;&nbsp;&nbsp;default: OUTPUT
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class StringArraySplit
  extends AbstractArrayProvider {

  private static final long serialVersionUID = -1243814748394244839L;

  /**
   * Determines how to handle the string that triggered the split.
   */
  public enum SplitHandling {
    DISCARD,
    AT_START,
    AT_END,
  }

  /**
   * Determines how to handle any remainder of input data.
   */
  public enum RemainderHandling {
    DISCARD,
    OUTPUT,
  }

  /** the regular expression to identify the string to split on. */
  protected BaseRegExp m_RegExp;

  /** how to handle the string that triggered the split. */
  protected SplitHandling m_SplitHandling;

  /** how to handle any remaining strings. */
  protected RemainderHandling m_RemainderHandling;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Uses a regular expression to identify the string that triggers one or "
      + "more splits in a string array, resulting in smaller arrays being output.\n"
      + "What is done with the string triggering the split, is defined in the "
      + "split handling option, e.g., discard it.\n"
      + "Useful actor for splitting a log file into individual entry chunks.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "regexp", "regExp",
      new BaseRegExp(BaseRegExp.MATCH_ALL));

    m_OptionManager.add(
      "split-handling", "splitHandling",
      SplitHandling.DISCARD);

    m_OptionManager.add(
      "remainder-handling", "remainderHandling",
      RemainderHandling.OUTPUT);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String outputArrayTipText() {
    return "If enabled, the generate sub-arrays are output as an array rather than one-by-one.";
  }

  /**
   * Sets the regular expression to match the strings against.
   *
   * @param value	the regular expression
   */
  public void setRegExp(BaseRegExp value) {
    m_RegExp = value;
    reset();
  }

  /**
   * Returns the regular expression to match the strings against.
   *
   * @return		the regular expression
   */
  public BaseRegExp getRegExp() {
    return m_RegExp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String regExpTipText() {
    return "The regular expression used for matching the strings.";
  }

  /**
   * Sets how to handle the string that triggered the split.
   *
   * @param value	the handling
   */
  public void setSplitHandling(SplitHandling value) {
    m_SplitHandling = value;
    reset();
  }

  /**
   * Returns how to handle the string that triggered the split.
   *
   * @return		the handling
   */
  public SplitHandling getSplitHandling() {
    return m_SplitHandling;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String splitHandlingTipText() {
    return "Determines how the string is handled that triggered a split; "
      + SplitHandling.DISCARD + ": string gets discarded; "
      + SplitHandling.AT_START + ": is added at the start of the new output array; "
      + SplitHandling.AT_END + ": is added at the end of the previous output array";
  }

  /**
   * Sets how to handle any remaining strings after the last split.
   *
   * @param value	the handling
   */
  public void setRemainderHandling(RemainderHandling value) {
    m_RemainderHandling = value;
    reset();
  }

  /**
   * Returns how to handle any remaining strings after the last split.
   *
   * @return		the handling
   */
  public RemainderHandling getRemainderHandling() {
    return m_RemainderHandling;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String remainderHandlingTipText() {
    return "Determines how the handle any left over strings after the last split.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "regExp", m_RegExp, "regexp: ");
    result += QuickInfoHelper.toString(this, "splitHandling", m_SplitHandling, ", split: ");
    result += QuickInfoHelper.toString(this, "remainderHandling", m_RemainderHandling, ", remainder: ");

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{String[].class};
  }

  /**
   * Returns the base class of the items.
   *
   * @return		the class
   */
  @Override
  protected Class getItemClass() {
    return String[].class;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String[]		array;
    List<String> 	current;

    array   = m_InputToken.getPayload(String[].class);
    current = new ArrayList<>();
    m_Queue.clear();

    for (String line: array) {
      if (m_RegExp.isMatch(line)) {
        switch (m_SplitHandling) {
	  case DISCARD:
	    if (current.size() > 0)
	      m_Queue.add(current.toArray(new String[current.size()]));
	    current.clear();
	    break;
	  case AT_START:
	    if (current.size() > 0)
	      m_Queue.add(current.toArray(new String[current.size()]));
	    current.clear();
	    current.add(line);
	    break;
	  case AT_END:
	    current.add(line);
	    m_Queue.add(current.toArray(new String[current.size()]));
	    current.clear();
	    break;
	  default:
	    throw new IllegalStateException("Unhandled split handling: " + m_SplitHandling);
	}
      }
      else {
        current.add(line);
      }
    }

    // remainder?
    if (current.size() > 0) {
      switch (m_RemainderHandling) {
	case OUTPUT:
	  m_Queue.add(current.toArray(new String[current.size()]));
	  current.clear();
	  break;
	case DISCARD:
	  current.clear();
	  break;
	default:
	  throw new IllegalStateException("Unhandled remainder handling: " + m_RemainderHandling);
      }
    }

    return null;
  }
}
