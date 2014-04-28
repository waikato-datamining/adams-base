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
 *    RandomModelTrees.java
 *    Copyright (C) 2009 University of Waikato
 *
 */

package weka.classifiers.trees;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import weka.classifiers.RandomizableClassifier;
import weka.classifiers.functions.LinearRegression;
import weka.classifiers.meta.Corr;
import weka.core.Capabilities;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Utils;
import weka.core.WeightedInstancesHandler;
import weka.core.Capabilities.Capability;

// this version: ONLY do MAX-DEPTH, no -M anymore (for simplicity ...)
/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author Bernhard Pfahringer (bernhard@cs.waikato.ac.nz)
 * @version $Revision$
 */
public class RandomModelTrees
  extends RandomizableClassifier
  implements OptionHandler, WeightedInstancesHandler {

  /** for serialization. */
  private static final long serialVersionUID = -3131038090987633675L;

  protected Node[] m_node;

  /** The number of iterations. */
  protected int m_numIterations = 0;

  protected int m_max = 0;

  protected Instances m_data = null;

  protected int m_numTrials = 0;

  protected double m_ridge = 0.0;

  /**
   * Returns a string describing classifier.
   *
   * @return 		a description suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String globalInfo() {
    return
        "RandomModelTrees: work on raw input (no preprocesssing whatsoever)\n"
      + "does not even center the class-value (???)\n"
      + "uses straight LinearRegression in leaves";
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  public Enumeration listOptions() {
    Vector newVector = new Vector();

    newVector.addElement(new Option(
	"\tThe number of attrs to try. (0 = 5)",
	"K", 1, "-K <int>"));

    newVector.addElement(new Option(
	"\tThe ridge value for regression. (0.0 = 0.001 for M==2, 0.0001 else)",
	"R", 1, "-R <double>"));

    newVector.addElement(new Option(
	"\tNumber of trees.\n"
	+ "\t(default 0 = 250)",
	"N", 1, "-N <num>"));

    newVector.addElement(new Option(
	"\t Max Tree Height.\n"
	+ "\t(default 0 = max(2, log2(#ex)-log2(#attr)-1))",
	"M", 1, "-M <num>"));

    Enumeration enum1 = super.listOptions();
    while (enum1.hasMoreElements()) {
      newVector.addElement(enum1.nextElement());
    }
    return newVector.elements();
  }

  /**
   * Parses a given list of options.
   *
   <!-- options-start -->
   <!-- options-end -->
   *
   * @param options the list of options as an array of strings
   * @exception Exception if an option is not supported
   */
  public void setOptions(String[] options) throws Exception {

    String iterations = Utils.getOption('N', options);
    if (iterations.length() != 0) {
      setNumIterations(Integer.parseInt(iterations));
    } else {
      setNumIterations(0);
    }

    iterations = Utils.getOption('M', options);
    if (iterations.length() != 0) {
      setMax(Integer.parseInt(iterations));
    } else {
      setMax(0);
    }

    String tmpStr = Utils.getOption('K', options);
    if (tmpStr.length() > 0) {
      setTrials(Integer.parseInt(tmpStr));
    } else {
      setTrials(0);
    }

    tmpStr = Utils.getOption('R', options);
    if (tmpStr.length() > 0) {
      setRidge(Double.parseDouble(tmpStr));
    } else {
      setRidge(0.0);
    }

    super.setOptions(options);
  }

  /**
   * Gets the current settings of the Classifier.
   *
   * @return an array of strings suitable for passing to setOptions
   */
  public String [] getOptions() {

    String [] superOptions = super.getOptions();
    String [] options = new String [superOptions.length + 8];

    options[0] = "-K";
    options[1] = "" + getTrials();

    options[2] = "-M";
    options[3] = "" + getMax();

    options[4] = "-N";
    options[5] = "" + getNumIterations();

    options[6] = "-R";
    options[7] = "" + getRidge();

    System.arraycopy(superOptions, 0, options, 8, superOptions.length);

    return options;
  }

  public int getTrials() {
    return m_numTrials;
  }

  public void setTrials(int n) {
    m_numTrials = n;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String trialsTipText() {
    return "The number of trials.";
  }

  public double getRidge() {
    return m_ridge;
  }

  public void setRidge(double c) {
    m_ridge = c;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String ridgeTipText() {
    return "The ridge to use.";
  }

  /**
   * Sets the number of iterations.
   */
  public void setNumIterations(int numIterations) {

    m_numIterations = numIterations;
  }

  /**
   * Gets the number of iterations.
   *
   * @return the maximum number of iterations
   */
  public int getNumIterations() {

    return m_numIterations;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String numIterationsTipText() {
    return "The number of iterations.";
  }

  public int getMax() {
    return m_max;
  }

  public void setMax(int n) {
    m_max = n;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String maxTipText() {
    return "The maximum height of the trees.";
  }

  /**
   * Returns default capabilities of the classifier.
   *
   * @return      the capabilities of this classifier
   */
  public Capabilities getCapabilities() {
    Capabilities result = super.getCapabilities();

    // attributes
    result.enable(Capability.NUMERIC_ATTRIBUTES);
    result.enable(Capability.DATE_ATTRIBUTES);

    // class
    result.enable(Capability.NUMERIC_CLASS);

    return result;
  }

  /**
   *
   *
   * @param data the training data to be used for generating the
   * @exception Exception if the classifier could not be built successfully
   */
  public void buildClassifier(Instances data) throws Exception {

    getCapabilities().testWithFail(data);

    m_data = new Instances(data,0); // keep for printing ???

    int numIterations = getNumIterations();
    if (numIterations == 0) {
      numIterations = 250;
    }

    m_node = new Node[numIterations];
    Random r = new Random(getSeed());

    int n = data.numInstances();
    List<Instance> all = new ArrayList<Instance>(n);
    for(int i = 0; i < n; i++) {
      all.add(data.instance(i));
    }

    int trials = getTrials();
    if (trials == 0) {
      // should we make that depend on numAttributes?
      trials = 5;
    }
    int maxHeight = getMax();
    if (maxHeight == 0) {
      int nn = Integer.bitCount(Integer.highestOneBit(n) - 1);
      int aa = Integer.bitCount(Integer.highestOneBit(data.numAttributes()-1) - 1);
      if (getDebug()) {
	System.out.println("n " + n + " " + nn);
	System.out.println("a " + (data.numAttributes()-1)  + " " + aa);
      }
      maxHeight = nn - aa - 1;
      if (maxHeight < 2) {
	maxHeight = 2;
      }
    }
    double ridge = getRidge();
    if (ridge <= 0.0) {
      if (maxHeight == 2) {
	ridge = 0.001;
      } else {
	ridge = 0.0001;
      }
    }

    if (getDebug())
      System.out.println("effective parameters: n = " + numIterations + " r = " + ridge + " k = " + trials + " m = " + maxHeight);

    Comparator<Instance>[] comparators = getComparators(data.numAttributes());

    for(int j = 0; j < m_node.length; j++) {
      m_node[j] = new Node(all,r,maxHeight,ridge,trials,comparators);
      /* Bagging does not seem to work as well ???
	List<Instance> bag = new ArrayList<Instance>(n);
      for(int i = 0; i < n; i++) {
	bag.add(all.get(r.nextInt(n)));
      }
      m_node[j] = new Node(bag,r, maxHeight,ridge,trials);
      */
    }
  }

  public Comparator<Instance>[] getComparators(int k) {
    Comparator[] comparators = new Comparator[k];
    for(int i = 0; i < k; i++) {
      final int comparisonIndex = i;
      comparators[i] = new Comparator<Instance>() {
	  public int compare(Instance x1, Instance x2) {
	      double v1 = x1.value(comparisonIndex);
	      double v2 = x2.value(comparisonIndex);
	      if (v1 < v2) return -1;
	      if (v1 > v2) return 1;
	      return 0;
	  }
      };
    }
    return (Comparator<Instance>[]) comparators;
  }

  /**
   * Calculates the class membership probabilities for the given test instance.
   *
   * @param instance the instance to be classified
   * @return preedicted class probability distribution
   * @exception Exception if distribution can't be computed successfully
   */
  public double classifyInstance(Instance instance) throws Exception {

    double sum = 0.0;
    for(Node node: m_node) {
      sum += node.classifyInstance(instance);
    }
    double result = sum/m_node.length;
    if (getDebug()) {
      System.out.println("xxx " + result);
    }
    return result;
  }

  /**
   * Returns description of the bagged classifier.
   *
   * @return description of the bagged classifier as a string
   */
  public String toStringOLD() {

    if (m_node == null) {
      return "RandomModelTrees: No model built yet.";
    }
    StringBuffer text = new StringBuffer();
    text.append("RandomModelTrees: \n\n");
    for(Node node: m_node) {
      node.toString(0,text,m_data);
      text.append("\n\n");
      text.append("-------------------------------------");
    }
    return text.toString();
  }

  public String toString() {
    if (m_node == null) {
      return "RandomModelTrees: No model built yet.";
    }
    StringBuffer text = new StringBuffer();
    text.append("RandomModelTrees: \n\n");
    /*
      // save space for now (maybe make an option ???)
    List<String> models = new ArrayList<String>();
    for(Node node: m_node) {
      node.toString(0,text,models,m_data);
      text.append("\n\n");
      text.append("-------------------------------------");
    }
    for(int i = 0; i < models.size(); i++) {
      text.append("LM" + i + ":\n" + models.get(i) + "\n");
    }
    */
    return text.toString();
  }

  public String getRevision() {
    return "$Revision$";
  }

  /**
   * Main method for testing this class.
   *
   * @param argv the options
   */
  public static void main(String [] argv) {
    runClassifier(new RandomModelTrees(), argv);
  }
}


// could remove m_subset ...
class Node implements Serializable {

  // doubles as prediction, if splitIndex == -1
  protected double splitValue = 0.0;
  protected int splitIndex = -1;
  protected double[] m_coeffs;
  protected int[] m_subset;
  protected Node less;
  protected Node more;
  protected double min;
  protected double max;


  public void turnIntoLeaf(List<Instance> data, double ridge) throws Exception {
    //turnIntoLeafLR(data,ridge);
    turnIntoLeafCORR(data,ridge);
  }

  public void turnIntoLeafLR(List<Instance> data, double ridge) throws Exception {
    splitIndex = -1;
    computeMinMax(data);
    LinearRegression l = new LinearRegression();
    l.setOptions(new String[]{"-C","-S","1","-R", ""+ridge});
    l.turnChecksOff();
    Instances trainData = new Instances(data.get(0).dataset(),data.size());
    for (Instance instance: data) {
      trainData.add(instance);
    }
    l.buildClassifier(trainData);
    // NEEDS PATCHING !!!

    double[] coeffs = l.coefficients();
    //if (getDebug()) System.out.println("coeffs " + coeffs.length + " " + Arrays.toString(coeffs));
    //if (getDebug()) System.out.println(m_Classifier);
    m_coeffs = new double[coeffs.length-1];
    int offset = 0;
    for(int i = 0; i < m_coeffs.length; i++) {
      if (i != trainData.classIndex()) {
	m_coeffs[offset++] = coeffs[i];
      }
    }
    // copy bias ...
    m_coeffs[offset] = coeffs[m_coeffs.length];
    m_subset = null;
  }


  public void turnIntoLeafCORR(List<Instance> data, double ridge) throws Exception {
    splitIndex = -1;
    computeMinMax(data);
    LinearRegression l = new LinearRegression();
    l.setOptions(new String[]{"-C","-S","1","-R", ""+ridge});
    l.turnChecksOff();
    Corr corr = new Corr();
    //corr.setDebug(true);
    corr.setClassifier(l);
    Instances trainData = new Instances(data.get(0).dataset(),data.size());
    for (Instance instance: data) {
      trainData.add(instance);
    }
    corr.buildClassifier(trainData);
    //lr = corr;
    m_subset = corr.getSubset();
    m_coeffs = corr.getCoeffs();
  }




  public Node(List<Instance> data, Random r, int level, double ridge, int trials, Comparator<Instance>[] comparators) throws Exception {
    if ((level <= 0) || (data.size() < 10)) {
      turnIntoLeaf(data,ridge);
      return;
    }

    //findRandomSplit(data,r,trials);
    findRandomSplitMedian(data,r,trials,comparators);
    //findRandomSplitALL(data,r,trials);

    if (splitIndex == -1) {
      // failed to find a split ...
      turnIntoLeaf(data,ridge);
      return;
    }
    List<Instance> subset = new ArrayList<Instance>(data.size());
    for (Instance instance: data) {
      if (instance.value(splitIndex) < splitValue) {
	subset.add(instance);
      }
    }
    if ((subset.size() == 0) || (subset.size() == data.size())) {
      // not a proper split ...
      turnIntoLeaf(data,ridge);
      return;
    }
    less = new Node(subset,r,level-1,ridge,trials,comparators);
    subset.clear();
    for (Instance instance: data) {
      if (instance.value(splitIndex) >= splitValue) {
	subset.add(instance);
      }
    }
    more = new Node(subset,r,level-1,ridge,trials,comparators);
  }


  public void computeMinMax(List<Instance> data) throws Exception {
    min = data.get(0).classValue();
    max = min;
    for (Instance instance: data) {
      double v = instance.classValue();
      if (v > max) max = v;
      if (v < min) min = v;
    }
  }


  public double classifyInstance(Instance instance) throws Exception {
    if (splitIndex == -1) {
      double v = leafPrediction(instance);
      if (Double.isNaN(v)) return 0.5*(min+max);
      if (v > max) return max;
      if (v < min) return min;
      return v;
    }
    if (instance.value(splitIndex) < splitValue) return less.classifyInstance(instance);
    return more.classifyInstance(instance);
  }


  public double leafPrediction(Instance instance) throws Exception {
    //if (m_coeffs != null) {
    if (m_subset != null) {
      double sum = m_coeffs[m_coeffs.length-1];
      for(int i = 0; i < m_subset.length; i++) {
	sum += m_coeffs[i] * instance.value(m_subset[i]);
      }
      return sum;
    } else {
      int offset = 0;
      double sum = 0.0;
      for(int i = 0; i < instance.numAttributes(); i++) {
	if (i != instance.classIndex()) {
	  sum += m_coeffs[offset++] * instance.value(i);
	}
      }
      sum += m_coeffs[offset];
      return sum;
    }
  }




  // printing ...

  public void prefix(int indent, StringBuffer sb) {
    for(int i = 0; i < indent; i++) sb.append("| ");
  }

  public void toString(int indent, StringBuffer sb, List<String> models, Instances header) {
    prefix(indent,sb);
    if (splitIndex == -1) {
      sb.append("LM" + models.size() + "\n");
      //models.add(lr.toString());
    } else {
      sb.append(header.attribute(splitIndex).name() + " < " + splitValue + "\n");
      less.toString(indent+1,sb,models,header);
      prefix(indent,sb);
      sb.append(header.attribute(splitIndex).name() + " > " + splitValue + "\n");
      more.toString(indent+1,sb,models,header);
    }
  }

  public void toString(int indent, StringBuffer sb, Instances header) {
    prefix(indent,sb);
    if (splitIndex == -1) {
      sb.append("target = " + splitValue + "\n");
    } else {
      sb.append(header.attribute(splitIndex).name() + " < " + splitValue + "\n");
      less.toString(indent+1,sb,header);
      prefix(indent,sb);
      sb.append(header.attribute(splitIndex).name() + " > " + splitValue + "\n");
      more.toString(indent+1,sb,header);
    }
  }


  //
  // maybe we should simply find the best split for some attribute ?
  //
  public void findRandomSplit(List<Instance> data, Random r, int numTrials) {
    int classIndex = data.get(0).classIndex();
    //System.out.println("loop pair sse");
    for(int pairs = 0; pairs < 10; pairs++) { // try ten pairs at random
      int n = data.size();
      int index1 = r.nextInt(n);
      int index2 = r.nextInt(n-1);
      if (index2 >= index1) index2++;
      Instance instance1 = data.get(index1);
      Instance instance2 = data.get(index2);
      n = instance1.numValues();
      int bestSplitIndex = -1;
      double bestSplitValue = 0.0;
      double minSSE = Double.MAX_VALUE;
      //System.out.println("loop attr sse");
      if (n > 0) {
	for(int trial = 0; trial < numTrials; trial++) {
	  int index = r.nextInt(n);
	  int attrIndex = instance1.index(index);
	  if (attrIndex != classIndex) {
	    double v1 = instance1.valueSparse(index);
	    double v2 = instance2.value(attrIndex);
	    if (v1 != v2) { // splitable, will be a numeric attr
	      double fraction = r.nextDouble();
	      splitIndex = attrIndex;
	      splitValue = fraction*v1 + (1.0-fraction)*v2;
	      double sse = splitSSE(data);
	      //System.out.println("sse " + splitIndex + " " + splitValue + " " + sse);
	      if (sse < minSSE) {
		minSSE = sse;
		bestSplitIndex = splitIndex;
		bestSplitValue = splitValue;
	      }
	    }
	  }
	}
      }
      if (bestSplitIndex > -1) {
	// found some useful split ...
	splitIndex = bestSplitIndex;
	splitValue = bestSplitValue;
	return;
      }
    }
  }


  // splitIndex and splitValue are set ...
  // is this correct ???
  public double splitSSE(List<Instance> data) {
    double wSum1 = 0.0;
    double wSum2 = 0.0;
    double mean1 = 0.0;
    double mean2 = 0.0;
    double sse1 = 0.0;
    double sse2 = 0.0;

    for (Instance instance: data) {
      double value = instance.classValue();
      double w = 1.0; // instance.weight(); weights are abused ...
      if (instance.value(splitIndex) < splitValue) {
	if (wSum1 > 0.0) {
	  wSum1 += w;
	  double oldMean = mean1;
	  mean1 += (value - oldMean) / wSum1;
	  sse1 += (value - oldMean) * (value - mean1);  // ??? w * ... ???
	} else {
	  mean1 = value;
	  wSum1 = w;
	}
      } else {
	if (wSum2 > 0.0) {
	  wSum2 += w;
	  double oldMean = mean2;
	  mean2 += (value - oldMean) / wSum2;
	  sse2 += (value - oldMean) * (value - mean2);  // ??? w * ... ???
	} else {
	  mean2 = value;
	  wSum2 = w;
	}
      }
    }

    return sse1 + sse2;
  }


  //======================================================================
  //
  // pinched from GPForest, uses median splits only
  //
  //======================================================================


  public void findRandomSplitMedian(List<Instance> train, Random r, int numTrials, Comparator<Instance>[] comparators) {

    // just to be safe ...
    splitIndex = -1;

    int classIndex = train.get(0).classIndex();
    int numAttributes = train.get(0).numAttributes();
    int bestSplitIndex = -1;
    double bestSplitValue = 0.0;
    double minSSE = Double.MAX_VALUE;
    //System.out.println("loop attr sse");
    for(int trial = 0; trial < numTrials; trial++) {
      int attrIndex = r.nextInt(numAttributes);
      if (attrIndex != classIndex) {
	splitIndex = attrIndex;
	Collections.sort(train,comparators[splitIndex]);
	double sse = sse(train);
	if (sse < minSSE) {
	  minSSE = sse;
	  bestSplitIndex = splitIndex;
	  bestSplitValue = splitValue;
	}
      }
    }

    if (bestSplitIndex > -1) {
      splitIndex = bestSplitIndex;
      splitValue = bestSplitValue;
    }
  }

  // train must be  sorted, will update splitValue
  //  only compute sse for median split, which guarantees progress

  public double sse(List<Instance> data) {

    int middle = data.size()/2;

    splitValue = 0.5 * (data.get(middle-1).value(splitIndex) + data.get(middle).value(splitIndex));

    double sse1 = sse(data, 0, middle);
    double sse2 = sse(data, middle, data.size());

    return sse1 + sse2;
  }


  public double sse(List<Instance> data, int from, int to) {
    double mean = mean(data,from,to);
    double sse = 0.0;
    for(int i = from; i < to; i++) {
      double v = data.get(i).classValue();
      double delta = mean - v;
      sse += delta * delta;
    }
    return sse;
  }


  public double mean(List<Instance> data, int from, int to) {
    double sum = 0.0;
    for(int i = from; i < to; i++) {
      double v = data.get(i).classValue();
      sum += v;
    }
    return sum / ( to - from);
  }




  //======================================================================
  //
  // this is pinched from GPForest as well, considers ALL splits
  //
  //======================================================================


  public void findRandomSplitALL(List<Instance> train, Random r, int numTrials, Comparator<Instance>[] comparators) {

    // just to be safe ...
    splitIndex = -1;

    int classIndex = train.get(0).classIndex();
    int numAttributes = train.get(0).numAttributes();
    int bestSplitIndex = -1;
    double bestSplitValue = 0.0;
    double minSSE = Double.MAX_VALUE;
    //System.out.println("loop attr sse");
    for(int trial = 0; trial < numTrials; trial++) {
      int attrIndex = r.nextInt(numAttributes);
      if (attrIndex != classIndex) {
	splitIndex = attrIndex;
	Collections.sort(train,comparators[splitIndex]);
	double sse = sseALL(train);
	if (sse < minSSE) {
	  minSSE = sse;
	  bestSplitIndex = splitIndex;
	  bestSplitValue = splitValue;
	}
      }
    }

    if (bestSplitIndex > -1) {
      splitIndex = bestSplitIndex;
      splitValue = bestSplitValue;
    }
  }


  // train is sorted, will update splitValue
  public double sseALL(List<Instance> data) {

    double wSum1 = 0.0;
    double wSum2 = 0.0;
    double mean1 = 0.0;
    double mean2 = 0.0;
    double sse1 = 0.0;
    double sse2 = 0.0;

    for (Instance instance: data) {
      double value = instance.classValue();
      double w = 1.0; // ???
      if (wSum2 > 0.0) {
	wSum2 += w;
	double oldMean = mean2;
	mean2 += (value - oldMean) / wSum2;
	sse2 += (value - oldMean) * (value - mean2);  // ??? w * ... ???
      } else {
	mean2 = value;
	wSum2 = w;
      }
    }

    double minSSE = sse1 + sse2;
    double bestSplitValue = Double.MAX_VALUE;

    for(int i = 0; i < data.size()-1; i++) {
      Instance instance = data.get(i);
      double value = instance.classValue();
      double w = 1.0; // instance.weight(); weights are abused ...
      if (wSum1 > 0.0) {
	wSum1 += w;
	double oldMean = mean1;
	mean1 += (value - oldMean) / wSum1;
	sse1 += (value - oldMean) * (value - mean1);  // ??? w * ... ???
      } else {
	mean1 = value;
	wSum1 = w;
      }
      if (wSum2 > w) {
	double oldMean = mean2;
	mean2 = ( mean2 * wSum2 - value) / (wSum2 - w);
	sse2 -= (value - mean2) / (value - oldMean);
	wSum2 -= w;
      } else {
	wSum2 = 0;
	mean2 = 0;
	sse2 = 0;
      }

      if (sse1 + sse2 < minSSE) {
	minSSE = sse1 + sse2;
	bestSplitValue = 0.5 * (instance.value(splitIndex) + data.get(i+1).value(splitIndex));
      }
    }

    splitValue = bestSplitValue;

    return minSSE;
  }

}


