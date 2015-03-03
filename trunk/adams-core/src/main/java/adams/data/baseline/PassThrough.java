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
 * PassThrough.java
 * Copyright (C) 2009-2010 University of Waikato, Hamilton, New Zealand
 */

package adams.data.baseline;

import adams.data.container.DataContainer;

/**
 <!-- globalinfo-start -->
 * Dummy scheme, performs no basline correction at all.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre>-D (property: debug)
 * &nbsp;&nbsp;&nbsp;If set to true, scheme may output additional info to the console.
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  dale (dale at cs dot waikato dot ac dot nz)
 * @version $Revision$
 */
public class PassThrough
  extends AbstractBaselineCorrection<DataContainer> {

  /** for serialization. */
  private static final long serialVersionUID = -3140079797505735057L;

  /**
   * Returns a string describing the object.
   *
   * @return         a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Dummy scheme, performs no basline correction at all.";
  }

  /**
   * Does nothing.
   *
   * @param data	the data to correct
   * @return		the corrected data
   */
  protected DataContainer processData(DataContainer data) {
    return (DataContainer) data.getClone();
  }
}
