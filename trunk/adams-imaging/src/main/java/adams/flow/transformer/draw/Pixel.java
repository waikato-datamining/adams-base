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
 * Pixel.java
 * Copyright (C) 2013-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.draw;

import adams.core.QuickInfoHelper;
import adams.gui.core.ColorHelper;

import java.awt.image.BufferedImage;

/**
 <!-- globalinfo-start -->
 * Draws a pixel with the specified color at the specified location.
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
 * <pre>-color &lt;java.awt.Color&gt; (property: color)
 * &nbsp;&nbsp;&nbsp;The color of the pixel.
 * &nbsp;&nbsp;&nbsp;default: #000000
 * </pre>
 * 
 * <pre>-x &lt;int&gt; (property: X)
 * &nbsp;&nbsp;&nbsp;The X position of the pixel (1-based).
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-y &lt;int&gt; (property: Y)
 * &nbsp;&nbsp;&nbsp;The Y position of the pixel (1-based).
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-type &lt;RGBA|COLOR&gt; (property: type)
 * &nbsp;&nbsp;&nbsp;The type of value to use for the pixel.
 * &nbsp;&nbsp;&nbsp;default: RGBA
 * </pre>
 * 
 * <pre>-rgba &lt;int&gt; (property: RGBA)
 * &nbsp;&nbsp;&nbsp;The RGBA value (&gt;= 0).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Pixel
  extends AbstractColorDrawOperation {

  /** for serialization. */
  private static final long serialVersionUID = -337973956383988281L;

  /**
   * Enumeration that determines what value to use for the pixel.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum PixelValueType {
    /** uses the RGBA value. */
    RGBA,
    /** uses the Color value. */
    COLOR
  }
  
  /** the X position of the pixel (1-based). */
  protected int m_X;

  /** the Y position of the pixel (1-based). */
  protected int m_Y;

  /** what type of value to use for the pixel. */
  protected PixelValueType m_Type;
  
  /** the RGBA value of the pixel. */
  protected int m_RGBA;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Draws a pixel with the specified color at the specified location.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "x", "X",
	    1, 1, null);

    m_OptionManager.add(
	    "y", "Y",
	    1, 1, null);

    m_OptionManager.add(
	    "type", "type",
	    PixelValueType.RGBA);

    m_OptionManager.add(
	    "rgba", "RGBA",
	    0, 0, null);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "X", m_X, "X: ");
    result += QuickInfoHelper.toString(this, "Y", m_Y, ", Y: ");

    if (QuickInfoHelper.hasVariable(this, "type")) {
      result += ", " + QuickInfoHelper.getVariable(this, "type");
    }
    else {
      switch (m_Type) {
	case RGBA:
	  result += QuickInfoHelper.toString(this, "RGBA", m_RGBA, ", RGBA: ");
	  break;
	case COLOR:
	  result += QuickInfoHelper.toString(this, "color", ColorHelper.toHex(m_Color), ", Color: ");
	  break;
      }
    }

    return result;
  }

  /**
   * Sets the X position of the pixel.
   *
   * @param value	the position, 1-based
   */
  public void setX(int value) {
    if (value > 0) {
      m_X = value;
      reset();
    }
    else {
      getLogger().severe("X must be >0, provided: " + value);
    }
  }

  /**
   * Returns the X position of the pixel.
   *
   * @return		the position, 1-based
   */
  public int getX() {
    return m_X;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String XTipText() {
    return "The X position of the pixel (1-based).";
  }

  /**
   * Sets the Y position of the pixel.
   *
   * @param value	the position, 1-based
   */
  public void setY(int value) {
    if (value > 0) {
      m_Y = value;
      reset();
    }
    else {
      getLogger().severe("Y must be >0, provided: " + value);
    }
  }

  /**
   * Returns the Y position of the pixel.
   *
   * @return		the position, 1-based
   */
  public int getY() {
    return m_Y;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String YTipText() {
    return "The Y position of the pixel (1-based).";
  }

  /**
   * Sets the type of value to use.
   *
   * @param value	the type
   */
  public void setType(PixelValueType value) {
    m_Type = value;
    reset();
  }

  /**
   * Returns the type of value to use.
   *
   * @return		the type
   */
  public PixelValueType getType() {
    return m_Type;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeTipText() {
    return "The type of value to use for the pixel.";
  }

  /**
   * Sets the RGBA value of the pixel.
   *
   * @param value	the RGBA value
   */
  public void setRGBA(int value) {
    if (value >= 0) {
      m_RGBA = value;
      reset();
    }
    else {
      getLogger().severe("RGBA must be >=0, provided: " + value);
    }
  }

  /**
   * Returns the RGBA value of the pixel.
   *
   * @return		the RGBA value
   */
  public int getRGBA() {
    return m_RGBA;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String RGBATipText() {
    return "The RGBA value (>= 0).";
  }

  /**
   * Checks the image.
   *
   * @param image	the image to check
   * @return		null if OK, otherwise error message
   */
  protected String check(BufferedImage image) {
    String        result;

    result = super.check(image);

    if (result == null) {
      if (m_X > image.getWidth())
        result = "X is larger than image width: " + m_X + " > " + image.getWidth();
      else if (m_Y > image.getHeight())
        result = "Y is larger than image height: " + m_Y + " > " + image.getHeight();
    }

    return result;
  }

  /**
   * Performs the actual draw operation.
   * 
   * @param image	the image to draw on
   */
  @Override
  protected String doDraw(BufferedImage image) {
    String	result;

    result = null;

    switch (m_Type) {
      case RGBA:
        image.setRGB(m_X - 1, m_Y - 1, m_RGBA);
        break;
      case COLOR:
        image.setRGB(m_X - 1, m_Y - 1, m_Color.getRGB());
        break;
      default:
        throw new IllegalStateException("Unhandled pixel value type: " + m_Type);
    }

    if (isLoggingEnabled())
      getLogger().info("X=" + m_X + ", Y=" + m_Y + " -> " + m_RGBA);

    return result;
  }
}
