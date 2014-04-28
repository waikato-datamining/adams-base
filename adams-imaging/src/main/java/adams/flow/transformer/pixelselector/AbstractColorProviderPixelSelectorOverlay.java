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
 * AbstractColorProviderPixelSelectorOverlay.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.pixelselector;

import adams.gui.visualization.core.AbstractColorProvider;
import adams.gui.visualization.core.DefaultColorProvider;

/**
 * Overlay that uses a color provider for the painting the various elements 
 * of the overlay.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractColorProviderPixelSelectorOverlay
  extends AbstractPixelSelectorOverlay {
  
  /** for serialization. */
  private static final long serialVersionUID = -2158576853112526562L;
  
  /** the color of the overlay. */
  protected AbstractColorProvider m_ColorProvider;

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "color-provider", "colorProvider",
	    getDefaultColorProvider());
  }
  
  /**
   * Returns the default color provider.
   * 
   * @return		the default color provider
   */
  protected AbstractColorProvider getDefaultColorProvider() {
    return new DefaultColorProvider();
  }

  /**
   * Sets the color provider for the overlay.
   *
   * @param value	the color provider
   */
  public void setColorProvider(AbstractColorProvider value) {
    m_ColorProvider = value;
    reset();
  }

  /**
   * Returns the color provider for the overlay.
   *
   * @return		the color provider
   */
  public AbstractColorProvider getColorProvider() {
    return m_ColorProvider;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String colorProviderTipText() {
    return "The color provider to use for the overlay.";
  }
}
