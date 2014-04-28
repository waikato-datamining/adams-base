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
 * AbstractPositionableMapOverlay.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.sink.openstreetmapviewer;

import java.awt.Graphics;
import java.awt.Rectangle;

import org.openstreetmap.gui.jmapviewer.JMapViewer;

import adams.flow.sink.OpenStreetMapViewer;

/**
 * Ancestor for overlays that can be positioned on the viewer.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractPositionableMapOverlay
  extends AbstractMapOverlay {
  
  /** for serialization. */
  private static final long serialVersionUID = 5439828929470172755L;

  /** the X position of the overlay. */
  protected int m_X;

  /** the Y position of the overlay. */
  protected int m_Y;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "x", "x",
	    getDefaultX(), -3, null);

    m_OptionManager.add(
	    "y", "y",
	    getDefaultY(), -3, null);
  }

  /**
   * Returns the default X position for the overlay.
   *
   * @return		the default X position
   */
  protected int getDefaultX() {
    return -3;
  }

  /**
   * Sets the X position of the overlay.
   *
   * @param value 	the X position
   */
  public void setX(int value) {
    m_X = value;
    reset();
  }

  /**
   * Returns the currently set X position of the overlay.
   *
   * @return 		the X position
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
  public String xTipText() {
    return "The X position of the overlay (>=0: absolute, -1: left, -2: center, -3: right).";
  }

  /**
   * Returns the default Y position for the overlay.
   *
   * @return		the default Y position
   */
  protected int getDefaultY() {
    return -1;
  }

  /**
   * Sets the Y position of the overlay.
   *
   * @param value 	the Y position
   */
  public void setY(int value) {
    m_Y = value;
    reset();
  }

  /**
   * Returns the currently set Y position of the overlay.
   *
   * @return 		the Y position
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
  public String yTipText() {
    return "The Y position of the overlay (>=0: absolute, -1: top, -2: center, -3: bottom).";
  }

  /**
   * Returns the height.
   *
   * @return 		the height
   */
  protected int getHeight() {
    return 0;
  }

  /**
   * Returns the width.
   *
   * @return 		the width
   */
  protected int getWidth() {
    return 0;
  }

  /**
   * Performs the actual painting.
   * 
   * @param viewer	the associated viewer
   * @param g		the graphics context
   * @param x		the actual x coordinate
   * @param y		the actual y coordinate
   */
  protected abstract void doPaintOverlay(OpenStreetMapViewer viewer, Graphics g, int x, int y);

  /**
   * Computes the actual coordinates before doing the actual painting.
   * 
   * @param viewer	the associated viewer
   * @param g		the graphics context
   * @see		#doPaintOverlay(JMapViewer, Graphics, int, int)
   */
  @Override
  protected void doPaintOverlay(OpenStreetMapViewer viewer, Graphics g) {
    int		actX;
    int		actY;
    Rectangle	bounds;

    actX   = m_X;
    actY   = m_Y;
    bounds = viewer.getViewer().getViewer().getBounds();

    if (isLoggingEnabled())
      getLogger().fine("bounds: " + bounds);

    // X
    if (m_X == -1)
      actX = 0;
    else if (m_X == -2)
      actX = (int) ((bounds.width - getWidth()) / 2);
    else if (m_X == -3)
      actX = (int) (bounds.width - getWidth());
    else
      actX = m_X;

    // Y
    if (m_Y == -1)
      actY = 0;
    else if (m_Y == -2)
      actY = (int) ((bounds.height - getHeight()) / 2);
    else if (m_Y == -3)
      actY = (int) (bounds.height - getHeight());
    else
      actY = m_Y;
    actY += getHeight();
    
    if (isLoggingEnabled())
      getLogger().fine("x: " + m_X + " -> " + actX + ", y: " + m_Y + " -> " + actY);
    
    doPaintOverlay(viewer, g, actX, actY);
  }
}
