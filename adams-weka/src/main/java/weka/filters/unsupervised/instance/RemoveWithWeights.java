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
 * RemoveWithWeights.java
 * Copyright (C) 2018 University of Waikato, Hamilton, New Zealand
 */

package weka.filters.unsupervised.instance;

import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.RevisionUtils;
import weka.core.Utils;
import weka.core.WeightedInstancesHandler;
import weka.filters.SimpleBatchFilter;
import weka.filters.UnsupervisedFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

/**
 <!-- globalinfo-start -->
 * Removes instances with weights outside the defined limits.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p>
 *
 * <pre> -drop-above &lt;0.0-1.0&gt;
 *  The threshold for the weight above which instances
 *  get dropped.
 *  default: 1.0</pre>
 *
 * <pre> -drop-below &lt;0.0-1.0&gt;
 *  The threshold for the weight below which instances
 *  get dropped.
 *  default: 0.0</pre>
 *
 * <pre> -output-debug-info
 *  If set, filter is run in debug mode and
 *  may output additional info to the console</pre>
 *
 * <pre> -do-not-check-capabilities
 *  If set, filter capabilities are not checked before filter is built
 *  (use with caution).</pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class RemoveWithWeights
  extends SimpleBatchFilter
  implements UnsupervisedFilter, WeightedInstancesHandler {

  /** for serialization. */
  private static final long serialVersionUID = -6784901276150528252L;

  /** the threshold of weight above which to drop instances. */
  protected double m_DropAbove = 1.0;

  /** the threshold of weight below which to drop instances. */
  protected double m_DropBelow = 0.0;

  /**
   * Returns a string describing this classifier.
   *
   * @return      a description of the classifier suitable for
   *              displaying in the explorer/experimenter gui
   */
  public String globalInfo() {
    return "Removes instances with weights outside the defined limits.";
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  public Enumeration listOptions() {
    Vector	result;
    Enumeration	enm;

    result = new Vector();

    result.addElement(new Option(
      "\tThe threshold for the weight above which instances\n"
	+ "\tget dropped.\n"
	+ "\tdefault: 1.0",
      "drop-above", 1, "-drop-above <0.0-1.0>"));

    result.addElement(new Option(
      "\tThe threshold for the weight below which instances\n"
	+ "\tget dropped.\n"
	+ "\tdefault: 0.0",
      "drop-below", 1, "-drop-below <0.0-1.0>"));

    enm = super.listOptions();
    while (enm.hasMoreElements())
      result.add(enm.nextElement());

    return result.elements();
  }

  /**
   * Parses a list of options for this object.
   *
   * @param options the list of options as an array of strings
   * @throws Exception if an option is not supported
   */
  public void setOptions(String[] options) throws Exception {
    String	tmpStr;

    tmpStr = Utils.getOption("drop-above", options);
    if (tmpStr.length() == 0)
      setDropAbove(0.0);
    else
      setDropAbove(Double.parseDouble(tmpStr));

    tmpStr = Utils.getOption("drop-below", options);
    if (tmpStr.length() == 0)
      setDropBelow(0.0);
    else
      setDropBelow(Double.parseDouble(tmpStr));

    super.setOptions(options);
  }

  /**
   * Gets the current settings of the filter.
   *
   * @return an array of strings suitable for passing to setOptions
   */
  public String[] getOptions() {
    List<String> 	result;

    result = new ArrayList<>();

    result.add("-drop-above");
    result.add("" + getDropAbove());

    result.add("-drop-below");
    result.add("" + getDropBelow());

    result.addAll(Arrays.asList(super.getOptions()));

    return result.toArray(new String[0]);
  }

  /**
   * Sets the threshold of the weights above which to drop instances.
   *
   * @param value     the threshold (0-1)
   */
  public void setDropAbove(double value) {
    if ((value >= 0) && (value <= 1))
      m_DropAbove = value;
    else
      System.err.println(
	  "'drop-above' threshold must be within [0;1], provided: " + value);
  }

  /**
   * Returns the threshold of the weights above which to drop instances.
   *
   * @return		the threshold (0-1)
   */
  public double getDropAbove() {
    return m_DropAbove;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return    tip text for this property suitable for
   *            displaying in the explorer/experimenter gui
   */
  public String dropAboveTipText() {
    return
        "The threshold of the weights above which to drop instances (0-1).";
  }

  /**
   * Sets the threshold of the weights below which to drop instances.
   *
   * @param value     the threshold (0-1)
   */
  public void setDropBelow(double value) {
    if ((value >= 0) && (value <= 1))
      m_DropBelow = value;
    else
      System.err.println(
	  "'drop-below' threshold must be within [0;1], provided: " + value);
  }

  /**
   * Returns the threshold of the weights below which to drop instances.
   *
   * @return		the threshold (0-1)
   */
  public double getDropBelow() {
    return m_DropBelow;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return    tip text for this property suitable for
   *            displaying in the explorer/experimenter gui
   */
  public String dropBelowTipText() {
    return
        "The threshold of the weights below which to drop instances (0-1).";
  }

  /**
   * Returns the Capabilities of this filter.
   *
   * @return            the capabilities of this object
   * @see               Capabilities
   */
  public Capabilities getCapabilities() {
    Capabilities 	result;

    result = new Capabilities(this);
    result.enableAll();
    result.enable(Capability.NO_CLASS);
    result.enable(Capability.MISSING_VALUES);
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

    // only first batch will get processed
    if (m_FirstBatchDone)
      return new Instances(instances);

    if (getDebug())
      System.err.println("Dataset size (before): " + instances.numInstances());

    result = new Instances(instances, instances.numInstances());
    for (i = 0; i < instances.numInstances(); i++) {
      if (instances.instance(i).weight() > m_DropAbove)
        continue;
      if (instances.instance(i).weight() < m_DropBelow)
        continue;
      result.add((Instance) instances.instance(i).copy());
    }

    result.compactify();
    if (getDebug())
      System.err.println("Dataset size (after): " + result.numInstances());

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
    runFilter(new RemoveWithWeights(), args);
  }
}
