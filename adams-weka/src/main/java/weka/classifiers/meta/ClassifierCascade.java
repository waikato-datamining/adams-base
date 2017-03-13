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
 * ClassifierCascade.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package weka.classifiers.meta;

import adams.core.option.OptionUtils;
import adams.data.statistics.StatUtils;
import adams.env.Environment;
import adams.flow.container.WekaTrainTestSetContainer;
import adams.flow.core.EvaluationHelper;
import adams.flow.core.EvaluationStatistic;
import adams.multiprocess.WekaCrossValidationExecution;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.RandomSplitGenerator;
import weka.classifiers.RandomizableMultipleClassifiersCombiner;
import weka.classifiers.evaluation.NominalPrediction;
import weka.classifiers.evaluation.Prediction;
import weka.core.Attribute;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;
import weka.core.WekaOptionUtils;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 <!-- globalinfo-start -->
 * Generates a classifier cascade, with each deeper level of classifiers being built on the input data and either the class distributions (nominal class) or classification (numeric class) of the classifiers of the previous level in the cascade.<br>
 * The build process is stopped when either the maximum number of levels is reached, the termination criterion is satisfied or no further improvement is achieved.<br>
 * In case of a level performing worse than the prior one, the build process is terminated immediately and the current level discarded.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p>
 * 
 * <pre> -max-levels &lt;value&gt;
 *  The maximum number of levels to build.
 *  (default: 10)</pre>
 * 
 * <pre> -statistic &lt;value&gt;
 *  The statistic to evaluate on.
 *  (default: Percent correct)</pre>
 * 
 * <pre> -threshold &lt;value&gt;
 *  The threshold that, when reached, terminates the build process.
 *  (default: 90.0)</pre>
 * 
 * <pre> -threshold-check &lt;value&gt;
 *  How to apply the provided threshold.
 *  (default: ABOVE)</pre>
 * 
 * <pre> -min-improvement &lt;value&gt;
 *  The minimum improvement between levels, otherwise the build process gets terminated.
 *  (default: 0.01)</pre>
 * 
 * <pre> -num-folds &lt;value&gt;
 *  The number of folds to use for internal cross-validation.
 *  (default: 10)</pre>
 * 
 * <pre> -num-threads &lt;value&gt;
 *  The number of threads to use.
 *  (default: -1)</pre>
 * 
 * <pre> -holdout-percentage &lt;value&gt;
 *  The size of the validation set in percent (0-100).
 *  (default: 20.0)</pre>
 * 
 * <pre> -class-index &lt;value&gt;
 *  The 0-based index of the class-label to use for class-label-based statistics.
 *  (default: 0)</pre>
 * 
 * <pre> -combination &lt;value&gt;
 *  Determines how to combine the statistics.
 *  (default: MEDIAN)</pre>
 * 
 * <pre> -S &lt;num&gt;
 *  Random number seed.
 *  (default 1)</pre>
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
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ClassifierCascade
  extends RandomizableMultipleClassifiersCombiner {

  private static final long serialVersionUID = 8324353885319971960L;

  /** the prefix for the additional cascade attributes. */
  public static final String ATTRIBUTE_PREFIX = "Cascade-";

  public static final int DEFAULT_MAX_LEVELS = 10;

  public static final EvaluationStatistic DEFAULT_STATISTIC = EvaluationStatistic.PERCENT_CORRECT;

  public static final double DEFAULT_THRESHOLD = 90.0;

  public static final ThresholdCheck DEFAULT_THRESHOLD_CHECK = ThresholdCheck.ABOVE;

  public static final double DEFAULT_MIN_IMPROVEMENT = 0.01;

  public static final int DEFAULT_NUM_FOLDS = 10;

  public static final int DEFAULT_NUM_THREADS = -1;

  public static final double DEFAULT_HOLDOUT_PERCENTAGE = 20.0;

  public static final int DEFAULT_CLASS_INDEX = 0;

  public static final Combination DEFAULT_COMBINATION = Combination.MEDIAN;

  /**
   * Defines how to combine the predictions of the final layer and turn it into
   * actual predictions.
   */
  public enum Combination {
    /** average the probabilities/classifications. */
    AVERAGE,
    /** use the median of the probabilities/classifications. */
    MEDIAN,;
  }

  /**
   * Defines how to check the threshold.
   */
  public enum ThresholdCheck {
    BELOW,
    ABOVE,;
  }

  protected static String MAX_LEVELS = "max-levels";

  protected static String STATISTIC = "statistic";

  protected static String THRESHOLD = "threshold";

  protected static String THRESHOLD_CHECK = "threshold-check";

  protected static String MIN_IMPROVEMENT = "min-improvement";

  protected static String NUM_FOLDS = "num-folds";

  protected static String NUM_THREADS = "num-threads";

  protected static String HOLDOUT_PERCENTAGE = "holdout-percentage";

  protected static String CLASS_INDEX = "class-index";

  protected static String COMBINATION = "combination";

  /** the maximum number of levels in the cascade. */
  protected int m_MaxLevels = DEFAULT_MAX_LEVELS;

  /** the statistic to use for termination. */
  protected EvaluationStatistic m_Statistic = DEFAULT_STATISTIC;

  /** the threshold for the statistic for termination. */
  protected double m_Threshold = DEFAULT_THRESHOLD;

  /** whether to go below or above the threshold. */
  protected ThresholdCheck m_ThresholdCheck = DEFAULT_THRESHOLD_CHECK;

  /** the minimum improvement between levels that the statistic must improve. */
  protected double m_MinImprovement = DEFAULT_MIN_IMPROVEMENT;

  /** the number of folds for cross-validation. */
  protected int m_NumFolds = DEFAULT_NUM_FOLDS;

  /** the number of threads to use. */
  protected int m_NumThreads = DEFAULT_NUM_THREADS;

  /** the percentage to use for validation set to determine termination criterion (0-100). */
  protected double m_HoldOutPercentage = DEFAULT_HOLDOUT_PERCENTAGE;

  /** the class index. */
  protected int m_ClassIndex = DEFAULT_CLASS_INDEX;

  /** how to combine the statistics. */
  protected Combination m_Combination = DEFAULT_COMBINATION;

  // TODO
  // - (optionally) weight classifier output, using an evaluation statistic to determine weights
  // - (optionally) build classifier on final predictions (class probs or classification) to make predictions similar to SMO

  /** the cascade. */
  protected List<List<Classifier>> m_Cascade = null;

  /** the meta-level structure. */
  protected Instances m_MetaLevelHeader = null;

  /** the start indices for the classifier stats in the meta-levels. */
  protected List<Integer> m_MetaLevelStart = null;

  /** whether regression or classification. */
  protected boolean m_Nominal;

  /**
   * Returns a string describing classifier.
   *
   * @return 		a description suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String globalInfo() {
    return
      "Generates a classifier cascade, with each deeper level of classifiers "
	+ "being built on the input data and either the class distributions "
	+ "(nominal class) or classification (numeric class) of the classifiers "
	+ "of the previous level in the cascade.\n"
	+ "The build process is stopped when either the maximum number of levels "
	+ "is reached, the termination criterion is satisfied or no further "
	+ "improvement is achieved.\n"
	+ "In case of a level performing worse than the prior one, the build "
	+ "process is terminated immediately and the current level discarded.";
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return 		an enumeration of all the available options.
   */
  public Enumeration listOptions() {
    Vector result;

    result = new Vector();

    WekaOptionUtils.addOption(result, maxLevelsTipText(), "" + getMaxLevels(), MAX_LEVELS);
    WekaOptionUtils.addOption(result, statisticTipText(), "" + getStatistic(), STATISTIC);
    WekaOptionUtils.addOption(result, thresholdTipText(), "" + getThreshold(), THRESHOLD);
    WekaOptionUtils.addOption(result, thresholdCheckTipText(), "" + getThresholdCheck(), THRESHOLD_CHECK);
    WekaOptionUtils.addOption(result, minImprovementTipText(), "" + getMinImprovement(), MIN_IMPROVEMENT);
    WekaOptionUtils.addOption(result, numFoldsTipText(), "" + getNumFolds(), NUM_FOLDS);
    WekaOptionUtils.addOption(result, numThreadsTipText(), "" + getNumThreads(), NUM_THREADS);
    WekaOptionUtils.addOption(result, holdOutPercentageTipText(), "" + getHoldOutPercentage(), HOLDOUT_PERCENTAGE);
    WekaOptionUtils.addOption(result, classIndexTipText(), "" + getClassIndex(), CLASS_INDEX);
    WekaOptionUtils.addOption(result, combinationTipText(), "" + getCombination(), COMBINATION);
    WekaOptionUtils.add(result, super.listOptions());
    return WekaOptionUtils.toEnumeration(result);
  }

  /**
   * Parses a given list of options.
   *
   * @param options 	the list of options as an array of strings
   * @throws Exception 	if an option is not supported
   */
  public void setOptions(String[] options) throws Exception {
    setMaxLevels(WekaOptionUtils.parse(options, MAX_LEVELS, DEFAULT_MAX_LEVELS));
    setStatistic((EvaluationStatistic) WekaOptionUtils.parse(options, STATISTIC, DEFAULT_STATISTIC));
    setThreshold(WekaOptionUtils.parse(options, THRESHOLD, DEFAULT_THRESHOLD));
    setThresholdCheck((ThresholdCheck) WekaOptionUtils.parse(options, THRESHOLD_CHECK, DEFAULT_THRESHOLD_CHECK));
    setMinImprovement(WekaOptionUtils.parse(options, MIN_IMPROVEMENT, DEFAULT_MIN_IMPROVEMENT));
    setNumFolds(WekaOptionUtils.parse(options, NUM_FOLDS, DEFAULT_NUM_FOLDS));
    setNumThreads(WekaOptionUtils.parse(options, NUM_THREADS, DEFAULT_NUM_THREADS));
    setHoldOutPercentage(WekaOptionUtils.parse(options, HOLDOUT_PERCENTAGE, DEFAULT_HOLDOUT_PERCENTAGE));
    setClassIndex(WekaOptionUtils.parse(options, CLASS_INDEX, DEFAULT_CLASS_INDEX));
    setCombination((Combination) WekaOptionUtils.parse(options, COMBINATION, DEFAULT_COMBINATION));
    super.setOptions(options);
  }

  /**
   * Gets the current settings of the classifier.
   *
   * @return 		an array of strings suitable for passing to setOptions
   */
  public String [] getOptions() {
    List<String> result = new ArrayList<>();
    WekaOptionUtils.add(result, MAX_LEVELS, getMaxLevels());
    WekaOptionUtils.add(result, STATISTIC, getStatistic());
    WekaOptionUtils.add(result, THRESHOLD, getThreshold());
    WekaOptionUtils.add(result, THRESHOLD_CHECK, getThresholdCheck());
    WekaOptionUtils.add(result, MIN_IMPROVEMENT, getMinImprovement());
    WekaOptionUtils.add(result, NUM_FOLDS, getNumFolds());
    WekaOptionUtils.add(result, NUM_THREADS, getNumThreads());
    WekaOptionUtils.add(result, HOLDOUT_PERCENTAGE, getHoldOutPercentage());
    WekaOptionUtils.add(result, CLASS_INDEX, getClassIndex());
    WekaOptionUtils.add(result, COMBINATION, getCombination());
    WekaOptionUtils.add(result, super.getOptions());
    return WekaOptionUtils.toArray(result);
  }

  /** the maximum number of levels in the cascade. */
  public void setMaxLevels(int maxLevels) {
    m_MaxLevels = maxLevels;
  }

  /** the maximum number of levels in the cascade. */
  public int getMaxLevels() {
    return m_MaxLevels;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String maxLevelsTipText() {
    return "The maximum number of levels to build.";
  }

  /** the statistic to use for termination. */
  public void setStatistic(EvaluationStatistic statistic) {
    m_Statistic = statistic;
  }

  /** the statistic to use for termination. */
  public EvaluationStatistic getStatistic() {
    return m_Statistic;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String statisticTipText() {
    return "The statistic to evaluate on.";
  }

  /** the threshold for the statistic for termination. */
  public void setThreshold(double threshold) {
    m_Threshold = threshold;
  }

  /** the threshold for the statistic for termination. */
  public double getThreshold() {
    return m_Threshold;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String thresholdTipText() {
    return "The threshold that, when reached, terminates the build process.";
  }

  /** whether to go below or above the threshold. */
  public void setThresholdCheck(ThresholdCheck thresholdCheck) {
    m_ThresholdCheck = thresholdCheck;
  }

  /** whether to go below or above the threshold. */
  public ThresholdCheck getThresholdCheck() {
    return m_ThresholdCheck;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String thresholdCheckTipText() {
    return "How to apply the provided threshold.";
  }

  /** the minimum improvement between levels that the statistic must improve. */
  public void setMinImprovement(double minImprovement) {
    m_MinImprovement = minImprovement;
  }

  /** the minimum improvement between levels that the statistic must improve. */
  public double getMinImprovement() {
    return m_MinImprovement;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String minImprovementTipText() {
    return "The minimum improvement between levels, otherwise the build process gets terminated.";
  }

  /** the number of folds for cross-validation. */
  public void setNumFolds(int numFolds) {
    m_NumFolds = numFolds;
  }

  /** the number of folds for cross-validation. */
  public int getNumFolds() {
    return m_NumFolds;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String numFoldsTipText() {
    return "The number of folds to use for internal cross-validation.";
  }

  /** the number of threads to use. */
  public void setNumThreads(int numThreads) {
    m_NumThreads = numThreads;
  }

  /** the number of threads to use. */
  public int getNumThreads() {
    return m_NumThreads;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String numThreadsTipText() {
    return "The number of threads to use.";
  }

  /** the percentage to use for validation set to determine termination criterion (0-100). */
  public void setHoldOutPercentage(double holdOutPercentage) {
    m_HoldOutPercentage = holdOutPercentage;
  }

  /** the percentage to use for validation set to determine termination criterion (0-100). */
  public double getHoldOutPercentage() {
    return m_HoldOutPercentage;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String holdOutPercentageTipText() {
    return "The size of the validation set in percent (0-100).";
  }

  /** the class index. */
  public void setClassIndex(int classIndex) {
    m_ClassIndex = classIndex;
  }

  /** the class index. */
  public int getClassIndex() {
    return m_ClassIndex;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String classIndexTipText() {
    return "The 0-based index of the class-label to use for class-label-based statistics.";
  }

  /** how to combine the statistics. */
  public void setCombination(Combination combination) {
    m_Combination = combination;
  }

  /** how to combine the statistics. */
  public Combination getCombination() {
    return m_Combination;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String combinationTipText() {
    return "Determines how to combine the statistics.";
  }

  /**
   * Returns combined capabilities of the base classifiers, i.e., the
   * capabilities all of them have in common.
   *
   * @return      the capabilities of the base classifiers
   */
  @Override
  public Capabilities getCapabilities() {
    Capabilities 	result;
    Capabilities	other;
    int			i;

    if (m_Classifiers.length == 0)
      return new Capabilities(this);

    result = m_Classifiers[0].getCapabilities();
    for (i = 1; i < m_Classifiers.length; i++) {
      other = m_Classifiers[i].getCapabilities();
      result.and(other);
      if (other.getMinimumNumberInstances() > result.getMinimumNumberInstances())
	result.setMinimumNumberInstances(other.getMinimumNumberInstances());
    }
    result.enable(Capability.MISSING_CLASS_VALUES);

    return result;
  }

  /**
   * Generates the dataset structure for the meta-levels.
   *
   * @param data	the training data
   * @return		the structure
   */
  protected Instances createMetaLevelHeader(Instances data) {
    Instances			result;
    ArrayList<Attribute>	atts;
    int				classIndex;
    int				i;
    int				n;
    List<Integer>		start;

    // assemble attributes
    start      = new ArrayList<>();
    atts       = new ArrayList<>();
    classIndex = data.classIndex();
    for (i = 0; i < data.numAttributes(); i++) {
      if (i == classIndex)
	continue;
      atts.add((Attribute) data.attribute(i).copy());
    }
    for (i = 0; i < m_Classifiers.length; i++) {
      start.add(atts.size());
      if (data.classAttribute().isNominal()) {
	for (n = 0; n < data.classAttribute().numValues(); n++)
	  atts.add(new Attribute(ATTRIBUTE_PREFIX + (i+1) + "-" + (n+1)));
      }
      else {
	atts.add(new Attribute(ATTRIBUTE_PREFIX + (i+1)));
      }
    }
    atts.add((Attribute) data.classAttribute().copy());

    result = new Instances(ATTRIBUTE_PREFIX + data.relationName(), atts, 0);
    result.setClassIndex(result.numAttributes() - 1);

    if (m_MetaLevelStart == null)
      m_MetaLevelStart = start;

    return result;
  }

  /**
   * Generates an instance for the meta-level using the original data.
   *
   * @param data	the original data
   * @return		the meta-level instance, but with missing meta-level data
   */
  protected Instance createMetaLevelInstance(Instances metaLevel, Instance data) {
    Instance		result;
    double[]		values;
    int			i;
    int			n;
    int			classIndex;
    int			index;

    classIndex = data.classIndex();
    values     = new double[metaLevel.numAttributes()];
    index      = 0;
    for (i = 0; i < data.numAttributes(); i++) {
      if (i == classIndex)
	continue;
      switch (data.attribute(i).type()) {
	case Attribute.NUMERIC:
	case Attribute.DATE:
	case Attribute.NOMINAL:
	  values[index] = data.value(i);
	  break;
	case Attribute.STRING:
	  values[index] = metaLevel.attribute(index).addStringValue(data.stringValue(i));
	  break;
	case Attribute.RELATIONAL:
	  values[index] = metaLevel.attribute(index).addRelation(data.relationalValue(i));
	  break;
	default:
	  throw new IllegalStateException(
	    "Unhandled attribute type at #" + (i+1) + ": " + Attribute.typeToString(data.attribute(i).type()));
      }
      index++;
    }
    for (i = 0; i < m_Classifiers.length; i++) {
      if (data.classAttribute().isNominal()) {
	for (n = 0; n < data.classAttribute().numValues(); n++) {
	  values[index] = Utils.missingValue();
	  index++;
	}
      }
      else {
	values[index] = Utils.missingValue();
	index++;
      }
    }
    values[values.length - 1] = data.classValue();

    result = new DenseInstance(data.weight(), values);
    result.setDataset(metaLevel);

    return result;
  }

  /**
   * Adds the class distribution of the specified classifier to the meta-level instance.
   *
   * @param inst	the meta-level instance to modify
   * @param index 	the index of the classifier
   * @param dist 	the class distribution to add
   */
  protected void addMetaLevelPrediction(Instance inst, int index, double[] dist) {
    int		i;

    for (i = 0; i < dist.length; i++) {
      if (Double.isNaN(dist[i]))
	throw new IllegalStateException("NaN in class distribution of classifier " + (index+1) + " at #" + (i+1));
      inst.setValue(m_MetaLevelStart.get(index) + i, dist[i]);
    }
  }

  /**
   * Adds the class distribution of the specified classifier to the meta-level instance.
   *
   * @param inst	the meta-level instance to modify
   * @param index 	the index of the classifier
   * @param cls		the classification
   */
  protected void addMetaLevelPrediction(Instance inst, int index, double cls) {
    if (Double.isNaN(cls))
      throw new IllegalStateException("NaN in classification of classifier " + (index+1) + "!");
    inst.setValue(m_MetaLevelStart.get(index), cls);
  }

  /**
   * Applies the selected combination to the array.
   *
   * @param stats	the statistic values to combine
   * @return		the combination
   */
  protected double applyCombination(double[] stats) {
    int		i;

    // check for NaNs
    for (i = 0; i < stats.length; i++) {
      if (Double.isNaN(stats[i]))
	throw new IllegalStateException("NaN in statistics at #" + (i+1) + "!");
    }

    switch (m_Combination) {
      case AVERAGE:
	return StatUtils.mean(stats);
      case MEDIAN:
	return StatUtils.median(stats);
      default:
	throw new IllegalStateException("Unhandled combination: " + m_Combination);
    }
  }

  /**
   * Builds the classifier.
   *
   * @param data	the training data
   * @throws Exception	if build fails
   */
  @Override
  public void buildClassifier(Instances data) throws Exception {
    Instances				train;
    Instances				test;
    Instances 				priorTrain;
    Instances 				priorTest;
    Instances 				metaTrain;
    Instances 				metaTest;
    RandomSplitGenerator		rand;
    WekaTrainTestSetContainer		cont;
    int					level;
    Evaluation				eval;
    double[]				stats;
    double				stat;
    double				priorStat;
    int					i;
    int					n;
    Classifier				cls;
    WekaCrossValidationExecution	cv;
    String				msg;
    int[]				indices;
    ArrayList<Prediction>		preds;
    Map<Integer,Instance>		unordered;
    Instance				inst;
    List<Classifier>			current;
    boolean				converged;
    boolean				improved;

    getCapabilities().testWithFail(data);

    data = new Instances(data);
    data.deleteWithMissingClass();

    // data structure
    m_MetaLevelHeader = null;

    // train/test
    rand      = new RandomSplitGenerator(data, m_Seed, (100.0 - m_HoldOutPercentage) / 100.0);
    cont      = rand.next();
    train     = (Instances) cont.getValue(WekaTrainTestSetContainer.VALUE_TRAIN);
    test      = (Instances) cont.getValue(WekaTrainTestSetContainer.VALUE_TEST);
    metaTrain = null;
    metaTest  = null;
    stat      = Double.NaN;
    m_Nominal = data.classAttribute().isNominal();

    m_Cascade         = new ArrayList<>();
    m_MetaLevelHeader = createMetaLevelHeader(train);
    for (level = 0; level < m_MaxLevels; level++) {
      m_Cascade.add(new ArrayList<>());
      if (getDebug())
	System.out.println("Level " + (level+1) + "...");

      // generate meta-level data
      priorStat  = stat;
      priorTrain = metaTrain;
      priorTest  = metaTest;
      metaTrain  = new Instances(m_MetaLevelHeader, train.numInstances());
      for (i = 0; i < m_Classifiers.length; i++) {
	if (getDebug())
	  System.out.println("- Classifier " + (i+1) + "...");

	// cross-validate classifier
	cv = new WekaCrossValidationExecution();
	cv.setClassifier((Classifier) OptionUtils.shallowCopy(m_Classifiers[i]));
	cv.setNumThreads(m_NumThreads);
	cv.setDiscardPredictions(false);
	cv.setFolds(m_NumFolds);
	cv.setSeed(m_Seed);
	cv.setData(train);
	msg = cv.execute();
	if (msg != null) {
	  throw new IllegalStateException(
	    "Failed to evaluate classifier #" + (i+1) + " at level #" + (level+1) + ":\n" + msg);
	}
	indices = cv.getOriginalIndices();
	preds   = cv.getEvaluation().predictions();
	// create meta-level instances
	if (i == 0) {
	  unordered = new HashMap<>();
	  for (int index : indices) {
	    inst = createMetaLevelInstance(metaTrain, train.instance(index));
	    unordered.put(index, inst);
	  }
	  for (n = 0; n < unordered.size(); n++)
	    metaTrain.add(unordered.get(n));
	  unordered.clear();
	}
	// add predictions
	for (n = 0; n < indices.length; n++) {
	  if (m_Nominal)
	    addMetaLevelPrediction(metaTrain.instance(indices[n]), i, ((NominalPrediction) preds.get(n)).distribution());
	  else
	    addMetaLevelPrediction(metaTrain.instance(indices[n]), i, preds.get(n).predicted());
	}
      }

      // build models
      current = new ArrayList<>();
      for (i = 0; i < m_Classifiers.length; i++) {
	cls = (Classifier) OptionUtils.shallowCopy(m_Classifiers[i]);
	if (priorTrain == null) {
	  cls.buildClassifier(train);
	  current.add(cls);
	}
	else {
	  cls.buildClassifier(metaTrain);
	  current.add(cls);
	}
      }
      m_Cascade.get(m_Cascade.size() - 1).addAll(current);

      // evaluate on test set
      stats = new double[current.size()];
      for (i = 0; i < current.size(); i++) {
	if (priorTest == null) {
	  eval = new Evaluation(test);
	  eval.evaluateModel(current.get(i), test);
	}
	else {
	  eval = new Evaluation(metaTest);
	  eval.evaluateModel(current.get(i), metaTest);
	}
	stats[i] = EvaluationHelper.getValue(eval, m_Statistic, m_ClassIndex);
      }
      if (getDebug())
	System.out.println("--> " + m_Statistic + " (all): " + Utils.arrayToString(stats));
      stat = applyCombination(stats);
      if (getDebug())
	System.out.println("--> " + m_Statistic + " (" + m_Combination + "): " + stat);

      // converged?
      switch (m_ThresholdCheck) {
	case ABOVE:
	  converged = (stat > m_Threshold);
	  break;
	case BELOW:
	  converged = (stat < m_Threshold);
	  break;
	default:
	  throw new IllegalStateException("Unhandled threshold check (convergence): " + m_ThresholdCheck);
      }
      if (getDebug())
	System.out.println("--> Converged: " + converged);
      if (converged)
	break;

      // no more improvement?
      if (!Double.isNaN(priorStat)) {
	switch (m_ThresholdCheck) {
	  case ABOVE:
	    improved = (stat >= priorStat + m_MinImprovement);
	    break;
	  case BELOW:
	    improved = (stat <= priorStat - m_MinImprovement);
	    break;
	  default:
	    throw new IllegalStateException("Unhandled threshold check (improvement): " + m_ThresholdCheck);
	}
	if (getDebug())
	  System.out.println("--> Improved: " + improved);
	if (!improved) {
	  m_Cascade.remove(m_Cascade.size() - 1);
	  break;
	}
      }

      // build next test set
      metaTest = createMetaLevelHeader(test);
      for (n = 0; n < test.numInstances(); n++)
	metaTest.add(createMetaLevelInstance(metaTest, test.instance(n)));
      if (priorTest == null)
	priorTest = test;
      for (i = 0; i < current.size(); i++) {
	for (n = 0; n < metaTest.numInstances(); n++) {
	  if (m_Nominal)
	    addMetaLevelPrediction(metaTest.instance(n), i, current.get(i).distributionForInstance(priorTest.instance(n)));
	  else
	    addMetaLevelPrediction(metaTest.instance(n), i, current.get(i).classifyInstance(priorTest.instance(n)));
	}
      }
    }
  }

  /**
   * Returns the prediction for the instance.
   *
   * @param instance	the instance to get the class distribution for
   * @param distribution	class distribution or classification
   * @return		the class distribution or prediction
   * @throws Exception	if prediction fails
   */
  protected Object predictionForInstance(Instance instance, boolean distribution) throws Exception {
    int			i;
    int			n;
    int			level;
    Instance		meta;
    Instance		prior;
    List<Classifier>	current;
    double[]		dist;
    double[]		stats;
    double		cls;
    List<Object>	preds;
    int			finalLevel;

    preds      = new ArrayList<>();
    meta       = null;
    finalLevel = m_Cascade.size() - 1;
    for (level = 0; level < m_Cascade.size(); level++) {
      current = m_Cascade.get(level);
      prior = meta;
      meta = createMetaLevelInstance(m_MetaLevelHeader, instance);
      if (level == 0) {
	for (i = 0; i < current.size(); i++) {
	  if (distribution) {
	    dist = current.get(i).distributionForInstance(instance);
	    if (level == finalLevel)
	      preds.add(dist);
	    addMetaLevelPrediction(meta, i, dist);
	  }
	  else {
	    cls = current.get(i).classifyInstance(instance);
	    if (level == finalLevel)
	      preds.add(cls);
	    addMetaLevelPrediction(meta, i, cls);
	  }
	}
      }
      else {
	for (i = 0; i < current.size(); i++) {
	  if (distribution) {
	    dist = current.get(i).distributionForInstance(prior);
	    if (level == finalLevel)
	      preds.add(dist);
	    addMetaLevelPrediction(meta, i, dist);
	  }
	  else {
	    cls = current.get(i).classifyInstance(instance);
	    if (level == finalLevel)
	      preds.add(cls);
	    addMetaLevelPrediction(meta, i, cls);
	  }
	}
      }
    }

    if (distribution) {
      dist = new double[instance.numClasses()];
      for (i = 0; i < dist.length; i++) {
	stats = new double[m_Classifiers.length];
	for (n = 0; n < m_Classifiers.length; n++)
	  stats[n] = ((double[]) preds.get(n))[i];
	dist[i] = applyCombination(stats);
      }
      return dist;
    }
    else {
      stats = new double[m_Classifiers.length];
      for (n = 0; n < m_Classifiers.length; n++)
	stats[n] = (Double) preds.get(n);
      return applyCombination(stats);
    }
  }

  /**
   * Returns the distribution for the instance.
   *
   * @param instance	the instance to get the class distribution for
   * @return		the class distribution
   * @throws Exception	if prediction fails
   */
  @Override
  public double[] distributionForInstance(Instance instance) throws Exception {
    return (double[]) predictionForInstance(instance, true);
  }

  /**
   * Returns the classification for the instance.
   *
   * @param instance	the instance to get the classification for
   * @return		the classification
   * @throws Exception	if prediction fails
   */
  @Override
  public double classifyInstance(Instance instance) throws Exception {
    return (Double) predictionForInstance(instance, false);
  }

  /**
   * Outputs a short description of the classifier model.
   *
   * @return		the model description
   */
  public String toString() {
    StringBuilder	result;
    int			i;

    result = new StringBuilder();

    if (m_Cascade == null) {
      result.append("No cascade built yet!");
    }
    else {
      result.append(getClass().getName()).append("\n");
      result.append(getClass().getName().replaceAll(".", "=")).append("\n\n");
      result.append("Max levels: ").append(m_MaxLevels).append("\n");
      result.append("Actual levels: ").append(m_Cascade.size()).append("\n");
      result.append("Statistic: ").append(m_Statistic).append("\n");
      result.append("Threshold: ").append(m_Threshold).append("\n");
      result.append("Min improvement: ").append(m_MinImprovement).append("\n");
      result.append("# Folds: ").append(m_NumFolds).append("\n");
      result.append("Holdout %: ").append(m_HoldOutPercentage).append("\n");
      result.append("Classifiers:\n");
      for (i = 0; i < m_Classifiers.length; i++)
	result.append((i+1)).append(". ").append(Utils.toCommandLine(m_Classifiers[i])).append("\n");
    }

    return result.toString();
  }

  /**
   * Returns the revision string.
   *
   * @return the revision
   */
  @Override
  public String getRevision() {
    return "$Revision: 12765 $";
  }

  /**
   * Main method for executing the class.
   *
   * @param args 	the options
   */
  public static void main(String[] args) throws Exception {
    Environment.setEnvironmentClass(Environment.class);
    runClassifier(new ClassifierCascade(), args);
  }
}
