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
 * SimpleMenu.java
 * Copyright (C) 2020 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.template;

import adams.core.Utils;
import adams.core.VariableName;
import adams.core.base.BaseString;
import adams.flow.condition.bool.BooleanCondition;
import adams.flow.condition.bool.Expression;
import adams.flow.condition.bool.VariableFlagSet;
import adams.flow.control.Block;
import adams.flow.control.Stop;
import adams.flow.control.Switch;
import adams.flow.control.Trigger;
import adams.flow.control.WhileLoop;
import adams.flow.core.Actor;
import adams.flow.core.MutableActorHandler;
import adams.flow.source.EnterValue;
import adams.flow.source.Start;
import adams.flow.transformer.SetVariable;

import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Generates a sub-flow that displays a dialog with the choice strings as buttons.<br>
 * If custom values are used, then these strings get stored in the variable rather than the choice strings.<br>
 * When enabling looping, ensure you have an exit option in the menu that sets the loop_state variable to 'false' to avoid an endless loop menu.
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
 * &nbsp;&nbsp;&nbsp;default: Please select
 * </pre>
 *
 * <pre>-choice &lt;adams.core.base.BaseString&gt; [-choice ...] (property: choices)
 * &nbsp;&nbsp;&nbsp;The strings that get displayed as buttons.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-use-custom-values &lt;boolean&gt; (property: useCustomValues)
 * &nbsp;&nbsp;&nbsp;If enabled, the associated custom value gets stored in the variable rather
 * &nbsp;&nbsp;&nbsp;than the choice string.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-value &lt;adams.core.base.BaseString&gt; [-value ...] (property: values)
 * &nbsp;&nbsp;&nbsp;The values associated with the choice strings.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-choice-variable &lt;adams.core.VariableName&gt; (property: choiceVariable)
 * &nbsp;&nbsp;&nbsp;The name of the variable to use for storing the choice.
 * &nbsp;&nbsp;&nbsp;default: choice
 * </pre>
 *
 * <pre>-cancelled-variable &lt;adams.core.VariableName&gt; (property: cancelledVariable)
 * &nbsp;&nbsp;&nbsp;The name of the variable to use for storing the cancelled state.
 * &nbsp;&nbsp;&nbsp;default: cancelled
 * </pre>
 *
 * <pre>-loop-menu &lt;boolean&gt; (property: loopMenu)
 * &nbsp;&nbsp;&nbsp;If enabled, the menu gets wrapped in a while loop.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-loop-variable &lt;adams.core.VariableName&gt; (property: loopVariable)
 * &nbsp;&nbsp;&nbsp;The name of the variable for storing the loop state; this variable needs
 * &nbsp;&nbsp;&nbsp;to get initialized with 'true' before the while loop.
 * &nbsp;&nbsp;&nbsp;default: loop_state
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class SimpleMenu
  extends AbstractActorTemplate {

  /** for serialization. */
  private static final long serialVersionUID = 2310015199489870240L;

  /** the message for the user. */
  protected String m_Message;

  /** the choices. */
  protected BaseString[] m_Choices;

  /** whether to use custom values. */
  protected boolean m_UseCustomValues;

  /** the values associated with the choice strings. */
  protected BaseString[] m_Values;

  /** the variable to store the choice in. */
  protected VariableName m_ChoiceVariable;

  /** the variable to store the cancelled state in. */
  protected VariableName m_CancelledVariable;

  /** whether to loop the menu. */
  protected boolean m_LoopMenu;

  /** the variable for storing the loop state. */
  protected VariableName m_LoopVariable;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Generates a sub-flow that displays a dialog with the choice strings as buttons.\n"
      + "If custom values are used, then these strings get stored in the variable rather than the choice strings.\n"
      + "When enabling looping, ensure you have an exit option in the menu that sets the loop_state variable to 'false' "
      + "to avoid an endless loop menu.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "message", "message",
      "Please select");

    m_OptionManager.add(
      "choice", "choices",
      new BaseString[0]);

    m_OptionManager.add(
      "use-custom-values", "useCustomValues",
      false);

    m_OptionManager.add(
      "value", "values",
      new BaseString[0]);

    m_OptionManager.add(
      "choice-variable", "choiceVariable",
      new VariableName("choice"));

    m_OptionManager.add(
      "cancelled-variable", "cancelledVariable",
      new VariableName("cancelled"));

    m_OptionManager.add(
      "loop-menu", "loopMenu",
      false);

    m_OptionManager.add(
      "loop-variable", "loopVariable",
      new VariableName("loop_state"));
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
   * Sets the choices, which get displayed as buttons.
   *
   * @param value	the choices
   */
  public void setChoices(BaseString[] value) {
    m_Choices = value;
    m_Values  = (BaseString[]) Utils.adjustArray(m_Values, m_Choices.length, new BaseString());
    reset();
  }

  /**
   * Returns the choices, which get displayed as buttons.
   *
   * @return 		the choices
   */
  public BaseString[] getChoices() {
    return m_Choices;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String choicesTipText() {
    return "The strings that get displayed as buttons.";
  }

  /**
   * Sets whether to store the associate value in the variable rather than the choice string.
   *
   * @param value	true if to use custom values
   */
  public void setUseCustomValues(boolean value) {
    m_UseCustomValues = value;
    reset();
  }

  /**
   * Returns whether to store the associate value in the variable rather than the choice string.
   *
   * @return 		true if to use custom values
   */
  public boolean getUseCustomValues() {
    return m_UseCustomValues;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String useCustomValuesTipText() {
    return "If enabled, the associated custom value gets stored in the variable rather than the choice string.";
  }

  /**
   * Sets the variable name to use for storing the choice.
   *
   * @param value	the variable name
   */
  public void setChoiceVariable(VariableName value) {
    m_ChoiceVariable = value;
    reset();
  }

  /**
   * Returns the variable name to use for storing the choice.
   *
   * @return 		the variable name
   */
  public VariableName getChoiceVariable() {
    return m_ChoiceVariable;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String choiceVariableTipText() {
    return "The name of the variable to use for storing the choice.";
  }

  /**
   * Sets the associated value strings.
   *
   * @param value	the values
   */
  public void setValues(BaseString[] value) {
    m_Values  = value;
    m_Choices = (BaseString[]) Utils.adjustArray(m_Choices, m_Values.length, new BaseString());
    reset();
  }

  /**
   * Returns the associated value strings.
   *
   * @return 		the values
   */
  public BaseString[] getValues() {
    return m_Values;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String valuesTipText() {
    return "The values associated with the choice strings.";
  }

  /**
   * Sets the variable name to use for storing the cancelled state.
   *
   * @param value	the variable name
   */
  public void setCancelledVariable(VariableName value) {
    m_CancelledVariable = value;
    reset();
  }

  /**
   * Returns the variable name to use for storing the cancelled state.
   *
   * @return 		the variable name
   */
  public VariableName getCancelledVariable() {
    return m_CancelledVariable;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String cancelledVariableTipText() {
    return "The name of the variable to use for storing the cancelled state.";
  }

  /**
   * Sets whether to wrap the menu in a while loop.
   *
   * @param value	true if to loop
   */
  public void setLoopMenu(boolean value) {
    m_LoopMenu = value;
    reset();
  }

  /**
   * Returns whether to wrap the menu in a while loop.
   *
   * @return 		true if to loop
   */
  public boolean getLoopMenu() {
    return m_LoopMenu;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String loopMenuTipText() {
    return "If enabled, the menu gets wrapped in a while loop.";
  }

  /**
   * Sets the variable name to use for storing the cancelled state.
   *
   * @param value	the variable name
   */
  public void setLoopVariable(VariableName value) {
    m_LoopVariable = value;
    reset();
  }

  /**
   * Returns the variable name to use for storing the cancelled state.
   *
   * @return 		the variable name
   */
  public VariableName getLoopVariable() {
    return m_LoopVariable;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String loopVariableTipText() {
    return "The name of the variable for storing the loop state; this variable needs to get initialized with 'true' before the while loop.";
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
   * @return 		the generated actor
   */
  @Override
  protected Actor doGenerate() {
    MutableActorHandler 	result;
    Trigger			prompt;
    Switch			actions;
    EnterValue			enter;
    SetVariable			setvar;
    Switch			swtch;
    int				i;
    Stop			stop;
    List<BooleanCondition> 	conditions;
    Expression			expr;
    Trigger			action;

    if (m_LoopMenu) {
      VariableFlagSet flagSet = new VariableFlagSet();
      flagSet.setVariableName((VariableName) m_LoopVariable.getClone());
      result = new WhileLoop();
      ((WhileLoop) result).setCondition(flagSet);
    }
    else {
      result = new Trigger();
    }
    result.setName(m_Name.isEmpty() ? "menu" : m_Name);

    adams.flow.standalone.SetVariable reset = new adams.flow.standalone.SetVariable();
    reset.setName("reset");
    reset.setVariableName(m_CancelledVariable.getValue());
    reset.setVariableValue("true");
    result.add(reset);

    result.add(new Start());

    // prompt
    prompt = new Trigger();
    prompt.setName("menu prompt");
    result.add(prompt);
    {
      enter = new EnterValue();
      enter.setMessage(new BaseString(m_Message));
      enter.setSelectionValues(m_Choices);
      enter.setUseButtons(true);
      enter.setVerticalButtons(true);
      enter.setStopFlowIfCanceled(false);
      prompt.add(enter);

      setvar = new SetVariable();
      setvar.setVariableName(m_ChoiceVariable.getValue());
      prompt.add(setvar);

      SetVariable set = new SetVariable();
      set.setName("set");
      set.setVariableName(m_CancelledVariable.getValue());
      set.setVariableValue("false");
      prompt.add(set);

      if (m_UseCustomValues) {
	swtch = new Switch();
	swtch.removeAll();
	conditions = new ArrayList<>();
	for (i = 0; i < m_Choices.length; i++) {
	  expr = new Expression();
	  expr.setExpression("\"" + m_ChoiceVariable.paddedValue() + "\" = \"" + m_Choices[i].getValue() + "\"");
	  conditions.add(expr);
	  setvar = new SetVariable();
	  setvar.setName(m_Values[i].getValue());
	  setvar.setVariableName(m_ChoiceVariable.getValue());
	  setvar.setVariableValue(m_Values[i].getValue());
	  swtch.add(setvar);
	}

	stop = new Stop();
	stop.setName("Unhandled choice");
	stop.setStopMessage("Unhandled choice!");
	swtch.add(stop);
	swtch.setConditions(conditions.toArray(new BooleanCondition[0]));

	prompt.add(swtch);
      }
    }

    // block if cancelled
    VariableFlagSet cancelledFlag = new VariableFlagSet();
    cancelledFlag.setVariableName((VariableName) m_CancelledVariable.getClone());
    Block block = new Block();
    block.setName("block if cancelled");
    block.setCondition(cancelledFlag);
    result.add(block);

    // actions
    actions = new Switch();
    actions.setLenient(true);
    actions.removeAll();
    result.add(actions);
    conditions = new ArrayList<>();
    for (i = 0; i < m_Choices.length; i++) {
      // condition
      expr = new Expression();
      if (m_UseCustomValues)
	expr.setExpression("\"" + m_ChoiceVariable.paddedValue() + "\" = \"" + m_Values[i].getValue() + "\"");
      else
	expr.setExpression("\"" + m_ChoiceVariable.paddedValue() + "\" = \"" + m_Choices[i].getValue() + "\"");
      conditions.add(expr);
      // action
      action = new Trigger();
      if (m_UseCustomValues)
	action.setName(m_Values[i].getValue());
      else
	action.setName(m_Choices[i].getValue());
      action.add(new Start());
      actions.add(action);
    }
    actions.setConditions(conditions.toArray(new BooleanCondition[0]));

    return result;
  }
}
