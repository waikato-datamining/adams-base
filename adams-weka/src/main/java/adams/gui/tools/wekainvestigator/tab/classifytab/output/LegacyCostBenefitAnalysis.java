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
 * LegacyCostBenefitAnalysis.java
 * Copyright (C) 2016-2025 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.classifytab.output;

import adams.core.MessageCollection;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.weka.WekaLabelIndex;
import adams.flow.core.Token;
import adams.flow.sink.WekaCostBenefitAnalysis;
import adams.gui.core.GUIHelper;
import adams.gui.tools.wekainvestigator.output.ComponentContentPanel;
import adams.gui.tools.wekainvestigator.tab.classifytab.ResultItem;
import weka.classifiers.Evaluation;

import javax.swing.JPanel;

/**
 * Generates cost benefit analysis (legacy Weka output).
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class LegacyCostBenefitAnalysis
  extends AbstractOutputGeneratorWithSeparateFoldsSupport<ComponentContentPanel> {

  private static final long serialVersionUID = -6829245659118360739L;

  /** the index of the class label. */
  protected WekaLabelIndex m_ClassIndex;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates cost benefit analysis (legacy Weka output).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "index", "classIndex",
	    new WekaLabelIndex(WekaLabelIndex.FIRST));
  }

  /**
   * Sets the index of class label (1-based).
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
    return "The range of class label indices.";
  }

  /**
   * The title to use for the tab.
   *
   * @return		the title
   */
  public String getTitle() {
    return "Cost benefit (legacy)";
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
    WekaCostBenefitAnalysis 	sink;
    JPanel 			panel;

    sink  = new WekaCostBenefitAnalysis();
    sink.setClassIndex(m_ClassIndex);
    panel = sink.createDisplayPanel(new Token(eval));
    panel.setPreferredSize(GUIHelper.getDefaultDialogDimension());

    return new ComponentContentPanel(panel, sink.displayPanelRequiresScrollPane());
  }
}
