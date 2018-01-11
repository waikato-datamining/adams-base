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
 * RemoveInstancesWithMissingValue.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package weka.filters.unsupervised.instance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.RevisionUtils;
import weka.core.Utils;
import weka.filters.SimpleBatchFilter;
import weka.filters.UnsupervisedFilter;

/**
 <!-- globalinfo-start -->
 * Removes all instances that contain missing values.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 * 
 * <pre> -D
 *  Turns on output of debugging information.</pre>
 * 
 * <pre> -ignore-class
 *  Whether to ignore the class attribute.
 *  (default: off)</pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RemoveInstancesWithMissingValue
  extends SimpleBatchFilter
  implements UnsupervisedFilter {

  /** for serialization. */
  private static final long serialVersionUID = -8611897473185237907L;

  /** whether to ignore the class attribute. */
  protected boolean m_IgnoreClass = false;

  /**
   * Returns a string describing this classifier.
   *
   * @return      a description of the classifier suitable for
   *              displaying in the explorer/experimenter gui
   */
  @Override
  public String globalInfo() {
    return "Removes all instances that contain missing values.";
  }

  /**
   * Gets an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  @Override
  public Enumeration listOptions() {
    Vector		result;
    Enumeration		enm;

    result = new Vector();

    enm = super.listOptions();
    while (enm.hasMoreElements())
      result.addElement(enm.nextElement());

    result.addElement(new Option(
	"\tWhether to ignore the class attribute.\n"
	+ "\t(default: off)",
	"ignore-class", 0, "-ignore-class"));

    return result.elements();
  }

  /**
   * returns the options of the current setup.
   *
   * @return      the current options
   */
  @Override
  public String[] getOptions() {
    List<String>	result;

    result = new ArrayList<String>(Arrays.asList(super.getOptions()));

    if (getIgnoreClass())
      result.add("-ignore-class");

    return result.toArray(new String[result.size()]);
  }

  /**
   * Parses the options for this object. <br><br>
   *
   <!-- options-start -->
   * Valid options are: <br><br>
   * 
   * <pre> -D
   *  Turns on output of debugging information.</pre>
   * 
   * <pre> -ignore-class
   *  Whether to ignore the class attribute.
   *  (default: off)</pre>
   * 
   <!-- options-end -->
   *
   * @param options	the options to use
   * @throws Exception	if the option setting fails
   */
  @Override
  public void setOptions(String[] options) throws Exception {
    super.setOptions(options);

    setIgnoreClass(Utils.getFlag("ignore-class", options));
  }

  /**
   * Sets whether to ignore the class.
   *
   * @param value 	true if to ignore the class
   */
  public void setIgnoreClass(boolean value) {
    m_IgnoreClass = value;
  }

  /**
   * Gets whether to ignore the class.
   *
   * @return 		true if to ignore the class
   */
  public boolean getIgnoreClass() {
    return m_IgnoreClass;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String ignoreClassTipText() {
    return "If true, the class attribute is not taken into account.";
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
    Instances	result;
    Instance	inst;
    int		i;
    int		n;
    boolean	missing;
    int		classIndex;

    result     = new Instances(instances, instances.numInstances());
    classIndex = instances.classIndex();

    // only copy instance objects with no missing values at all
    for (i = 0; i < instances.numInstances(); i++) {
      inst    = instances.instance(i);
      missing = false;
      for (n = 0; n < inst.numAttributes(); n++) {
	if (m_IgnoreClass && (n == classIndex))
	  continue;
	if (inst.isMissing(n)) {
	  missing = true;
	  break;
	}
      }
      if (!missing)
	result.add(new DenseInstance(inst));
      else if (m_Debug)
	System.out.println("Instance #" + (i+1) + " contains missing value(s).");
    }

    result.compactify();
    if (m_Debug)
      System.out.println("Reduction: " + instances.numInstances() + " -> " + result.numInstances());

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
   * Main method for testing this class.
   *
   * @param args should contain arguments to the filter: use -h for help
   */
  public static void main(String [] args) {
    runFilter(new RemoveInstancesWithMissingValue(), args);
  }
}
