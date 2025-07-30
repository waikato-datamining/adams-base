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
 * AbstractOutputGeneratorWithSeparateFoldsSupport.java
 * Copyright (C) 2019-2025 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.classifytab.output;

import adams.core.MessageCollection;
import adams.data.spreadsheet.SpreadSheet;
import adams.gui.core.MultiPagePane;
import adams.gui.tools.wekainvestigator.tab.classifytab.PredictionHelper;
import adams.gui.tools.wekainvestigator.tab.classifytab.ResultItem;
import com.github.fracpete.javautils.enumerate.Enumerated;
import weka.classifiers.Evaluation;

import javax.swing.JComponent;

import static com.github.fracpete.javautils.Enumerate.enumerate;

/**
 * Ancestor for output generators that can generate output for separate folds
 * just using the Evaluation objects.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractOutputGeneratorWithSeparateFoldsSupport<T extends JComponent>
  extends AbstractOutputGenerator {

  private static final long serialVersionUID = -1143220703202297185L;

  /**
   * Generates the output for the evaluation.
   *
   * @param item		the item to generate output for
   * @param eval		the evaluation to use as basis
   * @param originalIndices 	the original indices to use, can be null
   * @param additionalAttributes the additional attributes to display, can be null
   * @param errors 		for collecting errors
   * @return			the generated table, null if failed to generate
   */
  protected abstract T createOutput(ResultItem item, Evaluation eval, int[] originalIndices, SpreadSheet additionalAttributes, MessageCollection errors);

  /**
   * Generates output from the item.
   *
   * @param item	the item to generate output for
   * @param errors	for collecting error messages
   * @return		the output component, null if failed to generate
   */
  public JComponent createOutput(ResultItem item, MessageCollection errors) {
    MultiPagePane 	multiPage;
    Evaluation		foldEval;
    int[]		foldIndices;
    SpreadSheet		foldAdditional;

    if (item.hasFoldEvaluations()) {
      multiPage = newMultiPagePane(item);
      addPage(multiPage, "Full", createOutput(item, item.getEvaluation(), item.getOriginalIndices(), item.getAdditionalAttributes(), errors), 0);
      for (Enumerated<Evaluation> eval: enumerate(item.getFoldEvaluations())) {
	foldEval       = item.getFoldEvaluation(eval.index);
	foldIndices    = PredictionHelper.toSubset(item.getFoldOriginalIndices(eval.index));
	foldAdditional = PredictionHelper.toSubset(item.getFoldOriginalIndices(eval.index), item.getAdditionalAttributes());
	addPage(multiPage, "Fold " + (eval.index + 1), createOutput(item, foldEval, foldIndices, foldAdditional, errors), eval.index + 1);
      }
      if (multiPage.getPageCount() > 0)
	multiPage.setSelectedIndex(0);
      return multiPage;
    }
    else {
      return createOutput(item, item.getEvaluation(), item.getOriginalIndices(), item.getAdditionalAttributes(), errors);
    }
  }
}
