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
 * FlowTabbedPane.java
 * Copyright (C) 2011-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow;

import adams.core.CleanUpHandler;
import adams.core.Properties;
import adams.flow.core.AbstractActor;
import adams.gui.core.BaseTabbedPane;
import adams.gui.core.DragAndDropTabbedPane;
import adams.gui.flow.tab.RegisteredBreakpointsTab;
import adams.gui.flow.tab.RegisteredDisplaysTab;
import adams.gui.flow.tree.Tree;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.TreePath;
import java.awt.Component;
import java.io.File;
import java.lang.reflect.Constructor;

/**
 * Specialized tabbed pane for Flow panels.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FlowTabbedPane
  extends DragAndDropTabbedPane
  implements CleanUpHandler {

  /** for serialization. */
  private static final long serialVersionUID = -1675887825433207074L;

  /** the owning editor. */
  protected FlowEditorPanel m_Owner;

  /**
   * Initializes the tabbed pane.
   *
   * @param owner	the owning editor
   */
  public FlowTabbedPane(FlowEditorPanel owner) {
    super();

    m_Owner = owner;

    setCloseTabsWithMiddelMouseButton(true);
    setMiddleMouseButtonCloseApprover(new MiddleMouseButtonCloseApprover() {
      public boolean approveClosingWithMiddleMouseButton(BaseTabbedPane source) {
	boolean	result = checkForModified();
	// to avoid second popup from checkModified() in removeTab method
	FlowPanel panel = getCurrentPanel();
	if (result && panel.isModified())
	  panel.setModified(false);
	return result;
      }
    });

    addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
	tabSelected(e);
      }
    });
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
   * Adds a new tab with an empty panel.
   *
   * @return		the new flow panel
   */
  public FlowPanel newPanel() {
    FlowPanel	result;
    Properties	props;
    String	clsname;
    Class	cls;
    Constructor	constr;
    
    props   = FlowEditorPanel.getPropertiesEditor();
    clsname = props.getProperty("FlowPanelClass", FlowPanel.class.getName());
    try {
      cls    = Class.forName(clsname);
      constr = cls.getConstructor(new Class[]{FlowTabbedPane.class});
      result = (FlowPanel) constr.newInstance(new Object[]{this});
    }
    catch (Exception e) {
      System.err.println("Failed to instantiate flow panel class: " + clsname);
      e.printStackTrace();
      result = new FlowPanel(this);
    }

    addTab(result.getTitle(), result);
    setSelectedComponent(result);

    return result;
  }

  /**
   * Returns the number of panels in the tabbed pane.
   *
   * @return		the number of panels
   */
  public int getPanelCount() {
    return getTabCount();
  }

  /**
   * Returns the panel at the specified position.
   *
   * @param index	the tab index
   * @return		the requested panel
   */
  public FlowPanel getPanelAt(int index) {
    return (FlowPanel) getComponentAt(index);
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
      return (FlowPanel) getComponentAt(getSelectedIndex());
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
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    int		i;

    for (i = 0; i < getPanelCount(); i++)
      getPanelAt(i).cleanUp();
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
   * Hook method that gets executed after a tab was successfully removed with
   * a middle mouse button click.
   * 
   * @param index	the original index
   * @param comp	the component that was removed
   */
  @Override
  protected void afterTabClosedWithMiddleMouseButton(int index, Component comp) {
    if (((FlowPanel) comp).isRunning())
      ((FlowPanel) comp).stop(true);
    else
      ((FlowPanel) comp).cleanUp();
    updateOwnerTitle();
  }

  /**
   * Gets called when a tab gets selected.
   * 
   * @param e		the event that triggered the action
   */
  protected void tabSelected(ChangeEvent e) {
    // actor tabs
    if (getPanelCount() == 0)
      m_Owner.getTabs().notifyTabs(
	  new TreePath[0],
	  new AbstractActor[0]);
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
      getOwner().showStatus("");

    // ensure that tabs are visible
    if (hasCurrentPanel()) {
      getOwner().getTabs().setVisible(RegisteredBreakpointsTab.class, getCurrentPanel().hasRegisteredBreakpoints(), false);
      getOwner().getTabs().setVisible(RegisteredDisplaysTab.class, getCurrentPanel().hasRegisteredDisplays(), false);
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
    else {
      getCurrentPanel().updateTitle();
    }
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
  public void removeTabAt(int index) {
    FlowPanel	panel;

    if (index < 0)
      return;
    if (!checkForModified())
      return;

    panel = getPanelAt(index);
    panel.cleanUp();

    super.removeTabAt(index);

    updateOwnerTitle();
  }
}
