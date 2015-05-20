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
 * TimeseriesLOWESSBased.java
 * Copyright (C) 2013-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.data.smoothing;

import adams.data.filter.AbstractLOWESS;
import adams.data.filter.TimeseriesLOWESS;
import adams.data.timeseries.Timeseries;

/**
 <!-- globalinfo-start -->
 * A LOWESS based smoothing algorithm.<br>
 * For more information on LOWESS see:<br>
 * <br>
 * WikiPedia. Local Regression. URL http:&#47;&#47;en.wikipedia.org&#47;wiki&#47;Lowess.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-window-size &lt;int&gt; (property: windowSize)
 * &nbsp;&nbsp;&nbsp;The window size to use, must be at least 20.
 * &nbsp;&nbsp;&nbsp;default: 20
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TimeseriesLOWESSBased
  extends AbstractLOWESSBased<Timeseries> {

  /** for serialization. */
  private static final long serialVersionUID = -4052647569528377770L;

  /**
   * Returns the default LOWESS filter.
   *
   * @return		the default filter
   */
  @Override
  protected AbstractLOWESS getDefault() {
    return new TimeseriesLOWESS();
  }
}
