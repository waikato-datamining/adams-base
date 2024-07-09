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
 * Predictions.java
 * Copyright (C) 2024 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.classifytab.output.repeated;

import adams.core.MessageCollection;
import adams.data.spreadsheet.SpreadSheet;
import adams.gui.core.SpreadSheetTable;
import adams.gui.tools.wekainvestigator.output.TableContentPanel;
import adams.gui.tools.wekainvestigator.tab.classifytab.ResultItem;
import adams.gui.tools.wekainvestigator.tab.classifytab.output.AbstractOutputGenerator;

import javax.swing.JComponent;

/**
 * Generates statistics for predictions from repeated cross-validation runs.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Predictions
  extends AbstractOutputGenerator {

  private static final long serialVersionUID = -6829245659118360739L;

  /** the statistic to generate. */
  protected CenterStatistic m_Statistic;

  /** the lower value to compute. */
  protected LowerStatistic m_Lower;

  /** the upper value to compute. */
  protected UpperStatistic m_Upper;

  /** the number of decimals to round to. */
  protected int m_NumDecimals;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates statistics for predictions from repeated cross-validation runs.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "statistic", "statistic",
      CenterStatistic.MEDIAN);

    m_OptionManager.add(
      "lower", "lower",
      LowerStatistic.QUARTILE25);

    m_OptionManager.add(
      "upper", "upper",
      UpperStatistic.QUARTILE75);

    m_OptionManager.add(
      "num-decimals", "numDecimals",
      3, 0, null);
  }

  /**
   * Sets the statistic to output.
   *
   * @param value	the statistic
   */
  public void setStatistic(CenterStatistic value) {
    m_Statistic = value;
    reset();
  }

  /**
   * Returns the statistic to output.
   *
   * @return		the statistic
   */
  public CenterStatistic getStatistic() {
    return m_Statistic;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String statisticTipText() {
    return "The statistic to output.";
  }

  /**
   * Sets the lower value to output.
   *
   * @param value	the lower value
   */
  public void setLower(LowerStatistic value) {
    m_Lower = value;
    reset();
  }

  /**
   * Returns the lower value to output.
   *
   * @return		the lower value
   */
  public LowerStatistic getLower() {
    return m_Lower;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String lowerTipText() {
    return "The lower value to output.";
  }

  /**
   * Sets the upper value to output.
   *
   * @param value	the upper value
   */
  public void setUpper(UpperStatistic value) {
    m_Upper = value;
    reset();
  }

  /**
   * Returns the upper value to output.
   *
   * @return		the upper value
   */
  public UpperStatistic getUpper() {
    return m_Upper;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String upperTipText() {
    return "The upper value to output.";
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
    return "Predictions (RCV)";
  }

  /**
   * Checks whether output can be generated from this item.
   *
   * @param item	the item to check
   * @return		true if output can be generated
   */
  public boolean canGenerateOutput(ResultItem item) {
    return item.hasRunEvaluations()
	     && item.hasRunOriginalIndices()
	     && (item.getHeader().classIndex() > -1)
	     && item.getHeader().classAttribute().isNumeric();
  }

  /**
   * Generates output from the item.
   *
   * @param item	the item to generate output for
   * @param errors	for collecting error messages
   * @return		the output component, null if failed to generate
   */
  public JComponent createOutput(ResultItem item, MessageCollection errors) {
    SpreadSheetTable	table;
    SpreadSheet		stats;

    stats = PredictionUtils.calcStats(item, errors, m_Statistic, m_Lower, m_Upper, m_NumDecimals, getLogger(), null);
    table = new SpreadSheetTable(stats);

    return new TableContentPanel(table, true, true);
  }
}
