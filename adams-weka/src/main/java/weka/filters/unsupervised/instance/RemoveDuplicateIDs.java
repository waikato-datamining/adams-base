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
 * RemoveDuplicateIDs.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package weka.filters.unsupervised.instance;

import adams.data.weka.WekaAttributeIndex;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.RevisionUtils;
import weka.core.Utils;
import weka.filters.SimpleBatchFilter;
import weka.filters.UnsupervisedFilter;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Vector;

/**
 <!-- globalinfo-start -->
 * Removes rows with IDs that occur multiple times.<br/>
 * Also skips rows with missing ID.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre> -id &lt;1-based index or name&gt;
 *  The index/name of ID attribute to use for identifying duplicates.
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RemoveDuplicateIDs
  extends SimpleBatchFilter
  implements UnsupervisedFilter {

  /** for serialization. */
  private static final long serialVersionUID = -7024951985782351356L;

  /** the attribute with the IDs. */
  protected WekaAttributeIndex m_ID = new WekaAttributeIndex(WekaAttributeIndex.FIRST);

  /**
   * Returns a string describing this classifier.
   *
   * @return      a description of the classifier suitable for
   *              displaying in the explorer/experimenter gui
   */
  public String globalInfo() {
    return
      "Removes rows with IDs that occur multiple times.\n"
      + "Also skips rows with missing ID.";
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
      "\tThe index/name of ID attribute to use for identifying duplicates.\n",
      "id", 1, "-id <1-based index or name>"));

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

    tmpStr = Utils.getOption("id", options);
    if (tmpStr.isEmpty())
      setID(new WekaAttributeIndex(tmpStr));
    else
      setID(new WekaAttributeIndex(WekaAttributeIndex.FIRST));

    super.setOptions(options);
  }

  /**
   * Gets the current settings of the filter.
   *
   * @return an array of strings suitable for passing to setOptions
   */
  public String[] getOptions() {
    Vector<String>	result;

    result = new Vector<String>();

    result.add("-id");
    result.add("" + getID().getIndex());

    result.addAll(Arrays.asList(super.getOptions()));

    return result.toArray(new String[result.size()]);
  }

  /**
   * Sets the attribute name/index of attribute with IDs.
   *
   * @param value     the attribute name/index
   */
  public void setID(WekaAttributeIndex value) {
    m_ID = value;
  }

  /**
   * Returns the attribute name/index of attribute with IDs.
   *
   * @return		the attribute name/index
   */
  public WekaAttributeIndex getID() {
    return m_ID;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return    tip text for this property suitable for
   *            displaying in the explorer/experimenter gui
   */
  public String IDTipText() {
    return "The attribute name or index of the attribute with the IDs; " + m_ID.getExample();
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
    result.disable(Capability.RELATIONAL_ATTRIBUTES);
    result.disable(Capability.RELATIONAL_CLASS);
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
    m_ID.setData(inputFormat);
    if (m_ID.getIntIndex() == -1)
      throw new IllegalStateException("Attribute name/index not found: " + m_ID);
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
    Instances		result;
    int			i;
    HashSet<String>	duplicates;
    HashSet<String>	ids;
    String		id;
    int			index;
    boolean		numeric;

    result  = new Instances(instances, instances.numInstances());
    index   = m_ID.getIntIndex();
    numeric = instances.attribute(index).isNumeric();

    // identify duplicates
    ids        = new HashSet<>();
    duplicates = new HashSet<>();
    for (Instance inst: instances) {
      if (inst.isMissing(index))
	continue;
      if (numeric)
	id = "" + inst.value(index);
      else
	id = inst.stringValue(index);
      if (ids.contains(id))
	duplicates.add(id);
      ids.add(id);
    }

    if (m_Debug)
      System.out.println("Duplicate IDs: " + duplicates);

    // build dataset
    for (Instance inst: instances) {
      if (inst.isMissing(index))
	continue;
      if (numeric)
	id = "" + inst.value(index);
      else
	id = inst.stringValue(index);
      if (duplicates.contains(id))
	continue;
      result.add((Instance) inst.copy());
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
  public String getRevision() {
    return RevisionUtils.extract("$Revision$");
  }

  /**
   * Main method for running this filter.
   *
   * @param args should contain arguments to the filter: use -h for help
   */
  public static void main(String [] args) {
    runFilter(new RemoveDuplicateIDs(), args);
  }
}
