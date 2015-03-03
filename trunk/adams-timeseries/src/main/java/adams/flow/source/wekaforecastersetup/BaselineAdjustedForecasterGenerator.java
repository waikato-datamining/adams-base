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
 * BaselineAdjustedForecasterGenerator.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.source.wekaforecastersetup;

import weka.classifiers.timeseries.AbstractForecaster;
import weka.classifiers.timeseries.BaselineAdjustedForecaster;
import adams.core.QuickInfoHelper;
import adams.data.baseline.AbstractBaselineCorrection;
import adams.data.baseline.LOWESSBased;

/**
 <!-- globalinfo-start -->
 * Outputs a configured instance of a baseline-adjusted Forecaster.
 * <p/>
 <!-- globalinfo-end -->
 * 
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-correction &lt;adams.data.baseline.AbstractBaselineCorrection&gt; (property: correction)
 * &nbsp;&nbsp;&nbsp;The baseline correction scheme to use.
 * &nbsp;&nbsp;&nbsp;default: adams.data.baseline.LOWESSBased
 * </pre>
 * 
 * <pre>-baseline &lt;adams.flow.source.wekaforecastersetup.AbstractForecasterGenerator&gt; (property: baseline)
 * &nbsp;&nbsp;&nbsp;The forecaster generator for the baseline data.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.source.wekaforecastersetup.WekaForecasterGenerator -classifier \"weka.classifiers.functions.LinearRegression -S 0 -R 1.0E-8\" -lag-maker adams.flow.core.LagMakerOptions
 * </pre>
 * 
 * <pre>-periodicity &lt;adams.flow.source.wekaforecastersetup.AbstractForecasterGenerator&gt; (property: periodicity)
 * &nbsp;&nbsp;&nbsp;The forecaster generator for the periodicity data.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.source.wekaforecastersetup.WekaForecasterGenerator -classifier \"weka.classifiers.functions.LinearRegression -S 0 -R 1.0E-8\" -lag-maker adams.flow.core.LagMakerOptions
 * </pre>
 * 
 <!-- options-end -->
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BaselineAdjustedForecasterGenerator
  extends AbstractForecasterGenerator {

  /** for serialization. */
  private static final long serialVersionUID = -6919031737595447726L;

  /** the baseline correction scheme. */
  protected AbstractBaselineCorrection m_Correction;

  /** the forecaster setup for the baseline. */
  protected AbstractForecasterGenerator m_Baseline;

  /** the forecaster setup for the periodicity. */
  protected AbstractForecasterGenerator m_Periodicity;
  
  /**
   * Returns a string describing the object.
   * 
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Outputs a configured instance of a baseline-adjusted Forecaster.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"correction", "correction", 
	new LOWESSBased());

    m_OptionManager.add(
	"baseline", "baseline", 
	new WekaForecasterGenerator());

    m_OptionManager.add(
	"periodicity", "periodicity", 
	new WekaForecasterGenerator());
  }

  /**
   * Sets the baseline correction scheme to use.
   * 
   * @param value	the scheme
   */
  public void setCorrection(AbstractBaselineCorrection value) {
    m_Correction = value;
    reset();
  }

  /**
   * Returns the baseline correction scheme in use.
   * 
   * @return 		the scheme
   */
  public AbstractBaselineCorrection getCorrection() {
    return m_Correction;
  }

  /**
   * Returns the tip text for this property.
   * 
   * @return 		tip text for this property suitable for displaying in the GUI or
   *         		for listing the options.
   */
  public String correctionTipText() {
    return "The baseline correction scheme to use.";
  }

  /**
   * Sets the forecaster generator for the baseline data.
   * 
   * @param value	the generator
   */
  public void setBaseline(AbstractForecasterGenerator value) {
    m_Baseline = value;
    reset();
  }

  /**
   * Returns the forecaster generator in use for the baseline data.
   * 
   * @return 		the generator
   */
  public AbstractForecasterGenerator getBaseline() {
    return m_Baseline;
  }

  /**
   * Returns the tip text for this property.
   * 
   * @return 		tip text for this property suitable for displaying in the GUI or
   *         		for listing the options.
   */
  public String baselineTipText() {
    return "The forecaster generator for the baseline data.";
  }

  /**
   * Sets the forecaster generator for the periodicity data.
   * 
   * @param value	the generator
   */
  public void setPeriodicity(AbstractForecasterGenerator value) {
    m_Periodicity = value;
    reset();
  }

  /**
   * Returns the forecaster generator in use for the periodicity data.
   * 
   * @return 		the generator
   */
  public AbstractForecasterGenerator getPeriodicity() {
    return m_Periodicity;
  }

  /**
   * Returns the tip text for this property.
   * 
   * @return 		tip text for this property suitable for displaying in the GUI or
   *         		for listing the options.
   */
  public String periodicityTipText() {
    return "The forecaster generator for the periodicity data.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   * 
   * @return 		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    
    result  = QuickInfoHelper.toString(this, "correction", m_Correction, "correction: ");
    result += QuickInfoHelper.toString(this, "baseline", m_Baseline, ", baseline: ");
    result += QuickInfoHelper.toString(this, "periodicity", m_Periodicity, ", periodicity: ");
    
    return result;
  }

  /**
   * Generates an instance of a {@link AbstractForecaster}.
   * 
   * @return		the forecaster instance
   */
  @Override
  public AbstractForecaster generate() throws Exception {
    BaselineAdjustedForecaster result;

    result = new BaselineAdjustedForecaster();
    result.setCorrection(m_Correction.shallowCopy());
    result.setBaseline(m_Baseline.generate());
    result.setPeriodicity(m_Periodicity.generate());
    
    return result;
  }
}
