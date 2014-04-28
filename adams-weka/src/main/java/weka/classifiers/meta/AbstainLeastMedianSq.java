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
 * AbstainLeastMedianSq.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */

package weka.classifiers.meta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import weka.classifiers.AbstainingClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.SingleClassifierEnhancer;
import weka.classifiers.functions.LinearRegression;
import weka.core.AttributeStats;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.RevisionUtils;
import weka.core.SelectedTag;
import weka.core.Utils;
import weka.core.WeightedInstancesHandler;
import weka.experiment.Stats;
import weka.filters.Filter;
import weka.filters.unsupervised.instance.Resample;

/**
 <!-- globalinfo-start -->
 * Finds the base classifier with the best least median squared error.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre> -D
 *  If set, classifier is run in debug mode and
 *  may output additional info to the console</pre>
 *
 * <pre> -W
 *  Full name of base classifier.
 *  (default: weka.classifiers.rules.ZeroR)</pre>
 *
 * <pre>
 * Options specific to classifier weka.classifiers.rules.ZeroR:
 * </pre>
 *
 * <pre> -D
 *  If set, classifier is run in debug mode and
 *  may output additional info to the console</pre>
 *
 * <pre> -S &lt;sint&gt;
 *  Number of regressions
 *  (default: 10)
 * </pre>
 *
 * <pre> -P &lt;int&gt;
 *  Percent sample size
 *  (default: 60)
 * </pre>
 *
 * <pre> -E &lt;int&gt;
 *  Percent Error at
 *  (default: 50)
 * </pre>
 *
 * <pre> -G &lt;seed&gt;
 *  Set the seed used to generate samples
 *  (default: 0)
 * </pre>
 *
 <!-- options-end -->
 *
 * @author Dale
 * @version $Revision$
 */
public class AbstainLeastMedianSq
  extends SingleClassifierEnhancer
  implements WeightedInstancesHandler, AbstainingClassifier {

  /** for serialization. */
  private static final long serialVersionUID = -1411619239601765926L;

  protected double[] m_Residuals;

  protected double[] m_weight;

  protected double m_SSR;

  protected double m_scalefactor;

  protected double m_bestMedian = Double.POSITIVE_INFINITY;

  protected double m_Ridge=1;

  protected Classifier m_currentClassifier;

  protected Classifier m_bestClassifier;

  protected Instances m_Data;

  protected Instances m_SubSample;

  protected int m_numreg = 10;

  protected int m_instancepct = 80;

  protected int m_errPct = 50;

  protected boolean m_debug = false;

  protected Random m_random;

  protected long m_randomseed = 0;
  
  protected double m_variance=0.5;
  
  protected boolean  m_ok=true;
  
  protected double m_min,m_max;
  
  protected Classifier[] m_cs;

  /**
   * Returns a string describing classifier.
   *
   * @return 		a description suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String globalInfo() {
    return "Finds the base classifier with the best least median squared error.";
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  @Override
  public Enumeration listOptions() {

    Vector newVector = new Vector();

    Enumeration enu = super.listOptions();
    while (enu.hasMoreElements()) {
      newVector.addElement(enu.nextElement());
    }

    newVector.addElement(new Option("\tNumber of regressions\n"
	+ "\t(default: 10)\n",
	"S", 1, "-S <sint>"));
    newVector.addElement(new Option("\tPercent sample size\n"
	+ "\t(default: 60)\n",
	"P", 1, "-P <int>"));
    newVector.addElement(new Option("\tPercent Error at\n"
	+ "\t(default: 50)\n",
	"E", 1, "-E <int>"));
    newVector.addElement(new Option("\tSet the seed used to generate samples\n"
	+ "\t(default: 0)\n",
	"G", 1, "-G <seed>"));
    newVector.addElement(new Option("\tThreshold for variance of regression residuals\n"
	+ "\t(default: 0.5)\n",
	"V", 1, "-V <seed>"));

    return newVector.elements();
  }

  /**
   * Sets the OptionHandler's options using the given list. All options
   * will be set (or reset) during this call (i.e. incremental setting
   * of options is not possible).
   *
   <!-- options-start -->
   * Valid options are: <p/>
   *
   * <pre> -D
   *  If set, classifier is run in debug mode and
   *  may output additional info to the console</pre>
   *
   * <pre> -W
   *  Full name of base classifier.
   *  (default: weka.classifiers.rules.ZeroR)</pre>
   *
   * <pre>
   * Options specific to classifier weka.classifiers.rules.ZeroR:
   * </pre>
   *
   * <pre> -D
   *  If set, classifier is run in debug mode and
   *  may output additional info to the console</pre>
   *
   * <pre> -S &lt;sint&gt;
   *  Number of regressions
   *  (default: 10)
   * </pre>
   *
   * <pre> -P &lt;int&gt;
   *  Percent sample size
   *  (default: 60)
   * </pre>
   *
   * <pre> -E &lt;int&gt;
   *  Percent Error at
   *  (default: 50)
   * </pre>
   *
   * <pre> -G &lt;seed&gt;
   *  Set the seed used to generate samples
   *  (default: 0)
   * </pre>
   *
   <!-- options-end -->
   *
   * @param options the list of options as an array of strings
   * @throws Exception if an option is not supported
   */
  @Override
  public void setOptions(String[] options) throws Exception {

    String curropt = Utils.getOption('S', options);
    if ( curropt.length() != 0) {
      setNumRegressions(Integer.parseInt(curropt));
    } else
      setNumRegressions(10);

    curropt = Utils.getOption('P', options);
    if ( curropt.length() != 0) {
      setPct(Integer.parseInt(curropt));
    } else
      setPct(60);

    curropt = Utils.getOption('E', options);
    if ( curropt.length() != 0) {
      setError(Integer.parseInt(curropt));
    } else
      setError(50);

    curropt = Utils.getOption('G', options);
    if ( curropt.length() != 0) {
      setRandomSeed(Long.parseLong(curropt));
    } else {
      setRandomSeed(0);
    }
    
    curropt = Utils.getOption('V', options);
    if ( curropt.length() != 0) {
      setVariance(Double.parseDouble(curropt));
    } else {
      setVariance(0.5);
    }

    super.setOptions(options);
  }

  /**
   * Gets the current option settings for the OptionHandler.
   *
   * @return the list of current option settings as an array of strings
   */
  @Override
  public String[] getOptions() {
    List<String> result = new ArrayList<String>();
    
    result.add ("-S");
    result.add (""+getNumRegressions());

    result.add ("-P");
    result.add ("" + getPct());

    result.add ("-E");
    result.add ("" + getError());

    result.add ("-G");
    result.add ("" + getRandomSeed());
    
    result.add ("-V");
    result.add ("" + getVariance());
    
    String[] options = super.getOptions();
    for (String option: options) result.add(option);
    return result.toArray(new String[result.size()]);
  }

  /**
   * Finds residuals (squared) for the current
   * regression.
   *
   * @throws Exception if an error occurs
   */
  protected void findResiduals() throws Exception{
    m_SSR = 0;
    m_Residuals = new double [m_Data.numInstances()];
    double maxr=0;
    for(int i = 0; i < m_Data.numInstances(); i++) {
      m_Residuals[i] = m_currentClassifier.classifyInstance(m_Data.instance(i));
      m_Residuals[i] -= m_Data.instance(i).value(m_Data.classAttribute());
      m_Residuals[i] *= m_Residuals[i];
      if (m_Residuals[i] > maxr){
	maxr=m_Residuals[i];
      }
      m_SSR += m_Residuals[i];
    }
    //System.err.println("max-res="+maxr);
  }

  /**
   * finds the median residual squared for the
   * current regression.
   *
   * @throws Exception if an error occurs
   */
  protected void getErrorAtPct(int pct) throws Exception{
    findResiduals();
    Vector<Double> v=new Vector<Double>();
    for (int i=0;i<m_Residuals.length;i++) {
      v.add(m_Residuals[i]);
    }
    Collections.sort(v);
    int p = m_Residuals.length;
    int pos = (int)((double)p * (double)pct/100.0);
    if (m_debug) {
      System.err.println("pos="+pos+"   residual:"+v.get(pos));
    }

    if(v.get(pos) < m_bestMedian) {
      if (m_debug) {
	System.err.println("+++ pos="+pos+"   new best residual:"+v.get(pos));

      }
      m_bestMedian = v.get(pos);
      m_bestClassifier = m_currentClassifier;
    }
  }

  /**
   * Finds the best regression generated from m_samples
   * random samples from the training data.
   *
   * @throws Exception if an error occurs
   */
  protected void findBestRegression()throws Exception{
    Random rn=new Random(this.getRandomSeed());
    m_bestMedian = Double.POSITIVE_INFINITY;
    if (m_debug) {
      System.out.println("Starting:");
    }
    double resids[]=new double[m_numreg];
    m_cs=new Classifier[m_numreg];
    for(int s = 0, r = 0; s < this.m_numreg; s++, r++) {
      if (m_debug) {
	System.out.print("*");
      }
      genRegression(rn);
      findResiduals();
      //getErrorAtPct(getError());
      resids[s]=m_SSR;
      m_cs[s]=m_currentClassifier;
    }
    
    //double max=resids[Utils.maxIndex(resids)];
    //double min=resids[Utils.minIndex(resids)];
    //System.out.println("Max="+max+"  Min="+min);
//    System.out.println(hashCode()+" : "+Utils.variance(resids));
    if (Math.sqrt(Utils.variance(resids))/Utils.mean(resids) > getVariance()){
      m_ok=false;
    } else {
      m_ok=true;
      m_Classifier.buildClassifier(m_Data);
    }
   
    
    //m_Classifier = m_bestClassifier;
    if (m_debug) {
      //System.out.println("");
    }
    
  }

  /**
   * Produces a random sample from m_Data
   * in m_SubSample.
   *
   * @param data data from which to take sample
   * @throws Exception if an error occurs
   */
  protected void selectSubSample(Instances data, Random r) throws Exception {
    Resample f=new Resample();
    f.setRandomSeed(r.nextInt());
    f.setSampleSizePercent((double)this.m_instancepct);
    f.setInputFormat(data);
    f.setNoReplacement(true);
    m_SubSample = Filter.useFilter(data, f);
    //System.err.println(m_SubSample.toString());
  }

  /**
   * Generates a LinearRegression classifier from
   * the current m_SubSample.
   *
   * @throws Exception if an error occurs
   */
  protected void genRegression(Random r) throws Exception {
    m_currentClassifier = new LinearRegression();
    ((LinearRegression)(m_currentClassifier)).setEliminateColinearAttributes(false);
    ((LinearRegression)(m_currentClassifier)) .setAttributeSelectionMethod(new SelectedTag(LinearRegression.SELECTION_NONE, LinearRegression.TAGS_SELECTION));
 
    //m_currentClassifier = //AbstractClassifier.makeCopy(m_Classifier);
    selectSubSample(m_Data,r);
    m_currentClassifier.buildClassifier(m_SubSample);
  }

  /**
   * Returns default capabilities of the base classifier.
   *
   * @return      the capabilities of the base classifier
   */
  @Override
  public Capabilities getCapabilities() {
    Capabilities        result;

    result = super.getCapabilities();
    result.setMinimumNumberInstances(1);

    result.disableAllClasses();
    result.enable(Capability.NUMERIC_CLASS);
    result.enable(Capability.DATE_CLASS);
    result.enable(Capability.MISSING_CLASS_VALUES);

    return result;
  }

  /**
   * Builds the classifier.
   *
   * @param data	the training data
   * @throws Exception	if something goes wrong
   */
  @Override
  public void buildClassifier(Instances data) throws Exception {
    getCapabilities().testWithFail(data);
    m_Data = new Instances(data);
    m_Data.deleteWithMissingClass();
    if (m_Data.numInstances() <= m_numreg){
      m_ok=false;
      return;
    }
    findBestRegression();
    AttributeStats as=data.attributeStats(data.classIndex());
    Stats s=as.numericStats;
    m_min=s.min;
    m_max=s.max;
  }

  /**
   * Returns the prediction.
   */
  @Override
  public double classifyInstance(Instance inst) throws Exception {
    if (!m_ok)
      return Utils.missingValue();
    
    double res = getAbstentionClassification(inst);
    
    if (res< m_min || res > m_max)
      return Utils.missingValue();
 
    return res;
  }

  /**
   * Whether abstaining is possible, e.g., used in meta-classifiers.
   * 
   * @return		true if abstaining is possible
   */
  @Override
  public boolean canAbstain() {
    return true;
  }

  /**
   * The prediction that made the classifier abstain.
   * 
   * @param inst	the instance to get the prediction for
   * @return		the prediction
   * @throws Exception	if fails to make prediction
   */
  @Override
  public double getAbstentionClassification(Instance inst) throws Exception {
    if (!m_ok)
      return Utils.missingValue();
    if (m_Classifier instanceof AbstainingClassifier)
      return ((AbstainingClassifier) m_Classifier).getAbstentionClassification(inst);
    else
      return m_Classifier.classifyInstance(inst);
  }

  /**
   * The class distribution that made the classifier abstain.
   * 
   * @param inst	the instance to get the prediction for
   * @return		the class distribution
   * @throws Exception	if fails to make prediction
   */
  @Override
  public double[] getAbstentionDistribution(Instance inst) throws Exception {
    return new double[]{getAbstentionClassification(inst)};
  }

  /**
   * Returns description of classifier.
   */
  @Override
  public String toString() {
    return m_Classifier.toString();
  }

  /**
   * Returns the revision string.
   *
   * @return            the revision
   */
  @Override
  public String getRevision() {
    return RevisionUtils.extract("$Revision$");
  }

  /**
   * Returns the tip text for this property
   * @return tip text for this property suitable for
   * displaying in the explorer/experimenter gui
   */
  public String numRegressionsTipText() {
    return "Set the size of the random samples used to generate the least sqaured "
    +"regression functions.";
  }

  public void setVariance(double var) {
    this.m_variance=var;
  }

  public double getVariance() {
    return(m_variance);
  }

  public String varianceTipText() {
    return("residual variance threshold of regressions");
  }
  
  public void setPct(int pct) {
    this.m_instancepct=pct;
  }

  public int getPct() {
    return(m_instancepct);
  }

  public String pctTipText() {
    return("pct instances for regression");
  }

  public void setError(int pct) {
    this.m_errPct=pct;
  }

  public int getError() {
    return(m_errPct);
  }

  public String errorTipText() {
    return("pct pos to get error for comparison. e.g 50=median. 75=upper quartile");
  }

  /**
   * sets number of samples
   *
   * @param samplesize value
   */
  public void setNumRegressions(int samplesize) {

    this.m_numreg = samplesize;
  }

  /**
   * gets number of samples
   *
   * @return value
   */
  public int getNumRegressions() {

    return m_numreg;
  }

  /**
   * Returns the tip text for this property
   * @return tip text for this property suitable for
   * displaying in the explorer/experimenter gui
   */
  public String randomSeedTipText() {
    return "Set the seed for selecting random subsamples of the training data.";
  }

  /**
   * Set the seed for the random number generator
   *
   * @param randomseed the seed
   */
  public void setRandomSeed(long randomseed) {

    m_randomseed = randomseed;
  }

  /**
   * get the seed for the random number generator
   *
   * @return the seed value
   */
  public long getRandomSeed() {

    return m_randomseed;
  }

  /**
   * sets  whether or not debugging output shouild be printed
   *
   * @param debug true if debugging output selected
   */
  @Override
  public void setDebug(boolean debug) {

    m_debug = debug;
  }

  /**
   * Returns whether or not debugging output shouild be printed
   *
   * @return true if debuging output selected
   */
  @Override
  public boolean getDebug() {

    return m_debug;
  }

  /**
   * Main method for running this class.
   *
   * @param argv the options
   */
  public static void main(String[] argv) {
    runClassifier(new LeastMedianSq(), argv);
  }
}
