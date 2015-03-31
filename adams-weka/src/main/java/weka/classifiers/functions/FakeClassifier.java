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
 * FakeClassifier.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */
package weka.classifiers.functions;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.UpdateableClassifier;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.Randomizable;
import weka.core.Utils;

import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;

/**
 <!-- globalinfo-start -->
 * Fake classifier that requires no dataset for training and just outputs random values within the specified bounds.<br/>
 * Fake build and prediction times can be set as well.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre> -seed &lt;int&gt;
 *  The seed value to use.
 *  (default: 1)</pre>
 * 
 * <pre> -build-wait &lt;int&gt;
 *  The number of msec for the classifier to idle at build time.
 *  (default: 0)</pre>
 * 
 * <pre> -update-wait &lt;int&gt;
 *  The number of msec for the classifier to idle at incremental build time.
 *  (default: 0)</pre>
 * 
 * <pre> -predict-wait &lt;int&gt;
 *  The number of msec for the classifier to idle at prediction time.
 *  (default: 0)</pre>
 * 
 * <pre> -predict-min &lt;num&gt;
 *  The minimum value to use for prediction.
 *  (default: 0.0)</pre>
 * 
 * <pre> -predict-max &lt;num&gt;
 *  The maximum value to use for prediction.
 *  (default: 1.0)</pre>
 * 
 * <pre> -output-debug-info
 *  If set, classifier is run in debug mode and
 *  may output additional info to the console</pre>
 * 
 * <pre> -do-not-check-capabilities
 *  If set, classifier capabilities are not checked before classifier is built
 *  (use with caution).</pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FakeClassifier
  extends AbstractClassifier
  implements Randomizable, UpdateableClassifier {

  /** for serialization. */
  private static final long serialVersionUID = 8430850643799590721L;

  /** the seed. */
  protected int m_Seed = 1;

  /** the build wait time in msec. */
  protected int m_BuildWait = 0;

  /** the update wait time in msec. */
  protected int m_UpdateWait = 0;

  /** the predict wait time in msec. */
  protected int m_PredictWait = 0;

  /** the minimum to use for the predictions. */
  protected double m_PredictMin = 0.0;

  /** the maximum to use for the predictions. */
  protected double m_PredictMax = 1.0;

  /** for generating the random numbers. */
  protected Random m_Random;

  /**
   * Returns a string describing classifier
   *
   * @return a description suitable for displaying in the
   *         explorer/experimenter gui
   */
  public String globalInfo() {
    return 
	"Fake classifier that requires no dataset for training and just "
          + "outputs random values within the specified bounds.\n"
          + "Fake build and prediction times can be set as well.";
  }
  
  /**
   * Gets an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  @Override
  public Enumeration listOptions() {
    Vector        	result;
    Enumeration   	en;

    result = new Vector();

    result.addElement(new Option(
      "\tThe seed value to use.\n"
        + "\t(default: 1)",
      "seed", 1, "-seed <int>"));

    result.addElement(new Option(
      "\tThe number of msec for the classifier to idle at build time.\n"
        + "\t(default: 0)",
      "build-wait", 1, "-build-wait <int>"));

    result.addElement(new Option(
      "\tThe number of msec for the classifier to idle at incremental build time.\n"
        + "\t(default: 0)",
      "update-wait", 1, "-update-wait <int>"));

    result.addElement(new Option(
      "\tThe number of msec for the classifier to idle at prediction time.\n"
        + "\t(default: 0)",
      "predict-wait", 1, "-predict-wait <int>"));

    result.addElement(new Option(
      "\tThe minimum value to use for prediction.\n"
        + "\t(default: 0.0)",
      "predict-min", 1, "-predict-min <num>"));

    result.addElement(new Option(
      "\tThe maximum value to use for prediction.\n"
        + "\t(default: 1.0)",
      "predict-max", 1, "-predict-max <num>"));

    en = super.listOptions();
    while (en.hasMoreElements())
      result.addElement(en.nextElement());

    return result.elements();
  }

  /**
   * returns the options of the current setup
   *
   * @return		the current options
   */
  @Override
  public String[] getOptions() {
    int       	i;
    Vector    	result;
    String[]  	options;

    result = new Vector();

    result.add("-seed");
    result.add("" + getSeed());

    result.add("-build-wait");
    result.add("" + getBuildWait());

    result.add("-update-wait");
    result.add("" + getUpdateWait());

    result.add("-predict-wait");
    result.add("" + getPredictWait());

    result.add("-predict-min");
    result.add("" + getPredictMin());

    result.add("-predict-max");
    result.add("" + getPredictMax());

    options = super.getOptions();
    for (i = 0; i < options.length; i++)
      result.add(options[i]);

    return (String[]) result.toArray(new String[result.size()]);
  }

  /**
   * Parses the options for this object.
   *
   * @param options	the options to use
   * @throws Exception	if setting of options fails
   */
  @Override
  public void setOptions(String[] options) throws Exception {
    String	tmpStr;

    tmpStr = Utils.getOption("seed", options);
    if (tmpStr.length() != 0)
      setSeed(Integer.parseInt(tmpStr));
    else
      setSeed(1);

    tmpStr = Utils.getOption("build-wait", options);
    if (tmpStr.length() != 0)
      setBuildWait(Integer.parseInt(tmpStr));
    else
      setBuildWait(0);

    tmpStr = Utils.getOption("update-wait", options);
    if (tmpStr.length() != 0)
      setUpdateWait(Integer.parseInt(tmpStr));
    else
      setUpdateWait(0);

    tmpStr = Utils.getOption("predict-wait", options);
    if (tmpStr.length() != 0)
      setPredictWait(Integer.parseInt(tmpStr));
    else
      setPredictWait(0);

    tmpStr = Utils.getOption("predict-min", options);
    if (tmpStr.length() != 0)
      setPredictMin(Double.parseDouble(tmpStr));
    else
      setPredictMin(0.0);

    tmpStr = Utils.getOption("predict-max", options);
    if (tmpStr.length() != 0)
      setPredictMax(Double.parseDouble(tmpStr));
    else
      setPredictMax(0.0);

    super.setOptions(options);
  }

  /**
   * Sets the seed value for the random values.
   *
   * @param value 	the seed
   */
  public void setSeed(int value) {
    m_Seed = value;
  }

  /**
   * Returns the seed value for the random values.
   *
   * @return 		the seed
   */
  public int getSeed() {
    return m_Seed;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String seedTipText() {
    return "The seed value to use for the random values.";
  }

  /**
   * Sets the time in msec to wait when calling buildClassifier.
   *
   * @param value 	the time in msec
   */
  public void setBuildWait(int value) {
    if (value >= 0)
      m_BuildWait = value;
    else
      System.err.println("BuildWait time must be >= 0, provided: " + value);
  }

  /**
   * Returns the time in msec to wait when calling buildClassifier.
   *
   * @return 		the time in msec
   */
  public int getBuildWait() {
    return m_BuildWait;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String buildWaitTipText() {
    return "The time in msec to wait when calling 'buildClassifier'.";
  }

  /**
   * Sets the time in msec to wait when calling updateClassifier.
   *
   * @param value 	the time in msec
   */
  public void setUpdateWait(int value) {
    if (value >= 0)
      m_UpdateWait = value;
    else
      System.err.println("UpdateWait time must be >= 0, provided: " + value);
  }

  /**
   * Returns the time in msec to wait when calling updateClassifier.
   *
   * @return 		the time in msec
   */
  public int getUpdateWait() {
    return m_UpdateWait;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String updateWaitTipText() {
    return "The time in msec to wait when calling 'updateClassifier'.";
  }

  /**
   * Sets the time in msec to wait when calling classifyInstance.
   *
   * @param value 	the time in msec
   */
  public void setPredictWait(int value) {
    if (value >= 0)
      m_PredictWait = value;
    else
      System.err.println("PredictWait time must be >= 0, provided: " + value);
  }

  /**
   * Returns the time in msec to wait when calling classifyInstance.
   *
   * @return 		the time in msec
   */
  public int getPredictWait() {
    return m_PredictWait;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String predictWaitTipText() {
    return "The time in msec to wait when calling 'classifyInstance'.";
  }

  /**
   * Sets the minimum value to predict.
   *
   * @param value 	the minimum value
   */
  public void setPredictMin(double value) {
    m_PredictMin = value;
  }

  /**
   * Returns the minimum value to predict.
   *
   * @return 		the minimum value
   */
  public double getPredictMin() {
    return m_PredictMin;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String predictMinTipText() {
    return "The minimum value to predict.";
  }

  /**
   * Sets the maximum value to predict.
   *
   * @param value 	the maximum value
   */
  public void setPredictMax(double value) {
    m_PredictMax = value;
  }

  /**
   * Returns the maximum value to predict.
   *
   * @return 		the maximum value
   */
  public double getPredictMax() {
    return m_PredictMax;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String predictMaxTipText() {
    return "The maximum value to predict.";
  }

  /**
   * Returns the Capabilities of this classifier. Maximally permissive
   * capabilities are allowed by default. Derived classifiers should
   * override this method and first disable all capabilities and then
   * enable just those capabilities that make sense for the scheme.
   *
   * @return            the capabilities of this object
   * @see               Capabilities
   */
  @Override
  public Capabilities getCapabilities() {
    Capabilities    result;

    result = new Capabilities(this);

    result.enableAllAttributes();
    result.enable(Capability.MISSING_VALUES);

    result.enable(Capability.NUMERIC_CLASS);
    result.enable(Capability.MISSING_CLASS_VALUES);

    result.setMinimumNumberInstances(0);

    return result;
  }

  /**
   * Waits for a specified amount of time.
   *
   * @param msec    the time in msec to wait
   */
  protected void wait(int msec) {
    Long  wait;
    int   interval;
    int   current;

    wait     = new Long(System.currentTimeMillis());
    interval = Math.min(100, msec / 10);
    current  = 0;
    while (current < msec) {
      try {
        synchronized(wait) {
          wait.wait(interval);
        }
        current += interval;
      }
      catch (InterruptedException i) {
        break;
      }
      catch (Exception e) {
        // ignored
      }
    }
  }

  /**
   * Generates a classifier.
   *
   * @param data 	set of instances serving as training data
   * @throws Exception 	if the classifier has not been generated successfully
   */
  @Override
  public void buildClassifier(Instances data) throws Exception {
    if (m_PredictMin >= m_PredictMax)
      throw new IllegalStateException(
        "PredictMin must be smaller than PredictMax: " + m_PredictMin + " !< " + m_PredictMax);

    // wait
    if (m_BuildWait > 0)
      wait(m_BuildWait);
  }

  /**
   * Returns the random number generator to use.
   *
   * @return the random number generator
   */
  protected synchronized Random getRandom() {
    if (m_Random == null)
      m_Random = new Random(m_Seed);
    return m_Random;
  }

  /**
   * Does nothing.
   *
   * @param instance
   * @throws Exception
   */
  @Override
  public void updateClassifier(Instance instance) throws Exception {
    // wait
    if (m_UpdateWait > 0)
      wait(m_UpdateWait);
  }

  /**
   * Classifies the given test instance. The instance has to belong to a
   * dataset when it's being classified.
   *
   * @param instance 	the instance to be classified
   * @return 		the predicted most likely class for the instance or
   * 			Instance.missingValue() if no prediction is made
   * @throws Exception 	if an error occurred during the prediction
   */
  @Override
  public double classifyInstance(Instance instance) throws Exception {
    double    result;

    // wait
    if (m_PredictWait > 0)
      wait(m_PredictWait);

    result = getRandom().nextDouble();
    result = result * (m_PredictMax - m_PredictMin) + m_PredictMin;

    return result;
  }

  /**
   * Returns the revision string.
   *
   * @return            the revision
   */
  @Override
  public String getRevision() {
    return "$Revision$";
  }

  /**
   * Returns a string representation of the built model.
   *
   * @return		the model string
   */
  @Override
  public String toString() {
    StringBuilder   result;

    result = new StringBuilder();
    result.append(getClass().getName() + "\n");
    result.append(getClass().getName().replaceAll(".", "=") + "\n");
    result.append("\n");
    result.append("Min: " + getPredictMin() + "\n");
    result.append("Max: " + getPredictMax() + "\n");

    return result.toString();
  }

  /**
   * Main method for running this class.
   *
   * @param args	the commandline parameters
   */
  public static void main(String[] args) {
    runClassifier(new FakeClassifier(), args);
  }
}
