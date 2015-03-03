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
 * BaselineAdjustedForecaster.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package weka.classifiers.timeseries;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import weka.classifiers.evaluation.NumericPrediction;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Utils;
import adams.core.Constants;
import adams.core.option.OptionUtils;
import adams.data.DateFormatString;
import adams.data.baseline.AbstractBaselineCorrection;
import adams.data.baseline.LOWESSBased;
import adams.data.conversion.TimeseriesToWekaInstances;
import adams.data.conversion.WekaInstancesToTimeseries;
import adams.data.timeseries.Timeseries;
import adams.data.timeseries.TimeseriesPoint;
import adams.data.timeseries.TimeseriesUtils;
import adams.data.weka.WekaAttributeIndex;

/**
 <!-- globalinfo-start -->
 * Uses two base-forecasters for making predictions. The first one is trained on the baseline of the timeseries (= overall trend), the second is trained on the baseline-corrected data (= periodicity). At forecast time, the two predictions are super-imposed to generate the original signal again.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre> -correction &lt;classname+options&gt;
 *  The baseline correction scheme.
 *  (default: adams.data.baseline.LOWESSBased)</pre>
 * 
 * <pre> -baseline &lt;classname+options&gt;
 *  The baseline forecaster.
 *  (default: weka.classifiers.timeseries.WekaForecaster)</pre>
 * 
 * <pre> -periodicity &lt;classname+options&gt;
 *  The periodicity forecaster.
 *  (default: weka.classifiers.timeseries.WekaForecaster)</pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BaselineAdjustedForecaster
  extends AbstractForecaster
  implements Serializable, OptionHandler {

  /** for serialization. */
  private static final long serialVersionUID = 3169376392576809182L;

  /** the baseline correction scheme to use. */
  protected AbstractBaselineCorrection m_Correction;
  
  /** the forecaster for the baseline. */
  protected AbstractForecaster m_Baseline;
  
  /** the forecaster for the periodicity (= baseline-correct signal). */
  protected AbstractForecaster m_Periodicity;
  
  /** whether a model was built. */
  protected boolean m_ModelBuilt;
  
  /**
   * Initializes the forecaster.
   */
  public BaselineAdjustedForecaster() {
    initialize();
    reset();
  }
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return
        "Uses two base-forecasters for making predictions. The first one is "
	+ "trained on the baseline of the timeseries (= overall trend), "
        + "the second is trained on the baseline-corrected data (= periodicity). "
	+ "At forecast time, the two predictions are super-imposed to generate "
        + "the original signal again.";
  }
  
  /**
   * Provides a short name that describes the underlying algorithm
   * in some way.
   * 
   * @return a short description of this forecaster.
   */
  @Override
  public String getAlgorithmName() {
    return OptionUtils.getShortCommandLine(this);
  }

  /**
   * Initializes the members.
   */
  protected void initialize() {
    m_Correction    = new LOWESSBased();
    m_Baseline    = new WekaForecaster();
    m_Periodicity = new WekaForecaster();
  }
  
  /**
   * Reset this forecaster so that it is ready to construct a
   * new model.
   */
  @Override
  public void reset() {
    m_ModelBuilt = false;
    m_Correction.cleanUp();
    m_Baseline.reset();
    m_Periodicity.reset();
  }

  /**
   * Returns an enumeration of all the available options..
   *
   * @return an enumeration of all available options.
   */
  @Override
  public Enumeration listOptions() {
    Vector 	result;

    result = new Vector();
    
    result.addElement(new Option(
	"\tThe baseline correction scheme.\n"
	+ "\t(default: " + LOWESSBased.class.getName() + ")",
	"correction", 1, "-correction <classname+options>"));
    
    result.addElement(new Option(
	"\tThe baseline forecaster.\n"
	+ "\t(default: " + WekaForecaster.class.getName() + ")",
	"baseline", 1, "-baseline <classname+options>"));
    
    result.addElement(new Option(
	"\tThe periodicity forecaster.\n"
	+ "\t(default: " + WekaForecaster.class.getName() + ")",
	"periodicity", 1, "-periodicity <classname+options>"));

    return result.elements();
  }

  /**
   * Sets the OptionHandler's options using the given list. All options
   * will be set (or reset) during this call (i.e. incremental setting
   * of options is not possible).
   *
   * @param options the list of options as an array of strings
   * @throws Exception if an option is not supported
   */
  @Override
  public void setOptions(String[] options) throws Exception {
    String	tmpStr;

    reset();
    
    tmpStr = Utils.getOption("correction", options);
    if (tmpStr.isEmpty())
      setCorrection(new LOWESSBased());
    else
      setCorrection(AbstractBaselineCorrection.forCommandLine(tmpStr));

    tmpStr = Utils.getOption("baseline", options);
    if (tmpStr.isEmpty())
      setBaseline(new WekaForecaster());
    else
      setBaseline((AbstractForecaster) OptionUtils.forAnyCommandLine(AbstractForecaster.class, tmpStr));

    tmpStr = Utils.getOption("periodicity", options);
    if (tmpStr.isEmpty())
      setPeriodicity(new WekaForecaster());
    else
      setPeriodicity((AbstractForecaster) OptionUtils.forAnyCommandLine(AbstractForecaster.class, tmpStr));
  }

  /**
   * Gets the current option settings for the OptionHandler.
   *
   * @return the list of current option settings as an array of strings
   */
  @Override
  public String[] getOptions() {
    List<String>	result;
    
    result = new ArrayList<String>();

    result.add("-correction");
    result.add(OptionUtils.getCommandLine(m_Correction));

    result.add("-baseline");
    result.add(OptionUtils.getCommandLine(m_Baseline));

    result.add("-periodicity");
    result.add(OptionUtils.getCommandLine(m_Periodicity));
    
    return result.toArray(new String[result.size()]);
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
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String correctionTipText() {
    return "The baseline correction scheme to use.";
  }

  /**
   * Sets the forecaster to use for the baseline.
   *
   * @param value	the forecaster
   */
  public void setBaseline(AbstractForecaster value) {
    m_Baseline = value;
    reset();
  }

  /**
   * Returns the forecaster in use for the baseline
   *
   * @return 		the forecaster
   */
  public AbstractForecaster getBaseline() {
    return m_Baseline;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String baselineTipText() {
    return "The forecaster to use for the baseline.";
  }

  /**
   * Sets the forecaster to use for the periodicity.
   *
   * @param value	the forecaster
   */
  public void setPeriodicity(AbstractForecaster value) {
    m_Periodicity = value;
    reset();
  }

  /**
   * Returns the forecaster in use for the periodicity
   *
   * @return 		the forecaster
   */
  public AbstractForecaster getPeriodicity() {
    return m_Periodicity;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String periodicityTipText() {
    return "The forecaster to use for the periodicity.";
  }
  
  /**
   * Turns Weka Instances into a {@link Timeseries}.
   * 
   * @param insts	the instances to convert
   * @return		the timeseries
   * @throws Execption	if conversion fails
   */
  protected Timeseries instancesToTimeseries(Instances insts) throws Exception {
    Timeseries			result;
    WekaInstancesToTimeseries	toTime;
    String			msg;

    toTime = new WekaInstancesToTimeseries();
    if (insts.attribute(0).isDate()) {
      toTime.setDateAttribute(new WekaAttributeIndex("1"));
      toTime.setValueAttribute(new WekaAttributeIndex("2"));
    }
    else {
      toTime.setDateAttribute(new WekaAttributeIndex("2"));
      toTime.setValueAttribute(new WekaAttributeIndex("1"));
    }
    toTime.setInput(insts);
    msg = toTime.convert();
    if (msg != null)
      throw new Exception("Converting instances to timeseries failed: " + msg);
    result = (Timeseries) toTime.getOutput();
    toTime.cleanUp();
    
    return result;
  }
  
  /**
   * Turns a timeseries back into Weka Instances.
   * 
   * @param series	the timeseries to convert
   * @return		the Instances
   * @throws Exception	if conversion fails
   */
  protected Instances timeseriesToInstances(Timeseries series) throws Exception {
    Instances			result;
    TimeseriesToWekaInstances	fromTime;
    String			msg;
    
    fromTime = new TimeseriesToWekaInstances();
    fromTime.setFormat(new DateFormatString(Constants.TIMESTAMP_FORMAT_MSECS));
    fromTime.setInput(series);
    msg = fromTime.convert();
    if (msg != null)
      throw new Exception("Converting timeseries to instances failed: " + msg);
    result = (Instances) fromTime.getOutput();
    fromTime.cleanUp();
    
    return result;
  }
  
  /**
   * Subtracts the corrected signal from the original one to obtain the baseline.
   * 
   * @param raw		the original signal
   * @param corrected	the baseline-corrected signal
   * @return		the baseline adjustment
   */
  protected Timeseries extractBaseline(Timeseries raw, Timeseries corrected) {
    Timeseries		result;
    TimeseriesPoint	corr;
    int			i;
    int			index;
    
    result = raw.getHeader();
    
    for (i = 0; i < corrected.size(); i++) {
      corr  = (TimeseriesPoint) corrected.toList().get(i);
      index = TimeseriesUtils.findClosestTimestamp(raw.toList(), corr.getTimestamp());
      if (index > -1)
	result.add((TimeseriesPoint) raw.toList().get(index));
    }
    
    return result;
  }
  
  /**
   * Builds a new forecasting model using the supplied training
   * data. The instances in the data are assumed to be sorted in
   * ascending order of time and equally spaced in time. Some
   * methods may not need to implement this method and may
   * instead do their work in the primeForecaster method.
   * 
   * @param insts the training instances.
   * @param progress an optional varargs parameter supplying progress objects
   * to report to
   * @throws Exception if the model can't be constructed for some
   * reason.
   */
  @Override
  public void buildForecaster(Instances insts, PrintStream... progress) throws Exception {
    Timeseries		raw;
    Timeseries		corrected;
    Timeseries		baseline;
    Instances		correctedInst;
    Instances		baselineInst;
    
    if (insts.numAttributes() > 2)
      throw new IllegalArgumentException("Data must contain two attributes (timestamp, value), encountered: " + insts.numAttributes());
    if (!insts.checkForAttributeType(Attribute.DATE))
      throw new IllegalArgumentException("Data must contain a date attribute, none found!");
    if (!insts.checkForAttributeType(Attribute.NUMERIC))
      throw new IllegalArgumentException("Data must contain a numeric attribute, none found!");
    
    // generate timeseries
    raw = instancesToTimeseries(insts);

    // correct baseline
    corrected = (Timeseries) m_Correction.correct(raw);
    
    // extract baseline
    baseline = extractBaseline(raw, corrected);
    
    // build forecasters
    baselineInst = timeseriesToInstances(baseline);
    m_Baseline.buildForecaster(baselineInst, progress);

    correctedInst = timeseriesToInstances(corrected);
    m_Periodicity.buildForecaster(correctedInst, progress);
    
    m_ModelBuilt = true;
  }

  /**
   * Supply the (potentially) trained model with enough historical
   * data, up to and including the current time point, in order
   * to produce a forecast. Instances are assumed to be sorted in
   * ascending order of time and equally spaced in time.
   * 
   * @param insts the instances to prime the model with
   * @throws Exception if the model can't be primed for some
   * reason.
   */
  @Override
  public void primeForecaster(Instances insts) throws Exception {
    Timeseries		raw;
    Timeseries		corrected;
    Timeseries		baseline;
    Instances		correctedInst;
    Instances		baselineInst;
    
    if (!m_ModelBuilt)
      throw new IllegalStateException("No model built!");
    
    // generate timeseries
    raw = instancesToTimeseries(insts);

    // correct baseline
    corrected = (Timeseries) m_Correction.correct(raw);
    
    // extract baseline
    baseline = extractBaseline(raw, corrected);
    
    // prime forecasters
    baselineInst = timeseriesToInstances(baseline);
    m_Baseline.primeForecaster(baselineInst);

    correctedInst = timeseriesToInstances(corrected);
    m_Periodicity.primeForecaster(correctedInst);
  }

  /**
   * Produce a forecast for the target field(s). 
   * Assumes that the model has been built
   * and/or primed so that a forecast can be generated.
   * 
   * @param numSteps number of forecasted values to produce for each target. E.g.
   * a value of 5 would produce a prediction for t+1, t+2, ..., t+5.
   * @param progress an optional varargs parameter supplying progress objects
   * to report to
   * @return a List of Lists (one for each step) of forecasted values for each target
   * @throws Exception if the forecast can't be produced for some reason.
   */
  @Override
  public List<List<NumericPrediction>> forecast(int numSteps, PrintStream... progress) throws Exception {
    List<List<NumericPrediction>>	result;
    List<List<NumericPrediction>>	baseline;
    List<List<NumericPrediction>>	periodicity;
    int					i;
    int					n;
    NumericPrediction			pred;
    double				predicted;
    double				actual;
    
    if (!m_ModelBuilt)
      throw new IllegalStateException("No model built!");
    
    baseline    = m_Baseline.forecast(numSteps, progress);
    periodicity = m_Periodicity.forecast(numSteps, progress);
    result      = new ArrayList<List<NumericPrediction>>();
    
    for (i = 0; i < baseline.size(); i++) {
      result.add(new ArrayList<NumericPrediction>());
      for (n = 0; n < baseline.get(i).size(); n++) {
	predicted = baseline.get(i).get(n).predicted() + periodicity.get(i).get(n).predicted();
	actual    = baseline.get(i).get(n).actual()    + periodicity.get(i).get(n).actual();
	pred      = new NumericPrediction(actual, predicted);
	result.get(result.size() - 1).add(pred);
      }
    }
    
    return result;
  }

  /**
   * Outputs a short description of the model.
   * 
   * @return		the description
   */
  @Override
  public String toString() {
    StringBuilder	result;
    
    result = new StringBuilder();
    
    if (!m_ModelBuilt) {
      result.append("Forecaster has not been built yet!");
    }
    else {
      result.append("Baseline forecaster:\n\n" + m_Baseline);
      result.append("\n\n");
      result.append("Periodicity forecaster:\n\n" + m_Periodicity);
    }
    
    return result.toString();
  }
}
