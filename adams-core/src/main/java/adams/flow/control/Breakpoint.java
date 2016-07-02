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
 * Breakpoint.java
 * Copyright (C) 2011-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control;

import adams.core.QuickInfoHelper;
import adams.core.Variables;
import adams.core.base.BaseString;
import adams.flow.condition.bool.BooleanCondition;
import adams.flow.condition.bool.BooleanConditionSupporter;
import adams.flow.condition.bool.Expression;
import adams.flow.core.ActorPath;
import adams.flow.core.ControlActor;
import adams.flow.core.Unknown;
import adams.flow.execution.debug.AbstractScopeRestriction;
import adams.flow.execution.debug.NoScopeRestriction;
import adams.flow.execution.debug.View;
import adams.flow.execution.debug.PathBreakpoint;
import adams.flow.transformer.AbstractTransformer;
import adams.gui.tools.ExpressionWatchPanel.ExpressionType;

/**
 <!-- globalinfo-start -->
 * Allows to pause the execution of the flow when this actor is reached and the condition evaluates to 'true'.
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
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: Breakpoint
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
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing 
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-scope-restriction &lt;adams.flow.execution.debug.AbstractScopeRestriction&gt; (property: scopeRestriction)
 * &nbsp;&nbsp;&nbsp;The scopeRestriction to use for suspending the flow execution.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.execution.debug.NoScopeRestriction
 * </pre>
 * 
 * <pre>-on-pre-input &lt;boolean&gt; (property: onPreInput)
 * &nbsp;&nbsp;&nbsp;If set to true, the breakpoint gets evaluated at pre-input (of token) time;
 * &nbsp;&nbsp;&nbsp; token available.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-on-post-input &lt;boolean&gt; (property: onPostInput)
 * &nbsp;&nbsp;&nbsp;If set to true, the breakpoint gets evaluated at post-input (of token) time.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-on-pre-execute &lt;boolean&gt; (property: onPreExecute)
 * &nbsp;&nbsp;&nbsp;If set to true, the breakpoint gets evaluated at pre-execute time.
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 * 
 * <pre>-on-post-execute &lt;boolean&gt; (property: onPostExecute)
 * &nbsp;&nbsp;&nbsp;If set to true, the breakpoint gets evaluated at post-execute time.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-on-pre-output &lt;boolean&gt; (property: onPreOutput)
 * &nbsp;&nbsp;&nbsp;If set to true, the breakpoint gets evaluated at pre-output (of token) time;
 * &nbsp;&nbsp;&nbsp; token available.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-on-post-output &lt;boolean&gt; (property: onPostOutput)
 * &nbsp;&nbsp;&nbsp;If set to true, the breakpoint gets evaluated at post-output (of token) 
 * &nbsp;&nbsp;&nbsp;time.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-condition &lt;adams.flow.condition.bool.BooleanCondition&gt; (property: condition)
 * &nbsp;&nbsp;&nbsp;The condition to evaluate; if the condition evaluates to 'true', the execution 
 * &nbsp;&nbsp;&nbsp;of the flow is paused.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.condition.bool.Expression
 * </pre>
 * 
 * <pre>-watch &lt;adams.core.base.BaseString&gt; [-watch ...] (property: watches)
 * &nbsp;&nbsp;&nbsp;The expression to display initially in the watch dialog; the type of the 
 * &nbsp;&nbsp;&nbsp;watch needs to be specified as well.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-watch-type &lt;VARIABLE|BOOLEAN|NUMERIC|STRING&gt; [-watch-type ...] (property: watchTypes)
 * &nbsp;&nbsp;&nbsp;The types of the watch expressions; determines how the expressions get evaluated 
 * &nbsp;&nbsp;&nbsp;and displayed.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-view &lt;SOURCE|EXPRESSIONS|VARIABLES|STORAGE|INSPECT_TOKEN|BREAKPOINTS&gt; [-view ...] (property: views)
 * &nbsp;&nbsp;&nbsp;The views to display automatically when the breakpoint is reached.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Breakpoint
  extends AbstractTransformer
  implements ControlActor, BooleanConditionSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 1670185555433805533L;

  /** the scope restriction to use. */
  protected AbstractScopeRestriction m_ScopeRestriction;

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

  /** the condition to evaluate. */
  protected BooleanCondition m_Condition;

  /** the views to display automatically. */
  protected View[] m_Views;

  /** the watch expressions. */
  protected BaseString[] m_Watches;

  /** the watch expression types. */
  protected ExpressionType[] m_WatchTypes;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Allows to pause the execution of the flow when this actor is reached "
      + "and the condition evaluates to 'true'.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "scope-restriction", "scopeRestriction",
      new NoScopeRestriction());

    m_OptionManager.add(
      "on-pre-input", "onPreInput",
      false);

    m_OptionManager.add(
      "on-post-input", "onPostInput",
      false);

    m_OptionManager.add(
      "on-pre-execute", "onPreExecute",
      true);

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
      "condition", "condition",
      new Expression());

    m_OptionManager.add(
      "watch", "watches",
      new BaseString[0]);

    m_OptionManager.add(
      "watch-type", "watchTypes",
      new ExpressionType[0]);

    m_OptionManager.add(
      "view", "views",
      new View[0]);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "condition", m_Condition.getQuickInfo());
  }

  /**
   * Sets the restriction for the scope to use for suspending the flow execution.
   *
   * @param value	the restriction
   */
  public void setScopeRestriction(AbstractScopeRestriction value) {
    m_ScopeRestriction = value;
    reset();
  }

  /**
   * Returns the restriction for the scope to use for suspending the flow execution.
   *
   * @return		the restriction
   */
  public AbstractScopeRestriction getScopeRestriction() {
    return m_ScopeRestriction;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String scopeRestrictionTipText() {
    return "The scopeRestriction to use for suspending the flow execution.";
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
   * Sets the break condition to evaluate.
   *
   * @param value	the expression
   */
  public void setCondition(BooleanCondition value) {
    m_Condition = value;
    reset();
  }

  /**
   * Returns the break condition to evaluate.
   *
   * @return		the expression
   */
  public BooleanCondition getCondition() {
    return m_Condition;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String conditionTipText() {
    return
        "The condition to evaluate; if the condition evaluates to 'true', "
      + "the execution of the flow is paused.";
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
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String		result;
    Flow		flow;
    PathBreakpoint	breakpoint;

    result = super.setUp();

    if (result == null) {
      if (getRoot() instanceof Flow) {
	flow = (Flow) getRoot();

	breakpoint = new PathBreakpoint();
	breakpoint.setOnPreInput(m_OnPreInput);
	breakpoint.setOnPostInput(m_OnPostInput);
	breakpoint.setOnPreExecute(m_OnPreExecute);
	breakpoint.setOnPostExecute(m_OnPostExecute);
	breakpoint.setOnPreOutput(m_OnPreOutput);
	breakpoint.setOnPostOutput(m_OnPostOutput);
	breakpoint.setCondition(getCondition());
        breakpoint.setViews(m_Views.clone());
        breakpoint.setWatches(m_Watches.clone());
        breakpoint.setWatchTypes(m_WatchTypes.clone());
	breakpoint.setPath(new ActorPath(getFullName()));

	flow.addBreakpoint(breakpoint, m_ScopeRestriction);
      }
      else {
	result = "Root actor is not a flow, failed to set breakpoint!";
      }
    }

    return result;
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
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->adams.flow.core.Unknown.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{Unknown.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    m_OutputToken = m_InputToken;
    return null;
  }
}
