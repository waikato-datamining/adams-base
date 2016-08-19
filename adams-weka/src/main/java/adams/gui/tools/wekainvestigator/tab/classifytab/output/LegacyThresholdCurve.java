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
 * LegacyMarginCurve.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.classifytab.output;

import adams.core.Range;
import adams.data.weka.WekaLabelRange;
import adams.flow.core.Token;
import adams.flow.sink.WekaThresholdCurve;
import adams.flow.sink.WekaThresholdCurve.AttributeName;
import adams.gui.tools.wekainvestigator.output.ComponentContentPanel;
import adams.gui.tools.wekainvestigator.tab.classifytab.ResultItem;

import javax.swing.JPanel;

/**
 * Generates margin curve (legacy Weka output).
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class LegacyThresholdCurve
  extends AbstractOutputGenerator {

  private static final long serialVersionUID = -6829245659118360739L;

  /** the class label indices. */
  protected WekaLabelRange m_ClassLabelRange;

  /** the attribute on the X axis. */
  protected AttributeName m_AttributeX;

  /** the attribute on the Y axis. */
  protected AttributeName m_AttributeY;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates margin curve (legacy Weka output).";
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

    m_OptionManager.add(
	    "attribute-x", "attributeX",
	    AttributeName.FP_RATE);

    m_OptionManager.add(
	    "attribute-y", "attributeY",
	    AttributeName.TP_RATE);
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
   * Sets the attribute to show on the X axis.
   *
   * @param value 	the attribute
   */
  public void setAttributeX(AttributeName value) {
    m_AttributeX = value;
    reset();
  }

  /**
   * Returns the attribute to show on the X axis.
   *
   * @return 		the attribute
   */
  public AttributeName getAttributeX() {
    return m_AttributeX;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String attributeXTipText() {
    return "The attribute to show on the X axis.";
  }

  /**
   * Sets the attribute to show on the Y axis.
   *
   * @param value 	the attribute
   */
  public void setAttributeY(AttributeName value) {
    m_AttributeY = value;
    reset();
  }

  /**
   * Returns the attribute to show on the Y axis.
   *
   * @return 		the attribute
   */
  public AttributeName getAttributeY() {
    return m_AttributeY;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String attributeYTipText() {
    return "The attribute to show on the Y axis.";
  }

  /**
   * The title to use for the tab.
   *
   * @return		the title
   */
  public String getTitle() {
    return "Threshold curve (legacy)";
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
   * Generates output and adds it to the {@link ResultItem}.
   *
   * @param item	the item to add the output to
   * @return		null if output could be generated, otherwise error message
   */
  @Override
  public String generateOutput(ResultItem item) {
    WekaThresholdCurve 	sink;
    JPanel 		panel;

    sink  = new WekaThresholdCurve();
    sink.setClassLabelRange(m_ClassLabelRange);
    sink.setAttributeX(m_AttributeX);
    sink.setAttributeY(m_AttributeY);
    panel = sink.createDisplayPanel(new Token(item.getEvaluation()));

    addTab(item, new ComponentContentPanel(panel, sink.displayPanelRequiresScrollPane()));

    return null;
  }
}
