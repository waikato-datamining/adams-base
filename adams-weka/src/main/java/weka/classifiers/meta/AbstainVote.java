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

import weka.classifiers.AbstainingClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.RandomizableMultipleClassifiersCombiner;
import weka.classifiers.SingleClassifierEnhancer;
import weka.classifiers.functions.LinearRegressionJ;
import weka.core.*;
import weka.core.Capabilities.Capability;
import weka.experiment.Stats;
import weka.filters.Filter;
import weka.filters.unsupervised.instance.Resample;

import java.util.*;

/**
 <!-- globalinfo-start -->
 * Finds the base classifier with the best least median squared error.
 * <br><br>
 <!-- globalinfo-end -->
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
 * @author Dale
 * @version $Revision: 12763 $
 */
public class AbstainVote
  extends RandomizableMultipleClassifiersCombiner
  implements WeightedInstancesHandler, AbstainingClassifier {


  /** for serialization */
  static final long serialVersionUID = -637891196294399624L;

  /** combination rule: Average of Probabilities */
  public static final int AVERAGE_RULE = 1;
  /** combination rule: Product of Probabilities (only nominal classes) */
  public static final int PRODUCT_RULE = 2;
  /** combination rule: Majority Voting (only nominal classes) */
  public static final int MAJORITY_VOTING_RULE = 3;
  /** combination rule: Minimum Probability */
  public static final int MIN_RULE = 4;
  /** combination rule: Maximum Probability */
  public static final int MAX_RULE = 5;
  /** combination rule: Median Probability (only numeric class) */
  public static final int MEDIAN_RULE = 6;
  /** combination rules */
  public static final Tag[] TAGS_RULES = {
    new Tag(AVERAGE_RULE, "AVG", "Average of Probabilities"),
    new Tag(PRODUCT_RULE, "PROD", "Product of Probabilities"),
    new Tag(MAJORITY_VOTING_RULE, "MAJ", "Majority Voting"),
    new Tag(MIN_RULE, "MIN", "Minimum Probability"),
    new Tag(MAX_RULE, "MAX", "Maximum Probability"),
    new Tag(MEDIAN_RULE, "MED", "Median")
  };

  /** Combination Rule variable */
  protected int m_CombinationRule = AVERAGE_RULE;

  /** the random number generator used for breaking ties in majority voting
   * @see #distributionForInstanceMajorityVoting(Instance) */
  protected Random m_Random;

  protected double m_MaxDifference;

  protected double[] m_MaxDifferences;
  protected double[] m_Mins;
  protected double[] m_Maxes;
  protected boolean[] m_IsPercent;
  protected String m_MaxDifferences_string="";

  /**
   * Returns a string describing classifier
   * @return a description suitable for
   * displaying in the explorer/experimenter gui
   */
  public String globalInfo() {
    return
      "Class for combining classifiers. Different combinations of "
        + "probability estimates for classification are available.\n\n"
        + "For more information see:\n\n"
        + getTechnicalInformation().toString();
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  public Enumeration listOptions() {
    Enumeration 	enm;
    Vector		result;

    result = new Vector();

    enm = super.listOptions();
    while (enm.hasMoreElements())
      result.addElement(enm.nextElement());

    result.addElement(new Option(
      "\tThe combination rule to use\n"
        + "\t(default: AVG)",
      "R", 1, "-R " + Tag.toOptionList(TAGS_RULES)));

    result.addElement(new Option("\tMaximum acceptable difference between classifiers\n"
      + "\t(default: 10)\n",
      "E", 1, "-E <String>"));

    return result.elements();
  }

  /**
   * Gets the current settings of Vote.
   *
   * @return an array of strings suitable for passing to setOptions()
   */
  public String [] getOptions() {
    int       	i;
    Vector    	result;
    String[]  	options;

    result = new Vector();

    options = super.getOptions();
    for (i = 0; i < options.length; i++)
      result.add(options[i]);

    result.add("-R");
    result.add("" + getCombinationRule());

    result.add("-E");
    result.add("" + getMaxDifference());


    return (String[]) result.toArray(new String[result.size()]);
  }

  /**
   * Parses a given list of options. <p/>
   *
   <!-- options-start -->
   * Valid options are: <p/>
   *
   * <pre> -S &lt;num&gt;
   *  Random number seed.
   *  (default 1)</pre>
   *
   * <pre> -B &lt;classifier specification&gt;
   *  Full class name of classifier to include, followed
   *  by scheme options. May be specified multiple times.
   *  (default: "weka.classifiers.rules.ZeroR")</pre>
   *
   * <pre> -D
   *  If set, classifier is run in debug mode and
   *  may output additional info to the console</pre>
   *
   * <pre> -R &lt;AVG|PROD|MAJ|MIN|MAX|MED&gt;
   *  The combination rule to use
   *  (default: AVG)</pre>
   *
   <!-- options-end -->
   *
   * @param options the list of options as an array of strings
   * @throws Exception if an option is not supported
   */
  public void setOptions(String[] options) throws Exception {
    String 	tmpStr;

    tmpStr = Utils.getOption('R', options);
    if (tmpStr.length() != 0)
      setCombinationRule(new SelectedTag(tmpStr, TAGS_RULES));
    else
      setCombinationRule(new SelectedTag(AVERAGE_RULE, TAGS_RULES));

    String curropt = Utils.getOption('E', options);
    if (curropt.length() != 0) {
      //setMaxDifference(Double.parseDouble(curropt));
      setMaxDifference(curropt);
    } else {
      //setMaxDifference(10);
      setMaxDifference("0-100,10");
    }

    super.setOptions(options);
  }

  /**
   * Returns an instance of a TechnicalInformation object, containing
   * detailed information about the technical background of this class,
   * e.g., paper reference or book this class is based on.
   *
   * @return the technical information about this class
   */
  public TechnicalInformation getTechnicalInformation() {
    TechnicalInformation 	result;
    TechnicalInformation 	additional;

    result = new TechnicalInformation(TechnicalInformation.Type.BOOK);
    result.setValue(TechnicalInformation.Field.AUTHOR, "Ludmila I. Kuncheva");
    result.setValue(TechnicalInformation.Field.TITLE, "Combining Pattern Classifiers: Methods and Algorithms");
    result.setValue(TechnicalInformation.Field.YEAR, "2004");
    result.setValue(TechnicalInformation.Field.PUBLISHER, "John Wiley and Sons, Inc.");

    additional = result.add(TechnicalInformation.Type.ARTICLE);
    additional.setValue(TechnicalInformation.Field.AUTHOR, "J. Kittler and M. Hatef and Robert P.W. Duin and J. Matas");
    additional.setValue(TechnicalInformation.Field.YEAR, "1998");
    additional.setValue(TechnicalInformation.Field.TITLE, "On combining classifiers");
    additional.setValue(TechnicalInformation.Field.JOURNAL, "IEEE Transactions on Pattern Analysis and Machine Intelligence");
    additional.setValue(TechnicalInformation.Field.VOLUME, "20");
    additional.setValue(TechnicalInformation.Field.NUMBER, "3");
    additional.setValue(TechnicalInformation.Field.PAGES, "226-239");

    return result;
  }

  /**
   * Returns default capabilities of the classifier.
   *
   * @return      the capabilities of this classifier
   */
  public Capabilities getCapabilities() {
    Capabilities result = super.getCapabilities();

    // class
    if (    (m_CombinationRule == PRODUCT_RULE)
      || (m_CombinationRule == MAJORITY_VOTING_RULE) ) {
      result.disableAllClasses();
      result.disableAllClassDependencies();
      result.enable(Capability.NOMINAL_CLASS);
      result.enableDependency(Capability.NOMINAL_CLASS);
    }
    else if (m_CombinationRule == MEDIAN_RULE) {
      result.disableAllClasses();
      result.disableAllClassDependencies();
      result.enable(Capability.NUMERIC_CLASS);
      result.enableDependency(Capability.NUMERIC_CLASS);
    }

    return result;
  }

  /**
   * Buildclassifier selects a classifier from the set of classifiers
   * by minimising error on the training data.
   *
   * @param data the training data to be used for generating the
   * boosted classifier.
   * @throws Exception if the classifier could not be built successfully
   */
  public void buildClassifier(Instances data) throws Exception {

    // can classifier handle the data?
    getCapabilities().testWithFail(data);
    parseString(getMaxDifference());
    // remove instances with missing class
    Instances newData = new Instances(data);
    newData.deleteWithMissingClass();

    m_Random = new Random(getSeed());

    for (int i = 0; i < m_Classifiers.length; i++) {
      getClassifier(i).buildClassifier(newData);
    }
  }

  /**
   * Classifies the given test instance.
   *
   * @param instance the instance to be classified
   * @return the predicted most likely class for the instance or
   * Instance.missingValue() if no prediction is made
   * @throws Exception if an error occurred during the prediction
   */
  public double classifyInstance(Instance instance) throws Exception {
    double result;
    double[] dist;
    int index;

    switch (m_CombinationRule) {
      case AVERAGE_RULE:
        if (instance.classAttribute().isNumeric()) {
          return (classificationNumericAverage(instance));
        }
      case PRODUCT_RULE:
      case MAJORITY_VOTING_RULE:
      case MIN_RULE:
      case MAX_RULE:
        dist = distributionForInstance(instance);
        if (instance.classAttribute().isNominal()) {
          index = Utils.maxIndex(dist);
          if (dist[index] == 0)
            result = Utils.missingValue();
          else
            result = index;
        }
        else if (instance.classAttribute().isNumeric()){
          result = dist[0];
        }
        else {
          result = Utils.missingValue();
        }
        break;
      case MEDIAN_RULE:
        result = classifyInstanceMedian(instance);
        break;
      default:
        throw new IllegalStateException("Unknown combination rule '" + m_CombinationRule + "'!");
    }

    return result;
  }

  /**
   * Classifies the given test instance, returning the median from all
   * classifiers.
   *
   * @param instance the instance to be classified
   * @return the predicted most likely class for the instance or
   * Instance.missingValue() if no prediction is made
   * @throws Exception if an error occurred during the prediction
   */
  protected double classifyInstanceMedian(Instance instance) throws Exception {

    if (instance.classAttribute().isNumeric()){
      return(classificationNumericMedian(instance));
    }
    double[] results = new double[m_Classifiers.length];
    double result;

    for (int i = 0; i < results.length; i++)
      results[i] = m_Classifiers[i].classifyInstance(instance);

    if (results.length == 0)
      result = 0;
    else if (results.length == 1)
      result = results[0];
    else
      result = Utils.kthSmallestValue(results, results.length / 2);

    return result;
  }

  /**
   * Classifies a given instance using the selected combination rule.
   *
   * @param instance the instance to be classified
   * @return the distribution
   * @throws Exception if instance could not be classified
   * successfully
   */
  public double[] distributionForInstance(Instance instance) throws Exception {
    double[] result = new double[instance.numClasses()];

    switch (m_CombinationRule) {
      case AVERAGE_RULE:
        result = distributionForInstanceAverage(instance);
        break;
      case PRODUCT_RULE:
        result = distributionForInstanceProduct(instance);
        break;
      case MAJORITY_VOTING_RULE:
        result = distributionForInstanceMajorityVoting(instance);
        break;
      case MIN_RULE:
        result = distributionForInstanceMin(instance);
        break;
      case MAX_RULE:
        result = distributionForInstanceMax(instance);
        break;
      case MEDIAN_RULE:
        result[0] = classifyInstance(instance);
        break;
      default:
        throw new IllegalStateException("Unknown combination rule '" + m_CombinationRule + "'!");
    }

    if (!instance.classAttribute().isNumeric() && (Utils.sum(result) > 0))
      Utils.normalize(result);

    return result;
  }

  protected double classificationNumericMedian(Instance instance)throws Exception{
    double max=Double.NEGATIVE_INFINITY;
    double min=Double.POSITIVE_INFINITY;

    double[] results = new double[m_Classifiers.length];
    double result;

    for (int i = 0; i < results.length; i++) {

      results[i] = m_Classifiers[i].classifyInstance(instance);
      if (results[i] > max){
        max=results[i];
      }
      if (results[i] < min){
        min=results[i];
      }
    }
    if (Math.abs(max-min) > m_MaxDifference){
      return(Utils.missingValue());
    }

    if (results.length == 0)
      result = 0;
    else if (results.length == 1)
      result = results[0];
    else
      result = Utils.kthSmallestValue(results, results.length / 2);

    return result;
  }

  protected boolean checkOK(double avgPrediction, double difference){

    for (int i=0;i<m_MaxDifferences.length;i++){
      if (avgPrediction >= m_Mins[i] && avgPrediction <= m_Maxes[i]){
        if (m_IsPercent[i]){
          if (difference/avgPrediction <= m_MaxDifferences[i]) {
            return (true);
          } else {
            return (false);
          }
        } else {
          if (difference <= m_MaxDifferences[i]) {
            return (true);
          } else {
            return (false);
          }
        }
      }
    }
    return(false);
  }

  /**
   * Expect ; separated entries with min,max,difference e.g.
   * 0-50,6;50-100,10%;100-200,20
   * @param input
   */
  protected void parseString(String input){

    if (input == null){
      return;
    }
    String[] entries=input.split(";");
    m_Mins=new double[entries.length];
    m_Maxes=new double[entries.length];
    m_MaxDifferences=new double[entries.length];
    m_IsPercent=new boolean[entries.length];

    for (int i=0;i<entries.length;i++){
      String[] entry=entries[i].split(",");
      String[] range=entry[0].split("-");
      double val1=Double.parseDouble(range[0]);
      double val2=Double.parseDouble(range[1]);
      double diff=Double.MAX_VALUE;
      if (entry[1].matches(".*%")){
        String d=entry[1].replaceAll("%","");
        diff=Double.parseDouble(d)/100.0;
        m_IsPercent[i]=true;
      } else {
         diff = Double.parseDouble(entry[1]);
        m_IsPercent[i]=false;
      }
      m_Mins[i]=Math.min(val1,val2);
      m_Maxes[i]=Math.max(val1,val2);
      m_MaxDifferences[i]=diff;
    }
  }

  protected String diffToString(){
    String res="";
    if (m_MaxDifferences == null){
      return("");
    }
    for (int i=0;i<m_MaxDifferences.length;i++){
      if (i != 0){
        res+=";";
      }
      res+=m_Mins[i]+"-"+m_Maxes[i]+",";
      if (m_IsPercent[i]) {
        res+=m_MaxDifferences[i]*100+"%";
      } else {
        res+=m_MaxDifferences[i];
      }
    }
    return(res);
  }

  protected double classificationNumericAverage(Instance instance) throws Exception{
    double max=Double.NEGATIVE_INFINITY;
    double min=Double.POSITIVE_INFINITY;

    double sum=0;
    for (int i = 0; i < m_Classifiers.length; i++) {
      double pred = getClassifier(i).classifyInstance(instance);
      if (pred > max){
        max=pred;
      }
      if (pred < min){
        min=pred;
      }
      sum +=pred;
    }
    double result=sum/m_Classifiers.length;
    double difference=Math.abs(max-min);
    if (!checkOK(result,difference)){
      return(Utils.missingValue());
    }

    return(result);
  }

  /**
   * Classifies a given instance using the Average of Probabilities
   * combination rule.
   *
   * @param instance the instance to be classified
   * @return the distribution
   * @throws Exception if instance could not be classified
   * successfully
   */
  protected double[] distributionForInstanceAverage(Instance instance) throws Exception {

    if (instance.classAttribute().isNumeric()){
      double[] ret=new double[1];
      ret[0]=classificationNumericAverage(instance);
      return(ret);
    }
    double[] probs = getClassifier(0).distributionForInstance(instance);
    probs = (double[])probs.clone();

    for (int i = 1; i < m_Classifiers.length; i++) {
      double[] dist = getClassifier(i).distributionForInstance(instance);
      for (int j = 0; j < dist.length; j++) {
        probs[j] += dist[j];
      }
    }
    for (int j = 0; j < probs.length; j++) {
      probs[j] /= (double)m_Classifiers.length;
    }
    return probs;
  }

  /**
   * Classifies a given instance using the Product of Probabilities
   * combination rule.
   *
   * @param instance the instance to be classified
   * @return the distribution
   * @throws Exception if instance could not be classified
   * successfully
   */
  protected double[] distributionForInstanceProduct(Instance instance) throws Exception {

    double[] probs = getClassifier(0).distributionForInstance(instance);
    probs = (double[])probs.clone();

    for (int i = 1; i < m_Classifiers.length; i++) {
      double[] dist = getClassifier(i).distributionForInstance(instance);
      for (int j = 0; j < dist.length; j++) {
        probs[j] *= dist[j];
      }
    }

    return probs;
  }

  /**
   * Classifies a given instance using the Majority Voting combination rule.
   *
   * @param instance the instance to be classified
   * @return the distribution
   * @throws Exception if instance could not be classified
   * successfully
   */
  protected double[] distributionForInstanceMajorityVoting(Instance instance) throws Exception {

    double[] probs = new double[instance.classAttribute().numValues()];
    double[] votes = new double[probs.length];

    for (int i = 0; i < m_Classifiers.length; i++) {
      probs = getClassifier(i).distributionForInstance(instance);
      int maxIndex = 0;
      for(int j = 0; j<probs.length; j++) {
        if(probs[j] > probs[maxIndex])
          maxIndex = j;
      }

      // Consider the cases when multiple classes happen to have the same probability
      for (int j=0; j<probs.length; j++) {
        if (probs[j] == probs[maxIndex])
          votes[j]++;
      }
    }

    int tmpMajorityIndex = 0;
    for (int k = 1; k < votes.length; k++) {
      if (votes[k] > votes[tmpMajorityIndex])
        tmpMajorityIndex = k;
    }

    // Consider the cases when multiple classes receive the same amount of votes
    Vector<Integer> majorityIndexes = new Vector<Integer>();
    for (int k = 0; k < votes.length; k++) {
      if (votes[k] == votes[tmpMajorityIndex])
        majorityIndexes.add(k);
    }
    // Resolve the ties according to a uniform random distribution
    int majorityIndex = majorityIndexes.get(m_Random.nextInt(majorityIndexes.size()));

    //set probs to 0
    probs = new double[probs.length];
    probs[majorityIndex] = 1; //the class that have been voted the most receives 1

    return probs;
  }

  /**
   * Classifies a given instance using the Maximum Probability combination rule.
   *
   * @param instance the instance to be classified
   * @return the distribution
   * @throws Exception if instance could not be classified
   * successfully
   */
  protected double[] distributionForInstanceMax(Instance instance) throws Exception {

    double[] max = getClassifier(0).distributionForInstance(instance);
    max = (double[])max.clone();

    for (int i = 1; i < m_Classifiers.length; i++) {
      double[] dist = getClassifier(i).distributionForInstance(instance);
      for (int j = 0; j < dist.length; j++) {
        if(max[j]<dist[j])
          max[j]=dist[j];
      }
    }

    return max;
  }

  /**
   * Classifies a given instance using the Minimum Probability combination rule.
   *
   * @param instance the instance to be classified
   * @return the distribution
   * @throws Exception if instance could not be classified
   * successfully
   */
  protected double[] distributionForInstanceMin(Instance instance) throws Exception {

    double[] min = getClassifier(0).distributionForInstance(instance);

    min = (double[])min.clone();

    for (int i = 1; i < m_Classifiers.length; i++) {
      double[] dist = getClassifier(i).distributionForInstance(instance);
      for (int j = 0; j < dist.length; j++) {
        if(dist[j]<min[j])
          min[j]=dist[j];
      }
    }

    return min;
  }

  /**
   * Returns the tip text for this property
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String combinationRuleTipText() {
    return "The combination rule used.";
  }

  /**
   * Gets the combination rule used
   *
   * @return 		the combination rule used
   */
  public SelectedTag getCombinationRule() {
    return new SelectedTag(m_CombinationRule, TAGS_RULES);
  }

  /**
   * Sets the combination rule to use. Values other than
   *
   * @param newRule 	the combination rule method to use
   */
  public void setCombinationRule(SelectedTag newRule) {
    if (newRule.getTags() == TAGS_RULES)
      m_CombinationRule = newRule.getSelectedTag().getID();
  }

  /**
   * Output a representation of this classifier
   *
   * @return a string representation of the classifier
   */
  public String toString() {

    if (m_Classifiers == null) {
      return "Vote: No model built yet.";
    }

    String result = "Vote combines";
    result += " the probability distributions of these base learners:\n";
    for (int i = 0; i < m_Classifiers.length; i++) {
      result += '\t' + getClassifierSpec(i) + '\n';
    }
    result += "using the '";

    switch (m_CombinationRule) {
      case AVERAGE_RULE:
        result += "Average of Probabilities";
        break;

      case PRODUCT_RULE:
        result += "Product of Probabilities";
        break;

      case MAJORITY_VOTING_RULE:
        result += "Majority Voting";
        break;

      case MIN_RULE:
        result += "Minimum Probability";
        break;

      case MAX_RULE:
        result += "Maximum Probability";
        break;

      case MEDIAN_RULE:
        result += "Median Probability";
        break;

      default:
        throw new IllegalStateException("Unknown combination rule '" + m_CombinationRule + "'!");
    }

    result += "' combination rule \n";

    return result;
  }

  /**
   * Returns the revision string.
   *
   * @return		the revision
   */
  public String getRevision() {
    return RevisionUtils.extract("$Revision: 7930 $");
  }

  /**
   * Main method for testing this class.
   *
   * @param argv should contain the following arguments:
   * -t training file [-T test file] [-c class index]
   */
  public static void main(String [] argv) {
    runClassifier(new Vote(), argv);
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

      return classifyInstance(inst);
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
   * Returns the tip text for this property
   * @return tip text for this property suitable for
   * displaying in the explorer/experimenter gui
   */
  public String maxDifferenceTipText() {
    return "Max difference, else abstain.";
  }

  /**
   * gets number of samples
   *
   * @return value
   */
  public String getMaxDifference() {

    return m_MaxDifferences_string;
  }

  public void setMaxDifference(String s) {

    m_MaxDifferences_string=s;

  }


}
