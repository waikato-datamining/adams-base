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
 * SubsetEnsemble.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package weka.classifiers.meta;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.RandomizableSingleClassifierEnhancer;
import weka.classifiers.rules.ZeroR;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.RevisionUtils;
import weka.core.SelectedTag;
import weka.core.Tag;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.MultiFilter;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.instance.RemoveInstancesWithMissingValue;
import JSci.maths.wavelet.IllegalScalingException;

/**
 <!-- globalinfo-start -->
 * Generates an ensemble using the following approach:<br>
 * - for each attribute apart from class attribute do:<br>
 *   * create new dataset with only this feature and the class attribute<br>
 *   * remove all instances that contain a missing value<br>
 *   * if no instances left in subset, don't build a classifier for this feature<br>
 *   * if at least 1 instance is left in subset, build base classifier with it<br>
 * If no classifier gets built at all, use ZeroR as backup model, built on the full dataset.<br>
 * In addition to the default feature for a subset, a number of random features can be added to the subset before the classifier is trained.<br>
 * At prediction time, the Vote meta-classifier (using the pre-built classifiers) is used to determing the class probabilities or regression value.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 *
 * <pre> -num-slots &lt;num&gt;
 *  Number of execution slots.
 *  (default: 1 - i.e. no parallelism)</pre>
 *
 * <pre> -combination-rule &lt;AVG|PROD|MAJ|MIN|MAX|MED&gt;
 *  The combination rule to use
 *  (default: AVG)</pre>
 *
 * <pre> -num-random &lt;num&gt;
 *  Number of random features to use in addition.
 *  (default: 0)</pre>
 *
 * <pre> -S &lt;num&gt;
 *  Random number seed.
 *  (default 1)</pre>
 *
 * <pre> -D
 *  If set, classifier is run in debug mode and
 *  may output additional info to the console</pre>
 *
 * <pre> -W
 *  Full name of base classifier.
 *  (default: weka.classifiers.rules.ZeroR)</pre>
 *
 * <pre>
 * Options specific to classifier weka.classifiers.rules.ZeroR:
 * </pre>
 *
 * <pre> -D
 *  If set, classifier is run in debug mode and
 *  may output additional info to the console</pre>
 *
 <!-- options-end -->
 *
 * Options after -- are passed to the designated classifier.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SubsetEnsemble
  extends RandomizableSingleClassifierEnhancer {

  /** for serialization. */
  private static final long serialVersionUID = -7637300579884789439L;

  /** the actual classifiers in use. */
  protected Classifier[] m_Classifiers;

  /** The number of threads to have executing at any one time */
  protected int m_NumExecutionSlots = 1;

  /** Combination Rule variable. */
  protected int m_CombinationRule = Vote.AVERAGE_RULE;

  /** the number of random features to use (in addition to base attribute). */
  protected int m_NumRandomFeatures = 0;

  /** Pool of threads to train models with */
  protected transient ThreadPoolExecutor m_ExecutorPool;

  /** The number of classifiers completed so far */
  protected int m_Completed;

  /** The number of classifiers that experienced a failure of some sort
   * during construction. */
  protected int m_Failed;

  /** For holding the original training set temporarily. */
  protected Instances m_Data;

  /** The header of the training set. */
  protected Instances m_Header;

  /** The backup classifier, in case no ensemble could be constructed at
   * prediction time. */
  protected ZeroR m_BackupModel;

  /**
   * Returns a string describing the classifier.
   *
   * @return 		a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return
        "Generates an ensemble using the following approach:\n"
      + "- for each attribute apart from class attribute do:\n"
      + "  * create new dataset with only this feature and the class attribute\n"
      + "  * remove all instances that contain a missing value\n"
      + "  * if no instances left in subset, don't build a classifier for this feature\n"
      + "  * if at least 1 instance is left in subset, build base classifier with it\n"
      + "If no classifier gets built at all, use ZeroR as backup model, built on the "
      + "full dataset.\n"
      + "In addition to the default feature for a subset, a number of random "
      + "features can be added to the subset before the classifier is trained.\n"
      + "At prediction time, the Vote meta-classifier (using the pre-built "
      + "classifiers) is used to determing the class probabilities or regression "
      + "value.";
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return 		an enumeration of all the available options.
   */
  public Enumeration listOptions() {
    Vector 	result;
    Enumeration	enm;

    result = new Vector();

    result.addElement(new Option(
	"\tNumber of execution slots.\n"
	+ "\t(default: 1 - i.e. no parallelism)",
	"num-slots", 1, "-num-slots <num>"));

    result.addElement(new Option(
	"\tThe combination rule to use\n"
	+ "\t(default: AVG)",
	"combination-rule", 1, "-combination-rule " + Tag.toOptionList(Vote.TAGS_RULES)));

    result.addElement(new Option(
	"\tNumber of random features to use in addition.\n"
	+ "\t(default: 0)",
	"num-random", 1, "-num-random <num>"));

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
   * <pre> -num-slots &lt;num&gt;
   *  Number of execution slots.
   *  (default: 1 - i.e. no parallelism)</pre>
   *
   * <pre> -combination-rule &lt;AVG|PROD|MAJ|MIN|MAX|MED&gt;
   *  The combination rule to use
   *  (default: AVG)</pre>
   *
   * <pre> -num-random &lt;num&gt;
   *  Number of random features to use in addition.
   *  (default: 0)</pre>
   *
   * <pre> -S &lt;num&gt;
   *  Random number seed.
   *  (default 1)</pre>
   *
   * <pre> -D
   *  If set, classifier is run in debug mode and
   *  may output additional info to the console</pre>
   *
   * <pre> -W
   *  Full name of base classifier.
   *  (default: weka.classifiers.rules.ZeroR)</pre>
   *
   * <pre>
   * Options specific to classifier weka.classifiers.rules.ZeroR:
   * </pre>
   *
   * <pre> -D
   *  If set, classifier is run in debug mode and
   *  may output additional info to the console</pre>
   *
   <!-- options-end -->
   *
   * Options after -- are passed to the designated classifier.<p>
   *
   * @param options 	the list of options as an array of strings
   * @throws Exception 	if an option is not supported
   */
  public void setOptions(String[] options) throws Exception {
    String 	tmpStr;

    tmpStr = Utils.getOption("num-slots", options);
    if (tmpStr.length() != 0)
      setNumExecutionSlots(Integer.parseInt(tmpStr));
    else
      setNumExecutionSlots(1);

    tmpStr = Utils.getOption("combination-rule", options);
    if (tmpStr.length() != 0)
      setCombinationRule(new SelectedTag(tmpStr, Vote.TAGS_RULES));
    else
      setCombinationRule(new SelectedTag(Vote.AVERAGE_RULE, Vote.TAGS_RULES));

    tmpStr = Utils.getOption("num-random", options);
    if (tmpStr.length() != 0)
      setNumRandomFeatures(Integer.parseInt(tmpStr));
    else
      setNumRandomFeatures(0);

    super.setOptions(options);
  }

  /**
   * Gets the current settings of the classifier.
   *
   * @return 		an array of strings suitable for passing to setOptions
   */
  public String [] getOptions() {
    Vector<String>	result;

    result = new Vector<String>();

    result.add("-num-slots");
    result.add("" + getNumExecutionSlots());

    result.add("-combination-rule");
    result.add("" + getCombinationRule());

    result.add("-num-random");
    result.add("" + getNumRandomFeatures());

    result.addAll(Arrays.asList(super.getOptions()));

    return result.toArray(new String[result.size()]);
  }

  /**
   * Set the number of execution slots (threads) to use for building the
   * members of the ensemble.
   *
   * @param value 	the number of slots to use.
   */
  public void setNumExecutionSlots(int value) {
    if (value >= 1)
      m_NumExecutionSlots = value;
    else
      System.err.println("Number of execution slots must be >= 1");
  }

  /**
   * Get the number of execution slots (threads) to use for building
   * the members of the ensemble.
   *
   * @return 		the number of slots to use
   */
  public int getNumExecutionSlots() {
    return m_NumExecutionSlots;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String numExecutionSlotsTipText() {
    return "The number of execution slots (threads) to use for constructing the ensemble.";
  }

  /**
   * Sets the combination rule to use. Values other than
   *
   * @param value 	the combination rule method to use
   */
  public void setCombinationRule(SelectedTag value) {
    if (value.getTags() == Vote.TAGS_RULES)
      m_CombinationRule = value.getSelectedTag().getID();
  }

  /**
   * Gets the combination rule used
   *
   * @return 		the combination rule used
   */
  public SelectedTag getCombinationRule() {
    return new SelectedTag(m_CombinationRule, Vote.TAGS_RULES);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String combinationRuleTipText() {
    return "The combination rule used.";
  }

  /**
   * Set the number of additional random features to use.
   *
   * @param value 	the number of random features
   */
  public void setNumRandomFeatures(int value) {
    if (value >= 0)
      m_NumRandomFeatures = value;
    else
      System.err.println("Number of additional random features must be >= 0");
  }

  /**
   * Returns the number of additional random features to use.
   *
   * @return 		the number of random features
   */
  public int getNumRandomFeatures() {
    return m_NumRandomFeatures;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String numRandomFeaturesTipText() {
    return "The number of additional random features to use.";
  }

  /**
   * Start the pool of execution threads.
   */
  protected void startExecutorPool() {
    if (m_ExecutorPool != null)
      m_ExecutorPool.shutdownNow();

    m_ExecutorPool = new ThreadPoolExecutor(
	m_NumExecutionSlots, m_NumExecutionSlots,
        120, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
  }

  /**
   * Blocks building classifiers until another slot becomes available.
   *
   * @param wait	whether to wait (block) or notify (unblock)
   */
  private synchronized void block(boolean wait) {
    if (wait) {
      try {
        wait();
      }
      catch (InterruptedException ex) {
	// ignored
      }
    }
    else {
      notifyAll();
    }
  }

  /**
   * Does the actual construction of the ensemble.
   *
   * @throws Exception 	if something goes wrong during the training process
   */
  protected synchronized void buildClassifiers() throws Exception {
    int		i;
    Runnable 	newTask;
    Random	rand;

    rand = new Random(m_Seed);
    for (i = 0; i < m_Classifiers.length; i++) {
      final int index = i;
      final int seed = rand.nextInt();
      if (m_Debug) {
	System.out.print("Training classifier (" + (i +1) + ")");
      }
      newTask = new Runnable() {
	public void run() {
	  try {
	    Instances train = getTrainingSet(index, seed);
	    // any training data left?
	    if (train.numInstances() > 0) {
	      FilteredClassifier fc = new FilteredClassifier();
	      fc.setFilter(getFilter(index, seed, false));
	      fc.setClassifier(m_Classifiers[index]);
	      fc.buildClassifier(m_Data);
	      m_Classifiers[index] = fc;
	    }
	    else {
	      m_Classifiers[index] = null;
	    }
	    completedClassifier(index, true);
	  }
	  catch (Exception ex) {
	    ex.printStackTrace();
	    completedClassifier(index, false);
	  }
	}
      };

      // launch this task
      m_ExecutorPool.execute(newTask);
    }

    if (m_Completed + m_Failed < m_Classifiers.length)
      block(true);
  }

  /**
   * Records the completion of the training of a single classifier. Unblocks if
   * all classifiers have been trained.
   *
   * @param index 	the index of the classifier that has completed
   * @param success 	whether the classifier trained successfully
   */
  protected synchronized void completedClassifier(int index, boolean success) {
    if (!success) {
      m_Failed++;
      if (m_Debug)
        System.err.println("Building of classifier " + index + " failed!");
    }
    else {
      m_Completed++;
    }

    if ((m_Completed + m_Failed) == m_Classifiers.length) {
      if (m_Failed > 0) {
        if (m_Debug)
          System.err.println("Problem building classifiers - some iterations failed.");
      }

      // have to shut the pool down or program executes as a server
      // and when running from the command line does not return to the
      // prompt
      m_ExecutorPool.shutdown();
      block(false);
      m_Data = null;
    }
  }

  /**
   * Returns the actual index in the data of the feature attribute.
   *
   * @param index 	the index for the requested attribute
   * @return 		the actual attribute index for the supplied index
   * @throws Exception 	if something goes wrong
   */
  protected int getActualIndex(int index) throws Exception {
    int		result;
    int		count;
    int		i;

    result = -1;
    count  = 0;
    for (i = 0; i < m_Header.numAttributes(); i++) {
      if (i == m_Header.classIndex())
	continue;

      if (count == index) {
	result = i;
	break;
      }

      count++;
    }

    if (result == -1)
      throw new IllegalScalingException("Actual attribute index for index " + index + " could not be determined!");

    return result;
  }

  /**
   * Gets a filter for a particular index.
   *
   * @param index 	the index for the requested filter
   * @param seed	the seed value to use for the determining the additional
   * 			random features
   * @param withMissing	whether to include the RemoveInstancesWithMissingValue filter
   * @return 		the filter for the supplied index
   * @throws Exception 	if something goes wrong
   */
  protected Filter getFilter(int index, int seed, boolean withMissing) throws Exception {
    Filter				result;
    Remove				remove;
    RemoveInstancesWithMissingValue	missing;
    int					actualIndex;
    HashSet<Integer>			features;
    int[]				indices;
    int					i;
    int					numRandomFeatures;
    Random				rand;

    actualIndex = getActualIndex(index);

    features = new HashSet<Integer>();
    features.add(actualIndex);
    features.add(m_Data.classIndex());

    if (m_NumRandomFeatures > 0) {
      numRandomFeatures = Math.min(m_NumRandomFeatures, m_Data.numAttributes() - 2);
      rand              = new Random(seed);
      while (features.size() < numRandomFeatures) {
	i = rand.nextInt(m_Data.numAttributes());
	features.add(i);
      }
    }

    indices = new int[features.size()];
    i       = 0;
    for (Integer idx: features) {
      indices[i] = idx;
      i++;
    }
    Arrays.sort(indices);

    remove = new Remove();
    remove.setAttributeIndicesArray(indices);
    remove.setInvertSelection(true);

    if (withMissing) {
      missing = new RemoveInstancesWithMissingValue();
      result  = new MultiFilter();
      ((MultiFilter) result).setFilters(new Filter[]{remove, missing});
    }
    else {
      result = remove;
    }

    return result;
  }

  /**
   * Gets a training set for a particular index.
   *
   * @param index 	the index for the requested training set
   * @param seed	the seed value to use for the determining the additional
   * 			random features
   * @return 		the training set for the supplied index
   * @throws Exception 	if something goes wrong
   */
  protected Instances getTrainingSet(int index, int seed) throws Exception {
    Instances		result;
    Filter		filter;

    filter = getFilter(index, seed, true);
    filter.setInputFormat(m_Data);
    result = Filter.useFilter(m_Data, filter);

    return result;
  }

  /**
   * Stump method for building the classifiers
   *
   * @param data 	the training data to be used for generating the ensemble
   * @throws Exception 	if the classifier could not be built successfully
   */
  public void buildClassifier(Instances data) throws Exception {
    // can classifier handle the data?
    getCapabilities().testWithFail(data);

    // remove instances with missing class
    m_Data = new Instances(data);
    m_Data.deleteWithMissingClass();
    m_Header = new Instances(m_Data, 0);

    if (m_Classifier == null)
      throw new Exception("A base classifier has not been specified!");
    m_Classifiers = AbstractClassifier.makeCopies(m_Classifier, m_Data.numAttributes() - 1);

    startExecutorPool();

    m_Completed = 0;
    m_Failed    = 0;

    // train backup model
    m_BackupModel = new ZeroR();
    m_BackupModel.buildClassifier(m_Data);

    buildClassifiers();
  }

  /**
   * Constructs the ensemble.
   *
   * @param instance	the instance to base the construction on
   */
  protected Classifier constructEnsemble(Instance instance) {
    Classifier		result;
    Vector<Classifier>	classifiers;
    int			i;
    int			count;

    classifiers = new Vector<Classifier>();
    count       = 0;
    for (i = 0; i < instance.numAttributes(); i++) {
      if (i == instance.classIndex())
	continue;
      // skip classifier if missing value
      if (instance.isMissing(i))
	continue;
      // skip un-initialized classifiers (due to no training data available)
      if (m_Classifiers[count] == null)
	continue;

      classifiers.add(m_Classifiers[count]);

      count++;
    }

    if (classifiers.size() > 1) {
      result = new Vote();
      ((Vote) result).setCombinationRule(getCombinationRule());
      ((Vote) result).setClassifiers(classifiers.toArray(new Classifier[classifiers.size()]));
    }
    else if (classifiers.size() == 1) {
      result = classifiers.get(0);
    }
    else {
      result = m_BackupModel;
    }

    return result;
  }

  /**
   * Classifies the given test instance. The instance has to belong to a
   * dataset when it's being classified.
   *
   * @param instance 	the instance to be classified
   * @return 		the predicted most likely class for the instance or
   * 			Utils.missingValue() if no prediction is made
   * @throws Exception 	if an error occurred during the prediction
   */
  public double classifyInstance(Instance instance) throws Exception {
    return constructEnsemble(instance).classifyInstance(instance);
  }

  /**
   * Predicts the class memberships for a given instance. If
   * an instance is unclassified, the returned array elements
   * must be all zero. If the class is numeric, the array
   * must consist of only one element, which contains the
   * predicted value.
   *
   * @param instance 	the instance to be classified
   * @return 		an array containing the estimated membership
   * 			probabilities of the test instance in each class
   * 			or the numeric prediction
   * @throws Exception 	if distribution could not be computed successfully
   */
  public double[] distributionForInstance(Instance instance) throws Exception {
    return constructEnsemble(instance).distributionForInstance(instance);
  }

  /**
   * Returns a string representation of the classifier.
   *
   * @return		the string representation
   */
  public String toString() {
    StringBuilder	result;
    int			i;
    int			actIndex;

    result = new StringBuilder();

    if (m_BackupModel == null) {
      result.append("No model built yet!");
    }
    else {
      result.append("--> Backup model\n");
      result.append(m_BackupModel.toString());
      result.append("\n");

      for (i = 0; i < m_Classifiers.length; i++) {
	try {
	  result.append("\n");
	  actIndex = getActualIndex(i);
	  result.append("--> Classifier #" + (i+1) + " (for attribute #" + (actIndex+1) + "):\n");
	  if (m_Classifiers[i] == null)
	    result.append("No model built - no useful data available");
	  else
	    result.append(m_Classifiers[i].toString());
	  result.append("\n");
	}
	catch (Exception e) {
	  result.append("Classifier #" + (i+1) + ": skipped due to error\n");
	}
      }
    }

    return result.toString();
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
   * Main method for running this class from commandline.
   *
   * @param args 	the options
   */
  public static void main(String[] args) {
    runClassifier(new SubsetEnsemble(), args);
  }
}
