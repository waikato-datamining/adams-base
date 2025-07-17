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
 * ProgressConsole.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.sink;

import adams.core.QuickInfoHelper;
import adams.data.DecimalFormatString;

import java.text.DecimalFormat;

/**
 <!-- globalinfo-start -->
 * Outputs progress information in the console. The incoming token is used as 'current' value to be displayed. For convenience, the incoming token representing a number can also be in string format. Only outputs something if different from the last output.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.Number<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: ProgressConsole
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
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-min &lt;double&gt; (property: minimum)
 * &nbsp;&nbsp;&nbsp;The minimum to use.
 * &nbsp;&nbsp;&nbsp;default: 0.0
 * </pre>
 *
 * <pre>-max &lt;double&gt; (property: maximum)
 * &nbsp;&nbsp;&nbsp;The maximum to use.
 * &nbsp;&nbsp;&nbsp;default: 100.0
 * </pre>
 *
 * <pre>-prefix &lt;java.lang.String&gt; (property: prefix)
 * &nbsp;&nbsp;&nbsp;The prefix string to print before the percentage.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-format &lt;adams.data.DecimalFormatString&gt; (property: format)
 * &nbsp;&nbsp;&nbsp;The format string to use for outputting the current value, use empty string
 * &nbsp;&nbsp;&nbsp;to suppress output.
 * &nbsp;&nbsp;&nbsp;default: #.#%
 * &nbsp;&nbsp;&nbsp;more: https:&#47;&#47;docs.oracle.com&#47;en&#47;java&#47;javase&#47;11&#47;docs&#47;api&#47;java.base&#47;java&#47;text&#47;DecimalFormat.html
 * </pre>
 *
 * <pre>-suffix &lt;java.lang.String&gt; (property: suffix)
 * &nbsp;&nbsp;&nbsp;The suffix string to print before the percentage.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-title &lt;java.lang.String&gt; (property: title)
 * &nbsp;&nbsp;&nbsp;The 'title' to use, used as prefix to the generated progress string, separated
 * &nbsp;&nbsp;&nbsp;by ': '
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ProgressConsole
  extends AbstractSink {

  /** for serialization. */
  private static final long serialVersionUID = -4075776040257181463L;

  /** the minimum of the progress bar. */
  protected double m_Minimum;

  /** the maximum of the progress bar. */
  protected double m_Maximum;

  /** the prefix. */
  protected String m_Prefix;

  /** the format of the current value. */
  protected DecimalFormatString m_Format;

  /** the suffix. */
  protected String m_Suffix;

  /** the title. */
  protected String m_Title;

  /** the actual format. */
  protected transient DecimalFormat m_ActualFormat;

  /** the last string that was output. */
  protected transient String m_LastOutput;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Outputs progress information in the console. The incoming token is used as 'current' value "
	+ "to be displayed. For convenience, the incoming token representing a "
	+ "number can also be in string format. Only outputs something if different from the last output.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "min", "minimum",
      0.0);

    m_OptionManager.add(
      "max", "maximum",
      100.0);

    m_OptionManager.add(
      "prefix", "prefix",
      "");

    m_OptionManager.add(
      "format", "format",
      new DecimalFormatString("#.#%"));

    m_OptionManager.add(
      "suffix", "suffix",
      "");

    m_OptionManager.add(
      "title", "title",
      "");
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_LastOutput = null;
  }

  /**
   * Sets the minimum.
   *
   * @param value	the minimum
   */
  public void setMinimum(double value) {
    m_Minimum = value;
    reset();
  }

  /**
   * Returns the minimum.
   *
   * @return		the minimum
   */
  public double getMinimum() {
    return m_Minimum;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String minimumTipText() {
    return "The minimum to use.";
  }

  /**
   * Sets the maximum.
   *
   * @param value	the maximum
   */
  public void setMaximum(double value) {
    m_Maximum = value;
    reset();
  }

  /**
   * Returns the maximum.
   *
   * @return		the maximum
   */
  public double getMaximum() {
    return m_Maximum;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String maximumTipText() {
    return "The maximum to use.";
  }

  /**
   * Sets the prefix string.
   *
   * @param value	the prefix
   */
  public void setPrefix(String value) {
    m_Prefix = value;
    reset();
  }

  /**
   * Returns the prefix string.
   *
   * @return		the prefix
   */
  public String getPrefix() {
    return m_Prefix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String prefixTipText() {
    return "The prefix string to print before the percentage.";
  }

  /**
   * Sets the optional title string.
   *
   * @param value	the title
   */
  public void setTitle(String value) {
    m_Title = value;
    reset();
  }

  /**
   * Returns the optional title string.
   *
   * @return		the title
   */
  public String getTitle() {
    return m_Title;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String titleTipText() {
    return "The 'title' to use, used as prefix to the generated progress string, separated by ': '";
  }

  /**
   * Sets the format string for the current value.
   *
   * @param value	the format, empty string to suppress output
   */
  public void setFormat(DecimalFormatString value) {
    m_Format = value;
    reset();
  }

  /**
   * Returns the format string for the current value.
   *
   * @return		the format, empty string is suppressing output
   */
  public DecimalFormatString getFormat() {
    return m_Format;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String formatTipText() {
    return "The format string to use for outputting the current value, use empty string to suppress output.";
  }

  /**
   * Sets the suffix string.
   *
   * @param value	the suffix
   */
  public void setSuffix(String value) {
    m_Suffix = value;
    reset();
  }

  /**
   * Returns the suffix string.
   *
   * @return		the suffix
   */
  public String getSuffix() {
    return m_Suffix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String suffixTipText() {
    return "The suffix string to print before the percentage.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String		result;

    result  = QuickInfoHelper.toString(this, "minimum", m_Minimum, "min: ");
    result += QuickInfoHelper.toString(this, "maximum", m_Maximum, ", max: ");
    result += QuickInfoHelper.toString(this, "format", m_Format, ", format: ");

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Number.class, String.class};
  }

  /**
   * Executes the flow item.
   *
   * @return null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    double	value;
    double	perc;
    String	curr;

    if (m_InputToken.hasPayload(String.class))
      value = Double.parseDouble(m_InputToken.getPayload(String.class));
    else
      value = ((Number) m_InputToken.getPayload()).doubleValue();
    perc = (value - getMinimum()) / (getMaximum()- getMinimum());
    if (m_ActualFormat == null)
      m_ActualFormat = getFormat().toDecimalFormat();
    curr = getPrefix() + m_ActualFormat.format(perc) + getSuffix();
    if (!getTitle().isEmpty())
      curr = getTitle() + ": " + curr;

    if ((m_LastOutput == null) || !m_LastOutput.equals(curr)) {
      if (isLoggingEnabled())
	getLogger().info(curr);
      else
	System.out.println(curr);
    }

    m_LastOutput = curr;

    return null;
  }
}
