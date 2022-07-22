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
 * Resize.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.data.image.transformer;

import adams.data.image.BufferedImageContainer;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

/**
 <!-- globalinfo-start -->
 * Resizes the image to predefined width and height.<br>
 * For more information on the scaling types, see:<br>
 * https:&#47;&#47;docs.oracle.com&#47;en&#47;java&#47;javase&#47;11&#47;docs&#47;api&#47;java.desktop&#47;java&#47;awt&#47;Image.html
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-width &lt;double&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;The width to resize the image to; use -1 to use original width; use (0-1
 * &nbsp;&nbsp;&nbsp;) for percentage.
 * &nbsp;&nbsp;&nbsp;default: -1.0
 * &nbsp;&nbsp;&nbsp;minimum: -1.0
 * </pre>
 *
 * <pre>-height &lt;double&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height to resize the image to; use -1 to use original height; use (0
 * &nbsp;&nbsp;&nbsp;-1) for percentage.
 * &nbsp;&nbsp;&nbsp;default: -1.0
 * &nbsp;&nbsp;&nbsp;minimum: -1.0
 * </pre>
 *
 * <pre>-scaling-type &lt;DEFAULT|FAST|SMOOTH|REPLICATE|AREA_AVERAGING&gt; (property: scalingType)
 * &nbsp;&nbsp;&nbsp;The type of scaling to perform.
 * &nbsp;&nbsp;&nbsp;default: DEFAULT
 * </pre>
 *
 * <pre>-force-percentage &lt;boolean&gt; (property: forcePercentage)
 * &nbsp;&nbsp;&nbsp;Whether to always interpret width&#47;height as percentage (eg when upscaling
 * &nbsp;&nbsp;&nbsp;the image).
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class Resize
    extends AbstractBufferedImageTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -7139209460998569352L;

  /**
   * Type of scaling to perform.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   */
  public enum ScalingType {
    DEFAULT(Image.SCALE_DEFAULT),
    FAST(Image.SCALE_FAST),
    SMOOTH(Image.SCALE_SMOOTH),
    REPLICATE(Image.SCALE_REPLICATE),
    AREA_AVERAGING(Image.SCALE_AREA_AVERAGING);

    /** the Image constant associated with the type. */
    private int m_Type;

    /**
     * Initializes the enum.
     *
     * @param type	the Image type constant
     */
    private ScalingType(int type) {
      m_Type = type;
    }

    /**
     * Returns the Image constant associated with the enum.
     *
     * @return		the type
     */
    public int getType() {
      return m_Type;
    }
  }

  /** the new width. */
  protected double m_Width;

  /** the new height. */
  protected double m_Height;

  /** the type of interpolation to perform. */
  protected ScalingType m_ScalingType;

  /** whether to force percentage (eg for scaling larger). */
  protected boolean m_ForcePercentage;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Resizes the image to predefined width and height.\n"
	+ "For more information on the scaling types, see:\n"
	+ "https://docs.oracle.com/en/java/javase/11/docs/api/java.desktop/java/awt/Image.html";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"width", "width",
	-1.0, -1.0, null);

    m_OptionManager.add(
	"height", "height",
	-1.0, -1.0, null);

    m_OptionManager.add(
	"scaling-type", "scalingType",
	ScalingType.DEFAULT);

    m_OptionManager.add(
	"force-percentage", "forcePercentage",
	false);
  }

  /**
   * Sets the width to resize to.
   *
   * @param value 	the width, -1 uses original width
   */
  public void setWidth(double value) {
    if (value >= -1) {
      m_Width = value;
      reset();
    }
    else {
      getLogger().severe(
	  "Width must be -1 (current width) or greater, provided: " + value);
    }
  }

  /**
   * Returns the width to resize to.
   *
   * @return 		the width, -1 if original width is used
   */
  public double getWidth() {
    return m_Width;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String widthTipText() {
    return "The width to resize the image to; use -1 to use original width; use (0-1) for percentage.";
  }

  /**
   * Sets the height to resize to.
   *
   * @param value 	the height, -1 uses original height
   */
  public void setHeight(double value) {
    if (value >= -1) {
      m_Height = value;
      reset();
    }
    else {
      getLogger().severe(
	  "Height must be -1 (current height) or greater, provided: " + value);
    }
  }

  /**
   * Returns the height to resize to.
   *
   * @return 		the height, -1 if original height is used
   */
  public double getHeight() {
    return m_Height;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String heightTipText() {
    return "The height to resize the image to; use -1 to use original height; use (0-1) for percentage.";
  }

  /**
   * Sets the type of interpolation to use.
   *
   * @param value 	the type
   */
  public void setScalingType(ScalingType value) {
    m_ScalingType = value;
    reset();
  }

  /**
   * Returns the type of interpolation in use.
   *
   * @return 		the type
   */
  public ScalingType getScalingType() {
    return m_ScalingType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String scalingTypeTipText() {
    return "The type of scaling to perform.";
  }

  /**
   * Sets whether to always interpret the width/height as percentage (eg when upscaling the image).
   *
   * @param value 	true if to force
   */
  public void setForcePercentage(boolean value) {
    m_ForcePercentage = value;
    reset();
  }

  /**
   * Returns whether to always interpret the width/height as percentage (eg when upscaling the image).
   *
   * @return 		true if to force
   */
  public boolean getForcePercentage() {
    return m_ForcePercentage;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String forcePercentageTipText() {
    return "Whether to always interpret width/height as percentage (eg when upscaling the image).";
  }

  /**
   * Performs no transformation at all, just returns the input.
   *
   * @param img		the image to process (can be modified, since it is a copy)
   * @return		the copy of the image
   */
  @Override
  protected BufferedImageContainer[] doTransform(BufferedImageContainer img) {
    BufferedImageContainer[]	result;
    BufferedImage		im;
    Image	 		imNew;
    BufferedImage		bimNew;
    int				width;
    int				height;
    Graphics 			g;

    im = img.getImage();

    if (m_Width == -1)
      width = im.getWidth();
    else if (((m_Width >= 0) && (m_Width <= 1)) || m_ForcePercentage)
      width = (int) (im.getWidth() *  m_Width);  // x percentage
    else
      width = (int) m_Width;    // absolute

    if (m_Height == -1)
      height = im.getHeight();
    else if (((m_Height >= 0) && (m_Height <= 1)) || m_ForcePercentage)
      height = (int) (im.getHeight() *  m_Height);  // x percentage
    else
      height = (int) m_Height;    // absolute

    imNew  = im.getScaledInstance(width, height, m_ScalingType.getType());
    bimNew = new BufferedImage(width, height, im.getType());
    g      = bimNew.getGraphics();
    g.drawImage(imNew, 0, 0, null);
    g.dispose();

    result    = new BufferedImageContainer[1];
    result[0] = (BufferedImageContainer) img.getHeader();
    result[0].setImage(bimNew);

    return result;
  }
}
