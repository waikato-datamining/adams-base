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
 * BaseMultiPagePane.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.core;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Similar to a {@link BaseTabbedPane}, but with the names of the pages
 * listed in a {@link BaseList} on the left-hand side.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BaseMultiPagePane
  extends BasePanel {

  /** for serialization. */
  private static final long serialVersionUID = 887135856139374858L;

  /** the model for displaying the page names. */
  protected DefaultListModel<String> m_ModelNames;

  /** the list for displaying the page names. */
  protected BaseList m_ListNames;
  
  /** the scrollpane for the names list. */
  protected BaseScrollPane m_ScrollPaneNames;

  /** the split pane. */
  protected BaseSplitPane m_SplitPane;
  
  /** for displaying the page component. */
  protected JPanel m_PageComponent;
  
  /** the pages lookup. */
  protected HashMap<String, Component> m_PageLookup;
  
  /** the page order. */
  protected List<String> m_PageOrder;
  
  /** the currently selected page. */
  protected int m_SelectedPage;
  
  /** the change listeners (for page events). */
  protected HashSet<ChangeListener> m_ChangeListeners;
  
  @Override
  protected void initialize() {
    super.initialize();
    
    m_PageLookup      = new HashMap<String, Component>();
    m_PageOrder       = new ArrayList<String>();
    m_SelectedPage    = -1;
    m_ChangeListeners = new HashSet<ChangeListener>();
  }
  
  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();
    
    setLayout(new BorderLayout());
    
    m_SplitPane = new BaseSplitPane(BaseSplitPane.HORIZONTAL_SPLIT);
    m_SplitPane.setResizeWeight(0);
    m_SplitPane.setDividerLocation(200);
    m_SplitPane.setOneTouchExpandable(true);
    add(m_SplitPane, BorderLayout.CENTER);
    
    m_ModelNames = new DefaultListModel<String>();
    m_ListNames  = new BaseList(m_ModelNames);
    m_ListNames.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    m_ListNames.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent e) {
	updatePage();
      }
    });
    m_ScrollPaneNames = new BaseScrollPane(m_ListNames);
    m_SplitPane.setLeftComponent(m_ScrollPaneNames);
    
    m_PageComponent = new JPanel(new BorderLayout());
    m_SplitPane.setRightComponent(m_PageComponent);
  }
  
  /**
   * Returns the underlying split pane.
   * 
   * @return		the split pane
   */
  public BaseSplitPane getSplitPane() {
    return m_SplitPane;
  }
  
  /**
   * Removes all pages.
   */
  @Override
  public void removeAll() {
    m_PageOrder.clear();
    m_PageLookup.clear();
    m_ModelNames.clear();
    m_SelectedPage = -1;
    notifyChangeListeners(new ChangeEvent(this));
  }

  /**
   * Updates the page being displayed.
   */
  protected void updatePage() {
    m_PageComponent.removeAll();
    if (m_ListNames.getSelectedIndex() != -1) {
      m_SelectedPage = m_PageOrder.indexOf(m_ListNames.getSelectedValue());
      m_PageComponent.add(m_PageLookup.get(m_PageOrder.get(m_SelectedPage)));
    }
    else {
      if (m_PageOrder.size() > 0) {
	m_SelectedPage = 0;
	m_PageComponent.add(m_PageLookup.get(m_PageOrder.get(m_SelectedPage)));
      }
    }
    invalidate();
    validate();
    repaint();
    notifyChangeListeners(new ChangeEvent(this));
  }
  
  /**
   * Adds the page under the given name.
   * 
   * @param name	the name of the page
   * @param page	the page component
   */
  public void addPage(String name, Component page) {
    m_PageLookup.put(name, page);
    if (m_PageOrder.contains(name))
      m_PageOrder.remove(name);
    m_PageOrder.add(name);
    if (m_ModelNames.contains(name))
      m_ModelNames.removeElement(name);
    m_ModelNames.addElement(name);
    if (m_SelectedPage == -1)
      m_ListNames.setSelectedValue(name, true);
  }
  
  /**
   * Removes the page at the specified index.
   * 
   * @param index	the index of the page to remove
   */
  public void removePageAt(int index) {
    String	name;
    
    if ((index < 0) || (index >= m_PageOrder.size()))
      return;
    
    name = m_PageOrder.get(index);
    m_PageComponent.removeAll();
    m_PageOrder.remove(index);
    m_PageLookup.remove(name);
    m_ModelNames.removeElement(name);
    
    if (index == m_SelectedPage) {
      if (m_PageOrder.size() > 0) {
	if (m_SelectedPage == m_PageOrder.size())
	  m_ListNames.setSelectedIndex(m_SelectedPage - 1);
	else
	  m_ListNames.setSelectedIndex(m_SelectedPage);
      }
    }
  }
  
  /**
   * Removes the currently page.
   * 
   * @return		true if the page was removed
   */
  public boolean removeSelectedPage() {
    if (m_SelectedPage == -1)
      return false;
    
    removePageAt(m_SelectedPage);
    return true;
  }

  /**
   * Returns the current page count.
   * 
   * @return		the number of pages
   */
  public int getPageCount() {
    return m_PageLookup.size();
  }
  
  /**
   * Adds the specified listener.
   * 
   * @param l		the listener to add
   */
  public void addChangeListener(ChangeListener l) {
    m_ChangeListeners.add(l);
  }
  
  /**
   * Removes the specified listener.
   * 
   * @param l		the listener to remove
   */
  public void removeChangeListener(ChangeListener l) {
    m_ChangeListeners.remove(l);
  }
  
  /**
   * Notifies all change listeners with the specified event.
   * 
   * @param e		the event to send
   */
  protected void notifyChangeListeners(ChangeEvent e) {
    ChangeListener[]	listeners;
    
    listeners = m_ChangeListeners.toArray(new ChangeListener[m_ChangeListeners.size()]);
    for (ChangeListener listener: listeners)
      listener.stateChanged(e);
  }
}
