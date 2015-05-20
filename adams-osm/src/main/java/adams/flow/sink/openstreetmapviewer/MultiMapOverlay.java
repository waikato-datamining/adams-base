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
 * MultiMapOverlay.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink.openstreetmapviewer;

import java.awt.Graphics;

import adams.core.option.OptionUtils;
import adams.flow.sink.OpenStreetMapViewer;

/**
 <!-- globalinfo-start -->
 * A meta-overlay that paints multiple overlays.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-enabled &lt;boolean&gt; (property: enabled)
 * &nbsp;&nbsp;&nbsp;If enabled, the overlay gets painted.
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 * 
 * <pre>-overlay &lt;adams.flow.sink.openstreetmapviewer.AbstractMapOverlay&gt; [-overlay ...] (property: overlays)
 * &nbsp;&nbsp;&nbsp;The array of overlays to use.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MultiMapOverlay
  extends AbstractMapOverlay {

  /** for serialization. */
  private static final long serialVersionUID = 805661569976845842L;

  /** the overlays. */
  protected AbstractMapOverlay[] m_Overlays;

  /**
   * Returns a string describing the object.
   *
   * @return 		a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "A meta-overlay that paints multiple overlays.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "overlay", "overlays",
	    new AbstractMapOverlay[0]);
  }

  /**
   * Sets the overlays to use.
   *
   * @param value	the filters to use
   */
  public void setOverlays(AbstractMapOverlay[] value) {
    if (value != null) {
      m_Overlays = value;
      reset();
    }
    else {
      getLogger().severe(
	  this.getClass().getName() + ": overlays cannot be null!");
    }
  }

  /**
   * Returns the overlays in use.
   *
   * @return		the overlays
   */
  public AbstractMapOverlay[] getOverlays() {
    return m_Overlays;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String overlaysTipText() {
    return "The array of overlays to use.";
  }

  /**
   * Performs the actual painting.
   * 
   * @param viewer	the associated viewer
   * @param g		the graphics context
   */
  @Override
  protected void doPaintOverlay(OpenStreetMapViewer viewer, Graphics g) {
    int		i;
    
    for (i = 0; i < m_Overlays.length; i++) {
      getLogger().info(
	    "Overlay " + (i+1) + "/" + m_Overlays.length + ": "
	    + OptionUtils.getCommandLine(m_Overlays[i]));

      m_Overlays[i].paintOverlay(viewer, g);
    }

    getLogger().info("Finished!");
  }
}
