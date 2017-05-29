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
 * KennardStone.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package weka.filters.unsupervised.instance;

import adams.core.Range;
import gnu.trove.list.TDoubleList;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;
import weka.core.Capabilities;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.WekaOptionUtils;
import weka.filters.Filter;
import weka.filters.SimpleBatchFilter;
import weka.filters.unsupervised.attribute.SavitzkyGolay;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

/**
 <!-- globalinfo-start -->
 * Applies the Kennard-Stone algorithm to the dataset.<br>
 * Each row has the pre-filter (eg PLS) applied before performing the search. The rows selected by the algorithm are returned in the original space, however.<br>
 * Distance calculation only uses numeric attributes in the defined range.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p>
 * 
 * <pre> -number-in-subset &lt;value&gt;
 *  Number of rows in subset.
 *  (default: -1)</pre>
 * 
 * <pre> -pre-filter &lt;value&gt;
 *  Pre-filter to apply to the data to perform the search on.
 *  (default: weka.filters.unsupervised.attribute.SavitzkyGolay -left 3 -right 3 -polynomial 2 -derivative 1)</pre>
 * 
 * <pre> -att-range &lt;value&gt;
 *  The attribute range to limit distance calculation to (after applying pre-filter).
 *  (default: first-last)</pre>
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
public class KennardStone
  extends SimpleBatchFilter {

  private static final long serialVersionUID = 7465262788509209875L;

  protected static String NUMBER_IN_SUBSET = "number-in-subset";

  protected static String PRE_FILTER = "pre-filter";

  protected static String ATT_RANGE = "att-range";

  public static final int DEFAULT_NUMBER_IN_SUBSET = -1;

  public static final Filter DEFAULT_PRE_FILTER = new SavitzkyGolay();

  public static final Range DEFAULT_ATT_RANGE = new Range("first-last");

  /** Number of spectra to select in subset */
  protected int m_NumberInSubset = DEFAULT_NUMBER_IN_SUBSET;

  /** Pre filter to apply before selection */
  protected Filter m_PreFilter = DEFAULT_PRE_FILTER;

  /** the range of attributes to apply to. */
  protected Range m_AttRange = DEFAULT_ATT_RANGE;

  /**
   * Returns a string describing this filter.
   *
   * @return a description of the filter suitable for displaying in the
   *         explorer/experimenter gui
   */
  @Override
  public String globalInfo() {
    return
      "Applies the Kennard-Stone algorithm to the dataset.\n"
	+ "Each row has the pre-filter (eg PLS) applied before performing the search. "
	+ "The rows selected by the algorithm are returned in the original space, however.\n"
	+ "Distance calculation only uses numeric attributes in the defined range.";
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return 		an enumeration of all the available options.
   */
  public Enumeration listOptions() {
    Vector result;

    result = new Vector();

    WekaOptionUtils.addOption(result, numberInSubsetTipText(), "" + DEFAULT_NUMBER_IN_SUBSET, NUMBER_IN_SUBSET);
    WekaOptionUtils.addOption(result, preFilterTipText(), DEFAULT_PRE_FILTER, PRE_FILTER);
    WekaOptionUtils.addOption(result, attRangeTipText(), DEFAULT_ATT_RANGE, ATT_RANGE);
    WekaOptionUtils.add(result, super.listOptions());
    return WekaOptionUtils.toEnumeration(result);
  }

  /**
   * Parses a given list of options.
   *
   * @param options 	the list of options as an array of strings
   * @throws Exception 	if an option is not supported
   */
  public void setOptions(String[] options) throws Exception {
    setNumberInSubset(WekaOptionUtils.parse(options, NUMBER_IN_SUBSET, DEFAULT_NUMBER_IN_SUBSET));
    setPreFilter((Filter) WekaOptionUtils.parse(options, PRE_FILTER, DEFAULT_PRE_FILTER));
    setAttRange(WekaOptionUtils.parse(options, ATT_RANGE, DEFAULT_ATT_RANGE));
    super.setOptions(options);
  }

  /**
   * Gets the current settings of the classifier.
   *
   * @return 		an array of strings suitable for passing to setOptions
   */
  public String [] getOptions() {
    List<String> result = new ArrayList<>();
    WekaOptionUtils.add(result, NUMBER_IN_SUBSET, getNumberInSubset());
    WekaOptionUtils.add(result, PRE_FILTER, getPreFilter());
    WekaOptionUtils.add(result, ATT_RANGE, getAttRange());
    WekaOptionUtils.add(result, super.getOptions());
    return WekaOptionUtils.toArray(result);
  }

  /**
   * Sets the number of rws to select in subset.
   *
   * @param value 	the number
   */
  public void setNumberInSubset(int value) {
    m_NumberInSubset = value;
    reset();
  }

  /**
   * Returns the number of rows to select in subset.
   *
   * @return		the number
   */
  public int getNumberInSubset() {
    return m_NumberInSubset;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String numberInSubsetTipText() {
    return "Number of rows in subset.";
  }

  /**
   * Sets the pre-filter to apply to the data to perform the search on.
   *
   * @param value 	the filter
   */
  public void setPreFilter(Filter value) {
    m_PreFilter = value;
    reset();
  }

  /**
   * Returns the pre-filter to apply to the data to perform the search on.
   *
   * @return		the filter
   */
  public Filter getPreFilter() {
    return m_PreFilter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String preFilterTipText() {
    return "Pre-filter to apply to the data to perform the search on.";
  }

  /**
   * Sets the attribute range to use for distance calculation (after applying pre-filter).
   *
   * @param value 	the range
   */
  public void setAttRange(Range value) {
    m_AttRange = value;
    reset();
  }

  /**
   * Returns the attribute range to use for distance calculation (after applying pre-filter).
   *
   * @return		the range
   */
  public Range getAttRange() {
    return m_AttRange;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String attRangeTipText() {
    return "The attribute range to limit distance calculation to (after applying pre-filter).";
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

    result = m_PreFilter.getCapabilities();
    result.setOwner(this);

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
   */
  @Override
  protected Instances determineOutputFormat(Instances inputFormat) throws Exception {
    return new Instances(inputFormat, 0);
  }

  /**
   * Calculate the distance between any two instances. Currently just uses euclidean distance.
   *
   * @param inst1	Instance1
   * @param inst2 	Instance2
   * @return		the distance
   */
  protected double calculateDistance(Instance inst1, Instance inst2) {
    double		result;
    TDoubleList		val1;
    TDoubleList		val2;
    int[]		indices;
    int			i;

    val1 = new TDoubleArrayList();
    val2 = new TDoubleArrayList();

    indices = m_AttRange.getIntIndices();
    for (i = 0; i < indices.length; i++) {
      if (inst1.attribute(indices[i]).isNumeric()) {
	val1.add(inst1.value(indices[i]));
	val2.add(inst2.value(indices[i]));
      }
    }

    org.apache.commons.math3.ml.distance.EuclideanDistance dist = new org.apache.commons.math3.ml.distance.EuclideanDistance();
    result = dist.compute(val1.toArray(), val2.toArray());

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
    Instances 	original;
    Instances 	filtered;
    double[][] 	distances2D;
    Instance 	inst1;
    Instance 	inst2;
    int		i;
    int		j;
    int		m;
    TIntList 	chosen;
    TIntList 	remaining;
    double 	maxDistance;
    int 	chosen1;
    int 	chosen2;
    int 	indexTest;
    int 	indexExisting;
    double 	maxDistanceTest;
    double 	lowestDistanceSingle;
    int 	bestIndex;
    double 	thisDistance;

    if (isFirstBatchDone())
      return instances;

    // original instances
    original = new Instances(instances);

    // apply pre-filter
    m_PreFilter.setInputFormat(instances);
    filtered = Filter.useFilter(instances, m_PreFilter);

    m_AttRange.setMax(filtered.numAttributes());

    result = new Instances(instances, 0);

    // calculate the intersample matrix once
    distances2D = new double[filtered.numInstances()][filtered.numInstances()];
    for (i = 0; i < filtered.numInstances() - 1; i++) {
      inst1 = filtered.instance(i);
      for (j = i + 1; j < filtered.numInstances(); j++) {
	inst2 = filtered.instance(j);
	distances2D[i][j] = calculateDistance(inst1, inst2);
      }
    }

    //Keep a record of chosen and remaining indices
    chosen    = new TIntArrayList();
    remaining = new TIntArrayList();
    for (i = 0; i < filtered.numInstances(); i++)
      remaining.add(i);

    //find 2 samples that are furthest apart using uniform distance
    maxDistance = -1;
    chosen1     = -1;
    chosen2     = -1;
    for (i = 0; i < filtered.numInstances() - 1; i++) {
      for (j = i + 1; j < filtered.numInstances(); j++) {
	if (distances2D[i][j] > maxDistance) {
	  maxDistance = distances2D[i][j];
	  chosen1 = i;
	  chosen2 = j;
	}
      }
    }
    chosen.add(chosen1);
    chosen.add(chosen2);
    remaining.remove(remaining.indexOf(chosen1));
    remaining.remove(remaining.indexOf(chosen2));

    //Loop through until the right amount are found.
    for (m = 3; m <= m_NumberInSubset; m++) {
      maxDistanceTest = -1;
      bestIndex = -1;
      for (i = 0; i < remaining.size(); i++) {
	lowestDistanceSingle = Double.POSITIVE_INFINITY;
	indexTest = remaining.get(i);
	for (j = 0; j < chosen.size(); j++) {
	  indexExisting = chosen.get(j);
	  thisDistance  = distances2D[Math.min(indexTest, indexExisting)][Math.max(indexTest, indexExisting)];
	  if (thisDistance < lowestDistanceSingle)
	    lowestDistanceSingle = thisDistance;
	}
	if (lowestDistanceSingle > maxDistanceTest) {
	  maxDistanceTest = lowestDistanceSingle;
	  bestIndex = indexTest;
	}
      }
      chosen.add(bestIndex);
      remaining.remove(remaining.indexOf(bestIndex));
    }

    for (i = 0; i < chosen.size(); i++)
      result.add(original.instance(chosen.get(i)));

    return result;
  }
}
