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
 * Oval.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.draw;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import adams.core.QuickInfoHelper;
import adams.gui.core.ColorHelper;
import adams.gui.core.GUIHelper;

/**
 <!-- globalinfo-start -->
 * Draws an oval with the specified color and dimensions at the given location. The oval can be filled as well.
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
 * <pre>-stroke-thickness &lt;float&gt; (property: strokeThickness)
 * &nbsp;&nbsp;&nbsp;The thickness of the stroke.
 * &nbsp;&nbsp;&nbsp;default: 1.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.01
 * </pre>
 * 
 * <pre>-anti-aliasing-enabled (property: antiAliasingEnabled)
 * &nbsp;&nbsp;&nbsp;If enabled, uses anti-aliasing for drawing.
 * </pre>
 * 
 * <pre>-x &lt;int&gt; (property: X)
 * &nbsp;&nbsp;&nbsp;The X position of the top-left corner of the oval (1-based).
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-y &lt;int&gt; (property: Y)
 * &nbsp;&nbsp;&nbsp;The Y position of the top-left corner of the oval (1-based).
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-width &lt;int&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;The width of the oval.
 * &nbsp;&nbsp;&nbsp;default: 10
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-height &lt;int&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height of the oval.
 * &nbsp;&nbsp;&nbsp;default: 10
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-fill (property: fill)
 * &nbsp;&nbsp;&nbsp;If enabled, the oval gets filled with the specified color.
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Oval
  extends AbstractColorStrokeDrawOperation {

  /** for serialization. */
  private static final long serialVersionUID = -1242368406478391978L;

  /** the X position of the oval (1-based). */
  protected int m_X;

  /** the Y position of the oval (1-based). */
  protected int m_Y;

  /** the width of the oval. */
  protected int m_Width;

  /** the height of the oval. */
  protected int m_Height;
  
  /** whether to fill the oval. */
  protected boolean m_Fill;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Draws an oval with the specified color and dimensions at the "
	+ "given location. The oval can be filled as well.";
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
	    "width", "width",
	    10, 1, null);

    m_OptionManager.add(
	    "height", "height",
	    10, 1, null);

    m_OptionManager.add(
	    "fill", "fill",
	    false);
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
    result += QuickInfoHelper.toString(this, "width", m_Width, ", W: ");
    result += QuickInfoHelper.toString(this, "height", m_Height, ", H: ");
    result += QuickInfoHelper.toString(this, "color", ColorHelper.toHex(m_Color), ", Color: ");
    result += QuickInfoHelper.toString(this, "strokeThickness", m_StrokeThickness, ", Stroke: ");
    result += QuickInfoHelper.toString(this, "fill", m_Fill, "filled", ", ");
    
    return result;
  }

  /**
   * Sets the X position of the oval (top-left corner).
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
   * Returns the X position of the oval (top-left corner).
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
    return "The X position of the top-left corner of the oval (1-based).";
  }

  /**
   * Sets the Y position of the oval (top-left corner).
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
   * Returns the Y position of the oval (top-left corner).
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
    return "The Y position of the top-left corner of the oval (1-based).";
  }

  /**
   * Sets the width of the oval.
   *
   * @param value	the width
   */
  public void setWidth(int value) {
    if (value > 0) {
      m_Width = value;
      reset();
    }
    else {
      getLogger().severe("Width must be >0, provided: " + value);
    }
  }

  /**
   * Returns the width of the oval.
   *
   * @return		the width
   */
  public int getWidth() {
    return m_Width;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String widthTipText() {
    return "The width of the oval.";
  }

  /**
   * Sets the height of the oval.
   *
   * @param value	the height
   */
  public void setHeight(int value) {
    if (value > 0) {
      m_Height = value;
      reset();
    }
    else {
      getLogger().severe("Height must be >0, provided: " + value);
    }
  }

  /**
   * Returns the height of the oval.
   *
   * @return		the height
   */
  public int getHeight() {
    return m_Height;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String heightTipText() {
    return "The height of the oval.";
  }

  /**
   * Sets whether to fill the oval.
   *
   * @param value	true if to fill
   */
  public void setFill(boolean value) {
    m_Fill = value;
    reset();
  }

  /**
   * Returns whether to fill the oval.
   *
   * @return		true if to fill
   */
  public boolean getFill() {
    return m_Fill;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fillTipText() {
    return "If enabled, the oval gets filled with the specified color.";
  }

  /**
   * Performs the actual draw operation.
   * 
   * @param image	the image to draw on
   */
  @Override
  protected String doDraw(BufferedImage image) {
    String	result;
    Graphics	g;

    result = null;

    if (m_X > image.getWidth())
      result = "X is larger than image width: " + m_X + " > " + image.getWidth();
    else if (m_Y > image.getHeight())
      result = "Y is larger than image height: " + m_Y + " > " + image.getHeight();
    else if (m_X + m_Width > image.getWidth())
      result = "X+Width is larger than image width: " + (m_X+m_Width) + " > " + image.getWidth();
    else if (m_Y + m_Height > image.getHeight())
      result = "Y+Height is larger than image height: " + (m_Y+m_Height) + " > " + image.getHeight();
    
    if (result == null) {
      g = image.getGraphics();
      g.setColor(m_Color);
      GUIHelper.configureAntiAliasing(g, m_AntiAliasingEnabled);
      ((Graphics2D) g).setStroke(new BasicStroke(m_StrokeThickness));
      if (m_Fill)
	g.fillOval(m_X, m_Y, m_Width, m_Height);
      else
	g.drawOval(m_X, m_Y, m_Width, m_Height);
    }
    
    return result;
  }
}
