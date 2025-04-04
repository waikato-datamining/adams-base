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
 * MultiFilter.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.data.imagesegmentation.filter;

import adams.flow.container.ImageSegmentationContainer;

/**
 * Applies the base filters sequentially.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class MultiFilter
  extends AbstractImageSegmentationContainerFilter {

  private static final long serialVersionUID = -6559340258634055902L;

  /** the base filters. */
  protected AbstractImageSegmentationContainerFilter[] m_Filters;
  
  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Applies the base filters sequentially.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
        "filter", "filters",
        new AbstractImageSegmentationContainerFilter[]{});
  }

  /**
   * Sets the filters to apply sequentially.
   *
   * @param value	the filters
   */
  public void setFilters(AbstractImageSegmentationContainerFilter[] value) {
    m_Filters = value;
    reset();
  }

  /**
   * Returns the filters to apply sequentially.
   *
   * @return		the filters
   */
  public AbstractImageSegmentationContainerFilter[] getFilters() {
    return m_Filters;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String filtersTipText() {
    return "The filters to apply sequentially.";
  }

  /**
   * Performs the filtering of the container.
   *
   * @param cont the container to filter
   * @return the filtered container
   */
  @Override
  protected ImageSegmentationContainer doFilter(ImageSegmentationContainer cont) {
    ImageSegmentationContainer  result;

    result = cont;

    for (AbstractImageSegmentationContainerFilter filter: m_Filters)
      result = filter.filter(result);

    return result;
  }
}
