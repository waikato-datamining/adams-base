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
 * AbstractDataContainerUpdatingPostProcessor.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.scripting;

import java.io.Serializable;
import java.util.List;

import adams.data.container.DataContainer;

/**
 * Abstract ancestor for classes that need to post-process the containers
 * that the container manager got updated with.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractDataContainerUpdatingPostProcessor
  implements Serializable {

  /** for serialization. */
  private static final long serialVersionUID = 2269820505356261133L;

  /** the owning processor. */
  protected AbstractCommandProcessor m_Owner;

  /**
   * Initializes the post-processor.
   *
   * @param owner	the owning processor
   */
  public AbstractDataContainerUpdatingPostProcessor(AbstractCommandProcessor owner) {
    super();

    m_Owner = owner;
  }

  /**
   * Returns the owning scriptlet.
   *
   * @return		the scriptlet
   */
  public AbstractCommandProcessor getOwner() {
    return m_Owner;
  }

  /**
   * Post-processes the containers.
   *
   * @param conts	the containers to post-process
   */
  public abstract void postProcess(List<? extends DataContainer> conts);
}
