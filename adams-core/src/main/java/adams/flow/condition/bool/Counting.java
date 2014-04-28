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
 * Counting.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.condition.bool;

import adams.core.QuickInfoHelper;
import adams.flow.core.Actor;
import adams.flow.core.Token;
import adams.flow.core.Unknown;

/**
 <!-- globalinfo-start -->
 * Counts the tokens passing through and returns 'true' if min&#47;max&#47;interval are met.
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
 * <pre>-min &lt;int&gt; (property: minimum)
 * &nbsp;&nbsp;&nbsp;The minimum number of tokens to count before activating (-1 to disable).
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-max &lt;int&gt; (property: maximum)
 * &nbsp;&nbsp;&nbsp;The maximum number of tokens to count before de-activating (-1 to disable
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-interval &lt;int&gt; (property: interval)
 * &nbsp;&nbsp;&nbsp;The number of tokens (or multiples) to count before teeing-off the input 
 * &nbsp;&nbsp;&nbsp;token.
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Counting
  extends AbstractBooleanCondition {

  /** for serialization. */
  private static final long serialVersionUID = 8559824356449366329L;

  /** the minimum number of tokens to process before activating. */
  protected int m_Minimum;

  /** the maximum number of tokens to process before de-activating. */
  protected int m_Maximum;

  /** the number of tokens after which to tee-off token. */
  protected int m_Interval;

  /** the current count. */
  protected int m_Current;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Counts the tokens passing through and returns 'true' if "
      + "min/max/interval are met.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "min", "minimum",
	    -1, -1, null);

    m_OptionManager.add(
	    "max", "maximum",
	    -1, -1, null);

    m_OptionManager.add(
	    "interval", "interval",
	    -1, -1, null);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Current = 0;
  }

  /**
   * Returns the counter of how many tokens have passed through so far.
   *
   * @return		the current counter value
   */
  protected int getCurrent() {
    return m_Current;
  }

  /**
   * Sets the minimum number of counts before activating.
   *
   * @param value	the minimum (-1 to disable)
   */
  public void setMinimum(int value) {
    if (value < -1)
      value = -1;

    m_Minimum = value;
    reset();
  }

  /**
   * Returns the minimum of counts before activating.
   *
   * @return		the minimum (-1 if disabled)
   */
  public int getMinimum() {
    return m_Minimum;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String minimumTipText() {
    return "The minimum number of tokens to count before activating (-1 to disable).";
  }

  /**
   * Sets the maximum number of counts before de-activating.
   *
   * @param value	the maximum (-1 to disable)
   */
  public void setMaximum(int value) {
    if (value < -1)
      value = -1;

    m_Maximum = value;
    reset();
  }

  /**
   * Returns the maximum of counts before de-activating.
   *
   * @return		the maximum (-1 if disabled)
   */
  public int getMaximum() {
    return m_Maximum;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String maximumTipText() {
    return "The maximum number of tokens to count before de-activating (-1 to disable).";
  }

  /**
   * Sets the number of tokens after which to tee off the input token.
   *
   * @param value	the number
   */
  public void setInterval(int value) {
    if (value < -1)
      value = -1;

    m_Interval = value;
    reset();
  }

  /**
   * Returns the number of tokens after which to tee off the input token.
   *
   * @return		the number
   */
  public int getInterval() {
    return m_Interval;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String intervalTipText() {
    return "The number of tokens (or multiples) to count before teeing-off the input token.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    
    result  = QuickInfoHelper.toString(this, "minimum", m_Minimum, "min=");
    result += QuickInfoHelper.toString(this, "maximum", m_Maximum, ", max=");
    result += QuickInfoHelper.toString(this, "interval", m_Interval, ", interval=");
    
    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		adams.flow.core.Unknown.class
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Unknown.class};
  }

  /**
   * Performs the actual evaluation.
   *
   * @param owner	the owning actor
   * @param token	the current token passing through
   * @return		the result of the evaluation
   */
  @Override
  protected boolean doEvaluate(Actor owner, Token token) {
    boolean		result;

    m_Current++;

    // can we tee-off?
    result = true;
    if (result && (getMinimum() > -1) && (getCurrent() < getMinimum()))
      result = false;
    if (result && (getMaximum() > -1) && (getCurrent() > getMaximum()))
      result = false;
    if (result && (getInterval() > -1) && (getCurrent() % getInterval() != 0))
      result = false;

    if (isLoggingEnabled())
      getLogger().info(
	  "current=" + getCurrent()
	  + ", min=" + getMinimum()
	  + ", max=" + getMaximum()
	  + ", interval=" + getInterval() + ": " + result);

    return result;
  }
}
