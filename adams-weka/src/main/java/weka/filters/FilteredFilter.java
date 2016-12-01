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

/**
 * FilteredFilter.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package weka.filters;

import adams.core.option.OptionUtils;
import adams.data.weka.rowfinder.ByLabel;
import weka.core.Capabilities;
import weka.core.Instances;
import weka.core.Option;
import weka.core.RevisionUtils;
import weka.core.Utils;
import weka.filters.supervised.attribute.PLS;
import weka.filters.unsupervised.instance.DatasetCleaner;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Vector;

/**
 <!-- globalinfo-start -->
 * First applies the pre-filter to the data and the generated data is fed into the main filter. It is possible to apply the pre-filter only during the first batch ('training time').
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p>
 * 
 * <pre> -output-debug-info
 *  If set, filter is run in debug mode and
 *  may output additional info to the console</pre>
 * 
 * <pre> -do-not-check-capabilities
 *  If set, filter capabilities are not checked before filter is built
 *  (use with caution).</pre>
 * 
 * <pre> -pre &lt;filter specification&gt;
 *  Full class name of pre-filter to use, followed by scheme options.
 *  (default: weka.filters.unsupervised.instance.DatasetCleaner)</pre>
 * 
 * <pre> -main &lt;filter specification&gt;
 *  Full class name of main filter to use, followed by scheme options.
 *  (default: weka.filters.supervised.attribute.PLS)</pre>
 * 
 * <pre> -only-first-batch
 *  Whether to only apply pre-filtering to first batch.
 *  (default: off)</pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FilteredFilter
  extends SimpleBatchFilter {

  /** for serialization. */
  private static final long serialVersionUID = 2750612199034543886L;

  /** The pre-filter to apply to the data. */
  protected Filter m_PreFilter = getDefaultPreFilter();

  /** The main filter to apply to the data. */
  protected Filter m_MainFilter = getDefaultMainFilter();

  /** Whether to only apply during first batch. */
  protected boolean m_OnlyFirstBatch = false;

  /**
   * Returns a string describing this filter.
   *
   * @return a description of the filter suitable for displaying in the
   *         explorer/experimenter gui
   */
  @Override
  public String globalInfo() {
    return
      "First applies the pre-filter to the data and the generated data is "
	+ "fed into the main filter. It is possible to apply the pre-filter "
	+ "only during the first batch ('training time').";
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return 		an enumeration of all the available options.
   */
  @Override
  public Enumeration listOptions() {
    Vector        	result;
    Enumeration   	en;

    result = new Vector();

    en = super.listOptions();
    while (en.hasMoreElements())
      result.addElement(en.nextElement());

    result.addElement(new Option(
	"\tFull class name of pre-filter to use, followed by scheme options.\n"
	+ "\t(default: " + getDefaultPreFilter().getClass().getName() + ")",
	"pre", 1, "-pre <filter specification>"));

    result.addElement(new Option(
	"\tFull class name of main filter to use, followed by scheme options.\n"
	+ "\t(default: " + getDefaultMainFilter().getClass().getName() + ")",
	"main", 1, "-main <filter specification>"));

    result.addElement(new Option(
	"\tWhether to only apply pre-filtering to first batch.\n"
	+ "\t(default: off)",
	"only-first-batch", 0, "-only-first-batch"));

    return result.elements();
  }

  /**
   * Parses the options for this object.
   *
   * @param options	the options to use
   * @throws Exception	if setting of options fails
   */
  @Override
  public void setOptions(String[] options) throws Exception {
    String	tmpStr;

    setOnlyFirstBatch(Utils.getFlag("only-first-batch", options));

    tmpStr = Utils.getOption("pre", options);
    if (tmpStr.length() == 0)
      tmpStr = getDefaultPreFilter().getClass().getName();
    setPreFilter((Filter) OptionUtils.forAnyCommandLine(Filter.class, tmpStr));

    tmpStr = Utils.getOption("main", options);
    if (tmpStr.length() == 0)
      tmpStr = getDefaultMainFilter().getClass().getName();
    setMainFilter((Filter) OptionUtils.forAnyCommandLine(Filter.class, tmpStr));

    super.setOptions(options);
  }

  /**
   * Gets the current settings of the classifier.
   *
   * @return 		an array of strings suitable for passing to setOptions
   */
  @Override
  public String[] getOptions() {
    Vector<String>	result;

    result = new Vector<>(Arrays.asList(super.getOptions()));

    if (getOnlyFirstBatch())
      result.add("-only-first-batch");

    result.add("-pre");
    result.add(OptionUtils.getCommandLine(getPreFilter()));

    result.add("-main");
    result.add(OptionUtils.getCommandLine(getMainFilter()));

    return result.toArray(new String[result.size()]);
  }

  /**
   * Returns the Capabilities of this filter.
   *
   * @return            the capabilities of this object
   * @see               Capabilities
   */
  @Override
  public Capabilities getCapabilities() {
    return m_PreFilter.getCapabilities();
  }

  /**
   * Derived filters may removed rows.
   *
   * @return 		true if instances might get removed
   */
  @Override
  public boolean mayRemoveInstanceAfterFirstBatchDone() {
    return false;
  }

  /**
   * Returns the default pre-filter.
   *
   * @return		the default
   */
  protected Filter getDefaultPreFilter() {
    DatasetCleaner	result;

    result = new DatasetCleaner();
    result.setRowFinder(new ByLabel());

    return result;
  }

  /**
   * Sets the pre-filter to use.
   *
   * @param value 	the filter
   */
  public void setPreFilter(Filter value) {
    m_PreFilter = value;
    reset();
  }

  /**
   * Returns the pre-filter in use.
   *
   * @return 		the filter
   */
  public Filter getPreFilter() {
    return m_PreFilter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String preFilterTipText() {
    return "The filter to generate the filtered to feed into the main filter.";
  }

  /**
   * Returns the default main filter.
   *
   * @return		the default
   */
  protected Filter getDefaultMainFilter() {
    return new PLS();
  }

  /**
   * Sets the main filter to use.
   *
   * @param value 	the filter
   */
  public void setMainFilter(Filter value) {
    m_MainFilter = value;
    reset();
  }

  /**
   * Returns the main filter in use.
   *
   * @return 		the filter
   */
  public Filter getMainFilter() {
    return m_MainFilter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String mainFilterTipText() {
    return "The actual filter to use, uses the pre-filtered data.";
  }

  /**
   * Set whether to apply row finder during first batch.
   *
   * @param value 	true if to only apply during first batch
   */
  public void setOnlyFirstBatch(boolean value) {
    m_OnlyFirstBatch = value;
    reset();
  }

  /**
   * Returns whether to apply row finder during first batch.
   *
   * @return 		true if to only apply during first batch
   */
  public boolean getOnlyFirstBatch() {
    return m_OnlyFirstBatch;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String onlyFistBatchTipText() {
    return
      "If enabled the row finder will only get applied during the first batch.";
  }

  /**
   * Returns whether to allow the determineOutputFormat(Instances) method access
   * to the full dataset rather than just the header.
   *
   * @return whether determineOutputFormat has access to the full input dataset
   */
  @Override
  public boolean allowAccessToFullInputFormat() {
    return true;
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
  @Override
  protected Instances determineOutputFormat(Instances inputFormat) throws Exception {
    Instances	result;
    Instances	tmp;

    // the pre-filter shouldn't alter the structure!
    m_PreFilter.setInputFormat(inputFormat);
    tmp = Filter.useFilter(inputFormat, m_PreFilter);
    if (!tmp.equalHeaders(inputFormat))
      throw new IllegalStateException("Pre-filter alters the dataset structure!");

    // let main filter determine the output format
    m_MainFilter.setInputFormat(inputFormat);
    tmp    = Filter.useFilter(inputFormat, m_MainFilter);
    result = new Instances(tmp, 0);

    return result;
  }

  /**
   * Processes the given data (may change the provided dataset) and returns
   * the modified version. This method is called in batchFinished().
   *
   * @param instances   the data to process
   * @return            the modified data
   * @throws Exception  in case the processing goes wrong
   * @see               #batchFinished()
   */
  @Override
  protected Instances process(Instances instances) throws Exception {
    Instances		result;
    Instances		reduced;

    // apply pre-filter
    if (!m_OnlyFirstBatch || !isFirstBatchDone())
      m_PreFilter.setInputFormat(instances);
    reduced = Filter.useFilter(instances, m_PreFilter);

    if (!m_OnlyFirstBatch || !isFirstBatchDone()) {
      m_MainFilter.setInputFormat(reduced);
      Filter.useFilter(reduced, m_MainFilter);
    }
    result = Filter.useFilter(instances, m_MainFilter);

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
   * @param args 	should contain arguments to the filter: use -h for help
   */
  public static void main(String[] args) {
    runFilter(new FilteredFilter(), args);
  }
}
