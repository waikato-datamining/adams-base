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
 * WeightsBasedResample.java
 * Copyright (C) 2018 University of Waikato, Hamilton, New Zealand
 */

package weka.filters.unsupervised.instance;

import gnu.trove.list.TDoubleList;
import gnu.trove.list.array.TDoubleArrayList;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.Randomizable;
import weka.core.RevisionUtils;
import weka.core.Utils;
import weka.filters.SimpleBatchFilter;
import weka.filters.UnsupervisedFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;
import java.util.Vector;

/**
 <!-- globalinfo-start -->
 * Normalizes all instance weights and drops the ones that fall below the specified threshold, but at most the specified percentage.<br>
 * Of the left over instances, the smallest weight, e.g., 0.2, represents one instance, which translates a weight of 1.0 to five instances. This factor can be limited to avoid an instance explosion if the smallest weight is very small.<br>
 * The overall, final dataset size can be limited as well.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p>
 *
 * <pre> -drop-below &lt;0.0-1.0&gt;
 *  The threshold for the (normalized) weight below which instances
 *  get dropped.
 *  default: 0.0</pre>
 *
 * <pre> -drop-at most &lt;0.0-1.0&gt;
 *  The maximum percentage of instances to drop (0-1).
 *  default: 1.0</pre>
 *
 * <pre> -max-factor &lt;num&gt;
 *  The maximum factor to allow for instances to be multiplied with.
 *  Disabled if &lt;= 0.
 *  default: -1</pre>
 *
 * <pre> -size-limit &lt;num&gt;
 *  The size limit for the resulting dataset.
 *  Disabled if &lt;= 0, percentage if 0&lt;x&lt;=10 (0-10,000%),
 *  &gt;10 absolute number of instances.
 *  default: -1</pre>
 *
 * <pre> -seed &lt;num&gt;
 *  The seed value for randomizing the final dataset.
 *  default: 1</pre>
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
public class WeightsBasedResample
  extends SimpleBatchFilter
  implements UnsupervisedFilter, Randomizable {

  /** for serialization. */
  private static final long serialVersionUID = -6784901276150528252L;

  /** the threshold of weight below which to drop instances. */
  protected double m_DropBelow = 0.0;

  /** the maximum percentage (0-1) of instances to drop. */
  protected double m_DropAtMost = 1.0;

  /** the upper limit of the multiplication factor (<= 0 is not capped). */
  protected double m_MaxFactor = -1;

  /** the maximum size of the dataset to generate (<= 0 is off, <= 10 is percentage, > 10 is absolute). */
  protected double m_SizeLimit = -1;

  /** the seed for randomizing the final dataset. */
  protected int m_Seed = 1;

  /**
   * Returns a string describing this classifier.
   *
   * @return      a description of the classifier suitable for
   *              displaying in the explorer/experimenter gui
   */
  public String globalInfo() {
    return
      "Normalizes all instance weights and drops the ones that fall below "
	+ "the specified threshold, but at most the specified percentage.\n"
	+ "Of the left over instances, the smallest weight, e.g., 0.2, represents "
	+ "one instance, which translates a weight of 1.0 to five instances. "
	+ "This factor can be limited to avoid an instance explosion if the "
	+ "smallest weight is very small.\n"
	+ "The overall, final dataset size can be limited as well.";
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
      "\tThe threshold for the (normalized) weight below which instances\n"
	+ "\tget dropped.\n"
	+ "\tdefault: 0.0",
      "drop-below", 1, "-drop-below <0.0-1.0>"));

    result.addElement(new Option(
      "\tThe maximum percentage of instances to drop (0-1).\n"
	+ "\tdefault: 1.0",
      "drop-at-most", 1, "-drop-at most <0.0-1.0>"));

    result.addElement(new Option(
      "\tThe maximum factor to allow for instances to be multiplied with.\n"
	+ "\tDisabled if <= 0.\n"
	+ "\tdefault: -1",
      "max-factor", 1, "-max-factor <num>"));

    result.addElement(new Option(
      "\tThe size limit for the resulting dataset.\n"
	+ "\tDisabled if <= 0, percentage if 0<x<=10 (0-10,000%), \n"
	+ "\t>10 absolute number of instances.\n"
	+ "\tdefault: -1",
      "size-limit", 1, "-size-limit <num>"));

    result.addElement(new Option(
      "\tThe seed value for randomizing the final dataset.\n"
	+ "\tdefault: 1",
      "seed", 1, "-seed <num>"));

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

    tmpStr = Utils.getOption("drop-below", options);
    if (tmpStr.length() == 0)
      setDropBelow(0.0);
    else
      setDropBelow(Double.parseDouble(tmpStr));

    tmpStr = Utils.getOption("drop-at-most", options);
    if (tmpStr.length() == 0)
      setDropAtMost(1.0);
    else
      setDropAtMost(Double.parseDouble(tmpStr));

    tmpStr = Utils.getOption("max-factor", options);
    if (tmpStr.length() == 0)
      setMaxFactor(-1);
    else
      setMaxFactor(Double.parseDouble(tmpStr));

    tmpStr = Utils.getOption("size-limit", options);
    if (tmpStr.length() == 0)
      setSizeLimit(-1);
    else
      setSizeLimit(Double.parseDouble(tmpStr));

    tmpStr = Utils.getOption("seed", options);
    if (tmpStr.length() == 0)
      setSeed(1);
    else
      setSeed(Integer.parseInt(tmpStr));

    super.setOptions(options);
  }

  /**
   * Gets the current settings of the filter.
   *
   * @return an array of strings suitable for passing to setOptions
   */
  public String[] getOptions() {
    List<String> 	result;

    result = new ArrayList<>();

    result.add("-drop-below");
    result.add("" + getDropBelow());

    result.add("-drop-at-most");
    result.add("" + getDropAtMost());

    result.add("-max-factor");
    result.add("" + getMaxFactor());

    result.add("-size-limit");
    result.add("" + getSizeLimit());

    result.add("-seed");
    result.add("" + getSeed());

    result.addAll(Arrays.asList(super.getOptions()));

    return result.toArray(new String[0]);
  }

  /**
   * Sets the threshold of the normalized weights below which to drop instances.
   *
   * @param value     the threshold (0-1)
   */
  public void setDropBelow(double value) {
    if ((value >= 0) && (value <= 1))
      m_DropBelow = value;
    else
      System.err.println(
	  "'drop-below' threshold must be within [0;1], provided: " + value);
  }

  /**
   * Returns the threshold of the normalized weights below which to drop instances.
   *
   * @return		the threshold (0-1)
   */
  public double getDropBelow() {
    return m_DropBelow;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return    tip text for this property suitable for
   *            displaying in the explorer/experimenter gui
   */
  public String dropBelowTipText() {
    return
        "The threshold of the normalized weights below which to drop instances (0-1).";
  }

  /**
   * Sets the maximum percentage of instances to drop.
   *
   * @param value     the threshold (0-1)
   */
  public void setDropAtMost(double value) {
    if ((value >= 0) && (value <= 1))
      m_DropAtMost = value;
    else
      System.err.println(
	  "'drop-below' threshold must be within [0;1], provided: " + value);
  }

  /**
   * Returns the threshold of the normalized weights below which to drop instances.
   *
   * @return		the threshold (0-1)
   */
  public double getDropAtMost() {
    return m_DropAtMost;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return    tip text for this property suitable for
   *            displaying in the explorer/experimenter gui
   */
  public String dropAtMostTipText() {
    return
        "The threshold of the normalized weights below which to drop instances (0-1).";
  }

  /**
   * Sets the upper limit for the multiplication factor for instances.
   * Disabled if <= 0.
   *
   * @param value     the upper limit
   */
  public void setMaxFactor(double value) {
    m_MaxFactor = value;
  }

  /**
   * Returns the upper limit for the multiplication factor for instances.
   * Disabled if <= 0.
   *
   * @return		the upper limit
   */
  public double getMaxFactor() {
    return m_MaxFactor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return    tip text for this property suitable for
   *            displaying in the explorer/experimenter gui
   */
  public String maxFactorTipText() {
    return
        "The upper limit for the multiplication factor for instances, disabled if <= 0.";
  }

  /**
   * Sets the size limit for the final dataset.
   * Disabled if <= 0, 0<x<=10 percentage (0-10,000%), >10 absolute number of
   * instances.
   *
   * @param value     the limit
   */
  public void setSizeLimit(double value) {
    m_SizeLimit = value;
  }

  /**
   * Returns the threshold of the normalized weights below which to drop instances.
   * Disabled if <= 0, 0<x<=10 percentage (0-10,000%), >10 absolute number of
   * instances.
   *
   * @return		the limit
   */
  public double getSizeLimit() {
    return m_SizeLimit;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return    tip text for this property suitable for
   *            displaying in the explorer/experimenter gui
   */
  public String sizeLimitTipText() {
    return
        "The size limit for the final dataset: disabled if <= 0, "
	  + "0<x<=10 percentage (0-10,000%), >10 absolute number of instances.";
  }

  /**
   * Set the seed for random number generation.
   *
   * @param seed the seed
   */
  public void setSeed(int seed) {
    m_Seed = seed;
  }

  /**
   * Gets the seed for the random number generations
   *
   * @return the seed for the random number generation
   */
  public int getSeed() {
    return m_Seed;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return    tip text for this property suitable for
   *            displaying in the explorer/experimenter gui
   */
  public String seedTipText() {
    return "The seed value to use for randomizing the final dataset.";
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
    double[]		weights;
    double		min;
    double		max;
    double		range;
    int			i;
    TDoubleList		dropped;
    double		dropBelow;
    int			index;
    List<Integer>	cleaned;
    double		factor;
    int			maxSize;
    int			n;

    // only first batch will get processed
    if (m_FirstBatchDone)
      return new Instances(instances);

    // get normalized weights
    weights = new double[instances.numInstances()];
    for (i = 0; i < instances.numInstances(); i++)
      weights[i] = instances.instance(i).weight();
    min = weights[Utils.minIndex(weights)];
    max = weights[Utils.maxIndex(weights)];
    range = max - min;
    if (getDebug())
      System.err.println("min weight: " + min + ", max weight: " + max + ", range: " + range);
    if (range == 0) {
      System.err.println("No difference between smallest and largest weight, cannot resample!");
      return new Instances(instances);
    }
    for (i = 0; i < weights.length; i++)
      weights[i] = (weights[i] - min) / range;

    // determine weight threshold below which to drop
    dropBelow = m_DropBelow;
    dropped = new TDoubleArrayList();
    for (i = 0; i < weights.length; i++) {
      if (weights[i] < m_DropBelow)
        dropped.add(weights[i]);
    }
    if ((double) dropped.size() / instances.numInstances() > m_DropAtMost) {
      dropped.sort();
      index = dropped.size() - (int) Math.round(instances.numInstances() * m_DropAtMost) + 1;
      dropBelow = dropped.get(index);
    }
    if (getDebug())
      System.err.println("Drop below (defined/used): " + m_DropBelow + "/" + dropBelow);

    // build list of instances that have weight above threshold and sort them
    // from largest weight to smallest
    cleaned = new ArrayList<>();
    for (i = 0; i < instances.numInstances(); i++) {
      if (weights[i] > dropBelow)
        cleaned.add(i);
    }
    Collections.sort(cleaned, new Comparator<Integer>() {
      @Override
      public int compare(Integer o1, Integer o2) {
	return -Double.compare(weights[o1], weights[o2]);
      }
    });

    // determine maximum dataset size
    maxSize = -1;
    if (m_SizeLimit > 0) {
      if (m_SizeLimit <= 10)
        maxSize = (int) Math.round(instances.numInstances() * (m_SizeLimit * 100));
      else
        maxSize = (int) m_SizeLimit;
    }
    if (getDebug())
      System.err.println("Max dataset size: " + maxSize);

    // generate final dataset
    result = new Instances(instances, maxSize);
    for (i = 0; i < cleaned.size(); i++) {
      // calculate factor for instance
      factor = 1.0 / weights[cleaned.get(i)];
      if (m_MaxFactor > 0)
	factor = Math.max(m_MaxFactor, factor);
      factor = (int) Math.round(factor);
      if (factor < 1)
        factor = 1;

      // add # instances to result
      for (n = 0; n < factor; n++) {
	result.add((Instance) instances.get(cleaned.get(n)).copy());
	result.get(result.size() - 1).setWeight(1.0);
      }
      if ((maxSize > -1) && (result.numInstances() >= maxSize))
	break;
    }
    result.compactify();
    if (getDebug())
      System.err.println("Final dataset size: " + result.numInstances());

    // randomize dataset
    result.randomize(new Random(m_Seed));

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
    runFilter(new WeightsBasedResample(), args);
  }
}
