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
 * SequencePlotContainerManager.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.sink.sequenceplotter;

import adams.data.sequence.XYSequence;
import adams.gui.visualization.container.ContainerListManager;
import adams.gui.visualization.sequence.XYSequenceContainerManager;

/**
 * A handler for the sequence plot containers.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SequencePlotContainerManager
  extends XYSequenceContainerManager {

  /** for serialization. */
  private static final long serialVersionUID = 70788049300684676L;

  /**
   * Initializes the manager.
   *
   * @param owner	the owning panel
   */
  public SequencePlotContainerManager(ContainerListManager owner) {
    super(owner);
  }
  
  /**
   * Whether to update the search whenever the content changes.
   * 
   * @return		always FALSE, as the {@link AbstractPlotUpdater} classes
   * 			handle updating the plot
   */
  @Override
  protected boolean updateSearchOnUpdate() {
    return false;
  }

  /**
   * Returns a new container containing the given payload.
   *
   * @param o		the payload to encapsulate
   * @return		the new container
   */
  @Override
  public SequencePlotContainer newContainer(Comparable o) {
    return new SequencePlotContainer(this, (XYSequence) o);
  }
}
