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
 *    LWLSynchroSplit.java
 *    Copyright (C) 1999-2024 University of Waikato, Hamilton, New Zealand
 *
 */

package weka.classifiers.lazy;

import adams.core.StoppableUtils;
import adams.core.StoppableWithFeedback;
import adams.core.option.OptionUtils;
import weka.classifiers.ThreadSafeClassifier;
import weka.classifiers.functions.GPD;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.RevisionUtils;
import weka.core.Utils;
import weka.core.neighboursearch.NearestNeighbourSearch;
import weka.core.neighboursearch.NearestNeighbourSearchWithIndices;
import weka.core.neighboursearch.NewNNSearchWithIndices;
import weka.filters.AllFilter;
import weka.filters.Filter;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Vector;

/**
 <!-- globalinfo-start -->
 * Locally weighted learning. Uses an instance-based algorithm to assign instance weights which are then used by a specified WeightedInstancesHandler.<br>
 * Can do classification (e.g. using naive Bayes) or regression (e.g. using linear regression).<br>
 * <br>
 * For more info, see<br>
 * <br>
 * Eibe Frank, Mark Hall, Bernhard Pfahringer: Locally Weighted Naive Bayes. In: 19th Conference in Uncertainty in Artificial Intelligence, 249-256, 2003.<br>
 * <br>
 * C. Atkeson, A. Moore, S. Schaal (1996). Locally weighted learning. AI Review..<br>
 * <br>
 * This version of LWL applies two filters to the incoming training data:<br>
 *  - search filter: generates the data use for the neighborhood search algorithm<br>
 *  - train filter: generates the data to use for building the base classifierThe dataset indices determined by the search algorithm are used to compile the subset from the train filter dataset which is used for building the base classifier.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- technical-bibtex-start -->
 * BibTeX:
 * <pre>
 * &#64;inproceedings{Frank2003,
 *    author = {Eibe Frank and Mark Hall and Bernhard Pfahringer},
 *    booktitle = {19th Conference in Uncertainty in Artificial Intelligence},
 *    pages = {249-256},
 *    publisher = {Morgan Kaufmann},
 *    title = {Locally Weighted Naive Bayes},
 *    year = {2003}
 * }
 *
 * &#64;article{Atkeson1996,
 *    author = {C. Atkeson and A. Moore and S. Schaal},
 *    journal = {AI Review},
 *    title = {Locally weighted learning},
 *    year = {1996}
 * }
 * </pre>
 * <br><br>
 <!-- technical-bibtex-end -->
 *
 <!-- options-start -->
 * Valid options are: <p>
 *
 * <pre> -filter-search &lt;filter specification&gt;
 *  The filter to use for generating the data for the search.
 *  (default: weka.filters.AllFilter)</pre>
 *
 * <pre> -filter-train &lt;filter specification&gt;
 *  The filter to use for generating the data for training the base classifier.
 *  (default: weka.filters.AllFilter)</pre>
 *
 * <pre> -no-update
 *  Suppresses the update of the nearest neighbor search (nns)
 *  algorithm with the data that is to be classified.
 * (default: nns gets updated).
 * </pre>
 *
 * <pre> -A
 *  The nearest neighbour search algorithm to use (default: weka.core.neighboursearch.LinearNNSearch).
 * </pre>
 *
 * <pre> -K &lt;number of neighbours&gt;
 *  Set the number of neighbours used to set the kernel bandwidth.
 *  (default all)</pre>
 *
 * <pre> -U &lt;number of weighting method&gt;
 *  Set the weighting kernel shape to use. 0=Linear, 1=Epanechnikov,
 *  2=Tricube, 3=Inverse, 4=Gaussian.
 *  (default 0 = Linear)</pre>
 *
 * <pre> -W &lt;classifier name&gt;
 *  Full name of base classifier.
 *  (default: weka.classifiers.functions.GPD)</pre>
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
 * Options specific to classifier weka.classifiers.functions.GPD:
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
 * <pre> -L &lt;double&gt;
 *  Level of Gaussian Noise.
 *  (default: 0.01)</pre>
 *
 * <pre> -G &lt;double&gt;
 *  Gamma for the RBF kernel.
 *  (default: 0.01)</pre>
 *
 * <pre> -N
 *  Whether to 0=normalize/1=standardize/2=neither.
 *  (default: 0=normalize)</pre>
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
 * Note: the <code>build(Instance)</code> needs manual syncing with the
 * original WEKA classifier (<code>distributionForInstance(Instance)</code>
 * method).
 *
 * @author Len Trigg (trigg@cs.waikato.ac.nz)
 * @author Eibe Frank (eibe@cs.waikato.ac.nz)
 * @author Ashraf M. Kibriya (amk14[at-the-rate]cs[dot]waikato[dot]ac[dot]nz)
 * @author Dale (dale at waikato dot ac dot nz)
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @see LWL#distributionForInstance(Instance)
 */
public class LWLSynchroSplit
  extends LWL
  implements ThreadSafeClassifier, StoppableWithFeedback {

  /** for serialization. */
  private static final long serialVersionUID = 1979797405383665815L;

  /** whether to suppress the update of the nearest-neighbor search algorithm
   * when making predictions. */
  protected boolean m_NoUpdate;

  /** whether the classifier was stopped. */
  protected boolean m_Stopped;

  /** the filter to apply to the search data. */
  protected Filter m_FilterSearch = new AllFilter();

  /** the filter to apply to the neighborhood data before training the base classifier. */
  protected Filter m_FilterTrain = new AllFilter();

  /** the data to use for the search. */
  protected Instances m_DataSearch;

  /** the data to use for building the base classifier. */
  protected Instances m_DataTrain;

  /**
   * Initializes the classifier.
   */
  public LWLSynchroSplit() {
    super();

    m_Classifier = new GPD();
    m_NNSearch = new NewNNSearchWithIndices();
  }

  /**
   * Returns a string describing classifier.
   *
   * @return a description suitable for
   * displaying in the explorer/experimenter gui
   */
  @Override
  public String globalInfo() {
    return super.globalInfo() + "\n\n"
	     + "This version of LWL applies two filters to the incoming training data:\n"
	     + " - search filter: generates the data use for the neighborhood search algorithm\n"
	     + " - train filter: generates the data to use for building the base classifier"
	     + "The dataset indices determined by the search algorithm are used to compile the subset "
	     + "from the train filter dataset which is used for building the base classifier.";
  }

  /**
   * Default classifier classname.
   *
   * @return		the classname
   */
  @Override
  protected String defaultClassifierString() {
    return GPD.class.getName();
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
      "\tThe filter to use for generating the data for the search.\n"
	+ "\t(default: " + AllFilter.class.getName() + ")",
      "filter-search", 1, "-filter-search <filter specification>"));

    result.addElement(new Option(
      "\tThe filter to use for generating the data for training the base classifier.\n"
	+ "\t(default: " + AllFilter.class.getName() + ")",
      "filter-train", 1, "-filter-train <filter specification>"));

    result.addElement(
      new Option(
	"\tSuppresses the update of the nearest neighbor search (nns)\n"
	  + "\talgorithm with the data that is to be classified.\n"
	  + "(default: nns gets updated).\n",
	"no-update", 0, "-no-update"));

    enm = super.listOptions();
    while (enm.hasMoreElements())
      result.addElement(enm.nextElement());

    return result.elements();
  }

  /**
   * Parses a given list of options.
   *
   * @param options the list of options as an array of strings
   * @throws Exception if an option is not supported
   */
  @Override
  public void setOptions(String[] options) throws Exception {
    String	tmpStr;
    String[]	tmpOptions;

    tmpStr     = Utils.getOption("filter-search", options);
    tmpOptions = Utils.splitOptions(tmpStr);
    if (tmpOptions.length != 0) {
      tmpStr        = tmpOptions[0];
      tmpOptions[0] = "";
      setFilterSearch((Filter) OptionUtils.forName(Filter.class, tmpStr, tmpOptions));
    }
    else {
      setFilterSearch(new AllFilter());
    }

    tmpStr     = Utils.getOption("filter-train", options);
    tmpOptions = Utils.splitOptions(tmpStr);
    if (tmpOptions.length != 0) {
      tmpStr        = tmpOptions[0];
      tmpOptions[0] = "";
      setFilterTrain((Filter) OptionUtils.forName(Filter.class, tmpStr, tmpOptions));
    }
    else {
      setFilterTrain(new AllFilter());
    }

    setNoUpdate(Utils.getFlag("no-update", options));

    super.setOptions(options);
  }

  /**
   * Gets the current settings of the classifier.
   *
   * @return an array of strings suitable for passing to setOptions
   */
  @Override
  public String[] getOptions() {
    Vector<String>	result;

    result = new Vector<>();

    result.add("-filter-search");
    result.add(OptionUtils.getCommandLine(m_FilterSearch));

    result.add("-filter-train");
    result.add(OptionUtils.getCommandLine(m_FilterTrain));

    if (getNoUpdate())
      result.add("-no-update");

    result.addAll(Arrays.asList(super.getOptions()));

    return result.toArray(new String[0]);
  }

  /**
   * Sets the nearestNeighbourSearch algorithm to be used for finding nearest
   * neighbour(s).
   * @param nearestNeighbourSearchAlgorithm - The NearestNeighbourSearch class.
   */
  public void setNearestNeighbourSearchAlgorithm(NearestNeighbourSearch nearestNeighbourSearchAlgorithm) {
    if (nearestNeighbourSearchAlgorithm instanceof NearestNeighbourSearchWithIndices)
      m_NNSearch = nearestNeighbourSearchAlgorithm;
    else
      System.err.println("Nearest neighbour search must implement: " + NearestNeighbourSearchWithIndices.class.getName());
  }

  /**
   * Sets whether to suppress updating the nearest-neighbor search algorithm
   * when making predictions.
   *
   * @param value	if true then no update happens.
   */
  public void setNoUpdate(boolean value) {
    m_NoUpdate = value;
  }

  /**
   * Returns whether to suppress the update of the nearest-neighbor search
   * algorithm when making predictions.
   *
   * @return 		true if the update is suppressed
   */
  public boolean getNoUpdate() {
    return m_NoUpdate;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String noUpdateTipText() {
    return
      "If turned on, suppresses the update of the nearest-neighbor search "
	+ "algorithm when making predictions (EXPERIMENTAL).";
  }

  /**
   * Sets filter to generate the data for the search.
   *
   * @param value	the filter
   */
  public void setFilterSearch(Filter value) {
    m_FilterSearch = value;
  }

  /**
   * Returns the filter to generate the data for the search.
   *
   * @return 		the filter
   */
  public Filter getFilterSearch() {
    return m_FilterSearch;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String filterSearchTipText() {
    return "The filter to apply to the training data to generate the data used by the search.";
  }

  /**
   * Sets filter to use for to generate the training data for the base classifier.
   *
   * @param value	the filter
   */
  public void setFilterTrain(Filter value) {
    m_FilterTrain = value;
  }

  /**
   * Returns the filter to use for to generate the training data for the base classifier.
   *
   * @return 		the filter
   */
  public Filter getFilterTrain() {
    return m_FilterTrain;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String filterTrainTipText() {
    return "The filter to apply to the training data to generate the data used by the base classifier.";
  }

  /**
   * Generates a classifier. Must initialize all fields of the classifier
   * that are not being set via options (ie. multiple calls of buildClassifier
   * must always lead to the same result). Must not change the dataset
   * in any way.
   *
   * @param instances set of instances serving as training data
   * @exception Exception if the classifier has not been
   * generated successfully
   */
  @Override
  public void buildClassifier(Instances instances) throws Exception {
    m_Stopped = false;
    super.buildClassifier(instances);

    if (m_ZeroR == null) {
      m_FilterSearch.setInputFormat(m_Train);
      m_DataSearch = Filter.useFilter(m_Train, m_FilterSearch);
      m_NNSearch.setInstances(m_DataSearch);

      m_FilterTrain.setInputFormat(m_Train);
      m_DataTrain = Filter.useFilter(m_Train, m_FilterTrain);

      if (m_DataSearch.numInstances() != m_DataTrain.numInstances())
	throw new Exception(
	  "Filtered datasets for search and training differ in number of instances: "
	    + m_DataSearch.numInstances() + " != " + m_DataTrain.numInstances());
    }
  }

  /**
   * Builds the classifier.
   * <br><br>
   * Note: needs manual syncing with the distributionForInstance method of the
   * original WEKA classifier.
   *
   * @param instance	the instance to make prediction for
   * @throws Exception	if build fails
   * @see 		LWL#distributionForInstance(Instance)
   */
  protected void build(Instance instance) throws Exception {
    m_FilterSearch.input(instance);
    m_FilterSearch.batchFinished();
    Instance searchInstance = m_FilterSearch.output();

    if (!m_NoUpdate)
      m_NNSearch.addInstanceInfo(searchInstance);

    int k = m_Train.numInstances();
    if( (!m_UseAllK && (m_kNN < k)) /*&&
       !(m_WeightKernel==INVERSE ||
         m_WeightKernel==GAUSS)*/ ) {
      k = m_kNN;
    }

    int[] neighboursIndex = ((NearestNeighbourSearchWithIndices) m_NNSearch).kNearestNeighboursIndices(searchInstance, k);
    Instances trainNeighbours = new Instances(m_DataTrain, neighboursIndex.length);
    for (int index : neighboursIndex)
      trainNeighbours.add(m_DataTrain.instance(index));
    double[] distances = m_NNSearch.getDistances();

    if (m_Debug) {
      System.out.println("Test Instance: " + instance);
      System.out.println("Test Instance (filtered for search): " + searchInstance);
      System.out.println("For " + k + " kept " + neighboursIndex.length + " out of " + m_DataSearch.numInstances() + " instances.");
    }

    //IF LinearNN has skipped so much that <k neighbours are remaining.
    if(k > distances.length)
      k = distances.length;

    if (m_Debug) {
      System.out.println("Instance Distances");
      for (int i = 0; i < distances.length; i++)
	System.out.println((i+1) + ". " + distances[i]);
    }

    // Determine the bandwidth
    double bandwidth = distances[k-1];

    // Check for bandwidth zero
    if (bandwidth <= 0) {
      //if the kth distance is zero than give all instances the same weight
      Arrays.fill(distances, 1);
    } else {
      // Rescale the distances by the bandwidth
      for (int i = 0; i < distances.length; i++)
	distances[i] = distances[i] / bandwidth;
    }

    // Pass the distances through a weighting kernel
    for (int i = 0; i < distances.length; i++) {
      switch (m_WeightKernel) {
	case LINEAR:
	  distances[i] = 1.0001 - distances[i];
	  break;
	case EPANECHNIKOV:
	  distances[i] = 3/4D*(1.0001 - distances[i]*distances[i]);
	  break;
	case TRICUBE:
	  distances[i] = Math.pow( (1.0001 - Math.pow(distances[i], 3)), 3 );
	  break;
	case CONSTANT:
	  distances[i] = 1;
	  break;
	case INVERSE:
	  distances[i] = 1.0 / (1.0 + distances[i]);
	  break;
	case GAUSS:
	  distances[i] = Math.exp(-distances[i] * distances[i]);
	  break;
      }
    }

    if (m_Debug) {
      System.out.println("Instance Weights");
      for (int i = 0; i < distances.length; i++)
	System.out.println((i+1) + ". " + distances[i]);
    }

    // Set the weights on the training data
    double sumOfWeights = 0, newSumOfWeights = 0;
    for (int i = 0; i < distances.length; i++) {
      double weight = distances[i];
      Instance inst = trainNeighbours.instance(i);
      sumOfWeights += inst.weight();
      newSumOfWeights += inst.weight() * weight;
      inst.setWeight(inst.weight() * weight);
    }

    // Rescale weights
    for (int i = 0; i < trainNeighbours.numInstances(); i++) {
      Instance inst = trainNeighbours.instance(i);
      inst.setWeight(inst.weight() * sumOfWeights / newSumOfWeights);
    }

    // Create a weighted classifier
    m_Classifier.buildClassifier(trainNeighbours);
  }

  /**
   * Calculates the class membership probabilities for the given test instance.
   *
   * @param instance the instance to be classified
   * @return predicted class probability distribution
   * @throws Exception if distribution can't be computed successfully
   */
  @Override
  public synchronized double[] distributionForInstance(Instance instance) throws Exception {
    // default model?
    if (m_ZeroR != null)
      return m_ZeroR.distributionForInstance(instance);

    if (m_Train.numInstances() == 0)
      throw new Exception("No training instances!");

    build(instance);

    m_FilterTrain.input(instance);
    m_FilterTrain.batchFinished();
    Instance trainInstance = m_FilterTrain.output();

    if (m_Debug) {
      System.out.println("Classifying test instance: " + instance);
      System.out.println("Classifying test instance (filtered for prediction): " + instance);
      System.out.println("Built base classifier:\n" + m_Classifier.toString());
    }

    return m_Classifier.distributionForInstance(trainInstance);
  }

  /**
   * Returns a description of this classifier.
   *
   * @return a description of this classifier as a string.
   */
  @Override
  public String toString() {
    StringBuilder	result;

    result = new StringBuilder(super.toString());
    if (m_Train != null) {
      if (result.indexOf("neighbours") > -1)
	result.append("\n# of training instances: " + m_Train.numInstances() + "\n");
    }

    return result.toString();
  }

  /**
   * Stops the execution.
   */
  @Override
  public void stopExecution() {
    m_Stopped = true;
    StoppableUtils.stopAnyExecution(m_Classifier);
  }

  /**
   * Whether the execution has been stopped.
   *
   * @return		true if stopped
   */
  @Override
  public boolean isStopped() {
    return m_Stopped;
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
   * Main method for testing this class.
   *
   * @param argv the options
   */
  public static void main(String [] argv) {
    runClassifier(new LWLSynchroSplit(), argv);
  }
}
