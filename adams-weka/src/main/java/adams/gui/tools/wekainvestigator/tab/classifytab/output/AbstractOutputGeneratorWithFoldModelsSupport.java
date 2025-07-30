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
 * AbstractOutputGeneratorWithFoldModelsSupport.java
 * Copyright (C) 2025 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.classifytab.output;

import adams.core.MessageCollection;
import adams.gui.core.MultiPagePane;
import adams.gui.tools.wekainvestigator.tab.classifytab.ResultItem;
import com.github.fracpete.javautils.enumerate.Enumerated;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;

import javax.swing.JComponent;

import static com.github.fracpete.javautils.Enumerate.enumerate;

/**
 * Ancestor for output generators that can generate output for separate folds
 * just using the Classifier objects.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractOutputGeneratorWithFoldModelsSupport<T extends JComponent>
  extends AbstractOutputGenerator {

  private static final long serialVersionUID = -1143220703202297185L;

  /**
   * Checks whether the model can be handled.
   *
   * @param model	the model to check
   * @return		true if handled
   */
  protected boolean canHandleModel(Classifier model) {
    return true;
  }

  /**
   * Checks whether output can be generated from this item.
   *
   * @param item	the item to check
   * @return		true if output can be generated
   */
  public boolean canGenerateOutput(ResultItem item) {
    return (item.hasModel() && canHandleModel(item.getModel())
	      || (item.hasFoldModels() && canHandleModel(item.getFoldModels()[0])));
  }

  /**
   * Generates the output for the model.
   *
   * @param model		the model to use as basis
   * @param errors 		for collecting errors
   * @return			the generated table, null if failed to generate
   */
  protected abstract T createOutput(Classifier model, MessageCollection errors);

  /**
   * Generates output from the item.
   *
   * @param item	the item to generate output for
   * @param errors	for collecting error messages
   * @return		the output component, null if failed to generate
   */
  public JComponent createOutput(ResultItem item, MessageCollection errors) {
    MultiPagePane multiPage;

    if (item.hasFoldModels()) {
      multiPage = newMultiPagePane(item);
      if (item.hasModel())
	addPage(multiPage, "Full", createOutput(item.getModel(), errors), 0);
      for (Enumerated<Evaluation> eval: enumerate(item.getFoldEvaluations()))
	addPage(multiPage, "Fold " + (eval.index + 1), createOutput(item.getFoldModel(eval.index), errors), eval.index + 1);
      if (multiPage.getPageCount() > 0)
	multiPage.setSelectedIndex(0);
      return multiPage;
    }
    else {
      if (!item.hasModel()) {
	errors.add("No model available!");
	return null;
      }
      return createOutput(item.getModel(), errors);
    }
  }
}
