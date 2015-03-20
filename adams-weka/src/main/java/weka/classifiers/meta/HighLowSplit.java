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
 * LeastMedianSq.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package weka.classifiers.meta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.SingleClassifierEnhancer;
import weka.classifiers.rules.ZeroR;
import weka.core.Capabilities;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.RevisionUtils;
import weka.core.Utils;
import weka.core.WeightedInstancesHandler;

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
 * @author Dale?
 * @version $Revision: 6936 $
 */
public class HighLowSplit
  extends SingleClassifierEnhancer
  implements WeightedInstancesHandler{

  /** suid	 */
  private static final long serialVersionUID = -1948204213212845838L;
  
  protected Classifier m_hiClassifier=new ZeroR();
  protected Classifier m_loClassifier=new ZeroR();

  protected double m_hiLopoint=10.0;
  protected double m_loHipoint=15.0;
  protected double m_splitpoint=12.5;
  
  public double getHiLopoint(){
	  return(m_hiLopoint);
  }
  
  public void setHiLopoint(double h){
	  m_hiLopoint=h;
  }
  
  public String hiLopointTipText(){
	  return("hlpoint");
  }
  
  public double getLoHipoint(){
	  return(m_loHipoint);
  }

  public void setLoHipoint(double l){
	  m_loHipoint=l;
  }
  
  public String loHipointTipText(){
	  return("lhpoint");
  }
  
  public double getSplitpoint(){
	  return(m_splitpoint);
  }
  
  public void setSplitpoint(double s){
	  m_splitpoint=s;
  }
  
  public String splitpointTipText(){
	  return("splitpoint");
  }
  
  public Classifier getHiClassifier(){
	  return(m_hiClassifier);
  }
  
  public void setHiClassifier(Classifier c){
	  m_hiClassifier=c;
  }
  
  public String hiClassifierTipText(){
	  return("hi classifier");
  }
  
  public Classifier getLoClassifier(){
	  return(m_loClassifier);
  }
  
  public void setLoClassifier(Classifier c){
	  m_loClassifier=c;
  }
  
  public String loClassifierTipText(){
	  return("lo classifier");
  }
  
  /**
   * String describing default classifier.
   */
  protected String defaultClassifierString() {

    return "weka.classifiers.rules.ZeroR";
  }

  /**
   * String describing options for default classifier.
   */
  protected String[] defaultClassifierOptions() {

    return new String[0];
  }
  
  
  
  
  /**
   * Returns a string describing classifier.
   *
   * @return 		a description suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String globalInfo() {
    return "Uses base classifier to get guess, then get prediction from either lo/hi classifier";
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  public Enumeration listOptions() {

    Vector newVector = new Vector();

    Enumeration enu = super.listOptions();
    while (enu.hasMoreElements()) {
      newVector.addElement(enu.nextElement());
    }

    newVector.addElement(new Option("\tLowest value for hi classifier\n"
	+ "\t(default: 10)\n",
	"S", 1, "-S <dbl>"));
    newVector.addElement(new Option("\tHighest value for lo classifier\n"
	+ "\t(default: 15)\n",
	"P", 1, "-P <dbl>"));
    newVector.addElement(new Option("\tDecision boundary for hi/lo classifier\n"
	+ "\t(default: 12.5)\n",
	"E", 1, "-E <dbl>"));
    
    newVector.addElement(new Option(
            "\tFull name of hi classifier.\n"
            + "\t(default: " + defaultClassifierString() + 
            ((defaultClassifierOptions().length > 0) ? 
             " with options " + Utils.joinOptions(defaultClassifierOptions()) + ")" : ")"),
            "F", 1, "-F"));
      
      newVector.addAll(Collections.list(super.listOptions()));

      newVector.addElement(new Option(
            "",
            "", 0, "\nOptions specific to classifier "
            + m_hiClassifier.getClass().getName() + ":"));
      newVector.addAll(Collections.list(((OptionHandler)m_hiClassifier).listOptions()));
      
      
      newVector.addElement(new Option(
              "\tFull name of lo classifier.\n"
              + "\t(default: " + defaultClassifierString() + 
              ((defaultClassifierOptions().length > 0) ? 
               " with options " + Utils.joinOptions(defaultClassifierOptions()) + ")" : ")"),
              "G", 1, "-G"));
        
        newVector.addAll(Collections.list(super.listOptions()));

        newVector.addElement(new Option(
              "",
              "", 0, "\nOptions specific to classifier "
              + m_loClassifier.getClass().getName() + ":"));
        newVector.addAll(Collections.list(((OptionHandler)m_loClassifier).listOptions()));

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
  public void setOptions(String[] options) throws Exception {

    String curropt = Utils.getOption('S', options);
    if ( curropt.length() != 0) {
      setHiLopoint(Double.parseDouble(curropt));
    } else
    	setHiLopoint(10.0);

    curropt = Utils.getOption('P', options);
    if ( curropt.length() != 0) {
      setLoHipoint(Double.parseDouble(curropt));
    } else
      setLoHipoint(15.0);

    curropt = Utils.getOption('E', options);
    if ( curropt.length() != 0) {
      setSplitpoint(Double.parseDouble(curropt));
    } else
      setSplitpoint(12.5);


    String classifierName = Utils.getOption('F', options);

    if (classifierName.length() > 0) {
      setHiClassifier(AbstractClassifier.forName(classifierName, null));
      setHiClassifier(AbstractClassifier.forName(classifierName,
            Utils.partitionOptions(options)));
    } else {
      setHiClassifier(AbstractClassifier.forName(defaultClassifierString(), null));
      String[] classifierOptions = Utils.partitionOptions(options);
      if (classifierOptions.length > 0) {
        setHiClassifier(AbstractClassifier.forName(defaultClassifierString(),
                                                 classifierOptions));
      } else {
        setHiClassifier(AbstractClassifier.forName(defaultClassifierString(),
                                                 defaultClassifierOptions()));
      }
    }
    
    
    classifierName = Utils.getOption('G', options);

    if (classifierName.length() > 0) {
      setLoClassifier(AbstractClassifier.forName(classifierName, null));
      setLoClassifier(AbstractClassifier.forName(classifierName,
            Utils.partitionOptions(options)));
    } else {
      setLoClassifier(AbstractClassifier.forName(defaultClassifierString(), null));
      String[] classifierOptions = Utils.partitionOptions(options);
      if (classifierOptions.length > 0) {
        setLoClassifier(AbstractClassifier.forName(defaultClassifierString(),
                                                 classifierOptions));
      } else {
        setLoClassifier(AbstractClassifier.forName(defaultClassifierString(),
                                                 defaultClassifierOptions()));
      }
    }
    
    

    super.setOptions(options);
  }

  /**
   * Gets the current option settings for the OptionHandler.
   *
   * @return the list of current option settings as an array of strings
   */
  public String[] getOptions() {
    List<String> result = new ArrayList<String>();
    

    result.add ("-S");
    result.add (""+getHiLopoint());

    result.add ("-P");
    result.add ("" + getLoHipoint());

    result.add ("-E");
    result.add ("" + getSplitpoint());

    result.add ("-F");
    result.add(getHiClassifier().getClass().getName());
    
    String[] classifierOptions = ((OptionHandler)m_hiClassifier).getOptions();
    if (classifierOptions.length > 0) {
    	result.add("--");
      Collections.addAll(result, classifierOptions);
    }
    
    result.add ("-G");
    result.add(getLoClassifier().getClass().getName());
    
    classifierOptions = ((OptionHandler)m_loClassifier).getOptions();
    if (classifierOptions.length > 0) {
    	result.add("--");
      Collections.addAll(result, classifierOptions);
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
  public Capabilities getCapabilities() {
    Capabilities        result;

    result = super.getCapabilities();
    result.setMinimumNumberInstances(1);

    return result;
  }

  /**
   * Builds the classifier.
   *
   * @param data	the training data
   * @throws Exception	if something goes wrong
   */
  public void buildClassifier(Instances data) throws Exception {
    getCapabilities().testWithFail(data);
    
    // build overall
    m_Classifier.buildClassifier(data);
    
    // split into hi/lo
    
    Instances hi=new Instances(data,0);
    Instances lo=new Instances(data,0);
    
    for (Instance i:data){
    	if (i.classValue() <= m_loHipoint){
    		Instance in=new DenseInstance(i);
    		in.setDataset(lo);
    		lo.add(in);
    	}
    	if (i.classValue() >= m_hiLopoint){
    		Instance in=new DenseInstance(i);
    		in.setDataset(hi);
    		hi.add(in);
    	}
    }
    
    m_hiClassifier.buildClassifier(hi);
    m_loClassifier.buildClassifier(lo);
    
  }

  /**
   * Returns the prediction.
   */
  public double classifyInstance(Instance inst) throws Exception {
	  
	  double value=m_Classifier.classifyInstance(inst);
	  if (value < getSplitpoint()){
		  return(m_loClassifier.classifyInstance(inst));
	  } else {
		  return(m_hiClassifier.classifyInstance(inst));
	  }
  }

  /**
   * Returns description of classifier.
   */
  public String toString() {
    return m_Classifier.toString();
  }

  public String getRevision() {
    return RevisionUtils.extract("$Revision: 6936 $");
  }



  /**
   * Main method for running this class.
   *
   * @param argv the options
   */
  public static void main(String[] argv) {
    runClassifier(new HighLowSplit(), argv);
  }
}
