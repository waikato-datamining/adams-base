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
 * HasSize.java
 * Copyright (C) 2021-2022 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.condition.bool;

import adams.core.QuickInfoHelper;
import adams.flow.core.Actor;
import adams.flow.core.Token;

import java.util.Collection;

/**
 <!-- globalinfo-start -->
 * Checks whether the collection passing through has the required number of elements.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-min-size &lt;int&gt; (property: minSize)
 * &nbsp;&nbsp;&nbsp;The minimum number of elements that the collection has to have, no lower
 * &nbsp;&nbsp;&nbsp;bound if -1.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 *
 * <pre>-max-size &lt;int&gt; (property: maxSize)
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
public class HasSize
    extends AbstractBooleanCondition {

  /** for serialization. */
  private static final long serialVersionUID = 2973832676958171541L;

  /** the minimum number of elements. */
  protected int m_MinSize;

  /** the maximum number of elements. */
  protected int m_MaxSize;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Checks whether the collection passing through has the required number of elements.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"min-size", "minSize",
	1, -1, null);

    m_OptionManager.add(
	"max-size", "maxSize",
	-1, -1, null);
  }

  /**
   * Sets the minimum number of elements the collection has to have.
   *
   * @param value	the number of elements (-1: no lower bound)
   */
  public void setMinSize(int value) {
    if (getOptionManager().isValid("minSize", value)) {
      m_MinSize = value;
      reset();
    }
  }

  /**
   * Returns the minimum number of elements the collection has to have
   *
   * @return		the number of elements (-1: no lower bound)
   */
  public int getMinSize() {
    return m_MinSize;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String minSizeTipText() {
    return "The minimum number of elements that the collection has to have, no lower bound if -1.";
  }

  /**
   * Sets the maximum number of elements the array can have.
   *
   * @param value	the number of elements (-1: no upper bound)
   */
  public void setMaxSize(int value) {
    if (getOptionManager().isValid("maxSize", value)) {
      m_MinSize = value;
      reset();
    }
  }

  /**
   * Returns the maximum number of elements the array can have
   *
   * @return		the number of elements (-1: no upper bound)
   */
  public int getMaxSize() {
    return m_MaxSize;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String maxSizeTipText() {
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

    result = QuickInfoHelper.toString(this, "minSize", (m_MinSize == -1 ? "-any-" : "" + m_MinSize), "min size: ");
    result += QuickInfoHelper.toString(this, "maxSize", (m_MaxSize == -1 ? "-any-" : "" + m_MaxSize), ", max size: ");

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		adams.flow.core.Unknown.class
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Collection.class};
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
    Collection 	coll;

    result = (token.getPayload() instanceof Collection);

    if (result) {
      coll = (Collection) token.getPayload();

      if (m_MinSize > -1)
	result = (coll.size() >= m_MinSize);

      if (m_MaxSize > -1)
	result = (coll.size() <= m_MaxSize);
    }

    return result;
  }
}
