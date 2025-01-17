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
 * AbstractBreakpoint.java
 * Copyright (C) 2013-2025 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.execution.debug;

import adams.core.Variables;
import adams.core.base.BaseString;
import adams.core.option.AbstractOptionHandler;
import adams.flow.core.Actor;
import adams.flow.core.Token;
import adams.gui.tools.ExpressionWatchPanel.ExpressionType;

/**
 * Ancestor for breakpoints for execution listeners.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractBreakpoint
  extends AbstractOptionHandler {

  /** for serialization. */
  private static final long serialVersionUID = 4654096855875796107L;

  /** whether the breakpoint is disabled. */
  protected boolean m_Disabled;

  /** break on preInput. */
  protected boolean m_OnPreInput;

  /** break on postInput. */
  protected boolean m_OnPostInput;

  /** break on preExecute. */
  protected boolean m_OnPreExecute;

  /** break on postExecute. */
  protected boolean m_OnPostExecute;

  /** break on preOutput. */
  protected boolean m_OnPreOutput;

  /** break on postOutput. */
  protected boolean m_OnPostOutput;

  /** the views to display automatically. */
  protected View[] m_Views;

  /** the watch expressions. */
  protected BaseString[] m_Watches;

  /** the watch expression types. */
  protected ExpressionType[] m_WatchTypes;

  /** whether this is a one-off. */
  protected boolean m_OneOff;

  /** whether to bring the window to the front. */
  protected boolean m_BringToFront;

  /** the trigger counter. */
  protected int m_TriggerCount;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "disabled", "disabled",
      false);

    m_OptionManager.add(
      "on-pre-input", "onPreInput",
      false);

    m_OptionManager.add(
      "on-post-input", "onPostInput",
      false);

    m_OptionManager.add(
      "on-pre-execute", "onPreExecute",
      false);

    m_OptionManager.add(
      "on-post-execute", "onPostExecute",
      false);

    m_OptionManager.add(
      "on-pre-output", "onPreOutput",
      false);

    m_OptionManager.add(
      "on-post-output", "onPostOutput",
      false);

    m_OptionManager.add(
      "watch", "watches",
      new BaseString[0]);

    m_OptionManager.add(
      "watch-type", "watchTypes",
      new ExpressionType[0]);

    m_OptionManager.add(
      "view", "views",
      new View[0]);

    m_OptionManager.add(
      "one-off", "oneOff",
      false);

    m_OptionManager.add(
      "bring-to-front", "bringToFront",
      false);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_TriggerCount = 0;
  }

  /**
   * Sets whether to disable this breakpoint.
   *
   * @param value	true if to disable
   */
  public void setDisabled(boolean value) {
    m_Disabled = value;
    reset();
  }

  /**
   * Returns whether this breakpoint is disabled.
   *
   * @return		true if disabled
   */
  public boolean getDisabled() {
    return m_Disabled;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String disabledTipText() {
    return "If set to true, the breakpoint is completely disabled.";
  }

  /**
   * Sets whether to evaluate the breakpoing on pre-input (of token).
   *
   * @param value	true if evaluated
   */
  public void setOnPreInput(boolean value) {
    m_OnPreInput = value;
    reset();
  }

  /**
   * Returns whether the breakpoint gets evaluated on pre-input (of token).
   *
   * @return		true if evaluated
   */
  public boolean getOnPreInput() {
    return m_OnPreInput;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String onPreInputTipText() {
    return "If set to true, the breakpoint gets evaluated at pre-input (of token) time; token available.";
  }

  /**
   * Sets whether to evaluate the breakpoing on post-input (of token).
   *
   * @param value	true if evaluated
   */
  public void setOnPostInput(boolean value) {
    m_OnPostInput = value;
    reset();
  }

  /**
   * Returns whether the breakpoint gets evaluated on post-input (of token).
   *
   * @return		true if evaluated
   */
  public boolean getOnPostInput() {
    return m_OnPostInput;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String onPostInputTipText() {
    return "If set to true, the breakpoint gets evaluated at post-input (of token) time.";
  }

  /**
   * Sets whether to evaluate the breakpoing on pre-execute.
   *
   * @param value	true if evaluated
   */
  public void setOnPreExecute(boolean value) {
    m_OnPreExecute = value;
    reset();
  }

  /**
   * Returns whether the breakpoint gets evaluated on pre-execute.
   *
   * @return		true if evaluated
   */
  public boolean getOnPreExecute() {
    return m_OnPreExecute;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String onPreExecuteTipText() {
    return "If set to true, the breakpoint gets evaluated at pre-execute time.";
  }

  /**
   * Sets whether to evaluate the breakpoing on post-execute.
   *
   * @param value	true if evaluated
   */
  public void setOnPostExecute(boolean value) {
    m_OnPostExecute = value;
    reset();
  }

  /**
   * Returns whether the breakpoint gets evaluated on post-execute.
   *
   * @return		true if evaluated
   */
  public boolean getOnPostExecute() {
    return m_OnPostExecute;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String onPostExecuteTipText() {
    return "If set to true, the breakpoint gets evaluated at post-execute time.";
  }

  /**
   * Sets whether to evaluate the breakpoing on pre-output (of token).
   *
   * @param value	true if evaluated
   */
  public void setOnPreOutput(boolean value) {
    m_OnPreOutput = value;
    reset();
  }

  /**
   * Returns whether the breakpoint gets evaluated on pre-output (of token).
   *
   * @return		true if evaluated
   */
  public boolean getOnPreOutput() {
    return m_OnPreOutput;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String onPreOutputTipText() {
    return "If set to true, the breakpoint gets evaluated at pre-output (of token) time; token available.";
  }

  /**
   * Sets whether to evaluate the breakpoing on post-output (of token).
   *
   * @param value	true if evaluated
   */
  public void setOnPostOutput(boolean value) {
    m_OnPostOutput = value;
    reset();
  }

  /**
   * Returns whether the breakpoint gets evaluated on post-output (of token).
   *
   * @return		true if evaluated
   */
  public boolean getOnPostOutput() {
    return m_OnPostOutput;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String onPostOutputTipText() {
    return "If set to true, the breakpoint gets evaluated at post-output (of token) time.";
  }

  /**
   * Sets the watch expressions for the watch dialog.
   *
   * @param value	the expressions
   */
  public void setWatches(BaseString[] value) {
    int		i;

    for (i = 0; i < value.length; i++) {
      if (Variables.isPlaceholder(value[i].getValue()))
	value[i] = new BaseString("(" + value[i].getValue() + ")");
    }

    m_Watches = value;
    reset();
  }

  /**
   * Returns the watch expressions for the watch dialog.
   *
   * @return		the expressions
   */
  public BaseString[] getWatches() {
    return m_Watches;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String watchesTipText() {
    return
      "The expression to display initially in the watch dialog; the type of "
	+ "the watch needs to be specified as well.";
  }

  /**
   * Sets the types of the watch expressions.
   *
   * @param value	the types
   */
  public void setWatchTypes(ExpressionType[] value) {
    m_WatchTypes = value;
    reset();
  }

  /**
   * Returns the types of the watch expressions.
   *
   * @return		the types
   */
  public ExpressionType[] getWatchTypes() {
    return m_WatchTypes;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String watchTypesTipText() {
    return
      "The types of the watch expressions; determines how the expressions "
	+ "get evaluated and displayed.";
  }

  /**
   * Sets the views to display automatically.
   *
   * @param value	the views
   */
  public void setViews(View[] value) {
    m_Views = value;
    reset();
  }

  /**
   * Returns the views to display automatically.
   *
   * @return		the views
   */
  public View[] getViews() {
    return m_Views;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String viewsTipText() {
    return "The views to display automatically when the breakpoint is reached.";
  }

  /**
   * Sets whether the breakpoint is just a one-off one (gets removed once reached).
   *
   * @param value	true if a one-off breakpoint
   */
  public void setOneOff(boolean value) {
    m_OneOff = value;
    reset();
  }

  /**
   * Returns whether the breakpoint is just a one-off one (gets removed once reached).
   *
   * @return		true if a one-off breakpoint
   */
  public boolean getOneOff() {
    return m_OneOff;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String oneOffTipText() {
    return "If set to true, the breakpoint is a one-off and gets removed once reached.";
  }

  /**
   * Sets whether to bring the flow editor window to the front once the breakpoint is reached.
   *
   * @param value	true if to bring to front
   */
  public void setBringToFront(boolean value) {
    m_BringToFront = value;
    reset();
  }

  /**
   * Returns whether to bring the flow editor window to the front once the breakpoint is reached.
   *
   * @return		true if to bring to front
   */
  public boolean getBringToFront() {
    return m_BringToFront;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String bringToFrontTipText() {
    return "If set to true, it is attempted to bring the flow editor window to the front once the breakpoint is reached. NB Does not work on all platforms.";
  }

  /**
   * Evaluates the breakpoint at pre-input.
   *
   * @param actor	the current actor
   * @param token	the token available for input
   * @return		true if breakpoint triggers
   */
  protected abstract boolean evaluatePreInput(Actor actor, Token token);

  /**
   * Evaluates the breakpoint at post-input.
   *
   * @param actor	the current actor
   * @return		true if breakpoint triggers
   */
  protected abstract boolean evaluatePostInput(Actor actor);

  /**
   * Evaluates the breakpoint at pre-execute.
   *
   * @param actor	the current actor
   * @return		true if breakpoint triggers
   */
  protected abstract boolean evaluatePreExecute(Actor actor);

  /**
   * Evaluates the breakpoint at post-execute.
   *
   * @param actor	the current actor
   * @return		true if breakpoint triggers
   */
  protected abstract boolean evaluatePostExecute(Actor actor);

  /**
   * Evaluates the breakpoint at pre-output.
   *
   * @param actor	the current actor
   * @return		true if breakpoint triggers
   */
  protected abstract boolean evaluatePreOutput(Actor actor);

  /**
   * Evaluates the breakpoint at post-output.
   *
   * @param actor	the current actor
   * @param token	the token available for output
   * @return		true if breakpoint triggers
   */
  protected abstract boolean evaluatePostOutput(Actor actor, Token token);

  /**
   * Checks whether the breakpoint gets triggered in pre-input.
   *
   * @param actor	the current actor
   * @param token	the token available for input
   * @return		true if triggered
   */
  public boolean triggersPreInput(Actor actor, Token token) {
    return m_OnPreInput && evaluatePreInput(actor, token);
  }

  /**
   * Checks whether the breakpoint gets triggered in post-input.
   *
   * @param actor	the current actor
   * @return		true if triggered
   */
  public boolean triggersPostInput(Actor actor) {
    return m_OnPostInput && evaluatePostInput(actor);
  }

  /**
   * Checks whether the breakpoint gets triggered in pre-execute.
   *
   * @param actor	the current actor
   * @return		true if triggered
   */
  public boolean triggersPreExecute(Actor actor) {
    return m_OnPreExecute && evaluatePreExecute(actor);
  }

  /**
   * Checks whether the breakpoint gets triggered in post-execute.
   *
   * @param actor	the current actor
   * @return		true if triggered
   */
  public boolean triggersPostExecute(Actor actor) {
    return m_OnPostExecute && evaluatePostExecute(actor);
  }

  /**
   * Checks whether the breakpoint gets triggered in pre-output.
   *
   * @param actor	the current actor
   * @return		true if triggered
   */
  public boolean triggersPreOutput(Actor actor) {
    return m_OnPreOutput && evaluatePreOutput(actor);
  }

  /**
   * Checks whether the breakpoint gets triggered in post-output.
   *
   * @param actor	the current actor
   * @param token	the token available for output
   * @return		true if triggered
   */
  public boolean triggersPostOutput(Actor actor, Token token) {
    return m_OnPostOutput && evaluatePostOutput(actor, token);
  }

  /**
   * To be called when triggered (increments counter).
   */
  public void triggered() {
    m_TriggerCount++;
  }

  /**
   * Returns the number of times this breakpoint has been triggered.
   *
   * @return		the count
   */
  public int getTriggerCount() {
    return m_TriggerCount;
  }
}
