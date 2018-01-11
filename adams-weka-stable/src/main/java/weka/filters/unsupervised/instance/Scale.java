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
 * Scale.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package weka.filters.unsupervised.instance;

import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.RevisionUtils;
import weka.core.Utils;
import weka.filters.SimpleStreamFilter;
import weka.filters.UnsupervisedFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Vector;

/**
 <!-- globalinfo-start -->
 * Scales all numeric attributes between the specified min/max.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 * 
 * <pre> -min &lt;double&gt;
 *  Specifies the minimum that the values should have. (default: 0.0)
 * </pre>
 * 
 * <pre> -max &lt;double&gt;
 *  Specifies the maximum that the values should have. (default: 1.0)
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Scale
  extends SimpleStreamFilter
  implements UnsupervisedFilter {

  /** for serialization. */
  private static final long serialVersionUID = 6812351429964183179L;

  /** the minimum to use. */
  protected double m_Min = 0.0;

  /** the maximum to use. */
  protected double m_Max = 1.0;

  /**
   * Returns a string describing this filter.
   *
   * @return      a description of the filter suitable for
   *              displaying in the explorer/experimenter gui
   */
  public String globalInfo() {
    return "Scales all numeric attributes between the specified min/max.";
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  public Enumeration listOptions() {
    Vector result;

    result = new Vector();

    result.addElement(new Option(
              "\tSpecifies the minimum that the values should have. (default: 0.0)\n",
              "min", 1, "-min <double>"));

    result.addElement(new Option(
              "\tSpecifies the maximum that the values should have. (default: 1.0)\n",
              "max", 1, "-max <double>"));

    return result.elements();
  }

  /**
   * Parses a given list of options. <br><br>
   *
   <!-- options-start -->
   * Valid options are: <br><br>
   * 
   * <pre> -min &lt;double&gt;
   *  Specifies the minimum that the values should have. (default: 0.0)
   * </pre>
   * 
   * <pre> -max &lt;double&gt;
   *  Specifies the maximum that the values should have. (default: 1.0)
   * </pre>
   * 
   <!-- options-end -->
   *
   * @param options the list of options as an array of string.s
   * @throws Exception if an option is not supported.
   */
  public void setOptions(String[] options) throws Exception {
    String	tmpStr;

    tmpStr = Utils.getOption("min", options);
    if (tmpStr.length() != 0)
      setMin(Double.parseDouble(tmpStr));
    else
      setMin(0.0);

    tmpStr = Utils.getOption("max", options);
    if (tmpStr.length() != 0)
      setMax(Double.parseDouble(tmpStr));
    else
      setMax(1.0);

    super.setOptions(options);
  }

  /**
   * Gets the current settings of the filter.
   *
   * @return an array of strings suitable for passing to setOptions.
   */
  public String[] getOptions() {
    ArrayList<String> result;

    result = new ArrayList<String>(Arrays.asList(super.getOptions()));

    result.add("-min");
    result.add("" + getMin());
    result.add("-max");
    result.add("" + getMax());

    return result.toArray(new String[result.size()]);
  }

  /**
   * Sets the minimum for the values.
   *
   * @param value 	the minimum
   */
  public void setMin(double value) {
    m_Min = value;
  }

  /**
   * Returns the minimum for the values.
   *
   * @return 		the minimum
   */
  public double getMin() {
    return m_Min;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String minTipText() {
    return "The minimum for the values.";
  }

  /**
   * Sets the maximum for the values.
   *
   * @param value 	the maximum
   */
  public void setMax(double value) {
    m_Max = value;
  }

  /**
   * Returns the maximum for the values.
   *
   * @return 		the maximum
   */
  public double getMax() {
    return m_Max;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displayaxg ax the explorer/experimenter gui
   */
  public String maxTipText() {
    return "The maximum for the values.";
  }

  /**
   * Returns the Capabilities of this filter.
   *
   * @return            the capabilities of this object
   * @see               weka.core.Capabilities
   */
  public Capabilities getCapabilities() {
    Capabilities 	result;

    result = new Capabilities(this);

    result.enable(Capability.NUMERIC_ATTRIBUTES);

    result.enableAllClasses();
    result.enable(Capability.NO_CLASS);
    result.enable(Capability.MISSING_CLASS_VALUES);

    result.setMinimumNumberInstances(0);

    return result;
  }

  /**
   * Determines the output format based on the input format and returns
   * this.
   *
   * @param inputFormat     the input format to base the output format on
   * @return                the output format
   * @throws Exception      in case the determination goes wrong
   */
  protected Instances determineOutputFormat(Instances inputFormat) throws Exception {
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
  protected Instance process(Instance instance) throws Exception {
    Instance	result;
    double[]	values;
    int		i;
    double 	min;
    double 	max;

    values = instance.toDoubleArray();

    // find range
    min = Double.MAX_VALUE;
    max = Double.MIN_VALUE;
    for (i = 0; i < values.length; i++) {
      if (i == instance.classIndex())
	continue;
      if (values[i] < min)
	min = values[i];
      if (values[i] > max)
	max = values[i];
    }

    // adjust values
    for (i = 0; i < values.length; i++) {
      if (i == instance.classIndex())
	continue;
      if (max == min)
	values[i] = Utils.missingValue();
      else
	values[i] = m_Min + ((values[i] - min) / (max - min) * (m_Max - m_Min));
    }

    result = (Instance) instance.copy();
    for (i = 0; i < values.length; i++) {
      if (i == instance.classIndex())
	continue;
      result.setValue(i, values[i]);
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
   * Main method for testing this class.
   *
   * @param args should contain arguments to the filter: use -h for help
   */
  public static void main(String [] args) {
    runFilter(new Scale(), args);
  }
}

