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
 * MergeGrid.java
 * Copyright (C) 2020 University of Waikato, Hamilton, New Zealand
 */

package adams.data.image.multiimageoperation;

import adams.core.ClassCrossReference;
import adams.core.Utils;
import adams.data.image.BufferedImageContainer;
import adams.data.image.transformer.subimages.Grid;
import adams.data.objectfilter.Translate;
import adams.flow.transformer.DeleteOverlappingImageObjects;
import adams.flow.transformer.locateobjects.LocatedObjects;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

/**
 <!-- globalinfo-start -->
 * Merges a grid of sub-images into a single image, including the annotations.<br>
 * Opposite operation to adams.data.image.transformer.subimages.Grid.<br>
 * Expects the image array to represent the cells in the grid in a row-wise fashion.<br>
 * Only combines object annotations from reports, other field values will be discarded.<br>
 * Does not remove overlapping objects, see adams.flow.transformer.DeleteOverlappingImageObjects.<br>
 * <br>
 * See also:<br>
 * adams.data.image.transformer.subimages.Grid<br>
 * adams.flow.transformer.DeleteOverlappingImageObjects
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-prefix &lt;java.lang.String&gt; (property: prefix)
 * &nbsp;&nbsp;&nbsp;The report field prefix used in the report.
 * &nbsp;&nbsp;&nbsp;default: Object.
 * </pre>
 *
 * <pre>-num-cols &lt;int&gt; (property: numCols)
 * &nbsp;&nbsp;&nbsp;The number of columns.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-num-rows &lt;int&gt; (property: numRows)
 * &nbsp;&nbsp;&nbsp;The number of rows.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-overlap-x &lt;int&gt; (property: overlapX)
 * &nbsp;&nbsp;&nbsp;The overlap on the x axis.
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-overlap-y &lt;int&gt; (property: overlapY)
 * &nbsp;&nbsp;&nbsp;The overlap on the y axis.
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class MergeGrid
  extends AbstractBufferedImageMultiImageOperation
  implements ClassCrossReference {

  private static final long serialVersionUID = 1888786897723421704L;

  /** the prefix to use when generating a report. */
  protected String m_Prefix;

  /** the number of columns to use. */
  protected int m_NumCols;

  /** the number of rows to use. */
  protected int m_NumRows;

  /** the overlap on the x axis. */
  protected int m_OverlapX;

  /** the overlap on the y axis. */
  protected int m_OverlapY;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Merges a grid of sub-images into a single image, including the annotations.\n"
      + "Opposite operation to " + Utils.classToString(Grid.class) + ".\n"
      + "Expects the image array to represent the cells in the grid in a row-wise fashion.\n"
      + "Only combines object annotations from reports, other field values will be discarded.\n"
      + "Does not remove overlapping objects, see " + Utils.classToString(DeleteOverlappingImageObjects.class) + ".";
  }

  /**
   * Returns the cross-referenced classes.
   *
   * @return		the classes
   */
  public Class[] getClassCrossReferences() {
    return new Class[]{Grid.class, DeleteOverlappingImageObjects.class};
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "prefix", "prefix",
      "Object.");

    m_OptionManager.add(
      "num-cols", "numCols",
      1, 1, null);

    m_OptionManager.add(
      "num-rows", "numRows",
      1, 1, null);

    m_OptionManager.add(
      "overlap-x", "overlapX",
      0, 0, null);

    m_OptionManager.add(
      "overlap-y", "overlapY",
      0, 0, null);
  }

  /**
   * Sets the field prefix used in the report.
   *
   * @param value 	the field prefix
   */
  public void setPrefix(String value) {
    m_Prefix = value;
    reset();
  }

  /**
   * Returns the field prefix used in the report.
   *
   * @return 		the field prefix
   */
  public String getPrefix() {
    return m_Prefix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String prefixTipText() {
    return "The report field prefix used in the report.";
  }

  /**
   * Sets the number of columns in the grid.
   *
   * @param value	the number of columns
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
   * @return		the number of columns
   */
  public int getNumCols() {
    return m_NumCols;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String numColsTipText() {
    return "The number of columns.";
  }

  /**
   * Sets the number of rows in the grid.
   *
   * @param value	the number of rows
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
   * @return		the number of rows
   */
  public int getNumRows() {
    return m_NumRows;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String numRowsTipText() {
    return "The number of rows.";
  }

  /**
   * Sets the overlap on the x axis.
   *
   * @param value	the overlap
   */
  public void setOverlapX(int value) {
    if (getOptionManager().isValid("overlapX", value)) {
      m_OverlapX = value;
      reset();
    }
  }

  /**
   * Returns the overlap on the x axis.
   *
   * @return		the overlap
   */
  public int getOverlapX() {
    return m_OverlapX;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String overlapXTipText() {
    return "The overlap on the x axis.";
  }

  /**
   * Sets the overlap on the y axis.
   *
   * @param value	the overlap
   */
  public void setOverlapY(int value) {
    if (getOptionManager().isValid("overlapY", value)) {
      m_OverlapY = value;
      reset();
    }
  }

  /**
   * Returns the overlap on the y axis.
   *
   * @return		the overlap
   */
  public int getOverlapY() {
    return m_OverlapY;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String overlapYTipText() {
    return "The overlap on the y axis.";
  }

  /**
   * Returns the minimum number of images that are required for the operation.
   *
   * @return		the number of images that are required, <= 0 means no lower limit
   */
  @Override
  public int minNumImagesRequired() {
    return m_NumCols * m_NumRows;
  }

  /**
   * Returns the maximum number of images that are required for the operation.
   *
   * @return		the number of images that are required, <= 0 means no upper limit
   */
  public int maxNumImagesRequired() {
    return m_NumCols * m_NumRows;
  }

  /**
   * Determines the dimensions of the combined images.
   *
   * @param images	the images making up the grid
   * @return		the combined dimensions
   */
  protected Dimension determineDimensions(BufferedImageContainer[] images) {
    int		width;
    int		height;
    int		i;

    width  = 0;
    height = 0;

    // width
    for (i = 0; i < m_NumCols; i++) {
      width += images[i].getWidth();
      if (i > 0)
        width -= m_OverlapX;
    }

    // height
    for (i = 0; i < m_NumRows; i += m_NumCols) {
      height += images[i].getHeight();
      if (i > 0)
        height -= m_OverlapY;
    }

    return new Dimension(width, height);
  }

  /**
   * Calculates the X or Y offset for the specified image from the array for
   * insertion in the new image.
   *
   * @param images	the array images to use
   * @param index	the index in the array to calculate the offset for
   * @param calcX	whether to calculate the X or Y offset
   * @return		the offset
   */
  protected int calcOffset(BufferedImageContainer[] images, int index, boolean calcX) {
    int		result;
    int		x;
    int		y;
    int		i;

    result = 0;
    x = index % m_NumCols;
    y = index / m_NumCols;

    if (calcX) {
      for (i = 0; i < x; i++) {
	result += images[i].getWidth();
	result -= m_OverlapX;
      }
    }
    else {
      for (i = 0; i < y; i++) {
	result += images[i * m_NumCols].getHeight();
	result -= m_OverlapY;
      }
    }

    return result;
  }

  /**
   * Performs the actual processing of the images.
   *
   * @param images	the images to process
   * @return		the generated image(s)
   */
  @Override
  protected BufferedImageContainer[] doProcess(BufferedImageContainer[] images) {
    BufferedImageContainer[]	result;
    Dimension			dims;
    BufferedImage 		newImg;
    int                         i;
    int				x;
    int				y;
    BufferedImage		img;
    int[]                       pixels;
    LocatedObjects		all;
    LocatedObjects		objs;
    Translate			trans;

    result    = new BufferedImageContainer[1];
    dims      = determineDimensions(images);
    if (isLoggingEnabled())
      getLogger().info("Output dimensions: width=" + dims.width + ", height=" + dims.height);
    newImg    = new BufferedImage(dims.width, dims.height, BufferedImage.TYPE_INT_ARGB);
    result[0] = (BufferedImageContainer) images[0].getHeader();
    result[0].setImage(newImg);

    all = new LocatedObjects();
    for (i = 0; i < images.length; i++) {
      // image
      x      = calcOffset(images, i, true);
      y      = calcOffset(images, i, false);
      if (isLoggingEnabled())
        getLogger().info("Image #" + (i+1) + ": x=" + x + ", y=" + y);
      img    = images[i].getImage();
      pixels = img.getRGB(0, 0, img.getWidth(), img.getHeight(), null, 0, img.getWidth());
      newImg.setRGB(x, y, img.getWidth(), img.getHeight(), pixels, 0, img.getWidth());
      // annotations
      objs   = LocatedObjects.fromReport(images[i].getReport(), m_Prefix);
      if ((x > 0) || (y > 0)) {
	trans = new Translate();
	trans.setX(x);
	trans.setY(y);
	objs = trans.filter(objs);
      }
      all.addAll(objs);
    }

    result[0].getReport().mergeWith(all.toReport(m_Prefix));

    return result;
  }
}
