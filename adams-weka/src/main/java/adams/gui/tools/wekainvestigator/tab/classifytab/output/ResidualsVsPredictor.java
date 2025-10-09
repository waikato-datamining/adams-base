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
 * Copyright (C) 2019-2025 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.classifytab.output;

import adams.core.MessageCollection;
import adams.core.ObjectCopyHelper;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.container.SequencePlotterContainer;
import adams.flow.core.Token;
import adams.flow.sink.AbstractDisplayPanel;
import adams.flow.sink.SimplePlot;
import adams.gui.tools.wekainvestigator.output.ComponentContentPanel;
import adams.gui.tools.wekainvestigator.tab.classifytab.ResultItem;
import adams.gui.visualization.sequence.WatermarkPaintlet;
import adams.gui.visualization.watermark.Null;
import adams.gui.visualization.watermark.Watermark;
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

  /** the watermark to use. */
  protected Watermark m_Watermark;

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
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "watermark", "watermark",
      new Null());
  }

  /**
   * Sets the watermark to use.
   *
   * @param value 	the watermark
   */
  public void setWatermark(Watermark value) {
    m_Watermark = value;
    reset();
  }

  /**
   * Returns the watermark to use.
   *
   * @return 		the watermark
   */
  public Watermark getWatermark() {
    return m_Watermark;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String watermarkTipText() {
    return "The watermark to use for painting the data.";
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
   * Generates the output for the evaluation.
   *
   * @param item		the item to generate output for
   * @param eval		the evaluation to use as basis
   * @param originalIndices 	the original indices to use, can be null
   * @param additionalAttributes the additional attributes to display, can be null
   * @param errors 		for collecting errors
   * @return			the generated table, null if failed to generate
   */
  @Override
  protected ComponentContentPanel createOutput(ResultItem item, Evaluation eval, int[] originalIndices, SpreadSheet additionalAttributes, MessageCollection errors) {
    SimplePlot				plot;
    List<SequencePlotterContainer> 	points;
    SequencePlotterContainer		point;
    String				name;
    AbstractDisplayPanel		panel;
    WatermarkPaintlet 			wmPaintlet;

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
    if (!(m_Watermark instanceof Null)) {
      wmPaintlet = new WatermarkPaintlet();
      wmPaintlet.setWatermark(ObjectCopyHelper.copyObject(m_Watermark));
      plot.setOverlayPaintlet(wmPaintlet);
    }
    panel = plot.createDisplayPanel(new Token(points.toArray(new SequencePlotterContainer[0])));
    panel.wrapUp();

    return new ComponentContentPanel(panel, plot.displayPanelRequiresScrollPane());
  }
}
