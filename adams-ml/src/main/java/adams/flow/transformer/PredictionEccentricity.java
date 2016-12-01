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

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.data.image.BinaryMorphology;
import adams.data.image.BooleanArrayMatrixView;
import adams.data.image.moments.MomentHelper;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.data.spreadsheet.SpreadSheetUtils;
import adams.data.statistics.StatUtils;
import adams.flow.container.PredictionEccentricityContainer;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Treats the predictions from a regressor as an image and computes the 'eccentricity' of the actual vs predicted plot. Generated values range from 1 to infinity with a value of 1 representing a circular shape.<br>
 * Projects the predictions onto the specified grid, using the 'actual' for both axes.<br>
 * <br>
 * For more details see:<br>
 * https:&#47;&#47;en.wikipedia.org&#47;wiki&#47;Eccentricity_(mathematics)
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.SpreadSheet<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.PredictionEccentricityContainer<br>
 * <br><br>
 * Container information:<br>
 * - adams.flow.container.PredictionEccentricityContainer: Predictions, Eccentricity, Matrix
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: PredictionEccentricity
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this 
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical 
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing 
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-actual &lt;adams.data.spreadsheet.SpreadSheetColumnIndex&gt; (property: actual)
 * &nbsp;&nbsp;&nbsp;The column with the actual values.
 * &nbsp;&nbsp;&nbsp;default: Actual
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); column names can be surrounded by double quotes.
 * </pre>
 * 
 * <pre>-predicted &lt;adams.data.spreadsheet.SpreadSheetColumnIndex&gt; (property: predicted)
 * &nbsp;&nbsp;&nbsp;The column with the predicted values.
 * &nbsp;&nbsp;&nbsp;default: Predicted
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); column names can be surrounded by double quotes.
 * </pre>
 * 
 * <pre>-grid &lt;int&gt; (property: grid)
 * &nbsp;&nbsp;&nbsp;The size of the grid to project the predictions onto.
 * &nbsp;&nbsp;&nbsp;default: 100
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-morphology &lt;ERODE|DILATE&gt; [-morphology ...] (property: morphologies)
 * &nbsp;&nbsp;&nbsp;The morphologies to apply.
 * &nbsp;&nbsp;&nbsp;default: DILATE
 * </pre>
 * 
 * <pre>-num-cycles &lt;int&gt; (property: numCycles)
 * &nbsp;&nbsp;&nbsp;The number of cycles to apply.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PredictionEccentricity
  extends AbstractTransformer {

  private static final long serialVersionUID = 4894024583214919405L;

  /**
   * Enumeration of morphology cycles.
   *
   * @author FracPete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum Morphology {
    ERODE,
    DILATE,
  }

  /** the column with the actual values. */
  protected SpreadSheetColumnIndex m_Actual;

  /** the column with the predicted values. */
  protected SpreadSheetColumnIndex m_Predicted;

  /** the size of the grid. */
  protected int m_Grid;

  /** the morphologies to apply. */
  protected Morphology[] m_Morphologies;

  /** the number of cycles to apply. */
  protected int m_NumCycles;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Treats the predictions from a regressor as an image and computes the "
	+ "'eccentricity' of the actual vs predicted plot. Generated values "
	+ "range from 1 to infinity with a value of 1 representing a circular shape.\n"
	+ "Projects the predictions onto the specified grid, using the 'actual' "
	+ "for both axes.\n\n"
	+ "For more details see:\n"
	+ "https://en.wikipedia.org/wiki/Eccentricity_(mathematics)";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "actual", "actual",
      new SpreadSheetColumnIndex("Actual"));

    m_OptionManager.add(
      "predicted", "predicted",
      new SpreadSheetColumnIndex("Predicted"));

    m_OptionManager.add(
      "grid", "grid",
      100, 1, null);

    m_OptionManager.add(
      "morphology", "morphologies",
      new Morphology[]{Morphology.DILATE});

    m_OptionManager.add(
      "num-cycles", "numCycles",
      1, 0, null);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "grid", m_Grid, "grid: ");
    result += QuickInfoHelper.toString(this, "morphologies", m_Morphologies, ", morphologies: ");
    result += QuickInfoHelper.toString(this, "numCycles", m_NumCycles, ", #cycles: ");

    return result;
  }

  /**
   * Sets the column with the actual values.
   *
   * @param value	the column
   */
  public void setActual(SpreadSheetColumnIndex value) {
    m_Actual = value;
    reset();
  }

  /**
   * Returns the column with the actual values.
   *
   * @return		the range
   */
  public SpreadSheetColumnIndex getActual() {
    return m_Actual;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String actualTipText() {
    return "The column with the actual values.";
  }

  /**
   * Sets the column with the predicted values.
   *
   * @param value	the column
   */
  public void setPredicted(SpreadSheetColumnIndex value) {
    m_Predicted = value;
    reset();
  }

  /**
   * Returns the column with the predicted values.
   *
   * @return		the range
   */
  public SpreadSheetColumnIndex getPredicted() {
    return m_Predicted;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String predictedTipText() {
    return "The column with the predicted values.";
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
   * Sets the morphologies to apply.
   *
   * @param value	the morphologies
   */
  public void setMorphologies(Morphology[] value) {
    m_Morphologies = value;
    reset();
  }

  /**
   * Returns the morphologies to apply.
   *
   * @return		the morphologies
   */
  public Morphology[] getMorphologies() {
    return m_Morphologies;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String morphologiesTipText() {
    return "The morphologies to apply.";
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
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{SpreadSheet.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{PredictionEccentricityContainer.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    SpreadSheet		sheet;
    boolean[][]		predictions;
    double[]		actual;
    double[]		predicted;
    double		min;
    double		max;
    double		range;
    int			i;
    int			x;
    int			y;

    result = null;
    sheet  = (SpreadSheet) m_InputToken.getPayload();
    m_Actual.setData(sheet);
    m_Predicted.setData(sheet);
    if (m_Actual.getIntIndex() == -1)
      result = "'actual' column not found? Provided: " + m_Actual;
    else if (m_Predicted.getIntIndex() == -1)
      result = "'predicted' column not found? Provided: " + m_Predicted;

    actual    = null;
    predicted = null;
    if (result == null) {
      actual = SpreadSheetUtils.getNumericColumn(sheet, m_Actual.getIntIndex());
      predicted = SpreadSheetUtils.getNumericColumn(sheet, m_Predicted.getIntIndex());
      if (actual.length != predicted.length)
	result = "Differing number of actual and predicted values: " + actual.length + " != " + predicted.length;
    }

    if (result == null) {
      min         = StatUtils.min(actual);
      max         = StatUtils.max(actual);
      range       = max - min;
      predictions = new boolean[m_Grid][m_Grid];
      for (i = 0; i < actual.length; i++) {
	x = (int) Math.round((actual[i] - min) / range * m_Grid);
	y = m_Grid - 1 - (int) Math.round((predicted[i] - min) / range * m_Grid);
	if ((x >= 0) && (x < m_Grid) && (y >= 0) && (y < m_Grid))
	  predictions[y][x] = true;
      }

      for (i = 0; i < m_NumCycles; i++) {
        for (Morphology morphology: m_Morphologies) {
          switch (morphology) {
            case ERODE:
              predictions = BinaryMorphology.erode(predictions);
              break;
            case DILATE:
              predictions = BinaryMorphology.dilate(predictions);
              break;
            default:
              throw new IllegalStateException("Unsupported morphology: " + morphology);
          }
        }
      }

      m_OutputToken = new Token(
	new PredictionEccentricityContainer(
	  sheet,
	  MomentHelper.eccentricity(predictions),
	  new BooleanArrayMatrixView(predictions)));
    }

    return result;
  }
}
