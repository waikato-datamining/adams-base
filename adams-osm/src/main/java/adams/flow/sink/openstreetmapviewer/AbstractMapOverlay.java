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
 * AbstractMapOverlay.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.sink.openstreetmapviewer;

import java.awt.Graphics;

import org.openstreetmap.gui.jmapviewer.JMapViewer;

import adams.core.option.AbstractOptionHandler;
import adams.flow.sink.OpenStreetMapViewer;

/**
 * Ancestor for classes that paint an overlay on a {@link JMapViewer}.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractMapOverlay
  extends AbstractOptionHandler {

  /** for serialization. */
  private static final long serialVersionUID = -5960654102688727276L;
  
  /** whether the overlay is enabled. */
  protected boolean m_Enabled;
  
  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "enabled", "enabled",
	    true);
  }

  /**
   * Sets whether the overlay is enabled.
   *
   * @param value	true if enabled
   */
  public void setEnabled(boolean value) {
    m_Enabled = value;
    reset();
  }

  /**
   * Returns whether the overlay is enabled
   *
   * @return		true if enabled
   */
  public boolean isEnabled() {
    return m_Enabled;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String enabledTipText() {
    return "If enabled, the overlay gets painted.";
  }

  /**
   * Gets executed before the actual painting.
   * <br><br>
   * Default implementation does nothing.
   * 
   * @param viewer	the associated viewer
   * @param g		the graphics context
   */
  protected void prePaintOverlay(OpenStreetMapViewer viewer, Graphics g) {
  }

  /**
   * Performs the actual painting.
   * 
   * @param viewer	the associated viewer
   * @param g		the graphics context
   */
  protected abstract void doPaintOverlay(OpenStreetMapViewer viewer, Graphics g);

  /**
   * Gets executed after the actual painting.
   * <br><br>
   * Default implementation does nothing.
   * 
   * @param viewer	the associated viewer
   * @param g		the graphics context
   */
  protected void postPaintOverlay(OpenStreetMapViewer viewer, Graphics g) {
  }

  /**
   * Paints the overlay.
   * 
   * @param viewer	the associated viewer
   * @param g		the graphics context
   */
  public void paintOverlay(OpenStreetMapViewer viewer, Graphics g) {
    if (m_Enabled) {
      prePaintOverlay(viewer, g);
      doPaintOverlay(viewer, g);
      postPaintOverlay(viewer, g);
    }
  }
}
