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
 * GeneticAlgorithm.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */
package weka.classifiers.functions;

import adams.core.option.OptionUtils;
import adams.genetic.AbstractClassifierBasedGeneticAlgorithm;
import adams.genetic.Hermione;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.core.Capabilities;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

/**
 <!-- globalinfo-start -->
 * Applies the specified genetic algorithm to the training data and uses the best setup for the final model.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre> -algorithm &lt;classname + options&gt;
 *  The genetic algorithm.
 *  (default: adams.genetic.Hermione)</pre>
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
public class GeneticAlgorithm
  extends AbstractClassifier {

  /** for serialization. */
  private static final long serialVersionUID = 8430850643799590721L;

  /** the genetic algorithm. */
  protected AbstractClassifierBasedGeneticAlgorithm m_Algorithm = new Hermione();

  /** the final model. */
  protected Classifier m_Model = null;

  /**
   * Returns a string describing classifier
   *
   * @return a description suitable for displaying in the
   *         explorer/experimenter gui
   */
  public String globalInfo() {
    return
      "Applies the specified genetic algorithm to the training data and "
          + "uses the best setup for the final model.";
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
      "\tThe genetic algorithm.\n"
	+ "\t(default: " + Hermione.class.getName() + ")",
      "algorithm", 1, "-algorithm <classname + options>"));

    en = super.listOptions();
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
    List<String> 	result;

    result = new ArrayList<>();

    result.add("-algorithm");
    result.add(OptionUtils.getCommandLine(m_Algorithm));

    result.addAll(Arrays.asList(super.getOptions()));

    return result.toArray(new String[result.size()]);
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

    tmpStr = Utils.getOption("algorithm", options);
    if (tmpStr.length() != 0)
      setAlgorithm(
	(AbstractClassifierBasedGeneticAlgorithm) OptionUtils.forAnyCommandLine(
	  AbstractClassifierBasedGeneticAlgorithm.class, tmpStr));
    else
      setAlgorithm(new Hermione());

    super.setOptions(options);
  }

  /**
   * Sets the genetic algorithm to use.
   *
   * @param value 	the algorithm
   */
  public void setAlgorithm(AbstractClassifierBasedGeneticAlgorithm value) {
    m_Algorithm = value;
  }

  /**
   * Returns the seed value for the random values.
   *
   * @return 		the seed
   */
  public AbstractClassifierBasedGeneticAlgorithm getAlgorithm() {
    return m_Algorithm;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String algorithmTipText() {
    return "The genetic algorithm to use.";
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
    result.assign(m_Algorithm.getClassifier().getCapabilities());

    return result;
  }

  /**
   * Generates a classifier.
   *
   * @param data 	set of instances serving as training data
   * @throws Exception 	if the classifier has not been generated successfully
   */
  @Override
  public void buildClassifier(Instances data) throws Exception {
    m_Algorithm.setInstances(data);
    m_Algorithm.run();
    m_Model = (Classifier) m_Algorithm.getCurrentSetup();
    m_Model.buildClassifier(data);
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
    if (m_Model == null)
      throw new IllegalStateException("No model built!");

    return m_Model.classifyInstance(instance);
  }

  /**
   * Predicts the class memberships for a given instance. If an instance is
   * unclassified, the returned array elements must be all zero. If the class is
   * numeric, the array must consist of only one element, which contains the
   * predicted value.
   *
   * @param instance the instance to be classified
   * @return an array containing the estimated membership probabilities of the
   *         test instance in each class or the numeric prediction
   * @throws Exception if distribution could not be computed successfully
   */
  @Override
  public double[] distributionForInstance(Instance instance) throws Exception {
    if (m_Model == null)
      throw new IllegalStateException("No model built!");

    return m_Model.distributionForInstance(instance);
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
    if (m_Model == null)
      result.append("No model built yet");
    else
      result.append("Genetic algorithm: ")
	.append(OptionUtils.getCommandLine(m_Algorithm))
	.append("\n")
	.append(m_Model.toString());

    return result.toString();
  }

  /**
   * Main method for running this class.
   *
   * @param args	the commandline parameters
   */
  public static void main(String[] args) {
    runClassifier(new GeneticAlgorithm(), args);
  }
}
