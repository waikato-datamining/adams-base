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
 * NominalToNumeric.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package weka.filters.unsupervised.attribute;

import adams.core.base.BaseRegExp;
import adams.data.weka.WekaAttributeIndex;
import weka.core.Attribute;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.RevisionUtils;
import weka.core.Utils;
import weka.core.WekaOptionUtils;
import weka.filters.SimpleStreamFilter;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 <!-- globalinfo-start -->
 * Converts a nominal attribute into a numeric one. Can either just use the internal representation of the labels as numeric value or parse the label itself (subset can be extracted via regexp).
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p>
 *
 * <pre> -index &lt;value&gt;
 *  The index of the attribute to convert; An index is a number starting with 1; apart from attribute names (case-sensitive), the following placeholders can be used as well: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); attribute names can be surrounded by double quotes.
 *  (default: index=last, max=-1)</pre>
 *
 * <pre> -type &lt;value&gt;
 *  The type of conversion to perform.
 *  (default: INTERNAL_REPRESENTATION)</pre>
 *
 * <pre> -find &lt;value&gt;
 *  The regular expression to use for extracting the numeric part from the label; use .* to match label as a whole.
 *  (default: .*)</pre>
 *
 * <pre> -replace &lt;value&gt;
 *  The expression to use for assembling the numeric part; use $0 to use label as is.
 *  (default: $0)</pre>
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
 */
public class NominalToNumeric
  extends SimpleStreamFilter {

  private static final long serialVersionUID = -2908650889595166498L;

  /**
   * Enumeration of conversion types.
   */
  public enum ConversionType {
    INTERNAL_REPRESENTATION,
    FROM_LABEL,
  }

  public static final String INDEX = "index";

  public static final String TYPE = "type";

  public static final String FIND = "find";

  public static final String REPLACE = "replace";

  /** the attribute to convert. */
  protected WekaAttributeIndex m_Index = getDefaultIndex();

  /** the type of conversion to perform. */
  protected ConversionType m_Type = getDefaultType();

  /** the regular expression to use. */
  protected BaseRegExp m_Find = getDefaultFind();

  /** the replacement string. */
  protected String m_Replace = getDefaultReplace();

  /** the attribute index. */
  protected int m_AttIndex;

  /** the mapping between label and new value. */
  protected Map<String,Double> m_Mapping;

  /**
   * Returns a string describing this filter.
   *
   * @return a description of the filter suitable for displaying in the
   *         explorer/experimenter gui
   */
  @Override
  public String globalInfo() {
    return "Converts a nominal attribute into a numeric one. Can either just "
      + "use the internal representation of the labels as numeric value "
      + "or parse the label itself (subset can be extracted via regexp).";
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  @Override
  public Enumeration listOptions() {
    Vector result = new Vector();
    WekaOptionUtils.addOption(result, indexTipText(), "" + getDefaultIndex(), INDEX);
    WekaOptionUtils.addOption(result, typeTipText(), "" + getDefaultType(), TYPE);
    WekaOptionUtils.addOption(result, findTipText(), "" + getDefaultFind(), FIND);
    WekaOptionUtils.addOption(result, replaceTipText(), "" + getDefaultReplace(), REPLACE);
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
    setIndex((WekaAttributeIndex) WekaOptionUtils.parse(options, INDEX, getDefaultIndex()));
    setType((ConversionType) WekaOptionUtils.parse(options, TYPE, getDefaultType()));
    setFind((BaseRegExp) WekaOptionUtils.parse(options, FIND, getDefaultFind()));
    setReplace(WekaOptionUtils.parse(options, REPLACE, getDefaultReplace()));
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
    WekaOptionUtils.add(result, INDEX, getIndex());
    WekaOptionUtils.add(result, TYPE, getType());
    WekaOptionUtils.add(result, FIND, getFind());
    WekaOptionUtils.add(result, REPLACE, getReplace());
    WekaOptionUtils.add(result, super.getOptions());
    return WekaOptionUtils.toArray(result);
  }

  /**
   * Resets the cleaner.
   */
  @Override
  protected void reset() {
    super.reset();

    m_AttIndex = -1;
    m_Mapping  = null;
  }

  /**
   * Returns the default attribute index.
   *
   * @return		the default
   */
  protected WekaAttributeIndex getDefaultIndex() {
    return new WekaAttributeIndex(WekaAttributeIndex.LAST);
  }

  /**
   * Sets the index of the attribute to convert.
   *
   * @param value	the regexp
   */
  public void setIndex(WekaAttributeIndex value) {
    m_Index = value;
    reset();
  }

  /**
   * Returns the index of the attribute to convert.
   *
   * @return		the index
   */
  public WekaAttributeIndex getIndex() {
    return m_Index;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String indexTipText() {
    return "The index of the attribute to convert; " + m_Index.getExample();
  }

  /**
   * Returns the default regular expression for finding tokens to clean.
   *
   * @return		the default
   */
  protected ConversionType getDefaultType() {
    return ConversionType.INTERNAL_REPRESENTATION;
  }

  /**
   * Sets the conversion type to use.
   *
   * @param value	the type
   */
  public void setType(ConversionType value) {
    m_Type = value;
    reset();
  }

  /**
   * Returns the conversion type to use.
   *
   * @return		the type
   */
  public ConversionType getType() {
    return m_Type;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeTipText() {
    return "The type of conversion to perform.";
  }

  /**
   * Returns the default regular expression for finding tokens to clean.
   *
   * @return		the default
   */
  protected BaseRegExp getDefaultFind() {
    return new BaseRegExp(BaseRegExp.MATCH_ALL);
  }

  /**
   * Sets the regular expression to use for extracting the numeric part from the label.
   *
   * @param value	the regexp
   */
  public void setFind(BaseRegExp value) {
    m_Find = value;
    reset();
  }

  /**
   * Returns the regular expression to use for extracting the numeric part from the label.
   *
   * @return		the regexp
   */
  public BaseRegExp getFind() {
    return m_Find;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String findTipText() {
    return "The regular expression to use for extracting the numeric part from the label; use .* to match label as a whole.";
  }

  /**
   * Returns the default expression for replacing matching tokens with.
   *
   * @return		the default
   */
  protected String getDefaultReplace() {
    return "$0";
  }

  /**
   * Sets the expression to use for assembling the numeric part.
   *
   * @param value	the expression
   */
  public void setReplace(String value) {
    m_Replace = value;
    reset();
  }

  /**
   * Returns the expression to use for assembling the numeric part.
   *
   * @return		the expression
   */
  public String getReplace() {
    return m_Replace;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String replaceTipText() {
    return "The expression to use for assembling the numeric part; use $0 to use label as is.";
  }

  @Override
  public Capabilities getCapabilities() {
    Capabilities result;

    result = new Capabilities(this);

    result.enable(Capability.NUMERIC_ATTRIBUTES);
    result.enable(Capability.DATE_ATTRIBUTES);
    result.enable(Capability.NOMINAL_ATTRIBUTES);
    result.enable(Capability.STRING_ATTRIBUTES);
    result.enable(Capability.MISSING_VALUES);

    result.enable(Capability.NUMERIC_CLASS);
    result.enable(Capability.DATE_CLASS);
    result.enable(Capability.NOMINAL_CLASS);
    result.enable(Capability.STRING_CLASS);
    result.enable(Capability.NO_CLASS);
    result.enable(Capability.MISSING_CLASS_VALUES);

    result.setMinimumNumberInstances(0);

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
    Attribute			att;
    int				i;
    String			value;

    // check attribute
    m_Index.setData(inputFormat);
    m_AttIndex = m_Index.getIntIndex();
    if (m_AttIndex == -1)
      throw new Exception("Failed to locate attribute using index: " + m_Index);
    if (!inputFormat.attribute(m_AttIndex).isNominal())
      throw new Exception("Attribute at '" + m_Index + "' is not nominal!");

    // type-specific initialization
    switch (m_Type) {
      case INTERNAL_REPRESENTATION:
        // nothing to do
        break;
      case FROM_LABEL:
	m_Mapping = new HashMap<>();
	att       = inputFormat.attribute(m_AttIndex);
	for (i = 0; i < att.numValues(); i++) {
	  value = att.value(i).replaceAll(m_Find.getValue(), m_Replace);
	  try {
	    m_Mapping.put(att.value(i), Double.parseDouble(value));
	  }
	  catch (Exception e) {
	    throw new IllegalStateException(
	      "Failed to parse label #" + (i+1) + " '" + att.value(i) + "' with " + m_Find + "/" + m_Replace + " -> " + value + "!", e);
	  }
	}
        break;
      default:
        throw new IllegalStateException("Unhandled conversion type: " + m_Type);
    }

    // construct new header
    atts = new ArrayList<>();
    for (i = 0; i < inputFormat.numAttributes(); i++) {
      att = inputFormat.attribute(i);
      if (i == m_AttIndex)
        atts.add(new Attribute(att.name()));
      else
        atts.add((Attribute) att.copy());
    }
    result = new Instances(inputFormat.relationName(), atts, 0);
    result.setClassIndex(inputFormat.classIndex());

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
  @Override
  protected Instance process(Instance instance) throws Exception {
    Instance	result;
    double[]	values;

    values = instance.toDoubleArray();
    switch (m_Type) {
      case INTERNAL_REPRESENTATION:
        // nothing to do
        break;
      case FROM_LABEL:
        if (!Utils.isMissingValue(values[m_AttIndex]))
	  values[m_AttIndex] = m_Mapping.get(instance.stringValue(m_AttIndex));
        break;
      default:
        throw new IllegalStateException("Unhandled conversion type: " + m_Type);
    }

    result = new DenseInstance(instance.weight(), values);
    result.setDataset(outputFormatPeek());

    copyValues(result, false, instance.dataset(), outputFormatPeek());

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
    runFilter(new NominalToNumeric(), args);
  }
}
