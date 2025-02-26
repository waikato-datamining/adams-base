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
 * PLSRegressor.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package weka.classifiers.functions;

import adams.core.ObjectCopyHelper;
import adams.core.option.OptionUtils;
import adams.data.instancesanalysis.pls.AbstractPLS;
import adams.data.instancesanalysis.pls.PLS1;
import adams.data.instancesanalysis.pls.PredictionType;
import weka.classifiers.RandomizableClassifier;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.RevisionUtils;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.supervised.attribute.PLS;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;

/**
 <!-- globalinfo-start -->
 * A wrapper classifier for the PLS filter, utilizing the filter's ability to perform predictions.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p>
 *
 * <pre> -algorithm &lt;algorithm specification&gt;
 *  The PLS algorithm to use. Full classname of algorithm to include,  followed by scheme options.
 *  (default: adams.data.instancesanalysis.pls.PLS1)</pre>
 *
 * <pre> -S &lt;num&gt;
 *  Random number seed.
 *  (default 1)</pre>
 *
 * <pre> -output-debug-info
 *  If set, classifier is run in debug mode and
 *  may output additional info to the console</pre>
 *
 * <pre> -do-not-check-capabilities
 *  If set, classifier capabilities are not checked before classifier is built
 *  (use with caution).</pre>
 *
 * <pre> -num-decimal-places
 *  The number of decimal places for the output of numbers in the model (default 2).</pre>
 *
 * <pre> -batch-size
 *  The desired batch size for batch prediction  (default 100).</pre>
 *
 <!-- options-end -->
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class PLSRegressor
  extends RandomizableClassifier {

  private static final long serialVersionUID = -285299657101510738L;

  /** the PLS algorithm */
  protected AbstractPLS m_Algorithm = getDefaultAlgorithm();

  /** the actual filter to use */
  protected PLS m_ActualFilter = null;

  /**
   * Returns a string describing classifier
   *
   * @return a description suitable for displaying in the explorer/experimenter
   *         gui
   */
  public String globalInfo() {
    return "A wrapper classifier for the PLS filter, utilizing the filter's ability to perform predictions.";
  }

  /**
   * Gets an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  @Override
  public Enumeration<Option> listOptions() {
    Vector<Option> result = new Vector<>();

    result.addElement(new Option(
      "\tThe PLS algorithm to use. Full classname of algorithm to include, "
	+ "\tfollowed by scheme options.\n"
	+ "\t(default: " + getDefaultAlgorithm().getClass().getName() + ")", "algorithm",
      1, "-algorithm <algorithm specification>"));

    result.addAll(Collections.list(super.listOptions()));

    return result.elements();
  }

  /**
   * returns the options of the current setup
   *
   * @return the current options
   */
  @Override
  public String[] getOptions() {
    Vector<String> result = new Vector<>();

    result.add("-algorithm");
    result.add(OptionUtils.getCommandLine(m_Algorithm));

    Collections.addAll(result, super.getOptions());

    return result.toArray(new String[0]);
  }

  /**
   * Parses the options for this object.
   *
   * @param options the options to use
   * @throws Exception if setting of options fails
   */
  @Override
  public void setOptions(String[] options) throws Exception {
    String tmpStr = Utils.getOption("algorithm", options);
    if (!tmpStr.isEmpty())
      setAlgorithm((AbstractPLS) OptionUtils.forCommandLine(AbstractPLS.class, tmpStr));
    else
      setAlgorithm(getDefaultAlgorithm());

    super.setOptions(options);
  }

  /**
   * Returns the default algorithm.
   *
   * @return		the default
   */
  protected AbstractPLS getDefaultAlgorithm() {
    return new PLS1();
  }

  /**
   * Sets the PLS algorithm to use.
   *
   * @param value 	the algorithm
   */
  public void setAlgorithm(AbstractPLS value) {
    m_Algorithm = value;
  }

  /**
   * Returns the PLS algorithm to use.
   *
   * @return 		the algorithm
   */
  public AbstractPLS getAlgorithm() {
    return m_Algorithm;
  }

  /**
   * Returns the tip text for this property
   *
   * @return tip text for this property suitable for displaying in the
   *         explorer/experimenter gui
   */
  public String algorithmTipText() {
    return "The PLS algorithm to use in the PLS filter.";
  }

  /**
   * Returns default capabilities of the classifier.
   *
   * @return the capabilities of this classifier
   */
  @Override
  public Capabilities getCapabilities() {
    Capabilities result = getAlgorithm().getCapabilities();

    // class
    result.enable(Capability.MISSING_CLASS_VALUES);

    // other
    result.setMinimumNumberInstances(1);

    return result;
  }

  /**
   * builds the classifier
   *
   * @param data the training instances
   * @throws Exception if something goes wrong
   */
  @Override
  public void buildClassifier(Instances data) throws Exception {
    // do we need to resample?
    boolean resample = false;
    for (int i = 0; i < data.numInstances(); i++) {
      if (data.instance(i).weight() != 1.0) {
	resample = true;
	break;
      }
    }
    if (resample) {
      if (getDebug())
	System.err.println(getClass().getName() + ": resampling training data");
      data = data.resampleWithWeights(new Random(m_Seed));
    }

    // can classifier handle the data?
    getCapabilities().testWithFail(data);

    // remove instances with missing class
    data = new Instances(data);
    data.deleteWithMissingClass();

    // initialize filter
    m_ActualFilter = new PLS();
    m_ActualFilter.setAlgorithm(ObjectCopyHelper.copyObject(m_Algorithm));
    m_ActualFilter.getAlgorithm().setPredictionType(PredictionType.NONE);
    m_ActualFilter.setInputFormat(data);
    Filter.useFilter(data, m_ActualFilter);
    m_ActualFilter.getAlgorithm().setPredictionType(PredictionType.ALL);
  }

  /**
   * Classifies the given test instance. The instance has to belong to a dataset
   * when it's being classified.
   *
   * @param instance the instance to be classified
   * @return the predicted most likely class for the instance or
   *         Utils.missingValue() if no prediction is made
   * @throws Exception if an error occurred during the prediction
   */
  @Override
  public double classifyInstance(Instance instance) throws Exception {
    double result;
    Instance pred;

    m_ActualFilter.input(instance);
    m_ActualFilter.batchFinished();
    pred = m_ActualFilter.output();
    result = pred.classValue();

    return result;
  }

  /**
   * returns a string representation of the classifier
   *
   * @return a string representation of the classifier
   */
  @Override
  public String toString() {
    String result;

    result = this.getClass().getName() + "\n"
	       + this.getClass().getName().replaceAll(".", "=") + "\n\n";
    result += "Algorithm: " + OptionUtils.getCommandLine(m_Algorithm) + "\n";

    return result;
  }

  /**
   * Returns the revision string.
   *
   * @return the revision
   */
  @Override
  public String getRevision() {
    return RevisionUtils.extract("$Revision$");
  }

  /**
   * Main method for running this classifier from commandline.
   *
   * @param args the options
   */
  public static void main(String[] args) {
    runClassifier(new PLSRegressor(), args);
  }
}
