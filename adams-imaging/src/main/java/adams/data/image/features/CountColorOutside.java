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
 * CountColorOutside.java
 * Copyright (C) 2018 University of Waikato, Hamilton, New Zealand
 */

package adams.data.image.features;

import adams.data.image.BufferedImageContainer;
import adams.data.image.BufferedImageHelper;
import adams.data.image.IntArrayMatrixView;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @author  Dale (dale at cs dot waikato dot ac dot nz)
 */
public class CountColorOutside
  extends AbstractCountColor {

  /** for serialization. */
  private static final long serialVersionUID = -8349656592325229512L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Counts the occurrences of a specific color from the outside, going through the image line by line (alpha channel gets ignored).";
  }

  /**
   * Performs the actual feature generation.
   *
   * @param img		the image to process
   * @return		the generated features
   */
  @Override
  public List<Object>[] generateRows(BufferedImageContainer img) {
    List<Object>[]		result;
    BufferedImage		image;
    IntArrayMatrixView		view;
    int				x;
    int				y;
    int				lastX;
    int				count;
    int				color;

    image     = BufferedImageHelper.convert(img.getImage(), BufferedImage.TYPE_4BYTE_ABGR);
    count     = 0;
    color     = m_Color.getRGB() & 0x00FFFFFF;
    view      = new IntArrayMatrixView(BufferedImageHelper.getPixels(image), image.getWidth(), image.getHeight());
    for (y = 0; y < view.getHeight(); y++) {
      lastX = -1;
      // from left
      if ((view.get(0, y) & 0x00FFFFFF) == color) {
	for (x = 0; x < view.getWidth(); x++) {
	  if ((view.get(x, y) & 0x00FFFFFF) == color) {
	    lastX = x;
	    count++;
	  }
	  else {
	    break;
	  }
	}
      }
      // from right
      if ((lastX < view.getWidth() - 1) && (view.get(view.getWidth() - 1, y) & 0x00FFFFFF) == color) {
	for (x = view.getWidth() - 1; x >= 0 && x > lastX; x--) {
	  if ((view.get(x, y) & 0x00FFFFFF) == color)
	    count++;
	  else
	    break;
	}
      }
    }

    result    = new List[1];
    result[0] = new ArrayList<>();
    if (m_UsePercentage)
      result[0].add((double) count / (double) view.size());
    else
      result[0].add(count);

    return result;
  }
}
