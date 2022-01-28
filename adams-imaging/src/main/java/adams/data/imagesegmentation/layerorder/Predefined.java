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
 * Predefined.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.data.imagesegmentation.layerorder;

import adams.core.base.BaseObject;
import adams.core.base.BaseString;
import adams.flow.container.ImageSegmentationContainer;

import java.util.List;

/**
 * Returns the layers in sorted order.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class Predefined
  extends AbstractImageSegmentationContainerLayerOrder {

  private static final long serialVersionUID = -2366275466947298671L;

  /** the predefined layer order. */
  protected BaseString[] m_Layers;

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
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
        "layer", "layers",
        new BaseString[0]);
  }

  /**
   * Sets the predefined order. Any additioanl layers get appended.
   *
   * @param value	the order
   */
  public void setLayers(BaseString[] value) {
    m_Layers = value;
    reset();
  }

  /**
   * Returns the predefined order. Any additional layers get appended.
   *
   * @return		the order
   */
  public BaseString[] getLayers() {
    return m_Layers;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String layersTipText() {
    return "The predefined order; any addition layers get appended.";
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

    result = BaseObject.toStringList(m_Layers);
    for (String layer: cont.getLayers().keySet()) {
      if (!result.contains(layer))
        result.add(layer);
    }
    return result.toArray(new String[0]);
  }
}
