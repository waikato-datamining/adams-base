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
 * AbstractFilteredReportFilter.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.report;

import adams.data.container.DataContainer;

/**
 * Ancestor for report filters that ensure that certain fields are retained 
 * in the output when applying a report filter.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractFilteredReportFilter
  extends AbstractReportFilter {

  /** for serialization. */
  private static final long serialVersionUID = 247334339949735595L;

  /** the report filter to apply. */
  protected AbstractReportFilter m_Filter;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public abstract String globalInfo();

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "filter", "filter",
	    new PassThrough());
  }

  /**
   * Sets the filter to apply to the report.
   *
   * @param value	the filter
   */
  public void setFilter(AbstractReportFilter value) {
    m_Filter = value;
    reset();
  }

  /**
   * Returns the filter to apply to the report.
   *
   * @return		the filter
   */
  public AbstractReportFilter getFilter() {
    return m_Filter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public abstract String filterTipText();

  /**
   * Returns the fields to keep.
   */
  protected abstract AbstractField[] getFields();
  
  /**
   * Performs the actual filtering.
   *
   * @param data	the data to filter
   * @return		the filtered data
   */
  @Override
  protected DataContainer processData(DataContainer data) {
    Report		backup;
    DataContainer	filtered;
    AbstractField[]	fields;
    
    if (!(data instanceof ReportHandler))
      return data;
    
    if (!((ReportHandler) data).hasReport())
      return data;
    
    backup   = ((ReportHandler) data).getReport().getClone();
    filtered = m_Filter.filter(data);
    fields   = getFields();
    for (AbstractField field: fields) {
      if (backup.hasValue(field))
	((ReportHandler) filtered).getReport().setValue(field, backup.getValue(field));
    }
    
    return filtered;
  }
}
