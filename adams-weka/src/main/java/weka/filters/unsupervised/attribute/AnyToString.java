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
 * AnyToString.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package weka.filters.unsupervised.attribute;

import adams.data.weka.WekaAttributeRange;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import weka.core.Attribute;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.RevisionUtils;
import weka.core.Utils;
import weka.filters.SimpleStreamFilter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

/**
 * Turns the selected range of attributes into string ones.
 * For numeric attributes, it just uses the Java functionality to turn a number into a string.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class AnyToString
  extends SimpleStreamFilter {

  private static final long serialVersionUID = -183001278236751069L;

  /** the attribute range to process. */
  protected WekaAttributeRange m_Range = new WekaAttributeRange(WekaAttributeRange.FIRST);

  /** the attribute indices to work on. */
  protected TIntSet m_Indices;

  /**
   * Returns a string describing this filter.
   *
   * @return a description of the filter suitable for displaying in the
   * explorer/experimenter gui
   */
  @Override
  public String globalInfo() {
    return "Turns the selected range of attributes into string ones.\n"
      + "For numeric attributes, it just uses the Java functionality to turn a number into a string.";
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
      "\tThe attribute range to process.\n"
	+ "\t(default: first)",
      "R", 1, "-R <range specification>"));

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

    tmpStr = Utils.getOption("R", options);
    if (!tmpStr.isEmpty())
      setRange(new WekaAttributeRange(tmpStr));
    else
      setRange(new WekaAttributeRange(WekaAttributeRange.LAST));

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

    result.add("-R");
    result.add(getRange().getRange());

    Collections.addAll(result, super.getOptions());

    return result.toArray(new String[result.size()]);
  }

  /**
   * Sets the first attribute range to use (regular expression on attribute names).
   *
   * @param value 	the expression
   */
  public void setRange(WekaAttributeRange value) {
    m_Range = value;
    reset();
  }

  /**
   * Returns the first attribute range to use (regular expression on attribute names).
   *
   * @return 		the expression
   */
  public WekaAttributeRange getRange() {
    return m_Range;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String rangeTipText() {
    return "The range of attributes to process";
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
    Capabilities result;

    result = new Capabilities(this);

    result.enableAllAttributes();
    result.enable(Capability.MISSING_VALUES);

    result.enableAllClasses();
    result.enable(Capability.MISSING_CLASS_VALUES);
    result.enable(Capability.NO_CLASS);

    result.setMinimumNumberInstances(0);

    return result;
  }

  /**
   * resets the filter, i.e., m_NewBatch to true and m_FirstBatchDone to false.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Indices = null;
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
   * @see #hasImmediateOutputFormat()
   * @see #batchFinished()
   * @see #preprocess(Instances)
   */
  @Override
  protected Instances determineOutputFormat(Instances inputFormat) throws Exception {
    Instances			result;
    ArrayList<Attribute>	atts;
    int				i;
    Attribute			att;

    m_Range.setData(inputFormat);
    m_Indices = new TIntHashSet(m_Range.getIntIndices());
    atts = new ArrayList<>();
    for (i = 0; i < inputFormat.numAttributes(); i++) {
      att = inputFormat.attribute(i);
      if (m_Indices.contains(i))
        atts.add(new Attribute(att.name(), (List) null));
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
    String	value;
    int		i;

    values = instance.toDoubleArray();

    // values to convert
    for (int index: m_Indices.toArray()) {
      switch (instance.attribute(index).type()) {
	case Attribute.NUMERIC:
	  value = "" + values[index];
	  break;
	case Attribute.DATE:
	case Attribute.NOMINAL:
	case Attribute.STRING:
	case Attribute.RELATIONAL:
	  value = instance.stringValue(index);
	  break;
	default:
	  throw new IllegalStateException("Unhandled attribute type: " + Attribute.typeToString(instance.attribute(index)));
      }
      values[index] = outputFormatPeek().attribute(index).addStringValue(value);
    }

    // other string values
    for (i = 0; i < instance.numAttributes(); i++) {
      if (m_Indices.contains(i))
        continue;
      if (!instance.attribute(i).isString())
        continue;
      value = instance.stringValue(i);
      values[i] = outputFormatPeek().attribute(i).addStringValue(value);

    }

    result = instance.copy(values);
    result.setDataset(outputFormatPeek());

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
   * @param args 	should contain arguments to the filter: use -h for help
   */
  public static void main(String [] args) {
    runFilter(new AnyToString(), args);
  }
}
