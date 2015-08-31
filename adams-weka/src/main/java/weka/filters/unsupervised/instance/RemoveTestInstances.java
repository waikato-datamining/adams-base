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
 * RemoveTestInstances.java
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
import weka.core.converters.AArffLoader;
import weka.core.converters.AbstractFileLoader;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.SimpleBatchFilter;
import weka.filters.UnsupervisedFilter;

import java.io.File;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Vector;

/**
 <!-- globalinfo-start -->
 * Removes all instances of the provided test set from the data passing through.<br/>
 * Requires an attribute in the data that uniquely identifies instances across datasets.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre> -test-set &lt;file&gt;
 *  The test set to load.
 * </pre>
 * 
 * <pre> -use-custom-loader
 *  Whether to use a custom loader.
 * </pre>
 * 
 * <pre> -custom-loader &lt;classname + options&gt;
 *  The custom loader to use.
 * </pre>
 * 
 * <pre> -id &lt;1-based index or name&gt;
 *  The index/name of ID attribute to use for identifying rows.
 * </pre>
 * 
 * <pre> -invert
 *  Whether to invert the matching (ie keep rather than remove).
 * </pre>
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
 * @version $Revision$
 */
public class RemoveTestInstances
  extends SimpleBatchFilter
  implements UnsupervisedFilter {

  /** for serialization. */
  private static final long serialVersionUID = -6784901276150528252L;

  /** the file containing the test set. */
  protected File m_TestSet = new File(".");

  /** whether to use a custom loader for the test set. */
  protected boolean m_UseCustomLoader = false;

  /** the file loader to use for loading the test set. */
  protected AbstractFileLoader m_CustomLoader = new AArffLoader();

  /** the attribute to use for identifying instances. */
  protected WekaAttributeIndex m_ID = new WekaAttributeIndex(WekaAttributeIndex.FIRST);

  /** whether to invert the matching. */
  protected boolean m_Invert = false;

  /**
   * Returns a string describing this classifier.
   *
   * @return      a description of the classifier suitable for
   *              displaying in the explorer/experimenter gui
   */
  public String globalInfo() {
    return
      "Removes all instances of the provided test set from the data passing through.\n"
      + "Requires an attribute in the data that uniquely identifies instances across datasets.";
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
	"\tThe test set to load.\n",
	"test-set", 1, "-test-set <file>"));

    result.addElement(new Option(
	"\tWhether to use a custom loader.\n",
	"use-custom-loader", 0, "-use-custom-loader"));

    result.addElement(new Option(
      "\tThe custom loader to use.\n",
      "custom-loader", 1, "-custom-loader <classname + options>"));

    result.addElement(new Option(
	"\tThe index/name of ID attribute to use for identifying rows.\n",
	"id", 1, "-id <1-based index or name>"));

    result.addElement(new Option(
	"\tWhether to invert the matching (ie keep rather than remove).\n",
	"invert", 0, "-invert"));

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
    String[]	tmpOptions;

    tmpStr = Utils.getOption("test-set", options);
    if (tmpStr.isEmpty())
      setTestSet(new File("."));
    else
      setTestSet(new File(tmpStr));

    setUseCustomLoader(Utils.getFlag("use-custom-loader", options));

    tmpStr = Utils.getOption("custom-loader", options);
    if (tmpStr.isEmpty()) {
      setCustomLoader(new AArffLoader());
    }
    else {
      tmpOptions    = Utils.splitOptions(tmpStr);
      tmpStr        = tmpOptions[0];
      tmpOptions[0] = "";
      setCustomLoader((AbstractFileLoader) Utils.forName(AbstractFileLoader.class, tmpStr, tmpOptions));
    }

    tmpStr = Utils.getOption("id", options);
    if (tmpStr.isEmpty())
      setID(new WekaAttributeIndex(tmpStr));
    else
      setID(new WekaAttributeIndex(WekaAttributeIndex.FIRST));

    setInvert(Utils.getFlag("invert", options));

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

    result.add("-test-set");
    result.add("" + getTestSet());

    if (getUseCustomLoader()) {
      result.add("-use-custom-loader");
      result.add("-custom-loader");
      result.add(Utils.toCommandLine(getCustomLoader()));
    }

    result.add("-id");
    result.add("" + getID().getIndex());

    if (getInvert())
      result.add("-invert");

    result.addAll(Arrays.asList(super.getOptions()));

    return result.toArray(new String[result.size()]);
  }

  /**
   * Sets the file containing the test set to remove from the data passing
   * through the filter.
   *
   * @param value     the file
   */
  public void setTestSet(File value) {
    m_TestSet = value;
  }

  /**
   * Returns the file containing the test set to remove from the data passing
   * through the filter.
   *
   * @return		the file
   */
  public File getTestSet() {
    return m_TestSet;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return    tip text for this property suitable for
   *            displaying in the explorer/experimenter gui
   */
  public String testSetTipText() {
    return "The file containing the test set to remove from the data passing through the filter.";
  }

  /**
   * Sets whether to use a custom loader or automatic loading.
   *
   * @param value     true if to use custom loader
   */
  public void setUseCustomLoader(boolean value) {
    m_UseCustomLoader = value;
  }

  /**
   * Returns whether to use a custom loader or automatic loading.
   *
   * @return		true if using custom loader
   */
  public boolean getUseCustomLoader() {
    return m_UseCustomLoader;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return    tip text for this property suitable for
   *            displaying in the explorer/experimenter gui
   */
  public String useCustomLoaderTipText() {
    return
      "If enabled, the specified custom loader is used for loading the test "
	+ "set rather than using automatic loading.";
  }

  /**
   * Sets the custom loader to use (if enabled).
   *
   * @param value     the custom loader
   */
  public void setCustomLoader(AbstractFileLoader value) {
    m_CustomLoader = value;
  }

  /**
   * Returns the custom loader to use (if enabled).
   *
   * @return		the custom loader
   */
  public AbstractFileLoader getCustomLoader() {
    return m_CustomLoader;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return    tip text for this property suitable for
   *            displaying in the explorer/experimenter gui
   */
  public String customLoaderTipText() {
    return "The custom loader to use (if enabled).";
  }

  /**
   * Sets the attribute name/index to use for identifying rows.
   *
   * @param value     the attribute name/index
   */
  public void setID(WekaAttributeIndex value) {
    m_ID = value;
  }

  /**
   * Returns the attribute name/index to use for identifying rows.
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
    return "The attribute name or index to use for identifying rows; " + m_ID.getExample();
  }

  /**
   * Sets whether to invert the matching sense (ie keep rather than remove).
   *
   * @param value     true if to invert
   */
  public void setInvert(boolean value) {
    m_Invert = value;
  }

  /**
   * Returns whether to invert the matching sense (ie keep rather than remove).
   *
   * @return		true if to invert
   */
  public boolean getInvert() {
    return m_Invert;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return    tip text for this property suitable for
   *            displaying in the explorer/experimenter gui
   */
  public String invertTipText() {
    return
      "If enabled, the matching sense gets inverted and the instances with "
	+ "the matching ID are kept rather than removed.";
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

    if (!m_TestSet.exists())
      throw new IllegalStateException("Test set does not exist: " + m_TestSet);
    if (m_TestSet.isDirectory())
      throw new IllegalStateException("Test set points to a directory: " + m_TestSet);

    return new Instances(inputFormat, 0);
  }

  protected Instances loadTestSet() throws Exception {
    Instances	result;

    if (getUseCustomLoader()) {
      m_CustomLoader.setFile(getTestSet());
      result = m_CustomLoader.getDataSet();
    }
    else {
      result = DataSource.read(getTestSet().getAbsolutePath());
    }

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
    Instances		result;
    Instances		test;
    HashSet<String> 	ids;
    int			index;
    boolean		numeric;
    boolean		exists;

    // only first batch will get processed
    if (m_FirstBatchDone)
      return new Instances(instances);

    test  = loadTestSet();
    m_ID.setData(test);
    index = m_ID.getIntIndex();
    if (index == -1)
      throw new IllegalStateException("ID attribute not found in test set: " + m_ID);
    numeric = test.attribute(index).isNumeric();
    ids     = new HashSet<String>();
    for (Instance inst: test) {
      if (numeric)
	ids.add("" + inst.value(index));
      else
	ids.add(inst.stringValue(index));
    }

    result = new Instances(instances, instances.numInstances());
    m_ID.setData(instances);
    index  = m_ID.getIntIndex();
    if (index == -1)
      throw new IllegalStateException("ID attribute not found in dataset: " + m_ID);

    for (Instance inst: instances) {
      if (numeric)
	exists = ids.contains("" + inst.value(index));
      else
	exists = ids.contains(inst.stringValue(index));
      if ((exists && !m_Invert) || (!exists && m_Invert))
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
   * Main method for testing this class.
   *
   * @param args should contain arguments to the filter: use -h for help
   */
  public static void main(String[] args) {
    runFilter(new RemoveTestInstances(), args);
  }
}
