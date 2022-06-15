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
 * MergeLayers.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.data.imagesegmentation.filter;

import adams.core.base.BaseString;
import adams.data.image.BufferedImageHelper;
import adams.flow.container.ImageSegmentationContainer;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Merges two or more layers into a new one (or replacing an existing one).
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class MergeLayers
  extends AbstractImageSegmentationContainerFilter {

  private static final long serialVersionUID = -8231656612700614421L;

  /** the layers to merge. */
  protected BaseString[] m_LayersToMerge;

  /** the new layer to generate. */
  protected String m_NewLayer;

  /** whether to delete the layers used for merging. */
  protected boolean m_DeleteOldLayers;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Merges two or more layers into a new one (or replacing an existing one).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"layer-to-merge", "layersToMerge",
	new BaseString[0]);

    m_OptionManager.add(
	"new-layer", "newLayer",
	"");

    m_OptionManager.add(
	"delete-old-layers", "deleteOldLayers",
	false);
  }

  /**
   * Sets the names of the layers to merge.
   *
   * @param value	the names
   */
  public void setLayersToMerge(BaseString[] value) {
    m_LayersToMerge = value;
    reset();
  }

  /**
   * Returns the names of the layers to merge.
   *
   * @return		the names
   */
  public BaseString[] getLayersToMerge() {
    return m_LayersToMerge;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String layersToMergeTipText() {
    return "The names of the layers to merge.";
  }

  /**
   * Sets the name of the layer generated from the merge, can replace an existing one.
   *
   * @param value	the name
   */
  public void setNewLayer(String value) {
    m_NewLayer = value;
    reset();
  }

  /**
   * Returns the name of the layer generated from the merge, can replace an existing one.
   *
   * @return		the name
   */
  public String getNewLayer() {
    return m_NewLayer;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String newLayerTipText() {
    return "The name of the layer generated from the merge, can replace an existing one.";
  }

  /**
   * Sets whether to remove the old layers that are no longer needed.
   *
   * @param value	true if to delete
   */
  public void setDeleteOldLayers(boolean value) {
    m_DeleteOldLayers = value;
    reset();
  }

  /**
   * Returns whether to remove the old layers that are no longer needed.
   *
   * @return		true if to delete
   */
  public boolean getDeleteOldLayers() {
    return m_DeleteOldLayers;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String deleteOldLayersTipText() {
    return "If enabled, removes the old layers that are no longer needed.";
  }

  /**
   * Performs the filtering of the container.
   *
   * @param cont the container to filter
   * @return the filtered container
   */
  @Override
  protected ImageSegmentationContainer doFilter(ImageSegmentationContainer cont) {
    Set<String> 		mergeLayers;
    List<BufferedImage> 	layers;
    int 			black;
    int[]			merged;
    int[]			pixels;
    int				i;
    int				n;
    BufferedImage		mergedImg;

    // collect layers to merge
    mergeLayers = new HashSet<>();
    for (BaseString layerToMerge: m_LayersToMerge)
      mergeLayers.add(layerToMerge.getValue());

    layers = new ArrayList<>();
    for (String layerName: cont.getLayers().keySet()) {
      if (mergeLayers.contains(layerName))
        layers.add(cont.getLayers().get(layerName));
    }

    // merge
    if (layers.size() > 0) {
      black  = Color.BLACK.getRGB();
      merged = BufferedImageHelper.getPixels(layers.get(0));
      for (i = 1; i < layers.size(); i++) {
	pixels = BufferedImageHelper.getPixels(layers.get(i));
	for (n = 0; n < pixels.length; n++) {
	  if ((pixels[n] | 0xFF000000) != black)
	    merged[n] = pixels[n];
	}
      }

      // add merged layer
      mergedImg = new BufferedImage(layers.get(0).getWidth(), layers.get(0).getHeight(), layers.get(0).getType());
      mergedImg.setRGB(0, 0, layers.get(0).getWidth(), layers.get(0).getHeight(), merged, 0, layers.get(0).getWidth());
      cont.getLayers().put(m_NewLayer, mergedImg);

      // remove obsolete layers?
      if (m_DeleteOldLayers) {
        for (String layer: mergeLayers) {
          if (layer.equals(m_NewLayer))
            continue;
	  cont.getLayers().remove(layer);
	}
      }
    }

    return cont;
  }
}
