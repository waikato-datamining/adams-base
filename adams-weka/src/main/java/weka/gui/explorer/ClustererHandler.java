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
 * ClustererHandler.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package weka.gui.explorer;

import java.util.ArrayList;
import java.util.Hashtable;

import weka.gui.explorer.Explorer.ExplorerPanel;

/**
 * Manages the {@link ClustererPanel}.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ClustererHandler
  extends AbstractExplorerPanelHandler {

  /** for serialization. */
  private static final long serialVersionUID = 2284676357783882049L;

  public static final String KEY_TEST = "test";
  public static final String KEY_PERCENTAGE_SPLIT = "percentage-split";
  public static final String KEY_CLASS_INDEX = "class index";
  public static final String KEY_IGNORED_ATTRIBUTES = "ignored attributes";
  public static final String KEY_STORE_CLUSTERS = "store clusters";
  public static final String VALUE_PERCENTAGE_SPLIT = "percentage split";
  public static final String VALUE_SUPPLIED_TEST_SET = "supplied test set";
  public static final String VALUE_TRAINING_SET = "training set";
  public static final String VALUE_CLASSES_TO_CLUSTERS = "classes to clusters";

  /**
   * Checks whether this handler can process the given panel.
   * 
   * @param panel	the panel to check
   * @return		always true
   */
  @Override
  public boolean handles(ExplorerPanel panel) {
    return (panel instanceof ClustererPanel);
  }

  /**
   * Generates a view of the explorer panel that can be serialized.
   * 
   * @param panel	the panel to serialize
   * @return		the data to serialize
   */
  @Override
  public Object serialize(ExplorerPanel panel) {
    ArrayList			result;
    ClustererPanel		pnl;
    Hashtable<String,Object>	options;

    pnl    = (ClustererPanel) panel;
    result = new ArrayList();
    
    // GOE
    result.add(serialize(pnl.m_ClustererEditor));
    
    // options
    options = new Hashtable<String,Object>();
    
    if (pnl.m_TrainBut.isSelected())
      options.put(KEY_TEST, VALUE_TRAINING_SET);
    else if (pnl.m_TestSplitBut.isSelected())
      options.put(KEY_TEST, VALUE_SUPPLIED_TEST_SET);
    else if (pnl.m_PercentBut.isSelected())
      options.put(KEY_TEST, VALUE_PERCENTAGE_SPLIT);
    else if (pnl.m_ClassesToClustersBut.isSelected())
      options.put(KEY_TEST, VALUE_CLASSES_TO_CLUSTERS);
    options.put(KEY_PERCENTAGE_SPLIT, pnl.m_PercentText.getText());
    options.put(KEY_CLASS_INDEX, pnl.m_ClassCombo.getSelectedIndex());
    options.put(KEY_STORE_CLUSTERS, pnl.m_StorePredictionsBut.isSelected());
    options.put(KEY_IGNORED_ATTRIBUTES, pnl.m_ignoreKeyList.getSelectedIndices());

    result.add(options);
    
    // history
    result.add(serialize(pnl.m_History));
    
    return result;
  }

  /**
   * Deserializes the data and configures the panel.
   * 
   * @param panel	the panel to update
   * @param data	the serialized data to restore the panel with
   */
  @Override
  public void deserialize(ExplorerPanel panel, Object data) {
    ArrayList			list;
    ClustererPanel		pnl;
    Hashtable<String,Object>	options;
    String			tmp;
    
    pnl  = (ClustererPanel) panel;
    list = (ArrayList) data;
    
    // GOE
    deserialize(list.get(0), pnl.m_ClustererEditor);
    
    // options
    options = (Hashtable<String,Object>) list.get(1);
    tmp = (String) options.get(KEY_TEST);
    if (tmp.equals(VALUE_TRAINING_SET))
      pnl.m_TrainBut.setSelected(true);
    else if (tmp.equals(VALUE_SUPPLIED_TEST_SET))
      pnl.m_TestSplitBut.setSelected(true);
    else if (tmp.equals(VALUE_PERCENTAGE_SPLIT))
      pnl.m_PercentBut.setSelected(true);
    else if (tmp.equals(VALUE_CLASSES_TO_CLUSTERS))
      pnl.m_ClassesToClustersBut.setSelected(true);
    restoreSelectedIndex(options, KEY_CLASS_INDEX, -1, pnl.m_ClassCombo);
    restoreText(options, KEY_PERCENTAGE_SPLIT, "66", pnl.m_PercentText);
    restoreSelectedState(options, KEY_STORE_CLUSTERS, true, pnl.m_StorePredictionsBut);
    restoreSelectedIndices(options, KEY_IGNORED_ATTRIBUTES, new int[0], pnl.m_ignoreKeyList);

    // history
    deserialize(list.get(2), pnl.m_History);
  }
}
