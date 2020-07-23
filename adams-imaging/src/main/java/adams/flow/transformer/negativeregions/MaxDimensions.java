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
 * MaxDimensions.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.negativeregions;

import adams.data.image.AbstractImageContainer;
import adams.flow.transformer.locateobjects.LocatedObjects;

/**
 * Enforces the specified maximum dimensions.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class MaxDimensions
  extends AbstractMetaNegativeRegionsGenerator {

  private static final long serialVersionUID = -904202231629949668L;

  /** the maximum width. */
  protected int m_MaxWidth;

  /** the maximum height. */
  protected int m_MaxHeight;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Enforces the specified maximum dimensions.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "max-width", "maxWidth",
      -1, -1, null);

    m_OptionManager.add(
      "max-height", "maxHeight",
      -1, -1, null);
  }

  /**
   * Sets the maximum width a negative region can have.
   *
   * @param value	the maximum width, ignored if <1
   */
  public void setMaxWidth(int value) {
    if (getOptionManager().isValid("maxWidth", value)) {
      m_MaxWidth = value;
      reset();
    }
  }

  /**
   * Returns the maximum width a negative region can have.
   *
   * @return		the maximum width, ignored if <1
   */
  public int getMaxWidth() {
    return m_MaxWidth;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String maxWidthTipText() {
    return "The maximum width that a negative region can have, ignored if <1.";
  }

  /**
   * Sets the maximum height a negative region can have.
   *
   * @param value	the maximum height, ignored if <1
   */
  public void setMaxHeight(int value) {
    if (getOptionManager().isValid("maxHeight", value)) {
      m_MaxHeight = value;
      reset();
    }
  }

  /**
   * Returns the maximum height a negative region can have.
   *
   * @return		the maximum height, ignored if <1
   */
  public int getMaxHeight() {
    return m_MaxHeight;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String maxHeightTipText() {
    return "The maximum height that a negative region can have, ignored if <1.";
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

    // check max height/width
    if ((result != null) && ((m_MaxHeight > 0) || (m_MaxWidth > 0))) {
      i = 0;
      while (i < result.size()) {
        if (isStopped())
          break;
	if (m_MaxWidth > 0) {
	  if (result.get(i).getWidth() > m_MaxWidth) {
	    if (isLoggingEnabled())
	      getLogger().info("Removed, width too large: " + result.get(i) + " > " + m_MaxWidth);
	    result.remove(i);
	    continue;
	  }
	}
	if (m_MaxHeight > 0) {
	  if (result.get(i).getHeight() > m_MaxHeight) {
	    if (isLoggingEnabled())
	      getLogger().info("Removed, height too large: " + result.get(i) + " > " + m_MaxHeight);
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
