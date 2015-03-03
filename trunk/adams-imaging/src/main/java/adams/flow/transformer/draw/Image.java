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
 * Image.java
 * Copyright (C) 2013-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.draw;

import adams.core.QuickInfoHelper;
import adams.data.image.AbstractImageContainer;
import adams.flow.core.CallableActorHelper;
import adams.flow.core.CallableActorReference;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 <!-- globalinfo-start -->
 * Draws the image obtained from a callable actor at the specified location.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-x &lt;int&gt; (property: X)
 * &nbsp;&nbsp;&nbsp;The X position of the top-left corner of the image (1-based).
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-y &lt;int&gt; (property: Y)
 * &nbsp;&nbsp;&nbsp;The Y position of the top-left corner of the image (1-based).
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-image-actor &lt;adams.flow.core.CallableActorReference&gt; (property: imageActor)
 * &nbsp;&nbsp;&nbsp;The callable actor to use for obtaining the image from.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Image
  extends AbstractDrawOperation {

  /** for serialization. */
  private static final long serialVersionUID = -1242368406478391978L;

  /** the X position of the image (1-based). */
  protected int m_X;

  /** the Y position of the image (1-based). */
  protected int m_Y;

  /** the callable actor to get the image from. */
  protected CallableActorReference m_ImageActor;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Draws the image obtained from a callable actor at the specified location.";
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
	    "image-actor", "imageActor",
	    new CallableActorReference());
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
    result += QuickInfoHelper.toString(this, "imageActor", m_ImageActor, ", Image: ");
    
    return result;
  }

  /**
   * Sets the X position of the image (top-left corner).
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
   * Returns the X position of the image (top-left corner).
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
    return "The X position of the top-left corner of the image (1-based).";
  }

  /**
   * Sets the Y position of the image (top-left corner).
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
   * Returns the Y position of the image (top-left corner).
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
    return "The Y position of the top-left corner of the image (1-based).";
  }

  /**
   * Sets the callable actor to obtain the image from.
   *
   * @param value	the actor reference
   */
  public void setImageActor(CallableActorReference value) {
    m_ImageActor = value;
    reset();
  }

  /**
   * Returns the callable actor to obtain the image from.
   *
   * @return		the actor reference
   */
  public CallableActorReference getImageActor() {
    return m_ImageActor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String imageActorTipText() {
    return "The callable actor to use for obtaining the image from.";
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
    String		result;
    Graphics		g;
    Object		obj;
    BufferedImage	todraw;

    result = null;

    obj    = CallableActorHelper.getSetupFromSource(Object.class, m_ImageActor, m_Owner);
    todraw = null;
    if (obj instanceof BufferedImage)
      todraw = (BufferedImage) obj;
    else if (obj instanceof AbstractImageContainer)
      todraw = ((AbstractImageContainer) obj).toBufferedImage();
    else
      result = "Unknown image class: " + obj.getClass().getName();
    
    if ((result == null) && (todraw != null)) {
      g = image.getGraphics();
      g.drawImage(todraw, m_X, m_Y, null);
    }
    
    return result;
  }
}
