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
 *    LWLSynchro.java
 *    Copyright (C) 1999-2015 University of Waikato, Hamilton, New Zealand
 *
 */

package weka.classifiers.lazy;

import adams.core.option.WekaCommandLineHandler;
import weka.classifiers.ThreadSafeClassifier;
import weka.classifiers.functions.GPD;
import weka.classifiers.functions.GaussianProcessesNoWeights;
import weka.core.*;
import weka.core.neighboursearch.FilteredNeighbourSearch;
import weka.core.neighboursearch.FilteredSearch;
import weka.core.neighboursearch.NearestNeighbourSearch;
import weka.filters.AllFilter;
import weka.filters.Filter;
import weka.filters.MultiFilter;
import weka.filters.unsupervised.attribute.AddID;
import weka.filters.unsupervised.attribute.RemoveByName;

import java.util.*;

/**
 <!-- globalinfo-start -->
 * Locally weighted learning. Uses an instance-based algorithm to assign instance weights which are then used by a specified WeightedInstancesHandler.<br>
 * Can do classification (e.g. using naive Bayes) or regression (e.g. using linear regression).<br>
 * <br>
 * For more info, see<br>
 * <br>
 * Eibe Frank, Mark Hall, Bernhard Pfahringer: Locally Weighted Naive Bayes. In: 19th Conference in Uncertainty in Artificial Intelligence, 249-256, 2003.<br>
 * <br>
 * C. Atkeson, A. Moore, S. Schaal (1996). Locally weighted learning. AI Review..
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
 * Valid options are: <br><br>
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
 * <pre> -D
 *  If set, classifier is run in debug mode and
 *  may output additional info to the console</pre>
 *
 * <pre> -W
 *  Full name of base classifier.
 *  (default: weka.classifiers.functions.GaussianProcesses)</pre>
 *
 * <pre>
 * Options specific to classifier weka.classifiers.functions.GaussianProcesses:
 * </pre>
 *
 * <pre> -D
 *  If set, classifier is run in debug mode and
 *  may output additional info to the console</pre>
 *
 * <pre> -L &lt;double&gt;
 *  Level of Gaussian Noise wrt transformed target. (default 1)</pre>
 *
 * <pre> -N
 *  Whether to 0=normalize/1=standardize/2=neither. (default 0=normalize)</pre>
 *
 * <pre> -K &lt;classname and parameters&gt;
 *  The Kernel to use.
 *  (default: weka.classifiers.functions.supportVector.PolyKernel)</pre>
 *
 * <pre>
 * Options specific to kernel weka.classifiers.functions.supportVector.PolyKernel:
 * </pre>
 *
 * <pre> -D
 *  Enables debugging output (if available) to be printed.
 *  (default: off)</pre>
 *
 * <pre> -no-checks
 *  Turns off all checks - use with caution!
 *  (default: checks on)</pre>
 *
 * <pre> -C &lt;num&gt;
 *  The size of the cache (a prime number), 0 for full cache and
 *  -1 to turn it off.
 *  (default: 250007)</pre>
 *
 * <pre> -E &lt;num&gt;
 *  The Exponent to use.
 *  (default: 1.0)</pre>
 *
 * <pre> -L
 *  Use lower-order terms.
 *  (default: no)</pre>
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
 * @version $Revision: 12986 $
 * @see LWL#distributionForInstance(Instance)
 */
public class LWLSynchroPrefilter
  extends LWL
  implements ThreadSafeClassifier {

  /** for serialization. */
  static final long serialVersionUID = 1979797405383665815L;

  /** whether to suppress the update of the nearest-neighbor search algorithm
   * when making predictions. */
  protected boolean m_NoUpdate;
  protected Filter m_PreFilter = new AllFilter();
  protected Instances m_filtered;
  protected Instances m_original;
  protected boolean m_run=false;
  protected AddID m_addId=new AddID();
  protected NearestNeighbourSearch m_ModifiedSearchMethod = null;
  protected int m_IndexOfID = -1;

  /**
   * Initializes the classifier.
   */
  public LWLSynchroPrefilter() {
    super();

    m_Classifier = new GPD();
  }




  protected void setUp(Instances data) throws Exception {
    if (m_run == true){
      return;
    }
    m_original=data;
    m_run=true;
    String attName="PF_addID";
    if(this.m_PreFilter == null) {
      throw new Exception("No prefilter!");
    } else {
      //this.getCapabilities().testWithFail(data);

      this.m_IndexOfID = 0;

      // prefilter & addif
      m_PreFilter.setInputFormat(data);
      m_filtered = Filter.useFilter(data, this.m_PreFilter);
      this.m_addId.setIDIndex("1");
      m_addId.setAttributeName(attName);
      m_addId.setInputFormat(data);
      Instances m_filtered_addid = Filter.useFilter(data, m_addId);

      // get NN search

      NearestNeighbourSearch nns=(NearestNeighbourSearch)(new SerializedObject(this.getNearestNeighbourSearchAlgorithm())).getObject();



      RemoveByName rbn=new RemoveByName();
      rbn.setExpression(attName);

      MultiFilter mf=new MultiFilter();

      if (nns instanceof FilteredSearch){
	FilteredSearch f=(FilteredSearch)nns;
	Filter fl=f.getFilter();
	mf.setFilters(new Filter[]{rbn,fl});
      } else {
	mf.setFilters(new Filter[]{rbn});
      }

      FilteredSearch fns=new FilteredSearch();
      fns.setFilter(mf);
      fns.setDistanceFunction(nns.getDistanceFunction());
      m_ModifiedSearchMethod=fns;

      this.m_ModifiedSearchMethod.getDistanceFunction().setAttributeIndices("1-last");
      this.m_ModifiedSearchMethod.getDistanceFunction().setInvertSelection(false);
      m_ModifiedSearchMethod.setInstances(m_filtered_addid);

      nns.getDistanceFunction().setAttributeIndices("1-last");


    }
  }

  /**
   * Default classifier classname.
   *
   * @return		the classname
   */
  @Override
  protected String defaultClassifierString() {
    return GaussianProcessesNoWeights.class.getName();
  }

  public String filterTipText() {
    return "The filter to be used.";
  }

  public void setFilter(Filter filter) {
    this.m_PreFilter = filter;
  }

  public Filter getFilter() {
    return this.m_PreFilter;
  }

  protected String getFilterSpec() {
    Filter c = this.getFilter();
    return c instanceof OptionHandler?c.getClass().getName() + " " + Utils.joinOptions(c.getOptions()):c.getClass().getName();
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

    result.addElement(
	new Option(
	    "\tSuppresses the update of the nearest neighbor search (nns)\n"
	    + "\talgorithm with the data that is to be classified.\n"
	    + "(default: nns gets updated).\n",
	    "no-update", 0, "-no-update"));

    result.addElement(
      new Option(
        "\tPre filter).\n",
        "pre-filter", 1, "-pre-filter <filterspec>"));

    enm = super.listOptions();
    while (enm.hasMoreElements())
      result.addElement(enm.nextElement());

    return result.elements();
  }

  /**
   * Parses a given list of options. <br><br>
   *
   <!-- options-start -->
   * Valid options are: <br><br>
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
   * <pre> -D
   *  If set, classifier is run in debug mode and
   *  may output additional info to the console</pre>
   *
   * <pre> -W
   *  Full name of base classifier.
   *  (default: weka.classifiers.functions.GaussianProcesses)</pre>
   *
   * <pre>
   * Options specific to classifier weka.classifiers.functions.GaussianProcesses:
   * </pre>
   *
   * <pre> -D
   *  If set, classifier is run in debug mode and
   *  may output additional info to the console</pre>
   *
   * <pre> -L &lt;double&gt;
   *  Level of Gaussian Noise wrt transformed target. (default 1)</pre>
   *
   * <pre> -N
   *  Whether to 0=normalize/1=standardize/2=neither. (default 0=normalize)</pre>
   *
   * <pre> -K &lt;classname and parameters&gt;
   *  The Kernel to use.
   *  (default: weka.classifiers.functions.supportVector.PolyKernel)</pre>
   *
   * <pre>
   * Options specific to kernel weka.classifiers.functions.supportVector.PolyKernel:
   * </pre>
   *
   * <pre> -D
   *  Enables debugging output (if available) to be printed.
   *  (default: off)</pre>
   *
   * <pre> -no-checks
   *  Turns off all checks - use with caution!
   *  (default: checks on)</pre>
   *
   * <pre> -C &lt;num&gt;
   *  The size of the cache (a prime number), 0 for full cache and
   *  -1 to turn it off.
   *  (default: 250007)</pre>
   *
   * <pre> -E &lt;num&gt;
   *  The Exponent to use.
   *  (default: 1.0)</pre>
   *
   * <pre> -L
   *  Use lower-order terms.
   *  (default: no)</pre>
   *
   <!-- options-end -->
   *
   * @param options the list of options as an array of strings
   * @throws Exception if an option is not supported
   */
  @Override
  public void setOptions(String[] options) throws Exception {
    String                  tmp;
    WekaCommandLineHandler  cmd;

    setNoUpdate(Utils.getFlag("no-update", options));

    tmp = Utils.getOption("pre-filter", options);
    if (tmp.isEmpty()) {
      setFilter(new AllFilter());
    }
    else {
      cmd = new WekaCommandLineHandler();
      setFilter((Filter) cmd.fromCommandLine(tmp));
    }

    super.setOptions(options);
  }

  /**
   * Gets the current settings of the classifier.
   *
   * @return an array of strings suitable for passing to setOptions
   */
  @Override
  public String[] getOptions() {
    List<String>            result;
    WekaCommandLineHandler  cmd;

    result = new ArrayList<>();

    if (getNoUpdate())
      result.add("-no-update");

    cmd = new WekaCommandLineHandler();
    result.add("-pre-filter");
    result.add(cmd.toCommandLine(getFilter()));

    result.addAll(Arrays.asList(super.getOptions()));

    return result.toArray(new String[result.size()]);
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
    setUp(m_Train);
    this.m_addId.input(instance);



    int k = m_Train.numInstances();
    if( (!m_UseAllK && (m_kNN < k)) /*&&
       !(m_WeightKernel==INVERSE ||
         m_WeightKernel==GAUSS)*/ ) {
      k = m_kNN;
    }

    Instance addedID=this.m_addId.output();
    Instances neighboursInFilteredSpace = this.m_ModifiedSearchMethod.kNearestNeighbours(addedID, k);
    Instances neighbours = new Instances(m_filtered, k);
    Iterator var5 = neighboursInFilteredSpace.iterator();

    while(var5.hasNext()) {
      Instance inst = (Instance)var5.next();
      neighbours.add(this.m_filtered.instance((int)inst.value(this.m_IndexOfID) - 1));
    }


    double distances[] = m_ModifiedSearchMethod.getDistances();

    if (m_Debug) {
      System.out.println("Test Instance: "+instance);
      System.out.println("For "+k+" kept " + neighbours.numInstances() + " out of " +
                         m_Train.numInstances() + " instances.");
    }

    //IF LinearNN has skipped so much that <k neighbours are remaining.
    if(k>distances.length)
      k = distances.length;

    if (m_Debug) {
      System.out.println("Instance Distances");
      for (int i = 0; i < distances.length; i++) {
	System.out.println("" + distances[i]);
      }
    }

    // Determine the bandwidth
    double bandwidth = distances[k-1];

    // Check for bandwidth zero
    if (bandwidth <= 0) {
      //if the kth distance is zero than give all instances the same weight
      for(int i=0; i < distances.length; i++)
        distances[i] = 1;
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
          //System.err.println("using constant kernel");
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
      for (int i = 0; i < distances.length; i++) {
	System.out.println("" + distances[i]);
      }
    }

    // Set the weights on the training data
    double sumOfWeights = 0, newSumOfWeights = 0;
    for (int i = 0; i < distances.length; i++) {
      double weight = distances[i];
      Instance inst = (Instance) neighbours.instance(i);
      sumOfWeights += inst.weight();
      newSumOfWeights += inst.weight() * weight;
      inst.setWeight(inst.weight() * weight);
      //weightedTrain.add(newInst);
    }

    // Rescale weights
    for (int i = 0; i < neighbours.numInstances(); i++) {
      Instance inst = neighbours.instance(i);
      inst.setWeight(inst.weight() * sumOfWeights / newSumOfWeights);
    }

    // Create a weighted classifier
    m_Classifier.buildClassifier(neighbours);
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
    build(instance);
    m_PreFilter.input(instance);

    if (m_ZeroR != null)
      return m_ZeroR.distributionForInstance(m_PreFilter.output());

    if (m_Train.numInstances() == 0)
      throw new Exception("No training instances!");



    if (m_Debug) {
      System.out.println("Classifying test instance: " + instance);
      System.out.println("Built base classifier:\n" + m_Classifier.toString());
    }

    return m_Classifier.distributionForInstance(m_PreFilter.output());
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
   * Returns the revision string.
   *
   * @return		the revision
   */
  @Override
  public String getRevision() {
    return RevisionUtils.extract("$Revision: 12986 $");
  }

  /**
   * Main method for testing this class.
   *
   * @param argv the options
   */
  public static void main(String [] argv) {
    runClassifier(new LWLSynchroPrefilter(), argv);
  }
}
