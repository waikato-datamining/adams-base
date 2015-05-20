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
 * FilteredIQR.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.weka.rowfinder;

import java.util.ArrayList;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;
import weka.filters.AllFilter;
import weka.filters.Filter;
import weka.filters.MultiFilter;
import weka.filters.unsupervised.attribute.InterquartileRange;
import adams.core.Range;

/**
 <!-- globalinfo-start -->
 * Returns indices of rows that got identified as outliers&#47;extreme values.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 * 
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to 
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-pre-filter &lt;weka.filters.Filter&gt; (property: preFilter)
 * &nbsp;&nbsp;&nbsp;The filter to pre-filter the data with before subjecting it to the IQR filter.
 * &nbsp;&nbsp;&nbsp;default: weka.filters.AllFilter
 * </pre>
 * 
 * <pre>-filter &lt;weka.filters.Filter&gt; (property: filter)
 * &nbsp;&nbsp;&nbsp;The IQR filter to use; parameters get set internally.
 * &nbsp;&nbsp;&nbsp;default: weka.filters.unsupervised.attribute.InterquartileRange -R first-last -O 3.0 -E 6.0
 * </pre>
 * 
 * <pre>-iqr &lt;double&gt; (property: iqr)
 * &nbsp;&nbsp;&nbsp;IQR multipler for min&#47;max values.
 * &nbsp;&nbsp;&nbsp;default: 4.25
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * </pre>
 * 
 * <pre>-attribute-range &lt;adams.core.Range&gt; (property: attributeRange)
 * &nbsp;&nbsp;&nbsp;The attribute range to work on.
 * &nbsp;&nbsp;&nbsp;default: first-last
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FilteredIQR
  extends AbstractTrainableRowFinder {

  /** for serialization. */
  private static final long serialVersionUID = -2705356105829555109L;

  /** the filter to apply to the data first. */
  protected Filter m_PreFilter;

  /** the IQR filter. */
  protected InterquartileRange m_Filter;

  /** the actual IQR filter. */
  protected InterquartileRange m_ActualFilter;

  /** the maximum value of the attribute. */
  protected double m_IQR;

  /** the attribute range to work on. */
  protected Range m_Range;
  
  /** the {@link MultiFilter} doing all the filtering. */
  protected MultiFilter m_FullFilter;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Returns indices of rows that got identified as outliers/extreme values.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"pre-filter", "preFilter",
	new AllFilter());

    m_OptionManager.add(
	"filter", "filter",
	new InterquartileRange());

    m_OptionManager.add(
	"iqr", "iqr",
	4.25, 0.0, null);

    m_OptionManager.add(
	"attribute-range", "attributeRange",
	new Range(Range.ALL));
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_Range = new Range();
  }

  /**
   * Resets the object.
   */
  @Override
  protected void reset() {
    super.reset();
    
    m_ActualFilter = null;
    m_FullFilter   = null;
  }
  
  /**
   * Sets the pre filter.
   *
   * @param value 	the filter
   */
  public void setPreFilter(Filter value) {
    m_PreFilter = value;
    reset();
  }

  /**
   * Returns the pre filter.
   *
   * @return 		the filter
   */
  public Filter getPreFilter() {
    return m_PreFilter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String preFilterTipText() {
    return "The filter to pre-filter the data with before subjecting it to the IQR filter.";
  }

  /**
   * Sets the IQR filter.
   *
   * @param value 	the filter
   */
  public void setFilter(Filter value) {
    if (value instanceof InterquartileRange) {
      m_Filter = (InterquartileRange) value;
      reset();
    }
    else {
      getLogger().severe(
	  "Only " + InterquartileRange.class.getName() 
	  + " and derived classes are allowed, provided: " + Utils.toCommandLine(value));
    }
  }

  /**
   * Returns the IQR filter.
   *
   * @return 		the filter
   */
  public Filter getFilter() {
    return m_Filter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String filterTipText() {
    return "The IQR filter to use; parameters get set internally.";
  }

  /**
   * Sets the IQR multiplier.
   *
   * @param value 	iqr
   */
  public void setIqr(double value) {
    m_IQR = value;
    reset();
  }

  /**
   * Returns the iqr multiplier.
   *
   * @return 		the iqr
   */
  public double getIqr() {
    return m_IQR;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String iqrTipText() {
    return "IQR multipler for min/max values.";
  }

  /**
   * Sets the attribute range to work on.
   *
   * @param value 	the range
   */
  public void setAttributeRange(Range value) {
    m_Range = value;
    reset();
  }

  /**
   * Returns the attribute range to work on.
   *
   * @return 		the range
   */
  public Range getAttributeRange() {
    return m_Range;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String attributeRangeTipText() {
    return "The attribute range to work on.";
  }

  /**
   * Performs the actual training of the row finder with the specified dataset.
   * 
   * @param data	the training data
   * @return		true if successfully trained
   */
  @Override
  protected boolean doTrainRowFinder(Instances data) {
    Instances		labeled;
    
    m_ActualFilter = (InterquartileRange) adams.core.Utils.deepCopy(m_Filter);
    m_ActualFilter.setOutlierFactor(m_IQR);
    m_ActualFilter.setExtremeValuesFactor(m_IQR + 1.0);
    m_ActualFilter.setExtremeValuesAsOutliers(true);
    m_ActualFilter.setAttributeIndices(m_Range.getRange());
    
    m_FullFilter = new MultiFilter();
    m_FullFilter.setFilters(new Filter[]{
	m_PreFilter,
	m_ActualFilter
    });

    try {
      m_FullFilter.setInputFormat(data);
      labeled = Filter.useFilter(data, m_FullFilter);
    }
    catch (Exception e) {
      throw new IllegalStateException(e);
    }
    
    if (labeled.numInstances() != data.numInstances())
      throw new IllegalStateException(
	  "Pre-filter changed number of instances in dataset? " + labeled.numInstances() + " != " + data.numInstances());
    
    return true;
  }

  /**
   * Returns the rows of interest in the dataset.
   * 
   * @param data	the dataset to inspect
   * @return		the rows of interest
   */
  @Override
  protected int[] doFindRows(Instances data) {
    int[]		result;
    ArrayList<Integer>	list;
    Instances		labeled;
    int			i;
    Instance		inst;
    int			index;

    try {
      labeled = Filter.useFilter(data, m_FullFilter);
    }
    catch (Exception e) {
      throw new IllegalStateException(e);
    }
    
    // collect outliers
    list  = new ArrayList<Integer>();
    index = labeled.numAttributes() - 1;
    for (i = 0; i < labeled.numInstances(); i++) {
      inst = labeled.instance(i);
      if (inst.stringValue(index).equals("yes"))
	list.add(i);
    }
    
    // generate array of indices
    result = new int[list.size()];
    for (i = 0; i < list.size(); i++)
      result[i] = list.get(i);
    
    return result;
  }
}
