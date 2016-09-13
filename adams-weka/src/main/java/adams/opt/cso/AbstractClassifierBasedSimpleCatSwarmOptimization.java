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
 * AbstractClassifierBasedSimpleCatSwarmOptimization.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.opt.cso;

import adams.core.io.PlaceholderDirectory;
import adams.event.CatSwarmOptimizationFitnessChangeEvent;
import adams.event.CatSwarmOptimizationFitnessChangeNotifier;
import org.jblas.DoubleMatrix;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.rules.ZeroR;
import weka.core.Instances;

import java.util.Hashtable;
import java.util.Random;

/**
 * Ancestor for classifier-based CSO optimization schemes.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractClassifierBasedSimpleCatSwarmOptimization
  extends AbstractSimpleCatSwarmOptimization
  implements ClassifierBasedCatSwarmOptimization, CatSwarmOptimizationFitnessChangeNotifier {

  private static final long serialVersionUID = -3893761358634772738L;

  /** the data to use for cross-validation. */
  protected Instances m_Instances;

  /** the classifier to use. */
  protected Classifier m_Classifier;

  /** the number of folds for cross-validation. */
  protected int m_Folds;

  /** the cross-validation seed. */
  protected int m_CrossValidationSeed;

  /** the measure to use for evaluating the fitness. */
  protected Measure m_Measure;

  /** the directory to store the generated ARFF files in. */
  protected PlaceholderDirectory m_OutputDirectory;

  /** the type of output to generate. */
  protected OutputType m_OutputType;

  /** the type of prefix to use for the output. */
  protected OutputPrefixType m_OutputPrefixType;

  /** the supplied prefix. */
  protected String m_SuppliedPrefix;

  /** the cache for results. */
  public Hashtable<DoubleMatrix,Double> m_StoredResults = new Hashtable<>();

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "folds", "folds",
      10, 2, null);

    m_OptionManager.add(
      "cv-seed", "crossValidationSeed",
      55);

    m_OptionManager.add(
      "classifier", "classifier",
      getDefaultClassifier());

    m_OptionManager.add(
      "measure", "measure",
      Measure.RMSE);

    m_OptionManager.add(
      "output-dir", "outputDirectory",
      new PlaceholderDirectory("."));

    m_OptionManager.add(
      "output-type", "outputType",
      getDefaultOutputType());

    m_OptionManager.add(
      "output-prefix-type", "outputPrefixType",
      OutputPrefixType.NONE);

    m_OptionManager.add(
      "supplied-prefix", "suppliedPrefix",
      "");
  }

  /**
   * Sets the data to use for cross-validation.
   *
   * @param value	the dataset
   */
  public void setInstances(Instances value) {
    m_Instances = value;
  }

  /**
   * Returns the currently set dataset for cross-validation.
   *
   * @return		the dataset
   */
  public Instances getInstances() {
    return m_Instances;
  }

  /**
   * Sets the number of folds to use in cross-validation.
   *
   * @param value	the number of folds
   */
  public void setFolds(int value){
    m_Folds = value;
    reset();
  }

  /**
   * Returns the number of folds to use in cross-validation.
   *
   * @return		the number of folds
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
   * Sets the seed value to use for cross-validation.
   *
   * @param value	the seed to use
   */
  public void setCrossValidationSeed(int value) {
    m_CrossValidationSeed = value;
    reset();
  }

  /**
   * Returns the current seed value for cross-validation.
   *
   * @return		the seed value
   */
  public int getCrossValidationSeed() {
    return m_CrossValidationSeed;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String crossValidationSeedTipText() {
    return "The seed value for cross-validation.";
  }

  /**
   * Returns the default classifier to use.
   *
   * @return		the classifier
   */
  protected Classifier getDefaultClassifier() {
    return new ZeroR();
  }

  /**
   * Sets the classifier to use.
   *
   * @param value	the classifier
   */
  public void setClassifier(Classifier value) {
    m_Classifier = value;
    reset();
  }

  /**
   * Returns the currently set classifier.
   *
   * @return		the classifier
   */
  public Classifier getClassifier() {
    return m_Classifier;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String classifierTipText() {
    return "The classifier to use.";
  }

  /**
   * Sets the measure used for evaluating the fitness.
   *
   * @param value	the fitness measure
   */
  public void setMeasure(Measure value) {
    m_Measure = value;
    reset();
  }

  /**
   * Returns the current measure for evaluating the fitness.
   *
   * @return		the measure
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
    return "The measure used for evaluating the fitness.";
  }

  /**
   * Sets the directory for the generated ARFF files.
   *
   * @param value	the directory
   */
  public void setOutputDirectory(PlaceholderDirectory value) {
    m_OutputDirectory = value;
    reset();
  }

  /**
   * Returns the currently set directory for the generated ARFF files.
   *
   * @return		the directory
   */
  public PlaceholderDirectory getOutputDirectory() {
    return m_OutputDirectory;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputDirectoryTipText() {
    return "The directory for storing the generated ARFF files.";
  }

  /**
   * Returns the default output type to use.
   *
   * @return		the type
   */
  protected abstract OutputType getDefaultOutputType();

  /**
   * Sets the type of output to generate.
   *
   * @param value	the type
   */
  public void setOutputType(OutputType value){
    m_OutputType = value;
    reset();
  }

  /**
   * Returns the type of output to generate.
   *
   * @return		the type
   */
  public OutputType getOutputType() {
    return m_OutputType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputTypeTipText() {
    return "The type of output to generate.";
  }

  /**
   * Sets the type of prefix to use for the output.
   *
   * @param value	the type
   */
  public void setOutputPrefixType(OutputPrefixType value){
    m_OutputPrefixType = value;
    reset();
  }

  /**
   * Returns the type of prefix to use for the output.
   *
   * @return		the type
   */
  public OutputPrefixType getOutputPrefixType() {
    return m_OutputPrefixType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputPrefixTypeTipText() {
    return "The type of prefix to use for the output.";
  }

  /**
   * Sets the prefix to use in case of {@link OutputPrefixType#SUPPLIED}.
   *
   * @param value	the prefix
   */
  public void setSuppliedPrefix(String value){
    m_SuppliedPrefix = value;
    reset();
  }

  /**
   * Returns the prefix to use in case of {@link OutputPrefixType#SUPPLIED}.
   *
   * @return		the number of folds
   */
  public String getSuppliedPrefix() {
    return m_SuppliedPrefix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String suppliedPrefixTipText() {
    return "The prefix to use in case of " + OutputPrefixType.SUPPLIED + ".";
  }

  /**
   * Adds a result to the cache.
   *
   * @param key		the key of the result
   * @param val		the value to add
   */
  protected synchronized void addResult(DoubleMatrix key, Double val) {
    m_StoredResults.put(key, val);
  }

  /**
   * Returns a value from the cache.
   *
   * @param key		the key of the result
   * @return		the result or null if not present
   */
  protected synchronized Double getResult(DoubleMatrix key){
    return m_StoredResults.get(key);
  }

  /**
   * Clears all currently stored results.
   */
  protected synchronized void clearResults() {
    m_StoredResults.clear();
  }

  /**
   * Returns the best currently best fitness.
   *
   * @return		the fitness
   */
  public double getCurrentFitness() {
    return m_Measure.actual(m_LastFitness);
  }

  /**
   * Hook method which gets called when the fitness changes.
   *
   * @param oldFitness		the old fitness
   * @param newFitness		the new fitness
   */
  protected void fitnessChanged(double oldFitness, double newFitness) {
    oldFitness = m_Measure.actual(oldFitness);
    newFitness = m_Measure.actual(newFitness);
    getLogger().info("Fitness improvement: " + oldFitness + " -> " + newFitness);
    notifyFitnessChangeListeners(new CatSwarmOptimizationFitnessChangeEvent(this, newFitness, getBestSetup()));
  }

  /**
   * Evaluates the classifier on the dataset and returns the metric.
   *
   * @param cls		the classifier to evaluate
   * @param data		the data to use for evaluation
   * @param folds		the number of folds to use
   * @param seed		the seed for the randomization
   * @return			the metric
   * @throws Exception	if the evaluation fails
   */
  protected double evaluateClassifier(Classifier cls, Instances data, int folds, int seed) throws Exception {
    Evaluation evaluation;

    evaluation = new Evaluation(data);
    evaluation.crossValidateModel(cls, data, folds, new Random(seed));

    return getMeasure().extract(evaluation, true);
  }

  /**
   * Gets executed before the actual run starts.
   */
  @Override
  protected void preRun() {
    super.preRun();
    clearResults();
  }
}
