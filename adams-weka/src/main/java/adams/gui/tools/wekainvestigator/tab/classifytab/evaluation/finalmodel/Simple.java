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
import weka.classifiers.Classifier;
import weka.core.Instances;

/**
 * Simply builds the classifier on the full dataset.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Simple
  extends AbstractFinalModelGenerator {

  private static final long serialVersionUID = 3061850581812117899L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Simply builds the classifier on the full dataset.";
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
    Classifier    cls;

    try {
      cls = ObjectCopyHelper.copyObject(item.getTemplate());
      cls.buildClassifier(data);
      item.setModel(cls);
      if (item.hasRunInformation())
	eval.addObjectSize(item.getRunInformation(), "Final model size", cls);
    }
    catch (Exception e) {
      eval.getOwner().logError("Failed to build final model!", e, "Final model build");
    }
  }
}
