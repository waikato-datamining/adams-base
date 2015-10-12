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

import gnu.trove.list.array.TDoubleArrayList;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * A filter for detecting outliers and extreme values based on interquartile ranges. The filter skips the class attribute.<br>
 * <br>
 * Outliers:<br>
 *   Q3 + OF*IQR &lt; x &lt;= Q3 + EVF*IQR<br>
 *   or<br>
 *   Q1 - EVF*IQR &lt;= x &lt; Q1 - OF*IQR<br>
 * <br>
 * Extreme values:<br>
 *   x &gt; Q3 + EVF*IQR<br>
 *   or<br>
 *   x &lt; Q1 - EVF*IQR<br>
 * <br>
 * Key:<br>
 *   Q1  = 25% quartile<br>
 *   Q3  = 75% quartile<br>
 *   IQR = Interquartile Range, difference between Q1 and Q3<br>
 *   OF  = Outlier Factor<br>
 *   EVF = Extreme Value Factor
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 *
 * <pre> -D
 *  Turns on output of debugging information.</pre>
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

  protected Hashtable<Integer,TDoubleArrayList> ht = new Hashtable<Integer,TDoubleArrayList>();

  protected Hashtable<Integer,List<IQRs>> m_iqrs = new Hashtable<Integer,List<IQRs>>();

  protected int m_sample_size = 150;

  protected int min_samples = 5;

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
      "A sampling filter made to behave like S2 for detecting outliers and extreme values based on "
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
   * Calculates and adds the IQR stats for this key.
   *
   * @param key		the key for the stats
   * @param v		the values
   */
  protected void addIQR(Integer key, TDoubleArrayList v) {
    if (v.size() >= min_samples) {
      double[] arr = v.toArray();
      Arrays.sort(arr);
      double q3val = valueAtPct(arr, 0.75);
      double q1val = valueAtPct(arr, 0.25);
      double med   = valueAtPct(arr, 0.5);
      double d     = arr[arr.length-1];
      IQRs is = new IQRs(q1val, q3val, d, med);
      List<IQRs> viqr = m_iqrs.get(key);
      if (viqr == null) {
	viqr = new ArrayList<IQRs>();
	m_iqrs.put(key,viqr);
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
    for (Integer key: ht.keySet()) {
      List<IQRs> viqr = m_iqrs.get(key);
      if (viqr == null) { //nothing there, so lets add this remainder
	TDoubleArrayList v = ht.get(key);
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

    m_UpperExtremeValue = new double[m_AttributeIndices.length];
    m_UpperOutlier      = new double[m_AttributeIndices.length];
    m_LowerOutlier      = new double[m_AttributeIndices.length];
    m_LowerExtremeValue = new double[m_AttributeIndices.length];
    m_Median            = new double[m_AttributeIndices.length];
    m_IQR               = new double[m_AttributeIndices.length];

    for (int i = 0; i < m_AttributeIndices.length; i++) {
      // non-numeric attribute?
      if (m_AttributeIndices[i] == NON_NUMERIC)
	continue;

      // sort attribute data
      values = instances.attributeToDoubleArray(m_AttributeIndices[i]);
      TDoubleArrayList v = new TDoubleArrayList();
      ht.put(i,v);

      for (int j = 0; j < values.length; j++) {
	v.add(values[j]);
	if (v.size() == m_sample_size) {
	  addIQR(i, v);
	  v.clear();
	}
      }
      clearRemainder();
    }

    for (Integer key: m_iqrs.keySet()) {
      double dmax = Double.NEGATIVE_INFINITY;
      List<IQRs> v = m_iqrs.get(key);
      if (v.size() == 0)
	continue;
      double[] q1s  = new double[v.size()];
      double[] q3s  = new double[v.size()];
      double[] meds = new double[v.size()];
      for(int k=0;k<v.size();k++) {
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

    ht     = new Hashtable<>(); // clear current store
    m_iqrs = new Hashtable<>(); // clear IQRs of samples
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
