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
 * MinDimensions.java
 * Copyright (C) 2018-2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.negativeregions;

import adams.data.image.AbstractImageContainer;
import adams.flow.transformer.locateobjects.LocatedObjects;

/**
 * Enforces the specified minimum dimensions.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class MinDimensions
  extends AbstractMetaNegativeRegionsGenerator {

  private static final long serialVersionUID = -904202231629949668L;

  /** the minimum width. */
  protected int m_MinWidth;

  /** the minimum height. */
  protected int m_MinHeight;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Enforces the specified minimum dimensions.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "min-width", "minWidth",
      -1, -1, null);

    m_OptionManager.add(
      "min-height", "minHeight",
      -1, -1, null);
  }

  /**
   * Sets the minimum width a negative region must have.
   *
   * @param value	the minimum width, ignored if <1
   */
  public void setMinWidth(int value) {
    if (getOptionManager().isValid("minWidth", value)) {
      m_MinWidth = value;
      reset();
    }
  }

  /**
   * Returns the minimum width a negative region must have.
   *
   * @return		the minimum width, ignored if <1
   */
  public int getMinWidth() {
    return m_MinWidth;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String minWidthTipText() {
    return "The minimum width that a negative region must have, ignored if <1.";
  }

  /**
   * Sets the minimum height a negative region must have.
   *
   * @param value	the minimum height, ignored if <1
   */
  public void setMinHeight(int value) {
    if (getOptionManager().isValid("minHeight", value)) {
      m_MinHeight = value;
      reset();
    }
  }

  /**
   * Returns the minimum height a negative region must have.
   *
   * @return		the minimum height, ignored if <1
   */
  public int getMinHeight() {
    return m_MinHeight;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String minHeightTipText() {
    return "The minimum height that a negative region must have, ignored if <1.";
  }

  /**
   * Generates the negative regions.
   *
   * @param cont	the image container to generate the regions for
   * @return		the generated regions
   */
  @Override
  protected LocatedObjects doGenerateRegions(AbstractImageContainer cont) {
    LocatedObjects 	result;
    int			i;

    result = getActualAlgorithm().generateRegions(cont);

    // check min height/width
    if ((result != null) && ((m_MinHeight > 0) || (m_MinWidth > 0))) {
      i = 0;
      while (i < result.size()) {
        if (isStopped())
          break;
	if (m_MinWidth > 0) {
	  if (result.get(i).getWidth() < m_MinWidth) {
	    if (isLoggingEnabled())
	      getLogger().info("Removed, width too small: " + result.get(i) + " < " + m_MinWidth);
	    result.remove(i);
	    continue;
	  }
	}
	if (m_MinHeight > 0) {
	  if (result.get(i).getHeight() < m_MinHeight) {
	    if (isLoggingEnabled())
	      getLogger().info("Removed, height too small: " + result.get(i) + " < " + m_MinHeight);
	    result.remove(i);
	    continue;
	  }
	}
	i++;
      }
    }

    if (isStopped())
      result.clear();

    return result;
  }
}
