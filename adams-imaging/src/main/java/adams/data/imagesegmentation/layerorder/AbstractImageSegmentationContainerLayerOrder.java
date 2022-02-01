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
 * AbstractImageSegmentationContainerLayerOrder.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.data.imagesegmentation.layerorder;

import adams.core.option.AbstractOptionHandler;
import adams.flow.container.ImageSegmentationContainer;

/**
 * Ancestor for schemes that generate an ordering of image segmentation layers.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractImageSegmentationContainerLayerOrder
  extends AbstractOptionHandler {

  private static final long serialVersionUID = 6786494515827769804L;

  /**
   * Hook method for performing checks prior to generating the layer order.
   *
   * @param cont	the container to check
   * @return		null if checks successful, otherwise errors
   */
  protected String check(ImageSegmentationContainer cont) {
    if (cont == null)
      return "No image segmentation container provided!";
    if (cont.getLayers().isEmpty())
      return "No layers available from container!";
    return null;
  }

  /**
   * Generates the layer order.
   *
   * @param cont	the container to use
   * @return		the generated order
   */
  protected abstract String[] doGenerate(ImageSegmentationContainer cont);

  /**
   * Generates the layer order.
   *
   * @param cont	the container to use
   * @return		the generated order
   */
  public String[] generate(ImageSegmentationContainer cont) {
    String	msg;

    msg = check(cont);
    if (msg != null)
      throw new IllegalStateException(msg);

    return doGenerate(cont);
  }
}
