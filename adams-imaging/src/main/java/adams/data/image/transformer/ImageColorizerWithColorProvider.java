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
 * ImageColorizer.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.data.image.transformer;

import adams.gui.visualization.core.ColorProvider;

/**
 * Interface for image colorizers that support a color provider.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public interface ImageColorizerWithColorProvider
  extends ImageColorizer {

  /**
   * Sets the color provider to use.
   *
   * @param value	the color provider
   */
  public void setColorProvider(ColorProvider value);

  /**
   * Returns the color provider to use.
   *
   * @return		the color provider
   */
  public ColorProvider getColorProvider();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String colorProviderTipText();
}
