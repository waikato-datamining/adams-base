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
 * WekaForecasterGenerator.java
 * Copyright (C) 2013-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.source.wekaforecastersetup;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.option.OptionUtils;
import adams.flow.core.LagMakerOptions;
import weka.classifiers.Classifier;
import weka.classifiers.functions.LinearRegressionJ;
import weka.classifiers.timeseries.AbstractForecaster;
import weka.classifiers.timeseries.WekaForecaster;

/**
 <!-- globalinfo-start -->
 * Outputs a configured instance of a Weka Forecaster.
 * <br><br>
 <!-- globalinfo-end -->
 * 
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-classifier &lt;weka.classifiers.Classifier&gt; (property: classifier)
 * &nbsp;&nbsp;&nbsp;The Weka classifier to train on the input data.
 * &nbsp;&nbsp;&nbsp;default: weka.classifiers.functions.LinearRegression -S 0 -R 1.0E-8
 * </pre>
 * 
 * <pre>-forecast-fields &lt;java.lang.String&gt; (property: forecastFields)
 * &nbsp;&nbsp;&nbsp;The fields to forecast (comma-separated list).
 * &nbsp;&nbsp;&nbsp;default: Value
 * </pre>
 * 
 * <pre>-overlay-fields &lt;java.lang.String&gt; (property: overlayFields)
 * &nbsp;&nbsp;&nbsp;The fields to overlay (comma-separated list).
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-lag-maker &lt;adams.flow.core.LagMakerOptions&gt; (property: lagMaker)
 * &nbsp;&nbsp;&nbsp;The lag maker options.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.core.LagMakerOptions
 * </pre>
 * 
 * <pre>-num-steps-confidence-intervals &lt;int&gt; (property: numStepsConfidenceIntervals)
 * &nbsp;&nbsp;&nbsp;The number of steps to generate confidence intervals for.
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-confidence-level &lt;double&gt; (property: confidenceLevel)
 * &nbsp;&nbsp;&nbsp;The confidence level to use (0-1).
 * &nbsp;&nbsp;&nbsp;default: 0.95
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * &nbsp;&nbsp;&nbsp;maximum: 1.0
 * </pre>
 * 
 <!-- options-end -->
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaForecasterGenerator
  extends AbstractForecasterGenerator {

  /** for serialization. */
  private static final long serialVersionUID = -6919031737595447726L;

  /** the base classifier. */
  protected Classifier m_Classifier;

  /** the fields to forecast (comma-separated list). */
  protected String m_ForecastFields;

  /** the fields to overlay (comma-separated list). */
  protected String m_OverlayFields;

  /** the lag maker options. */
  protected LagMakerOptions m_LagMaker;

  /** the number of steps for confidence intervals. */
  protected int m_NumStepsConfidenceIntervals;
  
  /** the confidence level (0-1). */
  protected double m_ConfidenceLevel;

  /**
   * Returns a string describing the object.
   * 
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Outputs a configured instance of a Weka Forecaster.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"classifier", "classifier", 
	new LinearRegressionJ());

    m_OptionManager.add(
	"forecast-fields", "forecastFields", 
	"Value");

    m_OptionManager.add(
	"overlay-fields", "overlayFields", 
	"");

    m_OptionManager.add(
	"lag-maker", "lagMaker", 
	new LagMakerOptions());

    m_OptionManager.add(
	"num-steps-confidence-intervals", "numStepsConfidenceIntervals", 
	0, 0, null);

    m_OptionManager.add(
	"confidence-level", "confidenceLevel", 
	0.95, 0.0, 1.0);
  }

  /**
   * Sets the classifier to use.
   * 
   * @param value	the classifier
   */
  public void setClassifier(Classifier value) {
    m_Classifier = value;
    reset();
  }

  /**
   * Returns the classifier in use.
   * 
   * @return 		the classifier
   */
  public Classifier getClassifier() {
    return m_Classifier;
  }

  /**
   * Returns the tip text for this property.
   * 
   * @return 		tip text for this property suitable for displaying in the GUI or
   *         		for listing the options.
   */
  public String classifierTipText() {
    return "The Weka classifier to train on the input data.";
  }

  /**
   * Sets the fields to forecast.
   * 
   * @param value	the fields (comma-separated list)
   */
  public void setForecastFields(String value) {
    m_ForecastFields = value;
    reset();
  }

  /**
   * Returns the fields to forecast.
   * 
   * @return 		the fields (comma-separated list)
   */
  public String getForecastFields() {
    return m_ForecastFields;
  }

  /**
   * Returns the tip text for this property.
   * 
   * @return 		tip text for this property suitable for displaying in the GUI or
   *         		for listing the options.
   */
  public String forecastFieldsTipText() {
    return "The fields to forecast (comma-separated list).";
  }

  /**
   * Sets the fields to overlay.
   * 
   * @param value	the fields (comma-separated list)
   */
  public void setOverlayFields(String value) {
    m_OverlayFields = value;
    reset();
  }

  /**
   * Returns the fields to overlay.
   * 
   * @return 		the fields (comma-separated list)
   */
  public String getOverlayFields() {
    return m_OverlayFields;
  }

  /**
   * Returns the tip text for this property.
   * 
   * @return 		tip text for this property suitable for displaying in the GUI or
   *         		for listing the options.
   */
  public String overlayFieldsTipText() {
    return "The fields to overlay (comma-separated list).";
  }

  /**
   * Sets the number of steps to generate confidence intervals for.
   * 
   * @param value	the number of steps
   */
  public void setNumStepsConfidenceIntervals(int value) {
    m_NumStepsConfidenceIntervals = value;
    reset();
  }

  /**
   * Returns the number of steps to generate confidence intervals for.
   * 
   * @return 		the number of steps
   */
  public int getNumStepsConfidenceIntervals() {
    return m_NumStepsConfidenceIntervals;
  }

  /**
   * Returns the tip text for this property.
   * 
   * @return 		tip text for this property suitable for displaying in the GUI or
   *         		for listing the options.
   */
  public String numStepsConfidenceIntervalsTipText() {
    return "The number of steps to generate confidence intervals for.";
  }

  /**
   * Sets the confidence level to use.
   * 
   * @param value	the level
   */
  public void setConfidenceLevel(double value) {
    if ((value > 0) && (value < 1)) {
      m_ConfidenceLevel = value;
      reset();
    }
    else {
      getLogger().severe("Confidence level must satisfy: 0<x<1, provided: " + value);
    }
  }

  /**
   * Returns the confidence level (0-1).
   * 
   * @return 		the level
   */
  public double getConfidenceLevel() {
    return m_ConfidenceLevel;
  }

  /**
   * Returns the tip text for this property.
   * 
   * @return 		tip text for this property suitable for displaying in the GUI or
   *         		for listing the options.
   */
  public String confidenceLevelTipText() {
    return "The confidence level to use (0-1).";
  }

  /**
   * Sets the lag options to use.
   * 
   * @param value	the lag options
   */
  public void setLagMaker(LagMakerOptions value) {
    m_LagMaker = value;
    reset();
  }

  /**
   * Returns the lag options.
   * 
   * @return 		the lag options
   */
  public LagMakerOptions getLagMaker() {
    return m_LagMaker;
  }

  /**
   * Returns the tip text for this property.
   * 
   * @return 		tip text for this property suitable for displaying in the GUI or
   *         		for listing the options.
   */
  public String lagMakerTipText() {
    return "The lag maker options.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   * 
   * @return 		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "classifier",
	Utils.shorten(OptionUtils.getShortCommandLine(m_Classifier), 40));
  }

  /**
   * Generates an instance of a {@link AbstractForecaster}.
   * 
   * @return		the forecaster instance
   */
  @Override
  public AbstractForecaster generate() throws Exception {
    WekaForecaster result;

    result = new WekaForecaster();
    result.setBaseForecaster((Classifier) OptionUtils.shallowCopy(m_Classifier));
    result.setFieldsToForecast(m_ForecastFields);
    if (m_OverlayFields.length() > 0)
      result.setOverlayFields(m_OverlayFields);
    result.setCalculateConfIntervalsForForecasts(m_NumStepsConfidenceIntervals);
    result.setConfidenceLevel(m_ConfidenceLevel);
    result.getTSLagMaker().setMinLag(m_LagMaker.getMinLag());
    result.getTSLagMaker().setMaxLag(m_LagMaker.getMaxLag());
    result.getTSLagMaker().setLagRange(m_LagMaker.getLagFineTune());
    result.getTSLagMaker().setAverageConsecutiveLongLags(m_LagMaker.getAverageConsecutiveLongLags());
    result.getTSLagMaker().setAverageLagsAfter(m_LagMaker.getAverageLagsAfter());
    result.getTSLagMaker().setNumConsecutiveLongLagsToAverage(m_LagMaker.getNumConsecutiveLongLagsToAverage());
    result.getTSLagMaker().setAdjustForTrends(m_LagMaker.getAdjustForTrends());
    result.getTSLagMaker().setAdjustForVariance(m_LagMaker.getAdjustForVariance());
    result.getTSLagMaker().setTimeStampField(m_LagMaker.getTimeStampField());
    result.getTSLagMaker().setAddAMIndicator(m_LagMaker.getAddAMIndicator());
    result.getTSLagMaker().setAddDayOfWeek(m_LagMaker.getAddDayOfWeek());
    result.getTSLagMaker().setAddDayOfMonth(m_LagMaker.getAddDayOfMonth());
    result.getTSLagMaker().setAddNumDaysInMonth(m_LagMaker.getAddNumDaysInMonth());
    result.getTSLagMaker().setAddWeekendIndicator(m_LagMaker.getAddWeekendIndicator());
    result.getTSLagMaker().setAddMonthOfYear(m_LagMaker.getAddMonthOfYear());
    result.getTSLagMaker().setAddQuarterOfYear(m_LagMaker.getAddQuarterOfYear());
    result.getTSLagMaker().setSkipEntries(m_LagMaker.getSkipEntries());
    
    return result;
  }
}
