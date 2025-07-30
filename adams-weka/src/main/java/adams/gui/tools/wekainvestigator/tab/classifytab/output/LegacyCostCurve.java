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
 * LegacyCostCurve.java
 * Copyright (C) 2016-2025 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.classifytab.output;

import adams.core.MessageCollection;
import adams.core.Range;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.weka.WekaLabelRange;
import adams.flow.core.Token;
import adams.flow.sink.WekaCostCurve;
import adams.gui.tools.wekainvestigator.output.ComponentContentPanel;
import adams.gui.tools.wekainvestigator.tab.classifytab.ResultItem;
import weka.classifiers.Evaluation;

import javax.swing.JPanel;

/**
 * Generates cost curve (legacy Weka output).
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class LegacyCostCurve
  extends AbstractOutputGeneratorWithSeparateFoldsSupport<ComponentContentPanel> {

  private static final long serialVersionUID = -6829245659118360739L;

  /** the class label range. */
  protected WekaLabelRange m_ClassLabelRange;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates cost curve (legacy Weka output).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "index", "classLabelRange",
	    new WekaLabelRange(Range.FIRST));
  }

  /**
   * Sets the class label indices.
   *
   * @param value 	the range
   */
  public void setClassLabelRange(WekaLabelRange value) {
    m_ClassLabelRange = value;
    reset();
  }

  /**
   * Returns the class label indices.
   *
   * @return 		the range
   */
  public WekaLabelRange getClassLabelRange() {
    return m_ClassLabelRange;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String classLabelRangeTipText() {
    return "The indices of the class labels to use for the plot.";
  }

  /**
   * The title to use for the tab.
   *
   * @return		the title
   */
  public String getTitle() {
    return "Cost curve (legacy)";
  }

  /**
   * Checks whether output can be generated from this item.
   *
   * @param item	the item to check
   * @return		true if output can be generated
   */
  public boolean canGenerateOutput(ResultItem item) {
    return item.hasEvaluation() && (item.getEvaluation().predictions() != null) && item.getEvaluation().getHeader().classAttribute().isNominal();
  }

  /**
   * Generates the output for the evaluation.
   *
   * @param eval		the evaluation to use as basis
   * @param originalIndices 	the original indices to use, can be null
   * @param additionalAttributes the additional attributes to display, can be null
   * @param errors 		for collecting errors
   * @return			the generated table, null if failed to generate
   */
  @Override
  protected ComponentContentPanel createOutput(Evaluation eval, int[] originalIndices, SpreadSheet additionalAttributes, MessageCollection errors) {
    WekaCostCurve	sink;
    JPanel 		panel;

    sink  = new WekaCostCurve();
    sink.setClassLabelRange(m_ClassLabelRange);
    panel = sink.createDisplayPanel(new Token(eval));

    return new ComponentContentPanel(panel, sink.displayPanelRequiresScrollPane());
  }
}
