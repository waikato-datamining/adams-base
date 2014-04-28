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
 * SequencePlotContainer.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.sink.sequenceplotter;

import adams.data.sequence.XYSequence;
import adams.gui.visualization.sequence.XYSequenceContainer;

/**
 * Container for sequence plot data.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SequencePlotContainer
  extends XYSequenceContainer {

  /** for serialization. */
  private static final long serialVersionUID = 4085925902403215258L;

  /**
   * Initializes the container.
   *
   * @param manager	the owning manager
   * @param data	the sequence to encapsulate
   */
  public SequencePlotContainer(SequencePlotContainerManager manager, XYSequence data) {
    super(manager, data);
  }
}
