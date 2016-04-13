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
 * ExplorerTSPanelHandler.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package weka.classifiers.timeseries.gui;

import weka.classifiers.functions.LinearRegressionJ;
import weka.classifiers.timeseries.gui.explorer.ExplorerTSPanelPublic;
import weka.gui.explorer.AbstractExplorerPanelHandler;
import weka.gui.explorer.Explorer.ExplorerPanel;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Handles the {@link ExplorerTSPanelPublic} panel.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ExplorerTSPanelHandler
  extends AbstractExplorerPanelHandler {

  /** for serialization. */
  private static final long serialVersionUID = 6604498681201464567L;

  // basic
  public static final String KEY_BASIC_COMPUTECONF = "basic: compute confidence";
  public static final String KEY_BASIC_CONFLEVEL = "basic: confidence level";
  public static final String KEY_BASIC_NUMTIMEUNITS = "basic: num time units";
  public static final String KEY_BASIC_SKIPLIST = "basic: skip list";
  public static final String KEY_BASIC_PERFORMEVAL = "basic: perform evaluation";

  // advanced
  public static final String KEY_ADV_BASELEARNER = "adv: base learner";
  public static final String KEY_ADV_LAGADJUST = "adv: lag adjust";
  public static final String KEY_ADV_CUSTOMLAG = "adv: custom lag";
  public static final String KEY_ADV_MINLAG = "adv: min lag";
  public static final String KEY_ADV_MAXLAG = "adv: max lag";
  public static final String KEY_ADV_FINETUNELAG = "adv: fine tune lag";
  public static final String KEY_ADV_AVGLONGLAGS = "adv: average long lags";
  public static final String KEY_ADV_AVGLAGSLONGERTHAN = "adv: average lags longer than";
  public static final String KEY_ADV_NUMLAGSTOAVG = "adv: num lags to average";
  public static final String KEY_ADV_OVERLAYDATA = "adv: use overlay data";
  public static final String KEY_ADV_EVALMETRICS = "adv: evaluation metrics";
  public static final String KEY_ADV_EVALONTRAIN = "adv: evaluation on train";
  public static final String KEY_ADV_EVALONHOLDOUT = "adv: evaluation on hold out";
  public static final String KEY_ADV_EVALHOLDOUTSIZE = "adv: evaluation hold out size";
  public static final String KEY_ADV_OUTPUTPREDS = "adv: output predictions";
  public static final String KEY_ADV_OUTPUTSTEP = "adv: output step";
  public static final String KEY_ADV_OUTPUTFUTURE = "adv: output future predictions";
  public static final String KEY_ADV_GRAPHPREDS = "adv: graph predictions";
  public static final String KEY_ADV_USEGRAPHTARGET = "adv: use graph target";
  public static final String KEY_ADV_GRAPHSTEP = "adv: graph step";
  public static final String KEY_ADV_GRAPHFUTURE = "adv: graph future predictions";

  /**
   * Checks whether this handler can process the given panel.
   * 
   * @param panel	the panel to check
   * @return		always true
   */
  @Override
  public boolean handles(ExplorerPanel panel) {
    return (panel instanceof ExplorerTSPanelPublic);
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
    ForecastingPanel		pnl;
    Hashtable<String,Object>	basic;
    Hashtable<String,Object>	advanced;

    pnl = ((ExplorerTSPanelPublic) panel).getForecastingPanel();

    result = new ArrayList();
    
    // basic
    basic = new Hashtable<String,Object>();
    basic.put(KEY_BASIC_COMPUTECONF, pnl.m_simpleConfigPanel.m_computeConfidence.isSelected());
    basic.put(KEY_BASIC_CONFLEVEL, pnl.m_simpleConfigPanel.m_confidenceLevelSpinner.getValue());
    basic.put(KEY_BASIC_NUMTIMEUNITS, pnl.m_simpleConfigPanel.m_horizonSpinner.getValue());
    basic.put(KEY_BASIC_SKIPLIST, pnl.m_simpleConfigPanel.m_skipText.getText());
    basic.put(KEY_BASIC_PERFORMEVAL, pnl.m_simpleConfigPanel.m_performEvaluation.isSelected());
    // TODO
    result.add(basic);
    
    // advanced
    advanced = new Hashtable<String,Object>();
    advanced.put(KEY_ADV_BASELEARNER, pnl.m_advancedConfigPanel.getBaseClassifier());
    advanced.put(KEY_ADV_LAGADJUST, pnl.m_advancedConfigPanel.m_adjustForVarianceCheckBox.isSelected());
    advanced.put(KEY_ADV_CUSTOMLAG, pnl.m_advancedConfigPanel.m_useCustomLags.isSelected());
    advanced.put(KEY_ADV_MINLAG, pnl.m_advancedConfigPanel.m_minLagSpinner.getValue());
    advanced.put(KEY_ADV_MAXLAG, pnl.m_advancedConfigPanel.m_maxLagSpinner.getValue());
    advanced.put(KEY_ADV_FINETUNELAG, pnl.m_advancedConfigPanel.m_fineTuneLagsField.getText());
    advanced.put(KEY_ADV_AVGLONGLAGS, pnl.m_advancedConfigPanel.m_averageLongLags.isSelected());
    advanced.put(KEY_ADV_AVGLAGSLONGERTHAN, pnl.m_advancedConfigPanel.m_averageLagsAfter.getValue());
    advanced.put(KEY_ADV_NUMLAGSTOAVG, pnl.m_advancedConfigPanel.m_numConsecutiveToAverage.getValue());
    advanced.put(KEY_ADV_EVALMETRICS, pnl.m_advancedConfigPanel.m_evaluationMetrics.getSelectedAttributes());
    advanced.put(KEY_ADV_EVALONTRAIN, pnl.m_advancedConfigPanel.m_trainingCheckBox.isSelected());
    advanced.put(KEY_ADV_EVALONHOLDOUT, pnl.m_advancedConfigPanel.m_holdoutCheckBox.isSelected());
    advanced.put(KEY_ADV_EVALHOLDOUTSIZE, pnl.m_advancedConfigPanel.m_holdoutSize.getText());
    advanced.put(KEY_ADV_OUTPUTPREDS, pnl.m_advancedConfigPanel.m_outputPredsCheckBox.isSelected());
    advanced.put(KEY_ADV_OUTPUTSTEP, pnl.m_advancedConfigPanel.m_outputStepSpinner.getValue());
    advanced.put(KEY_ADV_OUTPUTFUTURE, pnl.m_advancedConfigPanel.m_outputFutureCheckBox.isSelected());
    advanced.put(KEY_ADV_GRAPHPREDS, pnl.m_advancedConfigPanel.m_graphPredsAtStepCheckBox.isSelected());
    advanced.put(KEY_ADV_USEGRAPHTARGET, pnl.m_advancedConfigPanel.m_graphTargetForStepsCheckBox.isSelected());
    advanced.put(KEY_ADV_GRAPHSTEP, pnl.m_advancedConfigPanel.m_graphPredsAtStepSpinner.getValue());
    advanced.put(KEY_ADV_GRAPHFUTURE, pnl.m_advancedConfigPanel.m_graphFutureCheckBox.isSelected());
    // TODO
    result.add(advanced);
    
    // history
    result.add(serialize(pnl.m_history));

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
    ForecastingPanel		pnl;
    ArrayList			list;
    Hashtable<String,Object>	basic;
    Hashtable<String,Object>	advanced;

    pnl = ((ExplorerTSPanelPublic) panel).getForecastingPanel();
    list = (ArrayList) data;

    // basic
    basic = (Hashtable<String,Object>) list.get(0);
    restoreSelectedState(basic, KEY_BASIC_COMPUTECONF, false, pnl.m_simpleConfigPanel.m_computeConfidence);
    restoreSpinner(basic, KEY_BASIC_CONFLEVEL, 95, pnl.m_simpleConfigPanel.m_confidenceLevelSpinner);
    restoreSpinner(basic, KEY_BASIC_NUMTIMEUNITS, 1, pnl.m_simpleConfigPanel.m_horizonSpinner);
    restoreText(basic, KEY_BASIC_SKIPLIST, "", pnl.m_simpleConfigPanel.m_skipText);
    restoreSelectedState(basic, KEY_BASIC_PERFORMEVAL, false, pnl.m_simpleConfigPanel.m_performEvaluation);
    // TODO

    // advanced
    advanced = (Hashtable<String,Object>) list.get(1);
    restoreGOE(advanced, KEY_ADV_BASELEARNER, new LinearRegressionJ(), pnl.m_advancedConfigPanel.m_baseLearnerEditor);
    restoreSelectedState(advanced, KEY_ADV_LAGADJUST, false, pnl.m_advancedConfigPanel.m_adjustForVarianceCheckBox);
    restoreSelectedState(advanced, KEY_ADV_CUSTOMLAG, false, pnl.m_advancedConfigPanel.m_useCustomLags);
    restoreSpinner(advanced, KEY_ADV_MINLAG, 1, pnl.m_advancedConfigPanel.m_minLagSpinner);
    restoreSpinner(advanced, KEY_ADV_MAXLAG, 12, pnl.m_advancedConfigPanel.m_maxLagSpinner);
    restoreText(advanced, KEY_ADV_FINETUNELAG, "", pnl.m_advancedConfigPanel.m_fineTuneLagsField);
    restoreSelectedState(advanced, KEY_ADV_AVGLONGLAGS, false, pnl.m_advancedConfigPanel.m_averageLongLags);
    restoreSpinner(advanced, KEY_ADV_AVGLAGSLONGERTHAN, 2, pnl.m_advancedConfigPanel.m_averageLagsAfter);
    restoreSpinner(advanced, KEY_ADV_NUMLAGSTOAVG, 2, pnl.m_advancedConfigPanel.m_numConsecutiveToAverage);
    // TODO pnl.m_advancedConfigPanel.m_evaluationMetrics.setSelectedAttributes();
    restoreSelectedState(advanced, KEY_ADV_EVALONTRAIN, false, pnl.m_advancedConfigPanel.m_trainingCheckBox);
    restoreSelectedState(advanced, KEY_ADV_EVALONHOLDOUT, false, pnl.m_advancedConfigPanel.m_holdoutCheckBox);
    restoreText(advanced, KEY_ADV_EVALHOLDOUTSIZE, "0.3", pnl.m_advancedConfigPanel.m_holdoutSize);
    restoreSelectedState(advanced, KEY_ADV_OUTPUTPREDS, false, pnl.m_advancedConfigPanel.m_outputPredsCheckBox);
    restoreSpinner(advanced, KEY_ADV_OUTPUTSTEP, 1, pnl.m_advancedConfigPanel.m_outputStepSpinner);
    restoreSelectedState(advanced, KEY_ADV_OUTPUTFUTURE, true, pnl.m_advancedConfigPanel.m_outputFutureCheckBox);
    restoreSelectedState(advanced, KEY_ADV_GRAPHPREDS, false, pnl.m_advancedConfigPanel.m_graphPredsAtStepCheckBox);
    restoreSelectedState(advanced, KEY_ADV_EVALONHOLDOUT, false, pnl.m_advancedConfigPanel.m_holdoutCheckBox);
    restoreSelectedState(advanced, KEY_ADV_USEGRAPHTARGET, false, pnl.m_advancedConfigPanel.m_graphTargetForStepsCheckBox);
    restoreSpinner(advanced, KEY_ADV_GRAPHSTEP, 1, pnl.m_advancedConfigPanel.m_graphPredsAtStepSpinner);
    restoreSelectedState(advanced, KEY_ADV_GRAPHFUTURE, true, pnl.m_advancedConfigPanel.m_graphFutureCheckBox);

    pnl.m_advancedConfigPanel.updateEvalAndOutputEnabledStatus();
    
    // TODO

    // history
    deserialize(list.get(2), pnl.m_history);
  }
}
