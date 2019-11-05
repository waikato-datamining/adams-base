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

/*
 * SubRangeEvaluation.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.classifytab.output.perfold;

import adams.core.MessageCollection;
import adams.core.logging.LoggingLevel;
import adams.data.spreadsheet.MetaData;
import adams.gui.core.ConsolePanel;
import adams.gui.core.GUIHelper;
import adams.gui.core.NumberTextField;
import adams.gui.core.NumberTextField.Type;
import adams.gui.core.ParameterPanel;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.tools.wekainvestigator.tab.classifytab.ResultItem;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.Prediction;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Generates a fake evaluation using only predictions with an actual class value
 * that fits in the specified sub-range.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SubRangeEvaluation
  extends AbstractPerFoldPopupMenuItem {

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
  protected double[] queryUser(final PerFoldMultiPagePane pane, ResultItem item) {
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

    if (GUIHelper.getParentDialog(pane) != null)
      dialog = new ApprovalDialog(GUIHelper.getParentDialog(pane), ModalityType.DOCUMENT_MODAL);
    else
      dialog = new ApprovalDialog(GUIHelper.getParentFrame(pane), true);
    dialog.setTitle("Sub-range evaluation");
    dialog.getContentPane().add(params, BorderLayout.CENTER);
    dialog.pack();
    dialog.setLocationRelativeTo(pane);
    dialog.setVisible(true);
    if (dialog.getOption() != ApprovalDialog.APPROVE_OPTION)
      return null;

    min = textMin.getValue().doubleValue();
    max = textMax.getValue().doubleValue();
    if (min >= max) {
      GUIHelper.showErrorMessage(
        pane,
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
   * Creates the sub-range evaluation and adds it to the pane.
   *
   * @param pane	the per-fold pane to add the new item to
   * @param item 	the result item, for run information
   * @param eval	the evaluation to use as basis
   * @param range	the new range to use
   * @param fold 	the fold (0=full, 1..=1-based CV index)
   */
  protected void createSubEvaluation(PerFoldMultiPagePane pane, ResultItem item, Evaluation eval, int fold, double[] range) {
    Evaluation			evalFull;
    Evaluation			evalSub;
    ArrayList<Prediction> 	predsFull;
    Instances			data;
    Instance			inst;
    ArrayList<Attribute> 	atts;
    int				i;
    Prediction			prd;
    ResultItem 			itemSub;
    MetaData 			runInfoSub;
    JComponent			comp;
    MessageCollection		errors;
    String			title;

    try {
      // create dataset from predictions
      evalFull = eval;
      atts     = new ArrayList<>();
      atts.add(new Attribute("Prediction"));
      data     = new Instances(
        "[" + range[0] + ";" + range[1] + "]-" + evalFull.getHeader().relationName(),
        atts, evalFull.predictions().size());
      data.setClassIndex(0);
      predsFull = evalFull.predictions();
      for (Prediction pred: predsFull) {
        inst = new DenseInstance(1.0, new double[]{pred.actual()});
        data.add(inst);
      }

      // evaluate subset
      evalSub = new Evaluation(data);
      for (i = 0; i < predsFull.size(); i++) {
        prd = predsFull.get(i);
        if ((prd.actual() >= range[0]) && (prd.actual() <= range[1])) {
          evalSub.evaluateModelOnceAndRecordPrediction(
            new double[]{prd.predicted()},
            data.instance(i));
        }
      }

      // add to pane
      runInfoSub = addSubRangeInfo(item.getRunInformation(), range);
      itemSub    = new ResultItem(item.getTemplate(), new Instances(data, 0));
      itemSub.update(evalSub, null, runInfoSub);
      errors = new MessageCollection();
      comp   = m_OutputGenerator.createOutput(itemSub, errors);
      if (fold == 0)
        title = "Full";
      else
        title = "Fold " + fold;
      title += " [" + range[0] + ";" + range[1] + "]";
      pane.addPage(title, comp, -1);
      if (!errors.isEmpty())
        throw new Exception("Failed to generate output:\n" + errors);
      pane.setSelectedIndex(pane.getPageCount() - 1);
    }
    catch (Exception e) {
      ConsolePanel.getSingleton().append(LoggingLevel.SEVERE, "Failed to generate sub-range evaluation!", e);
    }
  }

  /**
   * Creates the sub-range evaluation and adds it to the pane.
   *
   * @param pane	the per-fold pane to add the new item to
   * @param item 	the result item, for run information
   * @param evals	the evaluations to use as basis
   * @param folds 	the folds (0=full, 1..=1-based CV index)
   * @param range	the new range to use
   */
  protected void createSubEvaluations(PerFoldMultiPagePane pane, ResultItem item, Evaluation[] evals, int[] folds, double[] range) {
    int		i;

    for (i = 0; i < evals.length; i++)
      createSubEvaluation(pane, item, evals[i], folds[i], range);
  }

  /**
   * Creates the menu item to add to the pane's popup menu.
   *
   * @param pane	the per-fold panel this menu is for
   * @param indices	the selected indices
   * @return		the menu item, null if failed to generate
   */
  @Override
  public JMenuItem createMenuItem(final PerFoldMultiPagePane pane, final int[] indices) {
    JMenuItem			result;
    final ResultItem 		item;
    int 			fold;
    final TIntList 		validIndices;
    boolean			valid;
    Evaluation 			eval;
    final List<Evaluation> 	validEvals;

    item         = pane.getItem();
    validIndices = new TIntArrayList();
    validEvals   = new ArrayList<>();
    for (int index: indices) {
      if (pane.getPageContainerAt(index).getMetaData().containsKey(PerFoldMultiPagePane.KEY_FOLD)) {
        fold = (Integer) pane.getPageContainerAt(index).getMetaData().get(PerFoldMultiPagePane.KEY_FOLD);
        eval = null;
        if (fold >= 0) {
	  // full
	  if (fold == 0) {
	    valid = (item.hasEvaluation())
	      && (item.getEvaluation().getHeader().classAttribute().isNumeric())
	      && (item.getEvaluation().predictions() != null);
	    if (valid)
	      eval = item.getEvaluation();
	  }
	  // fold
	  else {
	    valid = (item.hasFoldEvaluations())
	      && (item.getFoldEvaluations()[fold -1].getHeader().classAttribute().isNumeric())
	      && (item.getFoldEvaluations()[fold -1].predictions() != null);
	    if (valid)
	      eval = item.getFoldEvaluations()[fold -1];
	  }
	  if (valid) {
	    validIndices.add(fold);
	    validEvals.add(eval);
	  }
	}
      }
    }

    result = new JMenuItem(getTitle());
    result.setEnabled(validIndices.size() > 0);

    if (validIndices.size() == 0)
      return result;

    result.addActionListener((ActionEvent e) -> {
      double[] range = queryUser(pane, item);
      if (range == null)
        return;
      createSubEvaluations(pane, item, validEvals.toArray(new Evaluation[0]), validIndices.toArray(), range);
    });

    return result;
  }
}
