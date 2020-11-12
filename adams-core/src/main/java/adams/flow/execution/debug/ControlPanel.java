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
 * ControlPanel.java
 * Copyright (C) 2015-2020 University of Waikato, Hamilton, NZ
 */

package adams.flow.execution.debug;

import adams.core.CleanUpHandler;
import adams.core.option.DebugNestedProducer;
import adams.core.option.NestedConsumer;
import adams.flow.condition.bool.BooleanCondition;
import adams.flow.condition.bool.BooleanConditionSupporter;
import adams.flow.control.Breakpoint;
import adams.flow.control.Flow;
import adams.flow.core.Actor;
import adams.flow.core.Token;
import adams.flow.execution.Debug;
import adams.flow.execution.ExecutionStage;
import adams.gui.core.BaseButton;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseTabbedPane;
import adams.gui.core.BaseTextField;
import adams.gui.core.BaseToggleButton;
import adams.gui.core.Fonts;
import adams.gui.core.GUIHelper;
import adams.gui.core.TextEditorPanel;
import adams.gui.flow.FlowMultiPagePane;
import adams.gui.flow.FlowPanel;
import adams.gui.flow.FlowTreeHandler;
import adams.gui.flow.tree.Tree;
import adams.gui.goe.FlowHelper;
import adams.gui.goe.GenericObjectEditorPanel;
import adams.gui.tools.ExpressionWatchPanel;
import adams.gui.tools.ExpressionWatchPanel.ExpressionType;
import adams.gui.tools.VariableManagementPanel;
import adams.gui.visualization.debug.InspectionPanel;
import adams.gui.visualization.debug.StoragePanel;
import com.github.fracpete.jclipboardhelper.ClipboardHelper;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.HashSet;

/**
 * A little dialog for controlling the breakpoint. Cannot be static class!
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ControlPanel
  extends BasePanel
  implements CleanUpHandler, FlowTreeHandler {

  /** for serialization. */
  private static final long serialVersionUID = 1000900663466801934L;

  /** the tabbed pane for displaying the various display panels. */
  protected BaseTabbedPane m_TabbedPaneDisplays;

  /** the button for stopping execution. */
  protected BaseButton m_ButtonStop;

  /** the button for disabling/enabling the breakpoint. */
  protected BaseButton m_ButtonToggle;

  /** the button for resuming execution. */
  protected BaseButton m_ButtonPauseResume;

  /** the button for performing the next step when in manual mode. */
  protected BaseButton m_ButtonStep;

  /** the button for displaying dialog with watch expressions. */
  protected BaseToggleButton m_ButtonExpressions;

  /** the breakpoint condition. */
  protected GenericObjectEditorPanel m_GOEPanelCondition;

  /** the button to show the source code of the current flow. */
  protected BaseToggleButton m_ButtonSource;

  /** the button to show the variable management dialog. */
  protected BaseToggleButton m_ButtonVariables;

  /** the button to show the storage. */
  protected BaseToggleButton m_ButtonStorage;

  /** the button to show inspection panel for the current token. */
  protected BaseToggleButton m_ButtonInspectToken;

  /** the button to show the breakpoints management panel. */
  protected BaseToggleButton m_ButtonBreakpoints;

  /** the panel with the watch expressions. */
  protected ExpressionWatchPanel m_PanelExpressions;

  /** the panel with the variables. */
  protected VariableManagementPanel m_PanelVariables;

  /** the panel for inspecting the current token. */
  protected InspectionPanel m_PanelInspectionToken;

  /** the panel for displaying the source. */
  protected TextEditorPanel m_PanelSource;

  /** the panel for displaying the source (incl buttons). */
  protected BasePanel m_PanelSourceAll;

  /** the panel for displaying the temporary storage. */
  protected StoragePanel m_PanelStorage;

  /** the panel for managing the breakpoints. */
  protected BreakpointPanel m_PanelBreakpoints;

  /** the text field for the actor path. */
  protected BaseTextField m_TextActorPath;

  /** the button for copying the actor path. */
  protected BaseButton m_ButtonActorPath;

  /** the button for highlighting the ctor. */
  protected BaseButton m_ButtonActorHighlight;

  /** the text field for the stage. */
  protected BaseTextField m_TextStage;

  /** the owning listener. */
  protected transient Debug m_Owner;

  /** the current token. */
  protected Token m_CurrentToken;

  /** the current actor. */
  protected Actor m_CurrentActor;

  /** the current hook method. */
  protected ExecutionStage m_CurrentStage;

  /** the current boolean condition. */
  protected BooleanCondition m_CurrentCondition;

  /** the current breakpoint. */
  protected AbstractBreakpoint m_CurrentBreakpoint;

  /** whether the user has modified the view and it should be left alone. */
  protected boolean m_Manual;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Manual            = false;
    m_CurrentActor      = null;
    m_CurrentBreakpoint = null;
    m_CurrentCondition  = null;
    m_CurrentStage = null;
    m_CurrentToken      = null;
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    JPanel 	panelButtons1;
    JPanel 	panelButtons2;
    JPanel 	panelAllSections;
    JPanel 	panelSection;
    JPanel 	panelTmp1;
    JPanel	panelTmp2;
    JPanel	panelTmp3;

    super.initGUI();

    setLayout(new BorderLayout());

    // tabbed pane
    m_TabbedPaneDisplays = new BaseTabbedPane();
    add(m_TabbedPaneDisplays, BorderLayout.CENTER);

    // buttons
    // 1. execution
    panelAllSections = new JPanel(new GridLayout(2, 1));
    add(panelAllSections, BorderLayout.NORTH);
    panelSection = new JPanel(new BorderLayout());
    panelAllSections.add(panelSection);
    panelButtons1 = new JPanel(new GridLayout(1, 4));
    panelSection.add(panelButtons1, BorderLayout.NORTH);

    m_ButtonPauseResume = new BaseButton(GUIHelper.getIcon("resume.gif"));
    m_ButtonPauseResume.setToolTipText("Pause/Resume execution");
    m_ButtonPauseResume.addActionListener((ActionEvent e) -> pauseResumeExecution());
    panelButtons1.add(m_ButtonPauseResume);

    m_ButtonStep = new BaseButton(GUIHelper.getIcon("step.gif"));
    m_ButtonStep.setMnemonic('e');
    m_ButtonStep.setToolTipText("Step to next actor");
    m_ButtonStep.addActionListener((ActionEvent e) -> nextStep());
    panelButtons1.add(m_ButtonStep);

    m_ButtonToggle = new BaseButton(GUIHelper.getIcon("debug_off.png"));
    m_ButtonToggle.setMnemonic('D');
    m_ButtonToggle.setToolTipText("Disable the breakpoint");
    m_ButtonToggle.addActionListener((ActionEvent e) -> disableEnableBreakpoint());
    panelButtons1.add(m_ButtonToggle);

    m_ButtonStop = new BaseButton(GUIHelper.getIcon("stop_blue.gif"));
    m_ButtonStop.setMnemonic('S');
    m_ButtonStop.setToolTipText("Stops the flow execution immediately");
    m_ButtonStop.addActionListener((ActionEvent e) -> stopFlowExecution());
    panelButtons1.add(m_ButtonStop);

    // condition
    m_GOEPanelCondition = new GenericObjectEditorPanel(BooleanCondition.class, new Breakpoint().getCondition(), false);
    m_GOEPanelCondition.addChangeListener((ChangeEvent e) -> {
      if (getCurrentBreakpoint() instanceof BooleanConditionSupporter) {
	((BooleanConditionSupporter) getCurrentBreakpoint()).setCondition(
	  (BooleanCondition) m_GOEPanelCondition.getCurrent());
      }
      update();
    });
    panelTmp1 = new JPanel(new BorderLayout());
    panelTmp1.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
    panelTmp1.add(m_GOEPanelCondition, BorderLayout.CENTER);
    panelSection.add(panelTmp1, BorderLayout.CENTER);

    // 2. runtime information
    panelButtons2 = new JPanel(new GridLayout(1, 6, 0, 5));
    panelSection = new JPanel(new BorderLayout());
    panelSection.add(panelButtons2, BorderLayout.NORTH);
    panelAllSections.add(panelSection);

    m_ButtonInspectToken = new BaseToggleButton(GUIHelper.getIcon("properties.gif"));
    m_ButtonInspectToken.setMnemonic('I');
    m_ButtonInspectToken.setToolTipText("Display dialog for inspecting the current token");
    m_ButtonInspectToken.addActionListener((ActionEvent e) -> {
      m_Manual = true;
      inspectToken(m_ButtonInspectToken.isSelected());
    });
    panelButtons2.add(m_ButtonInspectToken);

    m_ButtonVariables = new BaseToggleButton(GUIHelper.getIcon("variable.gif"));
    m_ButtonVariables.setMnemonic('V');
    m_ButtonVariables.setToolTipText("Display dialog with currently active variables");
    m_ButtonVariables.addActionListener((ActionEvent e) -> {
      m_Manual = true;
      showVariables(m_ButtonVariables.isSelected());
    });
    panelButtons2.add(m_ButtonVariables);

    m_ButtonStorage = new BaseToggleButton(GUIHelper.getIcon("storage.gif"));
    m_ButtonStorage.setMnemonic('t');
    m_ButtonStorage.setToolTipText("Display dialog with items currently stored in temporary storage");
    m_ButtonStorage.addActionListener((ActionEvent e) -> {
      m_Manual = true;
      showStorage(m_ButtonStorage.isSelected());
    });
    panelButtons2.add(m_ButtonStorage);

    m_ButtonBreakpoints = new BaseToggleButton(GUIHelper.getIcon("breakpoints.gif"));
    m_ButtonBreakpoints.setMnemonic('n');
    m_ButtonBreakpoints.setToolTipText("Display dialog for inspecting the current flow");
    m_ButtonBreakpoints.addActionListener((ActionEvent e) -> {
      m_Manual = true;
      showBreakpoints(m_ButtonBreakpoints.isSelected());
    });
    panelButtons2.add(m_ButtonBreakpoints);

    m_ButtonExpressions = new BaseToggleButton(GUIHelper.getIcon("glasses.gif"));
    m_ButtonExpressions.setMnemonic('x');
    m_ButtonExpressions.setToolTipText("Display dialog for watch expressions");
    m_ButtonExpressions.addActionListener((ActionEvent e) -> {
      m_Manual = true;
      showWatchExpressions(m_ButtonExpressions.isSelected());
    });
    panelButtons2.add(m_ButtonExpressions);

    m_ButtonSource = new BaseToggleButton(GUIHelper.getIcon("source.png"));
    m_ButtonSource.setMnemonic('o');
    m_ButtonSource.setToolTipText("Display current flow state as source (nested format)");
    m_ButtonSource.addActionListener((ActionEvent e) -> {
      m_Manual = true;
      showSource(m_ButtonSource.isSelected());
    });
    panelButtons2.add(m_ButtonSource);

    // the path to the breakpoint
    m_TextActorPath = new BaseTextField();
    m_TextActorPath.setToolTipText("Actor path");
    m_TextActorPath.setEditable(false);
    m_ButtonActorPath = new BaseButton(GUIHelper.getIcon("copy.gif"));
    m_ButtonActorPath.setToolTipText("Copy path");
    m_ButtonActorPath.addActionListener((ActionEvent e) -> ClipboardHelper.copyToClipboard(m_TextActorPath.getText()));
    m_ButtonActorHighlight = new BaseButton(GUIHelper.getIcon("goto.gif"));
    m_ButtonActorHighlight.setToolTipText("Highlight actor in editor window");
    m_ButtonActorHighlight.addActionListener((ActionEvent e) -> highlightActor());
    m_TextStage = new BaseTextField();
    m_TextStage.setEditable(false);

    panelTmp3 = new JPanel(new GridLayout(1, 2));
    panelTmp3.add(m_ButtonActorPath);
    panelTmp3.add(m_ButtonActorHighlight);

    panelTmp2 = new JPanel(new BorderLayout(5, 0));
    panelTmp2.add(m_TextActorPath, BorderLayout.CENTER);
    panelTmp2.add(panelTmp3, BorderLayout.EAST);

    panelTmp1 = new JPanel(new BorderLayout(5, 0));
    panelTmp1.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
    panelTmp1.add(panelTmp2, BorderLayout.CENTER);
    panelTmp1.add(m_TextStage, BorderLayout.EAST);

    panelSection.add(panelTmp1, BorderLayout.CENTER);

    // watches
    m_PanelExpressions = new ExpressionWatchPanel();

    // breakpoints
    m_PanelBreakpoints = new BreakpointPanel();
  }

  /**
   * Finishes the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();
    update();
  }

  /**
   * Returns the underlying flow.
   *
   * @return		the flow
   */
  protected Flow getFlow() {
    return (Flow) m_CurrentActor.getRoot();
  }

  /**
   * Updates the enabled status of the buttons, text fields and other widget
   * updates.
   */
  public void update() {
    update((getOwner() != null) && getOwner().isBlocked());
  }

  /**
   * Updates the enabled status of the buttons, text fields and other widget
   * updates.
   *
   * @param blocked	whether flow execution has been blocked
   */
  public void update(boolean blocked) {
    boolean	actorPresent;
    boolean	stopped;
    boolean	hasToken;

    actorPresent = (getCurrentActor() != null);
    stopped      = actorPresent && getFlow().isStopped();
    hasToken     = (getCurrentToken() != null);

    m_ButtonStop.setEnabled(actorPresent && !stopped);
    m_ButtonToggle.setEnabled(actorPresent && !stopped && (m_CurrentBreakpoint != null));
    m_ButtonPauseResume.setEnabled(actorPresent && !stopped);
    m_ButtonStep.setEnabled(actorPresent && !stopped && blocked);
    m_ButtonVariables.setEnabled(actorPresent);
    m_ButtonStorage.setEnabled(actorPresent);
    m_ButtonExpressions.setEnabled(actorPresent && !stopped && blocked);
    m_ButtonBreakpoints.setEnabled(actorPresent && !stopped && blocked);
    m_ButtonInspectToken.setEnabled(actorPresent && !stopped && blocked && hasToken);
    m_ButtonSource.setEnabled(actorPresent && !stopped && blocked);

    m_GOEPanelCondition.setEnabled(actorPresent && !stopped && blocked);
    m_ButtonActorPath.setEnabled(m_TextActorPath.getText().length() > 0);
    m_ButtonActorHighlight.setEnabled(m_TextActorPath.getText().length() > 0);

    if (!m_ButtonInspectToken.isEnabled()) {
      if (m_PanelInspectionToken != null)
	m_PanelInspectionToken.setCurrent(null);
    }
    else {
      if (m_PanelInspectionToken != null)
	m_PanelInspectionToken.setCurrent(getCurrentToken());
    }

    if (getCurrentBreakpoint() != null) {
      if (getCurrentBreakpoint().getDisabled()) {
	m_ButtonToggle.setToolTipText("Enable current breakpoint");
	m_ButtonToggle.setIcon(GUIHelper.getIcon("debug.png"));
      }
      else {
	m_ButtonToggle.setToolTipText("Disable current breakpoint");
	m_ButtonToggle.setIcon(GUIHelper.getIcon("debug_off.png"));
      }
    }

    if (m_Owner != null) {
      if (m_Owner.isBlocked() || m_Owner.isStepMode())
	m_ButtonPauseResume.setIcon(GUIHelper.getIcon("resume.gif"));
      else
	m_ButtonPauseResume.setIcon(GUIHelper.getIcon("pause.gif"));
      m_PanelBreakpoints.setIgnoreUpdates(true);
      m_PanelBreakpoints.setOwner(this);
      m_PanelBreakpoints.setIgnoreUpdates(false);
    }
  }

  /**
   * Queues a call to {@link #update()} in the swing thread.
   */
  public void queueUpdate() {
    SwingUtilities.invokeLater(() -> update());
  }

  /**
   * Sets the owning listener.
   *
   * @param value	the owner
   */
  public void setOwner(Debug value) {
    m_Owner = value;
  }

  /**
   * Returns the owning listener.
   *
   * @return		the owner
   */
  public Debug getOwner() {
    return m_Owner;
  }

  /**
   * Sets the current token, if any.
   *
   * @param value	the token
   */
  public void setCurrentToken(Token value) {
    m_CurrentToken = value;
  }

  /**
   * Returns the current token, if any.
   *
   * @return		the token, null if none available
   */
  public Token getCurrentToken() {
    return m_CurrentToken;
  }

  /**
   * Sets the current actor.
   *
   * @param value	the actor
   */
  public void setCurrentActor(Actor value) {
    m_CurrentActor = value;
    if (m_CurrentActor != null) {
      if (m_PanelExpressions != null)
	m_PanelExpressions.setVariables(getCurrentActor().getVariables());
      if (m_PanelVariables != null)
	m_PanelVariables.setVariables(getCurrentActor().getVariables());
    }
  }

  /**
   * Returns the current actor.
   *
   * @return		the actor
   */
  public Actor getCurrentActor() {
    return m_CurrentActor;
  }

  /**
   * Sets the current stage.
   *
   * @param value	the stage
   */
  public void setCurrentStage(ExecutionStage value) {
    m_CurrentStage = value;
  }

  /**
   * Returns the current stage.
   *
   * @return		the stage
   */
  public ExecutionStage getCurrentStage() {
    return m_CurrentStage;
  }

  /**
   * Sets the current boolean condition.
   *
   * @param value	the condition, null if not available
   */
  public void setCurrentCondition(BooleanCondition value) {
    m_CurrentCondition = value;
  }

  /**
   * Returns the current boolean condition.
   *
   * @return		the condition, null if none available
   */
  public BooleanCondition getCurrentCondition() {
    return m_CurrentCondition;
  }

  /**
   * Sets the current boolean breakpoint.
   *
   * @param value	the breakpoint, null if not available
   */
  public void setCurrentBreakpoint(AbstractBreakpoint value) {
    m_CurrentBreakpoint = value;
  }

  /**
   * Returns the current boolean breakpoint.
   *
   * @return		the breakpoint, null if none available
   */
  public AbstractBreakpoint getCurrentBreakpoint() {
    return m_CurrentBreakpoint;
  }

  /**
   * Returns the tree, if available.
   *
   * @return		the tree, null if not available
   */
  public Tree getTree() {
    Tree	result;
    Flow	flow;

    result = null;

    if (getCurrentActor().getRoot() instanceof Flow) {
      flow = (Flow) getCurrentActor().getRoot();
      if (flow.isHeadless())
	return null;
      if (flow.getParentComponent() != null) {
	if (flow.getParentComponent() instanceof FlowPanel) {
	  if (((FlowPanel) flow.getParentComponent()).isDebugTreeVisible())
	    result = ((FlowPanel) flow.getParentComponent()).getDebugTree();
	  else
            result = ((FlowPanel) flow.getParentComponent()).getTree();
        }
	else if (flow.getParentComponent() instanceof Container)
	  result = FlowHelper.getTree((Container) flow.getParentComponent());
	else if (flow.getParentComponent() instanceof FlowTreeHandler)
	  result = ((FlowTreeHandler) flow.getParentComponent()).getTree();
      }
    }

    return result;
  }

  /**
   * Returns whether the control panel is enabled.
   *
   * @return		true if enabled
   */
  public boolean isControlPanelEnabled() {
    return m_ButtonStop.isEnabled();
  }

  /**
   * Continues the flow execution.
   */
  protected void continueFlowExecution() {
    getOwner().removeOneOffBreakpoints(null);
    getOwner().unblockExecution();
    queueUpdate();
  }

  /**
   * Stops the flow execution.
   */
  protected void stopFlowExecution() {
    // due to atomic execution
    m_PanelBreakpoints.removeAllBreakpoints();
    new Thread(() -> getFlow().stopExecution("User stopped flow!")).start();
    new Thread(() -> getOwner().unblockExecution()).start();
  }

  /**
   * Enables/disables step mode.
   *
   * @param enabled	if true step mode is enabled
   */
  public void setStepModeEnabled(boolean enabled) {
    if (m_PanelBreakpoints != null)
      m_PanelBreakpoints.setStepModeEnabled(enabled);
  }

  /**
   * Returns whether step mode is enabled.
   *
   * @return		true if step mode is enabled
   */
  public boolean isStepModeEnabled() {
    return (m_PanelBreakpoints != null) && m_PanelBreakpoints.isStepModeEnabled();
  }

  /**
   * Disable/enable the breakpoint.
   */
  protected void disableEnableBreakpoint() {
    if (getCurrentBreakpoint() == null) {
      if (getOwner().isLoggingEnabled())
        getOwner().getLogger().warning("No breakpoint to disable!");
      return;
    }

    if (getCurrentBreakpoint().isLoggingEnabled()) {
      getCurrentBreakpoint().getLogger().info("Setup: " + getCurrentBreakpoint().toCommandLine());
      getCurrentBreakpoint().getLogger().info("State (current): " + (getCurrentBreakpoint().getDisabled() ? "disabled" : "enabled"));
    }
    getCurrentBreakpoint().setDisabled(!getCurrentBreakpoint().getDisabled());
    if (getCurrentBreakpoint().isLoggingEnabled())
      getCurrentBreakpoint().getLogger().info("State (new): " + (getCurrentBreakpoint().getDisabled() ? "disabled" : "enabled"));

    queueUpdate();
  }

  /**
   * Pauses/resumes execution.
   */
  protected void pauseResumeExecution() {
    m_Owner.setStepMode(false);

    if (getOwner().isBlocked())
      continueFlowExecution();

    queueUpdate();
  }

  /**
   * Executes the next step in manual mode.
   */
  protected void nextStep() {
    getOwner().setStepMode(true);

    if (getOwner().isBlocked())
      continueFlowExecution();

    queueUpdate();
  }

  /**
   * Sets the visibility of a panel in the tabbed pane.
   *
   * @param panel	the panel to add/remove
   * @param title	the title of the panel
   * @param visible	if true the panel gets displayed, otherwise hidden
   */
  protected void setPanelVisible(JPanel panel, String title, boolean visible) {
    int	present;
    int	i;

    present = -1;
    for (i = 0; i < m_TabbedPaneDisplays.getTabCount(); i++) {
      if (m_TabbedPaneDisplays.getComponentAt(i) == panel) {
	present = i;
	break;
      }
    }

    // no change?
    if ((visible && (present > -1)) || (!visible && (present == -1)))
      return;

    if (visible) {
      m_TabbedPaneDisplays.addTab(title, panel);
      m_TabbedPaneDisplays.setSelectedIndex(m_TabbedPaneDisplays.getTabCount() - 1);
    }
    else {
      m_TabbedPaneDisplays.remove(present);
    }
  }

  /**
   * Displays dialog with watch expressions.
   *
   * @param visible	if true then displayed, otherwise hidden
   */
  protected void showWatchExpressions(boolean visible) {
    m_PanelExpressions.setVariables(getCurrentActor().getVariables());
    setPanelVisible(m_PanelExpressions, "Expressions", visible);
  }

  /**
   * Displays the source code (nested format) of the current flow.
   *
   * @param visible	if true then displayed, otherwise hidden
   */
  protected void showSource(boolean visible) {
    String		content;
    DebugNestedProducer producer;
    final BaseButton	buttonCopy;
    final BaseButton	buttonPasteAsNew;
    JPanel		buttons;

    if (m_PanelSource == null) {
      m_PanelSource = new TextEditorPanel();
      m_PanelSource.setTextFont(Fonts.getMonospacedFont());
      m_PanelSource.setEditable(false);
      m_PanelSource.setTabSize(2);

      m_PanelSourceAll = new BasePanel(new BorderLayout());
      m_PanelSourceAll.add(m_PanelSource, BorderLayout.CENTER);

      buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      m_PanelSourceAll.add(buttons, BorderLayout.SOUTH);

      buttonCopy = new BaseButton(GUIHelper.getIcon("copy.gif"));
      buttonCopy.setToolTipText("Copy to clipboard");
      buttonCopy.addActionListener((ActionEvent e) -> {
	if (m_PanelSource.getSelectedText() == null)
	  ClipboardHelper.copyToClipboard(m_PanelSource.getText());
	else
	  ClipboardHelper.copyToClipboard(m_PanelSource.getSelectedText());
      });
      buttons.add(buttonCopy);

      buttonPasteAsNew = new BaseButton(GUIHelper.getIcon("paste_as_new.gif"));
      buttonPasteAsNew.setToolTipText("Paste as new flow");
      buttonPasteAsNew.setEnabled(getFlow().getParentComponent() instanceof Container);
      buttonPasteAsNew.addActionListener((ActionEvent e) -> {
	if (getFlow().getParentComponent() instanceof Container) {
	  FlowMultiPagePane tabs = (FlowMultiPagePane) GUIHelper.getParent((Container) getFlow().getParentComponent(), FlowMultiPagePane.class);
	  if (tabs != null) {
	    FlowPanel panel = tabs.newPanel();
	    NestedConsumer consumer;
	    consumer = new NestedConsumer();
	    consumer.setQuiet(true);
	    Actor flow = (Actor) consumer.fromString(m_PanelSource.getText());
	    consumer.cleanUp();
	    if (flow != null) {
	      panel.setCurrentFlow(flow);
	      panel.setModified(true);
	    }
	  }
	}
      });
      buttons.add(buttonPasteAsNew);
    }

    if (visible) {
      producer = new DebugNestedProducer();
      producer.setOutputVariableValues(true);
      producer.produce(getFlow());
      content = producer.toString();
      producer.cleanUp();
    }
    else {
      content = "";
    }

    m_PanelSource.setContent(content);
    setPanelVisible(m_PanelSourceAll, "Source", visible);
  }

  /**
   * Displays the current variables in the system.
   *
   * @param visible	if true then displayed, otherwise hidden
   */
  protected void showVariables(boolean visible) {
    if (m_PanelVariables == null)
      m_PanelVariables = new VariableManagementPanel();
    m_PanelVariables.setVariables(getCurrentActor().getVariables());
    setPanelVisible(m_PanelVariables, "Variables", visible);
  }

  /**
   * Inspects the current token.
   *
   * @param visible	if true then displayed, otherwise hidden
   */
  protected void inspectToken(boolean visible) {
    if (!visible)
      m_ButtonInspectToken.setSelected(false);
    if (m_PanelInspectionToken == null)
      m_PanelInspectionToken = new InspectionPanel();
    m_PanelInspectionToken.setCurrent(m_CurrentToken);
    setPanelVisible(m_PanelInspectionToken, "Token", visible);
  }

  /**
   * Shows the current breakpoints.
   *
   * @param visible	if true then displayed, otherwise hidden
   */
  protected void showBreakpoints(boolean visible) {
    if (m_PanelBreakpoints == null) {
      m_PanelBreakpoints = new BreakpointPanel();
      m_PanelBreakpoints.setOwner(this);
    }
    m_PanelBreakpoints.refresh();
    setPanelVisible(m_PanelBreakpoints, "Breakpoints", visible);
  }

  /**
   * Shows the current temporary storage.
   *
   * @param visible	if true then displayed, otherwise hidden
   */
  protected void showStorage(boolean visible) {
    if (m_PanelStorage == null)
      m_PanelStorage = new StoragePanel();
    setPanelVisible(m_PanelStorage, "Storage", visible);
    m_PanelStorage.setHandler(getCurrentActor().getStorageHandler());
  }

  /**
   * Highlights the actor.
   */
  protected void highlightActor() {
    Component 	comp;
    final Tree	tree;

    if (getFlow().getParentComponent() == null)
      return;
    comp = getFlow().getParentComponent();
    if (!(comp instanceof FlowPanel))
      return;

    if (((FlowPanel) comp).isDebugTreeVisible())
      tree = ((FlowPanel) comp).getDebugTree();
    else
      tree = ((FlowPanel) comp).getTree();
    SwingUtilities.invokeLater(() -> {
      String full = m_CurrentActor.getFullName();
      tree.locateAndDisplay(full);
    });
  }

  /**
   * Called by actor when breakpoint reached.
   *
   * @param blocked	whether execution has been blocked
   */
  public void breakpointReached(boolean blocked) {
    HashSet<View> views;
    int		i;

    m_TextActorPath.setText(getCurrentActor().getFullName());
    m_TextStage.setText(getCurrentStage().toDisplay());

    if (getCurrentCondition() == null) {
      m_GOEPanelCondition.setEnabled(false);
    }
    else {
      m_GOEPanelCondition.setEnabled(true);
      m_GOEPanelCondition.setCurrent(getCurrentCondition().shallowCopy());
    }

    // combine watches
    if (getCurrentBreakpoint() != null) {
      for (i = 0; i < getCurrentBreakpoint().getWatches().length; i++) {
	m_PanelExpressions.addExpression(
	  getCurrentBreakpoint().getWatches()[i].getValue(),
	  getCurrentBreakpoint().getWatchTypes()[i]);
      }
    }
    m_PanelExpressions.refreshAllExpressions();

    if (m_PanelStorage != null)
      m_PanelStorage.setHandler(getCurrentActor().getStorageHandler());

    highlightActor();
    update(blocked);

    if (!m_Manual) {
      // combine views
      views = new HashSet<>(Arrays.asList(getOwner().getViews()));
      if (getCurrentBreakpoint() != null)
	views.addAll(Arrays.asList(getCurrentBreakpoint().getViews()));

      // show dialogs
      for (View d : views) {
	switch (d) {
	  case SOURCE:
	    m_ButtonSource.setSelected(true);
	    showSource(true);
	    break;
	  case EXPRESSIONS:
	    m_ButtonExpressions.setSelected(true);
	    showWatchExpressions(true);
	    break;
	  case INSPECT_TOKEN:
	    m_ButtonInspectToken.setSelected(getCurrentToken() != null);
	    inspectToken(getCurrentToken() != null);
	    break;
	  case STORAGE:
	    m_ButtonStorage.setSelected(true);
	    showStorage(true);
	    break;
	  case VARIABLES:
	    m_ButtonVariables.setSelected(true);
	    showVariables(true);
	    break;
	  case BREAKPOINTS:
	    m_ButtonBreakpoints.setSelected(true);
	    showBreakpoints(true);
	    break;
	  default:
	    throw new IllegalStateException("Unhandled dialog type: " + d);
	}
      }
    }
  }

  /**
   * Adds a new expression (if not already present).
   *
   * @param expr	the expression
   * @param type	the type of the expression
   * @see		ExpressionWatchPanel#addExpression(String, ExpressionType)
   * @return		true if added
   */
  public boolean addWatch(String expr, ExpressionType type) {
    return m_PanelExpressions.addExpression(expr, type);
  }

  /**
   * Checks whether the expression is already present.
   *
   * @param expr	the expression
   * @param type	the type of the expression
   * @return		true if already present
   * @see		ExpressionWatchPanel#hasExpression(String, ExpressionType)
   */
  public boolean hasWatch(String expr, ExpressionType type) {
    return m_PanelExpressions.hasExpression(expr, type);
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    if (m_PanelSource != null)
      m_PanelSource = null;
    if (m_PanelExpressions != null)
      m_PanelExpressions = null;
    if (m_PanelVariables != null)
      m_PanelVariables = null;
    if (m_PanelInspectionToken != null) {
      m_PanelInspectionToken.cleanUp();
      m_PanelInspectionToken = null;
    }
    if (m_PanelStorage != null) {
      m_PanelStorage.cleanUp();
      m_PanelStorage = null;
    }
    if (m_PanelBreakpoints != null) {
      m_PanelBreakpoints.cleanUp();
      m_PanelBreakpoints = null;
    }
  }
}
