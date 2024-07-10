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
 * TextOutput.java
 * Copyright (C) 2024 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.experimenttab.output;

import adams.core.MessageCollection;
import adams.core.ObjectCopyHelper;
import adams.core.base.BaseString;
import adams.flow.container.WekaExperimentContainer;
import adams.flow.core.ExperimentStatistic;
import adams.flow.core.Token;
import adams.flow.transformer.WekaExperimentEvaluation;
import adams.gui.core.BaseTextArea;
import adams.gui.core.Fonts;
import adams.gui.tools.wekainvestigator.output.RunInformationHelper;
import adams.gui.tools.wekainvestigator.output.TextualContentPanel;
import adams.gui.tools.wekainvestigator.tab.experimenttab.ResultItem;
import weka.experiment.PairedCorrectedTTester;
import weka.experiment.ResultMatrix;
import weka.experiment.Tester;
import weka.gui.experiment.ExperimenterDefaults;

import javax.swing.JComponent;

/**
 * Generates textual output.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class TextOutput
  extends AbstractOutputGenerator {

  private static final long serialVersionUID = -6829245659118360739L;

  /** the tester class to use. */
  protected Tester m_Tester;

  /** the comparison field. */
  protected ExperimentStatistic m_ComparisonField;

  /** the significance. */
  protected double m_Significance;

  /** the test base. */
  protected int m_TestBase;

  /** the row (= datasets). */
  protected BaseString[] m_Row;

  /** the column (= classifiers). */
  protected BaseString[] m_Column;

  /** whether to swap rows and columns. */
  protected boolean m_SwapRowsAndColumns;

  /** the output format. */
  protected ResultMatrix m_OutputFormat;

  /** whether to output the header. */
  protected boolean m_OutputHeader;

  /** whether to print the run information as well. */
  protected boolean m_RunInformation;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates textual output.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    String[]		list;
    BaseString[]	str;
    int			i;

    super.defineOptions();

    m_OptionManager.add(
      "tester", "tester",
      new PairedCorrectedTTester());

    m_OptionManager.add(
      "comparison", "comparisonField",
      ExperimentStatistic.ROOT_MEAN_SQUARED_ERROR);

    m_OptionManager.add(
      "significance", "significance",
      0.05, 0.0001, 0.9999);

    m_OptionManager.add(
      "test", "testBase",
      0, 0, null);

    list = ExperimenterDefaults.getRow().split(",");
    str  = new BaseString[list.length];
    for (i = 0; i < list.length; i++)
      str[i] = new BaseString(list[i]);
    m_OptionManager.add(
      "row", "row",
      str);

    list = ExperimenterDefaults.getColumn().split(",");
    str  = new BaseString[list.length];
    for (i = 0; i < list.length; i++)
      str[i] = new BaseString(list[i]);
    m_OptionManager.add(
      "col", "column",
      str);

    m_OptionManager.add(
      "swap", "swapRowsAndColumns",
      false);

    m_OptionManager.add(
      "format", "outputFormat",
      ExperimenterDefaults.getOutputFormat());

    m_OptionManager.add(
      "header", "outputHeader",
      true);

    m_OptionManager.add(
      "run-information", "runInformation",
      false);
  }

  /**
   * Sets the Tester to use.
   *
   * @param value	the Tester
   */
  public void setTester(Tester value) {
    m_Tester = value;
    reset();
  }

  /**
   * Returns the Tester in use.
   *
   * @return		the Tester
   */
  public Tester getTester() {
    return m_Tester;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String testerTipText() {
    return "The testing algorithm to use for performing the evaluations.";
  }

  /**
   * Sets the comparison field.
   *
   * @param value	the field
   */
  public void setComparisonField(ExperimentStatistic value) {
    m_ComparisonField = value;
    reset();
  }

  /**
   * Returns the comparison field.
   *
   * @return		the string
   */
  public ExperimentStatistic getComparisonField() {
    return m_ComparisonField;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String comparisonFieldTipText() {
    return "The field to base the comparison of algorithms on.";
  }

  /**
   * Sets the significance level (0-1).
   *
   * @param value	the significance
   */
  public void setSignificance(double value) {
    m_Significance = value;
    reset();
  }

  /**
   * Returns the current significance level (0-1).
   *
   * @return		the significance
   */
  public double getSignificance() {
    return m_Significance;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String significanceTipText() {
    return "The significance level (0-1).";
  }

  /**
   * Sets the index of the test base.
   *
   * @param value	the index
   */
  public void setTestBase(int value) {
    m_TestBase = value;
    reset();
  }

  /**
   * Returns the index of the test base.
   *
   * @return		the index
   */
  public int getTestBase() {
    return m_TestBase;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String testBaseTipText() {
    return "The index of the test base (normally the first classifier, ie '0').";
  }

  /**
   * Sets the list of fields that identify a row.
   *
   * @param value	the list of fields
   */
  public void setRow(BaseString[] value) {
    m_Row = value;
    reset();
  }

  /**
   * Returns the list of fields that identify a row.
   *
   * @return		the array of fields
   */
  public BaseString[] getRow() {
    return m_Row;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String rowTipText() {
    return "The list of fields that define a row (normally the dataset).";
  }

  /**
   * Sets list of fields that identify a column.
   *
   * @param value	the list of fields
   */
  public void setColumn(BaseString[] value) {
    m_Column = value;
    reset();
  }

  /**
   * Returns the list of fields that identify a column.
   *
   * @return		the array of fields
   */
  public BaseString[] getColumn() {
    return m_Column;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String columnTipText() {
    return "The list of fields that define a column (normally the schemes).";
  }

  /**
   * Sets whether to swap rows and columns.
   *
   * @param value 	true if to swap rows and columns
   */
  public void setSwapRowsAndColumns(boolean value) {
    m_SwapRowsAndColumns = value;
    reset();
  }

  /**
   * Returns whether to swap rows and columns.
   *
   * @return 		true if swapping rows and columns
   */
  public boolean getSwapRowsAndColumns() {
    return m_SwapRowsAndColumns;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String swapRowsAndColumnsTipText() {
    return "If set to true, rows and columns will be swapped.";
  }

  /**
   * Sets the output format to use for generating the output.
   *
   * @param value	the format
   */
  public void setOutputFormat(ResultMatrix value) {
    m_OutputFormat = value;
    reset();
  }

  /**
   * Returns the output format in use for generating the output.
   *
   * @return		the format
   */
  public ResultMatrix getOutputFormat() {
    return m_OutputFormat;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputFormatTipText() {
    return "The output format for generating the output.";
  }

  /**
   * Sets whether to output the header of the result matrix as well.
   *
   * @param value 	true if to output the header as well
   */
  public void setOutputHeader(boolean value) {
    m_OutputHeader = value;
    reset();
  }

  /**
   * Returns whether to output the header of the result matrix as well.
   *
   * @return 		true if to output the header as well
   */
  public boolean getOutputHeader() {
    return m_OutputHeader;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputHeaderTipText() {
    return "If set to true, then a header describing the experiment evaluation will get output as well.";
  }

  /**
   * Sets whether the run information is output as well.
   *
   * @param value	if true then the run information is output as well
   */
  public void setRunInformation(boolean value) {
    m_RunInformation = value;
    reset();
  }

  /**
   * Returns whether the run information is output as well.
   *
   * @return		true if the run information is output as well
   */
  public boolean getRunInformation() {
    return m_RunInformation;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String runInformationTipText() {
    return "If set to true, then the run information is output as well.";
  }

  /**
   * The title to use for the tab.
   *
   * @return		the title
   */
  public String getTitle() {
    return "Text";
  }

  /**
   * Checks whether output can be generated from this item.
   *
   * @param item	the item to check
   * @return		true if output can be generated
   */
  public boolean canGenerateOutput(ResultItem item) {
    return item.hasExperiment();
  }

  /**
   * Generates output from the item.
   *
   * @param item	the item to generate output for
   * @param errors	for collecting error messages
   * @return		the output component, null if failed to generate
   */
  public JComponent createOutput(ResultItem item, MessageCollection errors) {
    WekaExperimentEvaluation	eval;
    String			msg;
    BaseTextArea 		textArea;
    Token			output;
    StringBuilder		text;

    eval = new WekaExperimentEvaluation();
    eval.setTester(ObjectCopyHelper.copyObject(m_Tester));
    eval.setComparisonField(m_ComparisonField);
    eval.setSignificance(m_Significance);
    eval.setTestBase(m_TestBase);
    eval.setRow(ObjectCopyHelper.copyObjects(m_Row));
    eval.setColumn(ObjectCopyHelper.copyObjects(m_Column));
    eval.setSwapRowsAndColumns(m_SwapRowsAndColumns);
    eval.setOutputFormat(ObjectCopyHelper.copyObject(m_OutputFormat));
    eval.setOutputHeader(m_OutputHeader);

    eval.input(new Token(item.getExperiment().getValue(WekaExperimentContainer.VALUE_INSTANCES)));
    msg = eval.execute();
    if (msg != null) {
      eval.cleanUp();
      getLogger().severe(msg);
      return null;
    }
    output = eval.output();
    if (output == null) {
      eval.cleanUp();
      getLogger().severe("No output generated?");
      return null;
    }

    text = new StringBuilder(output.getPayload(String.class));
    if (m_RunInformation) {
      text.append("\n\n");
      text.append(RunInformationHelper.toString(item.getRunInformation().toSpreadSheet()));
    }
    textArea = new BaseTextArea();
    textArea.setEditable(false);
    textArea.setTextFont(Fonts.getMonospacedFont());
    textArea.setText(text.toString());
    textArea.setCaretPosition(0);

    eval.cleanUp();

    return new TextualContentPanel(textArea, true);
  }
}
