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
 * Copyright (C) 2011-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import adams.core.CleanUpHandler;
import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.Variables;
import adams.core.base.BaseString;
import adams.core.option.DebugNestedProducer;
import adams.flow.condition.bool.BooleanCondition;
import adams.flow.condition.bool.BooleanConditionSupporter;
import adams.flow.condition.bool.Expression;
import adams.flow.core.ActorUtils;
import adams.flow.core.ControlActor;
import adams.flow.core.Token;
import adams.flow.core.Unknown;
import adams.flow.transformer.AbstractTransformer;
import adams.gui.core.BaseDialog;
import adams.gui.core.BaseFrame;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseTabbedPane;
import adams.gui.core.GUIHelper;
import adams.gui.core.MenuBarProvider;
import adams.gui.core.TextEditorPanel;
import adams.gui.flow.FlowPanel;
import adams.gui.flow.StoragePanel;
import adams.gui.goe.GenericObjectEditorPanel;
import adams.gui.tools.ExpressionWatchPanel;
import adams.gui.tools.ExpressionWatchPanel.ExpressionType;
import adams.gui.tools.VariableManagementPanel;
import adams.gui.visualization.debug.InspectionPanel;
import adams.parser.BooleanExpression;

/**
 <!-- globalinfo-start -->
 * Allows to pause the execution of the flow when this actor is reached and the condition evaluates to 'true'.<br>
 * It is possible to define watches as well.<br>
 * <br>
 * The expression has the following underlying grammar:<br>
 * expr_list ::= '=' expr_list expr_part | expr_part ;<br>
 * expr_part ::=  expr ;<br>
 * <br>
 * expr      ::=   ( expr )<br>
 * <br>
 * # data types<br>
 *               | number<br>
 *               | string<br>
 *               | boolean<br>
 *               | date<br>
 * <br>
 * # constants<br>
 *               | true<br>
 *               | false<br>
 *               | pi<br>
 *               | e<br>
 *               | now()<br>
 *               | today()<br>
 * <br>
 * # negating numeric value<br>
 *               | -expr<br>
 * <br>
 * # comparisons<br>
 *               | expr &lt; expr<br>
 *               | expr &lt;= expr<br>
 *               | expr &gt; expr<br>
 *               | expr &gt;= expr<br>
 *               | expr = expr<br>
 *               | expr != expr (or: expr &lt;&gt; expr)<br>
 * <br>
 * # boolean operations<br>
 *               | ! expr (or: not expr)<br>
 *               | expr &amp; expr (or: expr and expr)<br>
 *               | expr | expr (or: expr or expr)<br>
 *               | if[else] ( expr , expr (if true) , expr (if false) )<br>
 *               | ifmissing ( variable , expr (default value if variable is missing) )<br>
 *               | isNaN ( expr )<br>
 * <br>
 * # arithmetics<br>
 *               | expr + expr<br>
 *               | expr - expr<br>
 *               | expr * expr<br>
 *               | expr &#47; expr<br>
 *               | expr ^ expr (power of)<br>
 *               | expr % expr (modulo)<br>
 *               ;<br>
 * <br>
 * # numeric functions<br>
 *               | abs ( expr )<br>
 *               | sqrt ( expr )<br>
 *               | log ( expr )<br>
 *               | exp ( expr )<br>
 *               | sin ( expr )<br>
 *               | cos ( expr )<br>
 *               | tan ( expr )<br>
 *               | rint ( expr )<br>
 *               | floor ( expr )<br>
 *               | pow[er] ( expr , expr )<br>
 *               | ceil ( expr )<br>
 *               | year ( expr )<br>
 *               | month ( expr )<br>
 *               | day ( expr )<br>
 *               | hour ( expr )<br>
 *               | minute ( expr )<br>
 *               | second ( expr )<br>
 *               | weekday ( expr )<br>
 *               | weeknum ( expr )<br>
 * <br>
 * # string functions<br>
 *               | substr ( expr , start [, end] )<br>
 *               | left ( expr , len )<br>
 *               | mid ( expr , start , len )<br>
 *               | right ( expr , len )<br>
 *               | rept ( expr , count )<br>
 *               | concatenate ( expr1 , expr2 [, expr3-5] )<br>
 *               | lower[case] ( expr )<br>
 *               | upper[case] ( expr )<br>
 *               | trim ( expr )<br>
 *               | matches ( expr , regexp )<br>
 *               | trim ( expr )<br>
 *               | len[gth] ( str )<br>
 *               | find ( search , expr [, pos] )<br>
 *               | replace ( str , pos , len , newstr )<br>
 *               | substitute ( str , find , replace [, occurrences] )<br>
 *               ;<br>
 * <br>
 * Notes:<br>
 * - Variables are either all upper case letters (e.g., "ABC") or any character   apart from "]" enclosed by "[" and "]" (e.g., "[Hello World]").<br>
 * - 'start' and 'end' for function 'substr' are indices that start at 1.<br>
 * - Index 'end' for function 'substr' is excluded (like Java's 'String.substring(int,int)' method)<br>
 * - Line comments start with '#'<br>
 * - Semi-colons (';') or commas (',') can be used as separator in the formulas,<br>
 *   e.g., 'pow(2,2)' is equivalent to 'pow(2;2)'<br>
 * - dates have to be of format 'yyyy-MM-dd' or 'yyyy-MM-dd HH:mm:ss'<br>
 * - times have to be of format 'HH:mm:ss' or 'yyyy-MM-dd HH:mm:ss'<br>
 * - the characters in square brackets in function names are optional:<br>
 *   e.g. 'len("abc")' is the same as 'length("abc")'<br>
 * <br>
 * A lot of the functions have been modeled after LibreOffice:<br>
 *   https:&#47;&#47;help.libreoffice.org&#47;Calc&#47;Functions_by_Category<br>
 * <br>
 * Additional functions:<br>
 * - env(String): String<br>
 * &nbsp;&nbsp;&nbsp;First argument is the name of the environment variable to retrieve.<br>
 * &nbsp;&nbsp;&nbsp;The result is the value of the environment variable.<br>
 * <br>
 * Additional procedures:<br>
 * - println(...)<br>
 * &nbsp;&nbsp;&nbsp;One or more arguments are printed as comma-separated list to stdout.<br>
 * &nbsp;&nbsp;&nbsp;If no argument is provided, a simple line feed is output.<br>
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
 * <pre>-display-in-editor &lt;boolean&gt; (property: displayInEditor)
 * &nbsp;&nbsp;&nbsp;If enabled displays the panel in a tab in the flow editor rather than in 
 * &nbsp;&nbsp;&nbsp;a separate frame.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-x &lt;int&gt; (property: x)
 * &nbsp;&nbsp;&nbsp;The X position of the dialog (&gt;=0: absolute, -1: left, -2: center, -3: right
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: -3
 * &nbsp;&nbsp;&nbsp;minimum: -3
 * </pre>
 * 
 * <pre>-y &lt;int&gt; (property: y)
 * &nbsp;&nbsp;&nbsp;The Y position of the dialog (&gt;=0: absolute, -1: top, -2: center, -3: bottom
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -3
 * </pre>
 * 
 * <pre>-width &lt;int&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;The width of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 800
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-height &lt;int&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 800
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-condition &lt;adams.flow.condition.bool.BooleanCondition&gt; (property: condition)
 * &nbsp;&nbsp;&nbsp;The condition to evaluate; if the condition evaluates to 'true', the execution 
 * &nbsp;&nbsp;&nbsp;of the flow is paused and a control window is opened.
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
 * <pre>-view &lt;SOURCE|EXPRESSIONS|VARIABLES|STORAGE|INSPECT_TOKEN|INSPECT_FLOW&gt; [-view ...] (property: views)
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

  /**
   * A little dialog for controlling the breakpoint. Cannot be static class!
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public class ControlPanel
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

    /** the breakpoint condition. */
    protected GenericObjectEditorPanel m_PanelCondition;

    /** the button for updating the breakpoing condition. */
    protected JToggleButton m_ButtonConditionUpdate;

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

    /** whether the stop button is enabled. */
    protected boolean m_StopButtonEnabled;

    /**
     * Initializes the widgets.
     */
    @Override
    protected void initGUI() {
      JPanel	panelCondition;
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
      panelCondition = new JPanel(new FlowLayout(FlowLayout.LEFT));
      panel.add(panelCondition, BorderLayout.CENTER);

      m_PanelCondition = new GenericObjectEditorPanel(BooleanCondition.class, new Breakpoint().getCondition(), true);
      m_PanelCondition.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          update();
        }
      });
      m_ButtonConditionUpdate = new JToggleButton("Update");
      m_ButtonConditionUpdate.setToolTipText("Update the breakpoint condition");
      m_ButtonConditionUpdate.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          updateCondition();
        }
      });
      panelCondition.add(new JLabel("Condition"));
      panelCondition.add(m_PanelCondition);
      panelCondition.add(m_ButtonConditionUpdate);

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
      panel.add(new JLabel("Actor path"));
      panel.add(m_TextActorPath);
      panel.add(m_ButtonActorPath);
      panelAllButtons.add(panel, BorderLayout.SOUTH);
      
      // watches
      m_PanelExpressions = new ExpressionWatchPanel();
    }

    /**
     * Sets whether the stop button is enabled.
     * 
     * @param value	true if the stop button should be enabled
     */
    public void setStopButtonEnabled(boolean value) {
      m_StopButtonEnabled = value;
      m_ButtonStop.setEnabled(value);
    }
    
    /**
     * Returns whether the stop button is enabled.
     * 
     * @return		true if the stop button is enabled
     */
    public boolean isStopButtonEnabled() {
      return m_StopButtonEnabled;
    }
    
    /**
     * Returns the underlying flow.
     *
     * @return		the flow
     */
    protected Flow getFlow() {
      return (Flow) getRoot();
    }

    /**
     * Updates the enabled status of the buttons, text fields.
     */
    protected void update() {
      Flow	flow;

      flow = getFlow();

      m_ButtonContinue.setEnabled(flow.isPaused());
      m_ButtonStop.setEnabled(!flow.isStopped() && m_StopButtonEnabled);
      m_ButtonDisableEnable.setEnabled(!flow.isStopped());
      m_ButtonExpressions.setEnabled(flow.isPaused());
      m_ButtonInspectFlow.setEnabled(flow.isPaused());
      m_ButtonInspectToken.setEnabled(flow.isPaused());
      m_ButtonSource.setEnabled(flow.isPaused());
      m_ButtonConditionUpdate.setEnabled(flow.isPaused());
    }

    /**
     * Continues the flow execution.
     */
    protected void continueFlowExecution() {
      BooleanCondition	current;
      
      // breakpoint condition updated?
      current = (BooleanCondition) m_PanelCondition.getCurrent();
      if (!current.equals(getCondition()))
	m_Condition = current.shallowCopy();
      m_OutputToken = m_CurrentToken;
      synchronized(m_Self) {
	try {
	  m_Self.notifyAll();
	}
	catch (Exception e) {
	  // ignored
	}
      }
      getFlow().resumeExecution();
      update();
    }

    /**
     * Stops the flow execution.
     */
    protected void stopFlowExecution() {
      Runnable	runnable;

      if (!m_DisplayInEditor) {
	if (getParentDialog() != null)
	  getParentDialog().setVisible(false);
	else if (getParentFrame() != null)
	  getParentFrame().setVisible(false);
      }

      runnable = new Runnable() {
	public void run() {
	  getFlow().stopExecution("User stopped flow!");
	}
      };
      SwingUtilities.invokeLater(runnable);
    }

    /**
     * Disable/enable the breakpoint.
     */
    protected void disableEnableBreakpoint() {
      Flow	flow;

      // cannot use setSkip(boolean) as this will reset the actor
      // and remove the control panel
      setDisabled(!isDisabled());
      if (isDisabled()) {
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

      flow = getFlow();
      if (flow.isPaused()) {
	m_OutputToken = m_CurrentToken;
	flow.resumeExecution();
      }

      update();
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
      m_PanelExpressions.setVariables(getVariables());
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
      m_PanelVariables.setVariables(getVariables());
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
      m_PanelInspectionFlow.setCurrent(m_Self.getRoot());
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
      m_PanelStorage.setHandler(getStorageHandler());
    }

    /**
     * Called by actor when breakpoint reached.
     */
    public void breakpointReached() {
      m_TextActorPath.setText(getFullName());
      m_PanelExpressions.refreshAllExpressions();
      m_PanelCondition.setCurrent(getCondition().shallowCopy());
      if (m_PanelInspectionToken != null)
	m_PanelInspectionToken.setCurrent(m_CurrentToken);
      if (m_PanelInspectionFlow != null)
	m_PanelInspectionFlow.setCurrent(m_Self.getRoot());
      if (m_PanelStorage != null)
	m_PanelStorage.setHandler(getStorageHandler());

      update();

      // show dialogs
      for (View d: m_Views) {
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
     * Updates the breakpoint condition.
     */
    public void updateCondition() {
      setCondition(((BooleanCondition) m_PanelCondition.getCurrent()).shallowCopy());
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

  /**
   * The breakpoint views available.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum View {
    /** the source dialog. */
    SOURCE,
    /** the expressions dialog. */
    EXPRESSIONS,
    /** the variables dialog. */
    VARIABLES,
    /** the storage dialog. */
    STORAGE,
    /** the dialog for inspecting the current token. */
    INSPECT_TOKEN,
    /** the dialog for inspecting the current flow. */
    INSPECT_FLOW
  }

  /** the condition to evaluate. */
  protected BooleanCondition m_Condition;

  /** the watch expressions. */
  protected BaseString[] m_Watches;

  /** the watch expression types. */
  protected ExpressionType[] m_WatchTypes;

  /** whether to display the panel in the editor rather than in a separate frame. */
  protected boolean m_DisplayInEditor;

  /** the X position of the dialog. */
  protected int m_X;

  /** the Y position of the dialog. */
  protected int m_Y;
  
  /** the width of the dialog. */
  protected int m_Width;
  
  /** the height of the dialog. */
  protected int m_Height;

  /** the panel to display. */
  protected BasePanel m_Panel;

  /** the dialog that's being displayed. */
  protected BaseFrame m_Frame;

  /** whether the breakpoint is disabled. */
  protected boolean m_Disabled;

  /** the current token. */
  protected transient Token m_CurrentToken;

  /** the views to display automatically. */
  protected View[] m_Views;

  /** whether the GUI is currently being updated. */
  protected Boolean m_Updating;

  /** whether the stop button is enabled. */
  protected boolean m_StopButtonEnabled;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Allows to pause the execution of the flow when this actor is reached "
      + "and the condition evaluates to 'true'.\n"
      + "It is possible to define watches as well.\n\n"
      + "The expression has the following underlying grammar:\n"
      + new BooleanExpression().getGrammar();
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "display-in-editor", "displayInEditor",
	    getDefaultDisplayInEditor());

    m_OptionManager.add(
	    "x", "x",
	    getDefaultX(), -3, null);

    m_OptionManager.add(
	    "y", "y",
	    getDefaultY(), -3, null);

    m_OptionManager.add(
	    "width", "width",
	    getDefaultWidth(), 1, null);

    m_OptionManager.add(
	    "height", "height",
	    getDefaultHeight(), 1, null);

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
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_StopButtonEnabled = true;
  }
  
  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    String	variable;
    int		i;

    result = QuickInfoHelper.toString(this, "condition", m_Condition.getQuickInfo());

    if ((m_Watches.length > 0) || QuickInfoHelper.hasVariable(this, "watches")) {
      variable = QuickInfoHelper.getVariable(this, "watches");
      if (result.length() > 0)
	result += ", ";
      if (variable != null) {
	result += variable;
      }
      else {
	result += m_Watches.length + " watch";
	if (m_Watches.length > 1)
	  result += "es";
      }
    }

    if ((m_Views.length > 0) || QuickInfoHelper.hasVariable(this, "views")) {
      if (result.length() > 0)
	result += ", ";
      result += "view";
      variable = QuickInfoHelper.getVariable(this, "views");
      if (variable != null) {
	result += variable;
      }
      else {
	if (m_Views.length > 1)
	  result += "s";
	result += ": ";
	for (i = 0; i < m_Views.length; i++) {
	  if (i > 0)
	    result += ", ";
	  result += m_Views[i];
	}
      }
    }

    return result;
  }

  /**
   * Returns the default value for displaying the panel in the editor
   * rather than in a separate frame.
   * 
   * @return		the default
   */
  protected boolean getDefaultDisplayInEditor() {
    return false;
  }

  /**
   * Sets whether to display the panel in the flow editor rather than
   * in a separate frame.
   *
   * @param value 	true if to display in editor
   */
  public void setDisplayInEditor(boolean value) {
    m_DisplayInEditor = value;
    reset();
  }
  
  /**
   * Returns whether to display the panel in the flow editor rather than
   * in a separate frame.
   *
   * @return 		true if to display in editor
   */
  public boolean getDisplayInEditor() {
    return m_DisplayInEditor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String displayInEditorTipText() {
    return 
	"If enabled displays the panel in a tab in the flow editor rather "
	+ "than in a separate frame.";
  }

  /**
   * Returns the default X position for the dialog.
   *
   * @return		the default X
   */
  protected int getDefaultX() {
    return -3;
  }

  /**
   * Sets the X position of the dialog.
   *
   * @param value 	the X position
   */
  public void setX(int value) {
    m_X = value;
    reset();
  }

  /**
   * Returns the currently set X position of the dialog.
   *
   * @return 		the X position
   */
  public int getX() {
    return m_X;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String xTipText() {
    return "The X position of the dialog (>=0: absolute, -1: left, -2: center, -3: right).";
  }

  /**
   * Returns the default Y position for the dialog.
   *
   * @return		the default Y position
   */
  protected int getDefaultY() {
    return -1;
  }

  /**
   * Sets the Y position of the dialog.
   *
   * @param value 	the Y position
   */
  public void setY(int value) {
    m_Y = value;
    reset();
  }

  /**
   * Returns the currently set Y position of the dialog.
   *
   * @return 		the Y position
   */
  public int getY() {
    return m_Y;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String yTipText() {
    return "The Y position of the dialog (>=0: absolute, -1: top, -2: center, -3: bottom).";
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
    return 800;
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
      + "the execution of the flow is paused and a control window is opened.";
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
   * Sets whether the stop button is enabled or not.
   * 
   * @param value	true if the stop button should be enabled
   */
  public void setStopButtonEnabled(boolean value) {
    m_StopButtonEnabled = value;
    if (m_Panel != null) {
      ((ControlPanel) m_Panel).setStopButtonEnabled(value);
    }
  }
  
  /**
   * Returns whether the stop button is enabled or not.
   * 
   * @return		true if the stop button is enabled
   */
  public boolean isStopButtonEnabled() {
    return m_StopButtonEnabled;
  }
  
  /**
   * Returns the control panel.
   * 
   * @return		the panel, null if not available
   */
  public ControlPanel getPanel() {
    return (ControlPanel) m_Panel;
  }
  
  /**
   * Creates the panel to display in the dialog.
   *
   * @return		the panel
   */
  protected BasePanel newPanel() {
    ControlPanel	result;
    int			i;

    result = new ControlPanel();
    result.setStopButtonEnabled(m_StopButtonEnabled);

    for (i = 0; i < m_Watches.length; i++)
      result.addWatch(m_Watches[i].getValue(), m_WatchTypes[i]);

    return result;
  }

  /**
   * Determines whether to execute the 'then' branch.
   *
   * @param input	the input token to use for evaluation
   * @return		true if the 'then' branch should get executed
   */
  protected boolean evalCondition(Token input) {
    return m_Condition.evaluate(this, input);
  }

  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;

    result = super.setUp();

    if (result == null) {
      if (m_Watches.length != m_WatchTypes.length)
	m_WatchTypes = (ExpressionType[]) Utils.adjustArray(m_WatchTypes, m_Watches.length, ExpressionType.STRING);
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
   * Sets whether the breakpoint is disabled or not.
   *
   * @param value	if true then the breakpoint gets disabled
   */
  protected void setDisabled(boolean value) {
    m_Disabled = value;
  }

  /**
   * Returns whether the breakpoint is diabled or not.
   *
   * @return		true if the breakpoint is disabled
   */
  protected boolean isDisabled() {
    return m_Disabled;
  }

  /**
   * Creates the actual frame.
   *
   * @param panel	the panel to display in the frame
   * @return		the created frame
   */
  protected BaseFrame createFrame(BasePanel panel) {
    BaseFrame	result;
    ImageIcon	icon;

    result = new BaseFrame(getName());
    result.getContentPane().setLayout(new BorderLayout());
    result.getContentPane().add(panel, BorderLayout.CENTER);
    result.setDefaultCloseOperation(BaseDialog.HIDE_ON_CLOSE);
    icon = GUIHelper.getIcon(getClass());
    if (icon != null)
      result.setIconImage(icon.getImage());
    else
      result.setIconImage(GUIHelper.getIcon("flow.gif").getImage());
    if (panel instanceof MenuBarProvider)
      result.setJMenuBar(((MenuBarProvider) panel).getMenuBar());
    else if (this instanceof MenuBarProvider)
      result.setJMenuBar(((MenuBarProvider) this).getMenuBar());
    result.setSize(m_Width, m_Height);
    result.setLocation(ActorUtils.determineLocation(result, m_X, m_Y));

    return result;
  }
  
  /**
   * Registers the panel with the flow editor, if possible.
   * 
   * @param panel	the panel to register
   */
  protected void registerWithEditor() {
    if (getParentComponent() instanceof FlowPanel)
      ((FlowPanel) getParentComponent()).registerBreakpoint(getName(), this);
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    Runnable	run;

    result = null;

    m_CurrentToken = m_InputToken;

    if (!isHeadless() && !isDisabled()) {
      if (evalCondition(m_CurrentToken)) {
	((Flow) getRoot()).pauseExecution();

	if (m_Panel == null) {
	  m_Panel = newPanel();
	  if (m_DisplayInEditor)
	    registerWithEditor();
	  else
	    m_Frame = createFrame(m_Panel);
	}

	run = new Runnable() {
	  public void run() {
	    if ((m_Frame != null) && !m_Frame.isVisible())
	      m_Frame.setVisible(true);
	    ((ControlPanel) m_Panel).breakpointReached();
	  }
	};
	SwingUtilities.invokeLater(run);
	while (((Flow) getRoot()).isPaused()) {
	  synchronized(m_Self) {
	    try {
	      m_Self.wait(100);
	    }
	    catch (Exception e) {
	      // ignored
	    }
	  }
	}
      }
      else {
	m_OutputToken = m_CurrentToken;
      }
    }
    else {
      m_OutputToken = m_CurrentToken;
    }

    return result;
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
   * Stops the execution. No message set.
   */
  @Override
  public void stopExecution() {
    m_Condition.stopExecution();
    if ((m_Frame != null) && (m_Frame.isVisible()))
      m_Frame.setVisible(false);

    try {
      synchronized(m_Self) {
	m_Self.notifyAll();
      }
    }
    catch (Exception e) {
      // ignored
    }
    
    super.stopExecution();
  }

  /**
   * Cleans up after the execution has finished. Removes the control panel.
   */
  @Override
  public void wrapUp() {
    m_CurrentToken = null;
    doCleanUpGUI();
    super.wrapUp();
  }

  /**
   * Removes all graphical components.
   */
  protected void cleanUpGUI() {
    if (m_Frame != null) {
      if (m_Panel instanceof CleanUpHandler)
	((CleanUpHandler) m_Panel).cleanUp();

      m_Frame.setVisible(false);
      m_Frame.dispose();

      m_Frame = null;
      m_Panel = null;
    }
  }

  /**
   * Queues the clean up of the GUI in the swing thread.
   *
   * @see		#cleanUpGUI()
   */
  protected void doCleanUpGUI() {
    Runnable	runnable;

    runnable = new Runnable() {
      public void run() {
	cleanUpGUI();
      }
    };
    SwingUtilities.invokeLater(runnable);
  }

  /**
   * Cleans up after the execution has finished. Also removes graphical
   * components.
   */
  @Override
  public void cleanUp() {
    doCleanUpGUI();
    super.cleanUp();
  }
}
