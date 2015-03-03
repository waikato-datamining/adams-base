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
 * StoreReportValueInVariable.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.template;

import adams.core.VariableName;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.flow.control.Tee;
import adams.flow.core.AbstractActor;
import adams.flow.transformer.GetReportValue;
import adams.flow.transformer.SetVariable;

/**
 <!-- globalinfo-start -->
 * Generates a sub-flow (enlosed by a Tee) that sets the value of a variable with the associated value from the report.
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
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The new name for the actor; leave empty to use current.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-field &lt;adams.data.report.Field&gt; (property: field)
 * &nbsp;&nbsp;&nbsp;The report field to retrieve.
 * &nbsp;&nbsp;&nbsp;default: blah[N]
 * </pre>
 *
 * <pre>-variable &lt;adams.core.VariableName&gt; (property: variableName)
 * &nbsp;&nbsp;&nbsp;The variable to store the report value in.
 * &nbsp;&nbsp;&nbsp;default: variable
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class StoreReportValueInVariable
  extends AbstractActorTemplate {

  /** for serialization. */
  private static final long serialVersionUID = 2310015199489870240L;

  /** the report field to extract. */
  protected Field m_Field;

  /** the variable to store the value in. */
  protected VariableName m_VariableName;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Generates a sub-flow (enlosed by a Tee) that sets the value of a "
      + "variable with the associated value from the report.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "field", "field",
	    new Field("blah", DataType.NUMERIC));

    m_OptionManager.add(
	    "variable", "variableName",
	    new VariableName());
  }

  /**
   * Sets the field to retrieve from the report.
   *
   * @param value	the field
   */
  public void setField(Field value) {
    m_Field = value;
    reset();
  }

  /**
   * Returns the field to retrieve from the report.
   *
   * @return		the field
   */
  public Field getField() {
    return m_Field;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String fieldTipText() {
    return "The report field to retrieve.";
  }

  /**
   * Sets the variable to set.
   *
   * @param value	the variable
   */
  public void setVariableName(VariableName value) {
    m_VariableName = value;
    reset();
  }

  /**
   * Returns the variable to set.
   *
   * @return		the variable
   */
  public VariableName getVariableName() {
    return m_VariableName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String variableNameTipText() {
    return "The variable to store the report value in.";
  }

  /**
   * Generates the actor.
   *
   * @return 		the generated actor
   */
  @Override
  protected AbstractActor doGenerate() {
    Tee			result;
    GetReportValue	getValue;
    SetVariable		setVar;

    result = new Tee();
    result.setName("Setting " + m_VariableName);

    getValue = new GetReportValue();
    getValue.setField(m_Field);

    setVar = new SetVariable();
    setVar.setVariableName((VariableName) m_VariableName.getClone());

    result.setActors(new AbstractActor[]{
	getValue,
	setVar
    });

    return result;
  }
}
