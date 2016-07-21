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
 * TextStatistics.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.classifytab;

import adams.core.Utils;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseTextArea;
import adams.gui.core.Fonts;

/**
 * Generates basic text statistic.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
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
   * The title to use for the tab.
   *
   * @return		the title
   */
  public String getTitle() {
    return "Statistics";
  }

  /**
   * Generates output and adds it to the {@link ResultItem}.
   *
   * @param item	the item to add the output to
   * @return		null if output could be generated, otherwise error message
   */
  @Override
  public String generateOutput(ResultItem item) {
    StringBuilder	buffer;
    BaseTextArea 	text;

    buffer = new StringBuilder();

    // summary
    buffer.append(item.getEvaluation().toSummaryString(m_ComplexityStatistics));

    // confusion matrix
    if (m_ConfusionMatrix) {
      try {
	buffer.append("\n\n" + item.getEvaluation().toMatrixString());
      }
      catch (Exception e) {
	return Utils.handleException(this, "Failed to generate confusion matrix: ", e);
      }
    }

    // class details
    if (m_ClassDetails) {
      try {
	buffer.append("\n\n" + item.getEvaluation().toClassDetailsString());
      }
      catch (Exception e) {
	return Utils.handleException(this, "Failed to generate class details: ", e);
      }
    }

    text = new BaseTextArea();
    text.setTextFont(Fonts.getMonospacedFont());
    text.setText(buffer.toString());
    text.setCaretPosition(0);
    addTab(item, new BaseScrollPane(text));

    return null;
  }
}
