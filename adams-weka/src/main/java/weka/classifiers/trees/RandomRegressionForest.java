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
 *    RandomRegressionForest.java
 *    Copyright (C) 2007 University of Waikato
 *
 */

package weka.classifiers.trees;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import weka.classifiers.RandomizableClassifier;
import weka.classifiers.functions.LinearRegression;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.SelectedTag;
import weka.core.Utils;
import weka.core.WeightedInstancesHandler;
import weka.filters.Filter;
import weka.filters.supervised.attribute.PLSFilter;

/**
 <!-- globalinfo-start -->
 * RandomRegressionForest: subtract mean and pls, then grow completely random trees (leaf: min .. 2min).<br>
 * plus local regression models (-S 1 -C), min &gt;&gt; numPLScomps
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 *
 * <pre> -N &lt;num&gt;
 *  Number of trees.
 *  (default 100)</pre>
 *
 * <pre> -M &lt;num&gt;
 *  Leaf threshold.
 *  (default 100)</pre>
 *
 * <pre> -C &lt;num&gt;
 *  Number of PLS components.
 *  (default 20)</pre>
 *
 * <pre> -S &lt;num&gt;
 *  Random number seed.
 *  (default 1)</pre>
 *
 * <pre> -D
 *  If set, classifier is run in debug mode and
 *  may output additional info to the console</pre>
 *
 <!-- options-end -->
 *
 * @author Bernhard Pfahringer (bernhard at cs dot waikato dot ac dot nz)
 * @version $Revision$
 */
public class RandomRegressionForest
  extends RandomizableClassifier
  implements WeightedInstancesHandler {

  /** for serialization */
  private static final long serialVersionUID = -3779643299723247400L;

  /** the generated nodes */
  protected Node[] m_Node;

  /** The number of iterations. */
  protected int m_NumIterations = 100;

  /** the number of components to use in PLS */
  protected int m_PLS = 20;

  /** the minimum number of instances in subsets */
  protected int m_Min = 100;

  /** the original header */
  protected Instances m_Data;

  /** the PLS filter used internally */
  protected PLSFilter m_PLSFilter = null;

  /** the mean */
  protected double m_Mean = 0.0;

  /**
   * Returns a string describing this classifier.
   *
   * @return      	a description of the classifier suitable for
   *              	displaying in the explorer/experimenter gui
   */
  public String globalInfo() {
    return
        "RandomRegressionForest: subtract mean and pls, then "
      + "grow completely random trees (leaf: min .. 2min).\n"
      + "plus local regression models (-S 1 -C), min >> numPLScomps";
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return 		an enumeration of all the available options.
   */
  public Enumeration listOptions() {
    Vector result = new Vector();

    result.addElement(new Option(
	"\tNumber of trees.\n"
	+ "\t(default 100)",
	"N", 1, "-N <num>"));

    result.addElement(new Option(
	"\tLeaf threshold.\n"
	+ "\t(default 100)",
	"M", 1, "-M <num>"));

    result.addElement(new Option(
	"\tNumber of PLS components.\n"
	+ "\t(default 20)",
	"C", 1, "-C <num>"));

    Enumeration enm = super.listOptions();
    while (enm.hasMoreElements())
      result.addElement(enm.nextElement());

    return result.elements();
  }

  /**
   * Parses a given list of options. <br><br>
   *
   <!-- options-start -->
   * Valid options are: <br><br>
   *
   * <pre> -N &lt;num&gt;
   *  Number of trees.
   *  (default 100)</pre>
   *
   * <pre> -M &lt;num&gt;
   *  Leaf threshold.
   *  (default 100)</pre>
   *
   * <pre> -C &lt;num&gt;
   *  Number of PLS components.
   *  (default 20)</pre>
   *
   * <pre> -S &lt;num&gt;
   *  Random number seed.
   *  (default 1)</pre>
   *
   * <pre> -D
   *  If set, classifier is run in debug mode and
   *  may output additional info to the console</pre>
   *
   <!-- options-end -->
   *
   * @param options 	the list of options as an array of strings
   * @throws Exception	if an option is not supported
   */
  public void setOptions(String[] options) throws Exception {
    String 	tmpStr;

    tmpStr = Utils.getOption('C', options);
    if (tmpStr.length() != 0) {
      setPLS(Integer.parseInt(tmpStr));
    } else {
      setPLS(20);
    }

    tmpStr = Utils.getOption('M', options);
    if (tmpStr.length() != 0) {
      setMin(Integer.parseInt(tmpStr));
    } else {
      setMin(100);
    }

    tmpStr = Utils.getOption('N', options);
    if (tmpStr.length() != 0) {
      setNumIterations(Integer.parseInt(tmpStr));
    } else {
      setNumIterations(100);
    }

    super.setOptions(options);
  }

  /**
   * Gets the current settings of the Classifier.
   *
   * @return 		an array of strings suitable for passing to setOptions
   */
  public String[] getOptions() {
    int       		i;
    Vector<String>	result;
    String[]		options;

    result = new Vector<String>();

    result.add("-C");
    result.add("" + getPLS());

    result.add("-M");
    result.add("" + getMin());

    result.add("-N");
    result.add("" + getNumIterations());

    options = super.getOptions();
    for (i = 0; i < options.length; i++)
      result.add(options[i]);

    return result.toArray(new String[result.size()]);
  }

  /**
   * Returns the tip text for this property
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String numIterationsTipText() {
    return "The number of iterations/trees.";
  }

  /**
   * Sets the number of iterations
   *
   * @param value	the number of iterations to use
   */
  public void setNumIterations(int value) {
    m_NumIterations = value;
  }

  /**
   * Gets the number of iterations
   *
   * @return 		the maximum number of iterations
   */
  public int getNumIterations() {
    return m_NumIterations;
  }

  /**
   * Returns the tip text for this property
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String minTipText() {
    return "The leaf threshold.";
  }

  /**
   * Sets the leaf threshold.
   *
   * @param value	the new leaf threshold
   */
  public void setMin(int value) {
    m_Min = value;
  }

  /**
   * Gets the current leaf threshold.
   *
   * @return		the current leaf threshold
   */
  public int getMin() {
    return m_Min;
  }

  /**
   * Returns the tip text for this property
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String PLSTipText() {
    return "The number of PLS components to generate.";
  }

  /**
   * Sets the number of PLS components to generate.
   *
   * @param value	the number of PLS components
   */
  public void setPLS(int value) {
    m_PLS = value;
  }

  /**
   * Gets the current number of PLS components to generate.
   *
   * @return		the current number of PLS components
   */
  public int getPLS() {
    return m_PLS;
  }

  /**
   * Centers the class value in the data.
   *
   * @param data	the data to work on
   * @return		the modified data
   */
  protected Instances centerClass(Instances data) {
    m_Mean = data.meanOrMode(data.classIndex());

    Instances newData = new Instances(data);
    for (int i = 0; i < newData.numInstances(); i++) {
      Instance instance = newData.instance(i);
      instance.setClassValue(instance.classValue() - m_Mean);
    }
    return newData;
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
    result.enable(Capability.DATE_CLASS);
    result.enable(Capability.MISSING_CLASS_VALUES);

    return result;
  }

  /**
   * builds the classifier
   *
   * @param data	the training data to be used for generating the
   * @throws Exception	if the classifier could not be built successfully
   */
  public void buildClassifier(Instances data) throws Exception {
    // can classifier handle the data?
    getCapabilities().testWithFail(data);

    // remove instances with missing class
    data = new Instances(data);
    data.deleteWithMissingClass();

    data = centerClass(data);

    // setup PLS filter
    m_PLSFilter = new PLSFilter();
    m_PLSFilter.setNumComponents(m_PLS);
    m_PLSFilter.setReplaceMissing(true);
    m_PLSFilter.setAlgorithm(new SelectedTag(PLSFilter.PREPROCESSING_CENTER, PLSFilter.TAGS_PREPROCESSING));
    m_PLSFilter.setAlgorithm(new SelectedTag(PLSFilter.ALGORITHM_SIMPLS, PLSFilter.TAGS_ALGORITHM));
    m_PLSFilter.setInputFormat(data);
    data = Filter.useFilter(data, m_PLSFilter);

    m_Data = data; // keep for printing ???

    m_Node = new Node[getNumIterations()];
    Random r = new Random(getSeed());

    for (int j = 0; j < m_Node.length; j++) {
      m_Node[j] = new Node(data,r,m_Min);
    }

    m_Data = new Instances(m_Data,0); // cleanOUT
  }

  /**
   * Calculates the class membership probabilities for the given test instance.
   *
   * @param instance 	the instance to be classified
   * @return preedicted	class probability distribution
   * @throws Exception 	if distribution can't be computed successfully
   */
  public double classifyInstance(Instance instance) throws Exception {
    m_PLSFilter.input(instance);
    m_PLSFilter.batchFinished();
    instance = m_PLSFilter.output();

    double sum = 0.0;
    for (Node node: m_Node) {
      sum += node.classifyInstance(instance);
    }

    return m_Mean + sum/m_Node.length;
  }

  /**
   * Returns description of the classifier.
   *
   * @return 		description of the classifier as a string
   */
  public String toString() {
    if (m_Node == null) {
      return "RandomRegressionForest: No model built yet.";
    }

    StringBuffer text = new StringBuffer();
    text.append("RandomRegressionForest: \n\n");
    List<String> models = new ArrayList<String>();
    for (Node node: m_Node) {
      node.toString(0,text,models);
      text.append("\n\n");
      text.append("-------------------------------------");
    }

    for (int i = 0; i < models.size(); i++) {
      text.append("LM" + i + ":\n" + models.get(i) + "\n");
    }

    return text.toString();
  }

  /**
   * Main method for testing this class.
   *
   * @param args 	the options
   */
  public static void main(String[] args) {
    runClassifier(new RandomRegressionForest(), args);
  }

  /**
   * TODO: description of class
   *
   * @author Bernhard Pfahringer (bernhard@cs.waikato.ac.nz)
   * @version $Revision$
   */
  public class Node
    implements Serializable {

    /** for serialization */
    private static final long serialVersionUID = -3856383120244210709L;

    protected double m_SplitValue;

    protected int m_SplitIndex = -1;

    protected LinearRegression m_LinearReg;

    protected Node m_Less;

    protected Node m_More;

    /**
     * the constructor
     *
     * @param data		the data to use
     * @param r			the random number generator to use
     * @param min		the leaf threshold
     * @throws Exception	if something goes wrong
     */
    public Node(Instances data, Random r, int min) throws Exception {
      if (data.numInstances() < 2*min) {
	turnIntoLeaf(data);
	return;
      }

      findRandomSplit(data, r, min);
      if (m_SplitIndex == -1) { // failed to find a split ...
	turnIntoLeaf(data);
	return;
      }

      Instances subset = new Instances(data,min);
      for (int i = 0; i < data.numInstances(); i++) {
	Instance instance = data.instance(i);
	if (instance.value(m_SplitIndex) < m_SplitValue) {
	  subset.add(instance);
	}
      }

      m_Less = new Node(subset, r, min);
      subset = new Instances(data, min);
      for (int i = 0; i < data.numInstances(); i++) {
	Instance instance = data.instance(i);
	if (instance.value(m_SplitIndex) >= m_SplitValue) {
	  subset.add(instance);
	}
      }

      m_More = new Node(subset, r, min);
    }

    /**
     * turns the node into a leaf
     *
     * @param data		the data to use for linear regression
     * @throws Exception	if training of LinearRegression fails
     */
    public void turnIntoLeaf(Instances data) throws Exception {
      m_LinearReg = new LinearRegression();
      m_LinearReg.setEliminateColinearAttributes(false);
      m_LinearReg.setAttributeSelectionMethod(
	  new SelectedTag(LinearRegression.SELECTION_NONE, LinearRegression.TAGS_SELECTION));
      m_LinearReg.turnChecksOff();
      m_LinearReg.setMinimal(true);
      m_LinearReg.buildClassifier(data);
    }

    /**
     * classifies the given instance
     *
     * @param instance		the instance to classify
     * @return			the regression value
     * @throws Exception	if the classification fails
     */
    public double classifyInstance(Instance instance) throws Exception {
      if (m_LinearReg != null)
	return m_LinearReg.classifyInstance(instance);
      else if (instance.value(m_SplitIndex) < m_SplitValue)
	return m_Less.classifyInstance(instance);
      else
	return m_More.classifyInstance(instance);
    }

    /**
     * determines a random split for the data, tries 10 pairs.
     *
     * @param data	the data to use
     * @param r		the random number generator for
     * @param min	the leaf threshold
     * @see 		#m_SplitIndex
     * @see 		#m_SplitValue
     */
    public void findRandomSplit(Instances data, Random r, int min) {
      int classIndex = data.classIndex();
      for (int pairs = 0; pairs < 10; pairs++) { // try ten pairs at random
	int n = data.numInstances();
	int index1 = r.nextInt(n);
	int index2 = r.nextInt(n-1);
	if (index2 >= index1)
	  index2++;
	Instance instance1 = data.instance(index1);
	Instance instance2 = data.instance(index2);
	n = instance1.numValues();
	if (n > 0) {
	  for (int retry = 0; retry < 10; retry++) { // try a few
	    index1 = r.nextInt(n);
	    int attrIndex = instance1.index(index1);
	    if (attrIndex != classIndex) {
	      double v1 = instance1.valueSparse(index1);
	      double v2 = instance2.value(attrIndex);
	      if (v1 != v2) { // splitable, will be a numeric attr (PLS output
		double fraction = r.nextDouble();
		m_SplitIndex = attrIndex;
		m_SplitValue = fraction*v1 + (1.0-fraction)*v2;
		if (subsetSizesOK(data,min)) return;
		m_SplitIndex = -1; // cannot split ...
	      }
	    }
	  }
	}
      }
    }

    /**
     * tests whether the leaf threshold is OK
     *
     * @param data	the data to use
     * @param min	the leaf threshold
     * @return		true if the size is OK
     */
    public boolean subsetSizesOK(Instances data, int min) {
      int smaller = 0;
      int larger = 0;
      for (int i = 0; i < data.numInstances(); i++) {
	if (data.instance(i).value(m_SplitIndex) < m_SplitValue) {
	  smaller++;
	} else {
	  larger++;
	}
      }
      return ((smaller >= min) && (larger >= min));
    }

    /**
     * generates the tree structure prefix
     *
     * @param indent	the depth
     * @param sb	the StringBuffer to add the prefix to
     */
    public void prefix(int indent, StringBuffer sb) {
      for (int i = 0; i < indent; i++)
	sb.append("| ");
    }

    /**
     * Generates a string representation of the node.
     *
     * @param indent	the depth
     * @param sb	the StringBuffer to add the output to
     * @param models	the list to add the generated output to
     */
    public void toString(int indent, StringBuffer sb, List<String> models) {
      prefix(indent, sb);
      if (m_SplitIndex == -1) {
	sb.append("LM" + models.size() + "\n");
	models.add(m_LinearReg.toString());
      } else {
	sb.append(m_Data.attribute(m_SplitIndex).name() + " < " + m_SplitValue + "\n");
	m_Less.toString(indent+1, sb, models);
	prefix(indent, sb);
	sb.append(m_Data.attribute(m_SplitIndex).name() + " > " + m_SplitValue + "\n");
	m_More.toString(indent+1, sb, models);
      }
    }
  }

  public String getRevision() {
    // TODO Auto-generated method stub
    return "1.0";
  }
}
