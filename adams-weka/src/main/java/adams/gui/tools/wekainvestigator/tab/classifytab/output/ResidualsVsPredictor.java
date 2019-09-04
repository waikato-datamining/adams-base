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
 * ResidualsVsPredictor.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.classifytab.output;

import adams.core.MessageCollection;
import adams.flow.container.SequencePlotterContainer;
import adams.flow.core.Token;
import adams.flow.sink.AbstractDisplayPanel;
import adams.flow.sink.SimplePlot;
import adams.gui.tools.wekainvestigator.output.ComponentContentPanel;
import adams.gui.tools.wekainvestigator.tab.classifytab.ResultItem;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.Prediction;

import java.util.ArrayList;
import java.util.List;

/**
 * Plots the residuals vs the predictor.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ResidualsVsPredictor
 extends AbstractOutputGeneratorWithSeparateFoldsSupport<ComponentContentPanel> {

  private static final long serialVersionUID = -8530631855400627283L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Plots the residuals vs the predictor.";
  }

  /**
   * The title to use for the tab.
   *
   * @return		the title
   */
  @Override
  public String getTitle() {
    return "Residuals vs Predictor";
  }

  /**
   * Checks whether output can be generated from this item.
   *
   * @param item	the item to check
   * @return		true if output can be generated
   */
  @Override
  public boolean canGenerateOutput(ResultItem item) {
    return item.hasEvaluation()
      && (item.getEvaluation().predictions() != null)
      && (item.getEvaluation().getHeader() != null)
      && (item.getEvaluation().getHeader().classAttribute().isNumeric());
  }

  /**
   * Generates the output from the evaluation.
   *
   * @param eval	the evaluation to use
   * @param errors	for collecting errors
   * @return		the generated output
   */
  protected ComponentContentPanel createOutput(Evaluation eval, MessageCollection errors) {
    SimplePlot				plot;
    List<SequencePlotterContainer> 	points;
    SequencePlotterContainer		point;
    String				name;
    AbstractDisplayPanel		panel;

    points = new ArrayList<>();
    name   = eval.getHeader().relationName();
    for (Prediction pred: eval.predictions()) {
      point = new SequencePlotterContainer(name, pred.actual(), pred.actual() - pred.predicted());
      points.add(point);
    }

    plot = new SimplePlot();
    plot.setTitle("Residuals vs Predictor");
    plot.getAxisX().setLabel("Predictor");
    plot.getAxisY().setLabel("Residuals");
    panel = plot.createDisplayPanel(new Token(points.toArray(new SequencePlotterContainer[0])));
    panel.wrapUp();

    return new ComponentContentPanel(panel, plot.displayPanelRequiresScrollPane());
  }
}
