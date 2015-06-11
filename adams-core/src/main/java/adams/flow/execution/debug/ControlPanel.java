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
 * ControlPanel.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.flow.execution.debug;

import adams.core.CleanUpHandler;
import adams.core.option.DebugNestedProducer;
import adams.flow.condition.bool.BooleanCondition;
import adams.flow.control.Breakpoint;
import adams.flow.control.Flow;
import adams.flow.core.Actor;
import adams.flow.core.Token;
import adams.flow.execution.AbstractBreakpoint;
import adams.flow.execution.Debug;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseTabbedPane;
import adams.gui.core.GUIHelper;
import adams.gui.core.TextEditorPanel;
import adams.gui.flow.FlowPanel;
import adams.gui.flow.FlowTreeHandler;
import adams.gui.flow.StoragePanel;
import adams.gui.flow.tree.Tree;
import adams.gui.goe.FlowHelper;
import adams.gui.goe.GenericObjectEditorPanel;
import adams.gui.tools.ExpressionWatchPanel;
import adams.gui.tools.ExpressionWatchPanel.ExpressionType;
import adams.gui.tools.VariableManagementPanel;
import adams.gui.visualization.debug.InspectionPanel;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.HashSet;

/**
 * A little dialog for controlling the breakpoint. Cannot be static class!
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 10983 $
 */
public class ControlPanel
  extends BasePanel
  implements CleanUpHandler, FlowTreeHandler {

  /** for serialization. */
  private static final long serialVersionUID = 1000900663466801934L;

  /** the tabbed pane for displaying the various display panels. */
  protected BaseTabbedPane m_TabbedPaneDisplays;

  /** the button for stopping execution. */
  protected JButton m_ButtonStop;

  /** the button for disabling/enabling the breakpoint. */
  protected JButton m_ButtonToggle;

  /** the button for resuming execution. */
  protected JButton m_ButtonPauseResume;

  /** the button for performing the next step when in manual mode. */
  protected JButton m_ButtonStep;

  /** the button for displaying dialog with watch expressions. */
  protected JToggleButton m_ButtonExpressions;

  /** the panel with the conditon. */
  protected JPanel m_PanelCondition;

  /** the breakpoint condition. */
  protected GenericObjectEditorPanel m_GOEPanelCondition;

  /** the button to show the source code of the current flow. */
  protected JToggleButton m_ButtonSource;

  /** the button to show the variable management dialog. */
  protected JToggleButton m_ButtonVariables;

  /** the button to show the storage. */
  protected JToggleButton m_ButtonStorage;

  /** the button to show inspection panel for the current token. */
  protected JToggleButton m_ButtonInspectToken;

  /** the button to show the breakpoints management panel. */
  protected JToggleButton m_ButtonBreakpoints;

  /** the panel with the watch expressions. */
  protected ExpressionWatchPanel m_PanelExpressions;

  /** the panel with the variables. */
  protected VariableManagementPanel m_PanelVariables;

  /** the panel for inspecting the current token. */
  protected InspectionPanel m_PanelInspectionToken;

  /** the panel for displaying the source. */
  protected TextEditorPanel m_PanelSource;

  /** the panel for displaying the temporary storage. */
  protected StoragePanel m_PanelStorage;

  /** the panel for managing the breakpoints. */
  protected BreakpointPanel m_PanelBreakpoints;

  /** the text field for the actor path. */
  protected JTextField m_TextActorPath;

  /** the button for copying the actor path. */
  protected JToggleButton m_ButtonActorPath;

  /** the text field for the hook method. */
  protected JTextField m_TextHookMethod;

  /** the owning listener. */
  protected transient Debug m_Owner;

  /** the current token. */
  protected Token m_CurrentToken;

  /** the current actor. */
  protected Actor m_CurrentActor;

  /** the current hook method. */
  protected String m_CurrentHook;

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
    m_CurrentHook       = null;
    m_CurrentToken      = null;
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    JPanel	panelButtons;
    JPanel	panelAllButtons;
    JPanel	panel;

    super.initGUI();

    setLayout(new BorderLayout());

    // tabbed pane
    m_TabbedPaneDisplays = new BaseTabbedPane();
    add(m_TabbedPaneDisplays, BorderLayout.CENTER);

    // buttons
    // 1. execution
    panelAllButtons = new JPanel(new BorderLayout());
    add(panelAllButtons, BorderLayout.NORTH);
    panel = new JPanel(new BorderLayout());
    panel.setBorder(BorderFactory.createTitledBorder("Execution"));
    panelAllButtons.add(panel, BorderLayout.NORTH);
    panelButtons = new JPanel(new GridLayout(1, 4));
    panel.add(panelButtons, BorderLayout.NORTH);

    m_ButtonPauseResume = new JButton("Resume", GUIHelper.getIcon("resume.gif"));
    m_ButtonPauseResume.setToolTipText("Pause/Resume execution");
    m_ButtonPauseResume.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	pauseResumeExecution();
      }
    });
    panelButtons.add(m_ButtonPauseResume);

    m_ButtonStep = new JButton("Step", GUIHelper.getIcon("step.gif"));
    m_ButtonStep.setToolTipText("Step to next actor");
    m_ButtonStep.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	nextStep();
      }
    });
    panelButtons.add(m_ButtonStep);

    m_ButtonToggle = new JButton("Disable", GUIHelper.getIcon("debug_off.png"));
    m_ButtonToggle.setMnemonic('D');
    m_ButtonToggle.setToolTipText("Disable the breakpoint");
    m_ButtonToggle.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	disableEnableBreakpoint();
      }
    });
    panelButtons.add(m_ButtonToggle);

    m_ButtonStop = new JButton("Stop", GUIHelper.getIcon("stop.gif"));
    m_ButtonStop.setMnemonic('S');
    m_ButtonStop.setToolTipText("Stops the flow execution immediately");
    m_ButtonStop.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	stopFlowExecution();
      }
    });
    panelButtons.add(m_ButtonStop);

    // condition
    m_PanelCondition = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel.add(m_PanelCondition, BorderLayout.CENTER);
    m_GOEPanelCondition = new GenericObjectEditorPanel(BooleanCondition.class, new Breakpoint().getCondition(), false);
    m_GOEPanelCondition.setEditable(false);
    m_GOEPanelCondition.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
	update();
      }
    });
    m_PanelCondition.add(new JLabel("Condition"));
    m_PanelCondition.add(m_GOEPanelCondition);

    // 2. runtime information
    panelButtons = new JPanel(new GridLayout(2, 3));
    panelButtons.setBorder(BorderFactory.createTitledBorder("Runtime information"));
    panel = new JPanel(new BorderLayout());
    panel.add(panelButtons, BorderLayout.NORTH);
    panelAllButtons.add(panel, BorderLayout.CENTER);

    m_ButtonExpressions = new JToggleButton("Expressions", GUIHelper.getIcon("glasses.gif"));
    m_ButtonExpressions.setMnemonic('x');
    m_ButtonExpressions.setToolTipText("Display dialog for watch expressions");
    m_ButtonExpressions.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	m_Manual = true;
	showWatchExpressions(m_ButtonExpressions.isSelected());
      }
    });
    panelButtons.add(m_ButtonExpressions);

    m_ButtonVariables = new JToggleButton("Variables", GUIHelper.getIcon("variable.gif"));
    m_ButtonVariables.setMnemonic('V');
    m_ButtonVariables.setToolTipText("Display dialog with currently active variables");
    m_ButtonVariables.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	m_Manual = true;
	showVariables(m_ButtonVariables.isSelected());
      }
    });
    panelButtons.add(m_ButtonVariables);

    m_ButtonStorage = new JToggleButton("Storage", GUIHelper.getIcon("disk.png"));
    m_ButtonStorage.setMnemonic('t');
    m_ButtonStorage.setToolTipText("Display dialog with items currently stored in temporary storage");
    m_ButtonStorage.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	m_Manual = true;
	showStorage(m_ButtonStorage.isSelected());
      }
    });
    panelButtons.add(m_ButtonStorage);

    m_ButtonInspectToken = new JToggleButton("Inspect token", GUIHelper.getIcon("properties.gif"));
    m_ButtonInspectToken.setMnemonic('I');
    m_ButtonInspectToken.setToolTipText("Display dialog for inspecting the current token");
    m_ButtonInspectToken.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	m_Manual = true;
	inspectToken(m_ButtonInspectToken.isSelected());
      }
    });
    panelButtons.add(m_ButtonInspectToken);

    m_ButtonBreakpoints = new JToggleButton("Breakpoints", GUIHelper.getIcon("flow.gif"));
    m_ButtonBreakpoints.setMnemonic('n');
    m_ButtonBreakpoints.setToolTipText("Display dialog for inspecting the current flow");
    m_ButtonBreakpoints.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	m_Manual = true;
	showBreakpoints(m_ButtonBreakpoints.isSelected());
      }
    });
    panelButtons.add(m_ButtonBreakpoints);

    m_ButtonSource = new JToggleButton("Source", GUIHelper.getIcon("source.png"));
    m_ButtonSource.setMnemonic('o');
    m_ButtonSource.setToolTipText("Display current flow state as source (nested format)");
    m_ButtonSource.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	m_Manual = true;
	showSource(m_ButtonSource.isSelected());
      }
    });
    panelButtons.add(m_ButtonSource);

    // the path to the breakpoint
    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    m_TextActorPath = new JTextField(30);
    m_TextActorPath.setEditable(false);
    m_ButtonActorPath = new JToggleButton(GUIHelper.getIcon("copy.gif"));
    m_ButtonActorPath.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	GUIHelper.copyToClipboard(m_TextActorPath.getText());
      }
    });
    m_TextHookMethod = new JTextField(15);
    m_TextHookMethod.setEditable(false);
    panel.add(new JLabel("Actor path"));
    panel.add(m_TextActorPath);
    panel.add(m_ButtonActorPath);
    panel.add(m_TextHookMethod);
    panelAllButtons.add(panel, BorderLayout.SOUTH);

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

    m_PanelCondition.setEnabled(actorPresent && !stopped && blocked);
    m_GOEPanelCondition.setEnabled(m_PanelCondition.isEnabled());
    m_ButtonActorPath.setEnabled(m_TextActorPath.getText().length() > 0);

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
	m_ButtonToggle.setText("Enable");
	m_ButtonToggle.setMnemonic('E');
	m_ButtonToggle.setToolTipText("Enable current breakpoint");
	m_ButtonToggle.setIcon(GUIHelper.getIcon("debug.png"));
      }
      else {
	m_ButtonToggle.setText("Disable");
	m_ButtonToggle.setMnemonic('D');
	m_ButtonToggle.setToolTipText("Disable current breakpoint");
	m_ButtonToggle.setIcon(GUIHelper.getIcon("debug_off.png"));
      }
    }

    if (m_Owner != null) {
      if (m_Owner.isBlocked() || m_Owner.isStepMode()) {
	m_ButtonPauseResume.setText("Resume");
	m_ButtonPauseResume.setIcon(GUIHelper.getIcon("resume.gif"));
      }
      else {
	m_ButtonPauseResume.setText("Pause");
	m_ButtonPauseResume.setIcon(GUIHelper.getIcon("pause.gif"));
      }
      m_PanelBreakpoints.setIgnoreUpdates(true);
      m_PanelBreakpoints.setOwner(this);
      m_PanelBreakpoints.setIgnoreUpdates(false);
    }
  }

  /**
   * Queues a call to {@link #update()} in the swing thread.
   */
  public void queueUpdate() {
    Runnable	run;

    run = new Runnable() {
      @Override
      public void run() {
	update();
      }
    };
    SwingUtilities.invokeLater(run);
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
   * Sets the current hook method.
   *
   * @param value	the method
   */
  public void setCurrentHook(String value) {
    m_CurrentHook = value;
  }

  /**
   * Returns the current hook method.
   *
   * @return		the method
   */
  public String getCurrentHook() {
    return m_CurrentHook;
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
	return result;
      if (flow.getParentComponent() != null) {
	if (flow.getParentComponent() instanceof FlowPanel)
	  result = ((FlowPanel) flow.getParentComponent()).getTree();
	else if (flow.getParentComponent() instanceof Container)
	  result = FlowHelper.getTree((Container) flow.getParentComponent());
	else if (flow.getParentComponent() instanceof FlowTreeHandler)
	  result = ((FlowTreeHandler) flow.getParentComponent()).getTree();
      }
    }

    return result;
  }

  /**
   * Continues the flow execution.
   */
  protected void continueFlowExecution() {
    getOwner().unblockExecution();
    queueUpdate();
  }

  /**
   * Stops the flow execution.
   */
  protected void stopFlowExecution() {
    if (getParentDialog() != null)
      getParentDialog().setVisible(false);
    else if (getParentFrame() != null)
      getParentFrame().setVisible(false);

    getFlow().stopExecution("User stopped flow!");
    getOwner().unblockExecution();
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
    if (m_PanelBreakpoints != null)
      return m_PanelBreakpoints.isStepModeEnabled();
    else
      return false;
  }

  /**
   * Disable/enable the breakpoint.
   */
  protected void disableEnableBreakpoint() {
    if (getCurrentBreakpoint() == null)
      return;

    getCurrentBreakpoint().setDisabled(!getCurrentBreakpoint().getDisabled());

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
    String			content;
    DebugNestedProducer producer;

    if (m_PanelSource == null) {
      m_PanelSource = new TextEditorPanel();
      m_PanelSource.setTextFont(GUIHelper.getMonospacedFont());
      m_PanelSource.setEditable(false);
      m_PanelSource.setTabSize(2);
    }

    if (visible) {
      producer = new DebugNestedProducer();
      producer.produce(getFlow());
      content = producer.toString();
      producer.cleanUp();
    }
    else {
      content = "";
    }

    m_PanelSource.setContent(content);
    setPanelVisible(m_PanelSource, "Source", visible);
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
   * Ensures that the frame is visible.
   */
  public void showFrame() {
    if (getParentDialog() != null)
      getParentDialog().setVisible(true);
    else if (getParentFrame() != null)
      getParentFrame().setVisible(true);
    // TODO make tab visible if inside tabbedpane?
  }

  /**
   * Highlights the actor.
   */
  protected void highlightActor() {
    Component comp;
    Tree	tree;
    Runnable	run;

    if (getFlow().getParentComponent() == null)
      return;
    comp = getFlow().getParentComponent();
    if (!(comp instanceof FlowPanel))
      return;

    tree = ((FlowPanel) comp).getTree();
    run = new Runnable() {
      @Override
      public void run() {
	String full = m_CurrentActor.getFullName();
	tree.locateAndDisplay(full);
      }
    };
    SwingUtilities.invokeLater(run);
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
    m_TextHookMethod.setText(getCurrentHook());

    if (getCurrentCondition() == null) {
      m_PanelCondition.setEnabled(false);
      m_GOEPanelCondition.setEnabled(false);
    }
    else {
      m_PanelCondition.setEnabled(true);
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
    if (m_PanelInspectionToken != null)
      m_PanelInspectionToken = null;
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
