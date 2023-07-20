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
 * ArrayToVariables.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.VariableName;
import adams.core.VariableUser;
import adams.core.Variables;
import adams.core.base.BaseObject;
import adams.data.conversion.Conversion;
import adams.data.conversion.TextRenderer;
import adams.flow.core.Unknown;

import java.lang.reflect.Array;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ArrayToVariables
  extends AbstractTransformer
  implements VariableUser {

  /** for serialization. */
  private static final long serialVersionUID = 9062714175599800719L;

  /** the names of the variables to map to. */
  protected VariableName[] m_VariableNames;

  /** the type of conversion. */
  protected Conversion m_Conversion;

  /**
   * Default constructor.
   */
  public ArrayToVariables() {
    super();
  }

  /**
   * Initializes with the specified names.
   *
   * @param variableNames   the names to use
   */
  public ArrayToVariables(VariableName[] variableNames) {
    this();
    setVariableNames(variableNames);
  }

  /**
   * Initializes with the specified names.
   *
   * @param variableNames   the names to use
   */
  public ArrayToVariables(String[] variableNames) {
    this();
    setVariableNames(variableNames);
  }

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Maps the elements of an array of any type to the provided variables.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "var-name", "variableNames",
      new VariableName[0]);

    m_OptionManager.add(
      "conversion", "conversion",
      new TextRenderer());
  }

  /**
   * Adds the variable.
   *
   * @param value	the names
   */
  public void addVariableName(VariableName value) {
    m_VariableNames = (VariableName[]) Utils.adjustArray(m_VariableNames, m_VariableNames.length + 1, value);
    reset();
  }

  /**
   * Sets the names of the variables.
   *
   * @param value	the names
   */
  public void setVariableNames(String[] value) {
    setVariableNames((VariableName[]) BaseObject.toObjectArray(value, VariableName.class));
  }

  /**
   * Sets the names of the variables.
   *
   * @param value	the names
   */
  public void setVariableNames(VariableName[] value) {
    m_VariableNames = value;
    reset();
  }

  /**
   * Returns the names of the variables.
   *
   * @return		the names
   */
  public VariableName[] getVariableNames() {
    return m_VariableNames;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String variableNamesTipText() {
    return "The names of the variables to retrieve as array.";
  }

  /**
   * Sets the type of conversion to perform.
   *
   * @param value	the type of conversion
   */
  public void setConversion(Conversion value) {
    m_Conversion = value;
    m_Conversion.setOwner(this);
    reset();
  }

  /**
   * Returns the type of conversion to perform.
   *
   * @return		the type of conversion
   */
  public Conversion getConversion() {
    return m_Conversion;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String conversionTipText() {
    return "The type of conversion to perform.";
  }

  /**
   * Returns whether variables are being used.
   *
   * @return		true if variables are used
   */
  public boolean isUsingVariables() {
    return !getSkip() && (m_VariableNames.length > 0);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "variableNames", Utils.flatten(m_VariableNames, ", "), "Names: ");
    if (result == null)
      result = "-no names specified-";
    result += QuickInfoHelper.toString(this, "conversion", m_Conversion, ", conversion: ");

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->[Ladams.flow.core.Unknown;.class<!-- flow-accepts-end -->
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Unknown[].class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->adams.flow.core.Unknown.class<!-- flow-generates-end -->
   */
  @Override
  public Class[] generates() {
    return new Class[]{Unknown.class};
  }

  /**
   * Executes the flow item.
   *
   * @return null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    Object	array;
    int		i;
    String 	value;
    String	msg;
    Variables	vars;

    result = null;
    array  = m_InputToken.getPayload();
    if (!array.getClass().isArray())
      result = "Not an array: " + array;
    else if (Array.getLength(array) != m_VariableNames.length)
      result = "Mismatching array length and number of variables: " + Array.getLength(array) + " != " + m_VariableNames.length;

    if (result == null) {
      vars = getVariables();
      for (i = 0; i < m_VariableNames.length; i++) {
        m_Conversion.setInput(Array.get(array, i));
        msg = m_Conversion.convert();
        if (msg != null) {
	  result = "Failed to convert element #" + (i + 1) + ": " + Array.get(array, i);
	  break;
	}
        value = "" + m_Conversion.getOutput();
        vars.set(m_VariableNames[i].getValue(), value);
      }
    }

    if (result == null)
      m_OutputToken = m_InputToken;

    return result;
  }
}
