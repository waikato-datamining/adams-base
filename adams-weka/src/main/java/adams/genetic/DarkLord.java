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
 * DarkLord.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.genetic;

import adams.core.Properties;
import adams.core.SerializationHelper;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderDirectory;
import adams.core.io.PlaceholderFile;
import adams.core.option.OptionUtils;
import adams.event.FitnessChangeEvent;
import adams.event.FitnessChangeListener;
import adams.event.FitnessChangeNotifier;
import adams.multiprocess.JobList;
import adams.multiprocess.JobRunner;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.rules.ZeroR;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.UnassignedClassException;
import weka.filters.unsupervised.attribute.Remove;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author Dale (dale at cs dot waikato dot ac dot nz)
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 4322 $
 */
public class DarkLord
  extends MTAbstractGeneticAlgorithm
  implements FitnessChangeNotifier {

  /** for serialization. */
  private static final long serialVersionUID = 4822397823362084867L;

  /** the bits per gene to use. */
  protected int m_BitsPerGene;

  /** the data to use for cross-validation. */
  protected Instances m_Instances;

  /** the filename of the data to use for cross-validation. */
  protected PlaceholderFile m_Dataset;

  /** the filename for the serialized classifier. */
  protected PlaceholderFile m_SerializedModel;

  /** the classifier to use if no serialized model is given. */
  protected Classifier m_Classifier;

  /** the directory to store the generated ARFF files in. */
  protected PlaceholderDirectory m_OutputDirectory;

  /** the number of folds for cross-validation. */
  protected int m_Folds;

  /** the cross-validation seed. */
  protected int m_CrossValidationSeed;

  /** the best fitness so far. */
  protected double m_BestFitness;

  /** the class index. */
  protected String m_ClassIndex;

  /** the measure to use for evaluating the fitness. */
  protected Measure m_Measure;

  /** the time period in seconds after which to notify "fitness" listeners. */
  protected int m_NotificationInterval;

  /** the fitness change listeners. */
  protected HashSet<FitnessChangeListener> m_FitnessChangeListeners;

  /** the timestamp the last notification got sent. */
  protected Long m_LastNotificationTime;

  /** the cache for results. */
  public static Hashtable<String,Double> m_StoredResults = new Hashtable<String,Double>();

  /**
   * Adds a result to the cache.
   *
   * @param key		the key of the result
   * @param val		the value to add
   */
  protected static synchronized void addResult(String key, Double val) {
    m_StoredResults.put(key, val);
  }

  /**
   * Returns a value from the cache.
   *
   * @param key		the key of the result
   * @return		the result or null if not present
   */
  protected static synchronized Double getResult(String key){
    Double res = m_StoredResults.get(key);
    return res;
  }

  /**
   * Clears all currently stored results.
   */
  protected static synchronized void clearResults() {
    m_StoredResults.clear();
  }

  @Override
  public Vector<int[]> getInitialSetups()
  {
      return new Vector<int[]>();
  }

  /**
   * A job class specific to The Dark Lord.
   *
   * @author  dale
   * @version $Revision: 4322 $
   */
  public static class DarkLordJob
    extends GeneticAlgorithmJob {

    /** for serialization. */
    private static final long serialVersionUID = 8259167463381721274L;

    /** the measure to use for evaluating the fitness. */
    protected Measure m_Measure;

    /**
     * Initializes the job.
     *
     * @param g		the algorithm object this job belongs to
     * @param num	the number of chromsomes
     * @param w		the initial weights
     */
    public DarkLordJob(DarkLord g, int num, int[] w) {
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
     * Returns the instances in use by the genetic algorithm.
     *
     * @return		the instances
     */
    protected Instances getInstances() {
      return ((DarkLord) m_genetic).getInstances();
    }

    /**
     * Returns the "mask" of attributes as range string.
     *
     * @return		the mask
     */
    public String getMaskAsString(){
      String ret = "[";
      int pos = 0;
      int last = -1;
      boolean thefirst = true;
      for(int a = 0; a < getInstances().numAttributes(); a++)
      {
	if(a == getInstances().classIndex())
	  continue;
	if(m_weights[a] == 0)
	{
	  if(last == -1)
	    continue;
	  if(thefirst)
	    thefirst = false;
	  else
	    ret = (new StringBuilder(String.valueOf(ret))).append(",").toString();
	  if(pos - last > 1)
	    ret = (new StringBuilder(String.valueOf(ret))).append(last).append("-").append(pos).toString();
	  else
	    if(pos - last == 1)
	      ret = (new StringBuilder(String.valueOf(ret))).append(last).append(",").append(pos).toString();
	    else
	      ret = (new StringBuilder(String.valueOf(ret))).append(last).toString();
	  last = -1;
	}
	if(m_weights[a] != 0)
	{
	  if(last == -1)
	    last = a;
	  pos = a;
	}
      }

      if(last != -1)
      {
	if(!thefirst)
	  ret = (new StringBuilder(String.valueOf(ret))).append(",").toString();
	if(pos - last > 1)
	  ret = (new StringBuilder(String.valueOf(ret))).append(last).append("-").append(pos).toString();
	else
	  if(pos - last == 1)
	    ret = (new StringBuilder(String.valueOf(ret))).append(last).append(",").append(pos).toString();
	  else
	    ret = (new StringBuilder(String.valueOf(ret))).append(last).toString();
      }
      return (new StringBuilder(String.valueOf(ret))).append("]").toString();

    }

    /**
     * Generates a range string of attributes to keep (= one has to use
     * the inverse matching sense with the Remove filter).
     *
     * @return		the range of attributes to keep
     */
    public String getRemoveAsString(){
      String ret = "";
      int pos = 0;
      int last = -1;
      boolean thefirst = true;
      for(int a = 0; a < getInstances().numAttributes(); a++)
      {
          if(m_weights[a] == 0 && a != getInstances().classIndex())
          {
              if(last == -1)
                  continue;
              if(thefirst)
                  thefirst = false;
              else
                  ret = (new StringBuilder(String.valueOf(ret))).append(",").toString();
              if(pos - last > 1)
                  ret = (new StringBuilder(String.valueOf(ret))).append(last + 1).append("-").append(pos + 1).toString();
              else
              if(pos - last == 1)
                  ret = (new StringBuilder(String.valueOf(ret))).append(last + 1).append(",").append(pos + 1).toString();
              else
                  ret = (new StringBuilder(String.valueOf(ret))).append(last + 1).toString();
              last = -1;
          }
          if(m_weights[a] != 0 || a == getInstances().classIndex())
          {
              if(last == -1)
                  last = a;
              pos = a;
          }
      }

      if(last != -1)
      {
          if(!thefirst)
              ret = (new StringBuilder(String.valueOf(ret))).append(",").toString();
          if(pos - last > 1)
              ret = (new StringBuilder(String.valueOf(ret))).append(last + 1).append("-").append(pos + 1).toString();
          else
          if(pos - last == 1)
              ret = (new StringBuilder(String.valueOf(ret))).append(last + 1).append(",").append(pos + 1).toString();
          else
              ret = (new StringBuilder(String.valueOf(ret))).append(last + 1).toString();
      }
      return ret;

    }

    /**
     * Calculates the new fitness.
     */
    @Override
    public void calcNewFitness(){
      try {
	getLogger().fine((new StringBuilder("calc for:")).append(weightsToString()).toString());

	// was measure already calculated for this attribute setup?
	Double cc = DarkLord.getResult(weightsToString());
	if (cc != null){
	  getLogger().info((new StringBuilder("Already present: ")).append(Double.toString(cc.doubleValue())).toString());
	  m_fitness = cc;
	  return;
	}
	// set the weights
	int cnt = 0;
	Instances newInstances = new Instances(getInstances());
	for (int i = 0; i < getInstances().numInstances(); i++) {
	  Instance in = newInstances.instance(i);
	  cnt = 0;
	  for (int a = 0; a < getInstances().numAttributes(); a++) {
	    if (a == getInstances().classIndex())
	      continue;
	    if (m_weights[cnt++] == 0){
	      in.setValue(a,0);
	    }else {
	      in.setValue(a,in.value(a));
	    }
	  }
	}

	// obtain classifier
	Classifier newClassifier = null;
	File model = ((DarkLord) m_genetic).getSerializedModel();
	if (model.isDirectory() || !model.exists()) {
	  newClassifier = AbstractClassifier.makeCopy(((DarkLord) m_genetic).getClassifier());
	}
	else {
	  newClassifier = (Classifier) SerializationHelper.read(((DarkLord) m_genetic).getSerializedModel().getAbsolutePath());
	}

	// evaluate classifier on data
	Evaluation evaluation = new Evaluation(newInstances);
	evaluation.crossValidateModel(
	    newClassifier,
	    newInstances,
	    ((DarkLord) m_genetic).getFolds(),
	    new Random(((DarkLord) m_genetic).getCrossValidationSeed()));

	// obtain measure
	double measure = 0;
	if (getMeasure() == Measure.ACC)
	  measure = evaluation.pctCorrect();
	else if (getMeasure() == Measure.CC)
	  measure = evaluation.correlationCoefficient();
	else if (getMeasure() == Measure.MAE)
	  measure = evaluation.meanAbsoluteError();
	else if (getMeasure() == Measure.RAE)
	  measure = evaluation.relativeAbsoluteError();
	else if (getMeasure() == Measure.RMSE)
	  measure = evaluation.rootMeanSquaredError();
	else if (getMeasure() == Measure.RRSE)
	  measure = evaluation.rootRelativeSquaredError();
	else
	  throw new IllegalStateException("Unhandled measure '" + getMeasure() + "'!");
	measure = getMeasure().adjust(measure);

	// process fitness
	m_fitness = measure;
	if (((DarkLord) m_genetic).setNewFitness(m_fitness)) {
	  File file = new File(
	      ((DarkLord) m_genetic).getOutputDirectory().getAbsolutePath()
	      + File.separator + Double.toString(getMeasure().adjust(measure)) + ".arff");
	  file.createNewFile();
	  Writer writer = new BufferedWriter(new FileWriter(file));
	  Instances header = new Instances(newInstances, 0);
	  header = m_genetic.updateHeader(header, this);
	  writer.write(header.toString());
	  writer.write("\n");
	  for (int i = 0; i < newInstances.numInstances(); i++) {
	    writer.write(newInstances.instance(i).toString());
	    writer.write("\n");
	  }
	  writer.flush();
	  writer.close();

	  file = new File(
	      ((DarkLord) m_genetic).getOutputDirectory().getAbsolutePath()
	      + File.separator + Double.toString(getMeasure().adjust(measure)) + ".txt");
          List<String> data = new ArrayList<>();
          data.add("Measure: " + getMeasure());
          data.add("Fitness: " + m_fitness);
	  data.add("Setup: " + OptionUtils.getCommandLine(newClassifier));
	  data.add("Mask: " + getMaskAsString());
	  String msg = FileUtils.saveToFileMsg(data, file, null);
          if (msg != null)
            getLogger().warning("Failed to write setup to '" + file + "': " + msg);

	  // notify the listeners
	  ((DarkLord) m_genetic).notifyFitnessChangeListeners(getMeasure().adjust(measure));
	}
	else {
	  getLogger().fine(getMaskAsString());
	}

	DarkLord.addResult(weightsToString(), m_fitness);
      }
      catch(Exception e){
	getLogger().log(Level.SEVERE, "Error: ", e);
	m_fitness = null;
      }
    }

    /**
     * Checks whether all pre-conditions have been met.
     *
     * @return		null if everything is OK, otherwise an error message
     */
    @Override
    protected String preProcessCheck() {
      String	result;

      result = super.preProcessCheck();

      if (result == null) {
	if (getInstances() == null)
	  result = "Null instances, which is poor..";
      }

      return result;
    }
  }

  /**
   * The measure to use for evaluating.
   *
   * @author FracPete (fracpete at waikato dot ac dot nz)
   * @version $Revision: 4322 $
   */
  public enum Measure {

    /** evaluation via: Correlation coefficient. */
    CC(false, false, true),
    /** evaluation via: Root mean squared error. */
    RMSE(true, true, true),
    /** evaluation via: Root relative squared error. */
    RRSE(true, true, true),
    /** evaluation via: Mean absolute error. */
    MAE(true, true, true),
    /** evaluation via: Relative absolute error. */
    RAE(true, true, true),
    /** evaluation via: Accuracy. */
    ACC(false, true, false);


    /** whether the measure is multiplied by -1 or not. Only used in sorting. */
    private boolean m_Negative;

    /** whether a nominal class is allowed. */
    private boolean m_Nominal;

    /** whether a numeric class is allowed. */
    private boolean m_Numeric;

    /**
     * initializes the measure with the given flags.
     *
     * @param negative	whether measures gets multiplied with -1
     * @param nominal	whether used for nominal classes
     * @param numeric	whether used for numeric classes
     */
    private Measure(boolean negative, boolean nominal, boolean numeric) {
      m_Negative = negative;
      m_Nominal  = nominal;
      m_Numeric  = numeric;
    }

    /**
     * Adjusts the measure value for sorting: either multiplies it with -1 or 1.
     *
     * @param measure	the raw measure
     * @return		the adjusted measure
     */
    public double adjust(double measure) {
      if (m_Negative)
	return -measure;
      else
	return measure;
    }

    /**
     * Checks whether the data can be used with this measure.
     *
     * @param data	the data to check
     * @return		true if the measure can be obtain for this kind of data
     */
    public boolean isValid(Instances data) {
      if (data.classIndex() == -1)
	throw new UnassignedClassException("No class attribute set!");

      if (data.classAttribute().isNominal())
	return m_Nominal;
      else if (data.classAttribute().isNumeric())
	return m_Numeric;
      else
	throw new IllegalStateException(
	    "Class attribute '" + data.classAttribute().type() + "' not handled!");
    }
  }

  /**
   * The default constructor.
   */
  public DarkLord() {
    super();
  }

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "The Dark Lord.";
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_BestFitness            = Double.NEGATIVE_INFINITY;
    m_FitnessChangeListeners = new HashSet<FitnessChangeListener>();
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
	    1);

    m_OptionManager.add(
	    "folds", "folds",
	    10);

    m_OptionManager.add(
	    "cv-seed", "crossValidationSeed",
	    55);

    m_OptionManager.add(
	    "serialized", "serializedModel",
	    new PlaceholderFile("."));

    m_OptionManager.add(
	    "classifier", "classifier",
	    new ZeroR());

    m_OptionManager.add(
	    "output-dir", "outputDirectory",
	    new PlaceholderDirectory("."));

    m_OptionManager.add(
	    "dataset", "dataset",
	    new PlaceholderFile("./data.arff"));

    m_OptionManager.add(
	    "class", "classIndex",
  	    "last");

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
   * Sets the filename of the dataset to use for cross-validation.
   *
   * @param value	the filename
   */
  public void setDataset(PlaceholderFile value) {
    m_Dataset = value;
    reset();
  }

  /**
   * Returns the currently set filename of the dataset for cross-validation.
   *
   * @return		the filename
   */
  public PlaceholderFile getDataset() {
    return m_Dataset;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String datasetTipText() {
    return "The dataset to use for cross-validation.";
  }

  /**
   * Sets the filename for the serialized classifier.
   *
   * @param value	the filename
   */
  public void setSerializedModel(PlaceholderFile value) {
    m_SerializedModel = value;
    reset();
  }

  /**
   * Returns the currently set filename for the serialized classifier.
   *
   * @return		the filename
   */
  public PlaceholderFile getSerializedModel() {
    return m_SerializedModel;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String serializedModelTipText() {
    return "The filename for the serialized classifier.";
  }

  /**
   * Sets the classifier to use (if no serialized model is used).
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
    return "The classifier to use if no serialized is supplied.";
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
   * Sets the class index.
   *
   * @param value	the class index
   */
  public void setClassIndex(String value) {
    m_ClassIndex = value;
    reset();
  }

  /**
   * Returns the current class index.
   *
   * @return		the class index
   */
  public String getClassIndex() {
    return m_ClassIndex;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String classIndexTipText() {
    return "The class index of the dataset ('first' and 'last' are accepted as well).";
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
   * Calculates the fitness of the population.
   */
  @Override
  public void calcFitness() {
    JobRunner<DarkLordJob> runner = new JobRunner<DarkLordJob>();
    JobList<DarkLordJob> jobs = new JobList<DarkLordJob>();
    for (int i = 0; i < getNumChrom(); i++) {
      int[] weights = new int[getNumGenes()];
      for (int j = 0; j < getNumGenes(); j++)  {
	int weight = 0;
	for (int k = 0; k < getBitsPerGene(); k++){
	  weight <<= 1;
	  if (getGene(i, (j*getBitsPerGene())+k))
	    weight += 1;
	}
	weights[j] = weight;
      }
      jobs.add(new DarkLordJob(this, i, weights));
    }
    runner.add(jobs);
    runner.start();
    runner.stop();

    for (int i = 0; i < jobs.size(); i++) {
      DarkLordJob job = jobs.get(i);
      // success? If not, just add the header of the original data
      if (job.getFitness() == null) {
	m_Fitness[job.getNumChrom()] = Double.NEGATIVE_INFINITY;
      }
      else {
	m_Fitness[job.getNumChrom()] = job.getFitness();
      }
      job.cleanUp();
    }
  }

  /**
   * Generates a Properties file that stores information on the setup of
   * the genetic algorithm. E.g., it backs up the original relation name.
   * The generated properties file will be used as new relation name for
   * the data.
   *
   * @param data	the data to create the setup for
   * @param job		the associated job
   * @see		#PROPS_RELATION
   * @return		the generated setup
   */
  @Override
  protected Properties storeSetup(Instances data, GeneticAlgorithmJob job) {
    Properties		result;
    DarkLordJob		jobDL;
    Remove		remove;

    result = super.storeSetup(data, job);
    jobDL  = (DarkLordJob) job;

    // mask string
    result.setProperty(PROPS_MASK, jobDL.getMaskAsString());

    // remove filter setup
    remove = new Remove();
    remove.setAttributeIndices(jobDL.getRemoveAsString());
    remove.setInvertSelection(true);
    result.setProperty(PROPS_FILTER, OptionUtils.getCommandLine(remove));

    return result;
  }

  /**
   * Some more initializations.
   */
  @Override
  protected void preRun() {
    FileReader	reader;
    int		classIndex;

    super.preRun();

    // loading the dataset
    try {
      reader      = new FileReader(m_Dataset.getAbsolutePath());
      m_Instances = new Instances(reader);
      reader.close();
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to read: " + m_Dataset, e);
      throw new IllegalStateException("Error loading dataset '" + m_Dataset + "': " + e);
    }

    // class index
    if (m_ClassIndex.equals("first"))
      classIndex = 0;
    else if (m_ClassIndex.equals("last"))
      classIndex = m_Instances.numAttributes() - 1;
    else
      classIndex = Integer.parseInt(m_ClassIndex);
    m_Instances.setClassIndex(classIndex);

    // does the measure handle the data?
    if (!m_Measure.isValid(m_Instances))
      throw new IllegalArgumentException(
	  "Measure '" + m_Measure + "' cannot process class of type '"
	  + m_Instances.classAttribute().type() + "'!");

    if (m_BestRange.getRange().length() != 0)
      m_BestRange.setMax(m_Instances.numAttributes());

    // setup structures
    init(20,m_Instances.numAttributes() * m_BitsPerGene);

    // reset timestamp of notification
    m_LastNotificationTime = null;

    // clear cache
    clearResults();
  }

  /**
   * Adds the given listener to its internal list of listeners.
   *
   * @param l		the listener to add
   */
  public void addFitnessChangeListener(FitnessChangeListener l) {
    m_FitnessChangeListeners.add(l);
  }

  /**
   * Removes the given listener from its internal list of listeners.
   *
   * @param l		the listener to remove
   */
  public void removeFitnessChangeListener(FitnessChangeListener l) {
    m_FitnessChangeListeners.remove(l);
  }

  /**
   * Notifies all the fitness change listeners of a change.
   *
   * @param e		the event to send
   */
  protected void notifyFitnessChangeListeners(FitnessChangeEvent e) {
    Iterator<FitnessChangeListener>	iter;

    iter = m_FitnessChangeListeners.iterator();
    while (iter.hasNext())
      iter.next().fitnessChanged(e);
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
