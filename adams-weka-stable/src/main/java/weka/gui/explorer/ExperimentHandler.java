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
 * ExperimentHandler.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package weka.gui.explorer;

import java.util.ArrayList;
import java.util.Hashtable;

import weka.gui.explorer.Explorer.ExplorerPanel;

/**
 * Manages the {@link ExperimentPanel}.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ExperimentHandler
  extends AbstractExplorerPanelHandler {

  /** for serialization. */
  private static final long serialVersionUID = 2284676357783882049L;

  public static final String KEY_FOLDS = "folds";
  public static final String KEY_EVALUATION = "evaluation";
  public static final String KEY_RUNS = "runs";
  public static final String KEY_CLASS_INDEX = "class index";

  /**
   * Checks whether this handler can process the given panel.
   * 
   * @param panel	the panel to check
   * @return		always true
   */
  @Override
  public boolean handles(ExplorerPanel panel) {
    return (panel instanceof ExperimentPanel);
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
    ExperimentPanel		pnl;
    Hashtable<String,Object>	options;
    
    pnl    = (ExperimentPanel) panel;
    result = new ArrayList();
    
    // GOE
    result.add(serialize(pnl.m_ClassifierEditor));
    
    // options
    options = new Hashtable<String,Object>();

    options.put(KEY_RUNS, pnl.m_RunsSpinner.getValue());
    options.put(KEY_EVALUATION, pnl.m_EvalCombo.getSelectedIndex());
    options.put(KEY_FOLDS, pnl.m_FoldsPercText.getText());
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
    ExperimentPanel		pnl;
    Hashtable<String,Object>	options;
    
    pnl  = (ExperimentPanel) panel;
    list = (ArrayList) data;
    
    // GOE
    deserialize(list.get(0), pnl.m_ClassifierEditor);
    
    // options
    options = (Hashtable<String,Object>) list.get(1);
    
    restoreSpinner(options, KEY_RUNS, 10, pnl.m_RunsSpinner);
    restoreSelectedIndex(options, KEY_EVALUATION, 0, pnl.m_EvalCombo);
    restoreText(options, KEY_FOLDS, "10", pnl.m_FoldsPercText);
    restoreSelectedIndex(options, KEY_CLASS_INDEX, -1, pnl.m_ClassCombo);

    // history
    deserialize(list.get(2), pnl.m_History);
  }
}
