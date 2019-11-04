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
 * SimpleBlockFill.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.negativeregions;

import adams.data.RoundingType;
import adams.data.image.AbstractImageContainer;
import adams.data.image.IntArrayMatrixView;
import adams.data.objectfilter.Scale;
import adams.data.objectfinder.AllFinder;
import adams.data.objectfinder.ObjectFinder;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;

import java.awt.Color;

/**
 * Finds largest blocks from starting points on grid.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SimpleBlockFill
  extends AbstractNegativeRegionsGenerator {

  private static final long serialVersionUID = -3098590558581645598L;

  /** the value for annotation. */
  public final static int ANNOTATION = Color.WHITE.getRGB();

  /** the value for a negative region. */
  public final static int NEGATIVE = Color.RED.getRGB();

  /** the object finder to use. */
  protected ObjectFinder m_Finder;

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
    return "Finds largest blocks from starting points on grid.\n"
      + "Generates a grid of starting points, removes any starting "
      + "points that fall within existing annotations. From these starting points, "
      + "it find largest blocks in x and y. Once regions have been located, overlaps get removed.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "finder", "finder",
      new AllFinder());

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
   * Sets the object finder to use.
   *
   * @param value 	the finder
   */
  public void setFinder(ObjectFinder value) {
    m_Finder = value;
    reset();
  }

  /**
   * Returns the object finder in use.
   *
   * @return 		the finder
   */
  public ObjectFinder getFinder() {
    return m_Finder;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String finderTipText() {
    return "The object finder to use for locating objects in the report.";
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
   * Finds the largest horizontal or vertical extent from the specified position
   * without hitting an annotation.
   *
   * @param matrix	the matrix with the annotations
   * @param s		the x starting point
   * @param t 		the y starting point
   * @param horizontal	whether to determine extent horizontally or vertically
   * @param ext		for storing the largest extent
   */
  protected void findExtent(IntArrayMatrixView matrix, int s, int t, boolean horizontal, int[] ext) {
    int 	x;
    int 	y;

    ext[0] = -1;
    ext[1] = -1;

    if (horizontal) {
      // left
      for (x = s; x >= 0; x--) {
	if (matrix.get(x, t) != 0)
	  break;
	ext[0] = x;
      }
      // right
      for (x = s; x < matrix.getWidth(); x++) {
	if (matrix.get(x, t) != 0)
	  break;
	ext[1] = x;
      }
    }
    else {
      // up
      for (y = t; y >= 0; y--) {
	if (matrix.get(s, y) != 0)
	  break;
	ext[0] = y;
      }
      // down
      for (y = t; y < matrix.getHeight(); y++) {
	if (matrix.get(s, y) != 0)
	  break;
	ext[1] = y;
      }
    }
  }

  /**
   * Checks whether the extent is valid.
   *
   * @param ext		the extent to check
   * @return		true if valid
   */
  protected boolean isValidExtent(int[] ext) {
    return (ext[0] > -1) && (ext[1] > -1) && (ext[0] <= ext[1]);
  }

  /**
   * Fills in the area of the object with the specified color.
   *
   * @param matrix	the matrix to update
   * @param obj		the object to fill in
   * @param color	the color to use
   */
  protected void fillInArea(IntArrayMatrixView matrix, LocatedObject obj, int color) {
    int		x;
    int		y;
    
    if ((obj.getX() < 0) || (obj.getX() >= matrix.getWidth())) {
      getLogger().warning("X outside: " + obj.getX() + " - [0; " + (matrix.getWidth() - 1) + "]");
      return;
    }
    if ((obj.getY() < 0) || (obj.getY() >= matrix.getHeight())) {
      getLogger().warning("Y outside: " + obj.getY() + " - [0; " + (matrix.getHeight() - 1) + "]");
      return;
    }
    if ((obj.getWidth() < 0) || ((obj.getX() + obj.getWidth()) >= matrix.getWidth())) {
      getLogger().warning("X+Width outside: " + (obj.getX() + obj.getWidth()) + " - [0; " + (matrix.getWidth() - 1) + "]");
      return;
    }
    if ((obj.getHeight() < 0) || ((obj.getY() + obj.getHeight()) >= matrix.getHeight())) {
      getLogger().warning("Y+Height outside: " + (obj.getY() + obj.getHeight()) + " - [0; " + (matrix.getHeight() - 1) + "]");
      return;
    }
    
    for (y = obj.getY(); y < obj.getY() + obj.getHeight(); y++) {
      for (x = obj.getX(); x < obj.getX() + obj.getWidth(); x++) {
	matrix.set(x, y, color);
      }
    }
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
    LocatedObject 	region;
    LocatedObjects	annotations;
    Scale		scale;
    int			width;
    int			height;
    int			offsetX;
    int			offsetY;
    int			tileWidth;
    int			tileHeight;
    IntArrayMatrixView	matrix;
    int			x;
    int			y;
    int			s;
    int			t;
    int			m;
    int			n;
    int[]		extX;
    int[]		extY;
    int[]		newX;
    int[]		newY;

    annotations = m_Finder.findObjects(cont.getReport());
    if (isLoggingEnabled())
      getLogger().info("# objects: " + annotations.size());

    // scale annotations
    if (m_ScaleFactor != 1.0) {
      if (isLoggingEnabled())
        getLogger().info("Scaling with factor: " + m_ScaleFactor);
      scale = new Scale();
      scale.setScaleX(m_ScaleFactor);
      scale.setScaleY(m_ScaleFactor);
      scale.setRoundingType(RoundingType.ROUND);
      annotations = scale.filter(annotations);
    }

    // init matrix for floodfill
    width      = (int) (cont.getWidth() * m_ScaleFactor);
    height     = (int) (cont.getHeight() * m_ScaleFactor);
    tileWidth  = width / m_NumCols;
    tileHeight = height / m_NumRows;
    offsetX    = tileWidth / 2;
    offsetY    = tileHeight / 2;
    matrix     = new IntArrayMatrixView(width, height);
    if (isLoggingEnabled()) {
      getLogger().info(
	"width=" + width + ", height=" + height
	  + ", tileWidth=" + tileWidth + ", tileHeight=" + tileHeight
	  + ", offsetX=" + offsetX + ", offsetY=" + offsetY);
    }

    // fill in annotations
    for (LocatedObject obj: annotations)
      fillInArea(matrix, obj, ANNOTATION);

    // find regions
    result = new LocatedObjects();
    extX       = new int[2];
    extY       = new int[2];
    newX       = new int[2];
    newY       = new int[2];
    for (y = 0; y < m_NumRows; y++) {
      for (x = 0; x < m_NumCols; x++) {
        s = x * tileWidth + offsetX;
        t = y * tileHeight + offsetY;
        if (matrix.get(s, t) != 0)
          continue;
        if (isLoggingEnabled())
          getLogger().info("row=" + y + ", col=" + x + ", y=" + t + ", x=" + s);

        // horizontal
	findExtent(matrix, s, t, true, extX);
	if (isValidExtent(extX)) {
	  extY[0] = -1;
	  extY[1] = -1;
	  // move up
	  for (n = t - 1; n >= 0; n--) {
	    findExtent(matrix, s, n, true, newX);
	    if (!isValidExtent(newX) || (newX[0] > extX[0]) || (newX[1] < extX[1]))
	      break;
	    extY[0] = n;
	  }
	  // move down
	  for (n = t + 1; n < height; n++) {
	    findExtent(matrix, s, n, true, newX);
	    if (!isValidExtent(newX) || (newX[0] > extX[0]) || (newX[1] < extX[1]))
	      break;
	    extY[1] = n;
	  }
	  if (isValidExtent(extY)) {
	    region = new LocatedObject(null, extX[0], extY[0], extX[1] - extX[0] + 1, extY[1] - extY[0] + 1);
	    result.add(region);
	    if (isLoggingEnabled())
	      getLogger().info("horizontal block found: " + region);
	    fillInArea(matrix, region, NEGATIVE);
	  }
	}

	// vertical
	findExtent(matrix, s, t, false, extY);
	if (isValidExtent(extY)) {
	  extX[0] = -1;
	  extX[1] = -1;
	  // move left
	  for (m = s - 1; m >= 0; m--) {
	    findExtent(matrix, m, t, false, newY);
	    if (!isValidExtent(newY) || (newY[0] > extY[0]) || (newY[1] < extY[1]))
	      break;
	    extX[0] = m;
	  }
	  // move right
	  for (m = s + 1; m < width; m++) {
	    findExtent(matrix, m, t, false, newY);
	    if (!isValidExtent(newY) || (newY[0] > extY[0]) || (newY[1] < extY[1]))
	      break;
	    extX[1] = m;
	  }
	  if (isValidExtent(extX)) {
	    region = new LocatedObject(null, extX[0], extY[0], extX[1] - extX[0] + 1, extY[1] - extY[0] + 1);
	    result.add(region);
	    if (isLoggingEnabled())
	      getLogger().info("vertical block found: " + region);
	    fillInArea(matrix, region, NEGATIVE);
	  }
	}
      }
    }

    // scale regions back into original space
    if (m_ScaleFactor != 1.0) {
      if (isLoggingEnabled())
        getLogger().info("Reverse scaling of negative regions: " + m_ScaleFactor);
      scale = new Scale();
      scale.setScaleX(1.0 / m_ScaleFactor);
      scale.setScaleY(1.0 / m_ScaleFactor);
      scale.setRoundingType(RoundingType.ROUND);
      result = scale.filter(result);
    }

    return result;
  }
}
