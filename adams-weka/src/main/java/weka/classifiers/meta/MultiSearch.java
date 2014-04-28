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
 * MultiSearch.java
 * Copyright (C) 2008-2010 University of Waikato, Hamilton, New Zealand
 */

package weka.classifiers.meta;

import java.io.File;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.RandomizableSingleClassifierEnhancer;
import weka.classifiers.functions.LinearRegression;
import weka.classifiers.meta.multisearch.Performance;
import weka.classifiers.meta.multisearch.PerformanceCache;
import weka.classifiers.meta.multisearch.PerformanceComparator;
import weka.core.AdditionalMeasureProducer;
import weka.core.Capabilities;
import weka.core.Debug;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.RevisionUtils;
import weka.core.SelectedTag;
import weka.core.SerializedObject;
import weka.core.SetupGenerator;
import weka.core.Summarizable;
import weka.core.Tag;
import weka.core.Utils;
import weka.core.WekaException;
import weka.core.Capabilities.Capability;
import weka.core.setupgenerator.AbstractParameter;
import weka.core.setupgenerator.MathParameter;
import weka.core.setupgenerator.Point;
import weka.core.setupgenerator.Space;
import weka.filters.Filter;
import weka.filters.supervised.attribute.PLSFilter;
import weka.filters.unsupervised.instance.Resample;
import adams.core.option.OptionUtils;

/**
 <!-- globalinfo-start -->
 * Performs a search of an arbitrary number of parameters of a classifier and chooses the best pair found for the actual filtering and training.<br/>
 * The default MultiSearch is using the following FilteredClassifier setup:<br/>
 *  - classifier: LinearRegression, searching for the "Ridge"<br/>
 *  - filter: PLSFilter, searching for the "# of Components"<br/>
 * The properties being explored are totally up to the user, it can be a mix of classifier and filter properties, or only classifier ones or only filter ones.<br/>
 * <br/>
 * Since the the MultiSearch classifier itself is used as the base object for the setups being generated, one has to prefix the properties with 'classifier.' (referring to MultiSearch's 'classifier' property).<br/>
 * E.g., if you have a FilteredClassifier selected as base classifier, sporting a PLSFilter and you want to explore the number of PLS components, then your property will be made up of the following components:<br/>
 *  - classifier: referring to MultiSearch's classifier property<br/>
 *    i.e., the FilteredClassifier.<br/>
 *  - filter: referring to the FilteredClassifier's property (= PLSFilter)<br/>
 *  - numComponents: the actual property of the PLSFilter that we want to modify<br/>
 * And assembled, the property looks like this:<br/>
 *   classifier.filter.numComponents<br/>
 * <br/>
 * The initial space is worked on with 2-fold CV to determine the values of the parameters for the selected type of evaluation (e.g., accuracy). The best point in the space is then taken as center and a 10-fold CV is performed with the adjacent parameters. If better parameters are found, then this will act as new center and another 10-fold CV will be performed (kind of hill-climbing). This process is repeated until no better pair is found or the best pair is on the border of the parameter space.<br/>
 * The number of CV-folds for the initial and subsequent spaces can be adjusted, of course.<br/>
 * <br/>
 * The outcome of a mathematical function (= double), MultiSearch will convert to integers (values are just cast to int), booleans (0 is false, otherwise true), float, char and long if necessary.<br/>
 * Via a user-supplied 'list' of parameters (blank-separated), one can also set strings and selected tags (drop-down comboboxes in Weka's GenericObjectEditor). Classnames with options (e.g., classifiers with their options) are possible as well.<br/>
 * <br/>
 * The best classifier setup can be accessed after the buildClassifier call via the getBestClassifier method.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre> -E &lt;CC|RMSE|RRSE|MAE|RAE|COMB|ACC|KAP&gt;
 *  Determines the parameter used for evaluation:
 *  CC = Correlation coefficient
 *  RMSE = Root mean squared error
 *  RRSE = Root relative squared error
 *  MAE = Mean absolute error
 *  RAE = Root absolute error
 *  COMB = Combined = (1-abs(CC)) + RRSE + RAE
 *  ACC = Accuracy
 *  KAP = Kappa
 *  (default: CC)</pre>
 *
 * <pre> -search "&lt;classname options&gt;"
 *  A property search setup.
 * </pre>
 *
 * <pre> -sample-size &lt;num&gt;
 *  The size (in percent) of the sample to search the inital space with.
 *  (default: 100)</pre>
 *
 * <pre> -log-file &lt;filename&gt;
 *  The log file to log the messages to.
 *  (default: none)</pre>
 *
 * <pre> -initial-folds &lt;num&gt;
 *  The number of cross-validation folds for the initial space.
 *  Numbers smaller than 2 turn off cross-validation and just
 *  perform evaluation on the training set.
 *  (default: 2)</pre>
 *
 * <pre> -subsequent-folds &lt;num&gt;
 *  The number of cross-validation folds for the subsequent sub-spaces.
 *  Numbers smaller than 2 turn off cross-validation and just
 *  perform evaluation on the training set.
 *  (default: 10)</pre>
 *
 * <pre> -num-slots &lt;num&gt;
 *  Number of execution slots.
 *  (default 1 - i.e. no parallelism)</pre>
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
 *  (default: weka.classifiers.meta.FilteredClassifier)</pre>
 *
 * <pre>
 * Options specific to classifier weka.classifiers.meta.FilteredClassifier:
 * </pre>
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
 * Options specific to classifier weka.classifiers.functions.LinearRegression:
 * </pre>
 *
 * <pre> -D
 *  Produce debugging output.
 *  (default no debugging output)</pre>
 *
 * <pre> -S &lt;number of selection method&gt;
 *  Set the attribute selection method to use. 1 = None, 2 = Greedy.
 *  (default 0 = M5' method)</pre>
 *
 * <pre> -C
 *  Do not try to eliminate colinear attributes.
 * </pre>
 *
 * <pre> -R &lt;double&gt;
 *  Set ridge parameter (default 1.0e-8).
 * </pre>
 *
 <!-- options-end -->
 *
 * General notes:
 * <ul>
 *   <li>Turn the <i>debug</i> flag on in order to see some progress output in the
 *       console</li>
 * </ul>
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MultiSearch
  extends RandomizableSingleClassifierEnhancer
  implements AdditionalMeasureProducer, Summarizable {

  /** for serialization. */
  private static final long serialVersionUID = -5129316523575906233L;

  /**
   * Helper class for evaluating a setup.
   */
  protected static class EvaluationTask
    implements Runnable {

    /** the owner. */
    protected MultiSearch m_Owner;

    /** the data to use for training. */
    protected Instances m_Data;

    /** the setup generator to use. */
    protected SetupGenerator m_Generator;

    /** the setup. */
    protected Point<Object> m_Values;

    /** the number of folds for cross-validation. */
    protected int m_Folds;

    /** the type of evaluation. */
    protected int m_Evaluation = Performance.EVALUATION_CC;

    /**
     * Initializes the task.
     *
     * @param owner		the owning MultiSearch classifier
     * @param inst		the data
     * @param generator		the generator to use
     * @param values		the setup values
     * @param folds		the number of cross-validation folds
     * @param eval		the type of evaluation
     */
    public EvaluationTask(MultiSearch owner, Instances inst,
        SetupGenerator generator, Point<Object> values, int folds, int eval) {

      super();

      m_Owner      = owner;
      m_Data       = inst;
      m_Generator  = generator;
      m_Values     = values;
      m_Folds      = folds;
      m_Evaluation = eval;
    }

    /**
     * Performs the evaluation.
     */
    public void run() {
      Point<Object>	evals;
      Evaluation	eval;
      Classifier	classifier;
      MultiSearch	multi;
      Performance	performance;
      boolean		completed;

      try {
        // setup
        evals      = m_Generator.evaluate(m_Values);
        multi      = (MultiSearch) m_Generator.setup(m_Owner, evals);
        classifier = multi.getClassifier();

        // evaluate
        try {
          eval = new Evaluation(m_Data);
          eval.setDiscardPredictions(true);
          if (m_Folds >= 2) {
            eval.crossValidateModel(classifier, m_Data, m_Folds, new Random(m_Owner.getSeed()));
          }
          else {
            classifier.buildClassifier(m_Data);
            eval.evaluateModel(classifier, m_Data);
          }
          completed = true;
        }
        catch (Exception e) {
          eval = null;
          System.err.println("Encountered exception while evaluating classifier, skipping!");
          System.err.println("- Classifier: " + m_Owner.getCommandline(classifier));
          e.printStackTrace();
          completed = false;
        }

        // store performance
        performance = new Performance(m_Values, eval, m_Evaluation);
        m_Owner.addPerformance(performance, m_Folds);

        // log
        m_Owner.log(performance + ": cached=false");

        // release slot
        m_Owner.completedEvaluation(classifier, completed);
      }
      catch (Exception e) {
        System.err.println("Encountered exception while evaluating classifier, skipping!");
        System.err.println("- Values: " + m_Values);
        e.printStackTrace();
        m_Owner.completedEvaluation(m_Values, false);
      }

      // clean up
      m_Owner     = null;
      m_Data      = null;
      m_Generator = null;
      m_Values    = null;
    }
  }

  /** evaluation. */
  public static final Tag[] TAGS_EVALUATION = {
    new Tag(Performance.EVALUATION_CC, "CC", "Correlation coefficient"),
    new Tag(Performance.EVALUATION_RMSE, "RMSE", "Root mean squared error"),
    new Tag(Performance.EVALUATION_RRSE, "RRSE", "Root relative squared error"),
    new Tag(Performance.EVALUATION_MAE, "MAE", "Mean absolute error"),
    new Tag(Performance.EVALUATION_RAE, "RAE", "Root absolute error"),
    new Tag(Performance.EVALUATION_COMBINED, "COMB", "Combined = (1-abs(CC)) + RRSE + RAE"),
    new Tag(Performance.EVALUATION_ACC, "ACC", "Accuracy"),
    new Tag(Performance.EVALUATION_KAPPA, "KAP", "Kappa")
  };

  /** the Classifier with the best setup. */
  protected Classifier m_BestClassifier;

  /** the best values. */
  protected Point<Object> m_Values = null;

  /** the type of evaluation. */
  protected int m_Evaluation = Performance.EVALUATION_CC;

  /** for generating the search parameters. */
  protected SetupGenerator m_Generator;

  /** the sample size to search the initial space with. */
  protected double m_SampleSize = 100;

  /** the log file to use. */
  protected File m_LogFile = new File(System.getProperty("user.dir"));

  /** the parameter space. */
  protected Space m_Space;

  /** the cache for points in the space that got calculated
   * (raw points in space, not evaluated ones!). */
  protected PerformanceCache m_Cache;

  /** whether all performances in the space are the same. */
  protected boolean m_UniformPerformance = false;

  /** the filtered classifier to use, in case a filter is used. */
  protected FilteredClassifier m_FilteredClassifier;

  /** the default parameters. */
  protected AbstractParameter[] m_DefaultParameters;

  /** number of cross-validation folds in the initial space. */
  protected int m_InitialSpaceNumFolds = 2;

  /** number of cross-validation folds in the subsequent spaces. */
  protected int m_SubsequentSpaceNumFolds = 10;

  /** The number of threads to have executing at any one time. */
  protected int m_NumExecutionSlots = 1;

  /** Pool of threads to train models with. */
  protected transient ThreadPoolExecutor m_ExecutorPool;

  /** The number of setups completed so far. */
  protected int m_Completed;

  /** The number of setups that experienced a failure of some sort
   * during construction. */
  protected int m_Failed;

  /** the number of setups to evaluate. */
  protected int m_NumSetups;

  /** for storing the performances. */
  protected Vector<Performance> m_Performances;

  /**
   * the default constructor.
   */
  public MultiSearch() {
    super();

    m_Generator = new SetupGenerator();

    // classifier
    LinearRegression classifier = new LinearRegression();
    classifier.setAttributeSelectionMethod(new SelectedTag(LinearRegression.SELECTION_NONE, LinearRegression.TAGS_SELECTION));
    classifier.setEliminateColinearAttributes(false);

    // filter
    PLSFilter filter = new PLSFilter();
    filter.setPreprocessing(new SelectedTag(PLSFilter.PREPROCESSING_STANDARDIZE, PLSFilter.TAGS_PREPROCESSING));
    filter.setReplaceMissing(true);

    m_Classifier = new FilteredClassifier();
    ((FilteredClassifier) m_Classifier).setFilter(filter);
    ((FilteredClassifier) m_Classifier).setClassifier(classifier);

    // search parameters
    AbstractParameter[] params = new AbstractParameter[2];

    MathParameter param = new MathParameter();
    param.setProperty("classifier.classifier.ridge");
    param.setMin(-10);
    param.setMax(+5);
    param.setStep(1);
    param.setBase(10);
    param.setExpression("pow(BASE,I)");
    params[0] = param;

    param = new MathParameter();
    param.setProperty("classifier.filter.numComponents");
    param.setMin(+5);
    param.setMax(+15);
    param.setStep(1);
    param.setBase(10);
    param.setExpression("I");
    params[1] = param;

    try {
      m_DefaultParameters = (AbstractParameter[]) new SerializedObject(params).getObject();
    }
    catch (Exception e) {
      e.printStackTrace();
    }

    m_Generator.setBaseObject(this);
    m_Generator.setParameters(params);

    try {
      m_BestClassifier = AbstractClassifier.makeCopy(m_Classifier);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Returns a string describing classifier.
   *
   * @return a description suitable for displaying in the
   *         explorer/experimenter gui
   */
  public String globalInfo() {
    return
        "Performs a search of an arbitrary number of parameters of a classifier "
      + "and chooses the best pair found for the actual filtering and training.\n"
      + "The default MultiSearch is using the following FilteredClassifier setup:\n"
      + " - classifier: LinearRegression, searching for the \"Ridge\"\n"
      + " - filter: PLSFilter, searching for the \"# of Components\"\n"
      + "The properties being explored are totally up to the user, it can be a "
      + "mix of classifier and filter properties, or only classifier ones or "
      + "only filter ones.\n"
      + "\n"
      + "Since the the MultiSearch classifier itself is used as the base object "
      + "for the setups being generated, one has to prefix the properties with "
      + "'classifier.' (referring to MultiSearch's 'classifier' property).\n"
      + "E.g., if you have a FilteredClassifier selected as base classifier, "
      + "sporting a PLSFilter and you want to explore the number of PLS components, "
      + "then your property will be made up of the following components:\n"
      + " - classifier: referring to MultiSearch's classifier property\n"
      + "   i.e., the FilteredClassifier.\n"
      + " - filter: referring to the FilteredClassifier's property (= PLSFilter)\n"
      + " - numComponents: the actual property of the PLSFilter that we want to modify\n"
      + "And assembled, the property looks like this:\n"
      + "  classifier.filter.numComponents\n"
      + "\n"
      + "The initial space is worked on with 2-fold CV to determine the values "
      + "of the parameters for the selected type of evaluation (e.g., "
      + "accuracy). The best point in the space is then taken as center and a "
      + "10-fold CV is performed with the adjacent parameters. If better parameters "
      + "are found, then this will act as new center and another 10-fold CV will "
      + "be performed (kind of hill-climbing). This process is repeated until "
      + "no better pair is found or the best pair is on the border of the parameter "
      + "space.\n"
      + "The number of CV-folds for the initial and subsequent spaces can be "
      + "adjusted, of course.\n"
      + "\n"
      + "The outcome of a mathematical function (= double), MultiSearch will convert "
      + "to integers (values are just cast to int), booleans (0 is false, otherwise "
      + "true), float, char and long if necessary.\n"
      + "Via a user-supplied 'list' of parameters (blank-separated), one can also "
      + "set strings and selected tags (drop-down comboboxes in Weka's "
      + "GenericObjectEditor). Classnames with options (e.g., classifiers with "
      + "their options) are possible as well.\n"
      + "\n"
      + "The best classifier setup can be accessed after the buildClassifier "
      + "call via the getBestClassifier method.";
  }

  /**
   * String describing default classifier.
   *
   * @return		the classname of the default classifier
   */
  protected String defaultClassifierString() {
    return FilteredClassifier.class.getName();
  }

  /**
   * Gets an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  public Enumeration listOptions() {
    Vector        	result;
    Enumeration   	en;
    String		desc;
    SelectedTag		tag;
    int			i;

    result = new Vector();

    desc  = "";
    for (i = 0; i < TAGS_EVALUATION.length; i++) {
      tag = new SelectedTag(TAGS_EVALUATION[i].getID(), TAGS_EVALUATION);
      desc  +=   "\t" + tag.getSelectedTag().getIDStr()
      	       + " = " + tag.getSelectedTag().getReadable()
      	       + "\n";
    }
    result.addElement(new Option(
	"\tDetermines the parameter used for evaluation:\n"
	+ desc
	+ "\t(default: " + new SelectedTag(Performance.EVALUATION_CC, TAGS_EVALUATION) + ")",
	"E", 1, "-E " + Tag.toOptionList(TAGS_EVALUATION)));

    result.addElement(new Option(
	"\tA property search setup.\n",
	"search", 1, "-search \"<classname options>\""));

    result.addElement(new Option(
	"\tThe size (in percent) of the sample to search the inital space with.\n"
	+ "\t(default: 100)",
	"sample-size", 1, "-sample-size <num>"));

    result.addElement(new Option(
	"\tThe log file to log the messages to.\n"
	+ "\t(default: none)",
	"log-file", 1, "-log-file <filename>"));

    result.addElement(new Option(
	"\tThe number of cross-validation folds for the initial space.\n"
	+ "\tNumbers smaller than 2 turn off cross-validation and just\n"
	+ "\tperform evaluation on the training set.\n"
	+ "\t(default: 2)",
	"initial-folds", 1, "-initial-folds <num>"));

    result.addElement(new Option(
	"\tThe number of cross-validation folds for the subsequent sub-spaces.\n"
	+ "\tNumbers smaller than 2 turn off cross-validation and just\n"
	+ "\tperform evaluation on the training set.\n"
	+ "\t(default: 10)",
	"subsequent-folds", 1, "-subsequent-folds <num>"));

    result.addElement(new Option(
        "\tNumber of execution slots.\n"
        + "\t(default 1 - i.e. no parallelism)",
        "num-slots", 1, "-num-slots <num>"));

    en = super.listOptions();
    while (en.hasMoreElements())
      result.addElement(en.nextElement());

    return result.elements();
  }

  /**
   * returns the options of the current setup.
   *
   * @return		the current options
   */
  public String[] getOptions() {
    int       		i;
    Vector<String>    	result;
    String[]  		options;

    result = new Vector<String>();

    result.add("-E");
    result.add("" + getEvaluation());

    for (i = 0; i < m_Generator.getParameters().length; i++) {
      result.add("-search");
      result.add(getCommandline(m_Generator.getParameters()[i]));
    }

    result.add("-sample-size");
    result.add("" + getSampleSizePercent());

    result.add("-log-file");
    result.add("" + getLogFile());

    result.add("-initial-folds");
    result.add("" + getInitialSpaceNumFolds());

    result.add("-subsequent-folds");
    result.add("" + getSubsequentSpaceNumFolds());

    result.add("-num-slots");
    result.add("" + getNumExecutionSlots());

    options = super.getOptions();
    for (i = 0; i < options.length; i++)
      result.add(options[i]);

    return result.toArray(new String[result.size()]);
  }

  /**
   * Parses the options for this object. <p/>
   *
   <!-- options-start -->
   * Valid options are: <p/>
   *
   * <pre> -E &lt;CC|RMSE|RRSE|MAE|RAE|COMB|ACC|KAP&gt;
   *  Determines the parameter used for evaluation:
   *  CC = Correlation coefficient
   *  RMSE = Root mean squared error
   *  RRSE = Root relative squared error
   *  MAE = Mean absolute error
   *  RAE = Root absolute error
   *  COMB = Combined = (1-abs(CC)) + RRSE + RAE
   *  ACC = Accuracy
   *  KAP = Kappa
   *  (default: CC)</pre>
   *
   * <pre> -search "&lt;classname options&gt;"
   *  A property search setup.
   * </pre>
   *
   * <pre> -sample-size &lt;num&gt;
   *  The size (in percent) of the sample to search the inital space with.
   *  (default: 100)</pre>
   *
   * <pre> -log-file &lt;filename&gt;
   *  The log file to log the messages to.
   *  (default: none)</pre>
   *
   * <pre> -initial-folds &lt;num&gt;
   *  The number of cross-validation folds for the initial space.
   *  Numbers smaller than 2 turn off cross-validation and just
   *  perform evaluation on the training set.
   *  (default: 2)</pre>
   *
   * <pre> -subsequent-folds &lt;num&gt;
   *  The number of cross-validation folds for the subsequent sub-spaces.
   *  Numbers smaller than 2 turn off cross-validation and just
   *  perform evaluation on the training set.
   *  (default: 10)</pre>
   *
   * <pre> -num-slots &lt;num&gt;
   *  Number of execution slots.
   *  (default 1 - i.e. no parallelism)</pre>
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
   *  (default: weka.classifiers.meta.FilteredClassifier)</pre>
   *
   * <pre>
   * Options specific to classifier weka.classifiers.meta.FilteredClassifier:
   * </pre>
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
   * Options specific to classifier weka.classifiers.functions.LinearRegression:
   * </pre>
   *
   * <pre> -D
   *  Produce debugging output.
   *  (default no debugging output)</pre>
   *
   * <pre> -S &lt;number of selection method&gt;
   *  Set the attribute selection method to use. 1 = None, 2 = Greedy.
   *  (default 0 = M5' method)</pre>
   *
   * <pre> -C
   *  Do not try to eliminate colinear attributes.
   * </pre>
   *
   * <pre> -R &lt;double&gt;
   *  Set ridge parameter (default 1.0e-8).
   * </pre>
   *
   <!-- options-end -->
   *
   * @param options	the options to use
   * @throws Exception	if setting of options fails
   */
  public void setOptions(String[] options) throws Exception {
    String		tmpStr;
    String[]		tmpOptions;
    Vector<String>	search;
    int			i;
    AbstractParameter[]	params;

    tmpStr = Utils.getOption('E', options);
    if (tmpStr.length() != 0)
      setEvaluation(new SelectedTag(tmpStr, TAGS_EVALUATION));
    else
      setEvaluation(new SelectedTag(Performance.EVALUATION_CC, TAGS_EVALUATION));

    search = new Vector<String>();
    do {
      tmpStr = Utils.getOption("search", options);
      if (tmpStr.length() > 0)
	search.add(tmpStr);
    }
    while (tmpStr.length() > 0);
    if (search.size() == 0) {
      for (i = 0; i < m_DefaultParameters.length; i++)
	search.add(getCommandline(m_DefaultParameters[i]));
    }
    params = new AbstractParameter[search.size()];
    for (i = 0; i < search.size(); i++) {
      tmpOptions    = Utils.splitOptions(search.get(i));
      tmpStr        = tmpOptions[0];
      tmpOptions[0] = "";
      params[i]     = (AbstractParameter) OptionUtils.forName(AbstractParameter.class, tmpStr, tmpOptions);
    }
    m_Generator.setParameters(params);

    tmpStr = Utils.getOption("sample-size", options);
    if (tmpStr.length() != 0)
      setSampleSizePercent(Double.parseDouble(tmpStr));
    else
      setSampleSizePercent(100);

    tmpStr = Utils.getOption("log-file", options);
    if (tmpStr.length() != 0)
      setLogFile(new File(tmpStr));
    else
      setLogFile(new File(System.getProperty("user.dir")));

    tmpStr = Utils.getOption("initial-folds", options);
    if (tmpStr.length() != 0)
      setInitialSpaceNumFolds(Integer.parseInt(tmpStr));
    else
      setInitialSpaceNumFolds(2);

    tmpStr = Utils.getOption("subsequent-folds", options);
    if (tmpStr.length() != 0)
      setSubsequentSpaceNumFolds(Integer.parseInt(tmpStr));
    else
      setSubsequentSpaceNumFolds(10);

    tmpStr = Utils.getOption("num-slots", options);
    if (tmpStr.length() != 0)
      setNumExecutionSlots(Integer.parseInt(tmpStr));
    else
      setNumExecutionSlots(1);

    super.setOptions(options);
  }

  /**
   * Set the base learner.
   *
   * @param newClassifier 	the classifier to use.
   */
  public void setClassifier(Classifier newClassifier) {
    boolean	numeric;
    boolean	nominal;

    Capabilities cap = newClassifier.getCapabilities();

    numeric =    cap.handles(Capability.NUMERIC_CLASS)
    	      || cap.hasDependency(Capability.NUMERIC_CLASS);

    nominal =    cap.handles(Capability.NOMINAL_CLASS)
              || cap.hasDependency(Capability.NOMINAL_CLASS)
              || cap.handles(Capability.BINARY_CLASS)
              || cap.hasDependency(Capability.BINARY_CLASS)
              || cap.handles(Capability.UNARY_CLASS)
              || cap.hasDependency(Capability.UNARY_CLASS);

    if ((m_Evaluation == Performance.EVALUATION_CC) && !numeric)
      throw new IllegalArgumentException(
	  "Classifier needs to handle numeric class for chosen type of evaluation!");

    if (((m_Evaluation == Performance.EVALUATION_ACC) || (m_Evaluation == Performance.EVALUATION_KAPPA)) && !nominal)
      throw new IllegalArgumentException(
	  "Classifier needs to handle nominal class for chosen type of evaluation!");

    super.setClassifier(newClassifier);

    try {
      m_BestClassifier = AbstractClassifier.makeCopy(m_Classifier);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String searchParametersTipText() {
    return "Defines the search parameters.";
  }

  /**
   * Sets the search parameters.
   *
   * @param value	the parameters
   */
  public void setSearchParameters(AbstractParameter[] value) {
    m_Generator.setParameters(value.clone());
  }

  /**
   * Returns the search parameters.
   *
   * @return		the parameters
   */
  public AbstractParameter[] getSearchParameters() {
    return m_Generator.getParameters();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String evaluationTipText() {
    return
        "Sets the criterion for evaluating the classifier performance and "
      + "choosing the best one.";
  }

  /**
   * Sets the criterion to use for evaluating the classifier performance.
   *
   * @param value 	.the evaluation criterion
   */
  public void setEvaluation(SelectedTag value) {
    if (value.getTags() == TAGS_EVALUATION) {
      m_Evaluation = value.getSelectedTag().getID();
    }
  }

  /**
   * Gets the criterion used for evaluating the classifier performance.
   *
   * @return 		the current evaluation criterion.
   */
  public SelectedTag getEvaluation() {
    return new SelectedTag(m_Evaluation, TAGS_EVALUATION);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String sampleSizePercentTipText() {
    return "The sample size (in percent) to use in the initial space search.";
  }

  /**
   * Gets the sample size for the initial space search.
   *
   * @return the sample size.
   */
  public double getSampleSizePercent() {
    return m_SampleSize;
  }

  /**
   * Sets the sample size for the initial space search.
   *
   * @param value the sample size for the initial space search.
   */
  public void setSampleSizePercent(double value) {
    m_SampleSize = value;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String logFileTipText() {
    return "The log file to log the messages to.";
  }

  /**
   * Gets current log file.
   *
   * @return 		the log file.
   */
  public File getLogFile() {
    return m_LogFile;
  }

  /**
   * Sets the log file to use.
   *
   * @param value 	the log file.
   */
  public void setLogFile(File value) {
    m_LogFile = value;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String initialSpaceNumFoldsTipText() {
    return
        "The number of cross-validation folds when evaluating the initial "
      + "space; values smaller than 2 turn cross-validation off and simple "
      + "evaluation on the training set is performed.";
  }

  /**
   * Gets the number of CV folds for the initial space.
   *
   * @return the number of folds.
   */
  public int getInitialSpaceNumFolds() {
    return m_InitialSpaceNumFolds;
  }

  /**
   * Sets the number of CV folds for the initial space.
   *
   * @param value the number of folds.
   */
  public void setInitialSpaceNumFolds(int value) {
    m_InitialSpaceNumFolds = value;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String subsequentSpaceNumFoldsTipText() {
    return
        "The number of cross-validation folds when evaluating the subsequent "
      + "sub-spaces; values smaller than 2 turn cross-validation off and simple "
      + "evaluation on the training set is performed.";
  }

  /**
   * Gets the number of CV folds for the sub-sequent sub-spaces.
   *
   * @return the number of folds.
   */
  public int getSubsequentSpaceNumFolds() {
    return m_SubsequentSpaceNumFolds;
  }

  /**
   * Sets the number of CV folds for the sub-sequent sub-spaces.
   *
   * @param value the number of folds.
   */
  public void setSubsequentSpaceNumFolds(int value) {
    m_SubsequentSpaceNumFolds = value;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String numExecutionSlotsTipText() {
    return "The number of execution slots (threads) to use for " +
      "constructing the ensemble.";
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
   * returns the best Classifier setup.
   *
   * @return		the best Classifier setup
   */
  public Classifier getBestClassifier() {
    return m_BestClassifier;
  }

  /**
   * Returns an enumeration of the measure names.
   *
   * @return an enumeration of the measure names
   */
  public Enumeration enumerateMeasures() {
    Vector	result;
    int		i;

    result = new Vector();

    for (i = 0; i < m_Values.dimensions(); i++) {
      if (m_Values.getValue(i) instanceof Double)
	result.add("measure-" + i);
    }

    return result.elements();
  }

  /**
   * Returns the value of the named measure.
   *
   * @param measureName the name of the measure to query for its value
   * @return the value of the named measure
   */
  public double getMeasure(String measureName) {
    if (measureName.startsWith("measure-"))
      return (Double) m_Generator.evaluate(getValues()).getValue(Integer.parseInt(measureName.replace("measure-", "")));
    else
      throw new IllegalArgumentException("Measure '" + measureName + "' not supported!");
  }

  /**
   * returns the parameter values that were found to work best.
   *
   * @return		the best parameter combination
   */
  public Point<Object> getValues() {
    return m_Values;
  }

  /**
   * Returns default capabilities of the classifier.
   *
   * @return		the capabilities of this classifier
   */
  public Capabilities getCapabilities() {
    Capabilities 	result;
    Capabilities	classes;
    Iterator		iter;
    Capability		capab;

    result = super.getCapabilities();

    // only nominal and numeric classes allowed
    classes = result.getClassCapabilities();
    iter = classes.capabilities();
    while (iter.hasNext()) {
      capab = (Capability) iter.next();
      if (    (capab != Capability.BINARY_CLASS)
	   && (capab != Capability.NOMINAL_CLASS)
	   && (capab != Capability.NUMERIC_CLASS)
	   && (capab != Capability.DATE_CLASS) )
	result.disable(capab);
    }

    // set dependencies
    for (Capability cap: Capability.values())
      result.enableDependency(cap);

    if (result.getMinimumNumberInstances() < 1)
      result.setMinimumNumberInstances(1);

    result.setOwner(this);

    return result;
  }

  /**
   * Returns the commandline of the given object.
   *
   * @param obj		the object to create the commandline for
   * @return		the commandline
   */
  protected String getCommandline(Object obj) {
    String	result;

    result = obj.getClass().getName();
    if (obj instanceof OptionHandler)
      result += " " + Utils.joinOptions(((OptionHandler) obj).getOptions());

    return result.trim();
  }

  /**
   * prints the specified message to stdout if debug is on and can also dump
   * the message to a log file.
   *
   * @param message	the message to print or store in a log file
   */
  protected void log(String message) {
    log(message, false);
  }

  /**
   * prints the specified message to stdout if debug is on and can also dump
   * the message to a log file.
   *
   * @param message	the message to print or store in a log file
   * @param onlyLog	if true the message will only be put into the log file
   * 			but not to stdout
   */
  protected void log(String message, boolean onlyLog) {
    // print to stdout?
    if (getDebug() && (!onlyLog))
      System.out.println(message);

    // log file?
    if (!getLogFile().isDirectory())
      Debug.writeToFile(getLogFile().getAbsolutePath(), message, true);
  }

  /**
   * replaces the current option in the options array with a new value.
   *
   * @param options	the current options
   * @param option	the option to set a new value for
   * @param value	the value to set
   * @return		the updated array
   * @throws Exception	if something goes wrong
   */
  protected String[] updateOption(String[] options, String option, String value)
    throws Exception {

    String[]		result;
    Vector		tmpOptions;
    int			i;

    // remove old option
    Utils.getOption(option, options);

    // add option with new value at the beginning (to avoid clashes with "--")
    tmpOptions = new Vector();
    tmpOptions.add("-" + option);
    tmpOptions.add("" + value);

    // move options into vector
    for (i = 0; i < options.length; i++) {
      if (options[i].length() != 0)
	tmpOptions.add(options[i]);
    }

    result = (String[]) tmpOptions.toArray(new String[tmpOptions.size()]);

    return result;
  }

  /**
   * generates a table string for all the performances in the space and returns
   * that.
   *
   * @param space		the current space to align the performances to
   * @param performances	the performances to align
   * @param type		the type of performance
   * @return			the table string
   */
  protected String logPerformances(Space space, Vector<Performance> performances, Tag type) {
    StringBuffer	result;
    int			i;

    result = new StringBuffer(type.getReadable() + ":\n");

    result.append(space.toString());
    result.append("\n");
    for (i = 0; i < performances.size(); i++) {
      result.append(performances.get(i).getPerformance(type.getID()));
      result.append("\n");
    }
    result.append("\n");

    return result.toString();
  }

  /**
   * aligns all performances in the space and prints those tables to the log
   * file.
   *
   * @param space		the current space to align the performances to
   * @param performances	the performances to align
   */
  protected void logPerformances(Space space, Vector<Performance> performances) {
    int		i;

    for (i = 0; i < TAGS_EVALUATION.length; i++)
      log("\n" + logPerformances(space, performances, TAGS_EVALUATION[i]), true);
  }

  /**
   * Start the pool of execution threads.
   */
  protected void startExecutorPool() {
    stopExecutorPool();

    log("Starting thread pool with " + m_NumExecutionSlots + " slots...");

    m_ExecutorPool = new ThreadPoolExecutor(
	m_NumExecutionSlots, m_NumExecutionSlots,
        120, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
  }

  /**
   * Stops the ppol of execution threads.
   */
  protected void stopExecutorPool() {
    log("Shutting down thread pool...");

    if (m_ExecutorPool != null)
      m_ExecutorPool.shutdownNow();

    m_ExecutorPool = null;
  }

  /**
   * Helper method used for blocking.
   *
   * @param doBlock	whether to block or not
   */
  protected synchronized void block(boolean doBlock) {
    if (doBlock) {
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
   * Records the completion of the training of a single classifier. Unblocks if
   * all classifiers have been trained.
   *
   * @param obj		the classifier or setup values that was attempted to train
   * @param success 	whether the classifier trained successfully
   */
  protected synchronized void completedEvaluation(Object obj, boolean success) {
    if (!success) {
      m_Failed++;
      if (m_Debug) {
	if (obj instanceof Classifier)
	  System.err.println("Training failed: " + getCommandline(obj));
	else
	  System.err.println("Training failed: " + obj);
      }
    }
    else {
      m_Completed++;
    }

    if (m_Completed + m_Failed == m_NumSetups) {
      if (m_Failed > 0) {
        if (m_Debug)
          System.err.println("Problem building classifiers - some failed to be trained.");
      }
      block(false);
    }
  }

  /**
   * Adds the performance to the cache and the current list of performances.
   * Does nothing if at least one setup failed.
   *
   * @param performance	the performance to add
   * @param folds	the number of folds
   * @see		#m_Failed
   */
  protected void addPerformance(Performance performance, int folds) {
    if (m_Failed > 0)
      return;

    m_Performances.add(performance);
    m_Cache.add(folds, performance);
  }

  /**
   * determines the best point for the given space, using CV with
   * specified number of folds.
   *
   * @param space	the space to work on
   * @param inst	the data to work with
   * @param folds	the number of folds for cross-validation, if &lt;2 then
   * 			evaluation based on the training set is used
   * @return		the best point (not actual parameters!)
   * @throws Exception	if setup or training fails
   */
  protected Point<Object> determineBestInSpace(Space space, Instances inst, int folds) throws Exception {
    Point<Object>		result;
    int				i;
    Enumeration<Point<Object>>	enm;
    Performance			performance;
    Point<Object>		values;
    boolean			allCached;
    Performance			p1;
    Performance			p2;
    EvaluationTask		newTask;

    m_Performances.clear();

    if (folds >= 2)
      log("Determining best values with " + folds + "-fold CV in space:\n" + space + "\n");
    else
      log("Determining best values with evaluation on training set in space:\n" + space + "\n");

    enm         = space.values();
    allCached   = true;
    m_Failed    = 0;
    m_Completed = 0;
    m_NumSetups = space.size();

    while (enm.hasMoreElements()) {
      values = enm.nextElement();

      // already calculated?
      if (m_Cache.isCached(folds, values)) {
	performance = m_Cache.get(folds, values);
	m_Performances.add(performance);
	log(performance + ": cached=true");
	m_Completed++;
      }
      else {
	allCached = false;
	newTask   = new EvaluationTask(
	    this, inst, m_Generator, values, folds, m_Evaluation);
        m_ExecutorPool.execute(newTask);
      }
    }

    // wait for execution to finish
    if (m_Completed + m_Failed < m_NumSetups)
      block(true);

    if (allCached) {
      log("All points were already cached - abnormal state!");
      throw new IllegalStateException("All points were already cached - abnormal state!");
    }

    if (m_Failed > 0)
      throw new WekaException("Failed to evaluate " + m_Failed + " setups!");

    // sort list
    Collections.sort(m_Performances, new PerformanceComparator(m_Evaluation));

    result = m_Performances.lastElement().getValues();

    // check whether all performances are the same
    m_UniformPerformance = true;
    p1 = m_Performances.get(0);
    for (i = 1; i < m_Performances.size(); i++) {
      p2 = m_Performances.get(i);
      if (p2.getPerformance(m_Evaluation) != p1.getPerformance(m_Evaluation)) {
	m_UniformPerformance = false;
	break;
      }
    }
    if (m_UniformPerformance)
      log("All performances are the same!");

    logPerformances(space, m_Performances);
    log("\nBest performance:\n" + m_Performances.lastElement());

    m_Performances.clear();

    return result;
  }

  /**
   * returns the best point in the space.
   *
   * @param inst	the training data
   * @return 		the best point (not evaluated parameters!)
   * @throws Exception 	if something goes wrong
   */
  protected Point<Object> findBest(Instances inst) throws Exception {
    Point<Integer>	center;
    Space		neighborSpace;
    boolean		finished;
    Point<Object>	evals;
    Point<Object>	result;
    Point<Object>	resultOld;
    int			iteration;
    Instances		sample;
    Resample		resample;
    MultiSearch		multi;

    log("Step 1:\n");

    // generate sample?
    if (getSampleSizePercent() == 100) {
      sample = inst;
    }
    else {
      log("Generating sample (" + getSampleSizePercent() + "%)");
      resample = new Resample();
      resample.setRandomSeed(getSeed());
      resample.setSampleSizePercent(getSampleSizePercent());
      resample.setInputFormat(inst);
      sample = Filter.useFilter(inst, resample);
    }

    finished             = false;
    iteration            = 0;
    m_UniformPerformance = false;

    // find first center
    log("\n=== Initial space - Start ===");
    result = determineBestInSpace(m_Space, sample, m_InitialSpaceNumFolds);
    log("\nResult of Step 1: " + result + "\n");
    log("=== Initial space - End ===\n");

    finished = m_UniformPerformance;

    if (!finished) {
      do {
	iteration++;
	resultOld = (Point<Object>) result.clone();
	center    = m_Space.getLocations(result);
	// on border? -> finished
	if (m_Space.isOnBorder(center)) {
	  log("Center is on border of space.");
	  finished = true;
	}

	// new space with current best one at center and immediate neighbors
	// around it
	if (!finished) {
	  neighborSpace = m_Space.subspace(center);
	  result = determineBestInSpace(neighborSpace, sample, m_SubsequentSpaceNumFolds);
	  log("\nResult of Step 2/Iteration " + (iteration) + ":\n" + result);
	  finished = m_UniformPerformance;

	  // no improvement?
	  if (result.equals(resultOld)) {
	    finished = true;
	    log("\nNo better point found.");
	  }
	}
      }
      while (!finished);
    }

    log("\nFinal result:" + result);
    evals = m_Generator.evaluate(result);
    multi = (MultiSearch) m_Generator.setup(this, evals);
    log("Classifier: " + getCommandline(multi.getClassifier()));

    return result;
  }

  /**
   * builds the classifier.
   *
   * @param data        the training instances
   * @throws Exception  if something goes wrong
   */
  public void buildClassifier(Instances data) throws Exception {
    Point<Object>	evals;
    MultiSearch		multi;

    // can classifier handle the data?
    getCapabilities().testWithFail(data);

    // remove instances with missing class
    data = new Instances(data);
    data.deleteWithMissingClass();

    m_Cache        = new PerformanceCache();
    m_Performances = new Vector<Performance>();
    startExecutorPool();

    // build space
    m_Generator.reset();
    m_Space = m_Generator.getSpace();

    log("\n"
	+ getClass().getName() + "\n"
	+ getClass().getName().replaceAll(".", "=") + "\n"
	+ "Options: " + Utils.joinOptions(getOptions()) + "\n");

    // find best
    m_Values = findBest(new Instances(data));
    stopExecutorPool();

    // setup best configurations
    evals            = m_Generator.evaluate(m_Values);
    multi            = (MultiSearch) m_Generator.setup(this, evals);
    m_BestClassifier = multi.getClassifier();

    // train classifier
    m_Classifier = AbstractClassifier.makeCopy(m_BestClassifier);
    m_Classifier.buildClassifier(data);
  }

  /**
   * Returns the distribution for the given instance.
   *
   * @param instance 	the test instance
   * @return 		the distribution array
   * @throws Exception 	if distribution can't be computed successfully
   */
  public double[] distributionForInstance(Instance instance) throws Exception {
    return m_Classifier.distributionForInstance(instance);
  }

  /**
   * returns a string representation of the classifier.
   *
   * @return a string representation of the classifier
   */
  public String toString() {
    String	result;
    int		i;

    result = "";

    if (m_Values == null) {
      result = "No search performed yet.";
    }
    else {
      result =
      	  this.getClass().getName() + ":\n"
      	+ "Classifier: " + getCommandline(getBestClassifier()) + "\n\n";
      for (i = 0; i < m_Generator.getParameters().length; i++)
      	result += (i+1) + ". property: " + m_Generator.getParameters()[i].getProperty() + "\n";
      result +=   "Evaluation: " + getEvaluation().getSelectedTag().getReadable() + "\n"
      	        + "Coordinates: " + getValues() + "\n";

      result +=
	  "Values: " + m_Generator.evaluate(getValues()) + "\n\n"
        + m_Classifier.toString();
    }

    return result;
  }

  /**
   * Returns a string that summarizes the object.
   *
   * @return 		the object summarized as a string
   */
  public String toSummaryString() {
    String	result;

    result = "Best classifier: " + getCommandline(getBestClassifier());

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
   * Main method for running this classifier from commandline.
   *
   * @param args 	the options
   */
  public static void main(String[] args) {
    runClassifier(new MultiSearch(), args);
  }
}
