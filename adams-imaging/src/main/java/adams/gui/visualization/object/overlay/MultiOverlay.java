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
 * MultiOverlay.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.object.overlay;

import adams.gui.visualization.object.ObjectAnnotationPanel;

import java.awt.Graphics;

/**
 * Combines multiple overlays.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class MultiOverlay
  extends AbstractOverlay {

  private static final long serialVersionUID = 6417360675029377483L;

  /** the overlays to combine. */
  protected Overlay[] m_Overlays;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Combines multiple overlays.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "overlay", "overlays",
      new Overlay[0]);
  }

  /**
   * Adds the overlays.
   *
   * @param value 	the overlay
   * @return		itself
   */
  public MultiOverlay addOverlay(Overlay value) {
    Overlay[] 	overlays;
    int		i;

    overlays = new Overlay[m_Overlays.length + 1];
    for (i = 0; i < m_Overlays.length; i++)
      overlays[i] = m_Overlays[i];
    overlays[overlays.length - 1] = value;

    setOverlays(overlays);

    return this;
  }

  /**
   * Sets the overlays to manage.
   *
   * @param value 	the overlays
   */
  public void setOverlays(Overlay[] value) {
    m_Overlays = value;
    reset();
  }

  /**
   * Returns the overlays to manage.
   *
   * @return 		the overlays
   */
  public Overlay[] getOverlays() {
    return m_Overlays;
  }

  /**
   * Paints the overlay.
   *
   * @param panel 	the owning panel
   * @param g		the graphics context
   */
  @Override
  protected void doPaint(ObjectAnnotationPanel panel, Graphics g) {
    int		i;

    for (i = 0; i < m_Overlays.length; i++)
      m_Overlays[i].paint(panel, g);
  }
}
