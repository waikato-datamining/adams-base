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
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.control.plotprocessor;

import java.util.List;

import adams.flow.container.SequencePlotterContainer;

/**
 <!-- globalinfo-start -->
 * Dummy processor which never generates any new containers.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-plot-name-suffix &lt;java.lang.String&gt; (property: plotNameSuffix)
 * &nbsp;&nbsp;&nbsp;The suffix for the plot name; if left empty, the plot container automatically 
 * &nbsp;&nbsp;&nbsp;becomes an OVERLAY.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PassThrough
  extends AbstractPlotProcessor {

  /** for serialization. */
  private static final long serialVersionUID = 6183944706595670117L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Dummy processor which never generates any new containers.";
  }

  /**
   * Processes the provided container. Generates new containers
   * if applicable.
   * 
   * @param cont	the container to process
   * @return		always null
   */
  @Override
  protected List<SequencePlotterContainer> doProcess(SequencePlotterContainer cont) {
    return null;
  }
}
