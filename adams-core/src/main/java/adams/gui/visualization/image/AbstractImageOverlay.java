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
 * AbstractImageOverlay.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.image;

import java.awt.Graphics;

import adams.core.ShallowCopySupporter;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.OptionUtils;
import adams.gui.visualization.image.ImagePanel.PaintPanel;

/**
 * Abstract ancestor for image overlays.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractImageOverlay
  extends AbstractOptionHandler
  implements ImageOverlay, ShallowCopySupporter<AbstractImageOverlay> {

  /** for serialization. */
  private static final long serialVersionUID = 4176141444398824387L;

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
   * Sets whether this overlay is enabled or not.
   *
   * @param value 	true if to enable this overlay
   */
  public void setEnabled(boolean value) {
    m_Enabled = value;
    reset();
  }

  /**
   * Returns whether this overlay is enabled or not.
   *
   * @return 		true if overlay enabled
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
    return "If enabled, this overlay is painted over the image.";
  }

  /**
   * Performs the actual painting of the overlay.
   *
   * @param panel	the panel this overlay is for
   * @param g		the graphics context
   */
  protected abstract void doPaintOverlay(PaintPanel panel, Graphics g);

  /**
   * Paints the overlay over the image.
   *
   * @param panel	the panel this overlay is for
   * @param g		the graphics context
   */
  public void paintOverlay(PaintPanel panel, Graphics g) {
    if (m_Enabled)
      doPaintOverlay(panel, g);
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @return		the shallow copy
   */
  public AbstractImageOverlay shallowCopy() {
    return shallowCopy(false);
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @param expand	whether to expand variables to their current values
   * @return		the shallow copy
   */
  public AbstractImageOverlay shallowCopy(boolean expand) {
    return (AbstractImageOverlay) OptionUtils.shallowCopy(this, expand);
  }
}
