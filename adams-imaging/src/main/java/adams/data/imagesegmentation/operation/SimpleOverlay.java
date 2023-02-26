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
 * SimpleOverlay.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.data.imagesegmentation.operation;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseObject;
import adams.core.base.BaseString;
import adams.data.image.BufferedImageContainer;
import adams.data.image.BufferedImageHelper;
import adams.flow.container.ImageSegmentationContainer;
import adams.gui.visualization.core.ColorProvider;
import adams.gui.visualization.core.DefaultColorProvider;
import adams.gui.visualization.segmentation.ImageUtils;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Combines base image (or just background color) and layers into a single image.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class SimpleOverlay
  extends AbstractImageSegmentationContainerOperation {

  private static final long serialVersionUID = 1742854771007616833L;

  /** whether to include the base image. */
  protected boolean m_IncludeBaseImage;

  /** the background color to use when not using the base image. */
  protected Color m_BackgroundColor;

  /** the layers to use. */
  protected BaseString[] m_Layers;

  /** the color provider for generating the colors. */
  protected ColorProvider m_ColorProvider;

  /** the alpha value to use for the overlay (0: transparent, 255: opaque). */
  protected int m_Alpha;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Combines base image (or just background color) and layers into a single image.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "include-base-image", "includeBaseImage",
      true);

    m_OptionManager.add(
      "background-color", "backgroundColor",
      Color.BLACK);

    m_OptionManager.add(
      "layer", "layers",
      new BaseString[0]);

    m_OptionManager.add(
      "color-provider", "colorProvider",
      new DefaultColorProvider());

    m_OptionManager.add(
      "alpha", "alpha",
      128, 0, 255);
  }

  /**
   * Sets whether to use the base image or a uniform background color.
   *
   * @param value 	true if to use
   */
  public void setIncludeBaseImage(boolean value) {
    m_IncludeBaseImage = value;
    reset();
  }

  /**
   * Returns whether to use the base image or a uniform background color.
   *
   * @return 		true if to use
   */
  public boolean getIncludeBaseImage() {
    return m_IncludeBaseImage;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for displaying in the GUI or for listing the options.
   */
  public String includeBaseImageTipText() {
    return "If enabled, the base image is used as base rather than a uniform background color.";
  }

  /**
   * Sets the color to use as background when not using the base image.
   *
   * @param value 	the background color
   */
  public void setBackgroundColor(Color value) {
    m_BackgroundColor = value;
    reset();
  }

  /**
   * Returns the color to use as background when not using the base image.
   *
   * @return 		the background color
   */
  public Color getBackgroundColor() {
    return m_BackgroundColor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for displaying in the GUI or for listing the options.
   */
  public String backgroundColorTipText() {
    return "The color to use as background if the base image is not used.";
  }

  /**
   * Sets the layers to display, uses all if empty array.
   *
   * @param value 	the layers
   */
  public void setLayers(BaseString[] value) {
    m_Layers = value;
    reset();
  }

  /**
   * Returns the layers to display, uses all if empty array.
   *
   * @return 		the layers
   */
  public BaseString[] getLayers() {
    return m_Layers;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for displaying in the GUI or for listing the options.
   */
  public String layersTipText() {
    return "The layers to display; displays all if empty array.";
  }

  /**
   * Sets the color provider to use.
   *
   * @param value	the color provider
   */
  public void setColorProvider(ColorProvider value) {
    m_ColorProvider = value;
    reset();
  }

  /**
   * Returns the color provider to use.
   *
   * @return		the color provider
   */
  public ColorProvider getColorProvider() {
    return m_ColorProvider;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String colorProviderTipText() {
    return "The color provider to use for the blue channel indices.";
  }

  /**
   * Sets the alpha value to use for the overlay: 0=transparent, 255=opaque.
   *
   * @param value	the alphae value
   */
  public void setAlpha(int value) {
    if (getOptionManager().isValid("alpha", value)) {
      m_Alpha = value;
      reset();
    }
  }

  /**
   * Returns the alpha value to use for the overlay: 0=transparent, 255=opaque.
   *
   * @return		the alpha value
   */
  public int getAlpha() {
    return m_Alpha;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String alphaTipText() {
    return "The alpha value to use for the overlay: 0=transparent, 255=opaque.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "includeBaseImage", m_IncludeBaseImage, (m_IncludeBaseImage ? "include base" : "no base"));
    result += QuickInfoHelper.toString(this, "backgroundColor", m_BackgroundColor, ", background: ");
    result += QuickInfoHelper.toString(this, "layers", m_Layers, ", layers: ");
    result += QuickInfoHelper.toString(this, "alpha", m_Alpha, ", alpha: ");

    return result;
  }

  /**
   * Returns the minimum number of containers that are required for the operation.
   *
   * @return the number of containers that are required, <= 0 means no lower limit
   */
  @Override
  public int minNumContainersRequired() {
    return 1;
  }

  /**
   * Returns the maximum number of containers that are required for the operation.
   *
   * @return the number of containers that are required, <= 0 means no upper limit
   */
  @Override
  public int maxNumContainersRequired() {
    return 1;
  }

  /**
   * The type of data that is generated.
   *
   * @return the class
   */
  @Override
  public Class generates() {
    return BufferedImageContainer.class;
  }

  /**
   * Performs the actual processing of the containers.
   *
   * @param containers the containers to process
   * @return the generated data
   */
  @Override
  protected Object doProcess(ImageSegmentationContainer[] containers) {
    BufferedImageContainer	result;
    ImageSegmentationContainer	cont;
    BufferedImage 		base;
    BufferedImage 		comb;
    Graphics2D			g2d;
    Map<String,BufferedImage>	layers;
    List<String> 		layerNames;
    BufferedImage		layerImage;
    Color			layerColor;
    AlphaComposite 		alpha;

    cont   = containers[0];
    result = new BufferedImageContainer();
    result.getReport().setStringValue("Name", cont.getValue(ImageSegmentationContainer.VALUE_NAME, String.class));

    // the layers to overlay
    layers = cont.getLayers();
    if (m_Layers.length > 0) {
      layerNames = BaseObject.toStringList(m_Layers);
    }
    else {
      layerNames = new ArrayList<>(layers.keySet());
      Collections.sort(layerNames);
    }

    // create base image
    comb = new BufferedImage(cont.getBaseImage().getWidth(), cont.getBaseImage().getHeight(), BufferedImage.TYPE_INT_ARGB);
    g2d  = comb.createGraphics();
    if (m_IncludeBaseImage) {
      base = BufferedImageHelper.convert(cont.getBaseImage(), BufferedImage.TYPE_INT_ARGB);
      g2d.drawImage(base, 0, 0, null);
    }
    else {
      g2d.setColor(m_BackgroundColor);
      g2d.fillRect(0, 0, comb.getWidth(), comb.getHeight());
    }
    result.setImage(comb);

    // add layers
    m_ColorProvider.resetColors();
    alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) m_Alpha / 255);
    g2d.setComposite(alpha);
    for (String layerName: layerNames) {
      layerColor = m_ColorProvider.next();
      layerImage = BufferedImageHelper.convert(layers.get(layerName), BufferedImage.TYPE_INT_ARGB);
      if (layerImage == null) {
        getLogger().warning("Layer '" + layerName + "' does not exist!");
        continue;
      }
      ImageUtils.replaceColor(layerImage, new Color(0, 0, 0, 255), new Color(0, 0, 0, 0));
      ImageUtils.replaceColor(layerImage, new Color(255, 255, 255, 255), layerColor);
      g2d.drawImage(layerImage, 0, 0, null);
    }

    g2d.dispose();

    return result;
  }
}
