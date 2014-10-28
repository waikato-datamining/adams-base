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
 * ListPage.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.wizard;

import gnu.trove.list.array.TIntArrayList;

import java.awt.BorderLayout;
import java.util.Arrays;
import java.util.List;

import javax.swing.DefaultListModel;

import adams.core.Properties;
import adams.core.Utils;
import adams.gui.core.BaseList;
import adams.gui.core.BaseScrollPane;

/**
 * Wizard page that use a {@link BaseList} for displaying a list of values.
 * The selected values are available in the properties object using the
 * key {@link #KEY_SELECTED}, all available values using the key 
 * {@link #KEY_LIST} (both lists are comma-separated).
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 9915 $
 */
public class ListPage
  extends AbstractWizardPage {

  /** for serialization. */
  private static final long serialVersionUID = -7633802524155866313L;

  /** key in the properties that contains the comma-separated list of all items. */
  public static final String KEY_LIST = "list";

  /** key in the properties that contains the comma-separated list of selected items. */
  public static final String KEY_SELECTED = "selected";
  
  /** the parameter panel for displaying the parameters. */
  protected BaseList m_List;
  
  /** whether to use store the full list of items in the properties as well. */
  protected boolean m_AddFullList;
  
  /**
   * Default constructor.
   */
  public ListPage() {
    super();
  }
  
  /**
   * Initializes the page with the given page name.
   * 
   * @param pageName	the page name to use
   */
  public ListPage(String pageName) {
    this();
    setPageName(pageName);
  }
  
  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_AddFullList = false;
  }
  
  /**
   * Initializes the widets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();
    
    m_List = new BaseList(new DefaultListModel());
    add(new BaseScrollPane(m_List), BorderLayout.CENTER);
  }
  
  /**
   * Sets the selection mode.
   * 
   * @param value	the mode
   * @see		BaseList#setSelectionMode(int)
   */
  public void setSelectionMode(int value) {
    m_List.setSelectionMode(value);
  }
  
  /**
   * Returns the selection mode.
   * 
   * @return		the mode
   * @see		BaseList#setSelectionMode(int)
   */
  public int getSelectionMode() {
    return m_List.getSelectionMode();
  }
  
  /**
   * Returns the underlying parameter panel.
   * 
   * @return		the parameter panel
   */
  public BaseList getList() {
    return m_List;
  }
  
  /**
   * Sets list values.
   * 
   * @param value	the values
   */
  public void setValues(String[] value) {
    setValues(Arrays.asList(value));
  }
  
  /**
   * Sets list values.
   * 
   * @param value	the values
   */
  public void setValues(List<String> value) {
    DefaultListModel	model;
    
    model = new DefaultListModel();
    for (String v: value)
      model.addElement(v);
    m_List.setModel(model);
  }
  
  /**
   * Sets the initially selected list values.
   * 
   * @param value	the values
   */
  public void setSelectedValues(String[] value) {
    setSelectedValues(Arrays.asList(value));
  }
  
  /**
   * Sets the initially selected list values.
   * 
   * @param value	the values
   */
  public void setSelectedValues(List<String> value) {
    TIntArrayList	selected;
    DefaultListModel	model;
    
    selected = new TIntArrayList();
    model    = (DefaultListModel) m_List.getModel();
    for (String v: value) {
      if (model.contains(v))
	selected.add(model.indexOf(v));
    }
    m_List.setSelectedIndices(selected.toArray());
  }
  
  /**
   * Sets whether to store the full list of items (not just selected ones) 
   * in the properties as well.
   * 
   * @param value	true if to add
   */
  public void setAddFullList(boolean value) {
    m_AddFullList = value;
  }

  /**
   * Returns whether to store the full list of items (not just selected ones) 
   * in the properties as well.
   * 
   * @param return	true if to add
   */
  public boolean getAddFullList() {
    return m_AddFullList;
  }
  
  /**
   * Returns the content of the page (ie parameters) as properties.
   * 
   * @return		the parameters as properties
   */
  @Override
  public Properties getProperties() {
    Properties	result;
    
    result = new Properties();

    if (m_AddFullList)
      result.setProperty(KEY_LIST, Utils.flatten(((DefaultListModel) m_List.getModel()).toArray(), ","));
    result.setProperty(KEY_SELECTED, Utils.flatten(m_List.getSelectedValuesList(), ","));
    
    return result;
  }
}
