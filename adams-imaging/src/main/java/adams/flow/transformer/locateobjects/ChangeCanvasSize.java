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
 * ChangeCanvasSize.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer.locateobjects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;

import adams.core.QuickInfoHelper;
import adams.data.image.ImageAnchor;
import adams.data.image.ImageAnchorHelper;

/**
 <!-- globalinfo-start -->
 * Places the located objects obtained from the base locator on a canvas of specified size at the specified position.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-locator &lt;adams.flow.transformer.locateobjects.AbstractObjectLocator&gt; (property: locator)
 * &nbsp;&nbsp;&nbsp;The base locator to use.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.transformer.locateobjects.PassThrough
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
 * <pre>-anchor &lt;TOP_LEFT|TOP_CENTER|TOP_RIGHT|MIDDLE_LEFT|MIDDLE_CENTER|MIDDLE_RIGHT|BOTTOM_LEFT|BOTTOM_CENTER|BOTTOM_RIGHT&gt; (property: anchor)
 * &nbsp;&nbsp;&nbsp;Defines where to anchor the position on the canvas.
 * &nbsp;&nbsp;&nbsp;default: TOP_LEFT
 * </pre>
 * 
 * <pre>-background &lt;java.awt.Color&gt; (property: background)
 * &nbsp;&nbsp;&nbsp;The background color to use.
 * &nbsp;&nbsp;&nbsp;default: #ffffff
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 9648 $
 */
public class ChangeCanvasSize
  extends AbstractMetaObjectLocator {

  /** for serialization. */
  private static final long serialVersionUID = 7548064590836834398L;

  /** the width of the canvas. */
  protected int m_CanvasWidth;

  /** the height of the canvas. */
  protected int m_CanvasHeight;

  /** where to anchor the position on the canvas. */
  protected ImageAnchor m_Anchor;

  /** the background color. */
  protected Color m_Background;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Places the located objects obtained from the base locator on a canvas "
	+ "of specified size at the specified position.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"canvas-width", "canvasWidth",
	100, 1, null);

    m_OptionManager.add(
	"canvas-height", "canvasHeight",
	100, 1, null);

    m_OptionManager.add(
	"anchor", "anchor",
	ImageAnchor.TOP_LEFT);

    m_OptionManager.add(
	"background", "background",
	Color.WHITE);
  }

  /**
   * Sets the width of the canvase.
   *
   * @param value	the width
   */
  public void setCanvasWidth(int value) {
    if (value > 0) {
      m_CanvasWidth = value;
      reset();
    }
    else {
      getLogger().severe("Canvas width has to be >0, provided: " + value);
    }
  }

  /**
   * Returns the width of the canvas.
   *
   * @return		the width
   */
  public int getCanvasWidth() {
    return m_CanvasWidth;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String canvasWidthTipText() {
    return "The width of the canvas in pixels.";
  }

  /**
   * Sets the height of the canvas.
   *
   * @param value	the height
   */
  public void setCanvasHeight(int value) {
    if (value > 0) {
      m_CanvasHeight = value;
      reset();
    }
    else {
      getLogger().severe("Canvas height has to be >0, provided: " + value);
    }
  }

  /**
   * Returns the height of the canvas.
   *
   * @return		the height
   */
  public int getCanvasHeight() {
    return m_CanvasHeight;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String canvasHeightTipText() {
    return "The height of the canvas in pixels.";
  }

  /**
   * Sets where to anchor the position on the canvas.
   *
   * @param value	the anchor
   */
  public void setAnchor(ImageAnchor value) {
    m_Anchor = value;
    reset();
  }

  /**
   * Returns where to anchor the position on the canvas.
   *
   * @return		the anchor
   */
  public ImageAnchor getAnchor() {
    return m_Anchor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String anchorTipText() {
    return "Defines where to anchor the position on the canvas.";
  }

  /**
   * Sets the background color to use.
   *
   * @param value	the color
   */
  public void setBackground(Color value) {
    m_Background = value;
    reset();
  }

  /**
   * Returns the background color to use.
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
   * 			displaying in the GUI or for listing the options.
   */
  public String backgroundTipText() {
    return "The background color to use.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    
    result  = QuickInfoHelper.toString(this, "canvasWidth", m_CanvasWidth, "width: ");
    result += QuickInfoHelper.toString(this, "canvasHeight", m_CanvasHeight, ", height: ");
    result += QuickInfoHelper.toString(this, "anchor", m_Anchor, ", anchor: ");
    result += QuickInfoHelper.toString(this, "background", m_Background, ", bg: ");
    
    return result;
  }

  /**
   * Performs the actual locating of the objects.
   *
   * @param imp	        the image to process
   * @return		the containers of located objects
   */
  @Override
  protected LocatedObjects doLocate(BufferedImage image) {
    LocatedObjects	result;
    LocatedObjects	base;
    Point[]		corners;
    int			x;
    int			y;
    int			leftNew;
    int			topNew;
    int			heightOrig;
    int			widthOrig;
    int			xNew;
    int			yNew;
    BufferedImage	modified;
    Graphics		g;

    result = new LocatedObjects();
    base   = m_Locator.locate(image);

    for (LocatedObject obj: base) {
      corners = ImageAnchorHelper.calculateCorners(m_CanvasWidth, (int) m_CanvasHeight, m_Anchor, 0.0, 0.0, obj.getImage().getWidth(), obj.getImage().getHeight());
      leftNew = (int) corners[0].getX();
      topNew  = (int) corners[0].getY();

      heightOrig = obj.getImage().getHeight();
      widthOrig  = obj.getImage().getWidth();

      modified = new BufferedImage(m_CanvasWidth, m_CanvasHeight, BufferedImage.TYPE_INT_ARGB);
      
      // background
      g = modified.createGraphics();
      g.setColor(m_Background);
      g.fillRect(0, 0, m_CanvasWidth, m_CanvasHeight);
      g.dispose();
      
      // transfer
      for (y = 0; y < heightOrig; y++) {
	yNew = topNew + y;
	if ((yNew < 0) || (yNew >= m_CanvasHeight))
	  continue;
	for (x = 0; x < widthOrig; x++) {
	  xNew = leftNew + x;
	  if ((xNew < 0) || (xNew >= m_CanvasWidth))
	    continue;
	  modified.setRGB(xNew, yNew, obj.getImage().getRGB(x, y));
	}
      }
      
      result.add(new LocatedObject(modified, obj.getX(), obj.getY(), obj.getWidth(), obj.getHeight()));
    }

    return result;
  }
}
