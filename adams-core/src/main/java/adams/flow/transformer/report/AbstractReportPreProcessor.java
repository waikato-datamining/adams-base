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
 * AbstractReportPreProcessor.java
 * Copyright (C) 2013-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.report;

import adams.core.option.AbstractOptionHandler;
import adams.data.report.Report;
import adams.flow.core.Actor;

/**
 * Ancestor for {@link Report} pre-processors.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractReportPreProcessor<T extends Report>
  extends AbstractOptionHandler {

  /** for serialization. */
  private static final long serialVersionUID = 4489141650668803307L;

  /** the owning actor. */
  protected Actor m_Owner;

  /**
   * Sets the owner for this preprocessor.
   *
   * @param value   the owning actor
   */
  public void setOwner(Actor value) {
    m_Owner = value;
  }

  /**
   * Returns the current owner of this preprocessor.
   *
   * @return        the owning actor, null if not available
   */
  public Actor getOwner() {
    return m_Owner;
  }

  /**
   * Checks the data to pre-process.
   * <br><br>
   * Default implementation only checks whether data is present.
   * 
   * @param data	the data to check
   */
  protected void check(T data) {
    if (m_Owner == null)
      throw new IllegalStateException("No owner set!");
    if (data == null)
      throw new IllegalStateException("No data provided!");
  }
  
  /**
   * Performs the actual pre-processing.
   * 
   * @param data	the data to process
   * @return		the processed data
   */
  protected abstract T doPreProcess(T data);
  
  /**
   * Pre-processes the data.
   * 
   * @param data	the data to post-process
   * @return		the processed data
   */
  public T preProcess(T data) {
    check(data);
    return doPreProcess(data);
  }
}
