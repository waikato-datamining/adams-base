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
 * WekaClassifierRanker.java
 * Copyright (C) 2010-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.EnumWithCustomDisplay;
import adams.core.Pausable;
import adams.core.QuickInfoHelper;
import adams.core.Randomizable;
import adams.core.ThreadLimiter;
import adams.core.Utils;
import adams.core.logging.LoggingHelper;
import adams.core.option.AbstractOption;
import adams.core.option.OptionUtils;
import adams.event.FlowPauseStateEvent;
import adams.event.FlowPauseStateEvent.Type;
import adams.event.FlowPauseStateListener;
import adams.event.JobCompleteEvent;
import adams.event.JobCompleteListener;
import adams.flow.core.AbstractActor;
import adams.flow.core.ActorUtils;
import adams.flow.core.CallableActorHelper;
import adams.flow.core.CallableActorReference;
import adams.flow.core.Compatibility;
import adams.flow.core.OutputProducer;
import adams.flow.core.PauseStateHandler;
import adams.flow.core.Token;
import adams.multiprocess.Job;
import adams.multiprocess.JobList;
import adams.multiprocess.JobRunner;
import weka.classifiers.Evaluation;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.meta.GridSearch;
import weka.classifiers.meta.MultiSearch;
import weka.classifiers.meta.multisearch.Performance;
import weka.classifiers.meta.multisearch.PerformanceComparator;
import weka.core.Instances;
import weka.core.setupgenerator.Point;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Performs a quick evaluation using cross-validation on a single dataset (or evaluation on a separate test set if the number of folds is less than 2) to rank the classifiers received on the input and forwarding the x best ones. Further evaluation can be performed using the Experimenter.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;weka.classifiers.Classifier[]<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;weka.classifiers.Classifier[]<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: WekaClassifierRanker
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-max &lt;int&gt; (property: max)
 * &nbsp;&nbsp;&nbsp;The maximum number of top-ranked classifiers to forward; use -1 to forward 
 * &nbsp;&nbsp;&nbsp;all of them (ranked array).
 * &nbsp;&nbsp;&nbsp;default: 3
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-seed &lt;long&gt; (property: seed)
 * &nbsp;&nbsp;&nbsp;The seed value to use in the cross-validation.
 * &nbsp;&nbsp;&nbsp;default: 1
 * </pre>
 * 
 * <pre>-folds &lt;int&gt; (property: folds)
 * &nbsp;&nbsp;&nbsp;The number of folds to use in cross-validation.
 * &nbsp;&nbsp;&nbsp;default: 10
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-measure &lt;CC|RMSE|RRSE|MAE|RAE|COMBINED|ACC|KAPPA&gt; (property: measure)
 * &nbsp;&nbsp;&nbsp;The measure used for ranking the classifiers.
 * &nbsp;&nbsp;&nbsp;default: CC
 * </pre>
 * 
 * <pre>-train &lt;adams.flow.core.CallableActorReference&gt; (property: train)
 * &nbsp;&nbsp;&nbsp;The name of the callable actor that is used for obtaining the training set.
 * &nbsp;&nbsp;&nbsp;default: train
 * </pre>
 * 
 * <pre>-test &lt;adams.flow.core.CallableActorReference&gt; (property: test)
 * &nbsp;&nbsp;&nbsp;The name of the callable actor that is used for obtaining the test set (
 * &nbsp;&nbsp;&nbsp;only if folds &lt;2).
 * &nbsp;&nbsp;&nbsp;default: test
 * </pre>
 * 
 * <pre>-output-best &lt;boolean&gt; (property: outputBestSetup)
 * &nbsp;&nbsp;&nbsp;If true, then for optimizers like GridSearch and MultiSearch the best setup 
 * &nbsp;&nbsp;&nbsp;that was found will be output instead of the optimizer setup.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-num-threads &lt;int&gt; (property: numThreads)
 * &nbsp;&nbsp;&nbsp;The number of threads to use for evaluating the classifiers in parallel 
 * &nbsp;&nbsp;&nbsp;(-1 means one for each core&#47;cpu).
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaClassifierRanker
  extends AbstractTransformer
  implements Randomizable, Pausable, FlowPauseStateListener, ThreadLimiter {

  /** for serialization. */
  private static final long serialVersionUID = -3019442578354930841L;

  /**
   * A job class specific to ranking classifiers.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class RankingJob
    extends Job {

    /** for serialization. */
    private static final long serialVersionUID = 6105881068149718863L;

    /** the classifier to evaluate. */
    protected weka.classifiers.Classifier m_Classifier;

    /** the index in actor's input array. */
    protected int m_Index;

    /** the train data to evaluate with. */
    protected Instances m_Train;

    /** the test data to evaluate with (if folds less than 2). */
    protected Instances m_Test;

    /** the seed value to use. */
    protected long m_Seed;

    /** the number of folds to use. */
    protected int m_Folds;

    /** the measure to use for ranking. */
    protected Measure m_Measure;

    /** the performance. */
    protected Performance m_Performance;

    /** for storing evaluating errors. */
    protected String m_EvaluationError;

    /** whether to output the best classifier. */
    protected boolean m_OutputBestSetup;

    /** the best classifier. */
    protected weka.classifiers.Classifier m_BestClassifier;

    /**
     * Initializes the job.
     *
     * @param cls	the classifier to evaluate
     * @param index	the index of the classifier in input array
     * @param train	the training data to use
     * @param test	the test data to use
     * @param seed	the seed value to use
     * @param folds	the number of folds to use
     * @param measure	the measure to use for ranking
     * @param best	whether to output the best classifier setup (for optimizers)
     */
    public RankingJob(weka.classifiers.Classifier cls, int index, Instances train, Instances test, long seed, int folds, Measure measure, boolean best) {
      super();

      m_Classifier      = cls;
      m_Index           = index;
      m_Train           = train;
      m_Test            = test;
      m_Seed            = seed;
      m_Folds           = folds;
      m_Measure         = measure;
      m_Performance     = null;
      m_EvaluationError = "";
      m_BestClassifier  = (weka.classifiers.Classifier) Utils.deepCopy(cls);
      m_OutputBestSetup = best;
    }

    /**
     * Returns the classifier being used.
     *
     * @return		the classifier in use
     */
    public weka.classifiers.Classifier getClassifier() {
      return m_Classifier;
    }

    /**
     * Returns the index of the classifier in the actor's input array.
     *
     * @return		the index
     */
    public int getIndex() {
      return m_Index;
    }

    /**
     * The training data.
     *
     * @return		the training data
     */
    public Instances getTrain() {
      return m_Train;
    }

    /**
     * The test data.
     *
     * @return		the test data
     */
    public Instances getTest() {
      return m_Test;
    }

    /**
     * Returns the seed value.
     *
     * @return		the seed value
     */
    public long getSeed() {
      return m_Seed;
    }

    /**
     * Returns the number of folds.
     *
     * @return		the folds
     */
    public int getFolds() {
      return m_Folds;
    }

    /**
     * Returns the measure for ranking.
     *
     * @return		the measure
     */
    public Measure getMeasure() {
      return m_Measure;
    }

    /**
     * The generated performance.
     *
     * @return		the performance, can be null
     */
    public Performance getPerformance() {
      return m_Performance;
    }

    /**
     * Returns the best classifier found. For optimizers like GridSearch, this
     * outputs the best setup found. For all others, that's the same setup
     * as the actual classifier that was evaluated.
     *
     * @return		the best setup or the original setup
     */
    public weka.classifiers.Classifier getBestClassifier() {
      return m_BestClassifier;
    }

    /**
     * Returns whether the best setup is output in case of optimizers like
     * GridSearch/MultiSearch.
     *
     * @return		true if the best setup is output
     */
    public boolean getOutputBestSetup() {
      return m_OutputBestSetup;
    }

    /**
     * Checks whether all pre-conditions have been met.
     *
     * @return		null if everything is OK, otherwise an error message
     */
    @Override
    protected String preProcessCheck() {
      if (m_Classifier == null)
	return "No classifier set!";

      if (m_Train == null)
	return "No training data set!";

      if ((m_Folds < 2) && (m_Test == null))
	return "No test data set!";

      return null;
    }

    /**
     * In case of GridSearch/MultiSearch the best setup is returned, otherwise
     * the classifier itself.
     *
     * @param template	the template classifier
     * @param trained	the trained classifier
     * @return		either the best classifier (in case of GridSearch/MultiSearch) or the template
     */
    protected weka.classifiers.Classifier getBestClassifier(weka.classifiers.Classifier template, weka.classifiers.Classifier trained) {
      weka.classifiers.Classifier	result;

      result = template;

      if (m_OutputBestSetup && (m_Folds < 2)) {
	if (trained instanceof GridSearch) {
	  result = new FilteredClassifier();
	  ((FilteredClassifier) result).setClassifier(((GridSearch) trained).getBestClassifier());
	  ((FilteredClassifier) result).setFilter(((GridSearch) trained).getBestFilter());
	}
	else if (trained instanceof MultiSearch) {
	  try {
	    result = (weka.classifiers.Classifier) OptionUtils.shallowCopy(((MultiSearch) trained).getBestClassifier());
	  }
	  catch (Exception e) {
	    getLogger().log(Level.SEVERE, "Failed to copy best MultiSearch classifier:", e);
	    result = template;
	  }
	}
	// TODO: further optimizers
      }

      return result;
    }

    /**
     * Does the actual execution of the job.
     * 
     * @throws Exception if fails to execute job
     */
    @Override
    protected void process() throws Exception {
      Evaluation			eval;
      weka.classifiers.Classifier	cls;

      eval = new Evaluation(m_Train);
      eval.setDiscardPredictions(true);
      if (m_Folds >= 2) {
	eval.crossValidateModel(m_Classifier, m_Train, m_Folds, new Random(m_Seed));
      }
      else {
	cls = (weka.classifiers.Classifier) OptionUtils.shallowCopy(m_Classifier);
	cls.buildClassifier(m_Train);
	eval.evaluateModel(cls, m_Test);
	m_BestClassifier = getBestClassifier(m_Classifier, cls);
	cls              = null;
      }
      m_Performance = new Performance(new Point(new Integer[]{m_Index}), eval, m_Measure.getMeasure());
      eval = null;
    }

    /**
     * Checks whether all post-conditions have been met.
     *
     * @return		null if everything is OK, otherwise an error message
     */
    @Override
    protected String postProcessCheck() {
      if (m_Performance == null)
	return "No performance established!";

      return null;
    }

    /**
     * Cleans up data structures, frees up memory.
     * Sets the input data to null.
     */
    @Override
    public void cleanUp() {
      super.cleanUp();

      m_Classifier  = null;
      m_Performance = null;
      m_Train       = null;
      m_Test        = null;
    }

    /**
     * Returns additional information to be added to the error message.
     *
     * @return		the additional information
     */
    @Override
    protected String getAdditionalErrorInformation() {
      return m_EvaluationError;
    }

    /**
     * Returns a string representation of the job.
     *
     * @return		a string representation
     */
    @Override
    public String toString() {
      String	result;

      result = "data:" + m_Train.relationName() + ", ";
      result += "classifier: " + OptionUtils.getCommandLine(m_Classifier) + ", ";
      result += "seed: " + m_Seed + ", ";
      result += "folds: " + m_Folds + ", ";
      result += "measure: " + m_Measure;

      return result;
    }
  }

  /**
   * The performance measure to use.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum Measure
    implements EnumWithCustomDisplay<Measure> {
    /** evaluation via: Correlation coefficient. */
    CC("Correlation coefficient", Performance.EVALUATION_CC),
    /** evaluation via: Root mean squared error. */
    RMSE("Root mean squared error", Performance.EVALUATION_RMSE),
    /** evaluation via: Root relative squared error. */
    RRSE("Root relative squared error", Performance.EVALUATION_RRSE),
    /** evaluation via: Mean absolute error. */
    MAE("Mean absolute error", Performance.EVALUATION_MAE),
    /** evaluation via: Relative absolute error. */
    RAE("Root absolute error", Performance.EVALUATION_RAE),
    /** evaluation via: Combined = ("", Performance.1-CC) + RRSE + RAE. */
    COMBINED("Combined: (1-abs(CC)) + RRSE + RAE", Performance.EVALUATION_COMBINED),
    /** evaluation via: Accuracy. */
    ACC("Accuracy", Performance.EVALUATION_ACC),
    /** evaluation via: Kappa statistic. */
    KAPPA("Kapp", Performance.EVALUATION_KAPPA);

    /** the display string. */
    private String m_Display;

    /** the commandline string. */
    private String m_Raw;

    /** the performance measure. */
    private int m_Measure;

    /**
     * The constructor.
     *
     * @param display	the string to use as display
     * @param measure	the performance measure
     * @see		MultiSearch#TAGS_EVALUATION
     */
    private Measure(String display, int measure) {
      m_Display = display;
      m_Raw     = super.toString();
      m_Measure = measure;
    }

    /**
     * Returns the associated measure.
     *
     * @return		the measure
     * @see		MultiSearch#TAGS_EVALUATION
     */
    public int getMeasure() {
      return m_Measure;
    }

    /**
     * Returns the display string.
     *
     * @return		the display string
     */
    public String toDisplay() {
      return m_Display;
    }

    /**
     * Returns the raw enum string.
     *
     * @return		the raw enum string
     */
    public String toRaw() {
      return m_Raw;
    }

    /**
     * Returns the display string.
     *
     * @return		the display string
     */
    @Override
    public String toString() {
      return toDisplay();
    }

    /**
     * Parses the given string and returns the associated enum.
     *
     * @param s		the string to parse
     * @return		the enum or null if not found
     */
    public Measure parse(String s) {
      return (Measure) valueOf((AbstractOption) null, s);
    }

    /**
     * Returns the enum as string.
     *
     * @param option	the current option
     * @param object	the enum object to convert
     * @return		the generated string
     */
    public static String toString(AbstractOption option, Object object) {
      return ((Measure) object).toRaw();
    }

    /**
     * Returns an enum generated from the string.
     *
     * @param option	the current option
     * @param str	the string to convert to an enum
     * @return		the generated enum or null in case of error
     */
    public static Measure valueOf(AbstractOption option, String str) {
      Measure	result;

      result = null;

      // default parsing
      try {
        result = valueOf(str);
      }
      catch (Exception e) {
        // ignored
      }

      // try display
      if (result == null) {
	for (Measure dt: values()) {
	  if (dt.toDisplay().equals(str)) {
	    result = dt;
	    break;
	  }
	}
      }

      return result;
    }
  }

  /** the callable actor to obtain the training dataset from. */
  protected CallableActorReference m_Train;

  /** the callable actor to obtain the test dataset for train/test evaluation from. */
  protected CallableActorReference m_Test;

  /** the maximum number of top-ranked classifiers to forward. */
  protected int m_Max;

  /** the random seed to use. */
  protected long m_Seed;

  /** the number of folds to use in cross-validation. */
  protected int m_Folds;

  /** the measure for the evaluation. */
  protected Measure m_Measure;

  /** whether to output the best setup in case of GridSearch/MultiSearch. */
  protected boolean m_OutputBestSetup;

  /** the number of threads to use for parallel execution. */
  protected int m_NumThreads;

  /** the helper class. */
  protected CallableActorHelper m_Helper;

  /** the job runner for evaluating the setups. */
  protected JobRunner<RankingJob> m_JobRunner;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Performs a quick evaluation using cross-validation on a single dataset "
      + "(or evaluation on a separate test set if the number of folds is less than 2) to "
      + "rank the classifiers received on the input and forwarding the x best "
      + "ones. Further evaluation can be performed using the Experimenter.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "max", "max",
	    3, -1, null);

    m_OptionManager.add(
	    "seed", "seed",
	    1L);

    m_OptionManager.add(
	    "folds", "folds",
	    10, 1, null);

    m_OptionManager.add(
	    "measure", "measure",
	    Measure.CC);

    m_OptionManager.add(
	    "train", "train",
	    new CallableActorReference("train"));

    m_OptionManager.add(
	    "test", "test",
	    new CallableActorReference("test"));

    m_OptionManager.add(
	    "output-best", "outputBestSetup",
	    false);

    m_OptionManager.add(
	    "num-threads", "numThreads",
	    -1, -1, null);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Helper = new CallableActorHelper();
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    String	variable;
    String	value;

    variable = QuickInfoHelper.getVariable(this, "max");
    if (variable != null)
      result = variable + " best";
    else if (m_Max < 1)
      result = "all";
    else
      result = m_Max + " best";

    value = QuickInfoHelper.toString(this, "folds", (m_Folds >= 2 ? m_Folds : null), ", ");
    if (value != null)
      result += value + " folds";

    result += QuickInfoHelper.toString(this, "train", m_Train, ", training data: ");

    if (QuickInfoHelper.hasVariable(this, "folds") || (m_Folds < 2))
      result += QuickInfoHelper.toString(this, "test", m_Test, ", test data: ");

    result += QuickInfoHelper.toString(this, "numThreads", (m_NumThreads < 1 ? "#cores" : m_NumThreads), ", threads: ");

    return result;
  }

  /**
   * Sets the maximum number of top-ranked classifiers to forward.
   *
   * @param value	the maximum number, use -1 for all
   */
  public void setMax(int value) {
    if ((value > 0) || (value == -1)) {
      m_Max = value;
      reset();
    }
    else {
      getLogger().severe(
	  "Maximum number must be >0 or -1 for 'all', provided: " + value);
    }
  }

  /**
   * Returns the maximum number of top-ranked classifiers to forward.
   *
   * @return		the maximum number, -1 if all returned
   */
  public int getMax() {
    return m_Max;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String maxTipText() {
    return
        "The maximum number of top-ranked classifiers to forward; use -1 to "
      + "forward all of them (ranked array).";
  }

  /**
   * Sets the seed value.
   *
   * @param value	the seed
   */
  public void setSeed(long value) {
    m_Seed = value;
    reset();
  }

  /**
   * Returns the seed value.
   *
   * @return  		the seed
   */
  public long getSeed() {
    return m_Seed;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String seedTipText() {
    return "The seed value to use in the cross-validation.";
  }

  /**
   * Sets the number of folds to use.
   *
   * @param value	the folds
   */
  public void setFolds(int value) {
    if (value >= 1) {
      m_Folds = value;
      reset();
    }
    else {
      getLogger().severe(
	  "Number of folds must be >=1, provided: " + value);
    }
  }

  /**
   * Returns the number of folds to use.
   *
   * @return		the folds
   */
  public int getFolds() {
    return m_Folds;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String foldsTipText() {
    return "The number of folds to use in cross-validation.";
  }

  /**
   * Sets the measure to use for ranking the classifiers.
   *
   * @param value	the ranking measure
   */
  public void setMeasure(Measure value) {
    m_Measure = value;
    reset();
  }

  /**
   * Returns the measure used for ranking the classifiers.
   *
   * @return		the ranking measure
   */
  public Measure getMeasure() {
    return m_Measure;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String measureTipText() {
    return "The measure used for ranking the classifiers.";
  }

  /**
   * Sets the name of the callable actor to obtain the training set.
   *
   * @param value	the name of the callable actor
   */
  public void setTrain(CallableActorReference value) {
    m_Train = value;
    reset();
  }

  /**
   * Returns the name of the callable actor to obtain the training set.
   *
   * @return		the name of the callable actor
   */
  public CallableActorReference getTrain() {
    return m_Train;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String trainTipText() {
    return "The name of the callable actor that is used for obtaining the training set.";
  }

  /**
   * Sets the name of the callable actor to obtain the test set.
   *
   * @param value	the name of the callable actor
   */
  public void setTest(CallableActorReference value) {
    m_Test = value;
    reset();
  }

  /**
   * Returns the name of the callable actor to obtain the test set.
   *
   * @return		the name of the callable actor
   */
  public CallableActorReference getTest() {
    return m_Test;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String testTipText() {
    return "The name of the callable actor that is used for obtaining the test set (only if folds <2).";
  }

  /**
   * Sets whether to output the best setup found for optimizers like GridSearch
   * and MultiSearch.
   *
   * @param value	true if the best setup is to be output
   */
  public void setOutputBestSetup(boolean value) {
    m_OutputBestSetup = value;
    reset();
  }

  /**
   * Returns whether to output the best setup for optimizers like GridSearch
   * and MultiSearch.
   *
   * @return		true if the best setup is output
   */
  public boolean getOutputBestSetup() {
    return m_OutputBestSetup;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputBestSetupTipText() {
    return
        "If true, then for optimizers like GridSearch and MultiSearch the "
      + "best setup that was found will be output instead of the optimizer setup.";
  }

  /**
   * Sets the number of threads to use.
   *
   * @param value	the number of threads
   */
  public void setNumThreads(int value) {
    if (value >= -1) {
      m_NumThreads = value;
      reset();
    }
  }

  /**
   * Returns the number of threads in use.
   *
   * @return		the number of threads
   */
  public int getNumThreads() {
    return m_NumThreads;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numThreadsTipText() {
    return "The number of threads to use for evaluating the classifiers in parallel (-1 means one for each core/cpu).";
  }

  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String		result;
    AbstractActor	actor;
    Compatibility	comp;

    result = super.setUp();

    if (result == null) {
      actor = m_Helper.findCallableActorRecursive(this, m_Train);
      comp  = new Compatibility();
      if (actor == null)
	result = "Callable actor '" + m_Train + "' providing the training set not found!";
      else if (!ActorUtils.isSource(actor))
	result = "Callable actor '" + m_Train + "' (training set) is not a source!";
      else if (!comp.isCompatible(((OutputProducer) actor).generates(), new Class[]{Instances.class}))
	result = "Callable actor '" + m_Train + "' (training set) does not generated " + Instances.class.getName() + "!";
    }

    if ((result == null) && (m_Folds < 2)) {
      actor = m_Helper.findCallableActorRecursive(this, m_Test);
      comp  = new Compatibility();
      if (actor == null)
	result = "Callable actor '" + m_Test + "' providing the test set not found!";
      else if (!ActorUtils.isSource(actor))
	result = "Callable actor '" + m_Test  + "' (test set) is not a source!";
      else if (!comp.isCompatible(((OutputProducer) actor).generates(), new Class[]{Instances.class}))
	result = "Callable actor '" + m_Test + "' (test set) does not generated " + Instances.class.getName() + "!";
    }

    if (result == null) {
      if (getRoot() instanceof PauseStateHandler)
	((PauseStateHandler) getRoot()).getPauseStateManager().addListener(this);
    }
    
    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->weka.classifiers.Classifier[].class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{weka.classifiers.Classifier[].class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->weka.classifiers.Classifier[].class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{weka.classifiers.Classifier[].class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String				result;
    Instances				train;
    Instances				test;
    AbstractActor			source;
    JobList<RankingJob>			jobs;
    RankingJob				job;
    weka.classifiers.Classifier[]	cls;
    int					i;
    int					index;
    List<Performance>			ranking;
    List<weka.classifiers.Classifier>	ranked;
    String				msg;

    result = null;

    try {
      // get classifiers
      cls = (weka.classifiers.Classifier[]) m_InputToken.getPayload();

      // get training data
      train   = null;
      source = m_Helper.findCallableActorRecursive(this, m_Train);
      result = source.execute();
      if (result == null) {
	train = ((Instances) ((OutputProducer) source).output().getPayload());
	if (train == null)
	  result = "Failed to obtain training data from '" + m_Train + "'!";
      }

      // get test data
      test = null;
      if (m_Folds < 2) {
	source = m_Helper.findCallableActorRecursive(this, m_Test);
	result = source.execute();
	if (result == null) {
	  test = ((Instances) ((OutputProducer) source).output().getPayload());
	  if (test == null)
	    result = "Failed to obtain test data from '" + m_Test + "'!";
	}
      }

      // evaluate classifiers
      jobs = new JobList<RankingJob>();
      for (i = 0; i < cls.length; i++) {
	job = new RankingJob(cls[i], i, train, test, m_Seed, m_Folds, m_Measure, m_OutputBestSetup);
	jobs.add(job);
      }
      m_JobRunner = new JobRunner<RankingJob>(m_NumThreads);
      m_JobRunner.addJobCompleteListener(new JobCompleteListener() {
	private static final long serialVersionUID = 4773790554588513879L;
	public void jobCompleted(JobCompleteEvent e) {
	  if (isLoggingEnabled())
	    System.out.print(".");
        }
      });
      m_JobRunner.add(jobs);
      m_JobRunner.start();
      m_JobRunner.stop();

      if (!isStopped()) {
	// rank classifiers
	if (LoggingHelper.isAtLeast(getLogger(), Level.FINE))
	  getLogger().fine("\nEvaluations:");
	ranking = new ArrayList<Performance>();
	for (i = 0; i < jobs.size(); i++) {
	  job = jobs.get(i);
	  if (job.getPerformance() != null) {
	    ranking.add(job.getPerformance());
	    if (LoggingHelper.isAtLeast(getLogger(), Level.FINE))
	      getLogger().fine((i+1) + ". " +  m_Measure.toRaw() + "=" + job.getPerformance().getPerformance() + ": " + OptionUtils.getCommandLine(cls[i]));
	  }
	  else {
	    msg = (i+1) + ". no evaluation: " + OptionUtils.getCommandLine(cls[i]);
	    getLogger().severe(msg);
	    if (result == null)
	      result = "";
	    else
	      result += "\n\n";
	    result += msg;
	    if (job.hasExecutionError()) {
	      getLogger().severe(job.getExecutionError());
	      result += job.getExecutionError();
	    }
	  }
	}
	Collections.sort(ranking, new PerformanceComparator(m_Measure.getMeasure()));

	// generate output
	  if (LoggingHelper.isAtLeast(getLogger(), Level.FINE))
	  getLogger().fine("\nChosen classifiers (ranked):");
	ranked = new ArrayList<weka.classifiers.Classifier>();
	i      = ranking.size() - 1;
	while ((i >= 0) && (ranked.size() < m_Max)) {
	  index = (Integer) ranking.get(i).getValues().getValue(0);
	  ranked.add(jobs.get(index).getBestClassifier());
	  if (LoggingHelper.isAtLeast(getLogger(), Level.FINE))
	    getLogger().fine(
		(i+1) + ". " + OptionUtils.getCommandLine(ranked.get(ranked.size() - 1))
		+ "/" + m_Measure.toRaw() + ": "
		+ ranking.get(i).getPerformance());
	  i--;
	}
	m_OutputToken = new Token(ranked.toArray(new weka.classifiers.Classifier[ranked.size()]));
      }

      // clean up
      for (i = 0; i < jobs.size(); i++) {
	job = (RankingJob) jobs.get(i);
	job.cleanUp();
      }
    }
    catch (Exception e) {
      m_OutputToken = null;
      result = handleException("Failed to rank: ", e);
    }

    return result;
  }

  /**
   * Gets called when the pause state of the flow changes.
   * 
   * @param e		the event
   */
  public void flowPauseStateChanged(FlowPauseStateEvent e) {
    if (e.getType() == Type.PAUSED)
      pauseExecution();
    else
      resumeExecution();
  }

  /**
   * Pauses the execution.
   */
  public void pauseExecution() {
    if (m_JobRunner != null)
      m_JobRunner.pauseExecution();
  }

  /**
   * Returns whether the object is currently paused.
   *
   * @return		true if object is paused
   */
  public boolean isPaused() {
    if (m_JobRunner != null)
      return m_JobRunner.isPaused();
    else
      return false;
  }

  /**
   * Resumes the execution.
   */
  public void resumeExecution() {
    if (m_JobRunner != null)
      m_JobRunner.resumeExecution();
  }

  /**
   * Stops the execution.
   */
  @Override
  public void stopExecution() {
    super.stopExecution();

    if (m_JobRunner != null)
      m_JobRunner.terminate();
  }

  /**
   * Frees up memory in a "destructive" non-reversible way.
   */
  @Override
  public void cleanUp() {
    super.cleanUp();

    m_JobRunner = null;
    m_Helper    = null;
  }
}
