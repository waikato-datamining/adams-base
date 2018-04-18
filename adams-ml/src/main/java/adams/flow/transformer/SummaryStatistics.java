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
 * SummaryStatistics.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.data.spreadsheet.SpreadSheetColumnRange;
import adams.data.spreadsheet.SpreadSheetUtils;
import adams.flow.core.Token;
import adams.flow.transformer.summarystatistics.CategoricalSummaryStatistic;
import adams.flow.transformer.summarystatistics.NumericSummaryStatistic;
import adams.flow.transformer.summarystatistics.SummaryStatistic;

/**
 <!-- globalinfo-start -->
 * Calculates the selected summary statistics and outputs a spreadsheet.
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
 * &nbsp;&nbsp;&nbsp;default: SummaryStatistics
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
 * <pre>-class-distribution-range &lt;adams.data.spreadsheet.SpreadSheetColumnRange&gt; (property: classDistributionRange)
 * &nbsp;&nbsp;&nbsp;The (optional) columns with the class distributions; if not available probability
 * &nbsp;&nbsp;&nbsp;of 1 is assumed for predicted label.
 * &nbsp;&nbsp;&nbsp;default:
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; column names (case-sensitive) as well as the following placeholders can be used: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); column names can be surrounded by double quotes.
 * </pre>
 *
 * <pre>-statistic &lt;adams.flow.transformer.summarystatistics.SummaryStatistic&gt; [-statistic ...] (property: statistics)
 * &nbsp;&nbsp;&nbsp;The statistics to compute.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SummaryStatistics
  extends AbstractSpreadSheetTransformer {

  private static final long serialVersionUID = 453666335915022509L;

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

  /** the column with the class distribution. */
  protected SpreadSheetColumnRange m_ClassDistributionRange;

  /** the statistics. */
  protected SummaryStatistic[] m_Statistics;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Calculates the selected summary statistics and outputs a spreadsheet.";
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
      "class-distribution-range", "classDistributionRange",
      new SpreadSheetColumnRange(""));

    m_OptionManager.add(
      "statistic", "statistics",
      new SummaryStatistic[0]);
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
    result += QuickInfoHelper.toString(this, "classDistributionRange", (m_ClassDistributionRange.isEmpty() ? "-none-" : m_ClassDistributionRange.getRange()), ", class dist: ");

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
   * Sets the columns with the class distributions (optional).
   *
   * @param value	the range
   */
  public void setClassDistributionRange(SpreadSheetColumnRange value) {
    m_ClassDistributionRange = value;
    reset();
  }

  /**
   * Returns the columns with the class distributions (optional).
   *
   * @return		the range
   */
  public SpreadSheetColumnRange getClassDistributionRange() {
    return m_ClassDistributionRange;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String classDistributionRangeTipText() {
    return "The (optional) columns with the class distributions; if not available probability of 1 is assumed for predicted label.";
  }

  /**
   * Sets the statistics to calculate.
   *
   * @param value	the statistics
   */
  public void setStatistics(SummaryStatistic[] value) {
    m_Statistics = value;
    reset();
  }

  /**
   * Returns the statistics to calculate.
   *
   * @return		the statistics
   */
  public SummaryStatistic[] getStatistics() {
    return m_Statistics;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String statisticsTipText() {
    return "The statistics to compute.";
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    SpreadSheet			sheet;
    int				actCol;
    int				predCol;
    int				probCol;
    int[]			distCols;
    boolean			numeric;
    double[]			actNum;
    double[]			predNum;
    double[]			prob;
    double[][]			dist;
    String[]			actCat;
    String[]			predCat;
    SpreadSheet			stats;
    Row				row;
    NumericSummaryStatistic	numStat;
    CategoricalSummaryStatistic	catStat;
    String[]			names;
    double[]			values;
    String[]			labels;
    int				i;

    result = null;
    sheet  = m_InputToken.getPayload(SpreadSheet.class);
    m_ActualColumn.setData(sheet);
    m_PredictedColumn.setData(sheet);
    m_ProbabilityColumn.setData(sheet);
    m_ClassDistributionRange.setData(sheet);
    actCol   = m_ActualColumn.getIntIndex();
    predCol  = m_PredictedColumn.getIntIndex();
    probCol  = m_ProbabilityColumn.getIntIndex();
    distCols = m_ClassDistributionRange.getIntIndices();
    if (actCol == -1)
      result = "Actual column not found: " + m_ActualColumn;
    else if (predCol == -1)
      result = "Predicted column not found: " + m_PredictedColumn;

    if (result == null) {
      stats   = new DefaultSpreadSheet();
      row     = stats.getHeaderRow();
      row.addCell("K").setContentAsString("Statistic");
      row.addCell("V").setContentAsString("Value");
      numeric = sheet.isNumeric(actCol) && sheet.isNumeric(predCol);

      // get probabilities
      if (probCol != -1)
        prob = SpreadSheetUtils.getNumericColumn(sheet, probCol);
      else
        prob = null;

      // get class distributions
      if (distCols.length == 0) {
        dist   = null;
        labels = null;
      }
      else {
        dist   = new double[distCols.length][];
        labels = new String[distCols.length];
        for (i = 0; i < distCols.length; i++) {
	  dist[i]   = SpreadSheetUtils.getNumericColumn(sheet, distCols[i]);
	  labels[i] = sheet.getColumnName(distCols[i]);
	}
      }

      if (numeric) {
        actNum  = SpreadSheetUtils.getNumericColumn(sheet, actCol);
        predNum = SpreadSheetUtils.getNumericColumn(sheet, predCol);
        for (SummaryStatistic s: m_Statistics) {
          if (s instanceof NumericSummaryStatistic) {
            numStat = (NumericSummaryStatistic) s;
            numStat.setNumericActual(actNum);
            numStat.setNumericPredicted(predNum);
            names  = numStat.getNames();
            values = numStat.calculate();
            for (i = 0; i < names.length; i++) {
              row = stats.addRow();
              row.addCell("K").setContentAsString(names[i]);
              row.addCell("V").setContent(values[i]);
	    }
	  }
	  else {
            getLogger().warning("Statistic " + Utils.classToString(s) + " is not for numeric predictions, skipping!");
	  }
	}
      }
      else {
        actCat  = SpreadSheetUtils.getColumn(sheet, actCol, false, false, CategoricalSummaryStatistic.MISSING_CATEGORICAL);
        predCat = SpreadSheetUtils.getColumn(sheet, predCol, false, false, CategoricalSummaryStatistic.MISSING_CATEGORICAL);
        for (SummaryStatistic s: m_Statistics) {
          if (s instanceof CategoricalSummaryStatistic) {
            catStat = (CategoricalSummaryStatistic) s;
            catStat.setCategoricalActual(actCat);
            catStat.setCategoricalPredicted(predCat);
            catStat.setCategoricalProbabilities(prob);
            catStat.setCategoricalClassDistributions(dist);
            catStat.setCategoricalClassDistributionLabels(labels);
            names  = catStat.getNames();
            values = catStat.calculate();
            for (i = 0; i < names.length; i++) {
              row = stats.addRow();
              row.addCell("K").setContentAsString(names[i]);
              row.addCell("V").setContent(values[i]);
	    }
	  }
	  else {
            getLogger().warning("Statistic " + Utils.classToString(s) + " is not for categorical predictions, skipping!");
	  }
	}
      }
      m_OutputToken = new Token(stats);
    }

    return result;
  }
}
