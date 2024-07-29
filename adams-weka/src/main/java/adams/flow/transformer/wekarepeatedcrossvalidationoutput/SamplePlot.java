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
 * SamplePlot.java
 * Copyright (C) 2024 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.wekarepeatedcrossvalidationoutput;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.container.SequencePlotterContainer;
import adams.flow.container.SequencePlotterContainer.ContentType;
import adams.flow.container.WekaEvaluationContainer;
import adams.gui.tools.wekainvestigator.tab.classifytab.output.repeated.CenterStatistic;
import adams.gui.tools.wekainvestigator.tab.classifytab.output.repeated.LowerStatistic;
import adams.gui.tools.wekainvestigator.tab.classifytab.output.repeated.PredictionUtils;
import adams.gui.tools.wekainvestigator.tab.classifytab.output.repeated.UpperStatistic;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import weka.classifiers.Evaluation;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates plot containers with statistics derived for each sample across the cross-validation runs.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SamplePlot
  extends AbstractWekaRepeatedCrossValidationOutput<SequencePlotterContainer[]> {

  private static final long serialVersionUID = -6829245659118360739L;

  /** the statistic to generate. */
  protected CenterStatistic m_Statistic;

  /** the lower value to compute. */
  protected LowerStatistic m_Lower;

  /** the upper value to compute. */
  protected UpperStatistic m_Upper;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates plot containers with statistics derived for each sample across the cross-validation runs.";
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
   * Returns the class that it generates.
   *
   * @return the class
   */
  @Override
  public Class generates() {
    return SequencePlotterContainer[].class;
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "statistic", m_Statistic, "statistic: ");
    result += QuickInfoHelper.toString(this, "lower", m_Lower, ", lower: ");
    result += QuickInfoHelper.toString(this, "upper", m_Upper, ", upper: ");

    return result;
  }

  /**
   * Checks whether the cross-validation results can be processed.
   *
   * @param conts	the containers to check
   * @return		null if the data can be processed, otherwise an error message
   */
  public String handles(WekaEvaluationContainer[] conts) {
    Evaluation eval;

    if (conts.length < 2)
      return "At least two evaluation containers are required, provided: " + conts.length;

    if (!conts[0].hasValue(WekaEvaluationContainer.VALUE_ORIGINALINDICES))
      return "No original indices available from containers!";

    eval = conts[0].getValue(WekaEvaluationContainer.VALUE_EVALUATION, Evaluation.class);
    if (eval.getHeader().classIndex() == -1)
      return "No class attribute set!";
    if (!eval.getHeader().classAttribute().isNumeric())
      return "Class attribute is not numeric: " + eval.getHeader().classAttribute().name();

    return null;
  }

  /**
   * Generates the data.
   *
   * @param conts the containers to process
   * @return the generated data
   */
  @Override
  protected SequencePlotterContainer[] doGenerateOutput(WekaEvaluationContainer[] conts) {
    List<SequencePlotterContainer>	result;
    MessageCollection			errors;
    SpreadSheet				stats;
    Row					row;
    int					r;
    TIntList				cols;
    SequencePlotterContainer		point;
    int					i;

    result = new ArrayList<>();
    cols   = new TIntArrayList();
    errors = new MessageCollection();
    stats  = PredictionUtils.calcStats(conts, null, errors, m_Statistic, m_Lower, m_Upper, -1, getLogger(), cols, true);
    if (!errors.isEmpty())
      throw new IllegalStateException("Failed to generated statistics: " + errors);

    // create plot data
    for (r = 0; r < stats.getRowCount(); r++) {
      row = stats.getRow(r);

      point = new SequencePlotterContainer(
	conts[0].getValue(WekaEvaluationContainer.VALUE_EVALUATION, Evaluation.class).getHeader().relationName(),
	row.getCell(cols.get(3)).toDouble(),
	row.getCell(cols.get(0)).toDouble(),
	null,
	new Double[]{row.getCell(cols.get(1)).toDouble(), row.getCell(cols.get(2)).toDouble()},
	ContentType.PLOT);

      // meta-data?
      if (cols.get(0) > 0) {
	for (i = 0; i < cols.get(0); i++)
	  point.addMetaData(stats.getHeaderRow().getCell(i).getContent(), row.getCell(i).getNative());
      }

      // add to sequence
      result.add(point);
    }

    return result.toArray(new SequencePlotterContainer[0]);
  }
}
