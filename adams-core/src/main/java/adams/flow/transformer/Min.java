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
 * Min.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.data.statistics.StatUtils;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Returns the minimum value from a double/int array or the index of the minimum value.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input/output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.Integer[]<br>
 * &nbsp;&nbsp;&nbsp;java.lang.Double[]<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.Integer<br>
 * &nbsp;&nbsp;&nbsp;java.lang.Double<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 *
 * <pre>-D (property: debug)
 * &nbsp;&nbsp;&nbsp;If set to true, scheme may output additional info to the console.
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: Min
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
 * <pre>-index (property: returnIndex)
 * &nbsp;&nbsp;&nbsp;If set to true, then the index of the minimum is returned instead of the
 * &nbsp;&nbsp;&nbsp;value.
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Min
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 2007764064808349672L;

  /** whether to return the index instead of the value. */
  protected boolean m_ReturnIndex;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Returns the minimum value from a double/int array or the index of the minimum value.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "index", "returnIndex",
	    false);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "returnIndex", (m_ReturnIndex ? "Index" : "Value"));
  }

  /**
   * Sets whether to return the value or the index.
   *
   * @param value	true if to return the index, false to return value
   */
  public void setReturnIndex(boolean value) {
    m_ReturnIndex = value;
    reset();
  }

  /**
   * Returns whether to return the value or the index.
   *
   * @return		true the index is returned, false if the value is returned
   */
  public boolean getReturnIndex() {
    return m_ReturnIndex;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String returnIndexTipText() {
    return "If set to true, then the index of the minimum is returned instead of the value.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->java.lang.Integer[].class, java.lang.Double[].class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{Integer[].class, Double[].class, int[].class, double[].class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->java.lang.Integer.class, java.lang.Double.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{Integer.class, Double.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    Double[]	doublesO;
    double[]	doublesP;
    Integer[]	integersO;
    int[]	integersP;

    result = null;

    try {
      if (m_InputToken.getPayload() instanceof Double[]) {
	doublesO = (Double[]) m_InputToken.getPayload();
	if (m_ReturnIndex)
	  m_OutputToken = new Token(new Integer(StatUtils.minIndex(doublesO)));
	else
	  m_OutputToken = new Token((Double) (StatUtils.min(doublesO)));
      }
      else if (m_InputToken.getPayload() instanceof double[]) {
	doublesP = (double[]) m_InputToken.getPayload();
	if (m_ReturnIndex)
	  m_OutputToken = new Token(new Integer(StatUtils.minIndex(doublesP)));
	else
	  m_OutputToken = new Token((Double) (StatUtils.min(doublesP)));
      }
      else if (m_InputToken.getPayload() instanceof Integer[]) {
	integersO = (Integer[]) m_InputToken.getPayload();
	if (m_ReturnIndex)
	  m_OutputToken = new Token(new Integer(StatUtils.minIndex(integersO)));
	else
	  m_OutputToken = new Token((Integer) (StatUtils.min(integersO)));
      }
      else if (m_InputToken.getPayload() instanceof int[]) {
	integersP = (int[]) m_InputToken.getPayload();
	if (m_ReturnIndex)
	  m_OutputToken = new Token(new Integer(StatUtils.minIndex(integersP)));
	else
	  m_OutputToken = new Token((Integer) (StatUtils.min(integersP)));
      }
      else {
	result = "Unhandled class: " + Utils.classToString(m_InputToken.getPayload().getClass());
      }
    }
    catch (Exception e) {
      m_OutputToken = null;
      result = handleException("Failed to determine minimum:", e);
    }

    return result;
  }
}
