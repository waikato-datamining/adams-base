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
 * AbstractObjectLocator.java
 * Copyright (C) 2013-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.locateobjects;

import adams.core.CleanUpHandler;
import adams.core.QuickInfoSupporter;
import adams.core.Stoppable;
import adams.core.option.AbstractOptionHandler;
import adams.data.report.Report;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Ancestor for algorithms that locate objects in images.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 78 $
 */
public abstract class AbstractObjectLocator
  extends AbstractOptionHandler 
  implements Stoppable, CleanUpHandler, QuickInfoSupporter {

  /** for serialization. */
  private static final long serialVersionUID = -4035633099365011707L;

  /** whether to place the located object on a fixed size canvas. */
  protected boolean m_CenterOnCanvas;
  
  /** the canvas width. */
  protected int m_CanvasWidth;
  
  /** the canvas height. */
  protected int m_CanvasHeight;
  
  /** the canvas color. */
  protected Color m_CanvasColor;

  /** for storing errors. */
  protected List<String> m_Errors;

  /** for storing warnings. */
  protected List<String> m_Warnings;
  
  /** whether the execution was stopped. */
  protected boolean m_Stopped;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
        "center-on-canvas", "centerOnCanvas",
        getDefaultCenterOnCanvas());

    m_OptionManager.add(
	"canvas-width", "canvasWidth",
	getDefaultCanvasWidth(), 1, null);

    m_OptionManager.add(
	"canvas-height", "canvasHeight",
	getDefaultCanvasHeight(), 1, null);

    m_OptionManager.add(
	"canvas-color", "canvasColor",
	getDefaultCanvasColor());
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_Errors   = new ArrayList<String>();
    m_Warnings = new ArrayList<String>();
  }
  
  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();
    
    m_Errors.clear();
    m_Warnings.clear();
  }

  /**
   * Returns the default for centering the located object on a canvas.
   * 
   * @return		the default
   */
  protected boolean getDefaultCenterOnCanvas() {
    return false;
  }
  
  /**
   * Sets whether to center the located object on a fixed-size canvas.
   *
   * @param value 	true if to center on fixed-size canvas
   */
  public void setCenterOnCanvas(boolean value) {
    m_CenterOnCanvas = value;
    reset();
  }

  /**
   * Returns whether to center the located object on a fixed-size canvas.
   *
   * @return 		true if to center on fixed-size canvas 
   */
  public boolean getCenterOnCanvas() {
    return m_CenterOnCanvas;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String centerOnCanvasTipText() {
    return "If enabled, the located objects get centered on a canvas of fixed size.";
  }

  /**
   * Returns the default width of the canvas in pixels.
   *
   * @return 		the width in pixels
   */
  protected int getDefaultCanvasWidth() {
    return 100;
  }

  /**
   * Sets the width of the canvas in pixels.
   *
   * @param value 	the width in pixels
   */
  public void setCanvasWidth(int value) {
    m_CanvasWidth = value;
    reset();
  }

  /**
   * Returns the width of the canvas in pixels.
   *
   * @return 		the width in pixels
   */
  public int getCanvasWidth() {
    return m_CanvasWidth;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String canvasWidthTipText() {
    return "The width of the canvas in pixels.";
  }

  /**
   * Returns the default height of the canvas in pixels.
   *
   * @return 		the height in pixels
   */
  protected int getDefaultCanvasHeight() {
    return 100;
  }

  /**
   * Sets the height of the canvas in pixels.
   *
   * @param value 	the height in pixels
   */
  public void setCanvasHeight(int value) {
    m_CanvasHeight = value;
    reset();
  }

  /**
   * Returns the height of the canvas in pixels.
   *
   * @return 		the height in pixels
   */
  public int getCanvasHeight() {
    return m_CanvasHeight;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String canvasHeightTipText() {
    return "The height of the canvas in pixels.";
  }

  /**
   * Returns the default color for the canvas.
   *
   * @return 		the color
   */
  protected Color getDefaultCanvasColor() {
    return Color.WHITE;
  }

  /**
   * Sets the color to use for filling the canvas.
   *
   * @param value 	the color
   */
  public void setCanvasColor(Color value) {
    m_CanvasColor = value;
    reset();
  }

  /**
   * Returns the color to use for filling the canvas.
   *
   * @return 		the color
   */
  public Color getCanvasColor() {
    return m_CanvasColor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String canvasColorTipText() {
    return "The color to use for filling the canvas.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   * <p/>
   * Default implementation returns null.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return null;
  }

  /**
   * Checks whether there are any errors recorded.
   * 
   * @return		true if at least one error recorded
   */
  public boolean hasErrors() {
    return (m_Errors.size() > 0);
  }
  
  /**
   * Returns the errors.
   * 
   * @return		the errors
   */
  public List<String> getErrors() {
    return m_Errors;
  }
  
  /**
   * Adds the error to its internal list of errors.
   * 
   * @param msg		the error message to add
   */
  protected void addError(String msg) {
    m_Errors.add(msg);
  }
  
  /**
   * Checks whether there are any warnings recorded;
   * 
   * @return		true if at least one error recorded
   */
  public boolean hasWarnings() {
    return (m_Warnings.size() > 0);
  }
  
  /**
   * Returns the warnings.
   * 
   * @return		the warnings
   */
  public List<String> getWarnings() {
    return m_Warnings;
  }
  
  /**
   * Adds the warning to its internal list of warnings.
   * 
   * @param msg		the warnings message to add
   */
  protected void addWarning(String msg) {
    m_Warnings.add(msg);
  }
  
  /**
   * Checks whether the input can be used.
   * <p/>
   * Default implementation only checks whether image is not null.
   * 
   * @param image	the image to check
   */
  protected void check(BufferedImage image) {
    if (image == null)
      throw new IllegalArgumentException("No image provided!");
  }
  
  /**
   * Copies the object image onto the canvas with defined dimensions and color.
   * In case the image is too large for the canvas, the 
   * 
   * @param img		the located object
   * @return 		the new image with the located image centered
   */
  protected BufferedImage centerOnCanvas(BufferedImage img) {
    BufferedImage 	result;
    Graphics2D 		g;
    int			dx1;
    int			dy1;
    int			dx2;
    int			dy2;
    int			sx1;
    int			sy1;
    int			sx2;
    int			sy2;
    
    result = new BufferedImage(m_CanvasWidth, m_CanvasHeight, BufferedImage.TYPE_INT_ARGB);
    g      = result.createGraphics();

    // background
    g.setColor(m_CanvasColor);
    g.fillRect(0, 0, m_CanvasWidth, m_CanvasHeight);
    
    // x coordinates
    if (result.getWidth() >= img.getWidth()) {
      dx1 = (result.getWidth() - img.getWidth()) / 2;
      dx2 = dx1 + img.getWidth() - 1;
      sx1 = 0;
      sx2 = sx1 + img.getWidth() - 1;
    }
    else {
      dx1 = 0;
      dx2 = dx1 + result.getWidth() - 1;
      sx1 = (img.getWidth() - result.getWidth()) / 2;
      sx2 = sx1 + result.getWidth() - 1;
    }
    
    // y coordinates
    if (result.getHeight() >= img.getHeight()) {
      dy1 = (result.getHeight() - img.getHeight()) / 2;
      dy2 = dy1 + img.getHeight() - 1;
      sy1 = 0;
      sy2 = sy1 + img.getHeight() - 1;
    }
    else {
      dy1 = 0;
      dy2 = dy1 + result.getHeight() - 1;
      sy1 = (img.getHeight() - result.getHeight()) / 2;
      sy2 = sy1 + result.getHeight() - 1;
    }
    
    // draw image
    g.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);

    g.dispose();
    
    return result;
  }

  /**
   * Performs the actual locating of the objects.
   * 
   * @param image	  the image to process
   * @param annotateOnly  whether to annotate only
   * @return		  the containers of located objects
   */
  protected abstract LocatedObjects doLocate(BufferedImage image, boolean annotateOnly);
  
  /**
   * Locates the objects in the image.
   * 
   * @param image	the image to process
   * @return		the containers of located objects
   */
  public LocatedObjects locate(BufferedImage image) {
    LocatedObjects	result;
    int			i;
    BufferedImage	img;
    LocatedObject	current;
    LocatedObject	updated;
    
    m_Stopped = false;
    m_Errors.clear();
    m_Warnings.clear();
   
    check(image);
    
    result = doLocate(image, false);
    if (m_Stopped) {
      result = new LocatedObjects();
    }
    else {
      if (m_CenterOnCanvas) {
	for (i = 0; i < result.size(); i++) {
	  current = result.get(i);
	  img     = centerOnCanvas(current.getImage());
	  updated = new LocatedObject(img, current.getX(), current.getY(), current.getWidth(), current.getHeight());
	  result.set(i, updated);
	}
      }
    }
    
    return result;
  }

  /**
   * Only annotates the objects in the image, does not output any sub-images.
   *
   * @param image	the image to process
   * @return		the annotated objects
   */
  public LocatedObjects annotate(BufferedImage image) {
    LocatedObjects	    result;
    Report                  report;
    LocatedObject           current;
    int			    i;

    m_Stopped = false;
    m_Errors.clear();
    m_Warnings.clear();

    check(image);

    result = doLocate(image, true);
    if (m_Stopped)
      result = new LocatedObjects();

    return result;
  }

  /**
   * Stops the execution.
   */
  public void stopExecution() {
    m_Stopped = true;
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    reset();
  }
  
  /**
   * Frees up memory in a "destructive" non-reversible way.
   */
  @Override
  public void destroy() {
    cleanUp();
    super.destroy();
  }
}
