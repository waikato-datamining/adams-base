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
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.flow.core.Token;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 <!-- globalinfo-start -->
 * Generates a confusion matrix from the specified actual and predicted columns containing class labels.<br>
 * Can take a probability column (of prediction) into account for generating weighted counts.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.SpreadSheet<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.SpreadSheet<br>
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
 * &nbsp;&nbsp;&nbsp;default: ConfusionMatrix
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
 * <pre>-actual-column &lt;adams.data.spreadsheet.SpreadSheetColumnIndex&gt; (property: actualColumn)
 * &nbsp;&nbsp;&nbsp;The column with the actual labels.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); column names can be surrounded by double quotes.
 * </pre>
 *
 * <pre>-actual-prefix &lt;java.lang.String&gt; (property: actualPrefix)
 * &nbsp;&nbsp;&nbsp;The prefix for the actual labels.
 * &nbsp;&nbsp;&nbsp;default: a:
 * </pre>
 *
 * <pre>-predicted-column &lt;adams.data.spreadsheet.SpreadSheetColumnIndex&gt; (property: predictedColumn)
 * &nbsp;&nbsp;&nbsp;The column with the predicted labels.
 * &nbsp;&nbsp;&nbsp;default: 2
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); column names can be surrounded by double quotes.
 * </pre>
 *
 * <pre>-predicted-prefix &lt;java.lang.String&gt; (property: predictedPrefix)
 * &nbsp;&nbsp;&nbsp;The prefix for the predicted labels.
 * &nbsp;&nbsp;&nbsp;default: p:
 * </pre>
 *
 * <pre>-probability-column &lt;adams.data.spreadsheet.SpreadSheetColumnIndex&gt; (property: probabilityColumn)
 * &nbsp;&nbsp;&nbsp;The (optional) column with the probabilities; if not available probability
 * &nbsp;&nbsp;&nbsp;of 1 is assumed.
 * &nbsp;&nbsp;&nbsp;default:
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); column names can be surrounded by double quotes.
 * </pre>
 *
 * <pre>-matrix-values &lt;COUNTS|PERCENTAGES|PERCENTAGES_PER_ROW&gt; (property: matrixValues)
 * &nbsp;&nbsp;&nbsp;The type of values to generate.
 * &nbsp;&nbsp;&nbsp;default: COUNTS
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ConfusionMatrix
  extends AbstractSpreadSheetTransformer {

  private static final long serialVersionUID = 6499246835313298302L;

  /**
   * Defines what values to show.
   */
  public enum MatrixValues {
    COUNTS,
    PERCENTAGES,
    PERCENTAGES_PER_ROW,
  }

  /** the column with the actual labels. */
  protected SpreadSheetColumnIndex m_ActualColumn;

  /** the optional prefix for the actual labels. */
  protected String m_ActualPrefix;

  /** the column with the predicted labels. */
  protected SpreadSheetColumnIndex m_PredictedColumn;

  /** the optional prefix for the predicted labels. */
  protected String m_PredictedPrefix;

  /** the column with the probabilities. */
  protected SpreadSheetColumnIndex m_ProbabilityColumn;

  /** what values to generate. */
  protected MatrixValues m_MatrixValues;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Generates a confusion matrix from the specified actual and predicted "
	+ "columns containing class labels.\n"
	+ "Can take a probability column (of prediction) into account for "
	+ "generating weighted counts.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "actual-column", "actualColumn",
      new SpreadSheetColumnIndex("1"));

    m_OptionManager.add(
      "actual-prefix", "actualPrefix",
      "a: ");

    m_OptionManager.add(
      "predicted-column", "predictedColumn",
      new SpreadSheetColumnIndex("2"));

    m_OptionManager.add(
      "predicted-prefix", "predictedPrefix",
      "p: ");

    m_OptionManager.add(
      "probability-column", "probabilityColumn",
      new SpreadSheetColumnIndex(""));

    m_OptionManager.add(
      "matrix-values", "matrixValues",
      MatrixValues.COUNTS);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "actualColumn", m_ActualColumn, "actual: ");
    result += QuickInfoHelper.toString(this, "predictedColumn", m_PredictedColumn, ", predicted: ");
    result += QuickInfoHelper.toString(this, "probabilityColumn", (m_ProbabilityColumn.isEmpty() ? "-none-" : m_ProbabilityColumn.getIndex()), ", probability: ");
    result += QuickInfoHelper.toString(this, "matrixValues", m_MatrixValues, ", values: ");

    return result;
  }

  /**
   * Sets the column of the actual labels.
   *
   * @param value	the index
   */
  public void setActualColumn(SpreadSheetColumnIndex value) {
    m_ActualColumn = value;
    reset();
  }

  /**
   * Returns the column of the actual labels.
   *
   * @return		the index
   */
  public SpreadSheetColumnIndex getActualColumn() {
    return m_ActualColumn;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String actualColumnTipText() {
    return "The column with the actual labels.";
  }

  /**
   * Sets the prefix of the actual labels.
   *
   * @param value	the prefix
   */
  public void setActualPrefix(String value) {
    m_ActualPrefix = value;
    reset();
  }

  /**
   * Returns the prefix of the actual labels.
   *
   * @return		the prefix
   */
  public String getActualPrefix() {
    return m_ActualPrefix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String actualPrefixTipText() {
    return "The prefix for the actual labels.";
  }

  /**
   * Sets the column of the predicted labels.
   *
   * @param value	the index
   */
  public void setPredictedColumn(SpreadSheetColumnIndex value) {
    m_PredictedColumn = value;
    reset();
  }

  /**
   * Returns the column of the predicted labels.
   *
   * @return		the index
   */
  public SpreadSheetColumnIndex getPredictedColumn() {
    return m_PredictedColumn;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String predictedColumnTipText() {
    return "The column with the predicted labels.";
  }

  /**
   * Sets the prefix of the predicted labels.
   *
   * @param value	the prefix
   */
  public void setPredictedPrefix(String value) {
    m_PredictedPrefix = value;
    reset();
  }

  /**
   * Returns the prefix of the predicted labels.
   *
   * @return		the prefix
   */
  public String getPredictedPrefix() {
    return m_PredictedPrefix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String predictedPrefixTipText() {
    return "The prefix for the predicted labels.";
  }

  /**
   * Sets the column with the probabilities (optional).
   *
   * @param value	the index
   */
  public void setProbabilityColumn(SpreadSheetColumnIndex value) {
    m_ProbabilityColumn = value;
    reset();
  }

  /**
   * Returns the column with the probabilities (optional).
   *
   * @return		the index
   */
  public SpreadSheetColumnIndex getProbabilityColumn() {
    return m_ProbabilityColumn;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String probabilityColumnTipText() {
    return "The (optional) column with the probabilities; if not available probability of 1 is assumed.";
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
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    SpreadSheet		sheet;
    int			actCol;
    int			predCol;
    int			probCol;
    SpreadSheet		matrix;
    Row			row;
    List<String> 	actLabels;
    List<String> 	predLabels;
    Map<String,Integer> actIndices;
    Map<String,Integer> predIndices;
    String 		actLabel;
    String 		predLabel;
    int			actIndex;
    int			predIndex;
    int			i;
    int			n;
    int			sum;
    double		val;

    result = null;
    sheet  = (SpreadSheet) m_InputToken.getPayload();
    m_ActualColumn.setData(sheet);
    m_PredictedColumn.setData(sheet);
    m_ProbabilityColumn.setData(sheet);
    actCol  = m_ActualColumn.getIntIndex();
    predCol = m_PredictedColumn.getIntIndex();
    probCol = m_ProbabilityColumn.getIntIndex();
    if (actCol == -1)
      result = "Actual column not found: " + m_ActualColumn;
    else if (predCol == -1)
      result = "Predicted column not found: " + m_PredictedColumn;

    if (result == null) {
      // set up matrix
      actIndices = new HashMap<>();
      predIndices = new HashMap<>();
      actLabels = sheet.getCellValues(actCol);
      predLabels = sheet.getCellValues(predCol);
      for (String label: actLabels) {
	if (!predLabels.contains(label))
	  predLabels.add(label);
      }
      for (String label: predLabels) {
	if (!actLabels.contains(label))
	  actLabels.add(label);
      }
      Collections.sort(predLabels);
      Collections.sort(actLabels);
      matrix = new DefaultSpreadSheet();
      row = matrix.getHeaderRow();
      row.addCell("0").setContentAsString("x");
      for (i = 0; i < predLabels.size(); i++) {
	row.addCell("" + (i + 1)).setContentAsString(m_PredictedPrefix + predLabels.get(i));
	predIndices.put(predLabels.get(i), i+1);
      }
      for (i = 0; i < actLabels.size(); i++) {
	row = matrix.addRow();
	for (n = 0; n < matrix.getColumnCount(); n++)
	  row.getCell(n).setContent(0);
	row.addCell(0).setContentAsString(m_ActualPrefix + actLabels.get(i));
	actIndices.put(actLabels.get(i), i);
      }

      // fill in matrix
      for (i = 0; i < sheet.getRowCount(); i++) {
	row = sheet.getRow(i);
	if (!row.hasCell(actCol) || row.getCell(actCol).isMissing())
	  continue;
	if (!row.hasCell(predCol) || row.getCell(predCol).isMissing())
	  continue;
	actLabel  = row.getCell(actCol).getContent();
	predLabel = row.getCell(predCol).getContent();
	actIndex  = actIndices.get(actLabel);
	predIndex = predIndices.get(predLabel);
	if (probCol == -1)
	  matrix.getCell(actIndex, predIndex).setContent(matrix.getCell(actIndex, predIndex).toLong() + 1);
	else
	  matrix.getCell(actIndex, predIndex).setContent(matrix.getCell(actIndex, predIndex).toDouble() + row.getCell(probCol).toDouble());
      }

      // post-process matrix
      switch (m_MatrixValues) {
	case COUNTS:
	  // do nothing
	  break;

	case PERCENTAGES:
	  sum = 0;
	  for (i = 0; i < matrix.getRowCount(); i++) {
	    for (n = 1; n < matrix.getColumnCount(); n++) {
	      sum += matrix.getCell(i, n).toLong();
	    }
	  }
	  if (sum > 0) {
	    for (i = 0; i < matrix.getRowCount(); i++) {
	      for (n = 1; n < matrix.getColumnCount(); n++) {
		matrix.getCell(i, n).setContent(matrix.getCell(i, n).toDouble() / sum);
	      }
	    }
	  }
	  break;

	case PERCENTAGES_PER_ROW:
	  for (i = 0; i < matrix.getRowCount(); i++) {
	    sum = 0;
	    for (n = 1; n < matrix.getColumnCount(); n++) {
	      sum += matrix.getCell(i, n).toLong();
	    }
	    if (sum > 0) {
	      for (n = 1; n < matrix.getColumnCount(); n++) {
		matrix.getCell(i, n).setContent(matrix.getCell(i, n).toDouble() / sum);
	      }
	    }
	  }
	  break;

	default:
	  throw new IllegalStateException("Unhandled matrix values: " + m_MatrixValues);
      }

      m_OutputToken = new Token(matrix);
    }

    return result;
  }
}
