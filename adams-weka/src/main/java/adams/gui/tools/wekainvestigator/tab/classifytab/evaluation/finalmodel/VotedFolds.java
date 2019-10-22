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
 * Simple.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.classifytab.evaluation.finalmodel;

import adams.core.ObjectCopyHelper;
import adams.gui.tools.wekainvestigator.tab.classifytab.ResultItem;
import adams.gui.tools.wekainvestigator.tab.classifytab.evaluation.AbstractClassifierEvaluation;
import weka.classifiers.meta.Vote;
import weka.core.Instances;

/**
 * Generates a Vote meta-classifier from the models from the cross-validation folds.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class VotedFolds
  extends AbstractFinalModelGenerator {

  private static final long serialVersionUID = 3061850581812117899L;

  /** the vote template. */
  protected Vote m_Template;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates a Vote meta-classifier from the models from the cross-validation folds.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "template", "template",
      new Vote());
  }

  /**
   * Sets the Vote template to use.
   *
   * @param value	the template
   */
  public void setTemplate(Vote value) {
    m_Template = value;
    reset();
  }

  /**
   * Returns the Vote template to use.
   *
   * @return		the template
   */
  public Vote getTemplate() {
    return m_Template;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String voteTipText() {
    return "The template of the Vote classifier to use.";
  }

  /**
   * Builds the final model and stores it in the result item.
   *
   * @param eval 	the evaluation that generated the result item
   * @param data 	the full training data
   * @param item	the result item
   */
  @Override
  public void generate(AbstractClassifierEvaluation eval, Instances data, ResultItem item) {
    Vote    cls;

    if (!item.hasFoldModels()) {
      eval.getOwner().logMessage(getClass().getName() + ": cannot generate Vote model as no fold models available!");
      return;
    }

    try {
      cls = ObjectCopyHelper.copyObject(m_Template);
      cls.setClassifiers(item.getFoldModels());
      item.setModel(cls);
      if (item.hasRunInformation())
	eval.addObjectSize(item.getRunInformation(), "Final model size", cls);
    }
    catch (Exception e) {
      eval.getOwner().logError("Failed to build final model!", e, "Final model build");
    }
  }
}
