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
 * Debug.java
 * Copyright (C) 2013-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.execution;

import adams.core.Stoppable;
import adams.core.Variables;
import adams.core.base.BaseString;
import adams.flow.condition.bool.BooleanConditionSupporter;
import adams.flow.core.Actor;
import adams.flow.core.InputConsumer;
import adams.flow.core.Token;
import adams.flow.execution.debug.ControlPanel;
import adams.flow.execution.debug.View;
import adams.gui.core.BasePanel;
import adams.gui.tools.ExpressionWatchPanel.ExpressionType;

import java.awt.Dimension;

/**
 <!-- globalinfo-start -->
 * Allows the user to define breakpoints that suspend the execution of the flow, allowing the inspection of the current flow state.<br>
 * Tokens can only inspected during 'preInput', 'preExecute' and 'postOutput' of Breakpoint control actors. Step-wise debugging stops in 'preExecute', which should be able to access the current token in case of input consumers (ie transformers and sinks).
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-width &lt;int&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;The width of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 800
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-height &lt;int&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 600
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-breakpoint &lt;adams.flow.execution.AbstractBreakpoint&gt; [-breakpoint ...] (property: breakpoints)
 * &nbsp;&nbsp;&nbsp;The breakpoints to use for suspending the flow execution.
 * &nbsp;&nbsp;&nbsp;default: 
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
 * <pre>-step-by-step &lt;boolean&gt; (property: stepByStep)
 * &nbsp;&nbsp;&nbsp;Whether to start in step-by-step mode or wait for first breakpoint.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Debug
  extends AbstractGraphicalFlowExecutionListener
  implements Stoppable {

  /** for serialization. */
  private static final long serialVersionUID = -7287036923779341439L;

  /** the width of the dialog. */
  protected int m_Width;

  /** the height of the dialog. */
  protected int m_Height;

  /** the breakpoints to use. */
  protected AbstractBreakpoint[] m_Breakpoints;

  /** the views to display automatically. */
  protected View[] m_Views;

  /** the watch expressions. */
  protected BaseString[] m_Watches;

  /** the watch expression types. */
  protected ExpressionType[] m_WatchTypes;

  /** whether to start in auto-progress mode. */
  protected boolean m_StepByStep;

  /** debug panel. */
  protected transient ControlPanel m_DebugPanel;
  
  /** whether the GUI currently blocks the flow execution. */
  protected boolean m_Blocked;

  /** the current actor. */
  protected transient Actor m_Current;

  /** whether we can execute the next step. */
  protected boolean m_ExecuteNext;

  /** whether the flow got stopped. */
  protected boolean m_Stopped;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Allows the user to define breakpoints that suspend the execution "
	  + "of the flow, allowing the inspection of the current flow state.\n"
	  + "Tokens can only inspected during 'preInput', 'preExecute' and 'postOutput' "
	  + "of Breakpoint control actors. Step-wise debugging stops in "
	  + "'preExecute', which should be able to access the current token in "
	  + "case of input consumers (ie transformers and sinks).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "width", "width",
	    getDefaultWidth(), -1, null);

    m_OptionManager.add(
	    "height", "height",
	    getDefaultHeight(), -1, null);

    m_OptionManager.add(
	    "breakpoint", "breakpoints",
	    new AbstractBreakpoint[0]);

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
	    "step-by-step", "stepByStep",
	    false);
  }

  /**
   * Returns the default width for the dialog.
   *
   * @return		the default width
   */
  protected int getDefaultWidth() {
    return 900;
  }

  /**
   * Sets the width of the dialog.
   *
   * @param value 	the width
   */
  public void setWidth(int value) {
    m_Width = value;
    reset();
  }

  /**
   * Returns the currently set width of the dialog.
   *
   * @return 		the width
   */
  public int getWidth() {
    return m_Width;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String widthTipText() {
    return "The width of the dialog.";
  }

  /**
   * Returns the default height for the dialog.
   *
   * @return		the default height
   */
  protected int getDefaultHeight() {
    return 600;
  }

  /**
   * Sets the height of the dialog.
   *
   * @param value 	the height
   */
  public void setHeight(int value) {
    m_Height = value;
    reset();
  }

  /**
   * Returns the currently set height of the dialog.
   *
   * @return 		the height
   */
  public int getHeight() {
    return m_Height;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String heightTipText() {
    return "The height of the dialog.";
  }

  /**
   * Sets the breakpoints to use for suspending the flow execution.
   *
   * @param value	the breakpoints
   */
  public void setBreakpoints(AbstractBreakpoint[] value) {
    m_Breakpoints = value;
    reset();
  }

  /**
   * Returns the breakpoints to use for suspending the flow execution.
   *
   * @return		the breakpoints
   */
  public AbstractBreakpoint[] getBreakpoints() {
    return m_Breakpoints;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String breakpointsTipText() {
    return "The breakpoints to use for suspending the flow execution.";
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
   * Sets whether to start in step-by-step mode or wait for first breakpoint.
   *
   * @param value	true if to start in step-by-step mode
   */
  public void setStepByStep(boolean value) {
    m_StepByStep = value;
    reset();
  }

  /**
   * Returns whether to start in step-by-step mode or wait for first breakpoint.
   *
   * @return		true if to start in step-by-step mode
   */
  public boolean getStepByStep() {
    return m_StepByStep;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String stepByStepTipText() {
    return "Whether to start in step-by-step mode or wait for first breakpoint.";
  }

  /**
   * Sets whether step mode is used.
   *
   * @param value	true if step mode
   */
  public void setStepMode(boolean value) {
    if (m_DebugPanel != null)
      m_DebugPanel.setStepModeEnabled(value);
  }

  /**
   * Returns whether step mode is used.
   *
   * @return		true if step mode
   */
  public boolean isStepMode() {
    if (m_DebugPanel != null)
      return m_DebugPanel.isStepModeEnabled();
    else
      return false;
  }

  /**
   * The title of this listener.
   * 
   * @return		the title
   */
  @Override
  public String getListenerTitle() {
    return "Debug";
  }

  /**
   * Gets called when the flow execution starts.
   */
  public void startListening() {
    super.startListening();

    m_Stopped = false;
  }

  /**
   * Returns the panel to use.
   * 
   * @return		the panel, null if none available
   */
  @Override
  public BasePanel newListenerPanel() {
    int		i;
    
    m_DebugPanel = new ControlPanel();
    m_DebugPanel.setOwner(this);
    for (i = 0; i < m_Watches.length; i++)
      m_DebugPanel.addWatch(m_Watches[i].getValue(), m_WatchTypes[i]);
    setStepMode(m_StepByStep);

    return m_DebugPanel;
  }
  
  /**
   * Returns the default size for the frame.
   * 
   * @return		the frame size
   */
  @Override
  public Dimension getDefaultFrameSize() {
    return new Dimension(getWidth(), getHeight());
  }

  /**
   * Returns whether the frame should get disposed when the flow finishes.
   *
   * @return		true if to dispose when flow finishes
   */
  public boolean getDisposeOnFinish() {
    return true;
  }

  /**
   * Closes the dialog.
   */
  @Override
  protected void updateGUI() {
    if (m_DebugPanel != null)
      m_DebugPanel.closeParent();
  }
  
  /**
   * Returns whether the flow execution is currently blocked.
   */
  public boolean isBlocked() {
    return m_Blocked;
  }
  
  /**
   * Blocks thhe flow execution.
   */
  public void blockExecution() {
    m_Blocked = true;
    m_DebugPanel.update();
    while (m_Blocked && !m_Stopped && !m_DebugPanel.getCurrentActor().isStopped()) {
      try {
	synchronized(this) {
	  wait(50);
	}
      }
      catch (Exception e) {
	// ignored
      }
    }
  }
  
  /**
   * Unblocks the flow execution.
   */
  public void unblockExecution() {
    m_Blocked = false;
  }
  
  /**
   * Suspends the flow execution.
   * 
   * @param point	the breakpoint that triggered the suspend
   * @param actor	the current actor
   * @param stage	the hook method (eg preInput)
   */
  protected void triggered(AbstractBreakpoint point, Actor actor, ExecutionStage stage) {
    boolean	blocked;

    if (m_Stopped || ((getOwner() != null) && getOwner().isStopped()))
      return;

    if (isLoggingEnabled())
      getLogger().info(point.getClass().getName() + "/" + stage + ": " + actor.getFullName());

    blocked = ((point == null) && isStepMode()) || (point != null);

    m_DebugPanel.setCurrentStage(stage);
    m_DebugPanel.setCurrentActor(actor);
    m_DebugPanel.setCurrentToken(null);
    m_DebugPanel.setCurrentBreakpoint(point);
    if (point instanceof BooleanConditionSupporter)
      m_DebugPanel.setCurrentCondition(((BooleanConditionSupporter) point).getCondition());
    else
      m_DebugPanel.setCurrentCondition(null);
    m_DebugPanel.showFrame();
    m_DebugPanel.breakpointReached(blocked);

    if (blocked)
      blockExecution();
  }
  
  /**
   * Suspends the flow execution.
   * 
   * @param point	the breakpoint that triggered the suspend
   * @param actor	the current actor
   * @param stage	the hook method (eg preInput)
   * @param token	the current token
   */
  protected void triggered(AbstractBreakpoint point, Actor actor, ExecutionStage stage, Token token) {
    boolean	blocked;

    if (m_Stopped || ((getOwner() != null) && getOwner().isStopped()))
      return;

    if (isLoggingEnabled())
      getLogger().info(point.getClass().getName() + "/" + stage + ": " + actor.getFullName() + "\n\t" + token);

    blocked = ((point == null) && isStepMode()) || (point != null);

    m_DebugPanel.setCurrentStage(stage);
    m_DebugPanel.setCurrentActor(actor);
    m_DebugPanel.setCurrentToken(token);
    m_DebugPanel.setCurrentBreakpoint(point);
    if (point instanceof BooleanConditionSupporter)
      m_DebugPanel.setCurrentCondition(((BooleanConditionSupporter) point).getCondition());
    else
      m_DebugPanel.setCurrentCondition(null);
    m_DebugPanel.showFrame();
    m_DebugPanel.breakpointReached(blocked);

    if (blocked)
      blockExecution();
  }
  
  /**
   * Gets called before the actor receives the token.
   * 
   * @param actor	the actor that will receive the token
   * @param token	the token that the actor will receive
   */
  @Override
  public void preInput(Actor actor, Token token) {
    for (AbstractBreakpoint point: m_Breakpoints) {
      if (!point.getDisabled() && point.triggersPreInput(actor, token)) {
	triggered(point, actor, ExecutionStage.PRE_INPUT, token);
	break;
      }
    }
  }
  
  /**
   * Gets called after the actor received the token.
   * 
   * @param actor	the actor that received the token
   */
  @Override
  public void postInput(Actor actor) {
    for (AbstractBreakpoint point: m_Breakpoints) {
      if (!point.getDisabled() && point.triggersPostInput(actor)) {
	triggered(point, actor, ExecutionStage.POST_INPUT);
	break;
      }
    }
  }
  
  /**
   * Gets called before the actor gets executed.
   * 
   * @param actor	the actor that will get executed
   */
  @Override
  public void preExecute(Actor actor) {
    Token	token;

    token = null;
    if (actor instanceof InputConsumer)
      token = ((InputConsumer) actor).currentInput();

    for (AbstractBreakpoint point : m_Breakpoints) {
      if (!point.getDisabled() && point.triggersPreExecute(actor)) {
	if (token == null)
	  triggered(point, actor, ExecutionStage.PRE_EXECUTE);
	else
	  triggered(point, actor, ExecutionStage.PRE_EXECUTE, token);
	break;
      }
    }
  }

  /**
   * Gets called after the actor was executed.
   * 
   * @param actor	the actor that was executed
   */
  @Override
  public void postExecute(Actor actor) {
    for (AbstractBreakpoint point: m_Breakpoints) {
      if (!point.getDisabled() && point.triggersPostExecute(actor)) {
	triggered(point, actor, ExecutionStage.POST_EXECUTE);
	break;
      }
    }
  }
  
  /**
   * Gets called before a token gets obtained from the actor.
   * 
   * @param actor	the actor the token gets obtained from
   */
  @Override
  public void preOutput(Actor actor) {
    for (AbstractBreakpoint point: m_Breakpoints) {
      if (!point.getDisabled() && point.triggersPreOutput(actor)) {
	triggered(point, actor, ExecutionStage.PRE_OUTPUT);
	break;
      }
    }
  }
  
  /**
   * Gets called after a token was acquired from the actor.
   * 
   * @param actor	the actor that the token was acquired from
   * @param token	the token that was acquired from the actor
   */
  @Override
  public void postOutput(Actor actor, Token token) {
    for (AbstractBreakpoint point: m_Breakpoints) {
      if (!point.getDisabled() && point.triggersPostOutput(actor, token)) {
	triggered(point, actor, ExecutionStage.POST_OUTPUT);
	break;
      }
    }
  }

  /**
   * Stops the execution.
   */
  @Override
  public void stopExecution() {
    m_Stopped = true;
    m_Blocked = false;
  }
}
