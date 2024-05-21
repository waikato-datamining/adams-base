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
 * AlignDataset.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package weka.filters.unsupervised.instance;

import adams.core.base.BaseRegExp;
import adams.core.option.OptionUtils;
import adams.data.weka.WekaAttributeIndex;
import adams.data.weka.WekaAttributeRange;
import weka.core.Attribute;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.RevisionUtils;
import weka.core.Utils;
import weka.core.converters.AArffLoader;
import weka.core.converters.AbstractFileLoader;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.converters.SimpleArffLoader;
import weka.filters.Filter;
import weka.filters.MultiFilter;
import weka.filters.SimpleBatchFilter;
import weka.filters.unsupervised.attribute.AnyToString;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

/**
 <!-- globalinfo-start -->
 * Aligns the dataset(s) passing through to the reference dataset.<br>
 * Makes use of the following other filters internally:<br>
 * - weka.filters.unsupervised.attribute.AnyToString<br>
 * - weka.filters.unsupervised.instance.RemoveWithLabels
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p>
 *
 * <pre> -reference-dataset &lt;file&gt;
 *  The reference dataset to load.
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
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class AlignDataset
  extends SimpleBatchFilter {

  private static final long serialVersionUID = -3311037407238263617L;

  /** the file containing the reference dataset. */
  protected File m_ReferenceDataset = new File(".");

  /** whether to use a custom loader for the reference data. */
  protected boolean m_UseCustomLoader = false;

  /** the file loader to use for loading the reference data. */
  protected AbstractFileLoader m_CustomLoader = new SimpleArffLoader();

  /** the supplied test set, when using programmatically. */
  protected Instances m_SuppliedReferenceDataset;

  /** the actual reference dataset in use. */
  protected transient Instances m_ActualReferenceDataset;

  /**
   * Returns a string describing this filter.
   *
   * @return a description of the filter suitable for displaying in the
   * explorer/experimenter gui
   */
  @Override
  public String globalInfo() {
    return "Aligns the dataset(s) passing through to the reference dataset.\n"
	     + "Makes use of the following other filters internally:\n"
	     + "- " + adams.core.Utils.classToString(AnyToString.class) + "\n"
	     + "- " + adams.core.Utils.classToString(RemoveWithLabels.class);
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  public Enumeration listOptions() {
    Vector result;
    Enumeration	enm;

    result = new Vector();

    result.addElement(new Option(
      "\tThe reference dataset to load.\n",
      "reference-dataset", 1, "-reference-dataset <file>"));

    result.addElement(new Option(
      "\tWhether to use a custom loader.\n",
      "use-custom-loader", 0, "-use-custom-loader"));

    result.addElement(new Option(
      "\tThe custom loader to use.\n",
      "custom-loader", 1, "-custom-loader <classname + options>"));

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

    tmpStr = Utils.getOption("reference-dataset", options);
    if (tmpStr.isEmpty())
      setReferenceDataset(new File("."));
    else
      setReferenceDataset(new File(tmpStr));

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

    super.setOptions(options);
  }

  /**
   * Gets the current settings of the filter.
   *
   * @return an array of strings suitable for passing to setOptions
   */
  public String[] getOptions() {
    List<String> result;

    result = new ArrayList<>();

    result.add("-reference-dataset");
    result.add("" + getReferenceDataset());

    if (getUseCustomLoader()) {
      result.add("-use-custom-loader");
      result.add("-custom-loader");
      result.add(Utils.toCommandLine(getCustomLoader()));
    }

    result.addAll(Arrays.asList(super.getOptions()));

    return result.toArray(new String[0]);
  }

  /**
   * Sets the file containing the reference dataset.
   *
   * @param value     the file
   */
  public void setReferenceDataset(File value) {
    m_ReferenceDataset = value;
  }

  /**
   * Returns the file containing the reference dataset.
   *
   * @return		the file
   */
  public File getReferenceDataset() {
    return m_ReferenceDataset;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return    tip text for this property suitable for
   *            displaying in the explorer/experimenter gui
   */
  public String testSetTipText() {
    return "The file containing the reference dataset to use for aligning.";
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
      "If enabled, the specified custom loader is used for loading the reference dataset "
	+ "rather than using automatic loading.";
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
   * Sets the reference dataset to use instead of loading one from disk.
   *
   * @param value	the reference dataset to use, null to remove
   */
  public void setSuppliedReferenceDataset(Instances value) {
    m_SuppliedReferenceDataset = value;
  }

  /**
   * Returns the manually set reference dataset instead of loading one from disk.
   *
   * @return		the manually set reference dataset to use, null if to load one from disk
   */
  public Instances getSuppliedReferenceDataset() {
    return m_SuppliedReferenceDataset;
  }

  /**
   * Loads the reference dataset from disk or returns the manually supplied one.
   *
   * @return		the dataset
   * @throws Exception	if loader fails
   * @see		#getSuppliedReferenceDataset()
   */
  protected Instances loadReferenceDataset() throws Exception {
    Instances	result;

    if (m_SuppliedReferenceDataset != null)
      return m_SuppliedReferenceDataset;

    if (getUseCustomLoader()) {
      m_CustomLoader.setFile(getReferenceDataset());
      result = m_CustomLoader.getDataSet();
    }
    else {
      result = DataSource.read(getReferenceDataset().getAbsolutePath());
    }

    if (result == null)
      throw new IllegalStateException("Failed to load test set: " + getReferenceDataset());

    return result;
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
   * Determines the output format based on the input format and returns this. In
   * case the output format cannot be returned immediately, i.e.,
   * immediateOutputFormat() returns false, then this method will be called from
   * batchFinished().
   *
   * @param inputFormat the input format to base the output format on
   * @return the output format
   * @throws Exception in case the determination goes wrong
   * @see #hasImmediateOutputFormat()
   * @see #batchFinished()
   */
  @Override
  protected Instances determineOutputFormat(Instances inputFormat) throws Exception {
    m_ActualReferenceDataset = null;

    if (m_SuppliedReferenceDataset == null) {
      if (!m_ReferenceDataset.exists())
	throw new IllegalStateException("Reference dataset does not exist: " + m_ReferenceDataset);
      if (m_ReferenceDataset.isDirectory())
	throw new IllegalStateException("Reference dataset points to a directory: " + m_ReferenceDataset);
      m_ActualReferenceDataset = loadReferenceDataset();
    }
    else {
      m_ActualReferenceDataset = m_SuppliedReferenceDataset;
    }

    return new Instances(m_ActualReferenceDataset, 0);
  }

  /**
   * Checks the compatibility between reference dataset and the one to be aligned with it.
   *
   * @param reference	the reference dataset
   * @param current	the dataset to align
   * @return		the multi filter for aligning the datasets, null if nothing needs to be done
   * @throws Exception	if the datasets cannot be aligned
   */
  protected MultiFilter checkCompatibility(Instances reference, Instances current) throws Exception {
    MultiFilter		result;
    List<Filter>	filters;
    int			i;
    int			refType;
    int			curType;
    AnyToString		anyToString;
    List<String>	anyToStringIndices;
    RemoveWithLabels	removeWithLabels;
    List<String>	labelsToRemove;
    int			n;

    filters            = new ArrayList<>();
    anyToStringIndices = new ArrayList<>();

    if (reference.numAttributes() != current.numAttributes())
      throw new IllegalArgumentException("# of attributes differ: reference=" + reference.numAttributes() + ", current=" + current.numAttributes());

    // check attributes
    for (i = 0; i < reference.numAttributes(); i++) {
      refType = reference.attribute(i).type();
      curType = current.attribute(i).type();

      // same type?
      if (refType == curType) {
	if (refType == Attribute.NOMINAL) {
	  // do we need to remove values?
	  labelsToRemove = new ArrayList<>();
	  for (n = 0; n < current.attribute(i).numValues(); n++) {
	    if (reference.attribute(i).indexOfValue(current.attribute(i).value(n)) == -1)
	      labelsToRemove.add(current.attribute(i).value(n));
	  }
	  if (!labelsToRemove.isEmpty()) {
	    removeWithLabels = new RemoveWithLabels();
	    removeWithLabels.setIndex(new WekaAttributeIndex("" + (i+1)));
	    removeWithLabels.setLabelRegExp(new BaseRegExp("^" + adams.core.Utils.flatten(labelsToRemove, "|") + "$"));
	    removeWithLabels.setUpdateHeader(true);
	    filters.add(removeWithLabels);
	  }
	}
	continue;
      }

      // anything can be converted into a string attribute
      if (refType == Attribute.STRING) {
	anyToStringIndices.add("" + (i+1));
	continue;
      }

      // incompatible
      throw new IllegalArgumentException(
	"Attribute at #" + (i+1) + " is " + Attribute.typeToString(refType) + " in reference dataset, "
	  + "but " + Attribute.typeToString(curType) + " in current dataset!");
    }

    if (!anyToStringIndices.isEmpty()) {
      anyToString = new AnyToString();
      anyToString.setRange(new WekaAttributeRange(adams.core.Utils.flatten(anyToStringIndices, ",")));
      filters.add(anyToString);
    }

    if (!filters.isEmpty()) {
      result = new MultiFilter();
      result.setFilters(filters.toArray(new Filter[0]));
      if (getDebug())
	System.out.println("Compatibility filter: " + OptionUtils.getCommandLine(result));
    }
    else {
      result = null;
      if (getDebug())
	System.out.println("Datasets already compatible!");
    }

    return result;
  }

  /**
   * Processes the given data (may change the provided dataset) and returns the
   * modified version. This method is called in batchFinished().
   *
   * @param instances the data to process
   * @return the modified data
   * @throws Exception in case the processing goes wrong
   * @see #batchFinished()
   */
  @Override
  protected Instances process(Instances instances) throws Exception {
    Instances	result;
    MultiFilter	multi;
    Instances	filtered;
    int		i;
    int		n;
    Instance	instOld;
    Instance	instNew;
    double[]	values;

    // are datasets compatible?
    multi = checkCompatibility(m_ActualReferenceDataset, instances);
    if (multi != null) {
      multi.setInputFormat(instances);
      filtered = Filter.useFilter(instances, multi);
    }
    else {
      filtered = instances;
    }

    // generate output data
    result = new Instances(getOutputFormat(), filtered.numInstances());
    for (i = 0; i < filtered.numInstances(); i++) {
      instOld = filtered.instance(i);
      values  = new double[result.numAttributes()];
      for (n = 0; n < instOld.numAttributes(); n++) {
	switch (result.attribute(n).type()) {
	  case Attribute.NUMERIC:
	  case Attribute.DATE:
	    values[n] = instOld.value(n);
	    break;
	  case Attribute.STRING:
	    values[n] = outputFormatPeek().attribute(n).addStringValue(instOld.stringValue(n));
	    break;
	  case Attribute.NOMINAL:
	    values[n] = outputFormatPeek().attribute(n).indexOfValue(instOld.stringValue(n));
	    break;
	  default:
	    throw new IllegalStateException("Unhandled attribute type at #" + (n+1) + ": " + Attribute.typeToString(result.attribute(n).type()));
	}
      }
      instNew = new DenseInstance(instOld.weight(), values);
      result.add(instNew);
    }

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
   * @param args 	should contain arguments to the filter: use -h for help
   */
  public static void main(String [] args) {
    runFilter(new AlignDataset(), args);
  }
}
