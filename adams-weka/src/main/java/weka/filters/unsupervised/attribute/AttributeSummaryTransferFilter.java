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
 * AttributeSummaryTransferFilter.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package weka.filters.unsupervised.attribute;

import adams.core.base.BaseRegExp;
import adams.core.base.BaseString;
import adams.core.option.OptionUtils;
import adams.data.weka.columnfinder.ByName;
import adams.data.weka.columnfinder.ColumnFinder;
import adams.data.weka.datasetsplitter.ColumnSplitter;
import adams.data.weka.datasetsplitter.RowSplitter;
import adams.data.weka.rowfinder.RowFinder;
import adams.flow.transformer.wekadatasetsmerge.Simple;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.Instances;
import weka.core.Option;
import weka.core.Utils;
import weka.core.WekaOptionUtils;
import weka.filters.Filter;
import weka.filters.SimpleBatchFilter;
import weka.filters.UnsupervisedFilter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

/**
 <!-- globalinfo-start -->
 * Filter which trains another filter to summarise a sub-set of the data's attributes. The trained filter should be a supervised or unsupervised attribute filter. Trains the summary filter on a large set of unannotated data so it can be applied to a relatively small set which is annotated with other information.
 * <br><br>
 <!-- globalinfo-end -->
 * <p>
 <!-- options-start -->
 * Valid options are: <p>
 *
 * <pre> -row-finder &lt;value&gt;
 *  Row finder which selects rows for training the attribute-summarising filter.
 *  (default: adams.data.weka.rowfinder.NullFinder)</pre>
 *
 * <pre> -column-finder &lt;value&gt;
 *  Column finder which selects attributes to summarise.
 *  (default: adams.data.weka.columnfinder.NullFinder)</pre>
 *
 * <pre> -summary-filter &lt;value&gt;
 *  The filter to use to summarise the attributes.
 *  (default: weka.filters.unsupervised.attribute.PrincipalComponentsJ -R 0.95 -A 5 -M -1)</pre>
 *
 * <pre> -preserve-id-column &lt;value&gt;
 *  Whether the first column of the test data should be treated as a sample ID and kept in the first position of the output.
 *  (default: off)</pre>
 *
 * <pre> -class-name &lt;value&gt;
 *  The name of the attribute to treat as the class for supervised filters.
 *  (default: )</pre>
 *
 * <pre> -keep-supervised-class &lt;value&gt;
 *  Whether the class value for supervised filters should be kept in the resultant dataset or discarded.
 *  (default: off)</pre>
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
 * @author Corey Sterling (csterlin at waikato dot ac dot nz)
 */
public class AttributeSummaryTransferFilter extends SimpleBatchFilter implements UnsupervisedFilter {

  /** Auto-generated serialisation UID#. */
  private static final long serialVersionUID = 741929497853488506L;

  /** The row-finder which separates training data from actual data. */
  protected RowFinder m_RowFinder = getDefaultRowFinder();

  /** The column-finder which selects the attributes to summarise. */
  protected ColumnFinder m_ColumnFinder = getDefaultColumnFinder();

  /** The filter which performs attribute summarising. */
  protected Filter m_SummaryFilter = getDefaultSummaryFilter();

  /** Whether to treat the first attribute as an ID. */
  protected boolean m_PreserveIDColumn;

  /** The class-attribute for supervised attribute filters. */
  protected BaseString m_ClassName = getDefaultClassName();

  /** Whether to keep the supervised filter class or discard it. */
  protected boolean m_KeepSupervisedClass;

  /** Merger for reconstructing partial datasets. */
  protected Simple m_Merger;

  /** Row-splitter for splitting training and actual data. */
  protected RowSplitter m_RowSplitter;

  /** Column-splitter for separating attributes to be summarised. */
  protected ColumnSplitter m_ColumnSplitter;

  /** Column-splitter for separating the ID column. */
  protected ColumnSplitter m_IDSplitter;

  /** Column-splitter for removing the supervised filter class. */
  protected ColumnSplitter m_SupervisedClassSplitter;

  /**
   * Returns a string describing this filter.
   *
   * @return a description of the filter suitable for displaying in the
   *         explorer/experimenter gui
   */
  @Override
  public String globalInfo() {
    return "Filter which trains another filter to summarise a sub-set of the data's attributes. " +
      "The trained filter should be a supervised or unsupervised attribute filter. Trains the summary " +
      "filter on a large set of unannotated data so it can be applied to a relatively small set which is " +
      "annotated with other information.";
  }

  /**
   * Gets an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  @Override
  public Enumeration<Option> listOptions() {
    Vector<Option> result = new Vector<>();

    WekaOptionUtils.addOption(result, rowFinderTipText(), OptionUtils.getCommandLine(getDefaultRowFinder()), "row-finder");
    WekaOptionUtils.addOption(result, columnFinderTipText(), OptionUtils.getCommandLine(getDefaultColumnFinder()), "column-finder");
    WekaOptionUtils.addOption(result, summaryFilterTipText(), OptionUtils.getCommandLine(getDefaultSummaryFilter()), "summary-filter");
    WekaOptionUtils.addOption(result, preserveIDColumnTipText(), "off", "preserve-id-column");
    WekaOptionUtils.addOption(result, classNameTipText(), getDefaultClassName().getValue(), "class-name");
    WekaOptionUtils.addOption(result, keepSupervisedClassTipText(), "off", "keep-supervised-class");
    WekaOptionUtils.add(result, super.listOptions());

    return result.elements();
  }

  /**
   * returns the options of the current setup
   *
   * @return the current options
   */
  @Override
  public String[] getOptions() {
    List<String> result = new ArrayList<>();

    WekaOptionUtils.add(result, "row-finder", getRowFinder());
    WekaOptionUtils.add(result, "column-finder", getColumnFinder());
    WekaOptionUtils.add(result, "summary-filter", getSummaryFilter());
    WekaOptionUtils.add(result, "preserve-id-column", getPreserveIDColumn());
    WekaOptionUtils.add(result, "class-name", getClassName());
    WekaOptionUtils.add(result, "keep-supervised-class", getKeepSupervisedClass());
    Collections.addAll(result, super.getOptions());

    return result.toArray(new String[0]);
  }

  /**
   * Parses the options for this object.
   *
   * @param options the options to use
   * @throws Exception if the option setting fails
   */
  @Override
  public void setOptions(String[] options) throws Exception {
    setRowFinder((RowFinder) WekaOptionUtils.parse(options, "row-finder", getDefaultRowFinder()));
    setColumnFinder((ColumnFinder) WekaOptionUtils.parse(options, "column-finder", getDefaultColumnFinder()));
    setSummaryFilter((Filter) WekaOptionUtils.parse(options, "summary-filter", getDefaultSummaryFilter()));
    setPreserveIDColumn(Utils.getFlag("preserve-id-column", options));
    setClassName((BaseString) WekaOptionUtils.parse(options, "class-name", getDefaultClassName()));
    setKeepSupervisedClass(Utils.getFlag("keep-supervised-class", options));
    super.setOptions(options);
    Utils.checkForRemainingOptions(options);
  }

  /**
   * Gets the default training data row selector.
   *
   * @return  The default training data row selector.
   */
  public RowFinder getDefaultRowFinder() {
    return new adams.data.weka.rowfinder.NullFinder();
  }

  /**
   * Sets the training data row selector.
   *
   * @param value  The training data row selector.
   */
  public void setRowFinder(RowFinder value) {
    m_RowFinder = value;
  }

  /**
   * Gets the training data row selector.
   *
   * @return  The training data row selector.
   */
  public RowFinder getRowFinder() {
    return m_RowFinder;
  }

  /**
   * Gets the tip-text for the row-finder option.
   *
   * @return  The tip-text as a string.
   */
  public String rowFinderTipText() {
    return "Row finder which selects rows for training the attribute-summarising filter.";
  }

  /**
   * Gets the default column finder which selects the attributes for summarisation.
   *
   * @return  The default column finder.
   */
  public ColumnFinder getDefaultColumnFinder() {
    return new adams.data.weka.columnfinder.NullFinder();
  }

  /**
   * Sets the column finder which selects the attributes for summarisation.
   *
   * @param value  The column finder.
   */
  public void setColumnFinder(ColumnFinder value) {
    m_ColumnFinder = value;
  }

  /**
   * Gets the column finder which selects the attributes for summarisation.
   *
   * @return  The column finder.
   */
  public ColumnFinder getColumnFinder() {
    return m_ColumnFinder;
  }

  /**
   * Gets the tip-text for the column-finder option.
   *
   * @return  The tip-text as a string.
   */
  public String columnFinderTipText() {
    return "Column finder which selects attributes to summarise.";
  }

  /**
   * Gets the default filter to use to summarise the attributes.
   *
   * @return  The default filter.
   */
  public Filter getDefaultSummaryFilter() {
    PrincipalComponentsJ filter = new PrincipalComponentsJ();
    filter.setSimpleAttributeNames(true);
    return filter;
  }

  /**
   * Sets the filter to use to summarise the attributes.
   *
   * @param value  The filter.
   */
  public void setSummaryFilter(Filter value) {
    m_SummaryFilter = value;
  }

  /**
   * Gets the filter to use to summarise the attributes.
   *
   * @return  The filter.
   */
  public Filter getSummaryFilter() {
    return m_SummaryFilter;
  }

  /**
   * Gets the tip-text for the pca-filter option.
   *
   * @return  The tip-text as a string.
   */
  public String summaryFilterTipText() {
    return "The filter to use to summarise the attributes.";
  }

  /**
   * Sets whether the first non-summary attribute should be treated as an ID
   * and moved to the first attribute position.
   *
   * @param value True to preserve the ID column, false to not.
   */
  public void setPreserveIDColumn(boolean value) {
    m_PreserveIDColumn = value;
  }

  /**
   * Gets whether the first non-summary attribute should be treated as an ID
   * and moved to the first attribute position.
   *
   * @return True to preserve the ID column, false to not.
   */
  public boolean getPreserveIDColumn() {
    return m_PreserveIDColumn;
  }

  /**
   * Gets the tip-text for the preserve-id-column option.
   *
   * @return  The tip-text as a string.
   */
  public String preserveIDColumnTipText() {
    return "Whether the first column of the test data should be treated as " +
      "a sample ID and kept in the first position of the output.";
  }

  /**
   * Gets the name of the default attribute to use as the class attribute for
   * supervised summary filters.
   *
   * @return  The default attribute name.
   */
  public BaseString getDefaultClassName() {
    return new BaseString("");
  }

  /**
   * Sets the name of the attribute to use as the class attribute for
   * supervised summary filters.
   *
   * @param value  The attribute name.
   */
  public void setClassName(BaseString value) {
    m_ClassName = value;
  }

  /**
   * Gets the name of the attribute to use as the class attribute for
   * supervised summary filters.
   *
   * @return  The attribute name.
   */
  public BaseString getClassName() {
    return m_ClassName;
  }

  /**
   * Gets the tip-text for the class-name option.
   *
   * @return  The tip-text as a string.
   */
  public String classNameTipText() {
    return "The name of the attribute to treat as the class for supervised filters.";
  }

  /**
   * Sets whether to keep the class attribute of the summary attributes
   * in the final dataset.
   *
   * @param value True to keep the attribute in the final dataset, false
   *              to discard it.
   */
  public void setKeepSupervisedClass(boolean value) {
    m_KeepSupervisedClass = value;
  }

  /**
   * Gets whether to keep the class attribute of the summary attributes
   * in the final dataset.
   *
   * @return True to keep the attribute in the final dataset, false
   *         to discard it.
   */
  public boolean getKeepSupervisedClass() {
    return m_KeepSupervisedClass;
  }

  /**
   * Gets the tip-text for the keep-supervised-class option.
   *
   * @return  The tip-text as a string.
   */
  public String keepSupervisedClassTipText() {
    return "Whether the class value for supervised filters should be " +
      "kept in the resultant dataset or discarded.";
  }

  /**
   * Returns whether to allow the determineOutputFormat(Instances) method access
   * to the full dataset rather than just the header.
   * <p/>
   * Default implementation returns false.
   *
   * @return whether determineOutputFormat has access to the full input dataset
   */
  @Override
  public boolean allowAccessToFullInputFormat() {
    return true; // Required to train the summary filter
  }

  /**
   * Returns the Capabilities of this filter. Derived filters have to override
   * this method to enable capabilities.
   *
   * @return the capabilities of this object
   * @see Capabilities
   */
  @Override
  public Capabilities getCapabilities() {
    Capabilities result = super.getCapabilities();

    // Not enabled by default
    result.enable(Capability.NO_CLASS);

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
    // Create the row- and column-splitters
    m_RowSplitter = new RowSplitter();
    m_RowSplitter.setRowFinder(m_RowFinder);
    m_ColumnSplitter = new ColumnSplitter();
    m_ColumnSplitter.setColumnFinder(m_ColumnFinder);

    // Get the input instances for the summary filter
    Instances[] rowSplit = m_RowSplitter.split(inputFormat);
    Instances[] columnSplit = m_ColumnSplitter.split(rowSplit[0]);
    Instances summaryInput = columnSplit[0];

    // Set the training class for supervised filters
    if (m_ClassName.length() != 0) {
      summaryInput.setClass(summaryInput.attribute(m_ClassName.getValue()));

      // Can't have missing class values, so filter them out
      summaryInput.deleteWithMissingClass();
    }

    // Initialise the summary filter
    m_SummaryFilter.setInputFormat(summaryInput);
    Filter.useFilter(summaryInput, m_SummaryFilter);

    // Get the output format for the summary filter
    Instances summaryOutputFormat = m_SummaryFilter.getOutputFormat();

    // Determine the rest of the output format from the input format
    Instances restOutputFormat = new Instances(columnSplit[1], 0);

    // Merge the two component output formats into the final output format
    m_Merger = new Simple();
    return formatOutput(summaryOutputFormat, restOutputFormat);
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
    // Discard any training data
    Instances data = m_RowSplitter.split(instances)[1];

    // Split into summary attributes and the rest
    Instances[] columnSplit = m_ColumnSplitter.split(data);

    // Perform summary of summary attributes
    Instances summarised = Filter.useFilter(columnSplit[0], m_SummaryFilter);

    // Return the merged dataset
    return formatOutput(summarised, columnSplit[1]);
  }

  /**
   * Handles merging of output datasets and formatting. Optionally moves the
   * ID attribute to the first position. Optionally removes the class attribute
   * for supervised filters.
   *
   * @param filterOutput The output of the attribute filter.
   * @param theRest The part of the input that was attribute-reduced.
   * @return  The formatted dataset.
   */
  protected Instances formatOutput(Instances filterOutput, Instances theRest) {
    // Remove the supervised class if we don't want it in the output
    if (!m_KeepSupervisedClass && m_ClassName.length() > 0) {
      // Create the splitter once
      if (m_SupervisedClassSplitter == null) {
        m_SupervisedClassSplitter = new ColumnSplitter();
        ByName classNameFinder = new ByName();
        classNameFinder.setRegExp(new BaseRegExp("^" + m_ClassName + "$"));
        m_SupervisedClassSplitter.setColumnFinder(classNameFinder);
      }

      // Split the class from the dataset
      filterOutput = m_SupervisedClassSplitter.split(filterOutput)[1];
    }

    if (!m_PreserveIDColumn) {
      // Just straight merge
      return m_Merger.merge(new Instances[]{filterOutput, theRest});
    } else {
      // Initialise the ID splitter once
      if (m_IDSplitter == null) {
        m_IDSplitter = new ColumnSplitter();
        ByName idNameFinder = new ByName();
        idNameFinder.setRegExp(new BaseRegExp("^" + theRest.attribute(0).name() + "$"));
        m_IDSplitter.setColumnFinder(idNameFinder);
      }

      // Split the ID column
      Instances[] idSplit = m_IDSplitter.split(theRest);

      // Put the ID column first, then the rest of the attributes
      return m_Merger.merge(new Instances[] { idSplit[0], filterOutput, idSplit[1] });
    }
  }
}