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
 * AbstractForLoop.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import adams.core.QuickInfoHelper;
import adams.flow.core.Token;

/**
 * Abstract ancestor for for-loops.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractForLoop
  extends AbstractSource {

  /** for serialization. */
  private static final long serialVersionUID = 6216146938771296415L;

  /** the lower bound of the for loop. */
  protected int m_LoopLower;

  /** the upper bound of the for loop. */
  protected int m_LoopUpper;

  /** the step size. */
  protected int m_LoopStep;

  /** the current value. */
  protected int m_Current;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "lower", "loopLower",
	    1);

    m_OptionManager.add(
	    "upper", "loopUpper",
	    10);

    m_OptionManager.add(
	    "step", "loopStep",
	    1);
  }

  /**
   * Sets the lower bound of the loop.
   *
   * @param value	the lower bound
   */
  public void setLoopLower(int value) {
    m_LoopLower = value;
    reset();
  }

  /**
   * Returns the lower bound of the loop.
   *
   * @return		the lower bound
   */
  public int getLoopLower() {
    return m_LoopLower;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String loopLowerTipText() {
    return "The lower bound of the loop (= the first value).";
  }

  /**
   * Sets the upper bound of the loop.
   *
   * @param value	the upper bound
   */
  public void setLoopUpper(int value) {
    m_LoopUpper = value;
    reset();
  }

  /**
   * Returns the upper bound of the loop.
   *
   * @return		the upper bound
   */
  public int getLoopUpper() {
    return m_LoopUpper;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String loopUpperTipText() {
    return "The upper bound of the loop.";
  }

  /**
   * Sets the step size.
   *
   * @param value	the step size
   */
  public void setLoopStep(int value) {
    m_LoopStep = value;
    reset();
  }

  /**
   * Returns the step size.
   *
   * @return		the step size
   */
  public int getLoopStep() {
    return m_LoopStep;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String loopStepTipText() {
    return "The step size of the loop.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	loopStep;
    String	loopLower;
    String	loopUpper;

    loopStep  = QuickInfoHelper.getVariable(this, "loopStep");
    loopLower = QuickInfoHelper.getVariable(this, "loopLower");
    loopUpper = QuickInfoHelper.getVariable(this, "loopUpper");

    if ((m_LoopStep >= 0) || (loopStep != null)) {
      return   
	  "for (i = " + ((loopLower == null) ? m_LoopLower : loopLower) + "; "
	  + "i <= " + ((loopUpper == null) ? m_LoopUpper : loopUpper) + "; "
	  + "i += " + ((loopStep == null) ? m_LoopStep : loopStep) + ")";
    }
    else {
      return   
	  "for (i = " + ((loopUpper == null) ? m_LoopUpper : loopUpper) + "; "
	  + "i >= " + ((loopLower == null) ? m_LoopLower : loopLower) + "; "
	  + "i += " + ((loopStep == null) ? m_LoopStep : loopStep) + ")";
    }
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->java.lang.Integer.class<!-- flow-generates-end -->
   */
  public abstract Class[] generates();

  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;

    result = super.setUp();

    if (result == null) {
      if (m_LoopStep == 0)
	result = "Step size must be either greater or smaller than 0!";
      else if ((m_LoopLower > m_LoopUpper) && (m_LoopStep > 0))
	result = "Lower bound cannot be larger than upper bound!";
      else if ((m_LoopLower < m_LoopUpper) && (m_LoopStep < 0))
	result = "Upper bound cannot be larger than lower bound!";
    }

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    if (m_LoopStep > 0)
      m_Current = m_LoopLower;
    else
      m_Current = m_LoopUpper;

    return null;
  }

  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  public abstract Token output();

  /**
   * Checks whether there is pending output to be collected after
   * executing the flow item.
   *
   * @return		true if there is pending output
   */
  public boolean hasPendingOutput() {
    boolean	result;

    result = false;

    if (m_Executed) {
      if (m_LoopStep > 0)
	result = (m_Current <= m_LoopUpper);
      else
	result = (m_Current >= m_LoopLower);
    }

    return result;
  }
}
