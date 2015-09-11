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
 * GeneticAlgorithm.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.optimise;

import adams.core.logging.LoggingLevel;
import adams.core.option.ArrayConsumer;
import adams.env.Environment;
import adams.multiprocess.AbstractJob;
import adams.multiprocess.JobList;
import adams.multiprocess.JobRunner;
import adams.multiprocess.LocalJobRunner;
import adams.optimise.genetic.PackData;
import adams.optimise.genetic.PackDataDef;
import adams.optimise.genetic.PackDataGeneticAlgorithm;
import adams.optimise.genetic.fitnessfunctions.AttributeSelection;
import weka.classifiers.functions.GPD;
import weka.classifiers.functions.LinearRegression;
import weka.classifiers.functions.PLSClassifier;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.SelectedTag;
import weka.filters.Filter;
import weka.filters.MultiFilter;
import weka.filters.supervised.attribute.PLSFilter;
import weka.filters.unsupervised.attribute.Remove;

import java.util.Hashtable;
import java.util.Vector;

/**
 <!-- globalinfo-start -->
 * Morticia (GEX).
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 *
 * <pre>-D (property: debug)
 *         If set to true, scheme may output additional info to the console.
 * </pre>
 *
 * <pre>-num-chrom &lt;int&gt; (property: numChrom)
 *         The number of chromosomes, ie, the population size.
 *         default: 50
 * </pre>
 *
 * <pre>-num-iter &lt;int&gt; (property: numIterations)
 *         The number of iterations to perform.
 *         default: 10000000
 * </pre>
 *
 * <pre>-seed &lt;int&gt; (property: seed)
 *         The seed value for the random number generator.
 *         default: 1
 * </pre>
 *
 * <pre>-favor-zeroes (property: favorZeroes)
 *         Whether to favor 0s instead of 1s.
 * </pre>
 *
 * <pre>-best &lt;java.lang.String&gt; (property: bestRange)
 *         The range of the best attributes.
 *         default: -none-
 * </pre>
 *
 * <pre>-max-train &lt;int&gt; (property: maxTrainTime)
 *         The maximum number of seconds to training time (0 = unlimited time).
 *         default: 0
 * </pre>
 *
 * <pre>-notify &lt;int&gt; (property: notificationInterval)
 *         The time interval in seconds after which notification events about changes
 *          in the fitness can be sent (-1 = never send notifications; 0 = whenever
 *          a change occurs).
 *         default: -1
 * </pre>
 *
 <!-- options-end -->
 *
 * @author Dale (dale at cs dot waikato dot ac dot nz)
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class GeneticAlgorithm
  extends PackDataGeneticAlgorithm {

  /**suid.*/
  private static final long serialVersionUID = 3050987598416662061L;

  protected int m_bits=5;
  protected int m_zerocount=Integer.MAX_VALUE;

  protected PackDataDef m_pdd=null;

  protected FitnessFunction m_fitnessfn=null;

  /** the timestamp the last notification got sent. */
  protected Long m_LastNotificationTime;

  /** the cache for results. */
  public Hashtable<String,Double> m_StoredResults = new Hashtable<String,Double>();

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
   * Resets the genetic algorihtm.
   * Derived classes must call this method in set-methods of parameters to
   * assure the invalidation of previously generated data.
   */
  @Override
  public void reset() {
    super.reset();

    m_StoredResults = new Hashtable<String,Double>();

  }


  /**
   * Returns a value from the cache.
   *
   * @param key		the key of the result
   * @return		the result or null if not present
   */
  protected synchronized Double getResult(String key) {
    Double res = m_StoredResults.get(key);
    return res;
  }

  /**
   * Clears all currently stored results.
   */
  protected synchronized void clearResults() {
    m_StoredResults.clear();
  }



  /**
   * Turns the weights into a string representation.
   *
   * @param weights	the weights to turn into a string
   * @return		the weights as string
   */
  public String weightsToString(int[] weights) {
    String ret = "";
    for (int i = 0; i < weights.length; i++) {
	if (weights[i] == 0) {
	  ret += "0";
	} else {
	  ret += "1";
	}
    }
    return ret;
  }

  /**
   * Calculates the new fitness.
   */
  public double calcNewFitness(FitnessFunction ff, int[] weights) {

    Double cc = getResult(weightsToString(weights));
    if (cc != null) {
      return cc;
    }

    PackData pd=new PackData(getDataDef());
    pd.putBits(weights);
    int count=0;
    for (int i=0;i<weights.length;i++){
      if (weights[i] == 0){
	count++;
      }
    }
    OptData odd=new OptData();
    for (String var:pd.getKeySet()) {
      odd.set(var,pd.get(var) );
    }
    double val=ff.evaluate(odd);
    checkBest(val,odd,ff,count);
    odd.cleanUp();
    return(val);
  }

  public synchronized void checkBest(Double fitness, OptData vars, FitnessFunction ff, int zerocount) {
    if (fitness > m_bestf || (fitness == m_bestf && zerocount < m_zerocount)) {
      m_zerocount=zerocount;
      m_bestf=new Double(fitness);
      if (m_bestv != null)
	m_bestv.cleanUp();
      m_bestv=vars.getClone();
      ff.newBest(fitness,vars);
    }
  }


  /**
   * The default constructor.
   */
  public GeneticAlgorithm() {
    super();
  }

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Genetic Algorithm.";
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
  }


  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "bits", "bits",
	    5);
  }

  /**
   * Bits per gene.
   *
   * @param value	 the number of bits.
   */
  public void setBits(int value) {
    m_bits = value;
  }

  /**
   * Gets the number of bits.
   *
   * @return		the number of bits
   */
  public int getBits() {
    return m_bits;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String bitsTipText() {
    return "The number of bits to use.";
  }

  /**
   * Class for multithreading the ga.
   * @author dale
   *
   */
  public static class GAJob
  	extends AbstractJob {

    /** ga. */
    protected GeneticAlgorithm m_ga=null;
    /** fitness function.*/
    protected FitnessFunction m_ff=null;
    /** weights. */
    protected int[] m_weights=null;

    public double m_fitness=Double.NaN;

    /**
     * Constructor. Set GA params.
     * @param ff	fitness function
     * @param weights	weights
     */
    public GAJob(GeneticAlgorithm ga, FitnessFunction ff, int[] weights) {
      m_ff=ff;
      m_weights=weights;
      m_ga=ga;
    }

    @Override
    protected String postProcessCheck() {
      return null;
    }

    @Override
    protected String preProcessCheck() {
      return null;
    }

    /**
     * Does the actual execution of the job.
     * 
     * @throws Exception if fails to execute job
     */
    @Override
    protected void process() throws Exception {
      m_fitness=m_ga.calcNewFitness(m_ff, m_weights);
    }

    @Override
    public String toString() {
      return "GA Job";
    }
  }

  /**
   * Calculates the fitness of the population.
   */
  @Override
  public void calcFitness() {
    JobRunner<GAJob> runner = new LocalJobRunner<GAJob>();
    JobList<GAJob> jobs = new JobList<GAJob>();
    GAJob[] jbs=new GAJob[getNumChrom()];
    for (int i = 0; i < getNumChrom(); i++) {
      int[] weights = new int[getNumGenes()];
      for (int j = 0; j < getNumGenes(); j++)  {
	if (getGene(i,j)) {
	  weights[j] =1;
	} else {
	  weights[j] =0;
	}
      }
      GAJob jb=new GAJob(this,m_fitnessfn , weights);
      jbs[i]=jb;
      jobs.add(jb);
    }

    runner.add(jobs);
    runner.start();
    runner.stop();
    for (int i=0;i<getNumChrom();i++){
      m_Fitness[i]=jbs[i].m_fitness;
    }
  }

  /**
   * Some more initializations.
   */
  @Override
  protected void preRun() {
    super.preRun();

    // reset timestamp of notification
    m_LastNotificationTime = null;

    init(getNumChrom());

    clearResults();
  }


  @Override
  public PackDataDef getDataDef() {
    return(m_pdd);
  }

  @Override
  public Vector<PackData> getDataSetups() {
    Vector<PackData> vpd=new Vector<PackData>();
    return(vpd);
  }

  @Override
  public OptData optimise(OptData datadef, FitnessFunction fitness) {
    m_pdd=new PackDataDef();
    m_fitnessfn=fitness;
    for (String var:datadef.getVarNames()) {
      OptVar ov=datadef.getVar(var);
      m_pdd.add(var, getBits(), ov.m_min, ov.m_max);
    }
    run();

    return(m_bestv);
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    super.cleanUp();

    clearResults();
  }

  public static void main(String[] args) {
    Environment.setEnvironmentClass(Environment.class);
    GeneticAlgorithm ga=new GeneticAlgorithm();
    ga.setBits(1);
    ga.setNumChrom(8);
    ga.setIterations(10000);
    ga.setFavorZeroes(true);


    AttributeSelection as=new AttributeSelection();
    //as.setDataset(new PlaceholderFile("/home/dale/blgg/conversion/merged/m_5_.75.arff"));
    ArrayConsumer.setOptions(as, args);
    PLSClassifier pls=new PLSClassifier();
    PLSFilter pf=(PLSFilter)pls.getFilter();
    pf.setNumComponents(11);

    LinearRegression reg=new LinearRegression();
    reg.setEliminateColinearAttributes(false);
    reg.setAttributeSelectionMethod(new SelectedTag(LinearRegression.SELECTION_NONE, LinearRegression.TAGS_SELECTION));

    GPD gp=new GPD();
    gp.setNoise(.01);
    //RBFKernel rbf = new RBFKernel();
    //rbf.setChecksTurnedOff(true);
    //rbf.setGamma(.01);
    //gp.setKernel(rbf);

    Remove remove = new Remove();
    remove.setAttributeIndices("1");
    FilteredClassifier fc = new FilteredClassifier();


    MultiFilter mf=new MultiFilter();
    Filter[] filters=new Filter[2];
    filters[0]=remove;
    filters[1]=pf;
    mf.setFilters(filters);

    fc.setClassifier(gp);
    fc.setFilter(pf);

    as.setClassifier(gp);
    as.setClassIndex("last");
    //as.setDataset(new PlaceholderFile("/home/dale/OMD_clean.arff"));
    //as.setOutputDirectory(new PlaceholderFile("/research/dale"));
    ga.setLoggingLevel(LoggingLevel.INFO);
    as.setLoggingLevel(LoggingLevel.INFO);
    ga.optimise(as.getDataDef(), as);

  }
}
