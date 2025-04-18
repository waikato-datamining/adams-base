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
 * IntArrayMatrixView.java
 * Copyright (C) 2014-2022 University of Waikato, Hamilton, New Zealand
 */
package adams.data.image;

import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetSupporter;

import java.awt.image.BufferedImage;
import java.io.Serializable;

/**
 * Allows a matrix view (2-dim) of an array (1-dim).
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class IntArrayMatrixView
  implements Serializable, BufferedImageSupporter, SpreadSheetSupporter {

  /** for serialization. */
  private static final long serialVersionUID = -5901549787330341842L;

  /** the underlaying array. */
  protected int[] m_Data;
  
  /** the width of the matrix. */
  protected int m_Width;
  
  /** the height of the matrix. */
  protected int m_Height;
  
  /**
   * Initializes the matrix view with an empty array.
   * 
   * @param width	the width of the matrix
   * @param height	the height of the matrix
   */
  public IntArrayMatrixView(int width, int height) {
    this(new int[width * height], width, height);
  }
  
  /**
   * Initializes the matrix view.
   * 
   * @param data	the 1-dim array data
   * @param width	the width of the matrix
   * @param height	the height of the matrix
   */
  public IntArrayMatrixView(int[] data, int width, int height) {
    if (data.length != width * height)
      throw new IllegalArgumentException("Length of array and width*height don't match: " + data.length + " != " + width * height);
    
    m_Data   = data;
    m_Width  = width;
    m_Height = height;
  }
  
  /**
   * Returns the underlying data.
   * 
   * @return		the data
   */
  public int[] getData() {
    return m_Data;
  }
  
  /**
   * Returns the width of the matrix.
   * 
   * @return 		the width
   */
  public int getWidth() {
    return m_Width;
  }
  
  /**
   * Returns the height of the matrix.
   * 
   * @return 		the height
   */
  public int getHeight() {
    return m_Height;
  }

  /**
   * Returns the number of data points (height * width).
   *
   * @return		the number of data points
   */
  public int size() {
    return m_Width * m_Height;
  }

  /**
   * Returns the value at the specified position.
   *
   * @param position	the 0-based position in the underlying array
   * @return		the value at the position
   */
  public int get(int position) {
    return m_Data[position];
  }

  /**
   * Returns the RGBA values at the specified location.
   *
   * @param position	the 0-based position in the underlying array
   * @return		the RGBA values at the position
   */
  public int[] getRGBA(int position) {
    return BufferedImageHelper.split(get(position));
  }

  /**
   * Returns the value at the specified location.
   * 
   * @param x		the 0-based column
   * @param y		the 0-based row
   * @return		the value at the position
   */
  public int get(int x, int y) {
    return m_Data[m_Width * y + x];
  }
  
  /**
   * Returns the RGBA values at the specified location.
   * 
   * @param x		the 0-based column
   * @param y		the 0-based row
   * @return		the RGBA values at the position
   */
  public int[] getRGBA(int x, int y) {
    return BufferedImageHelper.split(get(x, y));
  }

  /**
   * Sets the value at the specified location.
   *
   * @param position	the 0-based position in the underlying array
   * @param value	the value to set
   */
  public void set(int position, int value) {
    m_Data[position] = value;
  }

  /**
   * Sets the RGBA values at the specified location.
   *
   * @param position	the 0-based position in the underlying array
   * @param rgba	the RGBA values to set
   */
  public void setRGBA(int position, int[] rgba) {
    set(position, BufferedImageHelper.combine(rgba));
  }

  /**
   * Sets the value at the specified location.
   * 
   * @param x		the 0-based column
   * @param y		the 0-based row
   * @param value	the value to set
   */
  public void set(int x, int y, int value) {
    m_Data[m_Width * y + x] = value;
  }
  
  /**
   * Sets the RGBA values at the specified location.
   * 
   * @param x		the 0-based column
   * @param y		the 0-based row
   * @param rgba	the RGBA values to set
   */
  public void setRGBA(int x, int y, int[] rgba) {
    set(x, y, BufferedImageHelper.combine(rgba));
  }
  
  /**
   * Returns a short description of the view.
   * 
   * @return		the description
   */
  @Override
  public String toString() {
    return "w=" + m_Width + ", h=" + m_Height;
  }

  /**
   * Turns the matrix into an image.
   *
   * @param type	the BufferedImage type
   * @return		the image
   */
  public BufferedImage toBufferedImage(int type) {
    BufferedImage 	result;

    result = new BufferedImage(getWidth(), getHeight(), type);
    result.setRGB(0, 0, getWidth(), getHeight(), getData(), 0, getWidth());

    return result;
  }

  /**
   * Turns the matrix into an image.
   *
   * @return		the image
   */
  public BufferedImage toBufferedImage() {
    return toBufferedImage(BufferedImage.TYPE_INT_RGB);
  }

  /**
   * Returns the content as spreadsheet.
   *
   * @return		the content
   */
  @Override
  public SpreadSheet toSpreadSheet() {
    SpreadSheet		result;
    Row			row;
    int			i;
    int			n;

    result = new DefaultSpreadSheet();

    // header
    row = result.getHeaderRow();
    for (i = 0; i < getWidth(); i++)
      row.addCell("" + i).setContentAsString("" + (i+1));

    // data
    for (n = 0; n < getHeight(); n++) {
      row = result.addRow();
      for (i = 0; i < getWidth(); i++)
	row.addCell("" + i).setContent(get(i, n));
    }

    return result;
  }
}
