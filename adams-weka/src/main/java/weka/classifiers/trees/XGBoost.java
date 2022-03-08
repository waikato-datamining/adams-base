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
 * XGBoost.java
 * Copyright (C) 2019-2022 University of Waikato, Hamilton, NZ
 */

package weka.classifiers.trees;

import adams.core.TechnicalInformation;
import adams.core.TechnicalInformation.Type;
import adams.core.TechnicalInformationHandler;
import adams.core.base.BaseKeyValuePair;
import adams.core.management.LDD;
import adams.core.management.OS;
import adams.core.option.AbstractOption;
import ml.dmlc.xgboost4j.java.Booster;
import ml.dmlc.xgboost4j.java.DMatrix;
import ml.dmlc.xgboost4j.java.XGBoostError;
import weka.classifiers.simple.AbstractSimpleClassifier;
import weka.core.Attribute;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 <!-- globalinfo-start -->
 * Classifier implementing XGBoost.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- technical-bibtex-start -->
 * <pre>
 * &#64;inproceedings{Chen2016,
 *    address = {New York, NY, USA},
 *    author = {Chen, Tianqi and Guestrin, Carlos},
 *    booktitle = {Proceedings of the 22nd ACM SIGKDD International Conference on Knowledge Discovery and Data Mining},
 *    pages = {785--794},
 *    publisher = {ACM},
 *    series = {KDD '16},
 *    title = {XGBoost: A Scalable Tree Boosting System},
 *    year = {2016},
 *    ISBN = {978-1-4503-4232-2},
 *    keywords = {large-scale machine learning},
 *    location = {San Francisco, California, USA},
 *    URL = {http:&#47;&#47;doi.acm.org&#47;10.1145&#47;2939672.2939785}
 * }
 * </pre>
 * <br><br>
 <!-- technical-bibtex-end -->
 * <p>
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-booster &lt;GBTREE|GBLINEAR|DART&gt; (property: booster)
 * &nbsp;&nbsp;&nbsp;Which booster to use.
 * &nbsp;&nbsp;&nbsp;default: GBTREE
 * </pre>
 *
 * <pre>-verbosity &lt;SILENT|WARNING|INFO|DEBUG&gt; (property: verbosity)
 * &nbsp;&nbsp;&nbsp;Verbosity of printing messages.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-nthread &lt;int&gt; (property: numThreads)
 * &nbsp;&nbsp;&nbsp;The number of parallel threads used to run XGBoost.
 * &nbsp;&nbsp;&nbsp;default: -1
 * </pre>
 *
 * <pre>-eta &lt;float&gt; (property: eta)
 * &nbsp;&nbsp;&nbsp;The step size shrinkage to use in updates to prevent overfitting.
 * &nbsp;&nbsp;&nbsp;default: 0.3
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * &nbsp;&nbsp;&nbsp;maximum: 1.0
 * </pre>
 *
 * <pre>-gamma &lt;float&gt; (property: gamma)
 * &nbsp;&nbsp;&nbsp;The minimum loss reduction required to make a further partition on a leaf
 * &nbsp;&nbsp;&nbsp;node of the tree.
 * &nbsp;&nbsp;&nbsp;default: 0.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * &nbsp;&nbsp;&nbsp;maximum: Infinity
 * </pre>
 *
 * <pre>-max_depth &lt;int&gt; (property: maxDepth)
 * &nbsp;&nbsp;&nbsp;The maximum depth of a tree.
 * &nbsp;&nbsp;&nbsp;default: 6
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * &nbsp;&nbsp;&nbsp;maximum: 2147483647
 * </pre>
 *
 * <pre>-min_child_weight &lt;float&gt; (property: minChildWeight)
 * &nbsp;&nbsp;&nbsp;The minimum sum of instance weights (hessian) needed in a child.
 * &nbsp;&nbsp;&nbsp;default: 1.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * &nbsp;&nbsp;&nbsp;maximum: Infinity
 * </pre>
 *
 * <pre>-max_delta_step &lt;float&gt; (property: maximumDeltaStep)
 * &nbsp;&nbsp;&nbsp;The maximum delta step we allow each leaf output to be.
 * &nbsp;&nbsp;&nbsp;default: 0.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * &nbsp;&nbsp;&nbsp;maximum: 3.4028235E38
 * </pre>
 *
 * <pre>-subsample &lt;float&gt; (property: subsampleRatio)
 * &nbsp;&nbsp;&nbsp;The sub-sample ratio of the training instances.
 * &nbsp;&nbsp;&nbsp;default: 1.0
 * &nbsp;&nbsp;&nbsp;minimum: 1.4E-45
 * &nbsp;&nbsp;&nbsp;maximum: 1.0
 * </pre>
 *
 * <pre>-colsample_bytree &lt;float&gt; (property: columnSampleByTree)
 * &nbsp;&nbsp;&nbsp;The sub-sample ratio of columns when constructing each tree.
 * &nbsp;&nbsp;&nbsp;default: 1.0
 * &nbsp;&nbsp;&nbsp;minimum: 1.4E-45
 * &nbsp;&nbsp;&nbsp;maximum: 1.0
 * </pre>
 *
 * <pre>-colsample_bylevel &lt;float&gt; (property: columnSampleByLevel)
 * &nbsp;&nbsp;&nbsp;The sub-sample ratio of columns for each level.
 * &nbsp;&nbsp;&nbsp;default: 1.0
 * &nbsp;&nbsp;&nbsp;minimum: 1.4E-45
 * &nbsp;&nbsp;&nbsp;maximum: 1.0
 * </pre>
 *
 * <pre>-colsample_bynode &lt;float&gt; (property: columnSampleByNode)
 * &nbsp;&nbsp;&nbsp;The sub-sample ratio of columns for each node (split).
 * &nbsp;&nbsp;&nbsp;default: 1.0
 * &nbsp;&nbsp;&nbsp;minimum: 1.4E-45
 * &nbsp;&nbsp;&nbsp;maximum: 1.0
 * </pre>
 *
 * <pre>-tree_method &lt;AUTO|EXACT|APPROX|HIST|GPU_EXACT|GPU_HIST&gt; (property: treeMethod)
 * &nbsp;&nbsp;&nbsp;The tree construction algorithm used in XGBoost.
 * &nbsp;&nbsp;&nbsp;default: AUTO
 * </pre>
 *
 * <pre>-scale_pos_weight &lt;float&gt; (property: scalePositiveWeights)
 * &nbsp;&nbsp;&nbsp;Scales the weights of positive examples by this factor.
 * &nbsp;&nbsp;&nbsp;default: 1.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * &nbsp;&nbsp;&nbsp;maximum: 3.4028235E38
 * </pre>
 *
 * <pre>-process_type &lt;DEFAULT|UPDATE&gt; (property: processType)
 * &nbsp;&nbsp;&nbsp;The type of boosting process to run.
 * &nbsp;&nbsp;&nbsp;default: DEFAULT
 * </pre>
 *
 * <pre>-grow_policy &lt;DEPTHWISE|LOSSGUIDE&gt; (property: growPolicy)
 * &nbsp;&nbsp;&nbsp;The way new nodes are added to the tree.
 * &nbsp;&nbsp;&nbsp;default: DEPTHWISE
 * </pre>
 *
 * <pre>-max_leaves &lt;int&gt; (property: maxLeaves)
 * &nbsp;&nbsp;&nbsp;The maximum number of nodes to be added.
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * &nbsp;&nbsp;&nbsp;maximum: 2147483647
 * </pre>
 *
 * <pre>-max_bin &lt;int&gt; (property: maxBin)
 * &nbsp;&nbsp;&nbsp;The maximum number of discrete bins to bucket continuous features.
 * &nbsp;&nbsp;&nbsp;default: 256
 * &nbsp;&nbsp;&nbsp;minimum: 2
 * &nbsp;&nbsp;&nbsp;maximum: 2147483647
 * </pre>
 *
 * <pre>-predictor &lt;CPU|GPU|DEFAULT&gt; (property: predictor)
 * &nbsp;&nbsp;&nbsp;The type of predictor algorithm to use.
 * &nbsp;&nbsp;&nbsp;default: DEFAULT
 * </pre>
 *
 * <pre>-num_parallel_tree &lt;int&gt; (property: numberOfParallelTrees)
 * &nbsp;&nbsp;&nbsp;The number of parallel trees constructed during each iteration.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * &nbsp;&nbsp;&nbsp;maximum: 2147483647
 * </pre>
 *
 * <pre>-sample_type &lt;UNIFORM|WEIGHTED&gt; (property: sampleType)
 * &nbsp;&nbsp;&nbsp;The type of sampling algorithm.
 * &nbsp;&nbsp;&nbsp;default: UNIFORM
 * </pre>
 *
 * <pre>-normalize_type &lt;TREE|FOREST&gt; (property: normaliseType)
 * &nbsp;&nbsp;&nbsp;The type of normalisation algorithm.
 * &nbsp;&nbsp;&nbsp;default: TREE
 * </pre>
 *
 * <pre>-rate_drop &lt;float&gt; (property: rateDrop)
 * &nbsp;&nbsp;&nbsp;The dropout rate (a fraction of previous trees to drop during the dropout
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: 0.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * &nbsp;&nbsp;&nbsp;maximum: 1.0
 * </pre>
 *
 * <pre>-one_drop &lt;boolean&gt; (property: oneDrop)
 * &nbsp;&nbsp;&nbsp;Whether at least one tree is always dropped during the dropout.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-skip_drop &lt;float&gt; (property: skipDrop)
 * &nbsp;&nbsp;&nbsp;The probability of skipping the dropout procedure during a boosting iteration.
 * &nbsp;&nbsp;&nbsp;default: 0.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * &nbsp;&nbsp;&nbsp;maximum: 1.0
 * </pre>
 *
 * <pre>-lambda &lt;float&gt; (property: lambda)
 * &nbsp;&nbsp;&nbsp;The L2 regularisation term on weights.
 * &nbsp;&nbsp;&nbsp;default: 1.0
 * </pre>
 *
 * <pre>-alpha &lt;float&gt; (property: alpha)
 * &nbsp;&nbsp;&nbsp;The L1 regularisation term on weights.
 * &nbsp;&nbsp;&nbsp;default: 0.0
 * </pre>
 *
 * <pre>-updater &lt;SHOTGUN|COORD_DESCENT&gt; (property: updater)
 * &nbsp;&nbsp;&nbsp;The choice of algorithm to fit the linear model.
 * &nbsp;&nbsp;&nbsp;default: SHOTGUN
 * </pre>
 *
 * <pre>-feature_selector &lt;CYCLIC|SHUFFLE|RANDOM|GREEDY|THRIFTY&gt; (property: featureSelector)
 * &nbsp;&nbsp;&nbsp;The feature selection and ordering method.
 * &nbsp;&nbsp;&nbsp;default: CYCLIC
 * </pre>
 *
 * <pre>-top_k &lt;int&gt; (property: topK)
 * &nbsp;&nbsp;&nbsp;The number of top features to select when using the greedy or thrifty feature
 * &nbsp;&nbsp;&nbsp;selector.
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * &nbsp;&nbsp;&nbsp;maximum: 2147483647
 * </pre>
 *
 * <pre>-tweedie_variance_power &lt;float&gt; (property: tweedieVariancePower)
 * &nbsp;&nbsp;&nbsp;The parameter that controls the variance of the Tweedie distribution.
 * &nbsp;&nbsp;&nbsp;default: 1.5
 * &nbsp;&nbsp;&nbsp;minimum: 1.0
 * &nbsp;&nbsp;&nbsp;maximum: 2.0
 * </pre>
 *
 * <pre>-objective &lt;LINEAR_REGRESSION|LOGISTIC_REGRESSION|LOGISTIC_REGRESSION_FOR_BINARY_CLASSIFICATION|LOGIT_RAW_REGRESSION_FOR_BINARY_CLASSIFICATION|HINGE_LOSS_FOR_BINARY_CLASSIFICATION|POISSON_REGRESSION_FOR_COUNT_DATA|COX_REGRESSION|SOFTMAX_MULTICLASS_CLASSIFICATION|SOFTPROB_MULTICLASS_CLASSIFICATION|LAMBDAMART_PAIRWISE_RANKING|LAMBDAMART_MAXIMISE_NDCG|LAMBDAMART_MAXIMISE_MAP|GAMMA_REGRESSION|TWEEDIE_REGRESSION&gt; (property: objective)
 * &nbsp;&nbsp;&nbsp;The learning objective.
 * &nbsp;&nbsp;&nbsp;default: LINEAR_REGRESSION
 * </pre>
 *
 * <pre>-base_score &lt;float&gt; (property: baseScore)
 * &nbsp;&nbsp;&nbsp;The initial prediction score of all instances (global bias).
 * &nbsp;&nbsp;&nbsp;default: 0.5
 * </pre>
 *
 * <pre>-seed &lt;int&gt; (property: seed)
 * &nbsp;&nbsp;&nbsp;The random number seed.
 * &nbsp;&nbsp;&nbsp;default: 0
 * </pre>
 *
 * <pre>-rounds &lt;int&gt; (property: numberOfRounds)
 * &nbsp;&nbsp;&nbsp;The number of boosting rounds to perform.
 * &nbsp;&nbsp;&nbsp;default: 2
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * &nbsp;&nbsp;&nbsp;maximum: 2147483647
 * </pre>
 *
 * <pre>-other_params &lt;adams.core.base.BaseKeyValuePair&gt; [-other_params ...] (property: otherParameters)
 * &nbsp;&nbsp;&nbsp;Passes any additional parameters to XGBoost.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 <!-- options-end -->
 * <p>
 * Wrapper class that uses the XGBoost4J library to implement
 * XGBoost as a WEKA classifier.
 *
 * @author Corey Sterling (csterlin at waikato dot ac dot nz)
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class XGBoost extends AbstractSimpleClassifier implements TechnicalInformationHandler {

  /** Auto-generated serialisation UID#. */
  private static final long serialVersionUID = 7228620850250174821L;

  // https://github.com/dmlc/xgboost/issues/2578
  public final static String[] MIN_GLIBC_VERSION = {"2", "23"};

  /**
   * The available types of booster.
   */
  public enum BoosterType {
    GBTREE,
    GBLINEAR,
    DART
  }

  /**
   * The possible verbosity levels.
   */
  public enum Verbosity implements ParamValueProvider {
    SILENT,
    WARNING,
    INFO,
    DEBUG;

    @Override
    public Integer paramValue() {
      return ordinal();
    }
  }

  /**
   * The set of possible learning objectives.
   */
  public enum Objective implements ParamValueProvider {
    LINEAR_REGRESSION("reg:linear"),
    LOGISTIC_REGRESSION("reg:logistic"),
    LOGISTIC_REGRESSION_FOR_BINARY_CLASSIFICATION("binary:logistic"),
    LOGIT_RAW_REGRESSION_FOR_BINARY_CLASSIFICATION("binary:logitraw"),
    HINGE_LOSS_FOR_BINARY_CLASSIFICATION("binary:hinge"),
    POISSON_REGRESSION_FOR_COUNT_DATA("count:poisson"),
    COX_REGRESSION("survival:cox"),
    SOFTMAX_MULTICLASS_CLASSIFICATION("multi:softmax"),
    SOFTPROB_MULTICLASS_CLASSIFICATION("multi:softprob"),
    LAMBDAMART_PAIRWISE_RANKING("rank:pairwise"),
    LAMBDAMART_MAXIMISE_NDCG("rank:ndcg"),
    LAMBDAMART_MAXIMISE_MAP("rank:map"),
    GAMMA_REGRESSION("reg:gamma"),
    TWEEDIE_REGRESSION("reg:tweedie");

    private final String m_ParamString;

    Objective(String paramString) {
      m_ParamString = paramString;
    }

    @Override
    public String paramValue() {
      return m_ParamString;
    }
  }

  /**
   * Possible tree-method settings.
   */
  public enum TreeMethod {
    AUTO,
    EXACT,
    APPROX,
    HIST,
    GPU_EXACT,
    GPU_HIST
  }

  /**
   * Available process-type settings.
   */
  public enum ProcessType {
    DEFAULT,
    UPDATE
  }

  /**
   * Available grow policy settings.
   */
  public enum GrowPolicy {
    DEPTHWISE,
    LOSSGUIDE
  }

  /**
   * Available predictors.
   */
  public enum Predictor implements ParamValueProvider {
    CPU,
    GPU,
    DEFAULT;

    @Override
    public String paramValue() {
      return name().toLowerCase() + "_predictor";
    }
  }

  /**
   * Available sample-type settings.
   */
  public enum SampleType {
    UNIFORM,
    WEIGHTED
  }

  /**
   * Available normalisation-type settings.
   */
  public enum NormaliseType {
    TREE,
    FOREST
  }

  /**
   * Available updaters.
   */
  public enum Updater {
    SHOTGUN,
    COORD_DESCENT
  }

  /**
   * Available feature selectors.
   */
  public enum FeatureSelector {
    CYCLIC,
    SHUFFLE,
    RANDOM,
    GREEDY,
    THRIFTY
  }

  /**
   * Provides a value suitable as a proxy for the XGBoost parameter system.
   */
  protected interface ParamValueProvider {

    /**
     * Provides a proxy object suitable for the XGBoost parameter system
     * in place of this object.
     *
     * @return The object to give to XGBoost as a parameter.
     */
    Object paramValue();
  }

  /**
   * Marks a field as participating in the XGBoost parameter system.
   */
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.FIELD)
  protected @interface XGBoostParameter {

    /**
     * The name of the parameter this field corresponds to.
     */
    String value();
  }

  /*=== General Parameters ===*/

  /** The type of booster to use. */
  @XGBoostParameter("booster")
  protected BoosterType m_BoosterType;

  /** Verbosity of printing messages. */
  @XGBoostParameter("verbosity")
  protected Verbosity m_Verbosity;

  /** The number of threads to use. */
  @XGBoostParameter("nthread")
  protected int m_NumberOfThreads;

  /*=== Parameters for Tree Boosters ===*/

  /** The eta value (learning rate). */
  @XGBoostParameter("eta")
  protected float m_Eta;

  /** The gamma value (minimum split loss). */
  @XGBoostParameter("gamma")
  protected float m_Gamma;

  /** The maximum depth of the tree. */
  @XGBoostParameter("max_depth")
  protected int m_MaxDepth;

  /** The minimum child weight. */
  @XGBoostParameter("min_child_weight")
  protected float m_MinChildWeight;

  /** Maximum delta step. */
  @XGBoostParameter("max_delta_step")
  protected float m_MaxDeltaStep;

  /** Subsample ratio of the training instances. */
  @XGBoostParameter("subsample")
  protected float m_Subsample;

  /** Subsample ratio of columns when constructing each tree. */
  @XGBoostParameter("colsample_bytree")
  protected float m_ColumnSampleByTree;

  /** Subsample ratio of columns for each level. */
  @XGBoostParameter("colsample_bylevel")
  protected float m_ColumnSampleByLevel;

  /** Subsample ratio of columns for each node (split). */
  @XGBoostParameter("colsample_bynode")
  protected float m_ColumnSampleByNode;

  /** The tree construction algorithm. */
  @XGBoostParameter("tree_method")
  protected TreeMethod m_TreeMethod;

  /** Scales the weights of positive instances by this factor. */
  @XGBoostParameter("scale_pos_weight")
  protected float m_ScalePositiveWeights;

  /** The type of boosting process to run. */
  @XGBoostParameter("process_type")
  protected ProcessType m_ProcessType;

  /** Controls the way new nodes are added to the tree. */
  @XGBoostParameter("grow_policy")
  protected GrowPolicy m_GrowPolicy;

  /** Maximum number of nodes to be added. */
  @XGBoostParameter("max_leaves")
  protected int m_MaxLeaves;

  /** Maximum number of discrete bins to bucket continuous features. */
  @XGBoostParameter("max_bin")
  protected int m_MaxBin;

  /** The type of predictor algorithm to use. */
  @XGBoostParameter("predictor")
  protected Predictor m_Predictor;

  /** The number of parallel trees constructed during each iteration. */
  @XGBoostParameter("num_parallel_tree")
  protected int m_NumberOfParallelTrees;

  /*=== Additional Parameters for DART Booster ===*/

  /** Type of sampling algorithm. */
  @XGBoostParameter("sample_type")
  protected SampleType m_SampleType;

  /** Type of normalisation algorithm. */
  @XGBoostParameter("normalize_type")
  protected NormaliseType m_NormaliseType;

  /** Dropout rate. */
  @XGBoostParameter("rate_drop")
  protected float m_RateDrop;

  /** Whether to always drop at least one tree during dropout. */
  @XGBoostParameter("one_drop")
  protected boolean m_OneDrop;

  /** Probability of skipping the dropout procedure during the boosting operation. */
  @XGBoostParameter("skip_drop")
  protected float m_SkipDrop;

  /*=== Parameters for Linear Booster ===*/

  /** L2 regularisation term on weights. */
  @XGBoostParameter("lambda")
  protected float m_Lambda;

  /** L1 regularisation term on weights. */
  @XGBoostParameter("alpha")
  protected float m_Alpha;

  /** Choice of algorithm to fit linear model. */
  @XGBoostParameter("updater")
  protected Updater m_Updater;

  /** Feature selection and ordering method. */
  @XGBoostParameter("feature_selector")
  protected FeatureSelector m_FeatureSelector;

  /** The number of top features to select. */
  @XGBoostParameter("top_k")
  protected int m_TopK;

  /*=== Parameters for Tweedie Regression ===*/

  /** Parameter that controls the variance of the Tweedie distribution. */
  @XGBoostParameter("tweedie_variance_power")
  protected float m_TweedieVariancePower;

  /*=== Learning Task Parameters ===*/

  /** The learning objective. */
  @XGBoostParameter("objective")
  protected Objective m_Objective;

  /** Global bias. */
  @XGBoostParameter("base_score")
  protected float m_BaseScore;

  /** The random number seed. */
  @XGBoostParameter("seed")
  protected int m_Seed;

  /*=== Special-Handling-Required Parameters ===*/

  /** The number of boosting rounds to perform. */
  protected int m_NumberOfRounds;

  /** Allows the user to enter arbitrary parameters. */
  protected BaseKeyValuePair[] m_OtherParameters;

  /** The trained model. */
  protected Booster m_Booster;

  /** the training dataset. */
  protected Instances m_Header;

  /** the xgboost parameters. */
  protected Map<String, Object> m_Params;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Classifier implementing XGBoost.";
  }

  /**
   * Adds options to the internal list of options. Derived classes must
   * override this method to add additional options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    /* General Parameters */
    m_OptionManager.add("booster", "booster", BoosterType.GBTREE);
    m_OptionManager.add("verbosity", "verbosity", Verbosity.WARNING);
    m_OptionManager.add("nthread", "numThreads", -1);

    /* Parameters for Tree Boosters */
    m_OptionManager.add("eta", "eta", 0.3f, 0.0f, 1.0f);
    m_OptionManager.add("gamma", "gamma", 0.0f, 0.0f, Float.POSITIVE_INFINITY);
    m_OptionManager.add("max_depth", "maxDepth", 6, 0, Integer.MAX_VALUE);
    m_OptionManager.add("min_child_weight", "minChildWeight", 1.0f, 0.0f, Float.POSITIVE_INFINITY);
    m_OptionManager.add("max_delta_step", "maximumDeltaStep", 0.0f, 0.0f, Float.MAX_VALUE);
    m_OptionManager.add("subsample", "subsampleRatio", 1.0f, Float.MIN_VALUE, 1.0f);
    m_OptionManager.add("colsample_bytree", "columnSampleByTree", 1.0f, Float.MIN_VALUE, 1.0f);
    m_OptionManager.add("colsample_bylevel", "columnSampleByLevel", 1.0f, Float.MIN_VALUE, 1.0f);
    m_OptionManager.add("colsample_bynode", "columnSampleByNode", 1.0f, Float.MIN_VALUE, 1.0f);
    m_OptionManager.add("tree_method", "treeMethod", TreeMethod.AUTO);
    m_OptionManager.add("scale_pos_weight", "scalePositiveWeights", 1.0f, 0.0f, Float.MAX_VALUE);
    m_OptionManager.add("process_type", "processType", ProcessType.DEFAULT);
    m_OptionManager.add("grow_policy", "growPolicy", GrowPolicy.DEPTHWISE);
    m_OptionManager.add("max_leaves", "maxLeaves", 0, 0, Integer.MAX_VALUE);
    m_OptionManager.add("max_bin", "maxBin", 256, 2, Integer.MAX_VALUE);
    m_OptionManager.add("predictor", "predictor", Predictor.DEFAULT);
    m_OptionManager.add("num_parallel_tree", "numberOfParallelTrees", 1, 1, Integer.MAX_VALUE);

    /* Additional Parameters for DART Booster */
    m_OptionManager.add("sample_type", "sampleType", SampleType.UNIFORM);
    m_OptionManager.add("normalize_type", "normaliseType", NormaliseType.TREE);
    m_OptionManager.add("rate_drop", "rateDrop", 0.0f, 0.0f, 1.0f);
    m_OptionManager.add("one_drop", "oneDrop", false);
    m_OptionManager.add("skip_drop", "skipDrop", 0.0f, 0.0f, 1.0f);

    /* Parameters for Linear Booster */
    m_OptionManager.add("lambda", "lambda", 1.0f);
    m_OptionManager.add("alpha", "alpha", 0.0f);
    m_OptionManager.add("updater", "updater", Updater.SHOTGUN);
    m_OptionManager.add("feature_selector", "featureSelector", FeatureSelector.CYCLIC);
    m_OptionManager.add("top_k", "topK", 0, 0, Integer.MAX_VALUE);

    /* Parameters for Tweedie Regression */
    m_OptionManager.add("tweedie_variance_power", "tweedieVariancePower", 1.5f, 1.0f, 2.0f);

    /* Learning Task Parameters */
    m_OptionManager.add("objective", "objective", Objective.LINEAR_REGRESSION);
    m_OptionManager.add("base_score", "baseScore", 0.5f);
    m_OptionManager.add("seed", "seed", 0);

    /* Special-Handling-Required Parameters */
    m_OptionManager.add("rounds", "numberOfRounds", 2, 1, Integer.MAX_VALUE);
    m_OptionManager.add("other_params", "otherParameters", new BaseKeyValuePair[0]);

  }

  /*=== General Parameters ===*/

  /**
   * Gets the type of booster to use.
   *
   * @return The booster type.
   */
  public BoosterType getBooster() {
    return m_BoosterType;
  }

  /**
   * Sets the type of booster to use.
   *
   * @param value The booster type.
   */
  public void setBooster(BoosterType value) {
    m_BoosterType = value;
    reset();
  }

  /**
   * Gets the tip-text for the booster option.
   *
   * @return The tip-text as a string.
   */
  public String boosterTipText() {
    return "Which booster to use.";
  }

  /**
   * Gets the verbosity level.
   *
   * @return The verbosity level.
   */
  public Verbosity getVerbosity() {
    return m_Verbosity;
  }

  /**
   * Sets the verbosity level.
   *
   * @param value The verbosity level.
   */
  public void setVerbosity(Verbosity value) {
    m_Verbosity = value;
    reset();
  }

  /**
   * Gets the tip-text for the verbosity option.
   *
   * @return The tip-text as a string.
   */
  public String verbosityTipText() {
    return "Verbosity of printing messages.";
  }

  /**
   * Gets the number of parallel threads used to run XGBoost.
   *
   * @return The number of threads.
   */
  public int getNumThreads() {
    return m_NumberOfThreads;
  }

  /**
   * Sets the number of parallel threads used to run XGBoost.
   *
   * @param value The number of threads.
   */
  public void setNumThreads(int value) {
    m_NumberOfThreads = value;
    reset();
  }

  /**
   * Gets the tip-text for the numThreads option.
   *
   * @return The tip-text as a string.
   */
  public String numThreadsTipText() {
    return "The number of parallel threads used to run XGBoost.";
  }

  /*=== Parameters for Tree Boosters ===*/

  /**
   * Gets the step size shrinkage to use in updates to prevent overfitting.
   *
   * @return The eta value.
   */
  public float getEta() {
    return m_Eta;
  }

  /**
   * Sets the step size shrinkage to use in updates to prevent overfitting.
   *
   * @param value The eta value.
   */
  public void setEta(float value) {
    m_Eta = value;
    reset();
  }

  /**
   * Gets the tip-text for the eta option.
   *
   * @return The tip-text as a string.
   */
  public String etaTipText() {
    return "The step size shrinkage to use in updates to prevent overfitting.";
  }

  /**
   * Gets the minimum loss reduction required to make a further partition
   * on a leaf node of the tree.
   *
   * @return The gamma value.
   */
  public float getGamma() {
    return m_Gamma;
  }

  /**
   * Sets the minimum loss reduction required to make a further partition
   * on a leaf node of the tree.
   *
   * @param value The gamma value.
   */
  public void setGamma(float value) {
    m_Gamma = value;
    reset();
  }

  /**
   * Gets the tip-text for the gamma option.
   *
   * @return The tip-text as a string.
   */
  public String gammaTipText() {
    return "The minimum loss reduction required to make a further partition " +
      "on a leaf node of the tree.";
  }

  /**
   * Gets the maximum depth of a tree.
   *
   * @return The maximum depth.
   */
  public int getMaxDepth() {
    return m_MaxDepth;
  }

  /**
   * Sets the maximum depth of a tree.
   *
   * @param value The maximum depth.
   */
  public void setMaxDepth(int value) {
    m_MaxDepth = value;
    reset();
  }

  /**
   * Gets the tip-text for the maxDepth option.
   *
   * @return The tip-text as a string.
   */
  public String maxDepthTipText() {
    return "The maximum depth of a tree.";
  }

  /**
   * Gets the minimum sum of instance weights (hessian) needed in a child.
   *
   * @return The minimum sum.
   */
  public float getMinChildWeight() {
    return m_MinChildWeight;
  }

  /**
   * Sets the minimum sum of instance weights (hessian) needed in a child.
   *
   * @param value The minimum sum.
   */
  public void setMinChildWeight(float value) {
    m_MinChildWeight = value;
    reset();
  }

  /**
   * Gets the tip-text for the minChildWeight option.
   *
   * @return The tip-text as a string.
   */
  public String minChildWeightTipText() {
    return "The minimum sum of instance weights (hessian) needed in a child.";
  }

  /**
   * Gets the maximum delta step we allow each leaf output to be.
   *
   * @return The maximum delta step.
   */
  public float getMaximumDeltaStep() {
    return m_MaxDeltaStep;
  }

  /**
   * Sets the maximum delta step we allow each leaf output to be.
   *
   * @param value The maximum delta step.
   */
  public void setMaximumDeltaStep(float value) {
    m_MaxDeltaStep = value;
    reset();
  }

  /**
   * Gets the tip-text for the maximumDeltaStep option.
   *
   * @return The tip-text as a string.
   */
  public String maximumDeltaStepTipText() {
    return "The maximum delta step we allow each leaf output to be.";
  }

  /**
   * Gets the sub-sample ratio of the training instances.
   *
   * @return The sub-sample ratio.
   */
  public float getSubsampleRatio() {
    return m_Subsample;
  }

  /**
   * Sets the sub-sample ratio of the training instances.
   *
   * @param value The sub-sample ratio.
   */
  public void setSubsampleRatio(float value) {
    m_Subsample = value;
    reset();
  }

  /**
   * Gets the tip-text for the subsampleRatio option.
   *
   * @return The tip-text as a string.
   */
  public String subsampleRatioTipText() {
    return "The sub-sample ratio of the training instances.";
  }

  /**
   * Gets the sub-sample ratio of columns when constructing each tree.
   *
   * @return The sub-sample ratio.
   */
  public float getColumnSampleByTree() {
    return m_ColumnSampleByTree;
  }

  /**
   * Sets the sub-sample ratio of columns when constructing each tree.
   *
   * @param value The sub-sample ratio.
   */
  public void setColumnSampleByTree(float value) {
    m_ColumnSampleByTree = value;
    reset();
  }

  /**
   * Gets the tip-text for the columnSampleByTree option.
   *
   * @return The tip-text as a string.
   */
  public String columnSampleByTreeTipText() {
    return "The sub-sample ratio of columns when constructing each tree.";
  }

  /**
   * Gets the sub-sample ratio of columns for each level.
   *
   * @return The sub-sample ratio.
   */
  public float getColumnSampleByLevel() {
    return m_ColumnSampleByLevel;
  }

  /**
   * Sets the sub-sample ratio of columns for each level.
   *
   * @param value The sub-sample ratio.
   */
  public void setColumnSampleByLevel(float value) {
    m_ColumnSampleByLevel = value;
    reset();
  }

  /**
   * Gets the tip-text for the columnSampleByLevel option.
   *
   * @return The tip-text as a string.
   */
  public String columnSampleByLevelTipText() {
    return "The sub-sample ratio of columns for each level.";
  }

  /**
   * Gets the sub-sample ratio of columns for each node (split).
   *
   * @return The sub-sample ratio.
   */
  public float getColumnSampleByNode() {
    return m_ColumnSampleByNode;
  }

  /**
   * Sets the sub-sample ratio of columns for each node (split).
   *
   * @param value The sub-sample ratio.
   */
  public void setColumnSampleByNode(float value) {
    m_ColumnSampleByNode = value;
    reset();
  }

  /**
   * Gets the tip-text for the columnSampleByNode option.
   *
   * @return The tip-text as a string.
   */
  public String columnSampleByNodeTipText() {
    return "The sub-sample ratio of columns for each node (split).";
  }

  /**
   * Gets the tree construction algorithm used in XGBoost.
   *
   * @return The algorithm.
   */
  public TreeMethod getTreeMethod() {
    return m_TreeMethod;
  }

  /**
   * Sets the tree construction algorithm used in XGBoost.
   *
   * @param value The algorithm.
   */
  public void setTreeMethod(TreeMethod value) {
    m_TreeMethod = value;
    reset();
  }

  /**
   * Gets the tip-text for the treeMethod option.
   *
   * @return The tip-text as a string.
   */
  public String treeMethodTipText() {
    return "The tree construction algorithm used in XGBoost.";
  }

  /**
   * Gets the positive-weights scale factor.
   *
   * @return The scale factor.
   */
  public float getScalePositiveWeights() {
    return m_ScalePositiveWeights;
  }

  /**
   * Sets the positive-weights scale factor.
   *
   * @param value The scale factor.
   */
  public void setScalePositiveWeights(float value) {
    m_ScalePositiveWeights = value;
    reset();
  }

  /**
   * Gets the tip-text for the scalePositiveWeights option.
   *
   * @return The tip-text as a string.
   */
  public String scalePositiveWeightsTipText() {
    return "Scales the weights of positive examples by this factor.";
  }

  /**
   * Gets the type of boosting process to run.
   *
   * @return The process type.
   */
  public ProcessType getProcessType() {
    return m_ProcessType;
  }

  /**
   * Sets the type of boosting process to run.
   *
   * @param value The process type.
   */
  public void setProcessType(ProcessType value) {
    m_ProcessType = value;
    reset();
  }

  /**
   * Gets the tip-text for the processType option.
   *
   * @return The tip-text as a string.
   */
  public String processTypeTipText() {
    return "The type of boosting process to run.";
  }

  /**
   * Gets the way new nodes are added to the tree.
   *
   * @return The grow policy.
   */
  public GrowPolicy getGrowPolicy() {
    return m_GrowPolicy;
  }

  /**
   * Sets the way new nodes are added to the tree.
   *
   * @param value The grow policy.
   */
  public void setGrowPolicy(GrowPolicy value) {
    m_GrowPolicy = value;
    reset();
  }

  /**
   * Gets the tip-text for the growPolicy option.
   *
   * @return The tip-text as a string.
   */
  public String growPolicyTipText() {
    return "The way new nodes are added to the tree.";
  }

  /**
   * Gets the maximum number of nodes to be added.
   *
   * @return The maximum number of nodes.
   */
  public int getMaxLeaves() {
    return m_MaxLeaves;
  }

  /**
   * Sets the maximum number of nodes to be added.
   *
   * @param value The maximum number of nodes.
   */
  public void setMaxLeaves(int value) {
    m_MaxLeaves = value;
    reset();
  }

  /**
   * Gets the tip-text for the maxLeaves option.
   *
   * @return The tip-text as a string.
   */
  public String maxLeavesTipText() {
    return "The maximum number of nodes to be added.";
  }

  /**
   * Gets the maximum number of discrete bins to bucket continuous features.
   *
   * @return The maximum number of bins.
   */
  public int getMaxBin() {
    return m_MaxBin;
  }

  /**
   * Sets the maximum number of discrete bins to bucket continuous features.
   *
   * @param value The maximum number of bins.
   */
  public void setMaxBin(int value) {
    m_MaxBin = value;
    reset();
  }

  /**
   * Gets the tip-text for the maxBin option.
   *
   * @return The tip-text as a string.
   */
  public String maxBinTipText() {
    return "The maximum number of discrete bins to bucket continuous features.";
  }

  /**
   * Gets the type of predictor algorithm to use.
   *
   * @return The predictor algorithm.
   */
  public Predictor getPredictor() {
    return m_Predictor;
  }

  /**
   * Sets the type of predictor algorithm to use.
   *
   * @param value The predictor algorithm.
   */
  public void setPredictor(Predictor value) {
    m_Predictor = value;
    reset();
  }

  /**
   * Gets the tip-text for the predictor option.
   *
   * @return The tip-text as a string.
   */
  public String predictorTipText() {
    return "The type of predictor algorithm to use.";
  }

  /**
   * Gets the number of parallel trees constructed during each iteration.
   *
   * @return The number of parallel trees.
   */
  public int getNumberOfParallelTrees() {
    return m_NumberOfParallelTrees;
  }

  /**
   * Sets the number of parallel trees constructed during each iteration.
   *
   * @param value The number of parallel trees.
   */
  public void setNumberOfParallelTrees(int value) {
    m_NumberOfParallelTrees = value;
    reset();
  }

  /**
   * Gets the tip-text for the numberOfParallelTrees option.
   *
   * @return The tip-text as a string.
   */
  public String numberOfParallelTreesTipText() {
    return "The number of parallel trees constructed during each iteration.";
  }

  /*=== Additional Parameters for DART Booster ===*/

  /**
   * Gets the type of sampling algorithm.
   *
   * @return The type of sampling algorithm.
   */
  public SampleType getSampleType() {
    return m_SampleType;
  }

  /**
   * Sets the type of sampling algorithm.
   *
   * @param value The type of sampling algorithm.
   */
  public void setSampleType(SampleType value) {
    m_SampleType = value;
    reset();
  }

  /**
   * Gets the tip-text for the sampleType option.
   *
   * @return The tip-text as a string.
   */
  public String sampleTypeTipText() {
    return "The type of sampling algorithm.";
  }

  /**
   * Gets the type of normalisation algorithm.
   *
   * @return The type of normalisation algorithm.
   */
  public NormaliseType getNormaliseType() {
    return m_NormaliseType;
  }

  /**
   * Sets the type of normalisation algorithm.
   *
   * @param value The type of normalisation algorithm.
   */
  public void setNormaliseType(NormaliseType value) {
    m_NormaliseType = value;
    reset();
  }

  /**
   * Gets the tip-text for the normaliseType option.
   *
   * @return The tip-text as a string.
   */
  public String normaliseTypeTipText() {
    return "The type of normalisation algorithm.";
  }

  /**
   * Gets the dropout rate (a fraction of previous trees to drop during the dropout).
   *
   * @return The dropout rate.
   */
  public float getRateDrop() {
    return m_RateDrop;
  }

  /**
   * Sets the dropout rate (a fraction of previous trees to drop during the dropout).
   *
   * @param value The dropout rate.
   */
  public void setRateDrop(float value) {
    m_RateDrop = value;
    reset();
  }

  /**
   * Gets the tip-text for the rateDrop option.
   *
   * @return The tip-text as a string.
   */
  public String rateDropTipText() {
    return "The dropout rate (a fraction of previous trees to drop during the dropout).";
  }

  /**
   * Sets whether at least one tree is always dropped during the dropout.
   *
   * @return The flag state.
   */
  public boolean getOneDrop() {
    return m_OneDrop;
  }

  /**
   * Sets whether at least one tree is always dropped during the dropout.
   *
   * @param value The flag state.
   */
  public void setOneDrop(boolean value) {
    m_OneDrop = value;
    reset();
  }

  /**
   * Gets the tip-text for the oneDrop option.
   *
   * @return The tip-text as a string.
   */
  public String oneDropTipText() {
    return "Whether at least one tree is always dropped during the dropout.";
  }

  /**
   * Gets the probability of skipping the dropout procedure during a boosting iteration.
   *
   * @return The probability.
   */
  public float getSkipDrop() {
    return m_SkipDrop;
  }

  /**
   * Sets the probability of skipping the dropout procedure during a boosting iteration.
   *
   * @param value The probability.
   */
  public void setSkipDrop(float value) {
    m_SkipDrop = value;
    reset();
  }

  /**
   * Gets the tip-text for the skipDrop option.
   *
   * @return The tip-text as a string.
   */
  public String skipDropTipText() {
    return "The probability of skipping the dropout procedure during a boosting iteration.";
  }

  /*=== Parameters for Linear Booster ===*/

  /**
   * Gets the L2 regularisation term on weights.
   *
   * @return The L2 regularisation term.
   */
  public float getLambda() {
    return m_Lambda;
  }

  /**
   * Sets the L2 regularisation term on weights.
   *
   * @param value The L2 regularisation term.
   */
  public void setLambda(float value) {
    m_Lambda = value;
    reset();
  }

  /**
   * Gets the tip-text for the lambda option.
   *
   * @return The tip-text as a string.
   */
  public String lambdaTipText() {
    return "The L2 regularisation term on weights.";
  }

  /**
   * Gets the L1 regularisation term on weights.
   *
   * @return The L1 regularisation term.
   */
  public float getAlpha() {
    return m_Alpha;
  }

  /**
   * Sets the L1 regularisation term on weights.
   *
   * @param value The L1 regularisation term.
   */
  public void setAlpha(float value) {
    m_Alpha = value;
    reset();
  }

  /**
   * Gets the tip-text for the alpha option.
   *
   * @return The tip-text as a string.
   */
  public String alphaTipText() {
    return "The L1 regularisation term on weights.";
  }

  /**
   * Gets the choice of algorithm to fit the linear model.
   *
   * @return The algorithm.
   */
  public Updater getUpdater() {
    return m_Updater;
  }

  /**
   * Sets the choice of algorithm to fit the linear model.
   *
   * @param value The algorithm.
   */
  public void setUpdater(Updater value) {
    m_Updater = value;
    reset();
  }

  /**
   * Gets the tip-text for the updater option.
   *
   * @return The tip-text as a string.
   */
  public String updaterTipText() {
    return "The choice of algorithm to fit the linear model.";
  }

  /**
   * Gets the feature selection and ordering method.
   *
   * @return The feature selector.
   */
  public FeatureSelector getFeatureSelector() {
    return m_FeatureSelector;
  }

  /**
   * Gets the feature selection and ordering method.
   *
   * @param value The feature selector.
   */
  public void setFeatureSelector(FeatureSelector value) {
    m_FeatureSelector = value;
    reset();
  }

  /**
   * Gets the tip-text for the featureSelector option.
   *
   * @return The tip-text as a string.
   */
  public String featureSelectorTipText() {
    return "The feature selection and ordering method.";
  }

  /**
   * Gets the number of top features to select when using the
   * greedy or thrifty feature selector.
   *
   * @return The number of features to select.
   */
  public int getTopK() {
    return m_TopK;
  }

  /**
   * Sets the number of top features to select when using the
   * greedy or thrifty feature selector.
   *
   * @param value The number of features to select.
   */
  public void setTopK(int value) {
    m_TopK = value;
    reset();
  }

  /**
   * Gets the tip-text for the topK option.
   *
   * @return The tip-text as a string.
   */
  public String topKTipText() {
    return "The number of top features to select when using the " +
      "greedy or thrifty feature selector.";
  }

  /*=== Parameters for Tweedie Regression ===*/

  /**
   * Gets the parameter that controls the variance of the Tweedie distribution.
   *
   * @return The parameter value.
   */
  public float getTweedieVariancePower() {
    return m_TweedieVariancePower;
  }

  /**
   * Sets the parameter that controls the variance of the Tweedie distribution.
   *
   * @param value The parameter value.
   */
  public void setTweedieVariancePower(float value) {
    m_TweedieVariancePower = value;
    reset();
  }

  /**
   * Gets the tip-text for the tweedieVariancePower option.
   *
   * @return The tip-text as a string.
   */
  public String tweedieVariancePowerTipText() {
    return "The parameter that controls the variance of the Tweedie distribution.";
  }

  /*=== Learning Task Parameters ===*/

  /**
   * Gets the learning objective.
   *
   * @return The learning objective.
   */
  public Objective getObjective() {
    return m_Objective;
  }

  /**
   * Sets the learning objective.
   *
   * @param value The learning objective.
   */
  public void setObjective(Objective value) {
    m_Objective = value;
    reset();
  }

  /**
   * Gets the tip-text for the objective option.
   *
   * @return The tip-text as a string.
   */
  public String objectiveTipText() {
    return "The learning objective.";
  }

  /**
   * Gets the initial prediction score of all instances (global bias).
   *
   * @return The global bias.
   */
  public float getBaseScore() {
    return m_BaseScore;
  }

  /**
   * Sets the initial prediction score of all instances (global bias).
   *
   * @param value The global bias.
   */
  public void setBaseScore(float value) {
    m_BaseScore = value;
    reset();
  }

  /**
   * Gets the tip-text for the baseScore option.
   *
   * @return The tip-text as a string.
   */
  public String baseScoreTipText() {
    return "The initial prediction score of all instances (global bias).";
  }

  /**
   * Gets the random number seed.
   *
   * @return The seed value.
   */
  public int getSeed() {
    return m_Seed;
  }

  /**
   * Sets the random number seed.
   *
   * @param value The seed value.
   */
  public void setSeed(int value) {
    m_Seed = value;
    reset();
  }

  /**
   * Gets the tip-text for the seed option.
   *
   * @return The tip-text as a string.
   */
  public String seedTipText() {
    return "The random number seed.";
  }

  /*=== Special-Handling-Required Parameters ===*/

  /**
   * Gets the number of boosting rounds to perform.
   *
   * @return The number of boosting rounds to perform.
   */
  public int getNumberOfRounds() {
    return m_NumberOfRounds;
  }

  /**
   * Sets the number of boosting rounds to perform.
   *
   * @param value The number of boosting rounds to perform.
   */
  public void setNumberOfRounds(int value) {
    m_NumberOfRounds = value;
    reset();
  }

  /**
   * Gets the tip-text for the number of rounds option.
   *
   * @return The tip-text as a string.
   */
  public String numberOfRoundsTipText() {
    return "The number of boosting rounds to perform.";
  }

  /**
   * Gets any other XGBoost parameters the user has set.
   *
   * @return The parameters.
   */
  public BaseKeyValuePair[] getOtherParameters() {
    return m_OtherParameters;
  }

  /**
   * Sets any additional XGBoost parameters.
   *
   * @param value The parameters, as a string of name=value pairs.
   */
  public void setOtherParameters(BaseKeyValuePair[] value) {
    m_OtherParameters = value;
    reset();
  }

  /**
   * Gets the tip-text for the otherParameters option.
   *
   * @return The tip-text as a string.
   */
  public String otherParametersTipText() {
    return "Passes any additional parameters to XGBoost.";
  }

  /**
   * Returns an instance of a TechnicalInformation object, containing
   * detailed information about the technical background of this class,
   * e.g., paper reference or book this class is based on.
   *
   * @return the technical information about this class
   */
  @Override
  public TechnicalInformation getTechnicalInformation() {
    TechnicalInformation result = new TechnicalInformation(Type.INPROCEEDINGS);

    result.setValue(TechnicalInformation.Field.AUTHOR, "Chen, Tianqi and Guestrin, Carlos");
    result.setValue(TechnicalInformation.Field.TITLE, "XGBoost: A Scalable Tree Boosting System");
    result.setValue(TechnicalInformation.Field.BOOKTITLE, "Proceedings of the 22nd ACM SIGKDD International Conference on Knowledge Discovery and Data Mining");
    result.setValue(TechnicalInformation.Field.SERIES, "KDD '16");
    result.setValue(TechnicalInformation.Field.YEAR, "2016");
    result.setValue(TechnicalInformation.Field.ISBN, "978-1-4503-4232-2");
    result.setValue(TechnicalInformation.Field.LOCATION, "San Francisco, California, USA");
    result.setValue(TechnicalInformation.Field.PAGES, "785--794");
    result.setValue(TechnicalInformation.Field.URL, "http://doi.acm.org/10.1145/2939672.2939785");
    result.setValue(TechnicalInformation.Field.PUBLISHER, "ACM");
    result.setValue(TechnicalInformation.Field.ADDRESS, "New York, NY, USA");
    result.setValue(TechnicalInformation.Field.KEYWORDS, "large-scale machine learning");

    return result;
  }

  /**
   * Calculates the number of columns required to represent the attributes
   * of the given dataset when converted to a DMatrix.
   *
   * @param instances The dataset being converted.
   * @return The number of columns required by the converted DMatrix.
   */
  protected int numberOfRequiredDMatrixColumns(Instances instances) {
    // Tally the number of columns
    int nColumns = 0;

    // Remember the class index
    int classIndex = instances.classIndex();

    // Process each attribute
    for (int i = 0; i < instances.numAttributes(); i++) {
      // Get the next attribute
      Attribute attribute = instances.attribute(i);

      // Skip the class attribute
      if (classIndex == i) continue;

      // Date and numeric attributes take one column each
      if (attribute.isNumeric() || attribute.isDate()) {
        nColumns += 1;
      }
      else if (attribute.isNominal()) {
        // One-hot encoding requires one column per nominal value
        nColumns += attribute.numValues();
      }
    }

    return nColumns;
  }

  /**
   * Converts a WEKA dataset into a DMatrix (the input type expected by
   * XGBoost).
   *
   * @param instances The dataset to convert.
   * @return The converted dataset.
   */
  protected DMatrix instancesToDMatrix(Instance[] instances) throws XGBoostError {
    // Get the number of rows and columns we need to create
    int nRows = instances.length;
    int nColumns = numberOfRequiredDMatrixColumns(m_Header);

    // Check we aren't trying to create a zero-area matrix
    if (nRows == 0 || nColumns == 0) return null;

    // Create the data arrays
    float[] data = new float[nRows * nColumns];
    float[] labels = new float[nRows];
    float[] weights = new float[nRows];

    // Remember the class index
    int classIndex = m_Header.classIndex();

    // Keep track of where to insert the next value (contiguous)
    int insertionIndex = 0;

    // Process each row in turn
    for (int rowIndex = 0; rowIndex < nRows; rowIndex++) {
      // Get the instance for this row
      Instance instance = instances[rowIndex];

      // Extract the raw values
      double[] instanceData = instance.toDoubleArray();

      // Save the weighting for this row
      weights[rowIndex] = (float) instance.weight();

      // Save the class value for this row
      if (instance.classIsMissing())
        labels[rowIndex] = 0.0f;  // XGBoost can't handle NaN
      else
        labels[rowIndex] = (float) instanceData[classIndex];

      // Extract the instance data into the DMatrix array
      for (int i = 0; i < instanceData.length; i++) {
        // Get the attribute for this column
        Attribute attribute = m_Header.attribute(i);

        // Skip the class index
        if (i == classIndex) continue;

        // Insert the data
        if (attribute.isDate() || attribute.isNumeric()) {
          data[insertionIndex] = (float) instanceData[i];
          insertionIndex++;
        }
        else if (attribute.isNominal()) {
          // One-hot encoding
          data[insertionIndex + ((int) instanceData[i])] = 1.0f;
          insertionIndex += attribute.numValues();
        }
      }
    }

    // Create the DMatrix object from the extracted data
    DMatrix dMatrix = new DMatrix(data, nRows, nColumns, (float) Utils.missingValue());
    dMatrix.setLabel(labels);
    dMatrix.setWeight(weights);

    return dMatrix;
  }

  /**
   * Converts the options into a parameter map as expected by XGBoost.
   *
   * @return The parameter map.
   */
  protected Map<String, Object> createParamsFromOptions() {
    // Create the parameter map
    Map<String, Object> params = new HashMap<>();

    // Process the additional parameters string first, so option
    // parameters can overwrite them
    params.putAll(BaseKeyValuePair.toMap(getOtherParameters()));

    // Add any non-default options as parameters
    for (Field field : getClass().getDeclaredFields()) {
      // If a field has an XGBoostParameter annotation, process it for a parameter
      XGBoostParameter param = field.getAnnotation(XGBoostParameter.class);
      if (param == null) continue;

      // Get the option that corresponds to this parameter
      AbstractOption paramOption = m_OptionManager.findByFlag(param.value());
      if (paramOption == null) continue;

      // Can skip any options that are still default valued
      if (m_OptionManager.isDefaultValueByFlag(param.value())) continue;

      // Get the (non-default) value of the option
      Object optionValue = paramOption.getCurrentValue();

      // Convert it to its parameter form if it provides one,
      // otherwise just use the raw value itself
      if (optionValue instanceof ParamValueProvider) {
        optionValue = ((ParamValueProvider) optionValue).paramValue();
      }
      else if (optionValue instanceof Enum) {
        optionValue = ((Enum) optionValue).name().toLowerCase();
      }

      // Add the parameter to the map
      params.put(param.value(), optionValue);
    }

    // Return the map of parameters
    return params;
  }

  /**
   * Returns the Capabilities of this classifier. Maximally permissive
   * capabilities are allowed by default. Derived classifiers should override
   * this method and first disable all capabilities and then enable just those
   * capabilities that make sense for the scheme.
   *
   * @return the capabilities of this object
   * @see Capabilities
   */
  @Override
  public Capabilities getCapabilities() {
    // Remove all capabilities
    Capabilities capabilities = super.getCapabilities();
    capabilities.disableAll();

    // Add the capabilities XGBoost supports
    capabilities.enable(Capability.NUMERIC_CLASS);
    capabilities.enable(Capability.NUMERIC_ATTRIBUTES);
    capabilities.enable(Capability.NOMINAL_CLASS);
    capabilities.enable(Capability.NOMINAL_ATTRIBUTES);
    capabilities.enable(Capability.DATE_ATTRIBUTES);
    capabilities.enable(Capability.DATE_CLASS);

    capabilities.setMinimumNumberInstances(1);

    return capabilities;
  }

  /**
   * Trains the XGBoost classifier on the incoming dataset.
   *
   * @param instances The training dataset.
   * @throws Exception Any internal XGBoost error.
   */
  @Override
  public void buildClassifier(Instances instances) throws Exception {
    getCapabilities().test(instances);

    if (OS.isLinux()) {
      if (LDD.compareTo(MIN_GLIBC_VERSION) < 0) {
        throw new Exception(
            "XGBoost requires a minimum glibc version of " + adams.core.Utils.flatten(MIN_GLIBC_VERSION, ".")
                + " but found only " + adams.core.Utils.flatten(LDD.version(), ".") + "!");
      }
    }

    m_Header = new Instances(instances, 0);

    // Convert the training dataset to the required form
    DMatrix train = instancesToDMatrix(instances.toArray(new Instance[0]));

    // Abort if we can't create the training set
    if (train == null) {
      m_Booster = null;
      return;
    }

    // Setup the parameters for XGBoost
    m_Params = createParamsFromOptions();
    if (isLoggingEnabled())
      getLogger().info("XGBoost parameters: " + m_Params);

    // Add a watch on the the training set (unless in silent mode)
    Map<String, DMatrix> watches = new HashMap<>();
    if (getVerbosity() != Verbosity.SILENT) watches.put("train", train);

    // Train the classifier
    m_Booster = ml.dmlc.xgboost4j.java.XGBoost.train(train, m_Params, m_NumberOfRounds, watches, null, null);
  }

  /**
   * Classifies the given test instance. The instance has to belong to a dataset
   * when it's being classified. Note that a classifier MUST implement either
   * this or distributionForInstance().
   *
   * @param instance the instance to be classified
   * @return the predicted most likely class for the instance or
   * Utils.missingValue() if no prediction is made
   * @throws Exception if an error occurred during the prediction
   */
  @Override
  public double classifyInstance(Instance instance) throws Exception {
    // Make sure we have built the classifier and have data
    // to classify
    if (m_Booster == null || instance == null) return Utils.missingValue();

    // Convert the test instance to the required format
    DMatrix testData = instancesToDMatrix(new Instance[]{instance});

    // Abort if we can't create the test data
    if (testData == null) return Utils.missingValue();

    // Get XGBoost's prediction for the test data
    float[][] predictions = m_Booster.predict(testData);

    // Only one instance with one class, so only one prediction
    if (instance.classAttribute().isNumeric())
      return predictions[0][0];
    else
      return Math.round(predictions[0][0]);
  }

  /**
   * Returns a description of this classifier.
   *
   * @return a description of this classifier as a string.
   */
  @Override
  public String toString() {
    StringBuilder	result;

    result = new StringBuilder("XGBoost\n=======\n\n");
    result.append("Parameters: ");
    if (m_Params != null)
      result.append(m_Params.toString());
    else
      result.append("No model built yet");

    return result.toString();
  }

  /**
   * Main method for running this class.
   *
   * @param args the options
   */
  public static void main(String[] args) {
    runClassifier(new XGBoost(), args);
  }
}