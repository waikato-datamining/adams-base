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
 * PredictionEccentricity.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.classifytab.output;

import adams.core.Utils;
import adams.data.image.BooleanArrayMatrixView;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.flow.container.PredictionEccentricityContainer;
import adams.flow.container.WekaEvaluationContainer;
import adams.flow.core.Token;
import adams.flow.transformer.PredictionEccentricity.MorphologyCycle;
import adams.flow.transformer.WekaPredictionsToSpreadSheet;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseTextArea;
import adams.gui.tools.wekainvestigator.output.ComponentContentPanel;
import adams.gui.tools.wekainvestigator.tab.classifytab.ResultItem;
import adams.gui.visualization.image.ImagePanel;

import java.awt.BorderLayout;

/**
 * Generates classifier prediction eccentricity.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PredictionEccentricity
  extends AbstractOutputGenerator {

  private static final long serialVersionUID = -6829245659118360739L;

  /** the size of the grid. */
  protected int m_Grid;

  /** the morphology cycle to apply. */
  protected MorphologyCycle m_MorphologyCycle;

  /** the number of cycles to apply. */
  protected int m_NumCycles;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates classifier prediction eccentricity.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "grid", "grid",
      100, 1, null);

    m_OptionManager.add(
      "morphology-cycle", "morphologyCycle",
      MorphologyCycle.DILATE);

    m_OptionManager.add(
      "num-cycles", "numCycles",
      1, 0, null);
  }

  /**
   * Sets the size of the grid.
   *
   * @param value	the size
   */
  public void setGrid(int value) {
    if (getOptionManager().isValid("grid", value)) {
      m_Grid = value;
      reset();
    }
  }

  /**
   * Returns the grid size.
   *
   * @return		the size
   */
  public int getGrid() {
    return m_Grid;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String gridTipText() {
    return "The size of the grid to project the predictions onto.";
  }

  /**
   * Sets the type of the morphology cycle to apply.
   *
   * @param value	the cycle
   */
  public void setMorphologyCycle(MorphologyCycle value) {
    m_MorphologyCycle = value;
    reset();
  }

  /**
   * Returns the type of the morphology cycle to apply.
   *
   * @return		the cycle
   */
  public MorphologyCycle getMorphologyCycle() {
    return m_MorphologyCycle;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String morphologyCycleTipText() {
    return "The type of the morphology cycle to apply.";
  }

  /**
   * Sets the number of cycles to apply.
   *
   * @param value	the cycles
   */
  public void setNumCycles(int value) {
    if (getOptionManager().isValid("numCycles", value)) {
      m_NumCycles = value;
      reset();
    }
  }

  /**
   * Returns the number of cycles to apply.
   *
   * @return		the cycles
   */
  public int getNumCycles() {
    return m_NumCycles;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numCyclesTipText() {
    return "The number of cycles to apply.";
  }

  /**
   * The title to use for the tab.
   *
   * @return		the title
   */
  public String getTitle() {
    return "Prediction eccentricity";
  }

  /**
   * Checks whether output can be generated from this item.
   *
   * @param item	the item to check
   * @return		true if output can be generated
   */
  public boolean canGenerateOutput(ResultItem item) {
    return item.hasEvaluation()
      && (item.getEvaluation().predictions() != null)
      && item.getEvaluation().getHeader().classAttribute().isNumeric();
  }

  /**
   * Generates output and adds it to the {@link ResultItem}.
   *
   * @param item	the item to add the output to
   * @return		null if output could be generated, otherwise error message
   */
  @Override
  public String generateOutput(ResultItem item) {
    adams.flow.transformer.PredictionEccentricity 	trans;
    WekaPredictionsToSpreadSheet			p2s;
    WekaEvaluationContainer				cont;
    Token						token;
    PredictionEccentricityContainer 			eccCont;
    String						msg;
    double						ecc;
    BooleanArrayMatrixView				matrix;
    BasePanel 						panel;
    ImagePanel						imagePanel;
    BaseTextArea					textArea;

    cont = new WekaEvaluationContainer(item.getEvaluation());
    p2s  = new WekaPredictionsToSpreadSheet();
    p2s.input(new Token(cont));
    try {
      p2s.execute();
    }
    catch (Exception e) {
      return Utils.handleException(this, "Failed to assemble predictions!", e);
    }
    token = p2s.output();

    trans = new adams.flow.transformer.PredictionEccentricity();
    trans.setGrid(m_Grid);
    trans.setMorphologyCycle(m_MorphologyCycle);
    trans.setNumCycles(m_NumCycles);
    trans.setActual(new SpreadSheetColumnIndex("Actual"));
    trans.setPredicted(new SpreadSheetColumnIndex("Predicted"));
    trans.input(token);
    msg = trans.execute();
    if (msg != null)
      return msg;
    token = trans.output();
    eccCont = (PredictionEccentricityContainer) token.getPayload();
    if (!eccCont.hasValue(PredictionEccentricityContainer.VALUE_ECCENTRICITY))
      return "No eccentricity calculated!";
    ecc = (Double) eccCont.getValue(PredictionEccentricityContainer.VALUE_ECCENTRICITY);
    if (!eccCont.hasValue(PredictionEccentricityContainer.VALUE_MATRIX))
      return "No matrix generated!";
    matrix = (BooleanArrayMatrixView) eccCont.getValue(PredictionEccentricityContainer.VALUE_MATRIX);

    panel      = new BasePanel(new BorderLayout());
    textArea   = new BaseTextArea("Eccentricity (1 - Inf): " + Utils.doubleToStringFixed(ecc, 2), 2, 30);
    textArea.setEditable(false);
    imagePanel = new ImagePanel();
    imagePanel.setCurrentImage(matrix.toBufferedImage());
    imagePanel.setScale(-1);
    panel.add(imagePanel, BorderLayout.CENTER);
    panel.add(new BaseScrollPane(textArea), BorderLayout.NORTH);

    addTab(item, new ComponentContentPanel(panel, false));

    return null;
  }
}
