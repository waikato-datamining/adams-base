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
 * AbstractClassifierBasedGeneticAlgorithm.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.genetic;

import adams.core.io.FileUtils;
import adams.core.option.OptionUtils;
import adams.event.FitnessChangeEvent;
import adams.event.FitnessChangeNotifier;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.rules.ZeroR;
import weka.core.Instances;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import java.util.Vector;

/**
 * Ancestor for genetic algorithms that evaluate classifiers.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractClassifierBasedGeneticAlgorithm
  extends AbstractGeneticAlgorithmWithDataset
  implements FitnessChangeNotifier {

  private static final long serialVersionUID = 1615849384907266578L;

  /**
   * Job class for algorithms with datasets.
   *
   * @author  dale
   * @version $Revision: 4322 $
   */
  public static abstract class ClassifierBasedGeneticAlgorithmJob<T extends AbstractClassifierBasedGeneticAlgorithm>
    extends GeneticAlgorithmJobWithDataset<T> {

    /** for serialization. */
    private static final long serialVersionUID = 8259167463381721274L;

    /** the measure to use for evaluating the fitness. */
    protected Measure m_Measure;

    /**
     * Initializes the job.
     *
     * @param g   the algorithm object this job belongs to
     * @param num the number of chromsomes
     * @param w   the initial weights
     */
    public ClassifierBasedGeneticAlgorithmJob(T g, int num, int[] w) {
      super(g, num, w);

      m_Measure = g.getMeasure();
    }

    /**
     * Returns the measure used for evaluating the fitness.
     *
     * @return		the measure
     */
    public Measure getMeasure() {
      return m_Measure;
    }

    /**
     * Evaluates the classifier on the dataset and returns the metric.
     *
     * @param cls		the classifier to evaluate
     * @param data		the data to use for evaluation
     * @return			the metric
     * @throws Exception	if the evaluation fails
     */
    protected double evaluateClassifier(Classifier cls, Instances data) throws Exception {
      Evaluation 	evaluation;

      evaluation = new Evaluation(data);
      evaluation.crossValidateModel(
	cls,
	data,
	m_genetic.getFolds(),
	new Random(m_genetic.getCrossValidationSeed()));

      return getMeasure().extract(evaluation, true);
    }

    /**
     * Saves the instances to a file.
     *
     * @param fitness		the current measure/fitness
     * @param data		the instances to save
     * @throws Exception	if saving the file fails
     */
    protected void outputDataset(double fitness, Instances data) throws Exception {
      File file = new File(
        m_genetic.getOutputDirectory().getAbsolutePath()
          + File.separator + Double.toString(getMeasure().adjust(fitness)) + ".arff");
      file.createNewFile();
      Writer writer = new BufferedWriter(new FileWriter(file));
      Instances header = new Instances(data, 0);
      header = m_genetic.updateHeader(header, this);
      writer.write(header.toString());
      writer.write("\n");
      for (int i = 0; i < data.numInstances(); i++) {
        writer.write(data.instance(i).toString());
        writer.write("\n");
      }
      writer.flush();
      writer.close();
    }

    /**
     * Assembles the data for the textual setup output.
     *
     * @param fitness	the current fitness
     * @param cls	the current classifier
     * @return		the data
     */
    protected List<String> assembleSetup(double fitness, Classifier cls) {
      List<String>	result;

      result = new ArrayList<>();
      result.add(getGenetic().getClass().getSimpleName() + ": " + OptionUtils.getCommandLine(getGenetic()));
      result.add("Measure: " + getMeasure());
      result.add("Fitness: " + fitness);
      result.add("Setup: " + OptionUtils.getCommandLine(cls));

      return result;
    }

    /**
     * Saves the setup to a file.
     *
     * @param fitness		the current measure/fitness
     * @param cls	the current classifier setup
     * @throws Exception	if saving the file fails
     */
    protected boolean outputSetup(double fitness, Classifier cls) throws Exception {
      File 		file;
      List<String> 	data;
      String 		msg;

      file = new File(
	m_genetic.getOutputDirectory().getAbsolutePath()
	  + File.separator + Double.toString(getMeasure().adjust(fitness)) + ".txt");
      data = assembleSetup(fitness, cls);
      msg  = FileUtils.saveToFileMsg(data, file, null);
      if (msg != null)
	getLogger().warning("Failed to write setup to '" + file + "': " + msg);

      return (msg == null);
    }
  }

  /** the bits per gene to use. */
  protected int m_BitsPerGene;

  /** the classifier to use. */
  protected Classifier m_Classifier;

  /** the number of folds for cross-validation. */
  protected int m_Folds;

  /** the cross-validation seed. */
  protected int m_CrossValidationSeed;

  /** the best fitness so far. */
  protected double m_BestFitness;

  /** the measure to use for evaluating the fitness. */
  protected Measure m_Measure;

  /** the time period in seconds after which to notify "fitness" listeners. */
  protected int m_NotificationInterval;

  /** the timestamp the last notification got sent. */
  protected Long m_LastNotificationTime;

  /** the cache for results. */
  public Hashtable<String,Double> m_StoredResults = new Hashtable<String,Double>();

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_BestFitness            = Double.NEGATIVE_INFINITY;
    m_LastNotificationTime   = null;
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "bits-per-gene", "bitsPerGene",
      1, 1, null);

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
      "notify", "notificationInterval",
      -1);
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
   * Sets the bits per gene to use.
   *
   * @param value	the number of bits
   */
  public void setBitsPerGene(int value) {
    m_BitsPerGene = value;
    reset();
  }

  /**
   * Returns the currently set number of bits per gene.
   *
   * @return		the number of bits
   */
  public int getBitsPerGene() {
    return m_BitsPerGene;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String bitsPerGeneTipText() {
    return "The number of bits per gene to use.";
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
   * Sets the notification interval in seconds.
   *
   * @param value	the interval in seconds
   */
  public void setNotificationInterval(int value) {
    m_NotificationInterval = value;
    reset();
  }

  /**
   * Returns the currently set number of bits per gene.
   *
   * @return		the number of bits
   */
  public int getNotificationInterval() {
    return m_NotificationInterval;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String notificationIntervalTipText() {
    return
      "The time interval in seconds after which notification events about "
        + "changes in the fitness can be sent (-1 = never send notifications; "
        + "0 = whenever a change occurs).";
  }

  /**
   * Returns the currently best fitness.
   *
   * @return		the best fitness so far
   */
  public double getCurrentFitness() {
    return m_Measure.adjust(m_BestFitness);
  }

  /**
   * Sets a fitness and keep it if better. Also notifies the fitness change
   * listeners if setup.
   *
   * @param fitness	the new fitness
   * @return		true if the new fitness was better
   * @see		#m_FitnessChangeListeners
   * @see		#m_NotificationInterval
   */
  protected synchronized boolean setNewFitness(double fitness) {
    boolean 	result;

    result = false;

    if (fitness > m_BestFitness) {
      m_BestFitness = fitness;
      result        = true;
    }

    return result;
  }

  /**
   * Sends out a notification to all listeners that the fitness has changed, if
   * notifications is wanted and due.
   *
   * @param fitness	the fitness to broadcast
   */
  protected synchronized void notifyFitnessChangeListeners(double fitness) {
    boolean 	notify;
    long	currTime;

    if (m_NotificationInterval >= 0) {
      currTime = System.currentTimeMillis();
      notify   =    (m_NotificationInterval == 0)
        || ( (m_NotificationInterval > 0) && (m_LastNotificationTime == null) )
        || (    (m_NotificationInterval > 0)
        && ((double) (currTime - m_LastNotificationTime) / 1000.0 >= m_NotificationInterval));
      if (notify) {
        m_LastNotificationTime = currTime;
        notifyFitnessChangeListeners(new FitnessChangeEvent(this, fitness));
      }
    }
  }

  /**
   * Adds a result to the cache.
   *
   * @param key		the key of the result
   * @param val		the value to add
   */
  protected synchronized void addResult(String key, Double val) {
    m_StoredResults.put(key, val);
  }

  /**
   * Returns a value from the cache.
   *
   * @param key		the key of the result
   * @return		the result or null if not present
   */
  protected synchronized Double getResult(String key){
    return m_StoredResults.get(key);
  }

  /**
   * Clears all currently stored results.
   */
  protected synchronized void clearResults() {
    m_StoredResults.clear();
  }

  @Override
  public Vector<int[]> getInitialSetups() {
    return new Vector<int[]>();
  }

  /**
   * Returns a short string of the algorithm with the currently best fitness.
   *
   * @return		a short info string
   */
  @Override
  public String toString() {
    return
      super.toString()
        + "\n"
        + getCurrentFitness() + " (measure: " + getMeasure() + ")";
  }
}
