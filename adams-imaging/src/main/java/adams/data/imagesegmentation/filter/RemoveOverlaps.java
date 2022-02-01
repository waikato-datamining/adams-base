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
 * RemoveOverlaps.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.data.imagesegmentation.filter;

import adams.data.image.BufferedImageHelper;
import adams.data.image.IntArrayMatrixView;
import adams.data.imagesegmentation.layerorder.AbstractImageSegmentationContainerLayerOrder;
import adams.data.imagesegmentation.layerorder.AsIs;
import adams.flow.container.ImageSegmentationContainer;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Removes any overlaps of annotations between the layers.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class RemoveOverlaps
    extends AbstractImageSegmentationContainerFilter {

  private static final long serialVersionUID = -6559340258634055902L;

  /** how to order the layers. */
  protected AbstractImageSegmentationContainerLayerOrder m_Order;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Removes any overlaps of annotations between the layers.\n"
	+ "The top-most layer, as returned by the layer ordering scheme, will stay "
	+ "and subsequent layers will have their annotations removed if they overlap "
	+ "with any previos layer(s).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"order", "order",
	new AsIs());
  }

  /**
   * Sets the order to use.
   *
   * @param value	the order
   */
  public void setOrder(AbstractImageSegmentationContainerLayerOrder value) {
    m_Order = value;
    reset();
  }

  /**
   * Returns the order to use.
   *
   * @return		the order
   */
  public AbstractImageSegmentationContainerLayerOrder getOrder() {
    return m_Order;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String orderTipText() {
    return "How to order the layers.";
  }

  /**
   * Performs the filtering of the container.
   *
   * @param cont the container to filter
   * @return the filtered container
   */
  @Override
  protected ImageSegmentationContainer doFilter(ImageSegmentationContainer cont) {
    int 				black;
    ImageSegmentationContainer  	result;
    List<String> 			names;
    Map<String,IntArrayMatrixView> 	pixels;
    IntArrayMatrixView			viewPrev;
    IntArrayMatrixView			viewCurr;
    BufferedImage			img;
    int					n;
    int					i;
    Map<String,Boolean>			modified;
    boolean				modifiedAny;
    boolean				modifiedCurr;
    Map<String,BufferedImage>		layers;
    Map<String,BufferedImage>		layersNew;

    result = cont;
    black  = Color.BLACK.getRGB();
    layers = cont.getLayers();

    // init
    pixels = new HashMap<>();
    names  = new ArrayList<>();
    for (String name: m_Order.generate(cont)) {
      if (!layers.containsKey(name))
        continue;
      names.add(name);
      img = layers.get(name);
      pixels.put(name, new IntArrayMatrixView(BufferedImageHelper.getPixels(img), img.getWidth(), img.getHeight()));
    }

    // check layers
    modified = new HashMap<>();
    for (String name: layers.keySet())
      modified.put(name, false);
    for (n = 1; n < names.size(); n++) {
      viewPrev     = pixels.get(names.get(n - 1));
      viewCurr     = pixels.get(names.get(n));
      modifiedCurr = false;
      for (i = 0; i < viewCurr.size(); i++) {
        if ((viewCurr.get(i) | 0xFF000000) != black) {
          if ((viewPrev.get(i) | 0xFF000000) != black) {
            viewCurr.set(i, black & 0x00FFFFFF);
            modifiedCurr = true;
	  }
	}
      }
      if (modifiedCurr)
        modified.put(names.get(n), true);
    }

    // create output
    modifiedAny = false;
    for (String name: modified.keySet())
      modifiedAny = modifiedAny || modified.get(name);
    if (modifiedAny) {
      layersNew = new HashMap<>();
      for (String name: layers.keySet()) {
        if (pixels.containsKey(name)) {
	  img = layers.get(name);
          layersNew.put(name, pixels.get(name).toBufferedImage(img.getType()));
	}
        else {
          layersNew.put(name, layers.get(name));
	}
      }
      result = new ImageSegmentationContainer(
          cont.getValue(ImageSegmentationContainer.VALUE_NAME, String.class),
	  cont.getBaseImage(), layersNew);
    }

    return result;
  }
}
