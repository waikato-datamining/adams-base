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
 * SortOnAttribute.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package weka.filters.unsupervised.instance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Vector;

import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.Instances;
import weka.core.Option;
import weka.core.RevisionUtils;
import weka.core.SingleIndex;
import weka.core.Utils;
import weka.filters.SimpleBatchFilter;
import weka.filters.UnsupervisedFilter;

/**
 <!-- globalinfo-start -->
 * Sorts the instances on a particular attribute.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre> -C &lt;col&gt;
 *  The index of the attribute to sort on.
 *  (default: first).</pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SortOnAttribute
  extends SimpleBatchFilter
  implements UnsupervisedFilter {

  /** for serialization. */
  private static final long serialVersionUID = 4444860420131316510L;

  /** the index of the attribute to sort on. */
  protected SingleIndex m_AttributeIndex = new SingleIndex("first");

  /**
   * Returns a string describing this classifier.
   *
   * @return      a description of the classifier suitable for
   *              displaying in the explorer/experimenter gui
   */
  @Override
  public String globalInfo() {
    return "Sorts the instances on a particular attribute.";
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
	"\tThe index of the attribute to sort on.\n"
	+ "\t(default: first).",
	"C", 1, "-C <col>"));

    return result.elements();
  }

  /**
   * Parses a given list of options. <p/>
   *
   <!-- options-start -->
   * Valid options are: <p/>
   * 
   * <pre> -C &lt;col&gt;
   *  The index of the attribute to sort on.
   *  (default: first).</pre>
   * 
   <!-- options-end -->
   *
   * @param options the list of options as an array of string.s
   * @throws Exception if an option is not supported.
   */
  @Override
  public void setOptions(String[] options) throws Exception {
    String	tmpStr;

    tmpStr = Utils.getOption("C", options);
    if (tmpStr.length() > 0)
      setAttributeIndex(tmpStr);
    else
      setAttributeIndex("first");

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

    result = new ArrayList<String>(Arrays.asList(super.getOptions()));

    result.add("-C");
    result.add(getAttributeIndex());

    return result.toArray(new String[result.size()]);
  }

  /**
   * Sets the attribute index (1-based) of the attribute to sort on.
   *
   * @param value 	the index (1-based)
   */
  public void setAttributeIndex(String value) {
    m_AttributeIndex.setSingleIndex(value);
    reset();
  }

  /**
   * Returns the 1-based index of the attribute to sort on.
   *
   * @return 		the index (1-based)
   */
  public String getAttributeIndex() {
    return m_AttributeIndex.getSingleIndex();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String attributeIndexTipText() {
    return "The 1-based index of the attribute to sort on; 'first' and 'last' are accepted as well.";
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

    m_AttributeIndex.setUpper(instances.numAttributes() - 1);
    result = new Instances(instances);
    result.sort(m_AttributeIndex.getIndex());

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
    runFilter(new SortOnAttribute(), args);
  }
}
