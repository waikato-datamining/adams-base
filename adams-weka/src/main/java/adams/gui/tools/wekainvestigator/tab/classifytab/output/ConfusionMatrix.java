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
 * ConfusionMatrix.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.classifytab.output;

import adams.core.MessageCollection;
import adams.core.option.OptionUtils;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.flow.core.Token;
import adams.flow.transformer.ConfusionMatrix.MatrixValues;
import adams.gui.core.SpreadSheetTable;
import adams.gui.core.spreadsheettable.CellRenderingCustomizer;
import adams.gui.core.spreadsheettable.ConfusionMatrixCellRenderingCustomizer;
import adams.gui.tools.wekainvestigator.output.TableContentPanel;
import adams.gui.tools.wekainvestigator.tab.classifytab.PredictionHelper;
import adams.gui.tools.wekainvestigator.tab.classifytab.ResultItem;

import javax.swing.JComponent;

/**
 * Displays the confusion matrix.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ConfusionMatrix
  extends AbstractOutputGenerator {

  private static final long serialVersionUID = -6829245659118360739L;

  /** what values to generate. */
  protected MatrixValues m_MatrixValues;

  /** whether to use the probabilities rather 0 and 1. */
  protected boolean m_UseProbabilities;

  /** for highlighting the cells in the table. */
  protected CellRenderingCustomizer m_CellRenderingCustomizer;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates a confusion matrix.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "matrix-values", "matrixValues",
      MatrixValues.COUNTS);

    m_OptionManager.add(
      "use-probabilities", "useProbabilities",
      false);

    m_OptionManager.add(
      "cell-rendering-customizer", "cellRenderingCustomizer",
      new ConfusionMatrixCellRenderingCustomizer());
  }

  /**
   * Sets the type of values to generate.
   *
   * @param value	the type of values
   */
  public void setMatrixValues(MatrixValues value) {
    m_MatrixValues = value;
    reset();
  }

  /**
   * Returns the type of values to generate.
   *
   * @return		the type of values
   */
  public MatrixValues getMatrixValues() {
    return m_MatrixValues;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String matrixValuesTipText() {
    return "The type of values to generate.";
  }

  /**
   * Sets whether to use probabilities instead of 0 and 1 for the counts.
   *
   * @param value	true if to use probabilities
   */
  public void setUseProbabilities(boolean value) {
    m_UseProbabilities = value;
    reset();
  }

  /**
   * Returns whether to use probabilities instead of 0 and 1 for the counts.
   *
   * @return		true if to use probabilities
   */
  public boolean getUseProbabilities() {
    return m_UseProbabilities;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useProbabilitiesTipText() {
    return "If set to true, the probabilities are used for the counts rather than 0 and 1.";
  }

  /**
   * Sets the cell rendering customizer.
   *
   * @param value 	the customizer
   */
  public void setCellRenderingCustomizer(CellRenderingCustomizer value) {
    m_CellRenderingCustomizer = value;
    reset();
  }

  /**
   * Returns the cell rendering customizer.
   *
   * @return 		the customizer
   */
  public CellRenderingCustomizer getCellRenderingCustomizer() {
    return m_CellRenderingCustomizer;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String cellRenderingCustomizerTipText() {
    return "The customizer for the cell rendering.";
  }

  /**
   * The title to use for the tab.
   *
   * @return		the title
   */
  public String getTitle() {
    return "Confusion matrix";
  }

  /**
   * Checks whether output can be generated from this item.
   *
   * @param item	the item to check
   * @return		true if output can be generated
   */
  public boolean canGenerateOutput(ResultItem item) {
    return item.hasEvaluation() && (item.getEvaluation().predictions() != null);
  }

  /**
   * Generates output from the item.
   *
   * @param item	the item to generate output for
   * @param errors	for collecting error messages
   * @return		the output component, null if failed to generate
   */
  public JComponent createOutput(ResultItem item, MessageCollection errors) {
    SpreadSheet					sheet;
    SpreadSheetTable				table;
    adams.flow.transformer.ConfusionMatrix	matrix;
    String					msg;

    if (!item.getEvaluation().getHeader().classAttribute().isNominal())
      return null;

    sheet = PredictionHelper.toSpreadSheet(
      this, errors, item, true, false, false, true, false, false);
    if (sheet == null) {
      if (errors.isEmpty())
	errors.add("Failed to generate predictions!");
      return null;
    }
    matrix = new adams.flow.transformer.ConfusionMatrix();
    matrix.setMatrixValues(m_MatrixValues);
    matrix.setActualColumn(new SpreadSheetColumnIndex("Actual"));
    matrix.setPredictedColumn(new SpreadSheetColumnIndex("Predicted"));
    if (m_UseProbabilities)
      matrix.setProbabilityColumn(new SpreadSheetColumnIndex("Probability"));
    msg = matrix.setUp();
    if (msg != null) {
      errors.add(msg);
      return null;
    }
    matrix.input(new Token(sheet));
    msg = matrix.execute();
    if (msg != null) {
      errors.add(msg);
      return null;
    }
    sheet = matrix.output().getPayload(SpreadSheet.class);
    table = new SpreadSheetTable(sheet);
    table.setUseSimpleHeader(true);
    table.setCellRenderingCustomizer((CellRenderingCustomizer) OptionUtils.shallowCopy(m_CellRenderingCustomizer));

    return new TableContentPanel(table, true, true);
  }
}
