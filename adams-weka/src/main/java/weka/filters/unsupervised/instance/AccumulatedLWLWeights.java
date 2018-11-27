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
 * AccumulatedLWLWeights.java
 * Copyright (C) 2018 University of Waikato, Hamilton, New Zealand
 */

package weka.filters.unsupervised.instance;

import adams.core.ObjectCopyHelper;
import weka.classifiers.lazy.LWL;
import weka.classifiers.lazy.LWLDatasetBuilder;
import weka.classifiers.lazy.LWLDatasetBuilder.LWLContainer;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.Instances;
import weka.core.Option;
import weka.core.RevisionUtils;
import weka.core.Utils;
import weka.core.WeightedInstancesHandler;
import weka.core.neighboursearch.LinearNNSearch;
import weka.core.neighboursearch.NearestNeighbourSearch;
import weka.filters.SimpleBatchFilter;
import weka.filters.UnsupervisedFilter;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Vector;

/**
 <!-- globalinfo-start -->
 * Generates an LWL-like dataset for each instance of the data from the first batch and accumulate these weights. Once accumulated, the weights get normalized to be within the range of [0;1] and the output dataset accordingly adjusted.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p>
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
 * <pre> -no-update
 *  Suppresses the update of the nearest neighbor search (nns)
 *  algorithm with the data that is to be classified.
 * (default: nns gets updated).
 * </pre>
 *
 * <pre> -output-debug-info
 *  If set, filter is run in debug mode and
 *  may output additional info to the console</pre>
 *
 * <pre> -do-not-check-capabilities
 *  If set, filter capabilities are not checked before filter is built
 *  (use with caution).</pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class AccumulatedLWLWeights
  extends SimpleBatchFilter
  implements UnsupervisedFilter, WeightedInstancesHandler {

  /** for serialization. */
  private static final long serialVersionUID = -6784901276150528252L;

  /** The number of neighbours used to select the kernel bandwidth. */
  protected int m_kNN = 0;

  /** The weighting kernel method currently selected. */
  protected int m_WeightKernel = LWL.LINEAR;

  /** The nearest neighbour search algorithm to use.
   * (Default: weka.core.neighboursearch.LinearNNSearch)
   */
  protected NearestNeighbourSearch m_NNSearch =  new LinearNNSearch();

  /** whether to suppress the update of the nearest-neighbor search algorithm
   * when making predictions. */
  protected boolean m_NoUpdate;

  /**
   * Returns a string describing this classifier.
   *
   * @return      a description of the classifier suitable for
   *              displaying in the explorer/experimenter gui
   */
  public String globalInfo() {
    return
      "Generates an LWL-like dataset for each instance of the data from the "
	+ "first batch and accumulate these weights. Once accumulated, the "
	+ "weights get normalized to be within the range of [0;1] and the "
	+ "output dataset accordingly adjusted.";
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  public Enumeration<Option> listOptions() {
    Vector<Option> result = new Vector<>();

    result.addElement(new Option("\tThe nearest neighbour search " +
      "algorithm to use " +
      "(default: weka.core.neighboursearch.LinearNNSearch).\n",
      "A", 0, "-A"));

    result.addElement(new Option("\tSet the number of neighbours used to set"
      +" the kernel bandwidth.\n"
      +"\t(default all)",
      "K", 1, "-K <number of neighbours>"));

    result.addElement(new Option("\tSet the weighting kernel shape to use."
      +" 0=Linear, 1=Epanechnikov,\n"
      +"\t2=Tricube, 3=Inverse, 4=Gaussian.\n"
      +"\t(default 0 = Linear)",
      "U", 1,"-U <number of weighting method>"));

    result.addElement(
      new Option(
        "\tSuppresses the update of the nearest neighbor search (nns)\n"
          + "\talgorithm with the data that is to be classified.\n"
          + "(default: nns gets updated).\n",
        "no-update", 0, "-no-update"));

    result.addAll(Collections.list(super.listOptions()));

    return result.elements();
  }

  /**
   * Parses a given list of options.
   *
   *
   * @param options the list of options as an array of strings
   * @throws Exception if an option is not supported
   */
  public void setOptions(String[] options) throws Exception {
    String knnString = Utils.getOption('K', options);
    if (knnString.length() != 0) {
      setKNN(Integer.parseInt(knnString));
    } else {
      setKNN(0);
    }

    String weightString = Utils.getOption('U', options);
    if (weightString.length() != 0) {
      setWeightingKernel(Integer.parseInt(weightString));
    } else {
      setWeightingKernel(LWL.LINEAR);
    }

    String nnSearchClass = Utils.getOption('A', options);
    if(nnSearchClass.length() != 0) {
      String nnSearchClassSpec[] = Utils.splitOptions(nnSearchClass);
      if(nnSearchClassSpec.length == 0) {
        throw new Exception("Invalid NearestNeighbourSearch algorithm " +
                            "specification string.");
      }
      String className = nnSearchClassSpec[0];
      nnSearchClassSpec[0] = "";

      setNearestNeighbourSearchAlgorithm( (NearestNeighbourSearch)
                  Utils.forName( NearestNeighbourSearch.class,
                                 className,
                                 nnSearchClassSpec)
                                        );
    }
    else {
      this.setNearestNeighbourSearchAlgorithm(new LinearNNSearch());
    }

    setNoUpdate(Utils.getFlag("no-update", options));

    super.setOptions(options);
  }

  /**
   * Gets the current settings of the classifier.
   *
   * @return an array of strings suitable for passing to setOptions
   */
  public String [] getOptions() {
    Vector<String> options = new Vector<>();

    options.add("-U");
    options.add("" + getWeightingKernel());

    options.add("-K");
    options.add("" + getKNN());

    options.add("-A");
    options.add(m_NNSearch.getClass().getName()+" "+Utils.joinOptions(m_NNSearch.getOptions()));;

    if (getNoUpdate())
      options.add("-no-update");

    Collections.addAll(options, super.getOptions());

    return options.toArray(new String[0]);
  }

  /**
   * Returns the tip text for this property.
   * @return tip text for this property suitable for
   * displaying in the explorer/experimenter gui
   */
  public String KNNTipText() {
    return "How many neighbours are used to determine the width of the "
      + "weighting function (<= 0 means all neighbours).";
  }

  /**
   * Sets the number of neighbours used for kernel bandwidth setting.
   * The bandwidth is taken as the distance to the kth neighbour.
   *
   * @param knn the number of neighbours included inside the kernel
   * bandwidth, or 0 to specify using all neighbors.
   */
  public void setKNN(int knn) {
    if (knn < 0)
      knn = 0;
    m_kNN = knn;
  }

  /**
   * Gets the number of neighbours used for kernel bandwidth setting.
   * The bandwidth is taken as the distance to the kth neighbour.
   *
   * @return the number of neighbours included inside the kernel
   * bandwidth, or 0 for all neighbours
   */
  public int getKNN() {
    return m_kNN;
  }

  /**
   * Returns the tip text for this property.
   * @return tip text for this property suitable for
   * displaying in the explorer/experimenter gui
   */
  public String weightingKernelTipText() {
    return "Determines weighting function. [0 = Linear, 1 = Epnechnikov,"+
	   "2 = Tricube, 3 = Inverse, 4 = Gaussian and 5 = Constant. "+
	   "(default 0 = Linear)].";
  }

  /**
   * Sets the kernel weighting method to use. Must be one of LINEAR,
   * EPANECHNIKOV,  TRICUBE, INVERSE, GAUSS or CONSTANT, other values
   * are ignored.
   *
   * @param kernel the new kernel method to use. Must be one of LINEAR,
   * EPANECHNIKOV,  TRICUBE, INVERSE, GAUSS or CONSTANT.
   */
  public void setWeightingKernel(int kernel) {
    if ((kernel != LWL.LINEAR)
	&& (kernel != LWL.EPANECHNIKOV)
	&& (kernel != LWL.TRICUBE)
	&& (kernel != LWL.INVERSE)
	&& (kernel != LWL.GAUSS)
	&& (kernel != LWL.CONSTANT)) {
      return;
    }
    m_WeightKernel = kernel;
  }

  /**
   * Gets the kernel weighting method to use.
   *
   * @return the new kernel method to use. Will be one of LINEAR,
   * EPANECHNIKOV,  TRICUBE, INVERSE, GAUSS or CONSTANT.
   */
  public int getWeightingKernel() {
    return m_WeightKernel;
  }

  /**
   * Returns the tip text for this property.
   * @return tip text for this property suitable for
   * displaying in the explorer/experimenter gui
   */
  public String nearestNeighbourSearchAlgorithmTipText() {
    return "The nearest neighbour search algorithm to use (Default: LinearNN).";
  }

  /**
   * Returns the current nearestNeighbourSearch algorithm in use.
   * @return the NearestNeighbourSearch algorithm currently in use.
   */
  public NearestNeighbourSearch getNearestNeighbourSearchAlgorithm() {
    return m_NNSearch;
  }

  /**
   * Sets the nearestNeighbourSearch algorithm to be used for finding nearest
   * neighbour(s).
   * @param nearestNeighbourSearchAlgorithm - The NearestNeighbourSearch class.
   */
  public void setNearestNeighbourSearchAlgorithm(NearestNeighbourSearch nearestNeighbourSearchAlgorithm) {
    m_NNSearch = nearestNeighbourSearchAlgorithm;
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
   * Returns the Capabilities of this filter.
   *
   * @return            the capabilities of this object
   * @see               Capabilities
   */
  public Capabilities getCapabilities() {
    Capabilities 	result;

    result = new Capabilities(this);
    result.enableAll();
    result.enable(Capability.NO_CLASS);
    result.enable(Capability.MISSING_VALUES);
    result.enable(Capability.MISSING_CLASS_VALUES);

    result.setMinimumNumberInstances(0);

    return result;
  }

  /**
   * Determines the output format based on the input format and returns
   * this.
   *
   * @param inputFormat     the input format to base the output format on
   * @return                the output format
   * @throws Exception      in case the determination goes wrong
   */
  protected Instances determineOutputFormat(Instances inputFormat) throws Exception {
    return new Instances(inputFormat, 0);
  }

  /**
   * Processes the given data (may change the provided dataset) and returns
   * the modified version. This method is called in batchFinished().
   *
   * @param instances   the data to process
   * @return            the modified data
   * @throws Exception  in case the processing goes wrong
   */
  protected Instances process(Instances instances) throws Exception {
    Instances		result;
    double[]		weights;
    int			i;
    int			n;
    LWLDatasetBuilder	lwl;
    LWLContainer	cont;
    double		min;
    double		max;
    double		range;

    // only first batch will get processed
    if (m_FirstBatchDone)
      return new Instances(instances);

    lwl = new LWLDatasetBuilder();
    lwl.setTrain(instances);
    lwl.setKNN(m_kNN);
    lwl.setNoUpdate(m_NoUpdate);
    lwl.setWeightingKernel(m_WeightKernel);
    lwl.setSearchAlgorithm(ObjectCopyHelper.copyObject(m_NNSearch));
    weights = new double[instances.numInstances()];
    for (i = 0; i < instances.numInstances(); i++) {
      cont = lwl.build(instances.instance(i));
      for (n = 0; n < cont.originalIndices.length; n++)
        weights[cont.originalIndices[n]] += cont.dataset.instance(n).weight();
    }
    min = weights[Utils.minIndex(weights)];
    max = weights[Utils.maxIndex(weights)];
    range = max - min;
    if (range == 0) {
      System.err.println("Weights are all the same, not adjusting weights!");
      return new Instances(instances);
    }
    result = new Instances(instances);
    for (i = 0; i < weights.length; i++)
      result.instance(i).setWeight((weights[i] - min) / range);

    return result;
  }

  /**
   * Returns the revision string.
   *
   * @return		the revision
   */
  public String getRevision() {
    return RevisionUtils.extract("$Revision$");
  }

  /**
   * Main method for testing this class.
   *
   * @param args should contain arguments to the filter: use -h for help
   */
  public static void main(String [] args) {
    runFilter(new AccumulatedLWLWeights(), args);
  }
}
