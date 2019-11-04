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
 * LargestRegions.java
 * Copyright (C) 2018-2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.negativeregions;

import adams.data.image.AbstractImageContainer;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;

import java.util.Collections;
import java.util.Comparator;

/**
 * Returns only the top X largest regions from the base generator.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class LargestRegions
  extends AbstractMetaNegativeRegionsGenerator {

  private static final long serialVersionUID = -904202231629949668L;

  /** the maximum number of regions to return. */
  protected int m_MaxRegions;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Returns only the top X largest regions from the base generator.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "max-regions", "maxRegions",
      -1, -1, null);
  }

  /**
   * Sets the maximum number of regions to generate.
   *
   * @param value	the maximum, <1 returns all
   */
  public void setMaxRegions(int value) {
    if (getOptionManager().isValid("maxRegions", value)) {
      m_MaxRegions = value;
      reset();
    }
  }

  /**
   * Returns the maximum number of regions to generate.
   *
   * @return		the maximum, <1 returns all
   */
  public int getMaxRegions() {
    return m_MaxRegions;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String maxRegionsTipText() {
    return "The maximum number of regions to return; <1 returns all.";
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
    if ((m_MaxRegions < 1) || (result.size() <= m_MaxRegions))
      return result;

    Collections.sort(result, new Comparator<LocatedObject>() {
      @Override
      public int compare(LocatedObject o1, LocatedObject o2) {
	return -Double.compare(o1.getWidth()* o1.getHeight(), o2.getWidth()*o2.getHeight());
      }
    });
    while (result.size() > m_MaxRegions) {
      if (isStopped())
        break;
      result.remove(m_MaxRegions);
    }

    if (isStopped())
      result.clear();

    return result;
  }
}
