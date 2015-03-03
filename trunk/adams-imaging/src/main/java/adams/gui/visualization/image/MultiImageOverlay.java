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
 * MultiImageOverlay.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.image;

import java.awt.Graphics;

import adams.gui.visualization.image.ImagePanel.PaintPanel;

/**
 <!-- globalinfo-start -->
 * Combines multiple overlays, applies them sequentially.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-enabled (property: enabled)
 * &nbsp;&nbsp;&nbsp;If enabled, this overlay is painted over the image.
 * </pre>
 * 
 * <pre>-overlays &lt;adams.gui.visualization.image.AbstractImageOverlay&gt; [-overlays ...] (property: overlays)
 * &nbsp;&nbsp;&nbsp;The overlays to apply sequentially to the image.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MultiImageOverlay
  extends AbstractImageOverlay {

  /** for serialization. */
  private static final long serialVersionUID = -5447561040236560001L;
  
  /** the overlays to use. */
  protected AbstractImageOverlay[] m_Overlays;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Combines multiple overlays, applies them sequentially.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"overlays", "overlays",
	new AbstractImageOverlay[0]);
  }

  /**
   * Sets the overlays to apply.
   *
   * @param value 	the overlays
   */
  public void setOverlays(AbstractImageOverlay[] value) {
    m_Overlays = value;
    reset();
  }

  /**
   * Returns the overlays to apply.
   *
   * @return 		the overlays
   */
  public AbstractImageOverlay[] getOverlays() {
    return m_Overlays;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String overlaysTipText() {
    return "The overlays to apply sequentially to the image.";
  }

  /**
   * Notifies the overlay that the image has changed.
   *
   * @param panel	the panel this overlay belongs to
   */
  @Override
  protected synchronized void doImageChanged(PaintPanel panel) {
    for (AbstractImageOverlay overlay: m_Overlays)
      overlay.imageChanged(panel);
  }

  /**
   * Performs the actual painting of the overlay.
   *
   * @param panel	the panel this overlay is for
   * @param g		the graphics context
   */
  @Override
  protected synchronized void doPaintOverlay(PaintPanel panel, Graphics g) {
    for (AbstractImageOverlay overlay: m_Overlays)
      overlay.paintOverlay(panel, g);
  }
}
