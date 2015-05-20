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
 * SpellChecker.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */

package weka.filters.unsupervised.attribute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Vector;

import weka.core.Attribute;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.RevisionUtils;
import weka.core.SingleIndex;
import weka.core.SparseInstance;
import weka.core.Utils;
import weka.filters.SimpleStreamFilter;
import weka.filters.UnsupervisedFilter;

/**
 <!-- globalinfo-start -->
 * A simple filter that merges misspelled labels into a single correct one.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 *
 * <pre> -D
 *  Turns on output of debugging information.</pre>
 *
 * <pre> -C &lt;col&gt;
 *  The index of the attribute to process.
 *  (default: last).</pre>
 *
 * <pre> -incorrect &lt;blank separated labels&gt;
 *  The incorrectly spelled labels.
 *  (default: none).</pre>
 *
 * <pre> -correct &lt;label&gt;
 *  The correct spelling for the labels.
 *  (default: correct).</pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpellChecker
  extends SimpleStreamFilter
  implements UnsupervisedFilter {

  /** for serialization. */
  private static final long serialVersionUID = 5944266872914953692L;

  /** the index of the attribute to work on. */
  protected SingleIndex m_AttributeIndex = new SingleIndex("last");

  /** the (misspelled) labels of the attribute to replace. */
  protected String[] m_Incorrect = new String[0];

  /** the correct spelling for the labels. */
  protected String m_Correct = "";

  /** the hashset with the incorret labels (for faster access). */
  protected HashSet<String> m_IncorrectCache;

  /**
   * Returns a string describing this classifier.
   *
   * @return      a description of the classifier suitable for
   *              displaying in the explorer/experimenter gui
   */
  @Override
  public String globalInfo() {
    return "A simple filter that merges misspelled labels into a single correct one.";
  }

  /**
   * resets the filter.
   */
  @Override
  protected void reset() {
    super.reset();

    m_IncorrectCache = new HashSet<String>();
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return 		an enumeration of all the available options.
   */
  @Override
  public Enumeration listOptions() {
    Vector 	result;
    Enumeration	enm;

    result = new Vector();

    enm = super.listOptions();
    while (enm.hasMoreElements())
      result.add(enm.nextElement());

    result.addElement(new Option(
	"\tThe index of the attribute to process.\n"
	+ "\t(default: last).",
	"C", 1, "-C <col>"));

    result.addElement(new Option(
	"\tThe incorrectly spelled labels.\n"
	+ "\t(default: none).",
	"incorrect", 1, "-incorrect <blank separated labels>"));

    result.addElement(new Option(
	"\tThe correct spelling for the labels.\n"
	+ "\t(default: correct).",
	"correct", 1, "-correct <label>"));

    return result.elements();
  }

  /**
   * Parses a list of options for this object.
   * Also resets the state of the filter (this reset doesn't affect the
   * options).
   *
   * @param options 	the list of options as an array of strings
   * @throws Exception 	if an option is not supported
   * @see    		#reset()
   */
  @Override
  public void setOptions(String[] options) throws Exception {
    String	tmpStr;

    reset();

    tmpStr = Utils.getOption("C", options);
    if (tmpStr.length() > 0)
      setAttributeIndex(tmpStr);
    else
      setAttributeIndex("last");

    tmpStr = Utils.getOption("incorrect", options);
    if (tmpStr.length() > 0)
      setIncorrect(tmpStr);
    else
      setIncorrect("");

    tmpStr = Utils.getOption("correct", options);
    if (tmpStr.length() > 0)
      setCorrect(tmpStr);
    else
      setCorrect("correct");

    super.setOptions(options);
  }

  /**
   * Gets the current settings of the filter.
   *
   * @return 		an array of strings suitable for passing to setOptions
   */
  @Override
  public String[] getOptions() {
    Vector<String>	result;

    result = new Vector<String>(Arrays.asList(super.getOptions()));

    result.add("-C");
    result.add(getAttributeIndex());

    result.add("-incorrect");
    result.add(getIncorrect());

    result.add("-correct");
    result.add(getCorrect());

    return result.toArray(new String[result.size()]);
  }

  /**
   * Sets the attribute index (1-based) of the attribute to process.
   *
   * @param value 	the index (1-based)
   */
  public void setAttributeIndex(String value) {
    m_AttributeIndex.setSingleIndex(value);
    reset();
  }

  /**
   * Returns the 1-based index of the attribute to process.
   *
   * @return 		the index (1-based)
   */
  public String getAttributeIndex() {
    return m_AttributeIndex.getSingleIndex();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String attributeIndexTipText() {
    return "The 1-based index of the attribute to process; 'first' and 'last' are accepted as well.";
  }

  /**
   * Sets the incorrect labels, blank-separated list.
   *
   * @param value 	the labels
   */
  public void setIncorrect(String value) throws Exception {
    m_Incorrect = Utils.splitOptions(value);
    reset();
  }

  /**
   * Returns the incorrect labels, blank-separated list.
   *
   * @return 		the labels
   */
  public String getIncorrect() {
    return Utils.joinOptions(m_Incorrect);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String incorrectTipText() {
    return "The incorrect labels that get replaced by a single correct one (blank-separated list).";
  }

  /**
   * Sets the correct label.
   *
   * @param value 	the label
   */
  public void setCorrect(String value) {
    m_Correct = value;
    reset();
  }

  /**
   * Returns the correct label.
   *
   * @return 		the label
   */
  public String getCorrect() {
    return m_Correct;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String correctTipText() {
    return "The correct label replacing the incorrect ones.";
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
    result.enableAllAttributes();
    result.enable(Capability.MISSING_VALUES);

    // classes
    result.enableAllClasses();
    result.enable(Capability.NO_CLASS);
    result.enable(Capability.MISSING_CLASS_VALUES);

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
    ArrayList<Attribute>	atts;
    int				i;
    ArrayList<String>		labels;
    Attribute			attOld;
    Attribute			attNew;
    int				index;

    // some checks
    if (m_Incorrect.length == 0)
      throw new IllegalStateException("No incorrect labels provided!");
    for (i = 0; i < m_Incorrect.length; i++) {
      if (m_Incorrect[i].length() == 0)
	throw new IllegalStateException("Incorrect label #" + (i+1) + " has length 0!");
    }
    if (m_Correct.length() == 0)
      throw new IllegalStateException("Correct label has length 0!");

    m_AttributeIndex.setUpper(inputFormat.numAttributes() - 1);
    index = m_AttributeIndex.getIndex();
    if (!inputFormat.attribute(index).isNominal())
      throw new IllegalStateException("Attribute #" + m_AttributeIndex.getSingleIndex() + " is not nominal!");

    // create spell-checked attribute
    m_IncorrectCache = new HashSet<String>(Arrays.asList(m_Incorrect));
    attOld           = inputFormat.attribute(index);
    labels           = new ArrayList<String>();
    labels.add(m_Correct);
    for (i = 0; i < attOld.numValues(); i++) {
      if (m_IncorrectCache.contains(attOld.value(i)))
	continue;
      if (labels.contains(attOld.value(i)))
	continue;
      labels.add(attOld.value(i));
    }
    Collections.sort(labels);
    attNew = new Attribute(attOld.name(), labels);

    // create new attributes
    atts  = new ArrayList<Attribute>();
    for (i = 0; i < inputFormat.numAttributes(); i++) {
      if (i == index)
	atts.add(attNew);
      else
	atts.add((Attribute) inputFormat.attribute(i).copy());
    }

    // create new dataset
    result = new Instances(inputFormat.relationName(), atts, 0);
    result.setClassIndex(inputFormat.classIndex());

    return result;
  }

  /**
   * processes the given instance (may change the provided instance) and
   * returns the modified version.
   *
   * @param instance    the instance to process
   * @return            the modified data
   * @throws Exception  in case the processing goes wrong
   */
  @Override
  protected Instance process(Instance instance) throws Exception {
    Instance	result;
    double[]	values;
    int		index;
    String	label;

    index  = m_AttributeIndex.getIndex();
    if (instance.isMissing(index)) {
      result = (Instance) instance.copy();
    }
    else {
      values = instance.toDoubleArray();
      label  = instance.stringValue(index);
      if (m_IncorrectCache.contains(label))
	values[index] = outputFormatPeek().attribute(index).indexOfValue(m_Correct);
      else
	values[index] = outputFormatPeek().attribute(index).indexOfValue(label);
      if (instance instanceof SparseInstance)
	result = new SparseInstance(instance.weight(), values);
      else
	result = new DenseInstance(instance.weight(), values);
    }

    copyValues(instance, false, instance.dataset(), getOutputFormat());

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
    runFilter(new SpellChecker(), args);
  }
}
