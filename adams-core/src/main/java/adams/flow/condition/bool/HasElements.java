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
 * HasElements.java
 * Copyright (C) 2021-2022 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.condition.bool;

import adams.core.QuickInfoHelper;
import adams.core.annotation.DeprecatedClass;
import adams.flow.core.Actor;
import adams.flow.core.Token;
import adams.flow.core.Unknown;

import java.lang.reflect.Array;

/**
 <!-- globalinfo-start -->
 * Checks whether the array passing through has a at least the specified number of elements.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-num-elements &lt;int&gt; (property: numElements)
 * &nbsp;&nbsp;&nbsp;The minimum number of elements that the array needs to have.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
@DeprecatedClass(
    useInstead = HasLength.class
)
public class HasElements
  extends AbstractBooleanCondition {

  /** for serialization. */
  private static final long serialVersionUID = 2973832676958171541L;
  
  /** the number of elements to have at least. */
  protected int m_MinElements;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Checks whether the array passing through has a at least the specified number of elements.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "min-elements", "minElements",
      1, 0, null);
  }

  /**
   * Sets the minimum number of elements the array has to have.
   *
   * @param value	the number of elements (0-inf)
   */
  public void setMinElements(int value) {
    if (value >= 0) {
      m_MinElements = value;
      reset();
    }
    else {
      getLogger().warning("Number of elements must be at least 0, provided: " + value);
    }
  }

  /**
   * Returns the minimum number of elements the array has to have
   *
   * @return		the number of elements (0-inf)
   */
  public int getMinElements() {
    return m_MinElements;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String minElementsTipText() {
    return "The minimum number of elements that the array needs to have.";
  }

  /**
   * Returns the quick info string to be displayed in the flow editor.
   *
   * @return		the info or null if no info to be displayed
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "minElements", m_MinElements, "min elements: ");
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
    Object  	array;
    
    array = token.getPayload();
    return (array.getClass().isArray()) && (Array.getLength(array) >= m_MinElements);
  }
}
