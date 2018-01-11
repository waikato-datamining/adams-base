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
 * Consensus.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package weka.classifiers.meta;

import adams.core.Index;
import weka.classifiers.AbstainingClassifier;
import weka.classifiers.MultipleClassifiersCombiner;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.Utils;
import weka.core.WekaException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

/**
 <!-- globalinfo-start -->
 * Outputs predictions only if the ensemble agrees. In case of agreement, the classifier identified by the 'predictor' index is used for making the actual prediction.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p>
 *
 * <pre> -B &lt;classifier specification&gt;
 *  Full class name of classifier to include, followed
 *  by scheme options. May be specified multiple times.
 *  (default: "weka.classifiers.rules.ZeroR")</pre>
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
 * <pre>
 * Options specific to classifier weka.classifiers.rules.ZeroR:
 * </pre>
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
 * <pre> -predictor &lt;1-based index&gt;
 *  The index of the classifier for making the actual predictions.
 *  (default: 1)
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Consensus
  extends MultipleClassifiersCombiner
  implements AbstainingClassifier {

  private static final long serialVersionUID = 4801293402527570640L;

  /** the index of the classifier to use for making the actual prediction. */
  protected Index m_Predictor = new Index("1");

  /**
   * Returns a string describing classifier
   * @return a description suitable for
   * displaying in the explorer/experimenter gui
   */
  public String globalInfo() {
    return
      "Outputs predictions only if the ensemble agrees. In case of agreement, "
	+ "the classifier identified by the 'predictor' index is used for "
	+ "making the actual prediction.";
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  public Enumeration listOptions() {
    Enumeration 	enm;
    Vector 		result;

    result = new Vector();

    enm = super.listOptions();
    while (enm.hasMoreElements())
      result.addElement(enm.nextElement());


    result.addElement(new Option("\tThe index of the classifier for making the actual predictions.\n"
      + "\t(default: 1)\n",
      "predictor", 1, "-predictor <1-based index>"));

    return result.elements();
  }

  /**
   * Gets the current settings of Vote.
   *
   * @return an array of strings suitable for passing to setOptions()
   */
  public String [] getOptions() {
    List<String> result;

    result = new ArrayList<>(Arrays.asList(super.getOptions()));

    result.add("-predictor");
    result.add(getPredictor().getIndex());

    return result.toArray(new String[result.size()]);
  }

  /**
   * Parses a given list of options.
   *
   * @param options the list of options as an array of strings
   * @throws Exception if an option is not supported
   */
  public void setOptions(String[] options) throws Exception {
    String 	tmpStr;

    tmpStr = Utils.getOption("predictor", options);
    if (!tmpStr.isEmpty())
      setPredictor(new Index(tmpStr));
    else
      setPredictor(new Index("1"));

    super.setOptions(options);
  }

  /**
   * Sets the index of the classifier for making the actual predictions.
   *
   * @param value	the index (1-based)
   */
  public void setPredictor(Index value) {
    m_Predictor = value;
  }

  /**
   * Returns the index of the classifier for making the actual predictions.
   *
   * @return 		the index (1-based)
   */
  public Index getPredictor() {
    return m_Predictor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String predictorTipText() {
    return "The index (1-based) of the classifier to make the actual predictions.";
  }

  /**
   * Generates a classifier.
   *
   * @param data set of instances serving as training data
   * @throws Exception if the classifier has not been generated successfully
   */
  @Override
  public void buildClassifier(Instances data) throws Exception {
    int		i;

    getCapabilities().testWithFail(data);

    data = new Instances(data);
    data.deleteWithMissingClass();

    m_Predictor.setMax(m_Classifiers.length);
    if (m_Predictor.getIntIndex() == -1)
      throw new WekaException("Failed to determine predictor index using: " + m_Predictor);

    for (i = 0; i < m_Classifiers.length; i++)
      m_Classifiers[i].buildClassifier(data);
  }

  /**
   * Checks whether there is consensus between the classifiers.
   *
   * @param instance	the instance to check
   * @return		true if consensus
   * @throws Exception	if classification fails
   */
  protected boolean consensus(Instance instance) throws Exception {
    double 	first;
    double 	next;
    int		i;

    first = Utils.missingValue();
    for (i = 0; i < m_Classifiers.length; i++) {
      if (i == 0) {
	first = m_Classifiers[i].classifyInstance(instance);
      }
      else {
	next = m_Classifiers[i].classifyInstance(instance);
	if (next != first)
	  return false;
      }
    }

    return true;
  }

  /**
   * Classifies the given test instance. The instance has to belong to a
   * dataset when it's being classified.
   *
   * @param instance the instance to be classified
   * @return the predicted most likely class for the instance or
   * Utils.missingValue() if no prediction is made
   * @throws Exception if an error occurred during the prediction
   */
  public double classifyInstance(Instance instance) throws Exception {
    double	result;

    if (consensus(instance))
      result = m_Classifiers[m_Predictor.getIntIndex()].classifyInstance(instance);
    else
      result = Utils.missingValue();

    return result;
  }

  /**
   * Predicts the class memberships for a given instance. If
   * an instance is unclassified, the returned array elements
   * must be all zero. If the class is numeric, the array
   * must consist of only one element, which contains the
   * predicted value.
   *
   * @param instance the instance to be classified
   * @return an array containing the estimated membership
   * probabilities of the test instance in each class
   * or the numeric prediction
   * @throws Exception if distribution could not be
   * computed successfully
   */
  public double[] distributionForInstance(Instance instance) throws Exception {
    double[]	result;

    if (consensus(instance))
      result = m_Classifiers[m_Predictor.getIntIndex()].distributionForInstance(instance);
    else
      result = new double[instance.numClasses()];

    return result;
  }

  /**
   * Whether abstaining is possible, e.g., used in meta-classifiers.
   *
   * @return		true if abstaining is possible
   */
  @Override
  public boolean canAbstain() {
    return (m_Classifiers.length > 1);
  }

  /**
   * The prediction that made the classifier abstain.
   *
   * @param inst	the instance to get the prediction for
   * @return		the prediction
   * @throws Exception	if fails to make prediction
   */
  @Override
  public double getAbstentionClassification(Instance inst) throws Exception {
    return m_Classifiers[m_Predictor.getIntIndex()].classifyInstance(inst);
  }

  /**
   * The class distribution that made the classifier abstain.
   *
   * @param inst	the instance to get the prediction for
   * @return		the class distribution
   * @throws Exception	if fails to make prediction
   */
  @Override
  public double[] getAbstentionDistribution(Instance inst) throws Exception {
    return m_Classifiers[m_Predictor.getIntIndex()].distributionForInstance(inst);
  }
}
