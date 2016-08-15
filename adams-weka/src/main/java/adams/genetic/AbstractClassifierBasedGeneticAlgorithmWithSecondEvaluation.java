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
 * AbstractClassifierBasedGeneticAlgorithmWithSecondEvaluation.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.genetic;

import weka.core.Instances;

import java.util.Hashtable;

/**
 * Ancestor for genetic algorithms that offer a second evaluation using
 * a different seed value. The fitness value of the first evaluation only
 * gets added if it the best one so far and the evaluation with the second
 * seed value also generates the best value so far (using a second set of
 * stored results).
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractClassifierBasedGeneticAlgorithmWithSecondEvaluation
  extends AbstractClassifierBasedGeneticAlgorithm {

  private static final long serialVersionUID = -7323960806463832596L;

  /**
   * Job class for algorithms with datasets.
   *
   * @author  dale
   * @version $Revision: 4322 $
   */
  public static abstract class ClassifierBasedGeneticAlgorithmWithSecondEvaluationJob<T extends AbstractClassifierBasedGeneticAlgorithmWithSecondEvaluation>
    extends ClassifierBasedGeneticAlgorithmJob<T> {

    private static final long serialVersionUID = 5294683216617538910L;

    /** whether to use second evaluation. */
    protected boolean m_UseSecondEvaluation;

    /** the current fitness (second evaluation). */
    protected Double m_SecondFitness;

    /** the cross-validation seed (second evaluation). */
    protected int m_SecondSeed;

    /** the cross-validation folds (second evaluation). */
    protected int m_SecondFolds;

    /**
     * Initializes the job.
     *
     * @param g   	the algorithm object this job belongs to
     * @param chromosome the chromsome index
     * @param w   	the initial weights
     * @param data	the data to use
     */
    public ClassifierBasedGeneticAlgorithmWithSecondEvaluationJob(T g, int chromosome, int[] w, Instances data) {
      super(g, chromosome, w, data);

      m_UseSecondEvaluation = g.getUseSecondEvaluation();
      m_SecondFolds         = g.getSecondFolds();
      m_SecondSeed          = g.getSecondCrossValidationSeed();
      m_SecondFitness       = null;
    }

    /**
     * Returns the whether to use second evaluation.
     *
     * @return		true if to use second evaluation
     */
    public boolean getUseSecondEvaluation() {
      return m_UseSecondEvaluation;
    }

    /**
     * Returns the cross-validation seed (second evaluation).
     *
     * @return		the seed
     */
    public int getSecondSeed() {
      return m_SecondSeed;
    }

    /**
     * Returns the number of cross-validation folds (second evaluation).
     *
     * @return		the number of folds
     */
    public int getSecondFolds() {
      return m_SecondFolds;
    }

    /**
     * Returns the fitness (second evaluation).
     *
     * @return		the fitness
     */
    public Double getSecondFitness() {
      return m_SecondFitness;
    }
  }

  /** whether to use second evaluation with different seed. */
  protected boolean m_UseSecondEvaluation;

  /** the number of folds for cross-validation (second evaluation). */
  protected int m_SecondFolds;

  /** the cross-validation seed (second evaluation). */
  protected int m_SecondCrossValidationSeed;

  /** the cache for results (second evaluation). */
  public Hashtable<String,Double> m_SecondStoredResults = new Hashtable<>();

  /** the best fitness so far (second evaluation). */
  protected double m_SecondBestFitness;

  /** the best setup so far (second evaluation). */
  protected Object m_SecondBestSetup;

  /** the best weights/bits so far (second evaluation). */
  protected int[] m_SecondBestWeights;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "use-second-evaluation", "useSecondEvaluation",
      false);

    m_OptionManager.add(
      "second-folds", "secondFolds",
      10, 2, null);

    m_OptionManager.add(
      "second-cv-seed", "secondCrossValidationSeed",
      42);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_SecondBestFitness = Double.NEGATIVE_INFINITY;
    m_SecondBestSetup   = null;
    m_SecondBestWeights = null;
  }

  /**
   * Sets whether to use the second evaluation.
   *
   * @param value	true if to use second evaluation
   */
  public void setUseSecondEvaluation(boolean value) {
    m_UseSecondEvaluation = value;
    reset();
  }

  /**
   * Returns whether to use the second evaluation.
   *
   * @return		true if to use second evaluation
   */
  public boolean getUseSecondEvaluation() {
    return m_UseSecondEvaluation;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useSecondEvaluationTipText() {
    return "If enabled, a second evaluation is performed using the separate folds and seed.";
  }

  /**
   * Sets the number of folds to use in cross-validation (second evaluation).
   *
   * @param value	the number of folds
   */
  public void setSecondFolds(int value) {
    m_SecondFolds = value;
    reset();
  }

  /**
   * Returns the number of folds to use in cross-validation (second evaluation).
   *
   * @return		the number of folds
   */
  public int getSecondFolds() {
    return m_SecondFolds;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String secondFoldsTipText() {
    return "The number of folds to use in cross-validation (second evaluation).";
  }

  /**
   * Sets the seed value to use for cross-validation (second evaluation).
   *
   * @param value	the seed to use
   */
  public void setSecondCrossValidationSeed(int value) {
    m_SecondCrossValidationSeed = value;
    reset();
  }

  /**
   * Returns the current seed value for cross-validation (second evaluation).
   *
   * @return		the seed value
   */
  public int getSecondCrossValidationSeed() {
    return m_SecondCrossValidationSeed;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String secondCrossValidationSeedTipText() {
    return "The seed value for cross-validation (second evaluation).";
  }

  /**
   * Adds a result to the cache (second evaluation).
   *
   * @param key		the key of the result
   * @param val		the value to add
   */
  protected synchronized void addSecondResult(String key, Double val) {
    m_SecondStoredResults.put(key, val);
  }

  /**
   * Returns a value from the cache (second evaluation).
   *
   * @param key		the key of the result
   * @return		the result or null if not present
   */
  protected synchronized Double getSecondResult(String key) {
    return m_SecondStoredResults.get(key);
  }

  /**
   * Clears all currently stored results.
   */
  protected synchronized void clearResults() {
    super.clearResults();
    m_SecondStoredResults.clear();
  }

  /**
   * Checks whether the fitness is better (second evaluation).
   *
   * @param fitness	the new fitness
   * @return		true if the new fitness is better
   */
  public synchronized boolean isSecondBetterFitness(double fitness) {
    return (fitness > m_SecondBestFitness);
  }

  /**
   * Sets a fitness and keep it if better (second evaluation).
   *
   * @param fitness	the new fitness
   * @param setup	the new setup
   * @param weights	the new weights
   * @return		true if the new fitness was better
   */
  public synchronized boolean setSecondNewFitness(double fitness, Object setup, int[] weights) {
    boolean 	result;

    result = false;

    if (isSecondBetterFitness(fitness)) {
      m_SecondBestFitness = fitness;
      m_SecondBestSetup   = setup;
      m_SecondBestWeights = weights.clone();
      result              = true;
    }

    return result;
  }

  /**
   * Further initializations in derived classes.
   */
  protected void preRun() {
    super.preRun();

    m_SecondBestFitness = Double.NEGATIVE_INFINITY;
    m_SecondBestSetup   = null;
    m_SecondBestWeights = null;
  }
}
