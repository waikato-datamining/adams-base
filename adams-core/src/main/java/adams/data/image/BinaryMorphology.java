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
 * BinaryMorphology.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.data.image;

/**
 * Helper class for morphology operations on binary image data.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BinaryMorphology {

  /**
   * Performs an erode operation.
   *
   * @param data	the data to "erode"
   * @return		the eroded data
   */
  public static boolean[][] erode(boolean[][] data) {
    boolean[][]	result;
    int		x;
    int		y;

    result = new boolean[data.length][data[0].length];
    for (y = 1; y < data.length - 1; y++) {
      for (x = 1; x < data[y].length - 1; x++) {
	result[y][x] =
	  data[y - 1][x - 1] && data[y - 1][x] && data[y - 1][x + 1]
	    && data[y][x - 1] && data[y][x] && data[y][x + 1]
	    && data[y + 1][x - 1] && data[y + 1][x] && data[y + 1][x + 1];
      }
    }

    return result;
  }

  /**
   * Performs a dilate operation.
   *
   * @param data	the data to "dilate"
   * @return		the dilated data
   */
  public static boolean[][] dilate(boolean[][] data) {
    boolean[][]	result;
    int		x;
    int		y;

    result = new boolean[data.length][data[0].length];
    for (y = 1; y < data.length - 1; y++) {
      for (x = 1; x < data[y].length - 1; x++) {
	result[y][x] =
	  data[y - 1][x - 1] || data[y - 1][x] || data[y - 1][x + 1]
	    || data[y][x - 1] || data[y][x] || data[y][x + 1]
	    || data[y + 1][x - 1] || data[y + 1][x] || data[y + 1][x + 1];
      }
    }

    return result;
  }
}
