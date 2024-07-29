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
 * Statistics.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer.wekarepeatedcrossvalidationoutput;

import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.statistics.StatUtils;
import adams.data.weka.WekaLabelIndex;
import adams.flow.container.WekaEvaluationContainer;
import adams.flow.core.EvaluationHelper;
import adams.flow.core.EvaluationStatistic;
import weka.classifiers.Evaluation;

import java.util.logging.Level;

/**
 * Generates mean/stddev for the specified statistics.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class Statistics
  extends AbstractWekaRepeatedCrossValidationOutput<SpreadSheet> {

  private static final long serialVersionUID = -3653513403897505442L;

  /** the statistics to output. */
  protected EvaluationStatistic[] m_Statistics;

  /** the index of the class label. */
  protected WekaLabelIndex m_ClassIndex;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates mean/stddev for the specified statistics.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "statistic", "statistics",
      new EvaluationStatistic[0]);

    m_OptionManager.add(
      "class-index", "classIndex",
      new WekaLabelIndex(WekaLabelIndex.FIRST));
  }

  /**
   * Sets the statistics to output.
   *
   * @param value	the statistics
   */
  public void setStatistics(EvaluationStatistic[] value) {
    m_Statistics = value;
    reset();
  }

  /**
   * Returns the statistics to output.
   *
   * @return		the statistics
   */
  public EvaluationStatistic[] getStatistics() {
    return m_Statistics;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String statisticsTipText() {
    return "The statistics to output.";
  }

  /**
   * Sets the index of class label index (1-based).
   *
   * @param value	the label index
   */
  public void setClassIndex(WekaLabelIndex value) {
    m_ClassIndex = value;
    reset();
  }

  /**
   * Returns the current index of class label (1-based).
   *
   * @return		the label index
   */
  public WekaLabelIndex getClassIndex() {
    return m_ClassIndex;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String classIndexTipText() {
    return "The index of class label (eg used for AUC).";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "statistics", m_Statistics, "stats: ");
    result += QuickInfoHelper.toString(this, "classIndex", m_ClassIndex, ", class index: ");

    return result;
  }

  /**
   * Returns the class that it generates.
   *
   * @return the class
   */
  @Override
  public Class generates() {
    return SpreadSheet.class;
  }

  /**
   * Checks whether the cross-validation results can be processed.
   *
   * @param conts the containers to check
   * @return null if the data can be processed, otherwise an error message
   */
  @Override
  public String handles(WekaEvaluationContainer[] conts) {
    if (m_Statistics.length == 0)
      return "No statistics defined!";
    if (conts.length < 2)
      return "At least two evaluation containers are required, provided: " + conts.length;
    return null;
  }

  /**
   * Generates the data.
   *
   * @param conts the containers to process
   * @return the generated data
   */
  @Override
  protected SpreadSheet doGenerateOutput(WekaEvaluationContainer[] conts) {
    SpreadSheet		result;
    Row			row;
    int			s;
    int			r;
    double[]		values;
    Evaluation		eval;

    result = new DefaultSpreadSheet();
    row    = result.getHeaderRow();
    row.addCell("N").setContent("Statistics");
    row.addCell("M").setContent("Mean");
    row.addCell("S").setContent("StdDev");

    for (s = 0; s < m_Statistics.length; s++) {
      values = new double[conts.length];
      for (r = 0; r < conts.length; r++) {
	eval = conts[r].getValue(WekaEvaluationContainer.VALUE_EVALUATION, Evaluation.class);
	m_ClassIndex.setData(eval.getHeader().classAttribute());
	try {
	  values[r] = EvaluationHelper.getValue(eval, m_Statistics[s], m_ClassIndex.getIntIndex());
	}
	catch (Exception e) {
	  values[r] = Double.NaN;
	  getLogger().log(Level.SEVERE, "Failed to evaluate statistic " + m_Statistics[s] + " for run #" + (r+1) + "!", e);
	}
      }
      row = result.addRow();
      row.addCell("N").setContent(m_Statistics[s].toDisplayShort());
      row.addCell("M").setContent(StatUtils.mean(values));
      row.addCell("S").setContent(StatUtils.stddev(values, true));
    }

    return result;
  }
}
