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

import adams.core.CleanUpHandler;
import adams.core.Stoppable;
import adams.core.Variables;
import adams.core.base.BaseString;
import adams.core.option.DebugNestedProducer;
import adams.core.option.OptionUtils;
import adams.flow.condition.bool.BooleanCondition;
import adams.flow.condition.bool.BooleanConditionSupporter;
import adams.flow.control.Breakpoint;
import adams.flow.control.Flow;
import adams.flow.core.Actor;
import adams.flow.core.InputConsumer;
import adams.flow.core.Token;
import adams.gui.core.AbstractBaseTableModel;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseTabbedPane;
import adams.gui.core.BaseTableWithButtons;
import adams.gui.core.GUIHelper;
import adams.gui.core.TextEditorPanel;
import adams.gui.flow.FlowPanel;
import adams.gui.flow.FlowTreeHandler;
import adams.gui.flow.StoragePanel;
import adams.gui.flow.tree.Tree;
import adams.gui.goe.FlowHelper;
import adams.gui.goe.GenericObjectEditorDialog;
import adams.gui.goe.GenericObjectEditorPanel;
import adams.gui.tools.ExpressionWatchPanel;
import adams.gui.tools.ExpressionWatchPanel.ExpressionType;
import adams.gui.tools.VariableManagementPanel;
import adams.gui.visualization.debug.InspectionPanel;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

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

  /**
   * The breakpoint views available.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum View {
    /** the source code. */
    SOURCE,
    /** the expressions. */
    EXPRESSIONS,
    /** the variables. */
    VARIABLES,
    /** the storage. */
    STORAGE,
    /** inspection of the current token. */
    INSPECT_TOKEN,
    /** the breakpoints. */
    BREAKPOINTS
  }

  /**
   * Table model for displaying the current breakpoints.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class BreakpointTableModel
    extends AbstractBaseTableModel {

    private static final long serialVersionUID = 8719550408646036355L;

    /** the owner. */
    protected ControlPanel m_Owner;

    /**
     * Initializes the model.
     *
     * @param owner	the owning debug object
     */
    public BreakpointTableModel(ControlPanel owner) {
      m_Owner = owner;
    }

    /**
     * Returns the number of breakpoints.
     *
     * @return		the number of breakpoints
     */
    @Override
    public int getRowCount() {
      return m_Owner.getOwner().getBreakpoints().length;
    }

    /**
     * Returns the number of columns.
     *
     * @return		the number of columns
     */
    @Override
    public int getColumnCount() {
      return 11;
    }

    /**
     * Returns the name of the column.
     *
     * @param column	the index of the column
     * @return		the name
     */
    @Override
    public String getColumnName(int column) {
      switch (column) {
	case 0:
	  return "Current";
	case 1:
	  return "Enabled";
	case 2:
	  return "PreIn";
	case 3:
	  return "PostIn";
	case 4:
	  return "PreEx";
	case 5:
	  return "PostEx";
	case 6:
	  return "PreOut";
	case 7:
	  return "PostOut";
	case 8:
	  return "Type";
	case 9:
	  return "Quickinfo";
	case 10:
	  return "Condition";
	default:
	  throw new IllegalArgumentException("Illegal column index: " + column);
      }
    }

    /**
     * Returns the class for the specified column.
     *
     * @param columnIndex	the index
     * @return			the class
     */
    @Override
    public Class<?> getColumnClass(int columnIndex) {
      switch (columnIndex) {
	case 0:
	case 1:
	case 2:
	case 3:
	case 4:
	case 5:
	case 6:
	case 7:
	  return Boolean.class;
	case 8:
	case 9:
	case 10:
	  return String.class;
	default:
	  throw new IllegalArgumentException("Illegal column index: " + columnIndex);
      }
    }

    /**
     * Returns whether a cell is editable.
     *
     * @param rowIndex		the row, i.e., breakpoint
     * @param columnIndex	the column
     * @return			true if editable
     */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
      return (columnIndex >= 1) && (columnIndex <= 7);
    }

    /**
     * Sets the cell value at the specified location.
     *
     * @param aValue		the value to set
     * @param rowIndex		the row, i.e., the breakpoint
     * @param columnIndex	the column
     */
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
      AbstractBreakpoint	breakpoint;

      breakpoint = m_Owner.getOwner().getBreakpoints()[rowIndex];

      switch (columnIndex) {
	case 1:
	  breakpoint.setDisabled(!((Boolean) aValue));
	  break;
	case 2:
	  breakpoint.setOnPreInput((Boolean) aValue);
	  break;
	case 3:
	  breakpoint.setOnPostInput((Boolean) aValue);
	  break;
	case 4:
	  breakpoint.setOnPreExecute((Boolean) aValue);
	  break;
	case 5:
	  breakpoint.setOnPostExecute((Boolean) aValue);
	  break;
	case 6:
	  breakpoint.setOnPreOutput((Boolean) aValue);
	  break;
	case 7:
	  breakpoint.setOnPostOutput((Boolean) aValue);
	  break;
      }
    }

    /**
     * Returns the cell value at the specified location.
     *
     * @param rowIndex		the row, i.e., breakpoint
     * @param columnIndex	the column
     * @return			the value
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
      AbstractBreakpoint	breakpoint;

      breakpoint = m_Owner.getOwner().getBreakpoints()[rowIndex];

      switch (columnIndex) {
	case 0:
	  return (m_Owner.getCurrentBreakpoint() != null) && (breakpoint == m_Owner.getCurrentBreakpoint());
	case 1:
	  return !breakpoint.getDisabled();
	case 2:
	  return breakpoint.getOnPreInput();
	case 3:
	  return breakpoint.getOnPostInput();
	case 4:
	  return breakpoint.getOnPreExecute();
	case 5:
	  return breakpoint.getOnPostExecute();
	case 6:
	  return breakpoint.getOnPreOutput();
	case 7:
	  return breakpoint.getOnPostOutput();
	case 8:
	  return breakpoint.getClass().getSimpleName();
	case 9:
	  if (breakpoint instanceof BooleanConditionSupporter)
	    return ((BooleanConditionSupporter) breakpoint).getCondition().getQuickInfo();
	  else
	    return null;
	case 10:
	  if (breakpoint instanceof BooleanConditionSupporter)
	    return OptionUtils.getCommandLine(((BooleanConditionSupporter) breakpoint).getCondition());
	  else
	    return null;
	default:
	  throw new IllegalArgumentException("Illegal column index: " + columnIndex);
      }
    }

    /**
     * Removes all breakpoints.
     */
    public void clearBreakpoints() {
      m_Owner.getOwner().setBreakpoints(new AbstractBreakpoint[0]);
      fireTableDataChanged();
    }

    /**
     * Returns the breakpoint at the specified location.
     *
     * @param rowIndex	the row
     * @return		the breakpoints
     */
    public AbstractBreakpoint getBreakpointAt(int rowIndex) {
      return m_Owner.getOwner().getBreakpoints()[rowIndex];
    }

    /**
     * Sets the breakpoint at the specified location.
     *
     * @param rowIndex	the row
     * @param value	the breakpoint
     */
    public void setBreakpointAt(int rowIndex, AbstractBreakpoint value) {
      m_Owner.getOwner().getBreakpoints()[rowIndex] = value;
      fireTableRowsUpdated(rowIndex, rowIndex);
    }

    /**
     * Returns the breakpoint at the specified location.
     *
     * @param value	the breakpoint to add
     */
    public void addBreakpoint(AbstractBreakpoint value) {
      List<AbstractBreakpoint>	list;

      list = new ArrayList<>(Arrays.asList(m_Owner.getOwner().getBreakpoints()));
      list.add(value);
      m_Owner.getOwner().setBreakpoints(list.toArray(new AbstractBreakpoint[list.size()]));
      fireTableRowsUpdated(list.size() - 1, list.size() - 1);
    }

    /**
     * Removes the breakpoint at the specified location.
     *
     * @param rowIndex	the row
     * @return		the breakpoint
     */
    public AbstractBreakpoint removeBreakpointAt(int rowIndex) {
      AbstractBreakpoint	result;
      List<AbstractBreakpoint>	list;

      result = m_Owner.getOwner().getBreakpoints()[rowIndex];
      list   = new ArrayList<>(Arrays.asList(m_Owner.getOwner().getBreakpoints()));
      list.remove(rowIndex);
      m_Owner.getOwner().setBreakpoints(list.toArray(new AbstractBreakpoint[list.size()]));

      fireTableRowsDeleted(rowIndex, rowIndex);

      return result;
    }

    /**
     * Triggers an update.
     */
    public void refresh() {
      fireTableDataChanged();
    }
  }

  /**
   * Panel for managing the breakpoints.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class BreakpointPanel
    extends BasePanel
    implements CleanUpHandler, FlowTreeHandler, TableModelListener {

    private static final long serialVersionUID = 8469709963860298334L;

    /** the owner. */
    protected ControlPanel m_Owner;

    /** the table with all the breakpoints. */
    protected BaseTableWithButtons m_TableBreakpoints;

    /** the table model. */
    protected BreakpointTableModel m_TableModelBreakpoints;

    /** the disable/enable button for breakpoints. */
    protected JButton m_ButtonBreakpointsToggle;

    /** the add button for breakpoints. */
    protected JButton m_ButtonBreakpointsAdd;

    /** the edit button for breakpoints. */
    protected JButton m_ButtonBreakpointsEdit;

    /** the remove button for breakpoints. */
    protected JButton m_ButtonBreakpointsRemove;

    /** the remove all button for breakpoints. */
    protected JButton m_ButtonBreakpointsRemoveAll;

    /** the GOE for adding/editing breakpoints. */
    protected GenericObjectEditorDialog m_DialogGOE;

    /**
     * Initializes the members.
     */
    @Override
    protected void initialize() {
      super.initialize();

      m_TableModelBreakpoints = null;
      m_DialogGOE             = null;
    }

    /**
     * For initializing the GUI.
     */
    @Override
    protected void initGUI() {
      super.initGUI();

      m_TableBreakpoints = new BaseTableWithButtons();
      m_TableBreakpoints.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
      add(m_TableBreakpoints, BorderLayout.CENTER);

      m_ButtonBreakpointsToggle = new JButton("Toggle");
      m_ButtonBreakpointsToggle.addActionListener(new ActionListener() {
	@Override
	public void actionPerformed(ActionEvent e) {
	  toggleBreakpoints();
	}
      });
      m_TableBreakpoints.addToButtonsPanel(m_ButtonBreakpointsToggle);

      m_ButtonBreakpointsAdd = new JButton("Add...");
      m_ButtonBreakpointsAdd.addActionListener(new ActionListener() {
	@Override
	public void actionPerformed(ActionEvent e) {
	  addBreakpoint();
	}
      });
      m_TableBreakpoints.addToButtonsPanel(m_ButtonBreakpointsAdd);

      m_ButtonBreakpointsEdit = new JButton("Edit...");
      m_ButtonBreakpointsEdit.addActionListener(new ActionListener() {
	@Override
	public void actionPerformed(ActionEvent e) {
	  editBreakpoint(m_TableBreakpoints.getSelectedRow());
	}
      });
      m_TableBreakpoints.addToButtonsPanel(m_ButtonBreakpointsEdit);

      m_TableBreakpoints.addToButtonsPanel(new JLabel(""));

      m_ButtonBreakpointsRemove = new JButton("Remove");
      m_ButtonBreakpointsRemove.addActionListener(new ActionListener() {
	@Override
	public void actionPerformed(ActionEvent e) {
	  removeBreakpoints(m_TableBreakpoints.getSelectedRows());
	}
      });
      m_TableBreakpoints.addToButtonsPanel(m_ButtonBreakpointsRemove);

      m_ButtonBreakpointsRemoveAll = new JButton("Remove all");
      m_ButtonBreakpointsRemoveAll.addActionListener(new ActionListener() {
	@Override
	public void actionPerformed(ActionEvent e) {
	  removeBreakpoints(null);
	}
      });
      m_TableBreakpoints.addToButtonsPanel(m_ButtonBreakpointsRemoveAll);

      m_TableBreakpoints.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
	@Override
	public void valueChanged(ListSelectionEvent e) {
	  m_ButtonBreakpointsToggle.setEnabled(m_TableBreakpoints.getSelectedRowCount() > 0);
	  m_ButtonBreakpointsAdd.setEnabled(true);
	  m_ButtonBreakpointsRemove.setEnabled(m_TableBreakpoints.getSelectedRowCount() > 0);
	  m_ButtonBreakpointsRemoveAll.setEnabled(m_TableBreakpoints.getModel().getRowCount() > 0);
	}
      });
    }

    /**
     * Sets the owner.
     *
     * @param value	the owner
     */
    public void setOwner(ControlPanel value) {
      if ((m_TableModelBreakpoints == null) || (m_Owner !=  value)) {
	if (m_TableModelBreakpoints != null)
	  m_TableModelBreakpoints.removeTableModelListener(this);
	m_TableModelBreakpoints = new BreakpointTableModel(value);
	m_TableBreakpoints.setModel(m_TableModelBreakpoints);
	m_TableBreakpoints.setOptimalColumnWidth();
	m_TableModelBreakpoints.addTableModelListener(this);
      }
      m_Owner = value;
      refresh();
    }

    /**
     * Returns the current owner.
     *
     * @return		the owner, null if none set
     */
    public ControlPanel getOwner() {
      return m_Owner;
    }

    /**
     * Toggles the enabled state of the currently selected breakpoints.
     */
    protected void toggleBreakpoints() {
      AbstractBreakpoint	breakpoint;

      for (int index: m_TableBreakpoints.getSelectedRows()) {
	breakpoint = m_TableModelBreakpoints.getBreakpointAt(index);
	breakpoint.setDisabled(!breakpoint.getDisabled());
      }

      m_Owner.queueUpdate();
    }

    /**
     * Returns the default breakpoint to use.
     *
     * @return		the default
     */
    protected AbstractBreakpoint getDefaultBreakpoint() {
      PathBreakpoint	result;

      result = new PathBreakpoint();
      result.setOnPreInput(true);

      return result;
    }

    /**
     * Returns the GOE dialog to use for adding/editing breakpoints.
     * Gets instantiated if necessary.
     *
     * @return		the dialog
     */
    protected GenericObjectEditorDialog getGOEDialog() {
      if (m_DialogGOE == null) {
	m_DialogGOE = GenericObjectEditorDialog.createDialog(this);
	m_DialogGOE.getGOEEditor().setClassType(AbstractBreakpoint.class);
	m_DialogGOE.getGOEEditor().setCanChangeClassInDialog(true);
	m_DialogGOE.setCurrent(getDefaultBreakpoint());
      }
      return m_DialogGOE;
    }

    /**
     * Allows the user to add another breakpoint.
     */
    protected void addBreakpoint() {
      GenericObjectEditorDialog		dialog;

      dialog = getGOEDialog();
      dialog.setCurrent(getDefaultBreakpoint());
      dialog.setTitle("Add breakpoint");
      dialog.pack();
      dialog.setLocationRelativeTo(m_Owner);
      dialog.setVisible(true);
      if (dialog.getResult() != GenericObjectEditorDialog.APPROVE_OPTION)
	return;

      m_TableModelBreakpoints.addBreakpoint((AbstractBreakpoint) dialog.getCurrent());

      m_Owner.queueUpdate();
    }

    /**
     * Allows the user to edit a breakpoint.
     *
     * @param row	the breakpoint to edit
     */
    protected void editBreakpoint(int row) {
      GenericObjectEditorDialog		dialog;
      AbstractBreakpoint		breakpoint;

      breakpoint = m_TableModelBreakpoints.getBreakpointAt(row);

      dialog = getGOEDialog();
      dialog.setCurrent(breakpoint);
      dialog.setTitle("Edit breakpoint");
      dialog.pack();
      dialog.setLocationRelativeTo(m_Owner);
      dialog.setVisible(true);
      if (dialog.getResult() != GenericObjectEditorDialog.APPROVE_OPTION)
	return;

      m_TableModelBreakpoints.setBreakpointAt(row, (AbstractBreakpoint) dialog.getCurrent());

      m_Owner.queueUpdate();
    }

    /**
     * Removes breakpoints.
     *
     * @param indices	the indices of the breakpoints to remove, all if null
     */
    protected void removeBreakpoints(int[] indices) {
      int	i;

      if (indices == null) {
	m_TableModelBreakpoints.clearBreakpoints();
      }
      else {
	for (i = indices.length - 1; i >= 0; i--)
	  m_TableModelBreakpoints.removeBreakpointAt(indices[i]);
      }

      m_Owner.queueUpdate();
    }

    /**
     * Triggers an update.
     */
    public void refresh() {
      if (m_TableModelBreakpoints != null)
	m_TableModelBreakpoints.refresh();
    }

    /**
     * Cleans up data structures, frees up memory.
     */
    @Override
    public void cleanUp() {
      if (m_DialogGOE != null) {
	m_DialogGOE.dispose();
	m_DialogGOE = null;
      }
    }

    /**
     * Returns the tree.
     *
     * @return		the tree
     */
    @Override
    public Tree getTree() {
      return getOwner().getTree();
    }

    /**
     * Enables/disables step mode.
     *
     * @param enabled	if true step mode is enabled
     */
    public void setStepModeEnabled(boolean enabled) {
      AnyActorBreakpoint	breakpoint;
      List<AbstractBreakpoint>	breakpoints;

      if ((getOwner() == null) || (getOwner().getOwner() == null))
	return;

      breakpoint = null;
      for (AbstractBreakpoint bp: getOwner().getOwner().getBreakpoints()) {
	if (bp instanceof AnyActorBreakpoint) {
	  breakpoint = (AnyActorBreakpoint) bp;
	  break;
	}
      }

      // no need to add breakpoint?
      if (!enabled && (breakpoint == null))
	return;

      if (breakpoint == null) {
	breakpoint = new AnyActorBreakpoint();
	breakpoint.setOnPreExecute(true);
	breakpoints = new ArrayList<>(Arrays.asList(getOwner().getOwner().getBreakpoints()));
	breakpoints.add(breakpoint);
	getOwner().getOwner().setBreakpoints(breakpoints.toArray(new AbstractBreakpoint[breakpoints.size()]));
      }

      breakpoint.setDisabled(!enabled);
    }

    /**
     * Returns whether step mode is enabled.
     *
     * @return		true if step mode is enabled
     */
    public boolean isStepModeEnabled() {
      boolean 			result;
      AnyActorBreakpoint	breakpoint;

      result = false;

      if ((getOwner() != null) && (getOwner().getOwner() != null)) {
	breakpoint = null;
	for (AbstractBreakpoint bp: getOwner().getOwner().getBreakpoints()) {
	  if (bp instanceof AnyActorBreakpoint) {
	    breakpoint = (AnyActorBreakpoint) bp;
	    break;
	  }
	}
	if (breakpoint != null)
	  result = !breakpoint.getDisabled();
      }

      return result;
    }

    /**
     * Gets triggered if the table changes.
     *
     * @param e		the event
     */
    @Override
    public void tableChanged(TableModelEvent e) {
      m_Owner.queueUpdate();
    }
  }

  /**
   * A little dialog for controlling the breakpoint. Cannot be static class!
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class ControlPanel
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
	m_PanelBreakpoints.setOwner(this);
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
      if (m_PanelBreakpoints == null)
	m_PanelBreakpoints = new BreakpointPanel();
      m_PanelBreakpoints.setOwner(this);
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
    protected void showFrame() {
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
      HashSet<View>	views;
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

      if (m_PanelInspectionToken != null)
	m_PanelInspectionToken.setCurrent(getCurrentToken());

      if (m_ButtonInspectToken.isEnabled() && (getCurrentToken() == null))
	inspectToken(false);

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
  protected void blockExecution() {
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
    boolean	blocked;

    if (isLoggingEnabled())
      getLogger().info(point.getClass().getName() + "/" + hook + ": " + actor.getFullName());

    blocked = ((point == null) && isStepMode()) || (point != null);

    m_DebugPanel.setCurrentHook(hook);
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
   * @param hook	the hook method (eg preInput)
   * @param token	the current token
   */
  protected void triggered(AbstractBreakpoint point, Actor actor, String hook, Token token) {
    boolean	blocked;

    if (isLoggingEnabled())
      getLogger().info(point.getClass().getName() + "/" + hook + ": " + actor.getFullName() + "\n\t" + token);

    blocked = ((point == null) && isStepMode()) || (point != null);

    m_DebugPanel.setCurrentHook(hook);
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
    Token	token;

    token = null;
    if (actor instanceof InputConsumer)
      token = ((InputConsumer) actor).currentInput();

    for (AbstractBreakpoint point : m_Breakpoints) {
      if (!point.getDisabled() && point.triggersPreExecute(actor)) {
	if (token == null)
	  triggered(point, actor, "preExecute");
	else
	  triggered(point, actor, "preExecute", token);
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

  /**
   * Stops the execution.
   */
  @Override
  public void stopExecution() {
    m_Stopped = true;
    m_Blocked = false;
  }
}
