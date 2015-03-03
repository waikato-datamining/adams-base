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
 * AbstractExplorerPanelHandler.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package weka.gui.explorer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JSpinner;
import javax.swing.text.JTextComponent;

import weka.gui.GenericObjectEditor;
import weka.gui.ResultHistoryPanel;
import weka.gui.explorer.Explorer.ExplorerPanel;
import adams.core.ClassLister;

/**
 * Ancestor for handlers for specific Explorer panels.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractExplorerPanelHandler
  implements Serializable {

  /** for serialization. */
  private static final long serialVersionUID = 8194827957975338306L;

  /**
   * Checks whether this handler can process the given panel.
   * 
   * @param panel	the panel to check
   * @return		true if it can be processed
   */
  public abstract boolean handles(ExplorerPanel panel);
  
  /**
   * Serializes the content of a {@link GenericObjectEditor}.
   * 
   * @param editor	the editor to serialize
   * @return		the serialized content
   */
  protected Object serialize(GenericObjectEditor editor) {
    Object[]	result;
    
    result    = new Object[2];
    result[0] = editor.getValue();
    result[1] = editor.getHistory().getHistory();
    
    return result;
  }
  
  /**
   * Serializes a {@link ResultHistoryPanel}.
   * 
   * @param history	the history to serialize
   * @return		the serialized content
   */
  protected Object serialize(ResultHistoryPanel history) {
    ArrayList	result;
    int		i;
    String	name;
    Object[]	data;
    
    result = new ArrayList();
    
    result.add(history.getList().getSelectedIndices());
    
    for (i = 0; i < history.getList().getModel().getSize(); i++) {
      data = new Object[2];
      name = history.getNameAtIndex(i);
      data[0] = name;
      data[1] = history.getNamedBuffer(name);
      result.add(data);
    }
    
    return result;
  }
  
  /**
   * Generates a view of the explorer panel that can be serialized.
   * 
   * @param panel	the panel to serialize
   * @return		the data to serialize
   */
  public abstract Object serialize(ExplorerPanel panel);
  
  /**
   * Deserializes the data and configures the {@link GenericObjectEditor} with
   * it.
   * 
   * @param data	the content for the GOE
   * @param editor	the editor to configure
   */
  protected void deserialize(Object data, GenericObjectEditor editor) {
    Object[]	config;
    
    config = (Object[]) data;
    if (config[0] != null)
      editor.setValue(config[0]);

    if (config[1] != null) {
      editor.getHistory().clear();
      editor.getHistory().getHistory().addAll((Vector) config[1]);
    }
  }
  
  /**
   * Deserializes the data and configures the {@link ResultHistoryPanel} with
   * it.
   * 
   * @param data	the content for the history panel
   * @param history	the history panel to configure
   */
  protected void deserialize(Object data, ResultHistoryPanel history) {
    ArrayList		list;
    Object[]		config;
    int			i;
    String		name;
    StringBuffer	buffer;
    int[]		indices;
    
    history.clearResults();
    list = (ArrayList) data;
    indices = (int[]) list.remove(0);
    for (i = 0; i < list.size(); i++) {
      config = (Object[]) list.get(i);
      name   = (String) config[0];
      buffer = (StringBuffer) config[1];
      history.addResult(name, buffer);
    }
    history.getList().setSelectedIndices(indices);
  }

  /**
   * Returns the specified default value if the map doesn't contain a value
   * for the specified key.
   * 
   * @param map		the map to obtain the value from
   * @param key		the key to obtain the value for
   * @param defValue	the default value in case the value is not present in map
   */
  protected Object getValue(Map map, String key, Object defValue) {
    if (map.containsKey(key))
      return map.get(key);
    else
      return defValue;
  }
  
  /**
   * Sets the selected state of the checkbox.
   * 
   * @param map		the map to obtain the value from
   * @param key		the key to obtain the value for
   * @param defValue	the default value in case the value is not present in map
   * @param checkbox	the checkbox to update
   */
  protected void restoreSelectedState(Map map, String key, Boolean defValue, JCheckBox checkbox) {
    restoreSelectedState(checkbox, (Boolean) getValue(map, key, defValue));
  }
  
  /**
   * Sets the selected state of the checkbox.
   * 
   * @param selected	the default value in case the value is not present in map
   * @param checkbox	the checkbox to update
   */
  protected void restoreSelectedState(JCheckBox checkbox, Boolean selected) {
    checkbox.setSelected(!selected);
    checkbox.doClick();
  }
  
  /**
   * Restores the text of the text component, e.g., a JTextField.
   * 
   * @param map		the map to obtain the value from
   * @param key		the key to obtain the value for
   * @param defValue	the default value in case the value is not present in map
   * @param comp	the text component to update
   */
  protected void restoreText(Map map, String key, String defValue, JTextComponent comp) {
    comp.setText((String) getValue(map, key, defValue));
  }
  
  /**
   * Restores the integer value of the JSpinner.
   * 
   * @param map		the map to obtain the value from
   * @param key		the key to obtain the value for
   * @param defValue	the default value in case the value is not present in map
   * @param spinner	the spinner to update
   */
  protected void restoreSpinner(Map map, String key, Integer defValue, JSpinner spinner) {
    spinner.setValue((Integer) getValue(map, key, defValue));
  }
  
  /**
   * Restores the value of the GenericObjectEditor.
   * 
   * @param map		the map to obtain the value from
   * @param key		the key to obtain the value for
   * @param defValue	the default value in case the value is not present in map
   * @param goe	the GenericObjectEditor to update
   */
  protected void restoreGOE(Map map, String key, Object defValue, GenericObjectEditor goe) {
    goe.setValue(getValue(map, key, defValue));
  }
  
  /**
   * Restores the selected index of the JComboBox.
   * 
   * @param map		the map to obtain the index from
   * @param key		the key to obtain the index for
   * @param defValue	the default index in case the index is not present in map, -1 means last
   * @param combo	the JComboBox to update
   */
  protected void restoreSelectedIndex(Map map, String key, Integer defValue, JComboBox combo) {
    Integer	index;
    
    index = (Integer) getValue(map, key, defValue);
    if (index == -1)
      index = combo.getItemCount() - 1;
    combo.setSelectedIndex(index);
  }
  
  /**
   * Restores the selected indices of the JList.
   * 
   * @param map		the map to obtain the indices from
   * @param key		the key to obtain the indices for
   * @param defValue	the default indices in case the index is not present in map, -1 means last
   * @param list	the JList to update
   */
  protected void restoreSelectedIndices(Map map, String key, int[] defValue, JList list) {
    list.setSelectedIndices((int[]) getValue(map, key, defValue));
  }
  
  /**
   * Deserializes the data and configures the panel.
   * 
   * @param panel	the panel to update
   * @param data	the serialized data to restore the panel with
   */
  public abstract void deserialize(ExplorerPanel panel, Object data);

  /**
   * Returns a list with classnames of handlers.
   *
   * @return		the handler classnames
   */
  public static String[] getHandlers() {
    return ClassLister.getSingleton().getClassnames(AbstractExplorerPanelHandler.class);
  }
}
