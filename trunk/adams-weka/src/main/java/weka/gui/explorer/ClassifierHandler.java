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
 * ClassifierHandler.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package weka.gui.explorer;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import weka.classifiers.evaluation.output.prediction.Null;
import weka.gui.explorer.Explorer.ExplorerPanel;

/**
 * Manages the {@link ClassifierPanel}.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ClassifierHandler
  extends AbstractExplorerPanelHandler {

  /** for serialization. */
  private static final long serialVersionUID = 2284676357783882049L;

  public static final String KEY_EVALUATION_METRICS = "evaluation metrics";
  public static final String KEY_OUTPUT_SOURCE_CODE = "output source code";
  public static final String KEY_PRESERVE_ORDER = "preserve order";
  public static final String KEY_RANDOM_SEED = "random seed";
  public static final String KEY_COST_SENSITIVE_EVALUATION = "cost sensitive evaluation";
  public static final String KEY_OUTPUT_PREDICTIONS = "output predictions";
  public static final String KEY_ERROR_PLOT_POINT_SIZE = "error plot point size";
  public static final String KEY_STORE_PREDICTIONS = "store predictions";
  public static final String KEY_OUTPUT_CONFUSION_MATRIX = "output confusion matrix";
  public static final String KEY_OUTPUT_ENTROPY = "output entropy";
  public static final String KEY_OUTPUT_PER_CLASS = "output per class";
  public static final String KEY_OUTPUT_MODEL = "output model";
  public static final String KEY_PERCENTAGE_SPLIT = "percentage-split";
  public static final String KEY_CV_FOLDS = "cv-folds";
  public static final String KEY_TEST = "test";
  public static final String KEY_CLASS_INDEX = "class index";
  public static final String VALUE_PERCENTAGE_SPLIT = "percentage split";
  public static final String VALUE_CROSS_VALIDATION = "cross-validation";
  public static final String VALUE_SUPPLIED_TEST_SET = "supplied test set";
  public static final String VALUE_TRAINING_SET = "training set";
  
  /**
   * Checks whether this handler can process the given panel.
   * 
   * @param panel	the panel to check
   * @return		always true
   */
  @Override
  public boolean handles(ExplorerPanel panel) {
    return (panel instanceof ClassifierPanel);
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
    ClassifierPanel		pnl;
    Hashtable<String,Object>	options;
    
    pnl    = (ClassifierPanel) panel;
    result = new ArrayList();

    // GOE
    result.add(serialize(pnl.m_ClassifierEditor));
    
    options = new Hashtable<String,Object>();
    if (pnl.m_TrainBut.isSelected())
      options.put(KEY_TEST, VALUE_TRAINING_SET);
    else if (pnl.m_TestSplitBut.isSelected())
      options.put(KEY_TEST, VALUE_SUPPLIED_TEST_SET);
    else if (pnl.m_CVBut.isSelected())
      options.put(KEY_TEST, VALUE_CROSS_VALIDATION);
    else if (pnl.m_PercentBut.isSelected())
      options.put(KEY_TEST, VALUE_PERCENTAGE_SPLIT);
    options.put(KEY_CV_FOLDS, pnl.m_CVText.getText());
    options.put(KEY_PERCENTAGE_SPLIT, pnl.m_PercentText.getText());
    options.put(KEY_CLASS_INDEX, pnl.m_ClassCombo.getSelectedIndex());
    options.put(KEY_OUTPUT_MODEL, pnl.m_OutputModelBut.isSelected());
    options.put(KEY_OUTPUT_PER_CLASS, pnl.m_OutputPerClassBut.isSelected());
    options.put(KEY_OUTPUT_ENTROPY, pnl.m_OutputEntropyBut.isSelected());
    options.put(KEY_OUTPUT_CONFUSION_MATRIX, pnl.m_OutputConfusionBut.isSelected());
    options.put(KEY_STORE_PREDICTIONS, pnl.m_StorePredictionsBut.isSelected());
    options.put(KEY_ERROR_PLOT_POINT_SIZE, pnl.m_errorPlotPointSizeProportionalToMargin.isSelected());
    options.put(KEY_OUTPUT_PREDICTIONS, pnl.m_ClassificationOutputEditor.getValue());
    if (pnl.m_EvalWRTCostsBut.isSelected())
      options.put(KEY_COST_SENSITIVE_EVALUATION, pnl.m_CostMatrixEditor.getValue());
    options.put(KEY_RANDOM_SEED, pnl.m_RandomSeedText.getText());
    options.put(KEY_PRESERVE_ORDER, pnl.m_PreserveOrderBut.isSelected());
    if (pnl.m_OutputSourceCode.isSelected())
      options.put(KEY_OUTPUT_SOURCE_CODE, pnl.m_SourceCodeClass.getText());
    options.put(KEY_EVALUATION_METRICS, pnl.m_selectedEvalMetrics);
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
    ClassifierPanel		pnl;
    Hashtable<String,Object>	options;
    String			tmp;
    
    pnl  = (ClassifierPanel) panel;
    list = (ArrayList) data;
    
    // GOE
    deserialize(list.get(0), pnl.m_ClassifierEditor);
    
    // options
    options = (Hashtable<String,Object>) list.get(1);
    tmp = (String) options.get(KEY_TEST);
    if (tmp.equals(VALUE_TRAINING_SET))
      pnl.m_TrainBut.setSelected(true);
    else if (tmp.equals(VALUE_SUPPLIED_TEST_SET))
      pnl.m_TestSplitBut.setSelected(true);
    else if (tmp.equals(VALUE_CROSS_VALIDATION))
      pnl.m_CVBut.setSelected(true);
    else if (tmp.equals(VALUE_PERCENTAGE_SPLIT))
      pnl.m_PercentBut.setSelected(true);
    restoreText(options, KEY_CV_FOLDS, "10", pnl.m_CVText);
    restoreText(options, KEY_PERCENTAGE_SPLIT, "66", pnl.m_PercentText);
    restoreSelectedIndex(options, KEY_CLASS_INDEX, -1, pnl.m_ClassCombo);
    restoreSelectedState(options, KEY_OUTPUT_MODEL, true, pnl.m_OutputModelBut);
    restoreSelectedState(options, KEY_OUTPUT_PER_CLASS, true, pnl.m_OutputPerClassBut);
    restoreSelectedState(options, KEY_OUTPUT_ENTROPY, false, pnl.m_OutputEntropyBut);
    restoreSelectedState(options, KEY_OUTPUT_CONFUSION_MATRIX, true, pnl.m_OutputConfusionBut);
    restoreSelectedState(options, KEY_STORE_PREDICTIONS, true, pnl.m_StorePredictionsBut);
    restoreGOE(options, KEY_OUTPUT_PREDICTIONS, new Null(), pnl.m_ClassificationOutputEditor);
    restoreSelectedState(options, KEY_COST_SENSITIVE_EVALUATION, false, pnl.m_EvalWRTCostsBut);
    if (pnl.m_EvalWRTCostsBut.isSelected())
      pnl.m_CostMatrixEditor.setValue(options.get(KEY_COST_SENSITIVE_EVALUATION));
    restoreText(options, KEY_RANDOM_SEED, "1", pnl.m_RandomSeedText);
    restoreSelectedState(options, KEY_PRESERVE_ORDER, false, pnl.m_PreserveOrderBut);
    restoreSelectedState(options, KEY_OUTPUT_SOURCE_CODE, false, pnl.m_OutputSourceCode);
    if (pnl.m_OutputSourceCode.isSelected())
      restoreText(options, KEY_OUTPUT_SOURCE_CODE, "WekaClassifier", pnl.m_SourceCodeClass);
    pnl.m_selectedEvalMetrics = (List<String>) options.get(KEY_EVALUATION_METRICS);

    // history
    deserialize(list.get(2), pnl.m_History);
  }
}
