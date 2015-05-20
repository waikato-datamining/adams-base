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
 * PrimitiveArrayToObjectArray.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.lang.reflect.Array;

import adams.core.Utils;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Turns a primitive array into an object one. Depending on the input type, either an Integer or Double array.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;byte[]<br>
 * &nbsp;&nbsp;&nbsp;short[]<br>
 * &nbsp;&nbsp;&nbsp;int[]<br>
 * &nbsp;&nbsp;&nbsp;long[]<br>
 * &nbsp;&nbsp;&nbsp;float[]<br>
 * &nbsp;&nbsp;&nbsp;double[]<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.Integer[]<br>
 * &nbsp;&nbsp;&nbsp;java.lang.Double[]<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 * 
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to 
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: PrimitiveArrayToObjectArray
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * </pre>
 * 
 * <pre>-stop-flow-on-error (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PrimitiveArrayToObjectArray
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 3753370837923928436L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Turns a primitive array into an object one. Depending on the input type, either an Integer or Double array.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->byte[].class, short[].class, int[].class, long[].class, float[].class, double[].class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{byte[].class, short[].class, int[].class, long[].class, float[].class, double[].class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->java.lang.Integer[].class, java.lang.Double[].class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{Integer[].class, Double[].class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    Object	arrayIn;
    Object	arrayOut;
    int		i;
    int		len;

    result = null;

    try {
      arrayIn  = m_InputToken.getPayload();
      len      = Array.getLength(arrayIn);
      arrayOut = null;
      if (arrayIn instanceof byte[]) {
	arrayOut = new Integer[len];
	for (i = 0; i < len; i++)
	  Array.set(arrayOut, i, new Integer(Array.getByte(arrayIn, i)));
      }
      else if (arrayIn instanceof short[]) {
	arrayOut = new Integer[len];
	for (i = 0; i < len; i++)
	  Array.set(arrayOut, i, new Integer(Array.getShort(arrayIn, i)));
      }
      else if (arrayIn instanceof int[]) {
	arrayOut = new Integer[len];
	for (i = 0; i < len; i++)
	  Array.set(arrayOut, i, new Integer(Array.getInt(arrayIn, i)));
      }
      // TODO shift from int to long system-wide?
      else if (arrayIn instanceof long[]) {
	arrayOut = new Integer[len];
	for (i = 0; i < len; i++)
	  Array.set(arrayOut, i, new Integer((int) Array.getLong(arrayIn, i)));
      }
      else if (arrayIn instanceof float[]) {
	arrayOut = new Double[len];
	for (i = 0; i < len; i++)
	  Array.setDouble(arrayOut, i, new Double(Array.getFloat(arrayIn, i)));
      }
      else if (arrayIn instanceof double[]) {
	arrayOut = new Double[len];
	for (i = 0; i < len; i++)
	  Array.setDouble(arrayOut, i, new Double(Array.getDouble(arrayIn, i)));
      }
      else {
	result = "Unhandled class: " + Utils.classToString(m_InputToken.getPayload().getClass());
      }
      if (arrayOut != null)
	m_OutputToken = new Token(arrayOut);
    }
    catch (Exception e) {
      m_OutputToken = null;
      result = handleException("Failed to convert array: ", e);
    }

    return result;
  }
}
