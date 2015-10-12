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
 * InterquartileRangeSamp.java
 * Copyright (C) 2006-2015 University of Waikato, Hamilton, New Zealand
 */

package weka.filters.unsupervised.attribute;

import adams.core.base.BaseRegExp;
import gnu.trove.list.array.TDoubleArrayList;
import weka.core.Instances;
import weka.core.WekaOptionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

/**
 <!-- globalinfo-start -->
 * A sampling filter for detecting outliers and extreme values based on interquartile ranges. The filter skips the class attribute.<br/>
 * <br/>
 * Outliers:<br/>
 *   Q3 + OF*IQR &lt; x &lt;= Q3 + EVF*IQR<br/>
 *   or<br/>
 *   Q1 - EVF*IQR &lt;= x &lt; Q1 - OF*IQR<br/>
 * <br/>
 * Extreme values:<br/>
 *   x &gt; Q3 + EVF*IQR<br/>
 *   or<br/>
 *   x &lt; Q1 - EVF*IQR<br/>
 * <br/>
 * Key:<br/>
 *   Q1  = 25% quartile<br/>
 *   Q3  = 75% quartile<br/>
 *   IQR = Interquartile Range, difference between Q1 and Q3<br/>
 *   OF  = Outlier Factor<br/>
 *   EVF = Extreme Value Factor
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre> -sample-size &lt;value&gt;
 *  The sample size to use.
 *  (default: 150)</pre>
 * 
 * <pre> -min-samples &lt;value&gt;
 *  The minimum number of samples that are required for calculating IQR stats.
 *  (default: 5)</pre>
 * 
 * <pre> -ignored-attributes &lt;value&gt;
 *  The regular expression for attributes to ignore/skip.
 *  (default: ^.*_id$)</pre>
 * 
 * <pre> -R &lt;col1,col2-col4,...&gt;
 *  Specifies list of columns to base outlier/extreme value detection
 *  on. If an instance is considered in at least one of those
 *  attributes an outlier/extreme value, it is tagged accordingly.
 *  'first' and 'last' are valid indexes.
 *  (default none)</pre>
 * 
 * <pre> -O &lt;num&gt;
 *  The factor for outlier detection.
 *  (default: 3)</pre>
 * 
 * <pre> -E &lt;num&gt;
 *  The factor for extreme values detection.
 *  (default: 2*Outlier Factor)</pre>
 * 
 * <pre> -E-as-O
 *  Tags extreme values also as outliers.
 *  (default: off)</pre>
 * 
 * <pre> -P
 *  Generates Outlier/ExtremeValue pair for each numeric attribute in
 *  the range, not just a single indicator pair for all the attributes.
 *  (default: off)</pre>
 * 
 * <pre> -M
 *  Generates an additional attribute 'Offset' per Outlier/ExtremeValue
 *  pair that contains the multiplier that the value is off the median.
 *     value = median + 'multiplier' * IQR
 * Note: implicitely sets '-P'. (default: off)</pre>
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
 * Thanks to Dale for a few brainstorming sessions.
 *
 * @author  Dale Fletcher (dale at cs dot waikato dot ac dot nz)
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class InterquartileRangeSamp
  extends InterquartileRange {

  /** for serialization */
  protected static final long serialVersionUID = 3811630774543798261L;

  protected Hashtable<Integer,TDoubleArrayList> m_AttValues = new Hashtable<Integer,TDoubleArrayList>();

  protected Hashtable<Integer,List<IQRs>> m_IQRs = new Hashtable<Integer,List<IQRs>>();

  /** the sample size to use. */
  protected int m_SampleSize = getDefaultSampleSize();

  /** the minimum number of samples. */
  protected int m_MinSamples = getDefaultMinSamples();

  /** the regular expression for attributes to skip. */
  protected BaseRegExp m_IgnoredAttributes = getDefaultIgnoredAttributes();

  /**
   * Container class for the IQR values.
   */
  public static class IQRs{
    public double quartile1;
    public double median;
    public double quartile3;
    public double maxval;
    public IQRs(double q1, double q3, double mval, double med) {
      quartile1 = q1;
      quartile3 = q3;
      maxval    = mval;
      median    = med;
    }
  }

  /**
   * Returns a string describing this filter
   *
   * @return 		a description of the filter suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String globalInfo() {
    return
      "A sampling filter for detecting outliers and extreme values based on "
	+ "interquartile ranges. The filter skips the class attribute.\n\n"
	+ "Outliers:\n"
	+ "  Q3 + OF*IQR < x <= Q3 + EVF*IQR\n"
	+ "  or\n"
	+ "  Q1 - EVF*IQR <= x < Q1 - OF*IQR\n"
	+ "\n"
	+ "Extreme values:\n"
	+ "  x > Q3 + EVF*IQR\n"
	+ "  or\n"
	+ "  x < Q1 - EVF*IQR\n"
	+ "\n"
	+ "Key:\n"
	+ "  Q1  = 25% quartile\n"
	+ "  Q3  = 75% quartile\n"
	+ "  IQR = Interquartile Range, difference between Q1 and Q3\n"
	+ "  OF  = Outlier Factor\n"
	+ "  EVF = Extreme Value Factor";
  }

  /**
   * Returns the default sample size.
   *
   * @return 		the default
   */
  protected int getDefaultSampleSize() {
    return 150;
  }

  /**
   * Sets the sample size to use.
   *
   * @param value 	the size
   */
  public void setSampleSize(int value) {
    if (value > 0)
      m_SampleSize = value;
    else
      System.err.println("SampleSize must meet >0, provided: " + value);
  }

  /**
   * Returns the sample size to use.
   *
   * @return 		the samples
   */
  public int getSampleSize() {
    return m_SampleSize;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String sampleSizeTipText() {
    return "The sample size to use.";
  }

  /**
   * Returns the default minimum number of samples.
   *
   * @return 		the default
   */
  protected int getDefaultMinSamples() {
    return 5;
  }

  /**
   * Sets the minimum number of samples that are required for calculating IQR stats.
   *
   * @param value 	the samples
   */
  public void setMinSamples(int value) {
    if (value > 0)
      m_MinSamples = value;
    else
      System.err.println("MinSamples must meet >0, provided: " + value);
  }

  /**
   * Returns the minimum number of samples that are required for calculating IQR stats.
   *
   * @return 		the samples
   */
  public int getMinSamples() {
    return m_MinSamples;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String minSamplesTipText() {
    return "The minimum number of samples that are required for calculating IQR stats.";
  }

  /**
   * Returns the default regular expression for ignored/skipped attributes.
   *
   * @return 		the default
   */
  protected BaseRegExp getDefaultIgnoredAttributes() {
    return new BaseRegExp("^.*_id$");
  }

  /**
   * Sets the regular expression for ignored/skipped attributes.
   *
   * @param value 	the regexp
   */
  public void setIgnoredAttributes(BaseRegExp value) {
    m_IgnoredAttributes = value;
  }

  /**
   * Returns the regular expression for ignored/skipped attributes.
   *
   * @return 		the regexp
   */
  public BaseRegExp getIgnoredAttributes() {
    return m_IgnoredAttributes;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String ignoredAttributesTipText() {
    return "The regular expression for attributes to ignore/skip.";
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  @Override
  public Enumeration listOptions() {
    Vector result = new Vector();
    WekaOptionUtils.addOption(result, sampleSizeTipText(), "" + getDefaultSampleSize(), "sample-size");
    WekaOptionUtils.addOption(result, minSamplesTipText(), "" + getDefaultMinSamples(), "min-samples");
    WekaOptionUtils.addOption(result, ignoredAttributesTipText(), "" + getDefaultIgnoredAttributes(), "ignored-attributes");
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
    setSampleSize(WekaOptionUtils.parse(options, "sample-size", getDefaultSampleSize()));
    setMinSamples(WekaOptionUtils.parse(options, "min-samples", getDefaultMinSamples()));
    setIgnoredAttributes(new BaseRegExp(WekaOptionUtils.parse(options, "ignored-attributes", getDefaultIgnoredAttributes().getValue())));
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
    WekaOptionUtils.add(result, "sample-size", getSampleSize());
    WekaOptionUtils.add(result, "min-samples", getMinSamples());
    WekaOptionUtils.add(result, "ignored-attributes", getIgnoredAttributes().getValue());
    WekaOptionUtils.add(result, super.getOptions());
    return WekaOptionUtils.toArray(result);
  }

  /**
   * Calculates and adds the IQR stats for this key.
   *
   * @param key		the key for the stats
   * @param v		the values
   */
  protected void addIQR(Integer key, TDoubleArrayList v) {
    if (v.size() >= m_MinSamples) {
      double[] arr = v.toArray();
      Arrays.sort(arr);
      double q3val = valueAtPct(arr, 0.75);
      double q1val = valueAtPct(arr, 0.25);
      double med   = valueAtPct(arr, 0.5);
      double d     = arr[arr.length-1];
      IQRs is = new IQRs(q1val, q3val, d, med);
      List<IQRs> viqr = m_IQRs.get(key);
      if (viqr == null) {
	viqr = new ArrayList<IQRs>();
	m_IQRs.put(key,viqr);
      }
      viqr.add(is);
    }
  }

  /**
   * Calculates the value at the specified percentage.
   *
   * @param sorted_arr	the sorted array to use
   * @param pct		the percent
   * @return		the value
   */
  protected double valueAtPct(double[] sorted_arr, double pct) {
    double qindex = (pct * sorted_arr.length);
    int iqindex = (int) Math.floor(qindex);
    double qval;

    if (iqindex == qindex) {
      qval = sorted_arr[iqindex];
    }
    else {
      double d1= sorted_arr[iqindex];
      double d2= sorted_arr[iqindex+1];
      double pcte = qindex - (double)iqindex;
      qval = d1 + (d2 - d1) * pcte;
    }

    return qval;
  }

  protected void clearRemainder() {
    for (Integer key: m_AttValues.keySet()) {
      List<IQRs> viqr = m_IQRs.get(key);
      if (viqr == null) { //nothing there, so lets add this remainder
	TDoubleArrayList v = m_AttValues.get(key);
	addIQR(key, v);
      }
    }
  }

  /**
   * computes the thresholds for outliers and extreme values
   *
   * @param instances	the data to work on
   */
  protected void computeThresholds(Instances instances) {
    double[]	values;
    double	q1;
    double	q2;
    double	q3;
    String	name;

    m_UpperExtremeValue = new double[m_AttributeIndices.length];
    m_UpperOutlier      = new double[m_AttributeIndices.length];
    m_LowerOutlier      = new double[m_AttributeIndices.length];
    m_LowerExtremeValue = new double[m_AttributeIndices.length];
    m_Median            = new double[m_AttributeIndices.length];
    m_IQR               = new double[m_AttributeIndices.length];

    for (int i = 0; i < m_AttributeIndices.length; i++) {
      name = instances.attribute(i).name();
      // non-numeric attribute?
      if (m_AttributeIndices[i] == NON_NUMERIC) {
	if (getDebug())
	  System.out.println("Skipping non-numeric attribute: " + name);
	continue;
      }
      // ignored attribute?
      if (m_IgnoredAttributes.isMatch(name)) {
	if (getDebug())
	  System.out.println("Ignored attribute: " + name);
	continue;
      }

      // sort attribute data
      values = instances.attributeToDoubleArray(m_AttributeIndices[i]);
      TDoubleArrayList v = new TDoubleArrayList();
      m_AttValues.put(i, v);

      for (int j = 0; j < values.length; j++) {
	v.add(values[j]);
	if (v.size() == m_SampleSize) {
	  addIQR(i, v);
	  v.clear();
	}
      }
      clearRemainder();
    }

    for (Integer key: m_IQRs.keySet()) {
      double dmax = Double.NEGATIVE_INFINITY;
      List<IQRs> v = m_IQRs.get(key);
      if (v.size() == 0)
	continue;
      double[] q1s  = new double[v.size()];
      double[] q3s  = new double[v.size()];
      double[] meds = new double[v.size()];
      for(int k = 0; k < v.size(); k++) {
	IQRs iqrs = v.get(k);
	q1s[k]  = iqrs.quartile1;
	q3s[k]  = iqrs.quartile3;
	meds[k] = iqrs.median;
	if (iqrs.maxval > dmax) {
	  dmax = iqrs.maxval;
	}
      }
      Arrays.sort(q1s);
      Arrays.sort(q3s);
      Arrays.sort(meds);

      if (v.size() > 1) {
	q3 = valueAtPct(q3s,0.5);
	q1 = valueAtPct(q1s,0.5);
	q2 = valueAtPct(meds,0.5);
      }
      else {
	q3 = q3s[0];
	q1 = q1s[0];
	q2 = meds[0];
      }

      // determine thresholds and other values
      m_Median[key]            = q2;
      m_IQR[key]               = q3 - q1;
      m_UpperExtremeValue[key] = q3 + getExtremeValuesFactor() * m_IQR[key];
      m_UpperOutlier[key]      = q3 + getOutlierFactor()       * m_IQR[key];
      m_LowerOutlier[key]      = q1 - getOutlierFactor()       * m_IQR[key];
      m_LowerExtremeValue[key] = q1 - getExtremeValuesFactor() * m_IQR[key];
    }

    m_AttValues = new Hashtable<>(); // clear current store
    m_IQRs = new Hashtable<>(); // clear IQRs of samples
  }

  /**
   * Main method for testing this class.
   *
   * @param args should contain arguments to the filter: use -h for help
   */
  public static void main(String[] args) {
    runFilter(new InterquartileRangeSamp(), args);
  }
}
