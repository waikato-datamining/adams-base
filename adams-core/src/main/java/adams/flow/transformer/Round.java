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
 * Round.java
 * Copyright (C) 2009-2025 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.data.RoundingType;
import adams.data.RoundingUtils;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Performs the specified rounding operation on double tokens.<br>
 * If 'numDecimals' is zero, it will generate integers otherwise doubles.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.Double<br>
 * &nbsp;&nbsp;&nbsp;java.lang.Double[]<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.Integer<br>
 * &nbsp;&nbsp;&nbsp;java.lang.Integer[]<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: Round
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-action &lt;ROUND|CEILING|FLOOR|RINT&gt; (property: action)
 * &nbsp;&nbsp;&nbsp;The action to perform on the doubles passing through; ROUND: the closest
 * &nbsp;&nbsp;&nbsp;integer to the argument, with ties rounding to positive infinity; CEILING:
 * &nbsp;&nbsp;&nbsp; the smallest (closest to negative infinity) double value that is greater
 * &nbsp;&nbsp;&nbsp;than or equal to the argument and is equal to a mathematical integer; FLOOR:
 * &nbsp;&nbsp;&nbsp; the largest (closest to positive infinity) double value that is less than
 * &nbsp;&nbsp;&nbsp;or equal to the argument and is equal to a mathematical integer; RINT: the
 * &nbsp;&nbsp;&nbsp;double value that is closest in value to the argument and is equal to a
 * &nbsp;&nbsp;&nbsp;mathematical integer
 * &nbsp;&nbsp;&nbsp;default: ROUND
 * </pre>
 *
 * <pre>-num-decimals &lt;int&gt; (property: numDecimals)
 * &nbsp;&nbsp;&nbsp;The number of decimals after the decimal point to use.
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class Round
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 5849681965859916196L;

  /** the action to perform. */
  protected RoundingType m_Action;

  /** the number of decimals. */
  protected int m_NumDecimals;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Performs the specified rounding operation on double tokens.\n"
	     + "If 'numDecimals' is zero, it will generate integers otherwise doubles.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "action", "action",
      RoundingType.ROUND);

    m_OptionManager.add(
      "num-decimals", "numDecimals",
      0, 0, null);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "action", m_Action);
    result += QuickInfoHelper.toString(this, "numDecimals", m_NumDecimals, ", decimals: ");

    return result;
  }

  /**
   * Sets the action to perform on the doubles.
   *
   * @param value	the action
   */
  public void setAction(RoundingType value) {
    m_Action = value;
    reset();
  }

  /**
   * Returns the action to perform on the doubles.
   *
   * @return		the action
   */
  public RoundingType getAction() {
    return m_Action;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String actionTipText() {
    return "The action to perform on the doubles passing through; " + RoundingUtils.roundingTypeTipText();
  }

  /**
   * Sets the number of decimals after the decimal point to use.
   *
   * @param value	the number of decimals
   */
  public void setNumDecimals(int value) {
    if (getOptionManager().isValid("numDecimals", value)) {
      m_NumDecimals = value;
      reset();
    }
  }

  /**
   * Returns the number of decimals after the decimal point to use.
   *
   * @return		the number of decimals
   */
  public int getNumDecimals() {
    return m_NumDecimals;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numDecimalsTipText() {
    return "The number of decimals after the decimal point to use.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->java.lang.Double.class, java.lang.Double[].class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{Double.class, Double[].class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->java.lang.Integer.class, java.lang.Integer[].class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    if (m_NumDecimals == 0)
      return new Class[]{Integer.class, Integer[].class};
    else
      return new Class[]{Double.class, Double[].class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    Double[]	doubles;
    Number[] 	rounded;
    int		i;
    boolean	isArray;

    result = null;

    doubles = null;
    try {
      isArray = (m_InputToken.getPayload() instanceof Double[]);
      if (!isArray)
	doubles = new Double[]{(Double) m_InputToken.getPayload()};
      else
	doubles = (Double[]) m_InputToken.getPayload();

      if (m_NumDecimals == 0) {
	rounded = new Integer[doubles.length];
	for (i = 0; i < doubles.length; i++)
	  rounded[i] = (int) RoundingUtils.apply(m_Action, doubles[i], 0);
      }
      else {
	rounded = new Double[doubles.length];
	for (i = 0; i < doubles.length; i++)
	  rounded[i] = RoundingUtils.apply(m_Action, doubles[i], m_NumDecimals);
      }

      if (!isArray)
	m_OutputToken = new Token(rounded[0]);
      else
	m_OutputToken = new Token(rounded);
    }
    catch (Exception e) {
      m_OutputToken = null;
      result = handleException("Failed to round " + Utils.arrayToString(doubles), e);
    }

    return result;
  }
}
