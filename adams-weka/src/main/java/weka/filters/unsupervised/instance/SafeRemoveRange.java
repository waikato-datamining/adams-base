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
 *    SafeRemoveRange.java
 *    Copyright (C) 1999,2011 University of Waikato, Hamilton, New Zealand
 *
 */


package weka.filters.unsupervised.instance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Vector;

import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.RevisionUtils;
import weka.core.Utils;
import weka.filters.SimpleBatchFilter;
import weka.filters.UnsupervisedFilter;
import adams.core.Range;

/**
 <!-- globalinfo-start -->
 * A filter that removes a given range of instances of a dataset.<br>
 * Works just like weka.filters.unsupervised.instance.RemoveRange, but has a more robust handling of instance ranges. E.g., removal of 30-100 will not result in an error when presenting only 20 or 40 instances, but return no instance or instances 30-40 instead.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 *
 * <pre> -R &lt;inst1,inst2-inst4,...&gt;
 *  Specifies list of instances to select. First and last
 *  are valid indexes. (required)
 * </pre>
 *
 * <pre> -V
 *  Specifies if inverse of selection is to be output.
 * </pre>
 *
 <!-- options-end -->
 *
 * @author Eibe Frank (eibe@cs.waikato.ac.nz)
 * @version $Revision$
 */
public class SafeRemoveRange
  extends SimpleBatchFilter
  implements UnsupervisedFilter {

  /** for serialization */
  static final long serialVersionUID = -3064641215340828695L;

  /** Range of instances requested by the user. */
  protected Range m_Range = new Range("first-last");

  /** whether to invert the selection. */
  protected boolean m_InvertSelection;

  /**
   * Returns a string describing this filter
   *
   * @return a description of the filter suitable for
   * displaying in the GUI.
   */
  public String globalInfo() {
    return
        "A filter that removes a given range of instances of a dataset.\n"
      + "Works just like " + RemoveRange.class.getName() + ", but has a more "
      + "robust handling of instance ranges. E.g., removal of 30-100 will not "
      + "result in an error when presenting only 20 or 40 instances, but "
      + "return no instance or instances 30-40 instead.";
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  public Enumeration listOptions() {
    Vector 	result;

    result = new Vector();

    result.addElement(new Option(
              "\tSpecifies list of instances to select. First and last\n"
	      +"\tare valid indexes. (default: first-last)\n",
              "R", 1, "-R <inst1,inst2-inst4,...>"));

    result.addElement(new Option(
	      "\tSpecifies if inverse of selection is to be output.\n",
	      "V", 0, "-V"));

    return result.elements();
  }

  /**
   * Parses a given list of options. <br><br>
   *
   <!-- options-start -->
   * Valid options are: <br><br>
   *
   * <pre> -R &lt;inst1,inst2-inst4,...&gt;
   *  Specifies list of instances to select. First and last
   *  are valid indexes. (required)
   * </pre>
   *
   * <pre> -V
   *  Specifies if inverse of selection is to be output.
   * </pre>
   *
   <!-- options-end -->
   *
   * @param options the list of options as an array of string.s
   * @throws Exception if an option is not supported.
   */
  public void setOptions(String[] options) throws Exception {
    String	tmpStr;

    tmpStr = Utils.getOption('R', options);
    if (tmpStr.length() != 0)
      setInstancesIndices(tmpStr);
    else
      setInstancesIndices("first-last");

    setInvertSelection(Utils.getFlag('V', options));

    super.setOptions(options);
  }

  /**
   * Gets the current settings of the filter.
   *
   * @return an array of strings suitable for passing to setOptions.
   */
  public String[] getOptions() {
    ArrayList<String>	result;

    result = new ArrayList<String>(Arrays.asList(super.getOptions()));

    result.add("-R");
    result.add(getInstancesIndices());
    if (getInvertSelection())
      result.add("-V");

    return result.toArray(new String[result.size()]);
  }

  /**
   * Sets the range of instances to be selected.
   *
   * @param value 	a string representing the list of instances.
   * 			eg: first-3,5,6-last
   */
  public void setInstancesIndices(String value) {
    m_Range.setRange(value);
  }

  /**
   * Gets ranges of instances selected.
   *
   * @return 		the range
   */
  public String getInstancesIndices() {
    return m_Range.getRange();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String instancesIndicesTipText() {
    return "The range of instances to select; " + m_Range.getExample();
  }

  /**
   * Sets if selection is to be inverted.
   *
   * @param value 	true if inversion is to be performed
   */
  public void setInvertSelection(boolean value) {
    m_InvertSelection = value;
  }

  /**
   * Gets if selection is to be inverted.
   *
   * @return 		true if the selection is to be inverted
   */
  public boolean getInvertSelection() {
    return m_InvertSelection;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String invertSelectionTipText() {
    return "Whether to invert the selection.";
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

    // attributes
    result.enableAllAttributes();
    result.enable(Capability.MISSING_VALUES);

    // class
    result.enableAllClasses();
    result.enable(Capability.MISSING_CLASS_VALUES);
    result.enable(Capability.NO_CLASS);

    return result;
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
    return new Instances(inputFormat);
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
    Range	range;

    if (!isFirstBatchDone()) {
      range = new Range(m_Range.getRange());
      range.setInverted(m_InvertSelection);
      range.setMax(instances.numInstances());

      result = new Instances(getOutputFormat(), instances.size());
      for (i = 0; i < instances.numInstances(); i++) {
	if (!range.isInRange(i)) {
	  result.add((Instance) instances.instance(i));
	  copyValues(instances.instance(i), true, instances, result);
	}
      }

      result.compactify();
    }
    else {
      result = instances;
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
   * Main method for running this filter from command-line.
   *
   * @param args 	arguments to the filter: use -h for help
   */
  public static void main(String [] args) {
    runFilter(new SafeRemoveRange(), args);
  }
}
