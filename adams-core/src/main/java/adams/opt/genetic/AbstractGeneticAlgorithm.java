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
 * AbstractGeneticAlgorithm.java
 * Copyright (C) 2009-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.opt.genetic;

import adams.core.ClassLister;
import adams.core.Pausable;
import adams.core.Randomizable;
import adams.core.Range;
import adams.core.ShallowCopySupporter;
import adams.core.StoppableWithFeedback;
import adams.core.ThreadLimiter;
import adams.core.Utils;
import adams.core.logging.LoggingHelper;
import adams.core.option.AbstractOptionConsumer;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.ArrayConsumer;
import adams.core.option.OptionUtils;
import adams.env.Environment;
import adams.event.GeneticFitnessChangeEvent;
import adams.event.GeneticFitnessChangeListener;
import adams.opt.genetic.initialsetups.AbstractInitialSetupsProvider;
import adams.opt.genetic.initialsetups.EmptyInitialSetupsProvider;
import adams.opt.genetic.stopping.AbstractStoppingCriterion;
import adams.opt.genetic.stopping.MaxIterations;
import adams.multiprocess.AbstractJob;
import adams.multiprocess.JobWithOwner;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

/**
 * Base class for genetic algorithms.
 *
 * @author Dale (dale at cs dot waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractGeneticAlgorithm
  extends AbstractOptionHandler
  implements Randomizable, StoppableWithFeedback, Pausable, ThreadLimiter,
             ShallowCopySupporter<AbstractGeneticAlgorithm> {

  /** for serialization. */
  private static final long serialVersionUID = 2823734145266194843L;

  /**
   * A job class specific to genetic algorithms.
   *
   * @author  dale
   * @version $Revision$
   */
  public static abstract class GeneticAlgorithmJob<T extends AbstractGeneticAlgorithm>
    extends AbstractJob
    implements JobWithOwner<T> {

    /** for serialization. */
    private static final long serialVersionUID = -4974865548501195622L;

    /** the weights of the chromosomes (0 = turned off, 1 = turned on). */
    protected int[] m_Weights;

    /** the algorithm object this job belongs to. */
    protected T m_Genetic;

    /** the current fitness. */
    protected Double m_Fitness;

    /** the chromosome index. */
    protected int m_Chromosome;

    /**
     * Initializes the job.
     *
     * @param g		the algorithm object this job belongs to
     * @param chromosome the chromsome index
     * @param w		the initial weights
     */
    public GeneticAlgorithmJob(T g, int chromosome, int[] w) {
      super();

      m_Weights  = w;
      m_Genetic  = g;
      m_Chromosome = chromosome;
      m_Fitness  = null;
    }

    /**
     * Returns the algorithm this job belongs to.
     *
     * @return		the owner
     */
    public T getOwner() {
      return m_Genetic;
    }

    /**
     * Returns the current weights of the attributes.
     *
     * @return		the weights
     */
    public int[] getWeights() {
      return m_Weights;
    }

    /**
     * Returns the chromosome index.
     *
     * @return		the index
     */
    public int getChromosome() {
      return m_Chromosome;
    }

    /**
     * Returns the fitness.
     *
     * @return		the fitness
     */
    public Double getFitness() {
      return m_Fitness;
    }

    /**
     * Turns the weights into a string representation.
     *
     * @return		the weights as string
     */
    public String weightsToString() {
      return weightsToString(m_Weights);
    }

    /**
     * Turns the weights into a string representation.
     *
     * @param weights	the weights to turn into string
     * @return		the weights as string
     */
    public static String weightsToString(int[] weights) {
      StringBuilder 	result;

      result = new StringBuilder();
      for (int weight: weights) {
	if (weight == 0)
	  result.append("0");
	else
	  result.append("1");
      }

      return result.toString();
    }

    /**
     * Calculates the new fitness.
     */
    public abstract void calcNewFitness();

    /**
     * Checks whether all pre-conditions have been met.
     *
     * @return		null if everything is OK, otherwise an error message
     */
    @Override
    protected String preProcessCheck() {
      if (m_Genetic == null)
	return("Doesn't belong to genetic algorithm!");
      else
	return null;
    }

    /**
     * Does the actual execution of the job.
     * 
     * @throws Exception if fails to execute job
     */
    @Override
    protected void process() throws Exception {
      calcNewFitness();
    }

    /**
     * Checks whether all post-conditions have been met.
     *
     * @return		always null
     */
    @Override
    protected String postProcessCheck() {
      return null;
    }

    /**
     * Cleans up data structures, frees up memory.
     * Sets the weights and the genetic algorithm to null.
     */
    @Override
    public void cleanUp() {
      super.cleanUp();

      m_Weights = null;
      m_Genetic = null;
    }

    /**
     * Returns a string representation of the job.
     *
     * @return		a string representation
     */
    @Override
    public String toString() {
      return
          m_Genetic.getClass().getName()
        + ",#chrom=" + m_Chromosome
        + ",fitness=" + m_Fitness
        + ",weights=" + weightsToString();
    }
  }

  /** the number of threads to use (-1 for #of cores). */
  protected int m_NumThreads;

  /** number of genes per chromosome.
   * NB: must be initialized by the algorithm! */
  protected int m_NumGenes;

  /** number of chromosomes. */
  protected int m_NumChrom;

  /** actual number of chromosomes. */
  protected int m_ActualNumChrom;

  /** the stopping criterion. */
  protected AbstractStoppingCriterion m_StoppingCriterion;

  /** the current iteration. */
  protected int m_CurrentIteration;

  /** the genes. */
  protected BitSet[] m_Genes;

  /** the fitness of the genes. */
  protected double[] m_Fitness;

  /** stores the best range of attribtues. */
  protected Range m_BestRange;

  /** whether to favor 0s instead of 1s. */
  protected boolean m_FavorZeroes;

  /** the seed value. */
  protected long m_Seed;

  /** the initial setups provider. */
  protected AbstractInitialSetupsProvider m_InitialSetupsProvider;

  /** the initial weights. */
  protected String m_InitialWeights;

  /** the random number generator. */
  protected Random m_Random;

  /** whether the algorithm is still running. */
  protected boolean m_Running;

  /** whether the algorithm got stopped. */
  protected boolean m_Stopped;

  /** whether the algorithm is paused. */
  protected boolean m_Paused;

  /** the time period in seconds after which to notify "fitness" listeners. */
  protected int m_NotificationInterval;

  /** the timestamp the last notification got sent. */
  protected Long m_LastNotificationTime;

  /** the fitness change listeners. */
  protected HashSet<GeneticFitnessChangeListener> m_FitnessChangeListeners;

  /** the best fitness so far. */
  protected double m_BestFitness;

  /** the best setup so far. */
  protected Object m_BestSetup;

  /** the best weights/bits so far. */
  protected int[] m_BestWeights;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_NumGenes               = 0;  // must be set by the algorithm itself, e.g., in preRun()
    m_BestRange              = new Range();
    m_Paused                 = false;
    m_FitnessChangeListeners = new HashSet<>();
    m_LastNotificationTime   = null;
    m_BestFitness            = Double.NEGATIVE_INFINITY;
    m_BestSetup              = null;
    m_BestWeights            = null;
  }

  /**
   * Resets the genetic algorihtm.
   * Derived classes must call this method in set-methods of parameters to
   * assure the invalidation of previously generated data.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Running = false;
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "num-threads", "numThreads",
      -1, -1, null);

    m_OptionManager.add(
      "num-chrom", "numChrom",
      50);

    m_OptionManager.add(
      "stopping-criterion", "stoppingCriterion",
      new MaxIterations());

    m_OptionManager.add(
      "seed", "seed",
      1L);

    m_OptionManager.add(
      "initial-setups-provider", "initialSetupsProvider",
      getDefaultInitialSetupsProvider());

    m_OptionManager.add(
      "initial-weights", "initialWeights",
      "");

    m_OptionManager.add(
      "favor-zeroes", "favorZeroes",
      false);

    m_OptionManager.add(
      "best", "bestRange",
      "-none-");

    m_OptionManager.add(
      "notify", "notificationInterval",
      -1);
  }

  /**
   * Sets the number of threads to use.
   *
   * @param value 	the number of threads: -1 = # of CPUs/cores
   */
  public void setNumThreads(int value) {
    if (getOptionManager().isValid("numThreads", value)) {
      m_NumThreads = value;
      reset();
    }
  }

  /**
   * Returns the number of threads to use.
   *
   * @return 		the number of threads: -1 = # of CPUs/cores
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
    return "The number of threads to use for executing the jobs; use -1 for all available cores.";
  }

  /**
   * Sets the best range of attributes.
   *
   * @param value	the range
   */
  protected void setBestRange(Range value) {
    m_BestRange = value;
    reset();
  }

  /**
   * Sets the best range of attributes.
   *
   * @param value	the range
   */
  public void setBestRange(String value) {
    if (value.equals("-none-"))
      setBestRange(new Range());
    else
      setBestRange(new Range(value));
  }

  /**
   * Returns the best range of attributes.
   *
   * @return		the range
   */
  public String getBestRange() {
    if (m_BestRange.getRange().length() == 0)
      return "-none-";
    else
      return m_BestRange.getRange();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String bestRangeTipText() {
    return "The range of the best attributes.";
  }

  /**
   * Sets the seed value to use, resets the random number generator.
   *
   * @param value	the seed to use
   */
  public void setSeed(long value) {
    m_Seed = value;
    reset();
  }

  /**
   * Returns the current seed value.
   *
   * @return		the seed value
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
    return "The seed value for the random number generator.";
  }

  /**
   * Returns the default initial setups provider.
   *
   * @return		the default
   */
  protected AbstractInitialSetupsProvider getDefaultInitialSetupsProvider() {
    return new EmptyInitialSetupsProvider<>();
  }

  /**
   * Sets the initial setups provider to use.
   *
   * @param value	the provider
   */
  public void setInitialSetupsProvider(AbstractInitialSetupsProvider value) {
    m_InitialSetupsProvider = value;
    reset();
  }

  /**
   * Returns the initial setups provider to use.
   *
   * @return		the provider
   */
  public AbstractInitialSetupsProvider getInitialSetupsProvider() {
    return m_InitialSetupsProvider;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String initialSetupsProviderTipText() {
    return "The initial setups provider to use.";
  }

  /**
   * Sets the initial weights to use rather than random ones.
   *
   * @param value	the initial weights
   */
  public void setInitialWeights(String value) {
    m_InitialWeights = value;
    reset();
  }

  /**
   * Returns the initial weights to use rather than random ones.
   *
   * @return		the initial weights
   */
  public String getInitialWeights() {
    return m_InitialWeights;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String initialWeightsTipText() {
    return "The initial weights to use, rather than random ones (string of 0s and 1s).";
  }

  /**
   * Sets whether 0s are favored over 1s.
   *
   * @param value	if true then 0s are favored over 1s
   */
  public void setFavorZeroes(boolean value) {
    m_FavorZeroes = value;
    reset();
  }

  /**
   * Returns whether 0s are favored over 1s.
   *
   * @return		true if 0s are favored over 1s
   */
  public boolean getFavorZeroes() {
    return m_FavorZeroes;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String favorZeroesTipText() {
    return "Whether to favor 0s instead of 1s.";
  }

  /**
   * Returns the number of genes to use.
   *
   * @return		the number
   */
  public int getNumGenes() {
    return m_NumGenes;
  }

  /**
   * Sets the number of chromosomes to use.
   *
   * @param value	the number
   */
  public void setNumChrom(int value) {
    m_NumChrom = value;
    reset();
  }

  /**
   * Returns the number of chromosomes to use.
   *
   * @return		the number
   */
  public int getNumChrom() {
    return m_NumChrom;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numChromTipText() {
    return "The number of chromosomes, ie, the population size.";
  }

  /**
   * Sets the stopping criterion to use.
   *
   * @param value	the criterion
   */
  public void setStoppingCriterion(AbstractStoppingCriterion value) {
    m_StoppingCriterion = value;
    reset();
  }

  /**
   * Returns the stopping criterion in use.
   *
   * @return		the criterion
   */
  public AbstractStoppingCriterion getStoppingCriterion() {
    return m_StoppingCriterion;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String stoppingCriterionTipText() {
    return "The stopping criterion to use.";
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
   * Returns the current iteration.
   *
   * @return		the iteration
   */
  public int getCurrentIteration() {
    return m_CurrentIteration;
  }

  /**
   * Stops the execution of the algorithm.
   */
  public void stopExecution() {
    m_Stopped = true;
    m_Paused  = false;
  }

  /**
   * Whether the execution has been stopped.
   *
   * @return		true if stopped
   */
  public boolean isStopped() {
    return m_Stopped;
  }

  /**
   * Pauses the execution.
   */
  public void pauseExecution() {
    if (m_Running)
      m_Paused = true;
  }

  /**
   * Returns whether the object is currently paused.
   *
   * @return		true if object is paused
   */
  public boolean isPaused() {
    return m_Running && m_Paused;
  }

  /**
   * Resumes the execution.
   */
  public void resumeExecution() {
    if (m_Running)
      m_Paused = false;
  }

  /**
   * Returns whether the algorithm is still running.
   *
   * @return		true if the algorithm is still running
   */
  public boolean isRunning() {
    return m_Running;
  }

  /**
   * Returns the current fitness values.
   *
   * @return		the fitness values
   */
  public double[] getFitness() {
    return m_Fitness;
  }

  /**
   * Initializes the algorithm.
   *
   * @param ch		the chromosome
   * @param genes	the number of genes
   */
  protected void init(int ch, int genes) {
    List<int[]> 	setups;
    int 		weightIndex;
    int			i;
    int			j;
    int[] 		gene;
    double 		set;

    getLogger().info("#chrom=" + ch + ", #gene=" + genes);

    m_ActualNumChrom = ch;
    m_NumGenes       = genes;
    m_Genes          = new BitSet[m_ActualNumChrom];

    setups      = getInitialSetups();
    weightIndex = 0;
    for (i = 0; i < m_ActualNumChrom; i++) {
      m_Genes[i] = new BitSet(m_NumGenes);

      if (i < setups.size()) {
	gene = setups.get(i);
	for (j = 0; j < m_NumGenes; j++) {
	  if (gene[j] == 1) {
	    m_Genes[i].set(j);
	  }
	}
	continue;
      }
      for (j = 0; j < m_NumGenes; j++) {
	set = m_Random.nextDouble();
	if (weightIndex < m_InitialWeights.length()) {
	  if (m_InitialWeights.charAt(weightIndex) == '1')
	    m_Genes[i].set(j);
	  weightIndex++;
	}
	else {
	  if (set < 0.5)
	    m_Genes[i].set(j);
	}
      }

    }

    m_Fitness = new double[m_ActualNumChrom];
    for (int f = 0; f < m_ActualNumChrom; f++)
      m_Fitness[f] = 0;

    sort();
  }

  /**
   * Returns the value of the specified gene.
   *
   * @param chromosome	the chromosome index
   * @param gene	the gene index
   * @return		true if gene is active
   */
  public boolean getGene(int chromosome, int gene) {
    return m_Genes[chromosome].get(gene);
  }

  /**
   * Sets the value of the specified gene.
   *
   * @param chromosome	the chromosome index
   * @param gene	the gene index
   * @param value	the value to set, 0=clear, otherwise activate it
   */
  public void setGene(int chromosome, int gene, int value) {
    setGene(chromosome, gene, (value != 0));
  }

  /**
   * Sets the value of the specified gene.
   *
   * @param chromosome	the chromosome index
   * @param gene	the gene index
   * @param value	false=clear, true=activate it
   */
  public void setGene(int chromosome, int gene, boolean value) {
    if (value)
      m_Genes[chromosome].set(gene);
    else
      m_Genes[chromosome].clear(gene);
  }

  /**
   * return if a has more zeroes than b.
   *
   * @param a		the first gene
   * @param b		the second gene
   * @return		true if a has more zeroes than b
   */
  public boolean hasMoreZeroes(BitSet a, BitSet b) {
    return (a.cardinality() < b.cardinality());
  }

  /**
   * Sorts genes and fitness arrays according to fitness.
   */
  public void sort() {
    BitSet btemp;
    for (int c = 0; c < m_ActualNumChrom; c++) {
      for (int d = (m_ActualNumChrom - 2); d >= c; d--) {
	if (m_Fitness[d] < m_Fitness[d+1]) {
	  btemp = m_Genes[d];
	  double x = m_Fitness[d];
	  m_Genes[d] = m_Genes[d+1];
	  m_Fitness[d] = m_Fitness[d+1];
	  m_Genes[d+1] = btemp;
	  m_Fitness[d+1] = x;
	} else if (m_Fitness[d] == m_Fitness[d+1] && hasMoreZeroes(m_Genes[d+1],m_Genes[d])) {
	  btemp = m_Genes[d];
	  double x = m_Fitness[d];
	  m_Genes[d] = m_Genes[d+1];
	  m_Fitness[d] = m_Fitness[d+1];
	  m_Genes[d+1] = btemp;
	  m_Fitness[d+1] = x;
	}
      }
    }
  }

  /**
   * Performs cross-over.
   */
  public void doCrossovers()  {
    for (int m = 0; m < m_ActualNumChrom / 4; m++) {
      copyGene(m + m_ActualNumChrom*3/4, m);
      // copies first chromosome half over last half
    }

    // copy the 2 best genes so that their
    // genetic material is replicated frequently:
    if (m_ActualNumChrom > 4) {
      for (int i = 0; i < m_NumGenes; i++) {
	setGene(m_ActualNumChrom - 1, i, getGene(0, i));
	// Seems redundant but slightly different...
	setGene(m_ActualNumChrom - 2, i, getGene(0, i));
	// when compared with the CopyGene command.
	setGene(m_ActualNumChrom - 3, i, getGene(0, i));
	// Do one or the other.
	setGene(m_ActualNumChrom - 4, i, getGene(1, i));
	setGene(m_ActualNumChrom - 5, i, getGene(1, i));
      }
    }

    int num = m_ActualNumChrom / 4; //originally set to /4
    for (int i = 0; i < num; i++) {
      int c1 = 2 + (int)((m_ActualNumChrom - 2) * m_Random.nextDouble() * 0.99);
      int c2 = 2 + (int)((m_ActualNumChrom - 2) * m_Random.nextDouble() * 0.99);
      // finds two different chromosomes
      if (c1 != c2) {
	int locus = 2 + (int)((m_NumGenes - 3) * m_Random.nextDouble());
	// finds a place within
	for (int g = 0; g < locus; g++) {
	  boolean temp = getGene(c1, i);
	  // and recombines by switching genes
	  setGene(c1, i, getGene(c2, i));
	  setGene(c2, i, temp);
	}
      }
    }
  }

  /**
   * Copies the values of one gene to another.
   *
   * @param to		the gene to copy to
   * @param from	the gene to copy from
   */
  protected void copyGene(int to, int from) {
    for (int i = 0; i < m_NumGenes; i++) {
      if (getGene(from, i))
	setGene(to, i, 1);
      else
	setGene(to, i, 0);
    }
  }

  /**
   * Performs mutations.
   */
  public void doMutations() {
    for(int i = 0; i < m_ActualNumChrom*0.5; i++) {
      int c = 0 + (int)((m_ActualNumChrom - 2) * m_Random.nextDouble() * 0.95); //0-->2
      int g = (int)(m_NumGenes * m_Random.nextDouble() * 0.95);       //saves good genes
      if (getGene(c, g))
	setGene(c, g, 0);
      // switch gene from its present position
      else
	setGene(c, g, 1);
    }
  }

  /**
   * Performs mutations.
   */
  public void doMutations2() {
    for(int i = 0; i < m_ActualNumChrom*0.5; i++) {
      int c = 2 + (int)((m_ActualNumChrom - 2) * m_Random.nextDouble() * 0.99); //0-->2

      double thresh=1.0/(double)m_NumGenes;
      for (int gn=0;gn<m_NumGenes;gn++) {
	if (m_Random.nextDouble() < thresh) {
	  if (getGene(c, gn))
	    setGene(c, gn, 0);
	  // switch gene from its present position
	  else
	    setGene(c, gn, 1);
	}
      }
    }
  }
  /**
   * Override the following function in sub-classes.
   */
  public abstract void calcFitness();

  /**
   * Checks whether the fitness is better.
   *
   * @param fitness	the new fitness
   * @return		true if the new fitness is better
   */
  public synchronized boolean isBetterFitness(double fitness) {
    return (fitness > m_BestFitness);
  }

  /**
   * Sets a fitness and keep it if better.
   *
   * @param fitness	the new fitness
   * @param setup	the new setup
   * @param chromosome	the chromosome responsible
   * @param weights	the new weights
   * @return		true if the new fitness was better
   */
  public synchronized boolean setNewFitness(double fitness, Object setup, int chromosome, int[] weights) {
    boolean 	result;

    result = false;

    if (isBetterFitness(fitness)) {
      m_BestFitness = fitness;
      m_BestSetup   = setup;
      m_BestWeights = weights.clone();
      result        = true;
    }

    return result;
  }

  /**
   * Returns the currently best fitness.
   *
   * @return		the best fitness so far
   */
  public double getCurrentFitness() {
    return m_BestFitness;
  }

  /**
   * Returns the currently best setup.
   *
   * @return		the best setup so far
   */
  public Object getCurrentSetup() {
    return m_BestSetup;
  }

  /**
   * Returns the currently best weights/bits.
   *
   * @return		the best weights/bits so far
   */
  public int[] getCurrentWeights() {
    return m_BestWeights;
  }

  /**
   * Further initializations in derived classes.
   */
  protected void preRun() {
    m_Running              = true;
    m_Stopped              = false;
    m_Random               = new Random(m_Seed);
    m_LastNotificationTime = null;
    m_BestFitness          = Double.NEGATIVE_INFINITY;
    m_BestSetup            = null;
    m_BestWeights          = null;
    m_StoppingCriterion.start();
  }

  /**
   * Outputs the chromosomes (bits and fitness).
   */
  protected void logChromosomes() {
    StringBuilder	info;
    int			cx;
    int			po;

    for (cx = 0; cx < getNumChrom(); cx++) {
      info = new StringBuilder();
      info.append("Fitness for chromosome: " + cx + " ");
      for (po = 0; po < getNumGenes(); po++) {
        if (getGene(cx, po))
          info.append("1");
        else
          info.append("0");
      }
      info.append("--->" + getFitness()[cx]);
      if (LoggingHelper.isAtLeast(getLogger(), Level.FINE))
	getLogger().fine(info.toString());
    }
  }

  /**
   * Runs the genetic algorithm.
   *
   * @return		true if successfully finished, false when interrupted
   */
  public boolean run() {
    boolean	result;

    result = true;

    try {
      preRun();
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Error on preRun", e);
      result = false;
    }

    if (result) {
      try {
	m_CurrentIteration = 0;
	do {
	  if (isPaused() && !isStopped()) {
	    Utils.wait(this, this, 1000, 100);
	    continue;
	  }

	  if (isStopped()) {
	    getLogger().severe("Interrupted!");
	    break;
	  }

	  m_CurrentIteration++;
	  if (m_CurrentIteration % 100 == 0)
	    getLogger().info("[" + m_CurrentIteration + "] Iteration");

	  calcFitness();
	  if (isLoggingEnabled()) {
	    getLogger().info("[" + String.valueOf(m_CurrentIteration) + "] After calc fitness");
            logChromosomes();
	  }

	  sort();
	  if (isLoggingEnabled()) {
	    getLogger().info("[" + String.valueOf(m_CurrentIteration) + "] After sort");
            logChromosomes();
	  }

	  doCrossovers();
	  if (isLoggingEnabled()) {
	    getLogger().info("[" + String.valueOf(m_CurrentIteration) + "] After crossovers");
            logChromosomes();
	  }

	  doMutations2();
	  if (isLoggingEnabled()) {
	    getLogger().info("[" + String.valueOf(m_CurrentIteration) + "] After mutations");
            logChromosomes();
	  }

	  if (isStopped()) {
	    getLogger().severe("Interrupted!");
	    break;
	  }
	}
	while (!getStoppingCriterion().checkStopping(this));
      }
      catch (Exception e) {
	result = false;
	getLogger().log(Level.SEVERE, "Error in iteration", e);
      }
    }

    try {
      postRun();
    }
    catch (Exception e) {
      result = false;
      getLogger().log(Level.SEVERE, "Error in postRun", e);
    }

    return result;
  }

  /**
   * Further clean-ups in derived classes.
   *
   * @throws Exception	if something goes wrong
   */
  protected void postRun() throws Exception {
    m_Running = false;
  }

  /**
   * Adds the given listener to its internal list of listeners.
   *
   * @param l		the listener to add
   */
  public void addFitnessChangeListener(GeneticFitnessChangeListener l) {
    m_FitnessChangeListeners.add(l);
  }

  /**
   * Removes the given listener from its internal list of listeners.
   *
   * @param l		the listener to remove
   */
  public void removeFitnessChangeListener(GeneticFitnessChangeListener l) {
    m_FitnessChangeListeners.remove(l);
  }

  /**
   * Notifies all the fitness change listeners of a change.
   *
   * @param e		the event to send
   */
  protected void notifyFitnessChangeListeners(GeneticFitnessChangeEvent e) {
    Iterator<GeneticFitnessChangeListener> iter;

    iter = m_FitnessChangeListeners.iterator();
    while (iter.hasNext())
      iter.next().fitnessChanged(e);
  }

  /**
   * Sends out a notification to all listeners that the fitness has changed, if
   * notifications is wanted and due.
   *
   * @param fitness	the fitness
   * @param setup	the setup
   * @param weights 	the weights
   */
  protected synchronized void notifyFitnessChangeListeners(double fitness, Object setup, int[] weights) {
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
        notifyFitnessChangeListeners(new GeneticFitnessChangeEvent(this, fitness, setup, weights));
      }
    }
  }

  /**
   * Provides the initial gene setup.
   *
   * @return	the genes (0s and 1s)
   */
  public List<int[]> getInitialSetups() {
    return m_InitialSetupsProvider.getInitialSetups(this);
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @return		the shallow copy
   */
  public AbstractGeneticAlgorithm shallowCopy() {
    return shallowCopy(false);
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @param expand	whether to expand variables to their current values
   * @return		the shallow copy
   */
  public AbstractGeneticAlgorithm shallowCopy(boolean expand) {
    return (AbstractGeneticAlgorithm) OptionUtils.shallowCopy(this, expand);
  }

  /**
   * Runs the genetic algorithm with the given options.
   *
   * @param env		the environment class to use
   * @param genetic	the genetic algorithm to run
   * @param options	the options to set
   */
  public static void runGeneticAlgorithm(Class env, Class genetic, String[] options) {
    AbstractGeneticAlgorithm geneticInst;

    Environment.setEnvironmentClass(env);

    try {
      if (OptionUtils.helpRequested(options)) {
	System.out.println("Help requested...\n");
	geneticInst = forName(genetic.getName(), new String[0]);
	System.out.println("\n" + OptionUtils.list(geneticInst));
      }
      else {
	geneticInst = forName(genetic.getName(), options);
	geneticInst.run();
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Returns a list with classnames of genetic algorithms.
   *
   * @return		the genetic algorithm classnames
   */
  public static String[] getGeneticAlgorithms() {
    return ClassLister.getSingleton().getClassnames(AbstractGeneticAlgorithm.class);
  }

  /**
   * Instantiates the genetic algorithm with the given options.
   *
   * @param classname	the classname of the genetic algorithm to instantiate
   * @param options	the options for the genetic algorithm
   * @return		the instantiated genetic algorithm or null if an error occurred
   */
  public static AbstractGeneticAlgorithm forName(String classname, String[] options) {
    AbstractGeneticAlgorithm result;

    try {
      result = (AbstractGeneticAlgorithm) OptionUtils.forName(AbstractGeneticAlgorithm.class, classname, options);
    }
    catch (Exception e) {
      e.printStackTrace();
      result = null;
    }

    return result;
  }

  /**
   * Instantiates the genetic algorithm from the given commandline
   * (i.e., classname and optional options).
   *
   * @param cmdline	the classname (and optional options) of the
   * 			genetic algorithm to instantiate
   * @return		the instantiated genetic algorithm
   * 			or null if an error occurred
   */
  public static AbstractGeneticAlgorithm forCommandLine(String cmdline) {
    return (AbstractGeneticAlgorithm) AbstractOptionConsumer.fromString(ArrayConsumer.class, cmdline);
  }
}
