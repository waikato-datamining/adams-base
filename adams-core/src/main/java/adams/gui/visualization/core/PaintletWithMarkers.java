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
 * PaintletWithMarkers.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.core;

/**
 * Interface for paintlets that support markers.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface PaintletWithMarkers {

  /**
   * Returns whether marker shapes are disabled.
   *
   * @return		true if marker shapes are disabled
   */
  public boolean isMarkersDisabled();

  /**
   * Sets whether to draw markers or not.
   *
   * @param value	if true then marker shapes won't be drawn
   */
  public void setMarkersDisabled(boolean value);

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String markersDisabledTipText();

  /**
   * Returns whether marker shapes are always drawn.
   *
   * @return		true if marker shapes are always drawn, not just when zoomed in
   */
  public boolean getAlwaysShowMarkers();

  /**
   * Sets whether to always draw markers.
   *
   * @param value	if true then marker are always drawn, not just when zoomed in
   */
  public void setAlwaysShowMarkers(boolean value);

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String alwaysShowMarkersTipText();

  /**
   * Sets the extent (width and height of the shape around the plotted point).
   * 0 turns the plotting off. Should be an odd number for centering the shape.
   *
   * @param value	the new extent
   */
  public void setMarkerExtent(int value);

  /**
   * Returns the current marker extent (which is the width and height of the
   * shape).
   *
   * @return		the current extent
   */
  public int getMarkerExtent();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String markerExtentTipText();
}
