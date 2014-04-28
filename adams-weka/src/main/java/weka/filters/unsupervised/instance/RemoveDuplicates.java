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
 * RemoveDuplicates.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package weka.filters.unsupervised.instance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;

import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.InstanceComparator;
import weka.core.Instances;
import weka.core.Option;
import weka.core.Randomizable;
import weka.core.RevisionUtils;
import weka.core.Utils;
import weka.filters.SimpleBatchFilter;
import weka.filters.UnsupervisedFilter;

/**
 <!-- globalinfo-start -->
 * Removes all duplicate instances.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre> -include-class
 *  Whether to include the class attribute in the comparison as well.
 * </pre>
 *
 * <pre> -randomize
 *  Whether to randomize the data after the removal process.
 * </pre>
 *
 * <pre> -S &lt;int&gt;
 *  Specifies the seed value for randomization.
 *  (default: 42)
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RemoveDuplicates
  extends SimpleBatchFilter
  implements UnsupervisedFilter, Randomizable {

  /** for serialization. */
  private static final long serialVersionUID = -7024951985782351356L;

  /** whether to take the class into account. */
  protected boolean m_IncludeClass = false;

  /** whether to randomize the data after the removal. */
  protected boolean m_Randomize = false;

  /** the seed value for the randomization. */
  protected int m_Seed = 42;

  /**
   * Returns a string describing this classifier.
   *
   * @return      a description of the classifier suitable for
   *              displaying in the explorer/experimenter gui
   */
  public String globalInfo() {
    return
        "Removes all duplicate instances.";
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  public Enumeration listOptions() {
    Vector 	result;

    result = new Vector();

    result.addElement(new Option(
	      "\tWhether to include the class attribute in the comparison as well.\n",
	      "include-class", 0, "-include-class"));

    result.addElement(new Option(
	      "\tWhether to randomize the data after the removal process.\n",
	      "randomize", 0, "-randomize"));

    result.addElement(new Option(
              "\tSpecifies the seed value for randomization.\n"
	      + "\t(default: 42)\n",
              "S", 1, "-S <int>"));

    return result.elements();
  }

  /**
   * Parses a given list of options. <p/>
   *
   <!-- options-start -->
   * Valid options are: <p/>
   *
   * <pre> -include-class
   *  Whether to include the class attribute in the comparison as well.
   * </pre>
   *
   * <pre> -randomize
   *  Whether to randomize the data after the removal process.
   * </pre>
   *
   * <pre> -S &lt;int&gt;
   *  Specifies the seed value for randomization.
   *  (default: 42)
   * </pre>
   *
   <!-- options-end -->
   *
   * @param options the list of options as an array of string.s
   * @throws Exception if an option is not supported.
   */
  public void setOptions(String[] options) throws Exception {
    String	tmpStr;

    setIncludeClass(Utils.getFlag("include-class", options));
    setRandomize(Utils.getFlag("randomize", options));

    tmpStr = Utils.getOption('S', options);
    if (tmpStr.length() != 0)
      setSeed(Integer.parseInt(tmpStr));
    else
      setSeed(42);

    super.setOptions(options);
  }

  /**
   * Gets the current settings of the filter.
   *
   * @return an array of strings suitable for passing to setOptions.
   */
  public String[] getOptions() {
    ArrayList<String>	result;

    result = new ArrayList<String>(Arrays.asList(super.getOptions()));

    if (getIncludeClass())
      result.add("-include-class");
    if (getRandomize()) {
      result.add("-randomize");
      result.add("-S");
      result.add("" + getSeed());
    }

    return result.toArray(new String[result.size()]);
  }

  /**
   * Sets whether to include the class attribute in the comparison.
   *
   * @param value 	if true the class attribute gets included
   */
  public void setIncludeClass(boolean value) {
    m_IncludeClass = value;
  }

  /**
   * Returns whether to include the class attribute in the comparison.
   *
   * @return 		true if the class attribute is included
   */
  public boolean getIncludeClass() {
    return m_IncludeClass;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String includeClassTipText() {
    return "If enabled, the class attribute gets taken into account when comparing instances.";
  }

  /**
   * Sets whether to include the class attribute in the comparison.
   *
   * @param value 	if true the class attribute gets included
   */
  public void setRandomize(boolean value) {
    m_Randomize = value;
  }

  /**
   * Returns whether to include the class attribute in the comparison.
   *
   * @return 		true if the class attribute is included
   */
  public boolean getRandomize() {
    return m_Randomize;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String randomizeTipText() {
    return "If enabled, the data gets randomized after the removal process.";
  }

  /**
   * Set the seed for random number generation.
   *
   * @param value 	the seed
   */
  public void setSeed(int value) {
    m_Seed = value;
  }

  /**
   * Gets the seed for the random number generations
   *
   * @return 		the seed for the random number generation
   */
  public int getSeed() {
    return m_Seed;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String seedTipText() {
    return "The seed value for randomization (if enabled).";
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
   * Determines the output format based on the input format and returns
   * this.
   *
   * @param inputFormat     the input format to base the output format on
   * @return                the output format
   * @throws Exception      in case the determination goes wrong
   */
  protected Instances determineOutputFormat(Instances inputFormat) throws Exception {
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
    Instances		result;
    InstanceComparator	comp;
    int			i;

    // sort data
    result = new Instances(instances);
    comp   = new InstanceComparator(m_IncludeClass);
    Collections.sort(result, comp);

    // remove duplicates
    i = 1;
    while (i < result.numInstances()) {
      if (comp.compare(result.instance(i - 1), result.instance(i)) == 0)
	result.delete(i);
      else
	i++;
    }
    result.compactify();

    // randomize?
    if (m_Randomize)
      result.randomize(new Random(m_Seed));

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
   * Main method for running this filter.
   *
   * @param args should contain arguments to the filter: use -h for help
   */
  public static void main(String [] args) {
    runFilter(new RemoveDuplicates(), args);
  }
}
