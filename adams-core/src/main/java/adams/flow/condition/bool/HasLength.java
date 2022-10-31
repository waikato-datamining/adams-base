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
 * HasLength.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.condition.bool;

import adams.core.QuickInfoHelper;
import adams.flow.core.Actor;
import adams.flow.core.Token;
import adams.flow.core.Unknown;

import java.lang.reflect.Array;

/**
 <!-- globalinfo-start -->
 * Checks whether the array passing through has the required number of elements.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-min-length &lt;int&gt; (property: minLength)
 * &nbsp;&nbsp;&nbsp;The minimum number of elements that the array needs to have, no lower bound
 * &nbsp;&nbsp;&nbsp;if -1.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 *
 * <pre>-max-length &lt;int&gt; (property: maxLength)
 * &nbsp;&nbsp;&nbsp;The maximum number of elements that the array can have, no upper bound if
 * &nbsp;&nbsp;&nbsp;-1.
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class HasLength
    extends AbstractBooleanCondition {

  /** for serialization. */
  private static final long serialVersionUID = 2973832676958171541L;

  /** the minimum number of elements. */
  protected int m_MinLength;

  /** the maximum number of elements. */
  protected int m_MaxLength;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Checks whether the array passing through has the required number of elements.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"min-length", "minLength",
	1, -1, null);

    m_OptionManager.add(
	"max-length", "maxLength",
	-1, -1, null);
  }

  /**
   * Sets the minimum number of elements the array has to have.
   *
   * @param value	the number of elements (-1: no lower bound)
   */
  public void setMinLength(int value) {
    if (getOptionManager().isValid("minLength", value)) {
      m_MinLength = value;
      reset();
    }
  }

  /**
   * Returns the minimum number of elements the array has to have
   *
   * @return		the number of elements (-1: no lower bound)
   */
  public int getMinLength() {
    return m_MinLength;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String minLengthTipText() {
    return "The minimum number of elements that the array needs to have, no lower bound if -1.";
  }

  /**
   * Sets the maximum number of elements the array can have.
   *
   * @param value	the number of elements (-1: no upper bound)
   */
  public void setMaxLength(int value) {
    if (getOptionManager().isValid("maxLength", value)) {
      m_MinLength = value;
      reset();
    }
  }

  /**
   * Returns the maximum number of elements the array can have
   *
   * @return		the number of elements (-1: no upper bound)
   */
  public int getMaxLength() {
    return m_MaxLength;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String maxLengthTipText() {
    return "The maximum number of elements that the array can have, no upper bound if -1.";
  }

  /**
   * Returns the quick info string to be displayed in the flow editor.
   *
   * @return		the info or null if no info to be displayed
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "minLength", (m_MinLength == -1 ? "-any-" : "" + m_MinLength), "min length: ");
    result += QuickInfoHelper.toString(this, "maxLength", (m_MaxLength == -1 ? "-any-" : "" + m_MaxLength), ", max length: ");

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the accepted classes
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
    boolean	result;
    Object 	array;

    array  = token.getPayload();
    result = (array != null) && (array.getClass().isArray());

    if (result && (m_MinLength > -1))
      result = (Array.getLength(array) >= m_MinLength);

    if (result && (m_MaxLength > -1))
      result = (Array.getLength(array) <= m_MaxLength);

    return result;
  }
}
