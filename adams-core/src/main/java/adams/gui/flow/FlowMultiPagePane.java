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
 * FlowTabbedPane.java
 * Copyright (C) 2011-2019 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow;

import adams.core.ClassLister;
import adams.core.Properties;
import adams.flow.core.Actor;
import adams.gui.core.BasePopupMenu;
import adams.gui.core.ConsolePanel;
import adams.gui.core.GUIHelper;
import adams.gui.core.MultiPagePane;
import adams.gui.flow.multipageaction.AbstractMultiPageMenuItem;
import adams.gui.flow.tabhandler.AbstractTabHandler;
import adams.gui.flow.tree.Tree;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.event.ChangeEvent;
import javax.swing.tree.TreePath;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Specialized tabbed pane for Flow panels.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class FlowMultiPagePane
  extends MultiPagePane {

  /** for serialization. */
  private static final long serialVersionUID = -1675887825433207074L;

  /**
   * The cell renderer.
   */
  public static class FlowTitleRenderer
    extends TitleRenderer {

    private static final long serialVersionUID = 206875248088135198L;

    /**
     * Initializes the renderer with the owner.
     *
     * @param owner		the owner
     */
    public FlowTitleRenderer(MultiPagePane owner) {
      super(owner);
    }

    /**
     * Returns the rendering component.
     *
     * @param list		the list this renderer is for
     * @param value		the current list value
     * @param index		the index of the value
     * @param isSelected	whether the item is selected
     * @param cellHasFocus	whether the cell has the focus
     * @return			the rendering component
     */
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
      Component 	result;
      JLabel 		label;
      FlowMultiPagePane	flowMulti;
      Font		bold;

      result    = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      flowMulti = (FlowMultiPagePane) getOwner();
      if (flowMulti.isActiveFlowAt(index)) {
	label = (JLabel) result;
	bold  = label.getFont().deriveFont(Font.BOLD);
	label.setFont(bold);
      }
      return result;
    }
  }

  /**
   * For filtering panels.
   */
  public enum FlowPanelFilter {
    /** flow is running. */
    RUNNING,
    /** flow is stopping. */
    STOPPING,
    /** whether a swingworker is running. */
    SWINGWORKER,
    /** flow is debug view. */
    DEBUG,
    /** outermost actor is a Flow actor. */
    FLOW,
    /** whether the last executed flow is available. */
    HAS_LAST_FLOW,
  }

  /** the owning editor. */
  protected FlowEditorPanel m_Owner;

  /** the flowpanel class to use. */
  protected Class m_FlowPanelClass;

  /** the menu items. */
  protected List<AbstractMultiPageMenuItem> m_MenuItems;

  /** the active flow. */
  protected FlowPanel m_ActiveFlow;

  /**
   * Initializes the tabbed pane.
   *
   * @param owner	the owning editor
   */
  public FlowMultiPagePane(FlowEditorPanel owner) {
    super();

    m_Owner = owner;

    setMaxPageCloseUndo(10);
    setPageCloseApprover((MultiPagePane source, int index) -> {
      FlowPanel panel = getPanelAt(index);
      boolean result = checkForModified(panel);
      // to avoid second popup from checkModified() in removeTab method
      if (result && panel.isModified()) {
	panel.setModified(false);
	panel.updateTitle();
      }
      return result;
    });
    setToolTipCustomizer(new ToolTipCustomizer() {
      @Override
      public String customizeToolTip(int index, String toolTip) {
	if ((index >= 0) && (index < m_PageListModel.getSize())) {
	  FlowPanel panel = (FlowPanel) m_PageListModel.get(index).getPage();
	  if (panel.getCurrentFile() != null)
	    toolTip = "<html>" + toolTip + "<br>" + panel.getCurrentFile() + "</html>";
	}
	return toolTip;
      }
    });

    addChangeListener((ChangeEvent e) -> pageSelected(e));
  }

  /**
   * For initializing members.
   */
  @Override
  protected void initialize() {
    Class[]	classes;

    super.initialize();

    m_ActiveFlow = null;
    m_MenuItems  = new ArrayList<>();
    classes     = ClassLister.getSingleton().getClasses(AbstractMultiPageMenuItem.class);
    for (Class cls: classes) {
      try {
        m_MenuItems.add((AbstractMultiPageMenuItem) cls.newInstance());
      }
      catch (Exception e) {
        ConsolePanel.getSingleton().append(
          "Failed to instantiate multi-page pane menu item for flow: " + cls.getName(), e);
      }
    }
    Collections.sort(m_MenuItems);
  }

  /**
   * Returns the owning editor.
   *
   * @return		the owning editor
   */
  public FlowEditorPanel getOwner() {
    return m_Owner;
  }

  /**
   * Returns the renderer to use.
   *
   * @return		the renderer
   */
  protected TitleRenderer newRenderer() {
    return new FlowTitleRenderer(this);
  }

  /**
   * Adds a new tab with an empty panel.
   *
   * @return		the new flow panel
   */
  public FlowPanel newPanel() {
    FlowPanel	result;
    Properties	props;
    String	clsname;
    Constructor	constr;

    if (m_FlowPanelClass == null) {
      props   = FlowEditorPanel.getPropertiesEditor();
      clsname = props.getProperty("FlowPanelClass", FlowPanel.class.getName());
      try {
        m_FlowPanelClass = Class.forName(clsname);
      }
      catch (Exception e) {
	m_FlowPanelClass = FlowPanel.class;
	ConsolePanel.getSingleton().append("Failed to instantiate flow panel class: " + clsname, e);
      }
    }

    try {
      constr = m_FlowPanelClass.getConstructor(FlowMultiPagePane.class);
      result = (FlowPanel) constr.newInstance(this);
    }
    catch (Exception e) {
      ConsolePanel.getSingleton().append("Failed to instantiate flow panel class: " + m_FlowPanelClass.getClass().getName(), e);
      result = new FlowPanel(this);
    }

    addPage(result.getTitle(), result);
    setSelectedPage(result);
    result.requestFocus();

    return result;
  }

  /**
   * Returns the number of panels in the tabbed pane.
   *
   * @return		the number of panels
   */
  public int getPanelCount() {
    return getPageCount();
  }

  /**
   * Returns the indices of the flow panels that match the filter.
   *
   * @param filters	the filters to apply
   * @return		the indices of the panels that matched
   */
  public int[] getSelectedIndices(Map<FlowPanelFilter,Boolean> filters) {
    TIntList	result;
    FlowPanel	current;
    boolean	match;

    result = new TIntArrayList();

    for (int index: getSelectedIndices()) {
      current = getPanelAt(index);
      match   = true;
      for (FlowPanelFilter filter: filters.keySet()) {
        switch (filter) {
	  case RUNNING:
	    match = (current.isRunning() == filters.get(filter));
	    break;
	  case STOPPING:
	    match = (current.isStopping() == filters.get(filter));
	    break;
	  case DEBUG:
	    match = (current.getTree().isDebug() == filters.get(filter));
	    break;
	  case FLOW:
	    match = (current.getTree().isFlow() == filters.get(filter));
	    break;
	  case SWINGWORKER:
	    match = (current.isSwingWorkerRunning() == filters.get(filter));
	    break;
	  case HAS_LAST_FLOW:
	    match = ((current.getLastFlow() != null) && filters.get(filter))
	      || ((current.getLastFlow() == null) && !filters.get(filter));
	    break;
	  default:
	    throw new IllegalStateException("Unhandled panel filter: " + filter);
	}
	if (!match)
	  break;
      }
      if (match)
	result.add(index);
    }

    return result.toArray();
  }

  /**
   * Returns the panel at the specified position.
   *
   * @param index	the tab index
   * @return		the requested panel
   */
  public FlowPanel getPanelAt(int index) {
    return (FlowPanel) getPageAt(index);
  }

  /**
   * Returns the index of the panel with the specified title.
   *
   * @param title	the title to check
   * @return		the panel, -1 if not found
   */
  public int indexOfPanel(String title) {
    int		result;
    int		i;
    FlowPanel	panel;

    result = -1;

    for (i = 0; i < getPanelCount(); i++) {
      panel = getPanelAt(i);
      if (panel.getTitle().equals(title)) {
	result = i;
	break;
      }
    }

    return result;
  }

  /**
   * Returns whether a panel with the specified title exists.
   *
   * @param title	the title to look for
   * @return		true if it exists
   */
  public boolean hasPanel(String title) {
    return (indexOfPanel(title) > -1);
  }

  /**
   * Returns if a panel is available.
   *
   * @return		true if a panel available
   */
  public boolean hasCurrentPanel() {
    return (getCurrentPanel() != null);
  }

  /**
   * Returns the currently selected panel.
   *
   * @return		the current panel, null if not available
   */
  public FlowPanel getCurrentPanel() {
    if (getSelectedIndex() != -1)
      return (FlowPanel) getPageAt(getSelectedIndex());
    else
      return null;
  }

  /**
   * Returns the tree at the specified position.
   *
   * @param index	the tab index
   * @return		the requested tree
   */
  public Tree getTreeAt(int index) {
    return getPanelAt(index).getTree();
  }

  /**
   * Returns whether a tree is currently selected.
   *
   * @return		true if a tree available
   */
  public boolean hasCurrentTree() {
    return (getCurrentTree() != null);
  }

  /**
   * Returns the currently selected tree.
   *
   * @return		the currently selected tree, null if not available
   */
  public Tree getCurrentTree() {
    if (getSelectedIndex() != -1)
      return getPanelAt(getSelectedIndex()).getTree();
    else
      return null;
  }

  /**
   * Returns whether we can proceed with the operation or not, depending on
   * whether the user saved the flow or discarded the changes.
   *
   * @return		true if safe to proceed
   */
  protected boolean checkForModified() {
    if (m_Owner == null)
      return true;
    return m_Owner.checkForModified();
  }

  /**
   * Returns whether we can proceed with the operation or not, depending on
   * whether the user saved the flow or discarded the changes.
   *
   * @param panel	the panel to check
   * @return		true if safe to proceed
   */
  protected boolean checkForModified(FlowPanel panel) {
    if (m_Owner == null)
      return true;
    return m_Owner.checkForModified(panel);
  }

  /**
   * Gets called when a tab gets selected.
   * 
   * @param e		the event that triggered the action
   */
  protected void pageSelected(ChangeEvent e) {
    // actor tabs
    if ((getPanelCount() == 0) || (getSelectedIndex() == -1))
      m_Owner.getTabs().notifyTabs(
	  new TreePath[0],
	  new Actor[0]);
    else
      m_Owner.getTabs().notifyTabs(
	  m_Owner.getCurrentPanel().getTree().getSelectionPaths(),
	  m_Owner.getCurrentPanel().getTree().getSelectedActors());
    
    // title
    updateOwnerTitle();

    // statusbar
    if (hasCurrentPanel() && getCurrentPanel().isRunning())
      getOwner().showStatus("Running");
    else
      getOwner().showStatus(hasCurrentPanel() ? getCurrentPanel().getStatus() : "");

    // ensure that tabs are visible
    if (hasCurrentPanel()) {
      for (AbstractTabHandler handler: getCurrentPanel().getTabHandlers())
	handler.display();
    }
    
    // current directory
    updateCurrentDirectory();
    
    // update in general
    m_Owner.update();
  }

  /**
   * Updates the owner's title.
   */
  protected void updateOwnerTitle() {
    String	title;

    if (m_Owner == null)
      return;

    if (getPanelCount() == 0) {
      title = FlowEditorPanel.DEFAULT_TITLE;
      m_Owner.setParentTitle(title);
    }
    else if (getCurrentPanel() != null) {
      title = getCurrentPanel().generateTitle();
      m_Owner.setParentTitle(title);
    }
  }

  /**
   * Updates the title.
   *
   * @param panel	the panel to update the title for
   * @param title	the new title
   */
  public void updateTitle(FlowPanel panel, String title) {
    int		index;

    index = indexOfPage(panel);
    if (index == -1)
      return;

    setTitleAt(index, title);
    updateOwnerTitle();
  }

  /**
   * Updates the current working directory of the owner's file chooser to
   * the one represented by this flow.
   */
  public void updateCurrentDirectory() {
    File	file;
    
    if (!hasCurrentPanel())
      return;
    
    file = getCurrentPanel().getCurrentFile();
    if (file == null)
      return;
    
    getOwner().setCurrentDirectory(file.getParentFile());
  }

  /**
   * Removes the tab at <code>index</code>.
   * After the component associated with <code>index</code> is removed,
   * its visibility is reset to true to ensure it will be visible
   * if added to other containers.
   *
   * @param index the index of the tab to be removed
   */
  @Override
  public PageContainer removePageAt(int index) {
    PageContainer	result;
    FlowPanel		panel;

    if (index < 0)
      return null;

    panel = getPanelAt(index);
    if (panel.isRunning())
      panel.stop(true);
    else
      panel.cleanUp();

    if (panel == m_ActiveFlow)
      m_ActiveFlow = null;

    result = super.removePageAt(index);

    updateOwnerTitle();

    return result;
  }

  /**
   * Checks whether an active flow is set.
   *
   * @return		true if active flow set
   */
  public boolean hasActiveFlow() {
    return (m_ActiveFlow != null);
  }

  /**
   * Unsets the active flow.
   */
  public void unsetActiveFlow() {
    if (m_ActiveFlow != null) {
      m_ActiveFlow = null;
      update();
    }
  }

  /**
   * Returns the active flow panel (if any).
   *
   * @return		the panel, null if no active one set
   */
  public FlowPanel getActiveFlow() {
    return m_ActiveFlow;
  }

  /**
   * Checks whether the flow at the given position is the active one.
   *
   * @param index	the page index
   * @return		true if it is the active one
   */
  public boolean isActiveFlowAt(int index) {
    return (m_ActiveFlow != null) && (getPanelAt(index) == m_ActiveFlow);
  }

  /**
   * Sets the flow at the given position as the new active one.
   *
   * @param index	the flow to use as active one
   */
  public void setActiveFlowAt(int index) {
    m_ActiveFlow = getPanelAt(index);
    update();
  }

  /**
   * Generates the right-click menu for the JList.
   *
   * @param e		the event that triggered the popup
   * @return		the generated menu
   * @see		#showPopup(MouseEvent)
   */
  @Override
  protected BasePopupMenu createPopup(MouseEvent e) {
    BasePopupMenu	result;
    JMenuItem 		menuitem;
    String		group;
    final int		index;
    final int[]		indices;

    result  = super.createPopup(e);
    index   = getSelectedIndex();
    indices = getSelectedIndices();

    if (isActiveFlowAt(index)) {
      menuitem = new JMenuItem("Unset active", GUIHelper.getIcon("flow_unsetactive.gif"));
      menuitem.setEnabled(indices.length == 1);
      menuitem.addActionListener((ActionEvent ae) -> unsetActiveFlow());
      result.add(menuitem);
    }
    else {
      menuitem = new JMenuItem("Set active", GUIHelper.getIcon("flow_setactive.gif"));
      menuitem.setEnabled(indices.length == 1);
      menuitem.addActionListener((ActionEvent ae) -> setActiveFlowAt(index));
      result.add(menuitem);
    }

    group = "";
    for (AbstractMultiPageMenuItem item: m_MenuItems) {
      menuitem = item.getMenuItem(this);
      if (menuitem != null) {
	if (!item.getGroup().equals(group)) {
	  result.addSeparator();
	  group = item.getGroup();
	}
	result.add(menuitem);
      }
    }

    return result;
  }
}
