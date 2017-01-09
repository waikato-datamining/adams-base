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
 * AbstainAverage.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package weka.classifiers.meta;

import weka.classifiers.AbstainingClassifier;
import weka.classifiers.MultipleClassifiersCombiner;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.RevisionUtils;
import weka.core.Utils;
import weka.core.WeightedInstancesHandler;

import java.util.Enumeration;
import java.util.Vector;

/**
 <!-- globalinfo-start -->
 * Average base classifiers, abstain if difference outside thresholds
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 *
 *
 <!-- options-end -->
 *
 * @author Dale
 * @version $Revision: 12763 $
 */
public class AbstainAverage
  extends MultipleClassifiersCombiner
  implements WeightedInstancesHandler, AbstainingClassifier {


  /** for serialization */
  static final long serialVersionUID = -1L;


  protected double m_MaxDifference;

  protected double[] m_MaxDifferences;
  protected double[] m_Mins;
  protected double[] m_Maxes;
  protected boolean[] m_IsPercent;
  protected String m_MaxDifferences_string="0-100,10";

  /**
   * Returns a string describing classifier
   * @return a description suitable for
   * displaying in the explorer/experimenter gui
   */
  public String globalInfo() {
    return
      "Class for combining classifiers. Result is the average of the classifiers."
        + "Abstain if the difference between the classifiers is different from the thresholds "
        +"specified";
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
   *
   <!-- options-end -->
   *
   * @param options the list of options as an array of strings
   * @throws Exception if an option is not supported
   */
  public void setOptions(String[] options) throws Exception {
    String 	tmpStr;

    String curropt = Utils.getOption('E', options);
    if (curropt.length() != 0) {
      setMaxDifference(curropt);
    } else {
      setMaxDifference("0-100,10");
    }

    super.setOptions(options);
  }


  /**
   * Returns default capabilities of the classifier.
   *
   * @return      the capabilities of this classifier
   */
  public Capabilities getCapabilities() {
    Capabilities result = super.getCapabilities();

    result.disableAllClasses();
    result.disableAllClassDependencies();
    result.enable(Capability.NUMERIC_CLASS);
    result.enableDependency(Capability.NUMERIC_CLASS);


    return result;
  }

  /**
   * Buildclassifier builds all sub-classifiers
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

   return(classificationNumericAverage(instance,true));
  }


  /**
   * Check prediction difference against thresholds
   * @param avgPrediction
   * @param difference
   * @return
   */
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

  /**
   * Convert thresholds from array to string
   * @return
   */
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

  /**
   * Do the classification. Abstain (or not) depending on flag
   * @param instance
   * @param abstain
   * @return
   * @throws Exception
   */
  protected double classificationNumericAverage(Instance instance, boolean abstain) throws Exception{
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
    if (!abstain){
      return(result);
    }
    double difference=Math.abs(max-min);

    if (!checkOK(result,difference)){
      return(Utils.missingValue());
    }

    return(result);
  }


  /**
   * Output a representation of this classifier
   *
   * @return a string representation of the classifier
   */
  public String toString() {

    if (m_Classifiers == null) {
      return "AbstainAverage: No model built yet.";
    }

    String result = "AbstainAverage combines";
    result += " the average of these base learners:\n";
    for (int i = 0; i < m_Classifiers.length; i++) {
      result += '\t' + getClassifierSpec(i) + '\n';
    }


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
    runClassifier(new AbstainAverage(), argv);
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

      return classificationNumericAverage(inst,false);
  }

  @Override
  public double[] getAbstentionDistribution(Instance inst) throws Exception {
    double[] ret=new double[1];
    ret[0]=getAbstentionClassification(inst);
    return ret;
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
