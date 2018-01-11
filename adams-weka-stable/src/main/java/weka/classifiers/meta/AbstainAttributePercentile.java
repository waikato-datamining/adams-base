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
 * AbstainAttributePercentile.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */

package weka.classifiers.meta;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import weka.classifiers.AbstainingClassifier;
import weka.classifiers.SingleClassifierEnhancer;
import weka.core.AttributeStats;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.RevisionUtils;
import weka.core.Utils;
import weka.core.WeightedInstancesHandler;
import weka.experiment.Stats;
import adams.core.Range;
import adams.data.statistics.Percentile;

/**
 * Only predict if attribute value within percentile range. Ensure same for 
 * training set if req.
 *
 * @author Dale
 * @version $Revision$
 */
public class AbstainAttributePercentile
extends SingleClassifierEnhancer
implements WeightedInstancesHandler, AbstainingClassifier {

  /** for serialization. */
  private static final long serialVersionUID = 3063502797829542418L;

  protected Instances m_Data;

  protected int m_attnum = 1;

  protected boolean m_debug = false;

  protected double m_pctile=0.98;

  protected boolean m_ok=true;

  protected boolean m_removetrain=false;

  protected double m_min,m_max;


  /** the ranges (attribute index &lt;-&gt; double[]). */
  protected Hashtable<Integer,double[]> m_Ranges = new Hashtable<Integer,double[]>();

  protected Range m_Range=new Range("first-last");


  /**
   * Returns a string describing classifier.
   *
   * @return 		a description suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String globalInfo() {
    return "Only predict if attribute value within percentile range. Ensure same for training set if req.";
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

    newVector.addElement(new Option("\tAttribute Range to check\n"
	+ "\t(default: 1)\n",
	"S", 1, "-S <sint>"));
    newVector.addElement(new Option("\tPercentile\n"
	+ "\t(default: 0.98)\n",
	"P", 1, "-P <double>"));
    newVector.addElement(new Option("\tApply to Train\n"
	+ "\t(default: false)\n",
	"E", 0, "-E"));

    return newVector.elements();
  }

  /**
   * Sets the OptionHandler's options using the given list. All options
   * will be set (or reset) during this call (i.e. incremental setting
   * of options is not possible).
   *
   <!-- options-start -->
   * Valid options are: <br><br>
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
      setAttribute(curropt);
    } else
      setAttribute("first-last");

    curropt = Utils.getOption('P', options);
    if ( curropt.length() != 0) {
      setPercentile(Double.parseDouble(curropt));
    } else
      setPercentile(0.98);

    setRemoveTrain(Utils.getFlag('E', options));


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
    result.add (""+getAttribute());

    result.add ("-P");
    result.add ("" + getPercentile());

    if (getRemoveTrain()){
      result.add ("-E");
    }

    String[] options = super.getOptions();
    for (String option: options) result.add(option);
    return result.toArray(new String[result.size()]);
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
    result.disable(Capability.NOMINAL_CLASS);

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
    data = new Instances(data);

    m_Range.setMax(data.numAttributes());
    for (int i=0;i<data.numAttributes();i++){
      if (!m_Range.isInRange(i) || data.classIndex() == i){
	continue;
      }
      double[] dats=data.attributeToDoubleArray(i);
      Percentile<Double> p=new Percentile<Double>();
      for(double d:dats){
	p.add(d);
      }
      double[] minmax=new double[2]; 
      minmax[0]=p.getPercentile(1-getPercentile());
      minmax[1]=p.getPercentile(getPercentile());
      m_Ranges.put(i, minmax);
    }
    m_ok=false;
    if (getRemoveTrain()){
      m_Data = new Instances(data,100);
      for (Instance in:data){
	boolean inside=true;
	for (int i=0;i<data.numAttributes();i++){
	  if (!m_Range.isInRange(i) || data.classIndex() == i){
	    continue;
	  }
	  double val=in.value(i);
	  double[] minmax=m_Ranges.get(i);
	  if (minmax==null){
	    System.err.println("Got no minmax for:"+i);
	    continue;
	  }
	  if (val < minmax[0] || val > minmax[1]){
	    inside=false;
	    break;
	  }
	}
	if (inside){
	  m_Data.add(in);
	  m_ok=true;
	}
      }

    } else {
      m_Data = data;
      m_ok=true;
    }

    if (m_ok){
      m_Classifier.buildClassifier(m_Data);
    }

    AttributeStats as=data.attributeStats(m_Data.classIndex());
    Stats s=as.numericStats;
    m_min=s.min;
    m_max=s.max;

  }

  /**
   * Returns the prediction.
   */
  @Override
  public double classifyInstance(Instance inst) throws Exception {

    for (int i=0;i<inst.numAttributes();i++){
      if (!m_Range.isInRange(i) || inst.classIndex() == i){
	continue;
      }
      double val=inst.value(i);
      double[] minmax=m_Ranges.get(i);
      if (minmax==null){
	System.err.println("Got no minmax for:"+i);
	continue;
      }
      if (val < minmax[0] || val > minmax[1]){
	return Utils.missingValue();
      }
    }

    double res = m_Classifier.classifyInstance(inst);
    if (Utils.isMissingValue(res) || res< m_min || res > m_max){
      //System.err.println("Prediction outside training range.");
      return Utils.missingValue();
    }

    return(res);
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
    if (m_Classifier instanceof AbstainingClassifier)
      return ((AbstainingClassifier) m_Classifier).getAbstentionDistribution(inst);
    else
      return m_Classifier.distributionForInstance(inst);
  }

  /**
   * Returns description of classifier.
   */
  @Override
  public String toString() {
    return m_Classifier.toString();
  }

  @Override
  public String getRevision() {
    return RevisionUtils.extract("$Revision$");
  }

  public void setPercentile(double var) {
    this.m_pctile=var;
  }

  public double getPercentile() {
    return(m_pctile);
  }

  public String percentileTipText() {
    return("percentile of attribute values");
  }

  public void setAttribute(String at) {
    m_Range=new Range(at);
  }

  public String getAttribute() {
    return(m_Range.getRange());
  }

  public String attributeTipText() {
    return("Attributes to operate on");
  }


  public void setRemoveTrain(boolean t) {
    m_removetrain = t;
  }

  public boolean getRemoveTrain() {
    return m_removetrain;
  }

  public String removeTrainTipText() {
    return "Whether to remove data outside the percentile from the training data.";
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
