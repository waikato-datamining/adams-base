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
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.genetic;

import adams.core.discovery.AbstractGeneticDiscoveryHandler;
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
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; apart from attribute names (case-sensitive), the following placeholders can be used as well: first, second, third, last_2, last_1, last
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
 * &nbsp;&nbsp;&nbsp;default: ALL
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
  extends AbstractClassifierBasedGeneticAlgorithm {

  private static final long serialVersionUID = -4982024446995877986L;

  /** the handlers to use for discovery. */
  protected AbstractGeneticDiscoveryHandler[] m_Handlers;

  /** the actual handlers to use for discovery. */
  protected AbstractGeneticDiscoveryHandler[] m_ActualHandlers;

  protected List<Integer> m_numbits=null;

  protected List<Integer> m_start=null;

  /**
   * A job class specific to Hermione.
   *
   * @author  dale
   * @version $Revision: 4322 $
   */
  public static class HermioneJob
    extends ClassifierBasedGeneticAlgorithmJob<Hermione> {

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
     * Calculates the new fitness.
     */
    @Override
    public void calcNewFitness(){
      try {
	getLogger().fine((new StringBuilder("calc for:")).append(weightsToString()).toString());

	// was measure already calculated for this attribute setup?
	Double cc = getGenetic().getResult(weightsToString());
	if (cc != null){
	  getLogger().info((new StringBuilder("Already present: ")).append(Double.toString(cc.doubleValue())).toString());
	  m_Fitness = cc;
	  return;
	}

	Instances newInstances = new Instances(getInstances());

	// evaluate classifier
	Classifier newClassifier = getGenetic().generateClassifier(m_Weights);
	m_Fitness = evaluateClassifier(newClassifier, newInstances);

	// process fitness
	if (getGenetic().setNewFitness(m_Fitness, newClassifier, m_Weights)) {
	  generateOutput(m_Fitness, newInstances, newClassifier, m_Weights);
	  // notify the listeners
	  getGenetic().notifyFitnessChangeListeners(getMeasure().adjust(m_Fitness), newClassifier, m_Weights);
	}

	getGenetic().addResult(weightsToString(), m_Fitness);
      }
      catch(Exception e){
	getLogger().log(Level.SEVERE, "Error: ", e);
	m_Fitness = null;
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
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_ActualHandlers = new AbstractGeneticDiscoveryHandler[0];
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
   * @param num		the number of chromosomes
   * @param w		the initial weights
   * @return		the instance
   * @param data	the data to use
   */
  protected HermioneJob newJob(int num, int[] w, Instances data) {
    return new HermioneJob(this, num, w, data);
  }

  /**
   * Some more initializations.
   */
  @Override
  protected void preRun() {
    super.preRun();

    setupParamsAndClassifier(m_Handlers, m_Classifier);

    // setup structures
    init(20, getNumBits() * m_BitsPerGene);
  }

  /**
   * Setup classifier and genetic discovery. Attach classifier to g d
   * @param p
   * @param c
   */
  protected void setupParamsAndClassifier(AbstractGeneticDiscoveryHandler[] p, Classifier c){
    m_ActualHandlers = new AbstractGeneticDiscoveryHandler[p.length];
    for (int i = 0; i < p.length; i++)
      m_ActualHandlers[i] = (AbstractGeneticDiscoveryHandler) p[i].shallowCopy();
    DefaultPropertyDiscovery d = new DefaultPropertyDiscovery();
    d.setLoggingLevel(getLoggingLevel());
    d.discover(m_ActualHandlers, c);
    if (isLoggingEnabled()) {
      for (int i = 0; i < m_ActualHandlers.length; i++) {
	getLogger().info((i+1) + ". " + OptionUtils.getCommandLine(m_ActualHandlers[i]));
	for (PropertyContainer cont: m_ActualHandlers[i].getContainers())
	  getLogger().info("   " + cont.getPath());
      }
    }
  }

  /**
   * Int array of bits to string
   * @param ia
   * @return
   */
  public String intAtoStringA(int[] ia){
    String ret = new String();
    for (int i=0;i<ia.length;i++){
      ret+=""+ia[i];
    }
    return(ret);
  }

  /**
   * get bit array for parameter at pos
   * @param w
   * @param spoints
   * @param numbits
   * @param pos
   * @return
   */
  public int[] getBitsForPosition(int[] w,List<Integer> spoints, List<Integer> numbits,int pos){
    int[] ret=new int[numbits.get(pos)];
    int c=0;
    for (int i=spoints.get(pos);i<spoints.get(pos)+numbits.get(pos);i++) {
      ret[c++]=w[i];
    }
    return(ret);
  }

  /**
   * Get List containing number of bits used for params
   * @return
   */
  public List<Integer> getNumBitsForAll(){
    ArrayList<Integer> al=new ArrayList<>();
    for (AbstractGeneticDiscoveryHandler ag: m_ActualHandlers){
      List<PropertyPath.PropertyContainer> lpc=ag.getContainers();
      for (PropertyPath.PropertyContainer pc:lpc){
	al.add(ag.getNumBits());
      }
    }

    return(al);
  }

  /**
   * Get List of start positions in bit string
   * @return
   */
  public List<Integer> getStartPoints(){
    ArrayList<Integer> al=new ArrayList<>();
    int count=0;
    for (AbstractGeneticDiscoveryHandler ag: m_ActualHandlers){
      List<PropertyPath.PropertyContainer> lpc=ag.getContainers();
      for (PropertyPath.PropertyContainer pc:lpc){
	al.add(count);
	count+=ag.getNumBits();
      }
    }

    return(al);
  }

  /**
   * Get total number of bits for params
   * @return
   */
  public int getNumBits(){
    int count=0;
    for (AbstractGeneticDiscoveryHandler ag: m_ActualHandlers){
      List<PropertyPath.PropertyContainer> lpc=ag.getContainers();
      for (PropertyPath.PropertyContainer pc:lpc){
	count+=ag.getNumBits();
      }
    }
    return(count);
  }

  /**
   * Generate the classifier from current bit array
   * @param weights
   * @return
   */
  public Classifier generateClassifier(int weights[]){
    // foreach handler, pack according to bits in weights
    if (m_numbits==null){
      m_numbits=getNumBitsForAll();
    }
    if (m_start==null){
      m_start=getStartPoints();
    }

    if (isLoggingEnabled()) {
      StringBuilder w = new StringBuilder();
      for (int i = 0; i < weights.length; i++) {
	w.append(weights[i]);
      }
      getLogger().info("Weights: " + w);
    }
    DefaultPropertyDiscovery d = new DefaultPropertyDiscovery();
    AbstractGeneticDiscoveryHandler cp[] = new AbstractGeneticDiscoveryHandler[m_ActualHandlers.length];
    for (int i=0;i<cp.length;i++){
      cp[i]=(AbstractGeneticDiscoveryHandler) m_ActualHandlers[i].shallowCopy();
    }
    Classifier c= null;
    try {
      c = (Classifier) OptionUtils.shallowCopy(getClassifier());
    } catch (Exception e) {
      e.printStackTrace();
    }
    d.discover(cp, c);
    int pos=0;
    for (AbstractGeneticDiscoveryHandler ag:cp){
      List<PropertyPath.PropertyContainer> lpc=ag.getContainers();

      for (PropertyPath.PropertyContainer pc:lpc){
	String sa=intAtoStringA(getBitsForPosition(weights,m_start,m_numbits,pos));
	ag.unpack(pc, sa);
	pos++;
      }
    }
    return c;
  }
}
