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
 * AbstractTimeseriesSplitter.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.timeseriessplit;

import adams.core.QuickInfoSupporter;
import adams.core.option.AbstractOptionHandler;
import adams.data.timeseries.Timeseries;

/**
 * Ancestor for timeseries splitters.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractTimeseriesSplitter
  extends AbstractOptionHandler
  implements QuickInfoSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 7657662003022052340L;

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   * <br><br>
   * Default implementation returns null.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return null;
  }

  /**
   * Performs checks on the timeseries that is to be split.
   * <br><br>
   * Default implementation only checks whether data has been provided.
   * 
   * @param series	the timeseries to split
   */
  protected void check(Timeseries series) {
    if (series == null)
      throw new IllegalStateException("No data provided!");
  }
  
  /**
   * Performs the actual split.
   * 
   * @param series	the timeseries to split
   * @return		the generated sub-timeseries
   */
  protected abstract Timeseries[] doSplit(Timeseries series);
  
  /**
   * Post-processes the segments.
   * 
   * @param segments	the segments to process
   * @return		the processed segments
   */
  protected Timeseries[] postProcess(Timeseries[] segments) {
    for (Timeseries series: segments)
      series.getNotes().addProcessInformation(this);
    return segments;
  }
  
  /**
   * Splits the timeseries.
   * 
   * @param series	the timeseries to split
   * @return		the generated sub-timeseries
   */
  public Timeseries[] split(Timeseries series) {
    Timeseries[]	result;
    
    check(series);
    result = doSplit(series);
    return postProcess(result);
  }
}
