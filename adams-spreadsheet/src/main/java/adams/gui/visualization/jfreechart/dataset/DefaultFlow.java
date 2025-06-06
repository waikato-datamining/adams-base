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
 * DefaultFlowDataset.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.jfreechart.dataset;

import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import org.jfree.data.flow.DefaultFlowDataset;
import org.jfree.data.general.Dataset;

/**
 * Generates {@link DefaultFlowDataset}.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class DefaultFlow
  extends AbstractDatasetGenerator<org.jfree.data.flow.DefaultFlowDataset<String>> {

  private static final long serialVersionUID = -4393068845326967774L;

  /** the column with the optional stage. */
  protected SpreadSheetColumnIndex m_Stage;

  /** the column with the source. */
  protected SpreadSheetColumnIndex m_Source;

  /** the column with the target. */
  protected SpreadSheetColumnIndex m_Target;

  /** the column with the flow/weight. */
  protected SpreadSheetColumnIndex m_Flow;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates a JFree flow dataset from the incoming spreadsheet.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "stage", "stage",
      new SpreadSheetColumnIndex());

    m_OptionManager.add(
      "source", "source",
      new SpreadSheetColumnIndex("1"));

    m_OptionManager.add(
      "target", "target",
      new SpreadSheetColumnIndex("2"));

    m_OptionManager.add(
      "flow", "flow",
      new SpreadSheetColumnIndex("3"));
  }

  /**
   * Sets the stage column.
   *
   * @param value	the column
   */
  public void setStage(SpreadSheetColumnIndex value) {
    m_Stage = value;
    reset();
  }

  /**
   * Returns the stage column.
   *
   * @return		the column
   */
  public SpreadSheetColumnIndex getStage() {
    return m_Stage;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String stageTipText() {
    return "The (optional) column for the stage.";
  }

  /**
   * Sets the source column.
   *
   * @param value	the column
   */
  public void setSource(SpreadSheetColumnIndex value) {
    m_Source = value;
    reset();
  }

  /**
   * Returns the source column.
   *
   * @return		the column
   */
  public SpreadSheetColumnIndex getSource() {
    return m_Source;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String sourceTipText() {
    return "The column for the source.";
  }

  /**
   * Sets the target column.
   *
   * @param value	the column
   */
  public void setTarget(SpreadSheetColumnIndex value) {
    m_Target = value;
    reset();
  }

  /**
   * Returns the target column.
   *
   * @return		the column
   */
  public SpreadSheetColumnIndex getTarget() {
    return m_Target;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String targetTipText() {
    return "The column for the target.";
  }

  /**
   * Sets the flow/weight column.
   *
   * @param value	the column
   */
  public void setFlow(SpreadSheetColumnIndex value) {
    m_Flow = value;
    reset();
  }

  /**
   * Returns the flow/weight column.
   *
   * @return		the column
   */
  public SpreadSheetColumnIndex getFlow() {
    return m_Flow;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String flowTipText() {
    return "The column for the flow (= weight).";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "stage", (m_Stage.isEmpty() ? "-none-" : m_Stage), "stage: ");
    result += QuickInfoHelper.toString(this, "source", m_Source, ", source: ");
    result += QuickInfoHelper.toString(this, "target", m_Target, ", target: ");
    result += QuickInfoHelper.toString(this, "flow", m_Flow, ", flow: ");

    return result;
  }

  /**
   * Returns the class of dataset that it generates.
   *
   * @return the dataset class
   */
  @Override
  public Class<? extends Dataset> generates() {
    return DefaultFlowDataset.class;
  }

  /**
   * Hook method for checks before generating the dataset.
   *
   * @param data	the data to use
   * @return		null if checks passed, otherwise error message
   */
  @Override
  protected String check(SpreadSheet data) {
    String  	result;

    result = super.check(data);

    if (result == null) {
      m_Stage.setSpreadSheet(data);

      m_Source.setSpreadSheet(data);
      if (m_Source.getIntIndex() == -1)
	return "Failed to locate source column: " + m_Source.getIndex();

      m_Target.setSpreadSheet(data);
      if (m_Target.getIntIndex() == -1)
	return "Failed to locate target column: " + m_Target.getIndex();

      m_Flow.setSpreadSheet(data);
      if (m_Flow.getIntIndex() == -1)
	return "Failed to locate flow column: " + m_Flow.getIndex();
    }

    return result;
  }

  /**
   * Performs the actual generation of the dataset.
   *
   * @param data the data to use
   * @return the dataset
   */
  @Override
  protected DefaultFlowDataset<String> doGenerate(SpreadSheet data) {
    return addSeries(new DefaultFlowDataset<>(), data);
  }

  /**
   * Performs the actual addition of the series to the dataset.
   *
   * @param dataset the dataset to add the series to
   * @param data    the data to use
   * @return the updated dataset
   */
  @Override
  protected DefaultFlowDataset<String> doAddSeries(Dataset dataset, SpreadSheet data) {
    DefaultFlowDataset<String> 	result;
    int				colStage;
    int				colSource;
    int				colTarget;
    int				colFlow;

    result = (DefaultFlowDataset<String>) dataset;

    colStage = m_Stage.getIntIndex();
    colSource = m_Source.getIntIndex();
    colTarget = m_Target.getIntIndex();
    colFlow = m_Flow.getIntIndex();
    for (Row row: data.rows()) {
      result.setFlow(
	colStage == -1 ? 0 : row.getCell(colStage).toLong().intValue(),
	row.getCell(colSource).getContent(),
	row.getCell(colTarget).getContent(),
	row.getCell(colFlow).toDouble()
      );
    }

    return result;
  }
}
