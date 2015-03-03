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
 * DownSample.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */

package weka.filters.unsupervised.attribute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Vector;

import weka.core.Attribute;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.RevisionUtils;
import weka.core.Utils;
import weka.filters.SimpleStreamFilter;
import weka.filters.UnsupervisedFilter;

/**
 <!-- globalinfo-start -->
 * A simple filter that retains only every nth attribute.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre> -D
 *  Turns on output of debugging information.</pre>
 *
 * <pre> -nth &lt;int&gt;
 *  Only every n-th point will be output (&gt;0).
 *  (default: 1)</pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DownSample
  extends SimpleStreamFilter
  implements UnsupervisedFilter {

  /** for serialization. */
  private static final long serialVersionUID = 5944266872914953692L;

  /** the nth point. */
  protected int m_NthPoint = 1;

  /**
   * Returns a string describing this classifier.
   *
   * @return      a description of the classifier suitable for
   *              displaying in the explorer/experimenter gui
   */
  public String globalInfo() {
    return "A simple filter that retains only every nth attribute.";
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return 		an enumeration of all the available options.
   */
  public Enumeration listOptions() {
    Vector 	result;
    Enumeration	enm;

    result = new Vector();

    enm = super.listOptions();
    while (enm.hasMoreElements())
      result.add(enm.nextElement());

    result.addElement(new Option(
	"\tOnly every n-th point will be output (>0).\n"
	+ "\t(default: 1)",
	"nth", 1, "-nth <int>"));

    return result.elements();
  }

  /**
   * Parses a list of options for this object.
   * Also resets the state of the filter (this reset doesn't affect the
   * options).
   *
   * @param options 	the list of options as an array of strings
   * @throws Exception 	if an option is not supported
   * @see    		#reset()
   */
  public void setOptions(String[] options) throws Exception {
    String	tmpStr;

    reset();

    tmpStr = Utils.getOption("nth", options);
    if (tmpStr.length() > 0)
      setNthPoint(Integer.parseInt(tmpStr));
    else
      setNthPoint(1);

    super.setOptions(options);
  }

  /**
   * Gets the current settings of the filter.
   *
   * @return 		an array of strings suitable for passing to setOptions
   */
  public String[] getOptions() {
    Vector<String>	result;

    result = new Vector<String>(Arrays.asList(super.getOptions()));

    result.add("-nth");
    result.add("" + getNthPoint());

    return result.toArray(new String[result.size()]);
  }

  /**
   * Sets the nth point setting.
   *
   * @param value 	the nth point
   */
  public void setNthPoint(int value) {
    if (value > 0) {
      m_NthPoint = value;
      reset();
    }
    else {
      System.err.println(
	  "'n' must be larger than 0 (provided: " + value + ")!");
    }
  }

  /**
   * Returns the nth point setting.
   *
   * @return 		the nth point
   */
  public int getNthPoint() {
    return m_NthPoint;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String nthPointTipText() {
    return "Only every n-th point will be output (>0).";
  }

  /**
   * Returns the Capabilities of this filter. Derived filters have to
   * override this method to enable capabilities.
   *
   * @return            the capabilities of this object
   * @see               Capabilities
   */
  public Capabilities getCapabilities() {
    Capabilities 	result;

    result = new Capabilities(this);

    // attributes
    result.enableAllAttributes();
    result.enable(Capability.MISSING_VALUES);

    // classes
    result.enableAllClasses();
    result.enable(Capability.NO_CLASS);
    result.enable(Capability.MISSING_CLASS_VALUES);

    result.setMinimumNumberInstances(0);

    return result;
  }

  /**
   * Determines the output format based on the input format and returns
   * this. In case the output format cannot be returned immediately, i.e.,
   * hasImmediateOutputFormat() returns false, then this method will called
   * from batchFinished() after the call of preprocess(Instances), in which,
   * e.g., statistics for the actual processing step can be gathered.
   *
   * @param inputFormat     the input format to base the output format on
   * @return                the output format
   * @throws Exception      in case the determination goes wrong
   */
  protected Instances determineOutputFormat(Instances inputFormat) throws Exception {
    Instances			result;
    ArrayList<Attribute>	atts;
    int				i;
    int				count;
    boolean			hasClass;

    hasClass = (inputFormat.classIndex() > -1);

    // create new attributes
    atts  = new ArrayList<Attribute>();
    count = 0;
    for (i = 0; i < inputFormat.numAttributes(); i++) {
      if (i == inputFormat.classIndex())
	continue;
      count++;
      if (count % m_NthPoint == 0)
	atts.add((Attribute) inputFormat.attribute(i).copy());
    }

    // add class attribute (if present)
    if (hasClass)
      atts.add((Attribute) inputFormat.classAttribute().copy());

    // create new dataset
    result = new Instances(inputFormat.relationName(), atts, 0);
    if (hasClass)
      result.setClassIndex(result.numAttributes() - 1);

    return result;
  }

  /**
   * processes the given instance (may change the provided instance) and
   * returns the modified version.
   *
   * @param instance    the instance to process
   * @return            the modified data
   * @throws Exception  in case the processing goes wrong
   */
  protected Instance process(Instance instance) throws Exception {
    Instance	result;
    double[]	valuesOld;
    double[]	values;
    boolean	hasClass;
    int		count;
    int		i;
    int		n;

    hasClass = (instance.classIndex() > -1);

    valuesOld = instance.toDoubleArray();
    values    = new double[getOutputFormat().numAttributes()];

    count = 0;
    n     = 0;
    for (i = 0; i < valuesOld.length; i++) {
      if (i == instance.classIndex())
	continue;
      count++;
      if (count % m_NthPoint == 0) {
	values[n] = valuesOld[i];
	n++;
      }
    }

    // add class value
    if (hasClass)
      values[values.length - 1] = instance.classValue();

    // create instance
    result = new DenseInstance(instance.weight(), values);
    result.setDataset(getOutputFormat());

    return result;
  }

  /**
   * Returns the revision string.
   *
   * @return		the revision
   */
  public String getRevision() {
    return RevisionUtils.extract("$Revision$");
  }

  /**
   * Main method for testing this class.
   *
   * @param args 	should contain arguments to the filter: use -h for help
   */
  public static void main(String [] args) {
    runFilter(new DownSample(), args);
  }
}

