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
 * PassThrough.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.wekaclusterer;

import adams.flow.container.WekaModelContainer;

/**
 <!-- globalinfo-start -->
 * Dummy post-processor that just returns the model container as it is.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 * 
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to 
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PassThrough
  extends AbstractClustererPostProcessor {

  /** for serialization. */
  private static final long serialVersionUID = 2365043034989579599L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Dummy post-processor that just returns the model container as it is.";
  }
  
  /**
   * Returns the keys that the processor adds/modifies.
   * 
   * @return		always null
   */
  protected String[] getContainerKeys() {
    return null;
  }

  /**
   * Simply returns the container, no post-processing done.
   * 
   * @param cont	the container to post-process
   * @return		the post-processed container
   */
  protected WekaModelContainer doPostProcess(WekaModelContainer cont) {
    return cont;
  }
}
