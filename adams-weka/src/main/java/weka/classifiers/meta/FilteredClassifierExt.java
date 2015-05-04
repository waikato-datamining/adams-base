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
 *    FilteredClassifierExt.java
 *    Copyright (C) 1999-2010 University of Waikato, Hamilton, New Zealand
 *
 */

package weka.classifiers.meta;

import weka.classifiers.AbstainingClassifier;
import weka.classifiers.IntervalEstimator;
import weka.core.Capabilities;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.RevisionUtils;
import weka.core.Utils;
import weka.core.WeightedInstancesHandler;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Vector;

/**
 <!-- globalinfo-start -->
 * Class for running an arbitrary classifier on data that has been passed through an arbitrary filter. Like the classifier, the structure of the filter is based exclusively on the training data and test instances will be processed by the filter without changing their structure.<br/>
 * <br/>
 * In addition to the default FilteredClassifier, one can specify a range of attributes that are to be removed before applying the actual filter. Useful to remove ID attributes, without having to nest another FilteredClassifier.<br/>
 * <br/>
 * The meta-classifier also 'pretends' to be able to:<br/>
 * - handle weighted instances<br/>
 * - produce confidence intervals<br/>
 * This will only lead to reasonable results, of course, if the base classifier supports this functionality.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre> -R &lt;att list&gt;
 *  The range of attributes to remove. 'first' and 'last' are
 *  accepted as well.
 *  (default: none)</pre>
 *
 * <pre> -F &lt;filter specification&gt;
 *  Full class name of filter to use, followed
 *  by filter options.
 *  eg: "weka.filters.unsupervised.attribute.Remove -V -R 1,2"</pre>
 *
 * <pre> -D
 *  If set, classifier is run in debug mode and
 *  may output additional info to the console</pre>
 *
 * <pre> -W
 *  Full name of base classifier.
 *  (default: weka.classifiers.trees.J48)</pre>
 *
 * <pre>
 * Options specific to classifier weka.classifiers.trees.J48:
 * </pre>
 *
 * <pre> -U
 *  Use unpruned tree.</pre>
 *
 * <pre> -O
 *  Do not collapse tree.</pre>
 *
 * <pre> -C &lt;pruning confidence&gt;
 *  Set confidence threshold for pruning.
 *  (default 0.25)</pre>
 *
 * <pre> -M &lt;minimum number of instances&gt;
 *  Set minimum number of instances per leaf.
 *  (default 2)</pre>
 *
 * <pre> -R
 *  Use reduced error pruning.</pre>
 *
 * <pre> -N &lt;number of folds&gt;
 *  Set number of folds for reduced error
 *  pruning. One fold is used as pruning set.
 *  (default 3)</pre>
 *
 * <pre> -B
 *  Use binary splits only.</pre>
 *
 * <pre> -S
 *  Don't perform subtree raising.</pre>
 *
 * <pre> -L
 *  Do not clean up after the tree has been built.</pre>
 *
 * <pre> -A
 *  Laplace smoothing for predicted probabilities.</pre>
 *
 * <pre> -J
 *  Do not use MDL correction for info gain on numeric attributes.</pre>
 *
 * <pre> -Q &lt;seed&gt;
 *  Seed for random data shuffling (default 1).</pre>
 *
 <!-- options-end -->
 *
 * @author Len Trigg (trigg@cs.waikato.ac.nz)
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FilteredClassifierExt
  extends FilteredClassifier
  implements WeightedInstancesHandler, IntervalEstimator, AbstainingClassifier {

  /** for serialization. */
  private static final long serialVersionUID = -696353491455375160L;

  /** The additional remove filter. */
  protected Remove m_Remove = new weka.filters.unsupervised.attribute.Remove();
  
  /** whether the base classifier can abstain. */
  protected boolean m_CanAbstain = false;

  /**
   * Returns a string describing this classifier.
   *
   * @return 		a description of the classifier suitable for
   * 			displaying in the explorer/experimenter gui
   */
  @Override
  public String globalInfo() {
    return
        super.globalInfo()
      + "\n\n"
      + "In addition to the default FilteredClassifier, one can specify a "
      + "range of attributes that are to be removed before applying the "
      + "actual filter. Useful to remove ID attributes, without having to "
      + "nest another FilteredClassifier.\n"
      + "\n"
      + "The meta-classifier also 'pretends' to be able to:\n"
      + "- handle weighted instances\n"
      + "- produce confidence intervals\n"
      + "This will only lead to reasonable results, of course, if the base "
      + "classifier supports this functionality.";
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  @Override
  public Enumeration listOptions() {
    Vector 	result;
    Enumeration	enm;

    result = new Vector();
    result.addElement(new Option(
	"\tThe range of attributes to remove. 'first' and 'last' are \n"
	+ "\taccepted as well.\n"
	+ "\t(default: none)",
	"R", 1, "-R <att list>"));

    enm = super.listOptions();
    while (enm.hasMoreElements())
      result.addElement(enm.nextElement());

    return result.elements();
  }

  /**
   * Parses a given list of options. <p/>
   *
   <!-- options-start -->
   * Valid options are: <p/>
   *
   * <pre> -R &lt;att list&gt;
   *  The range of attributes to remove. 'first' and 'last' are
   *  accepted as well.
   *  (default: none)</pre>
   *
   * <pre> -F &lt;filter specification&gt;
   *  Full class name of filter to use, followed
   *  by filter options.
   *  eg: "weka.filters.unsupervised.attribute.Remove -V -R 1,2"</pre>
   *
   * <pre> -D
   *  If set, classifier is run in debug mode and
   *  may output additional info to the console</pre>
   *
   * <pre> -W
   *  Full name of base classifier.
   *  (default: weka.classifiers.trees.J48)</pre>
   *
   * <pre>
   * Options specific to classifier weka.classifiers.trees.J48:
   * </pre>
   *
   * <pre> -U
   *  Use unpruned tree.</pre>
   *
   * <pre> -O
   *  Do not collapse tree.</pre>
   *
   * <pre> -C &lt;pruning confidence&gt;
   *  Set confidence threshold for pruning.
   *  (default 0.25)</pre>
   *
   * <pre> -M &lt;minimum number of instances&gt;
   *  Set minimum number of instances per leaf.
   *  (default 2)</pre>
   *
   * <pre> -R
   *  Use reduced error pruning.</pre>
   *
   * <pre> -N &lt;number of folds&gt;
   *  Set number of folds for reduced error
   *  pruning. One fold is used as pruning set.
   *  (default 3)</pre>
   *
   * <pre> -B
   *  Use binary splits only.</pre>
   *
   * <pre> -S
   *  Don't perform subtree raising.</pre>
   *
   * <pre> -L
   *  Do not clean up after the tree has been built.</pre>
   *
   * <pre> -A
   *  Laplace smoothing for predicted probabilities.</pre>
   *
   * <pre> -J
   *  Do not use MDL correction for info gain on numeric attributes.</pre>
   *
   * <pre> -Q &lt;seed&gt;
   *  Seed for random data shuffling (default 1).</pre>
   *
   <!-- options-end -->
   *
   * @param options 	the list of options as an array of strings
   * @throws Exception 	if an option is not supported
   */
  @Override
  public void setOptions(String[] options) throws Exception {
    String	tmpStr;

    tmpStr = Utils.getOption('R', options);
    if (tmpStr.length() != 0)
      setRemoveAttributeIndices(tmpStr);
    else
      setRemoveAttributeIndices("");

    super.setOptions(options);
  }

  /**
   * Gets the current settings of the Classifier.
   *
   * @return 		an array of strings suitable for passing to setOptions
   */
  @Override
  public String[] getOptions() {
    Vector<String>	result;

    result = new Vector<String>();

    if (getRemoveAttributeIndices().length() > 0) {
      result.add("-R");
      result.add(getRemoveAttributeIndices());
    }

    result.addAll(Arrays.asList(super.getOptions()));

    return result.toArray(new String[result.size()]);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String removeAttributeIndicesTipText() {
    return "The attributes to remove before applying the actual filter.";
  }

  /**
   * Sets the attribute indices to remove before applying the actual filter.
   *
   * @param value	the attribute indices (1-based)
   */
  public void setRemoveAttributeIndices(String value) {
    m_Remove.setAttributeIndices(value);
  }

  /**
   * Returns the attributes indices that are removed before applying the actual
   * filter.
   *
   * @return 		the attribute indices (1-based)
   */
  public String getRemoveAttributeIndices() {
    return m_Remove.getAttributeIndices();
  }

  /**
   * Returns whether the Remove filter is used at all.
   *
   * @return		true if the Remove filter is used
   */
  protected boolean isRemoveUsed() {
    return (m_Remove.getAttributeIndices().length() > 0);
  }

  /**
   * Returns default capabilities of the classifier.
   *
   * @return      the capabilities of this classifier
   */
  @Override
  public Capabilities getCapabilities() {
    if (isRemoveUsed())
      return m_Remove.getCapabilities();
    else
      return super.getCapabilities();
  }

  /**
   * Filters the dataset through the remove filter if necessary.
   *
   * @param data	the data to filter
   * @return		the processed data
   * @throws Exception	if filtering fails
   * @see		#m_Remove
   */
  protected Instances filter(Instances data) throws Exception {
    if (isRemoveUsed()) {
      // assumption: the remove filter should handle all types of data...
      m_Remove.setInputFormat(data);
      data = Filter.useFilter(data, m_Remove);
    }

    return data;
  }

  /**
   * Build the classifier on the filtered data.
   *
   * @param data the training data
   * @throws Exception if the classifier could not be built successfully
   */
  @Override
  public void buildClassifier(Instances data) throws Exception {
    super.buildClassifier(filter(data));
    m_CanAbstain = (m_Classifier instanceof AbstainingClassifier) && ((AbstainingClassifier) m_Classifier).canAbstain();
  }

  /**
   * Filters the Instance through the specified filter.
   *
   * @param filter	the filter to use
   * @param instance	the instance to filter
   * @return		the processed instance
   * @throws Exception	if filtering fails
   */
  protected Instance filter(Filter filter, Instance instance) throws Exception {
    String	filterName;

    filterName = filter.getClass().getName().replaceAll(".*\\.", "");

    if (filter.numPendingOutput() > 0)
      throw new Exception(filterName + " filter output queue not empty!");
    if (!filter.input(instance))
      throw new Exception(filterName + " filter didn't make the test instance immediately available!");
    filter.batchFinished();
    instance = filter.output();

    return instance;
  }

  /**
   * Filters the Instance through the remove filter if necessary.
   *
   * @param instance	the instance to filter
   * @return		the processed instance
   * @throws Exception	if filtering fails
   * @see		#m_Remove
   */
  protected Instance filter(Instance instance) throws Exception {
    if (isRemoveUsed())
      instance = filter(m_Remove, instance);

    return instance;
  }

  /**
   * Classifies a given instance after filtering.
   *
   * @param instance 	the instance to be classified
   * @return 		the class distribution for the given instance
   * @throws Exception 	if instance could not be classified successfully
   */
  @Override
  public double[] distributionForInstance(Instance instance) throws Exception {
    return super.distributionForInstance(filter(instance));
  }

  /**
   * Batch scoring method. Calls the appropriate method for the base learner
   * if it implements BatchPredictor. Otherwise it simply calls the
   * distributionForInstance() method repeatedly.
   *
   * @param insts the instances to get predictions for
   * @return an array of probability distributions, one for each instance
   * @throws Exception if a problem occurs
   */
  public double[][] distributionsForInstances(Instances insts) throws Exception {
    return super.distributionsForInstances(filter(insts));
  }

  /**
   * Returns an N * 2 array, where N is the number of prediction
   * intervals. In each row, the first element contains the lower
   * boundary of the corresponding prediction interval and the second
   * element the upper boundary.
   *
   * @param instance 		the instance to make the prediction for.
   * @param confidenceLevel 	the percentage of cases that the interval should cover.
   * @return 			an array of prediction intervals
   * @throws Exception 		if the intervals can't be computed
   */
  public double[][] predictIntervals(Instance instance, double confidenceLevel) throws Exception {
    if (m_Classifier instanceof IntervalEstimator)
      return ((IntervalEstimator) m_Classifier).predictIntervals(filter(m_Filter, filter(instance)), confidenceLevel);
    else
      return new double[0][];
  }

  /**
   * Whether abstaining is possible, e.g., used in meta-classifiers.
   * 
   * @return		true if abstaining is possible
   */
  @Override
  public boolean canAbstain() {
    return m_CanAbstain;
  }

  /**
   * The prediction that made the classifier abstain.
   * 
   * @param inst	the instance to get the prediction for
   * @return		the prediction, {@link Utils#missingValue()} if abstaining is not possible
   * @throws Exception	if fails to make prediction
   */
  @Override
  public synchronized double getAbstentionClassification(Instance inst) throws Exception {
    if (m_CanAbstain)
      return ((AbstainingClassifier) m_Classifier).getAbstentionClassification(filter(m_Filter, filter(inst)));
    else
      return Utils.missingValue();
  }

  /**
   * The class distribution that made the classifier abstain.
   * 
   * @param inst	the instance to get the prediction for
   * @return		the class distribution, null if abstaining is not possible
   * @throws Exception	if fails to make prediction
   */
  @Override
  public synchronized double[] getAbstentionDistribution(Instance inst) throws Exception {
    if (m_CanAbstain) 
      return ((AbstainingClassifier) m_Classifier).getAbstentionDistribution(filter(m_Filter, filter(inst)));
    else
      return null;
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
   * Main method for running this classifier.
   *
   * @param args 	the parameters, use -h to display them
   */
  public static void main(String[] args)  {
    runClassifier(new FilteredClassifierExt(), args);
  }
}
