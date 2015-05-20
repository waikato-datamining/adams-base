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
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.execution;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import adams.core.CleanUpHandler;
import adams.core.Variables;
import adams.core.base.BaseString;
import adams.core.option.DebugNestedProducer;
import adams.flow.condition.bool.BooleanCondition;
import adams.flow.condition.bool.BooleanConditionSupporter;
import adams.flow.control.Breakpoint;
import adams.flow.control.Breakpoint.View;
import adams.flow.control.Flow;
import adams.flow.core.Actor;
import adams.flow.core.Token;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseTabbedPane;
import adams.gui.core.GUIHelper;
import adams.gui.core.TextEditorPanel;
import adams.gui.flow.StoragePanel;
import adams.gui.goe.GenericObjectEditorPanel;
import adams.gui.tools.ExpressionWatchPanel;
import adams.gui.tools.ExpressionWatchPanel.ExpressionType;
import adams.gui.tools.VariableManagementPanel;
import adams.gui.visualization.debug.InspectionPanel;

/**
 <!-- globalinfo-start -->
 * Allows the user to define breakpoints that suspend the execution of the flow, allowing the inspection of the current flow state.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 * 
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-breakpoint &lt;adams.flow.execution.AbstractBreakpoint&gt; [-breakpoint ...] (property: breakpoints)
 * &nbsp;&nbsp;&nbsp;The breakpoints to use for suspending the flow execution.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Debug
  extends AbstractGraphicalFlowExecutionListener {

  /** for serialization. */
  private static final long serialVersionUID = -7287036923779341439L;

  /**
   * A little dialog for controlling the breakpoint. Cannot be static class!
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class ControlPanel
    extends BasePanel
    implements CleanUpHandler {

    /** for serialization. */
    private static final long serialVersionUID = 1000900663466801934L;
    
    /** the tabbed pane for displaying the various panels. */
    protected BaseTabbedPane m_TabbedPane;
    
    /** the button for continuing execution. */
    protected JToggleButton m_ButtonContinue;

    /** the button for stopping execution. */
    protected JToggleButton m_ButtonStop;

    /** the button for disabling/enabling the breakpoint. */
    protected JToggleButton m_ButtonDisableEnable;

    /** the button for displaying dialog with watch expressions. */
    protected JToggleButton m_ButtonExpressions;

    /** the panel with the conditon. */
    protected JPanel m_PanelCondition;

    /** the breakpoint condition. */
    protected GenericObjectEditorPanel m_GOEPanelCondition;

    /** the button to bring up the source code of the current flow. */
    protected JToggleButton m_ButtonSource;

    /** the button to bring up the variable management dialog. */
    protected JToggleButton m_ButtonVariables;

    /** the button to bring up the storage dialog. */
    protected JToggleButton m_ButtonStorage;

    /** the button to bring up the dialog for inspecting the current token. */
    protected JToggleButton m_ButtonInspectToken;

    /** the button to bring up the dialog for inspecting the flow. */
    protected JToggleButton m_ButtonInspectFlow;

    /** the panel with the watch expressions. */
    protected ExpressionWatchPanel m_PanelExpressions;

    /** the panel with the variables. */
    protected VariableManagementPanel m_PanelVariables;

    /** the panel for inspecting the current token. */
    protected InspectionPanel m_PanelInspectionToken;

    /** the panel for inspecting the current flow. */
    protected InspectionPanel m_PanelInspectionFlow;
    
    /** the panel for displaying the source. */
    protected TextEditorPanel m_PanelSource;

    /** the panel for displaying the temporary storage. */
    protected StoragePanel m_PanelStorage;

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
      m_TabbedPane = new BaseTabbedPane();
      add(m_TabbedPane, BorderLayout.CENTER);
      
      // buttons
      // 1. execution
      panelAllButtons = new JPanel(new BorderLayout());
      add(panelAllButtons, BorderLayout.NORTH);
      panel = new JPanel(new BorderLayout());
      panel.setBorder(BorderFactory.createTitledBorder("Execution"));
      panelAllButtons.add(panel, BorderLayout.NORTH);
      panelButtons = new JPanel(new GridLayout(1, 3));
      panel.add(panelButtons, BorderLayout.NORTH);

      m_ButtonContinue = new JToggleButton("Continue", GUIHelper.getIcon("run.gif"));
      m_ButtonContinue.setMnemonic('C');
      m_ButtonContinue.setToolTipText("Continues with the flow execution");
      m_ButtonContinue.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          continueFlowExecution();
        }
      });
      panelButtons.add(m_ButtonContinue);

      m_ButtonStop = new JToggleButton("Stop", GUIHelper.getIcon("stop.gif"));
      m_ButtonStop.setMnemonic('S');
      m_ButtonStop.setToolTipText("Stops the flow execution immediately");
      m_ButtonStop.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  stopFlowExecution();
	}
      });
      panelButtons.add(m_ButtonStop);

      m_ButtonDisableEnable = new JToggleButton("Disable", GUIHelper.getIcon("debug_off.png"));
      m_ButtonDisableEnable.setMnemonic('D');
      m_ButtonDisableEnable.setToolTipText("Disable the breakpoint");
      m_ButtonDisableEnable.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  disableEnableBreakpoint();
	}
      });
      panelButtons.add(m_ButtonDisableEnable);

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
	  showWatchExpressions(m_ButtonExpressions.isSelected());
        }
      });
      panelButtons.add(m_ButtonExpressions);

      m_ButtonVariables = new JToggleButton("Variables", GUIHelper.getIcon("variable.gif"));
      m_ButtonVariables.setMnemonic('V');
      m_ButtonVariables.setToolTipText("Display dialog with currently active variables");
      m_ButtonVariables.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          showVariables(m_ButtonVariables.isSelected());
        }
      });
      panelButtons.add(m_ButtonVariables);

      m_ButtonStorage = new JToggleButton("Storage", GUIHelper.getIcon("disk.png"));
      m_ButtonStorage.setMnemonic('t');
      m_ButtonStorage.setToolTipText("Display dialog with items currently stored in temporary storage");
      m_ButtonStorage.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          showStorage(m_ButtonStorage.isSelected());
        }
      });
      panelButtons.add(m_ButtonStorage);

      m_ButtonInspectToken = new JToggleButton("Inspect token", GUIHelper.getIcon("properties.gif"));
      m_ButtonInspectToken.setMnemonic('I');
      m_ButtonInspectToken.setToolTipText("Display dialog for inspecting the current token");
      m_ButtonInspectToken.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          inspectToken(m_ButtonInspectToken.isSelected());
        }
      });
      panelButtons.add(m_ButtonInspectToken);

      m_ButtonInspectFlow = new JToggleButton("Inspect flow", GUIHelper.getIcon("flow.gif"));
      m_ButtonInspectFlow.setMnemonic('n');
      m_ButtonInspectFlow.setToolTipText("Display dialog for inspecting the current flow");
      m_ButtonInspectFlow.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          inspectFlow(m_ButtonInspectFlow.isSelected());
        }
      });
      panelButtons.add(m_ButtonInspectFlow);

      m_ButtonSource = new JToggleButton("Source", GUIHelper.getIcon("source.png"));
      m_ButtonSource.setMnemonic('o');
      m_ButtonSource.setToolTipText("Display current flow state as source (nested format)");
      m_ButtonSource.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
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
     * Updates the enabled status of the buttons, text fields.
     */
    public void update() {
      boolean	actorPresent;
      boolean	stopped;
      boolean	blocked;

      actorPresent = (getCurrentActor() != null);
      stopped      = actorPresent && getFlow().isStopped();
      blocked      = (getOwner() != null) && getOwner().isBlocked();

      m_ButtonContinue.setEnabled(actorPresent && !stopped && blocked);
      m_ButtonStop.setEnabled(actorPresent && !stopped);
      m_ButtonDisableEnable.setEnabled(actorPresent && !stopped);
      m_ButtonVariables.setEnabled(actorPresent);
      m_ButtonStorage.setEnabled(actorPresent);
      m_ButtonExpressions.setEnabled(actorPresent && !stopped && blocked);
      m_ButtonInspectFlow.setEnabled(actorPresent && !stopped && blocked);
      m_ButtonInspectToken.setEnabled(actorPresent && !stopped && blocked);
      m_ButtonSource.setEnabled(actorPresent && !stopped && blocked);
      
      m_PanelCondition.setEnabled(actorPresent && !stopped && blocked);
      m_ButtonActorPath.setEnabled(m_TextActorPath.getText().length() > 0);
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
     * Disable/enable the breakpoint.
     */
    protected void disableEnableBreakpoint() {
      // cannot use setSkip(boolean) as this will reset the actor
      // and remove the control panel
      getOwner().setDisabled(!getOwner().isDisabled());
      if (getOwner().isDisabled()) {
	m_ButtonDisableEnable.setText("Enable");
	m_ButtonDisableEnable.setMnemonic('E');
	m_ButtonDisableEnable.setToolTipText("Enable the breakpoint");
	m_ButtonDisableEnable.setIcon(GUIHelper.getIcon("debug.png"));
      }
      else {
	m_ButtonDisableEnable.setText("Disable");
	m_ButtonDisableEnable.setMnemonic('D');
	m_ButtonDisableEnable.setToolTipText("Disable the breakpoint");
	m_ButtonDisableEnable.setIcon(GUIHelper.getIcon("debug_off.png"));
      }

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
      for (i = 0; i < m_TabbedPane.getTabCount(); i++) {
	if (m_TabbedPane.getComponentAt(i) == panel) {
	  present = i;
	  break;
	}
      }
      
      // no change?
      if ((visible && (present > -1)) || (!visible && (present == -1)))
	return;
      
      if (visible) {
	m_TabbedPane.addTab(title, panel);
	m_TabbedPane.setSelectedIndex(m_TabbedPane.getTabCount() - 1);
      }
      else {
	m_TabbedPane.remove(present);
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
      DebugNestedProducer	producer;

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
      if (m_PanelInspectionToken == null)
	m_PanelInspectionToken = new InspectionPanel();
      m_PanelInspectionToken.setCurrent(m_CurrentToken);
      setPanelVisible(m_PanelInspectionToken, "Token", visible);
    }

    /**
     * Inspects the current flow.
     * 
     * @param visible	if true then displayed, otherwise hidden
     */
    protected void inspectFlow(boolean visible) {
      if (m_PanelInspectionFlow == null)
	m_PanelInspectionFlow = new InspectionPanel();
      m_PanelInspectionFlow.setCurrent(getCurrentActor().getRoot());
      setPanelVisible(m_PanelInspectionFlow, "Flow", visible);
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
    protected void showFrame() {
      if (getParentDialog() != null)
        getParentDialog().setVisible(true);
      else if (getParentFrame() != null)
        getParentFrame().setVisible(true);
      // TODO make tab visible if inside tabbedpane?
    }

    /**
     * Called by actor when breakpoint reached.
     */
    public void breakpointReached() {
      m_TextActorPath.setText(getCurrentActor().getFullName());
      m_TextHookMethod.setText(getCurrentHook());
      if (getCurrentCondition() == null) {
	m_PanelCondition.setVisible(false);
      }
      else {
	m_GOEPanelCondition.setCurrent(getCurrentCondition().shallowCopy());
	m_PanelCondition.setVisible(true);
      }
      m_PanelExpressions.refreshAllExpressions();
      if (m_PanelInspectionToken != null)
	m_PanelInspectionToken.setCurrent(m_CurrentToken);
      if (m_PanelInspectionFlow != null)
	m_PanelInspectionFlow.setCurrent(getCurrentActor().getRoot());
      if (m_PanelStorage != null)
	m_PanelStorage.setHandler(getCurrentActor().getStorageHandler());

      update();

      // show dialogs
      for (View d: getOwner().getViews()) {
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
	    m_ButtonInspectToken.setSelected(true);
	    inspectToken(true);
	    break;
	  case INSPECT_FLOW:
	    m_ButtonInspectFlow.setSelected(true);
	    inspectFlow(true);
	    break;
	  case STORAGE:
	    m_ButtonStorage.setSelected(true);
	    showStorage(true);
	    break;
	  case VARIABLES:
	    m_ButtonVariables.setSelected(true);
	    showVariables(true);
	    break;
	  default:
	    throw new IllegalStateException("Unhandled dialog type: " + d);
	}
      }
    }

    /**
     * Adds a new expression.
     *
     * @param expr	the expression
     * @param type	the type of the expression
     * @see		ExpressionWatchPanel#addExpression(String, ExpressionType)
     */
    public void addWatch(String expr, ExpressionType type) {
      m_PanelExpressions.addExpression(expr, type);
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
      if (m_PanelInspectionFlow != null)
	m_PanelInspectionFlow = null;
      if (m_PanelStorage != null) {
	m_PanelStorage.cleanUp();
	m_PanelStorage = null;
      }
    }
  }
  
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

  /** whether debugging has been disabled. */
  protected boolean m_Disabled;
  
  /** debug panel. */
  protected transient ControlPanel m_DebugPanel;
  
  /** whether the GUI currently blocks the flow execution. */
  protected boolean m_Blocked;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Allows the user to define breakpoints that suspend the execution "
	+ "of the flow, allowing the inspection of the current flow state.";
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
  }

  /**
   * Returns the default width for the dialog.
   *
   * @return		the default width
   */
  protected int getDefaultWidth() {
    return 800;
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
   * Sets whether listening has been disabled.
   * 
   * @param value	if true then disabled
   */
  public void setDisabled(boolean value) {
    m_Disabled = value;
  }
  
  /**
   * Returns whether listening has been disabled.
   * 
   * @return		true if disabled
   */
  public boolean isDisabled() {
    return m_Disabled || (m_DebugPanel == null);
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
  protected void blockExecution() {
    m_Blocked = true;
    m_DebugPanel.update();
    while (m_Blocked && !m_DebugPanel.getCurrentActor().isStopped()) {
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
  protected void unblockExecution() {
    m_Blocked = false;
  }
  
  /**
   * Suspends the flow execution.
   * 
   * @param point	the breakpoint that triggered the suspend
   * @param actor	the current actor
   * @param hook	the hook method (eg preInput)
   */
  protected void triggered(AbstractBreakpoint point, Actor actor, String hook) {
    if (isDisabled())
      return;
    
    if (isLoggingEnabled())
      getLogger().info(point.getClass().getName() + "/" + hook + ": " + actor.getFullName());
    
    m_DebugPanel.setCurrentHook(hook);
    m_DebugPanel.setCurrentActor(actor);
    m_DebugPanel.setCurrentToken(null);
    if (point instanceof BooleanConditionSupporter)
      m_DebugPanel.setCurrentCondition(((BooleanConditionSupporter) point).getCondition());
    else
      m_DebugPanel.setCurrentCondition(null);
    m_DebugPanel.showFrame();
    m_DebugPanel.breakpointReached();
    
    blockExecution();
  }
  
  /**
   * Suspends the flow execution.
   * 
   * @param point	the breakpoint that triggered the suspend
   * @param actor	the current actor
   * @param hook	the hook method (eg preInput)
   * @param token	the current token
   */
  protected void triggered(AbstractBreakpoint point, Actor actor, String hook, Token token) {
    if (isDisabled())
      return;
    
    if (isLoggingEnabled())
      getLogger().info(point.getClass().getName() + "/" + hook + ": " + actor.getFullName() + "\n\t" + token);

    m_DebugPanel.setCurrentHook(hook);
    m_DebugPanel.setCurrentActor(actor);
    m_DebugPanel.setCurrentToken(token);
    if (point instanceof BooleanConditionSupporter)
      m_DebugPanel.setCurrentCondition(((BooleanConditionSupporter) point).getCondition());
    else
      m_DebugPanel.setCurrentCondition(null);
    m_DebugPanel.showFrame();
    m_DebugPanel.breakpointReached();
    
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
	triggered(point, actor, "preInput", token);
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
	triggered(point, actor, "postInput");
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
    for (AbstractBreakpoint point: m_Breakpoints) {
      if (!point.getDisabled() && point.triggersPreExecute(actor)) {
	triggered(point, actor, "preExecute");
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
	triggered(point, actor, "postExecute");
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
	triggered(point, actor, "preOutput");
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
	triggered(point, actor, "postOutput");
	break;
      }
    }
  }
}
