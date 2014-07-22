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
 * LogTransform.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package weka.filters.unsupervised.attribute;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Vector;

import weka.core.BinarySparseInstance;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.Range;
import weka.core.RevisionUtils;
import weka.core.SparseInstance;
import weka.core.Utils;
import weka.filters.SimpleStreamFilter;
import weka.filters.UnsupervisedFilter;

/**
 <!-- globalinfo-start -->
 * Transforms all numeric attributes in the specified range using a log-transform.<br/>
 * The class attribute is omitted.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre> -output-debug-info
 *  If set, filter is run in debug mode and
 *  may output additional info to the console</pre>
 * 
 * <pre> -do-not-check-capabilities
 *  If set, filter capabilities are not checked when input format is set
 *  (use with caution).</pre>
 * 
 * <pre> -R &lt;range specification&gt;
 *  The range of attributes to process.
 *  (default: first-last)</pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 4521 $
 */
public class LogTransform
  extends SimpleStreamFilter
  implements UnsupervisedFilter {

  /** for serialization. */
  private static final long serialVersionUID = -8798054963031276516L;
  
  /** the range of attributes to log-transform. */
  protected Range m_AttributeRange = new Range("first-last");

  /**
   * Returns a string describing this classifier.
   *
   * @return      a description of the classifier suitable for
   *              displaying in the explorer/experimenter gui
   */
  @Override
  public String globalInfo() {
    return 
	"Transforms all numeric attributes in the specified range using a "
	+ "log-transform.\n"
	+ "The class attribute is omitted.";
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return 		an enumeration of all the available options.
   */
  @Override
  public Enumeration listOptions() {
    Vector 	result;
    Enumeration	enm;

    result = new Vector();

    enm = super.listOptions();
    while (enm.hasMoreElements())
      result.add(enm.nextElement());

    result.addElement(new Option(
	"\tThe range of attributes to process.\n"
	+ "\t(default: first-last)",
	"R", 1, "-R <range specification>"));

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
  @Override
  public void setOptions(String[] options) throws Exception {
    String	tmpStr;

    reset();

    tmpStr = Utils.getOption("R", options);
    if (tmpStr.length() != 0)
      setAttributeRange(tmpStr);
    else
      setAttributeRange("first-last");

    super.setOptions(options);
  }

  /**
   * Gets the current settings of the filter.
   *
   * @return 		an array of strings suitable for passing to setOptions
   */
  @Override
  public String[] getOptions() {
    Vector<String>	result;

    result = new Vector<String>(Arrays.asList(super.getOptions()));

    result.add("-R");
    result.add("" + getAttributeRange());

    return result.toArray(new String[result.size()]);
  }

  /**
   * Sets the range of attributes to process.
   *
   * @param value	the attribute range
   */
  public void setAttributeRange(String value) {
    m_AttributeRange.setRanges(value);
  }

  /**
   * Returns the range of attributes to process.
   *
   * @return 		the attribute range
   */
  public String getAttributeRange() {
    return m_AttributeRange.getRanges();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String attributeRangeTipText() {
    return "The range of attributes to process.";
  }

  /**
   * Returns the Capabilities of this filter. Derived filters have to
   * override this method to enable capabilities.
   *
   * @return            the capabilities of this object
   * @see               Capabilities
   */
  @Override
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
  @Override
  protected Instances determineOutputFormat(Instances inputFormat) throws Exception {
    m_AttributeRange.setUpper(inputFormat.numAttributes() - 1);
    return new Instances(inputFormat, 0);
  }

  /**
   * processes the given instance (may change the provided instance) and
   * returns the modified version.
   *
   * @param instance    the instance to process
   * @return            the modified data
   * @throws Exception  in case the processing goes wrong
   */
  @Override
  protected Instance process(Instance instance) throws Exception {
    Instance	result;
    double[]	values;
    int		i;

    values = instance.toDoubleArray().clone();
    for (i = 0; i < values.length; i++) {
      if (instance.isMissing(i))
	continue;
      if (i == instance.classIndex())
	continue;
      if (!instance.attribute(i).isNumeric())
	continue;
      if (m_AttributeRange.isInRange(i))
	values[i] = Math.log(values[i]);
    }

    // create instance
    if (instance instanceof SparseInstance)
      result = new SparseInstance(instance.weight(), values);
    else if (instance instanceof BinarySparseInstance)
      result = new BinarySparseInstance(instance.weight(), values);
    else
      result = new DenseInstance(instance.weight(), values);
    result.setDataset(getOutputFormat());
    copyValues(result, false, instance.dataset(), getOutputFormat());

    return result;
  }

  /**
   * Returns the revision string.
   *
   * @return		the revision
   */
  @Override
  public String getRevision() {
    return RevisionUtils.extract("$Revision: 4521 $");
  }

  /**
   * Main method for testing this class.
   *
   * @param args 	should contain arguments to the filter: use -h for help
   */
  public static void main(String [] args) {
    runFilter(new LogTransform(), args);
  }
}
