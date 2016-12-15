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

import adams.core.AutoOnOff;
import adams.core.MessageCollection;
import adams.core.Utils;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.data.spreadsheet.SpreadSheetColumnRange;
import adams.flow.core.Token;
import adams.flow.sink.ActualVsPredictedPlot;
import adams.flow.sink.ActualVsPredictedPlot.LimitType;
import adams.gui.core.GUIHelper;
import adams.gui.tools.wekainvestigator.output.ComponentContentPanel;
import adams.gui.tools.wekainvestigator.tab.classifytab.PredictionHelper;
import adams.gui.tools.wekainvestigator.tab.classifytab.ResultItem;

import javax.swing.JComponent;
import javax.swing.JPanel;
import java.util.ArrayList;
import java.util.List;

/**
 * Generates classifier errors plot.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ClassifierErrors
  extends AbstractOutputGenerator {

  private static final long serialVersionUID = -6829245659118360739L;

  /** the maximum number of data points before turning off anti-aliasing. */
  public final static int MAX_DATA_POINTS = 1000;

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

  /** the diameter of the cross. */
  protected int m_Diameter;

  /** whether to use the error for the cross-size. */
  protected boolean m_UseError;

  /** whether anti-aliasing is enabled. */
  protected AutoOnOff m_AntiAliasingEnabled;

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

    m_OptionManager.add(
      "diameter", "diameter",
      7, 1, null);

    m_OptionManager.add(
      "use-error", "useError",
      false);

    m_OptionManager.add(
      "anti-aliasing-enabled", "antiAliasingEnabled",
      AutoOnOff.AUTO);
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
   * Sets the cross diameter.
   *
   * @param value	the diameter
   */
  public void setDiameter(int value) {
    if (getOptionManager().isValid("diameter", value)) {
      m_Diameter = value;
      reset();
    }
  }

  /**
   * Returns the diameter of the cross.
   *
   * @return		the diameter
   */
  public int getDiameter() {
    return m_Diameter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String diameterTipText() {
    return "The diameter of the cross in pixels (if no error data supplied).";
  }

  /**
   * Sets whether to use the numeric error for the cross size.
   *
   * @param value	true if to use error
   */
  public void setUseError(boolean value) {
    m_UseError = value;
    reset();
  }

  /**
   * Returns whether to use the error for the cross size.
   *
   * @return		true if to use error
   */
  public boolean getUseError() {
    return m_UseError;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useErrorTipText() {
    return "If enabled, the numeric error is used for the cross size.";
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
   * Sets whether to use anti-aliasing.
   *
   * @param value	if anti-aliasing is used
   */
  public void setAntiAliasingEnabled(AutoOnOff value) {
    m_AntiAliasingEnabled = value;
    reset();
  }

  /**
   * Returns whether anti-aliasing is used.
   *
   * @return		if anti-aliasing is used
   */
  public AutoOnOff isAntiAliasingEnabled() {
    return m_AntiAliasingEnabled;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String antiAliasingEnabledTipText() {
    return "In auto mode, anti-aliasing is turned off if more than " + MAX_DATA_POINTS + " present or the system wide setting has been turned off.";
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
   * Generates output from the item.
   *
   * @param item	the item to generate output for
   * @param errors	for collecting error messages
   * @return		the output component, null if failed to generate
   */
  public JComponent createOutput(ResultItem item, MessageCollection errors) {
    ActualVsPredictedPlot 		sink;
    JPanel 				panel;
    boolean				showError;
    Token				token;
    SpreadSheet				sheet;
    List<String>			additional;
    int					i;

    showError = m_UseError && item.getEvaluation().getHeader().classAttribute().isNumeric();
    sheet     = PredictionHelper.toSpreadSheet(this, errors, item, true, showError);
    if (sheet == null) {
      if (errors.isEmpty())
	errors.add("Failed to generate prediction!");
      return null;
    }
    token = new Token(sheet);

    sink  = new ActualVsPredictedPlot();
    sink.setActualMin(m_ActualMin);
    sink.setActualMax(m_ActualMax);
    sink.setPredictedMin(m_PredictedMin);
    sink.setPredictedMax(m_PredictedMax);
    sink.setLimit(m_Limit);
    sink.setDiameter(m_Diameter);
    sink.setShowSidePanel(false);
    switch (m_AntiAliasingEnabled) {
      case AUTO:
	sink.setAntiAliasingEnabled(
	  ((SpreadSheet) token.getPayload()).getRowCount() <= MAX_DATA_POINTS
	    && GUIHelper.AntiAliasingEnabled);
	break;
      case ON:
	sink.setAntiAliasingEnabled(true);
	break;
      case OFF:
	sink.setAntiAliasingEnabled(false);
	break;
    }
    if (showError)
      sink.setError(new SpreadSheetColumnIndex("Error"));
    additional = null;
    if (item.hasAdditionalAttributes()) {
      additional = new ArrayList<>();
      for (i = 0; i < item.getAdditionalAttributes().getColumnCount(); i++)
	additional.add(SpreadSheetColumnRange.escapeName(item.getAdditionalAttributes().getColumnName(i)));
    }
    if ((additional != null) && (additional.size() > 0))
      sink.setAdditional(new SpreadSheetColumnRange(Utils.flatten(additional, ",")));
    panel = sink.createDisplayPanel(token);

    return new ComponentContentPanel(panel, sink.displayPanelRequiresScrollPane());
  }
}
