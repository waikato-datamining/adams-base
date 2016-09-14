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
 * Hermione.java
 * Copyright (C) 2015-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.opt.genetic;

import adams.core.Properties;
import adams.core.discovery.genetic.AbstractGeneticDiscoveryHandler;
import adams.core.discovery.DefaultPropertyDiscovery;
import adams.core.discovery.PropertyPath;
import adams.core.discovery.PropertyPath.PropertyContainer;
import adams.core.option.OptionUtils;
import weka.classifiers.Classifier;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Hermione.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-num-threads &lt;int&gt; (property: numThreads)
 * &nbsp;&nbsp;&nbsp;The number of threads to use for executing the jobs; use -1 for all available 
 * &nbsp;&nbsp;&nbsp;cores.
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 *
 * <pre>-num-chrom &lt;int&gt; (property: numChrom)
 * &nbsp;&nbsp;&nbsp;The number of chromosomes, ie, the population size.
 * &nbsp;&nbsp;&nbsp;default: 50
 * </pre>
 *
 * <pre>-stopping-criterion &lt;adams.genetic.stopping.AbstractStoppingCriterion&gt; (property: stoppingCriterion)
 * &nbsp;&nbsp;&nbsp;The stopping criterion to use.
 * &nbsp;&nbsp;&nbsp;default: adams.genetic.stopping.MaxIterations
 * </pre>
 *
 * <pre>-seed &lt;long&gt; (property: seed)
 * &nbsp;&nbsp;&nbsp;The seed value for the random number generator.
 * &nbsp;&nbsp;&nbsp;default: 1
 * </pre>
 *
 * <pre>-initial-setups-provider &lt;adams.genetic.initialsetups.AbstractInitialSetupsProvider&gt; (property: initialSetupsProvider)
 * &nbsp;&nbsp;&nbsp;The initial setups provider to use.
 * &nbsp;&nbsp;&nbsp;default: adams.genetic.initialsetups.EmptyInitialSetupsProvider
 * </pre>
 *
 * <pre>-initial-weights &lt;java.lang.String&gt; (property: initialWeights)
 * &nbsp;&nbsp;&nbsp;The initial weights to use, rather than random ones (string of 0s and 1s
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 *
 * <pre>-favor-zeroes &lt;boolean&gt; (property: favorZeroes)
 * &nbsp;&nbsp;&nbsp;Whether to favor 0s instead of 1s.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-best &lt;java.lang.String&gt; (property: bestRange)
 * &nbsp;&nbsp;&nbsp;The range of the best attributes.
 * &nbsp;&nbsp;&nbsp;default: -none-
 * </pre>
 *
 * <pre>-notify &lt;int&gt; (property: notificationInterval)
 * &nbsp;&nbsp;&nbsp;The time interval in seconds after which notification events about changes 
 * &nbsp;&nbsp;&nbsp;in the fitness can be sent (-1 = never send notifications; 0 = whenever 
 * &nbsp;&nbsp;&nbsp;a change occurs).
 * &nbsp;&nbsp;&nbsp;default: -1
 * </pre>
 *
 * <pre>-bits-per-gene &lt;int&gt; (property: bitsPerGene)
 * &nbsp;&nbsp;&nbsp;The number of bits per gene to use.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-class &lt;adams.data.weka.WekaAttributeIndex&gt; (property: classIndex)
 * &nbsp;&nbsp;&nbsp;The class index of the dataset, in case no class attribute is set.
 * &nbsp;&nbsp;&nbsp;default: last
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; apart from attribute names (case-sensitive), the following placeholders can be used as well: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); attribute names can be surrounded by double quotes.
 * </pre>
 *
 * <pre>-folds &lt;int&gt; (property: folds)
 * &nbsp;&nbsp;&nbsp;The number of folds to use in cross-validation.
 * &nbsp;&nbsp;&nbsp;default: 10
 * &nbsp;&nbsp;&nbsp;minimum: 2
 * </pre>
 *
 * <pre>-cv-seed &lt;int&gt; (property: crossValidationSeed)
 * &nbsp;&nbsp;&nbsp;The seed value for cross-validation.
 * &nbsp;&nbsp;&nbsp;default: 55
 * </pre>
 *
 * <pre>-classifier &lt;weka.classifiers.Classifier&gt; (property: classifier)
 * &nbsp;&nbsp;&nbsp;The classifier to use.
 * &nbsp;&nbsp;&nbsp;default: weka.classifiers.rules.ZeroR
 * </pre>
 *
 * <pre>-measure &lt;CC|RMSE|RRSE|MAE|RAE|ACC&gt; (property: measure)
 * &nbsp;&nbsp;&nbsp;The measure used for evaluating the fitness.
 * &nbsp;&nbsp;&nbsp;default: RMSE
 * </pre>
 *
 * <pre>-output-dir &lt;adams.core.io.PlaceholderDirectory&gt; (property: outputDirectory)
 * &nbsp;&nbsp;&nbsp;The directory for storing the generated ARFF files.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 *
 * <pre>-output-type &lt;NONE|SETUP|DATA|ALL&gt; (property: outputType)
 * &nbsp;&nbsp;&nbsp;The type of output to generate.
 * &nbsp;&nbsp;&nbsp;default: SETUP
 * </pre>
 *
 * <pre>-output-prefix-type &lt;NONE|RELATION|SUPPLIED&gt; (property: outputPrefixType)
 * &nbsp;&nbsp;&nbsp;The type of prefix to use for the output.
 * &nbsp;&nbsp;&nbsp;default: NONE
 * </pre>
 *
 * <pre>-supplied-prefix &lt;java.lang.String&gt; (property: suppliedPrefix)
 * &nbsp;&nbsp;&nbsp;The prefix to use in case of SUPPLIED.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 *
 * <pre>-use-second-evaluation &lt;boolean&gt; (property: useSecondEvaluation)
 * &nbsp;&nbsp;&nbsp;If enabled, a second evaluation is performed using the separate folds and 
 * &nbsp;&nbsp;&nbsp;seed.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-second-folds &lt;int&gt; (property: secondFolds)
 * &nbsp;&nbsp;&nbsp;The number of folds to use in cross-validation (second evaluation).
 * &nbsp;&nbsp;&nbsp;default: 10
 * &nbsp;&nbsp;&nbsp;minimum: 2
 * </pre>
 *
 * <pre>-second-cv-seed &lt;int&gt; (property: secondCrossValidationSeed)
 * &nbsp;&nbsp;&nbsp;The seed value for cross-validation (second evaluation).
 * &nbsp;&nbsp;&nbsp;default: 42
 * </pre>
 *
 * <pre>-handler &lt;adams.core.discovery.AbstractGeneticDiscoveryHandler&gt; [-handler ...] (property: handlers)
 * &nbsp;&nbsp;&nbsp;The discovery handlers to use.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 *
 <!-- options-end -->
 *
 * @author Dale (dale at cs dot waikato dot ac dot nz)
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 4322 $
 */
public class Hermione
  extends AbstractClassifierBasedGeneticAlgorithmWithSecondEvaluation {

  private static final long serialVersionUID = -4982024446995877986L;

  /** the handlers to use for discovery. */
  protected AbstractGeneticDiscoveryHandler[] m_Handlers;

  protected List<Integer> m_numbits=null;

  protected List<Integer> m_start=null;

  /**
   * A job class specific to Hermione.
   *
   * @author  dale
   * @version $Revision: 4322 $
   */
  public static class HermioneJob
    extends ClassifierBasedGeneticAlgorithmWithSecondEvaluationJob<Hermione> {

    /** for serialization. */
    private static final long serialVersionUID = 8259167463381721274L;

    /**
     * Initializes the job.
     *
     * @param g		the algorithm object this job belongs to
     * @param num	the number of chromsomes
     * @param w		the initial weights
     * @param data	the data to use
     */
    public HermioneJob(Hermione g, int num, int[] w, Instances data) {
      super(g, num, w, data);
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
      Properties 				result;
      int 					pos;
      List<Integer> 				numbits;
      List<Integer> 				start;
      AbstractGeneticDiscoveryHandler[] 	handlers;
      DefaultPropertyDiscovery 			discovery;
      List<PropertyPath.PropertyContainer> 	conts;
      String					strArray;

      result = super.assembleSetup(fitness, cls, chromosome, weights);

      // store individual weights
      pos      = 0;
      handlers = new AbstractGeneticDiscoveryHandler[getOwner().getHandlers().length];
      for (int i = 0; i < getOwner().getHandlers().length; i++)
	handlers[i] = (AbstractGeneticDiscoveryHandler) getOwner().getHandlers()[i].shallowCopy();
      discovery = new DefaultPropertyDiscovery();
      discovery.setLoggingLevel(getLoggingLevel());
      discovery.discover(handlers, cls);
      numbits  = getOwner().getNumBitsForAll(handlers);
      start    = getOwner().getStartPoints(handlers);
      for (AbstractGeneticDiscoveryHandler handler : handlers) {
	conts = handler.getContainers();
	for (PropertyPath.PropertyContainer cont : conts) {
	  strArray = getOwner().intArrayToString(getOwner().getBitsForPosition(weights, start, numbits, pos));
	  result.setProperty("Weights." + pos, strArray);
	  pos++;
	}
      }

      return result;
    }

    /**
     * Calculates the new fitness.
     */
    @Override
    public void calcNewFitness() {
      boolean		canAdd;
      Double 		measure;
      String		weightsStr;
      Instances 	newInstances;
      Classifier 	newClassifier;
      Classifier 	newSecondClassifier;

      weightsStr = weightsToString();
      if (isLoggingEnabled())
	getLogger().fine((new StringBuilder("calc for: ")).append(weightsStr).toString());

      try {
	// was measure already calculated for this attribute setup?
	measure = getOwner().getResult(weightsStr);
	if (measure != null) {
	  if (isLoggingEnabled())
	    getLogger().info("Already present: " + measure);
	  m_Fitness = measure;
	  return;
	}

	newInstances = new Instances(getInstances());

	// evaluate classifier
	newClassifier = getOwner().generateClassifier(m_Chromosome, m_Weights);
	m_Fitness     = evaluateClassifier(newClassifier, newInstances, getFolds(), getSeed());

	// process fitness
	if (getOwner().isBetterFitness(m_Fitness)) {
	  canAdd = true;

	  // second evaluation?
	  if (getUseSecondEvaluation()) {
	    newSecondClassifier = getOwner().generateClassifier(m_Chromosome, m_Weights);
	    m_SecondFitness = evaluateClassifier(newSecondClassifier , newInstances, getSecondFolds(), getSecondSeed());
	    canAdd = getOwner().isSecondBetterFitness(m_SecondFitness);
	    if (getOwner().setSecondNewFitness(m_SecondFitness, newSecondClassifier, m_Chromosome, m_Weights)) {
	      if (isLoggingEnabled())
		getLogger().info("Second evaluation is also better: " + m_SecondFitness);
	    }
	    else {
	      if (isLoggingEnabled())
		getLogger().info("Second evaluation is not better: " + m_SecondFitness);
	    }
	    getOwner().addSecondResult(weightsStr, m_SecondFitness);
	  }

	  if (canAdd && getOwner().setNewFitness(m_Fitness, newClassifier, m_Chromosome, m_Weights)) {
	    generateOutput(m_Fitness, newInstances, newClassifier, m_Chromosome, m_Weights);
	    // notify the listeners
	    getOwner().notifyFitnessChangeListeners(getMeasure().adjust(m_Fitness), newClassifier, m_Weights);
	  }
	}

	getOwner().addResult(weightsStr, m_Fitness);
      }
      catch(Exception e) {
	getLogger().log(Level.SEVERE, "Error: ", e);
	m_Fitness = null;
	if (getUseSecondEvaluation())
	  m_SecondFitness = null;
      }
    }
  }

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Hermione.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "handler", "handlers",
      new AbstractGeneticDiscoveryHandler[0]);
  }

  /**
   * Sets the discovery handlers to use.
   *
   * @param value	the classifier
   */
  public void setHandlers(AbstractGeneticDiscoveryHandler[] value) {
    m_Handlers = value;
    reset();
  }

  /**
   * Returns the currently set discovery handlers.
   *
   * @return		the handlers
   */
  public AbstractGeneticDiscoveryHandler[] getHandlers() {
    return m_Handlers;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String handlersTipText() {
    return "The discovery handlers to use.";
  }

  /**
   * Returns the default output type to use.
   *
   * @return		the type
   */
  protected OutputType getDefaultOutputType() {
    return OutputType.SETUP;
  }

  /**
   * Creates a new Job instance.
   *
   * @param chromosome		the number of chromosomes
   * @param w		the initial weights
   * @return		the instance
   * @param data	the data to use
   */
  protected HermioneJob newJob(int chromosome, int[] w, Instances data) {
    return new HermioneJob(this, chromosome, w, data);
  }

  /**
   * Some more initializations.
   */
  @Override
  protected void preRun() {
    super.preRun();

    AbstractGeneticDiscoveryHandler[] handlers = new AbstractGeneticDiscoveryHandler[m_Handlers.length];
    for (int i = 0; i < m_Handlers.length; i++)
      handlers[i] = (AbstractGeneticDiscoveryHandler) m_Handlers[i].shallowCopy();
    DefaultPropertyDiscovery discovery = new DefaultPropertyDiscovery();
    discovery.discover(handlers, OptionUtils.shallowCopy(m_Classifier));

    m_numbits = getNumBitsForAll(handlers);
    m_start   = getStartPoints(handlers);

    init(m_NumChrom, getNumBits(handlers) * m_BitsPerGene);

    int pos = 0;
    int[] dummyWeights = new int[getNumBits(handlers)];
    for (AbstractGeneticDiscoveryHandler handler : handlers) {
      List<PropertyPath.PropertyContainer> conts = handler.getContainers();
      for (PropertyPath.PropertyContainer cont : conts) {
        if (handler.requiresInitialization())
          handler.performInitialization(this, cont);
        String strArray = handler.pack(cont);
        int[] newWeights = stringToIntArray(strArray);
        for (int i = 0; i < m_ActualNumChrom; i++)
          setBitsForPosition(i, dummyWeights, m_start, m_numbits, pos, newWeights);
        pos++;
      }
    }

    if (isLoggingEnabled()) {
      for (int i = 0; i < handlers.length; i++) {
        getLogger().info((i+1) + ". " + OptionUtils.getCommandLine(handlers[i]));
        for (PropertyContainer cont: handlers[i].getContainers())
          getLogger().info("   " + cont.getPath());
      }
    }
  }

  /**
   * Converts the bit string into an int array.
   *
   * @param s		the string to convert
   * @return		the bit array
   */
  public int[] stringToIntArray(String s) {
    int[]	result;
    int		i;

    result = new int[s.length()];
    for (i = 0; i < result.length; i++) {
      if (s.charAt(i) == '0')
	result[i] = 0;
      else
	result[i] = 1;
    }

    return result;
  }

  /**
   * Int array of bits to string.
   *
   * @param ia		the int array
   * @return		the generated bit string
   */
  public String intArrayToString(int[] ia) {
    StringBuilder result = new StringBuilder();
    for (int i = 0; i < ia.length; i++)
      result.append("" + ia[i]);
    return result.toString();
  }

  /**
   * Updates the bits.
   *
   * @param chromosome	the chromosome this is for
   * @param weights	the overall weights
   * @param starts	the starting points
   * @param numbits	the number of bits
   * @param pos		the position
   * @param newWeights	the new weights to set
   */
  public void setBitsForPosition(int chromosome, int[] weights, List<Integer> starts, List<Integer> numbits, int pos, int[] newWeights) {
    int c = 0;
    for (int i = starts.get(pos); i < starts.get(pos) + numbits.get(pos); i++) {
      weights[i] = newWeights[c];
      if (newWeights[c] == 1)
	m_Genes[chromosome].set(c);
      else
	m_Genes[chromosome].clear(c);
      c++;
    }
  }

  /**
   * get bit array for parameter at pos.
   *
   * @param weights	the overall weights
   * @param starts	the starting points
   * @param numbits	the number of bits
   * @param pos		the position
   * @return		the weights subset
   */
  public int[] getBitsForPosition(int[] weights, List<Integer> starts, List<Integer> numbits, int pos) {
    int[] ret=new int[numbits.get(pos)];
    int c=0;
    for (int i= starts.get(pos);i< starts.get(pos)+numbits.get(pos);i++) {
      ret[c++]= weights[i];
    }
    return(ret);
  }

  /**
   * Get List containing number of bits used for params
   *
   * @return		the list of number of bits
   */
  public List<Integer> getNumBitsForAll(AbstractGeneticDiscoveryHandler[] handlers) {
    ArrayList<Integer> al=new ArrayList<>();
    for (AbstractGeneticDiscoveryHandler handler : handlers) {
      List<PropertyPath.PropertyContainer> conts = handler.getContainers();
      for (PropertyPath.PropertyContainer cont : conts) {
	al.add(handler.getNumBits());
      }
    }

    return(al);
  }

  /**
   * Get List of start positions in bit string
   *
   * @return		the starting positions
   */
  public List<Integer> getStartPoints(AbstractGeneticDiscoveryHandler[] handlers) {
    ArrayList<Integer> al=new ArrayList<>();
    int count=0;
    for (AbstractGeneticDiscoveryHandler handler : handlers) {
      List<PropertyPath.PropertyContainer> conts = handler.getContainers();
      for (PropertyPath.PropertyContainer cont : conts) {
	al.add(count);
	count += handler.getNumBits();
      }
    }

    return(al);
  }

  /**
   * Get total number of bits for params
   *
   * @return		the total number of bits
   */
  public int getNumBits(AbstractGeneticDiscoveryHandler[] handlers) {
    int count=0;
    for (AbstractGeneticDiscoveryHandler handler : handlers) {
      List<PropertyPath.PropertyContainer> conts = handler.getContainers();
      for (PropertyPath.PropertyContainer cont : conts) {
	count += handler.getNumBits();
      }
    }
    return(count);
  }

  /**
   * Generate the classifier from current bit array
   *
   * @param chromosome	the chromosome index
   * @param weights	the bit array
   * @return		the classifier
   */
  public Classifier generateClassifier(int chromosome, int weights[]) {
    if (isLoggingEnabled()) {
      StringBuilder w = new StringBuilder();
      for (int i = 0; i < weights.length; i++)
	w.append(weights[i]);
      getLogger().info("[generateClassifier] Chromosome: " + chromosome + ", " + "Weights: " + w);
    }

    AbstractGeneticDiscoveryHandler handlers[] = new AbstractGeneticDiscoveryHandler[m_Handlers.length];
    for (int i=0;i< handlers.length;i++)
      handlers[i]=(AbstractGeneticDiscoveryHandler) m_Handlers[i].shallowCopy();

    Classifier result;
    result = (Classifier) OptionUtils.shallowCopy(getClassifier());
    if (result == null) {
      getLogger().severe("Failed to copy classifier!");
      return null;
    }
    DefaultPropertyDiscovery discovery = new DefaultPropertyDiscovery();
    discovery.discover(handlers, result);

    // apply weights
    int pos=0;
    for (AbstractGeneticDiscoveryHandler handler : handlers) {
      List<PropertyPath.PropertyContainer> conts = handler.getContainers();
      for (PropertyPath.PropertyContainer cont : conts) {
	String strArray = intArrayToString(getBitsForPosition(weights, m_start, m_numbits, pos));
	handler.unpack(cont, strArray);
	pos++;
      }
    }

    return result;
  }
}
