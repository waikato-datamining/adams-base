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
 * Copyright (C) 2024 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.classifytab.output.repeatedCV;

import adams.core.MessageCollection;
import adams.data.RoundingType;
import adams.data.RoundingUtils;
import adams.data.statistics.StatUtils;
import adams.data.weka.WekaLabelIndex;
import adams.flow.core.EvaluationHelper;
import adams.flow.core.EvaluationStatistic;
import adams.gui.core.SortableAndSearchableTable;
import adams.gui.tools.wekainvestigator.output.TableContentPanel;
import adams.gui.tools.wekainvestigator.tab.classifytab.ResultItem;
import adams.gui.tools.wekainvestigator.tab.classifytab.output.AbstractOutputGenerator;

import javax.swing.JComponent;
import java.util.logging.Level;

/**
 * Generates statistics for repeated cross-validation runs.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Statistics
  extends AbstractOutputGenerator {

  private static final long serialVersionUID = -6829245659118360739L;

  /** the statistics to output. */
  protected EvaluationStatistic[] m_Statistics;

  /** the index of the class label. */
  protected WekaLabelIndex m_ClassIndex;

  /** the number of decimals to round to. */
  protected int m_NumDecimals;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates statistics for repeated cross-validation runs.";
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

    m_OptionManager.add(
      "num-decimals", "numDecimals",
      3, 0, null);
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
   * Sets the number of decimals to use for numeric values.
   *
   * @param value	the decimals
   */
  public void setNumDecimals(int value) {
    m_NumDecimals = value;
    reset();
  }

  /**
   * Returns the number of decimals to use for numeric values.
   *
   * @return		the decimals
   */
  public int getNumDecimals() {
    return m_NumDecimals;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numDecimalsTipText() {
    return "The number of decimals to use for the numeric output.";
  }

  /**
   * The title to use for the tab.
   *
   * @return		the title
   */
  public String getTitle() {
    return "Statistics (RCV)";
  }

  /**
   * Checks whether output can be generated from this item.
   *
   * @param item	the item to check
   * @return		true if output can be generated
   */
  public boolean canGenerateOutput(ResultItem item) {
    return item.hasRunEvaluations();
  }

  /**
   * Generates output from the item.
   *
   * @param item	the item to generate output for
   * @param errors	for collecting error messages
   * @return		the output component, null if failed to generate
   */
  public JComponent createOutput(ResultItem item, MessageCollection errors) {
    TableContentPanel 		result;
    Object[][]			stats;
    int				s;
    int				r;
    SortableAndSearchableTable	table;
    double[]			values;

    stats = new Object[m_Statistics.length][3];
    for (s = 0; s < m_Statistics.length; s++) {
      values = new double[item.getRunEvaluations().length];
      for (r = 0; r < item.getRunEvaluations().length; r++) {
	m_ClassIndex.setData(item.getRunEvaluations()[r].getHeader().classAttribute());
	try {
	  values[r] = EvaluationHelper.getValue(item.getRunEvaluations()[r], m_Statistics[s], m_ClassIndex.getIntIndex());
	}
	catch (Exception e) {
	  values[r] = Double.NaN;
	  getLogger().log(Level.SEVERE, "Failed to evaluate statistic " + m_Statistics[s] + " for run #" + (r+1) + "!", e);
	}
      }
      stats[s][0] = m_Statistics[s].toDisplayShort();
      stats[s][1] = RoundingUtils.apply(RoundingType.ROUND, StatUtils.mean(values), m_NumDecimals);
      stats[s][2] = RoundingUtils.apply(RoundingType.ROUND, StatUtils.stddev(values, true), m_NumDecimals);
    }

    table  = new SortableAndSearchableTable(stats, new String[]{"Statistic", "Mean", "StdDev"});
    table.setShowSimplePopupMenus(true);
    table.setAutoResizeMode(SortableAndSearchableTable.AUTO_RESIZE_OFF);
    table.setOptimalColumnWidth();
    result = new TableContentPanel(table, true);

    return result;
  }
}
