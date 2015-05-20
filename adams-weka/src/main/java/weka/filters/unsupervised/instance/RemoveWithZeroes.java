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
 * RemoveWithZeroes.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */

package weka.filters.unsupervised.instance;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Vector;

import weka.core.Capabilities;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.RevisionUtils;
import weka.core.Utils;
import weka.core.Capabilities.Capability;
import weka.filters.SimpleBatchFilter;
import weka.filters.UnsupervisedFilter;

/**
 <!-- globalinfo-start -->
 * Removes all instances that contain at least the specified number (or percentage) of zeroes in numeric attributes.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 *
 * <pre> -num-zeroes &lt;number of zeroes or percentage&gt;
 *  The number of zeroes that an instance must at least contain in
 *  order to be removed. If the number is between 0 and 1, it is
 *  interpreted as percentage.
 * </pre>
 *
 * <pre> -D
 *  Turns on output of debugging information.</pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RemoveWithZeroes
  extends SimpleBatchFilter
  implements UnsupervisedFilter {

  /** for serialization. */
  private static final long serialVersionUID = -6784901276150528252L;

  /** the number (or percentage) of zeroes that the row must contain to be removed. */
  protected double m_NumZeroes = 1.0;

  /** the number of numeric attributes in the dataset. */
  protected int m_NumNumericAttributes;

  /** the minimum number of zeroes that a row must have. */
  protected int m_MinZeroes;

  /**
   * Returns a string describing this classifier.
   *
   * @return      a description of the classifier suitable for
   *              displaying in the explorer/experimenter gui
   */
  public String globalInfo() {
    return
        "Removes all instances that contain at least the specified number "
      + "(or percentage) of zeroes in numeric attributes.";
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  public Enumeration listOptions() {
    Vector	result;
    Enumeration	enm;

    result = new Vector();

    result.addElement(new Option(
	"\tThe number of zeroes that an instance must at least contain in\n"
	+ "\torder to be removed. If the number is between 0 and 1, it is \n"
	+ "\tinterpreted as percentage.\n",
	"num-zeroes", 1, "-num-zeroes <number of zeroes or percentage>"));

    enm = super.listOptions();
    while (enm.hasMoreElements())
      result.add(enm.nextElement());

    return result.elements();
  }

  /**
   * Parses a list of options for this object.
   *
   * @param options the list of options as an array of strings
   * @throws Exception if an option is not supported
   */
  public void setOptions(String[] options) throws Exception {
    String	tmpStr;

    tmpStr = Utils.getOption("num-zeroes", options);
    if (tmpStr.length() == 0)
      setNumZeroes(1.0);
    else
      setNumZeroes(Double.parseDouble(tmpStr));

    super.setOptions(options);
  }

  /**
   * Gets the current settings of the filter.
   *
   * @return an array of strings suitable for passing to setOptions
   */
  public String[] getOptions() {
    Vector<String>	result;

    result = new Vector<String>();

    result.add("-num-zeroes");
    result.add("" + getNumZeroes());

    result.addAll(Arrays.asList(super.getOptions()));

    return result.toArray(new String[result.size()]);
  }

  /**
   * Sets the number of zeroes a row must have at least in order to be removed.
   * 0-1 is interpreted as percentage.
   *
   * @param value     the number of zeroes or percentage
   */
  public void setNumZeroes(double value) {
    if ((m_NumZeroes > 0) && (m_NumZeroes < 1))
      m_NumZeroes = value;
    else if ((m_NumZeroes >= 1) && (Math.floor(m_NumZeroes)) == Math.ceil(m_NumZeroes))
      m_NumZeroes = value;
    else
      System.err.println(
	  "Number of zeroes must be >0 and values >1 must be integers, provided: " + value);
  }

  /**
   * Returns the number of zeroes a row must have at least in order to be
   * removed. 0-1 is interpreted as percentage.
   *
   * @return		the number of zeroes or percentage
   */
  public double getNumZeroes() {
    return m_NumZeroes;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return    tip text for this property suitable for
   *            displaying in the explorer/experimenter gui
   */
  public String numZeroesTipText() {
    return
        "The number of zeroes that a row must have in order to be removed; "
      + "0-1 is interpreted as percentage.";
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
   * Resets the filter.
   */
  protected void reset() {
    super.reset();

    m_NumNumericAttributes = 0;
    m_MinZeroes            = Integer.MAX_VALUE;
  }

  /**
   * Determines the output format based on the input format and returns
   * this.
   *
   * @param inputFormat     the input format to base the output format on
   * @return                the output format
   * @throws Exception      in case the determination goes wrong
   */
  protected Instances determineOutputFormat(Instances inputFormat) throws Exception {
    int		i;

    m_NumNumericAttributes = 0;
    for (i = 0; i < inputFormat.numAttributes(); i++) {
      if (inputFormat.attribute(i).isNumeric())
	m_NumNumericAttributes++;
    }

    if (m_NumZeroes < 1)
      m_MinZeroes = (int) Math.round((double) m_NumNumericAttributes * m_NumZeroes);
    else
      m_MinZeroes = (int) m_NumZeroes;

    return new Instances(inputFormat, 0);
  }

  /**
   * Processes the given data (may change the provided dataset) and returns
   * the modified version. This method is called in batchFinished().
   *
   * @param instances   the data to process
   * @return            the modified data
   * @throws Exception  in case the processing goes wrong
   */
  protected Instances process(Instances instances) throws Exception {
    Instances	result;
    Instance	inst;
    int		i;
    int		n;
    int		numZeroes;

    // only first batch will get processed
    if (m_FirstBatchDone)
      return new Instances(instances);

    result = new Instances(instances, instances.numInstances());

    // only copy instance objects with no missing values at all
    for (i = 0; i < instances.numInstances(); i++) {
      inst      = instances.instance(i);
      numZeroes = 0;
      for (n = 0; n < inst.numAttributes(); n++) {
	if (inst.attribute(n).isNumeric() && (inst.value(n) == 0))
	  numZeroes++;
      }
      if (numZeroes < m_MinZeroes) {
	result.add((Instance) inst.copy());
      }
      else if (m_Debug) {
	System.out.println(
	    "Instance #" + (i+1) + " contains more than " + m_NumZeroes
	    + ((m_NumZeroes < 1) ? " (" + m_MinZeroes + ")" : "") + " zeroes.");
      }
    }

    result.compactify();
    if (m_Debug)
      System.out.println("Reduction: " + instances.numInstances() + " -> " + result.numInstances());

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
   * @param args should contain arguments to the filter: use -h for help
   */
  public static void main(String [] args) {
    runFilter(new RemoveWithZeroes(), args);
  }
}
