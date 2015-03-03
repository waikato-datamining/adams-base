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
 * Rotate.java
 * Copyright (C) 2011-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.data.jai.transformer;

import adams.data.image.BufferedImageContainer;

import javax.media.jai.InterpolationBilinear;
import javax.media.jai.InterpolationNearest;
import javax.media.jai.JAI;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.RenderedOp;
import java.awt.Color;

/**
 <!-- globalinfo-start -->
 * Rotates an image by a defined number of degrees.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-angle &lt;double&gt; (property: angle)
 * &nbsp;&nbsp;&nbsp;The angle, in degrees, to rotate the image by.
 * &nbsp;&nbsp;&nbsp;default: 0.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * &nbsp;&nbsp;&nbsp;maximum: 360.0
 * </pre>
 *
 * <pre>-origin-x &lt;int&gt; (property: originX)
 * &nbsp;&nbsp;&nbsp;The X position of the origin; special values: -1 = left, -2 = center, -3
 * &nbsp;&nbsp;&nbsp;= right.
 * &nbsp;&nbsp;&nbsp;default: -2
 * &nbsp;&nbsp;&nbsp;minimum: -3
 * </pre>
 *
 * <pre>-origin-y &lt;int&gt; (property: originY)
 * &nbsp;&nbsp;&nbsp;The Y position of the origin; special values: -1 = top, -2 = center, -3
 * &nbsp;&nbsp;&nbsp;= bottom.
 * &nbsp;&nbsp;&nbsp;default: -2
 * &nbsp;&nbsp;&nbsp;minimum: -3
 * </pre>
 *
 * <pre>-interpolation &lt;NONE|BILINEAR|NEAREST&gt; (property: interpolation)
 * &nbsp;&nbsp;&nbsp;The type of interpolation to perform.
 * &nbsp;&nbsp;&nbsp;default: BILINEAR
 * </pre>
 *
 * <pre>-background &lt;java.awt.Color&gt; (property: background)
 * &nbsp;&nbsp;&nbsp;The color to use for the background of the rotated image.
 * &nbsp;&nbsp;&nbsp;default: #000000
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Rotate
  extends AbstractJAITransformer {

  /** for serialization. */
  private static final long serialVersionUID = 2959486760492196174L;

  /**
   * The types of interpolations.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum InterpolationType {
    /** no interpolation. */
    NONE,
    /** bilinear. */
    BILINEAR,
    /** nearest. */
    NEAREST
  }

  /** the angle to rotate the image by. */
  protected double m_Angle;

  /** the X origin. */
  protected int m_OriginX;

  /** the Y origin. */
  protected int m_OriginY;

  /** the interpolation type. */
  protected InterpolationType m_Interpolation;

  /** the background color. */
  protected Color m_Background;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Rotates an image by a defined number of degrees.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "angle", "angle",
	    0.0, 0.0, 360.0);

    m_OptionManager.add(
	    "origin-x", "originX",
	    -2, -3, null);

    m_OptionManager.add(
	    "origin-y", "originY",
	    -2, -3, null);

    m_OptionManager.add(
	    "interpolation", "interpolation",
	    InterpolationType.BILINEAR);

    m_OptionManager.add(
	    "background", "background",
    	    Color.BLACK);
  }

  /**
   * Sets the rotation angle.
   *
   * @param value	the angle (0-360)
   */
  public void setAngle(double value) {
    if ((value >= 0.0) && (value <= 360.0)) {
      m_Angle = value;
      reset();
    }
    else {
      getLogger().severe("Angle has to fulfill 0 <= x <= 360, provided: " + value);
    }
  }

  /**
   * Returns the rotation angle.
   *
   * @return		the angle
   */
  public double getAngle() {
    return m_Angle;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String angleTipText() {
    return "The angle, in degrees, to rotate the image by.";
  }

  /**
   * Sets the X of the origin.
   *
   * @param value	the origin of X
   */
  public void setOriginX(int value) {
    if (value >= -3) {
      m_OriginX = value;
      reset();
    }
    else {
      getLogger().severe("Origin X has to fulfill -3 <= x, provided: " + value);
    }
  }

  /**
   * Returns the X of the origin.
   *
   * @return		the origin of X
   */
  public int getOriginX() {
    return m_OriginX;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String originXTipText() {
    return "The X position of the origin; special values: -1 = left, -2 = center, -3 = right.";
  }

  /**
   * Sets the Y of the origin.
   *
   * @param value	the origin of Y
   */
  public void setOriginY(int value) {
    if (value >= -3) {
      m_OriginY = value;
      reset();
    }
    else {
      getLogger().severe("Origin Y has to fulfill -3 <= x, provided: " + value);
    }
  }

  /**
   * Returns the Y of the origin.
   *
   * @return		the origin of Y
   */
  public int getOriginY() {
    return m_OriginY;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String originYTipText() {
    return "The Y position of the origin; special values: -1 = top, -2 = center, -3 = bottom.";
  }

  /**
   * Sets the type of interpolation to perform.
   *
   * @param value	the type
   */
  public void setInterpolation(InterpolationType value) {
    m_Interpolation = value;
    reset();
  }

  /**
   * Returns the type of interpolation to perform.
   *
   * @return		the type
   */
  public InterpolationType getInterpolation() {
    return m_Interpolation;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String interpolationTipText() {
    return "The type of interpolation to perform.";
  }

  /**
   * Sets the color to use for the background.
   *
   * @param value	the color
   */
  public void setBackground(Color value) {
    m_Background = value;
    reset();
  }

  /**
   * Returns the color in use for the background.
   *
   * @return		the color
   */
  public Color getBackground() {
    return m_Background;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String backgroundTipText() {
    return "The color to use for the background of the rotated image.";
  }

  /**
   * Performs no transformation at all, just returns the input.
   * <p/>
   * Original code for rotation taken from <a href="http://asserttrue.blogspot.com/2010/01/image-rotation-in-8-lines-using-java.html" target="_blank">here</a>,
   * which was placed in public domain by Kas Thomas.
   *
   * @param img		the image to process (can be modified, since it is a copy)
   * @return		the copy of the image
   */
  @Override
  protected BufferedImageContainer[] doTransform(BufferedImageContainer img) {
    BufferedImageContainer[]	result;
    ParameterBlockJAI		pb;
    RenderedOp			renderedOp;
    float			x;
    float			y;

    result = new BufferedImageContainer[1];
    pb     = new ParameterBlockJAI("rotate");
    pb.addSource(img.getImage());

    if (m_OriginX == -1)
      x = 0.0f;
    else if (m_OriginX == -2)
      x = (float) (img.getWidth() / 2);
    else if (m_OriginX == -3)
      x = (float) img.getWidth() - 1;
    else
      x = m_OriginX;

    if (m_OriginY == -1)
      y = 0.0f;
    else if (m_OriginY == -2)
      y = (float) (img.getHeight() / 2);
    else if (m_OriginY == -3)
      y = (float) img.getHeight() - 1;
    else
      y = m_OriginY;

    pb.setParameter("xOrigin", x);  // x-origin
    pb.setParameter("yOrigin", y);  // y-origin
    pb.setParameter("angle", (float) (m_Angle / 360 * Math.PI * 2));
    switch (m_Interpolation) {
      case NONE:
	// do nothing
	break;
      case BILINEAR:
	pb.setParameter("interpolation", new InterpolationBilinear());
	break;
      case NEAREST:
	pb.setParameter("interpolation", new InterpolationNearest());
	break;
      default:
	throw new IllegalStateException("Unhandled interpolation type: " + m_Interpolation);
    }
    pb.setParameter("backgroundValues", new double[]{m_Background.getRed(), m_Background.getGreen(), m_Background.getBlue()});

    renderedOp = JAI.create("rotate", pb, null);
    result[0]  = (BufferedImageContainer) img.getHeader();
    result[0].setImage(renderedOp.getRendering().getAsBufferedImage());

    return result;
  }
}
