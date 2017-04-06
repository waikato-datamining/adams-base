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
 * MetaDataColorPaintlet.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.sequence;

import adams.gui.visualization.sequence.metadatacolor.AbstractMetaDataColor;

/**
 * Interface for paintlets that can extract their color from meta-data attached
 * to the sequence points.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface MetaDataColorPaintlet
  extends XYSequencePaintlet {

  /**
   * Sets the scheme for extracting the color from the meta-data.
   *
   * @param value	the scheme
   */
  public void setMetaDataColor(AbstractMetaDataColor value);

  /**
   * Returns the scheme for extracting the color from the meta-data.
   *
   * @return		the scheme
   */
  public AbstractMetaDataColor getMetaDataColor();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String metaDataColorTipText();
}
