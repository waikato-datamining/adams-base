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
 * BooleanArrayMatrixView.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package adams.data.image;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.Serializable;

/**
 * Allows a matrix view (2-dim) of an array (1-dim).
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BooleanArrayMatrixView
  implements Serializable, BufferedImageSupporter {

  /** for serialization. */
  private static final long serialVersionUID = -5901549787330341842L;

  /** the underlaying array. */
  protected boolean[] m_Data;

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
  public BooleanArrayMatrixView(int width, int height) {
    this(new boolean[width * height], width, height);
  }

  /**
   * Initializes the matrix view the provided boolean matrix (gets turned
   * into an array in the process).
   *
   * @param data	the matrix to use
   */
  public BooleanArrayMatrixView(boolean[][] data) {
    this(matrixToArray(data), data[0].length, data.length);
  }

  /**
   * Initializes the matrix view.
   *
   * @param data	the 1-dim array data
   * @param width	the width of the matrix
   * @param height	the height of the matrix
   */
  public BooleanArrayMatrixView(boolean[] data, int width, int height) {
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
  public boolean[] getData() {
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
   * Returns the value at the specified location.
   *
   * @param x		the 0-based column
   * @param y		the 0-based row
   * @return		the value at the position
   */
  public boolean get(int x, int y) {
    return m_Data[m_Width * y + x];
  }

  /**
   * Sets the value at the specified location.
   *
   * @param x		the 0-based column
   * @param y		the 0-based row
   * @param value	the value to set
   */
  public void set(int x, int y, boolean value) {
    m_Data[m_Width * y + x] = value;
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
    int			white;
    int			black;
    int[]		data;
    int			i;

    white  = Color.WHITE.getRGB();
    black  = Color.BLACK.getRGB();
    data   = new int[m_Data.length];
    for (i = 0; i < m_Data.length; i++)
      data[i] = !m_Data[i] ? white : black;
    result = new BufferedImage(getWidth(), getHeight(), type);
    result.setRGB(0, 0, getWidth(), getHeight(), data, 0, getWidth());

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
   * Turns the matrix into an array.
   *
   * @param data	the matrix to convert
   * @return		the generated array
   */
  public static boolean[] matrixToArray(boolean[][] data) {
    boolean[]	result;
    int		x;
    int		y;
    int		i;

    result = new boolean[data[0].length * data.length];
    i      = 0;
    for (y = 0; y < data.length; y++) {
      for (x = 0; x < data[y].length; x++) {
	result[i] = data[y][x];
	i++;
      }
    }

    return result;
  }
}
