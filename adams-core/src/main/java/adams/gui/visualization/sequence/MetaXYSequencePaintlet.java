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
 * MetaXYSequencePaintlet.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.sequence;

/**
 * Interface for paintlets that wrap another paintlet.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface MetaXYSequencePaintlet
  extends XYSequencePaintlet {

  /**
   * Sets the actual paintlet to use.
   *
   * @param value	the paintlet
   */
  public void setPaintlet(XYSequencePaintlet value);

  /**
   * Returns the painlet in use.
   *
   * @return		the paintlet
   */
  public XYSequencePaintlet getPaintlet();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String paintletTipText();
}
