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
 * EquiDistance.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package weka.filters.unsupervised.attribute;

import adams.core.Range;
import adams.core.base.BaseRegExp;
import gnu.trove.list.TDoubleList;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;
import weka.core.Attribute;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.WekaOptionUtils;
import weka.filters.SimpleBatchFilter;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

/**
 <!-- globalinfo-start -->
 * A filter for interpolating the numeric attributes.Using the same number of points as are currently present in the input will have no effect.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p>
 *
 * <pre> -num-points &lt;value&gt;
 *  The number of points to generate, '-1' will use the same amount of points as currently in the input data.
 *  (default: -1)</pre>
 *
 * <pre> -att-sel &lt;value&gt;
 *  Determines how the attributes are selected.
 *  (default: RANGE)</pre>
 *
 * <pre> -range &lt;value&gt;
 *  The range of attributes to use, if RANGE selected.
 *  (default: range=first-last, max=-1, inv=false)</pre>
 *
 * <pre> -regexp &lt;value&gt;
 *  The regular expression for identifying the attributes via their name, if REGEXP selected.
 *  (default: .*)</pre>
 *
 * <pre> -prefix &lt;value&gt;
 *  The prefix to use for the new attributes; 1-based index gets appended.
 *  (default: att-)</pre>
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
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class EquiDistance
  extends SimpleBatchFilter {

  private static final long serialVersionUID = 1579715266499862368L;

  /**
   * Defines how the attributes are selected.
   *
   * @author FracPete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum AttributeSelection {
    RANGE,
    REGEXP
  }

  /** the option for the number of points. */
  public final static String NUM_POINTS = "num-points";

  /** the option for the attribute selection. */
  public final static String ATTRIBUTE_SELECTION = "att-sel";

  /** the option for the attribute range. */
  public final static String RANGE = "range";

  /** the option for the attribute regexp. */
  public final static String REGEXP = "regexp";

  /** the option for the prefix of the new attribute names. */
  public final static String PREFIX = "prefix";

  /** the number of points to output ("-1" uses the same amount of points as
   * currently in the data). */
  protected int m_NumPoints = getDefaultNumPoints();

  /** how to select the attributes. */
  protected AttributeSelection m_AttributeSelection = getDefaultAttributeSelection();

  /** the attribute range to use. */
  protected Range m_Range = getDefaultRange();

  /** the regular expression to use. */
  protected BaseRegExp m_RegExp = getDefaultRegExp();

  /** the prefix for the new attributes. */
  protected String m_Prefix = getDefaultPrefix();

  /** the indices of the identified attributes. */
  protected TIntList m_Attributes;

  /** the average spacing. */
  protected double m_AverageSpacing;

  /**
   * Returns a string describing this filter.
   *
   * @return a description of the filter suitable for displaying in the
   *         explorer/experimenter gui
   */
  @Override
  public String globalInfo() {
    return
      "A filter for interpolating the numeric attributes."
	+ "Using the same number of points as are currently present in the "
	+ "input will have no effect.";
  }

  /**
   * Resets the filter.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Attributes = null;
  }

  /**
   * Returns the default number of points.
   *
   * @return		the default
   */
  protected int getDefaultNumPoints() {
    return -1;
  }

  /**
   * Sets the number of points to use.
   *
   * @param value	the number of points
   */
  public void setNumPoints(int value) {
    if ((value > 0) || (value == -1)) {
      m_NumPoints = value;
      reset();
    }
    else {
      System.err.println(
	this.getClass().getName() + ": only '-1' (uses the number of points "
	  + "currently in the data) or positive numbers are allowed!");
    }
  }

  /**
   * Returns the number of points to output.
   *
   * @return		the number of points
   */
  public int getNumPoints() {
    return m_NumPoints;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String numPointsTipText() {
    return
      "The number of points to generate, '-1' will use the same amount of "
	+ "points as currently in the input data.";
  }

  /**
   * Returns the default attribute selection.
   *
   * @return		the default
   */
  protected AttributeSelection getDefaultAttributeSelection() {
    return AttributeSelection.RANGE;
  }

  /**
   * Sets how the attributes get selected.
   *
   * @param value	the selection
   */
  public void setAttributeSelection(AttributeSelection value) {
    m_AttributeSelection = value;
    reset();
  }

  /**
   * Returns how the attributes get selected.
   *
   * @return		the selection
   */
  public AttributeSelection getAttributeSelection() {
    return m_AttributeSelection;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String attributeSelectionTipText() {
    return "Determines how the attributes are selected.";
  }

  /**
   * Returns the default attribute range.
   *
   * @return		the default
   */
  protected Range getDefaultRange() {
    return new Range(Range.ALL);
  }

  /**
   * Sets the attribute range.
   *
   * @param value	the attribute range
   * @see		#getAttributeSelection()
   * @see		AttributeSelection#RANGE
   */
  public void setRange(Range value) {
    m_Range = value;
    reset();
  }

  /**
   * Returns the attribute range.
   *
   * @return		the attribute range
   * @see		#getAttributeSelection()
   * @see		AttributeSelection#RANGE
   */
  public Range getRange() {
    return m_Range;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String rangeTipText() {
    return "The range of attributes to use, if " + AttributeSelection.RANGE + " selected.";
  }

  /**
   * Returns the default regular expression for identifying attributes.
   *
   * @return		the default
   */
  protected BaseRegExp getDefaultRegExp() {
    return new BaseRegExp(BaseRegExp.MATCH_ALL);
  }

  /**
   * Sets the regular expression for identifying attributes.
   *
   * @param value	the expression
   * @see		#getAttributeSelection()
   * @see		AttributeSelection#REGEXP
   */
  public void setRegExp(BaseRegExp value) {
    m_RegExp = value;
    reset();
  }

  /**
   * Returns the regular expression for identifying attributes.
   *
   * @return		the expression
   * @see		#getAttributeSelection()
   * @see		AttributeSelection#REGEXP
   */
  public BaseRegExp getRegExp() {
    return m_RegExp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String regExpTipText() {
    return "The regular expression for identifying the attributes via their name, if " + AttributeSelection.REGEXP+ " selected.";
  }

  /**
   * Returns the default prefix for the new attributes.
   *
   * @return		the default
   */
  protected String getDefaultPrefix() {
    return "att-";
  }

  /**
   * Sets the prefix for the new attributes.
   *
   * @param value	the prefix
   */
  public void setPrefix(String value) {
    m_Prefix = value;
    reset();
  }

  /**
   * Returns the prefix for the new attributes.
   *
   * @return		the prefix
   */
  public String getPrefix() {
    return m_Prefix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String prefixTipText() {
    return "The prefix to use for the new attributes; 1-based index gets appended.";
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  @Override
  public Enumeration listOptions() {
    Vector result = new Vector();
    WekaOptionUtils.addOption(result, numPointsTipText(), "" + getDefaultNumPoints(), NUM_POINTS);
    WekaOptionUtils.addOption(result, attributeSelectionTipText(), "" + getDefaultAttributeSelection(), ATTRIBUTE_SELECTION);
    WekaOptionUtils.addOption(result, rangeTipText(), "" + getDefaultRange(), RANGE);
    WekaOptionUtils.addOption(result, regExpTipText(), "" + getDefaultRegExp(), REGEXP);
    WekaOptionUtils.addOption(result, prefixTipText(), "" + getDefaultPrefix(), PREFIX);
    WekaOptionUtils.add(result, super.listOptions());
    return WekaOptionUtils.toEnumeration(result);
  }

  /**
   * Sets the OptionHandler's options using the given list. All options
   * will be set (or reset) during this call (i.e. incremental setting
   * of options is not possible).
   *
   * @param options the list of options as an array of strings
   * @throws Exception if an option is not supported
   */
  @Override
  public void setOptions(String[] options) throws Exception {
    setNumPoints(WekaOptionUtils.parse(options, NUM_POINTS, getDefaultNumPoints()));
    setAttributeSelection((AttributeSelection) WekaOptionUtils.parse(options, ATTRIBUTE_SELECTION, getDefaultAttributeSelection()));
    setRange(WekaOptionUtils.parse(options, RANGE, getDefaultRange()));
    setRegExp((BaseRegExp) WekaOptionUtils.parse(options, REGEXP, getDefaultRegExp()));
    setPrefix(WekaOptionUtils.parse(options, PREFIX, getDefaultPrefix()));
    super.setOptions(options);
  }

  /**
   * Gets the current option settings for the OptionHandler.
   *
   * @return the list of current option settings as an array of strings
   */
  @Override
  public String[] getOptions() {
    List<String> result = new ArrayList<>();
    WekaOptionUtils.add(result, NUM_POINTS, getNumPoints());
    WekaOptionUtils.add(result, ATTRIBUTE_SELECTION, getAttributeSelection());
    WekaOptionUtils.add(result, RANGE, getRange());
    WekaOptionUtils.add(result, REGEXP, getRegExp());
    WekaOptionUtils.add(result, PREFIX, getPrefix());
    return WekaOptionUtils.toArray(result);
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
    Capabilities	result;

    result = new Capabilities(this);

    result.enable(Capability.NUMERIC_ATTRIBUTES);
    result.enable(Capability.DATE_ATTRIBUTES);
    result.enable(Capability.NOMINAL_ATTRIBUTES);
    result.enable(Capability.STRING_ATTRIBUTES);

    result.enable(Capability.NUMERIC_CLASS);
    result.enable(Capability.DATE_CLASS);
    result.enable(Capability.NOMINAL_CLASS);
    result.enable(Capability.NO_CLASS);

    result.setMinimumNumberInstances(0);

    return result;
  }

  /**
   * Returns interpolated value.
   *
   * @param index	the index we have to interpolate for
   * @param indexLeft	the index of the "earlier" attribute
   * @param valueLeft	the "earlier" attribute value
   * @param indexRight	the index of the "later" attribute
   * @param valueRight	the "later" attribute value
   * @return		the interpolated value
   */
  protected double interpolate(double index, int indexLeft, double valueLeft, int indexRight, double valueRight) {
    double	result;
    double 	indexdiff;
    double	percLeft;
    double	percRight;

    indexdiff = indexRight - indexLeft;
    percLeft  = 1.0 - ((index - indexLeft) / indexdiff);
    percRight = 1.0 - ((indexRight - index) / indexdiff);
    result    = valueLeft*percLeft + valueRight*percRight;

    return result;
  }

  /**
   * Determines the output format based on the input format and returns this. In
   * case the output format cannot be returned immediately, i.e.,
   * hasImmediateOutputFormat() returns false, then this method will called from
   * batchFinished() after the call of preprocess(Instances), in which, e.g.,
   * statistics for the actual processing step can be gathered.
   *
   * @param inputFormat the input format to base the output format on
   * @return the output format
   * @throws Exception in case the determination goes wrong
   */
  @Override
  protected Instances determineOutputFormat(Instances inputFormat) throws Exception {
    Instances			result;
    ArrayList<Attribute>	atts;
    int				i;

    if (m_NumPoints == -1)
      return new Instances(inputFormat, 0);

    // identify attributes
    m_Attributes = new TIntArrayList();
    switch (m_AttributeSelection) {
      case RANGE:
	m_Range.setMax(inputFormat.numAttributes());
	m_Attributes.add(m_Range.getIntIndices());
	if (m_Attributes.size() == 0)
	  throw new IllegalStateException("No matching attributes found (range: " + m_Range.getRange() + ")!");
	break;
      case REGEXP:
	for (i = 0; i < inputFormat.numAttributes(); i++) {
	  if (m_RegExp.isMatch(inputFormat.attribute(i).name()))
	    m_Attributes.add(i);
	}
	if (m_Attributes.size() == 0)
	  throw new IllegalStateException("No matching attributes found (regexp: " + m_RegExp.getValue() + ")!");
	break;
      default:
	throw new IllegalStateException("Unhandled attribute selection: " + m_AttributeSelection);
    }
    if (getDebug())
      System.out.println("# Attributes: " + m_Attributes.size());

    if (m_Attributes.size() == m_NumPoints)
      return new Instances(inputFormat, 0);

    // create new dataset
    atts = new ArrayList<>();
    for (i = 0; i < m_NumPoints; i++)
      atts.add(new Attribute(m_Prefix + (i+1)));
    for (i = 0; i < inputFormat.numAttributes(); i++) {
      if (!m_Attributes.contains(i)) {
	switch (inputFormat.attribute(i).type()) {
	  case Attribute.NUMERIC:
	  case Attribute.DATE:
	  case Attribute.NOMINAL:
	  case Attribute.STRING:
	    atts.add((Attribute) inputFormat.attribute(i).copy());
	    break;
	}
      }
    }
    result = new Instances(inputFormat.relationName(), atts, 0);

    // average spacing
    if (m_NumPoints < m_Attributes.size())
      m_AverageSpacing = (double) m_Attributes.size() / (m_NumPoints - 1);
    else
      m_AverageSpacing = (double) (m_Attributes.size() - 1) / (m_NumPoints - 1);
    if (getDebug())
      System.out.println("Average spacing: " + m_AverageSpacing);

    return result;
  }

  /**
   * processes the given instance (may change the provided instance) and returns
   * the modified version.
   *
   * @param instance the instance to process
   * @return the modified data
   * @throws Exception in case the processing goes wrong
   */
  protected Instance process(Instances header, Instance instance) throws Exception {
    Instance		result;
    TDoubleList		values;
    int			i;
    double		index;
    double 		newValue;
    TDoubleList		valuesOld;

    if (m_NumPoints == -1)
      return instance;
    if (m_Attributes.size() == m_NumPoints)
      return instance;

    valuesOld = new TDoubleArrayList();
    for (i = 0; i < m_Attributes.size(); i++)
      valuesOld.add(instance.value(m_Attributes.get(i)));

    values = new TDoubleArrayList();
    // first
    values.add(valuesOld.get(0));

    // inbetween
    for (i = 1; i < m_NumPoints - 1; i++) {
      index = (double) i * m_AverageSpacing;
      if (Math.floor(index) != Math.ceil(index)) {
	  newValue = interpolate(
	    index,
	    (int) Math.floor(index), valuesOld.get((int) Math.floor(index)),
	    (int) Math.ceil(index), valuesOld.get((int) Math.ceil(index)));
	values.add(newValue);
      }
      else {
	if (index < m_Attributes.get(m_Attributes.size() - 1))
	  newValue = interpolate(
	    index,
	    (int) index - 1, valuesOld.get((int) index - 1),
	    (int) index + 1, valuesOld.get((int) index + 1));
	else
	  newValue = interpolate(
	    index,
	    (int) index - 1, valuesOld.get((int) index - 1),
	    (int) index, valuesOld.get((int) index));
	newValue = (newValue + instance.value(m_Attributes.get((int) index))) / 2;
	values.add(newValue);
      }
    }

    // last
    values.add(valuesOld.get(valuesOld.size() - 1));

    // other attributes
    for (i = 0; i < instance.numAttributes(); i++) {
      if (!m_Attributes.contains(i)) {
	switch (instance.attribute(i).type()) {
	  case Attribute.NUMERIC:
	  case Attribute.DATE:
	  case Attribute.NOMINAL:
	    values.add(instance.value(i));
	    break;
	  case Attribute.STRING:
	    values.add(header.attribute(values.size()).addStringValue(instance.stringValue(i)));
	    break;
	}
      }
    }

    result = new DenseInstance(instance.weight(), values.toArray());

    return result;
  }

  /**
   * Processes the given data (may change the provided dataset) and returns the
   * modified version. This method is called in batchFinished().
   *
   * @param instances the data to process
   * @return the modified data
   * @throws Exception in case the processing goes wrong
   */
  @Override
  protected Instances process(Instances instances) throws Exception {
    Instances	result;

    result = getOutputFormat();
    for (Instance inst: instances)
      result.add(process(result, inst));

    return result;
  }

  /**
   * Main method for testing this class.
   *
   * @param args should contain arguments to the filter: use -h for help
   */
  public static void main(String[] args) {
    runFilter(new EquiDistance(), args);
  }
}
