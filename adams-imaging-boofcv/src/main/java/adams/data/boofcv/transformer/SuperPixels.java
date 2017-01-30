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
 * SuperPixels.java
 * Copyright (C) 2016-2017 University of Waikato, Hamilton, New Zealand
 */

package adams.data.boofcv.transformer;

import adams.data.boofcv.BoofCVHelper;
import adams.data.boofcv.BoofCVImageContainer;
import adams.data.image.BufferedImageHelper;
import boofcv.abst.segmentation.ImageSuperpixels;
import boofcv.factory.segmentation.ConfigSlic;
import boofcv.factory.segmentation.FactoryImageSegmentation;
import boofcv.gui.feature.VisualizeRegions;
import boofcv.struct.image.ImageSInt32;

import java.awt.image.BufferedImage;

/**
 <!-- globalinfo-start -->
 * Takes an image and divides it into super pixels.<br>
 * <br>
 * For more information see:<br>
 * http:&#47;&#47;boofcv.org&#47;index.php?title=Tutorial_Image_Segmentation
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-num-segments &lt;int&gt; (property: numSegments)
 * &nbsp;&nbsp;&nbsp;The number of segments to use.
 * &nbsp;&nbsp;&nbsp;default: 100
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-spatial-weight &lt;float&gt; (property: spatialWeight)
 * &nbsp;&nbsp;&nbsp;The spatial weight.
 * &nbsp;&nbsp;&nbsp;default: 200.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * </pre>
 * 
 * <pre>-outline-segments &lt;boolean&gt; (property: outlineSegments)
 * &nbsp;&nbsp;&nbsp;If enabled, segments get outlined.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-average-segments &lt;boolean&gt; (property: averageSegments)
 * &nbsp;&nbsp;&nbsp;If enabled, segments get averaged.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-random-colors &lt;boolean&gt; (property: randomColors)
 * &nbsp;&nbsp;&nbsp;If enabled, random colors are used.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author sjb90
 * @version $Revision$
 */
public class SuperPixels
  extends AbstractBoofCVTransformer{

  private static final long serialVersionUID = -6214162210938255035L;

  /** the index for red. */
  public final static int R = 0;

  /** the index for green. */
  public final static int G = 1;

  /** the index for blue. */
  public final static int B = 2;

  /** the number of segments. */
  protected int m_NumSegments;

  /** the spatial weight. */
  protected float m_SpatialWeight;

  /** whether to average the segments. */
  protected boolean m_AverageSegments;

  /** whether to outline the segments. */
  protected boolean m_OutlineSegments;

  /** whether to use random colors. */
  protected boolean m_RandomColors;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Takes an image and divides it into super pixels.\n\n"
	+ "For more information see:\n"
	+ "http://boofcv.org/index.php?title=Tutorial_Image_Segmentation";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();
    m_OptionManager.add("num-segments", "numSegments", 100, 1, null);
    m_OptionManager.add("spatial-weight", "spatialWeight", 200.0f, 0.0f, null);
    m_OptionManager.add("outline-segments", "outlineSegments", false);
    m_OptionManager.add("average-segments", "averageSegments", false);
    m_OptionManager.add("random-colors", "randomColors", false);
  }

  /**
   * Sets whether to use random colors.
   *
   * @param value	true if to use random colors
   */
  public void setRandomColors(boolean value) {
    m_RandomColors = value;
    reset();
  }

  /**
   * Returns whether to use random colors.
   *
   * @return		true if to use random colors
   */
  public boolean isRandomColors() {
    return m_RandomColors;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String randomColorsTipText() {
    return "If enabled, random colors are used.";
  }

  /**
   * Sets the number of segments to use.
   *
   * @param value	the number of segments
   */
  public void setNumSegments(int value) {
    if (getOptionManager().isValid("numSegments", value)) {
      m_NumSegments = value;
      reset();
    }
  }

  /**
   * Returns the number of segments.
   *
   * @return		the number of segments
   */
  public int getNumSegments() {
    return m_NumSegments;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numSegmentsTipText() {
    return "The number of segments to use.";
  }

  /**
   * Sets the spatial weight to use.
   *
   * @param value	the weight
   */
  public void setSpatialWeight(float value) {
    if (getOptionManager().isValid("spatialWeight", value)) {
    m_SpatialWeight = value;
    reset();
    }
  }

  /**
   * Returns the spatial weight in use.
   *
   * @return		the weight
   */
  public float getSpatialWeight() {
    return m_SpatialWeight;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String spatialWeightTipText() {
    return "The spatial weight.";
  }

  /**
   * Sets whether to average the segments.
   *
   * @param value	true if to average
   */
  public void setAverageSegments(boolean value) {
    m_AverageSegments = value;
    reset();
  }

  /**
   * Returns whether to average the segments.
   *
   * @return		true if to average
   */
  public boolean isAverageSegments() {
    return m_AverageSegments;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String averageSegmentsTipText() {
    return "If enabled, segments get averaged.";
  }

  /**
   * Sets whether to outline the segments.
   *
   * @param value	true if to outline
   */
  public void setOutlineSegments(boolean value) {
    m_OutlineSegments = value;
    reset();
  }

  /**
   * Returns whether to outline the segments.
   *
   * @return		true if to outline
   */
  public boolean isOutlineSegments() {
    return m_OutlineSegments;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outlineSegmentsTipText() {
    return "If enabled, segments get outlined.";
  }

  /**
   * Takes a super pixel map and an image and averages the colours in every super pixel region defined by the map.
   *
   * @param pixelMap The map of the super pixel regions
   * @param numSegments the number of super pixels
   * @param image The image to be transformed
   */
  protected void averageSegmentColour(ImageSInt32 pixelMap, int numSegments, BufferedImage image) {
    // Set the colour of the pixels to the average of the super pixel
    int[] count 	= new int[numSegments];
    int[][] runningSum 	= new int[numSegments][4];
    for (int i = 0; i < count.length; i++) {
      count[i] = 1;
    }
    // Count up the number of pixels for each segment and track the value of the RGB channels
    for (int y = 0; y < image.getHeight(); y++) {
      for (int x = 0; x < image.getWidth(); x++) {
	int segment = pixelMap.get(x,y);
	count[segment]++;
	int[] rgb = BufferedImageHelper.split(image.getRGB(x, y));
	runningSum[segment][R] += rgb[R];
	runningSum[segment][G] += rgb[G];
	runningSum[segment][B] += rgb[B];
      }
    }
    // Find average value of the RGB channels by dividing the RGB values by the number of pixels in the corresponding
    // segment.
    int[] averageRgbForSegment = new int[numSegments];
    for (int segment = 0; segment < numSegments; segment++) {
      averageRgbForSegment[segment] = BufferedImageHelper.combine(runningSum[segment][R] / count[segment],
	runningSum[segment][G] / count[segment], runningSum[segment][B] / count[segment], 255);
    }

    for (int y = 0; y < image.getHeight(); y++) {
      for (int x = 0; x < image.getWidth(); x++) {
	int segment = pixelMap.get(x,y);
	image.setRGB(x,y,averageRgbForSegment[segment]);
      }
    }
  }

  /**
   * Performs the actual transforming of the image.
   *
   * @param img the image to transform (can be modified, since it is a copy)
   * @return the generated image(s)
   */
  @Override
  protected BoofCVImageContainer[] doTransform(BoofCVImageContainer img) {
    BoofCVImageContainer[] result = new BoofCVImageContainer[1];
    result[0] = new BoofCVImageContainer();
    ImageSuperpixels alg = FactoryImageSegmentation.slic(new ConfigSlic(m_NumSegments, m_SpatialWeight), img.getImage().getImageType());
    ImageSInt32 output = new ImageSInt32(img.getWidth(), img.getHeight());
    alg.segment(img.getImage(),output);
    BufferedImage image = img.toBufferedImage();

    int numSegments = alg.getTotalSuperpixels();
    if(m_AverageSegments)
      averageSegmentColour(output,numSegments,image);
    if(m_RandomColors && !m_AverageSegments)
      VisualizeRegions.regions(output,numSegments,image);
    if(m_OutlineSegments)
      VisualizeRegions.regionBorders(output,0xff0000,image);


    result[0].setImage(BoofCVHelper.toBoofCVImage(image));
    result[0].setReport(img.getReport().getClone());
    result[0].getNotes().mergeWith(img.getNotes());
    return result;
  }
}
