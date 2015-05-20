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
 * ForLoop.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.source.newlist;

import java.util.ArrayList;
import java.util.List;

import adams.core.QuickInfoHelper;

/**
 <!-- globalinfo-start -->
 * Emulates the following for-loop for integer IDs:<br>
 * - positive step size:<br>
 *   for (int i = lower; i &lt;= upper; i += step)<br>
 * - negative step size:<br>
 *   for (int i = upper; i &gt;= lower; i += step)
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
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
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ForLoop
  extends AbstractListGenerator {

  /** for serialization. */
  private static final long serialVersionUID = -4623795710416726074L;

  /** the lower bound of the for loop. */
  protected int m_LoopLower;

  /** the upper bound of the for loop. */
  protected int m_LoopUpper;

  /** the step size. */
  protected int m_LoopStep;

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
      + "  for (int i = upper; i >= lower; i += step)";
  }

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
   * Hook method for checks.
   * <br><br>
   * Ensures that loop setup makes sense.
   * 
   * @return		the list of elements
   * @throws Exception	if check fails
   */
  @Override
  protected void check() throws Exception {
    super.check();
    
    if (m_LoopStep == 0)
      throw new IllegalStateException("Step size must be either greater or smaller than 0!");
    if ((m_LoopLower > m_LoopUpper) && (m_LoopStep > 0))
      throw new IllegalStateException("Lower bound cannot be larger than upper bound!");
    if ((m_LoopLower < m_LoopUpper) && (m_LoopStep < 0))
      throw new IllegalStateException("Upper bound cannot be larger than lower bound!");
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
   * Generates the actual list.
   * 
   * @return		the list of elements
   * @throws Exception	if generation fails
   */
  @Override
  protected List<String> doGenerate() throws Exception {
    List<String>	result;
    
    result = new ArrayList<String>();

    if (m_LoopStep > 0) {
      for (int i = m_LoopLower; i <= m_LoopUpper; i += m_LoopStep)
	result.add("" + i);
    }
    else {
      for (int i = m_LoopLower; i >= m_LoopUpper; i += m_LoopStep)
	result.add("" + i);
    }
    
    return result;
  }
}
