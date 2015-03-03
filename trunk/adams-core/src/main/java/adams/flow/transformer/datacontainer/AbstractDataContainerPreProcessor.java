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
 * AbstractDataContainerPreProcessor.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.datacontainer;

import adams.core.option.AbstractOptionHandler;
import adams.data.container.DataContainer;
import adams.flow.transformer.AbstractDataContainerDbWriter;

/**
 * Ancestor for {@link DataContainer} pre-processors.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractDataContainerPreProcessor<T extends DataContainer>
  extends AbstractOptionHandler {

  /** for serialization. */
  private static final long serialVersionUID = 4489141650668803307L;

  /** the owning transformer of this preprocessor. */
  protected AbstractDataContainerDbWriter m_Owner;
  
  /**
   * Sets the owner of this preprocessor.
   * 
   * @param value	the owner
   */
  public void setOwner(AbstractDataContainerDbWriter value) {
    m_Owner = value;
  }
  
  /**
   * Returns the current owner.
   * 
   * @return		the owner, null if none set
   */
  public AbstractDataContainerDbWriter getOwner() {
    return m_Owner;
  }
  
  /**
   * Checks the data to pre-process.
   * <p/>
   * Default implementation only checks whether data is present.
   * 
   * @param data	the data to check
   */
  protected void check(T data) {
    if (m_Owner == null)
      throw new IllegalStateException("No owning transformer set!");
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
   * @param data	the data to pre-process
   * @return		the processed data
   */
  public T preProcess(T data) {
    check(data);
    return doPreProcess(data);
  }
}
