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
 * XScreenMaskLocator.java
 * Copyright (C) 2014-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.locateobjects;

import adams.data.image.BufferedImageHelper;
import adams.data.image.XScreenMaskHelper;
import adams.data.image.XScreenMaskHelper.Color;
import adams.data.image.transformer.crop.AbstractCropAlgorithm;
import adams.data.image.transformer.crop.NoCrop;

import javax.media.jai.Interpolation;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.ScaleDescriptor;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 <!-- globalinfo-start -->
 * Using the XScreenMask, this locator masks out the background plate (similar to a green screening process)then proceeds to find blobs in the resultant image.
 * <br><br>
 <!-- globalinfo-end -->
 * <br><br>
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-center-on-canvas &lt;boolean&gt; (property: centerOnCanvas)
 * &nbsp;&nbsp;&nbsp;If enabled, the located objects get centered on a canvas of fixed size.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-canvas-width &lt;int&gt; (property: canvasWidth)
 * &nbsp;&nbsp;&nbsp;The width of the canvas in pixels.
 * &nbsp;&nbsp;&nbsp;default: 100
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-canvas-height &lt;int&gt; (property: canvasHeight)
 * &nbsp;&nbsp;&nbsp;The height of the canvas in pixels.
 * &nbsp;&nbsp;&nbsp;default: 100
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-canvas-color &lt;java.awt.Color&gt; (property: canvasColor)
 * &nbsp;&nbsp;&nbsp;The color to use for filling the canvas.
 * &nbsp;&nbsp;&nbsp;default: #ffffff
 * </pre>
 * 
 * <pre>-min-size &lt;int&gt; (property: minSize)
 * &nbsp;&nbsp;&nbsp;Minimum object size.
 * &nbsp;&nbsp;&nbsp;default: 10
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-max-size &lt;int&gt; (property: maxSize)
 * &nbsp;&nbsp;&nbsp;Maximum object size.
 * &nbsp;&nbsp;&nbsp;default: 200
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-crop &lt;adams.data.image.transformer.crop.AbstractCropAlgorithm&gt; (property: crop)
 * &nbsp;&nbsp;&nbsp;Cropping algorithm.
 * &nbsp;&nbsp;&nbsp;default: adams.data.image.transformer.crop.NoCrop
 * </pre>
 * 
 * <pre>-scale &lt;double&gt; (property: scale)
 * &nbsp;&nbsp;&nbsp;Scale factor of working image (decrease scale for speed, increase for accuracy
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: 0.2
 * &nbsp;&nbsp;&nbsp;minimum: 0.1
 * &nbsp;&nbsp;&nbsp;maximum: 1.0
 * </pre>
 * 
 * <pre>-color &lt;RED|GREEN|BLUE|YELLOW&gt; (property: color)
 * &nbsp;&nbsp;&nbsp;Color to be masked out.
 * &nbsp;&nbsp;&nbsp;default: RED
 * </pre>
 * 
 * <pre>-down &lt;boolean&gt; (property: down)
 * &nbsp;&nbsp;&nbsp;If true, then pixels &lt;= threshold are not masked and the others' alpha channel 
 * &nbsp;&nbsp;&nbsp;are set to 0 (made transparent).
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 * 
 * <pre>-threshold &lt;int&gt; (property: threshold)
 * &nbsp;&nbsp;&nbsp;Threshold value used for binarization, specify -1 to automatically determine 
 * &nbsp;&nbsp;&nbsp;a threshold.
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * &nbsp;&nbsp;&nbsp;maximum: 255
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author lx51 (lx51 at students dot waikato dot ac dot nz)
 * @version $Revision$
 */
public class XScreenMaskLocator extends AbstractObjectLocator {

  /**
   * For serialization.
   */
  private static final long serialVersionUID = -8858162456921699059L;

  /**
   * Minimum object size.
   */
  protected int m_MinSize;

  /**
   * Maximum object size.
   */
  protected int m_MaxSize;

  /**
   * Cropping algorithm.
   */
  protected AbstractCropAlgorithm m_Crop;

  /**
   * Scale factor of working image (decrease scale for speed, increase for accuracy).
   */
  protected double m_Scale;

  /**
   * Color to be masked out.
   */
  protected Color m_Color;

  /**
   * If true, then pixels <= threshold are not masked and the others' alpha channel are set to 0 (made transparent).
   */
  protected boolean m_Down;

  /**
   * Threshold value used for binarization, specify -1 to automatically determine a threshold.
   */
  protected int m_Threshold;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Using the XScreenMask, this locator masks out the background plate (similar to a green screening process)" +
        "then proceeds to find blobs in the resultant image.";
  }

  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add("min-size", "minSize", 10, 0, null);
    m_OptionManager.add("max-size", "maxSize", 200, 0, null);
    m_OptionManager.add("crop", "crop", new NoCrop());
    m_OptionManager.add("scale", "scale", 0.2d, 0.1d, 1d);
    m_OptionManager.add("color", "color", Color.RED);
    m_OptionManager.add("down", "down", true);
    m_OptionManager.add("threshold", "threshold", -1, -1, 255);
  }

  /**
   * Get minimum object size.
   *
   * @return minimum object size.
   */
  public int getMinSize() {
    return m_MinSize;
  }

  /**
   * Set minimum object size.
   *
   * @param value minimum object size.
   */
  public void setMinSize(int value) {
    if (value >= 0) {
      m_MinSize = value;
      reset();
    } else
      getLogger().severe("Minimum size must be >= 0, provided: " + value);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for displaying in the GUI or for listing the options
   */
  public String minSizeTipText() {
    return "Minimum object size.";
  }

  /**
   * Get maximum object size.
   *
   * @return maximum object size
   */
  public int getMaxSize() {
    return m_MaxSize;
  }

  /**
   * Set maximum object size.
   *
   * @param value maximum object size
   */
  public void setMaxSize(int value) {
    if (value >= 0) {
      m_MaxSize = value;
      reset();
    } else
      getLogger().severe("Maximum size must be >= 0, provided: " + value);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for displaying in the GUI or for listing the options
   */
  public String maxSizeTipText() {
    return "Maximum object size.";
  }

  /**
   * Get cropping algorithm.
   *
   * @return cropping algorithm.
   */
  public AbstractCropAlgorithm getCrop() {
    return m_Crop;
  }

  /**
   * Set cropping algorithm.
   *
   * @param value cropping algorithm
   */
  public void setCrop(AbstractCropAlgorithm value) {
    if (value != null) {
      m_Crop = value;
      reset();
    } else
      getLogger().severe("Cropping algorithm must not be null.");
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for displaying in the GUI or for listing the options
   */
  public String cropTipText() {
    return "Cropping algorithm.";
  }

  /**
   * Get scale factor of working image.
   *
   * @return scale factor
   */
  public double getScale() {
    return m_Scale;
  }


  /**
   * Set scale factor of working image.
   *
   * @param value scale factor
   */
  public void setScale(double value) {
    if (value >= 0.1 && value <= 1) {
      m_Scale = value;
      reset();
    } else
      getLogger().severe("Scale must be 0.1 <= value >= 1, provided: " + value);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for displaying in the GUI or for listing the options
   */
  public String scaleTipText() {
    return "Scale factor of working image (decrease scale for speed, increase for accuracy).";
  }

  /**
   * Gets color to be masked out.
   *
   * @return color to be masked out
   */
  public Color getColor() {
    return m_Color;
  }

  /**
   * Sets color to be masked out.
   *
   * @param value bias
   */
  public void setColor(Color value) {
    if (value != null) {
      m_Color = value;
      reset();
    } else
      getLogger().severe("Color must not be null.");
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for displaying in the GUI or for listing the options
   */
  public String colorTipText() {
    return "Color to be masked out.";
  }

  /**
   * Get whether mask is applied below or above the threshold.
   *
   * @return threshold direction
   */
  public boolean getDown() {
    return m_Down;
  }

  /**
   * Set whether mask is applied below or above the threshold.
   *
   * @param value threshold direction
   */
  public void setDown(boolean value) {
    m_Down = value;
    reset();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for displaying in the GUI or for listing the options
   */
  public String downTipText() {
    return "If true, then pixels <= threshold are not masked and the others' alpha channel are set to 0 (made transparent).";
  }

  /**
   * Get the manual threshold value.
   *
   * @return manual threshold value
   */
  public int getThreshold() {
    return m_Threshold;
  }

  /**
   * Set the manual threshold value.
   *
   * @param value manual threshold value
   */
  public void setThreshold(int value) {
    if (value >= -1 && value <= 255) {
      m_Threshold = value;
      reset();
    } else
      getLogger().severe("Threshold must be 0 >= value <= 255, or -1 for auto-thresholding, provided: " + value);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for displaying in the GUI or for listing the options
   */
  public String thresholdTipText() {
    return "Threshold value used for binarization, specify -1 to automatically determine a threshold.";
  }

  /**
   * Returns the input image as output.
   *
   * @param image        the image to process
   * @param annotateOnly whether to annotate only
   * @return the containers of located objects
   */
  @Override
  protected LocatedObjects doLocate(BufferedImage image, boolean annotateOnly) {
    BufferedImage tmp = BufferedImageHelper.deepCopy(image);

    tmp = m_Crop.crop(tmp);
    Point offset = m_Crop.getTopLeft();

    if (m_Scale != 1.0f) {
      RenderedOp renderedOp = ScaleDescriptor.create(tmp, (float) m_Scale, (float) m_Scale, 0.0f, 0.0f, Interpolation.getInstance(Interpolation.INTERP_NEAREST), null);
      tmp = renderedOp.getAsBufferedImage(new Rectangle(0, 0, (int) Math.floor(image.getWidth() * m_Scale), (int) Math.floor(image.getHeight() * m_Scale)), null);
    }

    int[][] mask = XScreenMaskHelper.generateMask(tmp, m_Color);
    XScreenMaskHelper.binarizeMask(mask, m_Threshold, m_Down, getLogger());

    LocatedObjects objects = new LocatedObjects();
    for (int y = 0; y < mask.length; y++) {
      for (int x = 0; x < mask[0].length; x++) {
        int[] rect = boundingBox(mask, x, y);
        if (rect != null) {
          int left = (int) (Math.round(rect[0] / m_Scale) + offset.getX()), top = (int) (Math.round(rect[1] / m_Scale) + offset.getY());
          int width = (int) Math.round((rect[2] - rect[0] + 1) / m_Scale), height = (int) Math.round((rect[3] - rect[1] + 1) / m_Scale);
          if (width >= m_MinSize && width <= m_MaxSize && height >= m_MinSize && height <= m_MaxSize)
            objects.add(new LocatedObject(annotateOnly ? null : image.getSubimage(left, top, width, height), left, top, width, height));
        }
      }
    }

    return objects;
  }

  /**
   * Find the bounding box in a binary mask using a flood-fill algorithm.
   * Returns an int[]{top left X, top left Y, bottom right X, bottom right Y}, inclusive.
   *
   * @param mask   generated mask
   * @param xStart start x coordinate
   * @param yStart start y coordinate
   * @return bounding box
   */
  protected int[] boundingBox(int[][] mask, int xStart, int yStart) {
    if (mask[yStart][xStart] != 1) return null;

    int width = mask[0].length, height = mask.length;
    int[] node = new int[]{xStart, yStart};

    Deque<int[]> stack = new ArrayDeque<>();
    stack.push(node);

    int left = width - 1, right = 0, top = height - 1, bottom = 0;
    while ((node = stack.pollFirst()) != null) {
      int y = node[1];
      int west = node[0], east = node[0];
      while (west > 0 && mask[y][west - 1] == 1) west--;
      while (east < width - 1 && mask[y][east + 1] == 1) east++;
      for (int x = west; x <= east; x++) {
        mask[y][x] = -1;
        if (y > 0 && mask[y - 1][x] == 1) stack.push(new int[]{x, y - 1,});
        if (y < height - 1 && mask[y + 1][x] == 1) stack.push(new int[]{x, y + 1});
      }
      if (west < left) left = west;
      if (east > right) right = east;
      if (y < top) top = y;
      if (y > bottom) bottom = y;
    }

    return new int[]{left, top, right, bottom};
  }
}
