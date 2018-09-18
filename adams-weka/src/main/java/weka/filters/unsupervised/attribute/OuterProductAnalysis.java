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
 * OuterProductAnalysis.java
 * Copyright (C) 2018 University of Waikato, Hamilton, New Zealand
 */

package weka.filters.unsupervised.attribute;

import adams.core.base.BaseRegExp;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import weka.core.Attribute;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.RevisionUtils;
import weka.core.TechnicalInformation;
import weka.core.TechnicalInformation.Field;
import weka.core.TechnicalInformation.Type;
import weka.core.TechnicalInformationHandler;
import weka.core.Utils;
import weka.filters.SimpleStreamFilter;
import weka.filters.UnsupervisedFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

/**
 <!-- globalinfo-start -->
 * Performs Outer Product Analysis (OPA).<br>
 * <br>
 * For more information, see:<br>
 * Fabricio S.Terra, Raphael A.Viscarra Rossel, Jose A.M.Dematte (2019). Spectral fusion by Outer Product Analysis (OPA) to improve predictions of soil organic C. Geoderma. 335:35-46. URL https://doi.org/10.1016/j.geoderma.2018.08.005.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p>
 *
 * <pre> -first &lt;regexp&gt;
 *  The first attribute range to use.
 *  (default: amplitude-.*)</pre>
 *
 * <pre> -second &lt;regexp&gt;
 *  The second attribute range to use.
 *  (default: amplitude-.*)</pre>
 *
 * <pre> -prefix &lt;string&gt;
 *  The prefix to use for the generated attributes.
 *  (default: opa-)</pre>
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
 */
public class OuterProductAnalysis
  extends SimpleStreamFilter
  implements UnsupervisedFilter, TechnicalInformationHandler {

  /** for serialization. */
  private static final long serialVersionUID = -4180301757935955561L;

  public static final String DEFAULT_REGEXP = "amplitude-.*";

  public static final String DEFAULT_PREFIX = "opa-";

  /** the first range of the attributes to use. */
  protected BaseRegExp m_FirstRange = new BaseRegExp(DEFAULT_REGEXP);

  /** the second range of the attributes to use. */
  protected BaseRegExp m_SecondRange = new BaseRegExp(DEFAULT_REGEXP);

  /** the prefix to use for the generated attributes. */
  protected String m_AttributePrefix = DEFAULT_PREFIX;

  /** the positions of the first range. */
  protected int[] m_First;

  /** the positions of the second range. */
  protected int[] m_Second;

  /** the new positions for the attributes. */
  protected int[] m_Positions;

  /** the types of the attributes. */
  protected int[] m_Types;

  /**
   * Returns a string describing this classifier.
   *
   * @return      a description of the classifier suitable for
   *              displaying in the explorer/experimenter gui
   */
  @Override
  public String globalInfo() {
    return
	"Performs Outer Product Analysis (OPA).\n\n"
      + "For more information, see:\n"
      + getTechnicalInformation();
  }

  /**
   * Returns an instance of a TechnicalInformation object, containing
   * detailed information about the technical background of this class,
   * e.g., paper reference or book this class is based on.
   *
   * @return the technical information about this class
   */
  @Override
  public TechnicalInformation getTechnicalInformation() {
    TechnicalInformation result;

    result = new TechnicalInformation(Type.ARTICLE);
    result.setValue(Field.TITLE, "Spectral fusion by Outer Product Analysis (OPA) to improve predictions of soil organic C");
    result.setValue(Field.AUTHOR, "Fabricio S.Terra, Raphael A.Viscarra Rossel, Jose A.M.Dematte");
    result.setValue(Field.JOURNAL, "Geoderma");
    result.setValue(Field.VOLUME, "335");
    result.setValue(Field.PAGES, "35-46");
    result.setValue(Field.YEAR, "2019");
    result.setValue(Field.URL, "https://doi.org/10.1016/j.geoderma.2018.08.005");

    return result;
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  @Override
  public Enumeration<Option> listOptions() {
    Vector<Option> result = new Vector<>();

    result.addElement(new Option(
      "\tThe first attribute range to use.\n"
	+ "\t(default: " + DEFAULT_REGEXP + ")",
      "first", 1, "-first <regexp>"));

    result.addElement(new Option(
      "\tThe second attribute range to use.\n"
	+ "\t(default: " + DEFAULT_REGEXP + ")",
      "second", 1, "-second <regexp>"));

    result.addElement(new Option(
      "\tThe prefix to use for the generated attributes.\n"
	+ "\t(default: " + DEFAULT_PREFIX + ")",
      "prefix", 1, "-prefix <string>"));

    result.addAll(Collections.list(super.listOptions()));

    return result.elements();
  }


  /**
   * Parses a given list of options.
   *
   * @param options the list of options as an array of strings
   * @throws Exception if an option is not supported
   */
  @Override
  public void setOptions(String[] options) throws Exception {
    String 	tmpStr;
    BaseRegExp  regexp;

    regexp = new BaseRegExp();

    tmpStr = Utils.getOption("first", options);
    if (!tmpStr.isEmpty() && regexp.isValid(tmpStr))
      setFirstRange(new BaseRegExp(tmpStr));
    else
      setFirstRange(new BaseRegExp(DEFAULT_REGEXP));

    tmpStr = Utils.getOption("second", options);
    if (!tmpStr.isEmpty() && regexp.isValid(tmpStr))
      setSecondRange(new BaseRegExp(tmpStr));
    else
      setSecondRange(new BaseRegExp(DEFAULT_REGEXP));

    tmpStr = Utils.getOption("prefix", options);
    setAttributePrefix(tmpStr);

    super.setOptions(options);

    Utils.checkForRemainingOptions(options);
  }

  /**
   * Gets the current settings of the Classifier.
   *
   * @return an array of strings suitable for passing to setOptions
   */
  @Override
  public String[] getOptions() {
    List<String> result = new ArrayList<>();

    result.add("-first");
    result.add(getFirstRange().getValue());

    result.add("-second");
    result.add(getSecondRange().getValue());

    result.add("-prefix");
    result.add(getAttributePrefix());

    Collections.addAll(result, super.getOptions());

    return result.toArray(new String[result.size()]);
  }

  /**
   * Sets the first attribute range to use (regular expression on attribute names).
   *
   * @param value 	the expression
   */
  public void setFirstRange(BaseRegExp value) {
    m_FirstRange = value;
    reset();
  }

  /**
   * Returns the first attribute range to use (regular expression on attribute names).
   *
   * @return 		the expression
   */
  public BaseRegExp getFirstRange() {
    return m_FirstRange;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String firstRangeTipText() {
    return "The first range of attributes (regular expression on attribute names)";
  }

  /**
   * Sets the prefix to use for the generated attributes.
   *
   * @param value 	the expression
   */
  public void setSecondRange(BaseRegExp value) {
    m_SecondRange = value;
    reset();
  }

  /**
   * Returns the second attribute range to use (regular expression on attribute names).
   *
   * @return 		the expression
   */
  public BaseRegExp getSecondRange() {
    return m_SecondRange;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String secondRangeTipText() {
    return "The second range of attributes (regular expression on attribute names)";
  }

  /**
   * Sets the second attribute range to use (regular expression on attribute names).
   *
   * @param value 	the prefix
   */
  public void setAttributePrefix(String value) {
    m_AttributePrefix = value;
    reset();
  }

  /**
   * Returns the prefix to use for the generated attributes.
   *
   * @return 		the prefix
   */
  public String getAttributePrefix() {
    return m_AttributePrefix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String attributePrefixTipText() {
    return "The prefix to use for the generated attributes.";
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
    Capabilities 	result;

    result = new Capabilities(this);

    // attributes
    result.disableAllAttributes();
    result.enable(Capability.MISSING_VALUES);
    result.enable(Capability.NUMERIC_ATTRIBUTES);
    result.enable(Capability.DATE_ATTRIBUTES);
    result.enable(Capability.NOMINAL_ATTRIBUTES);
    result.enable(Capability.STRING_ATTRIBUTES);

    // classes
    result.disableAllClasses();
    result.enable(Capability.NO_CLASS);
    result.enable(Capability.MISSING_CLASS_VALUES);
    result.enable(Capability.NUMERIC_CLASS);
    result.enable(Capability.DATE_CLASS);
    result.enable(Capability.NOMINAL_CLASS);

    result.setMinimumNumberInstances(0);

    return result;
  }

  /**
   * Determines the output format based on the input format and returns
   * this. In case the output format cannot be returned immediately, i.e.,
   * hasImmediateOutputFormat() returns false, then this method will called
   * from batchFinished() after the call of preprocess(Instances), in which,
   * e.g., statistics for the actual processing step can be gathered.
   *
   * @param inputFormat     the input format to base the output format on
   * @return                the output format
   * @throws Exception      in case the determination goes wrong
   */
  @Override
  protected Instances determineOutputFormat(Instances inputFormat) throws Exception {
    Instances			result;
    TIntList 			positions;
    TIntList			types;
    TIntList			first;
    TIntList			second;
    ArrayList<Attribute>	atts;
    int				i;
    String			name;
    int				clsIndex;
    boolean			numeric;
    boolean			match;

    positions = new TIntArrayList();
    types     = new TIntArrayList();
    first     = new TIntArrayList();
    second    = new TIntArrayList();
    clsIndex  = inputFormat.classIndex();
    atts      = new ArrayList<>();
    for (i = 0; i < inputFormat.numAttributes(); i++) {
      if (i == inputFormat.classIndex())
        continue;
      name    = inputFormat.attribute(i).name();
      numeric = inputFormat.attribute(i).isNumeric();
      match   = false;
      if (numeric && m_FirstRange.isMatch(name)) {
	first.add(i);
	match = true;
      }
      if (numeric && m_SecondRange.isMatch(name)) {
	second.add(i);
	match = true;
      }
      if (!match) {
        positions.add(i);
	types.add(inputFormat.attribute(i).type());
	atts.add((Attribute) inputFormat.attribute(i).copy());
      }
    }

    if (first.size() == 0)
      throw new IllegalStateException("No attribute names of numeric attributes matched the first range expression: " + m_FirstRange);
    if (second.size() == 0)
      throw new IllegalStateException("No attribute names of numeric attributes matched the second range expression: " + m_SecondRange);

    for (i = 0; i < first.size() * second.size(); i++)
      atts.add(new Attribute(m_AttributePrefix + (i + 1)));

    if (clsIndex > -1)
      atts.add((Attribute) inputFormat.classAttribute().copy());

    result      = new Instances(inputFormat + "-opa", atts, 0);
    m_Positions = positions.toArray();
    m_Types     = types.toArray();
    m_First     = first.toArray();
    m_Second    = second.toArray();

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
    Instances	header;
    double[]	values;
    double[]	old;
    int		x;
    int		y;
    int		i;

    header = getOutputFormat();
    values = new double[header.numAttributes()];
    Arrays.fill(values, Utils.missingValue());

    // old attributes
    for (i = 0; i < m_Positions.length; i++) {
      switch (m_Types[i]) {
	case Attribute.NUMERIC:
	case Attribute.DATE:
	case Attribute.NOMINAL:
	  values[i] = instance.value(m_Positions[i]);
	  break;
	case Attribute.STRING:
	  values[i] = header.attribute(i).addStringValue(instance.stringValue(m_Positions[i]));
	  break;
	default:
	  throw new IllegalStateException("Unhandled attribute type at original position " + (m_Positions[i]) + ": " + Attribute.typeToString(m_Types[i]));
      }
    }

    // matrix
    old = instance.toDoubleArray();
    for (y = 0; y < m_Second.length; y++) {
      for (x = 0; x < m_First.length; x++)
        values[m_Positions.length + m_First.length*y + x] = old[m_First[x]] * old[m_Second[y]];
    }

    // class?
    if (instance.classIndex() > -1)
      values[values.length - 1] = instance.classValue();

    result = new DenseInstance(instance.weight(), values);
    result.setDataset(header);

    copyValues(result, false, instance.dataset(), getOutputFormat());

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
    runFilter(new OuterProductAnalysis(), args);
  }
}
