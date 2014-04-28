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

/**
 * ClassificationViaPLS.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package weka.classifiers.functions;

import java.util.Enumeration;
import java.util.Vector;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.meta.ClassificationViaRegression;
import weka.classifiers.meta.ClassificationViaRegressionD;
import weka.core.Capabilities;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.PLSMatrixAccess;
import weka.core.SingleIndex;
import weka.core.Utils;
import weka.core.matrix.Matrix;
import weka.filters.Filter;
import adams.core.option.OptionUtils;

/**
 <!-- globalinfo-start -->
 * Performs ClassificationViaRegression using PLSClassifierWeightedWithLoadings as base classifier, allowing access to the PLS matrices.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre> -filter &lt;filter specification&gt;
 *  The PLS filter to use. Full classname of filter to include,  followed by scheme options.
 *  (default: weka.filters.supervised.attribute.PLSFilterWithLoadings)</pre>
 * 
 * <pre> -label-index &lt;num&gt;
 *  The label index (of the class attribute) to return the PLS matrices for.
 *  'first' and 'last' are accepted as well.
 *  (default: first)</pre>
 * 
 * <pre> -label-string &lt;label&gt;
 *  The label string (of the class attribute) to return the PLS matrices for.
 *  Overrides the '-label-index' option if non-empty.
 *  (default: '')</pre>
 * 
 * <pre> -D
 *  If set, classifier is run in debug mode and
 *  may output additional info to the console</pre>
 * 
 * <pre> 
 * Options specific to filter weka.filters.supervised.attribute.PLSFilterWithLoadings ('-filter'):
 * </pre>
 * 
 * <pre> -D
 *  Turns on output of debugging information.</pre>
 * 
 * <pre> -C &lt;num&gt;
 *  The number of components to compute.
 *  (default: 20)</pre>
 * 
 * <pre> -U
 *  Updates the class attribute as well.
 *  (default: off)</pre>
 * 
 * <pre> -M
 *  Turns replacing of missing values on.
 *  (default: off)</pre>
 * 
 * <pre> -A &lt;SIMPLS|PLS1&gt;
 *  The algorithm to use.
 *  (default: PLS1)</pre>
 * 
 * <pre> -P &lt;none|center|standardize&gt;
 *  The type of preprocessing that is applied to the data.
 *  (default: center)</pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ClassificationViaPLS
  extends AbstractClassifier
  implements PLSMatrixAccess {

  /** for serialization. */
  private static final long serialVersionUID = 8430850643799590721L;
  
  /** the header of the training set. */
  protected Instances m_Header;
  
  /** the {@link ClassificationViaRegression} used internally. */
  protected ClassificationViaRegressionD m_CVR;

  /** The label index to get the PLS matrices for. */
  protected SingleIndex m_LabelIndex = new SingleIndex("first");

  /** the label string to get the PLS matrices for (overrides the label index). */
  protected String m_LabelString = "";
  
  /**
   * Initializes the classifier.
   */
  public ClassificationViaPLS() {
    m_CVR = new ClassificationViaRegressionD();
    m_CVR.setClassifier(new PLSClassifierWeightedWithLoadings());
  }
  
  /**
   * Returns a string describing classifier
   *
   * @return a description suitable for displaying in the
   *         explorer/experimenter gui
   */
  public String globalInfo() {
    return 
	"Performs " + ClassificationViaRegression.class.getSimpleName() 
	+ " using " + PLSClassifierWeightedWithLoadings.class.getSimpleName()
	+ " as base classifier, allowing access to the PLS matrices.";
  }
  
  /**
   * Gets an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  @Override
  public Enumeration listOptions() {
    Vector        	result;
    Enumeration   	en;

    result = new Vector();

    result.addElement(new Option(
	"\tThe PLS filter to use. Full classname of filter to include, "
	+ "\tfollowed by scheme options.\n"
	+ "\t(default: " + ((PLSClassifierWeightedWithLoadings) m_CVR.getClassifier()).getDefaultFilter().getClass().getName() + ")",
	"filter", 1, "-filter <filter specification>"));

    result.addElement(new Option(
	"\tThe label index (of the class attribute) to return the PLS matrices for.\n"
	+ "\t'first' and 'last' are accepted as well.\n"
	+ "\t(default: first)",
	"label-index", 1, "-label-index <num>"));

    result.addElement(new Option(
	"\tThe label string (of the class attribute) to return the PLS matrices for.\n"
	+ "\tOverrides the '-label-index' option if non-empty.\n"
	+ "\t(default: '')",
	"label-string", 1, "-label-string <label>"));

    en = super.listOptions();
    while (en.hasMoreElements())
      result.addElement(en.nextElement());

    if (getFilter() instanceof OptionHandler) {
      result.addElement(new Option(
	  "",
	  "", 0, "\nOptions specific to filter "
	  + getFilter().getClass().getName() + " ('-filter'):"));

      en = ((OptionHandler) getFilter()).listOptions();
      while (en.hasMoreElements())
	result.addElement(en.nextElement());
    }

    return result.elements();
  }

  /**
   * returns the options of the current setup
   *
   * @return		the current options
   */
  @Override
  public String[] getOptions() {
    int       	i;
    Vector    	result;
    String[]  	options;

    result = new Vector();

    result.add("-filter");
    if (getFilter() instanceof OptionHandler)
      result.add(
  	    getFilter().getClass().getName()
	  + " "
	  + Utils.joinOptions(((OptionHandler) getFilter()).getOptions()));
    else
      result.add(
	  getFilter().getClass().getName());


    if (getLabelString().length() > 0) {
      result.add("-label-string");
      result.add("" + getLabelString());
    }
    else {
      result.add("-label-index");
      result.add("" + getLabelIndex());
    }

    options = super.getOptions();
    for (i = 0; i < options.length; i++)
      result.add(options[i]);

    return (String[]) result.toArray(new String[result.size()]);
  }

  /**
   * Parses the options for this object. <p/>
   *
   <!-- options-start -->
   * Valid options are: <p/>
   * 
   * <pre> -filter &lt;filter specification&gt;
   *  The PLS filter to use. Full classname of filter to include,  followed by scheme options.
   *  (default: weka.filters.supervised.attribute.PLSFilterWithLoadings)</pre>
   * 
   * <pre> -label-index &lt;num&gt;
   *  The label index (of the class attribute) to return the PLS matrices for.
   *  'first' and 'last' are accepted as well.
   *  (default: first)</pre>
   * 
   * <pre> -label-string &lt;label&gt;
   *  The label string (of the class attribute) to return the PLS matrices for.
   *  Overrides the '-label-index' option if non-empty.
   *  (default: '')</pre>
   * 
   * <pre> -D
   *  If set, classifier is run in debug mode and
   *  may output additional info to the console</pre>
   * 
   * <pre> 
   * Options specific to filter weka.filters.supervised.attribute.PLSFilterWithLoadings ('-filter'):
   * </pre>
   * 
   * <pre> -D
   *  Turns on output of debugging information.</pre>
   * 
   * <pre> -C &lt;num&gt;
   *  The number of components to compute.
   *  (default: 20)</pre>
   * 
   * <pre> -U
   *  Updates the class attribute as well.
   *  (default: off)</pre>
   * 
   * <pre> -M
   *  Turns replacing of missing values on.
   *  (default: off)</pre>
   * 
   * <pre> -A &lt;SIMPLS|PLS1&gt;
   *  The algorithm to use.
   *  (default: PLS1)</pre>
   * 
   * <pre> -P &lt;none|center|standardize&gt;
   *  The type of preprocessing that is applied to the data.
   *  (default: center)</pre>
   * 
   <!-- options-end -->
   *
   * @param options	the options to use
   * @throws Exception	if setting of options fails
   */
  @Override
  public void setOptions(String[] options) throws Exception {
    String	tmpStr;
    String[]	tmpOptions;

    super.setOptions(options);

    tmpStr     = Utils.getOption("filter", options);
    tmpOptions = Utils.splitOptions(tmpStr);
    if (tmpOptions.length != 0) {
      tmpStr        = tmpOptions[0];
      tmpOptions[0] = "";
      setFilter((Filter) OptionUtils.forName(Filter.class, tmpStr, tmpOptions));
    }
    else {
      setFilter(((PLSClassifierWeightedWithLoadings) m_CVR.getClassifier()).getDefaultFilter());
    }

    tmpStr = Utils.getOption("label-index", options);
    if (tmpStr.length() != 0)
      setLabelIndex(tmpStr);
    else
      setLabelIndex("first");

    tmpStr = Utils.getOption("label-string", options);
    if (tmpStr.length() != 0)
      setLabelString(tmpStr);
    else
      setLabelString("");
  }

  /**
   * Set the PLS filter (only used for setup).
   *
   * @param value	the kernel filter.
   * @throws Exception	if not PLSFilter
   */
  public void setFilter(Filter value) throws Exception {
    ((PLSClassifierWeightedWithLoadings) m_CVR.getClassifier()).setFilter(value);
  }

  /**
   * Get the PLS filter.
   *
   * @return 		the PLS filter
   */
  public Filter getFilter() {
    return ((PLSClassifierWeightedWithLoadings) m_CVR.getClassifier()).getFilter();
  }

  /**
   * Returns the tip text for this property
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String filterTipText() {
    return ((PLSClassifierWeightedWithLoadings) m_CVR.getClassifier()).filterTipText();
  }

  /**
   * Set the label index to get the PLS matrices for.
   *
   * @param value 	the label index of the class attribute
   */
  public void setLabelIndex(String value) {
    m_LabelIndex.setSingleIndex(value);
  }

  /**
   * Get the label index of the class attribute to get the PLS matrices for.
   *
   * @return 		the label index of the class attribute
   */
  public String getLabelIndex() {
    return m_LabelIndex.getSingleIndex();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String labelIndexTipText() {
    return "The index of the class attribute's label (1-based) to get the PLS matrices for; 'first' and 'last' are accepted as well.";
  }

  /**
   * Set the class attribute's label to get the PLS matrices for.
   * Overrides {@link #m_LabelIndex} if non-empty.
   *
   * @param value 	the label string of the class attribute
   */
  public void setLabelString(String value) {
    m_LabelString = value;
  }

  /**
   * Get the class attribute's label to get the PLS matrices for.
   * Overrides {@link #m_LabelIndex} if non-empty.
   *
   * @return 		the label string of the class attribute
   */
  public String getLabelString() {
    return m_LabelString;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String labelStringTipText() {
    return "The class attribute's label to get the PLS matrices for; 'first' and 'last' are accepted as well.";
  }

  /**
   * Returns the Capabilities of this classifier. Maximally permissive
   * capabilities are allowed by default. Derived classifiers should
   * override this method and first disable all capabilities and then
   * enable just those capabilities that make sense for the scheme.
   *
   * @return            the capabilities of this object
   * @see               Capabilities
   */
  @Override
  public Capabilities getCapabilities() {
    return m_CVR.getCapabilities();
  }

  /**
   * Generates a classifier.
   *
   * @param data 	set of instances serving as training data
   * @throws Exception 	if the classifier has not been generated successfully
   */
  @Override
  public void buildClassifier(Instances data) throws Exception {
    m_Header = null;
    m_CVR.buildClassifier(data);
    m_Header = new Instances(data, 0);
  }

  /**
   * Classifies the given test instance. The instance has to belong to a
   * dataset when it's being classified.
   *
   * @param instance 	the instance to be classified
   * @return 		the predicted most likely class for the instance or
   * 			Instance.missingValue() if no prediction is made
   * @throws Exception 	if an error occurred during the prediction
   */
  @Override
  public double classifyInstance(Instance instance) throws Exception {
    return m_CVR.classifyInstance(instance);
  }

  /**
   * Predicts the class memberships for a given instance. If
   * an instance is unclassified, the returned array elements
   * must be all zero. If the class is numeric, the array
   * must consist of only one element, which contains the
   * predicted value.
   *
   * @param instance 	the instance to be classified
   * @return 		an array containing the estimated membership
   * 			probabilities of the test instance in each class
   * 			or the numeric prediction
   * @throws Exception 	if distribution could not be computed successfully
   */
  @Override
  public double[] distributionForInstance(Instance instance) throws Exception {
    return m_CVR.distributionForInstance(instance);
  }

  /**
   * Returns the specified base classifier. Either using the label index or
   * the class label string to identify the classifier.
   * 
   * @return		the specified classifier, the default classifier if failed to locate
   */
  protected PLSClassifierWeightedWithLoadings getSelectedClassifier() {
    PLSClassifierWeightedWithLoadings	result;
    
    result = null;
    
    if (m_Header != null) {
      if (m_LabelString.length() > 0) {
	if (m_Header.classAttribute().indexOfValue(m_LabelString) > -1)
	  result = (PLSClassifierWeightedWithLoadings) m_CVR.getClassifier(m_Header.classAttribute().indexOfValue(m_LabelString));
	else
	  System.err.println(getClass().getName() + ": Failed to locate class label '" + m_LabelString + "'!");
      }
      else {
	m_LabelIndex.setUpper(m_Header.classAttribute().numValues() - 1);
	result = (PLSClassifierWeightedWithLoadings) m_CVR.getClassifier(m_LabelIndex.getIndex());
      }
    }
    else {
      System.err.println(getClass().getName() + ": classifier not built? Cannot retrieve base classifier!");
    }

    // dummy in case something goes wrong
    if (result == null)
      result = (PLSClassifierWeightedWithLoadings) m_CVR.getClassifier();
      
    return result;
  }
  
  /**
   * Returns the reg vector.
   * 
   * @return		the vector
   */
  public Matrix getPLS1RegVector() {
    return getSelectedClassifier().getPLS1RegVector();
  }

  /**
   * Returns the PLS1 P matrix.
   * 
   * @return		the matrix
   */
  public Matrix getPLS1P() {
    return getSelectedClassifier().getPLS1P();
  }
  
  /**
   * Returns the PLS1 W matrix.
   * 
   * @return		the matrix
   */
  public Matrix getPLS1W() {
    return getSelectedClassifier().getPLS1W();
  }
  
  /**
   * Returns the PLS1 b "hat" matrix.
   * 
   * @return		the matrix
   */
  public Matrix getPLS1bHat() {
    return getSelectedClassifier().getPLS1bHat();
  }
  
  /**
   * Returns the SIMPLS W matrix.
   * 
   * @return		the matrix
   */
  public Matrix getSimplsW() {
    return getSelectedClassifier().getSimplsW();
  }
  
  /**
   * Returns the SIMPLS B matrix.
   * 
   * @return		the matrix
   */
  public Matrix getSimplsB() {
    return getSelectedClassifier().getSimplsB();
  }

  /**
   * Returns the revision string.
   *
   * @return            the revision
   */
  @Override
  public String getRevision() {
    return "$Revision$";
  }
  
  /**
   * Returns a string representation of the built model.
   * 
   * @return		the model string
   */
  @Override
  public String toString() {
    return m_CVR.toString();
  }

  /**
   * Main method for running this class.
   *
   * @param args	the commandline parameters
   */
  public static void main(String[] args) {
    runClassifier(new ClassificationViaPLS(), args);
  }
}
