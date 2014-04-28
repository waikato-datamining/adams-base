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
 * Line.java
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
 * Draws a line between the given start and end coordinates.
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
 * <pre>-x1 &lt;int&gt; (property: X1)
 * &nbsp;&nbsp;&nbsp;The X position of the start of the line (1-based).
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-y1 &lt;int&gt; (property: Y1)
 * &nbsp;&nbsp;&nbsp;The Y position of the start of the line (1-based).
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-x2 &lt;int&gt; (property: X2)
 * &nbsp;&nbsp;&nbsp;The X position of the end of the line (1-based).
 * &nbsp;&nbsp;&nbsp;default: 10
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-y2 &lt;int&gt; (property: Y2)
 * &nbsp;&nbsp;&nbsp;The Y position of the end of the line (1-based).
 * &nbsp;&nbsp;&nbsp;default: 10
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Line
  extends AbstractColorStrokeDrawOperation {

  /** for serialization. */
  private static final long serialVersionUID = -1242368406478391978L;

  /** the X position of the start of the line (1-based). */
  protected int m_X1;

  /** the Y position of the start of the line (1-based). */
  protected int m_Y1;

  /** the X position of the end of the line (1-based). */
  protected int m_X2;

  /** the Y position of the end of the line (1-based). */
  protected int m_Y2;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Draws a line between the given start and end coordinates.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "x1", "X1",
	    1, 1, null);

    m_OptionManager.add(
	    "y1", "Y1",
	    1, 1, null);

    m_OptionManager.add(
	    "x2", "X2",
	    10, 1, null);

    m_OptionManager.add(
	    "y2", "Y2",
	    10, 1, null);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "X1", m_X1, "X1: ");
    result += QuickInfoHelper.toString(this, "Y1", m_Y1, ", Y1: ");
    result += QuickInfoHelper.toString(this, "X2", m_X2, ", X2: ");
    result += QuickInfoHelper.toString(this, "Y2", m_Y2, ", Y2: ");
    result += QuickInfoHelper.toString(this, "color", ColorHelper.toHex(m_Color), ", Color: ");
    result += QuickInfoHelper.toString(this, "strokeThickness", m_StrokeThickness, ", Stroke: ");
    
    return result;
  }

  /**
   * Sets the X position of the start of the line.
   *
   * @param value	the position, 1-based
   */
  public void setX1(int value) {
    if (value > 0) {
      m_X1 = value;
      reset();
    }
    else {
      getLogger().severe("X1 must be >0, provided: " + value);
    }
  }

  /**
   * Returns the X position of the start of the line.
   *
   * @return		the position, 1-based
   */
  public int getX1() {
    return m_X1;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String X1TipText() {
    return "The X position of the start of the line (1-based).";
  }

  /**
   * Sets the Y position of the start of the line.
   *
   * @param value	the position, 1-based
   */
  public void setY1(int value) {
    if (value > 0) {
      m_Y1 = value;
      reset();
    }
    else {
      getLogger().severe("Y1 must be >0, provided: " + value);
    }
  }

  /**
   * Returns the Y position of the start of the line.
   *
   * @return		the position, 1-based
   */
  public int getY1() {
    return m_Y1;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String Y1TipText() {
    return "The Y position of the start of the line (1-based).";
  }

  /**
   * Sets the X position of the end of the line.
   *
   * @param value	the position, 1-based
   */
  public void setX2(int value) {
    if (value > 0) {
      m_X2 = value;
      reset();
    }
    else {
      getLogger().severe("X2 must be >0, provided: " + value);
    }
  }

  /**
   * Returns the X position of the end of the line.
   *
   * @return		the position, 1-based
   */
  public int getX2() {
    return m_X2;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String X2TipText() {
    return "The X position of the end of the line (1-based).";
  }

  /**
   * Sets the Y position of the end of the line.
   *
   * @param value	the position, 1-based
   */
  public void setY2(int value) {
    if (value > 0) {
      m_Y2 = value;
      reset();
    }
    else {
      getLogger().severe("Y2 must be >0, provided: " + value);
    }
  }

  /**
   * Returns the Y position of the end of the line.
   *
   * @return		the position, 1-based
   */
  public int getY2() {
    return m_Y2;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String Y2TipText() {
    return "The Y position of the end of the line (1-based).";
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

    if (m_X1 > image.getWidth())
      result = "X1 is larger than image width: " + m_X1 + " > " + image.getWidth();
    else if (m_Y1 > image.getHeight())
      result = "Y1 is larger than image height: " + m_Y1 + " > " + image.getHeight();
    else if (m_X2 > image.getWidth())
      result = "X2 is larger than image width: " + m_X2 + " > " + image.getWidth();
    else if (m_Y2 > image.getHeight())
      result = "Y2 is larger than image height: " + m_Y2 + " > " + image.getHeight();
    
    if (result == null) {
      g = image.getGraphics();
      g.setColor(m_Color);
      GUIHelper.configureAntiAliasing(g, m_AntiAliasingEnabled);
      if (g instanceof Graphics2D)
	((Graphics2D) g).setStroke(new BasicStroke(m_StrokeThickness));
      g.drawLine(m_X1, m_Y1, m_X2, m_Y2);
    }
    
    return result;
  }
}
