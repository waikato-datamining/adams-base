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
 * ClassifierErrors.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.classifytab.output;

import adams.flow.core.Token;
import adams.flow.sink.ActualVsPredictedPlot;
import adams.flow.sink.ActualVsPredictedPlot.LimitType;
import adams.flow.transformer.WekaPredictionsToSpreadSheet;
import adams.gui.tools.wekainvestigator.output.ComponentContentPanel;
import adams.gui.tools.wekainvestigator.tab.classifytab.ResultItem;

import javax.swing.JPanel;

/**
 * Generates classifier errors plot.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ClassifierErrors
  extends AbstractOutputGenerator {

  private static final long serialVersionUID = -6829245659118360739L;

  /** the limit type. */
  protected LimitType m_Limit;

  /** the minimum to use for the actual values (neg inf = no restriction). */
  protected double m_ActualMin;

  /** the maximum to use for the actual values (pos inf = no restriction). */
  protected double m_ActualMax;

  /** the minimum to use for the predicted values (neg inf = no restriction). */
  protected double m_PredictedMin;

  /** the maximum to use for the predicted values (pos inf = no restriction). */
  protected double m_PredictedMax;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates classifier errors plot.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "actual-min", "actualMin",
      Double.NEGATIVE_INFINITY);

    m_OptionManager.add(
      "actual-max", "actualMax",
      Double.POSITIVE_INFINITY);

    m_OptionManager.add(
      "predicted-min", "predictedMin",
      Double.NEGATIVE_INFINITY);

    m_OptionManager.add(
      "predicted-max", "predictedMax",
      Double.POSITIVE_INFINITY);

    m_OptionManager.add(
      "limit", "limit",
      LimitType.NONE);
  }

  /**
   * Sets the lower limit in use for the actual values.
   *
   * @param value	the limit, neg inf if unlimited
   */
  public void setActualMin(double value) {
    m_ActualMin = value;
    reset();
  }

  /**
   * Returns the lower limit in use for the actual values.
   *
   * @return		the limit, neg inf if unlimited
   */
  public double getActualMin() {
    return m_ActualMin;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String actualMinTipText() {
    return "The minimum to use for the display of the actual axis; use " + Double.NaN + " for unlimited.";
  }

  /**
   * Sets the upper limit in use for the actual values.
   *
   * @param value	the limit, pos inf if unlimited
   */
  public void setActualMax(double value) {
    m_ActualMax = value;
    reset();
  }

  /**
   * Returns the upper limit in use for the actual values.
   *
   * @return		the limit, pos inf if unlimited
   */
  public double getActualMax() {
    return m_ActualMax;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String actualMaxTipText() {
    return "The maximum to use for the display of the actual axis; use " + Double.NaN + " for unlimited.";
  }

  /**
   * Sets the lower limit in use for the predicted values.
   *
   * @param value	the limit, neg inf if unlimited
   */
  public void setPredictedMin(double value) {
    m_PredictedMin = value;
    reset();
  }

  /**
   * Returns the lower limit in use for the predicted values.
   *
   * @return		the limit, neg inf if unlimited
   */
  public double getPredictedMin() {
    return m_PredictedMin;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String predictedMinTipText() {
    return "The minimum to use for the display of the predicted axis; use " + Double.NaN + " for unlimited.";
  }

  /**
   * Sets the upper limit in use for the predicted values.
   *
   * @param value	the limit, pos inf if unlimited
   */
  public void setPredictedMax(double value) {
    m_PredictedMax = value;
    reset();
  }

  /**
   * Returns the upper limit in use for the predicted values.
   *
   * @return		the limit, pos inf if unlimited
   */
  public double getPredictedMax() {
    return m_PredictedMax;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String predictedMaxTipText() {
    return "The maximum to use for the display of the predicted axis; use " + Double.NaN + " for unlimited.";
  }

  /**
   * Sets the limit to impose on the axes.
   *
   * @param value	the limit type
   */
  public void setLimit(LimitType value) {
    m_Limit = value;
    reset();
  }

  /**
   * Returns the limit to impose on the axes.
   *
   * @return		the limit type
   */
  public LimitType getLimit() {
    return m_Limit;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String limitTipText() {
    return
      "The type of limit to impose on the axes; NONE just uses the range "
	+ "determined from the data; ACTUAL uses the min/max from the actual "
	+ "column for both axes; SPECIFIED uses the specified limits or, if "
	+ "a value is 'infinity' then the corresponding value from the "
	+ "determine range.";
  }

  /**
   * The title to use for the tab.
   *
   * @return		the title
   */
  public String getTitle() {
    return "Errors";
  }

  /**
   * Checks whether output can be generated from this item.
   *
   * @param item	the item to check
   * @return		true if output can be generated
   */
  public boolean canGenerateOutput(ResultItem item) {
    return item.hasEvaluation() && (item.getEvaluation().predictions() != null);
  }

  /**
   * Generates output and adds it to the {@link ResultItem}.
   *
   * @param item	the item to add the output to
   * @return		null if output could be generated, otherwise error message
   */
  @Override
  public String generateOutput(ResultItem item) {
    ActualVsPredictedPlot 		sink;
    JPanel 				panel;
    WekaPredictionsToSpreadSheet	p2s;
    Token				token;

    p2s = new WekaPredictionsToSpreadSheet();
    p2s.input(new Token(item.getEvaluation()));
    p2s.execute();
    token = p2s.output();

    sink  = new ActualVsPredictedPlot();
    sink.setActualMin(m_ActualMin);
    sink.setActualMax(m_ActualMax);
    sink.setPredictedMin(m_PredictedMin);
    sink.setPredictedMax(m_PredictedMax);
    sink.setLimit(m_Limit);
    panel = sink.createDisplayPanel(token);

    addTab(item, new ComponentContentPanel(panel, sink.displayPanelRequiresScrollPane()));

    return null;
  }
}
