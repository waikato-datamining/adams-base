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
 * Copyright (C) 2010-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.data.outlier;

import java.util.ArrayList;
import java.util.List;

import adams.data.container.DataContainer;

/**
 <!-- globalinfo-start -->
 * A dummy detector that detects nothing.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre>-D (property: debug)
 *         If set to true, scheme may output additional info to the console.
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PassThrough
  extends AbstractOutlierDetector {

  /** for serialization. */
  private static final long serialVersionUID = 640418515170621204L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "A dummy detector that detects nothing.";
  }

  /**
   * Performs no detecting at all.
   *
   * @param data	the data to process
   * @return		the detections
   */
  @Override
  protected List<String> processData(DataContainer data) {
    return new ArrayList<String>();
  }
}
