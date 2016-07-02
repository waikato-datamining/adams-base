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
 * BreakpointPanel.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.flow.execution.debug;

import adams.core.CleanUpHandler;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseTableWithButtons;
import adams.gui.flow.FlowTreeHandler;
import adams.gui.flow.tree.Tree;
import adams.gui.goe.GenericObjectEditorDialog;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Panel for managing the breakpoints.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 10983 $
 */
public class BreakpointPanel
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

  /** whether to ignore updates. */
  protected boolean m_IgnoreUpdates;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_TableModelBreakpoints = null;
    m_DialogGOE             = null;
    m_IgnoreUpdates         = false;
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
    if ((m_TableModelBreakpoints == null) || (m_Owner != value)) {
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
    AbstractBreakpoint breakpoint;

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
    PathBreakpoint result;

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
    AnyActorBreakpoint breakpoint;
    List<AbstractBreakpoint> breakpoints;

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

  public void setIgnoreUpdates(boolean value) {
    m_IgnoreUpdates = value;
  }

  public boolean getIgnoreUpdates() {
    return m_IgnoreUpdates;
  }

  /**
   * Gets triggered if the table changes.
   *
   * @param e		the event
   */
  @Override
  public void tableChanged(TableModelEvent e) {
    if (m_IgnoreUpdates)
      return;
    m_Owner.queueUpdate();
  }
}
