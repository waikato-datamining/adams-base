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
 * Sorted.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.data.imagesegmentation.layerorder;

import adams.flow.container.ImageSegmentationContainer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Returns the layers in sorted order.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class Sorted
  extends AbstractImageSegmentationContainerLayerOrder {

  private static final long serialVersionUID = -2366275466947298671L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Returns the layers in sorted order.";
  }

  /**
   * Generates the layer order.
   *
   * @param cont the container to use
   * @return the generated order
   */
  @Override
  protected String[] doGenerate(ImageSegmentationContainer cont) {
    List<String> 	result;

    result = new ArrayList<>(cont.getLayers().keySet());
    Collections.sort(result);
    return result.toArray(new String[0]);
  }
}
