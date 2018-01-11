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
 * MetaPartitionedMultiFilter.java
 * Copyright (C) 2013-2015 University of Waikato, Hamilton, New Zealand
 */
package weka.filters.unsupervised.attribute;

import adams.core.base.BaseRegExp;
import adams.core.option.OptionUtils;
import weka.core.Attribute;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Range;
import weka.core.RevisionUtils;
import weka.core.Utils;
import weka.filters.AllFilter;
import weka.filters.Filter;
import weka.filters.SimpleBatchFilter;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

/**
 <!-- globalinfo-start -->
 * With each specified filter, a regular expression is associated that defines the range of attributes to apply the filter to. This is used to configure a weka.filters.unsupervised.attribute.PartitionedMultiFilter internally to filter that actual data.<br>
 * Unused attributes can be discarded as well.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 * 
 * <pre> -D
 *  Turns on output of debugging information.</pre>
 * 
 * <pre> -F &lt;classname [options]&gt;
 *  A filter to apply (can be specified multiple times).</pre>
 * 
 * <pre> -R &lt;regexp&gt;
 *  A regular expression for matching attribute names
 *  (can be specified multiple times).
 *  For each filter an expression must be supplied.</pre>
 * 
 * <pre> -U
 *  Flag for leaving unused attributes out of the output, by default
 *  these are included in the filter output.</pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MetaPartitionedMultiFilter
  extends SimpleBatchFilter {

  /** for serialization. */
  private static final long serialVersionUID = 1054608438486826054L;

  /** The prefixes. */
  protected BaseRegExp[] m_RegExp = {new BaseRegExp(BaseRegExp.MATCH_ALL)};

  /** The filters. */
  protected Filter[] m_Filters = {new AllFilter()};

  /** Whether unused attributes are left out of the output. */
  protected boolean m_RemoveUnused = false;

  /** the actual filter used internally for filtering the data. */
  protected PartitionedMultiFilter2 m_ActualFilter = null;
  
  /**
   * Returns a string describing this filter.
   *
   * @return      a description of the classifier suitable for
   *              displaying in the explorer/experimenter gui
   */
  @Override
  public String globalInfo() {
    return 
	"With each specified filter, a regular expression is associated that "
	+ "defines the range of attributes to apply the filter to. This is "
	+ "used to configure a " + PartitionedMultiFilter2.class.getName() + " "
	+ "internally to filter that actual data.\n"
	+ "Unused attributes can be discarded as well.";
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return 		an enumeration of all the available options.
   */
  @Override
  public Enumeration listOptions() {
    Vector result = new Vector();
    Enumeration enm = super.listOptions();
    while (enm.hasMoreElements())
      result.add(enm.nextElement());

    result.addElement(new Option(
        "\tA filter to apply (can be specified multiple times).",
        "F", 1, "-F <classname [options]>"));

    result.addElement(new Option(
        "\tA regular expression for matching attribute names\n"
	+ "\t(can be specified multiple times).\n"
	+ "\tFor each filter an expression must be supplied.",
        "R", 1, "-R <regexp>"));

    result.addElement(new Option(
        "\tFlag for leaving unused attributes out of the output, by default\n"
	+ "\tthese are included in the filter output.",
        "U", 0, "-U"));

    return result.elements();
  }

  /**
   * Parses a list of options for this object. <br><br>
   *
   <!-- options-start -->
   * Valid options are: <br><br>
   * 
   * <pre> -D
   *  Turns on output of debugging information.</pre>
   * 
   * <pre> -F &lt;classname [options]&gt;
   *  A filter to apply (can be specified multiple times).</pre>
   * 
   * <pre> -R &lt;regexp&gt;
   *  A regular expression for matching attribute names
   *  (can be specified multiple times).
   *  For each filter an expression must be supplied.</pre>
   * 
   * <pre> -U
   *  Flag for leaving unused attributes out of the output, by default
   *  these are included in the filter output.</pre>
   * 
   <!-- options-end -->
   *
   * @param options 	the list of options as an array of strings
   * @throws Exception 	if an option is not supported
   */
  @Override
  public void setOptions(String[] options) throws Exception {
    String	tmpStr;
    String	classname;
    String[]	options2;
    Vector	objects;
    BaseRegExp	regexp;

    super.setOptions(options);

    setRemoveUnused(Utils.getFlag("U", options));

    objects = new Vector();
    while ((tmpStr = Utils.getOption("F", options)).length() != 0) {
      options2    = Utils.splitOptions(tmpStr);
      classname      = options2[0];
      options2[0] = "";
      objects.add(Utils.forName(Filter.class, classname, options2));
    }

    // at least one filter
    if (objects.size() == 0)
      objects.add(new AllFilter());

    setFilters((Filter[]) objects.toArray(new Filter[objects.size()]));

    objects = new Vector();
    while ((tmpStr = Utils.getOption("R", options)).length() != 0) {
      regexp = new BaseRegExp(tmpStr);
      objects.add(regexp);
    }

    // at least one Range
    if (objects.size() == 0)
      objects.add(new BaseRegExp(BaseRegExp.MATCH_ALL));

    setRegExp((BaseRegExp[]) objects.toArray(new BaseRegExp[objects.size()]));

    // is number of filters the same as ranges?
    checkDimensions();
  }

  /**
   * Gets the current settings of the filter.
   *
   * @return 		an array of strings suitable for passing to setOptions
   */
  @Override
  public String[] getOptions() {
    Vector	result;
    String[]	options;
    int		i;

    result = new Vector();

    options = super.getOptions();
    for (i = 0; i < options.length; i++)
      result.add(options[i]);

    if (getRemoveUnused())
      result.add("-U");

    for (i = 0; i < getFilters().length; i++) {
      result.add("-F");
      result.add(getFilterSpec(getFilter(i)));
    }

    for (i = 0; i < getRegExp().length; i++) {
      result.add("-R");
      result.add(getRegExp(i).stringValue());
    }

    return (String[]) result.toArray(new String[result.size()]);
  }

  /**
   * checks whether the dimensions of filters and ranges fit together.
   *
   * @throws Exception	if dimensions differ
   */
  protected void checkDimensions() throws Exception {
    if (getFilters().length != getRegExp().length)
      throw new IllegalArgumentException(
	  "Number of filters (= " + getFilters().length + ") "
	  + "and regular expressions (= " + getRegExp().length + ") don't match!");
  }

  /**
   * Sets whether unused attributes (ones that are not covered by any of the
   * ranges) are removed from the output.
   *
   * @param value	if true then the unused attributes get removed
   */
  public void setRemoveUnused(boolean value) {
    m_RemoveUnused = value;
  }

  /**
   * Gets whether unused attributes (ones that are not covered by any of the
   * ranges) are removed from the output.
   *
   * @return		true if unused attributes are removed
   */
  public boolean getRemoveUnused() {
    return m_RemoveUnused;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return    	tip text for this property suitable for
   *            	displaying in the explorer/experimenter gui
   */
  public String removeUnusedTipText() {
    return
        "If true then unused attributes (ones that are not covered by any "
      + "of the ranges) will be removed from the output.";
  }

  /**
   * Sets the list of possible filters to choose from.
   * Also resets the state of the filter (this reset doesn't affect the
   * options).
   *
   * @param filters	an array of filters with all options set.
   * @see #reset()
   */
  public void setFilters(Filter[] filters) {
    m_Filters = filters;
    reset();
  }

  /**
   * Gets the list of possible filters to choose from.
   *
   * @return 		the array of Filters
   */
  public Filter[] getFilters() {
    return m_Filters;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return    	tip text for this property suitable for
   *            	displaying in the explorer/experimenter gui
   */
  public String filtersTipText() {
    return "The base filters to be used.";
  }

  /**
   * Gets a single filter from the set of available filters.
   *
   * @param index 	the index of the filter wanted
   * @return 		the Filter
   */
  public Filter getFilter(int index) {
    return m_Filters[index];
  }

  /**
   * returns the filter classname and the options as one string.
   *
   * @param filter	the filter to get the specs for
   * @return		the classname plus options
   */
  protected String getFilterSpec(Filter filter) {
    String        result;

    if (filter == null) {
      result = "";
    }
    else {
      result  = filter.getClass().getName();
      if (filter instanceof OptionHandler)
        result += " "
          + Utils.joinOptions(((OptionHandler) filter).getOptions());
    }

    return result;
  }

  /**
   * Sets the list of possible regular expressions.
   * Also resets the state of the Range (this reset doesn't affect the
   * options).
   *
   * @param value	an array of regular expressions
   * @see #reset()
   */
  public void setRegExp(BaseRegExp[] value) {
    m_RegExp = value;
    reset();
  }

  /**
   * Gets the list of regular expressions.
   *
   * @return 		the array of expressions
   */
  public BaseRegExp[] getRegExp() {
    return m_RegExp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return    	tip text for this property suitable for
   *            	displaying in the explorer/experimenter gui
   */
  public String regExpTipText() {
    return "The regular expressions defining the subsets for the filters.";
  }

  /**
   * Gets a single {@link BaseRegExp} from the set of available expressions.
   *
   * @param index 	the index of the expression wanted
   * @return 		the regular expression
   */
  public BaseRegExp getRegExp(int index) {
    return m_RegExp[index];
  }

  /** 
   * Returns the Capabilities of this filter. Derived filters have to
   * override this method to enable capabilities.
   *
   * @return            the capabilities of this object
   * @see               Capabilities
   */
  @Override
  public Capabilities getCapabilities() {
    Capabilities	result;
    
    result = new PartitionedMultiFilter2().getCapabilities();
    result.enable(Capability.NO_CLASS);
    
    return result;
  }

  /** 
   * Returns the Capabilities of this filter, customized based on the data.
   * I.e., if removes all class capabilities, in case there's not class
   * attribute present or removes the NO_CLASS capability, in case that
   * there's a class present.
   *
   * @param data	the data to use for customization
   * @return            the capabilities of this object, based on the data
   * @see               #getCapabilities()
   */
  @Override
  public Capabilities getCapabilities(Instances data) {
    Capabilities	result;
    
    result = new PartitionedMultiFilter2().getCapabilities(data);
    result.enable(Capability.NO_CLASS);
    
    return result;
  }
  
  /**
   * resets the filter.
   */
  @Override
  protected void reset() {
    super.reset();
    
    m_ActualFilter = null;
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
    int					i;
    int					n;
    HashMap<Integer,List<Integer>>	indices;
    Attribute				att;
    List<Range>				ranges;
    List<Filter>			filters;
    Integer[]				range;
    adams.core.Range 			aRange;

    if (!isFirstBatchDone()) {
      // we need the full dataset here, see process(Instances)
      if (inputFormat.numInstances() == 0)
	return null;

      // build indices of subsets
      indices = new HashMap<Integer,List<Integer>>();
      for (n = 0; n < m_RegExp.length; n++)
	indices.put(n, new ArrayList<Integer>());
      for (i = 0; i < inputFormat.numAttributes(); i++) {
	att = inputFormat.attribute(i);
	for (n = 0; n < m_RegExp.length; n++) {
	  if (m_RegExp[n].isMatch(att.name()))
	    indices.get(n).add(att.index());
	}
      }

      // compile list of ranges/filters
      ranges  = new ArrayList<Range>();
      filters = new ArrayList<Filter>();
      for (i = 0; i < m_RegExp.length; i++) {
	// output warning for empty subsets
	if (indices.get(i).size() == 0) {
	  System.err.println("RegExp #" + (i+1) + " (" + m_RegExp[i] + ") matched no attribute names!");
	}
	else {
	  range = indices.get(i).toArray(new Integer[indices.get(i).size()]);
	  aRange = new adams.core.Range();
	  aRange.setIndices(range);
	  ranges.add(new Range(aRange.getRange()));
	  filters.add((Filter) OptionUtils.shallowCopy(m_Filters[i]));
	}
      }

      // configure filter
      m_ActualFilter = new PartitionedMultiFilter2();
      m_ActualFilter.setRemoveUnused(m_RemoveUnused);
      m_ActualFilter.setFilters(filters.toArray(new Filter[filters.size()]));
      m_ActualFilter.setRanges(ranges.toArray(new Range[ranges.size()]));
      m_ActualFilter.setInputFormat(inputFormat);

      if (getDebug())
        System.out.println("setup: " + Utils.toCommandLine(m_ActualFilter));

      return Filter.useFilter(inputFormat, m_ActualFilter);
    }
    else {
      return getOutputFormat();
    }
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

    if (!isFirstBatchDone()) {
      result = determineOutputFormat(instances);
      setOutputFormat(new Instances(result, 0));
      return result;
    }
    
    return Filter.useFilter(instances, m_ActualFilter);
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
   * Main method for executing this class.
   *
   * @param args should contain arguments for the filter: use -h for help
   */
  public static void main(String[] args) {
    runFilter(new MetaPartitionedMultiFilter(), args);
  }
}
