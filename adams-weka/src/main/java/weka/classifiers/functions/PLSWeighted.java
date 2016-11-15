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
 * PLSWeighted.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package weka.classifiers.functions;

import adams.core.option.OptionUtils;
import weka.classifiers.AbstractClassifier;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.GenericPLSMatrixAccess;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.RevisionUtils;
import weka.core.Utils;
import weka.core.WeightedInstancesHandler;
import weka.core.matrix.Matrix;
import weka.filters.Filter;
import weka.filters.supervised.attribute.pls.AbstractPLS;
import weka.filters.supervised.attribute.pls.AbstractPLS.PredictionType;
import weka.filters.supervised.attribute.pls.PLS1;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
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
 *  The PLS algorithm to use. Full classname of filter to include,  followed by scheme options.
 *  (default: weka.filters.supervised.attribute.pls.PLS1)</pre>
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
 * <pre> 
 * Options specific to algorithm weka.filters.supervised.attribute.pls.PLS1 ('-algorithm'):
 * </pre>
 * 
 * <pre> -debug &lt;value&gt;
 *  If enabled, additional info may be output to the console.
 *  (default: false)</pre>
 * 
 * <pre> -preprocessing &lt;value&gt;
 *  The type of preprocessing to perform.
 *  (default: CENTER)</pre>
 * 
 * <pre> -C &lt;value&gt;
 *  The number of components to compute.
 *  (default: 20)</pre>
 * 
 * <pre> -prediction &lt;value&gt;
 *  The type of prediction to perform.
 *  (default: NONE)</pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PLSWeighted
  extends AbstractClassifier
  implements WeightedInstancesHandler, GenericPLSMatrixAccess {

  /** for serialization */
  private static final long serialVersionUID = 4819775160590973256L;

  /** the PLS algorithm */
  protected AbstractPLS m_Algorithm = getDefaultAlgorithm();

  /** the actual filter to use */
  protected weka.filters.supervised.attribute.PLS m_Filter = null;

  /**
   * Returns a string describing classifier
   *
   * @return a description suitable for displaying in the
   *         explorer/experimenter gui
   */
  public String globalInfo() {
    return
        "A wrapper classifier for the PLS filter, utilizing the filter's "
      + "ability to perform predictions.";
  }

  /**
   * Gets an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  @Override
  public Enumeration listOptions() {
    Vector<Option>	result;
    Enumeration   	en;

    result = new Vector<>();

    result.addElement(new Option(
	"\tThe PLS algorithm to use. Full classname of filter to include, "
	+ "\tfollowed by scheme options.\n"
	+ "\t(default: " + getDefaultAlgorithm().getClass().getName() + ")",
	"algorithm", 1, "-algorithm <algorithm specification>"));

    en = super.listOptions();
    while (en.hasMoreElements())
      result.addElement((Option) en.nextElement());

    result.addElement(new Option(
      "",
      "", 0, "\nOptions specific to algorithm "
      + getAlgorithm().getClass().getName() + " ('-algorithm'):"));

    en = getAlgorithm().listOptions();
    while (en.hasMoreElements())
      result.addElement((Option) en.nextElement());

    return result.elements();
  }

  /**
   * returns the options of the current setup
   *
   * @return		the current options
   */
  @Override
  public String[] getOptions() {
    int       		i;
    List<String> result;
    String[]  		options;

    result = new ArrayList<>();

    result.add("-algorithm");
    result.add(OptionUtils.getCommandLine(m_Algorithm));

    options = super.getOptions();
    for (i = 0; i < options.length; i++)
      result.add(options[i]);

    return result.toArray(new String[result.size()]);
  }

  /**
   * Parses the options for this object.
   *
   *
   * @param options	the options to use
   * @throws Exception	if setting of options fails
   */
  @Override
  public void setOptions(String[] options) throws Exception {
    String	tmpStr;
    String[]	tmpOptions;

    super.setOptions(options);

    tmpStr     = Utils.getOption("algorithm", options);
    tmpOptions = Utils.splitOptions(tmpStr);
    if (tmpOptions.length != 0) {
      tmpStr        = tmpOptions[0];
      tmpOptions[0] = "";
      setAlgorithm((AbstractPLS) OptionUtils.forName(AbstractPLS.class, tmpStr, tmpOptions));
    }
    else {
      setAlgorithm(getDefaultAlgorithm());
    }
  }

  /**
   * Returns the default PLS filter.
   *
   * @return		the default filter
   */
  public AbstractPLS getDefaultAlgorithm() {
    PLS1	result;

    result = new PLS1();
    result.setPredictionType(PredictionType.ALL);

    return result;
  }

  /**
   * Set the PLS algorithm (only used for setup).
   *
   * @param value	the algorithm
   */
  public void setAlgorithm(AbstractPLS value) {
    m_Algorithm = value;
  }

  /**
   * Get the PLS algorithm.
   *
   * @return 		the PLS algorithm
   */
  public AbstractPLS getAlgorithm() {
    return m_Algorithm;
  }

  /**
   * Returns the tip text for this property
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String algorithmTipText() {
    return "The PLS algorithm to be used (only used for setup).";
  }

  /**
   * Returns default capabilities of the classifier.
   *
   * @return		the capabilities of this classifier
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
   * @param data        the training instances
   * @throws Exception  if something goes wrong
   */
  public void buildClassifier(Instances data) throws Exception {
    AbstractPLS		pls;

    // can classifier handle the data?
    getCapabilities().testWithFail(data);

    // remove instances with missing class
    data = new Instances(data);
    data.deleteWithMissingClass();

    // initialize filter
    pls      = (AbstractPLS) OptionUtils.shallowCopy(m_Algorithm);
    pls.setPredictionType(PredictionType.EXCEPT_CLASS);
    m_Filter = new weka.filters.supervised.attribute.PLS();
    m_Filter.setAlgorithm(pls);
    m_Filter.setInputFormat(data);
    Filter.useFilter(data, m_Filter);
    pls.setPredictionType(PredictionType.ALL);
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
    double	result;
    Instance	pred;

    m_Filter.input(instance);
    m_Filter.batchFinished();
    pred   = m_Filter.output();
    result = pred.classValue();

    return result;
  }

  /**
   * Returns the all the available matrices.
   *
   * @return		the names of the matrices
   */
  public String[] getMatrixNames() {
    return m_Filter.getMatrixNames();
  }

  /**
   * Returns the matrix with the specified name.
   *
   * @param name	the name of the matrix
   * @return		the matrix, null if not available
   */
  public Matrix getMatrix(String name) {
    return m_Filter.getMatrix(name);
  }

  /**
   * Whether the algorithm supports return of loadings.
   *
   * @return		true if supported
   * @see		#getLoadings()
   */
  public boolean hasLoadings() {
    return m_Filter.hasLoadings();
  }

  /**
   * Returns the loadings, if available.
   *
   * @return		the loadings, null if not available
   */
  public Matrix getLoadings() {
    return m_Filter.getLoadings();
  }

  /**
   * returns a string representation of the classifier
   *
   * @return		a string representation of the classifier
   */
  @Override
  public String toString() {
    String	result;

    result =   getClass().getName() + "\n"
             + getClass().getName().replaceAll(".", "=") + "\n\n";
    result += "Algorithm: " + OptionUtils.getCommandLine(m_Algorithm) + "\n";

    return result;
  }

  /**
   * Returns the revision string.
   *
   * @return		the revision
   */
  @Override
  public String getRevision() {
    return RevisionUtils.extract("$Revision$");
  }

  /**
   * Main method for running this classifier from commandline.
   *
   * @param args 	the options
   */
  public static void main(String[] args) {
    runClassifier(new PLSWeighted(), args);
  }
}
