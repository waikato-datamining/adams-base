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
 *    AdditiveRegressionUserDefined.java
 *    Copyright (C) 2000-2025 University of Waikato, Hamilton, New Zealand
 *
 */

package weka.classifiers.meta;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.MultipleClassifiersCombiner;
import weka.core.AdditionalMeasureProducer;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.RevisionUtils;
import weka.core.TechnicalInformation;
import weka.core.TechnicalInformation.Field;
import weka.core.TechnicalInformation.Type;
import weka.core.TechnicalInformationHandler;
import weka.core.UnassignedClassException;
import weka.core.Utils;
import weka.core.WeightedInstancesHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

/**
 <!-- globalinfo-start -->
 * Meta classifier that enhances the performance of the regression base classifiers, iterating through the list of specified classifiers. Each iteration fits a model to the residuals left by the classifier on the previous iteration. Prediction is accomplished by adding the predictions of each classifier. Reducing the shrinkage (learning rate) parameter helps prevent overfitting and has a smoothing effect but increases the learning time.<br>
 * <br>
 * Based on additive regression, but iterates through the user-defined list of classifiers rather than using the same classifier in each iteration.<br>
 * <br>
 * For more information see:<br>
 * <br>
 * J.H. Friedman (1999). Stochastic Gradient Boosting.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- technical-bibtex-start -->
 * BibTeX:
 * <pre>
 * &#64;techreport{Friedman1999,
 *    author = {J.H. Friedman},
 *    institution = {Stanford University},
 *    title = {Stochastic Gradient Boosting},
 *    year = {1999},
 *    PS = {http://www-stat.stanford.edu/\~jhf/ftp/stobst.ps}
 * }
 * </pre>
 * <br><br>
 <!-- technical-bibtex-end -->
 *
 <!-- options-start -->
 * Valid options are: <p>
 *
 * <pre> -S
 *  Specify shrinkage rate. (default = 1.0, i.e., no shrinkage)</pre>
 *
 * <pre> -A
 *  Minimize absolute error instead of squared error (assumes that base learner minimizes absolute error).</pre>
 *
 * <pre> -resume
 *  Set whether classifier can continue training after performing therequested number of iterations.
 *  Note that setting this to true will retain certain data structures which can increase the
 *  size of the model.
 * </pre>
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
 * @author Mark Hall (mhall@cs.waikato.ac.nz)
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class AdditiveRegressionUserDefined
  extends MultipleClassifiersCombiner
  implements OptionHandler, AdditionalMeasureProducer, WeightedInstancesHandler, TechnicalInformationHandler {

  /** for serialization */
  private static final long serialVersionUID = -2368937577670527151L;

  /** ArrayList for storing the generated base classifiers.
   Note: we are hiding the variable from IteratedSingleClassifierEnhancer*/
  protected ArrayList<Classifier> m_ClassifierList;

  /** Shrinkage (Learning rate). Default = no shrinkage. */
  protected double m_shrinkage = 1.0;

  /** The mean or median */
  protected double m_InitialPrediction;

  /** whether we have suitable data or nor (if only mean/mode is used) */
  protected boolean m_SuitableData = true;

  /** The working data */
  protected Instances m_Data;

  /** The sum of (absolute or squared) residuals. */
  protected double m_Error;

  /** The improvement in the sum of (absolute or squared) residuals. */
  protected double m_Diff;

  /** Whether to minimise absolute error instead of squared error. */
  protected boolean m_MinimizeAbsoluteError;

  /**
   * Whether to allow training to continue at a later point after the initial
   * model is built.
   */
  protected boolean m_resume;

  /** Number of iterations performed in this session of iterating */
  protected int m_numItsPerformed;

  /**
   * Returns a string describing this attribute evaluator
   * @return a description of the evaluator suitable for
   * displaying in the explorer/experimenter gui
   */
  public String globalInfo() {
    return "Meta classifier that enhances the performance of the regression "
	     + "base classifiers, iterating through the list of specified classifiers. "
	     + "Each iteration fits a model to the residuals left "
	     + "by the classifier on the previous iteration. Prediction is "
	     + "accomplished by adding the predictions of each classifier. "
	     + "Reducing the shrinkage (learning rate) parameter helps prevent "
	     + "overfitting and has a smoothing effect but increases the learning "
	     + "time.\n\n"
	     + "Based on additive regression, but iterates through the user-defined "
	     + "list of classifiers rather than using the same classifier in each iteration.\n\n"
	     + "For more information see:\n\n"
	     + getTechnicalInformation().toString();
  }

  /**
   * Returns an instance of a TechnicalInformation object, containing
   * detailed information about the technical background of this class,
   * e.g., paper reference or book this class is based on.
   *
   * @return the technical information about this class
   */
  public TechnicalInformation getTechnicalInformation() {
    TechnicalInformation 	result;

    result = new TechnicalInformation(Type.TECHREPORT);
    result.setValue(Field.AUTHOR, "J.H. Friedman");
    result.setValue(Field.YEAR, "1999");
    result.setValue(Field.TITLE, "Stochastic Gradient Boosting");
    result.setValue(Field.INSTITUTION, "Stanford University");
    result.setValue(Field.PS, "http://www-stat.stanford.edu/~jhf/ftp/stobst.ps");

    return result;
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  public Enumeration<Option> listOptions() {
    Vector<Option> newVector = new Vector<>();

    newVector.addElement(new Option(
      "\tSpecify shrinkage rate. (default = 1.0, i.e., no shrinkage)",
      "S", 1, "-S"));

    newVector.addElement(new Option(
      "\tMinimize absolute error instead of squared error (assumes that base learner minimizes absolute error).",
      "A", 0, "-A"));

    newVector.addElement(new Option("\t" + resumeTipText() + "\n",
      "resume", 0, "-resume"));

    newVector.addAll(Collections.list(super.listOptions()));

    return newVector.elements();
  }

  /**
   * Parses a given list of options.
   *
   * @param options the list of options as an array of strings
   * @throws Exception if an option is not supported
   */
  public void setOptions(String[] options) throws Exception {
    String optionString = Utils.getOption('S', options);
    if (!optionString.isEmpty())
      setShrinkage(Double.parseDouble(optionString));
    else
      setShrinkage(1.0);

    setMinimizeAbsoluteError(Utils.getFlag('A', options));

    setResume(Utils.getFlag("resume", options));

    super.setOptions(options);
  }

  /**
   * Gets the current settings of the Classifier.
   *
   * @return an array of strings suitable for passing to setOptions
   */
  public String [] getOptions() {
    List<String> options = new ArrayList<>();

    options.add("-S"); options.add("" + getShrinkage());

    if (getMinimizeAbsoluteError())
      options.add("-A");

    if (getResume())
      options.add("-resume");

    Collections.addAll(options, super.getOptions());

    return options.toArray(new String[0]);
  }

  /**
   * Returns the tip text for this property
   * @return tip text for this property suitable for
   * displaying in the explorer/experimenter gui
   */
  public String shrinkageTipText() {
    return "Shrinkage rate. Smaller values help prevent overfitting and "
	     + "have a smoothing effect (but increase learning time). "
	     +"Default = 1.0, ie. no shrinkage.";
  }

  /**
   * Set the shrinkage parameter
   *
   * @param l the shrinkage rate.
   */
  public void setShrinkage(double l) {
    m_shrinkage = l;
  }

  /**
   * Get the shrinkage rate.
   *
   * @return the value of the learning rate
   */
  public double getShrinkage() {
    return m_shrinkage;
  }

  /**
   * Returns the tip text for this property
   * @return tip text for this property suitable for
   * displaying in the explorer/experimenter gui
   */
  public String minimizeAbsoluteErrorTipText() {
    return "Minimize absolute error instead of squared error (assume base learner minimizes absolute error)";
  }

  /**
   * Sets whether absolute error is to be minimized.
   *
   * @param f true if absolute error is to be minimized.
   */
  public void setMinimizeAbsoluteError(boolean f) {
    m_MinimizeAbsoluteError = f;
  }

  /**
   * Gets whether absolute error is to be minimized.
   *
   * @return true if absolute error is to be minimized
   */
  public boolean getMinimizeAbsoluteError() {
    return m_MinimizeAbsoluteError;
  }

  /**
   * Tool tip text for the resume property
   *
   * @return the tool tip text for the finalize property
   */
  public String resumeTipText() {
    return "Set whether classifier can continue training after performing the"
	     + "requested number of iterations. \n\tNote that setting this to true will "
	     + "retain certain data structures which can increase the \n\t"
	     + "size of the model.";
  }

  /**
   * If called with argument true, then the next time done() is called the model is effectively
   * "frozen" and no further iterations can be performed
   *
   * @param resume true if the model is to be finalized after performing iterations
   */
  public void setResume(boolean resume) {
    m_resume = resume;
  }

  /**
   * Returns true if the model is to be finalized (or has been finalized) after
   * training.
   *
   * @return the current value of finalize
   */
  public boolean getResume() {
    return m_resume;
  }

  /**
   * Returns default capabilities of the classifier.
   *
   * @return      the capabilities of this classifier
   */
  public Capabilities getCapabilities() {
    Capabilities result;

    if (getClassifiers().length == 0) {
      result = super.getCapabilities();

      // class
      result.disableAllClasses();
      result.disableAllClassDependencies();
      result.enable(Capability.NUMERIC_CLASS);
      result.enable(Capability.DATE_CLASS);
    }
    else {
      result = getClassifiers()[0].getCapabilities();
    }

    return result;
  }

  /**
   * Initialize classifier.
   *
   * @param data the training data
   * @throws Exception if the classifier could not be initialized successfully
   */
  public void initializeClassifier(Instances data) throws Exception {
    m_numItsPerformed = 0;

    if (m_Data == null) {
      // can classifier handle the data?
      getCapabilities().testWithFail(data);

      // remove instances with missing class
      m_Data = new Instances(data);
      m_Data.deleteWithMissingClass();

      // Add the model for the mean first
      if (getMinimizeAbsoluteError())
	m_InitialPrediction = m_Data.kthSmallestValue(m_Data.classIndex(), m_Data.numInstances() / 2);
      else
	m_InitialPrediction = m_Data.meanOrMode(m_Data.classIndex());

      // only class? -> use only ZeroR model
      if (m_Data.numAttributes() == 1) {
	System.err.println(
	  "Cannot build non-trivial model (only class attribute present in data!).");
	m_SuitableData = false;
	return;
      }
      else {
	m_SuitableData = true;
      }

      // Initialize list of classifiers and data
      m_ClassifierList = new ArrayList<>();
      m_Data = residualReplace(m_Data, m_InitialPrediction);

      // Calculate error
      m_Error = 0;
      m_Diff = Double.MAX_VALUE;
      for (int i = 0; i < m_Data.numInstances(); i++) {
	if (getMinimizeAbsoluteError())
	  m_Error += m_Data.instance(i).weight() * Math.abs(m_Data.instance(i).classValue());
	else
	  m_Error += m_Data.instance(i).weight() * m_Data.instance(i).classValue() * m_Data.instance(i).classValue();
      }
      if (m_Debug) {
	if (getMinimizeAbsoluteError()) {
	  System.err.println(
	    "Sum of absolute residuals (predicting the median) : " + m_Error);
	}
	else {
	  System.err.println(
	    "Sum of squared residuals (predicting the mean) : " + m_Error);
	}
      }
    }
  }

  /**
   * Perform another iteration.
   */
  public boolean next(int index) throws Exception {
    if ((!m_SuitableData) || (m_numItsPerformed >= m_Classifiers.length) ||
	  (m_Diff <= Utils.SMALL)) {
      return false;
    }

    if (getDebug())
      System.out.println("Iteration #" + index + ": " + Utils.toCommandLine(m_Classifiers[index]));

    // Build the classifier
    m_ClassifierList.add(AbstractClassifier.makeCopy(m_Classifiers[index]));
    m_ClassifierList.get(m_ClassifierList.size() - 1).buildClassifier(m_Data);

    m_Data = residualReplace(m_Data, m_ClassifierList.get(m_ClassifierList.size() - 1));
    double sum = 0;
    for (int i = 0; i < m_Data.numInstances(); i++) {
      if (getMinimizeAbsoluteError())
	sum += m_Data.instance(i).weight() * Math.abs(m_Data.instance(i).classValue());
      else
	sum += m_Data.instance(i).weight() * m_Data.instance(i).classValue() * m_Data.instance(i).classValue();
    }
    if (m_Debug) {
      if (getMinimizeAbsoluteError())
	System.err.println("Sum of absolute residuals: " + sum);
      else
	System.err.println("Sum of squared residuals: " + sum);
    }

    m_Diff = m_Error - sum;
    m_Error = sum;
    m_numItsPerformed++;

    return true;
  }

  /**
   * Method used to build the classifier.
   */
  public void buildClassifier(Instances data) throws Exception {
    // Initialize classifier
    initializeClassifier(data);

    // For the given number of iterations
    for (int index = 0; index < m_Classifiers.length; index++)
      next(index);

    // Clean up
    m_Data = null;
  }

  /**
   * Classify an instance.
   *
   * @param inst the instance to predict
   * @return a prediction for the instance
   * @throws Exception if an error occurs
   */
  public double classifyInstance(Instance inst) throws Exception {
    double prediction = m_InitialPrediction;

    // default model?
    if (!m_SuitableData) {
      return prediction;
    }

    for (Classifier classifier : m_ClassifierList) {
      double toAdd = classifier.classifyInstance(inst);
      if (Utils.isMissingValue(toAdd)) {
	throw new UnassignedClassException("AdditiveRegression: base learner predicted missing value.");
      }
      prediction += (toAdd * getShrinkage());
    }

    return prediction;
  }

  /**
   * Replace the class values of the instances from the current iteration
   * with residuals after predicting with the supplied classifier.
   *
   * @param data the instances to predict
   * @param c the classifier to use
   * @return a new set of instances with class values replaced by residuals
   * @throws Exception if something goes wrong
   */
  protected Instances residualReplace(Instances data, Classifier c) throws Exception {
    Instances newInst = new Instances(data);
    for (int i = 0; i < newInst.numInstances(); i++) {
      double pred = c.classifyInstance(newInst.instance(i));
      if (Utils.isMissingValue(pred)) {
	throw new UnassignedClassException("AdditiveRegression: base learner predicted missing value.");
      }
      newInst.instance(i).setClassValue(newInst.instance(i).classValue() - (pred * getShrinkage()));
    }
    return newInst;
  }

  /**
   * Replace the class values of the instances from the current iteration
   * with residuals after predicting the given constant.
   *
   * @param data the instances to predict
   * @param c the constant to use
   * @return a new set of instances with class values replaced by residuals
   * @throws Exception if something goes wrong
   */
  protected Instances residualReplace(Instances data, double c) throws Exception {
    Instances newInst = new Instances(data);
    for (int i = 0; i < newInst.numInstances(); i++)
      newInst.instance(i).setClassValue(newInst.instance(i).classValue() - c);
    return newInst;
  }

  /**
   * Returns an enumeration of the additional measure names
   * @return an enumeration of the measure names
   */
  public Enumeration<String> enumerateMeasures() {
    Vector<String> newVector = new Vector<>();
    newVector.addElement("measureNumIterations");
    return newVector.elements();
  }

  /**
   * Returns the value of the named measure
   * @param additionalMeasureName the name of the measure to query for its value
   * @return the value of the named measure
   * @throws IllegalArgumentException if the named measure is not supported
   */
  public double getMeasure(String additionalMeasureName) {
    if (additionalMeasureName.compareToIgnoreCase("measureNumIterations") == 0)
      return measureNumIterations();
    else
      throw new IllegalArgumentException(additionalMeasureName + " not supported (AdditiveRegression)");
  }

  /**
   * return the number of iterations (base classifiers) completed
   * @return the number of iterations (same as number of base classifier
   * models)
   */
  public double measureNumIterations() {
    return m_ClassifierList.size();
  }

  /**
   * Returns textual description of the classifier.
   *
   * @return a description of the classifier as a string
   */
  public String toString() {
    StringBuilder text = new StringBuilder();

    if (m_SuitableData && m_ClassifierList == null) {
      return "Classifier hasn't been built yet!";
    }

    // only ZeroR model?
    if (!m_SuitableData) {
      StringBuilder buf = new StringBuilder();
      buf.append(this.getClass().getName().replaceAll(".*\\.", "")).append("\n");
      buf.append(this.getClass().getName().replaceAll(".*\\.", "").replaceAll(".", "=")).append("\n\n");
      buf.append("Warning: Non-trivial model could not be built, initial prediction is: ");
      buf.append(m_InitialPrediction);
      return buf.toString();
    }

    text.append("Additive Regression\n\n");

    text.append("Initial prediction: ").append(m_InitialPrediction).append("\n\n");

    text.append("Base classifiers:\n");
    for (int i = 0; i < m_Classifiers.length; i++)
      text.append(Utils.toCommandLine(m_Classifiers[i])).append("\n");
    text.append("\n");
    text.append(m_ClassifierList.size()).append(" models generated.\n");

    for (int i = 0; i < m_ClassifierList.size(); i++) {
      text.append("\nModel number ").append(i).append("\n\n").append(m_ClassifierList.get(i)).append("\n");
    }

    return text.toString();
  }

  /**
   * Returns the revision string.
   *
   * @return		the revision
   */
  public String getRevision() {
    return RevisionUtils.extract("$Revision: 1 $");
  }

  /**
   * Main method for testing this class.
   *
   * @param args should contain the following arguments:
   * -t training file [-T test file] [-c class index]
   */
  public static void main(String[] args) {
    runClassifier(new AdditiveRegressionUserDefined(), args);
  }
}
