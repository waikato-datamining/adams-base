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
 * Grid.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.adams.transformer.subimages;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import adams.data.image.BufferedImageContainer;

/**
 <!-- globalinfo-start -->
 * Splits the image using a grid of specified number of columns and rows.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
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
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Grid
  extends AbstractSubImagesGenerator {

  /** for serialization. */
  private static final long serialVersionUID = 2488185528644078539L;

  /** the key for the column. */
  public final static String KEY_COLUMN = "Column";

  /** the key for the row. */
  public final static String KEY_ROW = "Row";
  
  /** the number of columns to use. */
  protected int m_NumCols;
  
  /** the number of rows to use. */
  protected int m_NumRows;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Splits the image using a grid of specified number of columns and rows.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "num-cols", "numCols",
	    1, 1, null);

    m_OptionManager.add(
	    "num-rows", "numRows",
	    1, 1, null);
  }

  /**
   * Sets the number of columns in the grid.
   *
   * @param value	the number of columns
   */
  public void setNumCols(int value) {
    if (value >= 1) {
      m_NumCols = value;
      reset();
    }
    else {
      getLogger().warning("Number of columns must be >= 1, provided: " + value);
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
    if (value >= 1) {
      m_NumRows = value;
      reset();
    }
    else {
      getLogger().warning("Number of rows must be >= 1, provided: " + value);
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
   * Performs the actual generation of the subimages.
   * 
   * @param image	the image to process
   * @return		the list of subimages generated
   */
  @Override
  protected List<BufferedImageContainer> doProcess(BufferedImageContainer image) {
    List<BufferedImageContainer>	result;
    BufferedImageContainer		cont;
    BufferedImage			bimage;
    int					dw;
    int					dh;
    int					w;
    int					h;
    int					x;
    int					y;
    int					width;
    int					height;
    
    result = new ArrayList<BufferedImageContainer>();
    
    bimage = image.getImage();
    dw     = bimage.getWidth() / m_NumCols;
    dh     = bimage.getHeight() / m_NumRows;

    if (isLoggingEnabled())
      getLogger().info("height=" + bimage.getHeight() + ", width=" + bimage.getWidth() + ", dh=" + dh + ", dw=" + dw);

    for (w = 0; w < m_NumCols; w++) {
      x = w * dw;
      if (w == m_NumCols - 1)
	width = bimage.getWidth() - x;
      else
	width = dw;
      
      for (h = 0; h < m_NumRows; h++) {
	y = h * dh;
	if (h == m_NumRows - 1)
	  height = bimage.getHeight() - y;
	else
	  height = dh;
	
	if (isLoggingEnabled())
	  getLogger().info("row=" + h + ", col=" + w + ", x=" + x + ", y=" + y + ", width=" + width + ", height=" + height);
	
	cont = (BufferedImageContainer) image.getHeader();
	cont.setImage(bimage.getSubimage(x, y, width, height));
	cont.getReport().setNumericValue(KEY_COLUMN, w);
	cont.getReport().setNumericValue(KEY_ROW,    h);
	result.add(cont);
      }
    }
    
    return result;
  }
}
