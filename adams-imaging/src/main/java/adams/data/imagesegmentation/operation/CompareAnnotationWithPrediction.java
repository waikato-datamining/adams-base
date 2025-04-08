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
 * CompareAnnotationWithPrediction.java
 * Copyright (C) 2023-2025 University of Waikato, Hamilton, NZ
 */

package adams.data.imagesegmentation.operation;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseObject;
import adams.core.base.BaseString;
import adams.data.image.BufferedImageContainer;
import adams.data.image.BufferedImageHelper;
import adams.flow.container.ImageSegmentationContainer;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Compares a prediction (first container) with the annotation
 * (second container) and outputs a color image per label that shows
 * the correct overlaps, the missed annotations and additionally
 * predicted pixels.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class CompareAnnotationWithPrediction
  extends AbstractImageSegmentationContainerOperation {

  private static final long serialVersionUID = 5451678654384977453L;

  /** the labels to analyze (all if none specified). */
  protected BaseString[] m_Labels;

  /** the background color to use. */
  protected Color m_BackgroundColor;

  /** the color for correct overlaps. */
  protected Color m_ColorOverlap;

  /** the color for missed annotations. */
  protected Color m_ColorMissed;

  /** the color for additional predictions. */
  protected Color m_ColorAdditional;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Compares a prediction (first container) with the annotation "
      + "(second container) and outputs a color image per label that shows "
      + "the correct overlaps, the missed annotations and additionally "
      + "predicted pixels.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "label", "labels",
      new BaseString[0]);

    m_OptionManager.add(
      "background-color", "backgroundColor",
      new Color(0, 0, 0, 0));

    m_OptionManager.add(
      "color-overlap", "colorOverlap",
      Color.GREEN);

    m_OptionManager.add(
      "color-missed", "colorMissed",
      Color.RED);

    m_OptionManager.add(
      "color-additional", "colorAdditional",
      Color.YELLOW);
  }

  /**
   * Sets the labels to generate the images for instead of all.
   *
   * @param value	the labels
   */
  public void setLabels(BaseString[] value) {
    m_Labels = value;
    reset();
  }

  /**
   * Returns the labels to generate the images for instead of all.
   *
   * @return		the labels
   */
  public BaseString[] getLabels() {
    return m_Labels;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String labelsTipText() {
    return "The labels to generate the images for instead of all.";
  }

  /**
   * Sets the color to use as background.
   *
   * @param value 	the background color
   */
  public void setBackgroundColor(Color value) {
    m_BackgroundColor = value;
    reset();
  }

  /**
   * Returns the color to use as background.
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
    return "The color to use as background.";
  }

  /**
   * Sets the color for correct overlaps.
   *
   * @param value 	the color
   */
  public void setColorOverlap(Color value) {
    m_ColorOverlap = value;
    reset();
  }

  /**
   * Returns the color for correct overlaps.
   *
   * @return 		the color
   */
  public Color getColorOverlap() {
    return m_ColorOverlap;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for displaying in the GUI or for listing the options.
   */
  public String colorOverlapTipText() {
    return "The color to use for correct overlaps between annotations/predictions.";
  }

  /**
   * Sets the color for missed annotations.
   *
   * @param value 	the color
   */
  public void setColorMissed(Color value) {
    m_ColorMissed = value;
    reset();
  }

  /**
   * Returns the color for missed annotations.
   *
   * @return 		the color
   */
  public Color getColorMissed() {
    return m_ColorMissed;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for displaying in the GUI or for listing the options.
   */
  public String colorMissedTipText() {
    return "The color to use for missed annotations.";
  }

  /**
   * Sets the color for predictions that had no annotations.
   *
   * @param value 	the color
   */
  public void setColorAdditional(Color value) {
    m_ColorAdditional = value;
    reset();
  }

  /**
   * Returns the color for predictions that had no annotations.
   *
   * @return 		the color
   */
  public Color getColorAdditional() {
    return m_ColorAdditional;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for displaying in the GUI or for listing the options.
   */
  public String colorAdditionalTipText() {
    return "The color to use for predictions that had no annotations.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "labels", m_Labels, "labels: ");
    result += QuickInfoHelper.toString(this, "colorOverlap", m_ColorOverlap, ", overlap: ");
    result += QuickInfoHelper.toString(this, "colorMissed", m_ColorMissed, ", missed: ");
    result += QuickInfoHelper.toString(this, "colorAdditional", m_ColorAdditional, ", additional: ");

    return result;
  }

  /**
   * Returns the minimum number of containers that are required for the operation.
   *
   * @return the number of containers that are required, <= 0 means no lower limit
   */
  @Override
  public int minNumContainersRequired() {
    return 2;
  }

  /**
   * Returns the maximum number of containers that are required for the operation.
   *
   * @return the number of containers that are required, <= 0 means no upper limit
   */
  @Override
  public int maxNumContainersRequired() {
    return 2;
  }

  /**
   * The type of data that is generated.
   *
   * @return the class
   */
  @Override
  public Class generates() {
    return Map.class;
  }

  /**
   * Compares the two pixel arrays and generates an images from it.
   *
   * @param predPixels	the prediction pixels
   * @param annoPixels	the annotation pixels
   * @return		the statistics
   */
  protected BufferedImageContainer generate(int width, int height, int[] predPixels, int[] annoPixels) {
    BufferedImageContainer	result;
    BufferedImage		img;
    int[] 			pixels;
    int				i;
    int				black;
    int				colorOverlap;
    int				colorMissed;
    int				colorAdditional;

    img             = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    black           = Color.BLACK.getRGB();
    colorOverlap    = m_ColorOverlap.getRGB();
    colorMissed     = m_ColorMissed.getRGB();
    colorAdditional = m_ColorAdditional.getRGB();
    pixels          = new int[width * height];
    Arrays.fill(pixels, m_BackgroundColor.getRGB());

    if (predPixels == null) {
      predPixels = new int[annoPixels.length];
      Arrays.fill(predPixels, black);
    }

    for (i = 0; i < predPixels.length; i++) {
      if ((annoPixels[i] != black) && (predPixels[i] != black))
	pixels[i] = colorOverlap;
      else if (annoPixels[i] != black)
	pixels[i] = colorMissed;
      else if (predPixels[i] != black)
	pixels[i] = colorAdditional;
    }

    img.setRGB(0, 0, width, height, pixels, 0, width);
    result = new BufferedImageContainer();
    result.setImage(img);

    return result;
  }

  /**
   * Performs the actual processing of the containers.
   *
   * @param containers the containers to process
   * @return the generated data
   */
  @Override
  protected Object doProcess(ImageSegmentationContainer[] containers) {
    Map<String, BufferedImageContainer>		result;
    ImageSegmentationContainer 			predCont;
    ImageSegmentationContainer 			annoCont;
    Map<String,BufferedImage> 			predLayers;
    Map<String,BufferedImage> 			annoLayers;
    Map<String,int[]> 				predPixels;
    Map<String,int[]> 				annoPixels;
    List<String> 				labels;

    predCont   = containers[0];
    predLayers = (Map<String,BufferedImage>) predCont.getValue(ImageSegmentationContainer.VALUE_LAYERS);
    annoCont   = containers[1];
    annoLayers = (Map<String,BufferedImage>) annoCont.getValue(ImageSegmentationContainer.VALUE_LAYERS);

    labels = new ArrayList<>(annoLayers.keySet());
    Collections.sort(labels);
    // specific subset?
    if (m_Labels.length > 0)
      labels.retainAll(BaseObject.toStringList(m_Labels));

    predPixels = new HashMap<>();
    annoPixels = new HashMap<>();
    for (String label: labels) {
      if (predLayers.containsKey(label))
	predPixels.put(label, BufferedImageHelper.getPixels(predLayers.get(label)));
      if (annoLayers.containsKey(label))
	annoPixels.put(label, BufferedImageHelper.getPixels(annoLayers.get(label)));
    }

    result = new HashMap<>();
    for (String label: labels) {
      result.put(label, generate(
	annoLayers.get(label).getWidth(), annoLayers.get(label).getHeight(),
	predPixels.get(label), annoPixels.get(label)));
      result.get(label).getReport().setStringValue("Label", label);
    }

    return result;
  }
}
