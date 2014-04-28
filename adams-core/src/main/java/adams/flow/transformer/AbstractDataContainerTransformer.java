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
 * AbstractDataContainerTransformer.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.data.container.DataContainer;


/**
 * Ancestor for actors that transform data containers.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractDataContainerTransformer
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -2382262152717706634L;

  /**
   * Returns the data container class in use.
   *
   * @return		the container class
   */
  protected abstract Class getDataContainerClass();

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the default DataContainer class for the project
   */
  public Class[] accepts() {
    return new Class[]{DataContainer.class, getDataContainerClass()};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the default DataContainer class for the project
   */
  public Class[] generates() {
    return new Class[]{DataContainer.class, getDataContainerClass()};
  }
}
