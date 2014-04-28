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
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Emulates the following for-loop for integer IDs:<br/>
 * - positive step size:<br/>
 *   for (int i = lower; i &lt;= upper; i += step)<br/>
 * - negative step size:<br/>
 *   for (int i = upper; i &gt;= lower; i += step)
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input/output:<br/>
 * - generates:<br/>
 * <pre>   java.lang.Integer</pre>
 * <p/>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre>-D (property: debug)
 *         If set to true, scheme may output additional info to the console.
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 *         The name of the actor.
 *         default: ForLoop
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 *         The annotations to attach to this actor.
 *         default:
 * </pre>
 *
 * <pre>-skip (property: skip)
 *         If set to true, transformation is skipped and the input token is just forwarded
 *          as it is.
 * </pre>
 *
 * <pre>-lower &lt;int&gt; (property: loopLower)
 *         The lower bound of the loop (= the first value).
 *         default: 1
 * </pre>
 *
 * <pre>-upper &lt;int&gt; (property: loopUpper)
 *         The upper bound of the loop.
 *         default: 10
 * </pre>
 *
 * <pre>-step &lt;int&gt; (property: loopStep)
 *         The step size of the loop.
 *         default: 1
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ForLoop
  extends AbstractForLoop {

  /** for serialization. */
  private static final long serialVersionUID = 6216146938771296415L;

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
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->java.lang.Integer.class<!-- flow-generates-end -->
   */
  @Override
  public Class[] generates() {
    return new Class[]{Integer.class};
  }

  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  @Override
  public Token output() {
    Token	result;

    if (isLoggingEnabled())
      getLogger().info("i=" + m_Current);

    result     = new Token(new Integer(m_Current));
    m_Current += m_LoopStep;

    return result;
  }
}
