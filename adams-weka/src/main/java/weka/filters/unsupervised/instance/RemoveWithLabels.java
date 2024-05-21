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
 * RemoveWithLabels.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package weka.filters.unsupervised.instance;

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
import weka.filters.SimpleBatchFilter;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 <!-- globalinfo-start -->
 * Allows the user to remove nominal labels via a regular expression.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p>
 *
 * <pre> -index &lt;value&gt;
 *  The index of the attribute to process; An index is a number starting with 1; apart from attribute names (case-sensitive), the following placeholders can be used as well: first, second, third, last_2, last_1, last; numeric indices can be enforced by preceding them with '#' (eg '#12'); attribute names can be surrounded by double quotes.
 *  (default: index=last, max=-1)</pre>
 *
 * <pre> -label-regexp &lt;value&gt;
 *  The regular expression for matching the labels to remove.
 *  (default: ^(label1|label2|label3)$)</pre>
 *
 * <pre> -invert
 *  If enabled, the matching sense is inverted, i.e., the matching labels are kept and all others removed.</pre>
 *
 * <pre> -update-header
 *  If enabled, the labels also get removed from the attribute definition.</pre>
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
public class RemoveWithLabels
  extends SimpleBatchFilter {

  private static final long serialVersionUID = -7380188286130951331L;

  public final static String INDEX = "index";

  public final static String LABEL_REGEXP = "label-regexp";

  public final static String INVERT = "invert";

  public final static String UPDATE_HEADER = "update-header";

  /** the attribute to remove the labels from. */
  protected WekaAttributeIndex m_Index = getDefaultIndex();

  /** the regular expression for matching the labels to remove. */
  protected BaseRegExp m_LabelRegExp = getDefaultLabelRegExp();

  /** whether to invert the matching. */
  protected boolean m_Invert = false;

  /** whether to update the header. */
  protected boolean m_UpdateHeader = false;

  /** the label mapping (old -> new). */
  protected Map<Integer,Integer> m_LabelMapping;

  /**
   * Returns a string describing this filter.
   *
   * @return a description of the filter suitable for displaying in the
   * explorer/experimenter gui
   */
  @Override
  public String globalInfo() {
    return "Allows the user to remove nominal labels via a regular expression.";
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  public Enumeration listOptions() {
    Vector result = new Vector();
    WekaOptionUtils.addOption(result, indexTipText(), "" + getDefaultIndex(), INDEX);
    WekaOptionUtils.addOption(result, labelRegExpTipText(), "" + getDefaultLabelRegExp(), LABEL_REGEXP);
    WekaOptionUtils.addFlag(result, invertTipText(), INVERT);
    WekaOptionUtils.addFlag(result, updateHeaderTipText(), UPDATE_HEADER);
    WekaOptionUtils.add(result, super.listOptions());
    return WekaOptionUtils.toEnumeration(result);
  }

  /**
   * Parses a list of options for this object.
   *
   * @param options the list of options as an array of strings
   * @throws Exception if an option is not supported
   */
  public void setOptions(String[] options) throws Exception {
    setIndex((WekaAttributeIndex) WekaOptionUtils.parse(options, INDEX, getDefaultIndex()));
    setLabelRegExp((BaseRegExp) WekaOptionUtils.parse(options, LABEL_REGEXP, getDefaultLabelRegExp()));
    setInvert(Utils.getFlag(INVERT, options));
    setUpdateHeader(Utils.getFlag(UPDATE_HEADER, options));
    super.setOptions(options);
  }

  /**
   * Gets the current settings of the filter.
   *
   * @return an array of strings suitable for passing to setOptions
   */
  public String[] getOptions() {
    List<String> result = new ArrayList<>();
    WekaOptionUtils.add(result, INDEX, getIndex());
    WekaOptionUtils.add(result, LABEL_REGEXP, getLabelRegExp());
    WekaOptionUtils.add(result, INVERT, getInvert());
    WekaOptionUtils.add(result, UPDATE_HEADER, getUpdateHeader());
    WekaOptionUtils.add(result, super.getOptions());
    return WekaOptionUtils.toArray(result);
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
    return "The index of the attribute to process; " + m_Index.getExample();
  }

  /**
   * Returns the default label regular expression.
   *
   * @return		the default
   */
  protected BaseRegExp getDefaultLabelRegExp() {
    return new BaseRegExp("^(label1|label2|label3)$");
  }

  /**
   * Sets the regular expression for matching the labels to remove.
   *
   * @param value	the expression
   */
  public void setLabelRegExp(BaseRegExp value) {
    m_LabelRegExp = value;
    reset();
  }

  /**
   * Returns the regular expression for matching the labels to remove.
   *
   * @return		the expression
   */
  public BaseRegExp getLabelRegExp() {
    return m_LabelRegExp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String labelRegExpTipText() {
    return "The regular expression for matching the labels to remove.";
  }

  /**
   * Sets whether to invert the matching sense.
   *
   * @param value	true if to invert
   */
  public void setInvert(boolean value) {
    m_Invert = value;
    reset();
  }

  /**
   * Returns whether to invert the matching sense.
   *
   * @return		true if to invert
   */
  public boolean getInvert() {
    return m_Invert;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String invertTipText() {
    return "If enabled, the matching sense is inverted, i.e., the matching labels are kept and all others removed.";
  }

  /**
   * Sets whether to remove the labels also from the attribute definition.
   *
   * @param value	true if to update header
   */
  public void setUpdateHeader(boolean value) {
    m_UpdateHeader = value;
    reset();
  }

  /**
   * Returns whether to remove the labels also from the attribute definition.
   *
   * @return		true if to update header
   */
  public boolean getUpdateHeader() {
    return m_UpdateHeader;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String updateHeaderTipText() {
    return "If enabled, the labels also get removed from the attribute definition.";
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
    Instances			result;
    int				index;
    ArrayList<Attribute>	atts;
    Attribute			oldAtt;
    List<String>		labels;
    int				i;
    int				n;
    boolean			add;

    m_Index.setData(inputFormat);
    index = m_Index.getIntIndex();
    if (index == -1)
      throw new IllegalStateException("Failed to obtain attribute index using: " + m_Index.getIndex());

    if (m_UpdateHeader) {
      m_LabelMapping = new HashMap<>();
      atts = new ArrayList<>();
      for (i = 0; i < inputFormat.numAttributes(); i++) {
	if (i == index) {
	  oldAtt = inputFormat.attribute(i);
	  labels = new ArrayList<>();
	  for (n = 0; n < oldAtt.numValues(); n++) {
	    add = !m_LabelRegExp.isMatch(oldAtt.value(n));
	    if (m_Invert)
	      add = !add;
	    if (add) {
	      m_LabelMapping.put(n, labels.size());
	      labels.add(oldAtt.value(n));
	    }
	  }
	  atts.add(new Attribute(oldAtt.name(), labels));
	}
	else {
	  atts.add((Attribute) inputFormat.attribute(i).copy());
	}
      }
      result = new Instances(inputFormat.relationName(), atts, 0);
    }
    else {
      m_LabelMapping = null;
      result = new Instances(inputFormat, 0);
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
    Instance 	oldInst;
    Instance	newInst;
    int		i;
    int		index;
    boolean	add;
    double[]	values;
    int		n;

    result = getOutputFormat();
    index  = m_Index.getIntIndex();

    for (i = 0; i < instances.numInstances(); i++) {
      oldInst = instances.instance(i);
      add = !m_LabelRegExp.isMatch(oldInst.stringValue(index));
      if (m_Invert)
	add = !add;
      if (add) {
	if (m_UpdateHeader) {
	  values = new double[result.numAttributes()];
	  for (n = 0; n < result.numAttributes(); n++) {
	    if (n == index)
	      values[n] = m_LabelMapping.get((int) oldInst.value(n));
	    else
	      values[n] = oldInst.value(n);
	  }
	  newInst = new DenseInstance(oldInst.weight(), values);
	}
	else {
	  newInst = (Instance) oldInst.copy();
	}
	copyValues(newInst, false, oldInst.dataset(), outputFormatPeek());
	result.add(newInst);
      }
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
    runFilter(new RemoveWithLabels(), args);
  }
}
