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
 * MultiRowProcessor.java
 * Copyright (C) 2021 University of Waikato, Hamilton, New Zealand
 */

package weka.filters.unsupervised.instance;

import adams.core.option.OptionUtils;
import adams.env.Environment;
import weka.core.Capabilities;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.RevisionUtils;
import weka.core.Utils;
import weka.core.WekaOptionUtils;
import weka.filters.SimpleBatchFilter;
import weka.filters.unsupervised.instance.multirowprocessor.processor.AbstractSelectionProcessor;
import weka.filters.unsupervised.instance.multirowprocessor.processor.PassThrough;
import weka.filters.unsupervised.instance.multirowprocessor.selection.AbstractRowSelection;
import weka.filters.unsupervised.instance.multirowprocessor.selection.IndividualRows;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

/**
 <!-- globalinfo-start -->
 * Uses the specified row selection scheme to identify groups of rows in the data coming through and then applies the selected row processor to these subsets.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p>
 *
 * <pre> -row-selection &lt;value&gt;
 *  The scheme for identifying the row subsets to process.
 *  (default: weka.filters.unsupervised.instance.multirowprocessor.selection.IndividualRows)</pre>
 *
 * <pre> -selection-processor &lt;value&gt;
 *  The scheme for processing the identified row subsets.
 *  (default: weka.filters.unsupervised.instance.multirowprocessor.processor.PassThrough)</pre>
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
public class MultiRowProcessor
    extends SimpleBatchFilter {

  private static final long serialVersionUID = 7355559053694071645L;

  protected static String ROW_SELECTION = "row-selection";

  public static final AbstractRowSelection DEFAULT_ROW_SELECTION = new IndividualRows();

  /** the row selection scheme. */
  protected AbstractRowSelection m_RowSelection;

  protected static String SELECTION_PROCESSOR = "selection-processor";

  public static final AbstractSelectionProcessor DEFAULT_SELECTION_PROCESSOR = new PassThrough();

  /** the row processing scheme. */
  protected AbstractSelectionProcessor m_SelectionProcessor;

  /**
   * Returns a string describing this filter.
   *
   * @return a description of the filter suitable for displaying in the
   * explorer/experimenter gui
   */
  @Override
  public String globalInfo() {
    return "Uses the specified row selection scheme to identify groups of rows "
        + "in the data coming through and then applies the selected row processor "
        + "to these subsets.";
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return 		an enumeration of all the available options.
   */
  public Enumeration listOptions() {
    Vector result;

    result = new Vector();

    WekaOptionUtils.addOption(result, rowSelectionTipText(), OptionUtils.getCommandLine(DEFAULT_ROW_SELECTION), ROW_SELECTION);
    WekaOptionUtils.addOption(result, selectionProcessorTipText(), OptionUtils.getCommandLine(DEFAULT_SELECTION_PROCESSOR), SELECTION_PROCESSOR);
    WekaOptionUtils.add(result, super.listOptions());
    return WekaOptionUtils.toEnumeration(result);
  }

  /**
   * Parses a given list of options.
   *
   * @param options 	the list of options as an array of strings
   * @throws Exception 	if an option is not supported
   */
  public void setOptions(String[] options) throws Exception {
    setRowSelection((AbstractRowSelection) WekaOptionUtils.parse(options, ROW_SELECTION, DEFAULT_ROW_SELECTION));
    setSelectionProcessor((AbstractSelectionProcessor) WekaOptionUtils.parse(options, SELECTION_PROCESSOR, DEFAULT_SELECTION_PROCESSOR));
    super.setOptions(options);
  }

  /**
   * Gets the current settings of the classifier.
   *
   * @return 		an array of strings suitable for passing to setOptions
   */
  public String [] getOptions() {
    List<String> result = new ArrayList<>();
    WekaOptionUtils.add(result, ROW_SELECTION, getRowSelection());
    WekaOptionUtils.add(result, SELECTION_PROCESSOR, getSelectionProcessor());
    WekaOptionUtils.add(result, super.getOptions());
    return WekaOptionUtils.toArray(result);
  }

  /**
   * Sets the row selection scheme to use.
   *
   * @param value 	the scheme
   */
  public void setRowSelection(AbstractRowSelection value) {
    m_RowSelection = value;
    reset();
  }

  /**
   * Returns the row selection scheme in use.
   *
   * @return		the scheme
   */
  public AbstractRowSelection getRowSelection() {
    return m_RowSelection;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String rowSelectionTipText() {
    return "The scheme for identifying the row subsets to process.";
  }

  /**
   * Sets the selection processor scheme to use.
   *
   * @param value 	the scheme
   */
  public void setSelectionProcessor(AbstractSelectionProcessor value) {
    m_SelectionProcessor = value;
    reset();
  }

  /**
   * Returns the selection processor scheme in use.
   *
   * @return		the scheme
   */
  public AbstractSelectionProcessor getSelectionProcessor() {
    return m_SelectionProcessor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String selectionProcessorTipText() {
    return "The scheme for processing the identified row subsets.";
  }

  /**
   * Outputs a debugging message on stderr.
   *
   * @param msg		the message
   */
  protected void debugMsg(String msg) {
    System.err.println(getClass().getName() + ": " + msg);
  }

  /**
   * Returns the Capabilities of this filter.
   *
   * @return the capabilities of this object
   * @see Capabilities
   */
  @Override
  public Capabilities getCapabilities() {
    Capabilities result;

    result = new Capabilities(this);
    result.enableAll();
    result.enable(Capabilities.Capability.NO_CLASS);

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
    return m_SelectionProcessor.generateOutputFormat(inputFormat);
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
    Instances		result;
    List<int[]>		subsets;
    List<Instance>	subset;
    List<Instance>	processed;
    int			i;
    int[]		rows;

    result = new Instances(getOutputFormat(), instances.numInstances());

    // identify subsets
    if (getDebug())
      debugMsg("Identifying subsets...");
    subsets = m_RowSelection.selectRows(instances);

    // process subsets
    if (getDebug())
      debugMsg("Processing subsets...");
    for (i = 0; i < subsets.size(); i++) {
      rows = subsets.get(i);
      if (getDebug())
        debugMsg("Subset " + (i+1) + "/" + subsets.size() + ": " + Utils.arrayToString(rows));
      subset = new ArrayList<>();
      for (int row: rows)
        subset.add(instances.instance(row));
      processed = m_SelectionProcessor.processRows(subset);
      for (Instance p: processed) {
        copyValues(p, false, instances, result);
        result.add(p);
      }
    }

    result.compactify();
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
    Environment.setEnvironmentClass(Environment.class);
    runFilter(new MultiRowProcessor(), args);
  }
}
