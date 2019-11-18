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
 * KeepRange.java
 * Copyright (C) 2019 University of Waikato, Hamilton, New Zealand
 */

package weka.filters.unsupervised.instance;

import adams.core.UnorderedRange;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.RevisionUtils;
import weka.core.Utils;
import weka.filters.SimpleBatchFilter;
import weka.filters.UnsupervisedFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Keeps only the range of rows, in the order specified.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class KeepRange
  extends SimpleBatchFilter
  implements UnsupervisedFilter {

  /** for serialization. */
  private static final long serialVersionUID = 4444860420131316510L;

  /** the index of the attribute to sort on. */
  protected UnorderedRange m_RowRange = new UnorderedRange(UnorderedRange.ALL);

  /**
   * Returns a string describing this classifier.
   *
   * @return      a description of the classifier suitable for
   *              displaying in the explorer/experimenter gui
   */
  @Override
  public String globalInfo() {
    return "Keeps only the range of rows, in the order specified.";
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  @Override
  public Enumeration listOptions() {
    Vector 	result;

    result = new Vector();

    result.addElement(new Option(
	"\tThe unordered range of rows to keep, using 1-based indices.\n"
	+ "\t(default: " + UnorderedRange.ALL + ").",
	"R", 1, "-R <unordered_range>"));

    return result.elements();
  }

  /**
   * Parses a given list of options.
   *
   * @param options the list of options as an array of string.s
   * @throws Exception if an option is not supported.
   */
  @Override
  public void setOptions(String[] options) throws Exception {
    String	tmpStr;

    tmpStr = Utils.getOption("R", options);
    if (!tmpStr.isEmpty())
      setRowRange(tmpStr);
    else
      setRowRange(UnorderedRange.ALL);

    super.setOptions(options);
  }

  /**
   * Gets the current settings of the filter.
   *
   * @return an array of strings suitable for passing to setOptions.
   */
  @Override
  public String[] getOptions() {
    ArrayList<String>	result;

    result = new ArrayList<>(Arrays.asList(super.getOptions()));

    result.add("-R");
    result.add(getRowRange());

    return result.toArray(new String[result.size()]);
  }

  /**
   * Sets the unordered range of rows to keep.
   *
   * @param value 	the index (1-based)
   */
  public void setRowRange(String value) {
    if (UnorderedRange.isValid(value, -1)) {
      m_RowRange.setRange(value);
      reset();
    }
  }

  /**
   * Returns the unordered range of rows to keep.
   *
   * @return 		the range (1-based)
   */
  public String getRowRange() {
    return m_RowRange.getRange();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String rowRangeTipText() {
    return "The unordered range of rows to keep; " + m_RowRange.getExample();
  }

  /**
   * Returns the Capabilities of this filter.
   *
   * @return            the capabilities of this object
   * @see               Capabilities
   */
  @Override
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
  @Override
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
  @Override
  protected Instances process(Instances instances) throws Exception {
    Instances		result;
    int[]		indices;

    m_RowRange.setMax(instances.numInstances());
    indices = m_RowRange.getIntIndices();
    result  = new Instances(instances, indices.length);
    for (int index: indices)
      result.add((Instance) instances.instance(index).copy());

    return result;
  }

  /**
   * Returns the revision string.
   *
   * @return		the revision
   */
  @Override
  public String getRevision() {
    return RevisionUtils.extract("$Revision$");
  }

  /**
   * Main method for running this filter.
   *
   * @param args should contain arguments to the filter: use -h for help
   */
  public static void main(String [] args) {
    runFilter(new KeepRange(), args);
  }
}
