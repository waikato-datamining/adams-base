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
 * VotedImbalance.java
 * Copyright (C) 2015-2016 University of Waikato, Hamilton, New Zealand
 */

package weka.classifiers.meta;

import adams.core.base.BaseKeyValuePair;
import adams.data.statistics.StatUtils;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.RandomizableSingleClassifierEnhancer;
import weka.classifiers.rules.ZeroR;
import weka.core.AttributeStats;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.ModelOutputHandler;
import weka.core.Option;
import weka.core.RevisionUtils;
import weka.core.SelectedTag;
import weka.core.Tag;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.supervised.instance.Resample;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 <!-- globalinfo-start -->
 * Generates an ensemble using the following approach:<br>
 * - do x times:<br>
 *   * create new dataset, resampled with specified bias<br>
 *   * build base classifier with it<br>
 * If no classifier gets built at all, use ZeroR as backup model, built on the full dataset.<br>
 * At prediction time, the Vote meta-classifier (using the pre-built classifiers) is used to determining the class probabilities or regression value.<br>
 * Instead of just using a fixed number of resampled models, you can also specify thresholds (= probability that the minority class does not meet) with associated number of resampled models to use.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p>
 *
 * <pre> -num-slots &lt;num&gt;
 *  Number of execution slots.
 *  (default: 1 - i.e. no parallelism)</pre>
 *
 * <pre> -combination-rule &lt;AVG|PROD|MAJ|MIN|MAX|MED&gt;
 *  The combination rule to use
 *  (default: AVG)</pre>
 *
 * <pre> -num-balanced &lt;num&gt;
 *  Number of balanced datasets (= number of classifiers) to create.
 *  (default: 1)</pre>
 *
 * <pre> -thresholds &lt;prob=# [prob=# [...]]&gt;
 *  Thresholds for number of resampled models (probability=#models); blank-separated list.
 *  (default: none)</pre>
 *
 * <pre> -num-balanced &lt;num&gt;
 *  Number of balanced datasets (= number of classifiers) to create.
 *  (default: 1)</pre>
 *
 * <pre> -B &lt;num&gt;
 *  Bias factor towards uniform class distribution.
 *  0 = distribution in input data -- 1 = uniform distribution.
 *  (default 0)</pre>
 *
 * <pre> -no-replacement
 *  Disables replacement of instances
 *  (default: with replacement)</pre>
 *
 * <pre> -suppress-model-output
 *  Suppress model output
 *  (default: no)</pre>
 *
 * <pre> -S &lt;num&gt;
 *  Random number seed.
 *  (default 1)</pre>
 *
 * <pre> -W
 *  Full name of base classifier.
 *  (default: weka.classifiers.rules.ZeroR)</pre>
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
 <!-- options-end -->
 *
 * Options after -- are passed to the designated classifier.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class VotedImbalance
  extends RandomizableSingleClassifierEnhancer
  implements ModelOutputHandler {

  /** for serialization. */
  private static final long serialVersionUID = -7637300579884789439L;

  /** the actual classifiers in use. */
  protected Classifier[] m_Classifiers;

  /** The number of threads to have executing at any one time */
  protected int m_NumExecutionSlots = 1;

  /** Combination Rule variable. */
  protected int m_CombinationRule = Vote.AVERAGE_RULE;

  /** the number of balanced datasets to generate. */
  protected int m_NumBalanced = 1;

  /** the thresholds to use (pair: probability minority class = num balanced). */
  protected BaseKeyValuePair[] m_Thresholds = new BaseKeyValuePair[0];

  /** the actual number of balanced datasets to generate. */
  protected int m_ActualNumBalanced;

  /** the bias for the dataset balancing (0 = distribution in input data -- 1 = uniform distribution). */
  protected double m_Bias = 0.0;

  /** Whether to perform sampling with replacement or without. */
  protected boolean m_NoReplacement = false;

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

  /** the vote classifier in use. */
  protected Classifier m_Ensemble;

  /** the sample percentage to use (0-100). */
  protected double m_SamplePercentage;

  /** whether to suppress the model output. */
  protected boolean m_SuppressModelOutput = false;

  /**
   * Returns a string describing the classifier.
   *
   * @return 		a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return
      "Generates an ensemble using the following approach:\n"
	+ "- do x times:\n"
	+ "  * create new dataset, resampled with specified bias\n"
	+ "  * build base classifier with it\n"
	+ "If no classifier gets built at all, use ZeroR as backup model, built on the "
	+ "full dataset.\n"
	+ "At prediction time, the Vote meta-classifier (using the pre-built "
	+ "classifiers) is used to determining the class probabilities or regression "
	+ "value.\n"
	+ "Instead of just using a fixed number of resampled models, you can also specify "
	+ "thresholds (= probability that the minority class does not meet) with "
	+ "associated number of resampled models to use.";
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
      "\tNumber of balanced datasets (= number of classifiers) to create.\n"
	+ "\t(default: 1)",
      "num-balanced", 1, "-num-balanced <num>"));

    result.addElement(new Option(
      "\tThresholds for number of resampled models (probability=#models); blank-separated list.\n"
	+ "\t(default: none)",
      "thresholds", 1, "-thresholds <prob=# [prob=# [...]]>"));

    result.addElement(new Option(
      "\tNumber of balanced datasets (= number of classifiers) to create.\n"
	+ "\t(default: 1)",
      "num-balanced", 1, "-num-balanced <num>"));

    result.addElement(new Option(
      "\tBias factor towards uniform class distribution.\n"
	+ "\t0 = distribution in input data -- 1 = uniform distribution.\n"
	+ "\t(default 0)",
      "B", 1, "-B <num>"));

    result.addElement(new Option(
      "\tDisables replacement of instances\n"
	+ "\t(default: with replacement)",
      "no-replacement", 0, "-no-replacement"));

    result.addElement(new Option(
      "\tSuppress model output\n"
	+ "\t(default: no)",
      "suppress-model-output", 0, "-suppress-model-output"));

    enm = super.listOptions();
    while (enm.hasMoreElements())
      result.addElement(enm.nextElement());

    return result.elements();
  }

  /**
   * Parses a given list of options.
   *
   * @param options 	the list of options as an array of strings
   * @throws Exception 	if an option is not supported
   */
  public void setOptions(String[] options) throws Exception {
    String 	tmpStr;

    tmpStr = Utils.getOption("num-slots", options);
    if (!tmpStr.isEmpty())
      setNumExecutionSlots(Integer.parseInt(tmpStr));
    else
      setNumExecutionSlots(1);

    tmpStr = Utils.getOption("combination-rule", options);
    if (!tmpStr.isEmpty())
      setCombinationRule(new SelectedTag(tmpStr, Vote.TAGS_RULES));
    else
      setCombinationRule(new SelectedTag(Vote.AVERAGE_RULE, Vote.TAGS_RULES));

    tmpStr = Utils.getOption("num-balanced", options);
    if (tmpStr.length() != 0)
      setNumBalanced(Integer.parseInt(tmpStr));
    else
      setNumBalanced(1);

    tmpStr = Utils.getOption("thresholds", options);
    if (!tmpStr.isEmpty())
      setThresholds(tmpStr);
    else
      setThresholds("");

    tmpStr = Utils.getOption('B', options);
    if (!tmpStr.isEmpty())
      setBias(Double.parseDouble(tmpStr));
    else
      setBias(0);

    setNoReplacement(Utils.getFlag("no-replacement", options));
    setSuppressModelOutput(Utils.getFlag("suppress-model-output", options));

    super.setOptions(options);
  }

  /**
   * Gets the current settings of the classifier.
   *
   * @return 		an array of strings suitable for passing to setOptions
   */
  public String [] getOptions() {
    Vector<String>	result;

    result = new Vector<>();

    result.add("-num-slots");
    result.add("" + getNumExecutionSlots());

    result.add("-combination-rule");
    result.add("" + getCombinationRule());

    result.add("-num-balanced");
    result.add("" + getNumBalanced());

    if (!getThresholds().isEmpty()) {
      result.add("-thresholds");
      result.add(getThresholds());
    }

    result.add("-B");
    result.add("" + getBias());

    if (getNoReplacement())
      result.add("-no-replacement");

    if (getSuppressModelOutput())
      result.add("-suppress-model-output");

    result.addAll(Arrays.asList(super.getOptions()));

    return result.toArray(new String[result.size()]);
  }

  @Override
  public void setClassifier(Classifier newClassifier) {
    Capabilities    cap;

    cap = newClassifier.getCapabilities();
    if (cap.handles(Capability.BINARY_CLASS))
      super.setClassifier(newClassifier);
    else
      System.err.println("Classifier must at least handle binary class!");
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
   * Set the number of balanced datasets to generated (= #classifiers).
   *
   * @param value 	the number of datasets
   */
  public void setNumBalanced(int value) {
    if (value >= 1)
      m_NumBalanced = value;
    else
      System.err.println("Number of datasets must be >= 1, provided: " + value);
  }

  /**
   * Returns the number of balanced datasets to generate (= #classifiers).
   *
   * @return 		the number of datasets
   */
  public int getNumBalanced() {
    return m_NumBalanced;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String numBalancedTipText() {
    return "The number of balanced datasets to generate (= #classifiers).";
  }

  /**
   * Set the pairs of threshold/number of resampled models.
   *
   * @param value 	the pairs (blank-separated list; probability=#models)
   */
  public void setThresholds(String value) {
    String[]			parts;
    List<BaseKeyValuePair>	pairs;
    BaseKeyValuePair		pair;
    int				i;

    if (value.trim().isEmpty()) {
      m_Thresholds = new BaseKeyValuePair[0];
      return;
    }

    try {
      pairs = new ArrayList<>();
      parts = Utils.splitOptions(value);
      for (i = 0; i < parts.length; i++) {
	pair = new BaseKeyValuePair();
	if (pair.isValid(parts[i])) {
	  pair.setValue(parts[i]);
	  if (!adams.core.Utils.isDouble(pair.getPairKey()) || (Double.parseDouble(pair.getPairKey()) < 0) || (Double.parseDouble(pair.getPairKey()) > 1)) {
	    System.err.println("Key #" + (i+1) + " is not a valid probability (0-1): " + pair.getPairKey());
	    continue;
	  }
	  if (!adams.core.Utils.isInteger(pair.getPairValue()) || (Integer.parseInt(pair.getPairValue()) < 1)) {
	    System.err.println("Value #" + (i+1) + " is not a valid model amount: " + pair.getPairValue());
	    continue;
	  }
	  pairs.add(pair);
	}
      }
      m_Thresholds = pairs.toArray(new BaseKeyValuePair[pairs.size()]);
    }
    catch (Exception e) {
      System.err.println("Invalid threshold specs: " + value);
    }
  }

  /**
   * Returns the pairs of threshold/number of resampled models.
   *
   * @return 		the pairs
   */
  public String getThresholds() {
    String[]	result;
    int		i;

    result = new String[m_Thresholds.length];
    for (i = 0; i < m_Thresholds.length; i++)
      result[i] = m_Thresholds[i].getValue();

    return Utils.joinOptions(result);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String thresholdsTipText() {
    return
      "The blank-separated list of probability thresholds for the minority "
	+ "class with their associated number of resampled models; e.g.: "
	+ "'0.5=1 0.3=3 0.1=5 0.05=10 0.01=25'.";
  }

  /**
   * Sets the bias towards a uniform class. A value of 0 leaves the class
   * distribution as-is, a value of 1 ensures the class distributions are
   * uniform in the output data.
   *
   * @param value the new bias value, between 0 and 1.
   */
  public void setBias(double value) {
    if ((value >= 0) && (value <= 1.0))
      m_Bias = value;
    else
      System.err.println("Bias must be 0 <= x <= 1, provided: " + value);
  }

  /**
   * Gets the bias towards a uniform class. A value of 0 leaves the class
   * distribution as-is, a value of 1 ensures the class distributions are
   * uniform in the output data.
   *
   * @return the current bias
   */
  public double getBias() {
    return m_Bias;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for displaying in the
   *         explorer/experimenter gui
   */
  public String biasTipText() {
    return "Whether to use bias towards a uniform class. A value of 0 leaves the class "
      + "distribution as-is, a value of 1 ensures the class distribution is "
      + "uniform in the output data.";
  }

  /**
   * Sets whether instances are drawn with or with out replacement.
   *
   * @param value if true then the replacement of instances is disabled
   */
  public void setNoReplacement(boolean value) {
    m_NoReplacement = value;
  }

  /**
   * Gets whether instances are drawn with or without replacement.
   *
   * @return true if the replacement is disabled
   */
  public boolean getNoReplacement() {
    return m_NoReplacement;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for displaying in the
   *         explorer/experimenter gui
   */
  public String noReplacementTipText() {
    return "Disables the replacement of instances.";
  }

  /**
   * Sets whether to output the model with the toString() method or not.
   *
   * @param value 	true if to suppress model output
   */
  public void setSuppressModelOutput(boolean value) {
    m_SuppressModelOutput = value;
  }

  /**
   * Returns whether to output the model with the toString() method or not.
   *
   * @return 		the label index
   */
  public boolean getSuppressModelOutput() {
    return m_SuppressModelOutput;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String suppressModelOutputTipText() {
    return "If enabled, suppresses any large model output.";
  }

  /**
   * Start the pool of execution threads.
   */
  protected void startExecutorPool() {
    if (m_ExecutorPool != null)
      m_ExecutorPool.shutdownNow();

    m_ExecutorPool = new ThreadPoolExecutor(
      m_NumExecutionSlots, m_NumExecutionSlots,
      120, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
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
   * Gets a filter for a particular index.
   *
   * @param index 	the index for the requested filter
   * @param seed	the seed value to use for the determining the additional
   * 			random features
   * @throws Exception 	if something goes wrong
   */
  protected Filter getFilter(int index, int seed) throws Exception {
    Resample resample;

    resample = new Resample();
    resample.setBiasToUniformClass(m_Bias);
    resample.setNoReplacement(m_NoReplacement);
    resample.setRandomSeed(seed);
    resample.setSampleSizePercent(m_SamplePercentage);

    return resample;
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

    filter = getFilter(index, seed);
    filter.setInputFormat(m_Data);
    result = Filter.useFilter(m_Data, filter);

    return result;
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
   * Does the actual construction of the ensemble.
   *
   * @throws Exception 	if something goes wrong during the training process
   */
  protected synchronized void buildClassifiers() throws Exception {
    int		i;
    Random	rand;

    rand = new Random(m_Seed);
    for (i = 0; i < m_Classifiers.length; i++) {
      final int index = i;
      final int seed = rand.nextInt();
      if (getDebug())
	System.out.print("Training classifier (" + (i +1) + ")");
      // launch this task
      m_ExecutorPool.execute(() -> {
	try {
	  Instances train = getTrainingSet(index, seed);
	  m_Classifiers[index].buildClassifier(train);
	  completedClassifier(index, true);
	}
	catch (Exception ex) {
	  System.err.println("Classifier #" + (index + 1) + " failed with:");
	  ex.printStackTrace();
	  completedClassifier(index, false);
	}
      });
    }

    if (m_Completed + m_Failed < m_Classifiers.length)
      block(true);
  }

  /**
   * Constructs the ensemble.
   */
  protected Classifier constructEnsemble() {
    Classifier		result;
    List<Classifier>    classifiers;

    classifiers = new ArrayList<>();
    for (Classifier cls: m_Classifiers) {
      if (cls == null)
	continue;
      classifiers.add(cls);
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
   * Returns default capabilities of the base classifier.
   *
   * @return      the capabilities of the base classifier
   */
  public Capabilities getCapabilities() {
    Capabilities        result;

    result = super.getCapabilities();
    result.disableAllClasses();

    // set dependencies
    for (Capability cap: Capability.values())
      result.enableDependency(cap);

    result.enable(Capability.NOMINAL_CLASS);
    result.enable(Capability.MISSING_CLASS_VALUES);

    result.setOwner(this);

    return result;
  }

  /**
   * Stump method for building the classifiers
   *
   * @param data 	the training data to be used for generating the ensemble
   * @throws Exception 	if the classifier could not be built successfully
   */
  public void buildClassifier(Instances data) throws Exception {
    int     		smallest;
    double		minorityClass;
    double		threshold;
    double		lastThreshold;
    AttributeStats	stats;
    double		total;
    int			i;

    // can classifier handle the data?
    getCapabilities().testWithFail(data);

    // remove instances with missing class
    m_Data = new Instances(data);
    m_Data.deleteWithMissingClass();
    m_Header = new Instances(m_Data, 0);

    m_ActualNumBalanced = m_NumBalanced;
    if (m_Thresholds.length > 0) {
      // determine minority class
      minorityClass = 1.0;
      stats         = data.attributeStats(data.classIndex());
      total         = StatUtils.sum(stats.nominalCounts);
      for (i = 0; i < stats.nominalCounts.length; i++) {
	if (stats.nominalCounts[i] == 0)
	  continue;
	minorityClass = Math.min((double) stats.nominalCounts[i] / total, minorityClass);
      }
      if (getDebug())
	System.out.println("Minority class probability: " + minorityClass);
      // determine threshold that applies
      lastThreshold = 1.0;
      for (i = 0; i < m_Thresholds.length; i++) {
	threshold = Double.parseDouble(m_Thresholds[i].getPairKey());
	if ((threshold > minorityClass) && (threshold < lastThreshold)) {
	  lastThreshold = threshold;
	  m_ActualNumBalanced = Integer.parseInt(m_Thresholds[i].getPairValue());
	}
      }
      if (getDebug())
	System.out.println("Actual # of resampled models: " + m_ActualNumBalanced);
    }

    if (data.numInstances() < m_ActualNumBalanced) {
      System.err.println(
	"WARNING: generating more balanced datasets than rows in input dataset "
	  + "(" + m_ActualNumBalanced + " > " + data.numInstances() + ")");
    }

    if (m_Classifier == null)
      throw new Exception("A base classifier has not been specified!");
    m_Classifiers = AbstractClassifier.makeCopies(m_Classifier, m_ActualNumBalanced);

    startExecutorPool();

    m_Completed = 0;
    m_Failed    = 0;

    // calculate sample percentage
    smallest = StatUtils.min(data.attributeStats(data.classIndex()).nominalCounts);
    m_SamplePercentage = 100.0 / data.numInstances() * (smallest * data.classAttribute().numValues());
    if (getDebug())
      System.out.println("Sample percentage: " + m_SamplePercentage);

    // train backup model
    m_BackupModel = new ZeroR();
    m_BackupModel.buildClassifier(m_Data);

    buildClassifiers();
    m_Ensemble = constructEnsemble();

    // clean up
    m_Classifiers = null;
    m_BackupModel = null;
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
    return m_Ensemble.classifyInstance(instance);
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
    return m_Ensemble.distributionForInstance(instance);
  }

  /**
   * Returns a string representation of the classifier.
   *
   * @return		the string representation
   */
  public String toString() {
    StringBuilder	result;

    result = new StringBuilder();

    if (m_Ensemble == null) {
      result.append("No model built yet!");
    }
    else {
      if (!m_SuppressModelOutput) {
	result.append("--> Model\n");
	result.append(m_Ensemble.toString());
	result.append("\n");
      }
      else {
	result.append("Model suppressed\n");
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
    runClassifier(new VotedImbalance(), args);
  }
}
