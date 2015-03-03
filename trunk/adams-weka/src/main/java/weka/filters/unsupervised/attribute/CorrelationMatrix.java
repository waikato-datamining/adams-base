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
 * CorrelationMatrix.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 *
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
import weka.core.Instances;
import weka.core.Option;
import weka.core.Range;
import weka.core.RevisionUtils;
import weka.core.Utils;
import weka.filters.SimpleBatchFilter;
import weka.filters.UnsupervisedFilter;
import adams.data.statistics.StatUtils;

/**
 <!-- globalinfo-start -->
 * Computes a matrix with the correlation coefficients between attributes.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre> -D
 *  Turns on output of debugging information.</pre>
 *
 * <pre> -R &lt;range specification&gt;
 *  The range of attributes to compute the matrix for.
 *  (default: first-last)</pre>
 *
 * <pre> -absolute
 *  If turned on, the absolute values of the correlation coefficients
 *  are returned.
 *  (default: off)</pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CorrelationMatrix
  extends SimpleBatchFilter
  implements UnsupervisedFilter {

  /** for serialization. */
  private static final long serialVersionUID = 2353556679864963099L;

  /** the range of attributes to work on. */
  protected Range m_AttributeRange = new Range("first-last");

  /** whether to return the absolute correlations. */
  protected boolean m_Absolute;

  /** the attribute indices to use. */
  protected Vector<Integer> m_Indices;

  /**
   * Returns a string describing this classifier.
   *
   * @return      a description of the classifier suitable for
   *              displaying in the explorer/experimenter gui
   */
  public String globalInfo() {
    return "Computes a matrix with the correlation coefficients between attributes.";
  }

  /**
   * Gets an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  public Enumeration listOptions() {
    Vector		result;
    Enumeration		enm;

    result = new Vector();

    enm = super.listOptions();
    while (enm.hasMoreElements())
      result.addElement(enm.nextElement());

    result.addElement(new Option(
	"\tThe range of attributes to compute the matrix for.\n"
	+ "\t(default: first-last)",
	"R", 1, "-R <range specification>"));

    result.addElement(new Option(
	"\tIf turned on, the absolute values of the correlation coefficients\n"
	+ "\tare returned.\n"
	+ "\t(default: off)",
	"absolute", 0, "-absolute"));

    return result.elements();
  }

  /**
   * returns the options of the current setup.
   *
   * @return      the current options
   */
  public String[] getOptions() {
    Vector<String>	result;

    result = new Vector<String>(Arrays.asList(super.getOptions()));

    result.add("-R");
    result.add("" + getAttributeRange());

    if (getAbsolute())
      result.add("-absolute");

    return (String[]) result.toArray(new String[result.size()]);
  }

  /**
   * Parses the options for this object. <p/>
   *
   <!-- options-start -->
   * Valid options are: <p/>
   *
   * <pre> -D
   *  Turns on output of debugging information.</pre>
   *
   * <pre> -R &lt;range specification&gt;
   *  The range of attributes to compute the matrix for.
   *  (default: first-last)</pre>
   *
   * <pre> -absolute
   *  If turned on, the absolute values of the correlation coefficients
   *  are returned.
   *  (default: off)</pre>
   *
   <!-- options-end -->
   *
   * @param options	the options to use
   * @throws Exception	if the option setting fails
   */
  public void setOptions(String[] options) throws Exception {
    String	tmpStr;

    super.setOptions(options);

    tmpStr = Utils.getOption("R", options);
    if (tmpStr.length() != 0)
      setAttributeRange(tmpStr);
    else
      setAttributeRange("first-last");

    setAbsolute(Utils.getFlag("absolute", options));
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String attributeRangeTipText() {
    return "The range of attributes to compute the matrix for.";
  }

  /**
   * Sets the range of attributes to compute the matrix for.
   *
   * @param value	the attribute range
   */
  public void setAttributeRange(String value) {
    m_AttributeRange.setRanges(value);
  }

  /**
   * Returns the range of attributes to compute the matrix for.
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
  public String absoluteTipText() {
    return "If set to true, the absolute values of the correlation coefficients are returned.";
  }

  /**
   * Sets whether to return the absolute values of the coefficients.
   *
   * @param value	if true then the absolute values are returned
   */
  public void setAbsolute(boolean value) {
    m_Absolute = value;
  }

  /**
   * Returns whether the absolute values of coefficients are returned.
   *
   * @return 		true if absolute values are returned
   */
  public boolean getAbsolute() {
    return m_Absolute;
  }

  /**
   * Determines the output format based on the input format and returns
   * this. In case the output format cannot be returned immediately, i.e.,
   * immediateOutputFormat() returns false, then this method will be called
   * from batchFinished().
   *
   * @param inputFormat     the input format to base the output format on
   * @return                the output format
   * @throws Exception      in case the determination goes wrong
   */
  protected Instances determineOutputFormat(Instances inputFormat) throws Exception {
    Instances			result;
    ArrayList<Attribute>	atts;
    ArrayList<String>		attVals;
    int				i;

    m_AttributeRange.setUpper(inputFormat.numAttributes() - 1);

    // determine indices of attributes to compute correlation for
    m_Indices = new Vector<Integer>();
    attVals   = new ArrayList<String>();
    for (i = 0; i < inputFormat.numAttributes(); i++) {
      if (!m_AttributeRange.isInRange(i))
	continue;
      if (!inputFormat.attribute(i).isNumeric())
	continue;
      m_Indices.add(i);
      attVals.add(inputFormat.attribute(i).name());
    }

    // create header
    atts = new ArrayList<Attribute>();
    atts.add(0, new Attribute("x", attVals));
    for (i = 0; i < attVals.size(); i++)
      atts.add(new Attribute(attVals.get(i)));

    result = new Instances("CorrelationMatrix: " + inputFormat.relationName(), atts, 0);

    return result;
  }

  /**
   * Returns the Capabilities of this filter.
   *
   * @return            the capabilities of this object
   * @see               Capabilities
   */
  public Capabilities getCapabilities() {
    Capabilities result = super.getCapabilities();
    result.disableAll();

    // attribute
    result.enable(Capability.NUMERIC_ATTRIBUTES);
    result.enable(Capability.DATE_ATTRIBUTES);

    // class
    result.enable(Capability.NOMINAL_CLASS);
    result.enable(Capability.NUMERIC_CLASS);
    result.enable(Capability.DATE_CLASS);
    result.enable(Capability.NO_CLASS);

    return result;
  }

  /**
   * Processes the given data (may change the provided dataset) and returns
   * the modified version. This method is called in batchFinished().
   *
   * @param instances   the data to process
   * @return            the modified data
   * @throws Exception  in case the processing goes wrong
   */
  protected Instances process(Instances instances) throws Exception {
    Instances	result;
    int		i;
    int		n;
    double[]	first;
    double[]	second;
    double	correlation;

    result = getOutputFormat();

    // initialize matrix
    for (i = 0; i < m_Indices.size(); i++) {
      first = new double[result.numAttributes()];
      // identifier of row
      first[0] = i;
      // diagonal is always one (attribute compared with itself)
      first[i + 1] = 1.0;
      result.add(new DenseInstance(1.0, first));
    }

    for (i = 0; i < m_Indices.size() - 1; i++) {
      first = instances.attributeToDoubleArray(m_Indices.get(i));
      for (n = i + 1; n < m_Indices.size(); n++) {
	second      = instances.attributeToDoubleArray(m_Indices.get(n));
	correlation = StatUtils.correlationCoefficient(first, second);
	if (m_Absolute)
	  correlation = Math.abs(correlation);
	result.instance(i).setValue(n + 1, correlation);
	result.instance(n).setValue(i + 1, correlation);
      }
    }

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
   * runs the filter with the given arguments.
   *
   * @param args      the commandline arguments
   */
  public static void main(String[] args) {
    runFilter(new CorrelationMatrix(), args);
  }
}
