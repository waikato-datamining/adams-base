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
 * SimpleFloodFill.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.negativeregions;

import adams.data.image.AbstractImageContainer;
import adams.flow.transformer.locateobjects.LocatedObjects;

/**
 * Flood-fill inspired region generator.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SimpleFloodFill
  extends AbstractNegativeRegionsGenerator {

  private static final long serialVersionUID = -3098590558581645598L;

  /** the number of columns. */
  protected int m_NumCols;

  /** the number of rows. */
  protected int m_NumRows;

  /** the scale factor to use on the image size. */
  protected double m_ScaleFactor;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Flood-fill inspired region generator.\n"
      + "Generates a grid of starting points for flood-fill, removes any starting "
      + "points that fall within existing annotations. From these starting points, "
      + "it uses flood-fill approach to find largest extents in x and y. Any "
      + "encompassed starting points get removed while flood-filling. Stops, once "
      + "all starting points have been consumed.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "num-cols", "numCols",
      20, 1, null);

    m_OptionManager.add(
      "num-rows", "numRows",
      15, 1, null);

    m_OptionManager.add(
      "scale-factor", "scaleFactor",
      1.0, 0.0001, null);
  }

  /**
   * Sets the number of columns in the grid.
   *
   * @param value	the number
   */
  public void setNumCols(int value) {
    if (getOptionManager().isValid("numCols", value)) {
      m_NumCols = value;
      reset();
    }
  }

  /**
   * Returns the number of columns in the grid.
   *
   * @return		the number
   */
  public int getNumCols() {
    return m_NumCols;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numColsTipText() {
    return "The number of columns in the grid for starting points.";
  }

  /**
   * Sets the number of rows in the grid.
   *
   * @param value	the number
   */
  public void setNumRows(int value) {
    if (getOptionManager().isValid("numRows", value)) {
      m_NumRows = value;
      reset();
    }
  }

  /**
   * Returns the number of rows in the grid.
   *
   * @return		the number
   */
  public int getNumRows() {
    return m_NumRows;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numRowsTipText() {
    return "The number of rows in the grid for starting points.";
  }

  /**
   * Sets the scale factor for the image.
   *
   * @param value	the factor
   */
  public void setScaleFactor(double value) {
    if (getOptionManager().isValid("scaleFactor", value)) {
      m_ScaleFactor = value;
      reset();
    }
  }

  /**
   * Returns the scale factor for the image.
   *
   * @return		the factor
   */
  public double getScaleFactor() {
    return m_ScaleFactor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String scaleFactorTipText() {
    return "The scale factor to use on the image.";
  }

  /**
   * Generates the negative regions.
   *
   * @param cont	the image container to generate the regions for
   * @return		the generated regions
   */
  @Override
  protected LocatedObjects doGenerateRegions(AbstractImageContainer cont) {
    // TODO
    return new LocatedObjects();
  }
}
