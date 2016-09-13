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

package adams.opt.genetic;

import adams.core.Properties;
import adams.core.ThreadLimiter;
import adams.core.io.PlaceholderDirectory;
import adams.core.option.OptionUtils;
import adams.data.weka.WekaAttributeIndex;
import adams.event.GeneticFitnessChangeNotifier;
import adams.flow.core.Actor;
import adams.flow.standalone.JobRunnerSetup;
import adams.multiprocess.JobList;
import adams.multiprocess.JobRunner;
import adams.multiprocess.LocalJobRunner;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.rules.ZeroR;
import weka.core.Instances;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Hashtable;
import java.util.Random;

/**
 * Ancestor for genetic algorithms that evaluate classifiers.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractClassifierBasedGeneticAlgorithm
  extends AbstractGeneticAlgorithm
  implements GeneticFitnessChangeNotifier {

  private static final long serialVersionUID = 1615849384907266578L;

  /**
   * Job class for algorithms with datasets.
   *
   * @author  dale
   * @version $Revision: 4322 $
   */
  public static abstract class ClassifierBasedGeneticAlgorithmJob<T extends AbstractClassifierBasedGeneticAlgorithm>
    extends GeneticAlgorithmJob<T> {

    /** for serialization. */
    private static final long serialVersionUID = 8259167463381721274L;

    /** the measure to use for evaluating the fitness. */
    protected Measure m_Measure;

    /** the data to use. */
    protected Instances m_Data;

    /** the cross-validation seed. */
    protected int m_Seed;

    /** the cross-validation folds. */
    protected int m_Folds;

    /**
     * Initializes the job.
     *
     * @param g   	the algorithm object this job belongs to
     * @param chromosome the chromsome index
     * @param w   	the initial weights
     * @param data	the data to use
     */
    public ClassifierBasedGeneticAlgorithmJob(T g, int chromosome, int[] w, Instances data) {
      super(g, chromosome, w);

      m_Measure = g.getMeasure();
      m_Data    = data;
      m_Seed    = g.getCrossValidationSeed();
      m_Folds   = g.getFolds();
    }

    /**
     * Returns the instances in use by the genetic algorithm.
     *
     * @return		the instances
     */
    protected Instances getInstances() {
      return m_Data;
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
     * Returns the cross-validation seed.
     *
     * @return		the seed
     */
    public int getSeed() {
      return m_Seed;
    }

    /**
     * Returns the number of cross-validation folds.
     *
     * @return		the number of folds
     */
    public int getFolds() {
      return m_Folds;
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
          result = "No instances provided!";
      }

      return result;
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
      Evaluation 	evaluation;

      evaluation = new Evaluation(data);
      evaluation.crossValidateModel(cls, data, folds, new Random(seed));

      return getMeasure().extract(evaluation, true);
    }

    /**
     * Generates a file name for the fitness.
     *
     * @param fitness	the current fitness
     * @param data	the dataset
     * @param ext	the extension (not dot!)
     * @return		the file
     */
    protected File createFileName(double fitness, Instances data, String ext) {
      String	filename;

      filename = getOwner().getOutputDirectory().getAbsolutePath() + File.separator;

      switch (getOwner().getOutputPrefixType()) {
	case NONE:
	  break;
	case RELATION:
	  filename += data.relationName() + "-";
	  break;
	case SUPPLIED:
	  filename += getOwner().getSuppliedPrefix() + "-";
	  break;
	default:
	  throw new IllegalStateException("Unhandled output prefix type: " + getOwner().getOutputPrefixType());
      }

      filename += Double.toString(getMeasure().adjust(fitness)) + "." + ext;

      return new File(filename);
    }

    /**
     * Saves the instances to a file.
     *
     * @param fitness		the current measure/fitness
     * @param data		the instances to save
     * @throws Exception	if saving the file fails
     */
    protected void outputDataset(double fitness, Instances data) throws Exception {
      File file = createFileName(fitness, data, "arff");
      Writer writer = new BufferedWriter(new FileWriter(file));
      Instances header = new Instances(data, 0);
      header = getOwner().updateHeader(header, this);
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
     * @param fitness		the current fitness
     * @param cls		the current classifier
     * @param chromosome	the chromosome responsible
     * @param weights		the weights
     * @return			the data
     */
    protected Properties assembleSetup(double fitness, Classifier cls, int chromosome, int[] weights) {
      Properties result;

      result = new Properties();
      result.setProperty("Commandline", OptionUtils.getCommandLine(getOwner()));
      result.setProperty("Measure", "" + getMeasure());
      result.setDouble("Fitness", fitness);
      result.setProperty("Setup", OptionUtils.getCommandLine(cls));
      result.setInteger("Chromosome", chromosome);
      result.setProperty("Weights", weightsToString(weights));

      return result;
    }

    /**
     * Saves the setup to a props file.
     *
     * @param fitness		the current measure/fitness
     * @param data		the dataset
     * @param cls		the current classifier setup
     * @param chromosome	the chromosome responsible
     * @param weights		the current weights/bits
     * @throws Exception	if saving the file fails
     */
    protected void outputSetup(double fitness, Instances data, Classifier cls, int chromosome, int[] weights) throws Exception {
      File 		file;
      Properties 	props;

      file  = createFileName(fitness, data, "props.gz");
      props = assembleSetup(fitness, cls, chromosome, weights);
      if (!props.save(file.getAbsolutePath()))
	getLogger().warning("Failed to write setup to '" + file + "'!");
    }

    /**
     * Generates the output requested output.
     *
     * @param fitness		the current fitness
     * @param data		the dataset
     * @param cls		the current classifier
     * @param chromosome	the chromosome responsible
     * @param weights		the current weights/bits
     * @throws Exception	if output fails
     */
    protected void generateOutput(double fitness, Instances data, Classifier cls, int chromosome, int[] weights) throws Exception {
      switch (getOwner().getOutputType()) {
	case NONE:
	  break;
	case SETUP:
	  outputSetup(fitness, data, cls, chromosome, weights);
	  break;
	case DATA:
	  outputDataset(fitness, data);
	  break;
	case ALL:
	  outputDataset(fitness, data);
	  outputSetup(fitness, data, cls, chromosome, weights);
	  break;
	default:
	  throw new IllegalStateException("Unhandled output type: " + getOwner().getOutputType());
      }
    }
  }

  /** the key for the relation name in the generated properties file.
   * @see #storeSetup(Instances,GeneticAlgorithmJob). */
  public final static String PROPS_RELATION = "relation";

  /** the key for a filter setup in the setup properties. */
  public final static String PROPS_FILTER = "filter";

  /** the key for the mask in the setup properties. */
  public final static String PROPS_MASK = "mask";

  /** the class index. */
  protected WekaAttributeIndex m_ClassIndex;

  /** the data to use for cross-validation. */
  protected Instances m_Instances;

  /** the bits per gene to use. */
  protected int m_BitsPerGene;

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
  public Hashtable<String,Double> m_StoredResults = new Hashtable<String,Double>();

  /** the jobrunner setup. */
  protected transient JobRunnerSetup m_JobRunnerSetup;

  /** the flow context. */
  protected Actor m_FlowContext;

  /** the job runner in use. */
  protected JobRunner<ClassifierBasedGeneticAlgorithmJob> m_JobRunner;

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
      "class", "classIndex",
      new WekaAttributeIndex(WekaAttributeIndex.LAST));

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
   * Sets the class index.
   *
   * @param value	the class index
   */
  public void setClassIndex(WekaAttributeIndex value) {
    m_ClassIndex = value;
    reset();
  }

  /**
   * Returns the current class index.
   *
   * @return		the class index
   */
  public WekaAttributeIndex getClassIndex() {
    return m_ClassIndex;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String classIndexTipText() {
    return "The class index of the dataset, in case no class attribute is set.";
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
   * Sets the jobrunner setup to use.
   *
   * @param value	the setup, can be null to use default
   */
  public void setJobRunnerSetup(JobRunnerSetup value) {
    m_JobRunnerSetup = value;
  }

  /**
   * Returns the jobrunner setup in use.
   *
   * @return		the setup, null if using default
   */
  public JobRunnerSetup getJobRunnerSetup() {
    return m_JobRunnerSetup;
  }

  /**
   * Sets the flow context, if any.
   *
   * @param value	the context
   */
  public void setFlowContext(Actor value) {
    m_FlowContext = value;
  }

  /**
   * Return the flow context, if any.
   *
   * @return		the context, null if none available
   */
  public Actor getFlowContext() {
    return m_FlowContext;
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
   * Generates a Properties file that stores information on the setup of
   * the genetic algorithm. E.g., it backs up the original relation name.
   * The generated properties file will be used as new relation name for
   * the data. Derived classes can add additional parameters to this
   * properties file.
   *
   * @param data	the data to create the setup for
   * @param job		the associated job
   * @see		#PROPS_RELATION
   * @return		the generated setup
   */
  protected Properties storeSetup(Instances data, GeneticAlgorithmJob job) {
    Properties	result;

    result = new Properties();

    // relation name
    result.setProperty(PROPS_RELATION, data.relationName());

    // filter (default is empty)
    result.setProperty(PROPS_FILTER, "");

    return result;
  }

  /**
   * Creates a new dataset, with the setup as the new relation name.
   *
   * @param data	the data to replace the relation name with the setup
   * @param job		the associated job
   * @return		the updated dataset
   */
  public Instances updateHeader(Instances data, GeneticAlgorithmJob job) {
    Properties 	props;

    props = storeSetup(data, job);
    data.setRelationName(props.toString());

    return data;
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

  /**
   * Creates a new Job instance.
   *
   * @param chromosome	the chromosome index
   * @param w		the initial weights
   * @return		the instance
   * @param data	the data to use
   */
  protected abstract ClassifierBasedGeneticAlgorithmJob newJob(int chromosome, int[] w, Instances data);

  /**
   * Calculates the fitness of the population.
   */
  @Override
  public void calcFitness() {
    JobList<ClassifierBasedGeneticAlgorithmJob> 	jobs;
    ClassifierBasedGeneticAlgorithmJob 			job;
    int 						i;
    int 						k;
    int[] 						weights;
    int 						weight;
    StringBuilder					weightStr;

    if (m_JobRunnerSetup == null)
      m_JobRunner = new LocalJobRunner<>();
    else
      m_JobRunner = m_JobRunnerSetup.newInstance();
    if (m_JobRunner instanceof ThreadLimiter)
      ((ThreadLimiter) m_JobRunner).setNumThreads(getNumThreads());
    m_JobRunner.setFlowContext(getFlowContext());
    jobs   = new JobList<>();
    for (i = 0; i < getNumChrom(); i++) {
      weights   = new int[getNumGenes()];
      weightStr = new StringBuilder();
      for (int j = 0; j < getNumGenes(); j++)  {
        weight = 0;
        for (k = 0; k < getBitsPerGene(); k++){
          weight <<= 1;
          if (getGene(i, (j*getBitsPerGene())+k))
            weight += 1;
        }
        weights[j] = weight;
	weightStr.append("" + weight);
      }
      if (isLoggingEnabled())
	getLogger().info("[" + m_CurrentIteration + "] before job: Chromosome " + i + " " + weightStr.toString());
      jobs.add(newJob(i, weights, m_Instances));
    }
    m_JobRunner.add(jobs);
    m_JobRunner.start();
    m_JobRunner.stop();

    for (i = 0; i < m_JobRunner.getJobs().size(); i++) {
      job = m_JobRunner.getJobs().get(i);
      // success? If not, just add the header of the original data
      if ((job.getFitness() == null) || m_Stopped)
        m_Fitness[job.getChromosome()] = Double.NEGATIVE_INFINITY;
      else
        m_Fitness[job.getChromosome()] = job.getFitness();
      job.cleanUp();
    }
    m_JobRunner.cleanUp();
    m_JobRunner.stop();

    m_JobRunner = null;
  }

  /**
   * Some more initializations.
   */
  @Override
  protected void preRun() {
    super.preRun();

    // class index?
    m_ClassIndex.setData(m_Instances);
    if (m_Instances.classIndex() == -1)
      m_Instances.setClassIndex(m_ClassIndex.getIntIndex());

    if (m_BestRange.getRange().length() != 0)
      m_BestRange.setMax(m_Instances.numAttributes());

    // does the measure handle the data?
    if (!m_Measure.isValid(m_Instances))
      throw new IllegalArgumentException(
        "Measure '" + m_Measure + "' cannot process class of type '"
          + m_Instances.classAttribute().type() + "'!");

    // clear cache
    clearResults();
  }

  /**
   * Stops the execution of the algorithm.
   */
  @Override
  public void stopExecution() {
    super.stopExecution();
    if (m_JobRunner != null)
      m_JobRunner.terminate();
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
