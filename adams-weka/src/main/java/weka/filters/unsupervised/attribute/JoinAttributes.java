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
 * JoinAttributes.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package weka.filters.unsupervised.attribute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Vector;

import weka.core.Attribute;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.Range;
import weka.core.RevisionUtils;
import weka.core.SparseInstance;
import weka.core.Utils;
import weka.filters.SimpleBatchFilter;
import weka.filters.UnsupervisedFilter;

/**
 <!-- globalinfo-start -->
 * A simple filter that joins several attributes into a single STRING one, with a user defined string acting as 'glue'.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre> -D
 *  Turns on output of debugging information.</pre>
 * 
 * <pre> -R &lt;range&gt;
 *  The range of the attributes to combine.
 *  (default: first-last).</pre>
 * 
 * <pre> -glue &lt;string&gt;
 *  The 'glue' string to insert between the attribute values.
 *  (default: none).</pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class JoinAttributes
  extends SimpleBatchFilter
  implements UnsupervisedFilter {

  /** for serialization. */
  private static final long serialVersionUID = -4180301757935955561L;

  /** the range of the attributes to work on. */
  protected Range m_AttributeRange = new Range("first-last");

  /** the glue to use for joining the attributes. */
  protected String m_Glue = "";
  
  /** the indices to work on. */
  protected int[] m_Indices = new int[0];

  /** the hashed indices. */
  protected HashSet<Integer> m_Hashed = new HashSet<Integer>();

  /**
   * Returns a string describing this classifier.
   *
   * @return      a description of the classifier suitable for
   *              displaying in the explorer/experimenter gui
   */
  @Override
  public String globalInfo() {
    return 
	"A simple filter that joins several attributes into a single STRING "
	+ "one, with a user defined string acting as 'glue'.";
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
	"\tThe range of the attributes to combine.\n"
	+ "\t(default: first-last).",
	"R", 1, "-R <range>"));

    result.addElement(new Option(
	"\tThe 'glue' string to insert between the attribute values.\n"
	+ "\t(default: none).",
	"glue", 1, "-glue <string>"));

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

    tmpStr = Utils.getOption("R", options);
    if (tmpStr.length() > 0)
      setAttributeRange(tmpStr);
    else
      setAttributeRange("first-last");

    tmpStr = Utils.getOption("glue", options);
    if (tmpStr.length() > 0)
      setGlue(tmpStr);
    else
      setGlue("");

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

    result.add("-R");
    result.add(getAttributeRange());

    result.add("-glue");
    result.add(getGlue());

    return result.toArray(new String[result.size()]);
  }

  /**
   * Sets the range (1-based) of the attributes to combine.
   *
   * @param value 	the range (1-based)
   */
  public void setAttributeRange(String value) {
    m_AttributeRange.setRanges(value);
    reset();
  }

  /**
   * Returns the 1-based range of the attributes to combine.
   *
   * @return 		the range (1-based)
   */
  public String getAttributeRange() {
    return m_AttributeRange.getRanges();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String attributeRangeTipText() {
    return "The range of attributes to combine (1-based); 'first' and 'last' are accepted as well.";
  }

  /**
   * Sets the glue to use. Tab character can be provided as escaped sequence ("\t").
   *
   * @param value 	the glue
   * @see		Utils#unbackQuoteChars(String)
   */
  public void setGlue(String value) {
    m_Glue = Utils.unbackQuoteChars(value);
    reset();
  }

  /**
   * Returns the glue to use.
   *
   * @return 		the glue
   * @see		Utils#backQuoteChars(String)
   */
  public String getGlue() {
    return Utils.backQuoteChars(m_Glue);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String glueTipText() {
    return "The glue to insert between the attribute values; tabs can be written as escaped sequence ('\t').";
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
    result.disable(Capability.RELATIONAL_ATTRIBUTES);

    // classes
    result.enableAllClasses();
    result.enable(Capability.NO_CLASS);
    result.enable(Capability.MISSING_CLASS_VALUES);
    result.disable(Capability.RELATIONAL_CLASS);

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
    String			newName;

    m_AttributeRange.setUpper(inputFormat.numAttributes() - 1);
    m_Indices = m_AttributeRange.getSelection();
    if (m_Indices.length == 0)
      throw new IllegalStateException("No attributes selected!");

    // combined name
    m_Hashed  = new HashSet<Integer>();
    newName = "";
    for (i = 0; i < m_Indices.length; i++) {
      m_Hashed.add(m_Indices[i]);
      if (i == 0)
	newName = inputFormat.attribute(m_Indices[i]).name();
      else
	newName += m_Glue + inputFormat.attribute(m_Indices[i]).name();
    }

    // create new header
    atts = new ArrayList<Attribute>();
    for (i = 0; i < inputFormat.numAttributes(); i++) {
      if (i == m_Indices[0])
	atts.add(new Attribute(newName, (FastVector) null));
      if (m_Hashed.contains(i))
	continue;
      atts.add((Attribute) inputFormat.attribute(i).copy());
    }
    result = new Instances(inputFormat.relationName(), atts, 0);

    // class attribute
    if (inputFormat.classIndex() != -1) {
      if (result.attribute(inputFormat.classAttribute().name()) != null)
	result.setClassIndex(result.attribute(inputFormat.classAttribute().name()).index());
    }

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
    Instances	result;
    Instance	inst;
    double[]	valuesOld;
    double[]	valuesNew;
    int		i;
    int		n;
    String	valueNew;
    String	part;

    result = getOutputFormat();
    for (Instance instance: instances) {
      valuesOld = instance.toDoubleArray();
      valuesNew = new double[outputFormatPeek().numAttributes()];

      // combined value
      valueNew = "";
      for (i = 0; i < m_Indices.length; i++) {
	if (instance.attribute(m_Indices[i]).isNumeric())
	  part = "" + instance.value(m_Indices[i]);
	else
	  part = instance.stringValue(m_Indices[i]);
	if (i == 0)
	  valueNew = part;
	else
	  valueNew += m_Glue + part;
      }

      // create new array
      n = 0;
      for (i = 0; i < valuesOld.length; i++) {
	if (i == m_Indices[0]) {
	  valuesNew[n] = result.attribute(n).addStringValue(valueNew);
	  n++;
	}
	else {
	  if (instance.attribute(i).isString())
	    valuesNew[n] = result.attribute(n).addStringValue(instance.stringValue(i));
	  else
	    valuesNew[n] = valuesOld[i];
	}
	if (m_Hashed.contains(i))
	  continue;
	n++;
      }

      // create instance
      if (instance instanceof SparseInstance)
	inst = new SparseInstance(instance.weight(), valuesNew);
      else
	inst = new DenseInstance(instance.weight(), valuesNew);
      result.add(inst);
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
    runFilter(new JoinAttributes(), args);
  }
}
