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
 * SubRangeEvaluation.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.classifytab.history;

import adams.core.logging.LoggingLevel;
import adams.data.spreadsheet.MetaData;
import adams.gui.core.ConsolePanel;
import adams.gui.core.GUIHelper;
import adams.gui.core.NumberTextField;
import adams.gui.core.NumberTextField.Type;
import adams.gui.core.ParameterPanel;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.tools.wekainvestigator.tab.ClassifyTab.HistoryPanel;
import adams.gui.tools.wekainvestigator.tab.classifytab.ResultItem;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.Prediction;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import javax.swing.JMenuItem;
import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

/**
 * Generates a fake evaluation using only predictions with an actual class value
 * that fits in the specified sub-range.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SubRangeEvaluation
  extends AbstractHistoryPopupMenuItem {

  /**
   * The category for grouping menu items.
   *
   * @return		the group
   */
  @Override
  public String getCategory() {
    return "Evaluation";
  }

  /**
   * The menu item title.
   *
   * @return		the title
   */
  @Override
  public String getTitle() {
    return "Sub-range evaluation...";
  }

  /**
   * Queries the user for the range.
   *
   * @param item	the item to get the range from
   * @return		the range (min/max), null if canceled
   */
  protected double[] queryUser(ResultItem item) {
    double		min;
    double		max;
    Evaluation 		eval;
    ParameterPanel	params;
    NumberTextField	textMin;
    NumberTextField	textMax;
    ApprovalDialog	dialog;

    // determine class range
    eval = item.getEvaluation();
    min  = Double.POSITIVE_INFINITY;
    max  = Double.NEGATIVE_INFINITY;
    for (Prediction pred: eval.predictions()) {
      min = Math.min(min, pred.actual());
      max = Math.max(max, pred.actual());
    }

    // query user
    params = new ParameterPanel();

    textMin = new NumberTextField(Type.DOUBLE);
    textMin.setValue(min);
    params.addParameter("Minimum", textMin);

    textMax = new NumberTextField(Type.DOUBLE);
    textMax.setValue(max);
    params.addParameter("Maximum", textMax);

    if (GUIHelper.getParentDialog(m_Owner) != null)
      dialog = new ApprovalDialog(GUIHelper.getParentDialog(m_Owner), ModalityType.DOCUMENT_MODAL);
    else
      dialog = new ApprovalDialog(GUIHelper.getParentFrame(m_Owner), true);
    dialog.setTitle("Sub-range evaluation");
    dialog.getContentPane().add(params, BorderLayout.CENTER);
    dialog.pack();
    dialog.setLocationRelativeTo(m_Owner);
    dialog.setVisible(true);
    if (dialog.getOption() != ApprovalDialog.APPROVE_OPTION)
      return null;

    min = textMin.getValue().doubleValue();
    max = textMax.getValue().doubleValue();
    if (min >= max) {
      GUIHelper.showErrorMessage(
        m_Owner,
        "Minimum must be smaller than maximum!\n"
          + "min: " + min + "\n"
          + "max: " + max);
      return null;
    }

    return new double[]{min, max};
  }

  /**
   * Adds the range information to a clone of the provided meta-data info
   * and returns it.
   *
   * @param info	the info to clone/extend
   * @param range	the range to add
   * @return		the new meta-data
   */
  protected MetaData addSubRangeInfo(MetaData info, double[] range) {
    MetaData 	result;
    String	prefix;
    String	key;
    int		count;
    boolean	added;

    result = info.getClone();
    prefix = "Sub-range evaluation";
    count  = 0;
    added  = false;
    do {
      if (count == 0)
	key = prefix;
      else
        key = prefix + " (" + (count+1) + ")";
      if (!result.has(key)) {
	result.add(key, "[" + range[0] + ";" + range[1] + "]");
	added = true;
      }
      count++;
    }
    while (!added);

    return result;
  }

  /**
   * Creates the sub-range evaluation and adds it to the history.
   *
   * @param history	the history to add the new item to
   * @param item	the result item to use as basis
   * @param range	the new range to use
   */
  protected void createSubEvaluation(HistoryPanel history, ResultItem item, double[] range) {
    Evaluation			evalFull;
    Evaluation			evalSub;
    Instances			data;
    Instance			inst;
    ArrayList<Attribute> 	atts;
    int				i;
    Prediction			prd;
    ResultItem 			itemSub;
    MetaData 			runInfoSub;

    try {
      // create dataset from predictions
      evalFull = item.getEvaluation();
      atts     = new ArrayList<>();
      atts.add(new Attribute("Prediction"));
      data     = new Instances(
        "[" + range[0] + ";" + range[1] + "]-" + evalFull.getHeader().relationName(),
        atts, evalFull.predictions().size());
      data.setClassIndex(0);
      for (Prediction pred: evalFull.predictions()) {
        inst = new DenseInstance(1.0, new double[]{pred.actual()});
        data.add(inst);
      }

      // evaluate subset
      evalSub = new Evaluation(data);
      for (i = 0; i < evalFull.predictions().size(); i++) {
        prd = evalFull.predictions().get(i);
        if ((prd.actual() >= range[0]) && (prd.actual() <= range[1])) {
          evalSub.evaluateModelOnceAndRecordPrediction(
            new double[]{prd.predicted()},
            data.instance(i));
        }
      }

      // add to history
      runInfoSub = addSubRangeInfo(item.getRunInformation(), range);
      itemSub    = new ResultItem(item.getTemplate(), new Instances(data, 0));
      itemSub.update(evalSub, null, runInfoSub);
      history.addEntry(itemSub.getName(), itemSub);
      history.setSelectedIndex(history.count() - 1);

      // create output
      m_Owner.generateOutput(itemSub);
    }
    catch (Exception e) {
      ConsolePanel.getSingleton().append(LoggingLevel.SEVERE, "Failed to generate sub-range evaluation!", e);
    }
  }

  /**
   * Creates the menu item to add to the history's popup menu.
   *
   * @param history	the history panel this menu is for
   * @param indices	the selected indices
   * @return		the menu item, null if failed to generate
   */
  @Override
  public JMenuItem createMenuItem(final HistoryPanel history, final int[] indices) {
    JMenuItem		result;
    boolean		enabled;
    final ResultItem 	item;

    enabled = (indices.length == 1)
      && (history.getEntry(indices[0]).hasEvaluation())
      && (history.getEntry(indices[0]).getEvaluation().getHeader().classAttribute().isNumeric())
      && (history.getEntry(indices[0]).getEvaluation().predictions() != null);

    result = new JMenuItem(getTitle());
    result.setEnabled(enabled);

    if (!enabled)
      return result;

    item = history.getEntry(indices[0]);
    result.addActionListener((ActionEvent e) -> {
      double[] range = queryUser(item);
      if (range == null)
        return;
      createSubEvaluation(history, item, range);
    });

    return result;
  }
}
