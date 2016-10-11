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
 * AbstractWorkspaceManagerPanel.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.workspace;

import adams.core.CleanUpHandler;
import adams.gui.core.AbstractNamedHistoryPanel.HistoryEntrySelectionEvent;
import adams.gui.core.AbstractNamedHistoryPanel.HistoryEntrySelectionListener;
import adams.gui.core.BasePanel;
import adams.gui.core.GUIHelper;

import javax.swing.JButton;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

/**
 * Ancestor for workspace managers.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 8799 $
 * @param <T> the type of workspace panel to handle
 */
public abstract class AbstractWorkspaceManagerPanel<T extends AbstractWorkspacePanel>
  extends BasePanel
  implements CleanUpHandler, HistoryEntrySelectionListener {

  /** for serialization. */
  private static final long serialVersionUID = -20320489406680254L;

  /** the split pane for the components. */
  protected JSplitPane m_SplitPane;

  /** the history panel. */
  protected AbstractWorkspaceListPanel<T> m_History;

  /** the actual panel for displaying the other panels. */
  protected BasePanel m_PanelWorkspace;

  /** the history panel. */
  protected BasePanel m_PanelHistory;

  /** the panel for the buttons. */
  protected BasePanel m_PanelButtons;

  /** the button for adding a panel. */
  protected JButton m_ButtonAdd;

  /** the button for removing a panel. */
  protected JButton m_ButtonRemove;

  /**
   * For initializing the GUI.
   */
  @Override
  protected void initGUI() {
    int		height;

    super.initGUI();

    setLayout(new BorderLayout());

    m_SplitPane = new JSplitPane();
    add(m_SplitPane, BorderLayout.CENTER);

    // right
    m_PanelWorkspace = new BasePanel(new BorderLayout());
    m_PanelWorkspace.setMinimumSize(new Dimension(100, 0));
    m_SplitPane.setBottomComponent(m_PanelWorkspace);

    // left
    m_History = newWorkspaceList();
    m_History.setPanel(m_PanelWorkspace);
    m_History.setAllowRename(true);
    m_History.addHistoryEntrySelectionListener(this);
    m_PanelHistory = new BasePanel(new BorderLayout());
    m_PanelHistory.setMinimumSize(new Dimension(100, 0));
    m_PanelHistory.add(m_History, BorderLayout.CENTER);
    m_PanelButtons = new BasePanel(new FlowLayout(FlowLayout.LEFT));
    m_PanelHistory.add(m_PanelButtons, BorderLayout.SOUTH);
    m_SplitPane.setTopComponent(m_PanelHistory);

    // left buttons
    m_ButtonAdd = new JButton(GUIHelper.getIcon("add.gif"));
    height = m_ButtonAdd.getHeight();
    m_ButtonAdd.setSize(height, height);
    m_ButtonAdd.setToolTipText("Adds a new workspace");
    m_ButtonAdd.addActionListener((ActionEvent e) -> {
      String initial = m_History.newEntryName(getDefaultWorkspaceName());
      String name = GUIHelper.showInputDialog(
        AbstractWorkspaceManagerPanel.this,
        "Please enter the name for the workspace:",
        initial);
      if (name == null)
        return;
      addPanel(newWorkspace(true), name);
    });
    m_PanelButtons.add(m_ButtonAdd);

    m_ButtonRemove = new JButton(GUIHelper.getIcon("remove.gif"));
    m_ButtonRemove.setSize(height, height);
    m_ButtonRemove.setToolTipText("Removes all selected workspaces");
    m_ButtonRemove.addActionListener((ActionEvent e) -> {
      int[] indices = m_History.getSelectedIndices();
      for (int i = indices.length - 1; i >= 0; i--)
        removePanel(indices[i]);
    });
    m_PanelButtons.add(m_ButtonRemove);

    m_SplitPane.setOneTouchExpandable(true);
    m_SplitPane.setResizeWeight(0);
    m_SplitPane.setDividerLocation(250);
  }

  /**
   * finishes the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();
    SwingUtilities.invokeLater(() -> addPanel(newWorkspace(true), getDefaultWorkspaceName()));
  }

  /**
   * The default name for a workspace.
   *
   * @return		the default
   */
  protected abstract String getDefaultWorkspaceName();

  /**
   * Returns a new workspace instance.
   *
   * @param init	whether to initialize the workspace
   * @return		the workspace
   */
  protected abstract T newWorkspace(boolean init);

  /**
   * Removes all panels.
   */
  public void clear() {
    SwingUtilities.invokeLater(() -> {
      m_History.clear();
      m_PanelWorkspace.removeAll();
    });
  }

  /**
   * Returns the number of experimenter panels.
   *
   * @return		the number of panels
   */
  public int count() {
    return m_History.count();
  }

  /**
   * Instantiates a new panel for workspaces.
   *
   * @return		the list panel
   */
  protected abstract AbstractWorkspaceListPanel<T> newWorkspaceList();

  /**
   * Returns the underlying history panel.
   *
   * @return		the panel
   */
  public AbstractWorkspaceListPanel<T> getHistory() {
    return m_History;
  }

  /**
   * Adds the given experimenter panel.
   *
   * @param panel	the panel to add
   * @param name	the name for the panel
   */
  public synchronized void addPanel(T panel, String name) {
    m_History.addEntry(m_History.newEntryName(name), panel);
    m_History.setSelectedIndex(count() - 1);
  }

  /**
   * Removes the panel with the given name.
   *
   * @param name	the name of the panel to remove
   * @return		true if successfully removed
   */
  public synchronized boolean removePanel(String name) {
    boolean	result;
    int		index;

    if (!m_History.hasEntry(name))
      return false;

    index  = m_History.indexOfEntry(name);
    result = (m_History.removeEntry(name) != null);

    if (m_History.count() > 0) {
      if (m_History.count() <= index)
        index--;
      m_History.updateEntry(m_History.getEntryName(index));
    }

    return result;
  }

  /**
   * Removes the panel at the specified index.
   *
   * @param index	the index of the panel to remove
   * @return		true if successfully removed
   */
  public synchronized boolean removePanel(int index) {
    return removePanel(m_History.getEntryName(index));
  }

  /**
   * Returns the panel with the specified name.
   *
   * @param name	the name of the panel to retrieve
   * @return		the panel, null if not found
   */
  public T getPanel(String name) {
    return m_History.getEntry(name);
  }

  /**
   * Returns the currently selected panel, if any.
   *
   * @return		the panel, null if none available
   */
  public T getCurrentPanel() {
    return m_History.getEntry(m_History.getSelectedEntry());
  }

  /**
   * Returns the panel with the workspace entries.
   *
   * @return		the panel entries
   */
  public AbstractWorkspaceListPanel getEntryPanel() {
    return m_History;
  }

  /**
   * Gets called whenever a history entry gets selected.
   *
   * @param e		the event
   */
  public void historyEntrySelected(HistoryEntrySelectionEvent e) {
    updateButtons();
  }

  /**
   * Updates the buttons.
   */
  protected void updateButtons() {
    m_ButtonAdd.setEnabled(true);
    m_ButtonRemove.setEnabled(m_History.getSelectedIndex() > -1);
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    m_History.clear(false);
  }
}
