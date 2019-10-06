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
 * PromptUser.java
 * Copyright (C) 2017-2019 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.template;

import adams.core.Utils;
import adams.core.VariableName;
import adams.core.Variables;
import adams.core.base.BaseText;
import adams.flow.control.Trigger;
import adams.flow.core.Actor;
import adams.flow.core.ActorUtils;
import adams.flow.core.VariableValueType;
import adams.flow.source.EnterManyValues;
import adams.flow.source.EnterManyValues.OutputType;
import adams.flow.source.valuedefinition.AbstractValueDefinition;
import adams.flow.standalone.SetVariable;
import adams.flow.transformer.MapToVariables;

/**
 <!-- globalinfo-start -->
 * Generates a sub-flow that prompts the user with the specified parameters and stores the values in variables.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The new name for the actor; leave empty to use current.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-message &lt;java.lang.String&gt; (property: message)
 * &nbsp;&nbsp;&nbsp;The message to prompt the user with.
 * &nbsp;&nbsp;&nbsp;default: Please enter values
 * </pre>
 *
 * <pre>-value &lt;adams.flow.source.valuedefinition.AbstractValueDefinition&gt; [-value ...] (property: values)
 * &nbsp;&nbsp;&nbsp;The value definitions that define the dialog prompting the user to enter
 * &nbsp;&nbsp;&nbsp;the values.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-restore-enabled &lt;boolean&gt; (property: restoreEnabled)
 * &nbsp;&nbsp;&nbsp;If enabled, the adams.flow.source.EnterManyValues actor will get set up
 * &nbsp;&nbsp;&nbsp;to automatically restore its values.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-restore-var &lt;adams.core.VariableName&gt; (property: restoreVar)
 * &nbsp;&nbsp;&nbsp;The name of the variable to use for storing the restore file.
 * &nbsp;&nbsp;&nbsp;default: restore
 * </pre>
 *
 * <pre>-restore-file &lt;java.lang.String&gt; (property: restoreFile)
 * &nbsp;&nbsp;&nbsp;The file to store the settings in.
 * &nbsp;&nbsp;&nbsp;default: &#64;{flow_filename_long}.props
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class PromptUser
  extends AbstractActorTemplate {

  /** for serialization. */
  private static final long serialVersionUID = 2310015199489870240L;

  /** the message for the user. */
  protected String m_Message;

  /** the value definitions. */
  protected AbstractValueDefinition[] m_Values;

  /** whether to enable automatic restore of values. */
  protected boolean m_RestoreEnabled;

  /** the name of the restore variable. */
  protected VariableName m_RestoreVar;

  /** the name of the restore file. */
  protected String m_RestoreFile;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Generates a sub-flow that prompts the user with the specified "
        + "parameters and stores the values in variables.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "message", "message",
      "Please enter values");

    m_OptionManager.add(
      "value", "values",
      new AbstractValueDefinition[0]);

    m_OptionManager.add(
      "restore-enabled", "restoreEnabled",
      false);

    m_OptionManager.add(
      "restore-var", "restoreVar",
      new VariableName("restore"));

    m_OptionManager.add(
      "restore-file", "restoreFile",
      Variables.padName(ActorUtils.FLOW_FILENAME_LONG) + ".props");
  }

  /**
   * Sets the message to prompt the user with.
   *
   * @param value	the message
   */
  public void setMessage(String value) {
    m_Message = value;
    reset();
  }

  /**
   * Returns the message the user is prompted with.
   *
   * @return 		the message
   */
  public String getMessage() {
    return m_Message;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String messageTipText() {
    return "The message to prompt the user with.";
  }

  /**
   * Sets the value definitions.
   *
   * @param value	the definitions
   */
  public void setValues(AbstractValueDefinition[] value) {
    m_Values = value;
    reset();
  }

  /**
   * Returns the value definitions.
   *
   * @return 		the definitions
   */
  public AbstractValueDefinition[] getValues() {
    return m_Values;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String valuesTipText() {
    return "The value definitions that define the dialog prompting the user to enter the values.";
  }

  /**
   * Sets whether to automatically restore the EnterManyValues settings.
   *
   * @param value	true if to restore
   */
  public void setRestoreEnabled(boolean value) {
    m_RestoreEnabled = value;
    reset();
  }

  /**
   * Returns whether to automatically restore the EnterManyValues settings.
   *
   * @return 		true if to restore
   */
  public boolean getRestoreEnabled() {
    return m_RestoreEnabled;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String restoreEnabledTipText() {
    return "If enabled, the " + Utils.classToString(EnterManyValues.class) + " actor will get set up to automatically restore its values.";
  }

  /**
   * Sets the variable name to use for the restore file.
   *
   * @param value	the variable name
   */
  public void setRestoreVar(VariableName value) {
    m_RestoreVar = value;
    reset();
  }

  /**
   * Returns the variable name to use for the restore file.
   *
   * @return 		the variable name
   */
  public VariableName getRestoreVar() {
    return m_RestoreVar;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String restoreVarTipText() {
    return "The name of the variable to use for storing the restore file.";
  }

  /**
   * Sets the file to store the settings in.
   *
   * @param value	the settings file
   */
  public void setRestoreFile(String value) {
    m_RestoreFile = value;
    reset();
  }

  /**
   * Returns the file to store the settings in.
   *
   * @return 		the settings file
   */
  public String getRestoreFile() {
    return m_RestoreFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String restoreFileTipText() {
    return "The file to store the settings in.";
  }

  /**
   * Whether the flow generated is an interactive one.
   *
   * @return		true if interactive
   */
  @Override
  public boolean isInteractive() {
    return true;
  }

  /**
   * Generates the actor.
   *
   * @return 		the generated acto
   */
  @Override
  protected Actor doGenerate() {
    Trigger		result;
    EnterManyValues	enter;
    MapToVariables	map2var;
    SetVariable		setVar;

    result = new Trigger();
    result.setName("prompt user");

    if (m_RestoreEnabled) {
      setVar = new SetVariable();
      setVar.setName("restore file");
      setVar.setVariableValue(new BaseText(m_RestoreFile));
      setVar.setValueType(VariableValueType.FILE_FORWARD_SLASHES);
      setVar.setExpandValue(true);
      setVar.setVariableName(new VariableName(m_RestoreVar.getValue()));
      result.add(setVar);
    }

    enter = new EnterManyValues();
    enter.setMessage(m_Message);
    enter.setValues(m_Values);
    enter.setStopFlowIfCanceled(true);
    enter.setOutputType(OutputType.MAP);
    if (m_RestoreEnabled) {
      enter.setRestorationEnabled(true);
      enter.getOptionManager().setVariableForProperty("restorationFile", m_RestoreVar.getValue());
    }
    result.add(enter);

    map2var = new MapToVariables();
    result.add(map2var);

    return result;
  }
}
