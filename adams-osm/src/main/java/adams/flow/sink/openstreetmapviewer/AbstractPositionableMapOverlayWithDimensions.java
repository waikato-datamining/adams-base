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
 * AbstractPositionableMapOverlayWithDimensions.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.sink.openstreetmapviewer;

import java.awt.Graphics;
import java.awt.Rectangle;

import org.openstreetmap.gui.jmapviewer.JMapViewer;

import adams.flow.sink.OpenStreetMapViewer;

/**
 * Ancestor for overlays that can be positioned on the viewer and support
 * dimensions.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractPositionableMapOverlayWithDimensions
  extends AbstractPositionableMapOverlay {
  
  /** for serialization. */
  private static final long serialVersionUID = 5439828929470172755L;

  /** the width of the overlay. */
  protected int m_Width;

  /** the height of the overlay. */
  protected int m_Height;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "width", "width",
	    getDefaultWidth(), -1, null);

    m_OptionManager.add(
	    "height", "height",
	    getDefaultHeight(), -1, null);
  }

  /**
   * Returns the default width for the overlay.
   *
   * @return		the default width
   */
  protected abstract int getDefaultWidth();

  /**
   * Sets the width of the overlay.
   *
   * @param value 	the width
   */
  public void setWidth(int value) {
    m_Width = value;
    reset();
  }

  /**
   * Returns the currently set width of the overlay.
   *
   * @return 		the width
   */
  @Override
  public int getWidth() {
    return m_Width;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public abstract String widthTipText();

  /**
   * Returns the default height for the overlay.
   *
   * @return		the default height
   */
  protected abstract int getDefaultHeight();

  /**
   * Sets the height of the overlay.
   *
   * @param value 	the height
   */
  public void setHeight(int value) {
    m_Height = value;
    reset();
  }

  /**
   * Returns the currently set height of the overlay.
   *
   * @return 		the height
   */
  @Override
  public int getHeight() {
    return m_Height;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public abstract String heightTipText();

  /**
   * Performs the actual painting.
   * 
   * @param viewer	the associated viewer
   * @param g		the graphics context
   * @param x		the actual x coordinate
   * @param y		the actual y coordinate
   */
  @Override
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
      actX = (int) ((bounds.width - m_Width) / 2) - 1;
    else if (m_X == -3)
      actX = (int) (bounds.width - m_Width) - 1;
    else
      actX = m_X;

    // Y
    if (m_Y == -1)
      actY = 0;
    else if (m_Y == -2)
      actY = (int) ((bounds.height - m_Height) / 2) - 1;
    else if (m_Y == -3)
      actY = (int) (bounds.height - m_Height) - 1;
    else
      actY = m_Y;
    actY += m_Height;
    
    if (isLoggingEnabled())
      getLogger().fine("x: " + m_X + " -> " + actX + ", y: " + m_Y + " -> " + actY);
    
    doPaintOverlay(viewer, g, actX, actY);
  }
}
