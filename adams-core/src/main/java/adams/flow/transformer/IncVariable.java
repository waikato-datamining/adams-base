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
 * IncVariable.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.VariableName;
import adams.core.VariableUpdater;
import adams.flow.core.Unknown;

/**
 <!-- globalinfo-start -->
 * Increments the value of a variable by either an integer or double increment.<br/>
 * If the variable has not been set yet, it will get set to 0.<br/>
 * If the variable contains a non-numerical value, no increment will be performed.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown<br/>
 * <p/>
 <!-- flow-summary-end -->
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
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: IncVariable
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
 * <pre>-var-name &lt;adams.core.VariableName&gt; (property: variableName)
 * &nbsp;&nbsp;&nbsp;The name of the variable to increment.
 * &nbsp;&nbsp;&nbsp;default: variable
 * </pre>
 *
 * <pre>-inc-type &lt;INTEGER|DOUBLE&gt; (property: incrementType)
 * &nbsp;&nbsp;&nbsp;The type of increment to perform.
 * &nbsp;&nbsp;&nbsp;default: INTEGER
 * </pre>
 *
 * <pre>-inc-int &lt;int&gt; (property: integerIncrement)
 * &nbsp;&nbsp;&nbsp;The increment in case of INTEGER increments.
 * &nbsp;&nbsp;&nbsp;default: 1
 * </pre>
 *
 * <pre>-inc-double &lt;double&gt; (property: doubleIncrement)
 * &nbsp;&nbsp;&nbsp;The increment in case of DOUBLE increments.
 * &nbsp;&nbsp;&nbsp;default: 1.0
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class IncVariable
  extends AbstractTransformer
  implements VariableUpdater {

  /** for serialization. */
  private static final long serialVersionUID = -8466653808821254082L;

  /**
   * The type of increment to perform.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum IncrementType {
    /** integer increment. */
    INTEGER,
    /** floating point increment. */
    DOUBLE
  }

  /** the name of the variable. */
  protected VariableName m_VariableName;

  /** the type of increment to perform. */
  protected IncrementType m_IncrementType;

  /** the integer increment. */
  protected int m_IntegerIncrement;

  /** the double increment. */
  protected double m_DoubleIncrement;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Increments the value of a variable by either an integer or double "
      + "increment.\n"
      + "If the variable has not been set yet, it will get set to 0.\n"
      + "If the variable contains a non-numerical value, no increment will be "
      + "performed.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "var-name", "variableName",
	    new VariableName());

    m_OptionManager.add(
	    "inc-type", "incrementType",
	    IncrementType.INTEGER);

    m_OptionManager.add(
	    "inc-int", "integerIncrement",
	    1);

    m_OptionManager.add(
	    "inc-double", "doubleIncrement",
	    1.0);
  }

  /**
   * Returns whether variables are being updated.
   * 
   * @return		true if variables are updated
   */
  public boolean isUpdatingVariables() {
    return !getSkip();
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    String	variable;

    result = QuickInfoHelper.toString(this, "variableName", m_VariableName.paddedValue());

    variable = QuickInfoHelper.getVariable(this, "incrementType");
    result += QuickInfoHelper.toString(this, "incrementType", m_IncrementType, ", ");
    if (variable == null) {
      result += ", inc: ";
      switch (m_IncrementType) {
	case INTEGER:
	  result += QuickInfoHelper.toString(this, "integerIncrement", m_IntegerIncrement);
	  break;
	case DOUBLE:
	  result += QuickInfoHelper.toString(this, "doubleIncrement", m_DoubleIncrement);
	  break;
	default:
	  throw new IllegalStateException("Unhandled increment type: " + m_IncrementType);
      }
    }

    return result;
  }

  /**
   * Sets the name of the variable to update.
   *
   * @param value	the name
   */
  public void setVariableName(VariableName value) {
    m_VariableName = value;
    reset();
  }

  /**
   * Returns the name of the variable to update.
   *
   * @return		the name
   */
  public VariableName getVariableName() {
    return m_VariableName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String variableNameTipText() {
    return "The name of the variable to increment.";
  }

  /**
   * Sets the type of increment to perform.
   *
   * @param value	the type
   */
  public void setIncrementType(IncrementType value) {
    m_IncrementType = value;
    reset();
  }

  /**
   * Returns the type of increment to perform.
   *
   * @return		the type
   */
  public IncrementType getIncrementType() {
    return m_IncrementType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String incrementTypeTipText() {
    return "The type of increment to perform.";
  }

  /**
   * Sets the increment value for integer increments.
   *
   * @param value	the increment
   */
  public void setIntegerIncrement(int value) {
    m_IntegerIncrement = value;
    reset();
  }

  /**
   * Returns the increment value for integer increments.
   *
   * @return		the increment
   */
  public int getIntegerIncrement() {
    return m_IntegerIncrement;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String integerIncrementTipText() {
    return "The increment in case of " + IncrementType.INTEGER + " increments.";
  }

  /**
   * Sets the increment value for double increments.
   *
   * @param value	the increment
   */
  public void setDoubleIncrement(double value) {
    m_DoubleIncrement = value;
    reset();
  }

  /**
   * Returns the increment value for double increments.
   *
   * @return		the increment
   */
  public double getDoubleIncrement() {
    return m_DoubleIncrement;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String doubleIncrementTipText() {
    return "The increment in case of " + IncrementType.DOUBLE + " increments.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->adams.flow.core.Unknown.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{Unknown.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    Number	value;

    result = null;

    try {
      if (getVariables().has(m_VariableName.getValue()))
	value = new Double(getVariables().get(m_VariableName.getValue()));
      else
	value = new Double(0.0);
    }
    catch (Exception e) {
      value = null;
    }

    if (value != null) {
      switch (m_IncrementType) {
	case INTEGER:
	  value = new Integer(value.intValue() + m_IntegerIncrement);
	  break;
	case DOUBLE:
	  value = new Double(value.doubleValue() + m_DoubleIncrement);
	  break;
	default:
	  throw new IllegalStateException("Unhandled increment type: " + m_IncrementType);
      }
      getVariables().set(m_VariableName.getValue(), "" + value);
      if (isLoggingEnabled())
	getLogger().info("Incremented variable '" + m_VariableName + "': " + value);
    }

    m_OutputToken = m_InputToken;

    return result;
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->adams.flow.core.Unknown.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{Unknown.class};
  }
}
