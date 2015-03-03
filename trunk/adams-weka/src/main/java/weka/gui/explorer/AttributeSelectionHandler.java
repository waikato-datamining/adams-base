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
 * AttributeSelectionHandler.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package weka.gui.explorer;

import java.util.ArrayList;
import java.util.Hashtable;

import weka.gui.explorer.Explorer.ExplorerPanel;

/**
 * Manages the {@link AttributeSelectionPanel}.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class AttributeSelectionHandler
  extends AbstractExplorerPanelHandler {

  /** for serialization. */
  private static final long serialVersionUID = 2284676357783882049L;

  public static final String KEY_TEST = "test";
  public static final String KEY_CV_FOLDS = "cv-folds";
  public static final String KEY_RANDOM_SEED = "random seed";
  public static final String KEY_CLASS_INDEX = "class index";
  public static final String VALUE_CROSS_VALIDATION = "cross-validation";
  public static final String VALUE_TRAINING_SET = "training set";

  /**
   * Checks whether this handler can process the given panel.
   * 
   * @param panel	the panel to check
   * @return		always true
   */
  @Override
  public boolean handles(ExplorerPanel panel) {
    return (panel instanceof AttributeSelectionPanel);
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
    AttributeSelectionPanel	pnl;
    Hashtable<String,Object>	options;
    
    pnl    = (AttributeSelectionPanel) panel;
    result = new ArrayList();
    
    // GOEs
    result.add(serialize(pnl.m_AttributeEvaluatorEditor));
    result.add(serialize(pnl.m_AttributeSearchEditor));

    // options
    options = new Hashtable<String,Object>();
    if (pnl.m_TrainBut.isSelected())
      options.put(KEY_TEST, VALUE_TRAINING_SET);
    else if (pnl.m_CVBut.isSelected())
      options.put(KEY_TEST, VALUE_CROSS_VALIDATION);
    options.put(KEY_CV_FOLDS, pnl.m_CVText.getText());
    options.put(KEY_RANDOM_SEED, pnl.m_SeedText.getText());
    options.put(KEY_CLASS_INDEX, pnl.m_ClassCombo.getSelectedIndex());
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
    AttributeSelectionPanel	pnl;
    Hashtable<String,Object>	options;
    String			tmp;
    
    pnl  = (AttributeSelectionPanel) panel;
    list = (ArrayList) data;
    
    // GOEs
    deserialize(list.get(0), pnl.m_AttributeEvaluatorEditor);
    deserialize(list.get(1), pnl.m_AttributeSearchEditor);
    
    // options
    options = (Hashtable<String,Object>) list.get(2);
    tmp = (String) options.get(KEY_TEST);
    if (tmp.equals(VALUE_TRAINING_SET))
      pnl.m_TrainBut.setSelected(true);
    else if (tmp.equals(VALUE_CROSS_VALIDATION))
      pnl.m_CVBut.setSelected(true);
    restoreText(options, KEY_CV_FOLDS, "10", pnl.m_CVText);
    restoreText(options, KEY_RANDOM_SEED, "1", pnl.m_SeedText);
    restoreSelectedIndex(options, KEY_CLASS_INDEX, -1, pnl.m_ClassCombo);

    // history
    deserialize(list.get(3), pnl.m_History);
  }
}
