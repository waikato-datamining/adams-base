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
 * WekaCapabilities.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.condition.bool;

import weka.core.Instance;
import weka.core.Instances;
import adams.flow.core.Actor;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Filters weka.core.Instance and weka.core.Instances objects based on defined capabilities. Only objects that match the capabilities will be passed on, all others get discarded.<br/>
 * The matching sense can be inverted as well.
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
 * <pre>-capability &lt;NOMINAL_ATTRIBUTES|BINARY_ATTRIBUTES|UNARY_ATTRIBUTES|EMPTY_NOMINAL_ATTRIBUTES|NUMERIC_ATTRIBUTES|DATE_ATTRIBUTES|STRING_ATTRIBUTES|RELATIONAL_ATTRIBUTES|MISSING_VALUES|NO_CLASS|NOMINAL_CLASS|BINARY_CLASS|UNARY_CLASS|EMPTY_NOMINAL_CLASS|NUMERIC_CLASS|DATE_CLASS|STRING_CLASS|RELATIONAL_CLASS|MISSING_CLASS_VALUES|ONLY_MULTIINSTANCE&gt; [-capability ...] (property: capabilities)
 * &nbsp;&nbsp;&nbsp;The capabilities that the objects must match.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-invert (property: invert)
 * &nbsp;&nbsp;&nbsp;If set to true, then objects that failed the capabilities test will pass 
 * &nbsp;&nbsp;&nbsp;through and all others get discarded.
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaCapabilities
  extends AbstractAttributeCapabilities {

  /** for serialization. */
  private static final long serialVersionUID = 3278345095591806425L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Filters " + Instance.class.getName() + " and " + Instances.class.getName() 
      + " objects based on defined capabilities. Only objects that match the "
      + "capabilities will be passed on, all others get discarded.\n"
      + "The matching sense can be inverted as well.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		Unknown
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Instance.class, Instances.class};
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
    Instances	inst;

    result = false;

    if ((token != null) && (token.getPayload() != null)) {
      // dataset
      if (token.getPayload() instanceof Instance)
	inst = ((Instance) token.getPayload()).dataset();
      else
	inst = (Instances) token.getPayload();
      if (inst == null) {
	getLogger().severe("No dataset present!");
	return result;
      }

      if (m_Invert)
	result = !m_ActualCapabilities.test(inst);
      else
	result = m_ActualCapabilities.test(inst);
    }

    return result;
  }
}
