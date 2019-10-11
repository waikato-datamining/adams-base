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
 * TextStatistics.java
 * Copyright (C) 2016-2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.classifytab.output;

import adams.core.MessageCollection;
import adams.core.logging.LoggingHelper;
import adams.data.spreadsheet.MetaData;
import adams.gui.core.BaseTextArea;
import adams.gui.core.Fonts;
import adams.gui.core.MultiPagePane;
import adams.gui.tools.wekainvestigator.output.RunInformationHelper;
import adams.gui.tools.wekainvestigator.output.TextualContentPanel;
import adams.gui.tools.wekainvestigator.tab.classifytab.ResultItem;
import com.github.fracpete.javautils.enumerate.Enumerated;
import weka.classifiers.Evaluation;

import javax.swing.JComponent;

import static com.github.fracpete.javautils.Enumerate.enumerate;

/**
 * Generates basic text statistic.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class TextStatistics
  extends AbstractOutputGenerator {

  private static final long serialVersionUID = -6829245659118360739L;

  /** whether to print the confusion matrix as well. */
  protected boolean m_ConfusionMatrix;

  /** whether to print the complexity statistics as well. */
  protected boolean m_ComplexityStatistics;

  /** whether to print the class details as well. */
  protected boolean m_ClassDetails;

  /** whether to print the run information as well. */
  protected boolean m_RunInformation;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates basic text statistic.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "confusion-matrix", "confusionMatrix",
      false);

    m_OptionManager.add(
      "complexity-stats", "complexityStatistics",
      false);

    m_OptionManager.add(
      "class-details", "classDetails",
      false);

    m_OptionManager.add(
      "run-information", "runInformation",
      false);
  }

  /**
   * Sets whether to output the confusion matrix as well.
   *
   * @param value	if true then the confusion matrix will be output as well
   */
  public void setConfusionMatrix(boolean value) {
    m_ConfusionMatrix = value;
    reset();
  }

  /**
   * Returns whether to output the confusion matrix as well.
   *
   * @return		true if the confusion matrix stats are output as well
   */
  public boolean getConfusionMatrix() {
    return m_ConfusionMatrix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String confusionMatrixTipText() {
    return "If set to true, then the confusion matrix will be output as well.";
  }

  /**
   * Sets whether to output complexity stats as well.
   *
   * @param value	if true then the complexity stats will be output as well
   */
  public void setComplexityStatistics(boolean value) {
    m_ComplexityStatistics = value;
    reset();
  }

  /**
   * Returns whether the complexity stats are output as well.
   *
   * @return		true if the complexity stats are output as well
   */
  public boolean getComplexityStatistics() {
    return m_ComplexityStatistics;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String complexityStatisticsTipText() {
    return "If set to true, then the complexity statistics will be output as well.";
  }

  /**
   * Sets whether the class details are output as well.
   *
   * @param value	if true then the class details are output as well
   */
  public void setClassDetails(boolean value) {
    m_ClassDetails = value;
    reset();
  }

  /**
   * Returns whether the class details are output as well.
   *
   * @return		true if the class details are output as well
   */
  public boolean getClassDetails() {
    return m_ClassDetails;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String classDetailsTipText() {
    return "If set to true, then the class details are output as well.";
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
    return "Statistics";
  }

  /**
   * Checks whether output can be generated from this item.
   *
   * @param item	the item to check
   * @return		true if output can be generated
   */
  public boolean canGenerateOutput(ResultItem item) {
    return item.hasEvaluation();
  }

  /**
   * Generates a panel from the results of the evaluation.
   *
   * @param eval	the evaluation to append
   * @param runInfo 	the run information to add
   * @return 		the generated panel
   */
  protected TextualContentPanel createOutput(Evaluation eval, MetaData runInfo) {
    StringBuilder 	buffer;
    BaseTextArea 	text;

    buffer = new StringBuilder();

    // summary
    try {
      buffer.append(eval.toSummaryString(m_ComplexityStatistics));
    }
    catch (Exception e) {
      buffer.append(eval.toSummaryString(false));
      LoggingHelper.handleException(this, "Failed to generate summary statistics: ", e);
    }

    // confusion matrix
    if (m_ConfusionMatrix) {
      try {
	buffer.append("\n\n" + eval.toMatrixString());
      }
      catch (Exception e) {
	LoggingHelper.handleException(this, "Failed to generate confusion matrix: ", e);
      }
    }

    // class details
    if (m_ClassDetails) {
      try {
	buffer.append("\n\n" + eval.toClassDetailsString());
      }
      catch (Exception e) {
	LoggingHelper.handleException(this, "Failed to generate class details: ", e);
      }
    }

    if (runInfo != null) {
      buffer.append("\n\n" + "=== Run information ===\n\n");
      buffer.append(RunInformationHelper.toString(runInfo.toSpreadSheet()));
    }

    text = new BaseTextArea();
    text.setEditable(false);
    text.setTextFont(Fonts.getMonospacedFont());
    text.setText(buffer.toString().trim());
    text.setCaretPosition(0);

    return new TextualContentPanel(text, true);
  }

  /**
   * Generates output from the item.
   *
   * @param item	the item to generate output for
   * @param errors	for collecting error messages
   * @return		the output component, null if failed to generate
   */
  public JComponent createOutput(ResultItem item, MessageCollection errors) {
    MultiPagePane	multiPage;

    if (item.hasFoldEvaluations()) {
      multiPage = newMultiPagePane();
      addPage(multiPage, "Full", createOutput(item.getEvaluation(), (m_RunInformation ? item.getRunInformation() : null)));
      for (Enumerated<Evaluation> eval: enumerate(item.getFoldEvaluations()))
	addPage(multiPage, "Fold " + (eval.index + 1), createOutput(item.getFoldEvaluations()[eval.index], null));
      if (multiPage.getPageCount() > 0)
	multiPage.setSelectedIndex(0);
      return multiPage;
    }
    else {
      return createOutput(item.getEvaluation(), m_RunInformation ? item.getRunInformation() : null);
    }
  }
}
