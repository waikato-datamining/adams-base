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
 * Draw.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.data.imagesegmentation.filter;

import adams.core.base.BaseKeyValuePair;
import adams.flow.container.ImageSegmentationContainer;
import adams.gui.core.ColorHelper;
import adams.gui.visualization.segmentation.ImageUtils;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * Draws the layers that have a color specified onto the base image.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class Draw
  extends AbstractImageSegmentationContainerFilter {

  private static final long serialVersionUID = 91438576971072522L;

  /** the layer/color mappings. */
  protected BaseKeyValuePair[] m_LayerColorMappings;

  /** the alpha value to use. */
  protected float m_Alpha;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Draws the layers that have a color specified onto the base image.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"layer-color-mapping", "layerColorMappings",
	new BaseKeyValuePair[0]);

    m_OptionManager.add(
	"alpha", "alpha",
	0.5f, 0.0f, 1.0f);
  }

  /**
   * Sets the mappings of label/color (in hex notation).
   *
   * @param value	the mappings
   */
  public void setLayerColorMappings(BaseKeyValuePair[] value) {
    m_LayerColorMappings = value;
    reset();
  }

  /**
   * Returns the mappings of label/color (in hex notation).
   *
   * @return		the mappings
   */
  public BaseKeyValuePair[] getLayerColorMappings() {
    return m_LayerColorMappings;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String layerColorMappingsTipText() {
    return "The mappings for layer name to color (in hex notation).";
  }

  /**
   * Sets the alpha value to use for the overlays (0: transparent, 1: opaque).
   *
   * @param value	the alpha value
   */
  public void setAlpha(float value) {
    m_Alpha = value;
    reset();
  }

  /**
   * Returns the alpha value to use for the overlays (0: transparent, 1: opaque).
   *
   * @return		the alpha value
   */
  public float getAlpha() {
    return m_Alpha;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String alphaTipText() {
    return "The alpha value to use for the overlays (0: transparent, 1: opaque).";
  }

  /**
   * Performs the filtering of the container.
   *
   * @param cont the container to filter
   * @return the filtered container
   */
  @Override
  protected ImageSegmentationContainer doFilter(ImageSegmentationContainer cont) {
    BufferedImage 	baseImg;
    BufferedImage 	layerImg;
    Graphics2D		g2d;
    String		layer;
    Color		color;

    if (m_LayerColorMappings.length > 0) {
      baseImg = cont.getBaseImage();
      g2d     = baseImg.createGraphics();
      g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getAlpha()));
      for (BaseKeyValuePair mapping : m_LayerColorMappings) {
        layer = mapping.getPairKey();
        color = ColorHelper.valueOf(mapping.getPairValue());
        if (cont.getLayers().containsKey(layer)) {
          layerImg = cont.getLayers().get(layer);
	  ImageUtils.initImage(layerImg, color);
	  g2d.drawImage(layerImg, null, 0, 0);
	}
      }
      g2d.dispose();
      cont.setValue(ImageSegmentationContainer.VALUE_BASE, baseImg);
    }

    return cont;
  }
}
